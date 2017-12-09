/*
 * SenseFilter.java
 *
 * Created on June 17, 2004, 4:19 PM
 */

package champions.interfaces;

import champions.Sense;

/**
 *
 * @author  1425
 */
public interface SenseFilter {
    /** Returns whether the filter accepts this sense.
     *
     */
    public boolean accept(Sense sense);
}
