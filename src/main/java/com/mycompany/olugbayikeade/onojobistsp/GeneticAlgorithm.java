/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.olugbayikeade.onojobistsp;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Olugbayike
 */
public class GeneticAlgorithm extends FileScanner{
    Conversions conv = new Conversions();
//    Chromosome[] population; 
    ArrayList<Chromosome> population;
    int populationSize;
    double totalFitness= 0.0;
    double totalInverseFitness = 0.0;
    double probabilityTotal = 0.0;
    private final double mutationRate;
    private final double crossoverChance;
    private int k;
    Random rand;
    Random r;
    
//    private Population population;
//    private Population initialPop;
    private int maxGen;             // The number of generations to run.
//    private int k;                  // For tournament selection.
    private int elitismValue;       // Quantity of Elite to carry along each generation.
    private double crossoverRate;   // Odds of crossover occurring.
//    private double mutationRate;    // Odds of mutation occurring.
    private boolean forceUniqueness;// If true, population always has all unique members.
    private double localSearchRate; // Odds of local search occurring on entire generation.
    private Random random;
//    private CrossoverType crossoverType = CrossoverType.UNIFORM_ORDER;
//    private MutationType mutationType = MutationType.INSERTION;

    private boolean finished;

    // Results
    private int averageDistanceOfFirstGeneration;
    private int averageDistanceOfLastGeneration;
    private Chromosome bestDistanceOfFirstGeneration;
    private Chromosome bestDistanceOfLastGeneration;
    private ArrayList<Integer> averageDistanceOfEachGeneration;
    private ArrayList<Chromosome> bestDistanceOfEachGeneration;
    private int areaUnderAverageDistances;
    private int areaUnderBestDistances;
    
    // There is a 1 in 4 chance that fittest individual is not selected.
    private static final int ODDS_OF_NOT_PICKING_FITTEST = 4;

    
    /**
     * The GeneticAlgorithm constructor accepts 4 parameters
     *  
     * @param filePath: String containing the text file location.
     * @param InitialPopulation: integer containing the initial population.
     * @param InitialPopulation should be 3x the city count.
     * @param mutationRate: integer containing rate at which the individuals in the population would get mutated
     * @param crossoverChance: integer containing rate at which crossover between parents in the population would occur.
     * @param elitismValue: integer containing quantity of Elite to carry along each generation.
     * @param maxGen: integer containing the number of generations to run.
     * 
     */
    public GeneticAlgorithm(String filePath, int populationSize, double mutationRate, double crossoverChance, int elitismValue, int maxGen) throws FileNotFoundException {
        super(filePath);
        //obchrom = new Chromosome(filePath, cityArray);
        this.elitismValue = elitismValue;
        this.crossoverChance = crossoverChance;
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.maxGen = maxGen;
        population = new ArrayList<>();
        rand = new Random();
        random = new Random();
        r = new Random();
        populate();
        k = 3;
        
        averageDistanceOfEachGeneration = new ArrayList<>();
        bestDistanceOfEachGeneration = new ArrayList<>();
    }
    
    
    public int getAverageDistanceOfFirstGeneration () {
        if (!finished) {
            throw new IllegalArgumentException("Genetic algorithm was never run.");
        }
        return averageDistanceOfFirstGeneration;
    }

    public int getAverageDistanceOfLastGeneration () {
        if (!finished) {
            throw new IllegalArgumentException("Genetic algorithm was never run.");
        }
        return averageDistanceOfLastGeneration;
    }

    public Chromosome getBestDistanceOfFirstGeneration () {
        if (!finished) {
            throw new IllegalArgumentException("Genetic algorithm was never run.");
        }
        return bestDistanceOfFirstGeneration;
    }

    public Chromosome getBestDistanceOfLastGeneration () {
        if (!finished) {
            throw new IllegalArgumentException("Genetic algorithm was never run.");
        }
        return bestDistanceOfLastGeneration;
    }

    public ArrayList<Integer> getAverageDistanceOfEachGeneration() {
        if (!finished) {
            throw new IllegalArgumentException("Genetic algorithm was never run.");
        }
        return averageDistanceOfEachGeneration;
    }

