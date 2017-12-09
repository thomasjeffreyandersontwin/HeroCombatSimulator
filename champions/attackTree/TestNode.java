/*
 * TestAttackTreeNode.java
 *
 * Created on October 29, 2001, 6:09 PM
 */

package champions.attackTree;

/**
 *
 * @author  twalker
 * @version 
 */
public class TestNode extends DefaultAttackTreeNode {

    /** Creates new TestAttackTreeNode */
    public TestNode(String name, AttackTreeModel model) {
        setModel(model);
        setName(name);
    }
    
    public AttackTreeNode advanceNode(AttackTreeNode activeChild) {
        AttackTreeNode nextNode = null;
        
        if ( activeChild == this ) {
            if ( children != null && children.size() != 0 ) {
                nextNode = (AttackTreeNode)children.get(0);
            }
        }        
        else if ( children != null ) {
            int position = children.indexOf(activeChild);
            if ( position != -1 ) {
                if (position + 1 < children.size() ) {
                    nextNode = (AttackTreeNode)children.get(position + 1);
                }
            }
            else if ( children.size() > 0 ) {
                nextNode =(AttackTreeNode)children.get(0);
            }
        }
        System.out.println("Node " + name + "->" + nextNode);
        return nextNode;
    }
    
    public boolean activateNode(boolean manualOverride) {
        System.out.println("Node " + name + " activated.");
        return manualOverride;
    }

}
