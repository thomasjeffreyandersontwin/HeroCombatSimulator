/*
 * effectDisarmed.java
 *
 * Created on April 22, 2001, 7:29 PM
 */
package champions.powers;

import champions.*;
import champions.exception.*;
import champions.battleMessage.GenericSummaryMessage;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.event.ActionEvent;
import java.awt.Color;

/**
 *
 * @author  pnoffke
 * @version 
 */
public class effectDisarmed extends Effect {

    public effectDisarmed(Target source) {
        super("Disarmed by " + source.getName(), "PERSISTENT", true);
        setCritical(true);
        setEffectColor(new Color(153, 0, 153));
    }

    public boolean addEffect(BattleEvent be, Target target)
            throws BattleEventException {
        be.addBattleMessage(new GenericSummaryMessage(target, " is disarmed"));
        return super.addEffect(be, target);
    }

    public void removeEffect(BattleEvent be, Target target)
            throws BattleEventException {
        be.addBattleMessage(new GenericSummaryMessage(target, " is no longer disarmed"));
        super.removeEffect(be, target);
    }

    public void addActions(Vector actions) {

        // The Next line creates the anonymous class.  It is anonymous, cause you
        // are not naming the class anything.
        //
        // The anonymous class is a subclass of AbstractAction, which is in turn
        // a subclass of Action.
        /**
         * @todo Implement weapons and get weapon name.
         */
//            Weapon weapon = getWeapon();
//            Action assignAction = new AbstractAction("Repair " + weapon.getName()) {
        Action assignAction = new AbstractAction("Retrieve disarmed") {

            // Define a method in your anonymous class called actionPerformed.
            // The GUI code knows that when you add an anonymous class of type
            // Action to the Vector v, that it can take that anonymous class
            // and execute the actionPerformed method shown below.
            public void actionPerformed(ActionEvent e) {
                // Do your work.  This is where all the guts of the anonymous
                // class have to be located.  Nothing can be outside this
                // method.

                BattleEvent newbe = new BattleEvent(BattleEvent.REMOVE_EFFECT,
                        effectDisarmed.this, effectDisarmed.this.getTarget());
                if (Battle.currentBattle != null) {
                    Battle.currentBattle.addEvent(newbe);
                }


            } // <--- This closes the method actionPerformed
            }; // <--- This closes the anonymous class you just created.

        actions.add(assignAction); //  <--- Add the class to the Vector so the gui knows to
    //  to display it in the actions menu.
    }
} // End effectDisarmbed
