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
public class data {

    private final int[] data;
    private final int label;

    public data(int[] newData, int newLabel) {
        data = newData;
        label = newLabel;
    }

    public int[] getFullData() {
        return data;
    }

    public int getData(int pos) {
        return data[pos];
    }

    public int getLabel() {
        return label;
    }

}
