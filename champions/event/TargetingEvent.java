/*
 * BattleMessageEvent.java
 *
 * Created on September 26, 2000, 4:48 PM
 */

package champions.event;

import java.util.*;
import champions.*;
/**
 *
 * @author  unknown
 * @version 
 */
public class TargetingEvent extends EventObject {

    public static final int TARGETING_REQUESTED = 1;
    public static final int TARGETING_CANCELLED = 2;
    public static final int TARGET_SELECTED = 3;
    /** Holds value of property type. */
    private int type;
    /** Creates new BattleMessageEvent */
    public TargetingEvent(Object s, int type) {
        super(s);
        this.type = type;
    }

    /** Getter for property target.
     * @return Value of property target.
     */
    public int getType() {
        return type;
    }
}