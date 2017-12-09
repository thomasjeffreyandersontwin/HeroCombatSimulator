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
public class SegmentAdvancedEvent extends EventObject {

    /** Holds value of property time. */
    private Chronometer time;
    /** Creates new BattleMessageEvent */
    public SegmentAdvancedEvent(Object s, Chronometer time) {
        super(s);
        this.time = time;
    }

    /** Getter for property time.
     * @return Value of property time.
     */
    public Chronometer getChronometer() {
        return time;
    }
}