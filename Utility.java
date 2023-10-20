import java.io.*;

public class Utility {

    private static final int THRESHOLD = 15;

    private QuadtreeNode buildQuadtree(int[][][] pixels, int x, int y, int size) {
        if (size == 1) {
            return new QuadtreeNode(x, y, size, calculateAverageColor(pixels, x, y, size));
        }
        
        int[] avgColor = calculateAverageColor(pixels, x, y, size);
        if (colorDifferenceWithinThreshold(pixels, x, y, size, avgColor)) {
            return new QuadtreeNode(x, y, size, avgColor);
        }
        
        int newSize = size / 2;
        return new QuadtreeNode(x, y, size,
            buildQuadtree(pixels, x, y, newSize),
            buildQuadtree(pixels, x + newSize, y, newSize),
            buildQuadtree(pixels, x, y + newSize, newSize),
            buildQuadtree(pixels, x + newSize, y + newSize, newSize)
        );
    }

    private int[] calculateAverageColor(int[][][] pixels, int x, int y, int size) {
        long sumR = 0, sumG = 0, sumB = 0;
        int maxX = Math.min(x + size, pixels.length);
        int maxY = Math.min(y + size, pixels[0].length);
        for (int i = x; i < maxX; i++) {
            for (int j = y; j < maxY; j++) {
                sumR += pixels[i][j][0];
                sumG += pixels[i][j][1];
                sumB += pixels[i][j][2];
            }
        }
        int totalPixels = size * size;
        return new int[]{(int) (sumR / totalPixels), (int) (sumG / totalPixels), (int) (sumB / totalPixels)};
    }

    // Simplified this function by removing unnecessary conditions and optimized early exit
    private boolean colorDifferenceWithinThreshold(int[][][] pixels, int x, int y, int size, int[] averageColor) {
        int maxX = Math.min(x + size, pixels.length);
        int maxY = Math.min(y + size, pixels[0].length);
        int countExceedingThreshold = 0;
        int allowedExceedingThreshold = size * size / 20; // Allow up to 5% of the pixels to exceed the threshold
        for (int i = x; i < maxX; i++) {
            for (int j = y; j < maxY; j++) {
                int diff = Math.abs(pixels[i][j][0] - averageColor[0]) + 
                           Math.abs(pixels[i][j][1] - averageColor[1]) + 
                           Math.abs(pixels[i][j][2] - averageColor[2]);
                if (diff > 3 * THRESHOLD) {
                    countExceedingThreshold++;
                    if (countExceedingThreshold > allowedExceedingThreshold) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void Compress(int[][][] pixels, String outputFileName) throws IOException {
        if(pixels == null || pixels.length == 0 || pixels[0].length == 0) {
            throw new IllegalArgumentException("Invalid pixel data provided.");
        }
    
        int maxSize = Math.max(pixels.length, pixels[0].length); // Handle non-square images
        QuadtreeNode root = buildQuadtree(pixels, 0, 0, maxSize);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFileName))) {
            oos.writeInt(pixels.length);  // Write the original width
            oos.writeInt(pixels[0].length);  // Write the original height
            oos.writeInt(maxSize);  // Write the maxSize
            writeQuadtree(oos, root);
        }
    }
    
    private void writeQuadtree(ObjectOutputStream oos, QuadtreeNode node) throws IOException {
        if (node == null) {
            oos.writeBoolean(false);
            return;
        }
        
        oos.writeBoolean(true);

        if (node.color != null) {
            oos.writeBoolean(true);
            oos.writeInt(colorToInt(node.color)); // Using our optimized color representation
        } else {
            oos.writeBoolean(false);
            for (int i = 0; i < 4; i++) {
                writeQuadtree(oos, node.children[i]);
            }
        }
    }
    
    private int colorToInt(int[] color) {
        return (color[0] << 16) | (color[1] << 8) | color[2];
    }

    private int[] intToColor(int rgb) {
        return new int[]{(rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF};
    }
    
    public int[][][] Decompress(String inputFileName) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputFileName))) {
            int originalWidth = ois.readInt();
            int originalHeight = ois.readInt();
            int size = ois.readInt();
            QuadtreeNode root = readQuadtree(ois, 0, 0, size);
            int[][][] pixels = new int[originalWidth][originalHeight][3];
            reconstructImage(pixels, root);
    
            if(pixels.length == 0 || pixels[0].length == 0) {
                throw new IllegalArgumentException("Decompression resulted in invalid pixel data.");
            }
    
            return pixels;
        }
    }
    
    private QuadtreeNode readQuadtree(ObjectInputStream ois, int x, int y, int size) throws IOException {
        boolean exists = ois.readBoolean();
        
        if (!exists) {
            return null;
        }
        
        boolean isLeaf = ois.readBoolean();
    
        QuadtreeNode node;
        
        if (isLeaf) {
            node = new QuadtreeNode(x, y, size, intToColor(ois.readInt()));
        } else {
            int newSize = size / 2;
            QuadtreeNode topLeft = readQuadtree(ois, x, y, newSize);
            QuadtreeNode topRight = readQuadtree(ois, x + newSize, y, newSize);
            QuadtreeNode bottomLeft = readQuadtree(ois, x, y + newSize, newSize);
            QuadtreeNode bottomRight = readQuadtree(ois, x + newSize, y + newSize, newSize);
            node = new QuadtreeNode(x, y, size, topLeft, topRight, bottomLeft, bottomRight);
        }
        return node;
    }
    
    private void reconstructImage(int[][][] pixels, QuadtreeNode node) {
        if (node == null) {
            return;  // Return immediately if the node is null
        }
        
        if (node.color != null) {
            for (int i = node.x; i < node.x + node.size && i < pixels.length; i++) {
                for (int j = node.y; j < node.y + node.size && j < pixels[i].length; j++) {
                    pixels[i][j] = node.color;
                }
            }
        } else {
            for (int i = 0; i < 4; i++) {
                reconstructImage(pixels, node.children[i]);
            }
        }
    }
}