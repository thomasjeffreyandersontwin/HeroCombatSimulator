/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;

import champions.Ability;
import champions.ActivationInfo;
import champions.AdvantageAdapter;
import champions.BattleEvent;
import champions.Dice;
import champions.Effect;
import champions.Target;
import champions.exception.BattleEventException;
import champions.interfaces.ChampionsConstants;
import champions.parameters.ParameterList;


/**
 *
 * @author  unknown
 * @version
 *
 * To Convert from old format powers, to new format powers:
 *
 * 1) Add implements ChampionsConstants to class definition.<P>
 * 2) Copy and Fill in Advantage Definition Variables. <P>
 * 3) Move Parameter Information to parameterArray. <P>
 * 4) Delete getParameters method (unless special parameter handling is necessary.<P>
 * 5) Change configurePAD(Ability,DetailList) method to configurePAD(Ability ability, ParameterList parameterList).<P>
 * 6) Edit configurePAD method to use format specified below.<P>
 * 7) Change checkParameter method to checkParameter(Ability ability, <i>int padIndex</i>, 
 * String key, Object value, Object oldValue);
 * 8) Edit getConfigSummary method to use parameterList instead of parseParameter methods.<P>
 * 9) Change all instances of parseParameter to getParameterValue.<P>
 * 10) Add getParameterArray method.<P>
 * 11) Add isUnique method.<P>
 * 12) Edit getName method to return advantageName variable.
 * 13) Change serialVersionUID by some amount.
 * 14) Add patterns array and define import patterns.<P>
 * 15) Add getImportPatterns() method.<P>
 */
public class advantageEntangleAndCharacter extends AdvantageAdapter implements ChampionsConstants {
    static final long serialVersionUID = -6399128355211532419L;

    static private Object[][] parameterArray = {
    };

    
    // Advantage Definition Variables
    public static String advantageName = "Entangle And Character Both Take Damage"; // The Name of the Advantage
    private static boolean affectsDC = false; // Indicates the Advantage affects the cost of damage classes
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability

    // Import Patterns Definitions
    private static Object[][] patterns = {
    };
   
    /** Creates new advCombatModifier */
    public advantageEntangleAndCharacter() {
    }

    public String getName() {
        return advantageName;
    }
    
    public Object[][] getParameterArray() {
        return parameterArray;
    }
    
    public boolean isUnique() {
        return unique;
    }

    public boolean configurePAD(Ability ability, ParameterList parameterList) {
        if ( ability == null ) return false;
        
        ParameterList pl = getParameterList();

        int index = ability.addAdvantageInfo(this, advantageName, parameterList);
        this.setAffectsDC(affectsDC);

        setPriority(4);
        setDescription( getConfigSummary() );
        return true;
    }

    public double calculateMultiplier() {
       return 0.5;
    }

    public String getConfigSummary() {       
            return "Entangle And Character Both Take Damage";
 
    }
        
    public Object[][] getImportPatterns() {
        return patterns;
    }
}