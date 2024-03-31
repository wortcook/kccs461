package edu.umkc.cs461.hw2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.NavigableMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Date;


import edu.umkc.cs461.hw2.model.*;
import edu.umkc.cs461.hw2.rules.*;

public class Main {

    public static final int STARTER_POPULATION = 10;

    public static void main(String[] args) {
        NavigableMap<Schedule, Double> population = new ValueSortedMap<Schedule,Double>();

        Model model = ModelLoader.loadModel();
        
        generateInitialPopulation(population, model);

        population = scorePopulation(model, population);
        population = normalizeScores(population);

        //sort the population by score
        boolean continueGeneration = true;

        double mutationRate = 0.1;

        int remainingGenerations = 1000;

        System.out.println("Population Size: " + population.size());

        do{
            population = cullPopulation(population);
            population = crossoverPopulation(population);
            population = mutatePopulation(population, model, mutationRate);
            population = scorePopulation(model, population);
            population = normalizeScores(population);

            Schedule bestSchedule = population.lastKey();
            Double bestScore = population.get(bestSchedule);

            Schedule worstSchedule = population.firstKey();
            Double worstScore = population.get(worstSchedule);

            System.out.println("Best Schedule Score: " + bestScore);
            System.out.println("Worst Schedule Score: " + worstScore);
            System.out.println("Population Size: " + population.size());

            if(remainingGenerations-- == 0){
                continueGeneration = false;
            }

        }while(continueGeneration);


    }

    private static NavigableMap<Schedule, Double> mutatePopulation(Map<Schedule, Double> population, Model model, double mutationRate) {
        ValueSortedMap<Schedule, Double> newPopulation = new ValueSortedMap<>();

        Map<Schedule,Double> mutations = new HashMap<Schedule,Double>();

        population.keySet().forEach(schedule -> {
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
        ValueSortedMap<Schedule, Double> newPopulation = new ValueSortedMap<>();

        List<Schedule> parentPopulation = new ArrayList<>(population.keySet());

        while(parentPopulation.size()>1){
            //get two random parents
            Schedule parent1 = parentPopulation.remove((int)(Math.random()*(parentPopulation.size()-1)));
            Schedule parent2 = parentPopulation.remove((int)(Math.random()*(parentPopulation.size()-1)));

            //for each activity, randomly select room, time, facilitator from one of the parents
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
        }

        newPopulation.putAll(population);

        return newPopulation;
    }

    //The culling algorithm we are using allows for additional random change
    //that a strong candiate will be removed from the population
    //or a weak candidate will be kept
    private static NavigableMap<Schedule, Double> cullPopulation(final Map<Schedule,Double> population) {

        final NavigableMap<Schedule, Double> retPop = new ValueSortedMap<Schedule,Double>();

        retPop.putAll(population);

        return retPop;
    }

    private static NavigableMap<Schedule, Double> scorePopulation(Model model, NavigableMap<Schedule, Double> population) {
        ValueSortedMap<Schedule, Double> scoredPopulation = new ValueSortedMap<>();

        Map<Schedule,Double> scored = population.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> ScheduleScorer.scoreSchedule(model, e.getKey())));

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
        IntStream.range(0, STARTER_POPULATION)./*parallel().*/forEach(i -> {
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
            population.put(schedule, 0.0);
        });
   }
}