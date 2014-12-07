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
import weka.classifiers.misc.InputMappedClassifier;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;
import weka.filters.unsupervised.attribute.StringToWordVector;
/**
 *
 * @author wira gotama
 */
public class MachineLearning {
    private ArffLoader loader;
    private Instances dataset;
    public InputMappedClassifier cls;
    private int classAttrNum; //default 0 untuk tubes aslinya
    private Instances unlabeled;
    private Instances labeled; //for testring
    public final String datasetPath = "dataset.arff";
    
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
    
    public InputMappedClassifier getModel() {
        return cls;
    }
    
    public int getClassAttrNum() {
        return classAttrNum;
    }
    
    public Instances getDataset() {
        return dataset;
    }
    
    public void loadDataset() throws IOException, Exception {
    /* I.S : path defined
       F.S : arff dataset loaded */ 
        
        loader = new ArffLoader();
        loader.setSource(new File("dataset.arff"));
        dataset = loader.getDataSet();
        dataset.setClass(dataset.attribute("label"));
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
        filter.setIDFTransform(true);
        filter.setInputFormat(dataset);  
        dataset = Filter.useFilter(dataset, filter);
        dataset.setClassIndex(classAttrNum); 
    }
    
    public void evaluate(int evalOption) throws Exception {
    /* I.S : classifier, path and evalOption defined 
       F.S : model evaluated using either 10-fold-cross or full */
        
        Evaluation eval = new Evaluation(dataset);
        if (evalOption==1) { //10-fold-cross
            eval.crossValidateModel(cls, unlabeled, 10, new Random(1));
        }
        else { //full
            eval.evaluateModel(cls, unlabeled);
        }
        
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
    
    public void loadHypothesis() throws Exception {
    /* I.S : option defined, model defined
       F.S : saved hypothesis loaded */
        
        cls = new InputMappedClassifier();
        cls = (InputMappedClassifier) weka.core.SerializationHelper.read("class weka.classifiers.misc.InputMappedClassifier.model");
        readClassAttrNum();
    }
    
    public void learningSVM() throws Exception {
    /* I.S : data defined
       F.S : SVM hypothesis constructed */
        
        cls = new InputMappedClassifier();
        cls.setOptions(Utils.splitOptions("-I -trim -W weka.classifiers.functions.SMO -- -C 1.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007\""));
        cls.buildClassifier(dataset);
        saveHypothesis(cls);
    }
    
    public void classifyTestInstance(String pathArff) throws Exception {
    /* I.S : test instances defined, classifier defined
       F.S : test instances classification output to screen */
       
        double clsLabel = 0;
        for (int i=0; i<unlabeled.numInstances(); i++) {
            clsLabel = cls.classifyInstance(unlabeled.instance(i));
            labeled.instance(i).setClassValue(clsLabel);
        }
        
        //save class attribute
        FileWriter fw = new FileWriter("result.csv");
        PrintWriter pw = new PrintWriter(fw);
        pw.println("Full_text,label");
        
        loader = new ArffLoader();
        loader.setSource(new File(pathArff));
        Instances full_text = loader.getDataSet();
        full_text.setClass(full_text.attribute("label"));
        
        for (int i=0; i<labeled.numInstances(); i++) {
            pw.println("\""+full_text.instance(i).stringValue(1)+"\""+","+labeled.instance(i).stringValue(0));
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
        
        loader = new ArffLoader();
        loader.setSource(new File(path));
        unlabeled = loader.getDataSet();
        unlabeled.setClass(unlabeled.attribute("label"));
        
        File stopwrods = new File("stopwordsFolder/stopwords.txt");
        StringToWordVector filter = new StringToWordVector();
        filter.setAttributeIndices("first-last");
        filter.setWordsToKeep(295);
        filter.setDoNotOperateOnPerClassBasis(true);
        filter.setLowerCaseTokens(true);
        filter.setStopwords(stopwrods);
        filter.setUseStoplist(true);
        filter.setIDFTransform(true);
        filter.setInputFormat(unlabeled);
        unlabeled = Filter.useFilter(unlabeled, filter);
        labeled = new Instances(unlabeled);
    }
    
    public void attrMapping() throws Exception {
    /*  I.S : dataset and datatest loaded
        F.S : test instance attribute mapped to dataset attribute */
        
        System.out.println(unlabeled.numAttributes());
        
        //delete attribute
        int i=0;
        while (i<unlabeled.numAttributes()) {
            boolean res = false;
            for (int j=0; j<dataset.numAttributes(); j++) {
                res = unlabeled.attribute(i).name().trim().equalsIgnoreCase(dataset.attribute(j).name().trim());
                if (res) break;
            }
            if (!res) {
                unlabeled.deleteAttributeAt(i);
            }
            else i++;
        }
        
        //add new attribute
        for (i=0; i<dataset.numAttributes(); i++) {
            boolean res = false;
            for (int j=0; j<unlabeled.numAttributes() && !res; j++) {
                res = unlabeled.attribute(j).name().trim().equalsIgnoreCase(dataset.attribute(i).name().trim());
            }
            if (!res) {
                Add add = new Add();
                add.setAttributeIndex("last");
                add.setAttributeName(dataset.attribute(i).name());
                add.setInputFormat(unlabeled);
                unlabeled = Filter.useFilter(unlabeled, add);
                for (int j=0; j<unlabeled.numInstances(); j++) {
                    unlabeled.instance(j).setValue(unlabeled.attribute(dataset.attribute(i).name()), 0);
                }
            }   
        }
        
        System.out.println(unlabeled.numAttributes());
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
