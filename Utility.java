import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class NodeComparator implements Comparator<Node> {
    @Override
    public int compare(Node node1, Node node2) {
        // First, compare by the Integer (key)
        int keyComparison = Integer.compare(node1.getRGB(), node2.getRGB());
        
        // If the keys are equal, compare by the size of the ArrayList
        if (keyComparison == 0) {
            return Integer.compare(node1.getSize(), node2.getSize());
        }
        
        return keyComparison;
    }
}

class PixelCoordinate implements Serializable {
    /**
     * Honestly just a class to store [int,int]. 
     * I didn't want to nest arraylist inside hashmap.
     */
    private static final long serialVersionUID = 1L; // Add a default serial version ID
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
        System.out.println("RGB " + nodeList.get(0).getRGB() + " has "
                + nodeList.get(0).getSize());
        java.util.Collections.sort(nodeList, Collections.reverseOrder(new NodeComparator()));
        System.out.println("RGB " + nodeList.get(0).getRGB() + " has "
                + nodeList.get(0).getSize());
        System.out.println("RGB " + nodeList.get(1).getRGB() + " has "
                + nodeList.get(1).getSize());
        System.out.println("RGB " + nodeList.get(2).getRGB() + " has "
                + nodeList.get(2).getSize());

        // Compressiong begins here
        // Given a threshold n, I want to loop through the list and combine the arraySize of things that are too little 
        // To the previous one
        int threshold = 100;
        int prevRGB = -1;

        HashMap <Integer, ArrayList<PixelCoordinate>> compressedMap = new HashMap<Integer, ArrayList<PixelCoordinate>>();
        // Start the loop from the second element (index 1) to the end
        for (int i = 0; i < nodeList.size(); i++) {
            Node currentNode = nodeList.get(i);
            if (prevRGB == -1 || Math.abs(prevRGB - currentNode.getRGB()) >= threshold) {
                // Create a new mapping
                compressedMap.put(currentNode.getRGB(), currentNode.getValue());
                prevRGB = currentNode.getRGB();
            } else {
                // Add to previous one
                ArrayList<PixelCoordinate> existingList = compressedMap.get(prevRGB);
                existingList.addAll(currentNode.getValue());
                // Setting prev hashMap to NONE to be garbage collected
                nodeList.get(i).coordinates = null;
            }

        }

        System.out.println("Size is now " + nodeList.size());
        System.out.println("Size is now " + compressedMap.size());
        System.out.println(compressedMap.getClass());

        // try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFileName))) {
        //     oos.writeObject(pixels);
        // }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFileName))) {
            oos.writeObject(compressedMap);
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

            // if (object instanceof int[][][]) {
            //     return (int[][][]) object;
            // } else {
            //     throw new IOException("Invalid object type in the input file");
            // }
            int[][][] output = new int[500][375][3];
            System.out.println(object.getClass());
            for (Map.Entry<Integer, ArrayList<PixelCoordinate>> entry : ((HashMap<Integer, ArrayList<PixelCoordinate>>) object).entrySet()) {
                int[] rgbComponents = decompressRGB(entry.getKey());
                for (PixelCoordinate coord : entry.getValue()) {
                    output[coord.x][coord.y][0] = rgbComponents[0];
                    output[coord.x][coord.y][1] = rgbComponents[1];
                    output[coord.x][coord.y][2] = rgbComponents[2];
                }
            }
            // for (HashMap<Integer, ArrayList<PixelCoordinate>> entry : object) {
            //     System.out.println(entry.getClass());
            // }
            // System.out.println(object.getClass());
            return output;
        }
    }

}