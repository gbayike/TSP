/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.olugbayikeade.onojobistsp;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author Olugbayike
 */
public class GeneticAlgorithm {
    Map<Integer, ArrayList<Integer>> data;
    Conversions conv = new Conversions();
    String filePath;
    FileScanner fs;
    Set<Integer> keyValues;
    public Integer[] cityArray;
    public int cityArrayLength;  
    int populationSize;
    Integer population[][];
    
    

    public GeneticAlgorithm(Map<Integer, ArrayList<Integer>> data) {
        this.data = data;
    }    

    public GeneticAlgorithm(String filePath, int populationSize) throws FileNotFoundException {
        this.filePath = filePath;
        fs = new FileScanner(this.filePath);
        this.data = fs.data;
        keyValues = data.keySet();
        cityArray = keyValues.toArray(new Integer[keyValues.size()]);
        cityArrayLength = cityArray.length;
        population = new Integer[populationSize][cityArrayLength];
        this.populationSize = populationSize;
    }
    
    
    
    public GeneticAlgorithm(){
    
    }
    
    /*
     * create population
    */ 
    // populate population
    public void populate() throws FileNotFoundException{
        for (int i = 0; i < populationSize; i++) {
            //arr[i] = gen.randomize(gen.cityArray, gen.cityArray.length);
            Integer[] city = randomize(cityArray, cityArray.length);
//            System.out.println(Arrays.toString(cityArray));
//            System.out.println(population[i].length);
//            System.out.println(cityArrayLength);
            System.arraycopy(city, 0, population[i], 0, cityArrayLength);
//            double totaldistance = totalDistance(population[i], cityArray.length);
            System.out.println(Arrays.toString(population[i]));
//            System.out.println(totaldistance);
        }
    }
    
      
    /*
     * calculate distance between points
    */
    private double euclideanDistance(int x1, int x2, int y1, int y2){
        double equation = Math.pow((x2-x1),2) + Math.pow((y2 - y1),2);
        return Math.sqrt(equation);
    }
    
    public double totalDistance(Integer arr [], int arrlength) throws FileNotFoundException{
        double totalDistance = 0;
        for(int i = 1; i < arrlength; i++){
            int city1x = fs.cityX(arr[i-1]);
            int city2x = fs.cityX(arr[i]);
            
            int city1y = fs.cityY(arr[i-1]);
            int city2y = fs.cityY(arr[i]);
            
            totalDistance += euclideanDistance(city1x, city2x, city1y, city2y);
        }        
        return totalDistance;
    }
    
    // using Fisher–Yates shuffle Algorithm to shuffle the array of cities to get the initial population
    public Integer[] randomize(Integer arr[], int n){
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
//        GeneticAlgorithm gen = new GeneticAlgorithm("src/test/Resources/test1tsp.txt");
        GeneticAlgorithm gen = new GeneticAlgorithm("src/test/Resources/test4-20.txt",10);
        
        // System.out.println(gen.euclideanDistance(5, 10, 3, 5));
        
        // create population
        //Integer population[][] = new Integer[10][gen.cityArrayLength];
        //Integer population[][] = gen.population;
        
        // populate population
//        for (int i = 0; i < 10; i++) {
//            //arr[i] = gen.randomize(gen.cityArray, gen.cityArray.length);
//            Integer[] city = gen.randomize(gen.cityArray, gen.cityArray.length);
//            System.arraycopy(city, 0, population[i], 0, gen.cityArrayLength);
////            double totaldistance = gen.totalDistance(arr[i], gen.cityArray.length);
//            System.out.println(Arrays.toString(population[i]));
////            System.out.println(totaldistance);
//        }
//        // loop each individual and get distance
        gen.populate();
        for(Integer[] individual : gen.population){
            double totaldistance = gen.totalDistance(individual, gen.cityArray.length);
            System.out.println(Arrays.toString(individual));
            System.out.println(totaldistance);
        }
        System.out.println(Arrays.deepToString(gen.population));
        System.out.println(gen.population.length);
//        double totalDistance = gen.totalDistance(gen.cityArray, gen.cityArrayLength);
//        System.out.println(totalDistance);
        //System.out.println(gen.fitnessfunction());
    }
}
