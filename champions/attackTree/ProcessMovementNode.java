/*
 * ProcessActivateAbilityNode.java
 *
 * Created on April 25, 2002, 4:05 PM
 */

package champions.attackTree;

import champions.BattleEngine;
import champions.exception.BattleEventException;

/**
 *
 * @author  Trevor Walker
 * @version
 */
public class ProcessMovementNode extends DefaultAttackTreeNode {
    
    
    /** Creates new ProcessActivateAbilityNode */
    public ProcessMovementNode(String name) {
        this.name = name;
        
        setVisible(false);
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
        
        BattleEngine.processMovement(battleEvent);
        
        return true;
    }
    
    public void checkNodes() {
        // Don't check the children ever.
    }
}
