/*
 * SweepActivateBattleEvent.java
 *
 * Created on January 5, 2004, 12:31 AM
 */

package champions;

import champions.interfaces.AbilityFilter;

/**
 *
 * @author  Trevor Walker
 */
public class CombinedAbilityBattleEvent extends BattleEvent {
    
    /** Store a source for this type of battleEvent, since there is
     * no activation info to get the source from.
     */
    private Target source;
    
    /** Creates a new instance of SweepActivateBattleEvent */
    public CombinedAbilityBattleEvent(CombinedAbility combinedAbility, Target source) {
        super(BattleEvent.LINKED_ACTIVATE,false);
        setCombinedAbility(combinedAbility);
        setSource(source);
    }
    
    private void buildBattleEvents() {
        CombinedAbility ca = getCombinedAbility();
        if ( ca != null ) {
            for(int i = 0; i < ca.getAbilityCount(); i++){
                Ability a = ca.getAbility(i);
                BattleEvent be = new BattleEvent(a);
                
                setSubBattleEvent(i, be);
            }
        }
    }

    public int getCombinedAbilityCount() {
        CombinedAbility ca = getCombinedAbility();
        if ( ca != null ) return ca.getAbilityCount();
        else return 0;
    }
    
    public Ability getCombinedAbility(int index) {
        CombinedAbility ca = getCombinedAbility();
        if ( ca != null ) return ca.getAbility(index);
        else return null;
    }
    
    public boolean isCombinedAbilityEnabled(int index) {
        return true;
    }
    
    public boolean isCombinedAbilityEnabled(String name) {
        return true;
    }

    public CombinedAbility getCombinedAbility() {
        return (CombinedAbility)getValue("BattleEvent.COMBINEDABILITY");
    }
    
    public BattleEvent getSubBattleEvent(int index) {
        return (BattleEvent) getIndexedValue(index, "SubAbility", "BATTLEEVENT");
    }
    
    public void setSubBattleEvent(int index, BattleEvent be) {
        addIndexed(index, "SubAbility", "BATTLEEVENT", be, true);
    }   

    public void setCombinedAbility(CombinedAbility combinedAbility) {
        add("BattleEvent.COMBINEDABILITY", combinedAbility, true, false);
        buildBattleEvents();
    }

    public Target getSource() {
        return source;
    }

    public void setSource(Target source) {
        this.source = source;
    }
    
    
}
