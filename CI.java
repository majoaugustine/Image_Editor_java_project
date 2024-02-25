// this is a simple image saturation manager which takes path of image as input.
// saturation factor(0-1) is taken as range. If given value more than 1, then saturation increases.
// saturation factor value 0 will make image black and white.

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class CI{

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

// Prompt user for input image path
        System.out.print("Enter the path of the input image: ");
        String inputImagePath = scanner.nextLine();

        BufferedImage inputImage = ImageIO.read(new File(inputImagePath));

// Asks to input Saturation factor
        System.out.print("Enter the saturation factor(should not be below 0): ");
        double saturationFactor = scanner.nextDouble();

// Adjust the saturation
        BufferedImage outputImage = adjustSaturation(inputImage, saturationFactor);

// Asks user for a new name for the output image
        System.out.print("Enter a new name for the output image (including the extension): ");
        String outputImageName = scanner.next();

// Construct the output image path by keeping the input path and changing the file name
        File outputImageFile = new File(new File(inputImagePath).getParent(), outputImageName);
        ImageIO.write(outputImage, "jpg", outputImageFile);

        System.out.println("Image processing complete. Adjusted image saved to: " + outputImageFile.getAbsolutePath());

        scanner.close();
    }

    private static BufferedImage adjustSaturation(BufferedImage image, double saturationFactor) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage adjustedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y));
                float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

// Adjust saturation
                hsb[1] *= saturationFactor;
                hsb[1] = Math.min(hsb[1], 1.0f); // Ensure saturation is in the valid range

                int rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
                adjustedImage.setRGB(x, y, rgb);
            }
        }

        return adjustedImage;
    }
}
