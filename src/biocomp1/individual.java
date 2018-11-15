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
    
    public individual() {
        geneSize = 60;
        gene = new int[geneSize];
        fitness = 0;
    }
    
    public int getSize(){
        return geneSize;
    }
    
    public int getFitness(){
        return fitness;
    }
    
    public void setFitness(int f){
        fitness = f;
    }
    
    public void setGene(int g, int i){
        gene[i] = g;
    }
    
    public int getGene(int i) {
        return gene[i];
    }

    public void updateFitness(){
        fitness++;
    }
    
    @Override
    public String toString(){
        String g = "";
        for (int i = 0; i < geneSize; i++) {
            g += " " + gene[i];
        }
        return g + " - " + fitness;
    }
}
