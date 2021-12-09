/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.olugbayikeade.onojobistsp;

import java.io.FileNotFoundException;
import static java.lang.Math.random;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Olugbayike
 */
public class Chromosome extends FileScanner implements Comparable<Chromosome> {
    Integer chromosome[];
    private boolean isFitnessChanged = true;
    Random r;
    private Random random;
    private double inverseFitness;
    
    /**
     *  Constructor for chromosomes
     * @param filePath - file path to get data from
     * @param randomPermutation - the random permutation of city sequence in array
     * @throws FileNotFoundException 
     */
    public Chromosome(String filePath, Integer[] randomPermutation) throws FileNotFoundException {
        super(filePath);   
        chromosome = randomPermutation.clone();
        r = new Random();
        inverseFitness();
//        System.arraycopy(randomPermutation, 0, chromosome, 0, cityArray.length);
        //generateChromosomes();
    }    
    
    /**
     * Shuffles the cities in the Chromosome.
     */
    void shuffle () {
        for (int i = 0; i < chromosome.length; i++) {
            swap(i, random.nextInt(chromosome.length));
        }
    }
    
    void shuffle(int j, int k){
        swap(j, k);
    }
    
    
    /**
     * Helper method for swapping two Cities in a Chromosome to change the tour.
     * @param array     the array of Cities to do the swap in
     * @param i         the index of the first City
     * @param j         the index of the second City
     */
    void swap (int i, int j) {
        int temp = chromosome[i];
        chromosome[i] = chromosome[j];
        chromosome[j] = temp;
    }
    /**
     *
     */
    private double euclideanDistance(int x1, int x2, int y1, int y2){
        int x = x2 - x1;
        int y = y2 - y1;
        double equation = Math.pow(x,2) + Math.pow(y,2);
        return Math.sqrt(equation);
    }
    
    public double fitness(){
        double totalDistance = 0;
        int n = chromosome.length;
        for(int i = 1; i < n; i++){
            int city1x = cityX(chromosome[i-1]);
            int city2x = cityX(chromosome[i]);
            
            int city1y = cityY(chromosome[i-1]);
            int city2y = cityY(chromosome[i]);
            
            totalDistance += euclideanDistance(city1x, city2x, city1y, city2y);
            
        }
        
        int city1x = cityX(chromosome[0]);
        int city2x = cityX(chromosome[n-1]);

        int city1y = cityY(chromosome[0]);
        int city2y = cityY(chromosome[n-1]);
        
        totalDistance += euclideanDistance(city1x, city2x, city1y, city2y);
        
        return totalDistance;
        
    }
    
    public double inverseFitness(){
        if (isFitnessChanged) {
            inverseFitness = (1 / fitness() * 10000);
            isFitnessChanged = false;
        }
        return inverseFitness;
    }

    @Override
    public int compareTo(Chromosome o) {
        return (int)(fitness() - o.fitness());
    }
}
