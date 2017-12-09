/*
 * TargetIsAliveFilter.java
 *
 * Created on October 26, 2007, 3:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.filters;

import champions.Effect;
import champions.Target;
import tjava.Filter;

/**
 *
 * @author twalker
 */
public class TargetHasEffectFilter implements Filter<Target> {
    
    protected Class<? extends Effect> effectClass;
    
    /** Creates a new instance of TargetIsAliveFilter */
    public TargetHasEffectFilter(Class<? extends Effect> effectClass) {
        this.effectClass =effectClass;
    }

    public boolean includeElement(Target filterObject) {
        return effectClass == null || filterObject.hasEffect( effectClass );
    }
    
}
