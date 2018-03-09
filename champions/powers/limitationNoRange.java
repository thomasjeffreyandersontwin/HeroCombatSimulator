/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;

import champions.*;
import champions.interfaces.*;
import champions.event.*;
import champions.parameters.ParameterList;


public class limitationNoRange extends LimitationAdapter implements ChampionsConstants {
    
    static final long serialVersionUID = -6870520616782472305L;
    
    

    static private Object[][] parameterArray = {
        
    };
    
    public static String limitationName = "No Range"; // The Name of the Limitation
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "No Range.*",null}
    };
    
    /** Creates new advCombatModifier */
    public limitationNoRange() {
    }
    
    public String getName() {
        return "Limited Power: No Range";
    }
    
    public Object[][] getParameterArray() {
        return parameterArray;
    }
    
    public boolean isUnique() {
        return unique;
    }
    
    public boolean configurePAD(Ability ability, ParameterList parameterList) {
        // Fail immediately if ability is null
        if ( ability == null ) return false;
        
             return true;
    }
    
    
    
    public double calculateMultiplier() {
        return -0.5;
    }
    
    public String getConfigSummary() {
        return "Limited Power: " + limitationName;
    }
    
    public int identifyLimitation(AbilityImport ai,int line) {
        int index,count;
        String possibleLimitation;
        
        possibleLimitation = ai.getImportLine(line);
        if ( possibleLimitation != null && possibleLimitation.indexOf( "No Range" ) != -1 ) {
            return 10;
        }
        else return 0;
        
    }
    
       public Object[][] getImportPatterns() {
        return patterns;
    }
}