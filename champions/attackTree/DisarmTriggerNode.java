/*
 * GrabTriggerNode.java
 *
 * Created on June 5, 2002, 3:24 PM
 */

package champions.attackTree;

import champions.ActivationInfo;
import champions.Target;
import champions.exception.BattleEventException;
import champions.battleMessage.GenericSummaryMessage;

/**
 *
 * @author pnoffke
 */
public class DisarmTriggerNode extends DefaultAttackTreeNode {
    
        
    private boolean childrenBuilt = false;
    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    /** Creates new DisarmTriggerNode */
    public DisarmTriggerNode(String name) {
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
            int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup() );
            if ( tindex != -1 ) {
                target = ai.getTarget(tindex);
            }
                
            if ( target != null ) {
                int challengerStrength = 0;
                if ( target.hasStat("STR") ) {
                    /**
                     * @todo The challenger strength needs to account for Extra DC bonuses.
                     */
                    getBattleEvent().addBattleMessage(new GenericSummaryMessage(target, " does NOT have any Extra DC bonuses applied (override manually if applicable)"));
                    challengerStrength = (int)Math.round( (double)target.getCurrentStat("STR"));
                }
                int challengeeStrength = (int)Math.round( battleEvent.getDC() * 5);
                // Build the very first node:  SkillVsSkillNode
                SkillVsSkillNode node = new SkillVsSkillNode("Strength vs. Strength");
                node.setDescription("Strength");
                node.setSkillName("Strength");
                node.setChallengee(battleEvent.getSource());
                node.setChallenger(target);
                node.setChallengeeDiceCount((int) Math.round((double) challengeeStrength / 5.0));
                node.setChallengerDiceCount((int) Math.round((double) challengerStrength / 5.0));
                
                addChild(node);
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
