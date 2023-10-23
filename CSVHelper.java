import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CSVHelper {
    private static final String CSV_FILE_NAME = "compressionSummaryData.csv";
    private static final String CSV_HEADER = "TestID,Compression Time,File Size Difference,Compression Rate,Decompression Time,MAE,MSE,PSNR\n";

    public CSVHelper(){
    }

    public void createCSV() {
        File file = new File(CSV_FILE_NAME);

        // If file doesn't exist, create it and write the header
        if (!file.exists()) {
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(CSV_HEADER);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void appendToCSV(
        String testID, 
        double compressionTime, 
        double fileSizeDifference, 
        double compressionRate, 
        double decompressionTime, 
        double MAE, 
        double MSE, 
        double PSNR) {
        try (FileWriter fw = new FileWriter(CSV_FILE_NAME, true)) { // true for append mode
            fw.write(String.format("%s,%f,%f,%f,%f,%f,%f,%f\n", testID, compressionTime, fileSizeDifference, 
                    compressionRate, decompressionTime, MAE, MSE, PSNR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
