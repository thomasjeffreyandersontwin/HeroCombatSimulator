/*
 * powerHandToHandAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
 */

package champions.powers;

import champions.Ability;
import champions.AbilityImport;
import champions.BattleEvent;
import champions.ChampionsMatcher;
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
 */ 
public class powerMissileDeflection extends Power implements ChampionsConstants {
    static final long serialVersionUID = 2367915068243652953L;
    
    static private String[] typeOptions = { "Thrown Objects", "Arrows, Slings, Etc.", "Bullets & Shrapnel", "Any Ranged Attack" };
    static private Integer[] typeCosts = new Integer[] { 5, 10, 15, 20 };
    
    static private String[] reflectOptions = { "No Reflection", "Reflect Attacks Back at Attacker", "Reflect Attacks at Any Target" };
    static private Integer[] reflectCosts = new Integer[] { 0, 20, 30 };
    
    static private Object[][] parameterArray = {
        {"Type","Power.TYPE", String.class, "Thrown Objects", "Type", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", typeOptions},
        {"Reflect","Power.REFLECTION", String.class, "No Reflection", "Reflection", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", reflectOptions},
    };
    
    // Cost Array - See Power.getCostArray()
    static private Object[][] costArray = {
        { "Type", COMBO_COST, STATIC_RECONFIG, ZERO_RECONFIG, typeCosts, typeOptions },
        { "Reflect", COMBO_COST, STATIC_RECONFIG, ZERO_RECONFIG, reflectCosts, reflectOptions },
    };
    
    // Power Definition Variables
    private static String powerName = "Missile Deflection/Reflection"; // The Name of the Power
    private static String targetType = "SELF"; // The Type of Attack: Either "SINGLE" or "SELF" or "QUICKSELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "ATTACK"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = -1; // The Point cost of a single Damage Class of this power.
    private static String attackType = null; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = null; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 0; // Indicates the END Multiplier for this power
    private static boolean dynamic = true;
    private static String description = "";
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
       
    };

        // Known Caveats Array
    private static String[] caveats = {
        "None known."
    };    
    
    /** Creates new powerHandToHandAttack */
    public powerMissileDeflection()  {
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
        // failure to configure
        
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
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    
    public void triggerPower(BattleEvent be, Ability ability, DetailList effectList, Target target, int refNumber, String targetGroup) throws BattleEventException{
        
        // Add Effects
        effectDeflection eb = new effectDeflection("Deflecting w/" + ability.getName(), ability);
        
        effectList.createIndexed( "Effect","EFFECT", eb);
    }
   
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList pl = getParameterList(ability);
        
        String type = pl.getParameterStringValue("Type");
        String reflect = pl.getParameterStringValue("Reflect");
        
        return "Missile Deflection (" + type + ", " + reflect + ")";
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
    
//    public boolean isEnabled(Ability ability, Target source) {
//	if ( source != null && source.hasEffect("Enraged/Berserk") ) 
//		return false;
//	else
//		return true;
//    }

    
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
    
    public Object[][] getCostArray(Ability ability) {
        return costArray;
    }
    
}