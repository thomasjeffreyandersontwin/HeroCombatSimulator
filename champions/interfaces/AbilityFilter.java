/*
 * AbilityFilter.java
 *
 * Created on April 4, 2004, 7:08 PM
 */

package champions.interfaces;

import tjava.Filter;
import champions.Ability;

/**
 *
 * @author  1425
 */
public interface AbilityFilter extends Filter<Ability>{
    /** Indicates the Ability is part of the filter.
     *
     * @return True to include ability, False to exclude it.
     */
   // public boolean accept(Ability ability);
}
