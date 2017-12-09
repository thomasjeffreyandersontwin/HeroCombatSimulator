/*
 * EnhancedCellEditor.java
 *
 * Created on June 13, 2001, 7:13 AM
 */

package champions.interfaces;

import java.util.EventObject;
import javax.swing.JTree;
/**
 *
 * @author  twalker
 * @version 
 */
public interface EnhancedCellEditor {
    /** Indicates editing should start immediately.
     * This method should be overriden to check and see if the event would
     * trigger editing to start.  By default, it checks to see if the event
     * is a mouseEvent and occurred inside of the renderer delegate.
     * @return True to trigger edit, False to ignore event.
     * @param e Event which might trigger edit.
     * @param tree Tree where event occurred.
     * @param row Row in which event occurred.
     * @param offset Offset from the row bounds at which the renderer starts drawing.
     * Offset includes the icon width if there is one and the label text gap.
 */
    public boolean canEditImmediately(EventObject e, JTree tree, int row, int offset);
}

