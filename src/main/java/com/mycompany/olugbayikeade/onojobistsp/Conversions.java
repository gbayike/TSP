/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.olugbayikeade.onojobistsp;

/**
 *
 * @author Olugbayike
 */
public class Conversions {
    public double inttoDouble(int data){
        return Double.valueOf(data);
    }
    
    public static void main(String[] args) {
        Conversions conv = new Conversions();
        System.err.println(conv.inttoDouble(11));
    }
}
