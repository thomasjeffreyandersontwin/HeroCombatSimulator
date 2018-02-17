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

public class limitationRestrainable extends LimitationAdapter implements ChampionsConstants {
    
    static final long serialVersionUID = -6880520616782472305L;
    
    
    
    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first have is replaced by
    // "Advantage#" and only the second half is kept.
    static private Object[][] parameterArray = {
        {"NotVSGrabOrEntangle","Restrainable.NOTVSGRABORENTANGLE", Boolean.class, new Boolean(false), "By means other than Grabs/Entangles", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED },
                
    };
    
    // Limitation Definition Variables
    public static String limitationName = "Restrainable"; // The Name of the Limitation
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "Restrainable.*",null}
    };
    
    /** Creates new advCombatModifier */
    public limitationRestrainable() {
    }
    
    public String getName() {
        return "Restrainable";
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
        
        setDescription( getConfigSummary() );
        
        // Return True to indicate success in configuringPAD
        return true;
    }
    
    
    
    public double calculateMultiplier() {
        ParameterList parameterList = getParameterList();
        boolean notvsgraborentangle = (Boolean)parameterList.getParameterValue("NotVSGrabOrEntangle");
        double total;
        total = -0.5;
        if (notvsgraborentangle) total = +0.25;
        return total;
    }
    
    public String getConfigSummary() {
        return limitationName;
    }
    
    public int identifyLimitation(AbilityImport ai,int line) {
        int index,count;
        String possibleLimitation;
        
        possibleLimitation = ai.getImportLine(line);
        if ( possibleLimitation != null && possibleLimitation.indexOf( "Restrainable" ) != -1 ) {
            return 10;
        } else return 0;
        
    }
    
    public Object[][] getImportPatterns() {
        return patterns;
    }
}