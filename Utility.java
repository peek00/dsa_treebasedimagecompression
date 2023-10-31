import java.io.*;

public class Utility {

    private int QUADTREE_THRESHOLD = 100;
    private double ALLOWED_EXCEEDING_THRESHOLD_FACTOR = 0.001; 

    public void setQuadTreeThreshold(int inputThreshold){
        this.QUADTREE_THRESHOLD = inputThreshold; 
    }

    public void setAllowedExceedingThresholdFactor(double inputFactor){
        this.ALLOWED_EXCEEDING_THRESHOLD_FACTOR = inputFactor; 
    }

    public class QuadtreeNode {
        int x, y; 
        int size; 
        int[] color; 
        QuadtreeNode[] children; 
    
        public QuadtreeNode(int x, int y, int size) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.color = null;
            this.children = null;
        }
    }

    private QuadtreeNode buildQuadtree(int[][][] pixels, int x, int y, int size) {
        if (x < 0 || y < 0 || x >= pixels.length || y >= pixels[0].length || size <= 0) {
            return null;
        }
        
        QuadtreeNode node = new QuadtreeNode(x, y, size);
    
        // Base case: if size is 1, then create a leaf node with the color of that pixel.
        if (size == 1) {
            node.color = pixels[x][y];
            return node;
        }
        
        int[] avgColor = calculateAverageColor(pixels, x, y, size);
        if (colorDifferenceWithinThreshold(pixels, x, y, size, avgColor)) {
            node.color = avgColor;
        } else {
            // int newSize = size / 2;
            int newSize = Math.round((float) size / 2);
            node.children = new QuadtreeNode[4];
            node.children[0] = buildQuadtree(pixels, x, y, newSize); // top-left quadrant
            node.children[1] = buildQuadtree(pixels, x + newSize, y, newSize); // top-right quadrant
            node.children[2] = buildQuadtree(pixels, x, y + newSize, newSize); // bottom-left quadrant
            node.children[3] = buildQuadtree(pixels, x + newSize, y + newSize, newSize); // bottom-right quadrant
        }
    
        return node;
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
    
        int totalPixels = (maxX - x) * (maxY - y);
        if (totalPixels == 0) {
            return new int[]{0, 0, 0}; // Return black if no pixels to average
        }
        return new int[]{(int) (sumR / totalPixels), (int) (sumG / totalPixels), (int) (sumB / totalPixels)};        
    }

    private boolean colorDifferenceWithinThreshold(int[][][] pixels, int x, int y, int size, int[] averageColor) {
        if (x < 0 || y < 0 || x >= pixels.length || y >= pixels[0].length) {
            return false;
        }
        
        int red = averageColor[0];
        int green = averageColor[1];
        int blue = averageColor[2];
        
        int maxX = Math.min(x + size, pixels.length);
        int maxY = Math.min(y + size, pixels[0].length);
    
        int countExceedingThreshold = 0;
        int allowedExceedingThreshold = (int) (ALLOWED_EXCEEDING_THRESHOLD_FACTOR * size * size); // Allow up to 10% of the pixels to exceed the threshold
        
        for (int i = x; i < maxX; i++) {
            for (int j = y; j < maxY; j++) {
                int diffRed = Math.abs(pixels[i][j][0] - red);
                int diffGreen = Math.abs(pixels[i][j][1] - green);
                int diffBlue = Math.abs(pixels[i][j][2] - blue);
                
                if (diffRed > QUADTREE_THRESHOLD || diffGreen > QUADTREE_THRESHOLD || diffBlue > QUADTREE_THRESHOLD) {
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
            oos.writeInt(pixels.length);    // Save the width
            oos.writeInt(pixels[0].length); // Save the height
            oos.writeInt(maxSize);          // Write the max size (which is still needed to build the quadtree)
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
            reconstructImage(pixels, root, originalWidth, originalHeight);
    
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
    
        QuadtreeNode node = new QuadtreeNode(x, y, size);
        
        if (isLeaf) {
            node.color = intToColor(ois.readInt());
        } else {
            // int newSize = size / 2;
            int newSize = Math.round((float) size / 2);
            node.children = new QuadtreeNode[4];
            node.children[0] = readQuadtree(ois, x, y, newSize);
            node.children[1] = readQuadtree(ois, x + newSize, y, newSize);
            node.children[2] = readQuadtree(ois, x, y + newSize, newSize);
            node.children[3] = readQuadtree(ois, x + newSize, y + newSize, newSize);
        }
        return node;
    }
    
    private void reconstructImage(int[][][] pixels, QuadtreeNode node, int originalWidth, int originalHeight) {
        if (node == null) {
            return;  // Return immediately if the node is null
        }
        
        if (node.color != null) {
            for (int i = node.x; i < node.x + node.size && i < originalWidth; i++) {
                for (int j = node.y; j < node.y + node.size && j < originalHeight; j++) {
                    pixels[i][j] = node.color;
                }
            }
        } else {
            for (int i = 0; i < 4; i++) {
                reconstructImage(pixels, node.children[i], originalWidth, originalHeight);
            }
        }
    }
    

}
