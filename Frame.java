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
        Canvas canvas = new Canvas();
        this.add(canvas);

        this.setVisible(true);
    }
}
