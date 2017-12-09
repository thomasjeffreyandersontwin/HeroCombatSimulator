/*
 * MeleeAbilityFilter.java
 *
 * Created on April 4, 2004, 7:14 PM
 */

package champions.filters;

import champions.Ability;
import champions.interfaces.AbilityFilter;
import java.io.Serializable;


/**
 *
 * @author  1425
 */
public class MeleeAbilityFilter implements AbilityFilter,Serializable {
    static final long serialVersionUID = 9112522536066525223L;
    
    /** Creates a new instance of MeleeAbilityFilter */
    public MeleeAbilityFilter() {
    }
    
    public boolean includeElement(Ability ability) {
        return ability != null && ability.isMeleeAttack();
    }
    
}
