import java.io.*;
import java.util.*;

public class Solution {

    public static void main(String[] args) {
        // initialization
        Scanner scanner = new Scanner(System.in);
        HashMap<Integer,Double> labels = new HashMap<>(); // <Label, Probability>
        ArrayList<ArrayList<Integer>> trainingDB = new ArrayList<>();
        ArrayList<ArrayList<Integer>> testDB = new ArrayList<>();
        
        scanner.next(); // throw away 1st line
        
        while (scanner.hasNext()) {
            String[] str = scanner.next().split(",");
            ArrayList<Integer> list = new ArrayList<>(); // line for each data, added to DB

            // Add to trainingDB
            if (Integer.parseInt(str[str.length-1]) != -1) {
                for (int i = 1; i < str.length; i++) { // throw away name of animal
                    list.add(Integer.parseInt(str[i]));
                }
                trainingDB.add(list);
                
                if (labels.containsKey(Integer.parseInt(str[str.length-1]))) {
                    double count = labels.get(Integer.parseInt(str[str.length-1]));
                    count++;
                    labels.put(Integer.parseInt(str[str.length-1]),count);
                }
                else {
                    labels.put(Integer.parseInt(str[str.length-1]),1.0);                    
                }
            }
            // Add to testDB
            else {
                for (int i = 1; i < str.length; i++) {
                    list.add(Integer.parseInt(str[i]));
                }
                testDB.add(list);
            }
        }        
        
        for (Map.Entry mapElement : labels.entrySet()) {  // sets probabilities
            // update probability
            int key = (int)mapElement.getKey(); 
            double value = ((double)mapElement.getValue())/trainingDB.size(); 
            labels.put(key,value);
        }          
        
        scanner.close();    
        
        // Get Naive Bayes Classifier for each data in test DB!
        // 1. Get label count for each attribute
        for (ArrayList<Integer> test:testDB) { // going through each test item
            // make empty hashmap
            HashMap<Integer, ArrayList<Double>> counts = new HashMap<>();
            for (Map.Entry mapElement : labels.entrySet()) { 
                int key = (int)mapElement.getKey(); 
                // initialize counts
                ArrayList<Double> empty = new ArrayList<>();
                for (int i = 0; i < trainingDB.get(0).size() - 1; i++) {
                    empty.add(0.0);
                }
                counts.put(key,empty);
            }  
            
            // go through entire DB to get counts
            for (ArrayList<Integer> data:trainingDB) { 
                for (int i = 0; i < data.size()-1; i++) {
                    int testVal = test.get(i);   
                    int trainVal = data.get(i);
                    if (testVal == trainVal) {
                        int label = data.get(data.size()-1);
                        double cnt = counts.get(label).get(i);
                        cnt++;
                        ArrayList<Double> countsLst = counts.get(label);
                        countsLst.set(i,cnt);
                        counts.put(label,countsLst);
                    }
                }                
            }
            
            double maxProb = 0.0;
            int maxKey = -1;
            for (Map.Entry mapElement : counts.entrySet()) { 
                int key = (int)mapElement.getKey(); 
                ArrayList<Double> list = (ArrayList<Double>)mapElement.getValue();
                
                double labelCount = labels.get(key)*trainingDB.size()+0.1;
                double labelProb = labels.get(key);
                double bayesSum = 1.0;
                for (double cnt:list) {
                    bayesSum *= (cnt+0.1)/labelCount;
                }
                bayesSum*=labelProb;
                if (bayesSum > maxProb) {
                    maxProb = bayesSum;
                    maxKey = key;
                }
            }              
            
            System.out.println(maxKey);
        }


    }
}