/*
 * powerHandToHandAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
 */

package champions.powers;

import champions.*;
import champions.exception.*;
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
 * 2) Copy and Fill in Power Definition Variables. <P>
 * 3) Move Parameter Information to parameterArray. <P>
 * 4) Delete getParameters method (unless special parameter handling is necessary.<P>
 * 5) Change configurePAD(Ability,DetailList) method to configurePAD(Ability ability, ParameterList parameterList).<P>
 * 6) Edit configurePAD method to use format specified below.<P>
 * 7) Change checkParameter method to checkParameter(Ability ability, <i>int padIndex</i>,
 * String key, Object value, Object oldValue);
 * 8) Edit getConfigSummary method to use parameterList instead of parseParameter methods.<P>
 * 9) Change all instances of parseParameter to getParameterValue.<P>
 * 10) Add getParameterArray method.<P>
 * 11) Edit getName method to return powerName variable.
 * 12) Change serialVersionUID by some amount.
 * 13) Add patterns array and define import patterns.<P>
 * 14) Add getImportPatterns() method.<P>
 */
public class disadvantagePhysicalLimitation extends Disadvantage implements ChampionsConstants {
    static final long serialVersionUID =5295848683348706403L;
    
    private static final String[] occursOptions =  {
        "Infrequently", "Frequently", "All the Time"
    };
    static private Integer[] occursCostArray = new Integer[] { new Integer(5), new Integer(10),new Integer(15)};
    
    private static final String[] impairsOptions =  {
        "Slightly Impairing", "Greatly Impairing", "Fully Impairing"
    };
    static private Integer[] impairsCostArray = new Integer[] { new Integer(0), new Integer(5), new Integer(10) };
    
    static private Object[][] parameterArray = {
        {"Occurs","Disadvantage.OCCURS", String.class, "Infrequently", "Occurs", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", occursOptions},
        {"Impairs","Disadvantage.IMPAIRS", String.class, "Slightly Impairing", "Impairs", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", impairsOptions},
        {"DCVLevel","Disadvantage.DCVLEVEL", Integer.class, new Integer(0), "DCV Penalty ", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
    };
    
    // Cost Array - See Power.getCostArray()
    static private Object[][] costArray = {
        { "Occurs", COMBO_COST, STATIC_RECONFIG, ZERO_RECONFIG, occursCostArray, occursOptions },
        { "Impairs", COMBO_COST, STATIC_RECONFIG, ZERO_RECONFIG,  impairsCostArray, impairsOptions },
    };
    
    // Power Definition Variables
    private static String powerName = "Physical Limitation"; // The Name of the Power
    private static String targetType = "SELF"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "PERSISTENT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "INSTANT"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = -1; // The Point cost of a single Damage Class of this power.
    private static String attackType = null; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = null; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 0; // Indicates the END Multiplier for this power
    
    // Import Patterns Definitions
    
    private static Object[][] patterns = {
        { "OCCURS: (.*)", new Object[] {"Occurs", String.class}},
        { "IMPAIRS: (.*)", new Object[] {"Impairs", String.class}},
        { "Assigned to DCV: (.*)", new Object[] {"DCVLevel", Integer.class}}
    };
    
    /** Creates new powerHandToHandAttack */
    public disadvantagePhysicalLimitation()  {
    }
    
    /* Returns an array which can be used to create the parameterList.
     */
    public Object[][] getParameterArray() {
        return parameterArray;
    }
    
    /** Get the English name of the PAD.
     * @return name of PAD
     */
    public String getName() {
        return powerName;
    }
    
    /** Configures the ability according to the parameters in parameterList.
     * The parameterList should be stored with the ability for configuration
     * later on. If an existing parameterList alread exists, it should be
     * replaced with this one.
     *
     * All value/pairs should be copied into the ability for direct access.
     */
    public boolean configurePAD(Ability ability, ParameterList parameterList) {
        // Fail immediately if ability is null
        if ( ability == null ) return false;
        
        // Always Set the ParameterList to the parameterList
        setParameterList(ability,parameterList);
        
        // Read in any parameters that will be needed to configure the power or
        // Determine the validity of the power configuration.  Read the parameters
        // from the parameterList, instead of directly from the ability, since the
        // Ability isn't configured yet.
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure.
        
        // No Validation Necessary
        
        // Always copy the configuration parameters directly into the ability.  This will
        // take the parameters stored in the parameter list and copy them into the
        // ability using the appropriate keys and values.
        parameterList.copyValues(ability);
        
        // Start to Actually Configure the Power.
        // The Add Power Info should always be executed to add information to the ability.
        // All of this information should be set in the Power Definition Variables at the
        // top of this file
        ability.addPowerInfo( this, powerName, targetType, persistenceType, activationTime);
        if ( attackType != null ) {
            ability.addAttackInfo( attackType,damageType );
            ability.add("Ability.PPDC", new Double(pointsPerDC), true);
        }
        ability.setGenerateDefaultEffects(generateDefaultDamage);
        if ( endMultiplier != 1 ) ability.setENDMultiplier(endMultiplier);
        
        // Add any dice information which is necessary to use this power.
        
        // Add A Damage Class Info
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        
        ability.setCType("DISADVANTAGE");
        ability.setTargetSelf(true);
        ability.setAutoHit(true);
        ability.setDisallowForcedActivation(true);
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    
    public String getConfigSummary(Ability ability, int not_used) {
        return "Physical Limitation";
    }
    
   
    public void triggerPower(BattleEvent be, Ability ability, DetailList effectList, Target target, int refNumber, String targetGroup) throws BattleEventException {
        Effect effect = new effectPhysicalLimitation( ability );
        
        effectList.createIndexed(   "Effect","EFFECT", effect)  ;
    }    
    /** Attempt to identify power
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     */
    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();
        
        if ( power != null && power.equals( "PHYSICALLIMITATION" )){
            return 10;
        }
        return 0;
    }
    
    /** Returns the patterns necessary to import the Power from CW.
     * The Object[][] returned should be in the following format:
     * patterns = Object[][] {
     *  { "PATTERN" , new Object[] { "PARAMETER1", parameter1.class, "PARAMETER2", parameter2.class, ... },
     *  ...
     *  }
     *
     * PATTERN should be a regular expression pattern.  For every PARAMETER* where should be one
     * parathesis group in the expression. The PARAMETERS sub-array can be null, if the line has no parameter
     * and is just informational.
     *
     * By default, the importPower will check each line of the getImportPatterns() array and if a match is
     * found, the specified parameters will be set in the powers parameter list.  It is assumed that each
     * PATTERN will only occur once.  If the pattern can occur multiple times in a valid import, a custom
     * importPower method will have to be used.
     */
    public Object[][] getImportPatterns() {
        return patterns;
    }
    
    public Object[][] getCostArray(Ability ability) {
        return costArray;
    }
}