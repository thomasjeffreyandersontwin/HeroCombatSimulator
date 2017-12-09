/*
 * maneuverEscapeFromGrab.java
 *
 * Created on April 22, 2001, 7:36 PM
 */

package champions.powers;

import champions.*;
import champions.attackTree.AttackTreeNode;
import champions.attackTree.SkillRollNode;
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
 */
public class skillStealth extends Skill implements ChampionsConstants {
    static final long serialVersionUID =5295828683348707403L;
    
    static public String[] statOptions = {"STR","CON","DEX","BODY","INT","EGO","PRE","COM","PD",
    "ED","SPD","REC","END","STUN"};
    
    static private Object[][] parameterArray = {
        {"BaseStat","Power.STAT", String.class, "DEX", "Base Stat", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", statOptions},
        {"Levels","Power.LEVEL", Integer.class, new Integer(0), "Extra Level", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"Crammed","Power.CRAMMED", Boolean.class, new Boolean(false), "Crammed Skill", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"RollImport","Power.ROLLIMPORT", Integer.class, new Integer(0), "Roll Import", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
    };
    
    // Known Caveats Array
    private static String[] caveats = {
        
    };
    
    // Power Definition Variables
    private static String powerName = "Stealth"; // The Name of the Power
    private static String targetType = "SELF"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "INSTANT"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = 5; // The Point cost of a single Damage Class of this power.
    private static String attackType = "NULL"; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = "NORMAL"; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 0; // Indicates the END Multiplier for this power
    private static boolean dynamic = true;
    private static String description = "";
    
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "ROLL: ([0-9]*)-", new Object[] {"RollImport", Integer.class}},
        { "ROLL: ([0-9]*)/", new Object[] {"RollImport", Integer.class}},
        { ".*", null},
        
        
    };
    
    // Cost Array - See PowerAdapter.getCostArray()
    static private Object[][] costArray = {
        { "Base", BASE_COST, STATIC_RECONFIG, ZERO_RECONFIG, new Integer(3) },
        { "Levels", GEOMETRIC_COST, DYNAMIC_RECONFIG, PROPORTIONAL_RECONFIG, new Integer(2), new Integer(1), new Integer(0), new Integer(0) },
    };
    
    /** Creates new powerHandToHandAttack */
    public skillStealth()  {
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
        //ability.setAutosource(true);
        //ability.setTargetSelf(true);
        //ability.setAutoHit(true);
        ability.setCType("SKILL");
        
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        String stat = (String)parameterList.getParameterValue("BaseStat");
        Integer levels = (Integer)parameterList.getParameterValue("Levels");
        
        String s = stat + " based skill with " + levels.toString() + " levels. (SR: " + stat + "/5+" + Integer.toString(levels.intValue() + 9) + ")";
        
        return s;
    }
    
    /** Returns the roll necessary to make a skill roll, based upon the ability and target.
     *
     * Calculates the Skill roll based upon the ability/target and the parameters.
     *
     * @param ability Ability to base roll upon.
     * @param target Target to base roll upon.
     * @return Roll necessary to succeed at skill.
     */
    public int getSkillRoll(Ability ability, Target target) {
        ParameterList parameterList = getParameterList(ability);
        String stat = (String)parameterList.getParameterValue("BaseStat");
        Integer levels = (Integer)parameterList.getParameterValue("Levels");
        boolean crammed = (Boolean)parameterList.getParameterValue("Crammed");
        
        if ( target == null ) return Integer.MIN_VALUE;
        
        double statValue = target.getCurrentStat(stat);
        
        if ( crammed ) {
            return 8;
        }
        else {
            return ChampionsUtilities.roundValue(statValue/5 + 9 + levels.intValue(), true);
        }
    }
    
/*        public AttackTreeNode getTriggerPowerNode(BattleEvent be, Target target, String TargetGroup, int refNumber) {
            Ability ability = be.getAbility();
            ParameterList parameterList = getParameterList(ability);
            SkillRollNode node = new SkillRollNode("Skill Roll");
            node.setAbility(ability);
 
            return node;
        }
 */
    public AttackTreeNode preactivate(BattleEvent be) {
        //        ParameterList parameterList = getParameterList(ability,index);
        
        //setMode(SKILL_TARGET);
        SkillRollNode node = new SkillRollNode("Skill Roll");
        Ability ability = be.getAbility();
        node.setTargetGroupSuffix("SKILLROLL");
        
        node.setAbility(ability);
        //node.setLimitationIndex(index);
        
        return node;
    }
    
//    public void triggerPower(BattleEvent be, Ability ability, DetailList effectList, Target target, int referenceNumber, String targetGroup) throws BattleEventException {
//        
//        Integer difference = be.getActivationInfo().getIntegerValue( "Attack.DIFFERENCE" );
//        Integer bonus = new Integer(difference.intValue() /3);
//        if (bonus.intValue() > 3) bonus = new Integer(3);
//
//        Effect effect =  new effectCombatModifier(ability.getName() +" modifier",bonus.intValue(),0);
//        effectList.createIndexed("Effect","EFFECT", effect );
//    }
    
    /** Initializes the power when it is first loaded by the Simulator.
     *
     * The <code>initialize</code> method is gauranteed to be called at least once prior to
     * any use of the power.  However, it is possible that the method would be called
     * multiple times.  If the initialization code can only be run one, the power should
     * track whether it has been initialized already.
     */
    public void initialize() {
        ProfileTemplate pt = ProfileTemplate.getDefaultProfileTemplate();
        pt.addOption( "Skill Roll", "SHOW_SKILL_PANEL",
        "This Description is cool.",
        "AttackTree.toHitIcon", null);
    }

    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();
        
        if ( power != null && power.equals( "STEALTH" )){
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
    
    public void importPower(Ability ability, AbilityImport ai) {
        // First Do the standard import.  This will parse the RegExp andpopulate
        super.importPower(ability,ai);
        
        ParameterList parameterList = getParameterList(ability);
        Integer rollimport = (Integer)parameterList.getParameterValue("RollImport");
        String powername = (String)ai.getValue("AbilityImport.POWERNAME");
        powername = (String)ai.getValue("AbilityImport.POWERNAME");
        int index = -1;
        
        
        parameterList.setParameterValue("BaseStat", "DEX" );
        CharacterImport ci = (CharacterImport)ai.getValue("AbilityImport.CHARACTERIMPORT");
        
        // Find the location of the DEX information in the import
        index = ci.findIndexed("Stat","NAME","DEX");
        if ( index != -1 ) {
            // Grab the EGO value and calculate the base MDF
            Integer currentstat = ci.getIndexedIntegerValue(index,"Stat","VALUE");
            Integer level = new Integer(rollimport.intValue() - ChampionsUtilities.roundValue(currentstat.doubleValue()/5.0, false) - 9);
            
            // Save the adjusted value back out
            parameterList.setParameterValue("Levels", level);
            
        }
        
        
    }

    
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
    
    /** Returns a String[] of Caveats about the Power
     * PowerAdapter uses this method to automatically build the getCaveats()
     * String.  The Strings returns by getCaveatArray() will be assembled into
     * list form and returned via getCaveats().
     *
     * Return an empty array if there are no known caveats for this power.
     */
    public String[] getCaveatArray() {
        return caveats;
    }
}