/*
 * BattleMessageEvent.java
 *
 * Created on September 26, 2000, 4:48 PM
 */

package champions.event;

import champions.Ability;
import java.util.EventObject;

/** Fired when one of the bases instances of an instance group changes.
 *
 * @author  unknown
 * @version 
 */
public class InstanceChangedEvent extends EventObject {

    public static final int BASE_INSTANCE = 1;
    public static final int FRAMEWORK_INSTANCE = 2;
    public static final int ADJUSTED_INSTANCE = 3;
    public static final int CURRENT_INSTANCE = 4;
    public static final int ALL_INSTANCES = 5;
    public static final int VARIATION_INSTANCES = 6;
    
    protected Ability oldInstance;
    protected Ability newInstance;
    protected int type;
    /** Creates new BattleMessageEvent */
    public InstanceChangedEvent(Object s, int type, Ability oldAbility, Ability newAbility) {
        super(s);
        this.oldInstance = oldInstance;
        this.newInstance = newInstance;
        this.type = type;
    }

    /**
     * Getter for property oldInstance.
     * @return Value of property oldInstance.
     */
    public champions.Ability getOldInstance() {
        return oldInstance;
    }
    
    /**
     * Setter for property oldInstance.
     * @param oldInstance New value of property oldInstance.
     */
    public void setOldInstance(champions.Ability oldInstance) {
        this.oldInstance = oldInstance;
    }
    
    /**
     * Getter for property newInstance.
     * @return Value of property newInstance.
     */
    public champions.Ability getNewInstance() {
        return newInstance;
    }
    
    /**
     * Setter for property newInstance.
     * @param newInstance New value of property newInstance.
     */
    public void setNewInstance(champions.Ability newInstance) {
        this.newInstance = newInstance;
    }
    
    /**
     * Getter for property type.
     * @return Value of property type.
     */
    public int getType() {
        return type;
    }
    
    /**
     * Setter for property type.
     * @param type New value of property type.
     */
    public void setType(int type) {
        this.type = type;
    }
    
}