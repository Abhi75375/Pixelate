import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Sidebar extends JPanel{

      private Canvas canvas;

      
      private JButton createColorButton(Color color){
          JButton button = new JButton();
          button.setBackground(color);
          button.setPreferredSize(new Dimension(30,30));
          button.addActionListener(e-> canvas.setCurrentColor(color));
          return button;
      }

      public Sidebar(Canvas canvas){
        this.canvas = canvas;

        setBackground(Color.lightGray);
        setPreferredSize(new Dimension(100,520));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));              //A-- BoxLayout 
        
        JButton EditColorButton = new JButton("Edit Colors");
        JButton ColorPickerButton = new JButton("🖊");
        JButton EraseButton = new JButton("🩹");
        JButton FillBucketButton = new JButton("Fill Bucket");
        JLabel dashLabel = new JLabel("******************");
        
        add(EditColorButton);add(ColorPickerButton);
        add(EraseButton);add(FillBucketButton);
        add(dashLabel);
        add(createColorButton(new Color(255, 0, 0)));
        add(createColorButton(new Color(0,255,0)));
        add(createColorButton(new Color(0,0,255)));
        add(createColorButton(new Color(255, 255, 0)));
        add(createColorButton(new Color(0,0,0)));

        
        EraseButton.addActionListener(e -> {
          canvas.currentColor = new Color(192, 192, 192);
        });
        ColorPickerButton.addActionListener(e-> {
          canvas.setPickerOn();
        });
        EditColorButton.addActionListener(e->{
          JColorChooser colorChooser = new JColorChooser();
          Color color = JColorChooser.showDialog(canvas,"Choose a color:", canvas.getCurrentColor());
          canvas.setCurrentColor(color);
        });
        FillBucketButton.addActionListener(e->{
          canvas.setFillBucket();System.out.println(canvas.checkFillBucket());
        });
        
      }

}
