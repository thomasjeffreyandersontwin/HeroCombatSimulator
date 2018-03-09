/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;


import champions.Ability;
import champions.AdvantageAdapter;
import champions.interfaces.ChampionsConstants;
import champions.parameters.ParameterList;


public class advantageCosmic extends AdvantageAdapter implements ChampionsConstants {
    static final long serialVersionUID = -6399121353283532419L;


    static private Object[][] parameterArray = {
    };
    
    // Advantage Definition Variables
    public static String advantageName = "Cosmic"; // The Name of the Advantage
    private static boolean affectsDC = false; // Indicates the Advantage affects the cost of damage classes
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability

    public advantageCosmic() {
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


      
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure

        if ( ability.hasAdvantage(advantagePowersCanBeChangedAsAZeroPhaseAction.advantageName) ||
                ability.hasAdvantage(advantagePowersCanBeChangedAsAHalfPhaseAction.advantageName) ||
                ability.hasAdvantage(advantageNoSkillRollRequired.advantageName)) {
            return false;
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

        // Update the Stored Description for this Limitation
        setDescription( getConfigSummary() );

        ability.reconfigurePower();
        
        // Return True to indicate success in configuringPAD
        return true;
    }


    public double calculateMultiplier() {

        return 0.5;
    }

    public String getConfigSummary() {
        return getName();

    }

    @Override
    public boolean isPrivate() {
        return true;
    }



}