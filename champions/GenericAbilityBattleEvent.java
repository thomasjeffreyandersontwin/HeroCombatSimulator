/*
 * GenericAbilityBattleEvent.java
 *
 * Created on March 20, 2004, 12:27 AM
 */

package champions;

/**
 *
 * @author  1425
 */
public class GenericAbilityBattleEvent extends BattleEvent {
    
    /** Creates a new instance of GenericAbilityBattleEvent */
    public GenericAbilityBattleEvent() {
        super(GENERIC_ABILITY_ACTIVATE, false);
        setActivationInfo( new ActivationInfo() );
    }
    
    /** Getter for property configure.
     * @return Value of property configure.
     *
     */
    public boolean isConfigured() {
        return getBooleanValue("Ability.CONFIGURED");
    }    
    
    /** Setter for property configure.
     * @param configure New value of property configure.
     *
     */
    public void setConfigured(boolean configured) {
        add( "Ability.CONFIGURED", configured ? "TRUE" : "FALSE", true);
    }    
    
}
