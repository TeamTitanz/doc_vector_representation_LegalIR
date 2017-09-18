package org.titans.fyp;

import java.io.*;
import java.util.List;

/**
 * Created by Buddhi on 8/27/2017.
 */
public class DocumentVector {

    public static void main(String[] args) {


        List<List<Double>> docVector = loadSerilized_file();
        System.out.println("Size:" + docVector.size());

        int count = 0;
        double[] vec = new double[600];
        for (List<Double> a : docVector) {
            int index = 0;
            for (double b : a) {
//                System.out.print(b + " ");
                if (b != 0.0) {
//                    System.out.println(b);
                }
                vec[index++] += b;
            }
//            System.out.println();
        }

        int oCount = 0;
        for (double val : vec) {
            System.out.print(val + ", ");
            if (val == 0.0) {
                oCount++;
            }
        }

        System.out.println("\nMin Value: " + getMinValue(vec));
        System.out.println("Max Value: " + getMaxValue(vec));
        System.out.println("Zero Count = " + oCount);

    }

    // getting the maximum value
    public static double getMaxValue(double[] array) {
        double maxValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxValue = array[i];
            }
        }
        return maxValue;
    }

    // getting the miniumum value
    public static double getMinValue(double[] array) {
        double minValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < minValue) {
                minValue = array[i];
            }
        }
        return minValue;
    }

    private static List<List<Double>> loadSerilized_file() {


        List<List<Double>> docVector = null;
        try {
            File file = new File("D:\\Project\\fyp\\word2vec\\code\\work8\\up4\\doc_vector_representation_LegalIR\\Serialized_folder\\document_vector.ser");
            if (file.exists()) {
                System.out.println("serialized file found. Reading from it");
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                docVector = (List<List<Double>>) in.readObject();
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
