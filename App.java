import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class App {
    public static void main(String[] args) throws IOException, ClassNotFoundException{

        //Create an instance of Utility
        Utility Utility = new Utility();

        //Define original file directory to loop through
        String ImageDirectory = "Original/";

        // List all files in the directory
        File directory = new File(ImageDirectory);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String imageName = file.getName();

                    //Converting image to pixels

                    ImagetoPixelConverter ImagetoPixelConverter = new ImagetoPixelConverter(ImageDirectory + imageName);

                    //Converting the image to pixels

                    int[][][] pixelData = ImagetoPixelConverter.getPixelData();
                    int width = ImagetoPixelConverter.getWidth();
                    int height = ImagetoPixelConverter.getHeight();

                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            int red = pixelData[x][y][0];
                            int green = pixelData[x][y][1];
                            int blue = pixelData[x][y][2];
                        }
                    }

                    // Now you have the image data in 'pixelData' that will be taken in by Compress

                    // Define location and name for the compressed file to be created
                    String compressed_file_name = "Compressed/" + imageName.substring(0, imageName.lastIndexOf('.')) + ".bin";

                    // start compress timer
                    long compressStartTime = System.currentTimeMillis();
                    
                    //call compress function
                    Utility.Compress(pixelData, compressed_file_name);
                    
                    //end timer for compress and record the total time passed
                    long compressEndTime = System.currentTimeMillis();
                    long compressExecutionTime = compressEndTime - compressStartTime;
                    System.out.println("Compress Execution Time for "+ imageName + " : " + compressExecutionTime + " milliseconds");

                    //Check the original file size
                    File originalFile = new File(ImageDirectory + imageName);
                    long originalFileSize = originalFile.length();
                    System.out.println("Size of the original file for " + imageName + ": " + originalFileSize + " bytes"); 
                    
                    // Check size of the compressed file
                    File compressedFile = new File(compressed_file_name);
                    long compressedFileSize = compressedFile.length();
                    System.out.println("Size of the compressed file for " + imageName + ": " + compressedFileSize + " bytes"); 
                    
                    //Find the Difference
                    long differenceInFileSize = originalFileSize - compressedFileSize;
                    System.out.println("Bytes saved from compression of " + imageName + ": " + differenceInFileSize + " bytes"); 

                    // start decompress timer
                    long decompressStartTime = System.currentTimeMillis();

                    // call decompress function
                    int [][][] newPixelData = Utility.Decompress(compressed_file_name);
                    
                    //end timer for decompress and record the total time passed
                    long decompressEndTime = System.currentTimeMillis();
                    long decompressExecutionTime = decompressEndTime - decompressStartTime;
                    System.out.println("Decompress Execution Time for " + imageName + " : " + decompressExecutionTime + " milliseconds");
                    

                    //convert back to image for visualisation
                    PixeltoImageConverter PixeltoImageConverter = new PixeltoImageConverter(newPixelData);
                    PixeltoImageConverter.saveImage("Decompressed/" + imageName, "png");

                    //Get the two bufferedimages for calculations
                    BufferedImage originalimage = ImageIO.read(new File(ImageDirectory + imageName));
                    BufferedImage decompressedimage = ImageIO.read(new File("Decompressed/" + imageName)); 

                    //calculate MAE
                    double MAE = MAECalculator.calculateMAE(originalimage, decompressedimage);
                    System.out.println("Mean Absolute Error of :" + imageName + " is " + MAE) ;

                    //calculate MSE
                    double MSE = MSECalculator.calculateMSE(originalimage, decompressedimage);
                    System.out.println("Mean Squared Error of :" + imageName + " is " + MSE) ;                  

                    //calculate PSNR
                    double PSNR = PSNRCalculator.calculatePSNR(originalimage, decompressedimage);
                    System.out.println("PSNR of :" + imageName + " is " + PSNR);   

                }
            }
        }

    }
}


        
