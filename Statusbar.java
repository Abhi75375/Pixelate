import java.awt.Color; // This must be uncommented
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.*;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Statusbar extends JPanel {
    JLabel xLabel;
    JLabel yLabel;
    JLabel colorLabel;
    Canvas canvas;

    public Statusbar(Canvas canvas) {
        this.canvas = canvas;

        setBackground(Color.GRAY);
        setPreferredSize(new Dimension(520, 35));
        setLayout(new FlowLayout(FlowLayout.LEFT));

        xLabel = new JLabel("X : 0");
        yLabel = new JLabel("Y : 0");
        colorLabel = new JLabel("Color : Black");

        add(xLabel);
        add(yLabel);
        add(colorLabel);

        // We add a listener directly to the canvas so the Statusbar knows exactly when the mouse moves
        this.canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateLabels(e.getX(), e.getY());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                updateLabels(e.getX(), e.getY());
            }
        });
    }

    // Moved the floating code into a proper method
    public void updateLabels(int rawX, int rawY) {
        // Convert to grid coordinates
        int gridX = rawX / canvas.pixelSize;
        int gridY = rawY / canvas.pixelSize;
        
        // Clamp bounds so UI doesn't show negative numbers if mouse leaves the screen
        gridX = Math.max(0, Math.min(gridX, canvas.gridSize - 1));
        gridY = Math.max(0, Math.min(gridY, canvas.gridSize - 1));

        xLabel.setText("X: " + gridX);
        yLabel.setText("Y: " + gridY);
        
        Color color = canvas.getCurrentColor();
        colorLabel.setText("Color: " + getColorName(color));
    }

    private String getColorName(Color color) {
        if (color.equals(Color.GREEN)) return "Green";
        else if (color.equals(Color.RED)) return "Red";
        else if (color.equals(Color.BLUE)) return "Blue";
        else if (color.equals(Color.BLACK)) return "Black";
        else return "Custom";
    }
}