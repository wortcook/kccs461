package edu.umkc.cs461.hw2.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import edu.umkc.cs461.hw2.model.Assignment;
import edu.umkc.cs461.hw2.model.Model;
import edu.umkc.cs461.hw2.model.Room;
import edu.umkc.cs461.hw2.model.Schedule;
import edu.umkc.cs461.hw2.model.ValueSortedMap;

/**
 * Interface for mutating a population of schedules.
 */
public interface PopulationMutator {
    default List<Schedule> mutatePopulation( Model model, List<Schedule> population, double mutationRate) {
        return new PopulationDefaultMutator().mutatePopulation(model, population, mutationRate);
    }

    /**
     * Default implementation of the mutatePopulation method. This implementation mutates the population by randomly changing the room, timeslot, and facilitator of each assignment.
     * Each schedule has an equal chance of being mutated.
     */
    public static class PopulationDefaultMutator implements PopulationMutator {
        @Override
        public List<Schedule> mutatePopulation( Model model, List<Schedule> population, double mutationRate) {
            final List<Schedule> mutations = Collections.synchronizedList(new ArrayList<>());

            //For all schedules
            population.parallelStream().forEach(schedule -> {

                //For all assignments in the schedule
                schedule.assignments().forEach((activity, assignment) -> {

                    //Mutate the assignment based on the mutation rate. Each assignment has a chance to be mutated.
                    final Room room = (Math.random() > mutationRate) ? assignment.location() : Model.getRandomRoom(model);
                    final Date timeslot = (Math.random() > mutationRate) ? assignment.timeslot() : Model.getRandomTimeslot(model);
                    final String facilitator = (Math.random() > mutationRate) ? assignment.facilitator() : Model.getRandomFacilitator(model);

                    final Assignment newAssignment = new Assignment(activity, timeslot, facilitator, room);
                    schedule.assignments().put(activity, newAssignment);
                });
                mutations.add(schedule);
            });

            return mutations;
        }
    }

    /**
     * Implementation of the mutatePopulation method that scales the mutation rate based on the position of the schedule in the population.
     * The higher the position of the schedule in the population the lower the mutation rate.
     */
    public static class ScaledProbabilityMutator implements PopulationMutator {
        @Override
        public List<Schedule> mutatePopulation( final Model model, final List<Schedule> population, final double mutationRate) {
            final List<Schedule> mutations = Collections.synchronizedList(new ArrayList<>(population.size()));
            final List<Schedule> sortedSchedules = Model.sortPopulation(population, model);

            final int populationSize = population.size();

            //For all schedules
            IntStream.range(0, population.size()).parallel().forEach(i -> {
                    final Schedule schedule = sortedSchedules.get(i);

                    //The mutation rate scales down as the schedule position increases, i.e. more fit schedules are less likely to mutate
                    final double scheduleMutationRate = mutationRate * (1.0- ((double)i / (double)populationSize));

                    schedule.assignments().forEach((activity, assignment) -> {
                        Room room = (Math.random() > scheduleMutationRate) ? assignment.location() : Model.getRandomRoom(model);
                        Date timeslot = (Math.random() > scheduleMutationRate) ? assignment.timeslot() : Model.getRandomTimeslot(model);
                        String facilitator = (Math.random() > scheduleMutationRate) ? assignment.facilitator() : Model.getRandomFacilitator(model);

                        Assignment newAssignment = new Assignment(activity, timeslot, facilitator, room);
                        schedule.assignments().put(activity, newAssignment);
                    });
                    mutations.add(schedule);
            });

            return mutations;
        }
    }
}
