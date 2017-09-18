package org.titans.fyp;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Buddhi on 9/18/2017.
 */
public class Tf_Idf_Calculator_Part1 {

    private double[][] t_matrix;
    private HashSet<String> vocabulary = new HashSet<String>();
    private HashMap<String, Double> vocabularyBase = new HashMap<String, Double>(); //First used as a counter then used to keep idf
    private HashMap<String, Double> gtfList = new HashMap<String, Double>();
    private String[][] document_array;
    StanfordLemmatizer slem = new StanfordLemmatizer();
    public static String cases_folder_path = "./LawIE/DocVector/Cases";
    public static String serialized_folder = "./LawIE/DocVector/Serialized_folder";

    private Tf_Idf_Calculator_Part1(int n) {
        document_array = new String[n][];
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
                document = document.concat(line + " \n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;

    }

    private String[] breakDocument(String document) {
        document = document.replace(".", " ");
        return document.split(" ");
    }

    void add_to_vocabulary(String document, int doc_number) {
        document = document.replace(".", " ");
        Double val = null;
        for (String word : document.split(" ")) {
            if (word.length() > 1) { //no point in empty string or the alphabet
                if (!vocabularyBase.keySet().contains(word)) {
                    vocabularyBase.put(word, 1.0);
                } else {
                    val = vocabularyBase.get(word);
                    if (val == null) {
                        val = 0.0;
                    }
                    val++;
                    vocabularyBase.remove(word);
                    vocabularyBase.put(word, val);
                }
            }
        }

    }

    private void nlp_pipeline(Tf_Idf_Calculator_Part1 tf, int n) {

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

            document_array[i] = breakDocument(text);
            tf.add_to_vocabulary(text, i);

            System.out.println("Document : " + i + " - NLP Pipeline Done");

            /*if(vocabulary.size()>200000){
                    printVocab();
                    System.exit(0);
            }*/


        }

        System.out.println("Vocabulary size : " + vocabularyBase.size());
        simplifyVocabulary();
        System.out.println("Vocabulary size : " + vocabulary.size());

    }

    private void simplifyVocabulary() {

        double n = vocabularyBase.size();
        double mean = 0;
        Iterator<String> itr = vocabularyBase.keySet().iterator();
        String word = null;
        Double val = null;
        while (itr.hasNext()) {
            word = itr.next();
            val = vocabularyBase.get(word);
            if (val != null) {
                mean += val;
            }
        }
        mean = mean / n;

        double sumOfDev = 0;
        itr = vocabularyBase.keySet().iterator();
        while (itr.hasNext()) {
            word = itr.next();
            val = vocabularyBase.get(word);
            if (val != null) {
                // System.out.println(mean+" "+val);
                // System.out.println(mean-val);
                sumOfDev += Math.pow((mean - val), 2);
            }
        }
        double variance = sumOfDev / n;
        System.out.println("variance: " + variance);

        double stDev = Math.sqrt(variance);
        System.out.println("stDev: " + stDev);


        double upperThresh = 0;
        double lowerThresh = 0;
        double mult = 2;   //68–95–99.7 rule


        upperThresh = mean + (mult * stDev);
        lowerThresh = mean - (mult * stDev);

        //   do {
        //        upperThresh = mean + (mult * stDev);
        //       lowerThresh = mean - (mult * stDev);
        //        mult /= 2;

        //    }while(lowerThresh<0);

        //Compansate for the skewedness
        //     lowerThresh/=2;
        //     mult=(mean-lowerThresh)/stDev;
        //   upperThresh = mean + (mult * stDev);


        if (lowerThresh < 0) {
            mult = ((mean - 10) / stDev); //get the lower threashold to 10
            upperThresh = mean + (mult * stDev);
            lowerThresh = mean - (mult * stDev);
        }


        itr = vocabularyBase.keySet().iterator();
        while (itr.hasNext()) {
            word = itr.next();
            val = vocabularyBase.get(word);
            if (val != null) {
                if (val <= upperThresh && val >= lowerThresh) {
                    vocabulary.add(word);
                }
            }
        }
        System.out.println("lowerThresh: " + lowerThresh);
        System.out.println("Mean: " + mean);
        System.out.println("upperThresh: " + upperThresh);
    }

