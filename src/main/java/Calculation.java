//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by Keetmalin on 6/13/2017
// * Project - doc_vector_representation_LegalIR
// */
//public class Calculation {
//
//    public List<List<Double>> CalDocumentVector(List<String> inputWordList, ArrayList<String> vocabulary, double[][] t_matrix) {
//
//        List<List<Double>> DocumentVector = new ArrayList<List<Double>>();
//
//        File f = new File(System.getProperty("user.dir") + File.separator + "Output");
//        FilenameFilter textFilter = new FilenameFilter() {
//            public boolean accept(File dir, String name) {
//                return name.toLowerCase().endsWith(".txt");
//            }
//        };
//
//        File[] files = f.listFiles(textFilter);
//        for (int docIndex = 0; docIndex < files.length; docIndex++) {
//            try {
//                String fileName = (files[docIndex]).getCanonicalPath();
//                DocumentVector.add(docVector(fileName, docIndex, inputWordList, vocabulary, t_matrix));
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return DocumentVector;
//    }
//
//    public List<Double> docVector(String fileName, int docIndex, List<String> inputWordList, ArrayList<String> vocabulary, double[][] t_matrix) {
//
//        BufferedReader br = null;
//        List<Double> docVector = new ArrayList<Double>();
//
//        try {
//            br = new BufferedReader(new FileReader(fileName));
//
//            StringBuilder sb = new StringBuilder();
//            String sCurrentLine;
//            while ((sCurrentLine = br.readLine()) != null) {
//                sb.append(sCurrentLine);
//                sb.append(" ");
//            }
//
//            String paragraph = sb.toString();
//            for (String word : inputWordList) {
//                if (paragraph.contains(word)) {
//                    int index = vocabulary.indexOf(word);
//                    docVector.add(t_matrix[index][docIndex]);
//                } else {
//                    docVector.add(0.0);
//                }
//            }
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return docVector;
//    }
//
//    public static void main(String[] args) {
//
//        Calculation cal = new Calculation();
//
//
//        cal.CalDocumentVector(List<String> inputWordList, ArrayList<String> vocabulary, double[][] t_matrix);
//
//    }
//
//    private void getPWordList(){
//        try {
//            File file = new File("src/test.ser");
//            if (file.exists()) {
//                System.out.println("serialized file found. Reading from it");
//                FileInputStream fileIn = new FileInputStream(file);
//                ObjectInputStream in = new ObjectInputStream(fileIn);
//                valueMatrices = (ValueMatrix[]) in.readObject();
//                in.close();
//                fileIn.close();
//            } else {
//    }
//}
