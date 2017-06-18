package org.titans.fyp;

import java.io.*;
import java.util.*;

/**
 * Created by Keetmalin on 6/13/2017
 * Project - Tf-idf calculator with Pipeline
 */
public class Tf_idf_Calculator {

    private double[][] t_matrix;
    private ArrayList<String> vocabulary = new ArrayList<String>();
    private String[] document_array;
    StanfordLemmatizer slem = new StanfordLemmatizer();

    private Tf_idf_Calculator(int n) {

        document_array = new String[n];
    }

    private String file_reader(String file_name) {
        String document = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file_name));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line;
        try {
            assert reader != null;
            while ((line = reader.readLine()) != null) {
                document = document.concat(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;

    }

    void add_to_vocabulary(String document, int doc_number) {

        for (String word : document.split(" ")) {
            if (!vocabulary.contains(word)) {
                vocabulary.add(word);
            }
        }

    }

    String convert_to_lowercase(String document) {
        return document.toLowerCase();
    }

    private void nlp_pipeline(Tf_idf_Calculator tf, int n) {

        File folder = new File("./LawIE/DocVector/Cases");

        File[] fileList = folder.listFiles();
        String[] fileNames = new String[n];
        for (int i = 0; i < fileList.length; i++) {
            fileNames[i] = fileList[i].toString().split("/")[1].split(".txt")[0];
        }


        Arrays.sort(fileNames);

        for (int i = 0; i < n; i++) {


            // open file
            String document = tf.file_reader("./LawIE/DocVector/Cases/" + fileNames[i] + ".txt");

            //convert to lowercase
            //String lowercase_document = tf.convert_to_lowercase(document);

            //tokenizing and lemmatizing

            List<String> lemmatized_text = slem.lemmatize(document);
            String text = String.join(" ", lemmatized_text);

            document_array[i] = text;
            tf.add_to_vocabulary(text, i);

            System.out.println("Document : " + i + " - NLP Pipeline Done");

        }

    }

    private void initialize_t_matrix() {
        t_matrix = new double[vocabulary.size()][document_array.length];
    }

    private void fill_t_matrix(int n) {
        int term_count = vocabulary.size();

        for (int i = 0; i < term_count; i++) {
            String temp = vocabulary.get(i);
            for (int j = 0; j < n; j++) {
                t_matrix[i][j] = calculate_tfidf(temp, document_array[j], document_array);
            }
            System.out.println("term " + (i + 1) + " / " + term_count + " - done");
        }

    }

    private double calc_tf(String term, String document) {
        double count = 0;
        double word_count = 0;
        for (String word : document.split(" ")) {
            if (term.equals(word)) {
                count = count + 1;
            }
            word_count = word_count + 1;
        }
        return count / word_count;
    }

    private double calc_idf(String term, String[] corpus) {
        double doc_count = corpus.length;
        double count = 0.0;

        for (String document : corpus) {
            for (String word : document.split(" ")) {
                if (term.equals(word)) {
                    count = count + 1;
                    break;
                }
            }
        }

        return Math.log(doc_count / count);
    }

    private double calculate_tfidf(String term, String document, String[] corpus) {
        double tf = calc_tf(term, document);
        double idf = calc_idf(term, corpus);
        return tf * idf;
    }

    private void serialize_t_matrix() {
        try {
            FileOutputStream fileOut =
                    new FileOutputStream("./LawIE/DocVector/Serialized_folder/t_matrix.ser");

            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(t_matrix);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in Serialized_folder/t_matrix.ser");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double calc_gtf(String term, String[] corpus) {
        double sum = 0.0;
        double n = document_array.length;
        for (int i = 0; i < n; i++) {
            sum += calc_tf(term, corpus[i]);
        }
        return sum / n;
    }

    private double calculate_gtfid(String term, String[] corpus) {
        double gtf = calc_gtf(term, corpus);
        double idf = calc_idf(term, corpus);

        return gtf * idf;
    }

    private List<String> get_top_p_words(int p) {

        HashMap<String, Double> map = new HashMap<String, Double>();
        ValueComparator bvc = new ValueComparator(map);
        TreeMap<String, Double> sorted_map = new TreeMap<String, Double>(bvc);
        String[] p_words = new String[p];

        for (int i = 0; i < vocabulary.size(); i++) {
            map.put(vocabulary.get(i), calculate_gtfid(vocabulary.get(i), document_array));
        }

        sorted_map.putAll(map);

        int count = 0;
        for (Map.Entry<String, Double> entry : sorted_map.entrySet()) {
            if (count >= p) break;

            p_words[count] = entry.getKey();
            count++;
        }
        List<String> p_word_list = Arrays.asList(p_words);
        return p_word_list;

    }

    private void serialize_p_list(List<String> p_list) {
        try {
            FileOutputStream fileOut =
                    new FileOutputStream("./LawIE/DocVector/Serialized_folder/p_list.ser");

            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(p_list);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in Serialized_folder/p_list.ser");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void serialize_vocabulary() {
        try {
            FileOutputStream fileOut =
                    new FileOutputStream("./LawIE/DocVector/Serialized_folder/vocabulary.ser");

            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(vocabulary);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in Serialized_folder/vocabulary.ser");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        //define document count
        int n = 54935;

        Tf_idf_Calculator tf = new Tf_idf_Calculator(n);
        tf.nlp_pipeline(tf, n);
        System.out.println("NLP Pipeline Done");
        tf.initialize_t_matrix();
        tf.fill_t_matrix(n);
        System.out.println("t_matrix creation done");

        //serialize t_matrix
        tf.serialize_t_matrix();

        // calculate top p words
        int p = 600;
        List<String> p_words_list = tf.get_top_p_words(p);
        System.out.println("Filtered p top words");

        tf.serialize_p_list(p_words_list);
        tf.serialize_vocabulary();


    }

    class ValueComparator implements Comparator<String> {
        Map<String, Double> base;

        public ValueComparator(Map<String, Double> base) {
            this.base = base;
        }

        // Note: this comparator imposes orderings that are inconsistent with
        // equals.
        public int compare(String a, String b) {
            if (base.get(a) >= base.get(b)) {
                return -1;
            } else {
                return 1;
            } // returning 0 would merge keys
        }
    }
}