    public ArrayList<Chromosome> getBestDistanceOfEachGeneration() {
        if (!finished) {
            throw new IllegalArgumentException("Genetic algorithm was never run.");
        }
        return bestDistanceOfEachGeneration;
    }

    public int getAreaUnderAverageDistances () {
        if (!finished) {
            throw new IllegalArgumentException("Genetic algorithm was never run.");
        }
        return areaUnderAverageDistances;
    }

    public int getAreaUnderBestDistances () {
        if (!finished) {
            throw new IllegalArgumentException("Genetic algorithm was never run.");
        }
        return areaUnderBestDistances;
    }
    
    
    public void setLocalSearchRate (double localSearchRate) {
        if (localSearchRate < 0 || localSearchRate > 1) {
            throw new IllegalArgumentException("Parameter must be between 1 and 0 inclusive.");
        }
        this.localSearchRate = localSearchRate;
    }
    
    private void performElitism (ArrayList<Chromosome> nextGen) throws FileNotFoundException {
        PriorityQueue<Chromosome> priorityQueue = new PriorityQueue<>();

        for (Chromosome chromosome : population) {
            priorityQueue.add(chromosome);
        }

        for (int i = 0; i < elitismValue; i++) {

            Chromosome chromosome = priorityQueue.poll();

            if (localSearchRate > 0) {
                chromosome = performLocalSearch(chromosome);
            }

            //if (random.nextDouble() < localSearchRate)
            //    chromosome = performLocalSearch(chromosome);

            nextGen.add(chromosome);
        }
    }
    
    private ArrayList<Chromosome> createNextGeneration () throws FileNotFoundException {

        ArrayList<Chromosome> nextGen = new ArrayList<Chromosome>(population.size());

        performElitism(nextGen);

        HashSet<Chromosome> chromosomesAdded = new HashSet<>(); // For checking duplicates.

        while (nextGen.size() < population.size()-1) {

            Chromosome p1 = tournamentSelection(population, k, random);
            Chromosome p2 = tournamentSelection(population, k, random);

            boolean doCrossover     = (random.nextDouble() <= crossoverRate);
            boolean doMutate1       = (random.nextDouble() <= mutationRate);
            boolean doMutate2       = (random.nextDouble() <= mutationRate);
            boolean doLocalSearch1  = (random.nextDouble() <= localSearchRate);
            boolean doLocalSearch2  = (random.nextDouble() <= localSearchRate);

            if (doCrossover) {
                ArrayList<Chromosome> children = crossover(p1, p2);
                p1 = children.get(0);
                p2 = children.get(1);
            }

            if (doMutate1) p1 = mutate(p1);
            if (doMutate2) p2 = mutate(p2);

            if (doLocalSearch1) p1 = performLocalSearch(p1);
            if (doLocalSearch2) p2 = performLocalSearch(p2);

            if (forceUniqueness) {
                if (!chromosomesAdded.contains(p1)) {
                    chromosomesAdded.add(p1);
                    nextGen.add(p1);
                }

                if (!chromosomesAdded.contains(p2)) {
                    chromosomesAdded.add(p2);
                    nextGen.add(p2);
                }
            } else {
                nextGen.add(p1);
                nextGen.add(p2);
            }

        }

        // If there is one space left, fill it up.
        if (nextGen.size() != population.size()) {
            nextGen.add(tournamentSelection(population, k, random));
        }

        if (nextGen.size() != population.size()) {
            throw new AssertionError("Next generation population should be full.");
        }

        return nextGen;
    }
    
    private ArrayList<Chromosome> crossover (Chromosome p1, Chromosome p2) throws FileNotFoundException {
        ArrayList<Chromosome> children;
         children = orderCrossover(p1, p2, random);
        return children;
    }
    
