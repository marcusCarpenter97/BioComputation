/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biocomp1;

/**
 *
 * @author marcus
 */
public class individual {

    private final int geneSize;
    private final int[] gene;
    private int fitness;
    private final int ruleSize;

    public individual(int numOfRules, int ruleSize) {
        this.ruleSize = ruleSize;
        geneSize = numOfRules * (ruleSize+1);  // 60 for data 1 or 80 for data 2
        gene = new int[geneSize];
        fitness = 0;
    }

    public int getSize() {
        return geneSize;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int f) {
        fitness = f;
    }

    public void setGene(int g, int i) {
        gene[i] = g;
    }

    public int getGene(int i) {
        return gene[i];
    }

    public void updateFitness() {
        fitness++;
    }

    @Override
    public String toString() {
        
        String g = "";
        
        for (int i = 0; i < geneSize; i++) {
            
            if((i+1) % (ruleSize+1) == 0) {  // 6 for data1 and 8 for data2
                g += " " + gene[i] + " ";
            } else {
            g += gene[i];
            }
        }
        return g + " - " + fitness;
    }
}
