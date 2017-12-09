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
public class limitationLimitedSpecialEffect extends LimitationAdapter implements ChampionsConstants{
    static final long serialVersionUID = -6399128355213532419L;
    /** Creates new advCombatModifier */
    static private String[] limitedFXOptions = {"Very Common", "Common", "Uncommon" };
    
    static private Object[][] parameterArray = {
        {"LimitedFX", "Limitation#.LimitedFX", String.class, "Very Common", "Limited Special Effect", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", limitedFXOptions },       
    };
    
    // Limitation Definition Variables
    public static String limitationName = "Limited Special Effect"; // The Name of the Limitation
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
  //      { "Affects: (.*), .*", new Object[] { "AdjustmentAffectsImport", String.class }}
        
    };
    
    public limitationLimitedSpecialEffect() {
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
        String limitedFX = (String)parameterList.getParameterValue("LimitedFX");
        
        // Start to Actually Configure the Power.
        // The Add Power Info should always be executed to add information to the ability.
        // All of this information should be set in the Power Definition Variables at the
        // top of this file
        int index = ability.addLimitationInfo(this, limitationName, parameterList);
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure.
        
        // No Validation Necessary
        if (limitedFX.equals("VERYCOMMON")) {
            parameterList.setParameterValue("LimitedFX", "Very Common");
        }
        else if (limitedFX.equals("COMMON")) {
            parameterList.setParameterValue("LimitedFX", "Common"); 
        }
        else if (limitedFX.equals("UNCOMMON")) {
            parameterList.setParameterValue("LimitedFX", "Uncommon"); 
        }
        
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
        String limitedFX = (String)parameterList.getParameterValue("LimitedFX");
        
        return "Limited FX (" + limitedFX + ")";
    }
    
    
    public double calculateMultiplier() {
        ParameterList parameterList = getParameterList();
        String limitedFX = (String)parameterList.getParameterValue("LimitedFX");
        
        // Add the Integer Value for the Adjustment level to the power...
        int limitedLevel;
        for (limitedLevel = 0; limitedLevel < limitedFXOptions.length; limitedLevel ++) {
            if ( limitedFX.equals( limitedFXOptions[limitedLevel] ) ) break;
        }
        
        switch ( limitedLevel ) {
            case 0:
                return -0.25;
            case 1:
                return -0.5;
            case 2:
                return -1;
            default:
                return 0;
        }
    }


    public Object[][] getImportPatterns() {
        return patterns;
    }
    
}