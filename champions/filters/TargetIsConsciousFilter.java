/*
 * TargetIsAliveFilter.java
 *
 * Created on October 26, 2007, 3:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.filters;

import champions.Target;
import tjava.Filter;

/**
 *
 * @author twalker
 */
public class TargetIsConsciousFilter implements Filter<Target> {
    
    /** Creates a new instance of TargetIsAliveFilter */
    public TargetIsConsciousFilter() {
    }

    public boolean includeElement(Target filterObject) {
        return ! filterObject.isUnconscious();
    }
    
}
