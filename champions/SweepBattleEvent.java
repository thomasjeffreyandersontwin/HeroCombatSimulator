/*
 * SweepActivateBattleEvent.java
 *
 * Created on January 5, 2004, 12:31 AM
 */

package champions;

import champions.interfaces.AbilityFilter;
import champions.powers.effectTwoWeaponFightingHTH;
import champions.powers.effectTwoWeaponFightingRanged;

/**
 *
 * @author  Trevor Walker
 */
public class SweepBattleEvent extends LinkedBattleEvent {
    
    transient private AbilityFilter abilityFilter;
    private SweepType sweepType;
    private Ability sweepAbility;


    
    public enum SweepType {
        SWEEP, RAPIDFIRE;
    }
    
    /** Creates a new instance of SweepActivateBattleEvent */
    public SweepBattleEvent(Target source, SweepType sweepType, Ability sweepAbility) {
        super(source);
        
        this.sweepType = sweepType;
        this.sweepAbility = sweepAbility;
    }

    /** Getter for property abilityFilter.
     * @return Value of property abilityFilter.
     *
     */
    public AbilityFilter getAbilityFilter() {
        return abilityFilter;
    }    
    
    /** Setter for property abilityFilter.
     * @param abilityFilter New value of property abilityFilter.
     *
     */
    public void setAbilityFilter(AbilityFilter abilityFilter) {
        this.abilityFilter = abilityFilter;
    }
    
    public boolean hasTwoWeaponFighting() {
        
        Target source = getSource();
        Ability a = getAbility();
        
        if ( sweepType == SweepType.SWEEP ) {
            return source.hasEffect( effectTwoWeaponFightingHTH.class );
        }
        else {
            return source.hasEffect( effectTwoWeaponFightingRanged.class );
        }
    }

        /**
     * @return the sweepAbility
     */
    public Ability getSweepAbility() {
        return sweepAbility;
    }
    
}
