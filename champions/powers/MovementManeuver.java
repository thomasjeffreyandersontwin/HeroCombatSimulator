/*
 * MovementManeuver.java
 *
 * Created on February 8, 2008, 3:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.powers;

import champions.Ability;
import champions.BattleEvent;
import champions.ChampionsUtilities;
import champions.Power;
import champions.Target;

/**  Common parent for movement manevuers MoveThrough and MoveBy.
 *
 * @author twalker
 */
public abstract class MovementManeuver extends Power {
    
    /**
     * Creates a new instance of MovementManeuver
     */
    public MovementManeuver() {
    }

    
    public static int getMaximumVelocity(BattleEvent be) {
        Ability movementAbility = getMovementAbility(be);
        if ( movementAbility == null ) return 0;
        
        int distanceMoved = getMovementDistance(be);
        return Math.min(movementAbility.getRange(), distanceMoved * 5);
    }

    
    public static Ability getMovementAbility(BattleEvent be) {
        return (Ability) be.getValue("MovementManeuver.MOVEMENT");
    }

    
    public static int getMovementDistance(BattleEvent be) {
        
        Integer i = be.getIntegerValue("MovementManeuver.MOVEDISTANCE");
        return ( i == null ? 0 : i.intValue());
    }

    
    public static int getVelocity(BattleEvent be) {
        Integer i = be.getIntegerValue("MovementManeuver.VELOCITY");
        return ( i == null ? 0 : i.intValue());
    }

    
    public static boolean isMovementDistanceSet(BattleEvent be) {
        return be.contains("MovementManeuver.MOVEDISTANCE");
    }

    
    public static boolean isVelocitySet(BattleEvent be) {
        return be.contains("MovementManeuver.VELOCITY");
    }

    
    public static void setMovementAbility(BattleEvent be, Ability movementAbility) {
        Ability oldAbility = getMovementAbility(be);
        
        if ( movementAbility != oldAbility ) {
            be.add("MovementManeuver.MOVEMENT", movementAbility, true); 
            setupMovementManeuverBattleEvent(be);
        }
    }

    
    public static void setMovementDistance(BattleEvent be, int value) {
        int oldDistance = getMovementDistance(be);
        if ( oldDistance != value || isMovementDistanceSet(be) == false ) {
            
            be.add("MovementManeuver.MOVEDISTANCE", value, true);
            setupMovementManeuverBattleEvent(be);
        }
    }

    
    public static void setVelocity(BattleEvent be, int velocity) {
        be.add("MovementManeuver.VELOCITY", velocity, true);
    }

    
    public static void setupMovementManeuverBattleEvent(BattleEvent battleEvent) {
        Target source = battleEvent.getSource();
        
        Ability movementAbility = getMovementAbility(battleEvent);
        if (movementAbility != null) {
            boolean fullPhase = source.hasFullPhase();
            if (isMovementDistanceSet(battleEvent) == false) {
                
                if (fullPhase) {
                    setMovementDistance(battleEvent, movementAbility.getRange());
                }
                else {
                    setMovementDistance(battleEvent, ChampionsUtilities.roundValue(movementAbility.getRange()/2.0,true));
                }
            }
            else {
                int distance = getMovementDistance(battleEvent);
                int maxDistance = movementAbility.getRange();
                int halfDistance = ChampionsUtilities.roundValue(movementAbility.getRange()/2.0,true);

                if ( fullPhase ) {
                    if ( distance >= maxDistance ) {
                        setMovementDistance(battleEvent,maxDistance);
                    }
                }
                else {
                    if ( distance >= halfDistance ) {
                        setMovementDistance(battleEvent,halfDistance);
                    }
                }
            }
            
            //if ( isVelocitySet(battleEvent) == false ) {
                int distanceMoved = getMovementDistance(battleEvent);
            setVelocity(battleEvent, Math.min(movementAbility.getRange(), distanceMoved * 5)  );
            //}
        }
    }
    
}
