/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;

import champions.Ability;
import champions.LimitationAdapter;
import champions.interfaces.ChampionsConstants;
import champions.parameters.ParameterList;



/**
 *
 * @author  unknown
 * @version
 */
public class limitationSTRMinimum extends LimitationAdapter implements ChampionsConstants {
    static final long serialVersionUID = -3750155454162962388L;
    /** Creates new advCombatModifier */
    
    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first have is replaced by
    // "SpecialParameter#" and only the second half is kept.
    static private Object[][] parameterArray = {
        {"StrengthMin","Minimum.STR", Integer.class, new Integer(0), "STR Minimum", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED}
    };
    
    // Limitation Definition Variables
    public static String limitationName = "STR Minimum"; // The Name of the Limitation
    private static String limitationDescription = "STR Minimum"; // The Description of the Limitation
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability

    
        // Import Patterns Definitions
    private static Object[][] patterns = {
        { "STR Min: (.*)", new Object[] {"StrengthMin", Integer.class}},
        { "STR Minimum ([0-9]*).*", new Object[] {"StrengthMin", Integer.class}}
    };


    public limitationSTRMinimum() {
        
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
        
        // Read in any parameters that will be needed to configure the power or
        // Determine the validity of the power configuration.  Read the parameters
        // from the parameterList, instead of directly from the ability, since the
        // Ability isn't configured yet.
        Integer minStr = (Integer)parameterList.getParameterValue("StrengthMin");
      
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
                
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
        
        ability.setSTRMinimum(minStr);
        
        // Add Extra Value/Pairs used by the Limitation/BattleEngine

        // Update the Stored Description for this Limitation
        setDescription( getConfigSummary() );
        
        // Return True to indicate success in configuringPAD
        return true;
    }
    
    /** Removes Special Configurations from the Ability which the Special Parameter might have added.
     *
     * removeSpecialParameter is called prior to an Special Parameter being removed from an ability.  The
     * removeSpecialParameter method should remove any value pairs that it specifically added to the
     * ability.
     *
     * The ability will take care of removing the Special Parameter configuration, object, and parameter
     * lists under the SpecialParameter#.* value pairs.
     */
    public void remove(Ability ability, int index) {
        //ability.remove("Minimum.STR");
        ability.removeSTRMinimum();
    }

    public String getConfigSummary() {
        ParameterList parameterList = getParameterList();
        Integer cost = (Integer)parameterList.getParameterValue("StrengthMin");
        
        return "STR Minimum " + cost;
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
    
    
    public double calculateMultiplier() {
        return -0.25;
    }
    
}