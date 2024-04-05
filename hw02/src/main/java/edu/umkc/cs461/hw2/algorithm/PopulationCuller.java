package edu.umkc.cs461.hw2.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import edu.umkc.cs461.hw2.model.Model;
import edu.umkc.cs461.hw2.model.Schedule;

/**
 * Interface for culling a population of schedules.
 */
public interface PopulationCuller {
    default List<Schedule> cullPopulation(final Model model, final List<Schedule> population, final int targetSize){
        return new PopulationDefaultCuller().cullPopulation(model, population, targetSize);
    }

    /**
     * Default implementation of the cullPopulation method. This implementation culls the population to the target size by removing the lowest scoring schedules.
     * The cull percentage starts at 50% and trends down to 33.33% as the population size nears the target size. This is to maintain the target size after crossover.
     * i.e. if we cull 1/3 of the population that will leave 2/3 of the population. If we then crossover we will then product another 1/3 of the population
     * which will bring the population back to the target size.
     */
    public static class PopulationDefaultCuller implements PopulationCuller {
        public List<Schedule> cullPopulation(final Model model, final List<Schedule> population, final int targetSize) {

            //calculate the size of the population to cull
            //Start near 50% and then trend down to 33.33% which will maintain the target size after crossover.
            final double ratio = ((double)targetSize)/((double)population.size());

            final double cullPercentage = 1.0 - (0.5 - (0.16666667 * ratio));

            int cullSize = (int)(population.size() * cullPercentage);

            return Model.sortPopulation(population, model).reversed().subList(0, cullSize);

        }
    }

    public static class PopulationUniqueCuller implements PopulationCuller {
        @Override
        public List<Schedule> cullPopulation(final Model model, final List<Schedule> population, final int targetSize) {
            final List<Schedule> returnCull = new PopulationDefaultCuller().cullPopulation(model, population, targetSize);
            Set<Schedule> uniqueSchedules = new HashSet<>(returnCull);
            return new ArrayList<>(uniqueSchedules);
        }
    }

    /**
     * Implementation of the cullPopulation method that backfills the population with random schedules if the population size falls below the target size.
     * Otherwise it uses the default culler to cull the population to the target size.
     */
    public static class BackfillCuller implements PopulationCuller {
        @Override
        public List<Schedule> cullPopulation(final Model model, final List<Schedule> population, final int targetSize) {

            //Cull as normal
            List<Schedule> returnCull = new PopulationDefaultCuller().cullPopulation(model, population, targetSize);

            final int cullTarget = (2*targetSize)/3;

            //backfill the population randomly if we fall below the target size
            if(returnCull.size() < cullTarget){
                int backfillSize = cullTarget - returnCull.size() + (int)((targetSize/100.0)*Math.random());

                returnCull.addAll(
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
        public List<Schedule> cullPopulation(final Model model, final List<Schedule> population, final int targetSize) {

            final List<Schedule> survivors = Collections.synchronizedList(new ArrayList<>());

            final int populationSize = population.size();

            //As the population size nears the target size, the cull rate trends down to 33.33%
            final double ratioThird = 0.333333*((double)targetSize)/((double)populationSize);

            final List<Schedule> sortedPopSchedule = Model.sortPopulation(population, model);

            //For each schedule in the population
            IntStream.range(0, sortedPopSchedule.size()).parallel().forEach(i ->{
                final Schedule schedule = sortedPopSchedule.get(i);

                //Calculate the survival rate for the schedule
                //Start at 100% and trend down to 0% survial rate from best to worst schedule
                //The ratio third is used to ensure we don't cull too much and fall below the target size
                final double scheduleSurvivalRate = ((double)i / (double)populationSize) + ratioThird;

                //If the schedule survives, add it to the survivor map
                if(Math.random() < scheduleSurvivalRate){
                    survivors.add(schedule);
                }
            });

            //safety net to ensure we don't fall too below the target size, if we don't do this
            //the population tends to collapse.
            if(survivors.size() < targetSize/3){
                final int backfillSize = targetSize - survivors.size();

                survivors.addAll(
                    (new PopulationGenerator.PopulationDefaultGenerator()).generateInitialPopulation(model, backfillSize)
                );
            }

            return survivors;
        }
    }
}
