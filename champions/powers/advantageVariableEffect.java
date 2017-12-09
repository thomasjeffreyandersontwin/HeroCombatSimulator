/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;

import champions.*;
import champions.Power;
import champions.interfaces.*;
import champions.parameters.ParameterList;

/**
 *
 * @author  unknown
 * @version
 */
public class advantageVariableEffect extends AdvantageAdapter implements ChampionsConstants{
    static final long serialVersionUID = -6399128355213532419L;
    /** Creates new advCombatModifier */
    static private String[] AdjustmentOptions = {"Single Ability or Char.", "Any 1 Power Of A Given SFX",
    "Any 2 Powers Of A Given SFX", "Any 4 Powers Of A Given SFX", "Variable, All Powers Of A Given SFX" };
    
    static private Object[][] parameterArray = {
        {"AdjustmentAffects", "Advantage#.ADJUSTMENTAFFECTS", String.class, "Single Ability or Stat", "Adjustment Affects", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", AdjustmentOptions },
        {"AdjustmentAffectsImport", "Advantage#.ADJUSTMENTAFFECTSIMPORT", String.class, "Single Ability or Stat", "Adjustment Affects",STRING_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED}
        
    };
    
    // Advantage Definition Variables
    public static String advantageName = "Variable Effect"; // The Name of the Advantage
    private static boolean affectsDC = true; // Indicates the Advantage affects the cost of damage classes
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "Affects: (.*), .*", new Object[] { "AdjustmentAffectsImport", String.class }},
        { "Variable Effect.*\\(.*\\) \\((.*)\\):.*", new Object[] { "AdjustmentAffects", String.class }},
        //hd
        { "(any|Two Powers|Four Powers|All Powers).*", new Object[] { "AdjustmentAffectsImport", String.class }},
        
    };
    
    public advantageVariableEffect() {
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
        String adjustmentaffects = (String)parameterList.getParameterValue("AdjustmentAffects");
        String adjustmentaffectsimport = (String)parameterList.getParameterValue("AdjustmentAffectsImport");

//            static private String[] AdjustmentOptions = {"Single Ability or Char.", "Any 1 Power Of A Given SFX",
//    "Any 2 Powers Of A Given SFX", "Any 4 Powers Of A Given SFX", "Variable, All Powers Of A Given SFX" };

        
        
        if (adjustmentaffectsimport.startsWith("any") || adjustmentaffects.startsWith("ONE")) {
            parameterList.setParameterValue("AdjustmentAffects", "Any 1 Power Of A Given SFX");
        } 
        else if (adjustmentaffectsimport.startsWith("Two Powers") || adjustmentaffects.startsWith("TWO")) {
            parameterList.setParameterValue("AdjustmentAffects", "Any 2 Powers Of A Given SFX");
        } 
        else if (adjustmentaffectsimport.startsWith("Four Powers") || adjustmentaffects.startsWith("FOUR")) {
            parameterList.setParameterValue("AdjustmentAffects", "Any 4 Powers Of A Given SFX");
        } 
        else if (adjustmentaffectsimport.startsWith("All Powers") || adjustmentaffects.startsWith("ALL")) {
            parameterList.setParameterValue("AdjustmentAffects", "Variable, All Powers Of A Given SFX");
        }
        
        
        Power p = ability.getPower();
        
        // Clear out any non-SFX/characteristic
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        // AdvantageAreaEffect has nothing to validate.
        
      /*  if (adjustmentaffectsimport.equals("Single Power") ){
            parameterList.setParameterValue( "AdjustmentAffects", "Single Ability or Stat");
            parameterList.setParameterValue( "AdjustmentAffectsImport" , "");
            adjustmentaffects = (String)parameterList.getParameterValue("AdjustmentAffects");
            ability.reconfigurePower();
       
        }
        else if (adjustmentaffectsimport.equals("Single Power of Special Effect") ){
            parameterList.setParameterValue( "AdjustmentAffects", "Variable Ability or Stat");
            parameterList.setParameterValue( "AdjustmentAffectsImport" , "");
            adjustmentaffects = (String)parameterList.getParameterValue("AdjustmentAffects");
            ability.reconfigurePower();
        }
        else if (adjustmentaffectsimport.equals("All Powers of Special Effect") ){
            parameterList.setParameterValue( "AdjustmentAffects", "Multiple Abilities and Stats");
            parameterList.setParameterValue( "AdjustmentAffectsImport" , "");
            adjustmentaffects = (String)parameterList.getParameterValue("AdjustmentAffects");
            ability.reconfigurePower();
        } */
        
        // Add the Integer Value for the Adjustment level to the power...
        int adjustmentLevel;
        for (adjustmentLevel = 0; adjustmentLevel < AdjustmentOptions.length; adjustmentLevel ++) {
            if ( adjustmentaffects.equals( AdjustmentOptions[adjustmentLevel] ) ) break;
        }
        
        // AdjustmentLevel + 1 should now correspond to the appropriate Constant for Adjustment Levels
        ability.add("Ability.ADJUSTMENTLEVEL", new Integer(adjustmentLevel+1),true);
        
        
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
        ability.reconfigurePower();
        
        // Update the Stored Description for this Limitation
        setDescription( getConfigSummary() );
        
        // Return True to indicate success in configuringPAD
        return true;
        
    }
    
    
    public String getConfigSummary() {
        ParameterList parameterList = getParameterList();
        String adjustmentaffects = (String)parameterList.getParameterValue("AdjustmentAffects");
        
        return adjustmentaffects;
    }
    
    
