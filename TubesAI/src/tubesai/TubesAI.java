/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tubesai;

import DB.Converter;
import java.util.Random;
import learning.MachineLearning;
import weka.classifiers.Evaluation;

/**
 *
 * @author wira gotama
 */
public class TubesAI {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        //String pathdataset = "dataset.arff"; this is absolute path
        String pathCSV = "res.csv";
        String pathArff = "testing.arff";
        
        MachineLearning l = new MachineLearning();
        
        if (args[0].equals("1")) {
            //learning
            Converter c = new Converter();
            c.loadDataset();
            c.saveToArff(l.datasetPath, c.dataDB);
            l.loadDataset();
            l.makeWordVector();
            l.learningSVM();
            l.loadTestInstance(pathArff);
            //l.classifyTestInstance(pathArff);
            l.evaluate(2);
        }
        else {
            l.loadHypothesis();
            Converter c = new Converter();
            c.saveCSVtoArff(pathCSV, pathArff);
            l.loadDataset();
            l.makeWordVector();
            l.loadTestInstance(pathArff);
            //l.classifyTestInstance(pathArff);
            l.evaluate(2);
        }
    }
}
