/*
 * ProcessActivateAbilityNode.java
 *
 * Created on April 25, 2002, 4:05 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.BattleEngine;
import champions.exception.BattleEventException;

/**
 *
 * @author  Trevor Walker
 * @version
 */
public class ProcessStateContinuing extends DefaultAttackTreeNode {
    
    private boolean childrenBuilt = false;
    
    /** Creates new ProcessActivateAbilityNode */
    public ProcessStateContinuing(String name) {
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
        
        BattleEngine.processStateContinuing(battleEvent);
        
        // Run the Preactivation for both the Ability and the Maneuver
        if ( childrenBuilt == false ) {
            addChild( BattleEngine.buildPreactivationNodes(battleEvent, battleEvent.getAbility() ) );
            
            Ability maneuver = battleEvent.getManeuver();
            if ( maneuver != null ) {
                addChild( BattleEngine.buildPreactivationNodes(battleEvent, maneuver ) );
            }
            
            childrenBuilt = true;
        }
        
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
        // Just leave these nodes alone.
    }
    
}
