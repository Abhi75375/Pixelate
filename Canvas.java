import java.awt.event.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Canvas extends JPanel implements MouseListener, MouseMotionListener{
    int truePixelSize = 8; //size of a pixel
    int scale = 3; //magnification factor(trust me this will be useful later)
    int pixelSize = truePixelSize * scale;//actual size of each individual "pixel"
    int gridSize = 32; //if this is n, the canvas is n x n "pixels"
    int canvasSize = pixelSize * gridSize;//actual size of canvas in px

    Color colors[][] = new Color[gridSize][gridSize];
    int last[]={-1,-1};// stores the last updated pixel while dragging


    Canvas(){
        this.setPreferredSize(new Dimension(canvasSize,canvasSize));
        newPixels();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
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

    public void drawPixels(Graphics g){
        if(g.getClip().getBounds().getWidth()!=pixelSize){
            for(int i=0; i < gridSize; i++){
                for(int j=0; j< gridSize; j++){
                    g.setColor(colors[i][j]);
                    g.fillRect(i*pixelSize,j*pixelSize,pixelSize,pixelSize);
                }
            }
        }
        else{
            int x = (int)g.getClip().getBounds().getX() / pixelSize;
            int y = (int)g.getClip().getBounds().getY() / pixelSize;
            g.setColor(colors[x][y]);
            g.fillRect(x*pixelSize,y*pixelSize,pixelSize,pixelSize);
        }
    }

    public void drawGrid(Graphics g){
        g.setColor(Color.black);
        for(int i= 0; i<canvasSize;i+=pixelSize){
            g.drawLine(i,0,i,canvasSize);
            g.drawLine(0,i,canvasSize, i);
        }
    }

	@Override
	public void mouseClicked(MouseEvent e) {
        int x=(int)(e.getX() / pixelSize);
        int y=(int)(e.getY() / pixelSize);
        colors[x][y]=Color.green;
        repaint(x*pixelSize,y*pixelSize,pixelSize,pixelSize);
	}
    @Override
    public void mouseDragged(MouseEvent e){
        int x=(int)(e.getX() / pixelSize);
        int y=(int)(e.getY() / pixelSize);
        colors[x][y]=Color.green;
        if(last[0]!=x || last[1]!=y){ //this is why i need the last[]
                                      //prevents unnecessary calling of repaint
            repaint(x*pixelSize,y*pixelSize,pixelSize,pixelSize);
            last[0]=x;last[1]=y;
        }
    }
	@Override
	public void mouseMoved(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
	}
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
    
}
