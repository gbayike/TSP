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
import java.util.Map;
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
    Random r;

    /**
     * The GeneticAlgorithm constructor accepts 4 parameters
     * @params 
     * file path: String containing the text file location.
     * Initial population: integer containing the initial population.
     * Initial population should be 3x the city count.
     * mutation rate: integer containing rate at which the individuals in the population would get mutated
     * crossover chance: integer containing rate at which crossover between parents in the population would occur.
    */
    
    public GeneticAlgorithm(String filePath, int populationSize, double mutationRate, double crossoverChance) throws FileNotFoundException {
        super(filePath);
        //obchrom = new Chromosome(filePath, cityArray);
        this.crossoverChance = crossoverChance;
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        population = new ArrayList<>();
        r = new Random();
    }
    
    
    
    /**
     * Function to swap mutate Chromosomes.
     */
    public Integer[] mutate(Integer[] array){
        //randomly select two indexes in the array and swap.
        Integer[] clone = array.clone();
        int cloneLength = clone.length;
        
        
        int index1 = r.nextInt(cloneLength);
        int index2 = r.nextInt(cloneLength);
        
        // To prevent repetiton of the index1 in index 2
        while (index2 == index1) index2 = r.nextInt(cloneLength);
        

        
        Integer temp = clone[index1];
        clone[index1] = clone[index2];
        clone[index2] = temp;
        
        
        return clone;
    }
    
    
