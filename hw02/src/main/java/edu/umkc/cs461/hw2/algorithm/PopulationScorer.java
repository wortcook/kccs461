package edu.umkc.cs461.hw2.algorithm;

import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentHashMap;

import edu.umkc.cs461.hw2.model.Model;
import edu.umkc.cs461.hw2.model.Schedule;
import edu.umkc.cs461.hw2.model.ValueSortedMap;
import edu.umkc.cs461.hw2.rules.ScheduleScorer;

/**
 * Interface for scoring a population of schedules.
 */
public interface PopulationScorer {
    default NavigableMap<Schedule, Double> scorePopulation(Model model, NavigableMap<Schedule, Double> population){
        return new PopulationDefaultScorer().scorePopulation(model, population);
    }

    /**
     * Default implementation of the scorePopulation method. This implementation scores the population by scoring each schedule in the population in parallel.
     */
    public static class PopulationDefaultScorer implements PopulationScorer {
        @Override
        public NavigableMap<Schedule, Double> scorePopulation(final Model model, final NavigableMap<Schedule, Double> population) {
            final Map<Schedule, Double> scored = new ConcurrentHashMap<>(population.size(), 1.0f, Runtime.getRuntime().availableProcessors());
            //iterate through population and score each schedule in parallel
            population.entrySet().parallelStream().forEach(e -> {
                scored.put(e.getKey(), ScheduleScorer.scoreSchedule(model, e.getKey()).score());
            });

            return new ValueSortedMap<>(scored);
        }
    }
}
