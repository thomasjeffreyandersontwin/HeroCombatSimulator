/*
 * effectDying.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.BattleEvent;
import champions.Effect;
import champions.Target;

/**
 *
 * @author  unknown
 * @version
 */
public class effectAttackTypeWatcher extends Effect {

    static private Object[][] parameterArray = {
   };
    /** Creates new effectCombatModifier */
    public effectAttackTypeWatcher(String name) {
        super( name, "PERSISTENT");
        setHidden(true);
        int index;

    }

    public boolean prephase(BattleEvent be, Target t) {
        return true;  // Remove the effect on prephase
    }

}