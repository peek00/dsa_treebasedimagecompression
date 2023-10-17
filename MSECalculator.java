import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MSECalculator {

    public static void main(String[] args) {
        try {
            // Load the original and decompressed images
            BufferedImage originalImage = ImageIO.read(new File("Original/nasilemak.jpg"));
            BufferedImage decompressedImage = ImageIO.read(new File("Decompressed/nasilemakdecompressedfrompng.jpg"));

            // Check if the images have the same dimensions
            if (originalImage.getWidth() != decompressedImage.getWidth() || 
                originalImage.getHeight() != decompressedImage.getHeight()) {
                System.err.println("Images have different dimensions.");
                return;
            }

            // Calculate the Mean Squared Error (MSE)
            double mse = calculateMSE(originalImage, decompressedImage);

            // Print the MSE value as a measure of image quality
            System.out.println("Mean Squared Error (MSE): " + mse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Calculate the Mean Squared Error (MSE) between two images
    public static double calculateMSE(BufferedImage image1, BufferedImage image2) {
        int width = image1.getWidth();
        int height = image1.getHeight();
        long mse = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel1 = image1.getRGB(x, y);
                int pixel2 = image2.getRGB(x, y);

                int r1 = (pixel1 >> 16) & 0xFF;
                int g1 = (pixel1 >> 8) & 0xFF;
                int b1 = pixel1 & 0xFF;

                int r2 = (pixel2 >> 16) & 0xFF;
                int g2 = (pixel2 >> 8) & 0xFF;
                int b2 = pixel2 & 0xFF;

                int dr = r1 - r2;
                int dg = g1 - g2;
                int db = b1 - b2;

                mse += (dr * dr) + (dg * dg) + (db * db);
            }
        }

        // Calculate the mean squared error
        return (double) mse / (width * height);
    }
}
