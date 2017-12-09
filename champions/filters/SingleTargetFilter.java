/*
 * SingleTargetFilter.java
 *
 * Created on October 26, 2007, 3:17 PM
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
public class SingleTargetFilter implements Filter<Target> {
    
    Target target;
    
    /** Creates a new instance of SingleTargetFilter */
    public SingleTargetFilter(Target target) {
        this.target = target;
    }

    public boolean includeElement(Target filterObject) {
        return target == null || target.equals(filterObject);
    }
    
}
