import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Statusbar extends JPanel{
      JLabel xLabel;
      JLabel yLabel;
      JLabel colorLabel;

      public Statusbar(){
        setBackground(Color.GRAY);
        setPreferredSize(new Dimension(520,35));
        setLayout(new FlowLayout(FlowLayout.LEFT));
        System.out.println("statusbar runs");

        xLabel = new JLabel("X : 0");
        yLabel = new JLabel("Y : 0");
        colorLabel = new JLabel("Color : Green");

        add(xLabel);add(yLabel);add(colorLabel);


      }
      public void updateStatus(int x,int y,Color color){
        xLabel.setText("X: "+ x);
        yLabel.setText("Y: "+ y);
        colorLabel.setText("Color: "+ getColorName(color));

      }
      private String getColorName(Color color){
        if(color.equals(Color.GREEN)){return "Green";}
        else if(color.equals(Color.RED)){return "Red";}
        else if(color.equals(Color.BLUE)){return "Blue";}
        else {return "Custom";}
      }
}     