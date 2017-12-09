/*
 * KnockbackEffectRollNode.java
 *
 * Created on November 8, 2001, 12:01 PM
 */

package champions.attackTree;

import champions.*;
import champions.battleMessage.DiceRollMessage;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version
 */
public class KnockbackEffectRollNode extends DefaultAttackTreeNode {
    
    /** Holds value of property knockbackGroup. */
    private String knockbackGroup;
    
    /** Holds value of property target. */
    private Target target;
    
    /**
     * Creates new KnockbackEffectRollNode
     */
    public KnockbackEffectRollNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.diceIcon");
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        if ( manualOverride || nodeRequiresInput() ) {
            KnockbackEffectRollPanel p = KnockbackEffectRollPanel.getDefaultPanel(getBattleEvent(), getTarget(), getKnockbackGroup());
            attackTreePanel.showInputPanel(this,p);
            attackTreePanel.setInstructions("Adjust the Knockback Roll Modifiers and enter Knockback Roll for " + getTarget().getName());
            
            acceptActivation = true;
        }
        
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
/*    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
 
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
                if ( nextNodeName.equals("Breakfall Roll") ) {
                    // Build the very first node: Attack Param
                    TargetBreakfallNode node = new TargetBreakfallNode("Breakfall Roll");
                    //TargetSkillRollNode = node;
                    //node.setTargetReferenceNumber(targetReferenceNumber);
                    //node.setTargetGroupSuffix("");
                    nextNode = node;
                }
            }
        }
        return nextNode;
    }
 
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        String getsvs = battleEvent.getActivationInfo().getStringValue( "Attack.SvS" );
        String getstate = battleEvent.getActivationInfo().getStringValue( "ActivationInfo.STATE" );
        //Target target = Battle.currentBattle.getActiveTarget();
        Target target = getTarget();
        Ability Breakfall = PADRoster.getPowerInstance("Breakfall");
 
        AbilityIterator aiter = target.getSkills();
 
        while (aiter.hasNext() ) { // Check to see if there is another item
            // Rip next ability from the iterator.
            // This a is guaranteed to have a power which is actually a skill
            // since we used the getSkills.  If we use getAbilities() we will
            // actually get both skills and powers.
            Ability a = aiter.nextAbility();
            if ( previousNodeName == null ) {
                if ( a.equals(Breakfall) && !target.isUnconscious() && !target.isStunned() ) {
 
                    nextNodeName = "Breakfall Roll";
                }
            }
 
        }
 
 
    return nextNodeName;
}
 */
    
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        
        AttackTreeNode nextNode = null;
        
        // AttackParameter has no children...
        
        return nextNode;
    }
    
    private boolean nodeRequiresInput() {
        boolean requiresInput = false;
        
        // Check to make sure that the Knockback Roll has been made...
        int kbindex = getBattleEvent().getKnockbackIndex(getTarget(), getKnockbackGroup());
        Dice dice = getBattleEvent().getKnockbackAmountRoll(kbindex);
        
        if ( dice == null ) requiresInput = true;
        
        
        return requiresInput && getBattleEvent().getSource().getBooleanProfileOption("SHOW_KNOCKBACK_AMOUNT_ROLL_PANEL");
    }
    
    
/*    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
 
        AttackTreeNode nextNode = null;
 
        // AttackParameter has no children...
 
        return nextNode;
    }
 */
    public boolean processAdvance() {
        // Calculate the Actual knockback before leaving the node.
        
        // First Gaurantee the die rolls where made in case the panel was bypassed...
        int kbindex = battleEvent.getKnockbackIndex(target, knockbackGroup);
        Dice d = battleEvent.getKnockbackAmountRoll(kbindex);
        
        KnockbackModifiersList kml = battleEvent.getKnockbackModifiersList(kbindex);
        int rollAmount = kml.getKnockbackRoll();
        
        if ( d == null ) {
            d = new Dice(rollAmount,true);
            battleEvent.setKnockbackAmountRoll(kbindex,d);
        }
        
        String msg = battleEvent.getSource().getName() + " rolled for " + d.getStun().toString() + " on " + Integer.toString(rollAmount) + "d6 for Knockback Distance versus " + target.getName() + ".";
        //battleEvent.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( battleEvent.getSource().getName() + " rolled for " + d.getStun().toString() + " on " + Integer.toString(rollAmount) + "d6 for Knockback Distance versus " + target.getName(), BattleEvent.MSG_DICE));
        battleEvent.addBattleMessage( new DiceRollMessage(battleEvent.getSource(), msg));
        
        int originalDistance = battleEvent.getKnockbackDistance(kbindex);
        int originalAmount = battleEvent.getKnockbackAmount(kbindex);
        boolean originalKnockdown = battleEvent.isKnockedDownPossible(kbindex);
        
        BattleEngine.calculateKnockback(getBattleEvent(), getTarget(), getKnockbackGroup());
        
        int newDistance = battleEvent.getKnockbackDistance(kbindex);
        int newAmount = battleEvent.getKnockbackAmount(kbindex);
        boolean newKnockdown = battleEvent.isKnockedDownPossible(kbindex);
        
        if(originalDistance != newDistance || originalAmount != newAmount || originalKnockdown != newKnockdown) {
            battleEvent.setKnockbackEffect(kbindex, null);
        }
        
        return true;
    }
    
    /** Getter for property knockbackGroup.
     * @return Value of property knockbackGroup.
     */
    public String getKnockbackGroup() {
        return knockbackGroup;
    }
    
    /** Setter for property knockbackGroup.
     * @param knockbackGroup New value of property knockbackGroup.
     */
    public void setKnockbackGroup(String knockbackGroup) {
        this.knockbackGroup = knockbackGroup;
    }
    
    /** Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        return target;
    }
    
    /** Setter for property target.
     * @param target New value of property target.
     */
    public void setTarget(Target target) {
        this.target = target;
    }
    
    public String getAutoBypassOption() {
        return "SHOW_KNOCKBACK_AMOUNT_ROLL_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return getTarget();
    }
}
