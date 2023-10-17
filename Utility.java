import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

class PixelCoordinate{
    int x;
    int y;

    public PixelCoordinate(int x, int y){
        // 0 for red, 1 for green, 2 for blue
        this.x = x;
        this.y = y;
    }
}


public class Utility {

    public static int compressRGB(int r, int g, int b) {
        return (r << 16) | (g << 8) | b;
    }

    public static int[] decompressRGB(int rgb) {
        int[] rgbComponents = new int[3];
        rgbComponents[0] = (rgb >> 16) & 0xFF; // Red component
        rgbComponents[1] = (rgb >> 8) & 0xFF;  // Green component
        rgbComponents[2] = rgb & 0xFF;         // Blue component
        return rgbComponents;
    }



    public void Compress(int[][][] pixels, String outputFileName) throws IOException {
        // The following is a bad implementation that we have intentionally put in the function to make App.java run, you should 
        // write code to reimplement the function without changing any of the input parameters, and making sure the compressed file
        // gets written into outputFileName

        // Create a hashmap of each pixel values (regardless of rgb)
        // pixel_value { (0,0,0) }
        //

        HashMap<Integer, ArrayList<PixelCoordinate>> pixelMap = new HashMap<Integer, ArrayList<PixelCoordinate>>();
        int width = pixels.length;
        int height = pixels[0].length;

        for (int x=0; x<width; x++){
            for (int y=0; y<height ; y++){
                PixelCoordinate r = new PixelCoordinate(x, y);
                PixelCoordinate g = new PixelCoordinate(x, y);
                PixelCoordinate b = new PixelCoordinate(x, y);

            }
        }


        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFileName))) { 
            oos.writeObject(pixels); 
        }
    }

    public int[][][] Decompress(String inputFileName) throws IOException, ClassNotFoundException {
        // The following is a bad implementation that we have intentionally put in the function to make App.java run, you should 
        // write code to reimplement the function without changing any of the input parameters, and making sure that it returns
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