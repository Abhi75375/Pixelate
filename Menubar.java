import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class Menubar extends JPanel{
       public Menubar(){
        setBackground(Color.GRAY);
        setPreferredSize(new Dimension(520,100));
        setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton newButton = new JButton("New");
        JButton saveButton = new JButton("Save");
        JButton undoButton = new JButton("Undo");
        JButton redoButton = new JButton("Redo");

        add(newButton);
        add(saveButton);
        add(undoButton);
        add(redoButton);
        
        saveButton.addActionListener(new ActionListener(){
            
            @Override
            public void actionPerformed(ActionEvent e){
                System.out.println("saved !");
            }
        });
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                System.out.println("new-button clicked");
            }
        });
      }

}   