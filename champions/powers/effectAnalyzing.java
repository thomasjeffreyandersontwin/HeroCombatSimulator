/*
 * effectAnalyzing.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.interfaces.*;
import champions.*;
import champions.event.*;
import champions.exception.*;

import java.util.Vector;
import java.awt.Color;

import javax.swing.*;
import java.awt.event.*;
import javax.swing.*;

import champions.*;
import champions.exception.*;
import champions.interfaces.*;
import champions.powers.*;
/**
 *
 * @author  unknown
 * @version
 */
public class effectAnalyzing extends Effect {
    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    public effectAnalyzing(Ability ability, Target target) {
        super( "Analyzing " + target.getName(), "PERSISTENT", true);
        setAbility(ability);
        setUnique(true);
        setCritical(true);
        setEffectColor(new Color(153,0,153));
        add("Effect.ANALYZETARGET", target, true);
        
    }
    
    public boolean addEffect(BattleEvent be, Target t)  throws BattleEventException {
        
        boolean added = super.addEffect(be,be.getSource() );
        if ( added ) {
            Target analyzetarget = (Target)getValue("Effect.ANALYZETARGET");
            Integer roll = be.getActivationInfo().getIntegerValue( "Attack.ROLL" );
            Integer needed = be.getActivationInfo().getIntegerValue( "Attack.NEEDED" );
            add("Effect.ROLL", roll , true);
            add("Effect.NEEDED",needed, true);
            if (needed != null && roll != null) {
                if (roll.intValue() <= needed.intValue()/2) {
                    be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is successfully analyzing " + analyzetarget, BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is successfully analyzing " + analyzetarget, BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " is successfully analyzing " + analyzetarget, BattleEvent.MSG_NOTICE);
                    be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage("and has discovered EXTENSIVE mannerisms to exploit.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage("and has discovered EXTENSIVE mannerisms to exploit.", BattleEvent.MSG_NOTICE)); // .addMessage("and has discovered EXTENSIVE mannerisms to exploit.", BattleEvent.MSG_NOTICE);
                }
                else if (roll.intValue() + 3 <= needed.intValue()) {
                    be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is successfully analyzing " + analyzetarget, BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is successfully analyzing " + analyzetarget, BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " is successfully analyzing " + analyzetarget, BattleEvent.MSG_NOTICE);
                    be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage("and has discovered distinctive mannerisms to exploit.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage("and has discovered distinctive mannerisms to exploit.", BattleEvent.MSG_NOTICE)); // .addMessage("and has discovered distinctive mannerisms to exploit.", BattleEvent.MSG_NOTICE);
                }
                else if (roll.intValue() + 2 <= needed.intValue()) {
                    be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is successfully analyzing " + analyzetarget, BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is successfully analyzing " + analyzetarget, BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " is successfully analyzing " + analyzetarget, BattleEvent.MSG_NOTICE);
                    be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage("and has discovered information how, where and by whom " + analyzetarget +" was trained.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage("and has discovered information how, where and by whom " + analyzetarget +" was trained.", BattleEvent.MSG_NOTICE)); // .addMessage("and has discovered information how, where and by whom " + analyzetarget +" was trained.", BattleEvent.MSG_NOTICE);
                }
                else if (roll.intValue() <= needed.intValue()) {
                    be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is successfully analyzing " + analyzetarget, BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is successfully analyzing " + analyzetarget, BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " is successfully analyzing " + analyzetarget, BattleEvent.MSG_NOTICE);
                    be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage("and " + t.getName() + " has a general idea of how skill the target is in comparison to source.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage("and " + t.getName() + " has a general idea of how skill the target is in comparison to source.", BattleEvent.MSG_NOTICE)); // .addMessage("and " + t.getName() + " has a general idea of how skill the target is in comparison to source.", BattleEvent.MSG_NOTICE);
                }
                
            }
        }
        return added;
    }
    
    public void removeEffect(BattleEvent be, Target t)  throws BattleEventException {
        super.removeEffect(be,t);
        Target analyzetarget = (Target)getValue("Effect.ANALYZETARGET");
        be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is no longer analyzing " + analyzetarget, BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is no longer analyzing " + analyzetarget, BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " is no longer analyzing " + analyzetarget, BattleEvent.MSG_NOTICE);
    }
    
    public void addDCVDefenseModifiers( CVList cvList , Ability attack) {
        Target analyzetarget = (Target)getValue("Effect.ANALYZETARGET");
        if (analyzetarget.getName().equals(cvList.getSourceName())) {
            Integer roll = (Integer)getValue("Effect.ROLL" );
            Integer needed = (Integer)getValue( "Effect.NEEDED" );
            
            if (needed != null && roll != null) {
                if (roll.intValue() + 3 <= needed.intValue()) {
                    cvList.addTargetCVModifier( this.getName(), 1);
                }
            }
        }
    }
    
    public void skillIsActivating(BattleEvent be, Ability ability)  {
        Ability effectAbility = getAbility();
        Ability maneuver = be.getManeuver();
        
        int index,count;
        Ability a;
        boolean activate = false;
        
        System.out.println(ability.getSource().getName());
        
        Target analyzetarget = (Target)getValue("Effect.ANALYZETARGET");
        if (analyzetarget.getName().equals(ability.getSource().getName()) ||
            analyzetarget.getName().equals(ability.getSource().getName()) ) {
            Integer roll = (Integer)getValue("Effect.ROLL" );
            Integer needed = (Integer)getValue( "Effect.NEEDED" );
            
            if (needed != null && roll != null) {
                if (roll.intValue() <= needed.intValue()/2) {
                    Integer level =  effectAbility.getIntegerValue("Power.SKILLLEVEL");
                    add("Effect.SKILLLEVEL", new Integer(2), true);
                    add("Power.LEVELTYPE", "Overall Level", true);
                }
                else {
                    add("Effect.SKILLLEVEL", new Integer(0), true);
                }
            }
        }
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
    Ability ability = getAbility();
    Target analyzetarget = (Target)getValue("Effect.ANALYZETARGET");
    Action assignAction = new AbstractAction("Stop analyzing " + analyzetarget ) {
        
        
        // Define a method in your anonymous class called actionPerformed.
        // The GUI code knows that when you add an anonymous class of type
        // Action to the Vector v, that it can take that anonymous class
        // and execute the actionPerformed method shown below.
        
        public void actionPerformed(ActionEvent e) {
            // Do your work.  This is where all the guts of the anonymous
            // class have to be located.  Nothing can be outside this
            // method.
            
            BattleEvent newbe = new BattleEvent(BattleEvent.REMOVE_EFFECT, effectAnalyzing.this, effectAnalyzing.this.getTarget());
            if ( Battle.currentBattle != null ) {
                Battle.currentBattle.addEvent( newbe );
                // newbe.setENDPaid(true);
                
            }
            
            
        } // <--- This closes the method actionPerformed
    }; // <--- This closes the anonymous class you just created.
    
    actions.add(assignAction); //  <--- Add the class to the Vector so the gui knows to
    //  to display it in the actions menu.
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


}