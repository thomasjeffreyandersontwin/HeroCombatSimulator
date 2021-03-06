/*
 * TreeTableModelAdapter.java
 *
 * Created on February 17, 2002, 4:22 PM
 */

package treeTable;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author  twalker
 * @version 
 */
public class TreeTableModelAdapter 
implements TreeModel, TreeModelListener {  
    /** Listeners. */
    protected EventListenerList listenerList = new EventListenerList();
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
    
    /** Holds value of property treeTableModel. */
    private TreeTableModel treeTableModel;
    
    /** Holds value of property treeTable. */
    private TreeTable treeTable;
    
    public TreeTableModelAdapter(TreeTable treeTable) {
        setTreeTable(treeTable);
    }
    
    public TreeTableModelAdapter(TreeTable treeTable, TreeTableModel treeTableModel) {
        this(treeTable);
        setTreeTableModel(treeTableModel);
    }

    /**
     * Returns the root of the tree.  Returns null only if the tree has
     * no nodes.
     *
     * @return  the root of the tree
     */
    public Object getRoot() {
        if ( treeTableModel != null ) return treeTableModel.getRoot();
        return null;
    }
    
    /**
     * Returns the index of child in parent.
     */
    public int getIndexOfChild(Object parent, Object child) {
        if ( treeTableModel != null ) return treeTableModel.getIndexOfChild(parent, child);
        return -1;
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
    public boolean isLeaf(Object node) {
    	try{
    		if ( treeTableModel != null ) return treeTableModel.isLeaf(node);
    	}
    	catch(Exception e){}
        return true;
    }
    
    /**
     * Returns the child of <I>parent</I> at index <I>index</I> in the parent's
     * child array.  <I>parent</I> must be a node previously obtained from
     * this data source. This should not return null if <i>index</i>
     * is a valid index for <i>parent</i> (that is <i>index</i> >= 0 &&
     * <i>index</i> < getChildCount(<i>parent</i>)).
     *
     * @param   parent  a node in the tree, obtained from this data source
     * @return  the child of <I>parent</I> at index <I>index</I>
     */
    public Object getChild(Object parent, int index) {
        if ( treeTableModel != null ) return treeTableModel.getChild(parent, index);
        return null;
    }
    
    /**
     * Messaged when the user has altered the value for the item identified
     * by <I>path</I> to <I>newValue</I>.  If <I>newValue</I> signifies
     * a truly new value the model should post a treeNodesChanged
     * event.
     *
     * @param path path to the node that the user has altered.
     * @param newValue the new value from the TreeCellEditor.
     */
    public void valueForPathChanged(TreePath path, Object newValue) { 
        
        // Don't do anything for now...
        treeTableModel.setValueAt(path.getLastPathComponent(), treeTable.getEditingColumn(), newValue);
        
    }
    
    /**
     * Returns the number of children of <I>parent</I>.  Returns 0 if the node
     * is a leaf or if it has no children.  <I>parent</I> must be a node
     * previously obtained from this data source.
     *
     * @param   parent  a node in the tree, obtained from this data source
     * @return  the number of children of the node <I>parent</I>
     */
    public int getChildCount(Object parent) {
        if ( treeTableModel != null ) return treeTableModel.getChildCount(parent);
        return 0;
    }
    
    /**
     * Adds a listener for the TreeModelEvent posted after the tree changes.
     *
     * @see     #removeTreeModelListener
     * @param   l       the listener to add
     */
    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }
    
    /**
     * Removes a listener previously added with <B>addTreeModelListener()</B>.
     *
     * @see     #addTreeModelListener
     * @param   l       the listener to remove
     */
    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }
    
    /** Getter for property treeTableModel.
     * @return Value of property treeTableModel.
     */
    public TreeTableModel getTreeTableModel() {
        return treeTableModel;
    }
    
    /** Setter for property treeTableModel.
     * @param treeTableModel New value of property treeTableModel.
     */
    public void setTreeTableModel(TreeTableModel treeTableModel) {
        if ( this.treeTableModel != treeTableModel ) {
            if ( this.treeTableModel != null ) {
                this.treeTableModel.removeTreeModelListener(this);
            }
            
            this.treeTableModel = treeTableModel;
            
            if ( this.treeTableModel != null ) {
                this.treeTableModel.addTreeModelListener(this);
            }
        }
    }
    
    /**
     * <p>Invoked after the tree has drastically changed structure from a
     * given node down.  If the path returned by e.getPath() is of length
     * one and the first element does not identify the current root node
     * the first element should become the new root of the tree.<p>
     *
     * <p>Use <code>e.getPath()</code>
     * to get the path to the node.
     * <code>e.getChildIndices()</code>
     * returns null.</p>
     */
    public void treeStructureChanged(TreeModelEvent e) {
        if ( treeTable.isEditing() ) {
            if ( treeTable.stopEditing() == false ) treeTable.cancelEditing();
        }
        fireTreeStructureChanged(e);
    }
    
    /**
     * <p>Invoked after nodes have been inserted into the tree.</p>
     *
     * <p>Use <code>e.getPath()</code>
     * to get the parent of the new node(s).
     * <code>e.getChildIndices()</code>
     * returns the index(es) of the new node(s)
     * in ascending order.</p>
     */
    public void treeNodesInserted(TreeModelEvent e) {
        fireTreeNodesInserted(e);
    }
    
    /**
     * <p>Invoked after nodes have been removed from the tree.  Note that
     * if a subtree is removed from the tree, this method may only be
     * invoked once for the root of the removed subtree, not once for
     * each individual set of siblings removed.</p>
     *
     * <p>Use <code>e.getPath()</code>
     * to get the former parent of the deleted node(s).
     * <code>e.getChildIndices()</code>
     * returns, in ascending order, the index(es)
     * the node(s) had before being deleted.</p>
     */
    public void treeNodesRemoved(TreeModelEvent e) {
        fireTreeNodesRemoved(e);
    }
    
    /**
     * <p>Invoked after a node (or a set of siblings) has changed in some
     * way. The node(s) have not changed locations in the tree or
     * altered their children arrays, but other attributes have
     * changed and may affect presentation. Example: the name of a
     * file has changed, but it is in the same location in the file
     * system.</p>
     * <p>To indicate the root has changed, childIndices and children
     * will be null. </p>
     *
     * <p>Use <code>e.getPath()</code>
     * to get the parent of the changed node(s).
     * <code>e.getChildIndices()</code>
     * returns the index(es) of the changed node(s).</p>
     */
    public void treeNodesChanged(TreeModelEvent e) {
        fireTreeNodesChanged(e);
    }
    
    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    protected void fireTreeNodesChanged(TreeModelEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                ((TreeModelListener)listeners[i+1]).treeNodesChanged(e);
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
    protected void fireTreeNodesInserted(TreeModelEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                ((TreeModelListener)listeners[i+1]).treeNodesInserted(e);
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
    protected void fireTreeNodesRemoved(TreeModelEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                ((TreeModelListener)listeners[i+1]).treeNodesRemoved(e);
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
    protected void fireTreeStructureChanged(TreeModelEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                ((TreeModelListener)listeners[i+1]).treeStructureChanged(e);
            }          
        }
    }
    
    /** Getter for property treeTable.
     * @return Value of property treeTable.
     */
    public TreeTable getTreeTable() {
        return treeTable;
    }
    
    /** Setter for property treeTable.
     * @param treeTable New value of property treeTable.
     */
    public void setTreeTable(TreeTable treeTable) {
        this.treeTable = treeTable;
    }
    
}
