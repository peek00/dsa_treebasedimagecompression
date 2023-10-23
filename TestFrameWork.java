import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import javax.imageio.ImageIO;

public class TestFrameWork{
    private String testResultFilePath;  
    private String testImageDirectory = "Original/"; // default value for the image directory 
    private String testName = "quadtree-pj-1";


    /* Constructors */
    public TestFrameWork(){

        // Initialize the TestResult file name with Date and Time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        this.testResultFilePath = "TestResult/TestResult_" + now.format(formatter) + ".log";

        // Create the TestResult
        File file = new File(testResultFilePath);
        file.getParentFile().mkdirs(); // Make sure the directory exists
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file: " + e.getMessage());
        }
    }

    public TestFrameWork(String ImageDirectory, String testName){
        this(); 
        this.testImageDirectory = ImageDirectory; // constructor chaining for specifying test image directories 
        this.testName = testName; 
    }

    public void writeToResult(String input){
        try (FileWriter fileWriter = new FileWriter(testResultFilePath, true); // second parameter 'true' enables append mode
    
            BufferedWriter bufferWriter = new BufferedWriter(fileWriter)) {
            
            bufferWriter.newLine();   // Move to a new line
            bufferWriter.write(input); // Write the input string

        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }
    
    public static double getAverage(List<? extends Number> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }

        double sum = 0;
        for (Number number : list) {
            sum += number.doubleValue();
        }
        return sum / list.size();
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException{

        //Create an instance of Utility
        Utility Utility = new Utility();

        // Create an instance of TestFramework 
        TestFrameWork testFrameWork = new TestFrameWork(); 

        //Define original file directory to loop through
        String ImageDirectory = testFrameWork.testImageDirectory; 
        
        
        // List all files in the directory
        File directory = new File(ImageDirectory);
        testFrameWork.writeToResult("Image Directory: " + ImageDirectory);

        /* Stores varies parameters for overall performance analysis*/
        ArrayList<Long> differenceInFileSizeList = new ArrayList<>(); 
        ArrayList<Double> compressionRateList = new ArrayList<>(); 
        ArrayList<Long> compressionTimeList = new ArrayList<>(); 
        ArrayList<Long> decompressionTimeList = new ArrayList<>(); 
        ArrayList<Double> maeList = new ArrayList<>(); 
        ArrayList<Double> mseList = new ArrayList<>(); 
        ArrayList<Double> psnrList = new ArrayList<>(); 


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
                    testFrameWork.writeToResult(String.format("Compressed File: %s", imageName));


                    // start compress timer
                    long compressStartTime = System.currentTimeMillis();
                    
                    //call compress function
                    Utility.Compress(pixelData, compressed_file_name);
                    
                    //end timer for compress and record the total time passed
                    long compressEndTime = System.currentTimeMillis();

                    long compressExecutionTime = compressEndTime - compressStartTime;

                    // add execution time to the list 
                    compressionTimeList.add(compressExecutionTime);
                    
                    // Execution Time Output 
                    String compressionExecutionTimeOutput = String.format("Compress Execution Time for %s : %d milliseconds", imageName, compressExecutionTime );
                    System.out.println(compressionExecutionTimeOutput);

                    // Write to test result 
                    testFrameWork.writeToResult("--------------------------------------------------------------------------------");
                    testFrameWork.writeToResult(compressionExecutionTimeOutput);
                    testFrameWork.writeToResult("--------------------------------------------------------------------------------");


                    //Check the original file size
                    File originalFile = new File(ImageDirectory + imageName);
                    long originalFileSize = originalFile.length();
                    
                    String originalSizeOutput = String.format("Size of the original file for %s : %d bytes", imageName, originalFileSize); 
                    System.out.println(originalSizeOutput);
                    
                    // Write to test result 
                    testFrameWork.writeToResult(originalSizeOutput);
                    
                    // Check size of the compressed file
                    File compressedFile = new File(compressed_file_name);
                    long compressedFileSize = compressedFile.length();

                    String compressedSizeOutput = String.format("Size of the compressed file for %s : %d bytes", imageName, compressedFileSize); 
                    System.out.println(compressedSizeOutput);
                    
                    // Write to test result 
                    testFrameWork.writeToResult(compressedSizeOutput);

                    
                    //Find the Difference
                    long differenceInFileSize = originalFileSize - compressedFileSize;
                    double compressionRate = 1- compressedFileSize / originalFileSize; 

                    // Add to the list 
                    differenceInFileSizeList.add(differenceInFileSize);
                    compressionRateList.add(compressionRate);


                    String differenceInFileSizeOutput = String.format("Bytes saved from compression of %s : %d bytes, with compression rate of: %.2f", imageName, differenceInFileSize, compressionRate); 

                    testFrameWork.writeToResult(differenceInFileSizeOutput);
                    testFrameWork.writeToResult("--------------------------------------------------------------------------------");

                    // start decompress timer
                    long decompressStartTime = System.currentTimeMillis();

                    // call decompress function
                    // Utility.Decompress(compressed_file_name);
                    pixelData = Utility.Decompress(compressed_file_name);
                    
                    //end timer for decompress and record the total time passed
                    long decompressEndTime = System.currentTimeMillis();
                    long decompressExecutionTime = decompressEndTime - decompressStartTime;

                    // Add to the list 
                    decompressionTimeList.add(decompressExecutionTime);

                    String decompressionExecutionOutput = String.format("Decompress Execution Time for  %s : %d milliseconds", imageName, decompressExecutionTime);
                    
                    // Write to test result 

                    testFrameWork.writeToResult(decompressionExecutionOutput);
                    testFrameWork.writeToResult("--------------------------------------------------------------------------------");

                    
                    //convert back to image for visualisation
                    PixeltoImageConverter PixeltoImageConverter = new PixeltoImageConverter(pixelData);
                    PixeltoImageConverter.saveImage("Decompressed/" + imageName, "png");

                    //Get the two bufferedimages for calculations
                    BufferedImage originalimage = ImageIO.read(new File(ImageDirectory + imageName));
                    BufferedImage decompressedimage = ImageIO.read(new File("Decompressed/" + imageName)); 

                    //calculate MAE
                    double MAE = MAECalculator.calculateMAE(originalimage, decompressedimage);
                    maeList.add(MAE);
                    String maeOutput = String.format("Mean Absolute Error of : %S is: %.2f",imageName ,MAE); 
                    System.out.println(maeOutput);
                    testFrameWork.writeToResult(maeOutput);

                    //calculate MSE
                    double MSE = MSECalculator.calculateMSE(originalimage, decompressedimage);
                    mseList.add(MSE);
                    System.out.println("Mean Squared Error of :" + imageName + " is " + MSE) ;
                    String mseOutput = String.format("Mean Squared Error of : %S is: %.2f",imageName ,MSE); 
                    System.out.println(mseOutput);
                    testFrameWork.writeToResult(mseOutput);

                    //calculate PSNR
                    double PSNR = PSNRCalculator.calculatePSNR(originalimage, decompressedimage);
                    psnrList.add(PSNR); 
                    System.out.println("PSNR of :" + imageName + " is " + PSNR);   
                    String psnrOutput = String.format("Mean Absolute Error of : %S is: %.2f",imageName ,PSNR); 
                    System.out.println(psnrOutput);
                    testFrameWork.writeToResult(psnrOutput);
                    testFrameWork.writeToResult("================================================================================");
                    System.out.println();
                }
            }
        }
        /* Process the summary Statistics */
        testFrameWork.processAverageStatistics(compressionTimeList, "compression time");
        testFrameWork.processAverageStatistics(differenceInFileSizeList, "difference in file size");
        testFrameWork.processAverageStatistics(compressionRateList, "compression rate");
        testFrameWork.processAverageStatistics(decompressionTimeList, "decompression");                
        testFrameWork.processAverageStatistics(maeList, "MAE");
        testFrameWork.processAverageStatistics(mseList, "MSE");
        testFrameWork.processAverageStatistics(psnrList, "PSNR");

    }

    public void processAverageStatistics(List<? extends Number> parameterList, String parameterName){
        double averageParameterValue = getAverage(parameterList); 
        String parameterOutput = String.format("The average %s is: %.2f for test: %s", parameterName, averageParameterValue, testName); 
        System.out.println(parameterOutput);
        writeToResult(parameterOutput);
    }
}

