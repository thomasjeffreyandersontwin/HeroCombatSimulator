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
public class advantageIncreasedStunMultiplier extends AdvantageAdapter implements ChampionsConstants{
    static final long serialVersionUID = -6399128355213532419L;
    /** Creates new advCombatModifier */
    
    
    static private Object[][] parameterArray = {
        //        {"Multiplier","CEV.MULTIPLIER", String.class, "TRUE"},
        {"StunMultiplier", "Ability.STUNMULTIPLIER", Integer.class, new Integer(1), "Increased Stun Multiplier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
    };
    
    // Advantage Definition Variables
    public static String advantageName = "Increased Stun Multiplier"; // The Name of the Advantage
    private static boolean affectsDC = true; // Indicates the Advantage affects the cost of damage classes
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "Increased STUN Multiplier: ([0-9].*), .*", new Object[] { "StunMultiplier", Integer.class }},
        //HeroDesigner
        //+2 Increased STUN Multiplier (+1/2)
        { "(\\+[0-9]*) Increased STUN Multiplier .*", new Object[] { "StunMultiplier", Integer.class }}
    };
    
    public advantageIncreasedStunMultiplier() {
    }
    
    public String getName() {
        return "Increased Stun Multiplier";
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
        Integer stunmultiplier = (Integer)parameterList.getParameterValue("StunMultiplier");
        
        
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
                
        if (stunmultiplier != null) {
            ability.add("Ability.STUNMULTIPLIER", stunmultiplier, true);
        }
        //ability.reconfigurePower();
        
        // Update the Stored Description for this Limitation
        setDescription( getConfigSummary() );
        
        // Return True to indicate success in configuringPAD
        return true;
        
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
        ability.remove("Ability.STUNMULTIPLIER");
    }
    
    
    public String getConfigSummary() {
        ParameterList parameterList = getParameterList();
        Integer stunmultiplier = (Integer)parameterList.getParameterValue("StunMultiplier");
        
        StringBuffer sb = new StringBuffer();
        sb.append( "+" + stunmultiplier + " Stun Multiplier" );
        
        return sb.toString();
    }
     
    
    public double calculateMultiplier() {
        ParameterList parameterList = getParameterList();
        Integer stunmultiplier = (Integer)parameterList.getParameterValue("StunMultiplier");

        double cost = stunmultiplier.intValue() * .50;
        
        return cost;
    }
    
    public int identifyAdvantage(AbilityImport ai, int line) {
        int index,count;
        String possibleAdvantage;
        
        possibleAdvantage = ai.getImportLine(line);
        if ( possibleAdvantage != null && possibleAdvantage.indexOf("Increased STUN Multiplier") != -1) return 10;
        return 0;
    }
    
    
    public Object[][] getImportPatterns() {
        return patterns;
    }
    
}