/*
 * PADTreeTableNode.java
 *
 * Created on February 28, 2002, 10:12 AM
 */

package champions.targetTree;

import champions.Target;
import javax.swing.tree.MutableTreeNode;
import treeTable.DefaultTreeTableNode;
import treeTable.TreeTableModel;



/**
 *
 * @author  twalker
 * @version 
 */
public class TTNode extends DefaultTreeTableNode {

    private boolean forceFolder = false;
    
    private boolean expandedByDefault = true;
    
    
    /** Creates new PADTreeTableNode */
    public TTNode() {
    }
    
    public TTNode(String name) {
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

    public boolean isExpandedByDefault() {
        return expandedByDefault;
    }

    public void setExpandedByDefault(boolean expandedByDefault) {
        this.expandedByDefault = expandedByDefault;
    }

    public void setModel(TreeTableModel model) {
        if ( this.model != model ) {
            
            this.model = model;
            
            if ( model != null ) {
                buildNode();
            }
            
            for(int i = 0; i < getChildCount(); i++) {
                if ( getChildAt(i) instanceof TTNode ) {
                    ((TTNode)getChildAt(i)).setModel(model);
                }
            }
        }
    }
    
    protected void buildNode() {
        
    }
    
}
