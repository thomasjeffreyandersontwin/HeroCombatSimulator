/*
 * TreeTableCellEditor.java
 *
 * Created on February 17, 2002, 3:18 PM
 */

package treeTable;

import java.awt.*;
import javax.swing.*;
import java.util.*;
/**
 *
 * @author  twalker
 * @version 
 */
public interface TreeTableCellEditor extends CellEditor {
    /**
     *  Sets an initial <code>value</code> for the editor.  This will cause
     *  the editor to <code>stopEditing</code> and lose any partially
     *  edited value if the editor is editing when this method is called. <p>
     *
     *  Returns the component that should be added to the client's
     *  <code>Component</code> hierarchy.  Once installed in the client's
     *  hierarchy this component will then be able to draw and receive
     *  user input.
     *
     * @param	table		the <code>JTable</code> that is asking the
     *				editor to edit; can be <code>null</code>
     * @param	value		the value of the cell to be edited; it is
     *				up to the specific editor to interpret
     *				and draw the value.  For example, if value is
     *				the string "true", it could be rendered as a
     *				string or it could be rendered as a check
     *				box that is checked.  <code>null</code>
     *				is a valid value
     * @param	isSelected	true if the cell is to be rendered with
     *				highlighting
     * @param	row     	the row of the cell being edited
     * @param	column  	the column of the cell being edited
     * @return	the component for editing
     */
    Component getTreeTableCellEditorComponent(TreeTable treeTable, Object node,
					  boolean isSelected, boolean expanded, 
					  boolean leaf, int row, int column);
    
    /** 
     * Returns whether the editor should start editing immediate.  
     * 
     * By default, the editor waits for 2 clicks or for the timer to time out
     * to start editing.  This can override that behaviour.
     */
    public boolean canEditImmediately(EventObject event, TreeTable treeTable, Object node, int row, int column, int offset);
    
    /** 
     * Informs the editor that it's current selection status may have changed to <code>isSelected</code>.
     */
    public void selectionStateChanged(boolean isSelected);

}

