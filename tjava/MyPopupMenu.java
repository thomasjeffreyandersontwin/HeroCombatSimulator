/*
 * MyPopupMenu.java
 *
 * Created on December 3, 2000, 5:11 PM
 */

package tjava;
import java.awt.*;
import javax.swing.*;
/**
 *
 * @author  unknown
 * @version 
 */
public class MyPopupMenu extends JPopupMenu {

    /** Creates new MyPopupMenu */
    public MyPopupMenu() {
    }
    
    public MyPopupMenu(String label) {
        super(label);
    }
    
    public void show (Component c, int x, int y) {
        super.show(c,x,y);

        Dimension popupSize =  this.getSize();
        Dimension screenSize = this.getToolkit().getScreenSize();
        Point location =  this.getLocationOnScreen();
        
        if ( popupSize.height + location.y > screenSize.height ) {
            location.y -= (popupSize.height + location.y) - screenSize.height + 5;
        }
        
        if ( location.y  < 0 ) {
            location.y = 5;
        }
        
        if ( popupSize.width + location.x > screenSize.width ) {
            location.x -= popupSize.width;
        }
                if ( location.x < 0 ) {
            location.x = 5;
        }

        setLocation(location);
    }
}