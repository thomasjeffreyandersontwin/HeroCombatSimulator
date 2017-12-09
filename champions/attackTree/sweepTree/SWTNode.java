/*
 * PADTreeTableNode.java
 *
 * Created on February 28, 2002, 10:12 AM
 */

package champions.attackTree.sweepTree;

import java.awt.datatransfer.DataFlavor;
import javax.swing.tree.MutableTreeNode;
import treeTable.DefaultTreeTableNode;
import champions.Ability;




/**
 *
 * @author  twalker
 * @version 
 */
public class SWTNode extends DefaultTreeTableNode {

    protected static DataFlavor abilityFlavor;
    
    private boolean forceFolder = false;
    /** Creates new PADTreeTableNode */
    public SWTNode() {
        setupFlavors();
    }
    
    public void setupFlavors() {
        try {
            if (abilityFlavor == null ) abilityFlavor = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType + "; class=" + Ability.class.getName() );
        }
        catch ( Exception e) {
            System.out.println(e);
        }
    }
    
    public SWTNode(String name) {
        setUserObject(name);
    }

    public boolean isLeaf() {
        return !forceFolder && super.isLeaf();
    }
    
    public boolean getAllowsChildren() {
        return forceFolder || super.getAllowsChildren();
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
    
}
