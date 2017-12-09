/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;

import champions.*;
import champions.interfaces.*;
import champions.parameters.ParameterList;

/**
 *
 * @author  unknown
 * @version
 */
public class advantageCEVary extends AdvantageAdapter
implements ChampionsConstants{
    static final long serialVersionUID = -6399128355213532419L;
    /** Creates new advCombatModifier */
    
    static private String[] VaryArray = {
        "Very Limited Group", "Limited Group", "Broad Group"
    };
    
    static private Object[][] parameterArray = {
        //        {"Multiplier","CEV.MULTIPLIER", String.class, "TRUE"},
        {"Multiplier", "Advantage#.MULTIPLIER", String.class, "Broad Group", "Vary Environment ", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", VaryArray},
    };
    
    // Advantage Definition Variables
    public static String advantageName = "CE: Varying Effect"; // The Name of the Advantage
    private static boolean affectsDC = true; // Indicates the Advantage affects the cost of damage classes
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "Effect: Variable.*", null}
    };
    
    public advantageCEVary() {
    }
    
    public String getName() {
        return "CE: Varying Effect";
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
        String multiplier = (String)parameterList.getParameterValue("Multiplier");
        
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        // AdvantageAreaEffect has nothing to validate.
        
        // Start to Actually Configure the Power.
        // The Add Power Info should always be executed to add information to the ability.
        // All of this information should be set in the Power Definition Variables at the
        // top of this file
        int index = ability.addAdvantageInfo(this, advantageName, parameterList);
        this.setAffectsDC(affectsDC);
        
        // Copy the configuration parameters directly into the ability.  This will
        // take the parameters stored in the parameter list and copy them into the
        // ability using the appropriate keys and values.  Make sure that these
        // Value/Pairs are unique to the PAD.  Any that are not, should be copied
        // manually.
        // Note that if the advantage is not unique, the values should always be
        // accessed via the parameterList and not directly.
        //parameterList.copyValues(ability,index);
        
        // Add Extra Value/Pairs used by the Advantage/BattleEngine
        // this.setAffectsDC(ability,index, ( affectsDCparam.equals("TRUE") ? true : false ) );
        
        // Update the Stored Description for this Limitation
        setDescription( getConfigSummary() );
        
        // Return True to indicate success in configuringPAD
        return true;
        
    }
    
    
    public String getConfigSummary() {
        
        return "CE: Vary Effect";
        
    }
    
    
    public double calculateMultiplier() {
        ParameterList parameterList = getParameterList();
        String multiplier = (String)parameterList.getParameterValue("Multiplier");
        double cost = 0.0;
        
        if (multiplier.equals("Very Limited Group")) {
            cost = 0.25;
        }
        else if (multiplier.equals("Limited Group")) {
            cost = 0.50;
        }
        
        else if (multiplier.equals("Broad Group")) {
            cost = 1.0;
        }
        
        return cost;
    }
    
    public int identifyAdvantage(AbilityImport ai, int line) {
        int index,count;
        String possibleAdvantage;
        
        possibleAdvantage = ai.getImportLine(line);
        if ( possibleAdvantage != null && possibleAdvantage.indexOf("Effect: Variable, +1") != -1) return 1;
        return 0;
    }
    
    
    public Object[][] getImportPatterns() {
        return patterns;
    }
    
}