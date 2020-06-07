import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;
 
public class Solution {
    private class Node {
        int attribute;
        
        ArrayList<Integer> partition1;
        ArrayList<Integer> partition2;
        
        Node child1;
        Node child2;    
        
        int label = -1;
        
        boolean isLeaf = false;
        
        public Node() {
            attribute = -1;
            partition1 = new ArrayList<>();
            partition2 = new ArrayList<>();
            child1 = null;
            child2 = null;
        }
        
        public void setAttribute(int attr) {
            attribute = attr;
        }
        
        public void setPart1(ArrayList<Integer> part1) {
            partition1 = part1;
        }
        
        public void setPart2(ArrayList<Integer> part2) {
            partition2 = part2;
        }       
        
        public void setChild1(Node c1) {
            child1 = c1;
        }
        
        public void setChild2(Node c2) {
            child2 = c2;
        }  
        
        public void setLabel(int l) {
            label = l;
            isLeaf = true;
        }
    }
    
    // Returns if we should stop and return a leaf node!
    public boolean stop (ArrayList<ArrayList<Integer>> trainDB, ArrayList<Integer> D, ArrayList<Integer> Attr) {
        boolean ans = false;

        if (Attr.size() <= 1) { // stop if on last attribute
            ans = true;
            //System.out.println("Leaf node: last attribute!");
        }
        else if (D.size() <= 1) { // stop if D size is small
            ans = true;
            //System.out.println("Leaf node: data set only has 1 item");            
        }
        else { // stop if all labels in D are same
            int preLabel = -1;
            int count = 0;
            for (int d:D) {
                int label = trainDB.get(d).get(0);
                if (label != preLabel) {
                    count++;
                    preLabel = label;
                }
            }
            if (count <= 1) { // checks if all same label (could alleviate to majority)
                ans = true;
                //System.out.println("Leaf node: all data has same label!");            
            }
        }
        return ans;
    }
    
    // returns majority label in leaf
    public int majority(ArrayList<ArrayList<Integer>> trainDB, ArrayList<Integer> D, ArrayList<Integer> Attr) {
        HashMap<Integer,Integer> db = new HashMap<>();
        for (int d:D) {
            int label = trainDB.get(d).get(0);
            if (db.containsKey(label)) {
                int count = db.get(label);
                count++;
                db.put(label,count);
            }
            else {
                db.put(label,1);
            }
        }

        int maxLabel = -1;
        int maxCount = -1;
        for (Map.Entry<Integer,Integer> entry : db.entrySet())  { //
            int label = entry.getKey(); // label
            int count = entry.getValue(); // label's count
            if (count > maxCount) {
                maxLabel = label;
                maxCount = count;
            }
        }

        return maxLabel;
    }
    
    public void printAVCs(ArrayList<HashMap<Integer,ArrayList<Integer>>> AVCs) {
        for (HashMap<Integer,ArrayList<Integer>> map:AVCs) {
            System.out.println("------------------");
            for (Map.Entry<Integer,ArrayList<Integer>> entry : map.entrySet())  { //
                int cat = entry.getKey(); // label
                ArrayList<Integer> labelList = entry.getValue(); // label's count
                System.out.println("category: " + cat + ", labels: " + labelList);
            }            
        }
    }
    
