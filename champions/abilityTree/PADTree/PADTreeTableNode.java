/*
 * PADTreeTableNode.java
 *
 * Created on February 28, 2002, 10:12 AM
 */

package champions.abilityTree.PADTree;

import champions.abilityTree.*;
import java.awt.event.KeyEvent;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import treeTable.DefaultTreeTableNode;


/**
 *
 * @author  twalker
 * @version 
 */
public class PADTreeTableNode extends DefaultTreeTableNode {

    /** Creates new PADTreeTableNode */
    public PADTreeTableNode() {
    }
    
    public void handleDoubleClick(champions.abilityTree.PADTree.PADTreeTable padTree, AbilityTreeTable tree, TreePath abilityPath) {
        
    }
    
    public void handleKeyTyped(KeyEvent keyEvent, champions.abilityTree.PADTree.PADTreeTable padTree, AbilityTreeTable tree, TreePath abilityPath) {
        if ( keyEvent.getKeyChar() == '\n' ) {
            handleDoubleClick(padTree, tree, abilityPath);
        }
    }
    
    /** Returns the PAD  associated with node.
     *
     * This should return a new instance of whatever the node represents.  
     * Typically, these will be abilities, advantages, limitations, special effects,
     * or special parameter objects.
     *
     * @return null if no PAD is associated, such as in the case of a folder.
     */
    public Object getPAD() {
        return null;
    }
    
    public void add(MutableTreeNode node) {
        super.add(node);
        
        if ( node instanceof DefaultTreeTableNode ) {
            ((DefaultTreeTableNode)node).setModel( getModel() );
            ((DefaultTreeTableNode)node).setTree( getTree() );
        }
    }
}
