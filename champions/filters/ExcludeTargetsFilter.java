/*
 * ExcludeTargetsFilter.java
 *
 * Created on October 26, 2007, 8:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.filters;

import champions.Target;
import tjava.Filter;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author twalker
 */
public class ExcludeTargetsFilter implements Filter<Target> {
    
    Set<Target> excludeTargets = new HashSet<Target>();
    
    /** Creates a new instance of ExcludeTargetsFilter */
    public ExcludeTargetsFilter() {
    }
    
    public ExcludeTargetsFilter(Target target) {
        addExcludedTarget(target);
    }
    
    public void addExcludedTarget(Target target) {
        excludeTargets.add(target);
    }

    public boolean includeElement(Target filterObject) {
        return excludeTargets.contains(filterObject) == false;
    }
    
}
