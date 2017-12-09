/*
 * RangedAbilityFilter.java
 *
 * Created on April 4, 2004, 7:14 PM
 */

package champions.senseFilters;

import champions.Sense;
import champions.interfaces.SenseFilter;

/**
 *
 * @author  1425
 */
public class GroupNameSenseFilter implements SenseFilter {
    
    /** substring which much match the ability name. */
    private String filterString = null;
    
    /** Creates a new instance of RangedAbilityFilter */
    public GroupNameSenseFilter() {
    }
    
    /** Creates a new instance of RangedAbilityFilter */
    public GroupNameSenseFilter(String groupName) {
        setFilterString(groupName);
    }
    
    public boolean accept(Sense sense) {
        return sense != null && sense.isSense() &&
                (filterString == null || 
                 "".equals(filterString) ||
                 (sense.getSenseGroupName() != null &&
                 sense.getSenseGroupName().toLowerCase().indexOf(filterString.toLowerCase()) != -1));
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
