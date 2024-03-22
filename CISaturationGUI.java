import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.RescaleOp;

public class CISaturationGUI extends JFrame {

    private JFrame chooseImageFrame;
    private JFrame adjustSaturationFrame;
    private JFrame adjustBrightnessFrame;
    private JFrame displayImageFrame;

    private JLabel inputImagePathLabel;
    private JTextField inputImagePathField;
    private JButton chooseImageButton;

    private JLabel saturationFactorLabel;
    private JSlider saturationFactorSlider;
    private JButton adjustSaturationButton;

    private JLabel brightnessLabel;
    private JSlider brightnessSlider;
    private JButton adjustBrightnessButton;

    private JLabel outputImageLabel;
    private JButton backButton;

    private BufferedImage outputImage;

    public CISaturationGUI() {
        createChooseImageFrame();
        createAdjustSaturationFrame();
        createAdjustBrightnessFrame();
        createDisplayImageFrame();

        chooseImageFrame.setVisible(true);
    }

    private void createChooseImageFrame() {
        chooseImageFrame = new JFrame("Image Editor");
        chooseImageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chooseImageFrame.setLayout(new GridLayout(5, 5));
        chooseImageFrame.getContentPane().setBackground(Color.YELLOW);

        inputImagePathLabel = new JLabel("Set Image Path:");
        inputImagePathField = new JTextField();
        chooseImageButton = new JButton("Choose Image");

        chooseImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif");
                fileChooser.setFileFilter(filter);
                int returnVal = fileChooser.showOpenDialog(chooseImageFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    inputImagePathField.setText(file.getAbsolutePath());
                    chooseImageFrame.setVisible(false);
                    adjustSaturationFrame.setVisible(true);
                }
            }
        });

        chooseImageFrame.add(inputImagePathLabel);
        chooseImageFrame.add(inputImagePathField);
        chooseImageFrame.add(chooseImageButton);
        chooseImageFrame.pack();
        chooseImageFrame.setLocationRelativeTo(null);
    }

    private void createAdjustSaturationFrame() {
        adjustSaturationFrame = new JFrame("Adjust Saturation");
        adjustSaturationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        adjustSaturationFrame.setLayout(new GridLayout(5, 5));
        adjustSaturationFrame.getContentPane().setBackground(Color.GREEN);

        saturationFactorLabel = new JLabel("Saturation Factor:");
        saturationFactorSlider = new JSlider(0, 200, 100);
        saturationFactorSlider.setMajorTickSpacing(10);
        saturationFactorSlider.setPaintTicks(true);
        saturationFactorSlider.setPaintLabels(true);
        adjustSaturationButton = new JButton("Adjust Saturation");

        adjustSaturationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputImagePath = inputImagePathField.getText();
                double saturationFactor = saturationFactorSlider.getValue() / 100.0;

                try {
                    BufferedImage inputImage = ImageIO.read(new File(inputImagePath));
                    outputImage = adjustSaturation(inputImage, saturationFactor);
                    adjustSaturationFrame.setVisible(false);
                    adjustBrightnessFrame.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(adjustSaturationFrame, "Error processing image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        adjustSaturationFrame.add(saturationFactorLabel);
        adjustSaturationFrame.add(saturationFactorSlider);
        adjustSaturationFrame.add(adjustSaturationButton);
        adjustSaturationFrame.pack();
        adjustSaturationFrame.setLocationRelativeTo(null);
    }

    private void createAdjustBrightnessFrame() {
        adjustBrightnessFrame = new JFrame("Adjust Brightness");
        adjustBrightnessFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        adjustBrightnessFrame.setLayout(new GridLayout(5, 5));
        adjustBrightnessFrame.getContentPane().setBackground(Color.BLUE);

        brightnessLabel = new JLabel("Brightness:");
        brightnessSlider = new JSlider(-255, 255, 0);
        brightnessSlider.setMajorTickSpacing(50);
        brightnessSlider.setPaintTicks(true);
        brightnessSlider.setPaintLabels(true);
        adjustBrightnessButton = new JButton("Adjust Brightness");

        adjustBrightnessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int brightnessValue = brightnessSlider.getValue();

                try {
                    outputImage = adjustBrightness(outputImage, brightnessValue);
                    displayImageFrame.repaint();
                    displayImageFrame.revalidate();
                    adjustBrightnessFrame.setVisible(false);
                    displayImageFrame.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(adjustBrightnessFrame, "Error adjusting brightness: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        adjustBrightnessFrame.add(brightnessLabel);
        adjustBrightnessFrame.add(brightnessSlider);
        adjustBrightnessFrame.add(adjustBrightnessButton);
        adjustBrightnessFrame.pack();
        adjustBrightnessFrame.setLocationRelativeTo(null);
    }

    private void createDisplayImageFrame() {
        displayImageFrame = new JFrame("Display Image");
        displayImageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        outputImageLabel = new JLabel();
        backButton = new JButton("Back");

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayImageFrame.setVisible(false);
                chooseImageFrame.setVisible(true);
            }
        });

        JScrollPane outputImageScrollPane = new JScrollPane(outputImageLabel);

        displayImageFrame.add(outputImageScrollPane, BorderLayout.CENTER);
        displayImageFrame.add(backButton, BorderLayout.SOUTH);
        displayImageFrame.pack();
        displayImageFrame.setLocationRelativeTo(null);
    }

    private BufferedImage adjustSaturation(BufferedImage image, double saturationFactor) {
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

        ImageIcon icon = new ImageIcon(adjustedImage);
        outputImageLabel.setIcon(icon);
        outputImageLabel.repaint();
        outputImageLabel.revalidate();

        return adjustedImage;
    }

    private BufferedImage adjustBrightness(BufferedImage image, int brightnessValue) {
        RescaleOp op = new RescaleOp(1.0f, brightnessValue, null);
        BufferedImage adjustedImage = op.filter(image, null);
        return adjustedImage;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CISaturationGUI();
            }
        });
    }
}



