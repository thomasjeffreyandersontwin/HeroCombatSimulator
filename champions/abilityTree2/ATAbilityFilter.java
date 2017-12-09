/*
 * ATAbilityFilter.java
 *
 * Created on January 27, 2008, 1:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.abilityTree2;

import champions.Ability;
import tjava.Filter;

/**
 *
 * @author twalker
 */
public class ATAbilityFilter implements Filter<Object> {
    
    protected Filter<Ability> abilityFilter;
    
    /**
     * Creates a new instance of ATAbilityFilter
     */
    public ATAbilityFilter(Filter<Ability> abilityFilter) {
        this.abilityFilter = abilityFilter;
    }

    public boolean includeElement(Object filterObject) {
        if ( abilityFilter == null ) return true;
        
        if ( filterObject instanceof Ability ) {
            Ability a = Ability.class.cast(filterObject);
            if ( a != null ) {
                return abilityFilter.includeElement(a);
            }
        }
        
        return true;
    }
    
}
