import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class Menubar extends JPanel {

    private final Canvas canvas;
    private JFrame parentFrame;

    public Menubar(Canvas canvas) {
        this.canvas = canvas;

        setBackground(new Color(36, 38, 42));
        setPreferredSize(new Dimension(800, 44));
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 6));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50, 52, 58)));

        // 1. File Actions
        JButton newBtn = createToolbarButton("📄 New", "Start a new canvas (Ctrl+N)");
        newBtn.addActionListener(e -> promptNewCanvas());

        JButton exportBtn = new JButton("💾 Export PNG");
        exportBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        exportBtn.setBackground(new Color(30, 136, 229));
        exportBtn.setForeground(Color.WHITE);
        exportBtn.setFocusPainted(false);
        exportBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exportBtn.setToolTipText("Export canvas to PNG image file (Ctrl+E)");
        exportBtn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(100, 181, 246), 1, true),
            new EmptyBorder(5, 12, 5, 12)
        ));
        exportBtn.addActionListener(e -> ExportDialog.showExportDialog(parentFrame, canvas));

        add(newBtn);
        add(exportBtn);
        add(createToolbarSeparator());

        // 2. History Actions
        JButton undoBtn = createToolbarButton("↶ Undo", "Undo last stroke (Ctrl+Z)");
        undoBtn.addActionListener(e -> canvas.doUndo());

        JButton redoBtn = createToolbarButton("↷ Redo", "Redo last undone stroke (Ctrl+Y)");
        redoBtn.addActionListener(e -> canvas.doRedo());

        JButton clearBtn = createToolbarButton("🗑 Clear", "Clear entire canvas");
        clearBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Clear the entire canvas?",
                "Clear Canvas",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                canvas.clearCanvas();
            }
        });

        add(undoBtn);
        add(redoBtn);
        add(clearBtn);
        add(createToolbarSeparator());

        // 3. Zoom & View Actions
        JButton zoomInBtn = createToolbarButton("🔍+ In", "Zoom in (Ctrl+=)");
        zoomInBtn.addActionListener(e -> canvas.zoomIn());

        JButton zoomOutBtn = createToolbarButton("🔍- Out", "Zoom out (Ctrl+-)");
        zoomOutBtn.addActionListener(e -> canvas.zoomOut());

        JButton resetZoomBtn = createToolbarButton("100% Fit", "Reset zoom level (Ctrl+0)");
        resetZoomBtn.addActionListener(e -> canvas.resetZoom());

        JToggleButton gridToggle = new JToggleButton("⌗ Grid", canvas.isShowGrid());
        gridToggle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gridToggle.setBackground(new Color(50, 54, 60));
        gridToggle.setForeground(new Color(225, 230, 240));
        gridToggle.setFocusPainted(false);
        gridToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gridToggle.setToolTipText("Toggle grid visibility (Ctrl+G)");
        gridToggle.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(70, 75, 85), 1, true),
            new EmptyBorder(4, 10, 4, 10)
        ));
        gridToggle.addActionListener(e -> canvas.setShowGrid(gridToggle.isSelected()));

        add(zoomInBtn);
        add(zoomOutBtn);
        add(resetZoomBtn);
        add(gridToggle);
    }

    public void setParentFrame(JFrame frame) {
        this.parentFrame = frame;
    }

    public JMenuBar createMenuBar(JFrame frame) {
        this.parentFrame = frame;
        JMenuBar mb = new JMenuBar();

        // FILE MENU
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem newItem = new JMenuItem("New Canvas...");
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newItem.addActionListener(e -> promptNewCanvas());

        JMenuItem exportItem = new JMenuItem("Export as PNG...");
        exportItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
        exportItem.addActionListener(e -> ExportDialog.showExportDialog(parentFrame, canvas));

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(newItem);
        fileMenu.add(exportItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // EDIT MENU
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);

        JMenuItem undoItem = new JMenuItem("Undo");
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        undoItem.addActionListener(e -> canvas.doUndo());

        JMenuItem redoItem = new JMenuItem("Redo");
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        redoItem.addActionListener(e -> canvas.doRedo());

        JMenuItem clearItem = new JMenuItem("Clear Canvas");
        clearItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_DOWN_MASK));
        clearItem.addActionListener(e -> canvas.clearCanvas());

        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.addSeparator();
        editMenu.add(clearItem);

        // VIEW MENU
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);

        JMenuItem zoomInItem = new JMenuItem("Zoom In");
        zoomInItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK));
        zoomInItem.addActionListener(e -> canvas.zoomIn());

        JMenuItem zoomOutItem = new JMenuItem("Zoom Out");
        zoomOutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK));
        zoomOutItem.addActionListener(e -> canvas.zoomOut());

        JMenuItem resetZoomItem = new JMenuItem("Reset Zoom");
        resetZoomItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, InputEvent.CTRL_DOWN_MASK));
        resetZoomItem.addActionListener(e -> canvas.resetZoom());

        JCheckBoxMenuItem gridItem = new JCheckBoxMenuItem("Show Grid", canvas.isShowGrid());
        gridItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
        gridItem.addActionListener(e -> canvas.setShowGrid(gridItem.isSelected()));

        viewMenu.add(zoomInItem);
        viewMenu.add(zoomOutItem);
        viewMenu.add(resetZoomItem);
        viewMenu.addSeparator();
        viewMenu.add(gridItem);

        // HELP MENU
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        JMenuItem shortcutsItem = new JMenuItem("Keyboard Shortcuts");
        shortcutsItem.addActionListener(e -> showShortcutsDialog());

        JMenuItem aboutItem = new JMenuItem("About Pixelate");
        aboutItem.addActionListener(e -> showAboutDialog());

        helpMenu.add(shortcutsItem);
        helpMenu.add(aboutItem);

        mb.add(fileMenu);
        mb.add(editMenu);
        mb.add(viewMenu);
        mb.add(helpMenu);

        return mb;
    }

    private void promptNewCanvas() {
        Object[] options = {"16 × 16", "32 × 32", "64 × 64 (Default)", "128 × 128", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
            parentFrame,
            "Select canvas dimensions for the new canvas:\n(Any unsaved changes will be lost)",
            "New Canvas",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[2]
        );

        int newSize = -1;
        if (choice == 0) newSize = 16;
        else if (choice == 1) newSize = 32;
        else if (choice == 2) newSize = 64;
        else if (choice == 3) newSize = 128;

        if (newSize > 0) {
            canvas.resetCanvas(newSize);
        }
    }

    private void showShortcutsDialog() {
        String msg = "<html><body style='font-family: sans-serif; padding: 6px;'>" +
            "<h3>Keyboard Shortcuts</h3>" +
            "<table>" +
            "<tr><td><b>Ctrl + E</b></td><td>Export as PNG</td></tr>" +
            "<tr><td><b>Ctrl + N</b></td><td>New Canvas</td></tr>" +
            "<tr><td><b>Ctrl + Z</b></td><td>Undo</td></tr>" +
            "<tr><td><b>Ctrl + Y</b></td><td>Redo</td></tr>" +
            "<tr><td><b>Ctrl + + / -</b></td><td>Zoom In / Out</td></tr>" +
            "<tr><td><b>Ctrl + 0</b></td><td>Reset Zoom</td></tr>" +
            "<tr><td><b>Ctrl + G</b></td><td>Toggle Grid</td></tr>" +
            "<tr><td><b>B</b></td><td>Pencil Tool</td></tr>" +
            "<tr><td><b>E</b></td><td>Eraser Tool</td></tr>" +
            "<tr><td><b>G</b></td><td>Paint Bucket Fill</td></tr>" +
            "<tr><td><b>I</b></td><td>Color Picker / Eyedropper</td></tr>" +
            "<tr><td><b>L</b></td><td>Line Tool</td></tr>" +
            "<tr><td><b>R</b></td><td>Rectangle Tool (Hold Shift for Square)</td></tr>" +
            "<tr><td><b>C</b></td><td>Circle Tool (Hold Shift for 1:1 Circle)</td></tr>" +
            "<tr><td><b>F</b></td><td>Toggle Filled / Outline Shapes</td></tr>" +
            "<tr><td><b>Right Click</b></td><td>Quick Erase Pixel</td></tr>" +
            "</table></body></html>";

        JOptionPane.showMessageDialog(parentFrame, new JLabel(msg), "Keyboard Shortcuts", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAboutDialog() {
        String msg = "<html><body style='font-family: sans-serif; text-align: center; padding: 10px;'>" +
            "<h2 style='margin-bottom: 2px;'>Pixelate</h2>" +
            "<p style='color: #666;'>A lightweight, modern Pixel Art Editor in Java Swing.</p>" +
            "<hr style='border: 0; border-top: 1px solid #ccc;'/>" +
            "<p>Supports PNG export at multi-scale resolutions, transparency, customizable palettes, and fluid pixel manipulation.</p>" +
            "</body></html>";

        JOptionPane.showMessageDialog(parentFrame, new JLabel(msg), "About Pixelate", JOptionPane.INFORMATION_MESSAGE);
    }

    private JButton createToolbarButton(String text, String tooltip) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setBackground(new Color(50, 54, 60));
        btn.setForeground(new Color(225, 230, 240));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setToolTipText(tooltip);
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(70, 75, 85), 1, true),
            new EmptyBorder(4, 10, 4, 10)
        ));
        return btn;
    }

    private JComponent createToolbarSeparator() {
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 24));
        sep.setForeground(new Color(65, 70, 80));
        return sep;
    }
}
