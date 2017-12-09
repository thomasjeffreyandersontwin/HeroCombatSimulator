/*
 * @(#)DefaultTreeTableColumnModel.java	1.45 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package treeTable;

import java.util.Vector;
import java.util.Enumeration;
import java.util.EventListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.Serializable;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * The standard column-handler for a <code>JTable</code>.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @version 1.45 01/23/03
 * @author Alan Chung
 * @author Philip Milne
 * @see JTable
 */
public class DefaultTreeTableColumnModel implements TreeTableColumnModel,
                                                    PropertyChangeListener, ListSelectionListener, Serializable {
//
// Instance Variables
//

    static final long serialVersionUID = 2535985817450550829L;

    /** Array of TreeTableColumn objects in this model */
    protected Vector tableColumns;

    /** Model for keeping track of column selections */
    protected ListSelectionModel selectionModel;

    /** Width margin between each column */
    protected int columnMargin;

    /** List of TreeTableColumnModelListener */
    protected EventListenerList listenerList = new EventListenerList();

    /** Change event (only one needed) */
    transient protected ChangeEvent changeEvent = null;

    /** Column selection allowed in this column model */
    protected boolean columnSelectionAllowed;

    /** A local cache of the combined width of all columns */
    protected int totalColumnWidth;

    protected boolean sortable;

    protected TreeTableColumn sortColumn = null;

    protected boolean sortAscending = true;

//
// Constructors
//
    /**
     * Creates a default table column model.
     */
    public DefaultTreeTableColumnModel() {
        super();

        // Initialize local ivars to default
        tableColumns = new Vector();
        setSelectionModel(createSelectionModel());
        getSelectionModel().setAnchorSelectionIndex(0);
        setColumnMargin(1);
        invalidateWidthCache();
        setColumnSelectionAllowed(false);
    }

//
// Modifying the model
//
    /**
     *  Appends <code>aColumn</code> to the end of the
     *  <code>tableColumns</code> array.
     *  This method also posts the <code>columnAdded</code>
     *  event to its listeners.
     *
     * @param	aColumn		the <code>TreeTableColumn</code> to be added
     * @exception IllegalArgumentException	if <code>aColumn</code> is
     *				<code>null</code>
     * @see	#removeColumn
     */
    @Override
    public void addColumn(TreeTableColumn aColumn) {
        if (aColumn == null) {
            throw new IllegalArgumentException("Object is null");
        }

        tableColumns.addElement(aColumn);
        aColumn.addPropertyChangeListener(this);
        invalidateWidthCache();

        // Post columnAdded event notification
        fireColumnAdded(new TreeTableColumnModelEvent(this, 0,
                getColumnCount() - 1));
    }

    /**
     *  Deletes the <code>column</code> from the
     *  <code>tableColumns</code> array.  This method will do nothing if
     *  <code>column</code> is not in the table's columns list.
     *  <code>tile</code> is called
     *  to resize both the header and table views.
     *  This method also posts a <code>columnRemoved</code>
     *  event to its listeners.
     *
     * @param	column		the <code>TreeTableColumn</code> to be removed
     * @see	#addColumn
     */
    @Override
    public void removeColumn(TreeTableColumn column) {
        int columnIndex = tableColumns.indexOf(column);

        if (columnIndex != -1) {
            // Adjust for the selection
            if (selectionModel != null) {
                selectionModel.removeIndexInterval(columnIndex, columnIndex);
            }

            column.removePropertyChangeListener(this);
            tableColumns.removeElementAt(columnIndex);
            invalidateWidthCache();

            // Post columnAdded event notification.  (JTable and JTableHeader
            // listens so they can adjust size and redraw)
            fireColumnRemoved(new TreeTableColumnModelEvent(this,
                    columnIndex, 0));
        }
    }

    /**
     * Moves the column and heading at <code>columnIndex</code> to
     * <code>newIndex</code>.  The old column at <code>columnIndex</code>
     * will now be found at <code>newIndex</code>.  The column
     * that used to be at <code>newIndex</code> is shifted
     * left or right to make room.  This will not move any columns if
     * <code>columnIndex</code> equals <code>newIndex</code>.  This method
     * also posts a <code>columnMoved</code> event to its listeners.
     *
     * @param	columnIndex			the index of column to be moved
     * @param	newIndex			new index to move the column
     * @exception IllegalArgumentException	if <code>column</code> or
     * 						<code>newIndex</code>
     *						are not in the valid range
     */
    @Override
    public void moveColumn(int columnIndex, int newIndex) {
        if ((columnIndex < 0) || (columnIndex >= getColumnCount()) ||
                (newIndex < 0) || (newIndex >= getColumnCount())) {
            throw new IllegalArgumentException("moveColumn() - Index out of range");
        }

        TreeTableColumn aColumn;

        // If the column has not yet moved far enough to change positions
        // post the event anyway, the "draggedDistance" property of the
        // tableHeader will say how far the column has been dragged.
        // Here we are really trying to get the best out of an
        // API that could do with some rethinking. We preserve backward
        // compatibility by slightly bending the meaning of these methods.
        if (columnIndex == newIndex) {
            fireColumnMoved(new TreeTableColumnModelEvent(this, columnIndex, newIndex));
            return;
        }
        aColumn = (TreeTableColumn) tableColumns.elementAt(columnIndex);

        tableColumns.removeElementAt(columnIndex);
        boolean selected = selectionModel.isSelectedIndex(columnIndex);
        selectionModel.removeIndexInterval(columnIndex, columnIndex);

        tableColumns.insertElementAt(aColumn, newIndex);
        selectionModel.insertIndexInterval(newIndex, 1, true);
        if (selected) {
            selectionModel.addSelectionInterval(newIndex, newIndex);
        }
        else {
            selectionModel.removeSelectionInterval(newIndex, newIndex);
        }

        fireColumnMoved(new TreeTableColumnModelEvent(this, columnIndex,
                newIndex));
    }

    /**
     * Sets the column margin to <code>newMargin</code>.  This method
     * also posts a <code>columnMarginChanged</code> event to its
     * listeners.
     *
     * @param	newMargin		the new margin width, in pixels
     * @see	#getColumnMargin
     * @see	#getTotalColumnWidth
     */
    @Override
    public void setColumnMargin(int newMargin) {
        if (newMargin != columnMargin) {
            columnMargin = newMargin;
            // Post columnMarginChanged event notification.
            fireColumnMarginChanged();
        }
    }

//
// Querying the model
//
    /**
     * Returns the number of columns in the <code>tableColumns</code> array.
     *
     * @return	the number of columns in the <code>tableColumns</code> array
     * @see	#getColumns
     */
    @Override
    public int getColumnCount() {
        return tableColumns.size();
    }

    /**
     * Returns an <code>Enumeration</code> of all the columns in the model.
     * @return an <code>Enumeration</code> of the columns in the model
     */
    @Override
    public Enumeration getColumns() {
        return tableColumns.elements();
    }

    @Override
    public int getColumnIndex(int modelIndex) {
        Enumeration enumeration = getColumns();
        TreeTableColumn aColumn;
        int index = 0;

        while (enumeration.hasMoreElements()) {
            aColumn = (TreeTableColumn) enumeration.nextElement();
            // Compare them this way in case the column's identifier is null.
            if (aColumn.getModelIndex() == modelIndex) {
                return index;
            }
            index++;
        }

        return -1;
    }

    /**
     * Returns the index of the first column in the <code>tableColumns</code>
     * array whose identifier is equal to <code>identifier</code>,
     * when compared using <code>equals</code>.
     *
     * @param		identifier		the identifier object
     * @return		the index of the first column in the 
     *			<code>tableColumns</code> array whose identifier
     *			is equal to <code>identifier</code>
     * @exception       IllegalArgumentException  if <code>identifier</code>
     *				is <code>null</code>, or if no
     *				<code>TreeTableColumn</code> has this
     *				<code>identifier</code>
     * @see		#getColumn
     */
    @Override
    public int getColumnIndex(Object identifier) {
        if (identifier == null) {
            throw new IllegalArgumentException("Identifier is null");
        }

        Enumeration enumeration = getColumns();
        TreeTableColumn aColumn;
        int index = 0;

        while (enumeration.hasMoreElements()) {
            aColumn = (TreeTableColumn) enumeration.nextElement();
            // Compare them this way in case the column's identifier is null.
            if (identifier.equals(aColumn.getIdentifier())) {
                return index;
            }
            index++;
        }
        throw new IllegalArgumentException("Identifier not found");
    }

    /**
     * Returns the <code>TreeTableColumn</code> object for the column
     * at <code>columnIndex</code>.
     *
     * @param	columnIndex	the index of the column desired
     * @return	the <code>TreeTableColumn</code> object for the column
     *				at <code>columnIndex</code>
     */
    @Override
    public TreeTableColumn getColumn(int columnIndex) {
        return (TreeTableColumn) tableColumns.elementAt(columnIndex);
    }

    /**
     * Returns the width margin for <code>TreeTableColumn</code>.
     * The default <code>columnMargin</code> is 1.
     *
     * @return	the maximum width for the <code>TreeTableColumn</code>
     * @see	#setColumnMargin
     */
    @Override
    public int getColumnMargin() {
        return columnMargin;
    }

    /**
     * Returns the index of the column that lies at position <code>x</code>,
     * or -1 if no column covers this point.
     *
     * In keeping with Swing's separable model architecture, a
     * TreeTableColumnModel does not know how the table columns actually appear on
     * screen.  The visual presentation of the columns is the responsibility
     * of the view/controller object using this model (typically JTable).  The
     * view/controller need not display the columns sequentially from left to
     * right.  For example, columns could be displayed from right to left to
     * accomodate a locale preference or some columns might be hidden at the
     * request of the user.  Because the model does not know how the columns
     * are laid out on screen, the given <code>xPosition</code> should not be
     * considered to be a coordinate in 2D graphics space.  Instead, it should
     * be considered to be a width from the start of the first column in the
     * model.  If the column index for a given X coordinate in 2D space is
     * required, <code>JTable.columnAtPoint</code> can be used instead.
     *
     * @param  x  the horizontal location of interest
     * @return	the index of the column or -1 if no column is found
     * @see javax.swing.JTable#columnAtPoint
     */
    @Override
    public int getColumnIndexAtX(int x) {
        if (x < 0) {
            return -1;
        }
        int cc = getColumnCount();
        for (int column = 0; column < cc; column++) {
            x = x - getColumn(column).getWidth();
            if (x < 0) {
                return column;
            }
        }
        return -1;
    }

    /**
     * Returns the total combined width of all columns.
     * @return the <code>totalColumnWidth</code> property
     */
    @Override
    public int getTotalColumnWidth() {
        if (totalColumnWidth == -1) {
            recalcWidthCache();
        }
        return totalColumnWidth;
    }

//
// Selection model
//
    /**
     *  Sets the selection model for this <code>TreeTableColumnModel</code>
     *  to <code>newModel</code>
     *  and registers for listener notifications from the new selection
     *  model.  If <code>newModel</code> is <code>null</code>,
     *  an exception is thrown.
     *
     * @param	newModel	the new selection model
     * @exception IllegalArgumentException      if <code>newModel</code>
     *						is <code>null</code>
     * @see	#getSelectionModel
     */
    @Override
    public void setSelectionModel(ListSelectionModel newModel) {
        if (newModel == null) {
            throw new IllegalArgumentException("Cannot set a null SelectionModel");
        }

        ListSelectionModel oldModel = selectionModel;

        if (newModel != oldModel) {
            if (oldModel != null) {
                oldModel.removeListSelectionListener(this);
            }

            selectionModel = newModel;

            if (newModel != null) {
                newModel.addListSelectionListener(this);
            }
        }
    }

    /**
     * Returns the <code>ListSelectionModel</code> that is used to
     * maintain column selection state.
     *
     * @return	the object that provides column selection state.  Or
     *		<code>null</code> if row selection is not allowed.
     * @see	#setSelectionModel
     */
    @Override
    public ListSelectionModel getSelectionModel() {
        return selectionModel;
    }

    // implements javax.swing.table.TreeTableColumnModel
    /**
     * Sets whether column selection is allowed.  The default is false.
     * @param  flag true if column selection will be allowed, false otherwise
     */
    @Override
    public void setColumnSelectionAllowed(boolean flag) {
        columnSelectionAllowed = flag;
    }

    // implements javax.swing.table.TreeTableColumnModel
    /**
     * Returns true if column selection is allowed, otherwise false.
     * The default is false.
     * @return the <code>columnSelectionAllowed</code> property
     */
    @Override
    public boolean getColumnSelectionAllowed() {
        return columnSelectionAllowed;
    }

    // implements javax.swing.table.TreeTableColumnModel
    /**
     * Returns an array of selected columns.  If <code>selectionModel</code>
     * is <code>null</code>, returns an empty array.
     * @return an array of selected columns or an empty array if nothing
     *			is selected or the <code>selectionModel</code> is
     *			<code>null</code>
     */
    @Override
    public int[] getSelectedColumns() {
        if (selectionModel != null) {
            int iMin = selectionModel.getMinSelectionIndex();
            int iMax = selectionModel.getMaxSelectionIndex();

            if ((iMin == -1) || (iMax == -1)) {
                return new int[0];
            }

            int[] rvTmp = new int[1 + (iMax - iMin)];
            int n = 0;
            for (int i = iMin; i <= iMax; i++) {
                if (selectionModel.isSelectedIndex(i)) {
                    rvTmp[n++] = i;
                }
            }
            int[] rv = new int[n];
            System.arraycopy(rvTmp, 0, rv, 0, n);
            return rv;
        }
        return new int[0];
    }

    // implements javax.swing.table.TreeTableColumnModel
    /**
     * Returns the number of columns selected.
     * @return the number of columns selected
     */
    @Override
    public int getSelectedColumnCount() {
        if (selectionModel != null) {
            int iMin = selectionModel.getMinSelectionIndex();
            int iMax = selectionModel.getMaxSelectionIndex();
            int count = 0;

            for (int i = iMin; i <= iMax; i++) {
                if (selectionModel.isSelectedIndex(i)) {
                    count++;
                }
            }
            return count;
        }
        return 0;
    }

//
// Listener Support Methods
//

    // implements javax.swing.table.TreeTableColumnModel
    /**
     * Adds a listener for table column model events.
     * @param x  a <code>TreeTableColumnModelListener</code> object
     */
    @Override
    public void addColumnModelListener(TreeTableColumnModelListener x) {
        listenerList.add(TreeTableColumnModelListener.class, x);
    }

    // implements javax.swing.table.TreeTableColumnModel
    /**
     * Removes a listener for table column model events.
     * @param x  a <code>TreeTableColumnModelListener</code> object
     */
    @Override
    public void removeColumnModelListener(TreeTableColumnModelListener x) {
        listenerList.remove(TreeTableColumnModelListener.class, x);
    }

    /**
     * Returns an array of all the column model listeners
     * registered on this model.
     *
     * @return all of this default table column model's <code>ColumnModelListener</code>s 
     *         or an empty
     *         array if no column model listeners are currently registered
     *
     * @see #addColumnModelListener
     * @see #removeColumnModelListener
     *
     * @since 1.4
     */
    public TreeTableColumnModelListener[] getColumnModelListeners() {
        return listenerList.getListeners(TreeTableColumnModelListener.class);
    }

//
//   Event firing methods
//
    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @param e  the event received
     * @see EventListenerList
     */
    protected void fireColumnAdded(TreeTableColumnModelEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeTableColumnModelListener.class) {
                // Lazily create the event:
                // if (e == null)
                //  e = new ChangeEvent(this);
                ((TreeTableColumnModelListener) listeners[i + 1]).columnAdded(e);
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @param  e  the event received
     * @see EventListenerList
     */
    protected void fireColumnRemoved(TreeTableColumnModelEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeTableColumnModelListener.class) {
                // Lazily create the event:
                // if (e == null)
                //  e = new ChangeEvent(this);
                ((TreeTableColumnModelListener) listeners[i + 1]).columnRemoved(e);
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @param  e the event received
     * @see EventListenerList
     */
    protected void fireColumnMoved(TreeTableColumnModelEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeTableColumnModelListener.class) {
                // Lazily create the event:
                // if (e == null)
                //  e = new ChangeEvent(this);
                ((TreeTableColumnModelListener) listeners[i + 1]).columnMoved(e);
            }
        }
    }

    protected void fireColumnSorted(TreeTableColumnModelEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeTableColumnModelListener.class) {
                // Lazily create the event:
                // if (e == null)
                //  e = new ChangeEvent(this);
                ((TreeTableColumnModelListener) listeners[i + 1]).columnSorted(e);
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @param e the event received
     * @see EventListenerList
     */
    protected void fireColumnSelectionChanged(ListSelectionEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeTableColumnModelListener.class) {
                // Lazily create the event:
                // if (e == null)
                //  e = new ChangeEvent(this);
                ((TreeTableColumnModelListener) listeners[i + 1]).columnSelectionChanged(e);
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    protected void fireColumnMarginChanged() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeTableColumnModelListener.class) {
                // Lazily create the event:
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((TreeTableColumnModelListener) listeners[i + 1]).columnMarginChanged(changeEvent);
            }
        }
    }

    /**
     * Returns an array of all the objects currently registered
     * as <code><em>Foo</em>Listener</code>s
     * upon this model.
     * <code><em>Foo</em>Listener</code>s are registered using the
     * <code>add<em>Foo</em>Listener</code> method.
     *
     * <p>
     *
     * You can specify the <code>listenerType</code> argument
     * with a class literal,
     * such as
     * <code><em>Foo</em>Listener.class</code>.
     * For example, you can query a
     * <code>DefaultTreeTableColumnModel</code> <code>m</code>
     * for its column model listeners with the following code:
     *
     * <pre>ColumnModelListener[] cmls = (ColumnModelListener[])(m.getListeners(ColumnModelListener.class));</pre>
     *
     * If no such listeners exist, this method returns an empty array.
     *
     * @param listenerType the type of listeners requested; this parameter
     *          should specify an interface that descends from
     *          <code>java.util.EventListener</code>
     * @return an array of all objects registered as
     *          <code><em>Foo</em>Listener</code>s on this model,
     *          or an empty array if no such
     *          listeners have been added
     * @exception ClassCastException if <code>listenerType</code>
     *          doesn't specify a class or interface that implements
     *          <code>java.util.EventListener</code>
     * 
     * @see #getColumnModelListeners
     * @since 1.3
     */
    public EventListener[] getListeners(Class listenerType) {
        return listenerList.getListeners(listenerType);
    }

