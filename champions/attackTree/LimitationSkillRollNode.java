/*
 * ToHitNode.java
 *
 * Created on November 9, 2001, 4:40 PM
 */

package champions.attackTree;


import champions.Ability;
import champions.ActivationInfo;
import champions.Dice;
import champions.Target;
import champions.battleMessage.DiceRollMessage;

import champions.battleMessage.GenericSummaryMessage;
import champions.interfaces.Limitation;
import champions.parameters.ParameterList;
import champions.powers.limitationSkillRoll;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version
 */
public class LimitationSkillRollNode extends DefaultAttackTreeNode {
    
    /** Holds value of property ability. */
    private Ability ability;
    
    /** Holds value of property diceName. */
    private String diceName;
    
    /** Holds value of property limitationIndex. */
    private int limitationIndex;
    
    /** Creates new ToHitNode */
    public LimitationSkillRollNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.toHitIcon");
        
        
    }
    
    private void setupNode() {
        if ( ability == battleEvent.getAbility() ) {
            setDiceName("ABILITY_SKILLROLL_" + Integer.toString(limitationIndex));
        }
        else {
            setDiceName("MANEUVER_SKILLROLL_" + Integer.toString(limitationIndex));
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
        
        setupNode();
        
        prepareBattleEvent();
        
        if ( manualOverride || nodeRequiresInput() ) {
            LimitationActivationPanel app = LimitationActivationPanel.getDefaultPanel( battleEvent, diceName);
            
            attackTreePanel.showInputPanel(this,app);
            attackTreePanel.setInstructions("Enter Skill Roll for " + ability.getName() + ".");
            
            acceptActivation = true;
        }
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
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
    public boolean processAdvance() {
        // Make sure if it is a roll, that there is a dice out there...
        
        Dice roll = battleEvent.getDiceRoll(diceName, "");
        if ( roll == null ) {
            roll = new Dice(3,true);
            battleEvent.setDiceRoll(diceName, "", roll);
        }
        
        // Grab the Info from the Activation Limitation
        ActivationInfo ai = battleEvent.getActivationInfo();
        limitationSkillRoll l = (limitationSkillRoll)ability.getLimitation(limitationIndex);

        Target source = battleEvent.getSource();
        
        int needed = l.getSkillRoll(ability);
        
        if ( roll.getStun().intValue() <= needed ) {
            String msg = source.getName() + "'s skill roll for " + ability.getName() + " was successful.  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + ".";
            //battleEvent.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(source.getName() + "'s activation roll for " + ability.getName() + " was successful.  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_HIT)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(source.getName() + "'s activation roll for " + ability.getName() + " was successful.  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_HIT)); // .addMessage(source.getName() + "'s activation roll for " + ability.getName() + " was successful.  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_HIT);
            battleEvent.addBattleMessage( new DiceRollMessage(source, msg));
            return true;
        }
        else {
            battleEvent.addAIStateEvent(ai, ai.getState(), AI_STATE_ACTIVATION_FAILED );
            ai.setState(AI_STATE_ACTIVATION_FAILED);
            
            String msg = source.getName() + "'s skill roll for " + ability.getName() + " was unsuccessful.  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + ".";
            //battleEvent.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(source.getName() + "'s activation roll for " + ability.getName() + " was unsuccessful.  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(source.getName() + "'s activation roll for " + ability.getName() + " was unsuccessful.  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS)); // .addMessage(source.getName() + "'s activation roll for " + ability.getName() + " was unsuccessful.  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS);
            battleEvent.addBattleMessage( new DiceRollMessage(source, msg));
            battleEvent.addBattleMessage( new GenericSummaryMessage(source, " failed activation skill roll for " + ability.getName()));
        }
        
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
            return "SHOW_SKILL_ROLL_PANEL";
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
    
    /** Getter for property limitationIndex.
     * @return Value of property limitationIndex.
     */
    public int getLimitationIndex() {
        return this.limitationIndex;
    }
    
    /** Setter for property limitationIndex.
     * @param limitationIndex New value of property limitationIndex.
     */
    public void setLimitationIndex(int limitationIndex) {
        this.limitationIndex = limitationIndex;
    }
    
}
