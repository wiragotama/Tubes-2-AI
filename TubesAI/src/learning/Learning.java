/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package learning;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.Utils;
import weka.classifiers.functions.SMO;
import weka.classifiers.Evaluation;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LibSVM;
import libsvm.*;
import java.io.*;
import java.util.Random;
import java.util.Scanner;
import weka.core.converters.ConverterUtils;
/**
 *
 * @author wira gotama
 */
public class Learning {
    private ArffLoader loader;
    private Instances dataset;
    public LibSVM SVM;
    private int classAttrNum; //default 10 untuk tubes aslinya
    private Instances unlabeled;
    private Instances labeled; //for testring
    
    
    public static void main(String[] args) throws IOException, Exception { //main disini untuk memudahkan testing aja
        
        Scanner in = new Scanner(System.in);
        int validationOption = in.nextInt();
        String path = "E://ITB 2012/TEKNIK INFORMATIKA 2012 NIM 13512015/Semester 5/Intelegensia Buatan/Tugas/Tubes 2/TubesAI/dataset.arff";
        
        Learning l = new Learning(10);
        l.loadDataset(path);
        System.out.println("Succesfully load the dataset");
        l.learningSVM();
        l.evaluate(l.SVM, path, validationOption);
        //l.loadHypothesis(validationOption);
    }
    
    public Learning(int attrNum) {
        classAttrNum = attrNum;
    }
    
    
    public void setDataset(Instances data, int classAttrNum) {
        this.dataset = data;
        this.classAttrNum = classAttrNum;
        this.dataset.setClassIndex(this.classAttrNum);
    }
    
    public void setClassAttrNum(int attrNum) {
        classAttrNum = attrNum;
    }
    
    public void setLabeled(Instances l) {
        labeled = l;
    }
    
    public void setUnlabeled(Instances ul) {
        unlabeled = ul;
    }
    
    public LibSVM getModel() {
        return SVM;
    }
    
    public int getClassAttrNum() {
        return classAttrNum;
    }
    
    public Instances getDataset() {
        return dataset;
    }
    
    public void loadDataset(String path) throws IOException {
    /* I.S : path defined
       F.S : arff dataset loaded */
        
        loader = new ArffLoader();
        loader.setSource(new File(path));
        dataset = loader.getDataSet();
        dataset.setClassIndex(classAttrNum);
    }
    
    public void evaluate(Classifier cls, String Path, int evalOption) throws Exception {
    /* I.S : classifier, path and evalOption defined 
       F.S : model evaluated using either 10-fold-cross or full */
        
        Evaluation eval = new Evaluation(dataset);
        if (evalOption==1) { //10-fold-cross
            eval.crossValidateModel(cls, dataset, 10, new Random(1));
        }
        else { //full
            eval.evaluateModel(cls, dataset);
        }
        
        System.out.println(cls.toString());
        System.out.println(eval.toSummaryString("\nResults\n\n", false));
    }
    
    public void saveHypothesis(Classifier cls) throws Exception {
    /* I.S : cls defined
       F.S : cls saved to external file */
        
        weka.core.SerializationHelper.write(cls.getClass().toString()+".model", cls);
        
        //save class attribute
        FileWriter fw = new FileWriter("classAttribute.txt");
        PrintWriter pw = new PrintWriter(fw);

        //Write to file line by line
        pw.println(classAttrNum);
        //Flush the output to the file
        pw.flush();
        //Close the Print Writer
        pw.close();
        //Close the File Writer
        fw.close(); 
    }
    
    public void readClassAttrNum() throws Exception {
    /* I.S : -
       F.S : classAttrNum load from external file */
        
        BufferedReader reader = new BufferedReader(new FileReader("classAttribute.txt"));
        String line = null;
        while ((line = reader.readLine()) != null) {
            classAttrNum = Integer.parseInt(line);
        }
    }
    
    public void loadHypothesis(int option) throws Exception {
    /* I.S : option defined, model defined
       F.S : saved hypothesis loaded */
        
        SVM = new LibSVM();
        SVM = (LibSVM) weka.core.SerializationHelper.read("class weka.classifiers.functions.LibSVM.model");
        readClassAttrNum(); dataset.setClassIndex(classAttrNum);
        System.out.println(SVM.toString());
    }
    
    public void learningSVM() throws Exception {
    /* I.S : data defined
       F.S : SVM hypothesis constructed */
        
        SVM = new LibSVM(); // new instance of SVM
        SVM.setOptions(Utils.splitOptions("-S 0 -K 2 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 2.0 -P 0.1 -seed 1")); 
        SVM.buildClassifier(dataset);
        saveHypothesis(SVM);
    }
    
    public void classifyTestInstance() throws Exception {
    /* I.S : test instances defined, classifier defined
       F.S : test instances classification output to screen */
        
        System.out.println("Data Test");
        double clsLabel = 0;
        for (int i = 0; i < unlabeled.numInstances(); i++) {
            clsLabel = SVM.classifyInstance(unlabeled.instance(i));
            labeled.instance(i).setClassValue(clsLabel);
        }
        ConverterUtils.DataSink.write(System.out, labeled);
        System.out.println();
    }
}
