/*
 * MyToggleButton.java
 *
 * Created on October 16, 2000, 2:45 PM
 */

package champions;

import javax.swing.*;
/**
 *
 * @author  unknown
 * @version 
 */
public class MyToggleButton extends JToggleButton {

    /** Creates new MyToggleButton */
    public MyToggleButton() {
        //super();
    }

    public boolean isFocusTraversable(){
        return false;
    }
    
    public boolean isRequestFocusEnabled() {
        return false;
    }
}