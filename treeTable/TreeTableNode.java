/*
 * TreeTableNode.java
 *
 * Created on February 27, 2002, 6:49 PM
 */

package treeTable;

import javax.swing.tree.TreePath;
/**
 *
 * @author twalker
 * @version 1
 */
public interface TreeTableNode {
    /** Notifies Node of a drop event occuring in or below the node.
     * The Node can use the handleDrop method to perform actions based on the drop of a DetailList on
     * the Node Jtree.  If the node wishes to handles the event, it should call acceptDrop and dropComplete
     * on the event, then return true to indicate the event was handled.
     * @returns True if event was handled.  False if additional handling
     * should be done.
     *
     * @return True if the event was handled, false if it wasn't.
     * @param treeTable Indicates the TreeTable which is currently displaying the node.
     * @param dropPath Indicates the location of the drop in the Tree hierarchy.
     * @param event The Drop event that is being handled.
     */
    
    public boolean handleDrop(TreeTable treeTable, TreePath dropPath, java.awt.dnd.DropTargetDropEvent event);
    
    /** Called to check if a node would handle a drop if it occurred.
     *
     * @return Returns the TreePath after which a feedback line could be drawn indicating where the drop will be placed.
     * @param treeTable Indicates the TreeTable which is currently displaying the node.
     * @param dropPath Indicates the location of the drop in the Tree hierarchy.
     * @param event The Drop event that is being handled.
     */
    
    public TreePath willHandleDrop(TreeTable treeTable, TreePath dropPath, java.awt.dnd.DropTargetDragEvent event);
    
    /** Getter for property expandDuringDrag.
     *
     * Indicates that the node should be expanded, after a suitable amount of time, if the cursor is
     * over this node during the indicated drag.
     * @return Value of property expandDuringDrag.
     * @param treeTable Indicates the TreeTable which is currently displaying the node.
     * @param dropPath Indicates the TreePath at which may be expanded.
     * @param event The DropTargetDragEvent event which is current being processed.
     */
    public boolean expandDuringDrag(TreeTable treeTable, TreePath dropPath, java.awt.dnd.DropTargetDragEvent event);
    
    /** Notifies the node that it should start a drag.
     *
     * startDrag is called when the TreeTable detects a gesture which would normally start the drag and drop
     * process.  If this node is capable of participating in drag and drop, it should create the appropriate
     * transferrable and call startDrag on the DragGestureEvent.
     *
     * @param treeTable Indicates the TreeTable which is currently displaying the node.
     * @param dragPath Indicates the TreePath at which the drag gesture occurred.
     * @param listener The source listener which should be specified in the startDrag method.
     * @param dge The drag gesture event which occurred.  This can be used to start
     * the drag by calling the method <code>dge.startDrag</CODE>.
     */
    public boolean startDrag(TreeTable treeTable, TreePath dragPath, java.awt.dnd.DragSourceListener listener, java.awt.dnd.DragGestureEvent dge);
    
    /** Allows the node to provide context menu feedback.
     *
     * invokeMenu is called whenever an event occurs which would cause a context menu to be displayed.
     * If the node has context menu items to display, it should add them to the JPopupMenu <code>popup</code>
     * and return true.
     *
     * @param treeTable Indicates the TreeTable which is currently displaying the node.
     * @param path The TreePath which the cursor was over when the context menu
     * event occurred.
     * @param popup The JPopupMenu which is being built.  Always non-null and initialized.
     * @return True if menus where added, False if menus weren't.
     */
    public boolean invokeMenu(TreeTable treeTable,TreePath path, javax.swing.JPopupMenu popup);
    
    /** Returns the Nodes custom input map.
     * This method should return a custom input map of action the Node can perform and has key bindings for.
     * When this node is selected, the Tree will load this input map and bind the keys appropriately.
     *
     * MenuItems returned by invokeMenu which have key bindings should populate an input map and return it.
     *
     * @return An InputMap which will be added to the current input map.
     * @param treeTable The TreeTable which is currently displaying the node.
     * @param path The path of the active tree node.  This is not necessarily the path
     * of the node being queried.
     */
    public javax.swing.InputMap getInputMap(TreeTable treeTable, TreePath path);
    
    /** Returns the Nodes custom action map.
     * This method should return a custom action map of action the Node can perform and has key bindings for.
     * When this node is selected, the Tree will load this action map and bind the keys appropriately.
     *
     * MenuItems returned by invokeMenu which have key bindings should populate an action map and return it.
     *
     * @return An ActionMap which will be merged with the TreeTables action map.
     * @param treeTable The TreeTable which is currently displaying the node.
     * @param path The path of the active tree node.  This is not necessarily the path
     * of the node being queried.
     */
    public javax.swing.ActionMap getActionMap(TreeTable treeTable, TreePath path);
    
    /** Returns whether the node has data which can be cut/copied.
     *
     * If the node returns true, it will be able to provide data for the requested operation.
     *
     * @return True if the node can execute a <CODE>copyOrCutNode</CODE> successfully.
     * @param treeTable The TreeTable which is currently displaying the node.
     * @param path The TreePath which is going to copied or cut.
     * @param cut If True, indicates the node will be cut.  If false, indicates the node will be copied.
     */
    public boolean isCopyOrCutEnabled(TreeTable treeTable, TreePath path, boolean cut);
    
