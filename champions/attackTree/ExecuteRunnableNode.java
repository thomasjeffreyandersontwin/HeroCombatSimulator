/*
 * ExecuteRunnableNode.java
 *
 * Created on April 30, 2002, 6:04 PM
 */

package champions.attackTree;

import champions.exception.BattleEventException;


/**
 *
 * @author  Trevor Walker
 * @version 
 */
public class ExecuteRunnableNode extends DefaultAttackTreeNode {
    
    private Runnable runnable;
    
    /** Creates new ProcessActivateAbilityNode */
    public ExecuteRunnableNode(String name, Runnable runnable) {
        this.name = name;
        this.runnable = runnable;
      //  setVisible(false);
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean activateNode = false;

        return activateNode;
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
     */
    public boolean processAdvance() throws BattleEventException {
        if ( runnable != null ) {
            runnable.run();
        }
        
        return true;
    }
    
    public void checkNodes() {
        // Do nothing here...
    }
}
