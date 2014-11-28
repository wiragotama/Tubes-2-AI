/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package learning;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.Utils;
import weka.classifiers.Evaluation;
import weka.classifiers.Classifier;
import java.io.*;
import java.util.Random;
import weka.classifiers.functions.LibSVM;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;
import weka.filters.unsupervised.attribute.Standardize;
import weka.filters.unsupervised.attribute.StringToWordVector;
/**
 *
 * @author wira gotama
 */
public class MachineLearning {
    private ArffLoader loader;
    private Instances dataset;
    public LibSVM SVM;
    private int classAttrNum; //default 0 untuk tubes aslinya
    private Instances unlabeled;
    private Instances labeled; //for testring
    
    public MachineLearning() {
        classAttrNum = 0;
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
    
    public void loadDataset(String path) throws IOException, Exception {
    /* I.S : path defined
       F.S : arff dataset loaded */ 
        loader = new ArffLoader();
        loader.setSource(new File(path));
        dataset = loader.getDataSet();
        dataset.setClassIndex(1);
    }
    
    public void makeWordVector() throws Exception {
    /* I.S : dataset loaded
       F.S : dataset met to word vector */
        
        File stopwrods = new File("stopwordsFolder/stopwords.txt");
        StringToWordVector filter = new StringToWordVector();
        filter.setAttributeIndices("first-last");
        filter.setWordsToKeep(295);
        filter.setDoNotOperateOnPerClassBasis(true);
        filter.setLowerCaseTokens(true);
        filter.setStopwords(stopwrods);
        filter.setUseStoplist(true);
        //filter.setTFTransform(true);
        filter.setIDFTransform(true);
        filter.setInputFormat(dataset);
        //filter.setMinTermFreq(15);
        dataset = Filter.useFilter(dataset, filter);
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
        
        System.out.println(eval.toSummaryString("\nResults\n\n", false));
        saveHypothesis(SVM);
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
        readClassAttrNum();
        System.out.println(SVM.toString());
    }
    
    public void learningSVM() throws Exception {
    /* I.S : data defined
       F.S : SVM hypothesis constructed */
        SVM = new LibSVM(); // new instance of SVM
        SVM.setOptions(Utils.splitOptions("**-G 0.09** -S 0 -T 3 -D 3 -R 0.0 -N 0.5 -M 100.0 -C 1.0 -E 0.9 -P 0.1 -seed 1 -B 1")); 
        SVM.buildClassifier(dataset);
        saveHypothesis(SVM);
    }
    
    public void classifyTestInstance() throws Exception {
    /* I.S : test instances defined, classifier defined
       F.S : test instances classification output to screen */
        
        double clsLabel = 0;
        for (int i=0; i<unlabeled.numInstances(); i++) {
            clsLabel = SVM.classifyInstance(unlabeled.instance(i));
            labeled.instance(i).setClassValue(clsLabel);
        }
        
        //save class attribute
        FileWriter fw = new FileWriter("result.csv");
        PrintWriter pw = new PrintWriter(fw);
        pw.println("Full_text,label");
        for (int i=0; i<labeled.numInstances(); i++) {
            pw.println("\""+dataset.instance(i).stringValue(0)+"\""+","+labeled.instance(i).stringValue(0));
        }
        //Flush the output to the file
        pw.flush();
        //Close the Print Writer
        pw.close();
        //Close the File Writer
        fw.close();
    }
    
    public void loadTestInstance(String path) throws Exception {
    /* I.S : path defined
       F.S : arff test instance loaded */
        
        loadDataset(path);
        File stopwrods = new File("stopwordsFolder/stopwords.txt");
        StringToWordVector filter = new StringToWordVector();
        filter.setAttributeIndices("first-last");
        filter.setWordsToKeep(295);
        filter.setDoNotOperateOnPerClassBasis(true);
        filter.setLowerCaseTokens(true);
        filter.setStopwords(stopwrods);
        filter.setUseStoplist(true);
        //filter.setTFTransform(true);
        filter.setIDFTransform(true);
        filter.setInputFormat(dataset);
        //filter.setMinTermFreq(15);
        unlabeled = Filter.useFilter(dataset, filter);
        unlabeled.setClassIndex(classAttrNum);
        labeled = new Instances(unlabeled);
    }
    
    private String mapping(int num) {
        switch (num) {
            case 1 : {
                return "Pendidikan";
            }
            case 2 : {
                return "Politik";
            }
            case 3 : {
                return "Hukum dan Kriminal";
            }
            case 4 : {
                return "Sosial Budaya";
            }
            case 5 : {
                return "Olahraga";
            }
            case 6 : {
                return "Teknologi dan Sains";
            }
            case 7 : {
                return "Hiburan";
            }
            case 8 : {
                return "Bisnis dan Ekonomi";
            }
            case 9 : {
                return "Kesehatan";
            }
            case 10 : {
                return "Bencana dan Kecelakaan";
            }
            default : {
                return "";
            }
        }
    }
}
