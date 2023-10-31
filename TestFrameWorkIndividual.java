import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class TestFrameWorkIndividual{
    private String testResultFilePath;  
    private String testImageDirectory = "Original/"; // default value for the image directory 
    private String testID =""; 

    
    /* Constructors */
    public TestFrameWorkIndividual(){

        // Initialize the TestResult file name with Date and Time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        this.testResultFilePath = "TestResult/TestResult_" + now.format(formatter) + ".log";
        // Initialize the testID for data analysis 
        this.testID = "test_"+ now.format(formatter); 

        // Create the TestResult
        File file = new File(testResultFilePath);
        file.getParentFile().mkdirs(); // Make sure the directory exists
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file: " + e.getMessage());
        }
    }

    public TestFrameWorkIndividual(String ImageDirectory){
        this(); 
        this.testImageDirectory = ImageDirectory; // constructor chaining for specifying test image directories 
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

    //TIMOTHY'S CODE TO COUNT UNIQUE PIXEL COUNTS
    private long uniquePixelCount(int[][][] pixels) {
        long _uniquePixelCount = 0;
        Set<String> uniqueColors = new HashSet<>();

        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                int r = pixels[i][j][0];
                int g = pixels[i][j][1];
                int b = pixels[i][j][2];
                String colorKey = r + "," + g + "," + b;
                uniqueColors.add(colorKey);
            }
        }
        for (String c : uniqueColors) {
            _uniquePixelCount++;
        }
        return _uniquePixelCount;
    }
    

    public void test(int quadtreeThreshold, double allowedExceedingThresholdFactor) throws IOException, ClassNotFoundException {
        //Create an instance of Utility
        Utility utilityObject = new Utility();

        // Update the Test Parameters 
        utilityObject.setQuadTreeThreshold(quadtreeThreshold);
        utilityObject.setAllowedExceedingThresholdFactor(allowedExceedingThresholdFactor);

        // Create an instance of TestFramework 
        TestFrameWorkIndividual testFrameWorkIndividual = new TestFrameWorkIndividual(); 

        //Define original file directory to loop through
        String ImageDirectory = testFrameWorkIndividual.testImageDirectory; 

        // Stores the compression stats for each image in following format 
        ArrayList<String[]> compressionDataTable = new ArrayList<>(); 
        
        // List all files in the directory
        File directory = new File(ImageDirectory);
        File[] files = directory.listFiles();

        testFrameWorkIndividual.writeToResult("testID: " + testFrameWorkIndividual.testID);
        testFrameWorkIndividual.writeToResult("quadtreeThreshold: " + quadtreeThreshold);
        testFrameWorkIndividual.writeToResult("allowedExceedingThreshold: " + allowedExceedingThresholdFactor);
        testFrameWorkIndividual.writeToResult("Image Directory: " + ImageDirectory);


        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String imageName = file.getName();

                    //Converting image to pixels
                    ImagetoPixelConverter ImagetoPixelConverter = new ImagetoPixelConverter(ImageDirectory + imageName);

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
                    testFrameWorkIndividual.writeToResult(String.format("Compressed File: %s", imageName));

                    // start compress timer
                    long compressStartTime = System.currentTimeMillis();
                    //call compress function
                    utilityObject.Compress(pixelData, compressed_file_name);
                    /**
                     * Please note that the threshold has been exposed here 
                     */
                    //end timer for compress and record the total time passed
                    long compressEndTime = System.currentTimeMillis();
                    long compressExecutionTime = compressEndTime - compressStartTime;
                    // Execution Time Output 
                    String compressionExecutionTimeOutput = String.format("Compress Execution Time for %s : %d milliseconds", imageName, compressExecutionTime );
                    System.out.println(compressionExecutionTimeOutput);
                    // Write to test result 
                    testFrameWorkIndividual.writeToResult("--------------------------------------------------------------------------------");
                    testFrameWorkIndividual.writeToResult(compressionExecutionTimeOutput);
                    testFrameWorkIndividual.writeToResult("--------------------------------------------------------------------------------");


                    //Check the original file size
                    File originalFile = new File(ImageDirectory + imageName);
                    long originalFileSize = originalFile.length();
                    String originalSizeOutput = String.format("Size of the original file for %s : %d bytes", imageName, originalFileSize); 
                    System.out.println(originalSizeOutput);
                    // Write to test result 
                    testFrameWorkIndividual.writeToResult(originalSizeOutput);


                    // Check size of the compressed file
                    File compressedFile = new File(compressed_file_name);
                    long compressedFileSize = compressedFile.length();
                    String compressedSizeOutput = String.format("Size of the compressed file for %s : %d bytes", imageName, compressedFileSize); 
                    System.out.println(compressedSizeOutput);
                    // Write to test result 
                    testFrameWorkIndividual.writeToResult(compressedSizeOutput);

                    
                    //Find the Difference in file size 
                    long differenceInFileSize = originalFileSize - compressedFileSize;
                    double compressionRate = 1.0 - compressedFileSize / (double) originalFileSize; // casting is necessary to preserve the accuracy 
                    String differenceInFileSizeOutput = String.format("Bytes saved from compression of %s : %d bytes", imageName, differenceInFileSize); 
                    String compressionRateOutput = String.format("Compression Rate (1 - compressedFileSize/originalFileSize) for %s : %.2f", imageName, compressionRate);
                    System.out.println(differenceInFileSizeOutput);
                    System.out.println(compressionRateOutput);
                    testFrameWorkIndividual.writeToResult(differenceInFileSizeOutput);
                    testFrameWorkIndividual.writeToResult(compressionRateOutput);
                    testFrameWorkIndividual.writeToResult("--------------------------------------------------------------------------------");

                    // start decompress timer
                    long decompressStartTime = System.currentTimeMillis();

                    // call decompress function
                    int[][][] newPixelData = utilityObject.Decompress(compressed_file_name);
                    
                    //end timer for decompress and record the total time passed
                    long decompressEndTime = System.currentTimeMillis();
                    long decompressExecutionTime = decompressEndTime - decompressStartTime;
                    String decompressionExecutionOutput = String.format("Decompress Execution Time for  %s : %d milliseconds", imageName, decompressExecutionTime);
                    System.out.println(decompressionExecutionOutput);
                    // Write to test result 
                    testFrameWorkIndividual.writeToResult(decompressionExecutionOutput);
                    testFrameWorkIndividual.writeToResult("--------------------------------------------------------------------------------");

                    // count the number of unique pixel before compression and after decompression 
                    // count the number of unique pixel after decompression 
                    long uniquePixelCountBefore = uniquePixelCount(pixelData); 
                    long uniquePixelCountAfter = uniquePixelCount(newPixelData); 
                    
                    String uniquePixelCountBeforeOutput = String.format("Unique pixel count before compression: %d", uniquePixelCountBefore);
                    String uniquePixelCountAfterOutput = String.format("Unique pixel count after compression: %d", uniquePixelCountAfter);
                    
                    System.out.println(uniquePixelCountBeforeOutput);
                    System.out.println(uniquePixelCountAfterOutput);
                    
                    testFrameWorkIndividual.writeToResult(uniquePixelCountBeforeOutput);
                    testFrameWorkIndividual.writeToResult(uniquePixelCountAfterOutput);


                    long uniquePixelCountChange = uniquePixelCountBefore - uniquePixelCountAfter; 
                    double uniquePixelCountReductionRate = 1 - uniquePixelCountAfter / (double) uniquePixelCountBefore; 

                    String uniquePixelCountChangeOutput = String.format("Unique pixel count reduction  for %s : %d", imageName, uniquePixelCountChange);
                    String uniquePixelCountReductionRateOutput = String.format("Unique Pixel Reduction Rate (1 - uniquePixelCountAfter/uniquePixelCountBefore) for %s : %.2f", imageName, uniquePixelCountReductionRate);

                    System.out.println(uniquePixelCountChangeOutput);
                    System.out.println(uniquePixelCountReductionRateOutput);

                    testFrameWorkIndividual.writeToResult(uniquePixelCountChangeOutput);
                    testFrameWorkIndividual.writeToResult(uniquePixelCountReductionRateOutput);

                    testFrameWorkIndividual.writeToResult("--------------------------------------------------------------------------------");

                    //convert back to image for visualisation
                    PixeltoImageConverter PixeltoImageConverter = new PixeltoImageConverter(newPixelData);
                    PixeltoImageConverter.saveImage("Decompressed/" + imageName, "png");

                    //Get the two bufferedimages for calculations
                    BufferedImage originalimage = ImageIO.read(new File(ImageDirectory + imageName));
                    BufferedImage decompressedimage = ImageIO.read(new File("Decompressed/" + imageName)); 

                    //calculate MAE
                    double MAE = MAECalculator.calculateMAE(originalimage, decompressedimage);
                    String maeOutput = String.format("Mean Absolute Error of : %S is: %.2f",imageName ,MAE); 
                    System.out.println(maeOutput);
                    testFrameWorkIndividual.writeToResult(maeOutput);

                    //calculate MSE
                    double MSE = MSECalculator.calculateMSE(originalimage, decompressedimage);
                    String mseOutput = String.format("Mean Squared Error of : %S is: %.2f",imageName ,MSE); 
                    System.out.println(mseOutput);
                    testFrameWorkIndividual.writeToResult(mseOutput);

                    //calculate PSNR
                    double PSNR = PSNRCalculator.calculatePSNR(originalimage, decompressedimage);
                    String psnrOutput = String.format("Peak Signal-to-Noise Ratio of : %S is: %.2f",imageName ,PSNR); 
                    System.out.println(psnrOutput);
                    testFrameWorkIndividual.writeToResult(psnrOutput);


                    // initialize a record row with 13 slots (0-11)
                    String[] recordRow = new String[17]; 
                    // 0_Test_ID
                    recordRow[0] = testFrameWorkIndividual.testID; // this test instance's testID 
                    // 1_File_Name
                    recordRow[1] = imageName; 
                    // 2_Original_Size
                    recordRow[2] = Long.toString(originalFileSize); 
                    // 3_Compressed_Bin_Size
                    recordRow[3] = Long.toString(compressedFileSize); 
                    // 4_File_Size_Difference, 5_Compression_Rate
                    recordRow[4] = Long.toString(differenceInFileSize); 
                    recordRow[5] = Double.toString(compressionRate); 
                    // 6_Compression_Time
                    recordRow[6] = Long.toString(compressExecutionTime); 
                    // 7_Decompression_Time
                    recordRow[7] = Long.toString(decompressExecutionTime); 
                    // 8_Unique_Pixel_Count_Before 
                    recordRow[8] = Long.toString(uniquePixelCountBefore); 
                    // 9_Unique_Pixel_Count_after
                    recordRow[9] = Long.toString(uniquePixelCountAfter); 
                    // 10_Unique_Pixel_Count_Change
                    recordRow[10] = Long.toString(uniquePixelCountChange); 
                    // 11_Unique_Pixel_Count_Reduction
                    recordRow[11] = Double.toString(uniquePixelCountReductionRate); 

                    // 12_MAE, 13_MSE, 14_PSNR
                    recordRow[12] = Double.toString(MAE); 
                    recordRow[13] = Double.toString(MSE); 
                    recordRow[14] = Double.toString(PSNR); 

                    // 15_QuadtreeThreshold
                    recordRow[15] = Integer.toString(quadtreeThreshold); 

                    // 16_AllowedExceedingThreshold
                    recordRow[16] = Double.toString(allowedExceedingThresholdFactor); 

                    // Add the row to the data table 
                    compressionDataTable.add(recordRow); 
                    

                    testFrameWorkIndividual.writeToResult("================================================================================");
                }
            }
        }

        testFrameWorkIndividual.writeToResult("Exporting data to IndividualCompressionData.csv");
        CSVHelperIndividual csvHelperIndividual = new CSVHelperIndividual(String.format("IndividualCompressionData_since_%s.csv", testID)); // first
        csvHelperIndividual.createCSV();
        csvHelperIndividual.appendToCSV(compressionDataTable);
}

    public static void enumerateTests (
        int minQuadtreeThreshold, 
        int maxQuadtreeThreshold, 
        int quadtreeThresholdStep, 
        double minAllowedExceedingThreshold, 
        double maxAllowedExceedingThreshold, 
        double allowedExceedingThresholdStep
    ) throws IOException, ClassNotFoundException {

        TestFrameWorkIndividual testFrameWorkIndividual = new TestFrameWorkIndividual(); 
        
        for (int qt = minQuadtreeThreshold; qt <= maxQuadtreeThreshold; qt = qt+quadtreeThresholdStep){
            for (double aet = minAllowedExceedingThreshold; aet <= maxAllowedExceedingThreshold; aet = aet+allowedExceedingThresholdStep){
                testFrameWorkIndividual.test(qt, aet); 
            }
        }
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException{

        enumerateTests(50, 200, 10, 0.001, 0.100, 0.010); 

    }
}