/*
 * powerTunneling.java
 *
 * Created on July 21, 2002, 9:58 PM
 */

package champions.powers;

import champions.*;
import champions.interfaces.*;
import champions.exception.*;
import champions.event.*;
import champions.parameters.ParameterList;
/**
 *
 * @author  unknown
 *
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
 *
 * The Following Steps must be performed to upgrade Power to Reconfigurable Format:
 * 1) Create costArray.
 * 2) Add the getCostArray() method, returning costArray
 * 3) Remove existing calculateCPCost.
 */
public class powerTunneling extends Power implements ChampionsConstants {
    static final long serialVersionUID = 2002072230347033561L;
    
    static private Object[][] parameterArray = {
        {"DistanceFromCollision","Ability.MOVEDISTANCE", Integer.class, new Integer(1), "Tunneling DistanceFromCollision", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(1)},
        {"ExtraDEF","Ability.EXTRADEF", Integer.class, new Integer(0), "Extra DEF", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(0)},
        {"TotalDEF","Ability.TOTALDEF", Integer.class, new Integer(0), "Total DEF", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(0)},
        {"NoncombatX","Power.NONCOMBATX", Integer.class, new Integer(2), "Non-Combat Multiplier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(2)},
        {"FillIn","Power.FILLIN", Boolean.class, new Boolean(false), "Fill In", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
    };
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "Tunneling \\(([0-9]*)\" through DEF ([0-9]*).*",
                  new Object[] { "DistanceFromCollision", Integer.class, "TotalDEF", Integer.class}},
                  { "Non-Combat Multiplier: ([0-9]*),.*", new Object[] { "NoncombatX", Integer.class}},
                  { "Non-Combat \\(MPH\\).*",null},
                  { "(Tunnels: Not Left Behind.*)", new Object[] { "FillIn", Boolean.class}},
                  { "(Tunnels:.*)", null}
    };
    
    // Cost Array - See Power.getCostArray()
    static private Object[][] costArray = {
        //        { "Base", BASE_COST, STATIC_RECONFIG, ZERO_RECONFIG, new Integer(10) },
        { "DistanceFromCollision", GEOMETRIC_COST, DYNAMIC_RECONFIG, ALL_RECONFIG, new Integer(5), new Integer(1), new Integer(0), new Integer(0) },
        //        { "TotalDEF", GEOMETRIC_COST, DYNAMIC_RECONFIG, ALL_RECONFIG, new Integer(0), new Integer(1), new Integer(0), new Integer(0) },
        { "ExtraDEF", GEOMETRIC_COST, DYNAMIC_RECONFIG, ALL_RECONFIG, new Integer(3), new Integer(1), new Integer(0), new Integer(0) },
        { "NoncombatX", LOGRITHMIC_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(5), new Integer(2), new Integer(2), new Integer(2) },
        { "FillIn", BOOLEAN_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(10)},
    };
    
    
    // Known Caveats Array
    private static String[] caveats = {
        
    };
    
    // Custom Adders
    private static Object[] customAdders = {
        Limitation.class, "Limited Medium",
        Limitation.class, "Fill In Always Occurs"
    };
    
    // Power Definition Variables
    private static String powerName = "Tunneling"; // The Name of the Power
    private static String targetType = "MOVE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "HALFMOVE"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = -1; // The Point cost of a single Damage Class of this power.
    private static String attackType = null; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = null; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 1; // Indicates the END Multiplier for this power
    private static boolean dynamic = true;
    private static String description = "Tunneling";
    
    /** Creates new powerHandToHandAttack */
    public powerTunneling()  {
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
        Integer distance ;
        try {
        	distance = (Integer)parameterList.getParameterValue("DistanceFromCollision");
        }
        catch(Exception e)
        {
        	distance = (Integer)parameterList.getParameterValue("Distance");
        }
        Integer NoncombatX = (Integer)parameterList.getParameterValue("NoncombatX");
        Integer totalDEF = (Integer)parameterList.getParameterValue("TotalDEF");
        
        if (totalDEF.intValue() > 0) {
            int extraDEF = totalDEF.intValue() - distance.intValue();
            parameterList.setParameterValue("ExtraDEF", new Integer(extraDEF));
            parameterList.setParameterValue("TotalDEF", new Integer(-1) );
        }
        
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
        //ability.addDiceInfo( "DamageDie", die, "Energy Blast Damage");
        
        // Add A Damage Class Info
        //ability.add("Base.DC",  new Double(ChampionsUtilities.StringToNormalDC(die)), true);
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        ability.add("Ability.ISMOVEMENT", "TRUE", true);
        ability.add("Ability.MOVETYPE", "Tunneling", true);
        
        ability.setRequiresTarget(false);
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
       
        
        
        // Return true to indicate success
        return true;
    }
    
 /*   public int calculateCPCost(Ability ability) {
        ParameterList parameterList = getParameterList(ability);
        Integer distance = (Integer)parameterList.getParameterValue("DistanceFromCollision");
        Integer NoncombatX = (Integer)parameterList.getParameterValue("NoncombatX");
  
        int cost = 10;
  
        if ( distance.intValue() > 5) cost += (distance.intValue() - 5) * 2;
        cost += (NoncombatX.intValue() - 2) * 5;
        return cost;
    } */
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        Integer distance ;
        try {
        	distance = (Integer)parameterList.getParameterValue("DistanceFromCollision");
        }
        catch(Exception e)
        {
        	distance = (Integer)parameterList.getParameterValue("Distance");
        }
        Integer extraDEF = (Integer)parameterList.getParameterValue("ExtraDEF");
        Integer NoncombatX = (Integer)parameterList.getParameterValue("NoncombatX");
        boolean fillIn = (Boolean)parameterList.getParameterValue("FillIn");
        