    // Returns ArrayList of AVCs (HashMaps) for each attribute
    public ArrayList<HashMap<Integer,ArrayList<Integer>>> getAVCs(ArrayList<ArrayList<Integer>> trainDB, ArrayList<Integer> D, ArrayList<Integer> Attr) {
        ArrayList<HashMap<Integer,ArrayList<Integer>>> AVCs = new ArrayList<>();
        
        // Get categories for each attribute + MaxLabel #
        int maxLabel = 0;
        ArrayList<ArrayList<Integer>> categories = new ArrayList<>(); // for each attribute, gets list of categories
        
        for (int a:Attr) {
            ArrayList<Integer> categoryList = new ArrayList<>();
            for (int d:D) {
                // Check if we got new maxLabel
                int label = trainDB.get(d).get(0);
                if (label > maxLabel) {
                    maxLabel = label;
                }
                // Add category to categoryList if it's new
                int category = trainDB.get(d).get(a);
                
                if (!categoryList.contains(category)) {
                    categoryList.add(category);
                }
            }      
            Collections.sort(categoryList);
            categories.add(categoryList);
        }  
        
        // Initialize AVCs with maxLabel and categories data
        for (ArrayList<Integer> category:categories) { // category list for each attribute
            HashMap<Integer,ArrayList<Integer>> map = new HashMap<>();
            
            for (int c:category) {
                ArrayList<Integer> zeroList = new ArrayList<Integer>(Collections.nCopies(maxLabel, 0));
                map.put(c, zeroList);
            }
            
            AVCs.add(map);
        }
        
        
        for (int d:D) { // iterate through DB
            int label = trainDB.get(d).get(0);
            
            int i = 0; // we're going thru each attribute, but they're not necessarily in chron. order (1,3,7) = (1,2,3)
            for (int a:Attr) { // get
                int category = trainDB.get(d).get(a);
                int count = AVCs.get(i).get(category).get(label-1);
                count++;
                AVCs.get(i).get(category).set(label-1,count);
                i++;
            }
        }
        
        return AVCs;
    }
    
    public double gini (HashMap<Integer,ArrayList<Integer>> AVC,ArrayList<Integer> left,ArrayList<Integer> right) {   
        int maxLabel = AVC.get(left.get(0)).size();
        ArrayList<Integer> leftLabels = new ArrayList<Integer>(Collections.nCopies(maxLabel, 0));
        ArrayList<Integer> rightLabels = new ArrayList<Integer>(Collections.nCopies(maxLabel, 0));

        for (int l:left) { // iterate through categories in left partition
            for (int j = 0; j < maxLabel; j++) { // go through each label and update count
                int count = AVC.get(l).get(j);
                int prevCount = leftLabels.get(j);
                leftLabels.set(j, count + prevCount);
            }
        }
        
        for (int r:right) { // iterate through categories in left partition
            for (int j = 0; j < maxLabel; j++) { // go through each label and update count
                int count = AVC.get(r).get(j);
                int prevCount = rightLabels.get(j);
                rightLabels.set(j, count + prevCount);
            }
        }
        
        int D1 = 0;
        for (int l:leftLabels) {
            D1 += l;
        }
       
        int D2 = 0;
        for (int r:rightLabels) {
            D2 += r;
        }        
        
        int D = D1 + D2;
        
        // calculate indiv. gini(D1)
        double p1 = 0.0;
        if (D1 != 0) {
            for (int l:leftLabels) {
                double p = (double) l/D1;
                p1 += p*p;
            }            
        }
        double gini1 = 1.0-p1;
        
        double p2 = 0.0;
        if (D2 != 0) {
            for (int r:rightLabels) {
                double p = (double) r/D2;
                p2 += p*p;
            }              
        }
        double gini2 = 1.0-p2;

        double gini = ((double)D1/D)*gini1 + ((double)D2/D)*gini2;
        //System.out.println(gini1 + "," + gini2 + ", gini: " + gini);
        return gini;
    }
    
