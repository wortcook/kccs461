package edu.umkc.cs461.hw2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Date;


import edu.umkc.cs461.hw2.model.*;
import edu.umkc.cs461.hw2.rules.*;

public class Main {

    public static final int STARTER_POPULATION = 1000000;
    public static final int TARGET_STABLE_POPULATION_SIZE = 1000;

    public static final double MUTATION_RATE = 0.15;
    public static final double MUTATION_RATE_DECAY = 0.99;

    public static final int GENERATION_COUNT = 512;

    public static void main(String[] args) {
        NavigableMap<Schedule, Double> population = new ValueSortedMap<Schedule,Double>();

        Model model = ModelLoader.loadModel();
        
        generateInitialPopulation(population, model);

        population = scorePopulation(model, population);
        population = normalizeScores(population);

        //sort the population by score
        boolean continueGeneration = true;

        double mutationRate = MUTATION_RATE;

        int remainingGenerations = GENERATION_COUNT;

        System.out.println("Population Size: " + population.size());

        do{
            population = cullPopulation(population, TARGET_STABLE_POPULATION_SIZE);
            population = crossoverPopulation(population);
            population = mutatePopulation(population, model, mutationRate);
            population = scorePopulation(model, population);

            Schedule bestSchedule = population.lastKey();
            Double bestScore = population.get(bestSchedule);

            Schedule worstSchedule = population.firstKey();
            Double worstScore = population.get(worstSchedule);

            System.out.println("Best Score: " + bestScore);
            System.out.println("Worst Score: " + worstScore);

            if(remainingGenerations-- == 0){
                continueGeneration = false;
            }

            System.out.println("Population Size: " + population.size());

            mutationRate = mutationRate * MUTATION_RATE_DECAY;


        }while(continueGeneration);

        //Now lets print out the best schedule as a table
        Schedule bestSchedule = population.lastKey();

        System.out.println("Best Schedule");
        //Print out the schedule as a table of M W F and the timeslots
        System.out.println(Schedule.scheduleToString(bestSchedule));

        Scorer.ScheduleScore score = ScheduleScorer.scoreSchedule(model, bestSchedule);
        System.out.println("Score: " + score.score());
        System.out.println("Score Breakdown:");
        score.scoreBreakdown().entrySet().forEach(e -> {
            System.out.println(e.getKey() + ": " + e.getValue());
        });




    }

    private static NavigableMap<Schedule, Double> mutatePopulation(Map<Schedule, Double> population, Model model, double mutationRate) {
        ValueSortedMap<Schedule, Double> newPopulation = new ValueSortedMap<>();

        Map<Schedule,Double> mutations = new HashMap<Schedule,Double>();

        population.keySet().parallelStream().forEach(schedule -> {
            schedule.assignments().forEach((activity, assignment) -> {
                Room room = (Math.random() > mutationRate) ? assignment.location() : Model.getRandomRoom(model);
                Date timeslot = (Math.random() > mutationRate) ? assignment.timeslot() : Model.getRandomTimeslot(model);
                String facilitator = (Math.random() > mutationRate) ? assignment.facilitator() : Model.getRandomFacilitator(model);

                Assignment newAssignment = new Assignment(activity, timeslot, facilitator, room);
                schedule.assignments().put(activity, newAssignment);
            });
            mutations.put(schedule, 0.0);
        });

        newPopulation.putAll(mutations);
        return newPopulation;
    }

