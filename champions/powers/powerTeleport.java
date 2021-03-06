
/*
 * powerHandToHandAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
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
public class powerTeleport extends Power
implements ChampionsConstants {
    static final long serialVersionUID = 7766760941350027456L;
    
    static private Object[][] parameterArray = {
        {"DistanceFromCollision","Ability.MOVEDISTANCE", Integer.class, new Integer(10), "DistanceFromCollision", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"NoncombatX","Power.NONCOMBATX", Integer.class, new Integer(2), "Non-Combat Multiplier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(2)},
        {"IncreasedRange","Power.INCREASEDRANGE", Integer.class, new Integer(1), "Increased Range Multiple", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(1)},
        {"FixedLocations","Power.FIXED", Integer.class, new Integer(0), "Fixed Locations", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(1)},
        {"FloatingLocations","Power.FLOATING", Integer.class, new Integer(0), "Floating Locations", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(1)},
        {"MassImport","Power.MASSMULTIPLIER", Integer.class, new Integer(1), "Mass Multiplier", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(1)},
        {"MassMultiplier","Power.MASSMULTIPLIER", Integer.class, new Integer(1), "Mass Multiplier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(1)},
        {"PositionShift","Power.POSITIONSHIFT", Boolean.class, new Boolean(false), "Position Shift", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED}
    };

    // Cost Array - See Power.getCostArray()
    static private Object[][] costArray = {
        { "DistanceFromCollision", GEOMETRIC_COST, DYNAMIC_RECONFIG, ALL_RECONFIG, new Integer(2), new Integer(1), new Integer(0), new Integer(0) },
        { "IncreasedRange", LOGRITHMIC_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(5), new Integer(2), new Integer(1), new Integer(2) },
        { "FixedLocations", GEOMETRIC_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(1), new Integer(1), new Integer(0), new Integer(0) },
        { "FloatingLocations", GEOMETRIC_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(5), new Integer(1), new Integer(0), new Integer(0) },
        { "MassMultiplier", LOGRITHMIC_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(5), new Integer(2), new Integer(1), new Integer(2) },
        { "PositionShift", BOOLEAN_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(5)},

    };
     
    // Known Caveats Array
    private static String[] caveats = {
        
    };    
    
    // Power Definition Variables
    private static String powerName = "Teleportation"; // The Name of the Power
    private static String targetType = "MOVE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "HALFMOVE"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = -1; // The Point cost of a single Damage Class of this power.
    private static String attackType = null; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = null; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 1; // Indicates the END Multiplier for this power
    private static boolean dynamic = true;
    private static String description = "Teleport";
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "([0-9]*)\" (.*) \\(Long Range .*\\)", new Object[] { "DistanceFromCollision", Integer.class, "!Name", String.class }},
        { "([0-9]*)\" .*", new Object[] { "DistanceFromCollision", Integer.class }},
        { "Teleportation: ([0-9]*)\" .*", new Object[] { "DistanceFromCollision", Integer.class }},
        { "Noncombat Movement \\(([0-9]*)\\):.*", new Object[] { "NoncombatX", Integer.class}},
        { "Noncombat \\(Miles Per Phase\\).*", null},
        { "Long Range:.*", null },
        { "Long Range \\(miles\\):.*", null },
        { "Fixed Locations: ([0-9]*).*", new Object[] { "FixedLocations", Integer.class}},
        { "Fixed Locations \\(([0-9]*)\\).*", new Object[] { "FixedLocations", Integer.class}},
        { "Floating Locations: ([0-9]*).*", new Object[] { "FloatingLocations", Integer.class}},
        { "Floating Locations \\(([0-9]*)\\).*", new Object[] { "FloatingLocations", Integer.class}},
        { "Mass Multiplier: ([0-9]*),.*", new Object[] { "MassMultiplier", Integer.class}},
        { "Mass.*([0-9]*).*", new Object[] { "MassImport", Integer.class}},
        { "Increased Range: ([0-9]*),.*", new Object[] { "IncreasedRange", Integer.class}},
        { "Range Multiplier: .*", null },
        { "Location: .*", null },
        { "(Position Shift:).*", new Object[] { "PositionShift", Boolean.class }},
        //hd
        { "Teleportation ([0-9]*)\".*", new Object[] { "DistanceFromCollision", Integer.class }},
        { "(Position Shift).*", new Object[] { "PositionShift", Boolean.class }},
        { "Improved Noncombat Movement \\(x([0-9]*)\\).*", new Object[] { "NoncombatX", Integer.class}},
        { "x([0-9]*) Increased Mass", new Object[] { "MassMultiplier", Integer.class}},

    };
    
    /** Creates new powerHandToHandAttack */
    public powerTeleport()  {
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
        Integer distance = (Integer)parameterList.getParameterValue("DistanceFromCollision");
        Integer fixedlocation = (Integer)parameterList.getParameterValue("FixedLocations");
        Integer floatinglocation = (Integer)parameterList.getParameterValue("FloatingLocations");
        Integer noncombat2x = (Integer)parameterList.getParameterValue("NoncombatX");
        
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
        ability.add("Ability.MOVETYPE", "Teleportation", true);
        
        ability.setRequiresTarget(false);
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    
/*    /*//*) {
        ParameterList parameterList = getParameterList(ability);
        Integer distance = (Integer)parameterList.getParameterValue("DistanceFromCollision");
        String noncombat2x = (String)parameterList.getParameterValue("Noncombat2X");
        
        int cost = 20;
        
        if ( distance.intValue() > 10 ) cost += (distance.intValue() - 10) * 2;
        if ( noncombat2x.equals("TRUE") ) cost += 5;
        return cost;
    }
*/    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        Integer distance = (Integer)parameterList.getParameterValue("DistanceFromCollision");
        Integer noncombat2x = (Integer)parameterList.getParameterValue("NoncombatX");
        return distance.toString() + "\" Teleport";
    }

            /** Attempt to identify power
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     */
    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();
        
        if ( power != null && ( power.equals( "TELEPORT" ) || power.equals( "Teleport" ) || power.equals( "Teleportation" ) )){
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
}