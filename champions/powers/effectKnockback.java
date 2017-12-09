/*
 * effectUnconscious.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.interfaces.*;
import champions.*;
import champions.event.*;
import champions.exception.*;
/**
 *
 * @author  unknown
 * @version
 */
public class effectKnockback extends Effect {
    
    /** Creates new effectUnconscious */
    public effectKnockback(int distance) {
        super ( "Knockback", "INSTANT", true );
    }

    /** Getter for property distance.
     * @return Value of property distance.
     */
    public int getDistance() {
        Integer i = getIntegerValue("Effect.DISTANCE");
        return ( i==null ) ? 0 : i.intValue();
    }
    
    /** Setter for property distance.
     * @param distance New value of property distance.
     */
    public void setDistance(int distance) {
        add("Effect.DISTANCE", new Integer(distance), true);
    }
    
}