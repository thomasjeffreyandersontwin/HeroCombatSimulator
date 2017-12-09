/*
 * CheckedCellRenderer.java
 *
 * Created on November 12, 2003, 10:04 PM
 */

package tjava;

import java.awt.Component;
import javax.swing.*;

/**
 *
 * @author  1425
 */
public class PopupListCellRenderer extends JLabel implements ListCellRenderer {
    
    private Icon checkedIcon = null;
    private Icon uncheckedIcon = null;
    
    public PopupListCellRenderer(String checkedIconProperty, String uncheckedIconProperty) {
        checkedIcon = UIManager.getIcon(checkedIconProperty);
        uncheckedIcon = UIManager.getIcon(uncheckedIconProperty);
    }
    // This is the only method defined by ListCellRenderer.
    // We just reconfigure the JLabel each time we're called.
    
    public Component getListCellRendererComponent(
    JList list,
    Object value,            // value to display
    int index,               // cell index
    boolean isSelected,      // is the cell selected
    boolean cellHasFocus)    // the list and the cell have the focus
    {
        String s = value.toString();
        setText(s);
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
            
            setIcon(checkedIcon);
        }
        else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
            
            setIcon(uncheckedIcon);
        }
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);
        return this;
    }
}
