package edu.umkc.cs461.hw2.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.IntStream;

import edu.umkc.cs461.hw2.model.Model;
import edu.umkc.cs461.hw2.model.Activity;
import edu.umkc.cs461.hw2.model.Assignment;
import edu.umkc.cs461.hw2.model.Room;
import edu.umkc.cs461.hw2.model.Schedule;

/**
 * PopulationCrossover
 * The PopulationCrossover interface defines the method to crossover a population of schedules. The crossover
 * method takes a model and a population of schedules and returns a new population of schedules. The crossover
 * method is responsible for selecting two parents from the population and creating a child schedule by randomly
 * selecting the room, time, and facilitator for each activity from one of the parents.
 * 
 * See the specific implementation classes for more details on how the crossover is performed.
 */
public interface PopulationCrossover {
    default List<Schedule> crossoverPopulation(Model model, List<Schedule> parentPopulation) {
        return new PopulationDefaultCrossover().crossoverPopulation(model, parentPopulation);
    }

    /**
     * Default crossover implementation. This implementation first shuffles the order of the population and then
     * and then splits it into two halfs. It them "zippers" the two collections together to create a new population
     * by randomly selecting the room, time, and facilitator for each activity from one of the parents.
     * Each attribute is selected with a 50% probability from one of the parents.
     */
    public static class PopulationDefaultCrossover implements PopulationCrossover {
        @Override
        public List<Schedule> crossoverPopulation(final Model model, final List<Schedule> population) {

            final List<Schedule> newPopulation = Collections.synchronizedList(new ArrayList<>(population.size()/2+1));
            final ArrayList<Schedule> parentPopulation = new ArrayList<>(population);
            Collections.shuffle(parentPopulation);

            //Split the list into two halves
            final List<Schedule> primary = parentPopulation.subList(0, parentPopulation.size()/2);
            final List<Schedule> secondary = parentPopulation.subList(parentPopulation.size()/2, parentPopulation.size());

            final int halfIndex = parentPopulation.size()/2;

            //Run the loop population/2 in parallel
            IntStream.range(0, halfIndex).parallel().forEach(i -> {

                //Pick a parent from each sublist
                final Schedule parent1 = primary.get(i);
                final Schedule parent2 = secondary.get(i);

                //for each activity, randomly select room, time, facilitator from one of the parents
                final NavigableMap<Activity, Assignment> childMap = new TreeMap<>();
                for(Activity activity : parent1.assignments().keySet()){
                    final Assignment assignment1 = parent1.assignments().get(activity);
                    final Assignment assignment2 = parent2.assignments().get(activity);

                    final Room childRoom = Math.random() < 0.5 ? assignment1.location() : assignment2.location();
                    final Date childTime = Math.random() < 0.5 ? assignment1.timeslot() : assignment2.timeslot();
                    final String childFacilitator = Math.random() < 0.5 ? assignment1.facilitator() : assignment2.facilitator();

                    final Assignment childAssignment = new Assignment(activity, childTime, childFacilitator, childRoom);
                    
                    childMap.put(activity, childAssignment);
                }

                newPopulation.add(new Schedule(childMap));
            });

            List<Schedule> retPop = new ArrayList<>(newPopulation);
            retPop.addAll(parentPopulation); //add parent population

            return retPop;
        }
    }

