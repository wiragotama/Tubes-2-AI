/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tubesai;

import DB.Converter;
import learning.MachineLearning;

/**
 *
 * @author wira gotama
 */
public class TubesAI {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        String pathdataset = "dataset.arff";
        String pathCSV = "testing.csv";
        String pathArff = "testing.arff";
        
        MachineLearning l = new MachineLearning();
        
        if (args[0].equals("1")) {
            //learning
            Converter c = new Converter();
            c.loadDataset();
            c.saveToArff(pathdataset, c.dataDB);
            l.loadDataset(pathdataset);
            l.makeWordVector();
            l.learningSVM();
            l.evaluate(l.SVM, pathdataset, 1);
        }
        else {
            l.loadHypothesis(1);
            Converter c = new Converter();
            c.saveCSVtoArff(pathCSV, pathArff);
            l.loadTestInstance(pathArff);
            l.classifyTestInstance();
        }
    }
}
