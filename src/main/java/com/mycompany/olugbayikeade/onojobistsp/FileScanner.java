/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.olugbayikeade.onojobistsp;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Olugbayike
 */
public class FileScanner{
    String S;
    // create and use a file reader wrapper in a BufferedReader.
    String filePath;
    Map<Integer, ArrayList<Integer>> data;
    /* 
     * @param FilePath -  the path of the file to read.
    */
    public FileScanner(String filePath) throws FileNotFoundException {
        this.filePath = filePath;
        data = ReadtextFromFile();
    }
    
    public String deleteSubString(String str, int index){
        str = str.substring(index);
        return str;
    }
    
    /*
     * ReadtextFrom file returns data whidh is a hash map of the data gotten from the
     * text file. 
     * output:
     * data - the data gotten from the text file.
     * read output with data.get(city number).get(index of x,y:0,1)
     *
    */
    public Map<Integer, ArrayList<Integer>> ReadtextFromFile() throws FileNotFoundException{
        Map<Integer, ArrayList<Integer>> data = new HashMap<Integer, ArrayList<Integer>>();
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))){
            while((S = br.readLine()) != null){
                /* Replace all spaces with , seperator */
                String modifiedS = S.replaceAll("\\s+", ",");
                
                /* if the first character after replacing space is , remove it*/
                if(modifiedS.charAt(0) == ',')
                    modifiedS = deleteSubString(modifiedS, 1);
                
                String []arrayS = modifiedS.split(",", 0);
                
                Integer cityNo = Integer.parseInt(arrayS[0]);
                Integer x = Integer.parseInt(arrayS[1]);
                Integer y = Integer.parseInt(arrayS[2]);
                
                data.put(cityNo, new ArrayList<Integer>());
                data.get(cityNo).add(x);
                data.get(cityNo).add(y);
            }
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex);
        }
        return data;
    }
    
    public int cityX(Integer cityno){
        return data.get(cityno).get(0);
    }
    
    public int cityY(Integer cityno){
        return data.get(cityno).get(1);
    }
    
    public static void main(String[] args) throws FileNotFoundException {
//        FileScanner fs = new FileScanner("src/test/Resources/test4-20.txt");
//        
//        Map<Integer, ArrayList<Integer>> file = fs.data;
//        
//        System.out.println(file);
//        System.out.println(file.get(2).get(1));
//        System.out.println(file.size());
        
//        int i = 4;
//        
//        System.out.println(file);
//        System.out.println(fs.cityX(3));
//        System.out.println(fs.cityY(3));
//        
//        Set<Integer> keyValues = file.keySet();
//        for(Integer keyValue: keyValues)
//            System.out.println(keyValue);
    }
}
