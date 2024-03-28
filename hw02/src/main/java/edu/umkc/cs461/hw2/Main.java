package edu.umkc.cs461.hw2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Date;

import edu.umkc.cs461.hw2.model.*;
import edu.umkc.cs461.hw2.rules.*;

public class Main {

    public static final int STARTER_POPULATION = 1000000;

    public static void main(String[] args) {
        Map<Schedule, Double> population = new HashMap<>();

        Model model = ModelLoader.loadModel();
        
        generateInitialPopulation(population, model);

        population = scorePopulation(model, population);
        population = normalizeScores(population);

        //sort the population by score
        boolean continueGeneration = true;

        double mutationRate = 0.1;

        int remainingGenerations = 1000;

        do{
            population = cullPopulation(population);
            population = crossoverPopulation(population);
            population = mutatePopulation(population, model, mutationRate);
            population = scorePopulation(model, population);
            population = normalizeScores(population);

            final Map<Schedule, Double> refPop = population;
            Comparator<Schedule> comparator = new Comparator<Schedule>() {
                @Override
                public int compare(Schedule o1, Schedule o2) {
                    return Double.compare(refPop.get(o1), refPop.get(o2));
                }
            };
    
            //sort the population by score
            TreeMap<Schedule, Double> sortedPopulation = new TreeMap<>(comparator);
            sortedPopulation.putAll(population);

            Schedule bestSchedule = sortedPopulation.lastKey();
            double bestScore = sortedPopulation.get(bestSchedule);

            Schedule worstSchedule = sortedPopulation.firstKey();
            double worstScore = sortedPopulation.get(worstSchedule);

            System.out.println("Best Schedule Score: " + bestScore);
            System.out.println("Worst Schedule Score: " + worstScore);
            System.out.println("Population Size: " + population.size());

            if(remainingGenerations-- == 0){
                continueGeneration = false;
            }

        }while(continueGeneration);


    }

    private static Map<Schedule, Double> mutatePopulation(Map<Schedule, Double> population, Model model, double mutationRate) {
        Map<Schedule, Double> newPopulation = new HashMap<>();

        population.keySet().parallelStream().forEach(schedule -> {
            schedule.assignments().forEach((activity, assignment) -> {
                Room room = (Math.random() > mutationRate) ? assignment.location() : Model.getRandomRoom(model);
                Date timeslot = (Math.random() > mutationRate) ? assignment.timeslot() : Model.getRandomTimeslot(model);
                String facilitator = (Math.random() > mutationRate) ? assignment.facilitator() : Model.getRandomFacilitator(model);

                Assignment newAssignment = new Assignment(activity, timeslot, facilitator, room);
                schedule.assignments().put(activity, newAssignment);
            });
            newPopulation.put(schedule, 0.0);
        });
        return newPopulation;
    }

    private static Map<Schedule, Double> crossoverPopulation(Map<Schedule, Double> population) {
        Map<Schedule, Double> newPopulation = new HashMap<>();

        List<Schedule> parentPopulation = new ArrayList<>(population.keySet());

        while(parentPopulation.size()>1){
            //get two random parents
            Schedule parent1 = parentPopulation.remove((int)(Math.random()*parentPopulation.size()));
            Schedule parent2 = parentPopulation.remove((int)(Math.random()*parentPopulation.size()));

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

        return newPopulation;
    }

    //The culling algorithm we are using allows for additional random change
    //that a strong candiate will be removed from the population
    //or a weak candidate will be kept
    private static Map<Schedule, Double> cullPopulation(Map<Schedule,Double> population) {
        return population;
        // Map<Schedule,Double> survivors = new HashMap<Schedule,Double>();

        //Since our population scores are a probability distribution
        //randomly select 66% of the population to survive
        
    }

    private static Map<Schedule, Double> scorePopulation(Model model, Map<Schedule, Double> population) {
        return population.entrySet().stream().parallel().collect(Collectors.toMap(Map.Entry::getKey, e -> ScheduleScorer.scoreSchedule(model, e.getKey())));
    }

    private static  Map<Schedule, Double> normalizeScores(Map<Schedule, Double> population) {
        final double sum = population.values().parallelStream().mapToDouble(Math::exp).sum();

       return population.entrySet().parallelStream().collect(Collectors.toMap(Map.Entry::getKey, e -> Math.exp(e.getValue()) / sum));
    }

    private static void generateInitialPopulation(final Map<Schedule, Double> population, Model model) {
        //initialize population randomly
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
            population.put(schedule, 0.0);
        });
   }
}