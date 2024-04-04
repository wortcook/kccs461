package edu.umkc.cs461.hw2.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import edu.umkc.cs461.hw2.model.Model;
import edu.umkc.cs461.hw2.model.Activity;
import edu.umkc.cs461.hw2.model.Assignment;
import edu.umkc.cs461.hw2.model.Room;
import edu.umkc.cs461.hw2.model.Schedule;
import edu.umkc.cs461.hw2.model.ValueSortedMap;

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
    default NavigableMap<Schedule, Double> crossoverPopulation(Model model, Map<Schedule, Double> population) {
        return new PopulationDefaultCrossover().crossoverPopulation(model, population);
    }

    /**
     * Default crossover implementation. This implementation first shuffles the order of the population and then
     * and then splits it into two halfs. It them "zippers" the two collections together to create a new population
     * by randomly selecting the room, time, and facilitator for each activity from one of the parents.
     * Each attribute is selected with a 50% probability from one of the parents.
     */
    public static class PopulationDefaultCrossover implements PopulationCrossover {
        @Override
        public NavigableMap<Schedule, Double> crossoverPopulation(final Model model, final Map<Schedule, Double> population) {
            final Map<Schedule, Double> newPopulation = new ConcurrentHashMap<>(population.size()/2+1, 1.0f, Runtime.getRuntime().availableProcessors());
            final List<Schedule> parentPopulation = new ArrayList<>(population.keySet());

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
                Map<Activity, Assignment> childMap = new HashMap<>();
                for(Activity activity : parent1.assignments().keySet()){
                    final Assignment assignment1 = parent1.assignments().get(activity);
                    final Assignment assignment2 = parent2.assignments().get(activity);

                    final Room childRoom = Math.random() < 0.5 ? assignment1.location() : assignment2.location();
                    final Date childTime = Math.random() < 0.5 ? assignment1.timeslot() : assignment2.timeslot();
                    final String childFacilitator = Math.random() < 0.5 ? assignment1.facilitator() : assignment2.facilitator();

                    final Assignment childAssignment = new Assignment(activity, childTime, childFacilitator, childRoom);
                    
                    childMap.put(activity, childAssignment);
                }

                newPopulation.put(new Schedule(childMap), 0.0);
            });

            final ValueSortedMap<Schedule, Double> retPop = new ValueSortedMap<>(newPopulation);
            retPop.putAll(population); //includes parents in return population as well

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
        public NavigableMap<Schedule, Double> crossoverPopulation(final Model model, final Map<Schedule, Double> population) {

            final Map<Schedule, Double> newPopulation = new ConcurrentHashMap<>(population.size()/2+1, 1.0f, Runtime.getRuntime().availableProcessors());

            final List<Schedule> parentPopulation = new ArrayList<>(population.keySet());

            final int maxCount = population.size()/2;

            //Run the loop population/2 in parallel, i.e. produce population/2 children
            IntStream.range(0, maxCount).parallel().forEach(
                i -> {
                    //Randomly select two parents, note, it's possible to select the same parent twice
                    final Schedule parent1 = parentPopulation.get((int)(Math.random()*(parentPopulation.size()-1)));
                    final Schedule parent2 = parentPopulation.get((int)(Math.random()*(parentPopulation.size()-1)));

                    //for each activity, randomly select room, time, facilitator from one of the parents
                    final Map<Activity, Assignment> childMap = new HashMap<>();
                    for(Activity activity : parent1.assignments().keySet()){
                        final Assignment assignment1 = parent1.assignments().get(activity);
                        final Assignment assignment2 = parent2.assignments().get(activity);

                        final Room childRoom = Math.random() < 0.5 ? assignment1.location() : assignment2.location();
                        final Date childTime = Math.random() < 0.5 ? assignment1.timeslot() : assignment2.timeslot();
                        final String childFacilitator = Math.random() < 0.5 ? assignment1.facilitator() : assignment2.facilitator();

                        final Assignment childAssignment = new Assignment(activity, childTime, childFacilitator, childRoom);
                        
                        childMap.put(activity, childAssignment);
                    }

                    newPopulation.put(new Schedule(childMap), 0.0);
                }
            );
            final ValueSortedMap<Schedule, Double> retPop = new ValueSortedMap<>(newPopulation);
            retPop.putAll(population); //add parent population

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
        public NavigableMap<Schedule, Double> crossoverPopulation(final Model model, final Map<Schedule, Double> population) {
            return crossoverPopulation(model, population, 10);
        }

        public NavigableMap<Schedule, Double> crossoverPopulation(final Model model, final Map<Schedule, Double> population, final int subSelectCount) {

            final Map<Schedule, Double> newPopulation = new ConcurrentHashMap<>(population.size()/2+1, 1.0f, Runtime.getRuntime().availableProcessors());

            final List<Schedule> parentPopulation = new ArrayList<>(population.keySet());

            final PopulationScorer scorer = new PopulationScorer.PopulationDefaultScorer();

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

                    //Make sure the 10 selected are scored
                    final ValueSortedMap<Schedule, Double> parent2Map = new ValueSortedMap<>();
                    for(Schedule parent2 : parent2List){
                        parent2Map.put(parent2, 0.0);
                    }

                    scorer.scorePopulation(model, parent2Map);

                    //get the best score
                    final Schedule parent2 = parent2Map.lastKey();

                    //for each activity, randomly select room, time, facilitator from one of the parents
                    final Map<Activity, Assignment> childMap = new HashMap<>();
                    for(Activity activity : parent1.assignments().keySet()){
                        final Assignment assignment1 = parent1.assignments().get(activity);
                        final Assignment assignment2 = parent2.assignments().get(activity);

                        final Room childRoom = Math.random() < 0.5 ? assignment1.location() : assignment2.location();
                        final Date childTime = Math.random() < 0.5 ? assignment1.timeslot() : assignment2.timeslot();
                        final String childFacilitator = Math.random() < 0.5 ? assignment1.facilitator() : assignment2.facilitator();

                        final Assignment childAssignment = new Assignment(activity, childTime, childFacilitator, childRoom);
                        
                        childMap.put(activity, childAssignment);
                    }

                    newPopulation.put(new Schedule(childMap), 0.0);
                }
            );
            final ValueSortedMap<Schedule, Double> retPop = new ValueSortedMap<>(newPopulation);
            retPop.putAll(population); //add parent population

            return retPop;
        }
    }
}
