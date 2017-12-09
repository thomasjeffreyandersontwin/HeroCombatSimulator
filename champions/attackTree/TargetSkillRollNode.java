/*
 * ToHitNode.java
 *
 * Created on November 9, 2001, 4:40 PM
 */

package champions.attackTree;

import champions.*;
import champions.battleMessage.DiceRollMessage;

import champions.exception.BattleEventException;
import champions.powers.effectCombatModifier;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version
 */
public class TargetSkillRollNode extends DefaultAttackTreeNode {
    
    /** Holds value of property diceName. */
    private String diceName;
    
    private MissTargetNode secondaryTargetNode;
    
    /** Creates new ToHitNode */
    public TargetSkillRollNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.diceIcon");
    }
    
    private void setupNode() {
        setDiceName("TARGET_SKILLROLL_0");
    }
    
    /**
     * Activates the node.
     *
     * The method is called when this node is being activated.  Based on
     * the information the node current has available, it can choose to
     * accept the activation or reject it.
     *
     * If the node rejects the activation, the model will call the appropriate
     * methods to advance out of the node.
     *
     * The manualOverride boolean indicates that the user click on this node
     * specifically.  Even if this node has all the information to continue processing
     * without user input, it should accept activation so the user can make changes.
     *
     * This method should be overriden by children.
     */
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        setupNode();
        
        prepareBattleEvent();
        
        Ability ability = battleEvent.getAbility();
        ActivationInfo ai = battleEvent.getActivationInfo();
        Target target = ai.getTarget(0);
        
        if ( manualOverride || nodeRequiresInput() ) {
            TargetSkillRollPanel app = TargetSkillRollPanel.getDefaultPanel( battleEvent, diceName, target, getTargetGroup());
            
            attackTreePanel.showInputPanel(this,app);
            attackTreePanel.setInstructions("Enter Skill Roll for " + ability.getName() + ".");
            
            acceptActivation = true;
        }
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    /**
     * Builds the next child based upon the last child which was constructed.
     *
     * If all the children necessary have been built, return null, otherwise
     * return the next node.
     *
     * BuildNextChild should add that child to the tree.
     */
    /**
     * Builds the next child based upon the last child which was constructed.
     *
     * If all the children necessary have been built, return null, otherwise
     * return the next node.
     *
     * BuildNextChild should add that child to the tree.
     */
    //    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
    // This node build everything upfront, when it's target is first set.
    //        return null;
    //    }
    
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
        }
        return nextNode;
    }
    
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        return nextNodeName;
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
    //    public boolean processAdvance() throws BattleEventException{
    //        Ability ability = battleEvent.getAbility();
    //        ActivationInfo ai = battleEvent.getActivationInfo();
    //        Integer usedsl = new Integer(0);
    //        Ability effectAbility;
    //        Integer level = new Integer(0);
    //        Integer allocatesl = new Integer(0);
    //        Integer skillbonus = new Integer(0);
    //        //Target source = battleEvent.getSource();
    //
    //        Target source = battleEvent.getActivationInfo().getSource();
    //        Target target = ai.getTarget(0);
    //        int count, i;
    //
    //        int sbtot = 0;
    //        //Effect effectsl = source.getEffect("Skill Levels");
    //
    //            //ability.add("Ability.ACTIVATED", "TRUE",  true);
    //            //be.addActivateEvent( ability, true );
    //
    //            // Run through the source Effects to let them know that Ability is activating
    //            // First build list, then run through list (just in case it changes).
    //            count = target.getIndexedSize("Effect");
    //            Effect[] targetEffects = new Effect[count];
    //            for (i=0;i<count;i++) {
    //                targetEffects[i] = (Effect)target.getIndexedValue( i,"Effect","EFFECT" );
    //            }
    //
    //            for (i=0;i<count;i++) {
    //                if ( target.findIndexed( "Effect","EFFECT",targetEffects[i]) != -1 ) {
    //                    targetEffects[i].skillIsActivating(battleEvent,ability);
    //                    usedsl = (Integer)targetEffects[i].getValue("Effect.USEDSL");
    //                    skillbonus = targetEffects[i].getIntegerValue("Effect.SKILLLEVEL");
    //                    effectAbility = (Ability)targetEffects[i].getValue("Effect.ABILITY");
    //                    level = effectAbility.getIntegerValue("Power.SKILLLEVEL");
    //                    allocatesl = effectAbility.getIntegerValue("CombatLevel.ALLOCATESL");
    //                    if (level == null) level = new Integer(0);
    //                    if (usedsl == null) usedsl = new Integer(0);
    //                    if (skillbonus != null) {
    //                        if (level.intValue() >= (usedsl.intValue() + skillbonus.intValue())) {
    //                            sbtot = sbtot + skillbonus.intValue();
    //                            usedsl = new Integer(usedsl.intValue() + skillbonus.intValue());
    //                        }
    //                        else {
    //                            sbtot = sbtot + (level.intValue() - usedsl.intValue());
    //                            usedsl = new Integer(level.intValue());
    //                        }
    //                    }
    //                    if (usedsl != null) {
    //                        //sourceEffects[i].add("Effect.USEDSL", new Integer(allocatesl.intValue() + usedsl.intValue()), true);
    //                        targetEffects[i].add("Effect.USEDSL", new Integer(usedsl.intValue()), true);
    //                    }
    //                    //}
    //
    //
    //                }
    //        }
    //
    //        // Make sure if it is a roll, that there is a dice out there...
    //
    //        Dice roll = battleEvent.getDiceRoll(diceName, getTargetGroup() );
    //        if ( roll == null ) {
    //            roll = new Dice(3,true);
    //            battleEvent.setDiceRoll(diceName,  getTargetGroup() , roll);
    //        }
    //        //Ability ability = battleEvent.getAbility();
    //
    //
    //        // Grab the Info from the Activation Limitation
    //        //ActivationInfo ai = battleEvent.getActivationInfo();
    //        Integer targetmodifier = battleEvent.getActivationInfo().getIntegerValue( "Attack.TARGETMODIFIER" );
    //        Integer targetbaseroll = battleEvent.getActivationInfo().getIntegerValue( "Attack.TARGETBASEROLL" );
    //        String surprisebonus =  ability.getStringValue("Ability.ROLLBASEDSURPRISEBONUS");
    //
    //
    //        //Target target = ai.getTarget(0);
    //
    //        //Target source = battleEvent.getActivationInfo().getSource();
    //
    //        //       target.getSkills();
    //
    //        if (targetbaseroll == null) {
    //            targetbaseroll = new Integer(0);
    //        }
    //        int targetneeded = targetbaseroll.intValue() + targetmodifier.intValue() + sbtot;
    //
    //        try {
    //            if ( roll.getStun().intValue() <= targetneeded ) {
    //                battleEvent.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + "'s skill vs. skill was successful.  Needed " + Integer.toString(targetneeded) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_HIT)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + "'s skill vs. skill was successful.  Needed " + Integer.toString(targetneeded) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_HIT)); // .addMessage(target.getName() + "'s skill vs. skill was successful.  Needed " + Integer.toString(targetneeded) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_HIT);
    //                battleEvent.addAIStateEvent(ai, ai.getState(), AI_STATE_ACTIVATION_FAILED );
    //                ai.setState(AI_STATE_ACTIVATION_FAILED);
    //
    //
    //                if (surprisebonus != null && surprisebonus.equals("TRUE") ) {
    //
    //                    int difference = (targetneeded - roll.getStun().intValue()) /3;
    //                    if (difference > 3) difference = 3;
    //
    //                    Effect effect =  new effectCombatModifier("Suprise Move",difference,0);
    //
    //                    // Add the Effect to the Target
    //                    effect.addEffect(battleEvent, target);
    //                }
    //            }
    //            else {
    //
    //                battleEvent.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + "'s skill vs. skill was unsuccessful.  Needed " + Integer.toString(targetneeded) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + "'s skill vs. skill was unsuccessful.  Needed " + Integer.toString(targetneeded) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS)); // .addMessage(target.getName() + "'s skill vs. skill was unsuccessful.  Needed " + Integer.toString(targetneeded) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS);
    //            }
    //        }
    //        catch ( BattleEventException bee ) {
    //            getModel().setError(bee);
    //        }
    //        if ( AttackTreeModel.DEBUG > 0 ) System.out.println( "Node " + name + " exited.");
    //        return true;
    //    }
    
    public boolean processAdvance() throws BattleEventException{
        
        Ability ability = battleEvent.getAbility();

        //grab data from the activationInfo:
        ActivationInfo ai = battleEvent.getActivationInfo();
        Integer sbtot = battleEvent.getActivationInfo().getIntegerValue( "Attack.SBTOT" );
        Integer targetmodifier = battleEvent.getActivationInfo().getIntegerValue( "Attack.TARGETMODIFIER" );
        Integer targetbaseroll = battleEvent.getActivationInfo().getIntegerValue( "Attack.TARGETBASEROLL" );

        //Check to see if the ability generates a surprise bonus ie: Acrobatics
        String surprisebonus =  ability.getStringValue("Ability.ROLLBASEDSURPRISEBONUS");
        Integer usedsl = new Integer(0);
        Ability effectAbility;
        Integer level = new Integer(0);
        Integer allocatesl = new Integer(0);
        Integer skillbonus = new Integer(0);
        
        Target source = battleEvent.getActivationInfo().getSource();
        Target target = ai.getTarget(0);
        int count, i;
 
        // Make sure if it is a roll, that there is a dice out there...
        
        Dice roll = battleEvent.getDiceRoll(diceName, getTargetGroup() );
        if ( roll == null ) {
            roll = new Dice(3,true);
            battleEvent.setDiceRoll(diceName,  getTargetGroup() , roll);
        }
        
        if (targetbaseroll == null) {
            targetbaseroll = new Integer(0);
        }
        
         if (sbtot == null) {
            sbtot = new Integer(0);
        }       
        int targetneeded = targetbaseroll.intValue() + targetmodifier.intValue() + sbtot.intValue();
        
        try {
            if ( roll.getStun().intValue() <= targetneeded ) {
                battleEvent.addBattleMessage( new DiceRollMessage( target, target.getName() + "'s skill vs. skill was successful.  Needed " + Integer.toString(targetneeded) + ".  Rolled " + roll.getStun().toString() + "."));//, BattleEvent.MSG_HIT)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + "'s skill vs. skill was successful.  Needed " + Integer.toString(targetneeded) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_HIT)); // .addMessage(target.getName() + "'s skill vs. skill was successful.  Needed " + Integer.toString(targetneeded) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_HIT);
                battleEvent.addAIStateEvent(ai, ai.getState(), AI_STATE_ACTIVATION_FAILED );
                ai.setState(AI_STATE_ACTIVATION_FAILED);
                
                
                if (surprisebonus != null && surprisebonus.equals("TRUE") ) {
                    
                    int difference = (targetneeded - roll.getStun().intValue()) /3;
                    if (difference > 3) difference = 3;
                    
                    Effect effect =  new effectCombatModifier("Suprise Move",difference,0,0,true);
                    
                    // Add the Effect to the Target
                    effect.addEffect(battleEvent, target);
                }
            }
            else {
                
                battleEvent.addBattleMessage( new DiceRollMessage( target, target.getName() + "'s skill vs. skill was unsuccessful.  Needed " + Integer.toString(targetneeded) + ".  Rolled " + roll.getStun().toString() + "."));//, BattleEvent.MSG_MISS)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + "'s skill vs. skill was unsuccessful.  Needed " + Integer.toString(targetneeded) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS)); // .addMessage(target.getName() + "'s skill vs. skill was unsuccessful.  Needed " + Integer.toString(targetneeded) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS);
            }
        }
        catch ( BattleEventException bee ) {
            getModel().setError(bee);
        }
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println( "Node " + name + " exited.");
        return true;
    }
    
    
    
    /** Determines if this node needs to show an input panel.
     *
     */
    protected  boolean nodeRequiresInput() {
        boolean requiresInput = false;
        
        Dice roll = battleEvent.getDiceRoll(diceName,  getTargetGroup() );
        
        return roll == null && getAutoBypassTarget().getBooleanProfileOption(getAutoBypassOption());
    }
    
    public String getAutoBypassOption() {
        return "SHOW_TARGETSKILL_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return battleEvent.getSource();
    }
    
    private void prepareBattleEvent() {
        battleEvent.addDiceInfo( diceName, getTargetGroup(), "Skill Roll Dice Roll", "3D6" );
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
    
    /** Getter for property limitationIndex.
     * @return Value of property limitationIndex.
     */
    
    
}
