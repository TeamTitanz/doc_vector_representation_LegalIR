package org.titans.fyp.test;

import java.io.*;

/**
 * Created by Buddhi on 8/27/2017.
 */
public class TMatrix {

    public static void main(String[] args) {


        double[][] docVector = loadSerilized_file();
        System.out.println("Size:"+docVector.length);

        for (double[] a: docVector) {
            for (double b: a) {
                System.out.print(b+", ");
            }
            System.out.println();
        }

    }

    private static double[][]  loadSerilized_file(){


        double[][]  docVector = null;
        try {
            File file = new File("D:\\Project\\fyp\\word2vec\\code\\work8\\up4\\doc_vector_representation_LegalIR\\Serialized_folder\\t_matrix.ser");
            if (file.exists()) {
                System.out.println("serialized file found. Reading from it");
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                docVector = (double[][]) in.readObject();
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
