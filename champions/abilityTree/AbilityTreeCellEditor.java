/*
 * AbilityTreeCellEditor.java
 *
 * Created on June 12, 2001, 12:14 PM
 */

package champions.abilityTree;

import champions.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.util.*;
import java.io.*;
/**
 *
 * @author  twalker
 * @version
 */
public class AbilityTreeCellEditor extends Object
implements TreeCellEditor, CellEditorListener, Serializable {
    
    protected AbilityTreeNode node;
    
    protected int lastRow;
    
    /** Holds value of property tree. */
    protected JTree tree;
    
    protected EventListenerList listenerList = new EventListenerList();
    
    /** Holds value of property editor. */
    private TreeCellEditor editor;
    
    /** Creates new AbilityTreeCellEditor */
    public AbilityTreeCellEditor() {
        
    }
    
    
    /**
     * Sets an initial <I>value</I> for the editor.  This will cause
     * the editor to stopEditing and lose any partially edited value
     * if the editor is editing when this method is called. <p>
     *
     * Returns the component that should be added to the client's
     * Component hierarchy.  Once installed in the client's hierarchy
     * this component will then be able to draw and receive user input.
     *
     * @param	table		the JTree that is asking the editor to edit
     * 				This parameter can be null.
     * @param	value		the value of the cell to be edited.
     * @param	isSelected	true is the cell is to be renderer with
     * 				selection highlighting
     * @param	expanded	true if the node is expanded
     * @param	leaf		true if the node is a leaf node
     * @param	row		the row index of the node being edited
     * @return	the component for editing
     */
    public Component getTreeCellEditorComponent(JTree tree,Object value,boolean isSelected,boolean expanded,boolean leaf,int row) {
        setTree(tree);
        lastRow = row;
        if ( value instanceof AbilityTreeNode ) {
            node = (AbilityTreeNode)value;
            TreeCellEditor newEditor = node.getTreeCellEditor(tree);
            setEditor(newEditor);
            
            if ( editor != null ) {
                return editor.getTreeCellEditorComponent(tree,value,isSelected,expanded,leaf,row);
            }
        }
        return null;
    }
    
    /** Returns the value contained in the editor */
    public Object getCellEditorValue() {
        if ( editor != null ) {
            return editor.getCellEditorValue();
        }
        return null;
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
        if ( editor != null ) {
            return editor.isCellEditable(anEvent);
        }
        return false;
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
        if ( editor != null ) {
            return editor.shouldSelectCell(anEvent);
        }
        return true;
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
        if ( editor != null ) {
            return editor.stopCellEditing();
        }
        return true;
    }
    
    /**
     * Tell the editor to cancel editing and not accept any partially
     * edited value.
     */
    public void cancelCellEditing() {
        if ( editor != null ) {
            editor.cancelCellEditing();
        }
    }
    
    /**
     * Adds a <code>CellEditorListener</code> to the listener list.
     * @param l  the new listener to be added
     */
    public void addCellEditorListener(CellEditorListener l) {
        listenerList.add(CellEditorListener.class, l);
    }
    
    /**
     * Removes a <code>CellEditorListener</code> from the listener list.
     * @param l  the listener to be removed
     */
    public void removeCellEditorListener(CellEditorListener l) {
        listenerList.remove(CellEditorListener.class, l);
    }
    
    /*
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    protected void fireEditingStopped(ChangeEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==CellEditorListener.class) {
                ((CellEditorListener)listeners[i+1]).editingStopped(e);
            }
        }
    }
    
    /*
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    protected void fireEditingCanceled(ChangeEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==CellEditorListener.class) {
                ((CellEditorListener)listeners[i+1]).editingCanceled(e);
            }
        }
    }
    
    /** Getter for property tree.
     * @return Value of property tree.
     */
    public JTree getTree() {
        return tree;
    }
    
    /** Setter for property tree.
     * @param tree New value of property tree.
     */
    public void setTree(JTree tree) {
        this.tree = tree;
    }
    
    /** This tells the listeners the editor has ended editing  */
    public void editingStopped(ChangeEvent e) {
        fireEditingStopped(e);
    }
    
    /** This tells the listeners the editor has canceled editing  */
    public void editingCanceled(ChangeEvent e) {
        fireEditingCanceled(e);
    }
    
    /** Getter for property editor.
     * @return Value of property editor.
     */
    public TreeCellEditor getEditor() {
        return editor;
    }
    
    /** Setter for property editor.
     * @param editor New value of property editor.
     */
    public void setEditor(TreeCellEditor editor) {
        if ( this.editor != editor ) {
            if ( this.editor != null ) {
                this.editor.removeCellEditorListener(this);
            }
            this.editor = editor;
            if ( this.editor != null ) {
                this.editor.addCellEditorListener(this);
            }
        }
    }
    
}
