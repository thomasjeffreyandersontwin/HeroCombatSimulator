/*
 * SkillRollNode.java
 *
 *
 
 *
 * Created on November 9, 2001, 4:40 PM
 */

package champions.attackTree;


import champions.Ability;
import champions.ActivationInfo;
import champions.BattleEngine;
import champions.DefenseList;
import champions.Dice;
import champions.Effect;
import champions.Target;
import champions.battleMessage.DiceRollMessage;

import champions.exception.BattleEventException;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version
 */
public class SkillRollNode extends DefaultAttackTreeNode {
    
    /** Holds value of property ability. */
    private Ability ability;
    
    /** Holds value of property diceName. */
    private String diceName;
    
    /** Holds value of property limitationIndex. */
    private int limitationIndex;
    
    private Effect effect;
    
    private int effectRoll;
    
    private MissTargetNode secondaryTargetNode;
    
    /** Store Target Selected for this node */
    protected Target target;
    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    
    /** Creates new ToHitNode */
    public SkillRollNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.diceIcon");
        
        
    }
    
    /** Creates new ToHitNode */
    public SkillRollNode(String name, int effectroll, Effect effect) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.diceIcon");
        setEffectRoll(effectroll);
        setEffect(effect);
        
    }
    private void setupNode() {
        if ( ability == battleEvent.getAbility() ) {
            setDiceName("SOURCE_SKILLROLL_0");
        }
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
        boolean fixed = false;
        String reason = null;
        setupNode();
        
        prepareBattleEvent();
        
        Ability ability = battleEvent.getAbility();
        Target target = battleEvent.getSource();
        int effectroll = -1;
        if ( manualOverride || nodeRequiresInput() ) {
            
            
            if (super.getName().equals("Effect Roll") ) {
                effectroll = getEffectRoll();
            }
            
            SkillRollPanel app = SkillRollPanel.getDefaultPanel( battleEvent, ability, target, diceName, getTargetGroup(),effectroll);
            
            attackTreePanel.showInputPanel(this,app);
            attackTreePanel.setInstructions("Enter Skill Roll for " + ability.getName() + ".");
            String getsvs = battleEvent.getActivationInfo().getStringValue( "Attack.SvS" );
            ActivationInfo ai = battleEvent.getActivationInfo();
            // Try to find a target based on the battleEvent
            //ActivationInfo ai = getBattleEvent().getActivationInfo();
            int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup() );
            
            acceptActivation = true;
        }
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
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
                    node.setMode(SKILL_TARGET);
                    node.setTargetGroupSuffix("SKILLTARGET");
                    node.setTargetReferenceNumber(0); // Indicate that this is the primary target of the Group.
                    nextNode = node;
                }
                else {
                    nextNode = null;
                }
            }
        }
        return nextNode;
    }
    
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        String getsvs = battleEvent.getActivationInfo().getStringValue( "Attack.SvS" );
        String skillrolloutcome =  battleEvent.getActivationInfo().getStringValue( "Attack.SKILLROLLOUTCOME");
        
        if ( previousNodeName == null ) {
            if ( skillrolloutcome != null && skillrolloutcome.equals("FALSE") ) {
                nextNodeName = "";
            }
            else if ( getsvs != null && getsvs.equals("TRUE") ) {
                nextNodeName = "Single Target";
            }
        }
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
    public boolean processAdvance() throws BattleEventException{
        Ability ability = battleEvent.getAbility();
        ActivationInfo ai = battleEvent.getActivationInfo();
        Target source = battleEvent.getSource();
        Integer usedsl = new Integer(0);
        Ability effectAbility;
        Integer level = new Integer(0);
        Integer allocatesl = new Integer(0);
        Integer skillbonus = new Integer(0);
        int count, i;
        
        int sbtot = 0;
        //Effect effectsl = source.getEffect("Skill Levels");
        
        if ( ai.isActivated() == false ) {
            battleEvent.addAIStateEvent(ai, ai.getState(), "ACTIVATED" );
            ai.setState("ACTIVATED");
            //ability.add("Ability.ACTIVATED", "TRUE",  true);
            //be.addActivateEvent( ability, true );
            
            // Run through the source Effects to let them know that Ability is activating
            // First build list, then run through list (just in case it changes).
            count = source.getEffectCount();
            Effect[] sourceEffects = new Effect[count];
            for (i=0;i<count;i++) {
                sourceEffects[i] = source.getEffect(i);
            }
            
            for (i=0;i<count;i++) {
                if ( source.hasEffect(sourceEffects[i]) ) {
                    sourceEffects[i].skillIsActivating(battleEvent,ability);
                    usedsl = (Integer)sourceEffects[i].getValue("Effect.USEDSL");
                    //if (effectsl != null) {
                    skillbonus = sourceEffects[i].getIntegerValue("Effect.SKILLLEVEL");
                    effectAbility = (Ability)sourceEffects[i].getValue("Effect.ABILITY");
                    if (effectAbility != null) {
                        level = effectAbility.getIntegerValue("Power.SKILLLEVEL");
                        allocatesl = effectAbility.getIntegerValue("CombatLevel.ALLOCATESL");
                    }
                    if (level == null) level = new Integer(0);
                    if (usedsl == null) usedsl = new Integer(0);
                    if (skillbonus != null) {
                        if (level.intValue() >= (usedsl.intValue() + skillbonus.intValue())) {
                            sbtot = sbtot + skillbonus.intValue();
                            usedsl = new Integer(usedsl.intValue() + skillbonus.intValue());
                        }
                        else {
                            sbtot = sbtot + (level.intValue() - usedsl.intValue());
                            usedsl = new Integer(level.intValue());
                        }
                    }
                    if (usedsl != null) {
                        //sourceEffects[i].add("Effect.USEDSL", new Integer(allocatesl.intValue() + usedsl.intValue()), true);
                        sourceEffects[i].add("Effect.USEDSL", new Integer(usedsl.intValue()), true);
                    }
                    //}
                    
                }
            }
        }
        
        // Make sure if it is a roll, that there is a dice out there...
        
        Dice roll = battleEvent.getDiceRoll(diceName, getTargetGroup());
        if ( roll == null ) {
            roll = new Dice(3,true);
            battleEvent.setDiceRoll(diceName, getTargetGroup(), roll);
        }
        //Ability ability = battleEvent.getAbility();
        
        
        // Grab the Info from the Activation Limitation
        //ActivationInfo ai = battleEvent.getActivationInfo();
        Integer modifier = battleEvent.getActivationInfo().getIntegerValue( "Attack.MODIFIER" );
        String surprisebonus =  ability.getStringValue("Ability.ROLLBASEDSURPRISEBONUS");
        ai.add("Attack.TARGETMODIFIER", new Integer(0), true);
        
        if (modifier == null) modifier = new Integer(0);
        
        //Target source = battleEvent.getSource();
        Target target = battleEvent.getTarget();
        
        
        //        Effect effectsl = source.getEffect("Skill Levels");
        int skillrollbase = ability.getSkillRoll(source);
        if (skillrollbase < 0) {
            skillrollbase = 0;
        }

        
        if (super.getName().equals("Effect Roll")) {
            skillrollbase = effectRoll;
        }
        int needed = skillrollbase + modifier.intValue() +sbtot;
        //        if (effectsl != null) {
        //            Integer skillbonus = effectsl.getIntegerValue("Effect.LEVEL");
        //            if (skillbonus != null) {
        //                needed = needed + skillbonus.intValue();
        //            }
        //        }
        ai.add("Attack.TARGETMODIFIER", new Integer(roll.getStun().intValue() - needed), true);
        
        //        try {
        
        
        
        
        if ( roll.getStun().intValue() <= needed ) {
            battleEvent.addBattleMessage( new DiceRollMessage(source, source.getName() + "'s skill roll for " + ability.getName() + " was successful.  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + "."));//, BattleEvent.MSG_HIT)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(source.getName() + "'s skill roll for " + ability.getName() + " was successful.  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_HIT)); // .addMessage(source.getName() + "'s skill roll for " + ability.getName() + " was successful.  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_HIT);
            ai.add("Attack.SKILLROLLOUTCOME", new String("TRUE"), true);
            ai.add("Attack.ROLL", new Integer(roll.getStun().intValue()), true);
            ai.add("Attack.NEEDED", new Integer(needed), true);
            ai.add("Attack.DIFFERENCE", new Integer(needed - roll.getStun().intValue()), true);
            
            //suprise bonus code should be moved to the skill and executed if the skill roll
            //succeeds.  A value pair would have to be entered to pick up the difference in the ability
            //if (surprisebonus != null && surprisebonus.equals("TRUE") ) {
            
            
            //                    int difference = (needed - roll.getStun().intValue()) /3;
            //                    if (difference > 3) difference = 3;
            //                    ai.add("Attack.DIFFERENCE", new Integer(difference), true);
            
            //                    Effect effect =  new effectCombatModifier("Surprise Move",difference,0);
            //
            // Add the Effect to the Target
            //                    effect.addEffect(battleEvent, source);
            //}
        }
        else {
            battleEvent.addAIStateEvent(ai, ai.getState(), AI_STATE_ACTIVATION_FAILED );
            ai.setState(AI_STATE_ACTIVATION_FAILED);
            
            battleEvent.addBattleMessage( new DiceRollMessage(source, source.getName() + "'s skill roll for " + ability.getName() + " was unsuccessful.  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + "."));//, BattleEvent.MSG_MISS)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(source.getName() + "'s skill roll for " + ability.getName() + " was unsuccessful.  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS)); // .addMessage(source.getName() + "'s skill roll for " + ability.getName() + " was unsuccessful.  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS);
            //To make sure that the target select node isn't run I built a value pair to be checked
            //to see if the roll failed during the assignment of the nextnode name above.
            ai.add("Attack.SKILLROLLOUTCOME", new String("FALSE"), true);
            if (super.getName().equals("Effect Roll") ) {
                //To track the amount...
                //First lookup the ability to see if there is a previous entry
                effect.createIndexed("Burnout", "ABILITY", ai.getAbility(), false);
            }
            
            
        }
        //        }
        //        catch ( BattleEventException bee ) {
        //            getModel().setError(bee);
        //        }
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println( "Node " + name + " exited.");
        return true;
    }
    
    /** Determines if this node needs to show an input panel.
     *
     */
    protected  boolean nodeRequiresInput() {
        boolean requiresInput = false;
        
        Dice roll = battleEvent.getDiceRoll(diceName, "");
        
        return roll == null && getAutoBypassTarget().getBooleanProfileOption(getAutoBypassOption());
    }
    
    public String getAutoBypassOption() {
        return "SHOW_SKILL_PANEL";
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
    
    /** Getter for property targetReferenceNumber.
     * @return Value of property targetReferenceNumber.
     */
    public int getTargetReferenceNumber() {
        return targetReferenceNumber;
    }
    
    /** Setter for property targetReferenceNumber.
     * @param targetReferenceNumber New value of property targetReferenceNumber.
     */
    public void setTargetReferenceNumber(int targetReferenceNumber) {
        this.targetReferenceNumber = targetReferenceNumber;
    }
    
    /** Getter for property targetReferenceNumber.
     * @return Value of property targetReferenceNumber.
     */
    public int getEffectRoll() {
        return effectRoll;
    }
    
    /** Setter for property targetReferenceNumber.
     * @param targetReferenceNumber New value of property targetReferenceNumber.
     */
    public void setEffectRoll(int effectroll) {
        this.effectRoll = effectroll;
    }
    
    
    /** Getter for property mode.
     * @return Value of property mode.
     */
    
    public Target getTarget() {
        return this.target;
    }
    
    public void setTarget(Target target) {
        if ( this.target != target ) {
            ActivationInfo ai = getBattleEvent().getActivationInfo();
            if ( this.target != null ) {
                ai.removeTarget(targetReferenceNumber, getTargetGroup());
                
                if ( children != null ) {
                    int index;
                    AttackTreeNode atn;
                    for( index = children.size() - 1; index >= 0; index--) {
                        atn = (AttackTreeNode)children.get(index);
                        removeChild(atn);
                    }
                }
            }
            
            this.target = target;
            
            if ( this.target != null ) {
                ai.addTarget(target, getTargetGroup(), targetReferenceNumber);
                
                // Do all the target initialization here
                int tindex = ai.getTargetIndex(targetReferenceNumber,getTargetGroup());
                
                DefenseList dl = new DefenseList();
                BattleEngine.buildDefenseList(dl,target);
                ai.setDefenseList(tindex, dl);
                
            }
            
            // if ( model != null ) {
            //     model.nodeChanged(this);
            // }
        }
    }
    
    /** Getter for property battleEvent.
     * @return Value of property battleEvent.
     */
    public Effect getEffect() {
        return effect;
    }
    
    
    /** Setter for property battleEvent.
     * @param battleEvent New value of property battleEvent.
     */
    public void setEffect(Effect effect) {
        this.effect = effect;
    }
}
