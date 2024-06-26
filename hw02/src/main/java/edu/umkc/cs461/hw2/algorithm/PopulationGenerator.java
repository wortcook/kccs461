package edu.umkc.cs461.hw2.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.IntStream;

import edu.umkc.cs461.hw2.model.Activity;
import edu.umkc.cs461.hw2.model.Assignment;
import edu.umkc.cs461.hw2.model.Model;
import edu.umkc.cs461.hw2.model.Room;
import edu.umkc.cs461.hw2.model.Schedule;

/**
 * Interface for generating an initial population of schedules.
  */
public interface PopulationGenerator {

    default List<Schedule> generateInitialPopulation(final Model model, final int populationSize) {
        return new PopulationDefaultGenerator().generateInitialPopulation(model, populationSize);
    }

    /**
     * Default implementation of the generateInitialPopulation method. This implementation generates a population of schedules with random assignments.
     */
    public static class PopulationDefaultGenerator implements PopulationGenerator {
        @Override
        public List<Schedule> generateInitialPopulation(final Model model, final int populationSize) {
            //initialize population randomly

            List<Schedule> newPopulation = Collections.synchronizedList(new ArrayList<>(populationSize));

            IntStream.range(0, populationSize).parallel().forEach(i -> {
            //for (int i = 0; i < STARTER_POPULATION; i++) {
                NavigableMap<Activity, Assignment> assignments = new TreeMap<>();
                for(Activity activity : model.activities().values()){
                    assignments.put(
                        activity, 
                        new Assignment(
                            activity,
                            Model.getRandomTimeslot(model),
                            Model.getRandomFacilitator(model),
                            Model.getRandomRoom(model)
                        )
                    );
                }

                Schedule schedule = new Schedule(assignments);
                newPopulation.add(schedule);

                if((i % 10000 == 0)&&(i!=0)){
                    System.out.println(newPopulation.size() + " schedules generated");
                }
            });

            return newPopulation;
            
        }
    }

    /**
     * Seeks to guarantee that at least one value from each domain is represented in the initial population.
     */
    public static class PopulationSpreadGenerator implements PopulationGenerator {
        @Override
        public List<Schedule> generateInitialPopulation(final Model model, final int populationSize) {
            //initialize population randomly

            List<Schedule> newPopulation = Collections.synchronizedList(new ArrayList<>(populationSize));

            //Ensure that each domain has at least one value represented in the initial population

            //For each room, create a schedule with a random assignment for each activity
            for(Room room : model.locations().values()){
                NavigableMap<Activity, Assignment> assignments = new TreeMap<>();
                for(Activity activity : model.activities().values()){
                    //get a random timeslot
                    Date timeslot = model.timeslots().values().stream().skip((int) (model.timeslots().size() * Math.random())).findFirst().get();
                    //get a random facilitator
                    String faciliator = model.facilitators().values().stream().skip((int) (model.facilitators().size() * Math.random())).findFirst().get();

                    Assignment assignment = new Assignment(activity, timeslot, faciliator, room);
                    assignments.put(activity, assignment);
                }
                newPopulation.add(new Schedule(assignments));
            }

            //For each timeslot, create a schedule with a random assignment for each activity
            for(Date timeslot : model.timeslots().values()){
                NavigableMap<Activity, Assignment> assignments = new TreeMap<>();
                for(Activity activity : model.activities().values()){
                    //get a random room
                    Room room = model.locations().values().stream().skip((int) (model.locations().size() * Math.random())).findFirst().get();
                    //get a random facilitator
                    String faciliator = model.facilitators().values().stream().skip((int) (model.facilitators().size() * Math.random())).findFirst().get();

                    Assignment assignment = new Assignment(activity, timeslot, faciliator, room);
                    assignments.put(activity, assignment);
                }
                newPopulation.add(new Schedule(assignments));
            }

            //For each facilitator, create a schedule with a random assignment for each activity
            for(String facilitator : model.facilitators().values()){
                NavigableMap<Activity, Assignment> assignments = new TreeMap<>();
                for(Activity activity : model.activities().values()){
                    //get a random room
                    Room room = model.locations().values().stream().skip((int) (model.locations().size() * Math.random())).findFirst().get();
                    //get a random timeslot
                    Date timeslot = model.timeslots().values().stream().skip((int) (model.timeslots().size() * Math.random())).findFirst().get();

                    Assignment assignment = new Assignment(activity, timeslot, facilitator, room);
                    assignments.put(activity, assignment);
                }
                newPopulation.add(new Schedule(assignments));
            }

            //Fill in the rest of the population randomly
            IntStream.range(newPopulation.size(), populationSize).parallel().forEach(i -> {
            //for (int i = 0; i < STARTER_POPULATION; i++) {
                NavigableMap<Activity, Assignment> assignments = new TreeMap<>();
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

                newPopulation.add(new Schedule(assignments));

                if((i % 10000 == 0)&&(i!=0)){
                    System.out.println(newPopulation.size() + " schedules generated");
                }
            });

            return newPopulation;
        }
    }
}
