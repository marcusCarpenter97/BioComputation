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
    
    public static int POPULATION_SIZE = 16;  //must be an even number
    public static double MUTATION_RATE = 0.01;
    public static int NUMBER_OF_RULES = 10;
    public static String DATA_SET_1 = "data1.txt";
    public static String DATA_SET_2 = "data2.txt";
    public static int RULE_SIZE_1 = 5;
    public static int RULE_SIZE_2 = 7;
    public static int MAXIMUM_GENERATIONS = 2000;

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
                
        String activeDataSet = DATA_SET_1;
        int activeRuleSize;
        
        if(activeDataSet.equals(DATA_SET_1)){
            activeRuleSize = RULE_SIZE_1;
        } else {
            activeRuleSize = RULE_SIZE_2;
        }

        String logPath = "/home/marcus/NetBeansProjects/biocomp1/src/dataFiles/log.csv";
        String lastPath = "/home/marcus/NetBeansProjects/biocomp1/src/dataFiles/last.csv";

        File log = new File(logPath);
        if (log.exists() == false) {
            log.createNewFile();
        }
        
        File last = new File(lastPath);
        if (last.exists() == false) {
            last.createNewFile();
        }

        population pop = new population(POPULATION_SIZE, MUTATION_RATE,
                NUMBER_OF_RULES, activeDataSet, activeRuleSize);

        addHeader(pop, log);
        addHeader(pop, last);

        //For benchmarking
        int runCount = 0;
        while (runCount < 20) {
            
            pop = new population(POPULATION_SIZE, MUTATION_RATE,
                NUMBER_OF_RULES, activeDataSet, activeRuleSize);

            pop.initPop();
            pop.makeRules();

            System.out.println("original pop");
            pop.printPopulation();

            int generation = 0;

            save(pop, log, generation);
            
            while (generation < MAXIMUM_GENERATIONS) {
                
                pop.tournamentSelection();

                pop.crossover();
                pop.makeRules();

                pop.mutation();
                pop.makeRules();

                pop.survivorSelection();

                generation++;

                save(pop, log, generation);
            }

            System.out.println("Pop after 50 gen");
            pop.printPopulation();
            
            save(pop, last, generation);

            runCount++;
        }
    }

    public static void save(population pop, File log, int gen) throws IOException {

        int totalFitness = pop.calctotal();

        try (PrintWriter out = new PrintWriter(new FileWriter(log, true))) {
            out.append(Integer.toString(gen));
            out.append(",");
            out.append(Integer.toString(totalFitness));
            out.append(",");
            out.append(Integer.toString(totalFitness / pop.getSize()));
            out.append(",");
            out.append(pop.getBest());
            out.append("\n");
        }
    }

    public static void addHeader(population pop, File log) throws IOException {

        try (PrintWriter out = new PrintWriter(new FileWriter(log, true))) {
            out.append("Population size: ");
            out.append(Integer.toString(pop.getSize()));
            out.append(" - Mutation rate: ");
            out.append(pop.getmutationRate());
            out.append(" - Number of rules: ");
            out.append(pop.getNumOfRules());
            out.append(" - Data set used: ");
            out.append(pop.getDataSetName());
            out.append("\n");
        }
    }
}
