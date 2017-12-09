/*
 * PADTreeTableNode.java
 *
 * Created on February 28, 2002, 10:12 AM
 */

package champions.abilityTree2;

import champions.Roster;
import champions.Target;
import tjava.Filter;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import treeTable.DefaultTreeTableNode;

/**
 * 
 * @author  twalker
 * @version 
 */
public class ATNode extends DefaultTreeTableNode {

    /** Holds the Ability Filter. */
    protected Filter<Object> nodeFilter;
    
    /** Indicates that empty sublists should be pruned. */
    protected boolean pruned;
    
    /** Indicates that this node requires an update. */
    protected boolean rebuildRequired = false;
    
    private boolean forceFolder = false;
    
    private boolean expandedByDefault = true;
    
    protected ATNodeFactory nodeFactory = null;
    
    /** Creates new PADTreeTableNode */
    public ATNode(ATNodeFactory nodeFactory, Filter<Object> nodeFilter, boolean pruned) {
        this.nodeFactory = nodeFactory;
        this.nodeFilter = nodeFilter;
        this.pruned = pruned;
    }
    
    public ATNode(String name, ATNodeFactory nodeFactory, Filter<Object> nodeFilter, boolean pruned) {
        this(nodeFactory, nodeFilter, pruned);
        setUserObject(name);
    }

    public boolean isLeaf() {
        return !forceFolder && super.isLeaf();
    }
    
    public boolean getAllowsChildren() {
        return forceFolder || super.getAllowsChildren();
    }
    /** Returns the Target associated with node.
     *
     * This should return whatever the node represents.
     *
     * @return null if no Target is associated, such as in the case of a folder.
     */
    public Target getTarget() {
        return null;
    }
    
    public void add(MutableTreeNode node) {
        super.add(node);
        
        if ( node instanceof DefaultTreeTableNode ) {
            ((DefaultTreeTableNode)node).setModel( getModel() );
            ((DefaultTreeTableNode)node).setTree( getTree() );
        }
    }
    
    /** Getter for property forceFolder.
     * @return Value of property forceFolder.
     *
     */
    public boolean isForceFolder() {
        return forceFolder;
    }    
    
    /** Setter for property forceFolder.
     * @param forceFolder New value of property forceFolder.
     *
     */
    public void setForceFolder(boolean forceFolder) {
        this.forceFolder = forceFolder;
    }
    
    /** Builds the ATNode according to the subclass.
     *
     *  This method should completely rebuild the node.  
     *
     *  Note, in general this should not be called directly, except
     *  when first constructing the node.  Use rebuildNode instead.
     */
    protected void buildNode() {
        
    }
    
    /** Getter for property pruned.
     * @return Value of property pruned.
     *
     */
    public boolean isPruned() {
        return pruned;
    }
    
    /** Setter for property pruned.
     * @param pruned New value of property pruned.
     *
     */
    public void setPruned(boolean pruned) {
        if ( this.pruned != pruned ) {
            this.pruned = pruned;
            rebuildNode();
        }
    }
    
    /** Getter for property abilityFilter.
     * @return Value of property abilityFilter.
     *
     */
    public Filter<Object> getNodeFilter() {
        return nodeFilter;
    }
    
    /** Setter for property abilityFilter.
     * @param abilityFilter New value of property abilityFilter.
     *
     */
    public void setNodeFilter(Filter<Object> nodeFilter) {
        if ( this.nodeFilter != nodeFilter ) {
            this.nodeFilter = nodeFilter;
            rebuildNode();
        }
    }
    
    public void setName(String name) {
        setUserObject( name );
    }
    
    /** Tells the renderer what font the node thinks is appropriate for column.
     *
     * Depending on the renderer, this hint might be used or ignored.
     * If null is returned, the default renderer font will be used.
     */
    public Font getColumnFont(int column) {
        return null;
    }
    
    /** Tells the Node that it should trigger it's default action. 
     *
     */
    public void triggerDefaultAction(MouseEvent mouseEvent) {
        
    }
    
