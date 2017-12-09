/*
 * LinkedBattleEvent.java
 *
 * Created on November 13, 2003, 12:53 AM
 */

package champions;

/**
 *
 * @author  1425
 */
public class LinkedBattleEvent extends BattleEvent {
    
    /** Store a source for this type of battleEvent, since there is
     * no activation info to get the source from.
     */
    private Target source;
    
    /** Creates a new instance of LinkedBattleEvent */
    public LinkedBattleEvent(Target source) {
        super(BattleEvent.LINKED_ACTIVATE, (Target)null);
        setSource(source);
    }
    
    public void addLinkedAbility( Ability ability, boolean followLinks ) {
        addLinkedAbility(ability, -1, followLinks);
    }
    
    public void addLinkedAbility( Ability ability, int beforePosition, boolean followLinks ) {
        BattleEvent be = ability.getActivateAbilityBattleEvent(ability, null, source);
        
        int index = createIndexed(beforePosition, "LinkedAbility", "BATTLEEVENT", be, false);
        addIndexed(index, "LinkedAbility", "ABILITY", ability, true);
        addIndexed(index, "LinkedAbility", "ABILITYNAME", ability.getName(), true);
        addIndexed(index, "LinkedAbility", "ENABLED", "TRUE", true);
        fireIndexedChanged("LinkedAbility");
        
        if ( followLinks ) {
            // We need to follow the links to all other linked abilities now...
            buildAbilityLinks(ability, true);
        }
    }
    
    public void addLinkedAbility(BattleEvent be) {
        addLinkedAbility(be, true);
    }
    
    public void addLinkedAbility(BattleEvent be, boolean followLinks) {
        addLinkedAbility(be, -1, followLinks);
    }
    
    public void addLinkedAbility(BattleEvent be, int beforePosition, boolean followLinks) {
        int index = createIndexed(beforePosition, "LinkedAbility", "BATTLEEVENT", be, false);
        addIndexed(index, "LinkedAbility", "ABILITY", be.getAbility(), true);
        addIndexed(index, "LinkedAbility", "ABILITYNAME", be.getAbility().getName(), true);
        addIndexed(index, "LinkedAbility", "ENABLED", "TRUE", true);
        fireIndexedChanged("LinkedAbility");
        
        if ( be.getManeuver() != null ) {
            addIndexed(index, "LinkedAbility", "MANEUVER", be.getManeuver(), true);
        }
        
        if ( followLinks ) {
            // We need to follow the links to all other linked abilities now...
            Ability a = be.getAbility();
            buildAbilityLinks(a, true);
        }
    }
    
    public void addLinkedAbility( Ability ability, Ability maneuver, boolean followLinks) {
        addLinkedAbility(ability, maneuver, -1, followLinks);
    }
    
    public void addLinkedAbility( Ability ability, Ability maneuver, int beforePosition, boolean followLinks) {
        BattleEvent be = new BattleEvent(ability);
        be.setManeuver(maneuver);
        
        int index = createIndexed(beforePosition, "LinkedAbility", "BATTLEEVENT", be, false);
        addIndexed(index, "LinkedAbility", "ABILITY", ability, true);
        addIndexed(index, "LinkedAbility", "ABILITYNAME", ability.getName(), true);
        addIndexed(index, "LinkedAbility", "ENABLED", "TRUE", true);
        addIndexed(index, "LinkedAbility", "MANEUVER", maneuver, true);
        fireIndexedChanged("LinkedAbility");
        
        if ( followLinks ) {
            // We need to follow the links to all other linked abilities now...
            Ability a = be.getAbility();
            buildAbilityLinks(a, true);
        }
    }
    
    public void removeLinkedAbility(int index) {
        removeAllIndexed(index, "LinkedAbility", true);
    }
    
    public void removeAllLinkedAbilities() {
        removeAll("LinkedAbility", true);
    }
    
