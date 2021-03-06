package org.titans.fyp;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Keetmalin on 6/13/2017
 * Project - doc_vector_representation_LegalIR
 */
public class Calculation {

    public static String output_folder = "./LawIE/DocVector/Output";
    StanfordLemmatizer slem = new StanfordLemmatizer();


    public List<List<Double>> CalDocumentVector(List<String> inputWordList, HashSet<String> vocabulary, double[][] t_matrix) {

        File folder = new File(output_folder);
        File[] fileList = folder.listFiles();
        int n = fileList.length;
        String[] fileNames = new String[n];

        for (int i = 0; i < fileList.length; i++) {
            String full_name = fileList[i].toString();
            int start_index = full_name.lastIndexOf(File.separator);
            int end_index = full_name.lastIndexOf('.');
            fileNames[i] = full_name.substring(start_index + 1, end_index);
//            System.out.println(fileNames[i]);
        }
        Arrays.sort(fileNames);

        List<List<Double>> DocumentVector = new ArrayList<List<Double>>();

        for (int docIndex = 0; docIndex < fileNames.length; docIndex++) {
            String fileName = output_folder + File.separator + fileNames[docIndex] + ".txt";
                DocumentVector.add(docVector(fileName, docIndex, inputWordList, vocabulary, t_matrix));
        }

        return DocumentVector;
    }

    public static int getIndex(HashSet<String> set, String value) {
        int result = 0;
        for (String entry : set) {
            if (entry.equals(value)) {
                return result;
            }
            result++;
        }
        return result;
    }

    public List<Double> docVector(String fileName, int docIndex, List<String> inputWordList, HashSet<String> vocabulary, double[][] t_matrix) {

        BufferedReader br;
        List<Double> docVector = new ArrayList<Double>();

        try {
            br = new BufferedReader(new FileReader(fileName));

            StringBuilder sb = new StringBuilder();
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                sb.append(sCurrentLine);
                sb.append(" \n");
            }

            String paragraph = sb.toString();
            List<String> lemmatized_text = slem.lemmatize(paragraph);
            String lemmatized_paragraph = String.join(" ", lemmatized_text);

            for (String word : inputWordList) {
                if (lemmatized_paragraph.contains(word)) {
                    int index = getIndex(vocabulary, word);
                    try {
                        docVector.add(t_matrix[index][docIndex]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

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

    public List<List<Double>> normalize_vectors(List<List<Double>> document_vector) {

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

    public void serialize_document_vector(List<List<Double>> document_vector) {
        try {
            FileOutputStream fileOut =
                    new FileOutputStream(Tf_idf_Calculator.serialized_folder + File.separator + "document_vector.ser");

            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(document_vector);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in " + Tf_idf_Calculator.serialized_folder + File.separator + "document_vector.ser");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getPWordList() {
        List<String> p_words_list = null;
        try {
            File file = new File(Tf_idf_Calculator.serialized_folder + File.separator + "p_list.ser");
            if (file.exists()) {
                System.out.println("P_LIST serialized file found. Reading from it");
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                p_words_list = (List<String>) in.readObject();
                in.close();
                fileIn.close();
            } else {
                System.out.println("P_LIST word serlized file not found in" + Tf_idf_Calculator.serialized_folder + File.separator + "p_list.ser");
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

    private HashSet<String> getVocabulary() {
        HashSet<String> vocabulary = null;
        try {
            File file = new File(Tf_idf_Calculator.serialized_folder + File.separator + "vocabulary.ser");
            if (file.exists()) {
                System.out.println("Vocabulary serialized file found. Reading from it");
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                vocabulary = (HashSet<String>) in.readObject();
                in.close();
                fileIn.close();
            } else {
                System.out.println("Vocabulary serialized file not found in " + Tf_idf_Calculator.serialized_folder + File.separator + "vocabulary.ser");
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
            File file = new File(Tf_idf_Calculator.serialized_folder + File.separator + "t_matrix.ser");
            if (file.exists()) {
                System.out.println("t_matrix serialized file found. Reading from it");
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                t_matrix = (double[][]) in.readObject();
                in.close();
                fileIn.close();
            } else {
                System.out.println("t_matrix serialized file not found in " + Tf_idf_Calculator.serialized_folder + File.separator + "t_matrix.ser");
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

    public static void main(String[] args) {

        if (args.length == 2) {
            Tf_idf_Calculator.serialized_folder = args[0];
            output_folder = args[1];
            System.out.println("Serialized Folder = " + Tf_idf_Calculator.serialized_folder);
            System.out.println("Output Folder = " + output_folder);
        }

        if (args.length == 3) {
            Tf_idf_Calculator.cases_folder_path = args[0];
            Tf_idf_Calculator.serialized_folder = args[1];
            output_folder = args[2];
            System.out.println("Cases Folder = " + Tf_idf_Calculator.cases_folder_path);
            System.out.println("Serialized Folder = " + Tf_idf_Calculator.serialized_folder);
            System.out.println("Output Folder = " + output_folder);
        }

        Calculation cal = new Calculation();
        List<String> p_words_list = cal.getPWordList();
        HashSet<String> vocabulary = cal.getVocabulary();
        double[][] t_matrix = cal.getTMatrix();

        List<List<Double>> document_vector = cal.CalDocumentVector(p_words_list, vocabulary, t_matrix);

        document_vector = cal.normalize_vectors(document_vector);
        cal.serialize_document_vector(document_vector);


    }

}
