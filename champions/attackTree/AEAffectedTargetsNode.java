/*
 * AEAffectedTargetsNode.java
 *
 * Created on December 28, 2001, 2:02 PM
 */

package champions.attackTree;

import champions.*;
import champions.exception.BattleEventException;

/**
 *
 * @author  twalker
 * @version
 */
public class AEAffectedTargetsNode extends DefaultAttackTreeNode {
	public static AEAffectedTargetsNode AENode;
    /** Creates new AEAffectedTargetsNode */
    public AEAffectedTargetsNode(String name) {
        this.name = name;
        setVisible(true);
        AENode = this;
    }
    
    public boolean activateNode(boolean manualOverride) {
        prepareBattleEvent();
        
        return false;
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
                if ( nextNodeName.equals("AESelectiveTargets")) {
                    AESelectiveTargetsNode node = new AESelectiveTargetsNode("AESelectiveTargets");
                    nextNode = node;
                }
                else if ( nextNodeName.equals("AENonSelectiveTargets")) {
                    AENonSelectiveTargetsNode node = new AENonSelectiveTargetsNode("AENonSelectiveTargets");
                    nextNode = node;
                    // AutofireAttackNode node = new AutofireAttackNode(nextNodeName);
                    // nextNode = node;
                }
                else if ( nextNodeName.equals("AENormalTargets")) {
                    // AutofireSprayAttackNode node = new AutofireSprayAttackNode(nextNodeName);
                    // nextNode = node;
                    AENormalTargetsNode node = new AENormalTargetsNode("AENormalTargets");
                    nextNode = node;
                }
            }
        }
        return nextNode;
    }
    
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        
        if ( previousNodeName == null ) {
            BattleEvent be = getBattleEvent();
            Ability ability = be.getAbility();
            ActivationInfo ai = be.getActivationInfo();
            
            if ( ability.getBooleanValue("Ability.ISSELECTIVEAE" ) ) {
                nextNodeName = "AESelectiveTargets";
            }
            else if ( ability.getBooleanValue("Ability.ISNONSELECTIVEAE" ) ) {
                nextNodeName = "AENonSelectiveTargets";
            }
            else {
                nextNodeName = "AENormalTargets";
            }
        }
        return nextNodeName;
    }
    
    public void prepareBattleEvent() {
        String newGroup = getTargetGroup();
        // Create the group for this attack
        ActivationInfo ai = getBattleEvent().getActivationInfo();
        
        int tgindex = ai.addTargetGroup(newGroup);
        ai.setKnockbackGroup(tgindex, "KB");
        
        try {
            // Add all attack information which depends on the AttackParameterPanel
            Target source = getBattleEvent().getSource();
            
            BattleEngine.calculateDamage(battleEvent, source, newGroup);
        }
        catch (BattleEventException bee) {
            
        }
    }
    
}
