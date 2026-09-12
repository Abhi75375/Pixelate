import javax.swing.*;
public class Frame extends JFrame implements ZoomListener{
    static final int WIDTH = 1600;
    static final int HEIGHT = 900;
    Canvas canvas;
    Frame(){

        this.setTitle("Pixelate");
        this.setSize(WIDTH,HEIGHT);
        this.setResizable(false);//for now
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        canvas = new Canvas();
        JScrollPane scrollpane = new JScrollPane(canvas);
        this.add(scrollpane);   
        canvas.setZoomListener(this);

        this.setVisible(true);
    }
    @Override
    public void zoomRequested(int mouseX, int mouseY, int rotation) {
        //call the zooom functions ig
        if(rotation > 0){
            canvas.scale++;
            canvas.resizeCanvas();
        }
        else{
            if(canvas.scale > 1){
                canvas.scale--;
                canvas.resizeCanvas();
            }
        }
    }
}
