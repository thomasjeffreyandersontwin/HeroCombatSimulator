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
public class BattleMessageEvent extends EventObject {

    /** Holds value of property type. */
    private int type;
    /** Holds value of property msg. */
    private String msg;
    /** Creates new BattleMessageEvent */
    public BattleMessageEvent(Object s, String msg, int type) {
        super(s);
        this.msg = msg;
        this.type = type;
    }
    
    public static final int MSG_COMBAT = 1;
    public static final int MSG_DEBUG = 2;
    public static final int MSG_ABILITY = 3;
    public static final int MSG_UTILITY = 4;
    public static final int MSG_ROSTER = 5;
    public static final int MSG_SEGMENT = 6;
    public static final int MSG_NOTICE = 7;
    public static final int MSG_HIT=8;
    public static final int MSG_MISS=9;

    /** Getter for property type.
     * @return Value of property type.
     */
    public int getType() {
        return type;
    }
    /** Getter for property msg.
     * @return Value of property msg.
     */
    public String getMessage() {
        return msg;
    }
}