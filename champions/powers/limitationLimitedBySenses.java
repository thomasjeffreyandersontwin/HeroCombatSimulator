/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM        int grouptotal = parameterList.getIndexedSize("SenseGroup" );
 */

package champions.powers;

import champions.Ability;
import champions.AbilityImport;
import champions.LimitationAdapter;
import champions.interfaces.ChampionsConstants;
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
 */
public class limitationLimitedBySenses extends LimitationAdapter implements ChampionsConstants {
    static final long serialVersionUID = -6399128355213332419L;
    /** Creates new advCombatModifier */
    
    static private String[] senseGroupArray = {
        "All Sight", "All Hearing", "All Radio", "All Smell", "All Unusual", "All Mental"
    };
    
    
    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first have is replaced by
    // "Advantage#" and only the second half is kept.
    static private Object[][] parameterArray = {
        {"SenseGroup","SenseGroup*.SENSEGROUP", String.class, null, "Sense Group", LIST_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", senseGroupArray},
        //tracks the total number of groups selected for cost purposes
        {"GroupTotal","Power.GROUPTOTAL", Integer.class, new Integer(0), "Total Groups", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
    };
    
    // Advantage Definition Variables
    public static String limitationName = "Limited By Senses"; // The Name of the Advantage
    private static boolean unique = false; // Indicates whether multiple copies can be added to ability
    
    public limitationLimitedBySenses() {
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
       // String multiplier = (String)parameterList.getParameterValue("Multiplier");
       // String description = (String)parameterList.getParameterValue("Description");
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        // AdvantageAreaEffect has nothing to validate.
        int grouptotal = parameterList.getIndexedParameterSize("SenseGroup" );
        parameterList.setParameterValue("GroupTotal", new Integer( grouptotal - 1 ) );
        
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
        // Note that if the advantage is not unique, the values should always be
        // accessed via the parameterList and not directly.
        //parameterList.copyValues(ability,index);
        
        // Add Extra Value/Pairs used by the Advantage/BattleEngine
        ability.reconfigurePower();
        
        // Update the Stored Description for this Limitation
        setDescription( getConfigSummary() );
        
        // Return True to indicate success in configuringPAD
        return true;
        
    }
    
    
    public String getConfigSummary() {
        //        ParameterList parameterList = getParameterList();
        //
        //        String s = description + "(" + multiplier;
        //        s = s + ")";
        //        return (s);
        return ".";
    }
    
    public double calculateMultiplier() {
        ParameterList parameterList = getParameterList();
        double cost = 0.0;
        
        int i;
        
        int grouptotal = parameterList.getIndexedParameterSize("SenseGroup" );
        //if (grouptotal != null) {
        cost = grouptotal * -0.25;
        for (i = 0; i < grouptotal; i++) {
            if (parameterList.getIndexedParameterValue("SenseGroup", i).equals("All Sight")) {
                cost = cost + -0.25;
            }
            
        }
        
        //}
        return cost;
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
    public int identifyLimitation(AbilityImport ai, int line) {
        int index,count;
        String possibleLimitation;
        
        possibleLimitation = ai.getImportLine(line);
        //remove the space in " -" for compatibility for HD.  Very week expression
        if ( possibleLimitation != null && possibleLimitation.indexOf("Set Effect") != -1) return 1;
        return 0;
    }
    
}