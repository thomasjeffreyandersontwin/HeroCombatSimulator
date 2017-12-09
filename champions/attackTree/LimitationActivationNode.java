/*
 * ToHitNode.java
 *
 * Created on November 9, 2001, 4:40 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.ActivationInfo;
import champions.ChampionsUtilities;
import champions.Dice;
import champions.Effect;
import champions.Target;
import champions.battleMessage.DiceRollMessage;
import champions.battleMessage.GenericSummaryMessage;
import champions.battleMessage.SimpleBattleMessage;

import champions.exception.BattleEventException;
import champions.interfaces.Limitation;
import champions.parameters.ParameterList;
import champions.powers.effectBurnedout;
import champions.powers.effectJammed;
import champions.powers.limitationActivation;
import java.awt.Color;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version
 */
public class LimitationActivationNode extends DefaultAttackTreeNode {
    
    /** Holds value of property ability. */
    private Ability ability;
    
    /** Holds value of property diceName. */
    private String diceName;
    
    /** Holds value of property limitationIndex. */
    private int limitationIndex;
    
    /** Creates new ToHitNode */
    public LimitationActivationNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.toHitIcon");
        
        
    }
    
    private void setupNode() {
        if ( ability == battleEvent.getAbility() ) {
            setDiceName("ABILITY_ACTIVATION_" + Integer.toString(limitationIndex));
        }
        else {
            setDiceName("MANEUVER_ACTIVATION_" + Integer.toString(limitationIndex));
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
            attackTreePanel.setInstructions("Enter Activation Roll for " + ability.getName() + ".");
            
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
            battleEvent.setDiceAutoRoll(diceName, "", true);
        }
        
        // Grab the Info from the Activation Limitation
        ActivationInfo ai = battleEvent.getActivationInfo();
        Limitation l = (Limitation)ability.getLimitation(limitationIndex);
        ParameterList parameterList = l.getParameterList(ability,limitationIndex);
        String level = (String)parameterList.getParameterValue("ActivationRoll");
        boolean canburnout = (Boolean)parameterList.getParameterValue("CanBurnout");
        boolean canjam = (Boolean)parameterList.getParameterValue("CanJam");
        
        Target source = battleEvent.getSource();
        
        if ( roll.getStun().intValue() <= limitationActivation.levelToRoll( level ) ) {
            battleEvent.addBattleMessage( new DiceRollMessage(source, source.getName() + "'s activation roll for " + ability.getName() + " was successful.  Needed " + level + ".  Got " + roll.getStun().toString() + "."));
        } 
        else {
            try {
                if (canjam ) {
                    Effect effect =  new effectJammed(ability);
                    
                    // Add the Effect to the Target
                    effect.addEffect(battleEvent, source);
                    
                    battleEvent.addBattleMessage( new SimpleBattleMessage(source, ChampionsUtilities.getHTMLColorString("ABILITY JAMMED!!", Color.RED) + " " + source.getName() + "'s activation roll for " + ability.getName() + " was unsuccessful.  Needed " + level + ".  Got " + roll.getStun().toString() + ".")); //, BattleEvent.MSG_MISS)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage("ABILITY JAMMED!! " + source.getName() + "'s activation roll for " + ability.getName() + " was unsuccessful.  Needed " + level + ".  Got " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS)); // .addMessage("ABILITY JAMMED!! " + source.getName() + "'s activation roll for " + ability.getName() + " was unsuccessful.  Needed " + level + ".  Got " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS);
                
                    battleEvent.addAIStateEvent(ai, ai.getState(), AI_STATE_ACTIVATION_FAILED );
                    ai.setState(AI_STATE_ACTIVATION_FAILED);
                } else if (canburnout ) {
                    // Burnout doesn't actually fail this time.  But it is burned out for next time...
                    Effect effect =  new effectBurnedout(ability);
                    
                    // Add the Effect to the Target
                    effect.addEffect(battleEvent, source);
                    
                    battleEvent.addBattleMessage( new SimpleBattleMessage(source, ChampionsUtilities.getHTMLColorString("ABILITY BURNED OUT WITH THIS USE!!", Color.RED) + " " + source.getName() + "'s activation roll for " + ability.getName() + " was unsuccessful.  Needed " + level + ".  Got " + roll.getStun().toString() + ".")); //, BattleEvent.MSG_MISS)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage("ABILITY BURNED OUT WITH THIS USE!! " + source.getName() + "'s activation roll for " + ability.getName() + " was unsuccessful.  Needed " + level + ".  Got " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS)); // .addMessage("ABILITY BURNED OUT WITH THIS USE!! " + source.getName() + "'s activation roll for " + ability.getName() + " was unsuccessful.  Needed " + level + ".  Got " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS);
                }
                else {
                    // Normal Failure...
                    battleEvent.addBattleMessage( new DiceRollMessage(source, source.getName() + "'s activation roll for " + ability.getName() + " was unsuccessful.  Needed " + level + ".  Got " + roll.getStun().toString() + ".")); //, BattleEvent.MSG_MISS)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(source.getName() + "'s activation roll for " + ability.getName() + " was unsuccessful.  Needed " + level + ".  Got " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS)); // .addMessage(source.getName() + "'s activation roll for " + ability.getName() + " was unsuccessful.  Needed " + level + ".  Got " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS);
                    battleEvent.addBattleMessage( new GenericSummaryMessage(source, " failed activation roll for " + ability.getName()));
                    
                    battleEvent.addAIStateEvent(ai, ai.getState(), AI_STATE_ACTIVATION_FAILED );
                    ai.setState(AI_STATE_ACTIVATION_FAILED);
                }
            }
            catch ( BattleEventException bee ) {
                getModel().setError(bee);
            }
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
        if ( battleEvent.getActivationInfo().isActivated() ) {
            return "SHOW_CONTINUING_ACTIVATION_ROLL_PANEL";
        }
        else {
            return "SHOW_INITIAL_ACTIVATION_ROLL_PANEL";
        }
    }
    
    public Target getAutoBypassTarget() {
        return battleEvent.getSource();
    }
    
    private void prepareBattleEvent() {
        battleEvent.addDiceInfo( diceName, "", "Activation Dice Roll", "3D6" );
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
