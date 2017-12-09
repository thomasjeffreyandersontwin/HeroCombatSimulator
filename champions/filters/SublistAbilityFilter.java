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
public class SublistAbilityFilter implements AbilityFilter {
    
    /** substring which much match the ability name. */
    private String filterString = null;
    
    /** Creates a new instance of RangedAbilityFilter */
    public SublistAbilityFilter() {
    }
    
    public SublistAbilityFilter(String sublistName) {
        setFilterString(sublistName);
    }
    
    public boolean includeElement(Ability ability) {
        return ability != null && ability.getAbilityList() != null && ability.getAbilityList().getName() != null &&
                (filterString == null || 
                 "".equals(filterString) ||
                 ability.getAbilityList().getName().toLowerCase().indexOf(filterString.toLowerCase()) != -1);
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
