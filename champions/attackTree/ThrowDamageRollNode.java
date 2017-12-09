/*
 * KnockbackEffectNode.java
 *
 * Created on November 8, 2001, 12:03 PM
 */

package champions.attackTree;

import champions.*;
import champions.enums.KnockbackEffect;

import champions.interfaces.AbilityIterator;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version
 */
public class ThrowDamageRollNode extends DefaultAttackTreeNode {
    
    /** Holds value of property knockbackGroup. */
    private String knockbackGroup;
    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    /** Holds value of property target. */
    private Target target;
    
    /** Holds value of property ability. */
    private Ability ability;
    
    /** Holds value of property diceName. */
    private String diceName;
    
    
    private KnockbackObstructionNode secondaryTargetNode;
    
    /** Creates new KnockbackEffectNode */
    public ThrowDamageRollNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.knockbackEffectIcon");
    }
    private void setupNode() {
        ActivationInfo ai = battleEvent.getActivationInfo();
        int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup() );
        setDiceName("BREAKFALL_SKILLROLL_" +tindex );
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        setupNode();
        prepareBattleEvent();
        if ( manualOverride || nodeRequiresInput() ) {
            acceptActivation = true;
            
            attackTreePanel.setInstructions( "Choose the effect of ...");
           // attackTreePanel.showInputPanel(this, ThrowEffectPanel.getDefaultPanel(battleEvent, getTarget(), ,diceName,getTargetGroup()));
        }
        
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        boolean requiresInput = false;
        
        int kbindex = 0;// = battleEvent.getKnockbackIndex(getTarget(), getKnockbackGroup());
        KnockbackEffect effect = battleEvent.getKnockbackEffect(kbindex);
        int distance = battleEvent.getKnockbackDistance(kbindex);
        
        if ( effect == null ) {
            requiresInput = true;
        }
        
        Dice roll = battleEvent.getDiceRoll(diceName, getTargetGroup() );
        
        return requiresInput && roll == null && getAutoBypassTarget().getBooleanProfileOption( getAutoBypassOption());
    }
    
    public boolean processAdvance() {
        boolean advance = true;
        //for breakfall        
        Dice roll = battleEvent.getDiceRoll(diceName, getTargetGroup() );
        if ( roll == null ) {
            roll = new Dice(3,true);
            battleEvent.setDiceRoll(diceName, getTargetGroup() , roll);
            
        }
        ActivationInfo ai = battleEvent.getActivationInfo();
        
        int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup() );
        Target target = ai.getTarget(tindex);
        Ability ability = battleEvent.getAbility();
        
    //    String effect = battleEvent.getKnockbackEffect(kbindex);
        
