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

    public GeneticAlgorithm(String filePath, int populationSize) throws FileNotFoundException {
        super(filePath);
        //obchrom = new Chromosome(filePath, cityArray);
        this.populationSize = populationSize;
        population = new ArrayList<>();
    }
    
    
    
    /**
     * Function to swap mutate Chromosomes.
     */
    public Integer[] mutate(Integer[] array){
        //randomly select two indexes in the array and swap.
        Integer[] clone = array.clone();
        int cloneLength = clone.length;
        Random r = new Random();
        
        int index1 = r.nextInt(cloneLength);
        int index2 = r.nextInt(cloneLength);
        
        // To prevent repetiton of the index1 in index 2
        while (index2 == index1) index2 = r.nextInt(cloneLength);
        
        System.out.println("insex1: " + index1);
        System.out.println("insex2: " + index2);
        
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
            Random r = new Random();
            double value = 0.0; 
            double selection = r.nextDouble(probabilityTotal);
            System.out.println(selection);
            for(int j = 0; j < populationSize;j++){
               value = population.get(j).cumulativeProbability;  
               if(value >= selection){
                  return population.get(j);
               }
            }
        return null;
    }
    public ArrayList<Chromosome> rouletteWheel(int picksNum) throws FileNotFoundException{
        ArrayList<Chromosome> picks = new ArrayList<>();
        fitnessProbability();
        Chromosome parent1 = pickParents();
        Chromosome parent2 = pickParents();
        while(Arrays.equals(parent1.chromosome, parent2.chromosome)){
            parent2 = pickParents();
        }
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
    
    public Integer[] OrderedCrossover(ArrayList<Chromosome> parents, int parent1index, int parent2index) throws FileNotFoundException{
        ArrayList<Chromosome> clone= new ArrayList<>();
//        Chromosome parent1;
//        Chromosome parent2;    
        int cityLength = cityArray.length;
        Integer[] child = new Integer[cityLength];
        Random r = new Random();
        int index1 = r.nextInt(cityLength-1);
        int index2 = r.nextInt(cityLength);
        
        
        int start = Math.min(index1, index2);
        int end = Math.max(index1, index2);
        
        System.out.println(index1);
        System.out.println(index1);
        System.out.println("Parent1: " + Arrays.toString(parents.get(parent1index).chromosome));
        System.out.println("Parent2: " + Arrays.toString(parents.get(parent2index).chromosome));
        
        for(int i = start;i<=end ;i++){
            child[i] = parents.get(parent1index).chromosome[i];
        }
        
        for(int i = 0; i<cityLength ;i++){
            
            System.out.println("element "+ i +" in child" + Arrays.asList(child).get(i));
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
        System.out.println( "Child: " + Arrays.toString(child));
        clone.add(new Chromosome(filePath, child));
        return child;   
    }
    
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
    
    void run_GA(int number_of_generations) throws FileNotFoundException{
        Chromosome Global_best_fitness = new Chromosome(filePath,cityArray);
        populate();
        for(int i =0; i < number_of_generations;i++){
            Chromosome current_best_fitness =  population.stream().min(Comparator.comparing(v -> v.fitness())).get();
            System.out.println(Arrays.toString(current_best_fitness.chromosome) +" "+current_best_fitness.fitness());
            if(i==0){
                Global_best_fitness.chromosome = current_best_fitness.chromosome.clone();
            }
            if(Global_best_fitness.fitness() > current_best_fitness.fitness()){
                Global_best_fitness.chromosome = current_best_fitness.chromosome.clone();
            }
            
            
            
        }
        System.out.println("Global fitness" + Arrays.toString( Global_best_fitness.chromosome) +" "+Global_best_fitness.fitness());
        
    }
    public static void main(String[] args) throws FileNotFoundException {
        //GeneticAlgorithm gen = new GeneticAlgorithm("src/test/Resources/test3atsp.txt");
        /**
         * Initial population should be 2x the city count.
         */
        
        GeneticAlgorithm gen = new GeneticAlgorithm("src/test/Resources/test1tsp.txt", 12);
//        GeneticAlgorithm gen = new GeneticAlgorithm("src/test/Resources/test4-20.txt", 64);
        
        gen.run_GA(1);
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
//        gen.fitnessProbability();
//        System.out.println();
//        System.out.println(gen.totalFitness);
//        var wrapper = new Object(){double sum = 0.0;};
//        population.forEach((n)->{
//            System.out.print(n.inverseFitness + " , ");
//            wrapper.sum += n.pickProbability;
//            System.out.print(n.pickProbability);
//            System.out.println();
//            System.out.println(n.cumulativeProbability);
//            System.out.println();
//        });
//        System.out.print(wrapper.sum);
//        System.out.println();
//        
//        ArrayList<Chromosome> parent2 = gen.rouletteWheel(2);
//        for(int i = 0; i < 4;i++){
//            ArrayList<Chromosome> parent = gen.rouletteWheel(2);
//            System.out.println("--------------Parent-------");
//            parent.forEach((n)-> {
//               System.out.println(Arrays.toString(n.chromosome) +" "+n.fitness());
//            });
//            System.out.println("--------------Child-------");
//            ArrayList<Chromosome> child = new ArrayList<>();
//            child.add(new Chromosome(gen.filePath, gen.OrderedCrossover(parent,0,1)));
//            child.add(new Chromosome(gen.filePath, gen.OrderedCrossover(parent,1,0)));
////            child.forEach((n)-> { 
////                n.mutate();
////                try {
////                System.out.println("--------Child " + n+"--------");
////               System.out.println(Arrays.toString(n.chromosome) +" "+n.fitness());
////               } catch (FileNotFoundException ex) {
////                   Logger.getLogger(GeneticAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
////               }
////            System.out.println("--------------END-------");
////        });
//            for (Chromosome chromosome : child) {
//                chromosome.mutate();
//                System.out.println("--------Child " + chromosome+"--------");
//               
//            }
//            
//            for (Chromosome chromosome : child) {
//                System.out.println(Arrays.toString(chromosome.chromosome) +" "+chromosome.fitness());
//            }
//           parent.forEach((n)-> { n.mutate();});
//           System.out.println("--------------");
//           parent.forEach((n)-> { System.out.println(Arrays.toString(n.chromosome) +" "+n.cumulativeProbability);});
//           System.out.println("--------------");
        } 
        
//        System.out.println("------Parent 2-----");
//        parent2.forEach((n)-> { System.out.println(Arrays.toString(n.chromosome) +" "+n.cumulativeProbability);});
        
    }
}
