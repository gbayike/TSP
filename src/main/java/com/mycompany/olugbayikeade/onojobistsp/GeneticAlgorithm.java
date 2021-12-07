/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.olugbayikeade.onojobistsp;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
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

    public GeneticAlgorithm(String filePath, int populationSize) throws FileNotFoundException {
        super(filePath);
        //obchrom = new Chromosome(filePath, cityArray);
        this.populationSize = populationSize;
        population = new ArrayList<>();
    }
    
    
    
    /**
     * Function to swap mutate Chromosomes.
     */
    public Chromosome[] mutate(Chromosome[] array,  int arrayLength){
        //randomly select two indexes in the array and swap.
        Chromosome[] clone = array.clone();
        Random r = new Random();
        
        int index1 = r.nextInt(arrayLength);
        int index2 = r.nextInt(arrayLength);
        
        // To prevent repetiton of the index1 in index 2
        while (index2 == index1) index2 = r.nextInt(arrayLength);
        
        System.out.println("insex1: " + index1);
        System.out.println("insex2: " + index2);
        
        Chromosome temp = clone[index1];
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
//            System.out.println(Arrays.toString(initialPopulation[i].chromosome));
//            System.out.println(initialPopulation[i].fitness());
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
        population.forEach((n) -> { try {
            totalFitness += n.fitness();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(GeneticAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        population.forEach((n)->{
            try {
                n.inverseFitness = totalFitness/n.fitness();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(GeneticAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
            }
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
    
    public ArrayList<Chromosome> rouletteWheel(int picksNum) throws FileNotFoundException{
        ArrayList<Chromosome> picks = new ArrayList<>();
        fitnessProbability();
        for(int i = 0; i < picksNum; i++){
            Random r = new Random();
            double value = 0.0; 
            double selection = r.nextDouble(probabilityTotal);
            System.out.println(selection);
            for(int j = 0; j < populationSize;j++){
               value = population.get(j).cumulativeProbability;  
               if(value >= selection){
                  picks.add(population.get(j));
                  break;
               }
            }
        }
        return picks;
    }
    /*
     * calculate distance between points
    */
    
    
    // using Fisher–Yates shuffle Algorithm to shuffle the array of cities to get the initial population
    public Integer[] randomize(Integer arr[], int n){
        //Integer[] arrdup = arr.clone();
        // Creating a object for Random class
        Random r = new Random();
          
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
    
    public static void main(String[] args) throws FileNotFoundException {
        //GeneticAlgorithm gen = new GeneticAlgorithm("src/test/Resources/test3atsp.txt");
        /**
         * Initial population should be 2x the city count.
         */
        
//        GeneticAlgorithm gen = new GeneticAlgorithm("src/test/Resources/test1tsp.txt", 8);
        GeneticAlgorithm gen = new GeneticAlgorithm("src/test/Resources/test4-20.txt", 64);
        
        gen.populate();
        System.out.println();
        //System.out.println(Arrays.deepToString(gen.population));
        System.out.println(gen.populationSize);
        ArrayList<Chromosome> population = gen.population;
        System.out.println();
        for(Chromosome individual : population){
            //double totaldistance = gen.totalDistance(individual, gen.cityArray.length);
            System.out.println(Arrays.toString(individual.chromosome));
            System.out.println(individual.fitness());
        }       
        gen.fitnessProbability();
        System.out.println();
        System.out.println(gen.totalFitness);
        var wrapper = new Object(){double sum = 0.0;};
        population.forEach((n)->{
            System.out.print(n.inverseFitness + " , ");
            wrapper.sum += n.pickProbability;
            System.out.print(n.pickProbability);
            System.out.println();
            System.out.println(n.cumulativeProbability);
            System.out.println();
        });
        System.out.print(wrapper.sum);
        System.out.println();
        
        ArrayList<Chromosome> parent2 = gen.rouletteWheel(2);
        for(int i = 0; i < 4;i++){
           ArrayList<Chromosome> parent = gen.rouletteWheel(2);
           parent.forEach((n)-> { System.out.println(Arrays.toString(n.chromosome) +" "+n.cumulativeProbability);});
        } 
        
//        System.out.println("------Parent 2-----");
//        parent2.forEach((n)-> { System.out.println(Arrays.toString(n.chromosome) +" "+n.cumulativeProbability);});
        
    }
}
