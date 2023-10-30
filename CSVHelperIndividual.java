import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CSVHelperIndividual {
    private String csvFileName; 
    private static final String CSV_HEADER = "SN,Test ID,File Name,Original File Size,Compressed Bin Size,File Size Difference,Compression Rate,Compression Time,Decompression Time,MAE,MSE,PSNR,QuadtreeThreshold,AllowedExceedingThreshold\n";

    public CSVHelperIndividual(String csvFilename){
        this.csvFileName = csvFilename; 
    }

    public void createCSV() {
        File file = new File(csvFileName);

        // If file doesn't exist, create it and write the header
        if (!file.exists()) {
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(CSV_HEADER);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void appendToCSV(ArrayList<String[]> dataTable){
        int sn = 0; // serial number 
        // data table: An ArrayList of String arrays, with 12 elements in each array 
        try (FileWriter fw = new FileWriter(csvFileName, true)) { // true for append mode
            // Loop through each row in the data table
            for (String[] row : dataTable) {
                sn ++; 
                // Convert the row array to a CSV formatted string
                String csvRow = String.join(",", row);
                
                // Write the CSV row to the file and add a newline
                fw.write(Integer.toString(sn) + ","+csvRow + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
