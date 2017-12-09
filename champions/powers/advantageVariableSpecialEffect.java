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
 *
 * To Convert from old format powers, to new format powers:
 *
 * 1) Add implements ChampionsConstants to class definition.<P>
 * 2) Copy and Fill in Advantage Definition Variables. <P>
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
 * 13) Change serialVersionUID by some amount.
 * 14) Add patterns array and define import patterns.<P>
 * 15) Add getImportPatterns() method.<P>
 */
public class advantageVariableSpecialEffect extends AdvantageAdapter implements ChampionsConstants {
    static final long serialVersionUID = -6399128353213222419L;

    private static String[] levelOptions = { "Limited Group", "Any Special Effect" };
    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first have is replaced by
    // "Advantage#" and only the second half is kept.
    static private Object[][] parameterArray = {
        {"Level","Advantage.VARIABLELEVEL", String.class, "Limited Group", "Level", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", levelOptions},
        {"Group","Advantage.GROUPDESCRIPTION", String.class, "", "Type", STRING_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "FIELDWIDTH", new Integer(200)}
    };
    
    // Advantage Definition Variables
    public static String advantageName = "Variable Special Effect"; // The Name of the Advantage
    private static boolean affectsDC = false; // Indicates the Advantage affects the cost of damage classes
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability

   // Import Patterns Definitions
    private static Object[][] patterns = {
        
    };
    
    /** Creates new advCombatModifier */
    public advantageVariableSpecialEffect() {
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
        
        // Delayed Effect has no parameters
      
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        String level = (String)parameterList.getParameterValue("Level");
        
        parameterList.setVisible("Group", level.equals("Limited Group"));
        
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

        // Update the Stored Description for this Limitation
        setDescription( getConfigSummary() );
        
        // Return True to indicate success in configuringPAD
        return true;
    }


    public double calculateMultiplier() {
        ParameterList pl = getParameterList();
        String level = (String)pl.getParameterValue("Level");
        
        if( level.equals("Limited Group")) {
            return 0.25;
        }
        else {
            return 0.5;
        }
    }

    public String getConfigSummary() {
        ParameterList pl = getParameterList();
        String level = (String)pl.getParameterValue("Level");
        
        if ( level.equals("Limited Group")) {
            String desc = (String)pl.getParameterValue("Group");
            
            if ( desc != null && desc.equals("")== false) {
                return "Variable Special Effect(Limited Group)";
            }
            else {
                return "Variable Special Effect(" + desc + ")";
            }
        }
        else {
            return "Variable Special Effect(Any SFX)";
        }
    }
    

    
    /** Removes Special Configurations from the Ability which the advantage might have added.
     *
     * RemoveAdvantage is called prior to an advantage being removed from an ability.  The
     * removeAdvantage method should remove any value pairs that it specifically added to the
     * ability.  
     *
     * The ability will take care of removing the advantages configuration, object, and parameter
     * lists under the advantage#.* value pairs.
     */
    public void removeAdvantage() {
        // Remove the PType so it will be reset correctly by the power
        
    }

}