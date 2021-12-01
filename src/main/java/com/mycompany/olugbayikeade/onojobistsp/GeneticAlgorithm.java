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
    
    

    public GeneticAlgorithm(Map<Integer, ArrayList<Integer>> data) {
        this.data = data;
    }    

    public GeneticAlgorithm(String filePath) throws FileNotFoundException {
        this.filePath = filePath;
        fs = new FileScanner(this.filePath);
        this.data = fs.data;
        keyValues = data.keySet();
        cityArray = keyValues.toArray(new Integer[keyValues.size()]);
        cityArrayLength = cityArray.length;
    }
    
    
    
    public GeneticAlgorithm(){
    
    }
    
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
    
    public static void main(String[] args) throws FileNotFoundException {
        //GeneticAlgorithm gen = new GeneticAlgorithm("src/test/Resources/test3atsp.txt");
        GeneticAlgorithm gen = new GeneticAlgorithm("src/test/Resources/test4-20.txt");
        
        // System.out.println(gen.euclideanDistance(5, 10, 3, 5));
        double totalDistance = gen.totalDistance(gen.cityArray, gen.cityArrayLength);
        System.out.println(totalDistance);
        //System.out.println(gen.fitnessfunction());
    }
}