    /**
     * RandomSelectionCrossover
     * The cross-over randomly selects two parents from the passed population and randomly selects the 
     * room, time, and facilitator for each activity from one of the parents. The cross-over for each
     * element in the population. The random selection of parents means that a parent can be selected
     * multiple times for cross-over and that some parents will not be selected at all. The selection
     * does not take fitness into account.
     */
    public static class RandomSelectionCrossover implements PopulationCrossover {
        @Override
        public List<Schedule> crossoverPopulation(final Model model, final List<Schedule> population) {

            final List<Schedule> newPopulation = Collections.synchronizedList(new ArrayList<>(population.size()/2+1));

            final List<Schedule> parentPopulation = new ArrayList<>(population);

            final int maxCount = population.size()/2;

            //Run the loop population/2 in parallel, i.e. produce population/2 children
            IntStream.range(0, maxCount).parallel().forEach(
                i -> {
                    //Randomly select two parents, note, it's possible to select the same parent twice
                    final Schedule parent1 = parentPopulation.get((int)(Math.random()*(parentPopulation.size()-1)));
                    final Schedule parent2 = parentPopulation.get((int)(Math.random()*(parentPopulation.size()-1)));

                    //for each activity, randomly select room, time, facilitator from one of the parents
                    final NavigableMap<Activity, Assignment> childMap = new TreeMap<>();
                    for(Activity activity : parent1.assignments().keySet()){
                        final Assignment assignment1 = parent1.assignments().get(activity);
                        final Assignment assignment2 = parent2.assignments().get(activity);

                        final Room childRoom = Math.random() < 0.5 ? assignment1.location() : assignment2.location();
                        final Date childTime = Math.random() < 0.5 ? assignment1.timeslot() : assignment2.timeslot();
                        final String childFacilitator = Math.random() < 0.5 ? assignment1.facilitator() : assignment2.facilitator();

                        final Assignment childAssignment = new Assignment(activity, childTime, childFacilitator, childRoom);
                        
                        childMap.put(activity, childAssignment);
                    }

                    newPopulation.add(new Schedule(childMap));
                }
            );
    
            List<Schedule> retPop = new ArrayList<>(newPopulation);
            retPop.addAll(parentPopulation); //add parent population

            return retPop;
        }
    }

    /**
     * Crossover where there is some choice on the part of one of the parents. This crossover iterates
     * from 0 to half the population size (the number of children to be produced). A random parent is selected
     * and then 10 other parents are randomly selected. The parent from the 10 with the best score is selected
     * as the second parent. The child is then created by randomly selecting the room, time, and facilitator for
     * each activity from one of the parents.
     * There is a 50% chance that the child will select the room, time, and facilitator from parent1 and a 50% chance
     * that the child will select the room, time, and facilitator from parent2.
     */
    public static class RandomValueSelectionCrossover implements PopulationCrossover {
        @Override
        public List<Schedule> crossoverPopulation(final Model model, final List<Schedule> population) {
            return crossoverPopulation(model, population, 10);
        }

        public List<Schedule> crossoverPopulation(final Model model, final List<Schedule> population, final int subSelectCount) {
            final List<Schedule> newPopulation = Collections.synchronizedList(new ArrayList<>(population.size()/2+1));

            final List<Schedule> parentPopulation = new ArrayList<>(population);

            final int maxCount = population.size()/2;

            //Run the loop population/2 in parallel, i.e. produce population/2 children
            IntStream.range(0, maxCount).parallel().forEach(
                i -> {
                    //Randomly select the initial parent
                    final Schedule parent1 = parentPopulation.get((int)(Math.random()*(parentPopulation.size()-1)));

                    //Randomly select 10 other parents
                    final List<Schedule> parent2List = new ArrayList<>(subSelectCount);
                    for(int j = 0; j < subSelectCount; j++){
                        parent2List.add(parentPopulation.get((int)(Math.random()*(parentPopulation.size()-1))));
                    }

                    //get the best score
                    final Schedule parent2 = Model.sortPopulation(parent2List, model).getLast();

                    //for each activity, randomly select room, time, facilitator from one of the parents
                    final NavigableMap<Activity, Assignment> childMap = new TreeMap<>();
                    for(Activity activity : parent1.assignments().keySet()){
                        final Assignment assignment1 = parent1.assignments().get(activity);
                        final Assignment assignment2 = parent2.assignments().get(activity);

                        final Room childRoom = Math.random() < 0.5 ? assignment1.location() : assignment2.location();
                        final Date childTime = Math.random() < 0.5 ? assignment1.timeslot() : assignment2.timeslot();
                        final String childFacilitator = Math.random() < 0.5 ? assignment1.facilitator() : assignment2.facilitator();

                        final Assignment childAssignment = new Assignment(activity, childTime, childFacilitator, childRoom);
                        
                        childMap.put(activity, childAssignment);
                    }

                    newPopulation.add(new Schedule(childMap));
                }
            );
    
            List<Schedule> retPop = new ArrayList<>(newPopulation);
            retPop.addAll(parentPopulation); //add parent population

            return retPop;
        }
    }
}
