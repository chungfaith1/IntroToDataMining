import java.io.*;
import java.util.*;

public class Solution {
    public static void printMap(HashMap<Integer,ArrayList<Integer>> map) {
        for (Map.Entry<Integer,ArrayList<Integer>> entry : map.entrySet())  { //
            Integer k = entry.getKey();
            ArrayList<Integer> v = entry.getValue();
            System.out.println("cluster: " + k + ", items: " + v.toString()); // DEBUG   
        }
    }
                
    // MAP (CLUSTER #, ARRAYLIST<INDEXES of ITEMS>)
    public static HashMap<Integer, ArrayList<Integer>> getClustering(ArrayList<Integer> list) {
        int size = list.size();
        HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();
        
        for (int i = 0; i < size; i++) {
            int clusterNum = list.get(i);
            if (!map.containsKey(clusterNum)) {
                ArrayList<Integer> lists = new ArrayList<>();
                lists.add(i);
                map.put(clusterNum, lists);
            }
            else {
                ArrayList<Integer> prevList = map.get(clusterNum);
                ArrayList<Integer> newList = new ArrayList<>(prevList);
                newList.add(i);
                map.put(clusterNum, newList);
            }
        }
        return map;
    }
    public static int matchesBtwnLists(ArrayList<Integer> l1, ArrayList<Integer> l2) {
        int count = 0;
        
        for (int x:l1) {
            if (l2.contains(x)) {
                count++;
            }
        }
        return count;
    }
    public static float NMI(ArrayList<Integer> truth, ArrayList<Integer> cluster) {
        int n = truth.size(); // number of points
        int r = Collections.max(cluster);
        int k = Collections.max(truth);
        
        HashMap<Integer, ArrayList<Integer>> clusters = getClustering(cluster);
        HashMap<Integer, ArrayList<Integer>> truths = getClustering(truth);
        
        float h_c = 0.0f;
        float h_t = 0.0f;
        float H = 0.0f;
        float I = 0.0f;
        float NMI = 0.0f;
        
        for (int i = 0; i <= r; i ++) {
            int clustSize = clusters.get(i).size();
            float pc = (float)clustSize/n;
            h_c += -1.0f * pc * Math.log10(pc);
            
            for (int j = 0; j <= k; j++) {
                int truthSize = truths.get(j).size();
                float pt = (float)truthSize/n;
                
                if (i == 0) {
                    h_t += -1.0f * pt * Math.log10(pt);     
                }
                
                int matches = matchesBtwnLists(clusters.get(i),truths.get(j));
                float pij = (float)matches/n;//clustSize;

                if (pij != 0) {
                    H += pij*(float)Math.log10(pij/(pc*pt));
                }
            }
        }
        
        NMI = H/(float)Math.sqrt(h_c*h_t);
        
        return NMI;
    }
    
    public static float jaccard(ArrayList<Integer> truth, ArrayList<Integer> cluster) {
        int n = truth.size(); // number of points
        int r = Collections.max(cluster);
        int k = Collections.max(truth);
        
        HashMap<Integer, ArrayList<Integer>> clusters = getClustering(cluster);
        HashMap<Integer, ArrayList<Integer>> truths = getClustering(truth);
        
        int TP = 0;
        int FN = 0;
        int fn = 0;        
        int FP = 0;
        int fp = 0;
        for (int i = 0; i <= r; i ++) {
            int sizee = clusters.get(i).size();
            if (sizee > 1) {
                fp += sizee*(sizee-1)/2;
            }  
            
            for (int j = 0; j <= k; j++) {
                // Calculate # true pairs for each label in each cluster
                int matches = matchesBtwnLists(clusters.get(i),truths.get(j));
                if (matches > 1) {
                    TP += matches*(matches-1)/2;
                }
                
                // Calculate # pairs per partition for FN
                if (i == 0) {
                    int size = truths.get(j).size();
                    if (size > 1) {
                        fn += size*(size-1)/2;
                    }                    
                }
            }
        }
        FN = fn - TP;
        FP = fp - TP;
        float Jaccard = (float) TP/(TP + FN + FP);

        return Jaccard;
    }
    
    public static void main(String[] args) {
        // set up truth and cluster arrayLists
        Scanner scanner = new Scanner(System.in);
        ArrayList<Integer> truth = new ArrayList<>();
        ArrayList<Integer> cluster = new ArrayList<>();
        
        while(scanner.hasNext()) {
            truth.add(Integer.parseInt(scanner.next()));
            cluster.add(Integer.parseInt(scanner.next()));   
        }        
        
        scanner.close();
        
        // Calculate NMI
        float nmi = NMI(truth, cluster);
        
        // Calculate Jaccard
        float jacc = jaccard(truth, cluster);
        
        // print answers
        System.out.printf("%.3f %.3f",nmi,jacc);

    }
}