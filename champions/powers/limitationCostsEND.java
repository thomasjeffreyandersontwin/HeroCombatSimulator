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

public class limitationCostsEND extends LimitationAdapter implements ChampionsConstants {
    
    static final long serialVersionUID = -6870520616782472305L;
    
    static private String[] endTypes = { "Costs END Every Phase","Only Costs END to Activate"};
    static private Object[][] parameterArray = {
        {"EndTypes","Limitation#.ENDTYPES", String.class, "Costs END Every Phase", "Costs END Option", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", endTypes},
    };
    
    public static String limitationName = "Limited Power: Costs END"; // The Name of the Limitation
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "Costs END:.*",null},
        { "Costs Endurance.*",null}
    };
    
    public limitationCostsEND() {
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
        String endtypes = (String)parameterList.getParameterValue("EndTypes");
        
        // Read in any parameters that will be needed to configure the power or
        // Determine the validity of the power configuration.  Read the parameters
        // from the parameterList, instead of directly from the ability, since the
        // Ability isn't configured yet.
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        // !!!Limitation has nothing to validate!!!
        
        // Start to Actually Configure the Power.
        // The Add Power Info should always be executed to add information to the ability.
        // All of this information should be set in the Power Definition Variables at the
        // top of this file
        int index = ability.addLimitationInfo(this, limitationName, parameterList);
        
        // Copy the configuration parameters directly into the ability.  This will
        // take the parameters stored in the parameter list and copy them into the
        // ability using the appropriate keys and values.  Make sure that these
        // Value/Pairs are unique to the PAD.  Any that are not, should be copied
        // manually.
        // Note that if the advantage is not unique, the values should always be
        // accessed via the parameterList and not directly.
        //parameterList.copyValues(ability,index);
        
        // Add Extra Value/Pairs used by the Limitation/BattleEngine
        if (endtypes.equals("Only Costs END to Activate")){
            ability.setCostsENDOnlyToActivate(true);
        } 
        else {
            ability.setCostsENDOnlyToActivate(false);            
        }
    
    // ability.setSecondaryENDSource( "Character" );
    
    ability.setENDMultiplier(1, this);
    
    // Update the Stored Description for this Limitation
    setDescription( getConfigSummary() );
    
    // Return True to indicate success in configuringPAD
    return true;
}



public double calculateMultiplier() {
    return -0.5;
}

public String getConfigSummary() {
    return "Limited Power: Costs END";
}

public int identifyLimitation(AbilityImport ai,int line) {
    int index,count;
    String possibleLimitation;
    
    possibleLimitation = ai.getImportLine(line);
    if ( possibleLimitation != null && possibleLimitation.indexOf( "Costs END:" ) != -1 || possibleLimitation.indexOf( "Costs Endurance" ) != -1) {
        return 10;
    } else return 0;
    
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