// import java.io.BufferedWriter;
// import java.io.File;
// import java.io.FileWriter;
// import java.io.IOException;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.awt.image.BufferedImage;
// import java.io.File;
// import java.io.IOException;
// import javax.imageio.ImageIO;

// import javax.imageio.ImageIO;

// public class TestFrameWork{
//     private String testResultFilePath;  
//     private String testImageDirectory = null; 


//     /* Constructors */
//     public TestFrameWork(){

//         // Initialize the TestResult file name with Date and Time
//         LocalDateTime now = LocalDateTime.now();
//         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
//         this.testResultFilePath = "TestResult/TestResult_" + now.format(formatter) + ".log";

//         // Create the TestResult
//         File file = new File(testResultFilePath);
//         file.getParentFile().mkdirs(); // Make sure the directory exists
//         try {
//             file.createNewFile();
//         } catch (IOException e) {
//             System.out.println("An error occurred while creating the file: " + e.getMessage());
//         }
//     }

//     public TestFrameWork(String ImageDirectory){
//         this(); 
//         this.testImageDirectory = ImageDirectory; // constructor chaining for specifying test image directories 
//     }

//     public void writeToResult(String input){
//         try (FileWriter fileWriter = new FileWriter(testResultFilePath, true); // second parameter 'true' enables append mode
    
