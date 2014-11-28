/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DB;
import java.util.Vector;
import java.lang.String;
/**
 *
 * @author wira gotama
 */
public class DataInstance {
    private Vector<Integer> attributeValues;
    private String label;
    static final int numAttr = 10;

    public DataInstance() {
        attributeValues = new Vector<Integer>(numAttr);
    }
    
    public void addValues(int value) {
        attributeValues.add(value);
    }
    
    public void setAttributeValue(int idx, int value) {
        attributeValues.set(idx, value);
    }
    
    public void resetAttributeValue() {
        attributeValues = new Vector<Integer>(numAttr);
    }
    
    public void setLabel(String l) {
        label = l;
    }
    
    public Vector<Integer> getAttributeValues() {
        return attributeValues;
    }
    
    public int getAttributeValue(int idx) {
        return attributeValues.get(idx);
    }
    
    public String getLabel() {
        return label;
    }
    
    public String toString() {
        String temp = "";
        for (int i=0; i<attributeValues.size(); i++) {
            temp += attributeValues.get(i) + ",";
        }
        temp += this.label.replace(" ", "");
        return temp;
    }
}
