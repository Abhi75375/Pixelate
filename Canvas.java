import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;           
import java.awt.event.*;
import javax.swing.JPanel;

public class Canvas extends JPanel implements KeyListener,MouseListener, MouseMotionListener, MouseWheelListener{
    int truePixelSize = 8; //size of a pixel
    float scale = 3; //magnification factor(trust me this will be useful later)
    int pixelSize = (int)(truePixelSize * scale);//actual size of each individual "pixel"
    int gridSize = 32; //if this is n, the canvas is n x n "pixels"
    int canvasSize = pixelSize * gridSize;//actual size of canvas in px

    Color colors[][] = new Color[gridSize][gridSize];
    int last[]={-1,-1};// stores the last updated pixel while dragging
    

    ZoomListener zoomListener;
//Color
    Color currentColor = new Color(0,0,0);   
    public void setCurrentColor(Color color){currentColor = color;}
    public Color getCurrentColor(){return currentColor;}
//ColorPicker
    public boolean colorPicker = false;
    public boolean checkPickerOn(){if(colorPicker){return true;}else{return false;}}
    public void setPickerOn(){if(colorPicker){colorPicker = false;} else{colorPicker = true;}}
//FillBucket
    public boolean fillBucket = false;
    public boolean checkFillBucket(){if(fillBucket){return true;}else{return false;}}
    public void setFillBucket(){if(fillBucket){fillBucket =false;}else{fillBucket = true;}}
    public boolean ColorCompare(Color color1,Color color2){
        if(color1 == null){color1 =new Color(192, 192, 192);}
        if(color2 == null){color2 =new Color(192, 192, 192);}
        if(color1.equals(color2)){return true;}
        else{return false;}
    }

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
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);                //A-- Clears the panel before drawing
        drawPixels(g);
        drawGrid(g);
        
    }

    public void drawPixels(Graphics g){
        if(g.getClip().getBounds().getWidth()!=pixelSize){
            for(int i=0; i < gridSize; i++){
                for(int j=0; j< gridSize; j++){
                    if(colors[i][j]==null)
                        g.setColor(new Color(192, 192, 192));
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
        pixelSize = (int)(truePixelSize * scale);
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
            scale+=0.1;
            resizeCanvas();
        }
        
        if(e.getKeyCode()==KeyEvent.VK_MINUS){ //and - for zoom out
            if(scale>1){
                scale-=0.1;
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
        int x= e.getX() / pixelSize;
        int y= e.getY() / pixelSize;
        
        //out of bounds fix
        if(x>=gridSize||x<0||y>=gridSize||y<0)
            return;
        //ColorPicker working
        if(checkPickerOn()){                                   
                if(colors[x][y] == null){setCurrentColor(new Color(192, 192, 192));}
                else{setCurrentColor(colors[x][y]);}
                
                setPickerOn();
        }
        //Fill Bucket working
        if(checkFillBucket()){
            if(getCurrentColor().equals(colors[x][y])){return;}
            else{
                runFillBucket(x, y, colors[x][y]);
                setFillBucket();
                repaint();
                return;
            }
        }
        colors[x][y]=currentColor;
        repaint(x*pixelSize,y*pixelSize,pixelSize,pixelSize);
	}
    public void runFillBucket(int x,int y,Color originalColor){
        colors[x][y] = getCurrentColor(); 

        if( y-1 >= 0 && ColorCompare(colors[x][y-1],originalColor) ) 
            runFillBucket(x, y-1,originalColor);

        if( y+1 < gridSize && ColorCompare(colors[x][y+1],originalColor) )
            runFillBucket(x, y+1,originalColor);

      if( x-1 >= 0 && ColorCompare(colors[x-1][y],originalColor) )
            runFillBucket(x-1, y,originalColor);

        if( x+1 < gridSize && ColorCompare(colors[x+1][y],originalColor) )
            runFillBucket(x+1, y,originalColor);
    }

    @Override
    public void mouseDragged(MouseEvent e){
        int x= e.getX() / pixelSize;
        int y= e.getY() / pixelSize;

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
        zoomListener.zoomRequested(e.getWheelRotation());
    }
    
}
