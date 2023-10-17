import java.io.*;

public class Utility {

    public void Compress(int[][][] pixels, String outputFileName) throws IOException {
        // The following is a bad implementation that we have intentionally put in the function to make App.java run, you should 
        // write code to reimplement the function without changing any of the input parameters, and making sure the compressed file
        // gets written into outputFileName
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