/*
 * SingleAttackNode.java
 *
 * Created on November 7, 2001, 10:39 PM
 */

package champions.attackTree;


import champions.Ability;
import champions.ActivationInfo;
import champions.BattleEngine;
import champions.BattleEvent;
import champions.Target;
import champions.battleMessage.BattleMessageGroup;
import champions.battleMessage.SingleAttackMessageGroup;
import champions.exception.BattleEventException;

/**
 *
 * @author  twalker
 * @version
 */
public class SingleAttackNode extends DefaultAttackTreeNode implements BattleMessageGroupProvider {
    
    /** Holds value of property selfTargeting. */
    private boolean selfTargeting;
    
    /** Hold the primary Target number, if this is a primary target. */
    private int primaryTargetNumber = -1;
    
    private SingleAttackMessageGroup singleAttackMessageGroup;
    
    /** Creates new TestAttackTreeNode */
    public SingleAttackNode(String name) {
        this.name = name;
        setVisible(false);
    }
    
    public boolean activateNode(boolean manualOverride) {
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        
        // When this is activated, make sure the battleEvent is correctly prepared.
        prepareBattleEvent();
        
        // The root node should never accept active node status, since
        // It isn't really a real node.
        return true;
    }
 
    
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        
        AttackTreeNode nextNode = null;
        // Compare the activeChild to the possible children, to determine what comes next.
        if ( activeChild != null && activeChild.getName() == null ) {
            // This is probably an error
            int index = children.indexOf(activeChild);
            System.out.println("Null Node name for child " + Integer.toString(index) + " of Parent " + this);
            nextNode = null;
        }
        else {
            String previousNodeName = (activeChild == null ) ? null : activeChild.getName();
            String nextNodeName = nextNodeName(previousNodeName);
            if ( nextNodeName != null ) {
                if ( nextNodeName.equals("Single Target")) {
                    SingleTargetNode node = new SingleTargetNode(nextNodeName);
                    node.setTargetReferenceNumber(0); // Indicate that this is the primary target of the Group.
                    if(battleEvent.getActivationInfo().getTargetIndex(0, getTargetGroup())==-1) {
                    	//jeff change
                    	node.setTargetReferenceNumber(1); 
                    }
                    node.setPrimaryTargetNumber(primaryTargetNumber);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Effect")) {
                    EffectNode node = new EffectNode(nextNodeName);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Close Group Node")) {
                    CloseMessageGroupNode node = new CloseMessageGroupNode(nextNodeName, this);
                    nextNode = node;
                }
                
                
            }
        }
        return nextNode;
    }
    
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        
        if ( previousNodeName == null ) {
            if ( isSelfTargeting() == false ) {
                nextNodeName = "Single Target";
            }
            else {
                nextNodeName = "Effect";
            }
        }
        else if ( previousNodeName.equals("Single Target") ) {
            BattleEvent be = getBattleEvent();
            ActivationInfo ai = be.getActivationInfo();
            int tindex;
            
            boolean somethingHit = ai.getTargetGroupHasHitTargets(getTargetGroup());
            
            if ( somethingHit == true ) {
                nextNodeName = "Effect";
            }
        }
        
        if ( nextNodeName == null && previousNodeName.equals("Close Group Node") == false ) {
            nextNodeName = "Close Group Node";
        }
        return nextNodeName;
    }
    
 /*   public void nodeChanged(AttackTreeNode changedNode) {
        if ( changedNode.getName().equals("ToHit") ) {
            checkNodes();
            
            AttackTreeNode atn = findNode("Effect", false);
            if ( atn != null ) atn.nodeChanged(changedNode);
        }
        
        //super.nodeChanged(changedNode);
    } */
    
    public void prepareBattleEvent() {
        boolean requiresMessageGroup = true;
        
        String newGroup = getTargetGroup();
        // Create the group for this attack
        ActivationInfo ai = getBattleEvent().getActivationInfo();
        
        int tgindex = ai.addTargetGroup(newGroup);
        ai.setKnockbackGroup(tgindex, "KB");
        
        // Reset the DCCalculated Flag, so things get recalculated appropriately.
      //  ai.addIndexed(tgindex, "TargetGroup", "DCCALCULATED", "FALSE", true);
        
       // ai.setTargetGroupDCModifier(newGroup, 0 );
        
        try {
            // Add all attack information which depends on the AttackParameterPanel
            Target source = getBattleEvent().getSource();
            BattleEngine.resetDamageCalculation(battleEvent, newGroup);
            BattleEngine.calculateDamage(battleEvent, battleEvent.getSource(), newGroup);
        }
        catch (BattleEventException bee) {
            
        }
        
        // Check if this is a Target Self Ability
        Ability ability = ai.getAbility();
        Target source = ai.getSource();
        if ( ability.isTargetSelf() == true ) {
            
            int tindex = ai.addTarget(source, newGroup, 0);
            
            ai.setTargetFixed(tindex, true, "The ability is a Self Targetting Only.");
            
            String explination = source.getName() + " is applying this ability against themself and does not require a ToHit roll.";
            
            ai.setTargetHitOverride(tindex, true, null, explination);
            
            this.setSelfTargeting(true);
            
            requiresMessageGroup = false;
        }
        else {
           this.setSelfTargeting(false); 
        
//            if ( battleEvent.getAbility().getPower() != null ) {
//                battleEvent.getAbility().getPower().adjustTarget(battleEvent, battleEvent.getSource(), 0, getTargetGroup(), primaryTargetNumber);
//            }
//
//            if ( battleEvent.getManeuver() != null && battleEvent.getManeuver().getPower() != null ) {
//                battleEvent.getManeuver().getPower().adjustTarget(battleEvent, battleEvent.getSource(), 0, getTargetGroup(), primaryTargetNumber);
//            }
//
//            // Call the adjustTarget() method to allow parent nodes to
//            // fix up the target if necessary.
//            adjustTarget(battleEvent, battleEvent.getSource(), 0, getTargetGroup(), primaryTargetNumber);
        }
        
        if ( requiresMessageGroup ) {
            singleAttackMessageGroup = new SingleAttackMessageGroup(source);
            battleEvent.openMessageGroup(singleAttackMessageGroup);
        }
        else {
            singleAttackMessageGroup = null;
        }
    }
    
    /** Getter for property selfTargeting.
     * @return Value of property selfTargeting.
     *
     */
    public boolean isSelfTargeting() {
        return this.selfTargeting;
    }
    
    /** Setter for property selfTargeting.
     * @param selfTargeting New value of property selfTargeting.
     *
     */
    public void setSelfTargeting(boolean selfTargeting) {
        this.selfTargeting = selfTargeting;
    }
    
    /** Getter for property primaryTargetNumber.
     * @return Value of property primaryTargetNumber.
     *
     */
    public int getPrimaryTargetNumber() {
        return primaryTargetNumber;
    }    
    
    /** Setter for property primaryTargetNumber.
     * @param primaryTargetNumber New value of property primaryTargetNumber.
     *
     */
    public void setPrimaryTargetNumber(int primaryTargetNumber) {
        this.primaryTargetNumber = primaryTargetNumber;
    }
    
    public boolean isPrimaryTargetNode() {
        return this.primaryTargetNumber != -1;
    }

    public BattleMessageGroup getBattleMessageGroup() {
        return singleAttackMessageGroup;
    }
    
}
