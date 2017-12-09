/*
 * AttackTreeModel.java
 *
 * Created on October 28, 2001, 12:26 AM
 */

package champions.attackTree;

import champions.BattleEvent;
import champions.InlineView;
import champions.exception.BattleEventException;
import java.util.Enumeration;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;


/**
 *
 * @author  twalker
 * @version
 *
 * The AttackTreeModel is used to maintain and access an a Tree of AttackTreeNodes.
 * Actually, the model maintains two seperate logical trees.  One is the visible
 * tree, which is what is actually shown in the JTree when this model is used.
 * The second logical tree is the real tree.  The real tree can be composed
 * of both visible and hidden nodes.  Hidden nodes are treated in a special way.
 * The Children of a hidden node in the real tree are considered direct children
 * of the invisible nodes parent.
 *
 * For Example:
 *
 * REAL TREE
 *   Root
 *     Node A
 *     (Node B) - Hidden
 *       Node C
 *       Node D
 *     Node E
 *
 * Corresponds to the Visible tree of:
 *
 *   Root
 *     Node A
 *     Node C - From hidden node B
 *     Node D - From hidden node B
 *     Node E
 *
 * All of the TreeModel interface methods return information about the visible
 * tree.
 *
 */

public class RealAttackTreeModel extends Object implements TreeModel {
    
    static protected final int DEBUG = 0;
    /**
     * Stores the current active node.
     *
     * Once processing is complete, this should always be the
     * node which is currently selected in the Tree.
     */
    protected AttackTreeNode activeNode;
    
    /**
     * Stores the root node.
     *
     * The root must be a visible node.  However, the root node
     * is not actually displayed in the AttackTree.
     */
    protected AttackTreeNode root;
    
   
    /** Listeners. */
    protected EventListenerList listenerList = new EventListenerList();
        
    /** Creates new AttackTreeModel */
    public RealAttackTreeModel() {
    }
    
    /** Creates new AttackTreeModel */
    public RealAttackTreeModel(AttackTreeNode root) {
        setRoot(root);
    }
    
    /**
     * Returns the root of the tree.  Returns null only if the tree has
     * no nodes.
     *
     * @return  the root of the tree
     */
    public Object getRoot() {
        return root;
    }
    
    /**
     * Sets the current root.  This should only be done with the model is
     * first constructed.
     */
    public void setRoot(AttackTreeNode root) {
        this.root = root;
    }
    
    /**
     * Returns the index of child in parent.
     *
     * This refers to the Visible Tree, not the Real Tree.
     */
    public int getIndexOfChild(Object parent, Object child) {
        return ((AttackTreeNode)parent).getRealIndex((AttackTreeNode)child);
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
        return ( node == null ) ? false : ((AttackTreeNode)node).isLeaf();
    }
    
    /**
     * Returns the child of <I>parent</I> at index <I>index</I> in the parent's
     * child array.  <I>parent</I> must be a node previously obtained from
     * this data source. This should not return null if <i>index</i>
     * is a valid index for <i>parent</i> (that is <i>index</i> >= 0 &&
     * <i>index</i> < getChildCount(<i>parent</i>)).
     *
     * This refers to the Visible Tree, not the Real Tree.
     *
     * @param   parent  a node in the tree, obtained from this data source
     * @return  the child of <I>parent</I> at index <I>index</I>
     */
    public Object getChild(Object parent, int index) {
        return ((AttackTreeNode)parent).getRealChildAt(index);
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
        // Since this tree is not directly editable, this method should never be used.
    }
    
    /**
     * Returns the number of children of <I>parent</I>.  Returns 0 if the node
     * is a leaf or if it has no children.  <I>parent</I> must be a node
     * previously obtained from this data source.
     *
     * This refers to the Visible Tree, not the Real Tree.
     *
     * @param   parent  a node in the tree, obtained from this data source
     * @return  the number of children of the node <I>parent</I>
     */
    public int getChildCount(Object parent) {
         return ((AttackTreeNode)parent).getRealChildCount();
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
        listenerList.remove(TreeModelListener.class, l);
    }
    
    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    protected void fireTreeNodesChanged(Object source, Object[] path,
    int[] childIndices,
    Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path,
                    childIndices, children);
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
    protected void fireTreeNodesInserted(Object source, Object[] path,
    int[] childIndices,
    Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
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
    protected void fireTreeNodesRemoved(Object source, Object[] path,
    int[] childIndices,
    Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path,
                    childIndices, children);
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
    protected void fireTreeStructureChanged(Object source, Object[] path,
    int[] childIndices,
    Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path,
                    childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeStructureChanged(e);
            }
        }
    }
}
