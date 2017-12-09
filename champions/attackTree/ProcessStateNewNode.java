/*
 * ProcessActivateAbilityNode.java
 *
 * Created on April 25, 2002, 4:05 PM
 */

package champions.attackTree;

import champions.BattleEngine;
import champions.Target;
import champions.exception.BattleEventException;

/**
 *
 * @author  Trevor Walker
 * @version
 */
public class ProcessStateNewNode extends DefaultAttackTreeNode {
    
        
    private boolean childrenBuilt = false;
    
    /** Creates new ProcessActivateAbilityNode */
    public ProcessStateNewNode(String name) {
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
        
        BattleEngine.processStateNew(battleEvent);
        
                
        if ( childrenBuilt == false ) {
            // Build the very first node: Attack Description
            AttackDescriptionNode adn = new AttackDescriptionNode("Attack Description");
            adn.setTargetGroupSuffix("");
            addChild(adn);
            
            // Build node: Attack Param
            AttackParametersNode apn = new AttackParametersNode("Attack Parameters");
            apn.setTargetGroupSuffix("");
            addChild(apn);

            Target t = battleEvent.getSource();
            AttackCombatLevelsNode acln = new AttackCombatLevelsNode(t.getName() + "'s Combat Levels", t, true);
            acln.setTargetGroupSuffix("");
            addChild(acln);
            
            addChild( BattleEngine.buildPredelayNodes(battleEvent) );
            childrenBuilt = true;
        }
        
        return true;
    }
    
    public void checkNodes() {
        // Don't check the children ever.
    }
}
