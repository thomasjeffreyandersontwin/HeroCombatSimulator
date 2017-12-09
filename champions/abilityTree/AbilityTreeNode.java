/*
 * AbilityTreeNode.java
 *
 * Created on June 12, 2001, 10:39 AM
 */

package champions.abilityTree;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.util.Iterator;

import champions.interfaces.*;

import treeTable.*;

import tjava.Destroyable;

/**
 *
 * @author  twalker
 * @version
 */
public class AbilityTreeNode extends DefaultMutableTreeNode
implements MutableTreeNode, ChampionsConstants, Destroyable {
   /* public static final int CRITICAL_STATUS = 0;
    public static final int ERROR_STATUS = 1;
    public static final int QUESTION_STATUS = 2;
    
    public static final int CHILD_CRITICAL_STATUS = 5;
    public static final int CHILD_ERROR_STATUS = 6;
    public static final int CHILD_QUESTION_STATUS = 7;
    public static final int OKAY_STATUS = 8; */
    
    /** Holds value of property model. */
    protected AbilityTreeTableModel model;
    protected JTree tree;
    
    /** Holds value of property expandDuringDrag. */
    private boolean expandDuringDrag;
    
    public Icon getIcon(JTree tree, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        return null;
    }
    
    public TreeCellRenderer getTreeCellRenderer(JTree tree) {
        return null;
    }
    
    public TreeCellEditor getTreeCellEditor(JTree tree) {
        return null;
    }
    

    
/** Returns the current status of the node.
 * The status of the node determine if additional marking are placed on the icons of the tree hierarchy.
 * OKAY_STATUS indicates there are no problems.
 * QUESTION_STATUS indicates a question or misconfiguration exists, but is not causing an error.
 * ERROR_STATUS indicates that a misconfiguration exists which will disable the node.
 * CRITICAL_STATUS indicates immidiate attention must be payed to the node.
 */
    
    public int getNodeStatus() {
        return OKAY_STATUS;
    }
    
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
    
    public boolean handleDrop(JTree tree, TreePath dropPath, DropTargetDropEvent event) {
        return false;
    }
    
/** Called to check if a node would handle a drop if it occurred.
 * @param dropPath Indicates the location of the drop in the Tree hierarchy.
 * @param event The Drop event that is being handled.
 * @return True if the drop could be handled.  False otherwise.
 */
    
    public TreePath willHandleDrop(JTree tree, TreePath dropPath, DropTargetDragEvent event) {
        return null;
    }
    
        /** Getter for property expandDuringDrag.
     * @return Value of property expandDuringDrag.
     */
    public boolean expandDuringDrag() {
        return expandDuringDrag;
    }
    
    /** Setter for property expandDuringDrag.
     * @param expandDuringDrag New value of property expandDuringDrag.
     */
    public void setExpandDuringDrag(boolean expandDuringDrag) {
        this.expandDuringDrag = expandDuringDrag;
    }
    
    /** Called to destroy the object.  All references should be set to null.
     * Once destroy has been called, the object will not be used anymore.
     */
    public void destroy() {
        if ( children != null ) {
            Iterator i = children.iterator();
            while(i.hasNext()) {
                ((AbilityTreeNode)i.next()).destroy();
            }
            
            children.clear();
            children = null;
        }
        setTree(null);
        setParent(null);
        setModel(null);
    }
    
    /** Getter for property model.
     * @return Value of property model.
     */
    public AbilityTreeTableModel getModel() {
        return model;
    }
    
    /** Setter for property model.
     * @param model New value of property model.
     */
    public void setModel(AbilityTreeTableModel model) {
        this.model = model;
        
        if ( children != null ) {
            int index;
            for(index=0;index<children.size();index++) {
                ((AbilityTreeNode)children.get(index)).setModel(model);
            }
        }
    }
    
    public void setTree(JTree tree) {
        this.tree = tree;
        
        if ( children != null ) {
            int index;
            for(index=0;index<children.size();index++) {
                ((AbilityTreeNode)children.get(index)).setTree(tree);
            }
        }
    }

   
    
    public void insert(MutableTreeNode node, int index) {
        super.insert(node,index);
        if ( model != null ) model.nodesWereInserted(this, new int[] {index});
    }
    
    public void remove(int index) {
        TreeNode child = getChildAt(index);
        super.remove(index);
        if ( model != null ) model.nodesWereRemoved(this, new int[] {index}, new Object[] {child});
    }
    
    public void remove(MutableTreeNode child) {
        int index = getIndex(child);
        super.remove(child);
        if ( model != null ) model.nodesWereRemoved(this, new int[] {index}, new Object[] {child});
    }
    
    public void removeFromParent() {
        TreeNode oldParent = getParent();
        if ( oldParent != null ) {
            int index = oldParent.getIndex(this);
            removeFromParent();
            if ( model != null ) model.nodesWereRemoved(oldParent, new int[] {index}, new Object[] {this});
        }
    }
    
    public boolean isEditable() {
        return false;
    }
    

    
 /*   public SublistNode findSublistNode(TreeNode node, String sublistName) {
        if ( node instanceof SublistNode && isSublistNameEqual( sublistName, ((SublistNode)node).getSublist()) ){
            return (SublistNode)node;
        }
        else {
            int count, index;
            TreeNode newNode;
            count = node.getChildCount();
            for(index=0;index<count;index++) {
                newNode = findSublistNode( node.getChildAt(index), sublistName);
                if ( newNode != null ) return (SublistNode)newNode;
            }
        }
        return null;
    } */
    
    public AbilityTreeNode findNode(String name) {
        if ( name != null && name.equals(this.toString()) ) return this;
        
        AbilityTreeNode node = null;
        if ( children != null ) {
            int index = children.size() -1;
            for(; index >=0; index--) {
                if ( ( node = ((AbilityTreeNode)children.get(index)).findNode(name)) != null ) break;
            }
        }
        
        return node;
    }
    
    /** Search the tree for nodes with Statuses other then OKAY_STATUS.
     * if startIndex == 0, the node should check itself and all it's children.
     * if startIndex > 0, the node should only check it's children with startIndex or higher.
     * if startIndex == -1, the node should check it's children and then check it's parent.
     */
    public AbilityTreeNode findNextError(int startIndex, boolean checkParents) {
        if ( startIndex == 0 && this.getNodeStatus() != OKAY_STATUS ) return this;
        
        AbilityTreeNode node = null;
        int index, count;
        count = ( children != null ) ? children.size() : 0;
        index = startIndex < 0 ? 0 : startIndex;
        for(;index<count;index++) {
            node = (AbilityTreeNode)children.get(index);
            node = node.findNextError(0,false);
            if ( node != null ) break;
        }
        
        if ( checkParents && node == null && parent != null) {
           index = parent.getIndex(this);
           node = ((AbilityTreeNode)parent).findNextError(index+1, checkParents);
        }
        return node;
    }
    
     /** Search the tree for nodes with isEditable() == true.
     * if startIndex == 0, the node should check itself and all it's children.
     * if startIndex > 0, the node should only check it's children with startIndex or higher.
     * if startIndex == -1, the node should check it's children and then check it's parent.
     */
    public AbilityTreeNode findNextEditableNode(int startIndex, boolean checkParents) {
        if ( startIndex == 0 && this.isEditable() ) return this;
        
        AbilityTreeNode node = null;
        int index, count;
        count = ( children != null ) ? children.size() : 0;
        index = startIndex < 0 ? 0 : startIndex;
        for(;index<count;index++) {
            node = (AbilityTreeNode)children.get(index);
            node = node.findNextEditableNode(0,false);
            if ( node != null ) break;
        }
        
        if ( checkParents && node == null && parent != null) {
           index = parent.getIndex(this);
           node = ((AbilityTreeNode)parent).findNextEditableNode(index+1, checkParents);
        }
        return node;
    }
    
    
    public boolean isSublistNameEqual(String name1, String name2) {
        if ( name1 == name2 ) return true;
        if ( name1 != null && name1.equals(name2) ) return true;
        return false;
    }
    
    public boolean startDrag(AbilityTreeTable tree, TreePath path, DragSourceListener listener, DragGestureEvent dge) {
        return false;
    }
    
    public boolean invokeMenu(JPopupMenu popup, JTree tree, TreePath path) {
        return false;
    }
    
    /** Returns the Nodes custom input map.
     * This method should return a custom input map of action the Node can perform and has key bindings for.
     * When this node is selected, the AbilityTree will load this input map and bind the keys appropriately.
     *
     * MenuItems returned by invokeMenu which have key bindings should populate an input map and return it.
     */
    
    public InputMap getInputMap() {
        return null;
    }
    
    /** Returns the Nodes custom action map.
     * This method should return a custom action map of action the Node can perform and has key bindings for.
     * When this node is selected, the AbilityTree will load this action map and bind the keys appropriately.
     *
     * MenuItems returned by invokeMenu which have key bindings should populate an action map and return it.
     */
    
    public ActionMap getActionMap() {
        return null;
    }
    
    public boolean canCopyOrCutNode() {
        return false;
    }
    
    public void copyOrCutNode(boolean cut) {
        
    }
    
    
    public boolean canPasteData(Transferable t) {
        return false;
    }
    
    public void pasteData(Transferable t) {
        
    }
    
    /* The following set of methods are for the support of the TreeTable abilityTrees
     *
     * Nodes should implement them, as appropriate.
     */
    
    public TreeTableCellRenderer getTreeTableCellRenderer(int columnIndex) {
        return null;
    }
    
    public TreeTableCellEditor getTreeTableCellEditor(int columnIndex) {
        return null;
    }
    
    public Object getValue(int columnIndex) {
        return null;
    }
    
    public boolean isEditable(int column) {
        return false;
    }
    
    public int getColumnSpan(int column) {
        return 1;
    }
    
    /**
     * Sets the value for node <code>node</code>,
     * at column number <code>column</code>.
     */
    public void setValueAt(int column, Object aValue) {
        // This is mostly unused, however allow the node to decide...
    }
    
    /** Returns the Text for the ToolTip for the node which the cursor is currently over.
     */
    public String getToolTipText(int column) {
        if ( this.getParent() != null ) {
            return ((AbilityTreeNode)getParent()).getToolTipText( AbilityTreeTableModel.ABILITY_TREE_ABILITYCOLUMN);
        }
        
        return null;
    }
    
    /** Attempts to scroll as much of this node to visible region.
     *
     *  This will attempt to scroll the node to visible.  If the node
     *  is expanded, it will attempt to scroll as many of it's children to
     *  visible as possible.  If the display area is too small, this node
     *  will be displayed over it's children.
     *
     *  The tree must be set for this node (and all children) for this
     *  to work properly.
     */
    public void scrollToVisible() {
        if( tree != null ) {
            TreePath tp = new TreePath(this.getPath());
            if ( tree.isExpanded(tp ) && children != null) {
                for(int index = children.size() - 1; index >= 0; index--) {
                    Object child = children.get(index);
                    if ( child instanceof AbilityTreeNode ) {
                        ((AbilityTreeNode)child).scrollToVisible();
                    }
                }
            }
            
            tree.scrollPathToVisible(tp);
        }
    }
    
    protected void fireNodeStatusChanged() {
        if ( model != null ) {
            model.nodeChanged(this);
        }
        
        if ( parent instanceof AbilityTreeNode ) {
            ((AbilityTreeNode)parent).fireNodeStatusChanged();
        }
    }
    
}

