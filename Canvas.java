import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Queue;

public class Canvas extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

    public enum Tool {
        PENCIL("Pencil"),
        ERASER("Eraser"),
        BUCKET("Bucket"),
        PICKER("Color Picker"),
        LINE("Line"),
        RECTANGLE("Rectangle"),
        CIRCLE("Circle");

        private final String label;
        Tool(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    public interface CanvasListener {
        void onColorChanged(Color color);
        void onToolChanged(Tool tool);
        void onZoomChanged(float scale);
        void onStatusMessage(String message);
        void onCanvasReset();
    }

    int truePixelSize = 8;
    float scale = 3.0f;
    int pixelSize = (int)(truePixelSize * scale);
    int gridSize = 64;
    int canvasSize = pixelSize * gridSize;

    Color[][] colors = new Color[gridSize][gridSize];
    int[] last = {-1, -1};

    private Tool currentTool = Tool.PENCIL;
    Color currentColor = Color.BLACK;
    private boolean showGrid = true;
    private boolean fillShapes = false;

    // Shape drag state
    private boolean isDraggingShape = false;
    private int dragStartX = -1, dragStartY = -1;
    private int dragEndX = -1, dragEndY = -1;
    private boolean isShiftDown = false;

    ZoomListener zoomListener;

    private final List<CanvasListener> listeners = new ArrayList<>();

    public Canvas() {
        setPreferredSize(new Dimension(canvasSize, canvasSize));
        setFocusable(true);
        setBackground(new Color(30, 30, 30));

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);

        newPixels();
        History.init(gridSize);
    }

    public void addCanvasListener(CanvasListener listener) {
        listeners.add(listener);
    }

    public void notifyStatusMessage(String msg) {
        for (CanvasListener l : listeners) {
            l.onStatusMessage(msg);
        }
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
        for (CanvasListener l : listeners) {
            l.onColorChanged(currentColor);
        }
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public Tool getCurrentTool() {
        return currentTool;
    }

    public void setCurrentTool(Tool tool) {
        this.currentTool = tool;
        for (CanvasListener l : listeners) {
            l.onToolChanged(currentTool);
        }
    }

    public boolean isShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
        repaint();
    }

    public void toggleGrid() {
        setShowGrid(!showGrid);
    }

    public boolean isFillShapes() {
        return fillShapes;
    }

    public void setFillShapes(boolean fillShapes) {
        this.fillShapes = fillShapes;
        notifyStatusMessage("Shape Fill: " + (fillShapes ? "ON" : "OFF"));
        repaint();
    }

    public void toggleFillShapes() {
        setFillShapes(!fillShapes);
    }

    // Backwards-compatibility methods
    public boolean checkPickerOn() {
        return currentTool == Tool.PICKER;
    }

    public void setPickerOn() {
        if (currentTool == Tool.PICKER) {
            setCurrentTool(Tool.PENCIL);
        } else {
            setCurrentTool(Tool.PICKER);
        }
    }

    public boolean checkFillBucket() {
        return currentTool == Tool.BUCKET;
    }

    public void setFillBucket() {
        if (currentTool == Tool.BUCKET) {
            setCurrentTool(Tool.PENCIL);
        } else {
            setCurrentTool(Tool.BUCKET);
        }
    }

    public boolean ColorCompare(Color color1, Color color2) {
        return Objects.equals(color1, color2);
    }

    public void newPixels() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                colors[i][j] = null;
            }
        }
    }

    public void clearCanvas() {
        History.canvasGonnaChange(colors);
        newPixels();
        repaint();
        notifyStatusMessage("Canvas cleared");
    }

    public void resetCanvas(int newGridSize) {
        this.gridSize = newGridSize;
        this.colors = new Color[gridSize][gridSize];
        newPixels();
        History.init(gridSize);
        resizeCanvas();
        for (CanvasListener l : listeners) {
            l.onCanvasReset();
        }
        notifyStatusMessage("New canvas created (" + gridSize + "x" + gridSize + ")");
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        drawPixels(g2);

        // Draw live shape preview overlay when dragging
        if (isDraggingShape) {
            List<Point> previewPoints = getShapePoints(dragStartX, dragStartY, dragEndX, dragEndY, currentTool, fillShapes, isShiftDown);
            g2.setColor(currentColor != null ? currentColor : Color.BLACK);
            for (Point p : previewPoints) {
                if (p.x >= 0 && p.x < gridSize && p.y >= 0 && p.y < gridSize) {
                    g2.fillRect(p.x * pixelSize, p.y * pixelSize, pixelSize, pixelSize);
                }
            }
        }

        if (showGrid && pixelSize >= 4) {
            drawGrid(g2);
        }

        // Canvas outer crisp border
        g2.setColor(new Color(60, 60, 60));
        g2.drawRect(0, 0, canvasSize - 1, canvasSize - 1);

        g2.dispose();
    }

    public void drawPixels(Graphics g) {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                int px = i * pixelSize;
                int py = j * pixelSize;
                if (colors[i][j] == null) {
                    boolean even = ((i + j) % 2 == 0);
                    g.setColor(even ? new Color(240, 240, 242) : new Color(220, 222, 226));
                    g.fillRect(px, py, pixelSize, pixelSize);
                } else {
                    g.setColor(colors[i][j]);
                    g.fillRect(px, py, pixelSize, pixelSize);
                }
            }
        }
    }

    public void drawGrid(Graphics g) {
        g.setColor(new Color(0, 0, 0, 40));
        for (int i = 0; i <= canvasSize; i += pixelSize) {
            g.drawLine(i, 0, i, canvasSize);
            g.drawLine(0, i, canvasSize, i);
        }
    }

    public void resizeCanvas() {
        pixelSize = (int)(truePixelSize * scale);
        if (pixelSize < 1) pixelSize = 1;
        canvasSize = gridSize * pixelSize;
        setPreferredSize(new Dimension(canvasSize, canvasSize));
        revalidate();
        repaint();
        for (CanvasListener l : listeners) {
            l.onZoomChanged(scale);
        }
    }

    public void zoomIn() {
        if (scale < 15.0f) {
            scale = Math.round((scale + 0.5f) * 10.0f) / 10.0f;
            resizeCanvas();
        }
    }

    public void zoomOut() {
        if (scale > 0.6f) {
            scale = Math.round((scale - 0.5f) * 10.0f) / 10.0f;
            resizeCanvas();
        }
    }

    public void resetZoom() {
        scale = 3.0f;
        resizeCanvas();
    }

    public void doUndo() {
        History.undo(colors);
        repaint();
        notifyStatusMessage("Undo performed");
    }

    public void doRedo() {
        History.redo(colors);
        repaint();
        notifyStatusMessage("Redo performed");
    }

    public void setZoomListener(ZoomListener listener) {
        this.zoomListener = listener;
    }

    // BFS Flood Fill - completely eliminates stack overflow risk
    public void runFillBucket(int startX, int startY, Color originalColor) {
        Color targetColor = currentColor;
        if (ColorCompare(originalColor, targetColor)) {
            return;
        }

        Queue<Point> queue = new ArrayDeque<>();
        queue.add(new Point(startX, startY));
        colors[startX][startY] = targetColor;

        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            for (int i = 0; i < 4; i++) {
                int nx = p.x + dx[i];
                int ny = p.y + dy[i];
                if (nx >= 0 && nx < gridSize && ny >= 0 && ny < gridSize) {
                    if (ColorCompare(colors[nx][ny], originalColor)) {
                        colors[nx][ny] = targetColor;
                        queue.add(new Point(nx, ny));
                    }
                }
            }
        }
    }

    // Shape Geometry Algorithms
    public List<Point> getShapePoints(int x0, int y0, int x1, int y1, Tool tool, boolean filled, boolean shift) {
        switch (tool) {
            case LINE:
                return getLinePixels(x0, y0, x1, y1);
            case RECTANGLE:
                return getRectPixels(x0, y0, x1, y1, filled, shift);
            case CIRCLE:
                return getCirclePixels(x0, y0, x1, y1, filled, shift);
            default:
                return Collections.emptyList();
        }
    }

    public static List<Point> getLinePixels(int x0, int y0, int x1, int y1) {
        List<Point> points = new ArrayList<>();
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        int currX = x0;
        int currY = y0;

        while (true) {
            points.add(new Point(currX, currY));
            if (currX == x1 && currY == y1) break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                currX += sx;
            }
            if (e2 < dx) {
                err += dx;
                currY += sy;
            }
        }
        return points;
    }

    public static List<Point> getRectPixels(int x0, int y0, int x1, int y1, boolean filled, boolean square) {
        List<Point> points = new ArrayList<>();
        if (square) {
            int w = Math.abs(x1 - x0);
            int h = Math.abs(y1 - y0);
            int side = Math.max(w, h);
            x1 = x0 + (x1 >= x0 ? side : -side);
            y1 = y0 + (y1 >= y0 ? side : -side);
        }
        int minX = Math.min(x0, x1);
        int maxX = Math.max(x0, x1);
        int minY = Math.min(y0, y1);
        int maxY = Math.max(y0, y1);

        if (filled) {
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    points.add(new Point(x, y));
                }
            }
        } else {
            for (int x = minX; x <= maxX; x++) {
                points.add(new Point(x, minY));
                if (minY != maxY) {
                    points.add(new Point(x, maxY));
                }
            }
            for (int y = minY + 1; y < maxY; y++) {
                points.add(new Point(minX, y));
                if (minX != maxX) {
                    points.add(new Point(maxX, y));
                }
            }
        }
        return points;
    }

    public static List<Point> getCirclePixels(int x0, int y0, int x1, int y1, boolean filled, boolean perfectCircle) {
        Set<Point> points = new LinkedHashSet<>();
        if (perfectCircle) {
            int w = Math.abs(x1 - x0);
            int h = Math.abs(y1 - y0);
            int side = Math.max(w, h);
            x1 = x0 + (x1 >= x0 ? side : -side);
            y1 = y0 + (y1 >= y0 ? side : -side);
        }
        int minX = Math.min(x0, x1);
        int maxX = Math.max(x0, x1);
        int minY = Math.min(y0, y1);
        int maxY = Math.max(y0, y1);

        double rx = (maxX - minX) / 2.0;
        double ry = (maxY - minY) / 2.0;
        double cx = minX + rx;
        double cy = minY + ry;

        if (rx <= 0.5 && ry <= 0.5) {
            points.add(new Point(minX, minY));
            return new ArrayList<>(points);
        }

        if (filled) {
            for (int y = minY; y <= maxY; y++) {
                double dy = (y + 0.5 - cy) / (ry == 0 ? 0.5 : ry);
                double dy2 = dy * dy;
                if (dy2 <= 1.0) {
                    double span = (rx == 0 ? 0.5 : rx) * Math.sqrt(Math.max(0, 1.0 - dy2));
                    int startX = (int) Math.round(cx - span);
                    int endX = (int) Math.round(cx + span - 0.5);
                    startX = Math.max(minX, startX);
                    endX = Math.min(maxX, endX);
                    for (int x = startX; x <= endX; x++) {
                        points.add(new Point(x, y));
                    }
                }
            }
        } else {
            drawEllipseOutline(minX, minY, maxX, maxY, points);
        }
        return new ArrayList<>(points);
    }

    private static void drawEllipseOutline(int x0, int y0, int x1, int y1, Set<Point> points) {
        int a = Math.abs(x1 - x0), b = Math.abs(y1 - y0), b1 = b & 1;
        long dx = 4L * (1 - a) * b * b, dy = 4L * (b1 + 1) * a * a;
        long err = dx + dy + (long) b1 * a * a, e2;

        if (x0 > x1) { x0 = x1; x1 += a; }
        if (y0 > y1) { y0 = y1; }
        y0 += (b + 1) / 2;
        y1 = y0 - b1;
        a *= 8 * a;
        b1 = 8 * b * b;

        do {
            points.add(new Point(x1, y0));
            points.add(new Point(x0, y0));
            points.add(new Point(x0, y1));
            points.add(new Point(x1, y1));
            e2 = 2 * err;
            if (e2 <= dy) { y0++; y1--; err += dy += a; }
            if (e2 >= dx || 2 * err > dy) { x0++; x1--; err += dx += b1; }
        } while (x0 <= x1);

        while (y0 - y1 <= b) {
            points.add(new Point(x0 - 1, y0));
            points.add(new Point(x1 + 1, y0++));
            points.add(new Point(x0 - 1, y1));
            points.add(new Point(x1 + 1, y1--));
        }
    }

    private boolean isShapeTool(Tool tool) {
        return tool == Tool.LINE || tool == Tool.RECTANGLE || tool == Tool.CIRCLE;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX() / pixelSize;
        int y = e.getY() / pixelSize;

        if (x < 0 || x >= gridSize || y < 0 || y >= gridSize) {
            return;
        }

        if (SwingUtilities.isRightMouseButton(e)) {
            // Quick erase on right click
            History.canvasGonnaChange(colors);
            colors[x][y] = null;
            repaint(x * pixelSize, y * pixelSize, pixelSize, pixelSize);
            last[0] = x;
            last[1] = y;
            return;
        }

        if (SwingUtilities.isLeftMouseButton(e)) {
            if (isShapeTool(currentTool)) {
                dragStartX = x;
                dragStartY = y;
                dragEndX = x;
                dragEndY = y;
                isShiftDown = e.isShiftDown();
                isDraggingShape = true;
                repaint();
                return;
            }

            switch (currentTool) {
                case PICKER:
                    if (colors[x][y] != null) {
                        setCurrentColor(colors[x][y]);
                    }
                    setCurrentTool(Tool.PENCIL);
                    notifyStatusMessage("Picked color: " + (colors[x][y] == null ? "None" : "RGB(" + colors[x][y].getRed() + "," + colors[x][y].getGreen() + "," + colors[x][y].getBlue() + ")"));
                    break;

                case BUCKET:
                    if (!ColorCompare(colors[x][y], currentColor)) {
                        History.canvasGonnaChange(colors);
                        runFillBucket(x, y, colors[x][y]);
                        repaint();
                    }
                    break;

                case ERASER:
                    History.canvasGonnaChange(colors);
                    colors[x][y] = null;
                    repaint(x * pixelSize, y * pixelSize, pixelSize, pixelSize);
                    last[0] = x;
                    last[1] = y;
                    break;

                case PENCIL:
                default:
                    History.canvasGonnaChange(colors);
                    colors[x][y] = currentColor;
                    repaint(x * pixelSize, y * pixelSize, pixelSize, pixelSize);
                    last[0] = x;
                    last[1] = y;
                    break;
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int x = e.getX() / pixelSize;
        int y = e.getY() / pixelSize;

        if (isDraggingShape) {
            dragEndX = Math.max(0, Math.min(x, gridSize - 1));
            dragEndY = Math.max(0, Math.min(y, gridSize - 1));
            isShiftDown = e.isShiftDown();
            repaint();
            return;
        }

        if (x < 0 || x >= gridSize || y < 0 || y >= gridSize) {
            return;
        }

        if (last[0] != x || last[1] != y) {
            if (SwingUtilities.isRightMouseButton(e) || currentTool == Tool.ERASER) {
                colors[x][y] = null;
                repaint(x * pixelSize, y * pixelSize, pixelSize, pixelSize);
            } else if (currentTool == Tool.PENCIL) {
                colors[x][y] = currentColor;
                repaint(x * pixelSize, y * pixelSize, pixelSize, pixelSize);
            }
            last[0] = x;
            last[1] = y;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isDraggingShape) {
            isDraggingShape = false;
            int x = e.getX() / pixelSize;
            int y = e.getY() / pixelSize;
            dragEndX = Math.max(0, Math.min(x, gridSize - 1));
            dragEndY = Math.max(0, Math.min(y, gridSize - 1));
            isShiftDown = e.isShiftDown();

            List<Point> shapePoints = getShapePoints(dragStartX, dragStartY, dragEndX, dragEndY, currentTool, fillShapes, isShiftDown);
            if (!shapePoints.isEmpty()) {
                History.canvasGonnaChange(colors);
                for (Point p : shapePoints) {
                    if (p.x >= 0 && p.x < gridSize && p.y >= 0 && p.y < gridSize) {
                        colors[p.x][p.y] = currentColor;
                    }
                }
                repaint();
                notifyStatusMessage("Drawn " + currentTool.getLabel());
            }
        }

        last[0] = -1;
        last[1] = -1;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (zoomListener != null) {
            zoomListener.zoomRequested(e.getWheelRotation());
        }
    }
}
