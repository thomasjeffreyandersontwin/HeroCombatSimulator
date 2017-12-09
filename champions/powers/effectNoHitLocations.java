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
public class effectNoHitLocations extends LinkedEffect{
    
    public static final String effectName = "No Hit Locations";
    
    
    /** Creates new effectUnconscious */
    public effectNoHitLocations(Ability ability, String name) {
        super( effectName, "LINKED", true);
        //setUnique(true);
        
        this.ability = ability;
    }
    
    public String getDescription() {
        return effectName;
    }


    
    
    
}