import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PixeltoImageConverter {
    private int[][][] pixelData;
    private int width;
    private int height;

    public PixeltoImageConverter(int[][][] pixelData) {
        this.pixelData = pixelData;
        this.width = pixelData.length;
        this.height = pixelData[0].length;
    }

    public void saveImage(String outputImagePath, String format) {
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int red = pixelData[x][y][0];
                int green = pixelData[x][y][1];
                int blue = pixelData[x][y][2];
                int rgb = (red << 16) | (green << 8) | blue; // Create an RGB color from the components
                outputImage.setRGB(x, y, rgb);
            }
        }

        try {
            ImageIO.write(outputImage, format, new File(outputImagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Example usage:
        int width = 100;
        int height = 100;
        int[][][] pixelData = new int[width][height][3];

        // Fill pixelData with pixel values...

        PixeltoImageConverter converter = new PixeltoImageConverter(pixelData);
        converter.saveImage("output_image.png", "png");
    }
}
