/*
 * effectUnconscious.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.Ability;
import champions.Battle;
import champions.BattleEvent;
import champions.Dice;
import champions.Effect;
import champions.LinkedEffect;
import champions.Target;
import champions.ValuePairUndoable;
import champions.exception.BattleEventException;

import champions.interfaces.Undoable;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;


/**
 *
 * @author  unknown
 * @version
 *
 * Effect Clinging is now Dynamic.
 */



public class effectAccidentalChange extends LinkedEffect {
    
    /** Holds value of property battleEvent. */
    
    private Effect effect;
    
    
    
    
    /** Hold the Ability which this effect is linked to */
    
    static private String[] timeIntervalOptions = {"1 Segment", "1 Phase","1 Turn", "1 Minute", "5 Minutes", "20 Minutes", "1 Hour", "6 Hours", "1 Day",
    "1 Week", "1 Month", "1 Season", "1 Year", "5 Years", "25 Years", "1 Century" };
    
    /** Creates new effectUnconscious */
    public effectAccidentalChange(Ability ability) {
        super(ability.getName(), "LINKED");
        //CIRCUMSTANCEPRESENT stores the state of the circumstance that triggers a change.  If this var is true then the substance is available
        //and the dependence is not affecting the character.  Opposite if the value is false
        this.add("Effect.CIRCUMSTANCEPRESENT","FALSE",false);
        setAbility(ability);
        setEffect(this);
        //this effect is hidden since it is on all the time but the  child dependence effects will NOT be hidden
        setHidden(true);
    }
    
    public boolean addEffect(BattleEvent be, Target target)
    throws BattleEventException {
        Ability ability = getAbility();
        Battle cb = Battle.getCurrentBattle();
        if ( super.addEffect(be,target ) ){
            return true;
        }
        return false;
    }
    
