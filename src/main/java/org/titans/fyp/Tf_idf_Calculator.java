package org.titans.fyp;

import java.io.*;
import java.util.*;

/**
 * Created by Keetmalin on 6/13/2017
 * Project - Tf-idf calculator with Pipeline
 */
public class Tf_idf_Calculator {

    private double[][] t_matrix;
    private HashSet<String> vocabulary = new HashSet<String>();
    private String[] document_array;
    StanfordLemmatizer slem = new StanfordLemmatizer();
    public static String cases_folder_path = "./LawIE/DocVector/Cases";
    public static String serialized_folder = "./LawIE/DocVector/Serialized_folder";

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
                line = line.replaceAll("[^\\x00-\\x7F]", "");
                document = document.concat(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;

    }

    void add_to_vocabulary(String document, int doc_number) {
        document=document.replace("."," ");
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

        File folder = new File(cases_folder_path);

        File[] fileList = folder.listFiles();
        String[] fileNames = new String[n];
        for (int i = 0; i < fileList.length; i++) {
            String full_name = fileList[i].toString();
            int start_index = full_name.lastIndexOf(File.separator);
            int end_index = full_name.lastIndexOf('.');
            fileNames[i] = full_name.substring(start_index + 1, end_index);
            System.out.println(fileNames[i]);
        }


        Arrays.sort(fileNames);

        for (int i = 0; i < n; i++) {


            // open file
            String document = tf.file_reader(cases_folder_path + File.separator + fileNames[i] + ".txt");

            //convert to lowercase
            //String lowercase_document = tf.convert_to_lowercase(document);

            //tokenizing and lemmatizing

            List<String> lemmatized_text = slem.lemmatize(document);
            String text = String.join(" ", lemmatized_text);

            document_array[i] = text;
            tf.add_to_vocabulary(text, i);

            System.out.println("Document : " + i + " - NLP Pipeline Done");
            System.out.println("Vocabulary size : "+vocabulary.size());
            /*if(vocabulary.size()>200000){
                    printVocab();
                    System.exit(0);
            }*/


        }

    }

    private void printVocab(){
        try{
            FileWriter fw = new FileWriter("vocab.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter writer = new PrintWriter(bw);
            Iterator<String> words=vocabulary.iterator();


            while(words.hasNext()){

                writer.println(words.next());

            }
            writer.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }



    }

    private void initialize_t_matrix() {
        System.out.println(vocabulary.size()+" X "+document_array.length);
        t_matrix = new double[vocabulary.size()][document_array.length];
    }

    private void fill_t_matrix(int n) {
        int term_count = vocabulary.size();
        Iterator<String> itr=vocabulary.iterator();
        String temp =null;
        int i=0;
        while(itr.hasNext()){
            temp = itr.next();
            for (int j = 0; j < n; j++) {
                t_matrix[i][j] = calculate_tfidf(temp, document_array[j], document_array);
            }
            System.out.println("term " + (i + 1) + " / " + term_count + " - done");
            i++;
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
                    new FileOutputStream(serialized_folder + File.separator + "t_matrix.ser");

            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(t_matrix);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in " + serialized_folder + File.separator + "t_matrix.ser");


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

        Iterator<String> itr=vocabulary.iterator();
        String word=null;
        while(itr.hasNext()){
            word=itr.next();
            map.put(word, calculate_gtfid(word, document_array));
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

    private void serialize_vocabulary() {
        try {
            FileOutputStream fileOut =
                    new FileOutputStream(serialized_folder + File.separator + "vocabulary.ser");

            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(vocabulary);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in " + serialized_folder + File.separator + "vocabulary.ser");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        if (args.length == 2) {
            cases_folder_path = args[0];
            serialized_folder = args[1];
            System.out.println("Cases Folder = " + cases_folder_path);
            System.out.println("Serialized Folder = " + serialized_folder);
        }
        if (args.length == 3) {
            cases_folder_path = args[0];
            serialized_folder = args[1];
            Calculation.output_folder = args[2];
            System.out.println("Cases Folder = " + cases_folder_path);
            System.out.println("Serialized Folder = " + serialized_folder);
            System.out.println("Output Folder = " + Calculation.output_folder);
        }

        //define document count
        int n = new File(cases_folder_path).listFiles().length;
        System.out.println("Number of file in path " + cases_folder_path + " = " + n);

        Tf_idf_Calculator tf = new Tf_idf_Calculator(n);
        tf.nlp_pipeline(tf, n);
        System.out.println("NLP Pipeline Done");
        tf.printVocab();
        tf.initialize_t_matrix();
        System.out.println("t_matrix creation done");
        tf.fill_t_matrix(n);
        System.out.println("t_matrix filling done");

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
