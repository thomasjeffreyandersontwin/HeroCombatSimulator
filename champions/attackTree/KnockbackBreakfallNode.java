/*
 * KnockbackNode.java
 *
 * Created on November 8, 2001, 11:55 AM
 */

package champions.attackTree;

import champions.Ability;
import champions.ActivationInfo;
import champions.PADRoster;
import champions.SkillBasedSkillRollInfo;
import champions.SkillRollInfo;
import champions.Target;
import champions.genericModifiers.IncrementGenericModifier;
import champions.genericModifiers.SkillModifierList;
import champions.interfaces.AbilityIterator;

/**
 *
 * @author  twalker
 * @version
 */
public class KnockbackBreakfallNode extends DefaultAttackTreeNode {
    
    /** Holds value of property knockbackGroup. */
    private String knockbackGroup;
    
    private Target target;
    
    /** Creates new KnockbackNode */
    public KnockbackBreakfallNode(String name) {
        this.name = name;
        setVisible(false);
    }
    
    public boolean activateNode(boolean manualOverride) {
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        
        SkillRollInfo sri = battleEvent.getSkillRollInfo("BreakfallRoll", getTargetGroup());
        
        if ( sri == null ) {
            Ability breakfall = KnockbackTargetNode.getBreakfallAbility(target);
            
            sri = new SkillBasedSkillRollInfo(target, breakfall);
            
            battleEvent.addSkillRoll("BreakfallRoll", getTargetGroup(), sri);
        }
        
        SkillModifierList sml = sri.getModifierList();
        
        if ( sml == null ) {
            sml = sri.createModifierList();
        }
        
        IncrementGenericModifier kbModifier = (IncrementGenericModifier)sml.getGenericModifier("Knockback Penalty");
        
        if ( kbModifier == null ) {
            kbModifier = new IncrementGenericModifier("Knockback Penalty", 0, false);
            sml.addGenericModifier(kbModifier);
        }
        
        int kbindex = battleEvent.getKnockbackIndex(getTarget(), getKnockbackGroup());
        int distance = battleEvent.getKnockbackDistance(kbindex);
        int amount = battleEvent.getKnockbackAmount(kbindex);
        
        kbModifier.setAmount( -1 * distance / 2);
        
        //updateChildren();
        // The root node should never accept active node status, since
        // It isn't really a real node.
        return true;
    }
    
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        // This nodes is built all at once using the updateChildren method.
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
                    SkillRollNode2 node = new SkillRollNode2(nextNodeName);
                    node.setSkillRollName("BreakfallRoll");
                    node.setPrintOutcome(false);
                    
                    nextNode = node;
                }
            }
        }
        
        return nextNode;
    }
    
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
    
        if ( previousNodeName == null ) {
            nextNodeName = "Breakfall Roll";
        }
        
        return nextNodeName;
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
    
    public Target getTarget() {
        return target;
    }
    
    public void setTarget(Target target) {
        this.target = target;
    }
    
//    public Ability getBreakfallAbility() {
//        
//        Ability breakfall = PADRoster.getSharedAbilityInstance("Breakfall");
//        
//        ActivationInfo ai = battleEvent.getActivationInfo();
//        //Target source = Battle.currentBattle.getActiveTarget();
//        
//        AbilityIterator aiter = target.getSkills();
//        
//        while (aiter.hasNext() ) { // Check to see if there is another item
//            // Rip next ability from the iterator.
//            // This a is guaranteed to have a power which is actually a skill
//            // since we used the getSkills.  If we use getAbilities() we will
//            // actually get both skills and powers.
//            Ability a = aiter.nextAbility();
//            if ( a.isEnabled(target,false) && a.equals(breakfall)) {
//                return a;
//            }
//            
//        }
//        return null;
//    }
    
}
