/*
 * RangedAbilityFilter.java
 *
 * Created on April 4, 2004, 7:14 PM
 */

package champions.filters;

import champions.Target;
import tjava.Filter;


/**
 *
 * @author  1425
 */
public class NameTargetFilter implements Filter<Target> {
    
    /** substring which much match the ability name. */
    private String filterString = null;
    
    /** Creates a new instance of RangedAbilityFilter */
    public NameTargetFilter() {
    }
    
    public NameTargetFilter(String includeText) {
        setFilterString(includeText);
    }
    
    public boolean includeElement(Target target) {
        return target != null && 
                (filterString == null || 
                 "".equals(filterString) ||
                 target.getName().toLowerCase().indexOf(filterString.toLowerCase()) != -1);
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
