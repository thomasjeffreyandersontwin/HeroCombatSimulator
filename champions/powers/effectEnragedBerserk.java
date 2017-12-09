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
import champions.Target;
import champions.exception.BattleEventException;
import champions.parameters.ParameterList;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 *
 * @author  unknown
 * @version
 */
public class effectEnragedBerserk extends Effect {
    
    /** Creates new effectUnconscious */
    static private Ability unjam = null;
    
    public effectEnragedBerserk(Ability ability) {
        super( "Enraged/Berserk", "PERSISTENT", true);
        // super( "Jammed", "PERSISTENT", true);
        setAbility(ability);
        setUnique(false);
        add("effect.RECOVERYATTEMPTED","FALSE",true);
        
        setCritical(true);
        setEffectColor(new Color(153,0,153));
    }
    
    public boolean addEffect(BattleEvent be, Target t)  throws BattleEventException {
        
        boolean added = super.addEffect(be,t);
        if ( added ) {
            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is Enraged/Berserk!", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is Enraged/Berserk!", BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " is Enraged/Berserk!", BattleEvent.MSG_NOTICE);
        }
        
        return added;
    }
    
    public void removeEffect(BattleEvent be, Target t)  throws BattleEventException {
        Dice roll = new Dice(3,true);
        Ability a = this.getAbility();
        //String recovery = a.getStringValue("Disadvantage.RECOVERY");
        ParameterList pl = a.getPower().getParameterList(a);
        String recovery = pl.getParameterStringValue("RecoveryRoll");
        int recoverylevel = levelToRoll(recovery);
        if ( roll.getStun().intValue() <= recoverylevel ) {
            super.removeEffect(be,t);
            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is no longer Enranged/Berserk!  Needed " + recoverylevel + ". Rolled " + roll.getStun().intValue() + ".", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is no longer Enranged/Berserk!  Needed " + recoverylevel + ". Rolled " + roll.getStun().intValue() + ".", BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " is no longer Enranged/Berserk!  Needed " + recoverylevel + ". Rolled " + roll.getStun().intValue() + ".", BattleEvent.MSG_NOTICE);
        }
        else {
            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " did not recover from Enranged/Berserk!  Needed " + recoverylevel + ". Rolled " + roll.getStun().intValue() + ".", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " did not recover from Enranged/Berserk!  Needed " + recoverylevel + ". Rolled " + roll.getStun().intValue() + ".", BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " did not recover from Enranged/Berserk!  Needed " + recoverylevel + ". Rolled " + roll.getStun().intValue() + ".", BattleEvent.MSG_NOTICE);
        }
        add("effect.RECOVERYATTEMPTED","TRUE",true);
    }
    
    public boolean postturn(BattleEvent be, Target t)
    throws BattleEventException {
        if ( this.getStringValue("effect.RECOVERYATTEMPTED").equals("FALSE")) {
            removeEffect(be, t);
        }
        add("effect.RECOVERYATTEMPTED","FALSE",true);
        return false;
    }
    public Ability getAbility() {
        return (Ability)getValue("Effect.ABILITY");
    }
    /** Setter for property sense.
     * @param sense New value of property sense.
     */
    public void setAbility(Ability ability) {
        add("Effect.ABILITY", ability, true);
    }
    
    public void addActions(Vector actions) {
        
        // The Next line creates the anonymous class.  It is anonymous, cause you
        // are not naming the class anything.
        //
        // The anonymous class is a subclass of AbstractAction, which is in turn
        // a subclass of Action.
        final Ability ability = getAbility();
        Action assignAction = new AbstractAction("Roll Recovery For" + ability.getName()) {
            
            // Define a method in your anonymous class called actionPerformed.
            // The GUI code knows that when you add an anonymous class of type
            // Action to the Vector v, that it can take that anonymous class
            // and execute the actionPerformed method shown below.
            
            public void actionPerformed(ActionEvent e) {
                // Do your work.  This is where all the guts of the anonymous
                // class have to be located.  Nothing can be outside this
                // method.
                
                BattleEvent newbe = new BattleEvent(BattleEvent.REMOVE_EFFECT, effectEnragedBerserk.this, effectEnragedBerserk.this.getTarget());
                if ( Battle.currentBattle != null ) {
                    Battle.currentBattle.addEvent( newbe );
                    // newbe.setENDPaid(true);
                    
                }
                
                
            } // <--- This closes the method actionPerformed
        }; // <--- This closes the anonymous class you just created.
        
        actions.add(assignAction); //  <--- Add the class to the Vector so the gui knows to
        //  to display it in the actions menu.
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