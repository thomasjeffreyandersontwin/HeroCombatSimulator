/*
 * TreeTableModel.java
 *
 * Created on February 17, 2002, 2:30 PM
 */

package treeTable;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
/**
 *
 * @param <N> Type of node used by the TreeTableModel
 * @author  twalker
 * @version 
 */
public interface TreeTableModel<N> {
    
    /**
     * Returns the root of the tree.  Returns null only if the tree has
     * no nodes.
     *
     * @return  the root of the tree
     */
    public N getRoot();

    /**
     * Returns the child of <I>parent</I> at index <I>index</I> in the parent's
     * child array.  <I>parent</I> must be a node previously obtained from
     * this data source. This should not return null if <i>index</i>
     * is a valid index for <i>parent</i> (that is <i>index</i> >= 0 &&
     * <i>index</i> < getChildCount(<i>parent</i>)).
     *
     * @param   parent  a node in the tree, obtained from this data source
     * @param index Index of the child.
     * @return  the child of <I>parent</I> at index <I>index</I>
     */
    public N getChild(Object parent, int index);

    /**
     * Returns the number of children of <I>parent</I>.  Returns 0 if the node
     * is a leaf or if it has no children.  <I>parent</I> must be a node
     * previously obtained from this data source.
     *
     * @param   parent  a node in the tree, obtained from this data source
     * @return  the number of children of the node <I>parent</I>
     */
    public int getChildCount(Object parent);

    /**
     * Returns true if <I>node</I> is a leaf.  It is possible for this method
     * to return false even if <I>node</I> has no children.  A directory in a
     * filesystem, for example, may contain no files; the node representing
     * the directory is not a leaf, but it also has no children.
     *
     * @param   node    a node in the tree, obtained from this data source
     * @return  true if <I>node</I> is a leaf
     */
    public boolean isLeaf(Object node);

    /**
     * Returns the index of child in parent.
     * @param parent
     * @param child
     * @return
     */
    public int getIndexOfChild(Object parent, Object child);
    
    /**
     * Returns the number ofs availible column.
     *
     * @return
     */
    public int getColumnCount();

    /**
     * Returns the type for column number <code>column</code>.
     * @param column 
     * @return
     */
    public Class getColumnClass(int column);
    
    /**
     * Returns the number of columns the <code>column</code> should span.
     * @param node 
     * @param column
     * @param columnModel
     * @return
     */
    public int getColumnSpan(Object node, int column, TreeTableColumnModel columnModel);
    
    /** 
     * Returns the default color for the column.
     * @param node
     * @param column
     * @return
     */
    public Color getColumnColor(Object node, int column);

    /**
     * Returns the value to be displayed for node <code>node</code>, 
     * at column number <code>column</code>.
     * @param node
     * @param column
     * @return
     */
    public Object getValueAt(Object node, int column);

    /**
     * Indicates whether the the value for node <code>node</code>, 
     * at column number <code>column</code> is editable.
     * @param node 
     * @param column
     * @return
     */
    public boolean isCellEditable(Object node, int column);

    /**
     * Sets the value for node <code>node</code>, 
     * at column number <code>column</code>.
     * @param node 
     * @param column 
     * @param aValue
     */
    public void setValueAt(Object node, int column, Object aValue);

    /**
     * Returns the Icon to be used when drawing this node. 
     *
     * If the Icon is null, the standard open, closed, leaf icons will be used.
     * @param treeTable 
     * @param expanded
     * @param node
     * @param isSelected
     * @param leaf
     * @param row
     * @return
     */
    public Icon getIcon(TreeTable treeTable, Object node, boolean isSelected, boolean expanded, boolean leaf, int row);
    
    /** Returns the renderer to be used for this node and column.
     * @param node 
     * @param column 
     * @return
     */
    public TreeTableCellRenderer getCellRenderer(Object node, int column);
    
     /** Returns the editor to be used for this node and column.
      * @param node
      * @param column
      * @return
      */
    public TreeTableCellEditor getCellEditor(Object node, int column);
    
    /** Returns the Primary Column of the Model.
     *
     * The primary column is the column that is highlighted when a row is selected.  
     * The model can return an column of the model it wants or it can return -1 to cause
     * no highlighting to occur.
     * @return
     */
    public int getPrimaryColumn();
    
    /** Returns the tooltip for this object/column. 
     *
     * @param node 
     * @param evt
     * @param column
     * @return
     */
    public String getToolTipText(Object node, int column, MouseEvent evt);
    
//
//  Change Events
//

    /**
     * Adds a listener for the TreeModelEvent posted after the tree changes.
     *
     * @see     #removeTreeModelListener
     * @param   l       the listener to add
     */
    public void addTreeModelListener(TreeModelListener l);

    /**
     * Removes a listener previously added with <B>addTreeModelListener()</B>.
     *
     * @see     #addTreeModelListener
     * @param   l       the listener to remove
     */  
    public void removeTreeModelListener(TreeModelListener l);

    /** Adds a property change listener.
     *
     * @param propertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);

    /** Removes a previously added property change listener.
     * 
     * @param propertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener);
    
    /** 
     * Returns the icon to use when the OpenIcon is needed.  
     *
     * If this method returns null, open icons will not be drawn.
     * @return
     */
    public Icon getOpenIcon();
    
    /** 
     * Returns the icon to use when the ClosedIcon is needed.  
     *
     * If this method returns null, open icons will not be drawn.
     * @return
     */
    public Icon getClosedIcon();
    
    /** 
     * Returns the icon to use when the LeafIcon is needed.  
     *
     * If this method returns null, open icons will not be drawn.
     * @return
     */
    public Icon getLeafIcon();
    
    /** 
     * Returns the Column Model to be used with this Model initially.
     *
     * The column model can be changed by the user.  If a model wants to
     * be notified of these changes, it must register a listener.
     *
     * If the model returns null, a column model will be built with the  
     * all columns.
     * @return
     */
    public TreeTableColumnModel getColumnModel();
    
    /** Build the popup menu for node and column.
     * 
     * The tree table will run through all the node in the TreePath, starting
     * with the deepest on clicked on.
     * 
     * @param treeTable TreeTable invoking the menu.
     * @param selectionPaths Currently selected paths.
     * @param popup Popup menu to populate.
     * @return true if a menu was added, false otherwise.
     */
    public boolean invokeMenu(TreeTable treeTable, TreePath[] selectionPaths, javax.swing.JPopupMenu popup);
    
}