    private void printVocab() {
        try {
            FileWriter fw = new FileWriter("vocab.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter writer = new PrintWriter(bw);
            Iterator<String> words = vocabulary.iterator();


            while (words.hasNext()) {

                writer.println(words.next());

            }
            writer.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }


    }

    private void initialize_t_matrix() {
        System.out.println(vocabulary.size() + " X " + document_array.length);
        t_matrix = new double[vocabulary.size()][document_array.length];
    }

    private void fill_t_matrix(int n) {

        Iterator<String> itr = vocabulary.iterator();
        String temp;
        int i = 0;
        vocabularyBase = new HashMap<String, Double>();

        // Empty out
        while (itr.hasNext()) {
            temp = itr.next();
            vocabularyBase.put(temp, calc_idf(temp, document_array));  //put idf to be used for gtfidf
            i++;
        }
        scaling_idf();

        int term_count = vocabulary.size();
        itr = vocabulary.iterator();
        i = 0;
        double tf;
        double gtf;
        while (itr.hasNext()) {

            gtf = 0.0;
            temp = itr.next();
            for (int j = 0; j < n; j++) {
                tf = calc_tf(temp, document_array[j]);
                gtf += tf;
                t_matrix[i][j] = tf * vocabularyBase.get(temp);
            }
            gtfList.put(temp, gtf / n);  //put gtf to be used for gtfidf

            System.out.println("term " + (i + 1) + " / " + term_count + " - done");
            i++;
        }
        scaling_gtf();

    }

    private void scaling_gtf() {

        double gtfList_min = 1;
        double gtfList_max = 0;
        for (Map.Entry<String, Double> entry : gtfList.entrySet()) {
            double value = entry.getValue();
            if (gtfList_min > value) {
                gtfList_min = value;
            }

            if (gtfList_max < value) {
                gtfList_max = value;
            }
        }
        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& gtfList min & max" + gtfList_min + "\t" + gtfList_max);
//        Remove the ones X s.t : X >mean+sigma || X <mean - sigma


        double sum1 = 0.0;    // sum of the numbers
        double sum2 = 0.0;    // sum of the squares
        HashMap<String, Double> new_gtfList = new HashMap<String, Double>();
        for (Map.Entry<String, Double> entry : gtfList.entrySet()) {
            double current_value = entry.getValue();
            double scaled_value = (current_value - gtfList_min) / (gtfList_max - gtfList_min);
            new_gtfList.put(entry.getKey(), scaled_value);

            sum1 += scaled_value;
            sum2 += scaled_value * scaled_value;
        }

        gtfList.clear();
        double count = new_gtfList.size();
        double mean = sum1 / count;
        double variance = (count * sum2 - sum1 * sum1) / (count * count);
        double sigma = Math.sqrt(variance);

        System.out.println("GTF -> (mean)" + mean);
        System.out.println("GTF -> (sigma)" + sigma);
        System.out.println("GTF -> (mean + sigma)" + (mean + sigma));
        System.out.println("GTF -> (mean - sigma)" + (mean - sigma));

        for (Map.Entry<String, Double> entry : new_gtfList.entrySet()) {
            double current_value = entry.getValue();

            if (current_value > (mean + sigma)) {
                current_value = 0;
            }
            if (current_value < (mean - sigma)) {
                current_value = 0;
            }
            gtfList.put(entry.getKey(), current_value);
        }
        new_gtfList.clear();

    }

    private void scaling_idf() {

        double vocabularyBase_min = 1;
        double vocabularyBase_max = 0;
        for (Map.Entry<String, Double> entry : vocabularyBase.entrySet()) {
            double value = entry.getValue();
            if (vocabularyBase_min > value) {
                vocabularyBase_min = value;
            }

            if (vocabularyBase_max < value) {
                vocabularyBase_max = value;
            }
        }
        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& vocabularyBase min & max\t" + vocabularyBase_min + "\t" + vocabularyBase_max);

        double sum1 = 0.0;    // sum of the numbers
        double sum2 = 0.0;    // sum of the squares
        HashMap<String, Double> new_vocabularyBase = new HashMap<String, Double>();
        for (Map.Entry<String, Double> entry : vocabularyBase.entrySet()) {
            double current_value = entry.getValue();
            double scaled_value = (current_value - vocabularyBase_min) / (vocabularyBase_max - vocabularyBase_min);
            new_vocabularyBase.put(entry.getKey(), scaled_value);

            sum1 += scaled_value;
            sum2 += scaled_value * scaled_value;
        }

        vocabularyBase.clear();
        double count = new_vocabularyBase.size();
        double mean = sum1 / count;
        double variance = (count * sum2 - sum1 * sum1) / (count * count);
        double sigma = Math.sqrt(variance);

        System.out.println("IDF -> (mean)" + mean);
        System.out.println("IDF -> (sigma)" + sigma);
        System.out.println("IDF -> (mean + sigma)" + (mean + sigma));
        System.out.println("IDF -> (mean - sigma)" + (mean - sigma));

        for (Map.Entry<String, Double> entry : new_vocabularyBase.entrySet()) {
            double current_value = entry.getValue();

            if (current_value > (mean + sigma)) {
                current_value = 0;
            }
            if (current_value < (mean - sigma)) {
                current_value = 0;
            }
            vocabularyBase.put(entry.getKey(), current_value);
        }
        new_vocabularyBase.clear();
    }

    private double calc_tf(String term, String[] words) {

        double count = 0;
        for (String word : words) {
            if (term.equals(word)) {
                count = count + 1;
            }
        }

        double most_occurring_term_value = Stream.of(words)
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue)).get().getValue();

