/*
 * effectGrabbing.java
 *
 * Created on April 22, 2001, 7:31 PM
 */

package champions.powers;

import java.awt.event.*;
import javax.swing.*;
import champions.*;
import champions.exception.*;
import champions.battleMessage.GenericSummaryMessage;
import java.util.Vector;

/**
 *
 * @author  twalker
 * @version 
 */

public class effectGrabbing extends Effect {
        public effectGrabbing(Ability ability, Ability maneuver, Target target, effectGrabbed grabbed) {
            super ( "Grabbing  " + target.getName(), "PERSISTENT", true );
            setAbility(ability);
            setManeuver(maneuver);
            setGrabbie(target);
            setGrabbedEffect(grabbed);
        }

        public boolean addEffect(BattleEvent be, Target target)
        throws BattleEventException {
            be.addBattleMessage( new GenericSummaryMessage(getGrabbie(), " is grabbed"));
            return super.addEffect(be,target);
        }

        public void removeEffect(BattleEvent be, Target target)
        throws BattleEventException {
            be.addBattleMessage( new GenericSummaryMessage(getGrabbie(), " is no longer grabbed"));
            super.removeEffect(be,target);
            if ( getGrabbie().hasEffect(getGrabbedEffect() )  ) {
                getGrabbedEffect().removeEffect(be,getGrabbie());
            }
        }
        /** Getter for property sense.
         * @return Value of property sense.
         */
        public Ability getAbility() {
            return (Ability)getValue("Effect.ABILITY");
        }
        /** Setter for property sense.
         * @param sense New value of property sense.
         */
        public void setAbility(Ability ability) {
            add("Effect.ABILITY", ability, true);
        }

        /** Getter for property sense.
         * @return Value of property sense.
         */
        public Ability getManeuver() {
            return (Ability)getValue("Effect.MANEUVER");
        }
        /** Setter for property sense.
         * @param sense New value of property sense.
         */
        public void setManeuver(Ability ability) {
            add("Effect.MANEUVER", ability, true);
        }
        /** Getter for property sense.
         * @return Value of property sense.
         */
        public Target getGrabbie() {
            return (Target)getValue("Grab.TARGET");
        }
        /** Setter for property sense.
         * @param sense New value of property sense.
         */
        public void setGrabbie(Target source) {
            add("Grab.TARGET", source,true);
        }

        /** Getter for property sense.
         * @return Value of property sense.
         */
        public effectGrabbed getGrabbedEffect() {
            return (effectGrabbed)getValue("Grab.EFFECT");
        }
        /** Setter for property sense.
         * @param sense New value of property sense.
         */
        public void setGrabbedEffect(effectGrabbed effect) {
            add("Grab.EFFECT", effect,true);
        }

        public void addActions(Vector actions) {
            Ability a = getThrowAbility( getGrabbie() );
            AbilityAction action = new AbilityAction( "Throw " + getGrabbie().getName(), a );
            action.setType(  BattleEvent.ACTIVATE );
            action.setSource( this.getTarget() );
            DetailList dl = new DetailList();
            dl.add("BattleEvent.THROWNOBJECT", getGrabbie(), true);
            action.setBeExtra( dl );

            actions.add(action);

            a = getSqueezeAbility( getGrabbie() );
            action = new AbilityAction( "Squeeze " + getGrabbie().getName(), a );
            action.setType(  BattleEvent.ACTIVATE );
            action.setSource( this.getTarget() );
            dl = new DetailList();
            dl.add("BattleEvent.SQUEEZEDOBJECT", getGrabbie(), true);
            action.setBeExtra( dl );
            actions.add(action);

            Action action2 = new AbstractAction ( "Release " + getGrabbie() ) {
                public void actionPerformed( ActionEvent e ) {
                    BattleEvent be = new BattleEvent( BattleEvent.REMOVE_EFFECT, effectGrabbing.this, getTarget() );
                    Battle.currentBattle.addEvent(be);

                }
            };
            actions.add(action2);
        }

        public Ability getThrowAbility(Target target) {
            Object o = getValue("Throw.ABILITY" );

            Ability throwAbility;
            if ( o == null ) {
                throwAbility = new Ability("Throw Grabbed");
                throwAbility.addPAD( new maneuverThrowFromGrab(), null);
                //throwAbility.createIndexed( "Target","TARGET", target);
                this.add("Throw.ABILITY", throwAbility,true);
            }
            else {
                throwAbility = (Ability)o;
            }
            return throwAbility;
        }

        public Ability getSqueezeAbility(Target target) {
            Object o = getValue("Squeeze.ABILITY" );

            Ability squeezeAbility ;
            if ( o == null ) {
                squeezeAbility = new Ability("Squeeze Grabbed");
                squeezeAbility.addPAD( new maneuverSqueezeFromGrab(), null);
                squeezeAbility.createIndexed( "Target", "TARGET", target );
                this.add("Squeeze.ABILITY",  squeezeAbility,true );
            }
            else {
                squeezeAbility = (Ability)o;
            }
            return squeezeAbility;
        }
    }
