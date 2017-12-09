/*
 * DefaultAttackTreeNode.java
 *
 * Created on October 28, 2001, 10:44 PM
 */
package champions.attackTree;

import champions.BattleEvent;
import champions.CVList;
import champions.Target;
import champions.exception.BattleEventException;
import champions.interfaces.ChampionsConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Provides the default implementation for an AttackTreeNode.
 *
 * In general all of the maintenance method are implemented with usable
 * code.  The activateNode and advanceNode method should be overriden and
 * implemented by children.
 *
 * @author  twalker
 * @version
 */
public class DefaultAttackTreeNode implements AttackTreeNode, ChampionsConstants {

    /**
     * Stores the parent of this node.
     *
     * Note, this stores the real parent.
     *
     * If this is the root, the parent is null.
     */
    protected AttackTreeNode parent;

    /**
     * Stores the children of this node.
     *
     * By default, this vector is not constructed till needed.
     *
     * Note, this stores the real children.
     */
    protected Vector children = null;

    /** Stores the visible property. */
    protected boolean visible = true;

    /** Stores the undo index. */
    protected int undoIndex;

    /** Stores the node's BattleEvent. */
    public BattleEvent battleEvent;

    /** Stores the node's AttackTreeModel. */
    protected AttackTreeModel model;

    /** Stores the node's AttackTreeInlinePanel. */
    protected AttackTreePanel attackTreePanel;

    /** Stores the node's Name. */
    protected String name;

    /** Stores the icon for this node. */
    protected Icon icon;

    /** Stores the base targetGroupSuffix for this node */
    protected String targetGroupSuffix;

    /** Stores the cached TargetGroup for this node */
    private String targetGroup = null;

    /** Creates new DefaultAttackTreeNode */
    public DefaultAttackTreeNode() {
    }

    /**
     * Returns the index of <code>node</code> in the receivers children.
     * If the receiver does not contain <code>node</code>, -1 will be
     * returned.
     * @param node
     * @return
     */
    @Override
    public int getIndex(TreeNode node) {
        int index = 0;
        int j;
        AttackTreeNode atn;
        if (children != null) {
            // Iterate through the children.  If the child is hidden, recursively
            // search it's children first.
            Iterator i = children.iterator();
            while (i.hasNext()) {
                atn = (AttackTreeNode) i.next();
                if (atn.isVisible() == false) {
                    if ((j = atn.getIndex(node)) != -1) {
                        return index + j;
                    }
                    else {
                        index += atn.getChildCount();
                    }
                }
                else if (atn == node) {
                    return index;
                }
                else {
                    index++;
                }
            }
        }
        return -1;
    }

    /**
     * Returns the parent <code>TreeNode</code> of the receiver.
     * @return
     */
    @Override
    public TreeNode getParent() {
        AttackTreeNode atn;
        atn = getRealParent();
        // Check to see if the real parent is visible.  If not,
        // call getParent on the parent, which will cause a recursion
        // to occur, resulting in the first visible parent in the
        // node branch.
        if (atn != null && atn.isVisible() == false) {
            atn = (AttackTreeNode) atn.getParent();
        }

        return atn;
    }

    /**
     * Returns the children of the reciever as an Enumeration.
     * @return
     */
    @Override
    public Enumeration children() {
        if (children == null) {
            return EMPTY_ENUMERATION;
        }

        return children.elements();
    }

