package edu.umkc.cs461.hw2.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.stream.Collectors;

import edu.umkc.cs461.hw2.model.Model;
import edu.umkc.cs461.hw2.model.Schedule;
import edu.umkc.cs461.hw2.model.ValueSortedMap;

public interface PopulationCuller {
    default NavigableMap<Schedule, Double> cullPopulation(final Model model, final Map<Schedule,Double> population, final int targetSize){
        return new PopulationDefaultCuller().cullPopulation(model, population, targetSize);
    }

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
}
