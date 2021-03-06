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
public class NameAbilityFilter implements AbilityFilter {
    
    /** substring which much match the ability name. */
    private String filterString = null;
    
    /** Creates a new instance of RangedAbilityFilter */
    public NameAbilityFilter() {
    }
    
    public NameAbilityFilter(String includeText) {
        setFilterString(includeText);
    }
    
    public boolean includeElement(Ability ability) {
        return ability != null && 
                (filterString == null || 
                 "".equalsIgnoreCase(filterString) ||
                 ability.getName().toLowerCase().indexOf(filterString.toLowerCase()) != -1);
    }    
    
    /**
     * Getter for property filterString.
     * @return Value of property filterString.
     */
    public java.lang.String getFilterString() {
        return filterString;
    }
    
    /**
     * Setter for property filterString.
     * @param filterString New value of property filterString.
     */
    public void setFilterString(java.lang.String filterString) {
        this.filterString = filterString;
    }
    
}
