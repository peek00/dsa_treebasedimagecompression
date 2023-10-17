import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImagetoPixelConverter {
    private BufferedImage image;
    private int[][][] pixelData;

    public ImagetoPixelConverter(String imagePath) {
        try {
            // Load the image from the specified file path
            File imageFile = new File(imagePath);
            this.image = ImageIO.read(imageFile);
            
            // Get image width and height
            int width = image.getWidth();
            int height = image.getHeight();
            
            // Initialize the pixelData array
            this.pixelData = new int[width][height][3];
            
            // Convert the image into pixelData
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int color = image.getRGB(x, y);
                    int red = (color >> 16) & 0xFF;
                    int green = (color >> 8) & 0xFF;
                    int blue = color & 0xFF;
                    pixelData[x][y][0] = red;
                    pixelData[x][y][1] = green;
                    pixelData[x][y][2] = blue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int[][][] getPixelData() {
        return pixelData;
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }
    
}