//
// Implementing the PropertyChangeListener interface
//

    /**
     * Property Change Listener change method.  Used to track changes
     * to the column width or preferred column width.
     *
     * @param  evt  <code>PropertyChangeEvent</code>
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String name = evt.getPropertyName();

        if ("width".equals(name) || name.equals(name)) {
            invalidateWidthCache();
            // This is a misnomer, we're using this method
            // simply to cause a relayout.
            fireColumnMarginChanged();
        }

    }

//
// Implementing ListSelectionListener interface
//

    // implements javax.swing.event.ListSelectionListener
    /**
     * A <code>ListSelectionListener</code> that forwards
     * <code>ListSelectionEvents</code> when there is a column
     * selection change.
     *
     * @param e  the change event
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        fireColumnSelectionChanged(e);
    }

//
// Protected Methods
//
    /**
     * Creates a new default list selection model.
     * @return
     */
    protected ListSelectionModel createSelectionModel() {
        return new DefaultListSelectionModel();
    }

    /**
     * Recalculates the total combined width of all columns.  Updates the
     * <code>totalColumnWidth</code> property.
     */
    protected void recalcWidthCache() {
        Enumeration enumeration = getColumns();
        totalColumnWidth = 0;
        while (enumeration.hasMoreElements()) {
            totalColumnWidth += ((TreeTableColumn) enumeration.nextElement()).getWidth();
        }
    }

    private void invalidateWidthCache() {
        totalColumnWidth = -1;
    }

    @Override
    public boolean isSortable() {
        return sortable;
    }

    public boolean isSorted() {
        return this.sortColumn != null;
    }

    @Override
    public void setSortable(boolean sortable) {
        if (this.sortable != sortable) {


            this.sortable = sortable;

            if (this.sortable == false) {
                if ( isSorted() ) {
                    setSort(null, sortAscending);
                }
            }
        }
    }

    @Override
    public TreeTableColumn getSortColumn() {
        return sortColumn;
    }

    @Override
    public boolean isSortAscending() {
        return sortAscending;
    }

    @Override
    public void setSort(TreeTableColumn column, boolean forward) {
        if (this.sortColumn != column || this.sortAscending != forward) {
            if ( isSortable() || column == null ) {
                this.sortColumn = column;
                this.sortAscending = forward;

                fireColumnSorted(new TreeTableColumnModelEvent(this, -1, -1));
            }
        }
    }
} // End of class DefaultTreeTableColumnModel

