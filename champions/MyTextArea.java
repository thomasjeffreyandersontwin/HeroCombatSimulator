/*
 * MyTextArea.java
 *
 * Created on October 16, 2000, 2:59 PM
 */

package champions;

import javax.swing.JTextArea;

/**
 *
 * @author  unknown
 * @version 
 */
public class MyTextArea extends JTextArea {

    /** Creates new MyTextArea */
    public MyTextArea() {
        super();
    }
    
    public boolean isFocusTraversable() {
        return false;
    }

}