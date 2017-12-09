/*
 * KnockbackEffectNode.java
 *
 * Created on November 8, 2001, 12:03 PM
 */

package champions.attackTree;

import champions.*;
import champions.battleMessage.DiceRollMessage;

import champions.interfaces.AbilityIterator;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version
 */
public class ThrowBreakfallNode extends DefaultAttackTreeNode {

    
    /** Creates new KnockbackEffectNode */
    public ThrowBreakfallNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.diceIcon");
    }
    
    private void setupNode() {
        battleEvent.addDiceInfo("THROW_BREAKFALL_ROLL", ".THROW", "Breakfall Roll", "3d6");
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        setupNode();
        if ( manualOverride || nodeRequiresInput() ) {
            acceptActivation = true;
            
            attackTreePanel.setInstructions( "Breakfall Roll for reduced damage...");
            attackTreePanel.showInputPanel(this, ThrowBreakfallPanel.getDefaultPanel(battleEvent, "THROW_BREAKFALL_ROLL", ".THROW"));
        }
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        boolean requiresInput = false;
        
        Dice roll = battleEvent.getDiceRoll("THROW_BREAKFALL_ROLL", ".THROW" );
        
        return requiresInput && roll == null && getAutoBypassTarget().getBooleanProfileOption( getAutoBypassOption());
    }
    
    public boolean processAdvance() {
        boolean advance = true;
        //for breakfall        
        Dice roll = battleEvent.getDiceRoll("THROW_BREAKFALL_ROLL", ".THROW");
        if ( roll == null ) {
            roll = new Dice(3,true);
            battleEvent.setDiceRoll("THROW_BREAKFALL_ROLL", ".THROW", roll);
            
        }
        ActivationInfo ai = battleEvent.getActivationInfo();
        
        Target target = (Target)ai.getValue("ActivationInfo.THROWNOBJECT");
        double dc = ai.getDoubleValue("ActivationInfo.THROWDC").doubleValue();
        
        if ( dc > 0 ) {
            
            Integer targetmodifier = ai.getIntegerValue( "ActivationInfo.THROWBREAKFALLMODIFIER" );
            Target t = (Target)ai.getValue( "ActivationInfo.THROWNOBJECT" );
            int targetbaseroll = getBreakfallRoll(t);

            if ( targetmodifier == null ) targetmodifier = new Integer(0);
        
            int needed = targetmodifier.intValue() + targetbaseroll - (int)ChampionsUtilities.roundValue(dc/2, true);
            
            if ( (roll.getStun().intValue() <= needed)  ) {
                battleEvent.addBattleMessage( new DiceRollMessage( t, t.getName() + "'s breakfall was successful while being thrown.  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + "."));//, BattleEvent.MSG_HIT)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + "'s breakfall was successful while being thrown.  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_HIT)); // .addMessage(t.getName() + "'s breakfall was successful while being thrown.  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_HIT);
                ai.add("ActivationInfo.THROWBREAKFALL", "TRUE", true);
            }
            else {
                battleEvent.addBattleMessage( new DiceRollMessage( t, target.getName() + "'s breakfall was unsuccessful.  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + "."));//, BattleEvent.MSG_MISS)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + "'s breakfall was unsuccessful.  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS)); // .addMessage(target.getName() + "'s breakfall was unsuccessful.  Needed " + Integer.toString(needed) + ".  Rolled " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS);
                ai.add("ActivationInfo.THROWBREAKFALL", "FALSE", true);
            }
        }
        
        return advance;
    }

    
    
    
    public String getAutoBypassOption() {
        return "SHOW_THROW_EFFECT_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return battleEvent.getSource();
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
