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
public class TargetSelectedEvent extends EventObject {

    /** Holds value of property target. */
    private Target target;
    private boolean preset;
    /** Creates new BattleMessageEvent */
    public TargetSelectedEvent(Object s, Target t, boolean preset) {
        super(s);
        this.preset = preset;
        target = t;
    }
    
    public TargetSelectedEvent(Object s, Target t) {
        this(s,t,false);
    }

    /** Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        return target;
    }
    
    public boolean isPreset() {
        return preset;
    }
}