    /** Returns the Tool text for the node. */
    public String getToolTipText(int column) {
        return null;
    }
//    
//    /** Returns whether the node should be expanded by default.
//     *
//     */
//    public boolean expandByDefault() {
//        return true;
//    }
    
    /** Indicates the node is enabled.
     *
     * This dictates both the renderer look and if actions are taken when 
     * the editor is clicked on...
     */
    public boolean isEnabled() {
        return true;
    }
    
    /** Tells the node to update it's information and the information of it children.
     *
     * This typically will involve only cosmetic changes and not structural 
     * changes.  Use it to update cached values that need to change after 
     * ability's have been activated/deactivate or the battleEngine has done
     * some processing.
     *
     * A node can return true from updateTree to indicate that it is not longer
     * a valid node.  When this occurs, the default behaviour it to remove that
     * child.
     */
    public void updateTree() {
        // Just try updating the children
        int count = getChildCount();
        for(int i = 0; i < count; i++) {
            ATNode node = (ATNode)getChildAt(i);
            node.updateTree();
        }
    }
    
    /** Tells the node to rebuild itself after major structural changes.
     *
     *  Unlike updateNode, this method should not recursively rebuild its
     *  children.
     *
     *  This method should always check to set if the node is currently 
     *  updatable.  If it isn't, it should queue the update.
     *
     * @return True if  the node was rebuilt
     */
    public boolean rebuildNode() {
        boolean result = false;
        if ( isUpdatable() ) {
            buildNode();
            
            setRebuildRequired(false);
            
            result = true;
        }
        else {
            setRebuildRequired(true);
        }
        
        return result;
    }
    
    /** Tells the node to rebuild itself and it child, if necessary.
     *
     *  This will iterate through the tree, calling rebuild node 
     *  on all nodes currently requiring a rebuild.
     */
    public boolean rebuildTree() {
        boolean result = false;
        
        if ( isRebuildRequired() ) {
            if ( rebuildNode() ) result = true;
        }
        
        int count = getChildCount();
        for(int i = 0; i < count; i++) {
            ATNode node = (ATNode)getChildAt(i);
            if ( node.rebuildTree() ) result = true;
        }
        return result;
    }
    
    /** Returns if the node is currently updatable.
     * 
     *  This will depend upon the abilityTree if it is set.  Otherwise,
     *  the node will always be updabable.
     *
     */
    public boolean isUpdatable() {
        if ( getTree() != null ) {
            return getTree().isUpdatable();
        }
        return true;
    }
    
    /** Queues an update.
     *
     *
     */
    public void setRebuildRequired(boolean rebuildRequired) {
        this.rebuildRequired = rebuildRequired;
    }
    
    /** Returns whether an update is required.
     *
     */
    public boolean isRebuildRequired() {
        return rebuildRequired;
    }
    
    /** Returns the ATAbilityTree this node is associated with.
     * 
     * Override the default getTree return type to be ATAbilityTree.
     */
    public ATTree getTree() {
        return (ATTree)tree;
    }

    public boolean isExpandedByDefault() {
        return expandedByDefault;
    }

    public void setExpandedByDefault(boolean expandedByDefault) {
        this.expandedByDefault = expandedByDefault;
    }
   
    public Color getBackgroundColor() {
        return null;
    }
    
    public void handleDoubleClick(ATTree tree, TreePath targetPath) {
    	ATNode node = (ATNode) targetPath.getLastPathComponent();
    	Target t = node.getTarget();
    	Roster r=null;
    	try{
    		if(targetPath.getLastPathComponent().getClass()==ATRosterNode.class) {
    			ATRosterNode rnode =(ATRosterNode) targetPath.getLastPathComponent();
    			r= rnode.getRoster();
    		}
    	}catch(Exception e){}
    	
    	VirtualDesktop.MessageExporter.exportEvent("Node Double CLick", t,r );
    	
        
    }

    public void nodeWillExpand() {
        
    }
    

}
