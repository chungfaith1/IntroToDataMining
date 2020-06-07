import java.io.*;
import java.util.*;

public class Solution {
    public static double euclidDistance(String c1, String c2) {
        String[] s1 = c1.split(",");
        String[] s2 = c2.split(",");
        
        double x1 = Float.parseFloat(s1[0]);
        double x2 = Float.parseFloat(s2[0]);
        double y1 = Float.parseFloat(s1[1]);
        double y2 = Float.parseFloat(s2[1]);
        
        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }
    
    // returns arraylist of euclidean distances btwn all elements in 2 clusters
    public static ArrayList<Double> distance(ArrayList<String> cluster1, ArrayList<String> cluster2) {
        ArrayList<Double> distances = new ArrayList<>();

        // does all of cluster2, per cluster1
        for (String c1: cluster1) {
            for (String c2: cluster2) {
                double dist = euclidDistance(c1,c2);
                distances.add(dist);
            }
        }
        return distances;
    }
    
    // returns the appropriate distance btwn 2 clusters, depending on m
    public static double getDistance(ArrayList<String> cluster1, ArrayList<String> cluster2, int m) {
        double dis = -1.0;
        
        // TODO: go through all combos and get distances! Just an arrayList should be fine (nxm list)
        ArrayList<Double> distances = distance(cluster1, cluster2);
        
        if (m == 0) { //single - return dist. btwn 2 closest items
            dis = Collections.min(distances);
        }
        else if (m == 1) { //complete - return dist. btwn 2 farthest items
            dis = Collections.max(distances);
        }
        else if (m == 2) { //average - return average dist. btwn 2 clusters
            double sum = 0.0;
            double count = 0.0;
            for (double x:distances) {
                sum+=x;
                count++;
            }
            if (count > 0) {
                dis = sum/count;
            }
        }
        return dis;
    }
    
    // goes through all clusters, finds 2 closest ones, and merges the 2 (keeping rest the same)
    // and returns new cluster list
    public static ArrayList<ArrayList<String>> merge(ArrayList<ArrayList<String>> clusters, int m) {
        ArrayList<ArrayList<String>> newClusters = new ArrayList<>();
        int size = clusters.size();

        HashMap<String, Double> distances = new HashMap<>(); // Key = "i,j" , Val = distance
        // compare the clusters and get their distances (for all valid combos of clusters)!
        for (int i = 0; i < size; i++) {
            for (int j = i+1; j < size; j++) {
                ArrayList<String> cluster1 = clusters.get(i);
                ArrayList<String> cluster2 = clusters.get(j);
                
                double dist = getDistance(cluster1, cluster2, m);
                String pair = i + "," + j;
                distances.put(pair,dist);
            }
        }

        /* DEBUG: prints the calculated distances
        System.out.println("DEBUG: CALCULATED DISTANCES BTWN CLUSTERS");
        for (Map.Entry<String,Double> entry : distances.entrySet())  { //
            String pair = entry.getKey();
            double dist = entry.getValue();

            System.out.println(pair + ", " + dist);
        } */
        
        // get 2 closest clusters based on results!
        String minKey = "";
        double minVal = -1.0;
        for (Map.Entry<String,Double> entry : distances.entrySet())  { //
            String k = entry.getKey();
            double v = entry.getValue();
            //System.out.println("k: " + k + ", v: " + v); // DEBUG
            if (minVal == -1.0) {
                minVal = v;
                minKey = k;
            }
            else if (v < minVal) {
                minVal = v;
                minKey = k;
            }
            //System.out.println("minval: " + minVal); // DEBUG

        }       
        
        String[] keys = minKey.split(",");
        int clus1 = Integer.parseInt(keys[0]);
        int clus2 = Integer.parseInt(keys[1]);

        /* DEBUG: prints 2 closest clusters
        System.out.println("DEBUG: 2 closest clusters are: ");
        System.out.println(clus1 + ", " + clus2);*/
        
        // join the 2 min clusters!
        ArrayList<String> cluster1 = clusters.get(clus1);
        ArrayList<String> cluster2 = clusters.get(clus2);
        cluster1.addAll(cluster2);
        
        for (int i = 0; i < size; i++) {
            if (i != clus1 && i != clus2) {
                newClusters.add(clusters.get(i));
            }
        }
        newClusters.add(cluster1);

        /* DEBUG: prints new clustering
        System.out.println("DEBUG: NEW CLUSTERING");
        int count = 0;
        for (ArrayList<String> clust:newClusters) {
            for (String str:clust) {                
                System.out.println(count + " " + str);
            }
            count++;
        }*/
        
        return newClusters;
    }
    
    public static void main(String[] args) {
        // initialization
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int k = scanner.nextInt();
        int m = scanner.nextInt();
        
        ArrayList<ArrayList<String>> clusters = new ArrayList<>();
        ArrayList<String> db = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String str = scanner.next() + "," + scanner.next();
            ArrayList<String> cluster = new ArrayList<>();
            cluster.add(str);
            clusters.add(cluster);
            db.add(str);
        }        
        
        scanner.close();
        
        
        while (clusters.size() > k) {
            clusters = merge(clusters, m); // get new clusters set! only 2 clusters merge per iteration
        }

        
        // DEBUG: FINAL SOLUTION
        //System.out.println("DEBUG: FINAL SOLUTION"); //DEBUG
        int count = 0;
        int[] ans = new int[db.size()];
        for (ArrayList<String> clust:clusters) {
            for (String str:clust) {    
                int index = db.indexOf(str);
                //System.out.println(count + " " + index); //DEBUG
                ans[index] = count;
            }
            count++;
        }
        
        for (int i = 0; i < ans.length; i++) {
            System.out.println(ans[i]);
        }

    }
}