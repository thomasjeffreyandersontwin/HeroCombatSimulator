/*
 * AttackTreeModel.java
 *
 * Created on October 28, 2001, 12:26 AM
 */

package champions.attackTree;

import champions.Ability;
import champions.BattleEvent;
import champions.Target;
import champions.exception.BattleEventException;
import java.util.Enumeration;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import VirtualDesktop.Mob.MobEffect;




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

public class AttackTreeModel extends Object
implements TreeModel {
    
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
    
    /** Stores the BattleEvent associated with this attackTreeModel. */
    public BattleEvent battleEvent;
    
    /** Stores the Tree associated with this attackTreeModel. */
    private JTree tree;
    
    /** Stores the Tree associated with this attackTreeSelectionModel. */
    private AttackTreeSelectionModel selectionModel;
    
    /** Stores the model's AttackTreeInlinePanel. */
    protected AttackTreePanel attackTreePanel;
    
    /** Listeners. */
    protected EventListenerList listenerList = new EventListenerList();
    
    /** Holds value of property showHidden. */
    private boolean showHidden = false;
    
    /** Holds value of property finished. */
    private boolean finished;
    
    /** Holds the Locking Object */
    private Object lockObject = new Object();
    
    /** Holds the Lock status */
    private boolean locked = false;
    
    /** Holds the Exception which was thrown, if any. */
    private BattleEventException error = null;
    
    /** Creates new AttackTreeModel */
    public AttackTreeModel() {
    	treeModel= this;
    }
    
    /** Creates new AttackTreeModel */
    public AttackTreeModel(AttackTreeNode root) {
        setRoot(root);
        treeModel = this;
        
    }
    public static AttackTreeModel treeModel;
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
        if ( root != null ) {
            root.setModel(this);
            root.setAttackTreePanel(attackTreePanel);
            //root.setBattleEvent(getBattleEvent());
            //root.setAttackTreePanel(getInlinePanel());
        }
    }
    
    /**
     * Returns the index of child in parent.
     *
     * This refers to the Visible Tree, not the Real Tree.
     */
    public int getIndexOfChild(Object parent, Object child) {
        if ( showHidden == false ) {
            return ( parent == null ) ? -1 : ((AttackTreeNode)parent).getIndex((AttackTreeNode)child);
        }
        else {
            return ( parent == null ) ? -1 : ((AttackTreeNode)parent).getRealIndex((AttackTreeNode)child);
        }
    }
    
    /**
     * Returns the index of child in parent.
     *
     * This refers to the Real Tree, not the Visible Tree.
     */
    public int getRealIndexOfChild(Object parent, Object child) {
        return ( parent == null ) ? -1 : ((AttackTreeNode)parent).getRealIndex((AttackTreeNode)child);
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
        if ( showHidden == false ) {
            return ( parent == null ) ? null : ((AttackTreeNode)parent).getChildAt(index);
        }
        else {
            return ( parent == null ) ? null : ((AttackTreeNode)parent).getRealChildAt(index);
        }
    }
    
    /**
     * Returns the child of <I>parent</I> at index <I>index</I> in the parent's
     * child array.  <I>parent</I> must be a node previously obtained from
     * this data source. This should not return null if <i>index</i>
     * is a valid index for <i>parent</i> (that is <i>index</i> >= 0 &&
     * <i>index</i> < getChildCount(<i>parent</i>)).
     *
     * This refers to the Real Tree, not the Visible Tree.
     *
     * @param   parent  a node in the tree, obtained from this data source
     * @return  the child of <I>parent</I> at index <I>index</I>
     */
    public Object getRealChild(Object parent, int index) {
        return ( parent == null ) ? null : ((AttackTreeNode)parent).getRealChildAt(index);
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
        if ( showHidden == false ) {
            return ( parent == null ) ? 0 : ((AttackTreeNode)parent).getChildCount();
        }
        else {
            return ( parent == null ) ? 0 : ((AttackTreeNode)parent).getRealChildCount();
        }
    }
    
    /**
     * Returns the number of children of <I>parent</I>.  Returns 0 if the node
     * is a leaf or if it has no children.  <I>parent</I> must be a node
     * previously obtained from this data source.
     *
     * This refers to the Real Tree, not the Visible Tree.
     *
     * @param   parent  a node in the tree, obtained from this data source
     * @return  the number of children of the node <I>parent</I>
     */
    public int getRealChildCount(Object parent) {
        return ( parent == null ) ? 0 : ((AttackTreeNode)parent).getRealChildCount();
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
    
    /** Advance through the tree and activate the first node that desires activation.
     * 
     *  If <code>node</code> is non-null, that node will be given the opertunity to
     *  be activated.  If <code>manualOverrideNode</code> is non-null, the tree will
     *  attempt to stop on that node when it is encountered.
     */
    public void advanceAndActivate(AttackTreeNode node, AttackTreeNode manualOverrideNode) {
        
        boolean accepted = false;
        
        if ( node != null ) {
            setFinished(false);
            accepted = activateNode(node, manualOverrideNode);
        }
        
        if ( attackTreePanel != null ) attackTreePanel.setProcessing(true);
        
        while(!accepted) {
        	int x=0;
            node = advanceNode();
            if ( node != null ) {
                setFinished(false);
                accepted = activateNode(node, manualOverrideNode);
            }
            else {
                setFinished(true);
                accepted = true;  
            }
        }
        
        if ( attackTreePanel != null ) attackTreePanel.setProcessing(false);
    }
    
    /** Advance the current node and return the next node in the tree.
     *
     */
    public AttackTreeNode advanceNode() {
        AttackTreeNode atn = getActiveNode();
        
        try {
            
            // Check to see if there is a activeNode set.  If not, just use the root and advance from there.
            if ( atn == null ) {
                atn = (AttackTreeNode)getRoot();
            }
            
            // If there is no activeNode or root node, we are screwed.  Return.
            if ( atn == null ) return null;
            
            AttackTreeNode newNode, activeChild;
            if(atn.getBattleEvent()==null)
            {
            	atn.setBattleEvent(battleEvent);
            }
            newNode = atn.advanceNode(atn);
            
            // Look for a non-null next node.  While next node is null, find the
            // parent of the current node, then see if the parent has a desired
            // next node.
            
            while ( newNode == null ) {
                activeChild = atn;
                atn = (AttackTreeNode)atn.getRealParent();
                if ( atn != null ) {
                    newNode = atn.advanceNode(activeChild);
                    int i=0;
                }
                else {
                    break;
                }
            }
            
            return newNode;    
        }
        catch ( BattleEventException bee ) {
            setError(bee);
            return null;
        }
    }
    
    /**
     * Set the ActiveNode.
     *
     * ActivateNode attempts to set the active node.  Prior to activation, events
     * will be rolled backwards and undo mark will be recorded, if necessary, and
     * the ActiveNode variable will be set.
     *
     * Then node.ActivateNode will be called.  If node.ActivateNode returns false,
     * advancePosition will be called to recursively advance to a node which desires
     * to be active.
     *
     * The manual override node is used to indicate that a user click on a node and
     * that the node should be selected.  If the node about to be activated is the
     * manual override node, the node.activateNode method will be called with
     * override parameter set to true.
     *
     */
    public boolean activateNode(AttackTreeNode node, AttackTreeNode manualOverrideNode) {
        // Bail if the node and root is null.  This assumes there is no time at which a node should be
        // set to null.
        
        try {
            
        if ( node == null ) node = (AttackTreeNode)getRoot();
        if ( node == null ) return false;
        
        AttackTreeNode activeNode = getActiveNode();
        if ( activeNode != null ) {
            BattleEvent be = node.getBattleEvent();

            // First check to make sure the BattleEvent is rolled back to the correct
            // Position.  If it isn't, partial roll it back to the correct location.  If
            // it already is rolled back properly, set the UndoIndex of node.
            if ( activeNode == node || getRealPreorderPosition(activeNode) > getRealPreorderPosition(node) ) {
                // The active Node is further down the tree, so roll and activate the new node
                if ( be != null ) {
                    if ( be.isEmbedded() ) {
                        BattleEvent parent = be.getParentEvent();
                        parent.partialRollbackBattleEvent( parent.getEmbeddedUndoIndex(be) + 1 );
                    }
                    
                    be.partialRollbackBattleEvent( node.getUndoIndex() );
                   // System.out.println("AttackTreeModel::activateNode: BattleEvent after rollback: \n" + be.toLongString() + "\n");
                    
                }
            }
            else {
                // The active node is prior to this node, so advance the active node
                if ( be != null ) node.setUndoIndex( be.getUndoableEventCount() );
            }
        }
        
        // Set the active node to the new node.
        setActiveNode( node );
        
        // Inform the node it is now active.
            boolean accept = node.activateNode( node == manualOverrideNode );
            
            
            // If the node did not want to be the active node, advance to the next one.  If it did
            // accept activeNode status, update the tree selection to reflex the active node.
            // Make sure that hidden nodes never get the selection.
//            if ( node.isVisible() == true && accept == false ) {
//                TreePath tp = new TreePath(getPathToRoot(node));
//                if ( tree != null && tree.isVisible(tp) == false ) tree.makeVisible(tp);
//                if ( tree != null ) tree.scrollPathToVisible(tp);
//            }

            if( accept == false || node.isVisible() == false ) {
                // reject the activation...
                return false;
            }
            else {
                TreePath tp = new TreePath(getPathToRoot(node));
                if ( selectionModel != null ) selectionModel.setSelectionPath( tp , false);
                if ( DEBUG == 1 ) System.out.println("Set selection path to " + tp);
                if ( tree != null ) {
                    if ( tree.isVisible(tp) == false  ) {
                        tree.makeVisible(tp);
                    }
                    tree.scrollPathToVisible(tp);
                }
                
                // Accept the activation
                return true;
            }
        }
        catch ( BattleEventException bee ) {
            setError(bee);
            bee.printStackTrace();
            return false;
        }
        
    }
    
//    /**
//     * Causes the Active Node to be advanced.
//     *
//     * advanceNode recusively calls advanceNode, starting with
//     * the ActiveNode.  If a node's advancePosition return null, advanceNode
//     * will be called on the parent, continuing until a non-null node is obtained.
//     *
//     * Once a node is found, this.activateNode will be called.
//     *
//     * The manual override node is used to indicate that a user click on a node and
//     * that the node should be selected.  The manual override node will be pass
//     * on to the ActivateNode method.
//     */
//    public void advanceNode(AttackTreeNode manualOverrideNode) {
//        AttackTreeNode atn = getActiveNode();
//        
//        try {
//            
//            // Check to see if there is a activeNode set.  If not, just use the root and advance from there.
//            if ( atn == null ) {
//                atn = (AttackTreeNode)getRoot();
//            }
//            
//            // If there is no activeNode or root node, we are screwed.  Return.
//            if ( atn == null ) return;
//            
//            AttackTreeNode newNode, activeChild;
//            
//            newNode = atn.advanceNode(atn);
//            
//            // Look for a non-null next node.  While next node is null, find the
//            // parent of the current node, then see if the parent has a desired
//            // next node.
//            while ( newNode == null ) {
//                activeChild = atn;
//                atn = (AttackTreeNode)atn.getRealParent();
//                if ( atn != null ) {
//                    newNode = atn.advanceNode(activeChild);
//                }
//                else {
//                    break;
//                }
//            }
//            
//            // If newNode is not null, we have found a node to activate.  Otherwise,
//            // there are no more nodes in the tree which need to be processed.
//            if ( newNode != null ) {
//                // Activate the new node.
//                
//                activateNode(newNode, manualOverrideNode);
//                
//                
//                setFinished(false);
//            }
//            else {
//                // There are no more nodes in the tree to process.  This will probably
//                // enable the finish button or something like that...
//                setFinished(true);
//            }
//            
//        }
//        catch ( BattleEventException bee ) {
//            setError(bee);
//            return;
//        }
//    }
//    
//    /**
//     * Set the ActiveNode.
//     *
//     * ActivateNode attempts to set the active node.  Prior to activation, events
//     * will be rolled backwards and undo mark will be recorded, if necessary, and
//     * the ActiveNode variable will be set.
//     *
//     * Then node.ActivateNode will be called.  If node.ActivateNode returns false,
//     * advancePosition will be called to recursively advance to a node which desires
//     * to be active.
//     *
//     * The manual override node is used to indicate that a user click on a node and
//     * that the node should be selected.  If the node about to be activated is the
//     * manual override node, the node.activateNode method will be called with
//     * override parameter set to true.
//     *
//     */
//    public void activateNode(AttackTreeNode node, AttackTreeNode manualOverrideNode) {
//        // Bail if the node and root is null.  This assumes there is no time at which a node should be
//        // set to null.
//        if ( node == null ) node = (AttackTreeNode)getRoot();
//        if ( node == null ) return;
//        
//        AttackTreeNode activeNode = getActiveNode();
//        if ( activeNode != null ) {
//            BattleEvent be = node.getBattleEvent();
//
//            // First check to make sure the BattleEvent is rolled back to the correct
//            // Position.  If it isn't, partial roll it back to the correct location.  If
//            // it already is rolled back properly, set the UndoIndex of node.
//            if ( activeNode == node || getRealPreorderPosition(activeNode) > getRealPreorderPosition(node) ) {
//                // The active Node is further down the tree, so roll and activate the new node
//                if ( be != null ) {
//                    be.partialRollbackBattleEvent( node.getUndoIndex() );
//                   // System.out.println("AttackTreeModel::activateNode: BattleEvent after rollback: \n" + be.toLongString() + "\n");
//                    if ( be.isEmbedded() ) {
//                        BattleEvent parent = be.getParentEvent();
//                        parent.partialRollbackBattleEvent( parent.getEmbeddedUndoIndex(be) + 1 );
//                    }
//                }
//            }
//            else {
//                // The active node is prior to this node, so advance the active node
//                if ( be != null ) node.setUndoIndex( be.getUndoableEventCount() );
//            }
//        }
//        
//        // Set the active node to the new node.
//        setActiveNode( node );
//        
//        // Inform the node it is now active.
//        try {
//            boolean accept = node.activateNode( node == manualOverrideNode );
//            
//            
//            // If the node did not want to be the active node, advance to the next one.  If it did
//            // accept activeNode status, update the tree selection to reflex the active node.
//            // Make sure that hidden nodes never get the selection.
//            if ( node.isVisible() == true && accept == false ) {
//                TreePath tp = new TreePath(getPathToRoot(node));
//                if ( tree != null && tree.isVisible(tp) == false ) tree.makeVisible(tp);
//                if ( tree != null ) tree.scrollPathToVisible(tp);
//            }
//
//            if( accept == false || node.isVisible() == false ) {
//                advanceNode(manualOverrideNode);
//            }
//            else {
//                TreePath tp = new TreePath(getPathToRoot(node));
//                if ( selectionModel != null ) selectionModel.setSelectionPath( tp , false);
//                if ( DEBUG == 1 ) System.out.println("Set selection path to " + tp);
//                if ( tree != null ) tree.scrollPathToVisible(tp);
//            }
//        }
//        catch ( BattleEventException bee ) {
//            setError(bee);
//            return;
//        }
//        
//    }
    
    /**
     * Returns the Real PreOrder position of the node in the tree.
     *
     * This method traverses the tree and returns the preorder position,
     * starting with the root at position 0.
     *
     * If the node doesn't exist in the tree, returns -1.
     */
    public int getRealPreorderPosition(AttackTreeNode node) {
        int position = 0;
        
        if ( getRoot() == null ) return -1;
        
        Enumeration e = ((AttackTreeNode)getRoot()).getRealPreorderEnumeration();
        
        while ( e.hasMoreElements() ) {
            if ( e.nextElement() == node ) {
                return position;
            }
            position ++;
        }
        return -1;
    }
    
    /**
     * Return the current active Node.
     */
    public AttackTreeNode getActiveNode() {
        return activeNode;
    }
    
    
//    /**
//     * Sets teh InlinePanel this node belongs to.
//     */
//    public void setAttackTreePanel(AttackTreeInlinePanel inline) {
//        this.attackTreePanel = inline;
//        
//        if ( root != null ) root.setAttackTreePanel(inline);
//        
//    }
//    
//    /**
//     * Gets the InlinePanel this node belongs to.
//     */
//    public AttackTreeInlinePanel getInlinePanel() {
//        return attackTreePanel;
//    }
    
    /**
     * Sets the model's JTree.
     */
    public void setTree(JTree tree) {
        this.tree = tree;
        
        if ( tree.getSelectionModel() instanceof AttackTreeSelectionModel ) {
            setSelectionModel((AttackTreeSelectionModel)tree.getSelectionModel());
        }
    }
    
    /**
     * Gets the model's JTree.
     */
    public JTree getTree() {
        return tree;
    }
    /**
     * Sets the model's JTree.
     */
    public void setSelectionModel(AttackTreeSelectionModel selectionModel) {
        if ( this.selectionModel != null ) {
            // this.selectionModel.removeTreeWillSelectListener(this);
        }
        
        this.selectionModel = selectionModel;
        
        if ( this.selectionModel != null ) {
            //this.selectionModel.addTreeWillSelectListener(this);
        }
    }
    
    /**
     * Gets the model's JTree.
     */
    public AttackTreeSelectionModel getSelectionModel() {
        return selectionModel;
    }
    
    /**
     * Sets the current active Node.
     *
     * This method should only be called via the appropriate AttackTreeModel methods.
     */
    protected void setActiveNode(AttackTreeNode node) {
        activeNode = node;
        if ( tree != null && node.isVisible() == true ) {
            TreePath tp = new TreePath(getPathToRoot(node));
            if ( tree != null ) tree.makeVisible(tp);
        }
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
    
    /**
     * Invoke this method after you've changed how node is to be
     * represented in the tree.
     */
    public void nodeChanged(AttackTreeNode node) {
        if(listenerList != null && node != null) {
            TreeNode         parent = node.getParent();
            
            if(parent != null) {
                int        anIndex = parent.getIndex(node);
                if(anIndex != -1) {
                    int[]        cIndexs = new int[1];
                    
                    cIndexs[0] = anIndex;
                    nodesChanged((AttackTreeNode)parent, cIndexs);
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
     */
    public void reload(TreeNode node) {
        if(node != null) {
            fireTreeStructureChanged(this, getPathToRoot(node), null, null);
        }
    }
    
    /**
     * Invoke this method after you've inserted some TreeNodes into
     * node.  childIndices should be the index of the new elements and
     * must be sorted in ascending order.
     */
    public void nodesWereInserted(TreeNode node, int[] childIndices) {
       /* if ( DEBUG > 0 ) {
            String s = "Nodes inserted to " + node + ": ";
            for(int j = 0; j < childIndices.length; j++) {
                s = s + "[" + Integer.toString(childIndices[j]) + "] " +node.getChildAt(childIndices[j]);
                if ( j + 1 < childIndices.length ) s = s+", ";
            }
            System.out.println(s);
        
            s = "Children are now: ";
            for(int j = 0; j < node.getChildCount(); j++) {
                s = s + "[" + Integer.toString(j) + "] " +node.getChildAt(j);
                if ( j + 1 < node.getChildCount() ) s = s+", ";
            }
            System.out.println(s);
        
            dumpTree();
        } */
        
        if(listenerList != null && node != null && childIndices != null
        && childIndices.length > 0) {
            int               cCount = childIndices.length;
            Object[]          newChildren = new Object[cCount];
            
            for(int counter = 0; counter < cCount; counter++)
                newChildren[counter] = node.getChildAt(childIndices[counter]);
            fireTreeNodesInserted(this, getPathToRoot(node), childIndices, newChildren);
        }
    }
    
    /**
     * Invoke this method after you've removed some TreeNodes from
     * node.  childIndices should be the index of the removed elements and
     * must be sorted in ascending order. And removedChildren should be
     * the array of the children objects that were removed.
     */
    public void nodesWereRemoved(TreeNode node, int[] childIndices,
    Object[] removedChildren) {
        if(node != null && childIndices != null) {
            fireTreeNodesRemoved(this, getPathToRoot(node), childIndices,
            removedChildren);
        }
    }
    
    /**
     * Invoke this method after you've changed how the children identified by
     * childIndicies are to be represented in the tree.
     */
    public void nodesChanged(AttackTreeNode node, int[] childIndices) {
        if(node != null) {
            if (childIndices != null) {
                int            cCount = childIndices.length;
                
                if(cCount > 0) {
                    Object[]       cChildren = new Object[cCount];
                    
                    for(int counter = 0; counter < cCount; counter++) {
                        cChildren[counter] = node.getChildAt(childIndices[counter]);
                    }
                    fireTreeNodesChanged(this, getPathToRoot(node), childIndices, cChildren);
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
     */
    public void nodeStructureChanged(TreeNode node) {
        if(node != null) {
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
     * @param an array of TreeNodes giving the path from the root to the
     *        specified node.
     */
    public TreeNode[] getPathToRoot(TreeNode aNode) {
        return getPathToRoot((AttackTreeNode)aNode, 0);
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
    protected TreeNode[] getPathToRoot(AttackTreeNode aNode, int depth) {
        TreeNode[]              retNodes;
        // This method recurses, traversing towards the root in order
        // size the array. On the way back, it fills in the nodes,
        // starting from the root and working back to the original node.
        
        /* Check for null, in case someone passed in a null node, or
           they passed in an element that isn't rooted at root. */
        if(aNode == null) {
            if(depth == 0)
                return null;
            else
                retNodes = new TreeNode[depth];
        }
        else {
            depth++;
            if(aNode == root)
                retNodes = new TreeNode[depth];
            else {
                if ( showHidden == false ) {
                    retNodes = getPathToRoot((AttackTreeNode)aNode.getParent(), depth);
                }
                else {
                    retNodes = getPathToRoot((AttackTreeNode)aNode.getRealParent(), depth);
                }
            }
            retNodes[retNodes.length - depth] = aNode;
        }
        return retNodes;
    }
    
    public String toString() {
        String s = "";
        if ( getRoot() == null ) {
            return "null root";
        }
        else {
            Enumeration e = root.getRealPreorderEnumeration();
            while (e.hasMoreElements() ) {
                s = s + e.nextElement().toString() + "\n";
            }
        }
        return s;
    }
    
    /** Getter for property showHidden.
     *
     * If showHidden is true, the model will include all the nodes.
     *
     * @return Value of property showHidden.
     */
    public boolean isShowHidden() {
        return showHidden;
    }
    
    /** Setter for property showHidden.
     * @param showHidden New value of property showHidden.
     */
    public void setShowHidden(boolean showHidden) {
        if ( this.showHidden != showHidden ) {
            this.showHidden = showHidden;
            
            if ( getRoot() != null ) nodeStructureChanged((AttackTreeNode)getRoot());
        }
    }
    
    /** Getter for property finished.
     * @return Value of property finished.
     */
    public boolean isFinished() {
        return finished;
    }
    
    /** Setter for property finished.
     * @param finished New value of property finished.
     */
    public void setFinished(boolean finished) {
        this.finished = finished;
        
        if ( finished == true && isAttackTreeModelLocked() ) {
            unlockAttackTreeModel();
        }
    }
    
    private void dumpTree() {
        dumpNode((AttackTreeNode)getRoot(), "");
    }
    
    private void dumpNode(AttackTreeNode tn, String prefix) {
        int count;
        int index;
        AttackTreeNode child;
        
        System.out.println(prefix + tn);
        
        count = this.getChildCount(tn);
        for(index = 0; index < count; index++) {
            child = (AttackTreeNode)this.getChild(tn,index);
            dumpNode(child, prefix + "  ");
        }
    }
    
    /** Starts Processing the Attack based upon the current battleEvent and RootNode.
     *
     * This method starts processing the attack.  All actual processing will take place in the
     * EventDispatch thread, no the battle event.  The attack tree will only be shown if it is
     * necessary.
     *
     * This method should only be called from the BattleEngine Thread.  The results are
     * unpredicatable if called from the Event Thread.
     *
     * This method will block until the attack process is complete.
     */
    public void processAttackTree() throws BattleEventException {
        if ( root  == null ){
            throw new BattleEventException("Root node of AttackTreeModel was null.");
        }
        
//        setInlineView(inlineView);
//        setupInlinePanel();
        
        BattleEvent be = root.getBattleEvent();
//        if ( be != null && be.isShowAttackTree() ) {
//            displayInlineView();
//        }
        
        attackTreePanel = AttackTreePanel.getAttackTreePanel(this);
        
        synchronized ( lockObject ) {
            // Setup a semaphore to block until the attack is finished...
            lockAttackTreeModel();
            
            // Dispatch the event to start processing the attack tree...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    //activateNode(null, null);
                    advanceAndActivate(null, null);
                }
            });
            
         
            // Wait until the gui is finished somehow...
            while ( isAttackTreeModelLocked() ) {
                try {
                    lockObject.wait();
                }
                catch ( InterruptedException ie ){
                    
                }
            }
     	   }
            
            // Check the error status and throw it if it is set...
            if ( getError() != null ) {
                //inlineView.hideBattleView( JOptionPane.CANCEL_OPTION );
                attackTreePanel.deactivateAttackTree();
                throw getError();
            }
            
            attackTreePanel.deactivateAttackTree();
            //inlineView.hideBattleView( JOptionPane.OK_OPTION );
        
    }
    
    private void lockAttackTreeModel() {
        synchronized ( lockObject ) {
            locked = true;
        }
    }
    
    private void unlockAttackTreeModel() {
        synchronized ( lockObject ) {
            locked = false;
            lockObject.notifyAll();
        }
    }
    
    private boolean isAttackTreeModelLocked() {
        synchronized ( lockObject ) {
            return locked;
        }
    }
    
    public void setError( BattleEventException e ) {
        synchronized ( lockObject ) {
            error = e;
            unlockAttackTreeModel();
        }
    }
    
    public BattleEventException getError() {
        return error;
    }
    
    public void setAttackTreePanel(AttackTreePanel panel) {
        if ( this.attackTreePanel != panel ) {
            this.attackTreePanel = panel;
            
            if ( root != null ) root.setAttackTreePanel(panel);
        }
    }
    
    public AttackTreePanel getAttackTreePanel() {
        return attackTreePanel;
    }
    
//    /** Getter for property inlinePanelSetup.
//     * @return Value of property inlinePanelSetup.
//     */
//    public boolean isInlinePanelSetup() {
//        return inlinePanelSetup;
//    }
//    
//    /** Setter for property inlinePanelSetup.
//     * @param inlinePanelSetup New value of property inlinePanelSetup.
//     */
//    public void setInlinePanelSetup(boolean inlinePanelSetup) {
//        this.inlinePanelSetup = inlinePanelSetup;
//    }
    
//    private final void setupInlinePanel() {
//        if ( isInlinePanelSetup() == false ) {
//            atip =  AttackTreeInlinePanel.getAttackTreeInlinePanel(this);
//            
//            setAttackTreePanel(atip);
//            setTree(atip.getTreePanel().getTree());
//            
//            setInlinePanelSetup(true);
//        }
//    }
    
//    /** Triggers the Dispaly on the Inline Panel containing the Attack Tree.
//     */
//    public final void displayInlineView() {
//        if ( inlineView.getViewState() != inlineView.BATTLE_STATE ) {
//            // Attach the model to the Tree
//            atip.attachModelToTree();
//            
//            // Add the inlinePanel to the InlineView
//            inlineView.addBattlePanel( attackTreePanel );
//            
//            // Display the InlineView...this will most definitely block until the okay button is pressed for the final time...
//            inlineView.showBattleView();
//            
//            
//        }
//    }
//    
//    public final boolean isInlineViewVisible() {
//        return inlineView.getViewState() == inlineView.BATTLE_STATE;
//    }
//    
//    /** Getter for property inlineView.
//     * @return Value of property inlineView.
//     */
//    public InlineView getInlineView() {
//        return inlineView;
//    }
//    
//    
//    /** Setter for property inlineView.
//     * @param inlineView New value of property inlineView.
//     */
//    public void setInlineView(InlineView inlineView) {
//        this.inlineView = inlineView;
//    }
    
    /** This is used to determine if the AttackTreePanel is visible for this model.
     *
     * This method will return true if that attackTreePanel is set (non-null) 
     * and this model is the currently attached model (ie, the one the treePanel
     * is actually displaying).
     */
    public boolean isAttackTreePanelVisible() {
        return attackTreePanel != null && attackTreePanel.isModelAttached(this);
    }
    
}
