package org.titans.fyp;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Keetmalin on 6/13/2017
 * Project - doc_vector_representation_LegalIR
 */
public class Calculation {

    public List<List<Double>> CalDocumentVector(List<String> inputWordList, ArrayList<String> vocabulary, double[][] t_matrix) {

        List<List<Double>> DocumentVector = new ArrayList<List<Double>>();

        File f = new File("./LawIE/DocVector/Output");
        FilenameFilter textFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".txt");
            }
        };

        File[] files = f.listFiles(textFilter);
        for (int docIndex = 0; docIndex < files.length; docIndex++) {
            try {
                String fileName = (files[docIndex]).getCanonicalPath();
                DocumentVector.add(docVector(fileName, docIndex, inputWordList, vocabulary, t_matrix));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return DocumentVector;
    }

    public List<Double> docVector(String fileName, int docIndex, List<String> inputWordList, ArrayList<String> vocabulary, double[][] t_matrix) {

        BufferedReader br = null;
        List<Double> docVector = new ArrayList<Double>();

        try {
            br = new BufferedReader(new FileReader(fileName));

            StringBuilder sb = new StringBuilder();
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                sb.append(sCurrentLine);
                sb.append(" ");
            }

            String paragraph = sb.toString();
            for (String word : inputWordList) {
                if (paragraph.contains(word)) {
                    int index = vocabulary.indexOf(word);
                    docVector.add(t_matrix[index][docIndex]);
                } else {
                    docVector.add(0.0);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return docVector;
    }

    public static void main(String[] args) {

        Calculation cal = new Calculation();
        List<String> p_words_list = cal.getPWordList();
        ArrayList<String> vocabulary = cal.getVocabulary();
        double[][] t_matrix = cal.getTMatrix();

        List<List<Double>> document_vector = cal.CalDocumentVector(p_words_list, vocabulary, t_matrix);

        document_vector = cal.normalize_vectors(document_vector);
        cal.serialize_document_vector(document_vector);


    }

    private List<List<Double>> normalize_vectors(List<List<Double>> document_vector) {

        for (int i = 0; i < document_vector.size(); i++) {
            Double sum = 0.0;
            for (int j = 0; j < document_vector.get(i).size(); j++) {
                sum += document_vector.get(i).get(j);
            }
            for (int j = 0; j < document_vector.get(i).size(); j++) {
                if (!sum.equals(0.0)) {
                    document_vector.get(i).set(j, document_vector.get(i).get(j) / sum);
                }
            }
        }
        return document_vector;
    }

    private void serialize_document_vector(List<List<Double>> document_vector) {
        try {
            FileOutputStream fileOut =
                    new FileOutputStream("./LawIE/DocVector/Serialized_folder/document_vector.ser");

            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(document_vector);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in Serialized_folder/document_vector.ser");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getPWordList() {
        List<String> p_words_list = null;
        try {
            File file = new File("./LawIE/DocVector/Serialized_folder/p_list.ser");
            if (file.exists()) {
                System.out.println("P_LIST serialized file found. Reading from it");
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                p_words_list = (List<String>) in.readObject();
                in.close();
                fileIn.close();
            } else {
                System.out.println("P_LIST word serlized file not found");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return p_words_list;
    }

    private ArrayList<String> getVocabulary() {
        ArrayList<String> vocabulary = null;
        try {
            File file = new File("./LawIE/DocVector/Serialized_folder/vocabulary.ser");
            if (file.exists()) {
                System.out.println("Vocabulary serialized file found. Reading from it");
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                vocabulary = (ArrayList<String>) in.readObject();
                in.close();
                fileIn.close();
            } else {
                System.out.println("Vocabulary serialized file not found");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return vocabulary;
    }

    private double[][] getTMatrix() {
        double[][] t_matrix = null;
        try {
            File file = new File("./LawIE/DocVector/Serialized_folder/t_matrix.ser");
            if (file.exists()) {
                System.out.println("t_matrix serialized file found. Reading from it");
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                t_matrix = (double[][]) in.readObject();
                in.close();
                fileIn.close();
            } else {
                System.out.println("t_matrix serialized file not found");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return t_matrix;
    }
}
