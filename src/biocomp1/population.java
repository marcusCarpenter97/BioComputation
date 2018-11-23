/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biocomp1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author marcus
 */
public class population {

    private final int popSize;
    private final double mutationRate;
    private final individual[] population;
    private final individual[] offspring;
    private rule currRule;
    private final int numOfRules;
    private final ArrayList<data> trainingData;
    private final ArrayList<rule> ruleList;
    private final String filePath;
    private final String fileName;

    public population() {
        
        popSize = 10;
        mutationRate = 0.01;
        population = new individual[popSize];
        offspring = new individual[popSize];
        numOfRules = 10;
        currRule = new rule();
        trainingData = new ArrayList<>();
        ruleList = new ArrayList<>();
        filePath = "/home/marcus/NetBeansProjects/biocomp1/src/dataFiles/";
        fileName = "data2.txt";  // data1.txt or data.2txt
    }

    /**
     * Initialises all individuals in population and offspring with random data
     * then reads in the training data from file.
     */
    public void initPop() {
        
        int i, j, rand, geneSize;
        
        for (i = 0; i < popSize; i++) {
            
            population[i] = new individual();
            offspring[i] = new individual();
            
            geneSize = population[i].getSize();
            
            for (j = 0; j < geneSize; j++) {
                
                rand = getRandBit();
                
                //if currGene mod (geneSize/10) == 0 then currGene is action bit
                //if a 2 is generated on an ation bit try again
                while (((j+1) % ((geneSize / 10))) == 0 && (rand == 2)) {
                    rand = getRandBit();
                }
                
                population[i].setGene(rand, j);
            }
        }
        
        try {
            readTrainingData();
        } catch (FileNotFoundException e) {
            System.out.println("File error!\n" + e);
        }
        
        for(data d : trainingData) {
            System.out.println(Arrays.toString(d.getFullData()) + " " + d.getLabel());
        }
    }

    /**
     * Fitness function for the GA, PSEUDOCODE:
     * for each individual
     *  for each rule
     *   for each gene
     *    set currGene into current rule
     *   set currGene into output
     *   add current gene to rule list
     *  for each data point in training data
     *   for each rule in rule list
     *    if rule matches data
     *     if output matches data
     *      increment fitness
     *     break
     * .
     */
    public void makeRules() { 

        int i, j, k, currGene;

        resetFitness();

        // Go through each individual in population
        for (i = 0; i < popSize; i++) {
            
            // Break down gene into rules
            currGene = 0;
            for (j = 0; j < numOfRules; j++) {
                for (k = 0; k < currRule.getRuleSize(); k++) {
                    currRule.setGene(k, population[i].getGene(currGene));
                    currGene++;
                }
                currRule.setOut(population[i].getGene(currGene));
                currGene++;

                ruleList.add(currRule);

                currRule = new rule();
            }
            
            // Find match in training data
            for (data d : trainingData) {
                for (rule r : ruleList) {
                    if (compareIntArrays(r.getCond(), d.getFullData())) {
                        if (r.getOut() == d.getLabel()) {
                            population[i].updateFitness();
                        }
                        break;
                    }
                }
            }
            ruleList.clear();
        }
    }

    /**
     * Read data from the file specified in the constructor.
     * @throws java.io.FileNotFoundException
     */
    public void readTrainingData() throws FileNotFoundException {
        
        int[] tempData;
        int tempInt;
        
        File f = new File(filePath + fileName);
        Scanner data = new Scanner(f);
        data.nextLine();
        
        while (data.hasNext()) {
            
            tempData = stringToInt(data.next());
            tempInt = Integer.parseInt(String.valueOf(data.next()));
            data d = new data(tempData, tempInt);
            trainingData.add(d);
        }
    }

    /**
     * Select offspring using roulette wheel selection.
     */
    public void roulettewheelSelection() {
        
        int i, j, selectionPoint, runningTotal;

        for (i = 0; i < popSize; i++) {
            selectionPoint = (int) (Math.random() * calctotal());
            runningTotal = 0;
            
            j = 0;
            while (runningTotal <= selectionPoint) {
                runningTotal += population[j].getFitness();
                j++;
            }
            copyPop(offspring, i, population, j - 1);
        }
    }

    /**
     * Select offspring using tournament selection.
     */
    public void tournamentSelection() {
        
        int i, parent1, parent2;
        
        for (i = 0; i < popSize; i++) {
            parent1 = (int) (Math.random() * popSize);
            parent2 = (int) (Math.random() * popSize);
            
            if (population[(int) parent1].getFitness()
                    >= population[(int) parent2].getFitness()) {
                copyPop(offspring, i, population, parent1);
                
            } else {
                copyPop(offspring, i, population, parent2);
            }
        }
    }

    /**
     * Copies the contents of one individual into another.
     * @param to list that contains individual to receive data
     * @param toIndx index of individual in "to"
     * @param from list that contains individual to provide data
     * @param fromIndx index of individual in "from"
     */
    public void copyPop(individual[] to, int toIndx, individual[] from, int fromIndx) {
        
        int geneSize = from[fromIndx].getSize();
        
        for (int i = 0; i < geneSize; i++) {
            to[toIndx].setGene(from[fromIndx].getGene(i), i);
        }
        to[toIndx].setFitness(from[fromIndx].getFitness());
    }

    
    /**
     * Crossover goes over all the offspring and
     * selects a random point in the current individual
     * then swaps the first half with the next individual.
     */
    public void crossover() {
        
        int crossoverPoint, temp, i, j;
        
        for (i = 0; i < popSize; i += 2) {
            crossoverPoint = (int) (Math.random() * offspring[i].getSize());
            
            for (j = 0; j < crossoverPoint; j++) {
                temp = offspring[i].getGene(j);
                offspring[i].setGene(offspring[i + 1].getGene(j), j);
                offspring[i + 1].setGene(temp, j);
            }
        }
    }

