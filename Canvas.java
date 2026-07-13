import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class Canvas extends JPanel{
    int truePixelSize = 8; //size of a pixel
    int scale = 3; //magnification factor(trust me this will be useful later)
    int pixelSize = truePixelSize * scale;//actual size of each individual "pixel"
    int gridSize = 32; //if this is n, the canvas is n x n "pixels"
    int canvasSize = pixelSize * gridSize;//actual size of canvas in px

    Color colors[][] = new Color[gridSize][gridSize];


    Canvas(){
        this.setPreferredSize(new Dimension(canvasSize,canvasSize));
        newPixels();
    }
    public void newPixels(){
        for(int i=0;i<gridSize;i++){
            for(int j=0; j<gridSize;j++){
                colors[i][j]=Color.lightGray;
            }
        }
    }

    public void paintComponent(Graphics g){
        drawPixels(g);
        drawGrid(g);
        
    }


    public void drawGrid(Graphics g){
        g.setColor(Color.black);
        for(int i= 0; i<canvasSize;i+=pixelSize){
            g.drawLine(i,0,i,canvasSize);
            g.drawLine(0,i,canvasSize, i);
        }
    }

    public void drawPixels(Graphics g){
        colors[6][7]=Color.blue;
        colors[7][7]=Color.blue;
        for(int i=0; i < gridSize; i++){
            for(int j=0; j< gridSize; j++){
                g.setColor(colors[i][j]);
                g.fillRect(i*pixelSize,j*pixelSize,pixelSize,pixelSize);
            }
        }
    }
}
