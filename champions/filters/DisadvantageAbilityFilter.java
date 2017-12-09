/*
 * RangedAbilityFilter.java
 *
 * Created on April 4, 2004, 7:14 PM
 */

package champions.filters;

import champions.Ability;
import champions.interfaces.AbilityFilter;
import champions.Disadvantage;

/**
 *
 * @author  1425
 */
public class DisadvantageAbilityFilter implements AbilityFilter {
    
    /** Creates a new instance of RangedAbilityFilter */
    public DisadvantageAbilityFilter() {
    }
    
    public boolean includeElement(Ability ability) {
        return ability != null && ability.getPower() instanceof Disadvantage;
    }    
    
}