    /**
     * Mutation goes through each gene in each individual
     * and generates a random number from 0 to 0.9 
     * if it is bellow the mutation rate the gene gets randomly changed.
     */
    public void mutation() {
        
        int i, j, tempRand;
        double mRate;
        
        for (i = 0; i < popSize; i++) {
            for (j = 0; j < offspring[i].getSize(); j++) {
                mRate = Math.random();
                
                if (mRate < mutationRate) {
                    if (((j+1) % ((population[i].getSize() / 10))) == 0) {
                        tempRand = getRandBit() % 2;
                    } else {
                        tempRand = getRandBit();
                    }
                    offspring[i].setGene(tempRand, j);
                }
            }
        }
    }

    /**
     * Select the best individual in population
     * and copy it over the worst individual and create offspring.
     */
    public void survivorSelection() {
        
        int i, currFit;
        int topFit = 0, topFitIndex = 0;
        int worstFit = 0, worstFitIndex = 0;
        
        individual[] tempIndv = new individual[1];
        tempIndv[0] = new individual();

        for (i = 0; i < popSize; i++) {
            currFit = population[i].getFitness();
            if (topFit <= currFit) {
                topFit = currFit;
                topFitIndex = i;
            }
        }
        copyPop(tempIndv, 0, population, topFitIndex);

        for (i = 0; i < popSize; i++) {
            copyPop(population, i, offspring, i);
        }

        for (i = 1; i < popSize; i++) {
            currFit = population[i].getFitness();
            if (currFit < worstFit) {
                worstFit = currFit;
                worstFitIndex = i;
            }
        }

        copyPop(population, worstFitIndex, tempIndv, 0);
    }

    /**
     * Calculate total fitness of population.
     */
    public int calctotal() {
        
        int fitnessSum = 0;
        
        for (int i = 0; i < popSize; i++) {
            fitnessSum += population[i].getFitness();
        }
        return fitnessSum;
    }

    /**
     * Converts a string into an array of ints.
     * @param toInt a String of 1`s and 0`s
     */
    public int[] stringToInt(String toInt) {  // Waht if string contains chars???
        
        int i;
        int[] ret = new int[toInt.length()];
        
        for (i = 0; i < toInt.length(); i++) {
            ret[i] = Integer.parseInt(String.valueOf(toInt.charAt(i)));
        }
        return ret;
    }

    /**
     * Compares the values if two int arrays using the number 2 as a wildcard.
     * @param a1 array 1
     * @param a2 array 2
     * @return true if the arrays are the same, false if not
     */
    public boolean compareIntArrays(int[] a1, int[] a2) {
        
        int i, correctCount = 0;

        for (i = 0; i < a1.length; i++) {
            
            // Number 2 acts as a wild card
            if ((a1[i] == a2[i]) || (a1[i] == 2)) {
                correctCount++;
            }
        }
        return correctCount == a1.length;
    }

    /**
     * Set all fitness values to 0.
     */
    public void resetFitness() {
        
        int i;
        
        for (i = 0; i < popSize; i++) {
            population[i].setFitness(0);
        }
    }
    
    /**
     * Finds the individual with best fitness and returns its fitness value.
     * @return best fitness in population as a String
     */
    public String getBest(){
        
        int i, currFit, topFit = 0,  topFitIndex = 0;
         
        for (i = 0; i < popSize; i++) {
            currFit = population[i].getFitness();
            if (topFit <= currFit) {
                topFit = currFit;
                topFitIndex = i;
            }
        }
        
        return "" + population[topFitIndex].getFitness();
    }

    /**
     * Getter for mutationRate
     * @return mutationRate
     */
    public String getmutationRate() {
        return Double.toString(mutationRate);
    }
    
    /**
     * Getter for popSize.
     * @return popSize 
     */
    public int getSize() {
        return popSize;
    }
    
    /**
     * Getter for numOfRules.
     * @return numOfRules
     */
    public String getNumOfRules() {
        return Integer.toString(numOfRules);
    }
    
    /**
     * Getter for fileName.
     * @return the name name of the data set file used
     */
    public String getDataSetName() {
        return fileName;
    }
    

    /**
     * Generate random bit between 0 to 2 inclusive.
     */
    public int getRandBit() {
        
        int rand;
        
        rand = (int) (Math.random() * 3);
        
        switch (rand) {
            case 0:
                return 0;
            case 1:
                return 1;
            default:
                return 2;
        }
    }

    /**
     * Print the current population and some fitness statistics.
     */
    public void printPopulation() {
        
        int i, fitnessSum;
        
        fitnessSum = calctotal();
        
        // Print population
        for (i = 0; i < popSize; i++) {
            System.out.println(population[i].toString());
        }
        
        System.out.println("Total population fitness: " + fitnessSum);
        System.out.println("Average fitness: " + (fitnessSum / popSize));
        System.out.println("Best fitness: " + getBest());
    }
}
