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

    private int[] cond;
    private int out;
    private final int ruleSize;

    public rule() {
        ruleSize = 7;  // 5 for data 1 or 7 for data 2
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
