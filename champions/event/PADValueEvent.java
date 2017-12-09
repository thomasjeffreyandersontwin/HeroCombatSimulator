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
public class PADValueEvent extends EventObject {

    /** Holds value of property value. */
    private Object value;
    /** Holds value of property key. */
    private String key;
    /** Holds value of property oldValue. */
    private Object oldValue;
    /** Creates new BattleMessageEvent */
    public PADValueEvent(Object s, String key, Object newValue, Object oldValue) {
        super(s);
        setKey(key);
        setValue(newValue);
        setOldValue(oldValue);
    }


    public Object getValue() {
        return value;
    }
    /** Setter for property value.
     * @param value New value of property value.
     */
    public void setValue(Object value) {
        this.value = value;
    }
    /** Getter for property key.
     * @return Value of property key.
     */
    public String getKey() {
        return key;
    }
    /** Setter for property key.
     * @param key New value of property key.
     */
    public void setKey(String key) {
        this.key = key;
    }
    /** Getter for property oldValue.
     * @return Value of property oldValue.
     */
    public Object getOldValue() {
        return oldValue;
    }
    /** Setter for property oldValue.
     * @param oldValue New value of property oldValue.
     */
    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }
}