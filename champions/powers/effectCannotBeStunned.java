/*
 * effectDying.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.Ability;
import champions.LinkedEffect;



/**
 *
 * @author  unknown
 * @version
 */
public class effectCannotBeStunned extends LinkedEffect {
    
    public static final String effectName = "Cannot Be Stunned";
    
    /** Creates new effectUnconscious */
    public effectCannotBeStunned(String name) {
        super( effectName, "LINKED", true);
        //setUnique(true);
    }
    
    public String getDescription() {
        return ("Cannot Be Stunned");
    }
    
    public Ability getAbility() {
        return null;
    }
    
}