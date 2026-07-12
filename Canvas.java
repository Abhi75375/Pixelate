import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class Canvas extends JPanel{
    final int truePixelSize = 8; //size of a pixel
    final int scale = 3; //magnification factor(trust me this will be useful later)
    final int pixelSize = truePixelSize * scale;//actual size of each individual "pixel"
    final int gridSize = 32; //if this is n, the canvas is n x n "pixels"
    final int canvasSize = pixelSize * gridSize;//actual size of canvas in px


    Canvas(){
        this.setPreferredSize(new Dimension(canvasSize,canvasSize));
        this.setBackground(Color.lightGray);
    }

    public void paint(Graphics g){
        Graphics2D g2d = (Graphics2D) g;//casting graphics object to a graphics2d object
        drawGrid(g2d);
    }


    public void drawGrid(Graphics2D g2){
        for(int i= 0; i<=canvasSize;i+=pixelSize){
            g2.drawLine(i,0,i,canvasSize);
            g2.drawLine(0,i,canvasSize, i);
        }
    }
}