    private void buildAbilityLinks(Ability ability, boolean recurse) {
        if ( ability.isLinked() ) {
            int size = ability.getLinkedAbilityCount();
            for(int i = 0; i < size; i++) {
                String linkedName = ability.getLinkedAbilityName(i);
                
                // Check if this name is already one of the abilities that
                // will be activated...
                if ( hasLinkedAbility(linkedName) == false ) {
                    Ability linkedAbility = source.getAbility(linkedName);
                    
                    if ( linkedAbility == null ) {
                        linkedAbility = Battle.getDefaultAbilitiesOld().getAbility(linkedName, true);
                    }
                    
                    if ( linkedAbility == null ) {
                        System.out.println("LinkedBattleEvent.buildAbilityLinks: Ability " + linkedAbility + " not found for " + source.getName());
                        continue;
                    }
                    
                    if ( linkedAbility.isActivated( getSource() ) == false ) {
                        // Add it to the list of linked abilities
                        // if it is not already activated.
                        addLinkedAbility(linkedAbility, recurse);
                    }
                }
            }
            
        }
    }
    

    
    public int getLinkedAbilityCount() {
        return getIndexedSize("LinkedAbility");
    }
    
    public BattleEvent getLinkedBattleEvent(int index) {
        return (BattleEvent) getIndexedValue(index, "LinkedAbility", "BATTLEEVENT");
    }
    
    public void setLinkedBattleEvent(int index, BattleEvent be) {
        addIndexed(index, "LinkedAbility", "BATTLEEVENT", be, true);
    }   
    
    public Ability getLinkedAbility(int index) {
        return (Ability) getIndexedValue(index, "LinkedAbility", "ABILITY");
    }
    
    public Ability getLinkedManeuver(int index) {
        return (Ability) getIndexedValue(index, "LinkedAbility", "MANEUVER");
    }
    
    public boolean hasLinkedAbility(Ability ability) {
        return findIndexed("LinkedAbility", "ABILITY", ability ) != -1;
    }
    
    public boolean hasLinkedAbility(String abilityName) {
        return findIndexed("LinkedAbility", "ABILITYNAME", abilityName ) != -1;
    }
    
    public int getLinkedAbilityIndex(String abilityName) {
        return findIndexed("LinkedAbility", "ABILITYNAME", abilityName );
    }
    
    public boolean isLinkedAbilityEnabled(String abilityName) {
        return isLinkedAbilityEnabled( getLinkedAbilityIndex(abilityName));
    }
    
    public boolean isLinkedAbilityEnabled(int index) {
        return getIndexedBooleanValue( index, "LinkedAbility",  "ENABLED" );
        
    }
    
    public void setLinkedAbilityEnabled(String abilityName, boolean enabled) {
        setLinkedAbilityEnabled( getLinkedAbilityIndex(abilityName), enabled);
    }
    
    public void setLinkedAbilityEnabled(int index, boolean enabled) {
        addIndexed( index, "LinkedAbility", "ENABLED", enabled?"TRUE":"FALSE", true);
    }
    
    public void changeLinkedAbilityOrder(int startIndex, int endIndex) {
        moveIndexed(startIndex, endIndex, "LinkedAbility", false);
    }
    /** Estimates the amount of END necessary to execute this battleEvent.
     *
     */
    public int getLinkedENDCost() {
        int totalEnd = 0;
        boolean continuing = getActivationInfo() != null ? getActivationInfo().isContinuing() : false;
        int index = getLinkedAbilityCount();
        for(; index >= 0; index-- ) {
            if ( isLinkedAbilityEnabled(index) ) {
                Ability a = getLinkedAbility(index);
                totalEnd += a.getENDCost(continuing);
                
                a = getLinkedManeuver(index);
                if ( a != null ) totalEnd += a.getENDCost(continuing);
            }
        }
        
        return totalEnd;
    }
    
    
    /** Getter for property source.
     * @return Value of property source.
     *
     */
    public champions.Target getSource() {
        return source;
    }
    
    /** Setter for property source.
     * @param source New value of property source.
     *
     */
    public void setSource(champions.Target source) {
        this.source = source;
    }
    
//    public ActivationInfo getActivationInfo() {
//        throw new NullPointerException("ActivationInfo is Null for all LinkedBattleEvents.");
//    }
    
//    public Ability getAbility() {
//        throw new NullPointerException("Ability is Null for all LinkedBattleEvents.");
//    }
    
}
