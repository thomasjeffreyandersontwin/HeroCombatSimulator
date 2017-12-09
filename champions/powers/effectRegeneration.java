/*
 * effectAdjustment.java
 *
 * Created on September 8, 2001, 9:45 PM
 */

package champions.powers;


import champions.*;
import champions.Power;
import champions.battleMessage.GainedEffectSummaryMessage;
import champions.interfaces.*;
import champions.exception.*;
import champions.parameters.ParameterList;

/**
 *
 * @author  twalker
 * @version
 */
public class effectRegeneration extends LinkedEffect implements ChampionsConstants  {
    /** Creates new effectAdjustment */
    public effectRegeneration(Target source, Ability sourceAbility) {
        super( sourceAbility.getName(), "LINKED", false);
        setSource(source);
        setAbility(sourceAbility);
    }
    
    
    
    public boolean addEffect(BattleEvent be, Target target) throws BattleEventException {
        
        if ( super.addEffect(be,target) ) {
            
            Ability ability = getAbility();
            Power power = ability.getPower();
            ParameterList pl = power.getParameterList(ability, -1);
            
            String rate = (String)pl.getParameterValue("Rate");
            
            
            
            // Adjust the start Time to be the beginning of this turn, then
            // increment the start time by the number of seconds in a period.
            long seconds = ChampionsUtilities.calculateSeconds( ChampionsUtilities.getTimeStep( rate ), 1);
            Chronometer startTime = Battle.currentBattle.getTime().getLeastTurnEndSegment( seconds );
            
            setNextRegeneration(startTime);
            
            be.addBattleMessage(new GainedEffectSummaryMessage(target, this, true));
        }
        return true;
    }
    

    public void removeEffect(BattleEvent be, Target target)
            throws BattleEventException {
        super.removeEffect(be, target);
        be.addBattleMessage(new GainedEffectSummaryMessage(target, this, false));
    }
    
    public boolean postturn(BattleEvent be, Target target) throws BattleEventException {
        // If this is the correct post turn, regenerate and reset the time...
        
        Chronometer time = Battle.getCurrentBattle().getTime();
        Chronometer startTime = getNextRegeneration();
        
        if ( time.equals( startTime ) ) {
            Ability ability = getAbility();
            Power power = ability.getPower();
            ParameterList pl = power.getParameterList(ability, -1);
            
            String rate = (String)pl.getParameterValue("Rate");
            boolean resurrection = (Boolean)pl.getParameterValue("Resurrection");
            
            long seconds = ChampionsUtilities.calculateSeconds( ChampionsUtilities.getTimeStep( rate ), 1);
            startTime.add( seconds );
            
            // Create a Heal effect and apply it...ParameterList pl = getSourceAbility().getPower().getParameterList(getSourceAbility(), -1);
            if ( (target.isDead() && resurrection) ||
                 (target.isDead() == false && ability.hasLimitation("Resurrection Only") == false)) {
                String d = (String)pl.getParameterValue("HealDie");
                try {
                    int amount =  new Dice(d).getD6();
                    effectSimpleHeal eh = new effectSimpleHeal(target, getAbility(), 0,  amount, 0, target.getAdjustedStat("BODY"));
                    eh.addEffect(be, target);
                }
                catch ( BadDiceException bde ) {
                }
            }
        }
        return false;
    }
    
    
    
    /** Getter for property sourceAbility.
     * @return Value of property sourceAbility.
     */
    public Target getSource() {
        Object o = getValue("Effect.SOURCE");
        return ( o == null ) ? null : (Target)o;
    }
    
    /** Setter for property sourceAbility.
     * @param sourceAbility New value of property sourceAbility.
     */
    public void setSource(Target source) {
        add("Effect.SOURCE", source, true);
    }

    
    
    public Chronometer getNextRegeneration() {
        return (Chronometer) getValue("Effect.NEXTREGENERATION");
    }
    
    public void setNextRegeneration(Chronometer time) {
        add("Effect.NEXTREGENERATION", time, true);
    }
    
}
