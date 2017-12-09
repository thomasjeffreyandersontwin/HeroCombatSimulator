/*
 * KnockbackTargetNode.java
 *
 * Created on November 8, 2001, 11:59 AM
 */

package champions.attackTree;

import champions.Ability;
import champions.ActivationInfo;
import champions.BattleEngine;
import champions.BattleEvent;
import champions.KnockbackModifiersList;
import champions.PADRoster;
import champions.SkillRollInfo;
import champions.Target;
import champions.battleMessage.BattleMessageGroup;
import champions.battleMessage.KnockbackSummaryGroup;
import champions.enums.KnockbackEffect;
import champions.interfaces.AbilityIterator;

/**
 *
 * @author  twalker
 * @version
 */
public class KnockbackTargetNode extends DefaultAttackTreeNode implements BattleMessageGroupProvider {
    
    public static AttackTreeNode Node;

	/** Holds value of property knockbackGroup. */
    private String knockbackGroup;
    
    /** Holds value of property target. */
    private Target target;
    
    private KnockbackSummaryGroup knockbackMessageGroup;
    
    /** Creates new KnockbackTargetNode */
    public KnockbackTargetNode(String name) {
        this.name = name;
        //setVisible(false);
        Node = this;
    }
    
    public boolean activateNode(boolean manualOverride) {
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        
        boolean activateNode = false;
        
        knockbackMessageGroup = new KnockbackSummaryGroup(target);
        battleEvent.openMessageGroup(knockbackMessageGroup);
        
        // Setup the Knockback information for this particular target.
        int kbindex = battleEvent.getKnockbackIndex(target, knockbackGroup);
        
        if ( kbindex != -1 ) {
            KnockbackModifiersList kml = battleEvent.getKnockbackModifiersList(kbindex);
            
            kml = BattleEngine.buildKnockbackModifiersList(battleEvent, target, knockbackGroup, kml);
            
            battleEvent.setKnockbackModifiersList(kbindex,kml);
        }
        
        if ( manualOverride ) {
            
            String info = "This node contains all of the knockback information for " + target.getName() +
                    ".\n\nHit Okay to enter the knockback roll, select effects, and select possible knockback " +
                    "targets.\n";
            
            InformationPanel mp = InformationPanel.getDefaultPanel(info);
            attackTreePanel.showInputPanel(this,mp);
            attackTreePanel.setInstructions("Hit Okay to Continue...");
            
            activateNode = true;
        }
        
        
        // The KnockbackTargetNode node should never accept active node status, since
        // it isn't really a real node.
        return activateNode;
    }
    
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        
        AttackTreeNode nextNode = null;
        // Compare the activeChild to the possible children, to determine what comes next.
        if ( activeChild != null && activeChild.getName() == null ) {
            // This is probably an error
            int index = children.indexOf(activeChild);
            System.out.println("Null Node name for child " + Integer.toString(index) + " of Parent " + this);
            nextNode = null;
        } else {
            String previousNodeName = (activeChild == null ) ? null : activeChild.getName();
            String nextNodeName = nextNodeName(previousNodeName);
            if ( nextNodeName != null ) {
                if ( nextNodeName.equals("Knockback Roll") ) {
                    KnockbackEffectRollNode node = new KnockbackEffectRollNode("Knockback Roll");
                    node.setTarget(getTarget());
                    node.setKnockbackGroup(getKnockbackGroup());
                    nextNode = node;
                } else if ( nextNodeName.equals("Knockback Effect") ) {
                    KnockbackEffectNode node = new KnockbackEffectNode("Knockback Effect");
                    node.setTarget(getTarget());
                    node.setKnockbackGroup(getKnockbackGroup());
                    nextNode = node;
                } else if ( nextNodeName.equals("Knockback Damage") ) {
                    KnockbackDamageApplyNode node = new KnockbackDamageApplyNode("Knockback Damage");
                    node.setTarget(getTarget());
                    node.setKnockbackGroup(getKnockbackGroup());
                    nextNode = node;
                } else if ( nextNodeName.equals("Knockback Damage Roll") ) {
                    KnockbackDamageRollNode node = new KnockbackDamageRollNode("Knockback Damage Roll");
                    node.setTarget(getTarget());
                    node.setKnockbackGroup(getKnockbackGroup());
                    nextNode = node;
                } else if ( nextNodeName.equals("Knockback Breakfall Node") ) {
                    KnockbackBreakfallNode node = new KnockbackBreakfallNode("Knockback Breakfall Node");
                    node.setTarget(getTarget());
                    node.setKnockbackGroup(getKnockbackGroup());
                    nextNode = node;
                } else if ( nextNodeName.equals("Knockback Close Message Node") ) {
                    CloseMessageGroupNode node = new CloseMessageGroupNode("Knockback Close Message Node", this);
                    nextNode = node;
                }
            }
        }
        return nextNode;
    }
    
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        
        if ( previousNodeName == null ) {
            nextNodeName = "Knockback Roll";
        } else if ( previousNodeName.equals("Knockback Roll") ) {
            nextNodeName = "Knockback Effect";
        } else if ( previousNodeName.equals("Knockback Effect") ) {
            int kbindex = battleEvent.getKnockbackIndex(getTarget(), getKnockbackGroup());
            KnockbackEffect effect = battleEvent.getKnockbackEffect(kbindex);
            int distance = battleEvent.getKnockbackDistance(kbindex);
            
            if ( effect == null || effect.equals(KnockbackEffect.NOEFFECT) ) {
                nextNodeName = "Knockback Damage";
            }
            else if ( effect.equals(KnockbackEffect.KNOCKDOWNONLY) ) {
                boolean breakfall = getTargetHasBreakfall();
                if ( breakfall ) {
                    nextNodeName = "Knockback Breakfall Node";
                }
                else {
                    nextNodeName = "Knockback Damage";
                }
            }
            else {
                ActivationInfo ai = battleEvent.getActivationInfo();
                
                boolean collisionOccurred = (effect.equals(KnockbackEffect.COLLISION) || ai.getKnockbackCollisionWithSecondaryTargetOccurred(getTargetGroup()));
                boolean breakfall = getTargetHasBreakfall();
                
                if ( collisionOccurred || breakfall == false) {
                    nextNodeName = "Knockback Damage Roll";
                    
                    // As a side effect, lets get rid of the breakfall roll info if it exists
                    battleEvent.removeSkillRollInfo("BreakfallRoll", getTargetGroup());
                    
                } else {
                    nextNodeName = "Knockback Breakfall Node";
                }
            }
        } else if ( previousNodeName.equals("Knockback Breakfall Node") ) {
            int kbindex = battleEvent.getKnockbackIndex(getTarget(), getKnockbackGroup());
            int distance = battleEvent.getKnockbackDistance(kbindex);

            if ( isBreakfallSuccessful(battleEvent, getTargetGroup()) || distance <= 0 ) {
                nextNodeName = "Knockback Damage";
            }
            else {
                nextNodeName = "Knockback Damage Roll";
            }
        } else if ( previousNodeName.equals("Knockback Damage Roll") ) {
            nextNodeName = "Knockback Damage";
        }
        
        if ( nextNodeName == null && previousNodeName.equals("Knockback Close Message Node") == false) {
            nextNodeName = "Knockback Close Message Node";
        }
        
        return nextNodeName;
    }
    
    public void prepareBattleEvent() {
        String newGroup = getTargetGroup();
        // Create the group for this attack
        ActivationInfo ai = getBattleEvent().getActivationInfo();
        
        int tgindex = ai.addTargetGroup(newGroup);
        ai.setKnockbackGroup(tgindex, newGroup + ".KB");
        ai.setTargetGroupIsTemporary(tgindex, true);
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
        
        // Create the Primary Knockback Target in the Target group, but set him to unhit...
        ActivationInfo ai = battleEvent.getActivationInfo();
        int tindex = ai.addTarget(target, getTargetGroup(), 0);
        ai.setKnockbackSourceTarget(tindex, true);
        ai.setTargetHit(tindex, false);
    }
    
    public String toString() {
        return "Knockback Vs. " + getTarget();
    }
    
    public void setTargetGroupSuffix(String suffix) {
        super.setTargetGroupSuffix(suffix);
        
        prepareBattleEvent();
    }
    
    private boolean getTargetHasBreakfall() {
        return getBreakfallAbility(getTarget()) != null;
    }
    
    public static Ability getBreakfallAbility(Target target) {
        
        Ability breakfall = PADRoster.getSharedAbilityInstance("Breakfall");
        
        
        AbilityIterator aiter = target.getSkills();
        
        while (aiter.hasNext() ) { // Check to see if there is another item
            // Rip next ability from the iterator.
            // This a is guaranteed to have a power which is actually a skill
            // since we used the getSkills.  If we use getAbilities() we will
            // actually get both skills and powers.
            Ability a = aiter.nextAbility();
            if ( a.isEnabled(target,false) && a.equals(breakfall)) {
                return a;
            }
            
        }
        return null;
    }
    
    public static boolean isBreakfallSuccessful(BattleEvent battleEvent, String targetGroup) {
        SkillRollInfo sri = battleEvent.getSkillRollInfo("BreakfallRoll", targetGroup);
        return sri != null && sri.isSuccessful();
    }

    public BattleMessageGroup getBattleMessageGroup() {
        return knockbackMessageGroup;
    }
    
}
