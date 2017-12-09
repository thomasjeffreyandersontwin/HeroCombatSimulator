/*
 * effectGrabbed.java
 *
 * Created on April 22, 2001, 7:29 PM
 */

package champions.powers;

import champions.*;
import champions.exception.*;
import champions.interfaces.AbilityIterator;
import java.util.Vector;
/**
 *
 * @author  twalker
 * @version 
 */
public class effectGrabbed extends Effect {
    
    /** Holds value of property grabStrength. */
    private int grabStrength;
    
        public effectGrabbed(Ability ability, Ability maneuver, Target source) {
            super ( "Grabbed by " + source.getName(), "PERSISTENT", true );
            setAbility(ability);
            setManeuver(maneuver);
            setGrabber(source);
        }

        public boolean addEffect(BattleEvent be, Target target)
        throws BattleEventException {
            effectGrabbing grabbingEffect = new effectGrabbing(getAbility(), getManeuver(), target, this);
            if ( grabbingEffect.addEffect(be,getGrabber()) ) {
                setGrabbingEffect( grabbingEffect );
                return super.addEffect(be,target);
            }
            return false;
        }

        public void removeEffect(BattleEvent be, Target target)
        throws BattleEventException {
            super.removeEffect(be,target);
            if ( getGrabber().hasEffect(getGrabbingEffect() )  ) {
                getGrabbingEffect().removeEffect(be,getGrabber());
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
        public Target getGrabber() {
            return (Target)getValue("Grab.SOURCE");
        }
        /** Setter for property sense.
         * @param sense New value of property sense.
         */
        public void setGrabber(Target source) {
            add("Grab.SOURCE", source,true);
        }

        /** Getter for property sense.
         * @return Value of property sense.
         */
        public effectGrabbing getGrabbingEffect() {
            return (effectGrabbing)getValue("Grab.EFFECT");
        }
        /** Setter for property sense.
         * @param sense New value of property sense.
         */
        public void setGrabbingEffect(effectGrabbing effect) {
            add("Grab.EFFECT", effect,true);
        }

        public void addDCVDefenseModifiers( CVList cvList, Ability attack ) {
            cvList.addTargetCVMultiplier( "Grabbed", 0.5);
        }

        public void addActions(Vector actions) {
            
            boolean found = false;
            AbilityIterator ai = getTarget().getAbilities();
            while(ai.hasNext() ) {
                Ability a = ai.nextAbility();
                if ( a.getPower() instanceof maneuverEscape ) {
                    AbilityAction action = new AbilityAction( "Escape from " + getGrabber().getName() + " using " + a.getName(), a );
                    action.setType(  BattleEvent.ACTIVATE );
                    action.setSource( this.getTarget() );
            
                    DetailList dl = new DetailList();
                    dl.add("Escape.EFFECTGRABBED", this, true);
                    action.setBeExtra(dl);

                    actions.add(action);
                    
                    found = true;
                }
            }
            
            if ( ! found ) {
                Ability a = getEscapeAbility( getGrabber() );
                AbilityAction action = new AbilityAction( "Escape from " + getGrabber().getName() + " using " + a.getName(), a );
                action.setType(  BattleEvent.ACTIVATE );
                action.setSource( this.getTarget() );

                DetailList dl = new DetailList();
                dl.add("Escape.EFFECTGRABBED", this, true);
                action.setBeExtra(dl);

                actions.add(action);
            }
        }

        public Ability getEscapeAbility(Target grabber) {
            Object o = getValue("Escape.ABIILTY" );
            Ability escapeAbility;
            if ( o == null ) {
                escapeAbility = new Ability("Escape from Grab");
                escapeAbility.addPAD( new maneuverEscape(), null);
                this.add("Escape.ABILITY", escapeAbility, true);
            }
            else {
                escapeAbility = (Ability)o;
            }

            return escapeAbility;
        }
        
        /** Getter for property grabStrength.
         * @return Value of property grabStrength.
         */
        public int getGrabStrength() {
            return this.grabStrength;
        }
        
        /** Setter for property grabStrength.
         * @param grabStrength New value of property grabStrength.
         */
        public void setGrabStrength(int grabStrength) {
            this.grabStrength = grabStrength;
        }
        
    } // End effectGrabbed
