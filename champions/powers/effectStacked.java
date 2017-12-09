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
 */
public class effectStacked extends Effect {
    
    // Variable to temporarily store BattleEvent when it is first passed in.
    private BattleEvent stackedBE;
    /** Creates new effectUnconscious */
    public effectStacked() {
        super ( "Stacked Abilities", "PERSISTENT", true );
    }
    
    public String getDescription() {
        StringBuffer sb = new StringBuffer();
        
        int stackCount = getStackedCount();
        
        if ( stackCount > 0 ) {
            sb.append( getTarget().getName() );
            sb.append( " currently has " );
            sb.append( Integer.toString( stackCount ) );
            sb.append( " abilities stacked:\n\n");
            
            int index;
            BattleEvent stackedBE;
            Ability ability;
            boolean first = true;
            
            for(index=0;index<stackCount;index++) {
                stackedBE = (BattleEvent)getIndexedValue(index, "StackedBattleEvent", "BATTLEEVENT");
                ability = stackedBE.getAbility();
                if ( first == false ) sb.append ( ", " );
                sb.append( ability.getName() );
                first = false;
            }
        }
        else {
            sb.append( getTarget().getName() );
            sb.append( " currently has no abilties stacked." );
        }
        
        sb.append( "\n\nMaximum stacked abilities: " );
        int max = getTarget().getMaximumStackedAbilities();
        if ( max == -1 ) {
            sb.append( "Infinite");
        }
        else {
            sb.append( Integer.toString(max) );
        }
        
        return sb.toString();
    }
    
/*    public boolean addEffect(BattleEvent be, Target target)
    throws BattleEventException {
            if ( super.addEffect(be,target ) ) {
                return true;
            }
        }
        return false;
    } */
    
    public void removeEffect(BattleEvent be, Target target)
    throws BattleEventException {
        super.removeEffect(be,target );
    }
    
    public void removeStackedEvent(BattleEvent stackedBE) {
        int index;
        if ( ( index = findExactIndexed( "StackedBattleEvent", "BATTLEEVENT", stackedBE) ) != -1 ) {
            removeAllIndexed( index, "StackedBattleEvent", true );
        }
    }
    
    public void addStackedEvent(BattleEvent stackedBE) {
        int index;
        if ( ( index = findExactIndexed( "StackedBattleEvent", "BATTLEEVENT", stackedBE) ) == -1 ) {
            createIndexed("StackedBattleEvent","BATTLEEVENT",stackedBE ,true);
        }
    }
    
    public int getStackedCount() {
        return getIndexedSize("StackedBattleEvent");
    }
    
}