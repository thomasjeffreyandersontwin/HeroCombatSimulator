/*
 * effectUnconscious.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.Ability;
import champions.BattleEvent;
import champions.Effect;
import champions.LinkedEffect;
import champions.Sense;
import champions.Target;
import champions.battleMessage.GainedEffectSummaryMessage;
import champions.battleMessage.SimpleBattleMessage;
import champions.exception.BattleEventException;

import champions.parameters.ParameterList;
import java.util.List;
import champions.powers.effectFlash;

/**
 *
 * @author  unknown
 * @version
 */
public class effectFlashDefense extends LinkedEffect{

    
    /** Creates new effectUnconscious */
    public effectFlashDefense(String name, Ability ability) {
        super ( name, "LINKED", true );
        
        this.ability = ability;
    }

    @Override
    public void predefense(BattleEvent be, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget) throws BattleEventException {
        if ( effect instanceof effectFlash ) {
            effectFlash flash = (effectFlash)effect;
            
            int duration = flash.getDuration();
            
            if ( isDefenseAgainstSense(flash.getSense()) ) {
                duration = Math.max(0, duration - getLevel());
                flash.setDuration(duration);
                
                be.addBattleMessage( new SimpleBattleMessage(target, getAbility().getName() + " reduced the flash duration against " + flash.getSense().getSenseName() + " by " + getLevel() + " to " + duration + " segments.")  );
            }
        }
    }
    
    

    public boolean addEffect(BattleEvent be, Target target)
    throws BattleEventException {
        if ( super.addEffect(be,target ) ){
            be.addBattleMessage( new GainedEffectSummaryMessage(target, this, true)); 
            return true;
        }
        return false;
    }

    public void removeEffect(BattleEvent be, Target target)
    throws BattleEventException {
        super.removeEffect(be,target );
        be.addBattleMessage( new GainedEffectSummaryMessage(target, this, false)); 
    }

    /** Getter for property sense.
     * @return Value of property sense.
     */
    public boolean isDefenseAgainstSense(Sense sense) {
        ParameterList pl = getAbility().getPowerParameterList();
        List<Sense> configuredSenses = pl.getIndexedParameterValues("Senses");
        
        for(Sense configuredSenseGroup : configuredSenses) {
            if ( configuredSenseGroup.equals(sense) || configuredSenseGroup.equals( sense.getSenseGroup() ) ) {
                return true;
            }
        }
        
        return false;
    }
    
    


    /** Getter for property sense.
     * @return Value of property sense.
     */
    public int getLevel() {
        ParameterList pl = getAbility().getPowerParameterList();
        return (Integer)pl.getParameterValue("Level");
    }



}