//    public double calculateMultiplier() {
//        switch ( ability.getAdjustmentLevel() ) {
//            case ADJ_SINGLE_ADJUSTMENT:
//                return 0;
//            case ADJ_VARIABLE_1_ADJUSTMENT:
//                return .25;
//            case ADJ_VARIABLE_2_ADJUSTMENT:
//                return .5;
//            case ADJ_VARIABLE_4_ADJUSTMENT:
//                return 1;
//            case ADJ_VARIABLE_ALL_ADJUSTMENT:
//                return 2;
//            default:
//                return 0;
//        }
//    }
    
    public double calculateMultiplier() {
        ParameterList parameterList = getParameterList();
        String adjustmentaffects = (String)parameterList.getParameterValue("AdjustmentAffects");
        
        if (adjustmentaffects.equals("Single Ability or Stat")) {
            return 0;
        }
        else if (adjustmentaffects.equals("Any 1 Power Of A Given SFX")) {
            return 0.25;
        }
        else if (adjustmentaffects.equals("Any 2 Powers Of A Given SFX")) {
            return 0.50;
        }
        else if (adjustmentaffects.equals("Any 4 Powers Of A Given SFX")) {
            return 1;
        }
        else if (adjustmentaffects.equals("Variable, All Powers Of A Given SFX")) {
            return 2;
        }
        return 0;
    }
    
    /** Attempt to identify Advantage
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     * @param ai AbilityImport which is currently being imported.
     * @return Value indicating likelyhood that AbilityImport is this kind of power.
     * 0 indicates there was no recognition.
     * 5 indicates probable match.
     * 10 indicates almost certain recognition.
     *
     */
    //    public int identifyAdvantage(AbilityImport ai, int line) {
    //        int index,count;
    //        String possibleAdvantage;
    //
    //        possibleAdvantage = ai.getImportLine(line);
    //
    //        if ( possibleAdvantage != null && possibleAdvantage.indexOf( "Variable Effect" ) != -1 ) {
    //            return 10;
    //        }
    //
    //        return 0;
    //    }
    
    public int identifyAdvantage(AbilityImport ai, int line) {
        int index,count;
        String possibleAdvantage;
        
        possibleAdvantage = ai.getImportLine(line);

        if ( possibleAdvantage != null && (possibleAdvantage.startsWith( "All Powers") || possibleAdvantage.startsWith( "Four Powers") || possibleAdvantage != null && possibleAdvantage.startsWith( "Two Powers") || possibleAdvantage != null && possibleAdvantage.startsWith( "any") || possibleAdvantage.indexOf("Variable Effect") != -1)) return 10;
        return 0;
    }
    
    public Object[][] getImportPatterns() {
        return patterns;
    }
    
    public void removeAdvantage() {
        ability.remove("Ability.ADJUSTMENTLEVEL");
    }
    
}