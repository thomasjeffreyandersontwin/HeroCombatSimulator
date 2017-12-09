/*
 * BattleMessageEvent.java
 *
 * Created on September 26, 2000, 4:48 PM
 */

package champions.event;

import java.lang.Object;
import java.util.*;
import champions.*;
/**
 *
 * @author  unknown
 * @version 
 */
public class RosterAddEvent extends EventObject {

    /** Holds value of property target. */
    private Target target;
    /** Holds value of property index. */
    private int index;
    /** Creates new BattleMessageEvent */
    public RosterAddEvent(Object s, Target target, int index) {
        super(s);
        this.target = target;
        this.index = index;
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