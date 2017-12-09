/*
 * PopupListWindow.java
 *
 * Created on October 7, 2001, 10:43 PM
 */

package tjava;

import javax.swing.*;
/**
 *
 * @author  twalker
 * @version 
 */
public class PopupListWindow extends JWindow {

    /** Creates new PopupListWindow */
    public PopupListWindow() {
    }
    
    public void show() {
            super.show();
            requestFocus();
        }

}
