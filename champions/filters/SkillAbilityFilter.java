/*
 * RangedAbilityFilter.java
 *
 * Created on April 4, 2004, 7:14 PM
 */

package champions.filters;

import champions.Ability;
import champions.interfaces.AbilityFilter;
import champions.Skill;

/**
 *
 * @author  1425
 */
public class SkillAbilityFilter implements AbilityFilter {
    
    /** Creates a new instance of RangedAbilityFilter */
    public SkillAbilityFilter() {
    }
    
    public boolean includeElement(Ability ability) {
        return ability != null && ability.getPower() instanceof Skill;
    }    
    
}
