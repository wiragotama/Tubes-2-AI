/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DB;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.experiment.InstanceQuery;
/**
 *
 * @author wira gotama
 */
public class Converter {
    public Instances dataDB;
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
    
    public Converter() { 
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
    }
    
    public void saveCSVtoArff(String filename, String outputname) throws IOException, Exception {
    /* I.S : filename and outputname defined
       F.S : save CSV file to arff */
        
        CSVLoader loader = new CSVLoader();
        //loader.setOptions(Utils.splitOptions("-H"));
        loader.setSource(new File(filename));
        Instances data = loader.getDataSet();
        
        FileWriter fw = new FileWriter(outputname);
        PrintWriter pw = new PrintWriter(fw);
        
        printArffHeader(pw);
        for (int i=0; i<data.numInstances(); i++) {
            pw.println("\""+data.instance(i).stringValue(2).replace("\"", "").replace("\n"," ")+"\", "+data.instance(i).stringValue(13).replace(" ",""));
        }
        
        //Flush the output to the file
        pw.flush();

        //Close the Print Writer
        pw.close();

        //Close the File Writer
        fw.close(); 
    }
            
    public void saveToArff(String filename, Instances dataDB) throws IOException { 
    /* I.S : dataset loaded
       F.S : data saved to arff format */
        
        FileWriter fw = new FileWriter(filename);
        PrintWriter pw = new PrintWriter(fw);
        
        printArffHeader(pw);
        for (int i=0; i<dataDB.numInstances(); i++) {
            pw.println("\""+dataDB.instance(i).stringValue(0).replace("\"", "").replace("\n"," ")+"\", "+dataDB.instance(i).stringValue(1).replace(" ",""));
        }

        //Flush the output to the file
        pw.flush();

        //Close the Print Writer
        pw.close();

        //Close the File Writer
        fw.close(); 
    }
    
    public void printArffHeader(PrintWriter pw) {
    /* I.S : -
       F.S : arff header output to file */
        
        pw.println("@relation news_aggregator");
        pw.println();
        pw.println("@attribute "+"full_text "+" string");
        String temp="";
        for (int i=1; i<=10; i++) {
            if (i<10)
                temp += mapping(i).replace(" ","") + ", ";
            else 
                temp += mapping(i).replace(" ","");
        }
        pw.println("@attribute label {"+temp+"}");
        pw.println();
        pw.println("@data");
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
