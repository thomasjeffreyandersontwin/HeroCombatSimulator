/*
 * StringTreeTableCellEditor.java
 *
 * Created on February 18, 2002, 11:59 AM
 */

package treeTable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.EventObject;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.CellEditorListener;
import javax.swing.event.EventListenerList;


/**
 *
 * @author  twalker
 * @version 
 */
public class DefaultTreeTableCellEditor extends JTextField 
implements TreeTableCellEditor, FocusListener {
    /** Listeners to proxy to realEditor */
    protected EventListenerList listenerList = new EventListenerList();
    
    protected TreeTable treeTable;
    protected Object node;
    protected int column;
    
    protected Font editorFont;
    
    /** Creates new StringTreeTableCellEditor */
    public DefaultTreeTableCellEditor() { 
        this.addFocusListener( this );
        
        setupKeyBindings(); 
    }
    
    public void setupKeyBindings() { 
        InputMap inputMap = getInputMap();
        inputMap.put( KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "StopEditing" );
        
        ActionMap actionMap = getActionMap();
        actionMap.put( "StopEditing", new EnterAction() );
        
    }
    
    /**
     * Sets an initial <code>value</code> for the editor.  This will cause
     * the editor to <code>stopEditing</code> and lose any partially
     * edited value if the editor is editing when this method is called. <p>
     *
     * Returns the component that should be added to the client's
     * <code>Component</code> hierarchy.  Once installed in the client's
     * hierarchy this component will then be able to draw and receive
     * user input.
     *
     * @param	table		the <code>JTable</code> that is asking the
     * 				editor to edit; can be <code>null</code>
     * @param	value		the value of the cell to be edited; it is
     * 				up to the specific editor to interpret
     * 				and draw the value.  For example, if value is
     * 				the string "true", it could be rendered as a
     * 				string or it could be rendered as a check
     * 				box that is checked.  <code>null</code>
     * 				is a valid value
     * @param	isSelected	true if the cell is to be rendered with
     * 				highlighting
     * @param	row     	the row of the cell being edited
     * @param	column  	the column of the cell being edited
     * @return	the component for editing
     */
    public Component getTreeTableCellEditorComponent(TreeTable treeTable, Object node, boolean isSelected, boolean expanded, boolean leaf, int row, int column) {
        this.treeTable = treeTable;
        this.node = node;
        this.column = column;
        
        TreeTableModel ttm = treeTable.getProxyTreeTableModel();
        
        Object o = ttm.getValueAt(node, column);
        if ( o != null ) {
            this.setText(o.toString() );
        }
        else {
            this.setText(null);
        }
        
        Color color = treeTable.getProxyTreeTableModel().getColumnColor(node, column);
        if ( color == null ) color = treeTable.getForeground();
        
        setForeground( color );
        setBackground( treeTable.getBackground() );
        if ( editorFont != null ) {
            setFont(editorFont);
        }
        else {
            setFont( treeTable.getFont() ); 
        }
        
        return this;
    }
    
    /**
     * Tell the editor to cancel editing and not accept any partially
     * edited value.
     */
    public void cancelCellEditing() {
    }
    
    /**
     * Ask the editor if it can start editing using <I>anEvent</I>.
     * <I>anEvent</I> is in the invoking component coordinate system.
     * The editor can not assume the Component returned by
     * getCellEditorComponent() is installed.  This method is intended
     * for the use of client to avoid the cost of setting up and installing
     * the editor component if editing is not possible.
     * If editing can be started this method returns true.
     *
     * @param	anEvent		the event the editor should use to consider
     * 				whether to begin editing or not.
     * @return	true if editing can be started.
     * @see #shouldSelectCell
     */
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }
    
    
    /**
     * Add a listener to the list that's notified when the editor starts,
     * stops, or cancels editing.
     *
     * @param	l		the CellEditorListener
     */
    public void addCellEditorListener(CellEditorListener l) {
        listenerList.add(CellEditorListener.class, l);
    }
    
    /**
     * Remove a listener from the list that's notified
     *
     * @param	l		the CellEditorListener
     */
    public void removeCellEditorListener(CellEditorListener l) {
        listenerList.remove(CellEditorListener.class, l);
    }
    
    /** Returns the value contained in the editor */
    public Object getCellEditorValue() {
        return getText();
    }
    
    /**
     * Tell the editor to stop editing and accept any partially edited
     * value as the value of the editor.  The editor returns false if
     * editing was not stopped, useful for editors which validates and
     * can not accept invalid entries.
     *
     * @return	true if editing was stopped
     */
    public boolean stopCellEditing() {
        // Write out the changes before stopping
        treeTable.getProxyTreeTableModel().setValueAt(node, column, getText());
        
        return true;
    }
    
    /**
     * The return value of shouldSelectCell() is a boolean indicating whether
     * the editing cell should be selected or not.  Typically, the return
     * value is true, because is most cases the editing cell should be
     * selected.  However, it is useful to return false to keep the selection
     * from changing for some types of edits.  eg. A table that contains
     * a column of check boxes, the user might want to be able to change
     * those checkboxes without altering the selection.  (See Netscape
     * Communicator for just such an example)  Of course, it is up to
     * the client of the editor to use the return value, but it doesn't
     * need to if it doesn't want to.
     *
     * @param	anEvent		the event the editor should use to start
     * 				editing.
     * @return	true if the editor would like the editing cell to be selected
     * @see #isCellEditable
     */
    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }
    
    /**
     * Returns whether the editor should start editing immediate.
     *
     * By default, the editor waits for 2 clicks or for the timer to time out
     * to start editing.  This can override that behaviour.
     */
    public boolean canEditImmediately(EventObject event, TreeTable treeTable, Object node, int row, int column, int offset) {
        return false;
    } 
    
    /**
     * Invoked when a component loses the keyboard focus.
     */
    public void focusLost(FocusEvent e) {
        if ( treeTable != null ) treeTable.stopEditing();
    }
    
    /**
     * Invoked when a component gains the keyboard focus.
     */
    public void focusGained(FocusEvent e) {
        selectAll();
    }
    
    /**
     * Informs the editor that it's current selection status may have changed to <code>isSelected</code>.
     */
    public void selectionStateChanged(boolean isSelected) {
    }
    
    private class EnterAction extends AbstractAction {
        
        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e) {
            if ( treeTable != null ) treeTable.stopEditing();
        }
        
    }

    public Font getEditorFont() {
        return editorFont;
    }

    public void setEditorFont(Font editorFont) {
        this.editorFont = editorFont;
    }
    
}