    private static NavigableMap<Schedule, Double> crossoverPopulation(Map<Schedule, Double> population) {
        Map<Schedule, Double> newPopulation = new HashMap<>();

        List<Schedule> parentPopulation = new ArrayList<>(population.keySet());

        Collections.shuffle(parentPopulation);

        final List<Schedule> primary = parentPopulation.subList(0, parentPopulation.size()/2);
        final List<Schedule> secondary = parentPopulation.subList(parentPopulation.size()/2, parentPopulation.size());

        final int halfIndex = parentPopulation.size()/2;

        //loop through the parent population and crossover the schedules select 2 at a time in parallel
        IntStream.range(0, halfIndex).parallel().forEach(i -> {
            Schedule parent1 = primary.get(i);
            Schedule parent2 = secondary.get(i);

            Map<Activity, Assignment> childMap = new HashMap<>();
            for(Activity activity : parent1.assignments().keySet()){
                Assignment assignment1 = parent1.assignments().get(activity);
                Assignment assignment2 = parent2.assignments().get(activity);

                Room childRoom = Math.random() < 0.5 ? assignment1.location() : assignment2.location();
                Date childTime = Math.random() < 0.5 ? assignment1.timeslot() : assignment2.timeslot();
                String childFacilitator = Math.random() < 0.5 ? assignment1.facilitator() : assignment2.facilitator();

                Assignment childAssignment = new Assignment(activity, childTime, childFacilitator, childRoom);
                
                childMap.put(activity, childAssignment);
            }

            Schedule child = new Schedule(childMap);
            newPopulation.put(child, 0.0);
        });

        // while(parentPopulation.size()>1){
        //     //get two random parents
        //     Schedule parent1 = parentPopulation.remove((int)(Math.random()*(parentPopulation.size()-1)));
        //     Schedule parent2 = parentPopulation.remove((int)(Math.random()*(parentPopulation.size()-1)));

        //     //for each activity, randomly select room, time, facilitator from one of the parents
        //     Map<Activity, Assignment> childMap = new HashMap<>();
        //     for(Activity activity : parent1.assignments().keySet()){
        //         Assignment assignment1 = parent1.assignments().get(activity);
        //         Assignment assignment2 = parent2.assignments().get(activity);

        //         Room childRoom = Math.random() < 0.5 ? assignment1.location() : assignment2.location();
        //         Date childTime = Math.random() < 0.5 ? assignment1.timeslot() : assignment2.timeslot();
        //         String childFacilitator = Math.random() < 0.5 ? assignment1.facilitator() : assignment2.facilitator();

        //         Assignment childAssignment = new Assignment(activity, childTime, childFacilitator, childRoom);
                
        //         childMap.put(activity, childAssignment);
        //     }

        //     Schedule child = new Schedule(childMap);
        //     newPopulation.put(child, 0.0);
        // }

        final ValueSortedMap<Schedule, Double> retPop = new ValueSortedMap<>();
        retPop.putAll(newPopulation);
        retPop.putAll(population);

        return retPop;
    }

    //The culling algorithm we are using allows for additional random change
    //that a strong candiate will be removed from the population
    //or a weak candidate will be kept
    private static NavigableMap<Schedule, Double> cullPopulation(final Map<Schedule,Double> population, final int targetSize) {


        //calculate the size of the population to cull
        //Start near 50% and then trend down to 33.33% which will maintain the target size after crossover.

        // double ratio = (Math.log10((double)targetSize)/Math.log10((double)population.size()));
        double ratio = ((double)targetSize)/((double)population.size());

        double cullPercentage = 1.0 - (0.5 - (0.16666667 * ratio));


        int cullSize = (int)(population.size() * cullPercentage);

        //cull the population by 50%
        final List<Schedule> cullList = new ArrayList<>(population.keySet()).reversed().subList(0, cullSize);

        final NavigableMap<Schedule, Double> retPop = new ValueSortedMap<Schedule,Double>();
        retPop.putAll(cullList.stream().collect(Collectors.toMap(s -> s, s -> 0.0)));

        return retPop;
    }

    private static NavigableMap<Schedule, Double> scorePopulation(Model model, NavigableMap<Schedule, Double> population) {
        ValueSortedMap<Schedule, Double> scoredPopulation = new ValueSortedMap<>();

        Map<Schedule,Double> scored = population.entrySet().parallelStream().collect(Collectors.toMap(Map.Entry::getKey, e -> ScheduleScorer.scoreSchedule(model, e.getKey()).score()));

        scoredPopulation.putAll(scored);
        return scoredPopulation;
    }

    private static  NavigableMap<Schedule, Double> normalizeScores(Map<Schedule, Double> population) {

        ValueSortedMap<Schedule, Double> retPop = new ValueSortedMap<>();

        final double sum = population.values().parallelStream().mapToDouble(Math::exp).sum();

        retPop.putAll(
            population.entrySet().parallelStream().collect(Collectors.toMap(Map.Entry::getKey, e -> Math.exp(e.getValue()) / sum))
        );
        return retPop;
    }

    private static void generateInitialPopulation(final Map<Schedule, Double> population, Model model) {
        //initialize population randomly

        Map<Schedule,Double> newPopulation = new HashMap<>();

        IntStream.range(0, STARTER_POPULATION).parallel().forEach(i -> {
        //for (int i = 0; i < STARTER_POPULATION; i++) {
            Map<Activity, Assignment> assignments = new HashMap<>();
            for(Activity activity : model.activities().values()){
                //randomly assign activities to rooms and timeslots
                //random code below provided by co-pilot
                //get a random room
                Room room = model.locations().values().stream().skip((int) (model.locations().size() * Math.random())).findFirst().get();
                //get a random timeslot
                Date timeslot = model.timeslots().values().stream().skip((int) (model.timeslots().size() * Math.random())).findFirst().get();
                //get a random facilitator
                String faciliator = model.facilitators().values().stream().skip((int) (model.facilitators().size() * Math.random())).findFirst().get();

                Assignment assignment = new Assignment(activity, timeslot, faciliator, room);

                assignments.put(activity, assignment);

            }

            if(i % 10000 == 0){
                System.out.println("Generated " + i + " schedules");
            }

            Schedule schedule = new Schedule(assignments);
            newPopulation.put(schedule, 0.0);
        });

        population.putAll(newPopulation);
   }
}