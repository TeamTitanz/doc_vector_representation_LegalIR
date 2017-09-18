package org.titans.fyp;

import java.io.*;
import java.util.*;

/**
 * Created by Buddhi on 9/18/2017.
 */
public class Tf_Idf_Calculator_Part2 {

    private HashSet<String> vocabulary = new HashSet<String>();
    private HashMap<String, Double> vocabularyBase = new HashMap<String, Double>(); //First used as a counter then used to keep idf
    private HashMap<String, Double> gtfList = new HashMap<String, Double>();
    private String[][] document_array;
    public static String serialized_folder = "./LawIE/DocVector/Serialized_folder";

    private double calculate_gtfid(String term) {
        double gtf = gtfList.get(term);
        double idf = vocabularyBase.get(term);
//        System.out.print(gtf + "," + idf);
        return gtf * idf;
    }

    private List<String> get_top_p_words(int p) {

        WordValue sorted_map[] = new WordValue[vocabulary.size()];
        Iterator<String> itr = vocabulary.iterator();

        int index = 0;
//        System.out.println("\n\n\nWords unsorted.............................................");
        while (itr.hasNext()) {
            String word = itr.next();

//            System.out.print(word + ",");
            double temp = calculate_gtfid(word);
//            System.out.print("," + temp + "\n");

            sorted_map[index++] = new WordValue(word, temp);
        }
//        System.out.println("Words unsorted...............................................\n\n\n");

        Arrays.sort(sorted_map, new ValueComparator());

//        System.out.println("\n\n\nSize: " + sorted_map.length);
//        System.out.println("Words Start.............................................");
//        for (int count = 0; count < sorted_map.length; count++) {
//            System.out.println(sorted_map[count].getWord() + " :- " + sorted_map[count].getValue());
//        }
//        System.out.println("Words End...............................................\n\n\n");

        String[] p_words = new String[p];
//        System.out.println("Size: " + p_words.length);
//        System.out.println("P_Words Start.............................................");
        for (int count = 0; count < p; count++) {
            String term = sorted_map[count].getWord();
            p_words[count] = term;
            int no_doc_count = 0;
            for (String[] document : document_array) {
                for (String word : document) {
                    if (term.equals(word)) {
                        no_doc_count += 1;
                        break;
                    }
                }
            }

//            System.out.println(term + "," + sorted_map[count].getValue() + "," + no_doc_count);
        }
//        System.out.println("\nP_Words End...............................................");

        return Arrays.asList(p_words);

    }

    class WordValue {
        private String word;
        private double value;

        public WordValue(String word, double value) {
            this.word = word;
            this.value = value;
        }

        public String getWord() {
            return word;
        }

        public double getValue() {
            return value;
        }
    }

    class ValueComparator implements Comparator<WordValue> {

        @Override
        public int compare(WordValue o1, WordValue o2) {
            // descending order
            double tem = o2.getValue() - o1.getValue();
            if (tem < 0) {
                return -1;
            } else if (tem == 0.0) {
                return 0;
            } else {
                return 1;
            }
        }

    }

    private void serialize_p_list(List<String> p_list) {
        try {
            FileOutputStream fileOut =
                    new FileOutputStream(serialized_folder + File.separator + "p_list.ser");

            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(p_list);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in " + serialized_folder + File.separator + "p_list.ser");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HashSet<String> getVocabulary() {
        HashSet<String> vocabulary = null;
        try {
            File file = new File(serialized_folder + File.separator + "vocabulary.ser");
            if (file.exists()) {
                System.out.println("Vocabulary serialized file found. Reading from it");
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                vocabulary = (HashSet<String>) in.readObject();
                in.close();
                fileIn.close();
            } else {
                System.out.println("Vocabulary serialized file not found in " + serialized_folder + File.separator + "vocabulary.ser");
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

    private HashMap<String, Double> getVocabularyBase() {
        HashMap<String, Double> vocabularyBase = null;
        try {
            File file = new File(serialized_folder + File.separator + "vocabularyBase.ser");
            if (file.exists()) {
                System.out.println("vocabularyBase serialized file found. Reading from it");
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                vocabularyBase = (HashMap<String, Double>) in.readObject();
                in.close();
                fileIn.close();
            } else {
                System.out.println("VocabularyBase serialized file not found in " + serialized_folder + File.separator + "vocabularyBase.ser");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return vocabularyBase;
    }

    private HashMap<String, Double> getGtfList() {
        HashMap<String, Double> gtfList = null;
        try {
            File file = new File(serialized_folder + File.separator + "gtf_list.ser");
            if (file.exists()) {
                System.out.println("GtfList serialized file found. Reading from it");
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                gtfList = (HashMap<String, Double>) in.readObject();
                in.close();
                fileIn.close();
            } else {
                System.out.println("GtfList serialized file not found in " + serialized_folder + File.separator + "gtf_list.ser");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return gtfList;
    }

    private String[][] getDocumentArray() {
        String[][] document_array = null;
        try {
            File file = new File(serialized_folder + File.separator + "document_array.ser");
            if (file.exists()) {
                System.out.println("document_array serialized file found. Reading from it");
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                document_array = (String[][]) in.readObject();
                in.close();
                fileIn.close();
            } else {
                System.out.println("document_array serialized file not found in " + serialized_folder + File.separator + "document_array.ser");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return document_array;
    }


    public static void main(String[] args) {

        int p = 600;

        if (args.length == 1) {
            serialized_folder = args[0];
            System.out.println("Serialized Folder = " + serialized_folder);
        }
        if (args.length == 2) {
            serialized_folder = args[0];
            p = Integer.parseInt(args[1]);
            System.out.println("Serialized Folder = " + serialized_folder);
            System.out.println("P value = " + p);
        }

        Tf_Idf_Calculator_Part2 tf = new Tf_Idf_Calculator_Part2();
        tf.vocabulary = tf.getVocabulary();
        tf.vocabularyBase = tf.getVocabularyBase();
        tf.gtfList = tf.getGtfList();
        tf.document_array = tf.getDocumentArray();

        // calculate top p words
        List<String> p_words_list = tf.get_top_p_words(p);
        System.out.println("Filtered p top words");

        tf.serialize_p_list(p_words_list);

    }

}
