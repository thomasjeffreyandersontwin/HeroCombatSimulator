/*
 * AbstractPADEditor.java
 *
 * Created on June 12, 2001, 3:22 PM
 */

package champions.parameterEditor;

import champions.event.PADValueEvent;
import champions.interfaces.EnhancedCellEditor;
import champions.interfaces.PADValueListener;
import champions.parameters.ParameterList;
import java.util.EventObject;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import treeTable.TreeTableCellEditor;
import treeTable.TreeTableCellRenderer;
/**
 *
 * @author  twalker
 * @version 
 */
public abstract class AbstractParameterEditor extends Object 
implements TableCellEditor, TreeCellEditor, TreeTableCellEditor, TreeCellRenderer, TableCellRenderer, TreeTableCellRenderer, EnhancedCellEditor {
    
    protected EventListenerList listenerList = new EventListenerList();
    protected EventListenerList padListenerList = new EventListenerList();
    transient protected ChangeEvent changeEvent = null;

    /** Holds value of property parameterList. */
    protected ParameterList parameterList;
    
    /** Holds value of property parameter. */
    protected String parameter;
    
    /** Holds value of property key. */
    protected String key;

    public AbstractParameterEditor() {

    }
    
    /** Creates new AbstractPADEditor */
    public AbstractParameterEditor(ParameterList parameterList,String parameter) {
        setParameterList(parameterList);
        setParameter(parameter);
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
  //  public Component getTableCellEditorComponent(JTable table,Object value,boolean isSelected,int row,int column);
    
    /** Returns the value contained in the editor */
 //   public Object getCellEditorValue();
    
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
 //   public boolean isCellEditable(EventObject anEvent);
    
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
  //  public boolean shouldSelectCell(EventObject anEvent);
    
    /**
     * Tell the editor to stop editing and accept any partially edited
     * value as the value of the editor.  The editor returns false if
     * editing was not stopped, useful for editors which validates and
     * can not accept invalid entries.
     *
     * @return	true if editing was stopped
 */
 //   public boolean stopCellEditing() ;
    
    /**
     * Tell the editor to cancel editing and not accept any partially
     * edited value.
 */
 //   public void cancelCellEditing() ;
    
    /**
     * Add a listener to the list that's notified when the editor starts,
     * stops, or cancels editing.
     *
     * @param	l		the CellEditorListener
 */
 //   public void addCellEditorListener(CellEditorListener l);
    
    /**
     * Remove a listener from the list that's notified
     *
     * @param	l		the CellEditorListener
 */
  //  public void removeCellEditorListener(CellEditorListener l);
    
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
  //  public Component getTreeCellEditorComponent(JTree tree,Object value,boolean isSelected,boolean expanded,boolean leaf,int row);
    
    /**
     * Sets the value of the current tree cell to <code>value</code>.
     * If <code>selected</code> is true, the cell will be drawn as if
     * selected. If <code>expanded</code> is true the node is currently
     * expanded and if <code>leaf</code> is true the node represets a
     * leaf anf if <code>hasFocus</code> is true the node currently has
     * focus. <code>tree</code> is the JTree the receiver is being
     * configured for.
     * Returns the Component that the renderer uses to draw the value.
     *
     * @return	Component that the renderer uses to draw the value.
 */
   // public Component getTreeCellRendererComponent(JTree tree,Object value,boolean selected,boolean expanded,boolean leaf,int row,boolean hasFocus);
    
    /**
     * Returns the component used for drawing the cell.  This method is
     * used to configure the renderer appropriately before drawing.
     *
     * @param	table		the <code>JTable</code> that is asking the 
     * 				renderer to draw; can be <code>null</code>
     * @param	value		the value of the cell to be rendered.  It is
     * 				up to the specific renderer to interpret
     * 				and draw the value.  For example, if
     * 				<code>value</code>
     * 				is the string "true", it could be rendered as a
     * 				string or it could be rendered as a check
     * 				box that is checked.  <code>null</code> is a
     * 				valid value
     * @param	isSelected	true if the cell is to be rendered with the
     * 				selection highlighted; otherwise false
     * @param	hasFocus	if true, render cell appropriately.  For
     * 				example, put a special border on the cell, if
     * 				the cell can be edited, render in the color used
     * 				to indicate editing
     * @param	row	        the row index of the cell being drawn.  When
     * 				drawing the header, the value of
     * 				<code>row</code> is -1
     * @param	column	        the column index of the cell being drawn
 */
 //   public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column);
    
        /**
     * Returns true.
     * @param e  an event object
     * @return true
     */
    public boolean isCellEditable(EventObject e) { 
	return true; 
    } 

    /**
     * Returns true.
     * @param e  an event object
     * @return true
     */
    public boolean shouldSelectCell(EventObject anEvent) {       
	return true;
    }
    
    /**
     * Calls <code>fireEditingStopped</code> and returns true.
     * @return true
     */
    public boolean stopCellEditing() { 
	fireEditingStopped(); 
	return true;
    }

    /**
     * Calls <code>fireEditingCanceled</code>.
     */
    public void  cancelCellEditing() { 
	fireEditingCanceled(); 
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
    protected void fireEditingStopped() {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==CellEditorListener.class) {
		// Lazily create the event:
		if (changeEvent == null)
		    changeEvent = new ChangeEvent(this);
		((CellEditorListener)listeners[i+1]).editingStopped(changeEvent);
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
    protected void fireEditingCanceled() {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==CellEditorListener.class) {
		// Lazily create the event:
		if (changeEvent == null)
		    changeEvent = new ChangeEvent(this);
		((CellEditorListener)listeners[i+1]).editingCanceled(changeEvent);
	    }	       
	}
    }
    
        /**
     * Adds a <code>CellEditorListener</code> to the listener list.
     * @param l  the new listener to be added
     */
    public void addPADValueListener(PADValueListener l) {
	padListenerList.add(PADValueListener.class, l);
    }

    /**
     * Removes a <code>CellEditorListener</code> from the listener list.
     * @param l  the listener to be removed
     */
    public void removePADValueListener(PADValueListener l) { 
	padListenerList.remove(PADValueListener.class, l);
    }
    
     /*
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    protected void firePADValueChanged(Object oldValue, Object newValue) {
	// Guaranteed to return a non-null array
	Object[] listeners = padListenerList.getListenerList();
        PADValueEvent event = new PADValueEvent(this,key,newValue,oldValue);
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==PADValueListener.class) {
		((PADValueListener)listeners[i+1]).PADValueChanged(event);
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
    protected boolean firePADValueChanging(Object oldValue, Object newValue) {
	// Guaranteed to return a non-null array
	Object[] listeners = padListenerList.getListenerList();
        PADValueEvent event = new PADValueEvent(this,key,newValue,oldValue);
        boolean rv = true;
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==PADValueListener.class) {
		rv = ((PADValueListener)listeners[i+1]).PADValueChanging(event);
                if ( rv == false ) break;
	    }	       
	}
        return rv;
    }
    
    /** Getter for property parameterList.
     * @return Value of property parameterList.
 */
    public ParameterList getParameterList() {
        return parameterList;
    }
    
    /** Setter for property parameterList.
     * @param parameterList New value of property parameterList.
 */
    public void setParameterList(ParameterList parameterList) {
        this.parameterList = parameterList;
    }
    
    /** Getter for property parameter.
     * @return Value of property parameter.
 */
    public String getParameter() {
        return parameter;
    }
    
    /** Setter for property parameter.
     * @param parameter New value of property parameter.
 */
    public void setParameter(String parameter) {
        this.parameter = parameter;
        if ( parameterList != null && parameter != null) {
            setKey( (String)parameterList.getParameter(parameter).getKey() );
        }
        else if ( parameter != null ) {
            setKey(  parameter);
        }
        else {
            setKey("");
        }
    }
    
    /** Getter for property key.
     * @return Value of property key.
 */
    public String getKey() {
        return key;
    }    
    
    /** Setter for property key.
     * @param key New value of property key.
 */
    public void setKey(String key) {
        this.key = key;
    }


    
}