    /*
     * create population
    */ 
    // populate population
    public void populate() throws FileNotFoundException{
        ArrayList<Chromosome> initialPopulation = new ArrayList<>(); 
        for (int i = 0; i < populationSize; i++) {
            Integer[] randomPermutation = randomize(cityArray, cityArray.length);
            initialPopulation.add(new Chromosome(filePath, randomPermutation));
        }
        population = initialPopulation;
    }
    
    
    private Chromosome performLocalSearch (Chromosome chromosome) throws FileNotFoundException {

        double bestDistance = chromosome.fitness();
        Chromosome array = new Chromosome(filePath, chromosome.chromosome);
        Chromosome bestArray = new Chromosome(filePath, chromosome.chromosome);

        
        for (int i = 0; i < array.chromosome.length-1; i++) {
            for (int k = i+1; k < array.chromosome.length; k++) {

                Chromosome temp = new Chromosome(filePath, chromosome.chromosome);

                // Reverse order from i to k.
                for (int j = i; j <= (i+k)/2; j++) {
                    temp.shuffle( j, k - (j-i));
                }



                double distance = temp.fitness();
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestArray = new Chromosome(filePath,temp.chromosome);
                }

            }
        }

        return bestArray;
    }
    
    /**
     * Swaps two randomly selected cities.
     * @param chromosome    The Chromosome who's cities will be swapped.
     * @param random        The Random object used for randomly selecting the cities
     * @return              the mutated Chromosome
     */
    Chromosome reciprocalExchange (Chromosome chromosome, Random random) throws FileNotFoundException {
        Chromosome cities = new Chromosome(filePath, chromosome.chromosome);
        int l = cities.chromosome.length;
        chromosome.swap(random.nextInt(l), random.nextInt(l));
        return cities;
    }
    
   private Chromosome mutate (Chromosome chromosome) throws FileNotFoundException{
       return reciprocalExchange(chromosome, random);
   }

    /**
     * Picks k Chromosomes at at random and then return the best one.
     * There is a small chance that the best one will not be selected.
     * @param population    the population to selected from
     * @param k             the number of chromosomes to select
     * @param random        the Random object for randomly selecting
     * @return              usually the fittest Chromosome from k randomly selected chromosomes
     */
    static Chromosome tournamentSelection (ArrayList<Chromosome> kChromosomes, int k, Random random) {
        if (k < 1) {
            throw new IllegalArgumentException("K must be greater than 0.");
        }

        ArrayList<Chromosome> chromoList = getKChromosomes(kChromosomes, k, random);
        return getChromosome(chromoList, random);
    }

    /**
     * Returns k randomly selected Chromosomes.
     * @param pop       an array of Chromosomes (a population)
     * @param k         the number of Chromosomes to randomly select
     * @param random    the Random object used for picking a random chromosomes
     * @return          k randomly selected chromosomes
     */
    private static ArrayList<Chromosome> getKChromosomes (ArrayList<Chromosome> pop, int k, Random random) {

        ArrayList<Chromosome> kChromosomes = new ArrayList<>();

        for (int j = 0; j < k; j++) {
            Chromosome chromosome = pop.get(random.nextInt(pop.size()));
            kChromosomes.add(chromosome);
        }

        return kChromosomes;
    }

    /**
     * Get the best Chromosome in a list of Chromosomes. There is a small chance
     * that a randomly selected Chromosome is picked instead of the best one.
     * @param arrayList     the list of Chromosomes
     * @param random        the Random object used for selecting a random Chromosome if needed
     * @return              usually the best Chromosome
     */
    private static Chromosome getChromosome (ArrayList<Chromosome> arrayList, Random random) {

        Chromosome bestChromosome = getBestChromosome(arrayList);

        // 1 in 5 chance to return a chromosome that is not the best.
        if (random.nextInt(ODDS_OF_NOT_PICKING_FITTEST) == 0 && arrayList.size() != 1) {
            arrayList.remove(bestChromosome);
            return arrayList.get(random.nextInt(arrayList.size()));
        }

        return bestChromosome;
    }

    /**
     * Get the best Chromosome in a list of Chromosomes.
     * @param arrayList     the list to search
     * @return              the best chromosome
     */
    private static Chromosome getBestChromosome (ArrayList<Chromosome> arrayList) {

        Chromosome bestC = null;

        for (Chromosome c : arrayList) {
            if (bestC == null) {
                bestC = c;
            } else if (c.fitness() < bestC.fitness()) {
                bestC = c;
            }
        }

        return bestC;
    }

    
