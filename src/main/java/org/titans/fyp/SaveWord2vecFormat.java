package org.titans.fyp;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Buddhi on 9/8/2017.
 */
public class SaveWord2vecFormat {

    public static void main(String[] args) {


        List<List<Double>> docVector = loadSerilized_file();
//    System.out.println("Size:"+docVector.size());

        File folder = new File("D:\\Project\\fyp\\word2vec\\code\\work8\\up2\\Output");
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

        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter("D:\\Project\\fyp\\word2vec\\code\\work8\\up2\\doc_vector_representation_LegalIR\\Serialized_folder\\word2vec.txt");
            bw = new BufferedWriter(fw);
            String tem;

            tem = docVector.size() + " " + docVector.get(0).size() + "\n";
            System.out.print(tem);
            bw.write(tem);
            for (int index = 0; index < fileNames.length; index++) {
                List<Double> vector = docVector.get(index);
                String strVec = "";
                for (double val : vector) {
                    strVec += val + " ";
                }

                tem = fileNames[index] + " " + strVec.substring(0, strVec.length() - 1) + "\n";
//        System.out.println(tem);
                bw.write(tem);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("Text file saved.");

    }

    private static List<List<Double>> loadSerilized_file() {


        List<List<Double>> docVector = null;
        try {
            File file = new File("D:\\Project\\fyp\\word2vec\\code\\work8\\up2\\doc_vector_representation_LegalIR\\Serialized_folder\\_new_document_vector.ser");
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
