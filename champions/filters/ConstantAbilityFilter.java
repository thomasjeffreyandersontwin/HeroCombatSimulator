/*
 * RangedAbilityFilter.java
 *
 * Created on April 4, 2004, 7:14 PM
 */

package champions.filters;

import champions.Ability;
import champions.interfaces.AbilityFilter;

/**
 *
 * @author  1425
 */
public class ConstantAbilityFilter implements AbilityFilter {
    
    /** Creates a new instance of RangedAbilityFilter */
    public ConstantAbilityFilter() {
    }
    
    public boolean includeElement(Ability ability) {
        return ability != null && ability.isConstant();
    }    
    
}