        return 0.5 + (0.5 * (count / most_occurring_term_value));
    }

    private double calc_idf(String term, String[][] corpus) {
        return Math.log10(corpus.length / doc_count_for_term(term, corpus));
    }

    private double doc_count_for_term(String term, String[][] corpus) {
        double no_doc_count = 0.0;
        for (String[] document : corpus) {
            for (String word : document) {
                if (term.equals(word)) {
                    no_doc_count += 1;
                    break;
                }
            }
        }
        return no_doc_count;
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

    private void serialize_document_array() {
        try {
            FileOutputStream fileOut =
                    new FileOutputStream(serialized_folder + File.separator + "document_array.ser");

            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(document_array);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in " + serialized_folder + File.separator + "document_array.ser");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void serialize_gtf_list() {
        try {
            FileOutputStream fileOut =
                    new FileOutputStream(serialized_folder + File.separator + "gtf_list.ser");

            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(gtfList);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in " + serialized_folder + File.separator + "gtf_list.ser");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void serialize_vocabularyBase() {
        try {
            FileOutputStream fileOut =
                    new FileOutputStream(serialized_folder + File.separator + "vocabularyBase.ser");

            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(vocabularyBase);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in " + serialized_folder + File.separator + "vocabularyBase.ser");

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

        //define document count
        int n = new File(cases_folder_path).listFiles().length;
        System.out.println("Number of file in path " + cases_folder_path + " = " + n);

        Tf_Idf_Calculator_Part1 tf = new Tf_Idf_Calculator_Part1(n);
        tf.nlp_pipeline(tf, n);
        System.out.println("NLP Pipeline Done");
        tf.printVocab();
        tf.initialize_t_matrix();
        System.out.println("t_matrix creation done");
        tf.fill_t_matrix(n);
        System.out.println("t_matrix filling done");

        //serialize t_matrix
        tf.serialize_t_matrix();
        //serialize vocabulary
        tf.serialize_vocabulary();
        //serialize document_array
        tf.serialize_document_array();
        //serialize gtf_list
        tf.serialize_gtf_list();
        //serialize vocabularyBase
        tf.serialize_vocabularyBase();

    }

}
