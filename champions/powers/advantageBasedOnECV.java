/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;

import champions.Ability;
import champions.AbilityImport;
import champions.AdvantageAdapter;
import champions.interfaces.ChampionsConstants;
import champions.parameters.ParameterList;


/**
 *
 * @author  unknown
 * @version
 *
 * * To Convert from old format advantage, to new format advantage:
 *
 * 1) Add implements ChampionsConstants to class definition.<P>
 * 2) Copy and Fill in advantage Definition Variables. <P>
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
 * 13) Add patterns array and define import patterns.<P>
 * 14) Add getImportPatterns() method.<P>
 */
public class advantageBasedOnECV extends AdvantageAdapter implements ChampionsConstants {
    
    static final long serialVersionUID = -6870520616782472305L;
    
    
    
    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first have is replaced by
    // "Advantage#" and only the second half is kept.
    static private Object[][] parameterArray = {
        //{"ImportDefense","Power.IMPORTDEFENSE", String.class, "", "Defense", STRING_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        {"vsMD","Ability.VSMD", Boolean.class, new Boolean(false), "Vs. MD", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        
    };
    
    // advantage Definition Variables
    public static String advantageName = "Based on Ego Combat Value"; // The Name of the advantage
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "Based on EGO Combat Value: vs. (ECV), .*",null},
        { "Based on EGO Combat Value: vs. (Mental Defense), .*",new Object[] { "vsMD",Boolean.class}}
    };
    
    /** Creates new advCombatModifier */
    public advantageBasedOnECV() {
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
        // Fail immediately if ability is null
        if ( ability == null ) return false;
        
        // Read in any parameters that will be needed to configure the power or
        // Determine the validity of the power configuration.  Read the parameters
        // from the parameterList, instead of directly from the ability, since the
        // Ability isn't configured yet.
        boolean  vsmd = (Boolean)parameterList.getParameterValue("vsMD");
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        // Start to Actually Configure the Advantage.
        // The Add Power Info should always be executed to add information to the ability.
        // All of this information should be set in the Power Definition Variables at the
        // top of this file
        int index = ability.addAdvantageInfo(this, advantageName, parameterList);
        
        // Copy the configuration parameters directly into the ability.  This will
        // take the parameters stored in the parameter list and copy them into the
        // ability using the appropriate keys and values.  Make sure that these
        // Value/Pairs are unique to the PAD.  Any that are not, should be copied
        // manually.
        // Note that if the advantage is not unique, the values should always be
        // accessed via the parameterList and not directly.
        //parameterList.copyValues(ability,index);
        
        // Add Extra Value/Pairs used by the advantage/BattleEngine
        ability.add("Ability.CVTYPE", "EGO", true );
        ability.add("Ability.CANSPREAD", "FALSE" , true );
        ability.add("Ability.DOESBODY", "FALSE", true);
        
        if ( vsmd ) {
            ability.add("Power.DEFENSE", "MD", true);
        }
        
        ability.reconfigurePower();
        
        // Update the Stored Description for this advantage
        setDescription( getConfigSummary() );
        
        //
        
        // Return True to indicate success in configuringPAD
        return true;
    }
    
    
    
    public double calculateMultiplier() {
        return 1;
    }
    
    public String getConfigSummary() {
        ParameterList parameterList = getParameterList();
        boolean  vsmd = (Boolean)parameterList.getParameterValue("vsMD");
        
        if ( vsmd ) {
            return "Based on ECV vs. MD";
        } else {
            return "Based on ECV vs. Normal";
        }
    }
    
    public int identifyAdvantage(AbilityImport ai,int line) {
        int index,count;
        String possibleAdvantage;
        
        possibleAdvantage = ai.getImportLine(line);
        if ( possibleAdvantage != null && possibleAdvantage.indexOf( "Based on EGO Combat Value" ) != -1 ) {
            return 10;
        }
        
        return 0;
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