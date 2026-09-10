import java.awt.BorderLayout;
import javax.swing.JFrame;
public class Frame extends JFrame{
    static final int WIDTH = 1600;
    static final int HEIGHT = 900;
    Frame(){

        this.setTitle("Pixelate");
        this.setSize(WIDTH,HEIGHT);
        this.setResizable(false);//for now
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //adding panels
      

        Canvas canvas = new Canvas();              //passing reference of statusbar to canvas
        this.add(canvas , BorderLayout.CENTER);

        Statusbar statusbar = new Statusbar(canvas);
        this.add(statusbar, BorderLayout.SOUTH);
        
        Menubar menubar = new Menubar();
        this.add(menubar, BorderLayout.NORTH);

        Ribbon ribbon = new Ribbon(canvas);
        this.add(ribbon,BorderLayout.WEST);
       

        this.setVisible(true);
        

    }
}
