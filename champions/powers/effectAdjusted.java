/*
 * effectDrainTracker.java
 *
 * Created on March 6, 2002, 10:33 PM
 */

package champions.powers;

import champions.Ability;
import champions.BattleEvent;
import champions.ChampionsUtilities;
import champions.Characteristic;
import champions.Chronometer;
import champions.Effect;
import champions.FadeTracker;
import champions.Target;
import champions.exception.BattleEventException;


/**
 *
 * @author  Trevor Walker
 * @version
 */
public class effectAdjusted extends Effect {
    
    /** Creates new effectDrainTracker */
    public effectAdjusted(Object adjustable, FadeTracker ft) {
        super(adjustable.toString() + " adjusted", "PERSISTENT", false);
        setAdjustable(adjustable);
        setFadeTracker(ft);
        setHidden(false);
    }
    
    public Object getAdjustable() {
        return getValue("Effect.ADJUSTMENTTARGET");
    }

    public void setAdjustable(Object adjustable) {
        add("Effect.ADJUSTMENTTARGET", adjustable, true, false);
        
        
    }
    
    public FadeTracker getFadeTracker() {
        return (FadeTracker)getValue("Effect.FADETRACKER");
                
    }

    public void setFadeTracker(FadeTracker fadeTracker) {
        add("Effect.FADETRACKER",fadeTracker,true);
        updateName();
    }
    
    public void updateName() {
        FadeTracker ft = getFadeTracker();
        Object adjustable = getAdjustable();
        
        if ( ft == null ) {
            setName( adjustable.toString() + " adjusted");
        }
        else {
            double aid = ft.getAidAdjustment();
            double drain = ft.getDrainAdjustment();
            double heal = ft.getHealAdjustment();
            
            if ( aid != 0 && drain == 0 && heal == 0 ) {
                setName(adjustable.toString() + " aided");
            }
            else if ( aid == 0 && drain != 0 && heal == 0 ) {
                setName(adjustable.toString() + " drained");
            }
            else if ( aid == 0 && drain == 0 && heal != 0 ) {
                setName(adjustable.toString() + " healed");
            }
            else {
                setName( adjustable.toString() + " adjusted");
            }
        }
    }
    
    public String getDescription() {
        FadeTracker ft = getFadeTracker();
        Object adjustable = getAdjustable();
        
        StringBuffer sb = new StringBuffer();
        
        if ( adjustable instanceof Ability ) {
            sb.append("Adjusted Ability: ").append(adjustable.toString()).append("\n");
        }
        else {
            sb.append("Adjusted Stat: ").append(adjustable.toString()).append("\n");
        }
        
        int total = (int)Math.round(ft.getAidAdjustment() + ft.getDrainAdjustment() + ft.getHealAdjustment());
        
        sb.append("Total Adjustment: ").append( ChampionsUtilities.toSignedString(total)).append("\n");
        
        sb.append("\nIndividual Adjustments:\n");
        
        int count = ft.getFaderCount();
        for(int i = 0; i < count; i++) {
            // Loop through all the trackers and build a 
            // description of all the modifications...
            String type = ft.getAdjustmentType(i);
            Target source = ft.getSourceTarget(i);
            Chronometer time = ft.getNextTime(i);
            double amount = ft.getAmount(i);
            
            long interval = ft.getDecayInterval(i);
            int rate = ft.getDecayRate(i);
            
            int timeStep = ChampionsUtilities.calculateTimeChartDifference(0, interval);
            long secondPerStep = ChampionsUtilities.getSecondsInTimestep(timeStep);
            String timeStepString = ChampionsUtilities.getTimeString(timeStep);
            
            long intervalX = interval / secondPerStep;
            
            
            sb.append(" ").append( amount ).append(" ").append(type).append(" by ").append(source.getName()).append(".\n");
            sb.append("   Next Fade: ").append( time ).append(".\n");
            sb.append("   Fade Rate: ").append( rate ).append(" cp / ");
            if ( intervalX > 1 ) {
                sb.append(intervalX).append(" ");
            }
            sb.append(timeStepString).append("\n");
            
            
        }
        
        return sb.toString();
    }
    
    
    /** Searches the Target for an effectAdjusted for the indicated power or characteristic.
     *
     *  Returns null if none is found.
     */
    public static effectAdjusted findAdjustedEffect(Target target, Object adjustable) {
        int count = target.getEffectCount();
        for(int i = 0; i < count; i++) {
            Effect e = target.getEffect(i);
            if ( e instanceof effectAdjusted ) {
                Object a = ((effectAdjusted)e).getAdjustable();
                if ( adjustable != null && adjustable.equals(a)) {
                    return (effectAdjusted)e;
                }
            }
        }
        return null;
    }
    
    /** Finds/Creates an effectAdjusted effect on the indicated target for the indicated power or characteristic.
     *
     * This method will search the target for the appropriate effect.  If it is not found, it will create
     * one and post an undoable to the battle event appropriately.
     */
    public static effectAdjusted getAdjustedEffect(BattleEvent be, Target target, Object adjustable, FadeTracker ft) throws BattleEventException {
           effectAdjusted ea = findAdjustedEffect(target, adjustable);
           
           if (ea == null ) {
               ea = new effectAdjusted(adjustable, ft);
               ea.addEffect(be, target);
           }
           
           return ea;
    }

    
}
