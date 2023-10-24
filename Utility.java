import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

class Node{
	int x, y;
    int width, height;
    int[] avgColor;
    /*
        children[0] = upper left quadrant or north-west
        children[1] = upper right quadrant or north-east
        children[2] = lower left quadrant or south-west
        children[3] = lower right quadrant or south-east
    */ 
    boolean isLeaf = false;
    Node[] children; 

    //To decompress leaf nodes
    Node(int x, int y, int width, int height, int[] color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.avgColor = color;
    }

    Node(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isLeaf = false;
    }


	Node(int x, int y, int width, int height, int[][][] pixels, float threshold) {
		this.x = x; 
		this.y = y;
        

        this.width=width;
        this.height=height;
        

        if(width/2 <= 2 || height /2 <= 2){
            avgColor = averageColor(x, y, width, height, pixels);
            // avgColor=new int[3];
            // avgColor[0]=255;
            // avgColor[1]=255;
            // avgColor[2]=255;
            isLeaf = true;
        }
        else if(measureDetail(x,y,width,height,pixels)<threshold){
            avgColor = averageColor(x, y, width, height, pixels);
            // avgColor=new int[3];
            // avgColor[0]=255;
            // avgColor[1]=255;
            // avgColor[2]=255;
            isLeaf = true;
        }
        else{
            double width_divided = Math.round((double) width/2);
            double height_divided = Math.round((double) height/2);
            // System.out.println("width_divided: "+width_divided+" height_divided: "+height_divided);
            // System.out.println("_________________________________");
            int widthConverted = (int) width_divided;
            int heightConverted = (int) height_divided;
            children = new Node[4];
            // upper left quadrant or north-west
            children[0] = new Node(x,y,widthConverted,heightConverted,pixels,threshold);
            // upper right quadrant or north-east
            children[1] = new Node(x+widthConverted,y,widthConverted,heightConverted,pixels,threshold);
            // lower left quadrant or south-west
            children[2] = new Node(x,y+heightConverted,widthConverted,height-heightConverted,pixels,threshold);
            // lower right quadrant or south-east
            children[3] = new Node(x+widthConverted,y+heightConverted,width-widthConverted,height-heightConverted,pixels,threshold);
        }
	}

    public int[] averageColor(int x, int y, int width, int height, int[][][] pixels){
        // System.out.println("x: "+x+" y: "+y+" width: "+width+" height: "+height);
        int red = 0;
        int green = 0;
        int blue = 0;
        for(int i = x; i < x + width; i++){
            for(int j = y; j < y + height; j++){
                if(i>=pixels.length && j>=pixels[0].length){
                    red+=pixels[pixels.length-1][pixels[0].length-1][0];
                    green+=pixels[pixels.length-1][pixels[0].length-1][1];
                    blue+=pixels[pixels.length-1][pixels[0].length-1][2];

                }else if(i>=pixels.length){
                    red+=pixels[pixels.length-1][j][0];
                    green+=pixels[pixels.length-1][j][1];
                    blue+=pixels[pixels.length-1][j][2];

                }else if(j>=pixels[0].length){
                    red+=pixels[i][pixels[0].length-1][0];
                    green+=pixels[i][pixels[0].length-1][1];
                    blue+=pixels[i][pixels[0].length-1][2];
    
                }else{
                    red += pixels[i][j][0];
                    green += pixels[i][j][1];
                    blue += pixels[i][j][2];
                }
            }
        }
        //number of pixels evaluated
        int area = width * height;
        int[] average = new int[3];
        average[0] = red/area;
        average[1] = green/area;
        average[2] = blue/area;
        return average;        
    }

    public int[] maxColor(int x, int y, int width, int height, int[][][] pixels){
        int[] average = new int[3];
        int maxPixel = pixels[x][y][0]+pixels[x][y][1]+pixels[x][y][2];
        for(int i = x; i < x + width; i++){
            for(int j = y; j < y + height; j++){
                int maxColor = pixels[i][j][0]+pixels[i][j][1]+pixels[i][j][2];
                if(maxPixel<maxColor){
                    maxPixel = maxColor;
                    average[0] = pixels[i][j][0];
                    average[1] = pixels[i][j][1];
                    average[2] = pixels[i][j][2];
                }
            }
        }
        return average;        
    }

    /* Measures the amount of detail of a rectangular grid*/
    public int measureDetail(int x, int y, int width, int height, int[][][] pixels){
        int[] average = averageColor(x, y, width, height, pixels);
        int red = average[0];
        int green = average[1];
        int blue = average[2];

        long colorSum = 0;
        /*
         * Iterate through the pixels in the region and calculate the squared
         * difference between the average color and the color of each pixel.
         * Accumulate the squared differences in the variable colorSum.
         */
        for(int i = x; i < x + width; i++){
            for(int j = y; j < y + height; j++){
                if(i>=pixels.length && j>=pixels[0].length){
                    red+=pixels[i][pixels[0].length-1][0];
                    green+=pixels[i][pixels[0].length-1][1];
                    blue+=pixels[i][pixels[0].length-1][2];
                    continue;
                }else if(i>=pixels.length){
                    red+=pixels[pixels.length-1][j][0];
                    green+=pixels[pixels.length-1][j][1];
                    blue+=pixels[pixels.length-1][j][2];
                    continue;
                }else if(j>=pixels[0].length){
                    red+=pixels[i][pixels[0].length-1][0];
                    green+=pixels[i][pixels[0].length-1][1];
                    blue+=pixels[i][pixels[0].length-1][2];
                    continue;
                }
                int pixelRed = pixels[i][j][0];
                int pixelGreen = pixels[i][j][1];
                int pixelBlue = pixels[i][j][2];
                
                // Calculate squared differences for each channel
                int diffRed = pixelRed - red;
                int diffGreen = pixelGreen - green;
                int diffBlue = pixelBlue - blue;

            // Accumulate the squared differences for each channel
                
                colorSum += (long)(diffRed * diffRed + diffGreen * diffGreen + diffBlue * diffBlue);
            }
        }

    // Calculate the standard deviation for each channel
        
        double stdDeviation = Math.sqrt((double) colorSum / (width * height));

        // Weight the standard deviation by the number of pixels in the region
        double weightedDetail = stdDeviation * width * height;
        return (int) weightedDetail;
        }
    