    public HashMap<Double,ArrayList<ArrayList<Integer>>> bestPartition(HashMap<Integer,ArrayList<Integer>> AVC) {
        // Get category list
        ArrayList<Integer> categories = new ArrayList<>();
        for (Map.Entry<Integer,ArrayList<Integer>> entry : AVC.entrySet())  { //
            int cat = entry.getKey(); // label
            categories.add(cat);
        }    
        
        // Get all possible binary partitions of categories
        ArrayList<ArrayList<Integer>> part1 = new ArrayList<>();
        ArrayList<ArrayList<Integer>> part2 = new ArrayList<>();
        
        for (int i = 0; i < categories.size(); i++) {
            if (categories.size() > 1) {
                for (int j = 1; j < categories.size(); j++) {
                    List<Integer> l = categories.subList(0,j);
                    List<Integer> r = categories.subList(j,categories.size());

                    ArrayList<Integer> left = new ArrayList<>();
                    ArrayList<Integer> right = new ArrayList<>();
                    for (int k:l) {
                        left.add(k);
                    }
                    for (int k:r) {
                        right.add(k);
                    }
                    Collections.sort(left);
                    Collections.sort(right);

                    if (!part1.contains(left) && !part2.contains(left)) {
                        part1.add(left);
                        part2.add(right);
                    }
                }                
            }
            else {
                part1.add(categories);
                ArrayList<Integer> empty = new ArrayList<>();
                part2.add(empty);
            }
            categories.add(categories.remove(0)); // Add front categories element to end for next iter
        }
        
        double minGini = 2;
        ArrayList<Integer> leftAns = new ArrayList<>();
        ArrayList<Integer> rightAns = new ArrayList<>();
        // For each partition, calculate gini
        for (int i = 0; i < part1.size(); i++) {
            ArrayList<Integer> left = part1.get(i);
            ArrayList<Integer> right = part2.get(i);
            //System.out.println("current partition: " + left.toString() + ";" + right.toString());
            double gin = gini(AVC, left, right);
            if (gin < minGini) {
                minGini = gin;
                leftAns = left;
                rightAns = right;
            }
        }
        
        //System.out.println("selected partition: " + minGini + ", left: " + leftAns.toString() + ", right: " + rightAns.toString());
        //System.out.println("----------------------------------------------------");
        
        HashMap<Double, ArrayList<ArrayList<Integer>>> ans = new HashMap<>();
        ArrayList<ArrayList<Integer>> lst = new ArrayList<>();
        lst.add(leftAns);
        lst.add(rightAns);
        ans.put(minGini,lst);
        return ans;
    }
    
    public ArrayList<Integer> newDs(ArrayList<ArrayList<Integer>> trainDB, ArrayList<Integer> D, ArrayList<Integer> part, int attr) {
        ArrayList<Integer> newD = new ArrayList<>();
        for (int d:D) {
            if (part.contains(trainDB.get(d).get(attr))) {
                newD.add(d);
            }
        }
        return newD;
    }
    
    // builds tree
    public Node buildTree(ArrayList<ArrayList<Integer>> trainDB, ArrayList<Integer> D, ArrayList<Integer> Attr) {   
        Node node = new Node();
        
        // Check if we should stop! If so, return Leaf Node with majority
        if (stop(trainDB,D,Attr)) {
            int label = majority(trainDB, D, Attr); // returns majority label
            node.setLabel(label);
        }
        else {
            // Build AVCs -> Do HashMap <Attr Index, Arraylist of label counts where index is label #>
            ArrayList<HashMap<Integer,ArrayList<Integer>>> AVCs = getAVCs(trainDB, D, Attr);
            //printAVCs(AVCs);
            // Get best attr + with respective binary split and gini
            double minGini = 2;
            int index = 0;
            int attr = 0;
            ArrayList<ArrayList<Integer>> bestPartition = new ArrayList<>(); // contains 2 list of partitioned categories
            bestPartition.add(new ArrayList<Integer>());
            bestPartition.add(new ArrayList<Integer>());
            for (HashMap<Integer,ArrayList<Integer>> AVC:AVCs) {
                
                HashMap<Double, ArrayList<ArrayList<Integer>>> ginis = bestPartition(AVC); 
                 Map.Entry<Double,ArrayList<ArrayList<Integer>>> entry = ginis.entrySet().iterator().next();
                 double key = entry.getKey();
                 ArrayList<ArrayList<Integer>> value = entry.getValue();
                
                if (key < minGini) {
                    minGini = key;
                    bestPartition.set(0,value.get(0));
                    bestPartition.set(1,value.get(1));
                    attr = Attr.get(index);
                }
                
                index++;                
            }
            
            //System.out.println("best attribute is: " + attr);
            //System.out.println("partitions are: " + bestPartition.get(0).toString() + ": "+ bestPartition.get(1).toString());

            // Construct the new node with attr, binary split data
            node.setAttribute(attr);
            node.setPart1(bestPartition.get(0));
            node.setPart2(bestPartition.get(1));
            
            // remove this attr from Attr
            Attr.remove(Attr.indexOf(attr));
            ArrayList<Integer> modifiedAttr = new ArrayList<>();
            for (int a:Attr) {
                modifiedAttr.add(a);
            }
            //System.out.println("new attribute list: " + modifiedAttr.toString());
            
            // make 2 D's, for child1 and child2 for recursive calls
            ArrayList<Integer> leftD = newDs(trainDB, D, bestPartition.get(0),attr);
            ArrayList<Integer> rightD = newDs(trainDB, D, bestPartition.get(1),attr);
            
            //System.out.println("left Ds: " + leftD.toString());
            //System.out.println("right Ds: " + rightD.toString());
            // Set new node.child1 to output Node from recursive call
            node.setChild1(buildTree(trainDB, leftD, modifiedAttr));
            
            // Set new node.child2 to output Node from recursive call       
            node.setChild2(buildTree(trainDB, rightD, modifiedAttr));
        }
        
        return node;
    }
    