//    public double rouletteWheel(Integer[] fitness){
//        
//    }
    public boolean checkArraySimilarities(Integer[] cityArray, Integer population[][]){
        boolean similar = false;
        for(int i = 0; i < population.length; i++){
            similar = Arrays.equals(population[i], cityArray);
            if(similar){
                break;
            }
        }
        return similar;   
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
    
    
    /*
     * calculate fitness probability
    */
    public  void fitnessProbability() throws FileNotFoundException{
        totalFitness= 0.0;
        totalInverseFitness = 0.0;
        probabilityTotal = 0.0;
        population.forEach((n) -> {
            totalFitness += n.fitness();
        });

        population.forEach((n)->{
                n.inverseFitness = totalFitness/n.fitness();
            
            totalInverseFitness += n.inverseFitness;
        });

        population.forEach((n)->{
            n.pickProbability = n.inverseFitness/totalInverseFitness;
        });
        
//        var wrapper = new Object(){};
        population.forEach((n)->{
            probabilityTotal += n.pickProbability;
            n.cumulativeProbability = probabilityTotal;
        });
    }
    public Chromosome pickParents(){    
            
            double value = 0.0; 
            Random rand = new Random();
            double selection = rand.nextDouble(probabilityTotal);

            for(int j = 0; j < populationSize;j++){
               value = population.get(j).cumulativeProbability;  
               if(value >= selection){
                  return population.get(j);
               }
            }
        return null;
    }
    public ArrayList<Chromosome> rouletteWheel() throws FileNotFoundException{
        ArrayList<Chromosome> picks = new ArrayList<>();
        fitnessProbability();
        Chromosome parent1 = pickParents();
        Chromosome parent2 = pickParents();
//        while(Arrays.equals(parent1.chromosome, parent2.chromosome)){
//            parent2 = pickParents();
//        }
        picks.add(parent1);
        picks.add(parent2);
        return picks;
    }
    /*
     * randomly pics two points to axchange.
     * @params 
     * parents - Array list containing parents.
     * parent1index - Index of parent 1 in the array list, flipped to index of prent 2 for 2nd child
     * parent2index - Index of parent 2 in the array list, flipped to index of prent 1 for 2nd child
     * returns Integer[] containing child
    */
    
    public void mutate(){
        
        for(Chromosome chrome : population){
            if(r.nextDouble() < mutationRate){
                chrome.mutate();
            }
        }
    }
    public Integer[] OrderedCrossover(ArrayList<Chromosome> parents, int parent1index, int parent2index) throws FileNotFoundException{
        ArrayList<Chromosome> clone= new ArrayList<>();   
        int cityLength = cityArray.length;
        Integer[] child = new Integer[cityLength];
        
        int index1 = r.nextInt(cityLength-1);
        int index2 = r.nextInt(cityLength);
        
        
        int start = Math.min(index1, index2);
        int end = Math.max(index1, index2);
        
        for(int i = start;i<=end ;i++){
            child[i] = parents.get(parent1index).chromosome[i];
        }
        
        int j = 0;
        int i = 0;
        while(i<cityLength){
            int gene = parents.get(parent2index).chromosome[i];
            if(!Arrays.asList(child).contains(gene)){
                while(j<cityLength){
                    if(Arrays.asList(child).get(j) == null){
                        child[j] = gene;
                        break;
                    }
                    j++;
                    //break;
                }
            }
            i++;
        }
        clone.add(new Chromosome(filePath, child));
        return child;   
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
    
    
    private void reproduceandReplace() throws FileNotFoundException {
        ArrayList<Chromosome> nextGeneration = new ArrayList<>();
        
        while(population.size()> nextGeneration.size()){
           ArrayList<Chromosome> parents = rouletteWheel();
           
           if(r.nextDouble() < crossoverChance){
               Chromosome child = new Chromosome(filePath, OrderedCrossover(parents, 0, 1));
               child.mutate();
               nextGeneration.add(child);
           }else{
               nextGeneration.addAll(parents);
           }          
        }
        
        if(nextGeneration.size() > population.size()){
            nextGeneration.remove(0);
        }
        
        population = nextGeneration;
    }
    
    void run_GA(int number_of_generations, long wait_time) throws FileNotFoundException{
        Chromosome Global_best_fitness = new Chromosome(filePath,cityArray);
        populate();
        int i =0;
        long start_time = System.currentTimeMillis();
//        long wait_time = 55000;
        long end_time = start_time + wait_time;
        long timeTaken = 0;
//        i < number_of_generations
        while(System.currentTimeMillis() < end_time){
            System.out.println("------------Gen "+i+" Start------------");
            Chromosome current_best_fitness =  population.stream().min(Comparator.comparing(v -> v.fitness())).get();
            System.out.println("Local Best: " + "\nPath: " + Arrays.toString(current_best_fitness.chromosome) + "\nDistance: " +current_best_fitness.fitness());
            if(i==0){
                Global_best_fitness.chromosome = current_best_fitness.chromosome.clone();
            }
            if(Global_best_fitness.fitness() > current_best_fitness.fitness()){
                Global_best_fitness.chromosome = current_best_fitness.chromosome.clone();
            }

            reproduceandReplace();
            
//            mutate();
            System.out.println("-------------Gen "+i+" End-------------");
            System.out.println();
            timeTaken = (System.currentTimeMillis() - start_time)/1000;
            i++;
        }
        System.out.println("Global Best: " + "\nPath: " + Arrays.toString(Global_best_fitness.chromosome) + "\nDistance: " + Global_best_fitness.fitness());
        System.out.println("Total time: " + timeTaken + " Seconds");
        
    }
    public static void main(String[] args) throws FileNotFoundException {
        /**
         * The GeneticAlgorithm constructor accepts 4 parameters
         * @params 
         * file path: String containing the text file location.
         * Initial population: integer containing the initial population.
         * Initial population should be 3x the city count.
         * mutation rate: integer containing rate at which the individuals in the population would get mutated
         * crossover chance: integer containing rate at which crossover between parents in the population would occur.
         */
        
//        GeneticAlgorithm gen = new GeneticAlgorithm("src/test/Resources/test1tsp.txt", 12, 0.4, 0.7);
//        GeneticAlgorithm gen = new GeneticAlgorithm("src/test/Resources/test2atsp.txt", 24, 0.4, 0.7);
        GeneticAlgorithm gen = new GeneticAlgorithm("src/test/Resources/test3atsp.txt", 27, 0.4, 0.7);
//        GeneticAlgorithm gen = new GeneticAlgorithm("src/test/Resources/test4-20.txt", 288 , 0.4, 0.7);
        
        gen.run_GA(267,55000);
        System.out.println();
        System.out.println("Population Size: " + gen.populationSize);
        System.out.println();
    }   
}
