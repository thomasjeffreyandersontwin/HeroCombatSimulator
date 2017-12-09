/*
 * AttackTreeNode.java
 *
 * Created on October 28, 2001, 10:16 PM
 */

package champions.attackTree;

import champions.BattleEvent;
import champions.CVList;
import champions.Target;
import champions.exception.BattleEventException;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.util.Enumeration;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * AttackTreeNode is the interface which all AttackTree Nodes must implement.
 * 
 * As with the AttackTreeModel, attack tree nodes represent two different trees,
 * the Real tree and the Visible tree.  The real tree includes both visible and
 * hidden nodes.  The visible tree skips all of the hidden nodes, but includes
 * hidden node children as children of the parent node.
 *
 * The root node must always be visible.
 *
 * @author  twalker
 * @version 
 */
    public interface AttackTreeNode extends TreeNode {

    /**
     * Returns the index of <code>node</code> in the receivers children.
     * If the receiver does not contain <code>node</code>, -1 will be
     * returned.
     *
     * Refers to the Visible Tree, not the Real Tree.
     */
     public int getIndex(TreeNode node);
    
    /**
     * Returns the index of <code>node</code> in the receivers children.
     * If the receiver does not contain <code>node</code>, -1 will be
     * returned.
     *
     * Refers to the Real Tree, not the Visible Tree.
     */
     public int getRealIndex(AttackTreeNode node);
    
    /**
     * Returns the parent <code>TreeNode</code> of the receiver.
     *
     *  Refers to the Visible Tree, not the Real Tree.
     */
     public TreeNode getParent();
    
    /**
     * Returns the parent <code>TreeNode</code> of the receiver.
     *
     *  Refers to the Real Tree, not the Visible Tree.
     */
     public AttackTreeNode getRealParent();
     
    /**
     * Returns the parent <code>TreeNode</code> of the receiver.
     *
     *  WARNING: Refers to the Real Tree, not the Visible Tree.
     */
     public void setParent(AttackTreeNode parent);
    
    /**
     * Returns the children of the reciever as an Enumeration.
     *
     * Refers to the Visible Tree, not the Real Tree.
     */
     public Enumeration children();
    
    /**
     * Returns the number of children <code>TreeNode</code>s the receiver
     * contains.
     *
     * Refers to the Visible Tree, not the Real Tree.
     */
     public int getChildCount();
    
    /**
     * Returns the number of children <code>TreeNode</code>s the receiver
     * contains.
     *
     * Refers to the Real Tree, not the Visible Tree.
     */
     public int getRealChildCount();
    
    /**
     * Returns true if the receiver allows children.
     */
     public boolean getAllowsChildren();
    
    /**
     * Returns the child <code>TreeNode</code> at index
     * <code>childIndex</code>.
     *
     * Refers to the Visible Tree, not the Real Tree.
     */
     public TreeNode getChildAt(int childIndex);
    
    /**
     * Returns the child <code>TreeNode</code> at index
     * <code>childIndex</code>.
     *
     * Refers to the Real Tree, not the Visible Tree.
     */
     public AttackTreeNode getRealChildAt(int childIndex);
    
    /**
     * Returns true if the receiver is a leaf.
     */
     public boolean isLeaf();
    
    /**
     * Activates the node.
     *
     * The method is called when this node is being activated.  Based on
     * the information the node current has available, it can choose to 
     * accept the activation or reject it.  
     *
     * If the node rejects the activation, the model will call the appropriate
     * methods to advance out of the node.
     *
     * The manualOverride boolean indicates that the user click on this node
     * specifically.  Even if this node has all the information to continue processing
     * without user input, it should accept activation so the user can make changes.
     */
     public boolean activateNode(boolean manualOverride) throws BattleEventException;
    
    /** 
     * Informs the node it is losing active node status and should do any processing necessary.
     *
     * When an event occurs which triggers an advancement of the currently active node, all nodes
     * from the current active node up to the new active node will have activateNode called.
     *
     * advanceNode should return the next eligible child to be activated.  If the node has no more
     * eligible children, it should return null to indicate it's parent should select the next node.
     *
     * In general, a node should do any process it needs to do in the attack process when advanceNode
     * is called.  If the node does not want to lose active node status, it should return itself, which
     * will cause the node to be reactivated.
     *
     * The activeChild lists the child of this node which contains the currently active child.  For
     * example, consider the following tree:
     *
     *  A
     *  |\
     *  | B
     *  |  \
     *  |   C
     *   \
     *    D
     *     \
     *      E
     *
     * If C is the active node and b.advanceNode returned null, a.advanceNode will be called with the
     * parameter of node B, since the active node is a child of B.
     */
     public AttackTreeNode advanceNode(AttackTreeNode activeChild) throws BattleEventException;
    
    /** 
     * Set the Undo Index value.
     *
     * The undo index value is the index of the current undoable of
     * the BattleEvent.  Event AttackTreeNode has an undo index which
     * is used to roll the BattleEvent forward and backward according
     * which node in the AttackTree is active.
     */
     public void setUndoIndex(int index);
    
    /** 
     * Get the Undo Index value.
     *
     * The undo index value is the index of the current undoable of
     * the BattleEvent.  Event AttackTreeNode has an undo index which
     * is used to roll the BattleEvent forward and backward according
     * which node in the AttackTree is active.
     */
     public int getUndoIndex();
     
     /**
      * Gets the model this node belongs to.
      */
     public AttackTreeModel getModel();
     
     /**
      * Sets teh model this node belongs to.
      */
     public void setModel(AttackTreeModel model);
     
     /**
      * Gets the InlinePanel this node belongs to.
      */
     public AttackTreePanel getAttackTreePanel();
     
     /**
      * Sets teh InlinePanel this node belongs to.
      */
     public void setAttackTreePanel(AttackTreePanel model);
     
     /**
      * Informs the parent that a child node was altered in a major way, which
      * might require reconstruction of the tree below that child.
      */
     public void childNodeAltered(AttackTreeModel childNode);
     
     /**
      * Returns whether the node is hidden or not.
      */
     public boolean isVisible();
     
     /** 
      * Sets the nodes isVisilbe property.
      *
      * Hidden nodes are not part of the Visible tree, but are part of the Real tree.
      */
     public void setVisible(boolean visible);
     
     /** 
      * Returns a preorder enumeration of the subtree node is a parent of.
      */
     public Enumeration getRealPreorderEnumeration();
     
    /**
     * Sets the nodes's BattleEvent.
     */
    public void setBattleEvent(BattleEvent be);
    
     /**
     * Gets the nodes's BattleEvent.
     */
    public BattleEvent getBattleEvent();
    
    /**
     * Destroys the node and releases any held resources.
     */
    public void destroy();
    
    /**
     * Gets the Name of the Node.
     *
     * The Node name can be used to ease the comparison of existing nodes when
     * building out the attack tree.
     */
    public String getName();
    
    /**
     * Sets the Name of the Node.
     *
     * The Node name can be used to ease the comparison of existing nodes when
     * building out the attack tree.
     */
    public void setName(String name);
    
    public Icon getIcon(JTree tree, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus);
    
    public TreeCellRenderer getTreeCellRenderer(JTree tree);
    
    public TreeCellEditor getTreeCellEditor(JTree tree);
    
/** Returns the current status of the node.
 * The status of the node determine if additional marking are placed on the icons of the tree hierarchy.
 * OKAY_STATUS indicates there are no problems.
 * QUESTION_STATUS indicates a question or misconfiguration exists, but is not causing an error.
 * ERROR_STATUS indicates that a misconfiguration exists which will disable the node.
 * CRITICAL_STATUS indicates immidiate attention must be payed to the node.
 */
    
    public int getNodeStatus();
    
/** Notifies Node of a drop event occuring in or below the node.
 * The Node can use the handleDrop method to perform actions based on the drop of a DetailList on
 * the Node Jtree.  If the node wishes to handles the event, it should call acceptDrop and dropComplete
 * on the event, then return true to indicate the event was handled.
 * @returns True if event was handled.  False if additional handling
 * should be done.
 * @param dropPath Indicates the location of the drop in the Tree hierarchy.
 * @param event The Drop event that is being handled.
 * @return True if the event was handled, false if it wasn't.
 */
    
    public boolean handleDrop(JTree tree, TreePath dropPath, DropTargetDropEvent event);
    
/** Called to check if a node would handle a drop if it occurred.
 * @param dropPath Indicates the location of the drop in the Tree hierarchy.
 * @param event The Drop event that is being handled.
 * @return True if the drop could be handled.  False otherwise.
 */
    
    public boolean willHandleDrop(JTree tree, TreePath dropPath, DropTargetDragEvent event);

    /**
     * Sets the TargetGroup of the Node.
     *
     * The TargetGroup should be the suffix associated with this particular node.
     *
     */
    public void setTargetGroupSuffix(String suffix);
    
    /**
     * Gets the TargetGroup of the Node.
     *
     * This is the complete targetGroup name, based upon the tree structure,
     * and the suffixes stored by each node.
     */
    public String getTargetGroup();
    
    /**
     * Indicates that one of the nodes in this nodes hierarchy changed, usually
     * either a direct parent or direct child.
     */
    public void nodeChanged(AttackTreeNode changedNode);
    
    /**
     * Performs a Breadth first search for a node with the name.
     *
     */
    public AttackTreeNode findNode(String name, boolean searchChildren);
    
    /**
     * Returns the AutoBypass OPTION NAME for the node.
     */
     public String getAutoBypassOption();
     
     /**
      * Returns the AutoBypass Target for the node.
      *
      * The AutoBypass Target is the Target which is inspected for 
      * the OPTION specified by getAutoBypassOption().
      */
    public Target getAutoBypassTarget();
    
    /** 
     * Allows node to adjust the CVList created for an attack.
     *
     * Every node in the tree path of a to-hit node has an oppertunity to
     * adjust the cvList for an attack via this method.  This method should
     * in most cases pass this call up to it's attack tree node parent (or
     * simply call the default implementation which does exactly that).
     */
    public void adjustCVList(BattleEvent be, Target source, Target target, CVList cvList);
    
    /** 
     * Allows node to adjust the target set for an attack.
     *
     * Every node in the tree path of a target node has an oppertunity to
     * adjust the target for an attack via this method.  This method should
     * in most cases pass this call up to it's attack tree node parent (or
     * simply call the default implementation which does exactly that).
     *
     * PrimaryTargetNumber indicates if this particular target is considered 
     * a primary target for this attack.  If it is, primaryTargetNumber should
     * be one or greater.
     */
    public void adjustTarget(BattleEvent be, Target source, int referenceNumber, String targetGroup, int primaryTargetNumber);

    /** 
     * Allows node to adjust the source for an attack.
     *
     */
    public void adjustSource(BattleEvent be, int referneceNumber, String targetGroup);
}
