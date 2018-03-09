/*
 * AdjustmentPower.java
 *
 * Created on November 12, 2006, 11:18 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.adjustmentPowers;

import champions.Ability;
import champions.Characteristic;
import champions.parameters.ParameterList;
import champions.powers.advantageMultipleSpecialEffects;
import champions.Power;
import champions.Target;
import champions.interfaces.AbilityIterator;
import champions.Power;
import champions.SpecialEffect;
import java.util.ArrayList;

/**
 *
 * @author 1425
 */
public abstract class AdjustmentPower extends Power {
    
        
        public abstract String getAdjustableParameterName();
   
        public ArrayList<Adjustable> getAvailableAdjustables(Ability sourceAbility, Target target) {
            return getAvailableAdjustables(sourceAbility, target, getAdjustableParameterName());
        }
        
        public ArrayList<Adjustable> getAvailableAdjustables(Ability sourceAbility, Target target, String adjustableParameter) {
            ArrayList<Adjustable> adjustableList = new ArrayList<Adjustable>();
        
        if(sourceAbility.findAdvantage(new advantageMultipleSpecialEffects().getName())!=-1)
        {
        	ParameterList p = getParameterList(sourceAbility);
        	int level = (int) sourceAbility.getValue("Ability.ADJUSTMENTEFFECTLEVEL");
        	if(level==4)
        	{
        		
        	}
        	
        }
        // First Grab all of the Abilities
        int index, count;
        
        ParameterList pl = sourceAbility.getPower().getParameterList(sourceAbility, -1);
        count = pl.getIndexedParameterSize( adjustableParameter );
        
        for( index = 0; index < count; index++ ) {
            AdjustmentClass ac = (AdjustmentClass)pl.getIndexedParameterValue(adjustableParameter, index);
            ac.getAdjustablesForTarget(target, adjustableList);
        }
        
        // Now Grab all of the available stats...
      /*  for( index = 0; index < count; index++ ) {
            if ( adjustables[index] instanceof Characteristic ) {
                if ( target.hasStat( ((Characteristic)adjustables[index]).getName() ) ) {
                    adjustableList.add( adjustables[index] );
                }
                adjustables[index] = null;
            }
        }
        
       
        AbilityIterator ai = target.getAbilities();
        while ( ai.hasNext() ) {
            Ability targetAbility = ai.nextAbility();
            
            for(index = 0; index < count; index++) {
                if ( adjustables[index] != null  && isAbilityAdjustable(targetAbility, adjustables[index]) ) {
                    adjustableList.add( targetAbility );
                    break;
                }
            }
        } */
        
        return adjustableList;
    }
        
   /* protected boolean isAbilityAdjustable(Ability targetAbility, Object adjustable) {
        boolean rv = false;
        
        if ( adjustable instanceof Ability ) {
            rv = ( targetAbility.equals(adjustable) );
        }
        else if ( adjustable instanceof SpecialEffect ) {
            rv = ( targetAbility.hasSpecialEffect( ((SpecialEffect)adjustable).getName() ) );
        }
        else if ( adjustable instanceof Power ) {
            rv = ( targetAbility.getPower().getClass().equals( adjustable.getClass() ) );
        }
        
        return rv;
    } */
    
    protected int getMaximumAdjustables(Ability sourceAbility) {
        switch ( getAdjustmentLevel(sourceAbility) ) {
            case ADJ_SINGLE_ADJUSTMENT:
                return 1;
            case ADJ_VARIABLE_1_ADJUSTMENT:
                return 1;
            case ADJ_VARIABLE_2_ADJUSTMENT:
                return 2;
            case ADJ_VARIABLE_4_ADJUSTMENT:
                return 4;
            case ADJ_VARIABLE_ALL_ADJUSTMENT:
                return Integer.MAX_VALUE;
        }
        return 1;
    }
    
    /** 
     * @return  
     */
    public int getAdjustmentLevel(Ability ability) {
        Integer i = ability.getIntegerValue( "Ability.ADJUSTMENTLEVEL" );
        return (i == null) ? ADJ_SINGLE_ADJUSTMENT : i.intValue();
    }

    
    public long getDecayInterval(Ability ability) {
        Long decayInterval = ability.getLongValue( "Ability.DECAYINTERVAL");
        if (decayInterval == null)            decayInterval = new Long(12);
        
        return decayInterval.longValue();
    }

    
    public int getDecayRate(Ability ability) {
        Integer decayRate = ability.getIntegerValue( "Ability.DECAYRATE");
        if (decayRate == null)            decayRate = new Integer(5);
        
        return decayRate.intValue();
    }
    
}