//             BufferedWriter bufferWriter = new BufferedWriter(fileWriter)) {
            
//             bufferWriter.newLine();   // Move to a new line
//             bufferWriter.write(input); // Write the input string

//         } catch (IOException e) {
//             System.out.println("An error occurred while writing to the file: " + e.getMessage());
//         }
//     }


//     public static void main(String[] args) throws IOException, ClassNotFoundException{

//         //Create an instance of Utility
//         Utility Utility = new Utility();

//         // Create an instance of TestFramework 
//         TestFrameWork testFrameWork = new TestFrameWork(); 

//         //Define original file directory to loop through
//         String ImageDirectory; 
//         if (testFrameWork.testImageDirectory == null)
//             ImageDirectory = "Original/";
//         else
//             ImageDirectory = testFrameWork.testImageDirectory; 
        
        
//         // List all files in the directory
//         File directory = new File(ImageDirectory);
//         testFrameWork.writeToResult("Image Directory: " + ImageDirectory);


//         File[] files = directory.listFiles();


//         if (files != null) {
//             for (File file : files) {
//                 if (file.isFile()) {
//                     String imageName = file.getName();

//                     //Converting image to pixels

//                     ImagetoPixelConverter ImagetoPixelConverter = new ImagetoPixelConverter(ImageDirectory + imageName);

//                     //Converting the image to pixels

//                     int[][][] pixelData = ImagetoPixelConverter.getPixelData();
//                     int width = ImagetoPixelConverter.getWidth();
//                     int height = ImagetoPixelConverter.getHeight();

//                     for (int x = 0; x < width; x++) {
//                         for (int y = 0; y < height; y++) {
//                             int red = pixelData[x][y][0];
//                             int green = pixelData[x][y][1];
//                             int blue = pixelData[x][y][2];
//                         }
//                     }

//                     // Now you have the image data in 'pixelData' that will be taken in by Compress

//                     // Define location and name for the compressed file to be created
//                     String compressed_file_name = "Compressed/" + imageName.substring(0, imageName.lastIndexOf('.')) + ".bin";
//                     testFrameWork.writeToResult(String.format("Compressed File: %s", imageName));


//                     // start compress timer
//                     long compressStartTime = System.currentTimeMillis();
                    
//                     //call compress function
//                     Utility.Compress(pixelData, compressed_file_name);
                    
//                     //end timer for compress and record the total time passed
//                     long compressEndTime = System.currentTimeMillis();

//                     long compressExecutionTime = compressEndTime - compressStartTime;
                    
//                     // Execution Time Output 
//                     String compressionExecutionTimeOutput = String.format("Compress Execution Time for %s : %d milliseconds", imageName, compressExecutionTime );
//                     System.out.println(compressionExecutionTimeOutput);