    public void removeEffect(BattleEvent be, Target target)
    throws BattleEventException {
        Ability ability = getAbility();
        this.remove("Effect.CHANGEOCCURRED");
        super.removeEffect(be,target );
    }
    
    
    public boolean prephase(BattleEvent be, Target t)
    throws BattleEventException {
        processCircumstanceChange(be, t, this,getAbility());
        return false;
    }
    
    
    public void processCircumstanceChange(BattleEvent be, Target t, Effect effect, Ability ability)
    throws BattleEventException {
        String circumstancepresent = effect.getStringValue("Effect.CIRCUMSTANCEPRESENT");
        String activationroll = ability.getStringValue("Disadvantage.LEVEL");
        String changeoccurred = effect.getStringValue("Effect.CHANGEOCCURRED");
        if (changeoccurred == null) {
            changeoccurred = "FALSE";
        }
        int activationlevel = levelToRoll(activationroll);
        
        if (circumstancepresent != null && circumstancepresent.equals("TRUE")) {
            //toss the bones and then compute success or failure of breakout based on all the values recovered above
            Dice roll = new Dice(3,true);
            if (activationroll.equals("Always")) {
                be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " has had an Accidental Change.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " has had an Accidental Change.", BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " has had an Accidental Change.", BattleEvent.MSG_NOTICE);
                effect.add("Effect.CHANGEOCCURRED","TRUE", true);
                Effect e = new effectAccidentalChangeOccurred(effect);
                Undoable u = new ValuePairUndoable(effect, "Effect.SECONDARY", e);
                effect.add( "Effect.SECONDARY", e, true);
                be.addUndoableEvent(u);
                e.addEffect(be, getTarget() );
            }
            if (changeoccurred.equals("FALSE")) {
                if ( roll.getStun().intValue() <= activationlevel) {
                    be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " rolled "  + roll.getStun() + " causing an Accidental Change.  Needed " + activationlevel + " or less.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " rolled "  + roll.getStun() + " causing an Accidental Change.  Needed " + activationlevel + " or less.", BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " rolled "  + roll.getStun() + " causing an Accidental Change.  Needed " + activationlevel + " or less.", BattleEvent.MSG_NOTICE);
                    effect.add("Effect.CHANGEOCCURRED","TRUE", true);
                    Effect e = new effectAccidentalChangeOccurred(effect);
                    Undoable u = new ValuePairUndoable(effect, "Effect.SECONDARY", e);
                    effect.add( "Effect.SECONDARY", e, true);
                    be.addUndoableEvent(u);
                    e.addEffect(be, getTarget() );
                }
                else {
                    be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " rolled " + roll.getStun() + " failing to cause an Accidental Change.    Needed " + activationlevel + " or less." , BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " rolled " + roll.getStun() + " failing to cause an Accidental Change.    Needed " + activationlevel + " or less." , BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " rolled " + roll.getStun() + " failing to cause an Accidental Change.    Needed " + activationlevel + " or less." , BattleEvent.MSG_NOTICE);
                }
            }
        }
        else if (circumstancepresent != null && circumstancepresent.equals("FALSE")) {
            Effect secondary = (Effect)effect.getValue("Effect.SECONDARY");
            if ( secondary != null ) {
                secondary.removeEffect(be, t);
                effect.remove("Effect.SECONDARY");
            }
        }
    }
    
    
    
    
    public String getDescription() {
        Ability ability = getAbility();
        if ( ability != null ) {
            Target target = this.getTarget();
            return "";
        }
        else {
            return "Effect Error";
        }
    }
    
    
    //addactions() is used to add items to the actionstab for abilities and more often effects
    //This follow addActions() actually has a toggling pair of actions that it adds
    public void addActions(Vector v) throws BattleEventException {
        final Ability ability = getAbility();
        
        //triggerRemove contains an actionPerformed that will change Effect.CIRCUMSTANCEPRESENT to false which will
        //eventually case the activation of dependence child effects
        Action triggerRemove = new AbstractAction( ability.getName() + ": Remove Circumstance" ) {
            public void actionPerformed(ActionEvent e){
                Action assignAction = new AbstractAction( "Adjust " + ability.getName() + " State") {
                    
                    public void actionPerformed(ActionEvent e){
                        final Ability currentAbility = ability.getInstanceGroup().getCurrentInstance();
                        Effect effect = getEffect();
                        
                        String circumstancepresent = effect.getStringValue("Effect.CIRCUMSTANCEPRESENT");
                        String newcircumstancepresent = "FALSE";
                        BattleEvent be = (BattleEvent)e.getSource();
                        Undoable u = new ValuePairUndoable(effect, "Effect.CIRCUMSTANCEPRESENT",
                        circumstancepresent, newcircumstancepresent);
                        effect.add( "Effect.CIRCUMSTANCEPRESENT", "FALSE", true);
                        be.addUndoableEvent(u);
                        effect.remove("Effect.CHANGEOCCURRED");
                        try {
                            processCircumstanceChange(be, getAbility().getSource(), effect,getAbility());
                            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( getAbility().getSource() + "'s "  + effect.getName() + " circumstance is no longer active.",BattleEvent.MSG_COMBAT)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( getAbility().getSource() + "'s "  + effect.getName() + " circumstance is no longer active.",BattleEvent.MSG_COMBAT)); // .addMessage( getAbility().getSource() + "'s "  + effect.getName() + " circumstance is no longer active.",BattleEvent.MSG_COMBAT);
                            
                        }
                        catch (BattleEventException bee) {
                        }
                        
                    }
                };
                
                BattleEvent newbe = new BattleEvent( assignAction );
                Battle.currentBattle.addEvent(newbe);
            }
        };
        
        //triggerReplace contains an actionPerformed that will change Effect.CIRCUMSTANCEPRESENT to TRUE which will
        //eventually cause the removal of dependence child effects
        Action triggerReplace = new AbstractAction( ability.getName() + ": Apply Circumstance" ) {
            public void actionPerformed(ActionEvent e){
                Action assignAction = new AbstractAction( "Adjust " + ability.getName() + " State") {
                    
                    public void actionPerformed(ActionEvent e){
                        final Ability currentAbility = ability.getInstanceGroup().getCurrentInstance();
                        Effect effect = getEffect();
                        
                        String circumstancepresent = effect.getStringValue("Effect.CIRCUMSTANCEPRESENT");
                        String newcircumstancepresent = "FALSE";
                        
                        BattleEvent be = (BattleEvent)e.getSource();
                        Undoable u = new ValuePairUndoable(effect, "Effect.CIRCUMSTANCEPRESENT",
                        circumstancepresent, newcircumstancepresent);
                        effect.add( "Effect.CIRCUMSTANCEPRESENT", "TRUE", true);
                        be.addUndoableEvent(u);
                        try {
                            processCircumstanceChange(be, getAbility().getSource(), effect,getAbility());
                        }
                        catch (BattleEventException bee) {
                        }
                    }
                };
                BattleEvent newbe = new BattleEvent( assignAction );
                Battle.currentBattle.addEvent(newbe);
            }
        };
        
        String circumstancepresent = effect.getStringValue("Effect.CIRCUMSTANCEPRESENT");
        if (circumstancepresent == null || circumstancepresent.equals("TRUE")) {
            v.add(triggerRemove);
        }
        else {
            v.add(triggerReplace);
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
    
    public static int levelToRoll(String level) {
        if (level.equals("8")) {
            return 8;
        }
        else if (level.equals("11")) {
            return 11;
        }
        else if (level.equals("14")) {
            return 14;
        }
        return -1;
    }
}

