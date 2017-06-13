import java.io.*;
import java.util.*;

/**
 * Created by Keetmalin on 6/13/2017
 * Project - Tf-idf calculator with Pipeline
 */
public class Tf_idf_Calculator {

    private double[][] t_matrix;
    private ArrayList<String> vocabulary = new ArrayList<>();
    private String[] document_array;

    private Tf_idf_Calculator(int n) {

        document_array = new String[n];
    }

    private String file_reader(String file_name) {
        String document = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("src/" + file_name));
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

        for (int i = 0; i < n; i++) {

            // open file
            String document = tf.file_reader("Cases/case" + (n + 1));

            //convert to lowercase
            String lowercase_document = tf.convert_to_lowercase(document);

            //tokanizing and lemmatizing
//            StanfordLemmatizer slem = new StanfordLemmatizer();
//            String lemmatized_text = slem.lemmatize(lowercase_document);

//            document_array[i] = lemmatized_text;
//            tf.add_to_vocabulary(lemmatized_text);

        }


//
//        System.out.println(document);
//        System.out.println(lowercase_document);
//        System.out.println(slem.lemmatize(lowercase_document));


    }

    private void initialize_t_matrix() {
        t_matrix = new double[vocabulary.size()][document_array.length];
    }

    private void fill_t_matrix(int n) {
        int term_count = vocabulary.size();

        for (int i = 0; i < vocabulary.size(); i++) {
            for (int j = 0; j < n; j++) {
                t_matrix[i][j] = calculate_tfidf(vocabulary.get(i), document_array[j], document_array);
            }
            System.out.println("term " + i+1 + " / " + term_count + " - done" );
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

    private void serialize_t_matrix(){
        try {
            FileOutputStream fileOut =
                    new FileOutputStream("Serialized_folder/t_matrix.ser");

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

    private double calc_gtf(String term, String[] corpus){
        double sum = 0.0;
        double n = document_array.length;
        for (int i = 0 ; i <n ; i++ ){
            sum += calc_tf(term , corpus[i]);
        }
        return sum/n;
    }
    private double calculate_gtfid(String term, String[] corpus){
        double gtf = calc_gtf(term, corpus);
        double idf = calc_idf(term, corpus);

        return gtf*idf;
    }

    private String[] get_top_p_words(int p){

        HashMap<String, Double> map = new HashMap<String, Double>();
        ValueComparator bvc = new ValueComparator(map);
        TreeMap<String, Double> sorted_map = new TreeMap<String, Double>(bvc);
        String[] p_words = new String[p];

//            map.put("A", 99.5);
//            map.put("B", 67.4);
//            map.put("C", 67.9);
//            map.put("D", 67.3);

        for (int i=0; i< vocabulary.size() ; i++){
            map.put(vocabulary.get(i), calc_gtf(vocabulary.get(i) , document_array));
        }

        sorted_map.putAll(map);

//
//        System.out.println("unsorted map: " + map);
//        sorted_map.putAll(map);
//        System.out.println("results: " + sorted_map);

        int count = 0;
        for (Map.Entry<String,Double> entry:sorted_map.entrySet()) {
            if (count >= p) break;

            p_words[count] = entry.getKey();
            count++;
        }
        return p_words;

    }


    public static void main(String[] args) {

        //define document count
        int n = 1;

        Tf_idf_Calculator tf = new Tf_idf_Calculator(n);
        tf.nlp_pipeline(tf, n);
        System.out.println("NLP Pipeline Done");
        tf.initialize_t_matrix();
        tf.fill_t_matrix(n);
        System.out.println("t_matrix creation done");

        //serialize t_matrix
        tf.serialize_t_matrix();
        // calculate top p words

        int p = 4;
        String[] p_words = tf.get_top_p_words(p);
        System.out.println();


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
