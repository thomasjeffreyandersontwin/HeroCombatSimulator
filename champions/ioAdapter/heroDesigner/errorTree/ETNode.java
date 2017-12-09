/*
 * PADTreeTableNode.java
 *
 * Created on February 28, 2002, 10:12 AM
 */

package champions.ioAdapter.heroDesigner.errorTree;

import champions.Target;
import javax.swing.tree.MutableTreeNode;
import treeTable.DefaultTreeTableNode;



/**
 *
 * @author  twalker
 * @version 
 */
public class ETNode extends DefaultTreeTableNode {

    private boolean expandedByDefault = true;
    
    
    /** Creates new PADTreeTableNode */
    public ETNode() {
    }
    
    public ETNode(String name) {
        setUserObject(name);
    }
    

    public boolean isExpandedByDefault() {
        return expandedByDefault;
    }

    public void setExpandedByDefault(boolean expandedByDefault) {
        this.expandedByDefault = expandedByDefault;
    }
}
