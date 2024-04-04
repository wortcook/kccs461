package edu.umkc.cs461.hw2;

import java.util.LinkedList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Queue;

import edu.umkc.cs461.hw2.algorithm.PopulationCrossover;
import edu.umkc.cs461.hw2.algorithm.PopulationCuller;
import edu.umkc.cs461.hw2.algorithm.PopulationGenerator;
import edu.umkc.cs461.hw2.algorithm.PopulationMutator;
import edu.umkc.cs461.hw2.algorithm.PopulationScorer;
import edu.umkc.cs461.hw2.model.*;
import edu.umkc.cs461.hw2.rules.*;

public class Main {

    public static final int STARTER_POPULATION = 100000;
    public static final int TARGET_STABLE_POPULATION_SIZE = 1000;

    public static final double MUTATION_RATE = 0.8;
    public static final double MUTATION_RATE_DECAY = 0.95;

    public static final int MIN_GENERATION_COUNT = 500;

    public static final int LAST_N_SCORES = 10;

    public static void main(String[] args) {
        Model model = ModelLoader.loadModel();

        // PopulationGenerator generator = new PopulationGenerator.PopulationDefaultGenerator();
        PopulationGenerator generator = new PopulationGenerator.PopulationSpreadGenerator();
        PopulationScorer scorer = new PopulationScorer.PopulationDefaultScorer();

        // PopulationCuller culler = new PopulationCuller.PopulationDefaultCuller();
        // PopulationCuller culler = new PopulationCuller.BackfillCuller();
        PopulationCuller culler = new PopulationCuller.RandomCuller();

        // PopulationCrossover crossover = new PopulationCrossover.PopulationDefaultCrossover();
        PopulationCrossover crossover = new PopulationCrossover.RandomSelectionCrossover();
        // PopulationCrossover crossover = new PopulationCrossover.RandomValueSelectionCrossover();

        // PopulationMutator mutator = new PopulationMutator.PopulationDefaultMutator();
        PopulationMutator mutator = new PopulationMutator.ScaledProbabilityMutator();

        NavigableMap<Schedule, Double> population = generator.generateInitialPopulation(model, STARTER_POPULATION);
        population = scorer.scorePopulation(model, population);

        // population = normalizeScores(population);

        //sort the population by score
        boolean continueGeneration = true;

        double mutationRate = MUTATION_RATE;

        int remainingGenerations = MIN_GENERATION_COUNT;

        List<Double> lastNScores = new LinkedList<>();

        int generationCount = 1;
        do{
            System.out.println("=====================================");
            System.out.println("Population Size: " + population.size());
            System.out.println("Mutation Rate: " + mutationRate);
            System.out.println("Generation: " + generationCount++);

            System.out.println("Starting Cull");
            population = culler.cullPopulation(model, population, TARGET_STABLE_POPULATION_SIZE);
            population = scorer.scorePopulation(model, population);
            System.out.println("Population Size After Cull: " + population.size());

            System.out.println("Starting Crossover");
            population = crossover.crossoverPopulation(model, population);
            population = scorer.scorePopulation(model, population);
            System.out.println("Population Size After Crossover: " + population.size());

            System.out.println("Starting Mutation");
            population = mutator.mutatePopulation(model, population, mutationRate);
            population = scorer.scorePopulation(model, population);
            System.out.println("Population Size After Mutation: " + population.size());

            Schedule bestSchedule = population.lastKey();
            Double bestScore = population.get(bestSchedule);

            Schedule worstSchedule = population.firstKey();
            Double worstScore = population.get(worstSchedule);

            System.out.println("Best Score: " + bestScore);
            System.out.println("Worst Score: " + worstScore);

            if(remainingGenerations == LAST_N_SCORES+1){
                lastNScores.addFirst(bestScore);
                if(lastNScores.size() > LAST_N_SCORES){
                    lastNScores.removeLast();

                    double averageScore = lastNScores.stream().mapToDouble(Double::doubleValue).average().getAsDouble();

                    if(Math.abs(averageScore - bestScore) < 0.001){
                        continueGeneration = false;
                    }
                }
            }else{
                remainingGenerations--;
            }


            mutationRate = mutationRate * MUTATION_RATE_DECAY;


        }while(continueGeneration);

        //Now lets print out the best schedule as a table
        Schedule bestSchedule = population.lastKey();

        System.out.println("Best Schedule");
        //Print out the schedule as a table of M W F and the timeslots
        System.out.println(Schedule.scheduleToString(bestSchedule));

        Scorer.ScheduleScore score = ScheduleScorer.scoreSchedule(model, bestSchedule);
        System.out.println("Score: " + score.score());
        System.out.println("Score Breakdown:");
        score.scoreBreakdown().entrySet().forEach(e -> {
            System.out.println(e.getKey() + ": " + e.getValue());
        });

        System.out.println();

        System.out.println("Worst Schedule");
        Schedule worstSchedule = population.firstKey();
        System.out.println(Schedule.scheduleToString(worstSchedule));

        score = ScheduleScorer.scoreSchedule(model, worstSchedule);
        System.out.println("Score: " + score.score());
        System.out.println("Score Breakdown:");
        score.scoreBreakdown().entrySet().forEach(e -> {
            System.out.println(e.getKey() + ": " + e.getValue());
        });



    }
}