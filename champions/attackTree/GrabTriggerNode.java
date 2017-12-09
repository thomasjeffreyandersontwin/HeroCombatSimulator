/*
 * GrabTriggerNode.java
 *
 * Created on June 5, 2002, 3:24 PM
 */

package champions.attackTree;

import champions.ActivationInfo;
import champions.Target;
import champions.exception.BattleEventException;
import champions.Ability;
import champions.interfaces.AbilityIterator;
import champions.powers.maneuverEscape;
import champions.battleMessage.GenericSummaryMessage;
import champions.BattleEvent;
import champions.BattleEngine;

/**
 *
 * @author  Trevor Walker
 */
public class GrabTriggerNode extends DefaultAttackTreeNode {
    
        
    private boolean childrenBuilt = false;
    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    /** Creates new GrabTriggerNode */
    public GrabTriggerNode(String name) {
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
        
        if ( childrenBuilt == false ) {
            Target target = null;
            ActivationInfo ai = getBattleEvent().getActivationInfo();
            int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup());
            if ( tindex != -1 ) {
                target = ai.getTarget(tindex);
            }
                
            if ( target != null ) {
                // Build the very first node: EscapeTriggerNode
                EscapeTriggerNode etn = new EscapeTriggerNode("EscapeTriggerNode");
                etn.setChallengee(battleEvent.getSource());
                etn.setChallenger(target);
                etn.setChallengeeAbility(battleEvent.getAbility());
                // Initial contest is casual, with no follow-up escape attempt at full STR.
                etn.setFollowUpWithFullStrContest(false);
                
                addChild(etn);
            }
            
            childrenBuilt = true;
        }
        
        return true;
    }
    
    public void checkNodes() {
        // Don't check the children ever.
    }
    
    /** Getter for property targetReferenceNumber.
     * @return Value of property targetReferenceNumber.
     */
    public int getTargetReferenceNumber() {
        return this.targetReferenceNumber;
    }
    
    /** Setter for property targetReferenceNumber.
     * @param targetReferenceNumber New value of property targetReferenceNumber.
     */
    public void setTargetReferenceNumber(int targetReferenceNumber) {
        this.targetReferenceNumber = targetReferenceNumber;
    }
    
}
