/*
 * effectDying.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.LinkedEffect;



/**
 *
 * @author  unknown
 * @version
 */
public class effectDoesNotBleed extends LinkedEffect{
    
    public static final String effectName = "Does Not Bleed";
    
    /** Creates new effectUnconscious */
    public effectDoesNotBleed(String name) {
        super( effectName, "LINKED", true);
        //setUnique(true);
    }
    
    public String getDescription() {
        return effectName;
    }
    
}