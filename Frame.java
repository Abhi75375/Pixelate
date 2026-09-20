import javax.swing.*;
import java.awt.BorderLayout;

public class Frame extends JFrame implements ZoomListener{

    static final int WIDTH = 1600;
    static final int HEIGHT = 900;
    Canvas canvas;
    JScrollPane scrollpane;
    Frame(){

        this.setTitle("Pixelate");
        this.setSize(WIDTH,HEIGHT);
        this.setResizable(false);//for now
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        canvas = new Canvas();
        this.add(canvas , BorderLayout.CENTER);

        scrollpane = new JScrollPane(canvas);
        this.add(scrollpane);   

        canvas.setZoomListener(this);


        Statusbar statusbar = new Statusbar(canvas);
        this.add(statusbar, BorderLayout.SOUTH);
        
        Menubar menubar = new Menubar();
        this.add(menubar, BorderLayout.NORTH);

        Sidebar sidebar = new Sidebar(canvas);
        this.add(sidebar,BorderLayout.WEST);
       

        this.setVisible(true);
        

    }
    @Override
    public void zoomRequested(int rotation) {
        int oldX = canvas.getMousePosition().x / canvas.pixelSize;
        int oldY = canvas.getMousePosition().y / canvas.pixelSize;
        if(rotation > 0)
            canvas.scale+=0.1;
        
        else
            if(canvas.scale > 1)
                canvas.scale-=0.1;
        
        canvas.resizeCanvas();

        int newX = canvas.getMousePosition().x / canvas.pixelSize;
        int newY = canvas.getMousePosition().y / canvas.pixelSize;

        int dx = (oldX - newX)* canvas.pixelSize;
        int dy = (oldY - newY)* canvas.pixelSize;
        JScrollBar h = scrollpane.getHorizontalScrollBar();
        JScrollBar v = scrollpane.getVerticalScrollBar();
        h.setValue(h.getValue()+dx);
        v.setValue(v.getValue()+dy);
    }
}
