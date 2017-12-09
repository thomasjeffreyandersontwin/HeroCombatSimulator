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
public class RosterRemoveEvent extends EventObject {

    /** Holds value of property target. */
    private Target target;
    /** Holds value of property index. */
    private int index;
    /** Creates new BattleMessageEvent */
    public RosterRemoveEvent(Object s, Target target, int i) {
        super(s);
        this.target = target;
        index = i;
    }

    /** Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        return target;
    }
    /** Getter for property index.
     * @return Value of property index.
     */
    public int getIndex() {
        return index;
    }
}