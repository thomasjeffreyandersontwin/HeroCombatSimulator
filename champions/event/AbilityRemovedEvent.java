/*
 * BattleMessageEvent.java
 *
 * Created on September 26, 2000, 4:48 PM
 */

package champions.event;

import champions.Ability;
import java.util.EventObject;

/**
 *
 * @author  unknown
 * @version 
 */
public class AbilityRemovedEvent extends EventObject {

    protected Ability ability;
    /** Creates new BattleMessageEvent */
    public AbilityRemovedEvent(Object s, Ability ability) {
        super(s);
        this.ability = ability;
    }

    /**
     * Getter for property ability.
     * @return Value of property ability.
     */
    public champions.Ability getAbility() {
        return ability;
    }
    
    /**
     * Setter for property ability.
     * @param ability New value of property ability.
     */
    public void setAbility(champions.Ability ability) {
        this.ability = ability;
    }
    
}