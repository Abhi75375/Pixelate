import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ExportDialog extends JDialog {

    private final Canvas canvas;
    private JComboBox<ScaleOption> scaleComboBox;
    private JRadioButton transparentRadio;
    private JRadioButton whiteRadio;
    private JRadioButton customRadio;
    private Color customBgColor = Color.BLACK;
    private JButton customColorBtn;

    private static class ScaleOption {
        final int scale;
        final String label;

        ScaleOption(int scale, int gridSize, String note) {
            this.scale = scale;
            int px = scale * gridSize;
            this.label = String.format("%dx (%d × %d px)%s", scale, px, px, note.isEmpty() ? "" : " - " + note);
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public ExportDialog(JFrame parent, Canvas canvas) {
        super(parent, "Export as PNG", true);
        this.canvas = canvas;

        initUI();
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        contentPanel.setBackground(new Color(40, 42, 46));

        // Header Title
        JLabel titleLabel = new JLabel("Export Pixel Art to PNG");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);

        JLabel descLabel = new JLabel("Choose export resolution and background options.");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descLabel.setForeground(new Color(170, 175, 185));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(descLabel);

        contentPanel.add(Box.createVerticalStrut(16));

        // Scale Section
        JLabel scaleLabel = new JLabel("Export Resolution / Scale:");
        scaleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        scaleLabel.setForeground(new Color(220, 225, 230));
        scaleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(scaleLabel);

        contentPanel.add(Box.createVerticalStrut(6));

        int gs = canvas.gridSize;
        ScaleOption[] options = new ScaleOption[] {
            new ScaleOption(1, gs, "Native Pixel Size"),
            new ScaleOption(2, gs, ""),
            new ScaleOption(4, gs, "Standard"),
            new ScaleOption(8, gs, "Recommended"),
            new ScaleOption(16, gs, "High Definition"),
            new ScaleOption(32, gs, "Ultra HD")
        };
        scaleComboBox = new JComboBox<>(options);
        scaleComboBox.setSelectedIndex(3); // Default 8x
        scaleComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        scaleComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(scaleComboBox);

        contentPanel.add(Box.createVerticalStrut(16));

        // Background Section
        JLabel bgLabel = new JLabel("Background:");
        bgLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        bgLabel.setForeground(new Color(220, 225, 230));
        bgLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(bgLabel);

        contentPanel.add(Box.createVerticalStrut(6));

        transparentRadio = new JRadioButton("Transparent (PNG Alpha)", true);
        whiteRadio = new JRadioButton("Solid White", false);
        customRadio = new JRadioButton("Custom Color", false);

        ButtonGroup bgGroup = new ButtonGroup();
        bgGroup.add(transparentRadio);
        bgGroup.add(whiteRadio);
        bgGroup.add(customRadio);

        Color radioTextColor = new Color(220, 220, 220);
        for (JRadioButton rb : new JRadioButton[]{transparentRadio, whiteRadio, customRadio}) {
            rb.setOpaque(false);
            rb.setForeground(radioTextColor);
            rb.setFocusPainted(false);
            rb.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(rb);
            contentPanel.add(Box.createVerticalStrut(2));
        }

        // Custom color button
        customColorBtn = new JButton("Choose Color...");
        customColorBtn.setEnabled(false);
        customColorBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        customRadio.addActionListener(e -> customColorBtn.setEnabled(customRadio.isSelected()));
        transparentRadio.addActionListener(e -> customColorBtn.setEnabled(false));
        whiteRadio.addActionListener(e -> customColorBtn.setEnabled(false));

        customColorBtn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Select Background Color", customBgColor);
            if (chosen != null) {
                customBgColor = chosen;
                customColorBtn.setBackground(customBgColor);
            }
        });
        contentPanel.add(customColorBtn);

        contentPanel.add(Box.createVerticalStrut(20));

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        JButton exportBtn = new JButton("Export PNG...");
        exportBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        exportBtn.setBackground(new Color(30, 136, 229));
        exportBtn.setForeground(Color.WHITE);
        exportBtn.setFocusPainted(false);
        exportBtn.addActionListener(e -> performExport());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(exportBtn);

        contentPanel.add(buttonPanel);

        getContentPane().add(contentPanel);
    }

    private void performExport() {
        ScaleOption selectedOption = (ScaleOption) scaleComboBox.getSelectedItem();
        int scale = (selectedOption != null) ? selectedOption.scale : 8;
        boolean transparent = transparentRadio.isSelected();
        Color bg = null;
        if (whiteRadio.isSelected()) {
            bg = Color.WHITE;
        } else if (customRadio.isSelected()) {
            bg = customBgColor;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PNG Image");
        fileChooser.setSelectedFile(new File("pixelart.png"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Images (*.png)", "png"));

        int result = fileChooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File targetFile = fileChooser.getSelectedFile();
        if (!targetFile.getName().toLowerCase().endsWith(".png")) {
            targetFile = new File(targetFile.getParentFile(), targetFile.getName() + ".png");
        }

        if (targetFile.exists()) {
            int overwrite = JOptionPane.showConfirmDialog(
                this,
                "The file '" + targetFile.getName() + "' already exists.\nDo you want to overwrite it?",
                "Confirm Overwrite",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (overwrite != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            saveImage(targetFile, scale, transparent, bg);
            canvas.notifyStatusMessage("Successfully exported to " + targetFile.getName());
            JOptionPane.showMessageDialog(
                this,
                "Pixel art exported successfully!\n\nFile: " + targetFile.getAbsolutePath() +
                "\nDimensions: " + (canvas.gridSize * scale) + " × " + (canvas.gridSize * scale) + " px",
                "Export Complete",
                JOptionPane.INFORMATION_MESSAGE
            );
            dispose();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error saving PNG file:\n" + ex.getMessage(),
                "Export Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void saveImage(File file, int scale, boolean transparent, Color bgColor) throws IOException {
        int gs = canvas.gridSize;
        int imgSize = gs * scale;
        BufferedImage image = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        if (!transparent && bgColor != null) {
            g2.setColor(bgColor);
            g2.fillRect(0, 0, imgSize, imgSize);
        }

        Color[][] colors = canvas.colors;
        for (int i = 0; i < gs; i++) {
            for (int j = 0; j < gs; j++) {
                Color c = colors[i][j];
                if (c != null) {
                    g2.setColor(c);
                    g2.fillRect(i * scale, j * scale, scale, scale);
                }
            }
        }

        g2.dispose();
        ImageIO.write(image, "PNG", file);
    }

    public static void showExportDialog(JFrame parent, Canvas canvas) {
        ExportDialog dialog = new ExportDialog(parent, canvas);
        dialog.setVisible(true);
    }
}
