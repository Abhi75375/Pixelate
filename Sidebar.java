import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Sidebar extends JPanel implements Canvas.CanvasListener {

    private final Canvas canvas;
    private final Map<Canvas.Tool, JButton> toolButtons = new HashMap<>();
    private JCheckBox fillShapesCheckbox;
    private JPanel currentColorDisplay;
    private JLabel hexLabel;

    // Curated 24-color pixel art palette (PICO-8 & Endesga inspired)
    private static final Color[] PALETTE = {
        new Color(0, 0, 0),         // Black
        new Color(29, 43, 83),      // Dark Blue
        new Color(126, 37, 83),     // Dark Purple
        new Color(0, 135, 81),      // Dark Green
        new Color(171, 82, 54),     // Brown
        new Color(95, 87, 79),      // Dark Gray
        new Color(194, 195, 199),   // Light Gray
        new Color(255, 255, 255),   // White
        new Color(255, 0, 77),      // Red
        new Color(255, 163, 0),     // Orange
        new Color(255, 236, 39),    // Yellow
        new Color(0, 228, 54),      // Green
        new Color(41, 173, 255),    // Light Blue
        new Color(131, 118, 156),   // Indigo
        new Color(255, 119, 168),   // Pink
        new Color(255, 204, 170),   // Peach / Skin
        new Color(162, 136, 121),   // Khaki
        new Color(24, 20, 37),      // Deep Charcoal
        new Color(143, 86, 59),     // Wood Brown
        new Color(102, 57, 49),     // Rust
        new Color(48, 96, 130),     // Slate Blue
        new Color(75, 180, 229),    // Sky Blue
        new Color(99, 199, 77),     // Lime
        new Color(181, 80, 136)     // Magenta
    };

    public Sidebar(Canvas canvas) {
        this.canvas = canvas;
        this.canvas.addCanvasListener(this);

        setBackground(new Color(36, 38, 42));
        setPreferredSize(new Dimension(190, 600));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        // 1. Tools Section
        add(createSectionHeader("TOOLS & SHAPES"));
        add(Box.createVerticalStrut(6));

        JPanel toolsGrid = new JPanel(new GridLayout(4, 2, 5, 5));
        toolsGrid.setOpaque(false);
        toolsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 132));

        JButton pencilBtn = createToolButton("✏ Pencil", Canvas.Tool.PENCIL, "Pencil Tool (B)");
        JButton eraserBtn = createToolButton("🧹 Eraser", Canvas.Tool.ERASER, "Eraser Tool (E) / Right-Click");
        JButton bucketBtn = createToolButton("🪣 Bucket", Canvas.Tool.BUCKET, "Paint Bucket Fill (G)");
        JButton pickerBtn = createToolButton("💉 Picker", Canvas.Tool.PICKER, "Color Picker / Eyedropper (I)");
        JButton lineBtn = createToolButton("📏 Line", Canvas.Tool.LINE, "Line Tool (L)");
        JButton rectBtn = createToolButton("▢ Rect", Canvas.Tool.RECTANGLE, "Rectangle / Square (R) [Hold Shift for Square]");
        JButton circleBtn = createToolButton("○ Circle", Canvas.Tool.CIRCLE, "Circle / Ellipse (C) [Hold Shift for Circle]");

        toolsGrid.add(pencilBtn);
        toolsGrid.add(eraserBtn);
        toolsGrid.add(bucketBtn);
        toolsGrid.add(pickerBtn);
        toolsGrid.add(lineBtn);
        toolsGrid.add(rectBtn);
        toolsGrid.add(circleBtn);

        // Fill Mode toggle button in the 8th slot
        JToggleButton fillToggleBtn = new JToggleButton("⬛ Fill", canvas.isFillShapes());
        fillToggleBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        fillToggleBtn.setBackground(new Color(50, 54, 60));
        fillToggleBtn.setForeground(new Color(210, 215, 225));
        fillToggleBtn.setFocusPainted(false);
        fillToggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        fillToggleBtn.setToolTipText("Toggle Fill Shape Mode: Outline vs Solid (F)");
        fillToggleBtn.setBorder(new LineBorder(new Color(70, 75, 85), 1, true));
        fillToggleBtn.addActionListener(e -> {
            boolean filled = fillToggleBtn.isSelected();
            canvas.setFillShapes(filled);
            fillToggleBtn.setBackground(filled ? new Color(30, 136, 229) : new Color(50, 54, 60));
            fillToggleBtn.setForeground(filled ? Color.WHITE : new Color(210, 215, 225));
            fillToggleBtn.setText(filled ? "⬛ Filled" : "▢ Outline");
        });
        toolsGrid.add(fillToggleBtn);

        add(toolsGrid);

        add(Box.createVerticalStrut(14));

        // 2. Current Color Section
        add(createSectionHeader("CURRENT COLOR"));
        add(Box.createVerticalStrut(6));

        currentColorDisplay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = canvas.getCurrentColor();
                if (c == null) {
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(new Color(220, 50, 50));
                    g2.drawLine(0, 0, getWidth(), getHeight());
                } else {
                    g2.setColor(c);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.setColor(new Color(90, 95, 105));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            }
        };
        currentColorDisplay.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        currentColorDisplay.setPreferredSize(new Dimension(166, 36));
        currentColorDisplay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        currentColorDisplay.setToolTipText("Click to pick custom color");
        currentColorDisplay.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                openColorChooser();
            }
        });
        add(currentColorDisplay);

        add(Box.createVerticalStrut(4));

        JPanel hexRow = new JPanel(new BorderLayout());
        hexRow.setOpaque(false);
        hexRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        hexLabel = new JLabel("#000000", SwingConstants.CENTER);
        hexLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        hexLabel.setForeground(new Color(190, 195, 205));
        hexRow.add(hexLabel, BorderLayout.CENTER);
        add(hexRow);

        add(Box.createVerticalStrut(4));

        JButton customColorBtn = new JButton("Custom Color...");
        styleSecondaryButton(customColorBtn);
        customColorBtn.addActionListener(e -> openColorChooser());
        add(customColorBtn);

        add(Box.createVerticalStrut(14));

        // 3. Palette Section
        add(createSectionHeader("PALETTE"));
        add(Box.createVerticalStrut(6));

        JPanel paletteGrid = new JPanel(new GridLayout(6, 4, 4, 4));
        paletteGrid.setOpaque(false);
        paletteGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        for (Color col : PALETTE) {
            JButton swatch = createPaletteSwatch(col);
            paletteGrid.add(swatch);
        }
        add(paletteGrid);

        add(Box.createVerticalStrut(14));

        // 4. Quick Actions
        add(createSectionHeader("CANVAS"));
        add(Box.createVerticalStrut(6));

        JButton clearBtn = new JButton("Clear Canvas");
        styleSecondaryButton(clearBtn);
        clearBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear the entire canvas?",
                "Clear Canvas",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                canvas.clearCanvas();
            }
        });
        add(clearBtn);

        add(Box.createVerticalGlue());

        updateToolSelection(canvas.getCurrentTool());
        updateColorDisplay(canvas.getCurrentColor());
    }

    private JLabel createSectionHeader(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(new Color(130, 138, 150));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JButton createToolButton(String text, Canvas.Tool tool, String tooltip) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setToolTipText(tooltip);
        btn.addActionListener(e -> canvas.setCurrentTool(tool));
        toolButtons.put(tool, btn);
        return btn;
    }

    private JButton createPaletteSwatch(Color color) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(color);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(new Color(0, 0, 0, 80));
                g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            }
        };
        btn.setPreferredSize(new Dimension(34, 22));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(new Color(60, 64, 72), 1));
        String hex = String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
        btn.setToolTipText(hex);
        btn.addActionListener(e -> {
            canvas.setCurrentColor(color);
            if (canvas.getCurrentTool() == Canvas.Tool.ERASER) {
                canvas.setCurrentTool(Canvas.Tool.PENCIL);
            }
        });
        return btn;
    }

    private void styleSecondaryButton(JButton btn) {
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setBackground(new Color(50, 54, 60));
        btn.setForeground(new Color(225, 230, 240));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(70, 75, 85), 1, true),
            new EmptyBorder(4, 8, 4, 8)
        ));
    }

    private void openColorChooser() {
        Color current = canvas.getCurrentColor();
        Color chosen = JColorChooser.showDialog(this, "Select Drawing Color", current != null ? current : Color.BLACK);
        if (chosen != null) {
            canvas.setCurrentColor(chosen);
            if (canvas.getCurrentTool() == Canvas.Tool.ERASER) {
                canvas.setCurrentTool(Canvas.Tool.PENCIL);
            }
        }
    }

    private void updateToolSelection(Canvas.Tool activeTool) {
        for (Map.Entry<Canvas.Tool, JButton> entry : toolButtons.entrySet()) {
            JButton btn = entry.getValue();
            boolean isSelected = (entry.getKey() == activeTool);
            if (isSelected) {
                btn.setBackground(new Color(30, 136, 229));
                btn.setForeground(Color.WHITE);
                btn.setBorder(new LineBorder(new Color(100, 181, 246), 2, true));
            } else {
                btn.setBackground(new Color(50, 54, 60));
                btn.setForeground(new Color(210, 215, 225));
                btn.setBorder(new LineBorder(new Color(70, 75, 85), 1, true));
            }
        }
    }

    private void updateColorDisplay(Color color) {
        if (color == null) {
            hexLabel.setText("None (Eraser)");
        } else {
            hexLabel.setText(String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue()));
        }
        currentColorDisplay.repaint();
    }

    @Override
    public void onColorChanged(Color color) {
        updateColorDisplay(color);
    }

    @Override
    public void onToolChanged(Canvas.Tool tool) {
        updateToolSelection(tool);
    }

    @Override
    public void onZoomChanged(float scale) {
    }

    @Override
    public void onStatusMessage(String message) {
    }

    @Override
    public void onCanvasReset() {
    }
}