//        if (getBreakfall(target) && distance > 0) {
//            
//            Integer targetbaseroll = battleEvent.getActivationInfo().getIntegerValue( "Attack.TARGETBASEROLL" );
//            Integer targetmodifier = battleEvent.getActivationInfo().getIntegerValue( "Attack.TARGETMODIFIER" );
//            
//            
//            if (targetmodifier == null ) targetmodifier = new Integer(0);
//            
//            if (targetbaseroll== null )  targetbaseroll = new Integer(0);
//            
//            int needed = targetbaseroll.intValue() + targetmodifier.intValue() - distance/2;
//            String breakfalloverride =  battleEvent.getActivationInfo().getIndexedStringValue(tindex,"Target","BREAKFALLOVERRIDE");
//            
//            
//            if ( (roll.getStun().intValue() <= needed) && !target.isUnconscious() && !target.isStunned() && breakfalloverride == null ) {
//                battleEvent.addBattleMessage( new champions.battleMessage.SimpleBattleMessage(target.getName() + "'s breakfall was successful during knockback of " + distance + "\".  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_HIT)); // .addBattleMessage( new champions.battleMessage.SimpleBattleMessage(target.getName() + "'s breakfall was successful during knockback of " + distance + "\".  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_HIT)); // .addMessage(target.getName() + "'s breakfall was successful during knockback of " + distance + "\".  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_HIT);
//                battleEvent.setKnockbackEffect(kbindex,KnockbackEffect.NOEFFECT);
//                
//                
//            }
//            else {
//                battleEvent.addAIStateEvent(ai, ai.getState(), AI_STATE_ACTIVATION_FAILED );
//                ai.setState(AI_STATE_ACTIVATION_FAILED);
//                
//                if (!target.isUnconscious() && !target.isStunned() && breakfalloverride == null ) {
//                    battleEvent.addBattleMessage( new champions.battleMessage.SimpleBattleMessage(target.getName() + "'s breakfall was unsuccessful.  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS)); // .addBattleMessage( new champions.battleMessage.SimpleBattleMessage(target.getName() + "'s breakfall was unsuccessful.  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS)); // .addMessage(target.getName() + "'s breakfall was unsuccessful.  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS);
//                }
//                else if (target.isUnconscious() && target.isStunned()) {
//                    battleEvent.addBattleMessage( new champions.battleMessage.SimpleBattleMessage(target.getName() + "'s breakfall was unsuccessful due to being unconscious or stunned.", BattleEvent.MSG_MISS)); // .addBattleMessage( new champions.battleMessage.SimpleBattleMessage(target.getName() + "'s breakfall was unsuccessful due to being unconscious or stunned.", BattleEvent.MSG_MISS)); // .addMessage(target.getName() + "'s breakfall was unsuccessful due to being unconscious or stunned.", BattleEvent.MSG_MISS);
//                }
//                else if (breakfalloverride != null ) {
//                    battleEvent.addBattleMessage( new champions.battleMessage.SimpleBattleMessage(target.getName() + "'s breakfall roll overridden by Knockback Effect selection." , BattleEvent.MSG_MISS)); // .addBattleMessage( new champions.battleMessage.SimpleBattleMessage(target.getName() + "'s breakfall roll overridden by Knockback Effect selection." , BattleEvent.MSG_MISS)); // .addMessage(target.getName() + "'s breakfall roll overridden by Knockback Effect selection." , BattleEvent.MSG_MISS);
//                }
//                
//                
//            }
//        }
//        effect = battleEvent.getKnockbackEffect(kbindex);
//        
//        String reason;
//        
//        int amount = battleEvent.getKnockbackAmount(kbindex);
//        int zero = 0;
//        if ( effect == null ) {
//            if ( battleEvent.getKnockbackDistance(kbindex) > 0 ) {
//                reason = "Knockback resulted in no collision.";
//                ai.setTargetHitOverride(tindex, true, reason, reason);
//                checkDice(kbindex, amount/2);
//            }
//            else {
//                reason = "Knockback caused no effect.";
//                ai.setTargetHitOverride(tindex, false, reason, reason );
//            }
//        }
//        else if ( effect.equals(KnockbackEffect.NOEFFECT) ){
//            reason = "Knockback caused no effect.";
//            ai.setTargetHitOverride(tindex, false, reason, reason );
//            removeSecondaryTarget();
//        }
//        else if ( effect.equals(KnockbackEffect.NOCOLLISION") ) {
//            reason = "Knockback resulted in no collision.";
//            ai.setTargetHitOverride(tindex, true, reason, reason);
//            checkDice(kbindex, amount/2);
//            removeSecondaryTarget();
//        }
//        else if ( effect.equals(KnockbackEffect.COLLISION") ) {
//            reason = "Knockback resulted in a collision.";
//            ai.setTargetHitOverride(tindex, true, reason, reason);
//            checkDice(kbindex, amount);
//        }
//        
        return advance;
    }
    private void checkDice(int kbindex, int dice) {
//        Dice d = battleEvent.getThrowDamageRoll();
//        
//        if ( d == null ) {
//            d = new Dice(dice, true);
//            battleEvent.setThrowDamageRoll(d);
//        }
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
                if ( nextNodeName.equals("Throw Damage") ) {
                    ThrowDamageNode node = new ThrowDamageNode("Throw Damage");
       //             node.setTargetReferenceNumber( 1 );
                    nextNode = node;
                }
            }
        }
        return nextNode;
    }
    
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        
        if ( previousNodeName == null ) {
            return "Throw Damage";
        }
        
        return nextNodeName;
    }
    
    public String getAutoBypassOption() {
        return "SHOW_THROW_EFFECT_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return battleEvent.getSource();
    }
    
    
    
    private void prepareBattleEvent() {
        battleEvent.addDiceInfo( diceName, getTargetGroup(), "Skill Roll Dice Roll", "3D6" );
    }
    
    /** Getter for property ability.
     * @return Value of property ability.
     */
    public Ability getAbility() {
        return this.ability;
    }
    
    /** Setter for property ability.
     * @param ability New value of property ability.
     */
    public void setAbility(Ability ability) {
        this.ability = ability;
        
        
    }
    
    /** Getter for property diceName.
     * @return Value of property diceName.
     */
    public String getDiceName() {
        return this.diceName;
    }
    
    /** Setter for property diceName.
     * @param diceName New value of property diceName.
     */
    public void setDiceName(String diceName) {
        this.diceName = diceName;
    }
    
    public boolean getBreakfall(Target target) {
        
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
                    ai.add("Attack.TARGETBASEROLL",new Integer(8),true);
                }
                else {
                    ai.add("Attack.TARGETBASEROLL",new Integer(targetbaseroll),true);
                }
                return true;
            }
            
        }
        return false;
    }
}
