/*
 * effectUnconscious.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.Effect;


/**
 *
 * @author  unknown
 * @version
 *
 * Effect Hover is now Dynamic.
 */


public class effectAccidentalChangeOccurred extends Effect {
    
    /** Hold the Ability which this effect is linked to */
    
    /** Creates new effectUnconscious */
    public effectAccidentalChangeOccurred(Effect effect) {
        super( effect.getName() + " Occurred", "PERSISTENT", true);
        
        
    }
    
//    public boolean addEffect(BattleEvent be, Target target)
//    throws BattleEventException {
//        if ( super.addEffect(be,target ) ){
//            return true;
//        }
//        return false;
//    }
//    
//    public void removeEffect(BattleEvent be, Target target)
//    throws BattleEventException {
//        super.removeEffect(be,target );
//        
//    }
    
    public String getDescription() {
        return this.getName();
    }
    
}

