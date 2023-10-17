import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MAECalculator {
    public static void main(String[] args) throws IOException {
        BufferedImage originalImage = ImageIO.read(new File("Original/nasilemak.jpg"));
        BufferedImage compressedImage = ImageIO.read(new File("Decompressed/nasilemakdecompressedfrompng.jpg"));

        double mae = calculateMAE(originalImage, compressedImage);
        System.out.println("Mean Absolute Error (MAE): " + mae);
    }

    public static double calculateMAE(BufferedImage img1, BufferedImage img2) {
        int width = img1.getWidth();
        int height = img1.getHeight();
        
        double totalError = 0.0;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel1 = img1.getRGB(x, y);
                int pixel2 = img2.getRGB(x, y);
                
                int r1 = (pixel1 >> 16) & 0xFF;
                int g1 = (pixel1 >> 8) & 0xFF;
                int b1 = pixel1 & 0xFF;
                
                int r2 = (pixel2 >> 16) & 0xFF;
                int g2 = (pixel2 >> 8) & 0xFF;
                int b2 = pixel2 & 0xFF;
                
                double error = Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
                totalError += error;
            }
        }
        
        double mae = totalError / (width * height * 3); // 3 channels (RGB)
        return mae;
    }
}
