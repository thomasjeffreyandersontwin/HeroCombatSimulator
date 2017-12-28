/*
 * DefaultTreeTableModel.java
 *
 * Created on February 17, 2002, 3:48 PM
 */
package treeTable;

import java.awt.Color;
import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.Comparator;
import java.util.Vector;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import tjava.Destroyable;
import tjava.Filter;

/**
 *
 * @param <N> Type of node used by the model.
 * @author  twalker
 * @version 
 */
@SuppressWarnings({"serial", "deprecation"})
public class DefaultTreeTableModel<N extends TreeNode> extends Object
        implements TreeTableModel<N>, Serializable, Destroyable,
                   TreeTableDnDHandler, TreeTableLegacyDnDHandler,
                   SortableTreeTableModelDelegate<N>, FilterableTreeTableModelDelegate<N> {

    /**
     *
     */
    protected N root = null;

    protected boolean legacyDnDSupported = true;

    protected boolean newDnDSupported = false;

    protected TreeTableColumnModel columnModel = null;

    /** Listeners. */
    transient protected EventListenerList listenerList;

    transient protected PropertyChangeSupport propertyChangeSupport;

    /**
     *
     */
    transient protected Icon openIcon;

    /**
     *
     */
    transient protected Icon closedIcon;

    /**
     *
     */
    transient protected Icon leafIcon;

    transient private SoftReference<N> lastCanImportNodeReference = null;

    /**
     * Determines how the <code>isLeaf</code> method figures
     * out if a node is a leaf node. If true, a node is a leaf 
     * node if it does not allow children. (If it allows 
     * children, it is not a leaf node, even if no children
     * are present.) That lets you distinguish between <i>folder</i>
     * nodes and <i>file</i> nodes in a file system, for example.
     * <p>
     * If this value is false, then any node which has no 
     * children is a leaf node, and any node may acquire 
     * children.
     *
     * @see TreeNode#getAllowsChildren
     * @see TreeModel#isLeaf
     * @see #setAsksAllowsChildren
     */
    protected boolean asksAllowsChildren;

    /**
     *
     */
    protected boolean alwaysDrawIcon = true;

    private boolean sortableDelegate;

    private boolean filterable;

//    private int sortColumnIndex = -1;
//
//    private boolean sortAscending = true;
    /** Creates new DefaultTreeTableModel */
    public DefaultTreeTableModel() {
    }

    /**
     *
     * @param root
     */
    public DefaultTreeTableModel(N root) {
        this(root, false);
    }

    /**
     * Creates a tree specifying whether any node can have children,
     * or whether only certain nodes can have children.
     *
     * @param root a N object that is the root of the tree
     * @param asksAllowsChildren a boolean, false if any node can
     *        have children, true if each node is asked to see if
     *        it can have children
     * @see #asksAllowsChildren
     */
    public DefaultTreeTableModel(N root, boolean asksAllowsChildren) {
        super();
        if (root == null) {
            throw new IllegalArgumentException("root is null");
        }
        this.root = root;
        this.asksAllowsChildren = asksAllowsChildren;

        setupIcons();
    }

    /**
     * Returns the root of the tree.  Returns null only if the tree has
     * no nodes.
     *
     * @return  the root of the tree
     */
    @Override
    public N getRoot() {
        return root;
    }

    /**
     *
     * @param root
     */
    protected void setRoot(N root) {
        this.root = root;
    }

    /**
     *
     */
    protected void setupIcons() {
        setLeafIcon(UIManager.getIcon("Tree.leafIcon"));
        setClosedIcon(UIManager.getIcon("Tree.closedIcon"));
        setOpenIcon(UIManager.getIcon("Tree.openIcon"));
    }

    /**
     * Returns the number of columns the <code>column</code> should span.
     * @param node
     * @param column
     * @param columnModel
     * @return
     */
    @Override
    public int getColumnSpan(Object node, int column, TreeTableColumnModel columnModel) {
        if (node instanceof DefaultTreeTableNode) {
            return ((DefaultTreeTableNode) node).getColumnSpan(column, columnModel);
        }

        return 1;
    }

    /**
     * Returns the name for column number <code>column</code>.
     * @param column
     * @return
     */
    public String getColumnName(int column) {
        return "";
    }

    /**
     * Returns the index of child in parent.
     * @param parent
     * @param child
     * @return
     */
    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent == null || child == null) {
            return -1;
        }
        return ((N) parent).getIndex((N) child);
    }

    /**
     * Returns true if <I>node</I> is a leaf.  It is possible for this method
     * to return false even if <I>node</I> has no children.  A directory in a
     * filesystem, for example, may contain no files; the node representing
     * the directory is not a leaf, but it also has no children.
     *
     * @param   node    a node in the tree, obtained from this data source
     * @return  true if <I>node</I> is a leaf
     */
    @Override
    public boolean isLeaf(Object node) {
        if (asksAllowsChildren) {
            return !((N) node).getAllowsChildren();
        }
        return ((N) node).isLeaf();
    }

    /**
     * Returns the value to be displayed for node <code>node</code>,
     * at column number <code>column</code>.
     * @param node 
     * @param column
     * @return
     */
    @Override
    public Object getValueAt(Object node, int column) {
        if (node instanceof DefaultTreeTableNode) {
            return ((DefaultTreeTableNode) node).getValueAt(column);
        }

        return (column == 0) ? node.toString() : null;
    }

    /**
     * Sets the value for node <code>node</code>,
     * at column number <code>column</code>.
     * @param node
     * @param column
     * @param aValue
     */
    @Override
    public void setValueAt(Object node, int column, Object aValue) {
        if (node instanceof DefaultTreeTableNode) {
            ((DefaultTreeTableNode) node).setValueAt(column, aValue);
        }
    }

    /**
     * Returns the child of <I>parent</I> at index <I>index</I> in the parent's
     * child array.  <I>parent</I> must be a node previously obtained from
     * this data source. This should not return null if <i>index</i>
     * is a valid index for <i>parent</i> (that is <i>index</i> >= 0 &&
     * <i>index</i> < getChildCount(<i>parent</i>)).
     *
     * @param   parent  a node in the tree, obtained from this data source
     * @param index
     * @return  the child of <I>parent</I> at index <I>index</I>
     */
    @Override
    public N getChild(Object parent, int index) {
        return (N) ((N) parent).getChildAt(index);
    }

    /**
     * Returns the type for column number <code>column</code>.
     * @param column
     * @return
     */
    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    /**
     * Indicates whether the the value for node <code>node</code>,
     * at column number <code>column</code> is editable.
     * @param node 
     * @param column 
     * @return
     */
    @Override
    public boolean isCellEditable(Object node, int column) {
        if (node instanceof DefaultTreeTableNode) {
            return ((DefaultTreeTableNode) node).isCellEditable(column);
        }

        return false;
    }

    /**
     * Returns the number of children of <I>parent</I>.  Returns 0 if the node
     * is a leaf or if it has no children.  <I>parent</I> must be a node
     * previously obtained from this data source.
     *
     * @param   parent  a node in the tree, obtained from this data source
     * @return  the number of children of the node <I>parent</I>
     */
    @Override
    public int getChildCount(Object parent) {
        return ((N) parent).getChildCount();
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        if ( listenerList == null ) {
            listenerList = new EventListenerList();
        }
        listenerList.add(TreeModelListener.class, l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        if ( listenerList != null ) {
            listenerList.remove(TreeModelListener.class, l);
        }
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        if ( propertyChangeSupport == null ) {
            propertyChangeSupport = new PropertyChangeSupport(this);
        }
        propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        if ( propertyChangeSupport != null ) {
            propertyChangeSupport.removePropertyChangeListener(propertyChangeListener);
        }
    }

    /**
     * Returns the number ofs availible column.
     * @return
     */
    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public Icon getIcon(TreeTable treeTable, Object node, boolean isSelected, boolean expanded, boolean leaf, int row) {
        if (node instanceof TreeTableNode) {
            return ((TreeTableNode) node).getIcon(treeTable, isSelected, expanded, leaf, row);
        }

        return null;
    }

    /** Returns the renderer to be used for this node and column.
     * @param node 
     * @param column
     * @return
     */
    @Override
    public TreeTableCellRenderer getCellRenderer(Object node, int column) {
        if (node instanceof DefaultTreeTableNode) {
            return ((DefaultTreeTableNode) node).getTreeTableCellRenderer(column);
        }

        return null;
    }

    /** Returns the editor to be used for this node and column.
     * @param node 
     * @param column
     * @return
     */
    @Override
    public TreeTableCellEditor getCellEditor(Object node, int column) {
        if (node instanceof DefaultTreeTableNode) {
            return ((DefaultTreeTableNode) node).getTreeTableCellEditor(column);
        }

        return null;
    }

    /** Returns the Primary Column of the Model.
     *
     * The primary column is the column that is highlighted when a row is selected.  
     * The model can return an column of the model it wants or it can return -1 to cause
     * no highlighting to occur.
     * @return
     */
    @Override
    public int getPrimaryColumn() {
        return 0;
    }

    /**
     * Invoke this method if you've modified the TreeNodes upon which this
     * model depends.  The model will notify all of its listeners that the
     * model has changed.
     */
    public void reload() {
        reload(root);
    }

    /** 
     * Returns the Column Model to be used with this Model initially.
     *
     * The column model can be changed by the user via GUI interaction.
     * If the tree table model wants to be notified of these changes,
     * it must register a listener.
     *
     * The column model is cached by the DefaultTreeTableModel.  If the
     * column model has not been created, it will be created via the
     * buildColumnModel method.  Subclasses should override that method
     * if they wish to customize model creation.
     * 
     * @return
     */
    @Override
    public TreeTableColumnModel getColumnModel() {
        if (columnModel == null) {
            columnModel = buildColumnModel();
        }
        return columnModel;
    }

    /** Sets the column model for this TreeTableModel.
     *
     * This method has no effect after the model has been attached to
     * a tree via the TreeTable.setTreeTableModel method.  If you wish
     * to see the column model at that point, set it on the tree
     * directly.
     *
     * @param columnModel
     */
    public void setColumnModel(TreeTableColumnModel columnModel) {
        if (this.columnModel != columnModel) {
            if (this.columnModel != null) {
            }

            this.columnModel = columnModel;

            if (this.columnModel != null) {
            }
        }
    }

    /** Create the ColumnModel based upon the column.
     *
     * @return
     */
    public TreeTableColumnModel buildColumnModel() {
        TreeTableColumnModel tableColumnModel = new DefaultTreeTableColumnModel();

        int count, index;

        count = getColumnCount();

        int[] visibleColumns = getVisibleColumns();

        // If the visible columns is null, show all of the 
        // columns...so fill the visibleColumns with the 
        // model indeciies in ascending order.
        if (visibleColumns == null) {
            visibleColumns = new int[count];
            for (int i = 0; i < count; i++) {
                visibleColumns[i] = i;
            }
        }

//        TreeTableColumn sortColumn = null;
//        int sortIndex = getSortColumnIndex();

        for (int modelIndex : visibleColumns) {
            TreeTableColumn tc = buildColumn(modelIndex);

            tableColumnModel.addColumn(tc);

//            if (sortIndex == tc.getModelIndex()) {
//                sortColumn = tc;
//            }
        }

        tableColumnModel.setSortable(isSortableDelegate());
//        tableColumnModel.setSort(sortColumn, isSortAscending());

        //setTableColumnModel(tableColumnModel);
        return tableColumnModel;
    }

    /** Returns an array of the initially visible columns.
     *
     *  The values of the array should correspond to the
     *  model indecies of the visible columns.
     *
     *  @return
     * @returns Array of visible column modelIndecies, in the order to be displayed.  Null if all columns should be displayed.
     */
    public int[] getVisibleColumns() {
        return null;
    }

    /** Returns whether a column is allowed by the current model.
     *
     *  Model that wish to control which columns are visible
     *  can do so via this method.  The user is able to 
     *  show allowed column even if they are not in the 
     *  original visible columns.
     * @param modelIndex 
     * @return
     */
    public boolean isColumnAllowed(int modelIndex) {
        return true;
    }

    /** Returns whehter a column is required by the current model.
     *
     *  If true, the model requires the column to be visible.  
     *  If false, the column can be hidden by the user.
     * @param modelIndex 
     * @return
     */
    public boolean isColumnRequired(int modelIndex) {
        return false;
    }

    /** Builds the initial TreeTableColumn for column modelIndex.
     *
     *  TreeTable builds columns based only upon the name,
     *  width, and preferred width of a column.  Using buildColumn
     *  a model is allowed more control over the column building
     *  process without implementing the full buildColumnModel method.
     * @param modelIndex 
     * @return
     */
    public TreeTableColumn buildColumn(int modelIndex) {
        TreeTableColumn tc = new TreeTableColumn(modelIndex);
        tc.setHeaderValue(getColumnName(modelIndex));
        int preferredWidth = getColumnPreferredWidth(modelIndex);
        int minimumWidth = getColumnMinimumWidth(modelIndex);
        int maximumWidth = getColumnMaximumWidth(modelIndex);
        int width = 85;

        if (preferredWidth != -1) {
            if (minimumWidth != -1) {
                preferredWidth = Math.max(minimumWidth, preferredWidth);
            }

            if (maximumWidth != -1) {
                preferredWidth = Math.min(maximumWidth, preferredWidth);
            }

            width = preferredWidth;
        }
        else {
            if (minimumWidth != -1) {
                width = Math.max(width, minimumWidth);
            }

            if (maximumWidth != -1) {
                width = Math.min(width, maximumWidth);
            }
        }

        if (preferredWidth != -1) {
            tc.setPreferredWidth(preferredWidth);
        }

        if (minimumWidth != -1) {
            tc.setMinWidth(minimumWidth);
        }

        if (maximumWidth != -1) {
            tc.setMinWidth(maximumWidth);
        }

        tc.setWidth(85);

        Icon icon = getColumnHeaderIcon(modelIndex);
        tc.setIcon(icon);

        return tc;
    }

    /**
     * Invoked this to insert newChild at location index in parents children.
     * This will then message nodesWereInserted to create the appropriate
     * event. This is the preferred way to add children as it will create
     * the appropriate event.
     * @param newChild
     * @param parent
     * @param index
     */
    public void insertNodeInto(MutableTreeNode newChild,
                               MutableTreeNode parent, int index) {
        parent.insert(newChild, index);

        int[] newIndexs = new int[1];

        newIndexs[0] = index;
        nodesWereInserted(parent, newIndexs);
    }

    /**
     * Message this to remove node from its parent. This will message
     * nodesWereRemoved to create the appropriate event. This is the
     * preferred way to remove a node as it handles the event creation
     * for you.
     * @param node
     */
    public void removeNodeFromParent(MutableTreeNode node) {
        MutableTreeNode parent = (MutableTreeNode) node.getParent();

        if (parent == null) {
            throw new IllegalArgumentException("node does not have a parent.");
        }
        int[] childIndex = new int[1];
        Object[] removedArray = new Object[1];

        childIndex[0] = parent.getIndex(node);
        parent.remove(childIndex[0]);
        removedArray[0] = node;
        nodesWereRemoved(parent, childIndex, removedArray);
    }

    /**
     * Invoke this method after you've changed how node is to be
     * represented in the tree.
     * @param node
     */
    public void nodeChanged(TreeNode node) {
    	//Jeff
        //assert SwingUtilities.isEventDispatchThread();

        if (listenerList != null && node != null) {
            TreeNode parent = node.getParent();

            if (parent != null) {
                int anIndex = parent.getIndex(node);
                if (anIndex != -1) {
                    int[] cIndexs = new int[1];

                    cIndexs[0] = anIndex;
                    nodesChanged(parent, cIndexs);
                }
            }
            else if (node == getRoot()) {
                nodesChanged(node, null);
            }
        }
    }

    /**
     * Invoke this method if you've modified the TreeNodes upon which this
     * model depends.  The model will notify all of its listeners that the
     * model has changed below the node <code>node</code>.
     * @param node
     */
    public void reload(N node) {
        if (node != null) {
            fireTreeStructureChanged(this, getPathToRoot(node), null, null);
        }
    }

    /**
     * Invoke this method after you've inserted some TreeNodes into
     * node.  childIndices should be the index of the new elements and
     * must be sorted in ascending order.
     * @param node 
     * @param childIndices
     */
    public void nodesWereInserted(TreeNode node, int[] childIndices) {
        assert SwingUtilities.isEventDispatchThread();

        if (listenerList != null && node != null && childIndices != null && childIndices.length > 0) {
            int cCount = childIndices.length;
            Object[] newChildren = new Object[cCount];

            for (int counter = 0; counter < cCount; counter++) {
                newChildren[counter] = node.getChildAt(childIndices[counter]);
            }
            fireTreeNodesInserted(this, getPathToRoot(node), childIndices,
                    newChildren);
        }
    }

    /**
     * Invoke this method after you've removed some TreeNodes from
     * node.  childIndices should be the index of the removed elements and
     * must be sorted in ascending order. And removedChildren should be
     * the array of the children objects that were removed.
     * @param node 
     * @param childIndices 
     * @param removedChildren
     */
    public void nodesWereRemoved(TreeNode node, int[] childIndices,
                                 Object[] removedChildren) {
        assert SwingUtilities.isEventDispatchThread();

        if (node != null && childIndices != null) {
            fireTreeNodesRemoved(this, getPathToRoot(node), childIndices,
                    removedChildren);
        }
    }

    /**
     * Invoke this method after you've changed how the children identified by
     * childIndicies are to be represented in the tree.
     * @param node 
     * @param childIndices
     */
    public void nodesChanged(TreeNode node, int[] childIndices) {
       // assert SwingUtilities.isEventDispatchThread();

        if (node != null) {
            if (childIndices != null) {
                int cCount = childIndices.length;

                if (cCount > 0) {
                    Object[] cChildren = new Object[cCount];

                    for (int counter = 0; counter < cCount; counter++) {
                        cChildren[counter] = node.getChildAt(childIndices[counter]);
                    }
                    fireTreeNodesChanged(this, getPathToRoot(node),
                            childIndices, cChildren);
                }
            }
            else if (node == getRoot()) {
                fireTreeNodesChanged(this, getPathToRoot(node), null, null);
            }
        }
    }

    /**
     * Invoke this method if you've totally changed the children of
     * node and its childrens children...  This will post a
     * treeStructureChanged event.
     * @param node
     */
    public void nodeStructureChanged(TreeNode node) {
        //assert SwingUtilities.isEventDispatchThread();
        
        if (node != null) {
            fireTreeStructureChanged(this, getPathToRoot(node), null, null);
        }
    }

    /**
     * Builds the parents of node up to and including the root node,
     * where the original node is the last element in the returned array.
     * The length of the returned array gives the node's depth in the
     * tree.
     * 
     * @param aNode the TreeNode to get the path for
     * @return
     */
    public TreeNode[] getPathToRoot(TreeNode aNode) {
        return getPathToRoot(aNode, 0);
    }

    /**
     * Builds the parents of node up to and including the root node,
     * where the original node is the last element in the returned array.
     * The length of the returned array gives the node's depth in the
     * tree.
     * 
     * @param aNode  the TreeNode to get the path for
     * @param depth  an int giving the number of steps already taken towards
     *        the root (on recursive calls), used to size the returned array
     * @return an array of TreeNodes giving the path from the root to the
     *         specified node 
     */
    protected TreeNode[] getPathToRoot(TreeNode aNode, int depth) {
        TreeNode[] retNodes;
        // This method recurses, traversing towards the root in order
        // size the array. On the way back, it fills in the nodes,
        // starting from the root and working back to the original node.

        /* Check for null, in case someone passed in a null node, or
        they passed in an element that isn't rooted at root. */
        if (aNode == null) {
            if (depth == 0) {
                return null;
            }
            else {
                retNodes = new TreeNode[depth];
            }
        }
        else {
            depth++;
            if (aNode == root) {
                retNodes = new TreeNode[depth];
            }
            else {
                retNodes = getPathToRoot(aNode.getParent(), depth);
            }
            retNodes[retNodes.length - depth] = aNode;
        }
        return retNodes;
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     *
     * @param source
     * @param path
     * @param childIndices
     * @param children
     */
    protected void fireTreeNodesChanged(Object source, Object[] path,
                                        int[] childIndices,
                                        Object[] children) {
        // Guaranteed to return a non-null array
        if ( listenerList != null ) {
            Object[] listeners = listenerList.getListenerList();
            TreeModelEvent e = null;
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == TreeModelListener.class) {
                    // Lazily create the event:
                    if (e == null) {
                        e = new TreeModelEvent(source, path,
                                childIndices, children);
                    }
                    ((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
                }
            }
        }
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     *
     * @param source
     * @param path
     * @param childIndices
     * @param children
     */
    protected void fireTreeNodesInserted(Object source, Object[] path,
                                         int[] childIndices,
                                         Object[] children) {
        // Guaranteed to return a non-null array
        if ( listenerList != null ) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event:
                if (e == null) {
                    e = new TreeModelEvent(source, path,
                            childIndices, children);
                }
                ((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
            }
        }
        }
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     * @param source
     * @param path
     * @param childIndices
     * @param children
     */
    protected void fireTreeNodesRemoved(Object source, Object[] path,
                                        int[] childIndices,
                                        Object[] children) {
        // Guaranteed to return a non-null array
        if ( listenerList != null ) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event:
                if (e == null) {
                    e = new TreeModelEvent(source, path,
                            childIndices, children);
                }
                ((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
            }
        }
        }
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    /**
     *
     * @param source
     * @param path
     * @param childIndices
     * @param children
     */
    protected void fireTreeStructureChanged(Object source, Object[] path,
                                            int[] childIndices,
                                            Object[] children) {
        // Guaranteed to return a non-null array
        if ( listenerList != null ) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event:
                if (e == null) {
                    e = new TreeModelEvent(source, path,
                            childIndices, children);
                }
                ((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
            }
        }
        }
    }

    /**
     * Support for reporting bound property changes for Object properties.
     * This method can be called when a bound property has changed and it will
     * send the appropriate PropertyChangeEvent to any registered
     * PropertyChangeListeners.
     *
     * @param propertyName the property whose value has changed
     * @param oldValue the property's previous value
     * @param newValue the property's new value
     */
    protected void firePropertyChange(String propertyName,
                                      Object oldValue, Object newValue) {
        if (propertyChangeSupport != null &&  (oldValue != newValue && (oldValue == null || oldValue.equals(newValue) == false)) ) {
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    // Serialization support.  

    private void writeObject(ObjectOutputStream s) throws IOException {
        Vector values = new Vector();

        s.defaultWriteObject();
        // Save the root, if its Serializable.
        if (root != null && root instanceof Serializable) {
            values.addElement("root");
            values.addElement(root);
        }
        s.writeObject(values);
    }

    private void readObject(ObjectInputStream s)
            throws IOException, ClassNotFoundException {
        s.defaultReadObject();

        Vector values = (Vector) s.readObject();
        int indexCounter = 0;
        int maxCounter = values.size();

        if (indexCounter < maxCounter && values.elementAt(indexCounter).
                equals("root")) {
            root = (N) values.elementAt(++indexCounter);
            indexCounter++;
        }
    }

    /**
     *
     */
    @Override
    public void destroy() {
        if (root instanceof Destroyable) {
            ((Destroyable) root).destroy();
        }

        root = null;
    }

    public int getColumnPreferredWidth(int column) {
        return -1;
    }

    public int getColumnMinimumWidth(int column) {
        return -1;
    }

    public int getColumnMaximumWidth(int column) {
        return -1;
    }

    @Override
    public Icon getOpenIcon() {
        return openIcon;
    }

    /**
     *
     * @param openIcon
     */
    public void setOpenIcon(Icon openIcon) {
        this.openIcon = openIcon;
    }

    @Override
    public Icon getClosedIcon() {
        return closedIcon;
    }

    /**
     *
     * @param closedIcon
     */
    public void setClosedIcon(Icon closedIcon) {
        this.closedIcon = closedIcon;
    }

    @Override
    public Icon getLeafIcon() {
        return leafIcon;
    }

    /**
     *
     * @param leafIcon
     */
    public void setLeafIcon(Icon leafIcon) {
        this.leafIcon = leafIcon;
    }

    public Icon getColumnHeaderIcon(int column) {
        return null;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isSortableDelegate() {
        return sortableDelegate;
    }

    /** Sets whether the model is a sortable delegate.
     *
     * This actually enables/disable the model as a sortable delegate,
     * not as a direct SortableTreeTableModel.
     *
     * @param sortable
     */
    public void setSortableDelegate(boolean sortable) {
        if (this.sortableDelegate != sortable) {

            this.sortableDelegate = sortable;

            firePropertyChange(SORTABLE_DELEGATE_PROPERTY, !sortable, sortable);
        }
    }

    @Override
    public Comparator<N> getSortComparator(N parentNode, int columnIndex, boolean ascending) {
        if (isSortableDelegate() && parentNode instanceof SortableTreeTableNode) {
            SortableTreeTableNode<N> sortableTreeTableNode = (SortableTreeTableNode<N>) parentNode;
            return sortableTreeTableNode.getSortComparator(columnIndex, ascending);
        }
        return null;
    }

    @Override
    public boolean childrenShouldBeFiltered(N parentNode, Filter<N> filter) {
        if (isFilterableDelegate() && parentNode instanceof FilterableTreeTableNode) {
            FilterableTreeTableNode<N> filterableTreeTableNode = (FilterableTreeTableNode<N>) parentNode;
            return filterableTreeTableNode.childrenShouldBeFiltered(filter);
        }
        return false;
    }

    @Override
    public boolean isNodeFiltered(N node, Filter<N> filter) {
        if (isFilterableDelegate() && node instanceof FilterableTreeTableNode) {
            FilterableTreeTableNode<N> filterableTreeTableNode = (FilterableTreeTableNode<N>) node;
            return filterableTreeTableNode.isNodeFiltered(filter);
        }
        return false;
    }

    @Override
    public boolean isNodePruned(N parentNode, Filter<N> filter) {
        if (isFilterableDelegate() && parentNode instanceof FilterableTreeTableNode) {
            FilterableTreeTableNode<N> filterableTreeTableNode = (FilterableTreeTableNode<N>) parentNode;
            return filterableTreeTableNode.isNodePruned(filter);
        }
        return false;
    }

    @Override
    public void willFilterChildren(N parentNode, Filter<N> filter) {
        if (isFilterableDelegate() && parentNode instanceof FilterableTreeTableNode) {
            FilterableTreeTableNode<N> filterableTreeTableNode = (FilterableTreeTableNode<N>) parentNode;
            filterableTreeTableNode.willFilterChildren(filter);
        }
    }


//    @Override
//    public boolean childrenShouldBeFiltered(N parentNode, Filter<N> filter) {
//        if (isFilterableDelegate() && parentNode instanceof FilterableTreeTableNode) {
//            FilterableTreeTableNode<N> filterableTreeTableNode = (FilterableTreeTableNode<N>) parentNode;
//            return filterableTreeTableNode.childrenShouldBeFiltered(filter);
//        }
//        return true;
//    }
//
//    @Override
//    public boolean isNodeFiltered(N node, Filter<N> filter, boolean hasUnfilteredChildren) {
//        if (isFilterableDelegate() && node instanceof FilterableTreeTableNode) {
//            FilterableTreeTableNode<N> filterableTreeTableNode = (FilterableTreeTableNode<N>) node;
//            return filterableTreeTableNode.isNodeFiltered(filter, hasUnfilteredChildren);
//        }
//        return false;
//    }

    @Override
    public boolean isFilterableDelegate() {
        return filterable;
    }

    public void setFilterableDelegate(boolean filterable) {
        if (this.filterable != filterable) {

            this.filterable = filterable;

            firePropertyChange(FILTERABLE_DELEGATE_PROPERTY, !filterable, filterable);

        }
        this.filterable = filterable;
    }

    @Override
    public Filter<N> setupFilter(Object filterObject) {
        return null;
    }



//    /**
//     *
//     * @return
//     * @deprecated
//     */
//    @Override
//    public final int getSortColumnIndex() {
//        return sortColumnIndex;
//    }
//    /**
//     *
//     * @return
//     * @deprecated
//     */
//    @Override
//    public final boolean isSortAscending() {
//        return sortAscending;
//    }
//    /**
//     *
//     * @param columnIndex
//     * @param ascending
//     * @deprecated
//     */
//    @Override
//    public final void setSortOrder(int columnIndex, boolean ascending) {
//        if (this.sortColumnIndex != columnIndex || this.sortAscending != ascending) {
//            sortColumnIndex = columnIndex;
//            sortAscending = ascending;
//
//            // We really don't want to be doing this!
//            // This is really just legacy support for sorting.
//            if (root instanceof DefaultTreeTableNode) {
//                ((DefaultTreeTableNode) root).setSortColumn(columnIndex, ascending);
//            }
//        }
//    }
    @Override
    public Color getColumnColor(Object node, int column) {
        if (node instanceof ColumnColorProvider) {
            return ((ColumnColorProvider) node).getColumnColor(column);
        }
        return null;
    }

    @Override
    public String getToolTipText(Object node, int column, MouseEvent evt) {
        if (node instanceof ColumnTooltipProvider) {
            return ((ColumnTooltipProvider) node).getToolTipText(column);
        }
        else {
            return node != null ? node.toString() : null;
        }
    }

    /** Set the Filter object.
     *
     * DefaultTreeTables that support filtering will call this method
     * whenever the filter object is changed.  This generally occurs when the
     * user types while the table has focus.
     *
     * By default, the filter object is a String object.  However, subclasses of
     * DefaultTreeTable can provide a more advanced filter GUI which might
     * return other types.
     *
     * TODO: ProxyTreeTable - Delete me
     *
     * @param filterObject The recently set filter object.  Null if the filter
     * should be cleared.
     * 
     */
    public final void setFilterObject(TreeNode filterObject) {
    }

    /** Returns whether this model support Legacy DnD.
     *
     * Currently, this defaults to true.  Eventually, it should probably
     * default to false.
     *
     * @return Whether this model support Legacy DnD.
     */
    @Override
    public boolean isLegacyDnDSupported() {
        return legacyDnDSupported;
    }

    /** Sets whether legacy DnD is supported.
     *
     * If you nodes support legacy DnD (ie are DefaultTreeTableNode with the legacy
     * DnD method implemented), set legacy DnD to true.  Note, the tree also has to
     * have legacy DnD enabled.
     * <P>
     * While it is possible to also support new style DnD (@see setDnDSupported),
     * typically one or the other is chosen since any single Tree can only use one
     * method at a time.
     *
     * @param legacyDnDSupported
     */
    public void setLegacyDnDSupport(boolean legacyDnDSupported) {
        this.legacyDnDSupported = legacyDnDSupported;
    }

    @Override
    @Deprecated
    public boolean startDrag(TreeTable treeTable, TreePath dragPath, DragSourceListener listener, DragGestureEvent dge) {
        Object last = dragPath.getLastPathComponent();
        if (last instanceof DefaultTreeTableNode) {
            return ((DefaultTreeTableNode) last).startDrag(treeTable, dragPath, listener, dge);
        }
        return false;
    }

    @Override
    @Deprecated
    public void endDrag(TreeTable treeTable, DragSourceDropEvent dsde) {
    }

    @Override
    @Deprecated
    public boolean expandDuringDrag(TreeTable treeTable, TreePath dropPath, DropTargetDragEvent event) {
        Object last = dropPath.getLastPathComponent();
        if (last instanceof DefaultTreeTableNode) {
            return ((DefaultTreeTableNode) last).expandDuringDrag(treeTable, dropPath, event);
        }
        return false;
    }

    @Override
    @Deprecated
    public boolean handleDrop(TreeTable treeTable, TreePath dropPath, DropTargetDropEvent event) {

        boolean rv = false;
        N node;

        int index;
        for (index = dropPath.getPathCount() - 1; index >= 0; index--) {
            node = (N) dropPath.getPathComponent(index);
            if (node instanceof DefaultTreeTableNode) {
                //System.out.println("dragOver: Check will handle for " + node );
                rv = ((DefaultTreeTableNode) node).handleDrop(treeTable, dropPath, event);
                if (rv == true) {
                    break;
                }
            }
        }

        return rv;
    }

    @Override
    @Deprecated
    public TreePath willHandleDrop(TreeTable treeTable, TreePath path, DropTargetDragEvent event) {

        for (int index = path.getPathCount() - 1; index >= 0; index--) {
            Object node = path.getPathComponent(index);
            if (node instanceof DefaultTreeTableNode) {
                TreePath newDropLocation = ((DefaultTreeTableNode) node).willHandleDrop(treeTable, path, event);
                if (newDropLocation != null) {
                    treeTable.setSelectionPath(new TreePath(((DefaultTreeTableNode) node).getPath()));
                    return newDropLocation;
                }
            }
        }

        return null;
    }

    @Override
    public boolean invokeMenu(TreeTable treeTable, TreePath[] paths, JPopupMenu popup) {
        boolean rv = false;

        int popupStart = popup.getComponentCount();

        int maxDepth = 0;
        if (paths != null) {
            for (TreePath path : paths) {
                if (path.getPathCount() > maxDepth) {
                    maxDepth = path.getPathCount();
                }
            }

            for (int depth = maxDepth - 1; depth >= 0; depth--) {
                for (TreePath path : paths) {
                    if (path.getPathCount() > depth) {

                        Object node = path.getPathComponent(depth);

                        if (node instanceof DefaultTreeTableNode) {
                            if (((DefaultTreeTableNode) node).invokeMenu(treeTable, path, popup)) {
                                rv = true;
                            }
                        }
                    }
                }
            }
        }

        if (rv == true) {

            for (int pindex = popupStart; pindex < popup.getComponentCount(); pindex++) {

                Component c1 = popup.getComponent(pindex);

                if (c1 instanceof JMenuItem && ((JMenuItem) c1).getAction() instanceof Combinable) {
                    Combinable a1 = (Combinable) ((JMenuItem) c1).getAction();
                    Combinable originalA1 = a1;

                    for (int pindex2 = pindex + 1; pindex2 < popup.getComponentCount(); pindex2++) {
                        Component c2 = popup.getComponent(pindex2);

                        if (c2 instanceof JMenuItem && ((JMenuItem) c2).getAction() instanceof Combinable) {
                            Combinable a2 = (Combinable) ((JMenuItem) c2).getAction();

                            try {
                                Combinable newA = a1.combine(a2);

                                if (newA != null && newA instanceof Action) {
                                    a1 = newA;
                                    popup.remove(c2);
                                    pindex2--;
                                }
                            } catch (CombinablesIncompatibleException ci) {
                                // We can't have these two things together...remove them both.
                                a1 = null;
                                popup.remove(c1);
                                popup.remove(c2);
                                pindex--;
                                break;
                            }
                        }
                    }

                    if (a1 != null && a1 != originalA1) {
                        // We changed it, so remove the old one and put the new one in
                        popup.remove(pindex);
                        popup.add(new JMenuItem((Action) originalA1), pindex);
                    }
                }
            }


            for (int pindex = popupStart; pindex < popup.getComponentCount(); pindex++) {

                Component c1 = popup.getComponent(pindex);

                if (c1 instanceof Combinable) {
                    for (int pindex2 = pindex + 1; pindex2 < popup.getComponentCount(); pindex2++) {
                        Component c2 = popup.getComponent(pindex2);

                        if (c2 instanceof Combinable) {
                            try {
                                Combinable newC = ((Combinable) c1).combine((Combinable) c2);

                                if (newC != null) {
                                    c1 = (Component) newC;
                                    popup.remove(c2);
                                    pindex2--;
                                }
                            } catch (CombinablesIncompatibleException ci) {
                                c1 = null;
                                popup.remove(c1);
                                popup.remove(c2);
                                pindex--;
                                break;
                            }
                        }
                    }

                    if (c1 != null && popup.getComponent(pindex) != c1) {
                        // We changed it, so remove the old one and put the new one in
                        popup.remove(pindex);
                        popup.add(c1, pindex);
                    }
                }
            }

            boolean lastWasSep = false;
            for (int pindex = popupStart; pindex < popup.getComponentCount(); pindex++) {
                Component c = popup.getComponent(pindex);

                if (c instanceof JSeparator) {
                    if (lastWasSep == true) {
                        popup.remove(pindex);
                        pindex--;
                    }

                    lastWasSep = true;
                }
                else {
                    lastWasSep = false;
                }
            }
        }

        return rv;
    }

    /** Returns whether this model support new-style DnD.
     *
     * Currently, this defaults to false.
     *
     * @return Whether this model support new-style DnD.
     */
    @Override
    public boolean isDnDSupported() {
        return newDnDSupported;
    }

    /** Sets whether new-style DnD is supported.
     *
     * If you nodes support new-style DnD (ie are DefaultTreeTableNode with the
     * DnD method implemented), set DnD to true.  Note, the tree also has to
     * have new-style DnD enabled.
     * <P>
     * While it is possible to also support legacy DnD (@see setLegacyDnDSupported),
     * typically one or the other is chosen since any single Tree can only use one
     * method at a time.
     *
     * @param newDnDSupported
     */
    public void setDnDSupported(boolean newDnDSupported) {
        this.newDnDSupported = newDnDSupported;
    }

    @Override
    public boolean canImport(TreeTable destinationTree, TreeTableTransferSupport support) {

        boolean isDrop = support.isDrop();
        TreePath path = null;

        if (support.isDrop()) {
            TreeTableDropLocation dropLocation = support.getDropLocation();

            path = dropLocation.getPath();
        }
        else {
            // It is a paste...
            TreePath[] paths = destinationTree.getSelectionPaths();
            if (paths != null && paths.length == 1) {
                path = paths[0];
            }
        }

        boolean canImport = false;

        if (path != null) {
            for (int index = path.getPathCount() - 1; index >= 0; index--) {
                N node = (N) path.getPathComponent(index);
                if (node instanceof TreeTableDnDNode) {
                    canImport = ((TreeTableDnDNode) node).canImport(destinationTree, support);

                    if (canImport) {
                        setLastCanImportNode(node);
                        if (isDrop) {
                            support.setShowDropLocation(true);
                        }
                        break;
                    }
                }
            }
        }

        return canImport;
    }

    @Override
    public boolean importData(TreeTable destinationTree, TreeTableTransferSupport support) {

        if (canImport(destinationTree, support) == false) {
            return false;
        }

        boolean rv = true;

        N importNode = getLastCanImportNode();
        if (importNode instanceof TreeTableDnDNode) {
            TreeTableDnDNode treeTableDnDHandler = (TreeTableDnDNode) importNode;
            rv = treeTableDnDHandler.importData(destinationTree, support);
        }

        setLastCanImportNode(null);

        return rv;
    }

    @Override
    public Transferable createTransferable(TreeTable sourceTree, TreePath[] transferredPaths) {

        if (transferredPaths != null) {
            transferredPaths = sourceTree.getDisplayOrderPaths(transferredPaths);

            for (int i = 0; i < transferredPaths.length; i++) {
                TreePath treePath = transferredPaths[i];

                Object o = treePath.getLastPathComponent();
                if (o instanceof TreeTableDnDNode) {
                    TreeTableDnDNode defaultTreeTableNode = (TreeTableDnDNode) o;
                    Transferable t = defaultTreeTableNode.createTransferable(sourceTree, treePath, transferredPaths);
                    if (t != null) {
                        return t;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public int getDragSourceActions(TreeTable sourceTree, TreePath[] transferredPaths) {
        if (transferredPaths != null) {
            transferredPaths = sourceTree.getDisplayOrderPaths(transferredPaths);

            for (int i = 0; i < transferredPaths.length; i++) {
                TreePath treePath = transferredPaths[i];

                Object o = treePath.getLastPathComponent();
                if (o instanceof TreeTableDnDNode) {
                    TreeTableDnDNode defaultTreeTableNode = (TreeTableDnDNode) o;
                    int actions = defaultTreeTableNode.getDragSourceActions(sourceTree, treePath, transferredPaths);
                    if (actions != DnDConstants.ACTION_NONE) {
                        return actions;
                    }
                }
            }
        }

        return DnDConstants.ACTION_NONE;
    }

    @Override
    public void exportDone(TreeTable sourceTree, TreePath[] transferredPaths, Transferable data, int action) {
        if (transferredPaths != null) {
            transferredPaths = sourceTree.getDisplayOrderPaths(transferredPaths);

            TreePath lastHandledPath = null;

            for (int i = 0; i < transferredPaths.length; i++) {
                TreePath path = transferredPaths[i];

                if (lastHandledPath == null || lastHandledPath.isDescendant(path) == false) {
                    for (int index = path.getPathCount() - 1; index >= 0; index--) {
                        Object node = path.getPathComponent(index);
                        if (node instanceof TreeTableDnDNode) {
                            TreeTableDnDNode defaultTreeTableNode = (TreeTableDnDNode) node;
                            boolean handled = defaultTreeTableNode.exportDone(sourceTree, path, transferredPaths, data, action);
                            if (handled == true) {
                                lastHandledPath = path;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /** Stores the last node that accepted to import data (as determined via canImport).
     *
     * This is for new dnd support.
     * 
     * @return the lastCanImportNode
     */
    protected N getLastCanImportNode() {
        return lastCanImportNodeReference == null ? null : lastCanImportNodeReference.get();
    }

    /**
     * @param lastCanImportNode the lastCanImportNode to set
     */
    protected void setLastCanImportNode(N lastCanImportNode) {
        if ((this.lastCanImportNodeReference != null || lastCanImportNode != null) && (this.lastCanImportNodeReference == null || this.lastCanImportNodeReference.get() != lastCanImportNode)) {
            if (lastCanImportNode == null) {
                this.lastCanImportNodeReference = null;
            }
            else {
                this.lastCanImportNodeReference = new SoftReference<N>(lastCanImportNode);
            }
        }
    }
}
