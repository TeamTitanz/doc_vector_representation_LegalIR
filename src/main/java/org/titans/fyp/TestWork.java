package org.titans.fyp;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Buddhi on 9/9/2017.
 */
class TestWork {

//    class WordValue {
//        private String word;
//        private double value;
//
//        public WordValue(String word, double value) {
//            this.word = word;
//            this.value = value;
//        }
//
//        public String getWord() {
//            return word;
//        }
//
//        public double getValue() {
//            return value;
//        }
//    }
//
//    class ValueComparator implements Comparator<WordValue> {
//
//        @Override
//        public int compare(WordValue o1, WordValue o2) {
//            // descending order
//            double tem = o2.getValue() - o1.getValue();
//            if (tem < 0) {
//                return -1;
//            } else if (tem == 0.0) {
//                return 0;
//            } else {
//                return 1;
//            }
//        }
//
//    }
//
//    public void test(){
//        WordValue wordValues[] = new WordValue[6];
//        wordValues[0] = new WordValue("A", 67.4);
//        wordValues[1] = new WordValue("B", 99.5);
//        wordValues[2] = new WordValue("C", 67.9);
//        wordValues[3] = new WordValue("D", 67.3);
//        wordValues[4] = new WordValue("E", 99.8);
//        wordValues[5] = new WordValue("F", 99.8);
//
//        System.out.println("Before");
//        for (int i = 0; i < wordValues.length; i++) {
//            System.out.println(wordValues[i].getWord() + "\t"
//                    + wordValues[i].getValue());
//        }
//
//        System.out.println("After");
//        Arrays.sort(wordValues, new ValueComparator());
//
//        for (int i = 0; i < wordValues.length; i++) {
//            System.out.println(wordValues[i].getWord() + "\t"
//                    + wordValues[i].getValue());
//        }
//    }
//
//    public static void main(String[] args) {
//
//        TestWork tw = new TestWork();
//        tw.test();
//    }

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


    public static void main(String[] args) {

        TestWork tw = new TestWork();

        String[] doc = {"Lorem", "ipsum", "dolor", "ipsum", "sit", "ipsum"};
        System.out.println(tw.calc_tf("ipsum", doc));

    }

}