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
public class advantageHoleInTheMiddle extends AdvantageAdapter implements ChampionsConstants{
    static final long serialVersionUID = -6399128355213532419L;
    /** Creates new advCombatModifier */
    static private String[] holesizeOptions = {"Fixed Size", "Variable Size" };
    
    
    static private Object[][] parameterArray = {
        //        {"Multiplier","CEV.MULTIPLIER", String.class, "TRUE"},
        {"HoleSize", "Advantage#.HOLESIZE", String.class, "Fixed Size", "Hole Size", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", holesizeOptions }
    };
    
    // Advantage Definition Variables
    public static String advantageName = "Hole in the Middle"; // The Name of the Advantage
    private static boolean affectsDC = true; // Indicates the Advantage affects the cost of damage classes
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "Hole in the Middle: (.*), .*", new Object[] { "HoleSize", String.class }}
    };
    
    public advantageHoleInTheMiddle() {
    }
    
    public String getName() {
        return "Hole in the Middle";
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
        String holesize = (String)parameterList.getParameterValue("HoleSize");
        
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        // AdvantageAreaEffect has nothing to validate.
        
        if (holesize.equals("FIXED")) {
            parameterList.setParameterValue("HoleSize", "Fixed Size");
        }
        else if (holesize.equals("VARIABLE")) {
            parameterList.setParameterValue("HoleSize", "Variable Size");
        }
        
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
        ParameterList parameterList = getParameterList();
        String holesize = (String)parameterList.getParameterValue("HoleSize");
        
        StringBuffer sb = new StringBuffer();
        sb.append( "Hole in the middle: " + holesize );
        
        return sb.toString();
    }
     
    
    public double calculateMultiplier() {
        ParameterList parameterList = getParameterList();
        String holesize = (String)parameterList.getParameterValue("HoleSize");
        double cost = 0.0;
        
        int i;
        
        for (i = 0;i< holesizeOptions.length;i++) {
            if ( holesize.equals(holesizeOptions[i] )) break;
        }
        
        cost = (i+1) * .25;
        
        return cost;
    }
    
        public int identifyAdvantage(AbilityImport ai,int line) {
        int index,count;
        String possibleAdvantage;
        
        possibleAdvantage = ai.getImportLine(line);
        if ( possibleAdvantage != null && possibleAdvantage.indexOf( "Hole" ) != -1 ) {
            return 10;
        }

        return 0;
    } 

    
    public Object[][] getImportPatterns() {
        return patterns;
    }
    
}