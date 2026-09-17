import javax.swing.*;

public class KeyBindings{
    public static void bind(JComponent comp, int key,int mask, String name, Action action){
        comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
            put(KeyStroke.getKeyStroke(key,mask),name);

        comp.getActionMap().
            put(name, action);
    }
}
