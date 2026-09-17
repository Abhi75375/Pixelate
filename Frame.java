import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

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

        Statusbar statusbar = new Statusbar(canvas);
        this.add(statusbar, BorderLayout.SOUTH);
        
        Menubar menubar = new Menubar();
        this.add(menubar, BorderLayout.NORTH);

        Ribbon ribbon = new Ribbon(canvas);
        this.add(ribbon,BorderLayout.WEST);

        canvas.setZoomListener(this);

        KeyBindings.bind(
                canvas,
                KeyEvent.VK_EQUALS,InputEvent.CTRL_DOWN_MASK,
                "zoomIn",
                new AbstractAction(){
                    @Override 
                    public void actionPerformed(ActionEvent e){
                        canvas.zoomIn();
                    }
                }
        );
        KeyBindings.bind(
                canvas,
                KeyEvent.VK_MINUS,InputEvent.CTRL_DOWN_MASK,
                "zoomOut",
                new AbstractAction(){
                    @Override 
                    public void actionPerformed(ActionEvent e){
                        canvas.zoomOut();
                    }
                }
        );

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
