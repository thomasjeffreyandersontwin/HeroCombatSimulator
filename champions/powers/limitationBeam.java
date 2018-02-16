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


public class limitationBeam extends LimitationAdapter implements ChampionsConstants {
    
    static final long serialVersionUID = -6870520616782472305L;
    
    static private Object[][] parameterArray = {
    };

    public static String limitationName = "Beam"; // The Name of the Limitation
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability

    private static Object[][] patterns = {
        { "Beam Attack:.*",null}
    };
    
    /** Creates new advCombatModifier */
    public limitationBeam() {
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
        return -0.25;
    }
    
    public String getConfigSummary() {
        return "Beam";
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
    
    
    /** Returns the patterns necessary to import the Power from CW.
     * The Object[][] returned should be in the following format:
     * patterns = Object[][] {
     * { "PATTERN" , new Object[] { "PARAMETER1", parameter1.class, "PARAMETER2", parameter2.class, ... },
     * ...
     * }
     *
     * PATTERN should be a regular expression pattern.  For every PARAMETER* where should be one
     * parathesis group in the expression. The PARAMETERS sub-array can be null, if the line has no parameter
     * and is just informational.
     *
     * By default, the importPower will check each line of the getImportPatterns() array and if a match is
     * found, the specified parameters will be set in the powers parameter list.  It is assumed that each
     * PATTERN will only occur once.  If the pattern can occur multiple times in a valid import, a custom
     * importPower method will have to be used.
     */
    public Object[][] getImportPatterns() {
        return patterns;
    }
}