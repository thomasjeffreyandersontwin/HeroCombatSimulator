/*
 * effectDying.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.interfaces.*;
import champions.*;
import champions.event.*;
import champions.exception.*;

import java.awt.Color;
/**
 *
 * @author  unknown
 * @version
 */
public class effectDependenceAdjustment extends Effect {
    
    
    
    private Effect originatingEffect;
    
    private Ability ability;
    
    static private String[] timeIntervalOptions = {"1 Segment", "1 Phase","1 Turn", "1 Minute", "5 Minutes", "20 Minutes", "1 Hour", "6 Hours", "1 Day",
    "1 Week", "1 Month", "1 Season", "1 Year", "5 Years", "25 Years", "1 Century" };
    
    
    /** Creates new effectUnconscious */
    public effectDependenceAdjustment(Ability ability, Effect effect) {
        super( "Dependence Adjustment", "PERSISTENT", true);
        //setUnique(true);
        setCritical(true);
        setEffectColor(Color.red);
        setAbility(ability);
        setOriginatingEffect(effect);
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
        Battle cb = Battle.getCurrentBattle();
        String dependenceeffecttype = this.getAbility().getStringValue("Disadvantage.EFFECT");
        Ability targetAbility;
        int sindex, i, count;
        AbilityIterator ai = target.getAbilities();
        while ( ai.hasNext() ) {
            targetAbility = ai.nextAbility();
            if (targetAbility.isPower()) {
                
                Effect penalty = new Effect("Dependance Penalty");
                //for each Ability a...
                if (targetAbility != null ) {
                    
                    // To track the amount...
                    // First lookup the ability to see if there is a previous entry
                    int index = findExactIndexed("Penalty", "ABILITY", targetAbility);
                    
                    // If index is -1, the there isn't an entry
                    if ( index != -1 ) {
                        // Get the previous amount
                        Integer value = getIndexedIntegerValue(index, "Penalty", "AMOUNT");
                        if (value != null) {
                            // Decrease the amount and save the value
                            addIndexed(index, "Penalty", "AMOUNT", new Integer(value.intValue() - value.intValue()), true);
                            
                            int dindex = penalty.addAidSubeffect(targetAbility.getName() + "Penalty","ABILITY", targetAbility, value.intValue());
                            // Remove the default power defense settings...
                            penalty.setSubeffectDefense(dindex, "NONE", "NONE");
                            // Apply the penalty effect to the target....
                            // The be should already be available via whereever you put this code
                            penalty.addEffect(be, target);
                            value = getIndexedIntegerValue(index, "Penalty", "AMOUNT");
                        }
                        
                        if (value.intValue() == 0) {
                            removeAllIndexed(index, "Penalty");
                            //break;
                        }
                    }
                    
                }
                
            }
        }
        super.removeEffect(be,target );
    }
    
    
    
    
    public boolean presegment(BattleEvent be, Target t)
    throws BattleEventException {
        String substanceavailable = getOriginatingEffect().getStringValue("Effect.SUBSTANCEAVAILABLE");
        Battle cb = Battle.getCurrentBattle();
        Chronometer time = (Chronometer)Battle.getCurrentBattle().getTime().clone();
        long currenttime = cb.getTime().getTime();
        String timeinterval = this.getAbility().getStringValue("Disadvantage.TIME");
        String dependenceeffecttype = this.getAbility().getStringValue("Disadvantage.EFFECT");
        long secondsdelay = this.getDelayInterval(this.getAbility(),1);
        Long starttime = getOriginatingEffect().getLongValue("Effect.STARTTIME" );
        if (substanceavailable != null && substanceavailable.equals("FALSE")) {
            if (time.isActivePhase(t.getEffectiveSpeed(time)) && timeinterval.equals("1 Phase" )) {
                applyAdjustmentPenalty(be,t);
            }
            else if ((currenttime - starttime.intValue() >= secondsdelay) && !timeinterval.equals("1 Phase")) {
                applyAdjustmentPenalty(be,t);
                
            }
        }
        
        return false;
    }
    
    
    public void applyAdjustmentPenalty(BattleEvent be, Target t)
    throws BattleEventException {
        Battle cb = Battle.getCurrentBattle();
        String dependenceeffecttype = this.getAbility().getStringValue("Disadvantage.EFFECT");
        
        if (dependenceeffecttype.endsWith("Active Points from Affected Power")) {
            Ability targetAbility;
            int sindex, i, count;
            AbilityIterator ai = t.getAbilities();
            while ( ai.hasNext() ) {
                
                targetAbility = ai.nextAbility();
                if (targetAbility.isPower()) {
                    Effect penalty = new Effect("Dependance Penalty");
                    //for each Ability a...
                    if (targetAbility != null ) {
                        //grab the proper adjustment penalty from the disadvantage
                        int amount = 0;
                        if (dependenceeffecttype.startsWith("-10")) {
                            amount = 10;
                        }
                        else if (dependenceeffecttype.startsWith("-20")) {
                            amount = 20;
                        }
                        else if (dependenceeffecttype.startsWith("-30")) {
                            amount = 30;
                        }
                        int dindex = penalty.addDrainSubeffect(targetAbility.getName() + "Penalty","ABILITY", targetAbility, amount);
                        
                        // Remove the default power defense settings...
                        penalty.setSubeffectDefense(dindex, "NONE", "NONE");
                        // Apply the penalty effect to the target....
                        // The be should already be available via whereever you put this code
                        penalty.addEffect(be, t);
                        
                        // To track the amount...
                        // First lookup the ability to see if there is a previous entry
                        int index = findExactIndexed("Penalty", "ABILITY", targetAbility);
                        // If index is -1, the there isn't an entry
                        if ( index == -1 ) {
                            
                            // Create the index and set the amount
                            index = createIndexed("Penalty", "ABILITY", targetAbility, false);
                            addIndexed(index, "Penalty", "AMOUNT", new Integer(amount), true);
                        }
                        else {
                            // Get the previous amount
                            Integer value = getIndexedIntegerValue(index, "Penalty", "AMOUNT");
                            // Check if there was no value, just for safety...
                            if ( value == null ) value = new Integer(0);
                            
                            // Increase the amount and save the value
                            addIndexed(index, "Penalty", "AMOUNT", new Integer(value.intValue()+ amount), true);
                        }
                    }
                    
                }
            }
        }
        getOriginatingEffect().add("Effect.STARTTIME",new Long(cb.getTime().getTime()), true);
        
    }
    
    
    private long getDelayInterval(Ability ability, int index) {
        String timeinterval = this.getAbility().getStringValue("Disadvantage.TIME");
        double cost = 0.0;
        
        int i;
        
        for (i = 0;i< timeIntervalOptions.length;i++) {
            if ( timeinterval.equals(timeIntervalOptions[i] )) break;
        }
        // After the above calculation, i should correspond to the time chart in the ChampionsConstants file.
        return ChampionsUtilities.calculateSeconds(i, 1,ability.getSource());
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
    
    
    /** Getter for property battleEvent.
     * @return Value of property battleEvent.
     */
    public Effect getOriginatingEffect() {
        return originatingEffect;
    }
    
    /** Setter for property battleEvent.
     * @param battleEvent New value of property battleEvent.
     */
    public void setOriginatingEffect(Effect effect) {
        this.originatingEffect = effect;
    }
    
    
}