    public void printTree(Node node) {
        System.out.println("---------------------------------");
        
        if (!node.isLeaf) {
            System.out.println("Split by attribute: " + node.attribute);
            System.out.println("partition 1: " + node.partition1.toString());
            System.out.println("partition 2: " + node.partition2.toString());            
            printTree(node.child1);
            printTree(node.child2);
        }
        else {
            System.out.println("Leaf! Label is: " + node.label);          
        }
    }
    
    public void navigateTree(Node nod, ArrayList<Integer> data) {
        Node ptr = null;
        
        if (nod.isLeaf) {
            System.out.println(nod.label);
        }
        else {
            int attr = nod.attribute;
            int cat = data.get(attr);
            // category in left partition
            if (nod.partition1.contains(cat)) {
                ptr = nod.child1;
            }
            // category in right partition
            else {
                ptr = nod.child2;
            }
            navigateTree(ptr, data);
        }
    }
    
    public void useTree(Node tree, ArrayList<ArrayList<Integer>> testDB) {
        for (ArrayList<Integer> data:testDB) {
            navigateTree(tree, data);
        }
    }
    
    public static void main(String[] args) {
        // set up training data
        Scanner scanner = new Scanner(System.in);
        int trainLength = Integer.parseInt(scanner.nextLine()); // size of training set
        ArrayList<ArrayList<Integer>> trainDB = new ArrayList<>();   // arrayList of arrayList<Integer> for each data
        ArrayList<Integer> D = new ArrayList<>();
        ArrayList<Integer> Attr = new ArrayList<>();

        for (int i = 0; i < trainLength; i++) {
            ArrayList<Integer> temp = new ArrayList<>();
            String[] data = scanner.nextLine().split(" ");
            temp.add(Integer.parseInt(data[0])); // 1st element is the label
            for (int j = 1; j < data.length; j++) {
                if (i == 0) {
                    Attr.add(j);
                }
                int d = Integer.parseInt(data[j].split(":")[1]);
                temp.add(d);
            }  
            D.add(i);
            trainDB.add(temp);
        }

        // set up real data
        int testLength = Integer.parseInt(scanner.nextLine());
        ArrayList<ArrayList<Integer>> testDB = new ArrayList<>();   // arrayList of arrayList<Integer> for each data
        for (int i = 0; i < testLength; i++) {
            ArrayList<Integer> trainSubData = new ArrayList<>();
            trainSubData.add(-1); // adds dummy variable, since index starts at 1!
            String[] data = scanner.nextLine().split(" ");
            for (int j = 0; j < data.length; j++) {
                int d = Integer.parseInt(data[j].split(":")[1]);
                trainSubData.add(d);
            } 
            testDB.add(trainSubData);
        }
        scanner.close();

        // Build decision tree!
        Solution soln = new Solution();
        Node tree = soln.buildTree(trainDB, D, Attr);
        //soln.printTree(tree);
        
        soln.useTree(tree,testDB);
    }
}