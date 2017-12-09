/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;

import champions.Ability;
import champions.AbilityImport;
import champions.ChampionsUtilities;
import champions.SpecialParameterAdapter;
import champions.interfaces.ChampionsConstants;
import champions.parameters.ParameterList;


/**
 *
 * @author  unknown
 * @version
 */
public class SpecialParameterOCVModifier extends SpecialParameterAdapter implements ChampionsConstants {
    static final long serialVersionUID = -3750155454162962388L;
    /** Creates new advCombatModifier */
    
    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first have is replaced by
    // "SpecialParameter#" and only the second half is kept.
    static private Object[][] parameterArray = {
        {"OCVModifier","SpecialParameter#.OCVMODIFIER", Integer.class, new Integer(0), "OCV Modifier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED}
    };

    // SpecialParameter Definition Variables
    private static String specialParameterName = "OCV Modifier"; // The Name of the SpecialParameter
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "OCV \\+([0-9]*).*", new Object[] {"OCVModifier", Integer.class}}

    };    

    public SpecialParameterOCVModifier() {
        
    }

    public String getName() {
        return specialParameterName;
    }
    
    public Object[][] getParameterArray() {
        return parameterArray;
    }
    
    public boolean isUnique() {
        return unique;
    }

    public boolean configure(Ability ability, ParameterList parameterList) {
        // Fail immediately if ability is null
        if ( ability == null ) return false;
        
        // Read in any parameters that will be needed to configure the power or
        // Determine the validity of the power configuration.  Read the parameters
        // from the parameterList, instead of directly from the ability, since the
        // Ability isn't configured yet.
        Integer modifier = (Integer)parameterList.getParameterValue("OCVModifier");
       
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        // SpecialParameterAreaEffect has nothing to validate.
        
        // Start to Actually Configure the Power.
        // The Add Power Info should always be executed to add information to the ability.
        // All of this information should be set in the Power Definition Variables at the
        // top of this file
        int index = ability.addSpecialParameterInfo(this, specialParameterName);
        
        // Always Set the ParameterList to the parameterList
        setParameterList(ability,index,parameterList);
        
        // Copy the configuration parameters directly into the ability.  This will
        // take the parameters stored in the parameter list and copy them into the
        // ability using the appropriate keys and values.  Make sure that these
        // Value/Pairs are unique to the PAD.  Any that are not, should be copied
        // manually.
        // Note that if the specialParameter is not unique, the values should always be
        // accessed via the parameterList and not directly.
        parameterList.copyValues(ability,index);
               
        // Add Extra Value/Pairs used by the SpecialParameter/BattleEngine
        ability.setOCVModifier(modifier);

        // Update the Stored Description for this Limitation
        ability.setSpecialParameterDescription(index,getConfigSummary(ability,index));
        
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
        //ability.remove("Ability.OCVBONUS");
        ability.setOCVModifier(0);
    }

    public String getConfigSummary(Ability ability, int index) {
        ParameterList parameterList = getParameterList(ability,index);
        Integer modifier = (Integer)parameterList.getParameterValue("OCVModifier");
        
        return "OCV " + ChampionsUtilities.toSignedString(modifier);
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
    
    /** Attempt to identify SpecialParameter 
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     * @param ai AbilityImport which is currently being imported.
     * @return Value indicating likelyhood that AbilityImport is this kind of power.
     * 0 indicates there was no recognition. 
     * 5 indicates probable match.
     * 10 indicates almost certain recognition.
     *
     */
    public int identifySpecialParameter(AbilityImport ai, int line) {
        int index,count;
        String possibleSpecialParameter;
        
        possibleSpecialParameter = ai.getImportLine(line);
        if ( possibleSpecialParameter != null && possibleSpecialParameter.startsWith( "OCV" )  ) {
            return 10;
        }

        return 0;
    }
}