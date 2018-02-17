/*
 * powerGliding.java
 *
 * Created on September 24, 2000, 5:06 PM
 */

package champions.powers;

import champions.*;
import champions.interfaces.*;
import champions.exception.*;
import champions.event.*;
import champions.parameters.ParameterList;

public class powerGliding extends Power implements ChampionsConstants {
    static final long serialVersionUID = 2002080930347033561L;
    
    static private Object[][] parameterArray = {
        {"DistanceFromCollision","Ability.MOVEDISTANCE", Integer.class, new Integer(5), "Gliding DistanceFromCollision", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(1)},
        {"NoncombatX","Power.NONCOMBATX", Integer.class, new Integer(2), "Non-Combat Multiplier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(2)},
        {"PositionShift","Power.POSITIONSHIFT", Boolean.class, new Boolean(false), "Position Shift", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED}
    };
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
//        { ".*([0-9]*)\" .*", new Object[] { "DistanceFromCollision", Integer.class}},
        { "([0-9]*)\" .*", new Object[] { "DistanceFromCollision", Integer.class}},
        { ".*: ([0-9]*)\" .*", new Object[] { "DistanceFromCollision", Integer.class}},
        { "Non-Combat Multiplier: ([0-9]*),.*", new Object[] { "NoncombatX", Integer.class}},
        { "Non-Combat \\(MPH\\).*",null},
        //hd
        { ".* ([0-9]*)\", .*", new Object[] { "DistanceFromCollision", Integer.class}},
        { ".* ([0-9]*)\".*", new Object[] { "DistanceFromCollision", Integer.class}},
        { "x([0-9]*) Noncombat.*", new Object[] { "NoncombatX", Integer.class}},
    };
    
    // Cost Array - See Power.getCostArray()
    static private Object[][] costArray = {
//        { "Base", BASE_COST, STATIC_RECONFIG, ZERO_RECONFIG, new Integer(10) },
        { "DistanceFromCollision", GEOMETRIC_COST, DYNAMIC_RECONFIG, ALL_RECONFIG, new Integer(1), new Integer(1), new Integer(0), new Integer(0) },
        { "NoncombatX", LOGRITHMIC_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(5), new Integer(2), new Integer(2), new Integer(2) }
    };

     
    // Known Caveats Array
    private static String[] caveats = {
        
    };    
    
    // Custom Adders
    private static Object[] customAdders = {
        Limitation.class, "Ground Gliding"
    };
    
    public Object[] getCustomAddersArray() {
        return customAdders;
    }

     
    // Power Definition Variables
    private static String powerName = "Gliding"; // The Name of the Power
    private static String targetType = "MOVE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "HALFMOVE"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = -1; // The Point cost of a single Damage Class of this power.
    private static String attackType = null; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = null; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 0; // Indicates the END Multiplier for this power
    private static boolean dynamic = true;
    private static String description = "Gliding";
    
    /** Creates new powerGliding */
    public powerGliding()  {
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
        Integer distance = null;
        if(parameterList.contains("DistanceFromCollision"))
		{
        	 distance = (Integer)parameterList.getParameterValue("DistanceFromCollision");
             
		}
        else {
        	distance = (Integer)parameterList.getParameterValue("Distance");
        }
        Integer NoncombatX = (Integer)parameterList.getParameterValue("NoncombatX");
        
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
        ability.add("Ability.MOVETYPE", "Gliding", true);
        
        ability.setRequiresTarget(false);
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        Integer distance = null;
        if(parameterList.contains("DistanceFromCollision"))
		{
        	 distance = (Integer)parameterList.getParameterValue("DistanceFromCollision");
             
		}
        else {
        	distance = (Integer)parameterList.getParameterValue("Distance");
        }
        Integer NoncombatX = (Integer)parameterList.getParameterValue("NoncombatX");
        return distance.toString() + "\" Gliding (NC: " + Integer.toString( distance.intValue() * NoncombatX.intValue() ) + "\")";
    }
    
    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();
        
        if ( power != null && ( power.equals( "GLIDING" ) || power.equals( "Gliding" ) )){
            return 10;
        }
        return 0;
    }
     
    public Object[][] getImportPatterns() {
        return patterns;
    }
    
    
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
    

    public String[] getCaveatArray() {
        return caveats;
    }
}