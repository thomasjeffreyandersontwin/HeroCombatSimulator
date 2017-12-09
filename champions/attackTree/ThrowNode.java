/*
 * AttackTypeNode.java
 *
 * Created on November 7, 2001, 10:23 PM
 */

package champions.attackTree;

import champions.*;
import champions.interfaces.AbilityIterator;

/**
 *
 * @author  twalker
 * @version
 */
public class ThrowNode extends DefaultAttackTreeNode {
    
    /** Creates new TestAttackTreeNode */
    public ThrowNode(String name) {
        this.name = name;
        setVisible(false);
    }
    
    public boolean activateNode(boolean manualOverride) {
        // This should actually check to make sure that the next node is correct give the
        // parameters configured for the ability activation...
        

        
        
        
        // The root node should never accept active node status, since
        // It isn't really a real node.
        return false;
    }
    
    public boolean processAdvance() {
        boolean advance = true;
    
            // setup the attack group for the throw
        ActivationInfo ai = battleEvent.getActivationInfo();
        ai.addTargetGroup(".THROW");
        
        ChampionsUtilities.calculateDCs(battleEvent, ".THROW");
        double dc = battleEvent.getDC();
        
        
        ai.add("ActivationInfo.THROWDC", new Double(dc), true);
        
        return advance;
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
                if ( nextNodeName.equals("Single Attack")) {
                    SingleAttackNode node = new SingleAttackNode(nextNodeName);
                    node.setPrimaryTargetNumber(1);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("AreaEffect")) {
                    AreaEffectAttackNode node = new AreaEffectAttackNode(nextNodeName);
                    node.setPrimaryTargetNumber(1);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Thrown Object Info")) {
                    ThrowEffectNode node = new ThrowEffectNode(nextNodeName);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Throw Breakfall Roll")) {
                    ThrowBreakfallNode node = new ThrowBreakfallNode(nextNodeName);
                    nextNode = node;
                }
            }
        }
        return nextNode;
    }
    
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        
        BattleEvent be = getBattleEvent();
        ActivationInfo ai = be.getActivationInfo();
        if ( previousNodeName == null ) {            
//            if ( be.getBooleanValue( "BattleEvent.PARTOFGRAB")) {
//                if ( ai.getValue("ActivationInfo.THROWNOBJECT") != null ) {
//                    nextNodeName = "Thrown Object Info";
//                }
//            }
//            else 
                if ( ai.getBooleanValue( "ActivationInfo.THROWISAOE" ) ) {
                nextNodeName = "AreaEffect";
            }
            else {
                nextNodeName = "Single Attack";
            }
        }
        else if ( previousNodeName.equals("Single Attack") || previousNodeName.equals("AreaEffect")){
            if ( ai.getValue("ActivationInfo.THROWNOBJECT") != null ) {
                nextNodeName = "Thrown Object Info";
            }
        }
        else if ( previousNodeName.equals("Thrown Object Info")) {
            Target target = (Target)ai.getValue("ActivationInfo.THROWNOBJECT");
            if ( target != null && getBreakfallRoll(target) != -1 ) {
                nextNodeName = "Throw Breakfall Roll";
            }
            else {
                nextNodeName = "Thrown Object Damage";
            }
        }
        else if ( previousNodeName.equals("Throw Breakfall Roll")){
            nextNodeName = "Thrown Object Damage";
        }
            
        return nextNodeName;
    }
    
    /** Return breakfall roll of target, -1 if target doesn't have breakfall
     *
     */
    protected int getBreakfallRoll(Target target) {
        
        Ability Breakfall = PADRoster.getSharedAbilityInstance("Breakfall");
        
        ActivationInfo ai = battleEvent.getActivationInfo();
        //Target source = Battle.currentBattle.getActiveTarget();
        
        AbilityIterator aiter = target.getSkills();
        
        
        while (aiter.hasNext() ) { // Check to see if there is another item
            // Rip next ability from the iterator.
            // This a is guaranteed to have a power which is actually a skill
            // since we used the getSkills.  If we use getAbilities() we will
            // actually get both skills and powers.
            Ability a = aiter.nextAbility();
            if ( a.isEnabled(target,false) && a.equals(Breakfall)) {
                int targetbaseroll = a.getSkillRoll(target);
                boolean crammed= (Boolean)a.getValue("Power.CRAMMED");
                if (crammed ) {
                    return 8;
                }
                else {
                    return targetbaseroll;
                }
            }
            
        }
        return -1;
    }
}
