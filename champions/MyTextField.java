/*
 * MyTextField.java
 *
 * Created on December 18, 2000, 8:16 PM
 */

package champions;

import javax.swing.*;
/**
 *
 * @author  unknown
 * @version 
 */
public class MyTextField extends JTextField {

    /** Creates new MyTextField */
    public MyTextField() {
    }

    public boolean isManagingFocus() {
        return true;
    }
}