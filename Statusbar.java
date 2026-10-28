import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class Statusbar extends JPanel implements Canvas.CanvasListener {
    private final Canvas canvas;
    private JLabel coordLabel;
    private JLabel sizeLabel;
    private JLabel toolLabel;
    private JLabel zoomLabel;
    private JPanel colorSwatch;
    private JLabel colorText;
    private JLabel statusMsgLabel;

    public Statusbar(Canvas canvas) {
        this.canvas = canvas;
        this.canvas.addCanvasListener(this);

        setBackground(new Color(32, 34, 38));
        setPreferredSize(new Dimension(520, 30));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(50, 52, 58)));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        leftPanel.setOpaque(false);

        coordLabel = createLabel("X: --  Y: --");
        sizeLabel = createLabel(canvas.gridSize + " × " + canvas.gridSize + " px");
        toolLabel = createLabel("Tool: Pencil");
        zoomLabel = createLabel("Zoom: " + (int)(canvas.scale * 100) + "%");

        leftPanel.add(coordLabel);
        leftPanel.add(createSeparator());
        leftPanel.add(sizeLabel);
        leftPanel.add(createSeparator());
        leftPanel.add(toolLabel);
        leftPanel.add(createSeparator());
        leftPanel.add(zoomLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightPanel.setOpaque(false);

        statusMsgLabel = createLabel("Ready");
        statusMsgLabel.setForeground(new Color(150, 200, 255));

        colorSwatch = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                Color c = canvas.getCurrentColor();
                if (c == null) {
                    g2.setColor(Color.WHITE);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(Color.RED);
                    g2.drawLine(0, 0, getWidth(), getHeight());
                } else {
                    g2.setColor(c);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                g2.setColor(new Color(100, 100, 100));
                g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            }
        };
        colorSwatch.setPreferredSize(new Dimension(16, 16));
        colorSwatch.setOpaque(false);

        colorText = createLabel(formatColor(canvas.getCurrentColor()));

        rightPanel.add(statusMsgLabel);
        rightPanel.add(createSeparator());
        rightPanel.add(colorSwatch);
        rightPanel.add(colorText);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);

        // Motion tracking on canvas
        this.canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateCoordinates(e.getX(), e.getY());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                updateCoordinates(e.getX(), e.getY());
            }
        });
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(new Color(200, 205, 215));
        return label;
    }

    private JLabel createSeparator() {
        JLabel sep = new JLabel("│");
        sep.setFont(new Font("SansSerif", Font.PLAIN, 11));
        sep.setForeground(new Color(70, 75, 85));
        return sep;
    }

    private void updateCoordinates(int rawX, int rawY) {
        int gridX = rawX / canvas.pixelSize;
        int gridY = rawY / canvas.pixelSize;

        if (gridX >= 0 && gridX < canvas.gridSize && gridY >= 0 && gridY < canvas.gridSize) {
            coordLabel.setText(String.format("X: %-2d  Y: %-2d", gridX, gridY));
        } else {
            coordLabel.setText("X: --  Y: --");
        }
    }

    private String formatColor(Color c) {
        if (c == null) return "Transparent";
        return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }

    @Override
    public void onColorChanged(Color color) {
        colorText.setText(formatColor(color));
        colorSwatch.repaint();
    }

    @Override
    public void onToolChanged(Canvas.Tool tool) {
        toolLabel.setText("Tool: " + tool.getLabel());
    }

    @Override
    public void onZoomChanged(float scale) {
        zoomLabel.setText("Zoom: " + (int)(scale * 100) + "%");
    }

    @Override
    public void onStatusMessage(String message) {
        statusMsgLabel.setText(message);
    }

    @Override
    public void onCanvasReset() {
        sizeLabel.setText(canvas.gridSize + " × " + canvas.gridSize + " px");
        zoomLabel.setText("Zoom: " + (int)(canvas.scale * 100) + "%");
    }
}