    /**
     * Returns the number of children <code>TreeNode</code>s the receiver
     * contains.
     * @return
     */
    @Override
    public int getChildCount() {
        int count = 0;
        AttackTreeNode atn;
        if (children != null) {
            // Iterate through the children.  If the child is hidden, count it's
            // children as my own.
            Iterator i = children.iterator();
            while (i.hasNext()) {
                atn = (AttackTreeNode) i.next();
                if (atn.isVisible() == false) {
                    count += atn.getChildCount();
                }
                else {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Returns true if the receiver allows children.
     *
     * This method should be overriden by child classes.
     * @return
     */
    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    /**
     * Returns the child <code>TreeNode</code> at index
     * <code>childIndex</code>.
     * @param childIndex
     * @return
     */
    @Override
    public TreeNode getChildAt(int childIndex) {
        int index = 0;
        AttackTreeNode atn;
        if (children != null) {
            // Iterate through the children.  If the child is hidden, recursively
            // check if the childIndex would fall within it's children.
            Iterator i = children.iterator();
            while (i.hasNext()) {
                atn = (AttackTreeNode) i.next();
                if (atn.isVisible() == false) {
                    int childCount = atn.getChildCount();
                    if (childIndex < index + childCount) {
                        return atn.getChildAt(childIndex - index);
                    }
                    else {
                        index += childCount;
                    }
                }
                else if (index == childIndex) {
                    return atn;
                }
                else {
                    index++;
                }
            }
        }
        return null;
    }

    /**
     * Get the Undo Index value.
     *
     * The undo index value is the index of the current undoable of
     * the BattleEvent.  Event AttackTreeNode has an undo index which
     * is used to roll the BattleEvent forward and backward according
     * which node in the AttackTree is active.
     * @return
     */
    @Override
    public int getUndoIndex() {
        return undoIndex;
    }

    /**
     * Set the Undo Index value.
     *
     * The undo index value is the index of the current undoable of
     * the BattleEvent.  Event AttackTreeNode has an undo index which
     * is used to roll the BattleEvent forward and backward according
     * which node in the AttackTree is active.
     * @param index
     */
    @Override
    public void setUndoIndex(int index) {
        undoIndex = index;
    }

    /**
     * Returns true if the receiver is a leaf.
     * @return
     */
    @Override
    public boolean isLeaf() {
        // This is actually an error, since it returns the real tree leaf status,
        // not the visible tree.  It is possible for the real tree to not be a leaf,
        // but for the real tree to be one.
        return (children == null || children.size() == 0);
    }

    /**
     * Returns the parent <code>TreeNode</code> of the receiver.
     *
     * Refers to the Real Tree, not the Visible Tree.
     * @return
     */
    @Override
    public AttackTreeNode getRealParent() {
        return parent;
    }

    /**
     * Returns the index of <code>node</code> in the receivers children.
     * If the receiver does not contain <code>node</code>, -1 will be
     * returned.
     *
     * Refers to the Real Tree, not the Visible Tree.
     * @param node
     * @return
     */
    @Override
    public int getRealIndex(AttackTreeNode node) {
        if (children != null) {
            return children.indexOf(node);
        }
        else {
            return -1;
        }
    }

    /**
     * Returns the child <code>TreeNode</code> at index
     * <code>childIndex</code>.
     *
     * Refers to the Real Tree, not the Visible Tree.
     * @param childIndex 
     * @return
     */
    @Override
    public AttackTreeNode getRealChildAt(int childIndex) {
        if (children != null && childIndex < children.size()) {
            return (AttackTreeNode) children.get(childIndex);
        }
        else {
            return null;
        }
    }

    /**
     * Returns the number of children <code>TreeNode</code>s the receiver
     * contains.
     *
     * Refers to the Real Tree, not the Visible Tree.
     * @return
     */
    @Override
    public int getRealChildCount() {
        return (children == null) ? 0 : children.size();
    }

    /**
     * Sets teh InlinePanel this node belongs to.
     * @param inline
     */
    @Override
    public void setAttackTreePanel(AttackTreePanel inline) {
        this.attackTreePanel = inline;

        if (children != null) {
            Iterator i = children.iterator();
            while (i.hasNext()) {
                ((AttackTreeNode) i.next()).setAttackTreePanel(inline);
            }
        }
    }

    /**
     * Gets the InlinePanel this node belongs to.
     * @return
     */
    @Override
    public AttackTreePanel getAttackTreePanel() {
        return attackTreePanel;
    }

    /**
     * Gets the model this node belongs to.
     * @return
     */
    @Override
    public AttackTreeModel getModel() {
        return model;
    }

    /**
     * Set the model that the node belongs to.
     *
     * All children of the node also have their model set by this method.
     * @param model
     */
    @Override
    public void setModel(AttackTreeModel model) {
        this.model = model;

        if (children != null) {
            Iterator i = children.iterator();
            while (i.hasNext()) {
                ((AttackTreeNode) i.next()).setModel(model);
            }
        }
    }

    /**
     * Returns whether the node is hidden or not.
     * @return
     */
    @Override
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets the nodes isVisilbe property.
     *
     * Hidden nodes are not part of the Visible tree, but are part of the Real tree.
     * @param visible
     */
    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Sets the node's BattleEvent.
     * @param be
     */
    @Override
    public void setBattleEvent(BattleEvent be) {
        battleEvent = be;

        if (children != null) {
            Iterator i = children.iterator();
            while (i.hasNext()) {
                AttackTreeNode atn = (AttackTreeNode) i.next();
                if (atn.getBattleEvent() == null) {
                    atn.setBattleEvent(be);
                }
            }
        }
    }

    /**
     * Gets the node's BattleEvent.
     * @return
     */
    @Override
    public BattleEvent getBattleEvent() {
        return battleEvent;
    }

    /**
     * Returns a preorder enumeration of the subtree node is a parent of.
     *
     * This refers to
     * @return
     */
    @Override
    public Enumeration getRealPreorderEnumeration() {
        return new DefaultAttackTreeNode.PreorderEnumeration(this);
    }

    /**
     * Returns the parent <code>TreeNode</code> of the receiver.
     *
     *  WARNING: Refers to the Real Tree, not the Visible Tree.
     * @param parent
     */
    @Override
    public void setParent(AttackTreeNode parent) {
        this.parent = parent;
        updateTargetGroup();
    }

    /**
     * Adds a child to this node.
     *
     * The child is added to this node and the model is
     * informed of the changes.
     * @param child
     */
    public void addChild(AttackTreeNode child) {
        addChild(child, (children == null) ? 0 : children.size(), true);
    }

    /**
     * Adds a child to this node.
     *
     * The child is added to this node and the model is
     * informed of the changes.
     * @param child
     * @param setBattleEvent
     */
    public void addChild(AttackTreeNode child, boolean setBattleEvent) {
        addChild(child, (children == null) ? 0 : children.size(), setBattleEvent);
    }

    /**
     * Adds a child to this node.
     *
     * The child is added to this node and the model is
     * informed of the changes.
     * @param child 
     * @param setBattleEvent
     * @param pos
     */
    public void addChild(AttackTreeNode child, int pos, boolean setBattleEvent) {
        if (child == null) {
            return;
        }
        if (children == null) {
            children = new Vector();
        }

        int currentSize = getChildCount();

        children.add(pos, child);
        child.setParent(this);
        child.setModel(getModel());
        if (setBattleEvent) {
            child.setBattleEvent(getBattleEvent());
        }
        child.setAttackTreePanel(getAttackTreePanel());

        if (getModel() != null) {
            if (isVisible() && child.isVisible()) {
                int insertedIndex = getIndex(child);
                getModel().nodesWereInserted(this, new int[]{insertedIndex});
            }
            else if (child.isVisible()) {
                // The child is visible, and hence , so actually only the child needs to be looked up
                // and inserted into the visibleParent
                AttackTreeNode visibleParent = (AttackTreeNode) getParent();

                int indexInVisibleParent = visibleParent.getIndex(child);
                getModel().nodesWereInserted(visibleParent, new int[]{indexInVisibleParent});
            }
            else {
                // The child is invisible, so add all the visible children of the child to the
                // a visible parent
                AttackTreeNode visibleParent;
                if (this.isVisible()) {
                    // This node is visible, so do everything from the viewpoint of this node.
                    visibleParent = this;
                }
                else {
                    // This node is hidden also, so find a visible parent before proceeding.
                    visibleParent = (AttackTreeNode) getParent();
                }

                int index, count;
                if ((count = child.getChildCount()) != 0) {
                    // If there are children, look the index of the first one up,
                    // then make an array for all the children.
                    AttackTreeNode firstVisibleChild = (AttackTreeNode) child.getChildAt(0);

                    if (firstVisibleChild != null) {
                        // There are visible children, so go ahead and tell the tree about them.
                        int firstInsertedIndex = visibleParent.getIndex(firstVisibleChild);

                        int[] indexes = new int[count];
                        for (index = 0; index < count; index++) {
                            indexes[index] = index + firstInsertedIndex;
                        }

                        getModel().nodesWereInserted(visibleParent, indexes);

                    }
                }
            }
        }
    }

    /**
     *
     * @param child
     */
    public void removeChild(AttackTreeNode child) {
        int index;
        if (AttackTreeModel.DEBUG > 0) {
            System.out.println("removing " + child + " from tree.");
        }
        if (children != null && (index = children.indexOf(child)) != -1) {


            // Before removing the node, make sure we built the removal lists
            int[] removedIndexes = null;
            Object[] removedNodes = null;
            AttackTreeNode removedParent = null;

            if (isVisible() && child.isVisible()) {
                int indexInVisibleParent = getIndex(child);

                removedParent = this;
                removedIndexes = new int[]{indexInVisibleParent};
                removedNodes = new Object[]{child};
            }
            else if (child.isVisible()) {
                // The child is visible, but the parent isn't, so actually only the child needs to be looked up
                // and inserted into the visibleParent
                AttackTreeNode visibleParent = (AttackTreeNode) getParent();

                int indexInVisibleParent = visibleParent.getIndex(child);
                removedParent = visibleParent;
                removedIndexes = new int[]{indexInVisibleParent};
                removedNodes = new Object[]{child};
            }
            else {
                // The child isn't visible, so add all it's visible children to the change list
                AttackTreeNode visibleParent;
                if (this.isVisible()) {
                    // This node is visible, so do everything from the viewpoint of this node.
                    visibleParent = this;
                }
                else {
                    // This node is hidden also, so find a visible parent before proceeding.
                    visibleParent = (AttackTreeNode) getParent();
                }

                int rindex, count;
                if ((count = child.getChildCount()) != 0) {
                    // If there are children, look the index of the first one up,
                    // then make an array for all the children.
                    AttackTreeNode firstVisibleChild = (AttackTreeNode) child.getChildAt(0);

                    if (firstVisibleChild != null) {
                        // There are Visible Children, so tell the tree about them
                        int firstInsertedIndex = visibleParent.getIndex(firstVisibleChild);

                        removedIndexes = new int[count];
                        removedNodes = new Object[count];
                        removedParent = visibleParent;

                        for (rindex = 0; rindex < count; rindex++) {
                            removedIndexes[rindex] = rindex + firstInsertedIndex;
                            removedNodes[rindex] = visibleParent.getChildAt(rindex + firstInsertedIndex);
                        }
                    }
                }
            }

            child.destroy();
            children.remove(index);

            if (model != null && removedParent != null) {
                model.nodesWereRemoved(removedParent, removedIndexes, removedNodes);
                /*  if ( AttackTreeModel.DEBUG > 0 ) {
                String s = "Nodes removed from " + removedParent.getName() + ": ";
                for(int j = 0; j < removedNodes.length; j++) {
                s = s + ((AttackTreeNode)removedNodes[j]).getName() + "[" + Integer.toString(removedIndexes[j]) + "]";
                if ( j + 1 < removedNodes.length ) s = s+", ";
                }
                System.out.println(s);
                } */
            }
        }
    }

    /**
     * Destroys the node and releases any held resources.
     */
    @Override
    public void destroy() {
        // If children exist, first destroy them and release the vector.
        if (children != null) {
            int index, count;
            count = children.size();
            for (index = 0; index < count; index++) {
                AttackTreeNode atn = (AttackTreeNode) children.get(index);
                atn.destroy();
            }
            children.clear();

            children = null;
        }

        parent = null;
        model = null;
        battleEvent = null;
        attackTreePanel = null;

    }

    /**
     * Informs the parent that a child node was altered in a major way, which
     * might require reconstruction of the tree below that child.
     * @param childNode
     */
    @Override
    public void childNodeAltered(AttackTreeModel childNode) {
    }

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
     * @param activeChild 
     * @return
     * @throws BattleEventException
     */
    @Override
    public AttackTreeNode advanceNode(AttackTreeNode activeChild) throws BattleEventException {
        AttackTreeNode nextNode = null;

        if (activeChild == this) {
            // Clear the attackTreePanel and instructions.
            // do this before processAdvance() since process advance uses info from the hidePanel method
            // of most InputPanels
            if (attackTreePanel != null) {
                attackTreePanel.clearInputPanel();
                attackTreePanel.setInstructions("");
            }


            boolean result = processAdvance();

            if (result == false) {
                nextNode = this;
            }
            else {
                // The node will allow advancement.
                // Check to make sure that the children are built correctly.
                checkNodes();

                if (children != null && children.size() > 0) {
                    nextNode = (AttackTreeNode) children.get(0);
                }
                else {
                    nextNode = buildNextChild(null);
                    if (nextNode != null) {
                        addChild(nextNode, propogateBattleEvent(nextNode));
                    }
                }
            }
        }
        else {
            // Check to make sure that the children are built correctly.
            checkNodes();

            // Check to see if there is already a child following the current one.
            // Lookup the index of the current child.  If index + 1 is less then size,
            // there are nodes following that child.
            int index;
            if (children != null && (index = children.indexOf(activeChild)) + 1 < children.size()) {
                nextNode = (AttackTreeNode) children.get(index + 1);
            }
            else {
                // activeChild is the last child, so build the next node based on that...
                nextNode = buildNextChild(activeChild);
                if (nextNode != null) {
                    addChild(nextNode, propogateBattleEvent(nextNode));
                }
            }
        }
        return nextNode;
    }

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
     *
     * This method should be overriden by children.
     * @param manualOverride 
     * @return
     * @throws BattleEventException
     */
    @Override
    public boolean activateNode(boolean manualOverride) throws BattleEventException {
        return false;
    }

    /**
     * Builds the next child based upon the last child which was constructed.
     *
     * If all the children necessary have been built, return null, otherwise
     * return the next node.
     *
     * BuildNextChild should add that child to the tree.
     * @param currentChild
     * @return
     */
    public AttackTreeNode buildNextChild(AttackTreeNode currentChild) {
        return null;
    }

    /**
     * Causes node to process an advance and perform any work that it needs to do.
     *
     * This is a method introduced by DefaultAttackTreeNode.  DefaultAttackTreeNode
     * delegates to this method if advanceNode node is called and this is the active
     * node.  This method should do all the work of advanceNode whenever it can, since
     * the next node processing and buildNextChild methods depend on the existing
     * DefaultAttackTreeNode advanceNode method.
     *
     * Returns true if it is okay to leave the node, false if the node should
     * be reactivated to gather more information.
     * @return 
     * @throws BattleEventException
     */
    public boolean processAdvance() throws BattleEventException {
        return true;
    }

    /**
     * Causes the node to verify the layout of it's children and fix any discrepencies.
     *
     * checkNodes should guarantee that the current child structure is complete and
     * all nodes which should exist do.
     *
     * The default implementation calls the nextNodeName method to check that the nodes
     * are arranged properly.  If they are not, it deletes all nodes that aren't properly
     * arranged.
     */
    public void checkNodes() {
        int count = (children == null) ? 0 : children.size();
        int index;
        String previousNodeName = null;
        String nextNodeName = null;
        AttackTreeNode atn;

        for (index = 0; index < count; index++) {
            atn = (AttackTreeNode) children.get(index);
            nextNodeName = nextNodeName(previousNodeName);
            if (atn.getName().equals(nextNodeName) == false) {
                // The next node name isn't what it should be, so rebuild this tree from this child down.
                while (index < children.size()) {
                    atn = (AttackTreeNode) children.get(index);
                    removeChild(atn);
                }
                break;
            }
            previousNodeName = nextNodeName;
        }
    }

    /**
     * Returns the name of the next nodes based upon the current node.
     *
     * nextNodeName is used to determine the order of the child nodes.  It is used
     * by the default implementation of checkNodes to gaurantee that the children
     * are arranged correctly.
     * @param previousNodeName
     * @return
     */
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;

        return nextNodeName;
    }

    /**
     * Sets the Name of the Node.
     *
     * The Node name can be used to ease the comparison of existing nodes when
     * building out the attack tree.
     * @param name
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the Name of the Node.
     *
     * The Node name can be used to ease the comparison of existing nodes when
     * building out the attack tree.
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the toString of the node.
     *
     * The default implementation is to print the node name.
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Indicates whether the battleEvent of this parent should be
     * propogated to the child nodes when addChild is called.
     * @param node 
     * @return
     */
    public boolean propogateBattleEvent(AttackTreeNode node) {
        return true;
    }

    /**
     * Sets the Name of the Node.
     *
     * The Node name can be used to ease the comparison of existing nodes when
     * building out the attack tree.
     * @param name
     */
    @Override
    public void setTargetGroupSuffix(String name) {
        this.targetGroupSuffix = name;

        updateTargetGroup();
    }

    /** Updates the Cached Target Group */
    private void updateTargetGroup() {
        if (targetGroupSuffix == null) {
            if (parent == null) {
                setTargetGroup("");
            }
            else {
                setTargetGroup(parent.getTargetGroup());
            }
        }
        else {
            if (parent == null) {
                setTargetGroup(targetGroupSuffix);
            }
            else {
                StringBuffer sb = new StringBuffer();
                sb.append(parent.getTargetGroup());
                sb.append(".");
                sb.append(targetGroupSuffix);
                setTargetGroup(sb.toString());
            }
        }
    }

    /**
     * Gets the Name of the Node.
     *
     * The Node name can be used to ease the comparison of existing nodes when
     * building out the attack tree.
     * @return
     */
    @Override
    public String getTargetGroup() {
        if (targetGroup == null) {
            updateTargetGroup();
        }

        return targetGroup;

        /*   if ( parent != null ) {
        String parentTargetGroup = parent.getTargetGroup();
        if ( parentTargetGroup.equals("") == false ) {
        return  parentTargetGroup + (targetGroupSuffix == null ? "" : ( "." + targetGroupSuffix));
        }
        else {
        return (targetGroupSuffix == null ? "" : targetGroupSuffix);
        }
        }
        else {
        return (targetGroupSuffix == null ? "" : targetGroupSuffix);
        } */
    }

    /**
     * Set the Currently Cached Target Group Name.
     */
    private void setTargetGroup(String targetGroup) {
        this.targetGroup = targetGroup;
    }

    /** Returns the current status of the node.
     * The status of the node determine if additional marking are placed on the icons of the tree hierarchy.
     * OKAY_STATUS indicates there are no problems.
     * QUESTION_STATUS indicates a question or misconfiguration exists, but is not causing an error.
     * ERROR_STATUS indicates that a misconfiguration exists which will disable the node.
     * CRITICAL_STATUS indicates immidiate attention must be payed to the node.
     * @return
     */
    @Override
    public int getNodeStatus() {
        return OKAY_STATUS;
    }

    /**
     *
     * @param tree
     * @return
     */
    @Override
    public TreeCellEditor getTreeCellEditor(JTree tree) {
        return null;
    }

    /**
     *
     * @param tree
     * @return
     */
    @Override
    public TreeCellRenderer getTreeCellRenderer(JTree tree) {
        return null;
    }

    /** Called to check if a node would handle a drop if it occurred.
     * @param tree
     * @param dropPath Indicates the location of the drop in the Tree hierarchy.
     * @param event The Drop event that is being handled.
     * @return True if the drop could be handled.  False otherwise.
     */
    @Override
    public boolean willHandleDrop(JTree tree, TreePath dropPath, DropTargetDragEvent event) {
        return false;
    }

    /** Notifies Node of a drop event occuring in or below the node.
     * The Node can use the handleDrop method to perform actions based on the drop of a DetailList on
     * the Node Jtree.  If the node wishes to handles the event, it should call acceptDrop and dropComplete
     * on the event, then return true to indicate the event was handled.
     * @returns True if event was handled.  False if additional handling
     * should be done.
     * @param tree
     * @param dropPath Indicates the location of the drop in the Tree hierarchy.
     * @param event The Drop event that is being handled.
     * @return True if the event was handled, false if it wasn't.
     */
    @Override
    public boolean handleDrop(JTree tree, TreePath dropPath, DropTargetDropEvent event) {
        return false;
    }

    /**
     *
     * @param tree
     * @param selected
     * @param expanded
     * @param leaf
     * @param row
     * @param hasFocus
     * @return
     */
    @Override
    public Icon getIcon(JTree tree, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        return icon;
    }

    /**
     * Indicates that one of the nodes in this nodes hierarchy changed, usually
     * either a direct parent or direct child.
     * @param changedNode
     */
    @Override
    public void nodeChanged(AttackTreeNode changedNode) {
        if (AttackTreeModel.DEBUG > 0) {
            System.out.println(this + ".nodeChanged(" + changedNode + ") called.");
        }
        // Just pass this up to the parent...
        if (getRealParent() != null) {
            getRealParent().nodeChanged(changedNode);
        }
    }

    @Override
    public AttackTreeNode findNode(String name, boolean searchChildren) {
        int index, count;
        AttackTreeNode node = null;

        count = (children != null) ? children.size() : 0;
        for (index = 0; index < count; index++) {
            node = (AttackTreeNode) children.get(index);
            if (node.getName().equals(name)) {
                return node;
            }
        }

        if (searchChildren) {
            // Didn't find it in the direct children, so continue down.
            AttackTreeNode child;

            for (index = 0; index < count; index++) {
                child = (AttackTreeNode) children.get(index);
                node = findNode(name, searchChildren);
                if (node != null) {
                    return node;
                }
            }
        }

        return null;
    }

    @Override
    public Target getAutoBypassTarget() {
        return null;
    }

    /**
     * Returns the AutoBypass OPTION NAME for the node.
     * @return
     */
    @Override
    public String getAutoBypassOption() {
        return null;
    }

    /**
     *
     * @return
     */
    public boolean getAutoBypassValue() {
        Target t = getAutoBypassTarget();
        String o = getAutoBypassOption();
        if (t != null && o != null) {
            return t.getBooleanProfileOption(o);
        }
        else {
            return false;
        }
    }

    /** 
     * Allows node to adjust the CVList created for an attack.
     *
     * Every node in the tree path of a to-hit node has an oppertunity to
     * adjust the cvList for an attack via this method.  This method should
     * in most cases pass this call up to it's attack tree node parent (or
     * simply call the default implementation which does exactly that).
     * @param be
     * @param source
     * @param target
     * @param cvList
     */
    @Override
    public void adjustCVList(BattleEvent be, Target source, Target target, CVList cvList) {
        if (parent != null) {
            parent.adjustCVList(be, source, target, cvList);
        }
    }

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
     * @param be 
     * @param source 
     * @param primaryTargetNumber
     * @param referenceNumber
     * @param targetGroup
     */
    @Override
    public void adjustTarget(BattleEvent be, Target source, int referenceNumber, String targetGroup, int primaryTargetNumber) {
        if (parent != null) {
            parent.adjustTarget(be, source, referenceNumber, targetGroup, primaryTargetNumber);
        }
    }

    /** 
     * Allows node to adjust the source for an attack.
     *
     * @param be
     * @param referneceNumber
     * @param targetGroup
     */
    @Override
    public void adjustSource(BattleEvent be, int referneceNumber, String targetGroup) {
        if (be != battleEvent) {
            return; // Make sure we didn't skip to a different battle event...
        }
        if (parent != null) {
            parent.adjustSource(be, referneceNumber, targetGroup);
        }
    }

    final static class PreorderEnumeration implements Enumeration {

        protected Stack<Enumeration> stack;

        public PreorderEnumeration(TreeNode rootNode) {
            super();
            stack = new Stack();
            stack.push( new SingletonEnumeration(rootNode) );
        }

        @Override
        public boolean hasMoreElements() {
            return (!stack.empty() && (stack.peek()).hasMoreElements());
        }

        @Override
        public Object nextElement() {
            Enumeration enumer = stack.peek();
            TreeNode node = (TreeNode) enumer.nextElement();
            Enumeration children = node.children();

            if (!enumer.hasMoreElements()) {
                stack.pop();
            }
            if (children.hasMoreElements()) {
                stack.push(children);
            }
            return node;
        }
    }  // End of class PreorderEnumeration
    
    /**
     * An enumeration that is always empty. This is used when an enumeration
     * of a leaf node's children is requested.
     */
    static public final Enumeration EMPTY_ENUMERATION = new Enumeration() {

        @Override
        public boolean hasMoreElements() {
            return false;
        }

        @Override
        public Object nextElement() {
            throw new NoSuchElementException("No more elements");
        }
    };

    public static class SingletonEnumeration<T> implements Enumeration<T> {

        T object;

        public SingletonEnumeration(T object) {
            this.object = object;
        }

        

        public boolean hasMoreElements() {
            return object != null;
        }

        public T nextElement() {
            T next = object;
            object = null;
            return next;
        }

    }

}
