import java.awt.event.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import javax.swing.JPanel;

public class Canvas extends JPanel implements KeyListener,MouseListener, MouseMotionListener, MouseWheelListener{
    int truePixelSize = 8; //size of a pixel
    int scale = 3; //magnification factor(trust me this will be useful later)
    int pixelSize = truePixelSize * scale;//actual size of each individual "pixel"
    int gridSize = 32; //if this is n, the canvas is n x n "pixels"
    int canvasSize = pixelSize * gridSize;//actual size of canvas in px

    Color currentColor = Color.green;
    Color colors[][] = new Color[gridSize][gridSize];
    int last[]={-1,-1};// stores the last updated pixel while dragging

    ZoomListener zoomListener;


    Canvas(){
        this.setPreferredSize(new Dimension(canvasSize,canvasSize));
        newPixels();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        this.addKeyListener(this);
        this.setFocusable(true);

    }
    public void newPixels(){
        for(int i=0;i<gridSize;i++){
            for(int j=0; j<gridSize;j++){
                colors[i][j]=null;
            }
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        drawPixels(g);
        drawGrid(g);
        
    }

    public void drawPixels(Graphics g){
        if(g.getClip().getBounds().getWidth()!=pixelSize){
            for(int i=0; i < gridSize; i++){
                for(int j=0; j< gridSize; j++){
                    if(colors[i][j]==null)
                        g.setColor(Color.lightGray);
                    else
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
        for(int i= 0; i<=canvasSize;i+=pixelSize){
            g.drawLine(i,0,i,canvasSize);
            g.drawLine(0,i,canvasSize, i);
        }
    }

    public void resizeCanvas(){
        pixelSize = truePixelSize * scale;
        canvasSize = gridSize * pixelSize;
        setPreferredSize(new Dimension(canvasSize,canvasSize));
        revalidate();
        repaint();
    }
    



    //Event Listener Methods

    public void setZoomListener(ZoomListener listener){
        zoomListener = listener;//frame
    }

    @Override
    public void keyPressed(KeyEvent e){
        if(e.getKeyCode()==KeyEvent.VK_EQUALS){ //for now press + for zoom in
            scale++;
            resizeCanvas();
        }
        
        if(e.getKeyCode()==KeyEvent.VK_MINUS){ //and - for zoom out
            if(scale>1){
                scale--;
                resizeCanvas();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e){
        //idk
    }

    @Override
    public void keyTyped(KeyEvent e){
        //idk
    }

	@Override
	public void mouseClicked(MouseEvent e) {
        int x=(int)(e.getX() / pixelSize);
        int y=(int)(e.getY() / pixelSize);
        
        //out of bounds fix
        if(x>=gridSize||x<0||y>=gridSize||y<0)
            return;

        colors[x][y]=currentColor;
        repaint(x*pixelSize,y*pixelSize,pixelSize,pixelSize);
	}
    @Override
    public void mouseDragged(MouseEvent e){
        int x=(int)(e.getX() / pixelSize);
        int y=(int)(e.getY() / pixelSize);

        //out of bounds fix
        if(x>=gridSize||x<0||y>=gridSize||y<0)
            return;

        colors[x][y]=currentColor;
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

    @Override
    public void mouseWheelMoved(MouseWheelEvent e){
        zoomListener.zoomRequested(e.getX(),e.getY(),e.getWheelRotation());
    }
    
}
