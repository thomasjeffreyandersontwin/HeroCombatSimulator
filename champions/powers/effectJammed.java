/*
 * effectUnconscious.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.Ability;
import champions.Battle;
import champions.BattleEvent;
import champions.Effect;
import champions.Target;
import champions.battleMessage.EffectSummaryMessage;
import champions.exception.BattleEventException;
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
public class effectJammed extends Effect {
    
    /** Creates new effectUnconscious */
    static private Ability unjam = null;
    
    public effectJammed(Ability ability) {
        super( ability.getName() +" Jammed", "PERSISTENT", true);
        // super( "Jammed", "PERSISTENT", true);
        setAbility(ability);
        setUnique(true);
        setCritical(true);
        setEffectColor(new Color(153,0,153));
    }
    
    public boolean addEffect(BattleEvent be, Target t)  throws BattleEventException {
        
        boolean added = super.addEffect(be,t);
        if ( added ) {
            //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is Jammed!", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is Jammed!", BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " is Jammed!", BattleEvent.MSG_NOTICE);
            be.addBattleMessage( new EffectSummaryMessage(t, this, true));
        }
        return added;
    }
    
    public void removeEffect(BattleEvent be, Target t)  throws BattleEventException {
        super.removeEffect(be,t);
        //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is no longer Jammed!", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is no longer Jammed!", BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " is no longer Jammed!", BattleEvent.MSG_NOTICE);
        be.addBattleMessage( new EffectSummaryMessage(t, this, false));
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
        Action assignAction = new AbstractAction("Unjam " + ability.getName()) {
            
            // Define a method in your anonymous class called actionPerformed.
            // The GUI code knows that when you add an anonymous class of type
            // Action to the Vector v, that it can take that anonymous class
            // and execute the actionPerformed method shown below.
            
            public void actionPerformed(ActionEvent e)
            {
                // Do your work.  This is where all the guts of the anonymous
                // class have to be located.  Nothing can be outside this
                // method.
                
                BattleEvent newbe = new BattleEvent(BattleEvent.REMOVE_EFFECT, effectJammed.this, effectJammed.this.getTarget());
                if ( Battle.currentBattle != null ) {
                    Battle.currentBattle.addEvent( newbe );
                   // newbe.setENDPaid(true);

                }
                
                
            } // <--- This closes the method actionPerformed
        }; // <--- This closes the anonymous class you just created.
        
        actions.add(assignAction); //  <--- Add the class to the Vector so the gui knows to
        //  to display it in the actions menu.
    }
}