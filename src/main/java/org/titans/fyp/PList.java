package org.titans.fyp;

import java.io.*;
import java.util.List;

/**
 * Created by Buddhi on 8/27/2017.
 */
public class PList {

    public static void main(String[] args) {


        List<String> docVector = loadSerilized_file();
        System.out.println("Size: "+docVector.size());
        System.out.println(docVector.toString());
    }

    private static List<String>  loadSerilized_file(){


        List<String>  docVector = null;
        try {
            File file = new File("D:\\Project\\fyp\\word2vec\\code\\work8\\up4\\doc_vector_representation_LegalIR\\Serialized_folder\\p_list.ser");
            if (file.exists()) {
                System.out.println("serialized file found. Reading from it");
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                docVector = (List<String>) in.readObject();
                in.close();
                fileIn.close();
            } else {
                System.out.println("Serialized File not found");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return docVector;
    }

}
