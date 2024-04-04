package edu.umkc.cs461.hw2.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.umkc.cs461.hw2.model.Model;
import edu.umkc.cs461.hw2.model.Schedule;
import edu.umkc.cs461.hw2.model.ValueSortedMap;

/**
 * Interface for culling a population of schedules.
 */
public interface PopulationCuller {
    default NavigableMap<Schedule, Double> cullPopulation(final Model model, final Map<Schedule,Double> population, final int targetSize){
        return new PopulationDefaultCuller().cullPopulation(model, population, targetSize);
    }

    /**
     * Default implementation of the cullPopulation method. This implementation culls the population to the target size by removing the lowest scoring schedules.
     * The cull percentage starts at 50% and trends down to 33.33% as the population size nears the target size. This is to maintain the target size after crossover.
     * i.e. if we cull 1/3 of the population that will leave 2/3 of the population. If we then crossover we will then product another 1/3 of the population
     * which will bring the population back to the target size.
     */
    public static class PopulationDefaultCuller implements PopulationCuller {
        public NavigableMap<Schedule, Double> cullPopulation(final Model model, final Map<Schedule,Double> population, final int targetSize) {

            //calculate the size of the population to cull
            //Start near 50% and then trend down to 33.33% which will maintain the target size after crossover.

            // double ratio = (Math.log10((double)targetSize)/Math.log10((double)population.size()));
            double ratio = ((double)targetSize)/((double)population.size());

            double cullPercentage = 1.0 - (0.5 - (0.16666667 * ratio));

            int cullSize = (int)(population.size() * cullPercentage);

            //cull the population
            final List<Schedule> cullList = new ArrayList<>(population.keySet()).reversed().subList(0, cullSize);

            return new ValueSortedMap<Schedule,Double>(cullList.stream().collect(Collectors.toMap(s -> s, s -> 0.0)));
        }
    }

    /**
     * Implementation of the cullPopulation method that backfills the population with random schedules if the population size falls below the target size.
     * Otherwise it uses the default culler to cull the population to the target size.
     */
    public static class BackfillCuller implements PopulationCuller {
        @Override
        public NavigableMap<Schedule, Double> cullPopulation(final Model model, final Map<Schedule, Double> population, final int targetSize) {
            NavigableMap<Schedule, Double> returnCull = new PopulationDefaultCuller().cullPopulation(model, population, targetSize);

            final int cullTarget = (2*targetSize)/3;

            if(returnCull.size() < cullTarget){
                int backfillSize = cullTarget - returnCull.size() + (int)((targetSize/100.0)*Math.random());

                returnCull.putAll(
                    (new PopulationGenerator.PopulationDefaultGenerator()).generateInitialPopulation(model, backfillSize)
                );
            }

            return returnCull;
        }
    }

    /**
     * Implementation of the cullPopulation method that culls the population by randomly selecting schedules to keep. The probability of keeping a schedule
     * is based on the schedule's position in the sorted population. The first schedule has a 100% chance of being kept, the schedule in the middle of the
     * population has a 50% chance of being kept, and the last schedule has a 0% chance of being kept. This nets to a 50% cull rate overall (the intergral).
     * 
     * The cull percentage starts at 50% and trends down to 33.33% as the population size nears the target size. This is to maintain the target size after crossover.
     */
    public static class RandomCuller implements PopulationCuller {
        @Override
        public NavigableMap<Schedule, Double> cullPopulation(final Model model, final Map<Schedule, Double> population, final int targetSize) {

            final List<Schedule> schedules = new ArrayList<>(population.keySet());

            final Map<Schedule,Double> survivorMap = new ConcurrentHashMap<Schedule,Double>(population.size(), 1.0f, Runtime.getRuntime().availableProcessors());

            final int populationSize = population.size();

            final double ratioThird = 0.333333*((double)targetSize)/((double)populationSize);

            IntStream.range(0, population.size()).parallel().forEach(i ->{
                final Schedule schedule = schedules.get(i);

                final double scheduleSurvivalRate = ((double)i / (double)populationSize) + ratioThird;

                if(Math.random() < scheduleSurvivalRate){
                    survivorMap.put(schedule, population.get(schedule));
                }
            });

            //safety net to ensure we don't fall too below the target size, if we don't do this
            //the population tends to collapse.
            if(survivorMap.size() < targetSize/3){
                final int backfillSize = targetSize - survivorMap.size();

                survivorMap.putAll(
                    (new PopulationGenerator.PopulationDefaultGenerator()).generateInitialPopulation(model, backfillSize)
                );
            }

            return new ValueSortedMap<Schedule,Double>(survivorMap);
        }
    }
}
