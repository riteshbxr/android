/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication;

import java.util.Scanner;

/**
 *
 * @author rites
 */
public class JavaApplication {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Long cases;
        String casevalues="";
        Scanner reader = new Scanner(System.in);
        cases=reader.nextLong();
        casevalues=reader.nextLine();
        for(int i=0;i<cases;i++)
        {
            System.out.println(casevalues);
        
        }
        
    }
    
}
