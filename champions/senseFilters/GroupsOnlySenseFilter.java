/*
 * SenseGroupSenseFilter.java
 *
 * Created on June 17, 2004, 4:21 PM
 */

package champions.senseFilters;

import champions.Sense;
import champions.interfaces.SenseFilter;

/**
 *
 * @author  1425
 */
public class GroupsOnlySenseFilter implements SenseFilter {
    
    /** Creates a new instance of SenseGroupSenseFilter */
    public GroupsOnlySenseFilter() {
        
    }
    
    public boolean accept(Sense sense) {
        return sense.isSenseGroup();
    }
}
