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

 
public class limitationAffectsBodyOnly extends LimitationAdapter implements ChampionsConstants {
    
    static final long serialVersionUID = -6870520616782472305L;
    
    
    
    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first have is replaced by
    // "Advantage#" and only the second half is kept.
    static private Object[][] parameterArray = {
    };
    
    // Limitation Definition Variables
    public static String limitationName = "Affects Body Only"; // The Name of the Limitation
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "Beam Attack:.*",null}
    };
    
    /** Creates new advCombatModifier */
    public limitationAffectsBodyOnly() {
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
        // Fail immediately if ability is null
        if ( ability == null ) return false;

        int index = ability.addLimitationInfo(this, limitationName, parameterList);

        
        ability.add("Ability.BEAMATTACKOVERRIDE", "TRUE" , false );
        ability.add("Ability.CANSPREAD", "FALSE" , true );
        
        // Update the Stored Description for this Limitation
        setDescription( getConfigSummary() );
        
        // Return True to indicate success in configuringPAD
        return true;
    }
    
    
    
    public double calculateMultiplier() {
        return -0.5;
    }
    
    public String getConfigSummary() {
        return "Affects Body Only";
    }
    
    public int identifyLimitation(AbilityImport ai,int line) {
        int index,count;
        String possibleLimitation;
        
        possibleLimitation = ai.getImportLine(line);
        if ( possibleLimitation != null && possibleLimitation.indexOf( "Beam Attack:" ) != -1 ) {
            return 10;
        }
        else return 0;
        
    }
    public Object[][] getImportPatterns() {
        return patterns;
    }
}