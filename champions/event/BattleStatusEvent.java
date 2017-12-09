/*
 * BattleMessageEvent.java
 *
 * Created on September 26, 2000, 4:48 PM
 */

package champions.event;

import java.util.*;
/**
 *
 * @author  unknown
 * @version 
 */
public class BattleStatusEvent extends EventObject {

    /** Holds value of property status. */
    private String status;
    /** Creates new BattleMessageEvent */
    public BattleStatusEvent(Object s, String status) {
        super(s);
        this.status = status;
        
    }

    /** Getter for property status.
     * @return Value of property status.
     */
    public String getStatus() {
        return status;
    }
}