    /** Causes the node to perform a copy or cut.
     *
     * This method triggers the node, if possible, to perform a copy/cut.  It should place the
     * copy/cut information onto the global clipboard.
     *
     * @param treeTable The TreeTable which is currently displaying the node.
     * @param path The TreePath which should be copied or cut.
     * @param cut If True, indicates the node should be cut.  If false, indicates the node should be copied.
     */
    public void copyOrCutNode(TreeTable treeTable, TreePath path, boolean cut);
    
    /** Returns whether the node is able to accept the data indicated in the transferable as a past operation.
     *
     * If the node returns true, it will be able to support the requested data.
     *
     * @param treeTable The TreeTable which is currently displaying the node.
     * @param path The TreePath which is receiving the paste request.
     * @param transferable The transferable which would be pasted.
     * @return True if the node will accept the paste, False if it won't.
     */
    public boolean isPasteEnabled(TreeTable treeTable, TreePath path, java.awt.datatransfer.Transferable transferable);
    
    /** Causes the node to perform a paste, getting data from the indicated transferable.
     *
     * This method triggers the node, if possible, to perform a paste.
     *
     * @param treeTable The TreeTable which is currently displaying the node.
     * @param path The TreePath which is receiving the paste request.
     * @param transferable The transferable which should be pasted.
     */
    public void pasteData(TreeTable treeTable, TreePath path, java.awt.datatransfer.Transferable transferable);
    
    /** Called to destroy the object.  All references should be set to null.
     * Once destroy has been called, the object will not be used anymore.
     */
    public void destroy();
    
    /** Getter for property model.
     *
     * Returns the model that is using this node.  Depending on the usage of the node, it is possible
     * that this model will not be set.  The model should always be checked to make sure it isn't null
     * prior to use.
     *
     * @return Value of property model.
     */
    public TreeTableModel getModel();
    
    /** Setter for property model.
     *
     * Sets the model for this node to be the specified model.  Typically, all nodes in a particular tree
     * should be set to the same model.  Thus, set model usually should result in the model of all children
     * being set appropriately.
     *
     * @param model New value of property model.
     */
    public void setModel(TreeTableModel model);
    
    /** Getter for property TableTree.
     *
     * Returns the tree that is using this node.  Depending on the usage of the node, it is possible
     * that this tree will not be set.  The tree should always be checked to make sure it isn't null
     * prior to use.
     *
     * @return Value of property tree.
     */
    public TreeTable getTree();
    
    /** Setter for property TableTree.
     *
     * Sets the tree for this node to be the specified tree.  Typically, all nodes in a particular tree
     * should return the same tree.  Thus, setTree usually should result in the tree of all children
     * being set appropriately.
     *
     * @param tree The TreeTable which is currently displaying the node.
     */
    public void setTree(TreeTable tree);
    
    /** Gets the nodes preferred CellRenderer for the column <code>columnIndex</code>.
     *
     * Returns the Nodes preferred CellRenderer for the indicated column.  Null can be returned to 
     * indicate that a default renderer can be used.
     *
     * @param columnIndex The Index of the column being rendered.
     * @return The TreeTableCellRenderer to use rendering the indicated column of this node.
     */
    public TreeTableCellRenderer getTreeTableCellRenderer(int columnIndex);
    
    /** Gets the nodes preferred CellEditor for the column <code>columnIndex</code>.
     *
     * Returns the Nodes preferred CellEditor for the indicated column.  Null can be returned to 
     * indicate that a default editor can be used.
     *
     * @param columnIndex The Index of the column being rendered.
     * @return The TreeTableCellEditor to use editing the indicated column of this node.
     */
    public TreeTableCellEditor getTreeTableCellEditor(int columnIndex);
    
    /**
     * Returns the value to be displayed for node <code>node</code>,
     * at column number <code>column</code>.
     * @param column The column of the node for which a value should be returned.
     * @return The Value of the column.
     */
    public Object getValueAt(int column);
    
    /**
     * Sets the value for node <code>node</code>,
     * at column number <code>column</code>.
     * @param column The column of the node for which a value should be set.
     * @param aValue The value which the node/column should be set to.
     */
    public void setValueAt(int column, Object aValue);

    /**
     * Indicates whether the the value for node <code>node</code>,
     * at column number <code>column</code> is editable.
     * @param column The column of the node which is being checked for editability.
     * @return True if the node can be edited, false if it is non-editable.
     */
    public boolean isCellEditable(int column);

    /**
     * Returns the Icon to be used when drawing this node.
     *
     * If the Icon is null, the standard open, closed, leaf icons will be used.
     * @param treeTable The treeTable which is currently displaying the node.
     * @param isSelected Whether this node is currently selected.
     * @param expanded Whether this node is currently expanded.
     * @param leaf Whether this node is currently considered a leaf.
     * @param row The row at which the node is current displayed in the TreeTable.
     * @return An Icon which should be used for this node.  Null if default icons should be used.
     */
     public javax.swing.Icon getIcon(TreeTable treeTable, boolean isSelected, boolean expanded, boolean leaf, int row);
    
     /** Returns the number of columns the <code>column</code> should span.
      *
      * If the node wishes a column to span multiple columns, it can indicate a columnSpan greater then
      * 1.  However, since the node often doesn't know the order in which the columns are currently arranged,
      * it may span columns it does not intend too.
      * @param column The column of the node which is being rendered.
      * @return The maximum number of columns this node/column should span.
      */
    public int getColumnSpan(int column, TreeTableColumnModel columnModel);
}

