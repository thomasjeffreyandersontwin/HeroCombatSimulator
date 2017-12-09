/*
 * effectDying.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.Ability;
import champions.BattleEvent;
import champions.Effect;
import champions.Target;


/**
 *
 * @author  unknown
 * @version
 */
public class effectDefenseManeuver extends Effect {

    static public String[] levelOptions = {"I","II","III","IV"};
    
    static private Object[][] parameterArray = {
        {"Level","Power.LEVEL", String.class, "I", "Maneuver Level", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", levelOptions},
    };

    /** Creates new effectCombatModifier */
    public effectDefenseManeuver(Ability ability) {
        super( ability.getName() , "PERSISTENT");
        setHidden(true);
        setUnique(true);
        int index;
        setAbility(ability);
        String level = (String)ability.parseParameter(parameterArray, "Level");
        add("Effect.LEVEL",  level,  true);


    }

    public boolean prephase(BattleEvent be, Target t) {
        return true;  // Remove the effect on prephase
    }
    public Ability getAbility() {
        return (Ability)getValue("Effect.ABILITY");
    }
    /** Setter for property sense.
     * @param sense New value of property sense.
     */
    public void setAbility(Ability ability) {
        add("Effect.ABILITY", ability, true);
    }
    
    public String getDescription() {
        Ability ability = getAbility();
        String level = (String)ability.parseParameter(parameterArray, "Level");
        
        StringBuffer sb = new StringBuffer();
        
        sb.append("Defense Maneuver Level " +level + "\n");
        
        return sb.toString();
    }    
    
}