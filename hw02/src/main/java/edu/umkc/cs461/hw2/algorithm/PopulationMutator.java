package edu.umkc.cs461.hw2.algorithm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import edu.umkc.cs461.hw2.model.Assignment;
import edu.umkc.cs461.hw2.model.Model;
import edu.umkc.cs461.hw2.model.Room;
import edu.umkc.cs461.hw2.model.Schedule;
import edu.umkc.cs461.hw2.model.ValueSortedMap;

public interface PopulationMutator {
    default NavigableMap<Schedule, Double> mutatePopulation( Model model, Map<Schedule, Double> population, double mutationRate) {
        return new PopulationDefaultMutator().mutatePopulation(model, population, mutationRate);
    }

    public static class PopulationDefaultMutator implements PopulationMutator {
        @Override
        public NavigableMap<Schedule, Double> mutatePopulation( Model model, Map<Schedule, Double> population, double mutationRate) {
            final Map<Schedule,Double> mutations = new ConcurrentHashMap<Schedule,Double>(population.size(), 1.0f, Runtime.getRuntime().availableProcessors());

            population.keySet().parallelStream().forEach(schedule -> {
                schedule.assignments().forEach((activity, assignment) -> {
                    final Room room = (Math.random() > mutationRate) ? assignment.location() : Model.getRandomRoom(model);
                    final Date timeslot = (Math.random() > mutationRate) ? assignment.timeslot() : Model.getRandomTimeslot(model);
                    final String facilitator = (Math.random() > mutationRate) ? assignment.facilitator() : Model.getRandomFacilitator(model);

                    final Assignment newAssignment = new Assignment(activity, timeslot, facilitator, room);
                    schedule.assignments().put(activity, newAssignment);
                });
                mutations.put(schedule, 0.0);
            });

            return new ValueSortedMap<>(mutations);
        }
    }

    public static class ScaledProbabilityMutator implements PopulationMutator {
        @Override
        public NavigableMap<Schedule, Double> mutatePopulation( final Model model, final Map<Schedule, Double> population, final double mutationRate) {

            final Map<Schedule,Double> mutatedPopulation = new ConcurrentHashMap<Schedule,Double>(population.size(), 1.0f, Runtime.getRuntime().availableProcessors());
            final List<Schedule> schedules = new ArrayList<>(population.keySet());
            final int populationSize = population.size();

            IntStream.range(0, population.size()).parallel().forEach(i -> {
                    final Schedule schedule = schedules.get(i);

                    final double scheduleMutationRate = mutationRate * (1.0- ((double)i / (double)populationSize));

                    schedule.assignments().forEach((activity, assignment) -> {
                        Room room = (Math.random() > scheduleMutationRate) ? assignment.location() : Model.getRandomRoom(model);
                        Date timeslot = (Math.random() > scheduleMutationRate) ? assignment.timeslot() : Model.getRandomTimeslot(model);
                        String facilitator = (Math.random() > scheduleMutationRate) ? assignment.facilitator() : Model.getRandomFacilitator(model);

                        Assignment newAssignment = new Assignment(activity, timeslot, facilitator, room);
                        schedule.assignments().put(activity, newAssignment);
                    });
                    mutatedPopulation.put(schedule, 0.0);
            });

            return new ValueSortedMap<>(mutatedPopulation);
        }
    }
}
