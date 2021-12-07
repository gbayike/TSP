/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.olugbayikeade.onojobistsp;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Olugbayike
 */
public class Chromosome extends FileScanner{
    Integer chromosome[];
//    double fitness;
    double pickProbability;
    double inverseFitness;
    double cumulativeProbability;
//    Integer[] cityArray = keyValues.toArray(new Integer[keyValues.size()]);
    
    
    public Chromosome(String filePath, Integer[] randomPermutation) throws FileNotFoundException {
        super(filePath);   
        chromosome = randomPermutation.clone();
//        System.arraycopy(randomPermutation, 0, chromosome, 0, cityArray.length);
        //generateChromosomes();
    }
//    public Chromosome(Integer[] chromosome, String filePath) throws FileNotFoundException {
//        super(filePath);
//        this.chromosome = chromosome;
//    }
    
    public void mutate(){
        //randomly select two indexes in the array and swap.
        Integer[] clone = chromosome.clone();
        int cloneLength = clone.length;
        Random r = new Random();
        
        int index1 = r.nextInt(cloneLength-1);
        int index2 = r.nextInt(cloneLength);
        
        // To prevent repetiton of the index1 in index 2
        while (index2 == index1) index2 = r.nextInt(cloneLength);
        
        System.out.println("insex1: " + index1);
        System.out.println("insex2: " + index2);
        
        Integer temp = clone[index1];
        clone[index1] = clone[index2];
        clone[index2] = temp;
        
        
        chromosome = clone.clone();
    }
    
    /**
     *
     */
    private double euclideanDistance(int x1, int x2, int y1, int y2){
        double equation = Math.pow((x2-x1),2) + Math.pow((y2 - y1),2);
        return Math.sqrt(equation);
    }
    
    public double fitness(){
        double totalDistance = 0;
        
        for(int i = 1; i < chromosome.length; i++){
            int city1x = cityX(chromosome[i-1]);
            int city2x = cityX(chromosome[i]);
            
            int city1y = cityY(chromosome[i-1]);
            int city2y = cityY(chromosome[i]);
            
            totalDistance += euclideanDistance(city1x, city2x, city1y, city2y);
            
        }
        
        
        return totalDistance;
        
    }
    
    
}