    public Node getChild(Node e, int i){
        if(e.children == null){
            return null;
        }
        return e.children[i];
    }

   
}

class Quadtree {
    Node root;

    public Quadtree(int x, int y, int width, int height, int[][][] pixels,int threshold){
        //Base case in node class
        root = new Node(0,0,width,height,pixels,threshold);                
    }

    public void printTree(Node node) {
        // Print information about the current node
        System.out.println("Node at (" + node.x + ", " + node.y + ") with dimensions (" + node.width + " x " + node.height + ")");

        // If the node has children, recursively print them
        if (node.children != null) {
            for (Node child : node.children) {
                printTree(child);
            }
        }
    }

    public static byte[] encodeQuadTree(Node node) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        encodeNodeRecursive(node, byteStream);
        return byteStream.toByteArray();
    }

    private static void encodeNodeRecursive(Node node, ByteArrayOutputStream byteStream) {
        try {
            byteStream.write(node.isLeaf ? 1 : 0);
            byteStream.write(ByteBuffer.allocate(4).putInt(node.x).array());
            byteStream.write(ByteBuffer.allocate(4).putInt(node.y).array());
            byteStream.write(ByteBuffer.allocate(4).putInt(node.width).array());
            byteStream.write(ByteBuffer.allocate(4).putInt(node.height).array());
            if (node.isLeaf) {
                for (int color : node.avgColor) {
                    byteStream.write(color);
                }
            } else {
                for (Node child : node.children) {
                    encodeNodeRecursive(child, byteStream);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

      public static Node decodeQuadTree(String fileName) {
        try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
            // Read the binary data from the file
            byte[] binaryData = new byte[fileInputStream.available()];
            fileInputStream.read(binaryData);

            // Initialize a ByteArrayInputStream for decoding
            ByteArrayInputStream byteStream = new ByteArrayInputStream(binaryData);

            // Decode the quadtree recursively
            return decodeNodeRecursive(byteStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Recursively decode a node and its children
    private static Node decodeNodeRecursive(ByteArrayInputStream byteStream) {
        try {
            int isLeaf = byteStream.read();
            int x = ByteBuffer.wrap(byteStream.readNBytes(4)).getInt();
            int y = ByteBuffer.wrap(byteStream.readNBytes(4)).getInt();
            int width = ByteBuffer.wrap(byteStream.readNBytes(4)).getInt();
            int height = ByteBuffer.wrap(byteStream.readNBytes(4)).getInt();

            Node node;
            if (isLeaf == 1) {
                int[] avgColor = new int[3];
                avgColor[0] = byteStream.read();
                avgColor[1] = byteStream.read();
                avgColor[2] = byteStream.read();
                node = new Node(x, y, width, height, avgColor);
            } else {
                node = new Node(x, y, width, height);
                node.children = new Node[4];
                for (int i = 0; i < 4; i++) {
                    node.children[i] = decodeNodeRecursive(byteStream);
                }
            }

            return node;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

public class Utility {

    public void Compress(int[][][] pixels, String outputFileName) throws IOException {
        // The following is a bad implementation that we have intentionally put in the function to make App.java run, you should 
        // write code to reimplement the function without changing any of the input parameters, and making sure the compressed file
        // gets written into outputFileName
        String fileName = outputFileName.substring(outputFileName.lastIndexOf('/') + 1, outputFileName.lastIndexOf('.'));
        System.out.println(fileName);
        File file = new File("Original/" +fileName+".png");
        if (file.exists()) {
            System.out.println("The file exists.");
        } else {
            System.out.println("The file does not exist.");
        }
        BufferedImage image = ImageIO.read(file);
        int width = image.getWidth();
        int height = image.getHeight();
        System.out.println("Image Width: " + width);
        System.out.println("Image Height: " + height);

        Quadtree tree = new Quadtree(0,0,width,height,pixels,50);
        byte[] binaryData = Quadtree.encodeQuadTree(tree.root);
        try(FileOutputStream fos = new FileOutputStream(outputFileName)){
            fos.write(binaryData);
        }catch(IOException e){
            e.printStackTrace();
        }        
    }

    public int[][][] Decompress(String inputFileName) throws IOException, ClassNotFoundException {
        Node root = Quadtree.decodeQuadTree(inputFileName);
        
        int[][][] pixels = new int[root.width][root.height][3];
        decompressTree(root, pixels);
        return pixels;
        
    }
    // }
    
    // Add a recursive method to decompress the tree
    private void decompressTree(Node node, int[][][] pixels) {
        if (node.children == null) {
            for (int i = node.x; i < node.x + node.width; i++) {
                for (int j = node.y; j < node.y + node.height; j++) {
                   if(i>=pixels.length || j>=pixels[0].length){
                    continue;
                }
                    pixels[i][j][0] = node.avgColor[0];
                    pixels[i][j][1] = node.avgColor[1];
                    pixels[i][j][2] = node.avgColor[2];
                }
            }
        } else {
            // Recursively decompress children
            for (int i = 0; i < 4; i++) {
                decompressTree(node.children[i], pixels);
            }
        }
    }
    


}

