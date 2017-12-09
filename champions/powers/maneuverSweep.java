/*
 * powerHandToHandAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
 */

package champions.powers;


import champions.Ability;
import champions.Battle;
import champions.BattleEvent;
import champions.Power;
import champions.SweepBattleEvent;
import champions.Target;
import champions.filters.MeleeAbilityFilter;
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
public class maneuverSweep extends Power implements ChampionsConstants {
    static final long serialVersionUID =5295846683348707403L;
    
    static private Object[][] parameterArray = {
      //  {"DamageDie","Power.DAMAGEDIE", String.class, "0d6", "Extra Damage Dice", DICE_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
      //  {"OCVModifier","Ability.OCVBONUS", Integer.class, new Integer(0), "Maneuver OCV Modifier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
      //  {"DCVModifier","Ability.DCVBONUS", Integer.class, new Integer(0), "Maneuver DCV Modifier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
    //    {"MartialManeuver","Ability.ISMARTIALMANEUVER", String.class, "FALSE", "Martial Arts Skill", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
    };
    
    // Power Definition Variables
    public  static String powerName = "Sweep"; // The Name of the Power
    private static String targetType = "SINGLE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "ATTACK"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = 0; // The Point cost of a single Damage Class of this power.
    private static String attackType = "MELEE"; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = "SPECIAL"; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 0; // Indicates the END Multiplier for this power
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
     //   { ".*\\(OCV ([-+][0-9]*), DCV ([-+][0-9]*),.*\\)", new Object[] {"OCVModifier", Integer.class, "DCVModifier", Integer.class}}
    };
    
    /** Creates new powerHandToHandAttack */
    public maneuverSweep()  {
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
        
        // No Validation necessary
        
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
     //   ability.addDiceInfo( "DamageDie", "", "Attack Damage");
        
        // Add A Damage Class Info
        //ability.add("Maneuver.DC",  dc, true);
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        ability.setIs( "MELEEMANEUVER",true);
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        
        return "Sweep";
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
    
    /** Creates an Activate BattleEvent for the indicated Maneuver. 
     *
     * This method is run when the user clicks on the ability to activate the ability
     * with a maneuver.  It should create the default action maneuver.  
     * For most powers, this will be an activate battle event.
     *
     * If the user wishes to use a maneuver with this ability, the maneuver's 
     * getActivateManeuverBattleEvent will first be called.  If it returns null, this 
     * method will be called.  If it returns non-null, this method will be skipped
     * and it will be assumed that the manuever is taking care of the 
     * activation.  
     *
     * Generally maneuvers will allow the power to build the activation event,
     * but either may be required to perform the action.
     *
     * This method will not be called if the ability is already activated.
     */
    @Override
    public BattleEvent getActivateManeuverBattleEvent(Ability ability, Ability maneuver, Target source) {
        // This one requires special handling of a sweep maneuver or ability...
        // In the activeManueverBattleEvent, the maneuver will be the sweep ability...
        if ( source == null && Battle.currentBattle != null ) source = Battle.currentBattle.getActiveTarget();
        SweepBattleEvent sbe = new SweepBattleEvent(source, SweepBattleEvent.SweepType.SWEEP, maneuver);
        sbe.setAbilityFilter( new SweepAbilityFilter(source) );
        sbe.addLinkedAbility(ability, false);
        
        return sbe;
    }
    
    /** Creates an Activate BattleEvent for the indicated Ability. 
     *
     * This method is run when the user clicks on the ability to activate the ability
     * with a maneuver.  It should create the default action maneuver.  
     * For most powers, this will be an activate battle event.
     *
     * If the user wishes to use a maneuver with this ability, the maneuver's 
     * getActivateManeuverBattleEvent will first be called.  If it returns null, this 
     * method will be called.  If it returns non-null, this method will be skipped
     * and it will be assumed that the manuever is taking care of the 
     * activation.  
     *
     * Generally maneuvers will allow the power to build the activation event,
     * but either may be required to perform the action.
     *
     * This method will not be called if the ability is already activated.
     */
    @Override
    public BattleEvent getActivateAbilityBattleEvent(Ability ability, Ability maneuver, Target source) {
        // This one requires special handling of a sweep maneuver or ability...
        // Sweep will never be launched as an ability with a maneuver along with it,
        // so maneuver will always be null.
        if ( source == null && Battle.currentBattle != null ) source = Battle.currentBattle.getActiveTarget();
        SweepBattleEvent sbe = new SweepBattleEvent(source, SweepBattleEvent.SweepType.SWEEP, ability);
        sbe.setAbilityFilter( new SweepAbilityFilter(source) );
        
        return sbe;
    }
    
    public class SweepAbilityFilter extends MeleeAbilityFilter {
        Target source;
        
        public SweepAbilityFilter(Target source ) {
            this.source = source;
        }
        
        public boolean includeElement(Ability ability) {
            if ( ability == null ) return false;
            
            if ( ability.getPower() instanceof maneuverSweep) return false;
            if ( ability.getPower() instanceof maneuverHaymaker ) return false;
            
            if ( isAutofire(ability) && hasSkillRapidAutofire() == false ) return false;
            
            return super.includeElement(ability);
        }
        
        protected boolean hasSkillRapidAutofire() {
            return false;
        }
        
        protected boolean isAutofire(Ability ability) {
            return ability.hasAdvantage( advantageAutofire.advantageName );
        }
    }
}