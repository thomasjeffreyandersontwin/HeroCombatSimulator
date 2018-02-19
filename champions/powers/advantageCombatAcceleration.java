/*
{"advantageDoesKnockback", "Advantages"}, * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;

import champions.*;
import champions.interfaces.*;
import champions.exception.*;
import champions.parameters.ParameterList;


 
public class advantageCombatAcceleration extends AdvantageAdapter implements ChampionsConstants {
    static final long serialVersionUID = -6399128355211532419L;

    static private Object[][] parameterArray = {
    };
    
    // Advantage Definition Variables
    public static String advantageName = "Combat Accleleration"; // The Name of the Advantage
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability

    private static Object[][] patterns = {
        { advantageName +":.*", null}
    };
   
    public advantageCombatAcceleration() {
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
        int index = ability.addAdvantageInfo(this, advantageName, parameterList);
        this.setAffectsDC(affectsDC);
       
        setDescription( getConfigSummary() );
        return true;
    }

    public double calculateMultiplier() {
        return 0.25;
    }

    public String getConfigSummary() {       
        return advantageName;
    }
    

    public Object[][] getImportPatterns() {
        return patterns;
    }
}