        String fillInString = fillIn ? ", Fill In" : "";
        
        int totalDEF = distance.intValue() + extraDEF.intValue();
        
        return distance.toString() + "\" Tunneling (Through " + totalDEF +
                " DEF, NC: " +
                Integer.toString( distance.intValue() * NoncombatX.intValue() ) +
                "\"" + fillInString + ")";
    }
    
    /** Attempt to identify power
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     */
    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();
        
        if ( power != null && ( power.equals( "TUNNELING" ) || power.equals( "Tunneling" ) )){
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
    /** Returns Power Cost array for this Power.
     *
     * The Power cost array is an Object[] array, which contains information detailing how to
     * calculate the cost of a power and reconfigure a power when the CP for an ability is adjusted.
     *
     * It is in the follow format:
     * Object[][] costArray = {
     * { Parameter, Type, Dynamic, ReconfigPercent, Type Options ... },
     * ...
     * }
     *
     * Where:
     * Parameter -> String representing the parameterName.  Must be parameter from getParameterArray() array.
     * Type -> Type of Cost Calculation: NORMAL_DICE_COST, KILLING_DICE_COST, GEOMETRIC_COST, LOGRITHMIC_COST,
     *     LIST_COST, BOOLEAN_COST, COMBO_COST.
     * Dynamic -> Indicater of Dynamic or Static reconfigurability: DYNAMIC_RECONFIG or STATIC_RECONFIG.
     * ReconfigPercent -> Integer indicate what percent of reconfigured CP should be allocated to this parameter
     *     by default.  Can be 0 to 100 or PROPORTIONAL_RECONFIG.  PROPORTIONAL_RECONFIG will base the proportion
     *     on the configuration of the base power.
     * Type Options -> Custom options depending on the specified type, as follows:
     *     NORMAL_DICE_COST -> PtsPerDC:Integer, Base:Integer, Minimum:Integer.
     *     KILLING_DICE_COST -> PtsPerDC:Integer, Base:Integer, Minimum:Integer.
     *     GEOMETRIC_COST -> X:Integer, Y:Integer, Base:Integer, Minimum:Integer.
     *     LOGRITHMIC_COST -> PtsPerMultiple:Integer, Multiple:Integer, Base:Integer, Minimum:Integer.
     *     LIST_COST -> PtsPerItem:Integer, Base:Integer.
     *     BOOLEAN_COST -> PtsForOption:Integer.
     *     COMBO_COST -> OptionCostArray:Integer[], OptionNames:String[].
     *
     */
    public Object[][] getImportPatterns() {
        return patterns;
    }
    
    /** Returns Power Cost array for this Power.
     *
     * The Power cost array is an Object[] array, which contains information detailing how to
     * calculate the cost of a power and reconfigure a power when the CP for an ability is adjusted.
     *
     * It is in the follow format:
     * Object[][] costArray = {
     * { Parameter, Type, Dynamic, ReconfigPercent, Type Options ... },
     * ...
     * }
     *
     * Where:
     * Parameter -> String representing the parameterName.  Must be parameter from getParameterArray() array.
     * Type -> Type of Cost Calculation: NORMAL_DICE_COST, KILLING_DICE_COST, GEOMETRIC_COST, LOGRITHMIC_COST,
     *     LIST_COST, BOOLEAN_COST, COMBO_COST.
     * Dynamic -> Indicater of Dynamic or Static reconfigurability: DYNAMIC_RECONFIG or STATIC_RECONFIG.
     * ReconfigPercent -> Integer indicate what percent of reconfigured CP should be allocated to this parameter
     *     by default.  Can be 0 to 100 or PROPORTIONAL_RECONFIG.  PROPORTIONAL_RECONFIG will base the proportion
     *     on the configuration of the base power.
     * Type Options -> Custom options depending on the specified type, as follows:
     *     NORMAL_DICE_COST -> PtsPerDC:Integer, Base:Integer, Minimum:Integer.
     *     KILLING_DICE_COST -> PtsPerDC:Integer, Base:Integer, Minimum:Integer.
     *     GEOMETRIC_COST -> X:Integer, Y:Integer, Base:Integer, Minimum:Integer.
     *     LOGRITHMIC_COST -> PtsPerMultiple:Integer, Multiple:Integer, Base:Integer, Minimum:Integer.
     *     LIST_COST -> PtsPerItem:Integer, Base:Integer.
     *     BOOLEAN_COST -> PtsForOption:Integer.
     *     COMBO_COST -> OptionCostArray:Integer[], OptionNames:String[].
     *
     */
    public Object[][] getCostArray(Ability ability) {
        return costArray;
    }
    
    /** Returns a Description of the Power
     */
    public String getDescription() {
        return description;
    }
    
    /** Returns whether power can be dynamcially reconfigured.
     */
    public boolean isDynamic() {
        return dynamic;
    }
    
    /**
     * Returns a String[] of Caveats about the Power
     * Power uses this method to automatically build the getCaveats()
     * String.  The Strings returns by getCaveatArray() will be assembled into
     * list form and returned via getCaveats().
     *
     * Return an empty array if there are no known caveats for this power.
     */
    public String[] getCaveatArray() {
        return caveats;
    }
    
    public Object[] getCustomAddersArray() {
        return customAdders;
    }
    
}