//    public Chromosome pickParents(){    
//            
//            double value = 0.0; 
//            
//            int selection = rand.nextInt(100);
//            
////            double selection = 0 + ( - 0) * r.nextDouble();
//            for(int j = 0; j < populationSize;j++){
//               value = population.get(j).cumulativeProbability; 
//               if(selection <= value){
//                  return population.get(j);
//               }
//            }
//        return null;
//    }

    
    ArrayList<Chromosome> orderCrossover (Chromosome parent1, Chromosome parent2, Random r) throws FileNotFoundException {

        Chromosome child1 = new Chromosome(filePath, new Integer[] {});
        Chromosome child2 = new Chromosome(filePath, new Integer[] {});;

        HashSet<Integer> citiesInChild1 = new HashSet<>();
        HashSet<Integer> citiesInChild2 = new HashSet<>();

        ArrayList<Integer> citiesNotInChild1 = new ArrayList<>();
        ArrayList<Integer> citiesNotInChild2 = new ArrayList<>();

        ArrayList<Chromosome> children = new ArrayList<>();
        int totalCities = parent1.chromosome.length;

        int firstPoint = r.nextInt(totalCities);
        int secondPoint = r.nextInt(totalCities - firstPoint) + firstPoint;

        // Inherit the cities before and after the points selected.
        for (int i = 0; i < firstPoint; i++) {
            child1.chromosome[i] = parent1.chromosome[i];
            child2.chromosome[i] = parent2.chromosome[i];
            citiesInChild1.add(parent1.chromosome[i]);
            citiesInChild2.add(parent2.chromosome[i]);
        }
        for (int i = secondPoint; i < totalCities; i++) {
            child1.chromosome[i] = parent1.chromosome[i];
            child2.chromosome[i] = parent2.chromosome[i];
            citiesInChild1.add(parent1.chromosome[i]);
            citiesInChild2.add(parent2.chromosome[i]);
        }

        // Get the cities of the opposite parent if the child does not already contain them.
        for (int i = firstPoint; i < secondPoint; i++) {
            if (!citiesInChild1.contains(parent2.chromosome[i])) {
                citiesInChild1.add(parent2.chromosome[i]);
                child1.chromosome[i] = parent2.chromosome[i];
            }
            if (!citiesInChild2.contains(parent1.chromosome[i])) {
                citiesInChild2.add(parent1.chromosome[i]);
                child2.chromosome[i] = parent1.chromosome[i];
            }
        }

        // Find all the cities that are still missing from each child.
        for (int i = 0; i < totalCities; i++) {
            if (!citiesInChild1.contains(parent2.chromosome[i])) {
                citiesNotInChild1.add(parent2.chromosome[i]);
            }
            if (!citiesInChild2.contains(parent1.chromosome[i])) {
                citiesNotInChild2.add(parent1.chromosome[i]);
            }
        }

        // Find which spots are still empty in each child.
        ArrayList<Integer> emptySpotsC1 = new ArrayList<>();
        ArrayList<Integer> emptySpotsC2 = new ArrayList<>();
        for (int i = 0; i < totalCities; i++) {
            if (child1.chromosome[i] == null) {
                emptySpotsC1.add(i);
            }
            if (child2.chromosome[i] == null) {
                emptySpotsC2.add(i);
            }
        }

        // Fill in the empty spots.
        for (Integer city : citiesNotInChild1) {
            child1.chromosome[emptySpotsC1.remove(0)] = city;
        }
        for (Integer city : citiesNotInChild2) {
            child2.chromosome[emptySpotsC2.remove(0)] = city;
        }

//        Chromosome childOne = new Chromosome(child1);
//        Chromosome childTwo = new Chromosome(child2);
        children.add(child1);
        children.add(child2);

        return children;
    }
    // using Fisher–Yates shuffle Algorithm to shuffle the array of cities to get the initial population
    public Integer[] randomize(Integer arr[], int n){
        //Integer[] arrdup = arr.clone();
        // Creating a object for Random class
        
          
        // Start from the last element and swap one by one. We don't
        // need to run for the first element that's why i > 0
        for (int i = n-1; i > 0; i--) {
              
            // Pick a random index from 0 to i
            int j = r.nextInt(i);
              
            // Swap arr[i] with the element at random index
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
        return arr;        
    }
    
    
    public void run () throws FileNotFoundException {
        long start_time = System.currentTimeMillis();
        long timeTaken = 0;
        for (int i = 0; i < maxGen; i++) {    
            System.out.println("------------Gen "+i+" Start------------");
            population = createNextGeneration();
            System.out.println("------------Chromosomes "+i+" Start------------");
            population.forEach((n) -> {
                System.out.println("Path: "+ Arrays.toString(n.chromosome) + " Distance: " + n.fitness());
            });
            
            System.out.println("------------Chromosomes "+i+" END------------");
            Chromosome best = population.stream().min(Comparator.comparing(v -> v.fitness())).get();
            System.out.println("Local Best: " + "\nPath: " + Arrays.toString(best.chromosome) + "\nDistance: " +best.fitness());
            bestDistanceOfEachGeneration.add(best);
            
            System.out.println("-------------Gen "+i+" End-------------");
            System.out.println("\n\n");
        }
        
        finished = true;

        bestDistanceOfLastGeneration = population.stream().min(Comparator.comparing(v -> v.fitness())).get();
        ArrayList<Chromosome> allBestPath = new ArrayList();
        allBestPath.addAll(bestDistanceOfEachGeneration);
        allBestPath.add(bestDistanceOfLastGeneration);
        
        Chromosome bestPath = allBestPath.stream().min(Comparator.comparing(v -> v.fitness())).get();
        System.out.println("---------------Global---------------");
        System.out.println("Global Best: " + "\nPath: " + Arrays.toString(bestPath.chromosome) + "\nDistance: " +bestPath.fitness());
        long currentTime = System.currentTimeMillis();
        if((currentTime - start_time)<1000){
            timeTaken = (currentTime - start_time) ;
            System.out.println("Total time: " + timeTaken + " miliSeconds");
        }
        else{
            timeTaken = (currentTime - start_time) / 1000 ;
            System.out.println("Total time: " + timeTaken + " Seconds");
        }
        System.out.println("-------------Global End-------------");       
    }
    
    public static void main(String[] args) throws FileNotFoundException {
        
        
//        GeneticAlgorithm gen = new GeneticAlgorithm("src/test/Resources/test1tsp.txt", 12, 0.4, 0.9,1,100);
//        GeneticAlgorithm gen = new GeneticAlgorithm("src/test/Resources/test2atsp.txt", 24, 0.4, 0.7,1,100);
//        GeneticAlgorithm gen = new GeneticAlgorithm("src/test/Resources/test3atsp.txt", 27, 0.4, 0.7,1,200);
//        GeneticAlgorithm gen = new GeneticAlgorithm("src/test/Resources/test1-21.txt", 36, 0.4, 0.7,1,200);
//        GeneticAlgorithm gen = new GeneticAlgorithm("src/test/Resources/test2-21.txt", 70, 0.4, 0.7,1,400);
//        GeneticAlgorithm gen = new GeneticAlgorithm("src/test/Resources/test3-21.txt", 102, 0.4, 0.7,2,500);
//        GeneticAlgorithm gen = new GeneticAlgorithm("src/test/Resources/test3-21.txt", 136, 0.4, 0.7,1,1000);
//        GeneticAlgorithm gen = new GeneticAlgorithm("src/test/Resources/test4-21.txt", 324, 0.4, 0.7, 1, 2000);
        GeneticAlgorithm gen = new GeneticAlgorithm("src/test/Resources/test4-21.txt", 216, 0.4, 0.7, 1, 1000);
//        GeneticAlgorithm gen = new GeneticAlgorithm("src/test/Resources/test4-20.txt", 96 , 0.4, 0.7,1, 2000);
        
        gen.run();
        System.out.println();
        System.out.println("Population Size: " + gen.populationSize);
        System.out.println();
    }   
}
