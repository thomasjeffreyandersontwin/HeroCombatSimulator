/*
 * PADHelpWatcher.java
 *
 * Created on December 24, 2000, 10:34 AM
 */

package champions;

import javax.swing.*;
import java.awt.event.*;
/**
 *
 * @author  unknown
 * @version 
 */
public class PADHelpWatcher extends Object 
implements MouseListener{

    /** Holds value of property helpPane. */
    private JTextArea helpPane;
    /** Holds value of property helpText. */
    private String helpText;
    /** Creates new PADHelpWatcher */
    public PADHelpWatcher(String helpText, JTextArea helpPane) {
        setHelpText(helpText);
        setHelpPane(helpPane);
    }

    /** Getter for property helpPane.
     * @return Value of property helpPane.
     */
    public JTextArea getHelpPane() {
        return helpPane;
    }
    /** Setter for property helpPane.
     * @param helpPane New value of property helpPane.
     */
    public void setHelpPane(JTextArea helpPane) {
        this.helpPane = helpPane;
    }
    /** Getter for property helpText.
     * @return Value of property helpText.
     */
    public String getHelpText() {
        return helpText;
    }
    /** Setter for property helpText.
     * @param helpText New value of property helpText.
     */
    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }
    /** Invoked when the mouse has been clicked on a component.
     */
    public void mouseClicked(MouseEvent e) {
    }
    /** Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent e) {
    }
    /** Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {
    }
    /** Invoked when the mouse enters a component.
     */
    public void mouseEntered(MouseEvent e) {
        if ( helpPane != null && helpText != null ) {
            helpPane.setText( helpText );
        }
    }
    /** Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {
    }

}