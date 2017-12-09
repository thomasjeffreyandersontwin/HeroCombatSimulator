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
public class limitationConditionalPower extends LimitationAdapter implements ChampionsConstants{
    static final long serialVersionUID = -6399128355213532419L;
    /** Creates new advCombatModifier */
    static public String[] levelOptions = {
        "Very Uncommon Circumstances",
        "Uncommon Circumstances",
        "Common Circumstances",
        "Very Common Circumstances",
        "Extremely Common Circumstances",
        "Ubiquitous Circumstances" };  
        
        static private double[] levelCosts = new double[] { 0, 0.25, 0.5, 1, 1.5, 2 };
    
    static private Object[][] parameterArray = {
        {"Condition", "Limitation#.Condition", String.class, "", "Condition", STRING_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED },       
        {"Level", "Limitation#.Level", String.class, "Very Uncommon Circumstances", "Frequency", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", levelOptions },       
    };
    
    // Limitation Definition Variables
    public static String limitationName = "Conditional Power"; // The Name of the Limitation
    private static boolean unique = false; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
  //      { "Affects: (.*), .*", new Object[] { "AdjustmentAffectsImport", String.class }}
        
    };
    
    public limitationConditionalPower() {
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
        // Note that if the limitation is not unique, the values should always be
        // accessed via the parameterList and not directly.
        //parameterList.copyValues(ability,index);
        
        // Add Extra Value/Pairs used by the Limitation/BattleEngine
        
        // Update the Stored Description for this Limitation
        setDescription( getConfigSummary() );
        
        // Return True to indicate success in configuringPAD
        return true;
        
    }
    
    
    public String getConfigSummary() {
        ParameterList parameterList = getParameterList();
        String reason = (String)parameterList.getParameterValue("Condition");
        
        return "Conditional Power (" + reason + ")";
    }
    
    
    public double calculateMultiplier() {
        ParameterList parameterList = getParameterList();
        String level = (String)parameterList.getParameterValue("Level");
        
        // Add the Integer Value for the Adjustment level to the power...
        int limitedLevel;
        for (limitedLevel = 0; limitedLevel < levelOptions.length; limitedLevel ++) {
            if ( level.equals( levelOptions[limitedLevel] ) ) return -1 * levelCosts[limitedLevel];
        }
        
        return 0;
    }


    public Object[][] getImportPatterns() {
        return patterns;
    }
    
}