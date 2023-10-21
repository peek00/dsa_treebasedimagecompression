import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// Store in a 
// [ byte1, byte2, byte3, x,y,x,y,x,y]
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

class Node {
    /**
     * A node is a class in a binary tree that stores
     * the RGB value and a list of all pixel coordinates associated.
     * Originally created to help with tree based algo,
     * but there is a chance we can use an array based
     * heap implementation and ignore this class entirely.
     */
    int rgb;
    ArrayList<int[]> coordinates;
    Node left;
    Node right;

    public Node(int rgb, ArrayList<int[]> coordinates) {
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

    public ArrayList<int[]> getValue() {
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

    public static byte compressToByte(int value){
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException("Value is out of the valid range (0-255).");
        }
        return (byte) value;
    }

    public static int decompressFromByte(byte compressedValue){
        return Byte.toUnsignedInt(compressedValue);
    }

    public static int compressRGB(int pixels, int pixels2, int pixels3) {
        /**
         * Compresses 3 rgb value into one int.
         */
        return (int) ((int) (pixels << 16) | (pixels2 << 8) | pixels3);
    }

    public static int[] decompressRGB(int rgb) {
        /**
         * Decompresses one int into 3 rgb values.
         * Returns it in a list [r,g,b]
         */
        int[] rgbComponents = new int[3];
        rgbComponents[0] = ((rgb >> 16) & 0xFF); // Red component
        rgbComponents[1] = ((rgb >> 8) & 0xFF); // Green component
        rgbComponents[2] = (rgb & 0xFF); // Blue component
        return rgbComponents;
    }

    public void Compress(int[][][] pixels, String outputFileName) throws IOException {

        // Creates a Hashmap of all the pixels and their coordinates

        HashMap<Integer, ArrayList<int[]>> pixelMap = new HashMap<Integer, ArrayList<int[]>>();
        int width = pixels.length;
        int height = pixels[0].length;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int[] coord = { x, y };
                int pixelValue = compressRGB(pixels[x][y][0], pixels[x][y][1], pixels[x][y][2]);
                // Put into map
                pixelMap.putIfAbsent(pixelValue, new ArrayList<int[]>());
                pixelMap.get(pixelValue).add(coord);
            }
        }

        // Sort this hashmap into a sorted list by the size of the content and construct
        List<Map.Entry<Integer, ArrayList<int[]>>> entryList = new ArrayList<>(pixelMap.entrySet());
        List<Node> nodeList = new ArrayList<>();
        for (Map.Entry<Integer, ArrayList<int[]>> entry : entryList) {
            nodeList.add(new Node(entry.getKey(), entry.getValue()));
        }
        java.util.Collections.sort(nodeList, Collections.reverseOrder(new NodeComparator()));
  
        int threshold = 25000;
        int prevRGB = -1;

        HashMap<Integer, ArrayList<int[]>> compressedMap = new HashMap<Integer, ArrayList<int[]>>();
        // Start the loop from the second element (index 1) to the end
        for (int i = 0; i < nodeList.size(); i++) {
            Node currentNode = nodeList.get(i);
            if (prevRGB == -1 || Math.abs(prevRGB - currentNode.getRGB()) >= threshold) {
                // Create a new mapping
                compressedMap.put(currentNode.getRGB(), currentNode.getValue());
                prevRGB = currentNode.getRGB();
            } else {
                // Add to previous one
                ArrayList<int[]> existingList = compressedMap.get(prevRGB);
                existingList.addAll(currentNode.getValue());
                // Setting prev hashMap to NONE to be garbage collected
                nodeList.get(i).coordinates = null;
            }
        }


        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFileName))) {
            oos.writeShort(pixels.length); // Write the original width
            oos.writeShort(pixels[0].length); // Write the original height

            // Loop thorugh
            for (Map.Entry<Integer, ArrayList<int[]>> entry : compressedMap.entrySet()) {
                // Write the rgb value as 3 bytes
                int[] rgbComponents = decompressRGB(entry.getKey());
                for (int i = 0; i < 3; i++) {
                    byte val = compressToByte(rgbComponents[i]);
                    // Write to thing
                    oos.writeByte(val);
                }
                oos.writeShort(entry.getValue().size()); // Write the size of the ArrayList
                for (int[] coord : entry.getValue()) {
                    oos.writeShort(coord[0]); // Write the x coordinate
                    oos.writeShort(coord[1]); // Write the y coordinate
                }
            }
        }
    }

    public int[][][] Decompress(String inputFileName) throws IOException, ClassNotFoundException {
        // The following is a bad implementation that we have intentionally put in the
        // function to make App.java run, you should
        // write code to reimplement the function without changing any of the input
        // parameters, and making sure that it returns
        // an int [][][]

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputFileName))) {
        short originalWidth = ois.readShort();  // Read the original width
        short originalHeight = ois.readShort();  // Read the original height

      // Read the size of the ArrayList
        int[][][] output = new int[originalWidth][originalHeight][3];
        
        while (true) {
                try {
                    int[] rgb = {
                        decompressFromByte(ois.readByte()),
                        decompressFromByte(ois.readByte()),
                        decompressFromByte(ois.readByte())
                    }; // Read the RGB value
                    short size = ois.readShort();  // Read the size of the ArrayList
                    for (short i = 0; i < size; i++) {
                        short x = ois.readShort();  // Read the x coordinate
                        short y = ois.readShort();  // Read the y coordinate
                        output[x][y] = rgb;
                    }
                } catch (EOFException e) {
                    break;
                }
        }
        return output;
    }
    }
}