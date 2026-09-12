import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Ribbon extends JPanel{

      private Canvas canvas;

      private JButton createColorButton(Color color){
          JButton button = new JButton();
          button.setBackground(color);
          button.setPreferredSize(new Dimension(30,30));
          button.addActionListener(e-> canvas.setCurrentColor(color));
          return button;
      }

      public Ribbon(Canvas canvas){
        this.canvas = canvas;

        setBackground(Color.lightGray);
        setPreferredSize(new Dimension(100,520));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));              //A-- BoxLayout 

        JButton penButton = new JButton("🖊");
        JButton eraseButton = new JButton("🩹");
        JLabel dashLabel = new JLabel("******************");
      
        add(penButton); add(eraseButton);
        add(dashLabel);
        add(createColorButton(Color.RED));
        add(createColorButton(Color.GREEN));add(createColorButton(Color.BLUE));
        add(createColorButton(Color.YELLOW));add(createColorButton(Color.BLACK));

        
        eraseButton.addActionListener(e -> {
            canvas.currentColor = Color.lightGray;
          });
          
        //saveButton.addActionListener(new ActionListener(){});
        
      }
}