//                     // Write to test result 
//                     testFrameWork.writeToResult("--------------------------------------------------------------------------------");
//                     testFrameWork.writeToResult(compressionExecutionTimeOutput);
//                     testFrameWork.writeToResult("--------------------------------------------------------------------------------");


//                     //Check the original file size
//                     File originalFile = new File(ImageDirectory + imageName);
//                     long originalFileSize = originalFile.length();
                    
//                     String originalSizeOutput = String.format("Size of the original file for %s : %d bytes", imageName, originalFileSize); 
//                     System.out.println(originalSizeOutput);
                    
//                     // Write to test result 
//                     testFrameWork.writeToResult(originalSizeOutput);
                    
//                     // Check size of the compressed file
//                     File compressedFile = new File(compressed_file_name);
//                     long compressedFileSize = compressedFile.length();

//                     String compressedSizeOutput = String.format("Size of the compressed file for %s : %d bytes", imageName, compressedFileSize); 
//                     System.out.println(compressedSizeOutput);
                    
//                     // Write to test result 
//                     testFrameWork.writeToResult(compressedSizeOutput);

                    
//                     //Find the Difference
//                     long differenceInFileSize = originalFileSize - compressedFileSize;
//                     double compressionRate = 1- compressedFileSize / originalFileSize; 

//                     String differenceInFileSizeOutput = String.format("Bytes saved from compression of %s : %d bytes, with compression rate of: %.2f", imageName, differenceInFileSize, compressionRate); 

//                     testFrameWork.writeToResult(differenceInFileSizeOutput);
//                     testFrameWork.writeToResult("--------------------------------------------------------------------------------");

//                     // start decompress timer
//                     long decompressStartTime = System.currentTimeMillis();

//                     // call decompress function
//                     // Utility.Decompress(compressed_file_name);
//                     pixelData = Utility.Decompress(compressed_file_name);
                    
//                     //end timer for decompress and record the total time passed
//                     long decompressEndTime = System.currentTimeMillis();
//                     long decompressExecutionTime = decompressEndTime - decompressStartTime;


//                     String decompressionExecutionOutput = String.format("Decompress Execution Time for  %s : %d milliseconds", imageName, decompressExecutionTime);
                    
//                     // Write to test result 

//                     testFrameWork.writeToResult(decompressionExecutionOutput);
//                     testFrameWork.writeToResult("--------------------------------------------------------------------------------");

                    
//                     //convert back to image for visualisation
//                     PixeltoImageConverter PixeltoImageConverter = new PixeltoImageConverter(pixelData);
//                     PixeltoImageConverter.saveImage("Decompressed/" + imageName, "png");

//                     //Get the two bufferedimages for calculations
//                     BufferedImage originalimage = ImageIO.read(new File(ImageDirectory + imageName));
//                     BufferedImage decompressedimage = ImageIO.read(new File("Decompressed/" + imageName)); 

//                     //calculate MAE
//                     double MAE = MAECalculator.calculateMAE(originalimage, decompressedimage);
//                     String maeOutput = String.format("Mean Absolute Error of : %S is: %.2f",imageName ,MAE); 
//                     System.out.println(maeOutput);
//                     testFrameWork.writeToResult(maeOutput);

//                     //calculate MSE
//                     double MSE = MSECalculator.calculateMSE(originalimage, decompressedimage);
//                     System.out.println("Mean Squared Error of :" + imageName + " is " + MSE) ;
//                     String mseOutput = String.format("Mean Squared Error of : %S is: %.2f",imageName ,MSE); 
//                     System.out.println(mseOutput);
//                     testFrameWork.writeToResult(mseOutput);

//                     //calculate PSNR
//                     double PSNR = PSNRCalculator.calculatePSNR(originalimage, decompressedimage);
//                     System.out.println("PSNR of :" + imageName + " is " + PSNR);   
//                     String psnrOutput = String.format("Mean Absolute Error of : %S is: %.2f",imageName ,PSNR); 
//                     System.out.println(psnrOutput);
//                     testFrameWork.writeToResult(psnrOutput);
//                     testFrameWork.writeToResult("================================================================================");

//                 }
//             }
//         }

//     }
// }