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


 
public class limitationNoNonCombat extends LimitationAdapter implements ChampionsConstants {
    static final long serialVersionUID = -6399128355211532419L;

    static private Object[][] parameterArray = {
    };
    
    // Advantage Definition Variables
    public static String limitationName = "No Non Combat Acceleration"; // The Name of the Advantage
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability

    private static Object[][] patterns = {
        { limitationName +":.*", null}
    };
   
    public limitationNoNonCombat() {
    }

    public String getName() {
        return limitationName;
    }
    
    public Object[][] getParameterArray() {
        return parameterArray;
    }
    
    public boolean isUnique() {
        return unique;
    }

    public boolean configurePAD(Ability ability, ParameterList parameterList) {
       
        setDescription( getConfigSummary() );
        return true;
    }

    public double calculateMultiplier() {
        return 0.25;
    }

    public String getConfigSummary() {       
        return limitationName;
    }
    

    public Object[][] getImportPatterns() {
        return patterns;
    }
}