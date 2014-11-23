/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package featureExtraction;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Vector;
import java.lang.String;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import weka.core.Instances;
import weka.experiment.InstanceQuery;
/**
 *
 * @author wira gotama
 */
public class FeatureExtractor {
    private Instances dataDB;
    private ResultSet fetchResult;
    private Vector < Vector<String> > featuredWords;
    private Vector < DataInstance > extractionResult;
    private Vector < String > wordVector;
    /*+----------+-----------------------------+
    | ID_Vector| LABEL                       |
    +----------+-----------------------------+
    |        1 | Pendidikan                  |
    |        2 | Politik                     |
    |        3 | Hukum dan Kriminal          |
    |        4 | Sosial Budaya               |
    |        5 | Olahraga                    |
    |        6 | Teknologi dan Sains         |
    |        7 | Hiburan                     |
    |        8 | Bisnis dan Ekonomi          |
    |        9 | Kesehatan                   |
    |       10 | Bencana dan kecelakaan      |
    +----------+-----------------------------+*/
    
    public static void main(String[] args) throws Exception {
        FeatureExtractor f = new FeatureExtractor();
        f.loadDataset();
        f.loadFeaturedWords();
        f.makeDatasetInstance();
        f.saveToArff("dataset.arff");
    }
    
    public FeatureExtractor() { 
        extractionResult = new Vector<DataInstance>();
        featuredWords = new Vector< Vector<String> >();
    }
    
    public void loadDataset() throws Exception { 
    /* I.S : -
       F.S : dataDB laoded from DB */
        
        InstanceQuery query = new InstanceQuery();
        //DB URL set in configuration file
        query.setUsername("root");
        query.setPassword("");
        query.setQuery("select full_text, label from artikel natural join artikel_kategori_verified natural join kategori");
        dataDB = query.retrieveInstances();
        
        //System.out.println(dataDB.numInstances());
        //System.out.println(dataDB.lastInstance().stringValue(0));
        //karena tokenizernya aneh, gw pake yg punya sendiri
    }
    
    public void loadFeaturedWords() throws IOException {
    /* I.S : -
       F.S : featured words laoded from DB */
        
        for (int i=1; i<=10; i++) {
            Vector<String> temp = new Vector<String>();
            BufferedReader br = new BufferedReader(new FileReader("featuredWordSource/"+mapping(i)+".txt"));
            String line = br.readLine();
            while (line!=null) {
                line = line.toLowerCase();
                temp.add(line);
                line = br.readLine();
            }
            featuredWords.add(temp);
        }
    }
    
    public void makeDatasetInstance() {
    /* I.S : dataDB loaded, featured words loaded
       F.S : make learning dataset from dataDB */
        
        for (int i=0; i<dataDB.numInstances(); i++) {
            DataInstance d = new DataInstance();
            int res;
            for (int j=0; j<10; j++) {
                res = countWordFrequency(j, dataDB.instance(i).stringValue(0)); //full_text diproses
                d.addValues(res);
            }
            d.setLabel(dataDB.instance(i).stringValue(1));
            extractionResult.add(d);
        }
        
        for (int i=0; i<extractionResult.size(); i++) {
            System.out.println(extractionResult.get(i).toString());
        }
    }
    
    public int countWordFrequency(int labelNum, String text) {
    /* I.S : featuredwords loaded, dataDB loaded
       F.S : featured word frequency of label counted for dataDB[idx] */
        
        String temp = "";
        int count = 0;
        int[] arr = new int[featuredWords.get(labelNum).size()]; 
        Arrays.fill(arr, 0);
        for (int i=0; i<text.length(); i++) {
            if (isAlpha(text.charAt(i))) {
                temp += text.charAt(i);
            }
            else {//masukkan ke word vector
                if (temp!="") {
                    temp = temp.toLowerCase();
                    int id = featuredWords.get(labelNum).indexOf(temp);
                    if (id!=-1 && arr[id]==0) {
                        arr[id] = 1;
                        count++;
                    }
                    temp = "";
                }
            }
        }
        return count;
    }
            
    public void saveToArff(String filename) throws IOException { 
    /* I.S : extraction process done
       F.S : extraction result saved in CSV format */
        
        FileWriter fw = new FileWriter(filename);
        PrintWriter pw = new PrintWriter(fw);
        
        pw.println("@relation news_aggregator");
        pw.println();
        String temp="";
        for (int i=1; i<=10; i++) {
            pw.println("@attribute "+mapping(i).replace(" ","")+" numeric");
            if (i<10)
                temp += mapping(i).replace(" ","") + ", ";
            else 
                temp += mapping(i).replace(" ","");
        }
        pw.println("@attribute label {"+temp+"}");
        pw.println();
        pw.println("@data");
        for (int i=0; i<extractionResult.size(); i++) {
                pw.println(extractionResult.get(i).toString());
        }

        //Flush the output to the file
        pw.flush();

        //Close the Print Writer
        pw.close();

        //Close the File Writer
        fw.close(); 
    }
    
    public static boolean isAlpha(char c) {
        return (((c-'A'>=0) && ('Z'-c<=25)) || ((c-'a'>=0) && ('z'-c<=25)));
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
    
    public void aaa() throws IOException {
        for (int i=0; i<featuredWords.size(); i++) {
            FileWriter fw = new FileWriter("featuredWordSource/"+mapping(i+1)+".txt");
            PrintWriter pw = new PrintWriter(fw);
            System.out.println("featuredWordSource/"+mapping(i+1)+".txt");
            for (int j=0; j<featuredWords.get(i).size(); j++) {
                String temp = featuredWords.get(i).get(j);
                System.out.println(temp);
                boolean stop = false;
                String simpan = "";
                for (int k=0; k<temp.length() && !stop; k++) {
                    if (isAlpha(temp.charAt(k))) {
                        simpan = simpan + temp.charAt(k);
                    }
                    else {
                        stop = true;
                    }
                }
                System.out.println(simpan);
                pw.println(simpan);
            }
            pw.flush();
            pw.close();
            fw.close();
        }
    }
}
