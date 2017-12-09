/*
 * BattleMessageEvent.java
 *
 * Created on September 26, 2000, 4:48 PM
 */

package champions.event;

import champions.Ability;
import champions.ActivationInfo;
import java.util.EventObject;

/**
 *
 * @author  unknown
 * @version 
 */
public class ActivationStateChangeEvent extends EventObject {

    protected Ability ability;
    protected ActivationInfo activationInfo;
    protected boolean newState;
    /** Creates new BattleMessageEvent */
    public ActivationStateChangeEvent(Object s, Ability ability, ActivationInfo ai, boolean newState) {
        super(s);
        this.ability = ability;
        this.activationInfo = ai;
        this.newState = newState;
    }

    /**
     * Getter for property ability.
     * @return Value of property ability.
     */
    public Ability getAbility() {
        return ability;
    }
    
    /**
     * Getter for property activationInfo.
     * @return Value of property activationInfo.
     */
    public ActivationInfo getActivationInfo() {
        return activationInfo;
    }
    
    /**
     * Getter for property newState.
     * @return Value of property newState.
     */
    public boolean getNewState() {
        return newState;
    }  
}