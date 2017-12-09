/*
 * MyMenu2.java
 *
 * Created on December 29, 2000, 4:07 PM
 */

package champions;

import javax.swing.*;
import java.awt.*;
/**
 *
 * @author  unknown
 * @version
 */
public class MyMenu2 extends JMenu {

    /** Creates new MyMenu2 */
    public MyMenu2(String name) {
        super(name);
    }

    protected Point getPopupMenuOrigin() {
        int x = 0;
        int y = 0;
        JPopupMenu pm = getPopupMenu();
        // Figure out the sizes needed to caclulate the menu position
        Dimension screenSize =Toolkit.getDefaultToolkit().getScreenSize();
        Dimension s = getSize();
        Dimension pmSize = pm.getSize();
        // For the first time the menu is popped up,
        // the size has not yet been initiated
        if (pmSize.width==0) {
            pmSize = pm.getPreferredSize();
        }
        Point position = getLocationOnScreen();

        Container parent = getParent();
        if (parent instanceof JPopupMenu) {
            // We are a submenu (pull-right)

            // First determine x:
            if (position.x+s.width + pmSize.width < screenSize.width) {
                x = s.width;         // Prefer placement to the right
            } else {
                x = 0-pmSize.width;  // Otherwise place to the left
            }

            // Then the y:
            if (position.y+pmSize.height < screenSize.height) {
                y = 0;                       // Prefer dropping down
            }
            else {
                y = s.height-pmSize.height;  // Otherwise drop 'up'
            }

            if ( y + position.y < 0 ) {
                y = - position.y + 5;
            }


        } else {
            // We are a toplevel menu (pull-down)


            // First determine the x:
            if (position.x+pmSize.width < screenSize.width) {
                x = 0;                     // Prefer extending to right
            } else {
                x = s.width-pmSize.width;  // Otherwise extend to left            
            }
            // Then the y:
            if (position.y+s.height+pmSize.height < screenSize.height) {
                y = s.height;          // Prefer dropping down
            } else {
                y = 0-pmSize.height;   // Otherwise drop 'up'
            }
        }
        return new Point(x,y);
    }

}
