import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PSNRCalculator {

    public static void main(String[] args) {
        try {
            // Load the original and decompressed images
            BufferedImage originalImage = ImageIO.read(new File("Original/nasilemak.jpg"));
            BufferedImage decompressedImage = ImageIO.read(new File("Decompressed/nasilemakdecompressedfrompng.jpg"));

            // Calculate the PSNR between the two images
            double psnr = calculatePSNR(originalImage, decompressedImage);

            // Print the PSNR value
            System.out.println("PSNR: " + psnr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double calculatePSNR(BufferedImage originalImage, BufferedImage decompressedImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        
        // Calculate the mean squared error (MSE) between original and decompressed images
        double mse = 0.0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelOriginal = originalImage.getRGB(x, y);
                int pixelDecompressed = decompressedImage.getRGB(x, y);

                int rOriginal = (pixelOriginal >> 16) & 0xFF;
                int gOriginal = (pixelOriginal >> 8) & 0xFF;
                int bOriginal = pixelOriginal & 0xFF;

                int rDecompressed = (pixelDecompressed >> 16) & 0xFF;
                int gDecompressed = (pixelDecompressed >> 8) & 0xFF;
                int bDecompressed = pixelDecompressed & 0xFF;

                double squaredError = Math.pow(rOriginal - rDecompressed, 2) +
                                      Math.pow(gOriginal - gDecompressed, 2) +
                                      Math.pow(bOriginal - bDecompressed, 2);
                mse += squaredError;
            }
        }
        mse /= (width * height);

        // Calculate the maximum possible pixel value
        double maxPixelValue = 255.0;

        // Calculate PSNR
        double psnr = 20 * Math.log10(maxPixelValue / Math.sqrt(mse));
        
        return psnr;
    }
}
