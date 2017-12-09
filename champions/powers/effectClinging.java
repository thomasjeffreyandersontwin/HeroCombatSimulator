/*
 * effectUnconscious.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.interfaces.*;
import champions.*;
import champions.event.*;
import champions.exception.*;
/**
 *
 * @author  unknown
 * @version
 *
 * Effect Clinging is now Dynamic.
 */


public class effectClinging extends LinkedEffect{
    
    /** Hold the Ability which this effect is linked to */
    
    static private Object[][] parameterArray = {
        {"Str","Power.STR", Integer.class, new Integer(0)}
        
    };
    
    /** Creates new effectUnconscious */
    public effectClinging(Ability ability) {
        super(ability.getName(), "LINKED");
        
        setAbility(ability);
    }
    
    public boolean addEffect(BattleEvent be, Target target)
    throws BattleEventException {
        Ability ability = getAbility();
        if ( super.addEffect(be,target ) ){
            Integer str = (Integer)ability.getIntegerValue("Power.STR");
            int sourcestr = target.getCurrentStat( "STR" ) + str.intValue();
            
            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " now has Clinging STR of " + sourcestr, BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " now has Clinging STR of " + sourcestr, BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " now has Clinging STR of " + sourcestr, BattleEvent.MSG_NOTICE );
            return true;
        }
        return false;
    }
    
    public void removeEffect(BattleEvent be, Target target)
    throws BattleEventException {
        Ability ability = getAbility();
        super.removeEffect(be,target );
        Integer str = (Integer)ability.getIntegerValue("Power.STR");
        int sourcestr = target.getCurrentStat( "STR" ) + str.intValue();
        
        be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " no longer has Clinging STR of " + sourcestr, BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " no longer has Clinging STR of " + sourcestr, BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " no longer has Clinging STR of " + sourcestr, BattleEvent.MSG_NOTICE );
        
    }
    
    public String getDescription() {
        Ability ability = getAbility();
        if ( ability != null ) {
            Integer str = (Integer)ability.getIntegerValue("Power.STR");
            
            //return level.toString() + "\" Knockback Resistance, +" + level.toString() + " PD & ED, and +" + strlevel.toString() + " STR";
            Target target = this.getTarget();
            
            int sourcestr = target.getCurrentStat( "STR" ) + str.intValue();
            
            return "Clinging STR of " + sourcestr;
        }
        else {
            return "Effect Error";
        }
    }

    public void addKnockbackModifiers(BattleEvent be, KnockbackModifiersList kml, String
    knockbackGroup) {
        
        kml.addKnockbackRollModifier( "Clinging", 1);
    }
    
}

