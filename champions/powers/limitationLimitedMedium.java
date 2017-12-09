/*
 * limitationLimitedMedium.java
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
public class limitationLimitedMedium extends LimitationAdapter implements ChampionsConstants{
    static final long serialVersionUID = 2002072930347033561L;
    /** Creates new advCombatModifier */
    static private String[] limitedMediumOptions = {"Limited", "Very Limited" };
    
    static private Object[][] parameterArray = {
        {"LimitedMedium", "Limitation#.LimitedMedium", String.class, "Limited", "Medium", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", limitedMediumOptions },       
    };
    
    // Limitation Definition Variables
    public static String limitationName = "Limited Medium"; // The Name of the Limitation
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
  //      { "Affects: (.*), .*", new Object[] { "AdjustmentAffectsImport", String.class }}
        
    };
    
    public limitationLimitedMedium() {
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
        String limitedFX = (String)parameterList.getParameterValue("LimitedMedium");
        
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
        String limitedMedium = (String)parameterList.getParameterValue("LimitedMedium");
        
        return "Limited Medium (" + limitedMedium + ")";
    }
    
    
    public double calculateMultiplier() {
        ParameterList parameterList = getParameterList();
        String limitedMedium = (String)parameterList.getParameterValue("LimitedMedium");
        
        // Add the Integer Value for the Adjustment level to the power...
        int limitedLevel;
        for (limitedLevel = 0; limitedLevel < limitedMediumOptions.length; limitedLevel ++) {
            if ( limitedMedium.equals( limitedMediumOptions[limitedLevel] ) ) break;
        }
        
        switch ( limitedLevel ) {
            case 0:
                return -0.5;
            case 1:
                return -1;
            default:
                return 0;
        }
    }


    public Object[][] getImportPatterns() {
        return patterns;
    }
    
}