/*
 * effectUnconscious.java
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
public class effectClairsentience extends LinkedEffect{
    
       
    /** Creates new effectUnconscious */
    public effectClairsentience(Ability ability,String name) {
        super ( name, "LINKED", true );
        this.ability = ability;
    }


  

}