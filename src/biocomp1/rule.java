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
public class rule {

    private int[] cond;  // d3: 14 floats
    private int out;
    private final int ruleSize;  // d3: 14

    public rule(int ruleSize) {
        this.ruleSize = ruleSize;
        cond = new int[ruleSize];
        out = 0;
    }

    public int[] getCond() {
        return cond;
    }

    public int getOut() {
        return out;
    }

    public int getRuleSize() {
        return ruleSize;
    }

    public void setGene(int pos, int gene) {
        cond[pos] = gene;
    }

    public int getGene(int pos) {
        return cond[pos];
    }

    public void setOut(int out) {
        this.out = out;
    }

    public void setCond(int[] newCond) {
        cond = newCond;
    }
}
