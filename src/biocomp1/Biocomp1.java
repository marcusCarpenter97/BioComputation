/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biocomp1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author marcus
 */
public class Biocomp1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException   {
        
        File log = new File("/home/marcus/NetBeansProjects/biocomp1/src/dataFiles/log.txt");
        if (log.exists() == false) {
            log.createNewFile();
        }

        population pop = new population();
        pop.initPop();
        pop.makeRules();

        System.out.println("original pop");
        pop.printPopulation();
        
        save(pop, log);

        int generations = 0;
        int maxGen = 100;
        while (generations < maxGen) {

            pop.roulettewheelSelection();
            
            pop.crossover();
            pop.makeRules();

            pop.mutation();
            pop.makeRules();

            pop.survivorSelection();

            generations++;
            
            save(pop, log);
        }
        
        System.out.println("Pop after 50 gen");
        pop.printPopulation();
        System.out.println(pop.printBest());  
    }
    
    public static void save(population pop, File log) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(log, true))) {
            out.append(pop.getmutationRate());
            out.append(",");
            out.append(pop.total());
            out.append(",");
            out.append(pop.average());
            out.append(",");
            out.append(pop.printBest());
            out.append("\n");
        }
    }
}
