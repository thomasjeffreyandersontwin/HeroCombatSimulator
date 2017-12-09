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
import java.awt.Color;
/**
 *
 * @author  unknown
 * @version
 *
 * Effect Off Ground is now Dynamic.
 */


public class effectOffGround extends LinkedEffect {
    
    /** Hold the Ability which this effect is linked to */
    
    static private Object[][] parameterArray = {
        {"Str","Power.STR", Integer.class, new Integer(0)}
        
    };
    
    /** Creates new effectUnconscious */
    public effectOffGround(Ability ability) {
        super(ability.getName(), "LINKED");
        setCritical(true);
        setEffectColor(Color.green);
        setAbility(ability);
    }
    
//    public boolean addEffect(BattleEvent be, Target target)
//    throws BattleEventException {
//        Ability ability = getAbility();
//        if ( super.addEffect(be,target ) ){
//             return true;
//        }
//        return false;
//    }
//
//    public void removeEffect(BattleEvent be, Target target)
//    throws BattleEventException {
//        Ability ability = getAbility();
//        super.removeEffect(be,target );
//
//    }
    
    public String getDescription() {
        Ability ability = getAbility();
        if ( ability != null ) {
            
            //return level.toString() + "\" Knockback Resistance, +" + level.toString() + " PD & ED, and +" + strlevel.toString() + " STR";
            Target target = this.getTarget();
            
            
            return "Off Ground STR of ";
        } else {
            return "Effect Error";
        }
    }

    
    public void addKnockbackModifiers(BattleEvent be, KnockbackModifiersList kml, String
            knockbackGroup) {
        
        kml.addKnockbackRollModifier( "Off Ground", -1);
    }
    
}

