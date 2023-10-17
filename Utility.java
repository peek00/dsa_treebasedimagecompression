import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class NodeSizeComparator implements Comparator<Node> {
    /**
     * Comparator created to compare two Node objects by the size of the ArrayList
     * Used to sort the rgb values by how many pixels have the same color
     */
    @Override
    public int compare(Node node1, Node node2) {
        // Compare the sizes of ArrayLists in Node objects
        return Integer.compare(node1.coordinates.size(), node2.coordinates.size());
    }
}

class PixelCoordinate {
    /**
     * Honestly just a class to store [int,int]. 
     * I didn't want to nest arraylist inside hashmap.
     */
    int x;
    int y;

    public PixelCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
}

class Node {
    /**
     * A node is a class in a binary tree that stores
     * the RGB value and a list of all pixel coordinates associated.
     * Originally created to help with tree based algo,
     * but there is a chance we can use an array based
     * heap implementation and ignore this class entirely.
     */
    int rgb;
    ArrayList<PixelCoordinate> coordinates;
    Node left;
    Node right;

    public Node(int rgb, ArrayList<PixelCoordinate> coordinates) {
        this.rgb = rgb;
        this.coordinates = coordinates;
        left = null;
        right = null;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public ArrayList<PixelCoordinate> getValue() {
        return this.coordinates;
    }

    public int getSize() {
        return this.coordinates.size();
    }

    public int getRGB() {
        return this.rgb;
    }
}

public class Utility {

    public static Node constructTree(List<Map.Entry<Integer, ArrayList<PixelCoordinate>>> entryList, int currentIndex,
            int depth) {
        if (currentIndex >= entryList.size() || depth == 0) {
            // Base case: Stop recursion if we've reached the end of the list or depth is
            // This code creates a max heap tree (I think) from a sorted list.
            // Not fully tested
            return null;
        }

        // Calculate the index of the left and right children
        int leftChildIndex = 2 * currentIndex + 1;
        int rightChildIndex = 2 * currentIndex + 2;

        // Create the current node using the entry at currentIndex
        Map.Entry<Integer, ArrayList<PixelCoordinate>> currentEntry = entryList.get(currentIndex);
        Node currentNode = new Node(currentEntry.getKey(), currentEntry.getValue());

        // Recursively construct left and right subtrees
        currentNode.setLeft(constructTree(entryList, leftChildIndex, depth - 1));
        currentNode.setRight(constructTree(entryList, rightChildIndex, depth - 1));

        return currentNode;
    }

    public static int compressRGB(int r, int g, int b) {
        /**
         * Compresses 3 rgb value into one int.
         */
        return (r << 16) | (g << 8) | b;
    }

    public static int[] decompressRGB(int rgb) {
        /**
         * Decompresses one int into 3 rgb values.
         * Returns it in a list [r,g,b]
         */
        int[] rgbComponents = new int[3];
        rgbComponents[0] = (rgb >> 16) & 0xFF; // Red component
        rgbComponents[1] = (rgb >> 8) & 0xFF; // Green component
        rgbComponents[2] = rgb & 0xFF; // Blue component
        return rgbComponents;
    }

    public static void printInOrder(Node root) {
    /**
     * Helper function that prints the tree in order.
     * Can adjust the order of statement below to print in pre/post order.
     */
        if (root == null) {
            return;
        }

        printInOrder(root.left);
        System.out.println(root.rgb + " has " + root.coordinates.size() + " coordinates inside.");
        printInOrder(root.right);
    }

    public void Compress(int[][][] pixels, String outputFileName) throws IOException {

        // Creates a Hashmap of all the pixels and their coordinates
        // 123456: [(1,2), (3,4), (5,6)]
        HashMap<Integer, ArrayList<PixelCoordinate>> pixelMap = new HashMap<Integer, ArrayList<PixelCoordinate>>();
        int width = pixels.length;
        int height = pixels[0].length;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                PixelCoordinate coord = new PixelCoordinate(x, y);
                int pixelValue = compressRGB(pixels[x][y][0], pixels[x][y][1], pixels[x][y][2]);
                // Put into map
                pixelMap.putIfAbsent(pixelValue, new ArrayList<PixelCoordinate>());
                pixelMap.get(pixelValue).add(coord);
            }
        }
        // Example at this point is 16777183 [(33, 16), (69, 52), (69, 53), (78, 29), (333, 45)]
        
        // @Timo and Axel this is where yall will likely be continuing
        // Sort this hashmap into a sorted list by the size of the content and construct
        List<Map.Entry<Integer, ArrayList<PixelCoordinate>>> entryList = new ArrayList<>(pixelMap.entrySet());
        List<Node> nodeList = new ArrayList<>();
        for (Map.Entry<Integer, ArrayList<PixelCoordinate>> entry : entryList) {
            nodeList.add(new Node(entry.getKey(), entry.getValue()));
        }
        System.out.println("RGB " + Arrays.toString(decompressRGB(nodeList.get(0).getRGB())) + " has "
                + nodeList.get(0).getSize());
        java.util.Collections.sort(nodeList, Collections.reverseOrder(new NodeSizeComparator()));
        System.out.println("RGB " + Arrays.toString(decompressRGB(nodeList.get(0).getRGB())) + " has "
                + nodeList.get(0).getSize());

        // Construct the tree
        // Node root = constructTree(nodeList, 0, 1);

        // System.out.println("In-Order Traversal:");
        // printInOrder(root);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFileName))) {
            oos.writeObject(pixels);
        }
    }

    public int[][][] Decompress(String inputFileName) throws IOException, ClassNotFoundException {
        // The following is a bad implementation that we have intentionally put in the
        // function to make App.java run, you should
        // write code to reimplement the function without changing any of the input
        // parameters, and making sure that it returns
        // an int [][][]
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputFileName))) {
            Object object = ois.readObject();

            if (object instanceof int[][][]) {
                return (int[][][]) object;
            } else {
                throw new IOException("Invalid object type in the input file");
            }
        }
    }

}