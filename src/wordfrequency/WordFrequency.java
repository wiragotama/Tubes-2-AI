/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package wordfrequency;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Vector;
import java.lang.String;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
/**
 *
 * @author wira gotama
 */
public class WordFrequency {

    /**
     * @param args the command line arguments
     */
    static ResultSet postFetchCategory;
    static Vector<String> wordVector;
    static Vector<Integer> wordFrequency;
    static Vector<Integer> postList;
    static ResultSet fullText;
    static ResultSet CategoryName;
    
    public static void main(String[] args) throws SQLException {
        // Mengakses instans
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance();
        wordVector = new Vector<String>();
        wordFrequency = new Vector<Integer>();
        postList = new Vector<Integer>();
        
        // Set konfigurasi
        databaseAccess.setDatabase("news_aggregator");
        databaseAccess.setUsername("root"); // secara default root
        databaseAccess.setPassword(""); // secara default string kosong

        // Membuka koneksi
        try {
                databaseAccess.openConnection();
                System.out.println("Berhasil membuka koneksi");
        } catch (SQLException e) {
                System.out.println("Gagal membuka koneksi");
                System.out.println(e);
        }
        
        int num = 3;
        getCategoryName(num);
        String categName = "";
        while (CategoryName.next()) categName = CategoryName.getString("label");
        for (int i=num; i<=num; i++) {
            FetchPostList(i);
            if (postFetchCategory!=null) { try {
                //yo man diproses word vector tiap kata
                while (postFetchCategory.next()) {
                      int p = postFetchCategory.getInt("id_artikel");
                      postList.add(p);
                }
                } catch (SQLException ex) {
                    Logger.getLogger(WordFrequency.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        //words extraction
        for (int i=0; i<postList.size(); i++) {
            String process = null;
            FetchArticle(postList.get(i).intValue());  
            while (fullText.next())
                 process = fullText.getString("full_text");
            if (process!=null) processText(process);
        }
        
        //outputkeun
        try {
            outputToExternalFile(categName+".txt");
        } catch (IOException ex) {
            Logger.getLogger(WordFrequency.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Menutup koneksi
        try {
                databaseAccess.closeConnection();
        } catch (SQLException e) {
                e.printStackTrace();
        }
    }
    
    public static void getCategoryName(int id_kategori) {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance();
        ArrayList<String> columns = new ArrayList(Arrays.asList("label"));
        String condition = "id_kelas="+id_kategori; // jika ada kondisi dapat dimasukkan seperti pada
        // INSERT dan DELETE;
        CategoryName = null;
        try {
                CategoryName = databaseAccess.selectRecords("kategori", columns, condition);
        } catch (SQLException e1) {
                System.out.println(e1);
        }
    }
    
    public static void FetchPostList(int id_kategori) {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance();
        ArrayList<String> columns = new ArrayList(Arrays.asList("id_artikel"));
        String condition = "id_kelas="+id_kategori; // jika ada kondisi dapat dimasukkan seperti pada
        // INSERT dan DELETE;
        postFetchCategory = null;
        try {
                postFetchCategory = databaseAccess.selectRecords("artikel_kategori_verified", columns, condition);
                
        } catch (SQLException e1) {
                System.out.println(e1);
        }
    }
    
    public static void FetchArticle(int id_artikel) {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance();
        ArrayList<String> columns = new ArrayList(Arrays.asList("full_text"));
        String condition = "id_artikel="+id_artikel; // jika ada kondisi dapat dimasukkan seperti pada
        fullText = null;
        try {
                fullText = databaseAccess.selectRecords("artikel", columns, condition);
        } catch (SQLException e1) {
                System.out.println(e1);
        }
    } 
    public static void processText(String text) {
        String temp = "";
        for (int i=0; i<text.length(); i++) {
            if (isAlpha(text.charAt(i))) {
                temp += text.charAt(i);
            }
            else {//masukkan ke word vector
                if (temp!="") {
                    boolean stop = false;
                    for (int j=0; j<wordVector.size() && !stop; j++) {
                        if (wordVector.get(j).equalsIgnoreCase(temp)) {
                            stop = true;
                            wordFrequency.set(j, wordFrequency.get(j)+1);
                        }
                    }
                    if (!stop) {
                        wordVector.add(temp);
                        wordFrequency.add(1);
                    }
                    temp = "";
                }
            }
        }
    }
    
    public static boolean isAlpha(char c) {
        return (((c-'A'>=0) && ('Z'-c<=25)) || ((c-'a'>=0) && ('z'-c<=25)) || ((c-'0'>=0) && ('9'-c<=9)));
    }
     
    public static void outputToExternalFile(String filename) throws IOException {
        FileWriter fw = new FileWriter(filename);
        PrintWriter pw = new PrintWriter(fw);

        //Write to file line by line
        int threshold = (int) (postList.size()/10);
        for (int i=0; i<wordVector.size(); i++) { 
           if (wordFrequency.get(i)>threshold)
                pw.println(wordVector.get(i) + "        " + wordFrequency.get(i));
        }

        //Flush the output to the file
        pw.flush();

        //Close the Print Writer
        pw.close();

        //Close the File Writer
        fw.close(); 
    }
}
