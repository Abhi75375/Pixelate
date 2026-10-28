import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class Frame extends JFrame implements ZoomListener {

    static final int WIDTH = 1200;
    static final int HEIGHT = 800;
    Canvas canvas;
    JScrollPane scrollpane;
    Menubar menubar;

    Frame() {
        this.setTitle("Pixelate - Pixel Art Editor");
        this.setSize(WIDTH, HEIGHT);
        this.setMinimumSize(new Dimension(850, 600));
        this.setResizable(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        canvas = new Canvas();

        // Center canvas inside scroll pane with dark workspace surround
        JPanel canvasWrapper = new JPanel(new GridBagLayout());
        canvasWrapper.setBackground(new Color(24, 25, 28));
        canvasWrapper.add(canvas);

        scrollpane = new JScrollPane(canvasWrapper);
        scrollpane.setBorder(BorderFactory.createEmptyBorder());
        scrollpane.getHorizontalScrollBar().setUnitIncrement(24);
        scrollpane.getVerticalScrollBar().setUnitIncrement(24);
        this.add(scrollpane, BorderLayout.CENTER);

        Statusbar statusbar = new Statusbar(canvas);
        this.add(statusbar, BorderLayout.SOUTH);

        menubar = new Menubar(canvas);
        menubar.setParentFrame(this);
        this.setJMenuBar(menubar.createMenuBar(this));
        this.add(menubar, BorderLayout.NORTH);

        Sidebar sidebar = new Sidebar(canvas);
        this.add(sidebar, BorderLayout.WEST);

        canvas.setZoomListener(this);
        setupKeyBindings();

        this.setVisible(true);

        // Center scrollpane view on canvas
        SwingUtilities.invokeLater(() -> {
            JScrollBar h = scrollpane.getHorizontalScrollBar();
            JScrollBar v = scrollpane.getVerticalScrollBar();
            h.setValue((h.getMaximum() - h.getVisibleAmount()) / 2);
            v.setValue((v.getMaximum() - v.getVisibleAmount()) / 2);
        });
    }

    void setupKeyBindings() {
        // Ctrl + E: Export as PNG
        KeyBindings.bind(
            canvas,
            KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK,
            "exportPNG",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ExportDialog.showExportDialog(Frame.this, canvas);
                }
            }
        );

        // Ctrl + = (or Ctrl + Plus): Zoom In
        KeyBindings.bind(
            canvas,
            KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK,
            "zoomIn",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    canvas.zoomIn();
                }
            }
        );

        // Ctrl + - : Zoom Out
        KeyBindings.bind(
            canvas,
            KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK,
            "zoomOut",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    canvas.zoomOut();
                }
            }
        );

        // Ctrl + 0 : Reset Zoom
        KeyBindings.bind(
            canvas,
            KeyEvent.VK_0, InputEvent.CTRL_DOWN_MASK,
            "resetZoom",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    canvas.resetZoom();
                }
            }
        );

        // Ctrl + Z: Undo
        KeyBindings.bind(
            canvas,
            KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK,
            "undo",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    canvas.doUndo();
                }
            }
        );

        // Ctrl + Y: Redo
        KeyBindings.bind(
            canvas,
            KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK,
            "redo",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    canvas.doRedo();
                }
            }
        );

        // Ctrl + G: Toggle Grid
        KeyBindings.bind(
            canvas,
            KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK,
            "toggleGrid",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    canvas.toggleGrid();
                }
            }
        );

        // Tool Shortcuts: B (Pencil), E (Eraser), G (Bucket), I (Eyedropper)
        KeyBindings.bind(
            canvas,
            KeyEvent.VK_B, 0,
            "toolPencil",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    canvas.setCurrentTool(Canvas.Tool.PENCIL);
                }
            }
        );

        KeyBindings.bind(
            canvas,
            KeyEvent.VK_E, 0,
            "toolEraser",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    canvas.setCurrentTool(Canvas.Tool.ERASER);
                }
            }
        );

        KeyBindings.bind(
            canvas,
            KeyEvent.VK_G, 0,
            "toolBucket",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    canvas.setCurrentTool(Canvas.Tool.BUCKET);
                }
            }
        );

        KeyBindings.bind(
            canvas,
            KeyEvent.VK_I, 0,
            "toolPicker",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    canvas.setCurrentTool(Canvas.Tool.PICKER);
                }
            }
        );

        // Shape Shortcuts: L (Line), R (Rectangle), C (Circle), F (Toggle Fill)
        KeyBindings.bind(
            canvas,
            KeyEvent.VK_L, 0,
            "toolLine",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    canvas.setCurrentTool(Canvas.Tool.LINE);
                }
            }
        );

        KeyBindings.bind(
            canvas,
            KeyEvent.VK_R, 0,
            "toolRect",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    canvas.setCurrentTool(Canvas.Tool.RECTANGLE);
                }
            }
        );

        KeyBindings.bind(
            canvas,
            KeyEvent.VK_C, 0,
            "toolCircle",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    canvas.setCurrentTool(Canvas.Tool.CIRCLE);
                }
            }
        );

        KeyBindings.bind(
            canvas,
            KeyEvent.VK_F, 0,
            "toggleFill",
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    canvas.toggleFillShapes();
                }
            }
        );
    }

    @Override
    public void zoomRequested(int rotation) {
        Point mousePos = canvas.getMousePosition();
        int oldX = (mousePos != null) ? mousePos.x / canvas.pixelSize : canvas.gridSize / 2;
        int oldY = (mousePos != null) ? mousePos.y / canvas.pixelSize : canvas.gridSize / 2;

        if (rotation < 0) {
            canvas.zoomIn();
        } else {
            canvas.zoomOut();
        }

        Point newMousePos = canvas.getMousePosition();
        int newX = (newMousePos != null) ? newMousePos.x / canvas.pixelSize : canvas.gridSize / 2;
        int newY = (newMousePos != null) ? newMousePos.y / canvas.pixelSize : canvas.gridSize / 2;

        int dx = (oldX - newX) * canvas.pixelSize;
        int dy = (oldY - newY) * canvas.pixelSize;
        JScrollBar h = scrollpane.getHorizontalScrollBar();
        JScrollBar v = scrollpane.getVerticalScrollBar();
        h.setValue(h.getValue() + dx);
        v.setValue(v.getValue() + dy);
    }
}
