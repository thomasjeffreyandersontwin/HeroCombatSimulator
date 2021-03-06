/*
 * powerHandToHandAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
 */

package champions.powers;

import champions.Ability;
import champions.AbilityImport;
import champions.BattleEvent;
import champions.DetailList;
import champions.Power;
import champions.Target;
import champions.exception.BattleEventException;
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
 *
 * The Following Steps must be performed to upgrade Power to Reconfigurable Format:
 * 1) Create costArray.
 * 2) Add the getCostArray() method, returning costArray.
 * 3) Remove existing calculateCPCost.
 */
public class powerDamageResistance extends Power 
implements ChampionsConstants {
    static final long serialVersionUID = 4789274063056667596L;

    static private Object[][] parameterArray = {
    {"DamageResistancePD","Power.DRPD", Integer.class, new Integer(0), "Resistant PD", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
    {"DamageResistanceED","Power.DRED", Integer.class, new Integer(0), "Resistant ED", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED}
    };
    
    // Cost Array - See Power.getCostArray()
    static private Object[][] costArray = {
        { "DamageResistancePD", GEOMETRIC_COST, DYNAMIC_RECONFIG, PROPORTIONAL_RECONFIG, new Integer(1), new Integer(2), new Integer(0), new Integer(0) },
        { "DamageResistanceED", GEOMETRIC_COST, DYNAMIC_RECONFIG, PROPORTIONAL_RECONFIG, new Integer(1), new Integer(2), new Integer(0), new Integer(0) }
    };
    
    // Power Definition Variables
    private static String powerName = "Damage Resistance"; // The Name of the Power
    private static String targetType = "SELF"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "PERSISTENT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "INSTANT"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = -1; // The Point cost of a single Damage Class of this power.
    private static String attackType = null; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = null; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 0; // Indicates the END Multiplier for this power
    private static boolean dynamic = true;
    private static String description = "Dude, its Damange Resistance, ok?";

    // Import Patterns Definitions
    private static Object[][] patterns = {
        //MC 4e and Hero Designer
        { ".*\\(([0-9]*) PD/([0-9]*) ED\\).*", new Object[] { "DamageResistancePD", Integer.class, "DamageResistanceED", Integer.class }},
        //MC 5e
        { "Damage Resistance.* \\(([0-9]*) PD, ([0-9]*) ED\\).*", new Object[] { "DamageResistancePD", Integer.class, "DamageResistanceED", Integer.class }},
        //Hero Designer 3/28/04 -kjr
        //Damage Resistance (2 PD/2 ED/2 Mental Def./2 Flash Def./2 Power Def.)
        //Damage Resistance (1 ED)
        //Damage Resistance (1 PD)
        { ".*\\(([0-9]*) PD/([0-9]*) ED.*\\).*", new Object[] { "DamageResistancePD", Integer.class, "DamageResistanceED", Integer.class }},
        { ".*\\(([0-9]*) PD\\).*", new Object[] { "DamageResistancePD", Integer.class }},
        { ".*\\(([0-9]*) ED\\).*", new Object[] { "DamageResistanceED", Integer.class }},
        { "LEVELS:.*", null },
    };

    
    // Known Caveats Array
    private static String[] caveats = {
        "None known."
    };
    
    /** Creates new powerHandToHandAttack */
    public powerDamageResistance()  {
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
        Integer pddefense = (Integer)parameterList.getParameterValue("DamageResistancePD");
        Integer eddefense = (Integer)parameterList.getParameterValue("DamageResistanceED");
        
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
        ability.add("Ability.rPDBONUS", pddefense,true);
        ability.add("Ability.rEDBONUS", eddefense,true);
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
     }

    public void triggerPower(BattleEvent be, Ability ability,DetailList effectList, Target target, int refNumber, String targetGroup) throws BattleEventException{
        String name;
        name = ability.getName();

        effectDefenseModifier2 effect = new effectDefenseModifier2(name, ability);

        effectList.createIndexed("Effect","EFFECT", effect)  ;
    }

    /*public int calculateCPCost(Ability ability) {
        ParameterList parameterList = getParameterList(ability);
        Integer pddefense = (Integer)parameterList.getParameterValue("DamageResistancePD");
        Integer eddefense = (Integer)parameterList.getParameterValue("DamageResistanceED");

        int calculatedCost = ( pddefense.intValue() + eddefense.intValue() ) / 2;
        
        return calculatedCost < 5 ? 5 : calculatedCost;
    }*/

    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        Integer pddefense = (Integer)parameterList.getParameterValue("DamageResistancePD");
        Integer eddefense = (Integer)parameterList.getParameterValue("DamageResistanceED");

        return pddefense.toString() + "/" + eddefense.toString() + " Damage Resistance";
    }

            /** Attempt to identify power
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     */
    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();
        
        if ( power != null && ( power.equals( "DAMAGERESISTANCE" ) || power.equals( "Damage Resistance" ) )){
            return 10;
        }
        return 0;
    }

    public boolean checkParameter(Ability ability, int not_used, String key, Object value, Object oldValue) {
        ParameterList parameterList = getParameterList(ability);
        Integer pddefense = (Integer)parameterList.getParameterValue("DamageResistancePD",key,value);
        Integer eddefense = (Integer)parameterList.getParameterValue("DamageResistanceED",key,value);

        if ( pddefense.intValue() < 0 || eddefense.intValue() <  0) {
            return false;
        }
        return true;
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