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
public class SpecialParameterFixedCost extends SpecialParameterAdapter implements ChampionsConstants {
    static final long serialVersionUID = -3750155454162962388L;
    /** Creates new advCombatModifier */
    
    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first have is replaced by
    // "SpecialParameter#" and only the second half is kept.
    static private Object[][] parameterArray = {
        {"FixedCPCost","Ability.FIXEDCP", Integer.class, new Integer(0), "Fixed Character Point Cost", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED}
    };
    
    // SpecialParameter Definition Variables
    private static String specialParameterName = "Fixed Cost"; // The Name of the SpecialParameter
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    

    public SpecialParameterFixedCost() {
        
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
        Integer cost = (Integer)parameterList.getParameterValue("FixedCPCost");
       
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
       // ability.add("Ability.FIXEDCPENABLED", "TRUE");
        ability.setFixedCPCost(cost);
        ability.setFixedCPEnabled(true);

        // Update the Stored Description for this Limitation
        ability.setSpecialParameterDescription(index,getConfigSummary(ability,index));
        
        // Reconfigure the cost of the ability
        ability.calculateCPCost();
        
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
        //ability.remove("Ability.FIXEDCPENABLED");
        //ability.remove("Ability.FIXEDCP");
        ability.setFixedCPEnabled(false);
    }

    public String getConfigSummary(Ability ability, int index) {
        ParameterList parameterList = getParameterList(ability,index);
        Integer cost = (Integer)parameterList.getParameterValue("FixedCPCost");
        
        return "Fixed Cost: " + cost;
    }

}