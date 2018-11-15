/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biocomp1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
    private final int[] crossoverPoints;
    private final double[] mrates;
    private final rule currRule;
    private final int numOfRules;
    private final ArrayList<data> trainingData;

    public population() {
        popSize = 10;  //test
        mutationRate = 0.02;  // 1 divided by gene size
        population = new individual[popSize];
        offspring = new individual[popSize];
        crossoverPoints = new int[popSize];
        mrates = new double[popSize];
        numOfRules = 10;
        currRule = new rule();
        trainingData = new ArrayList<>();
    }
    
    public void initPop() {
        int i, j;
        for (i = 0; i < popSize; i++) {
            population[i] = new individual();
            offspring[i] = new individual();
            for (j = 0; j < population[i].getSize(); j++) {
                population[i].setGene(getRandBit(), j);
            }
        }
        try{
            readTrainingData();
        } catch (FileNotFoundException e) {
            System.out.println("File error!\n" + e);
        }
    }
    
    /**
     *  for each individual 
     *      for each rule
     *          for each gene in rule
     *              set currGene into current gene in rule
     *          set currGene into out
     *          if new rule is in training data 
     *              increment fitness
     */
    public void makeRules() {
            
        int i, j, k, currGene;
        
        resetFitness();
        
        for(i = 0; i < popSize; i++) {
            currGene = 0;
            for(j = 0; j < numOfRules; j++) {
                for(k = 0; k < currRule.getRuleSize(); k++) {
                    currRule.setGene(k, population[i].getGene(currGene));
                    currGene++;
                }
                currRule.setOut(population[i].getGene(currGene));
                currGene++;
                
                if(findMatch(currRule)){
                    population[i].updateFitness();
                }
            }
        }
    }
    
    public void readTrainingData() throws FileNotFoundException {
        int[] tempData;
        int tempInt;
        File f = new File("/home/marcus/NetBeansProjects/biocomp1/src/dataFiles/data1.txt");
        Scanner data = new Scanner(f);
        data.nextLine();
        while(data.hasNext()){
            tempData = stringToInt(data.next());
            tempInt = Integer.parseInt(String.valueOf(data.next()));
            data d = new data(tempData, tempInt);
            trainingData.add(d);
        }
    }
    
    public void printTrainingData(){
        for(data d : trainingData){
            System.out.print(d.toString());
            System.out.print(" - " + d.getLabel() + "\n");
        }
    }
    
    public boolean findMatch(rule currRule) {
        for(data d : trainingData){
           if(compareIntArrays(currRule.getCond(), d.getFullData())){
              if(currRule.getOut()== d.getLabel()){
                  return true;
              }
           }
        }
        return false;
    }
    
    public void roulettewheelSelection(){
        int i, j, selectionPoint, runningTotal;
        
        for(i = 0; i < popSize; i++) {
            selectionPoint = (int) (Math.random() * calctotal());
            runningTotal = 0;
            j = 0;
            while(runningTotal <= selectionPoint){
                runningTotal += population[j].getFitness();
                j++;
            }
            copyPop(offspring, i, population, j-1);
        }
    }
    
    public void tournamentSelection(){
        int i;
        int parent1;
        int parent2;
        for (i = 0; i < popSize; i++) {
            parent1 = (int) (Math.random() * popSize);
            parent2 = (int) (Math.random() * popSize);
            if (population[(int)parent1].getFitness() >= 
                    population[(int)parent2].getFitness()) {
                copyPop(offspring, i, population, parent1);
            } else {
                copyPop(offspring, i, population, parent2);
            }
        }
    }
    
    public void copyPop(individual[] to, int toIndx, individual[] from, int fromIndx) {
        int geneSize = from[fromIndx].getSize();
        for (int i = 0; i < geneSize; i++) {
            to[toIndx].setGene(from[fromIndx].getGene(i), i);
        }
        to[toIndx].setFitness(from[fromIndx].getFitness());
    }
    
    public void crossover(){
        int crossoverPoint;
        int temp;
        int i, j;
        for (i = 0; i < popSize; i+=2) {
            crossoverPoint = (int) (Math.random() * offspring[i].getSize());
            crossoverPoints[i] = crossoverPoint;
            for (j = 0; j < crossoverPoint; j++) {
                temp = offspring[i].getGene(j);
                offspring[i].setGene(offspring[i+1].getGene(j), j);
                offspring[i+1].setGene(temp, j);
            }
        }        
    }
    
    public void mutation(){
        int i, j;
        double mRate;
        for (i = 0; i < popSize; i++) {
            for (j = 0; j < offspring[i].getSize(); j++) {
                mRate = Math.random();
                if (mRate < mutationRate) {
                    if (offspring[i].getGene(j) == 1) {
                        offspring[i].setGene(0, j);
                    } else {
                        offspring[i].setGene(1, j);
                    }
                }
            }
        }
    }
    
    public void survivorSelection() {
        int i, currFit;
        int topFit = 0;//population[0].getFitness();
        int worstFit = 0;//population[0].getFitness(); //???
        int topFitIndex = 0;
        int worstFitIndex = 0;
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
    
    public String printBest() {
        int i, currFit, topFit = 0, topFitIndex = 0;
        
        for (i = 0; i < popSize; i++) {
            currFit = population[i].getFitness();
            if (topFit <= currFit) {
                topFit = currFit;
                topFitIndex = i;
            }
        }
        return ""+population[topFitIndex].getFitness();
        //System.out.println("Best: " + population[topFitIndex].toString());
    }

    public int calctotal(){
        int fitnessSum = 0;
        for (int i = 0; i < popSize; i++) {
            fitnessSum += population[i].getFitness();
            //System.out.println(population[i].toString());
        }
        return fitnessSum;
    }
    
    public int[] stringToInt(String toInt){
        int i;
        int[] ret = new int[toInt.length()];
        for(i = 0; i < toInt.length(); i++){
            ret[i] = Integer.parseInt(String.valueOf(toInt.charAt(i)));
        }
        return ret;
    }
    
    public boolean compareIntArrays(int[] a1, int[] a2){
        int i;
        
        for(i = 0; i < a1.length; i++){
            if(a1[i] != a2[i]){
                return false;
            }
        }
        return true;
    }
    
    public void resetFitness(){
        int i;
        for(i = 0; i < popSize; i++){
            population[i].setFitness(0);
        }
    }
    
    public String total() {
        return ""+calctotal();
    }
    
    public String average(){
        return ""+(calctotal()/popSize);
    }
    
    public String getmutationRate(){
        return ""+mutationRate;
    }

    public int getRandBit() {
        int rand;
        rand = (int) (Math.random() * 2); 
        switch (rand) {
            case 0:
                return 0;
            case 1:
                return 1;
            default :
                return 2;
        }
    }
    
    public void printPopulation() {
        int fitnessSum = 0;
        for (int i = 0; i < popSize; i++) {
            fitnessSum += population[i].getFitness();
            System.out.println(population[i].toString());
        }
        System.out.println("Total population fitness: " + fitnessSum);
        System.out.println("Average: " + (fitnessSum/popSize));
    }
    
    public void printOffspring() {
        int fitnessSum = 0;
        for (int i = 0; i < popSize; i++) {
            fitnessSum += offspring[i].getFitness();
            System.out.println(offspring[i].toString() + " - " + crossoverPoints[i]);
        }
        System.out.println("Total offspring fitness: " + fitnessSum);
    }

}
