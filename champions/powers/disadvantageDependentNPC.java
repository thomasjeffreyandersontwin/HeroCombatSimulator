/*
 * powerHandToHandAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
 */

package champions.powers;

import champions.Ability;
import champions.AbilityImport;
import champions.BattleEvent;
import champions.Disadvantage;
import champions.parameters.ParameterList;
import champions.ProfileTemplate;
import champions.Target;
import champions.attackTree.AttackTreeNode;
import champions.attackTree.SkillRollNode;
import champions.interfaces.ChampionsConstants;


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
public class disadvantageDependentNPC extends Disadvantage implements ChampionsConstants {
    static final long serialVersionUID =5295848683348706403L;
    
    private static final String[] levels =  {"8", "11", "14"};
    
    static private Integer[] levelsCostArray = new Integer[] { new Integer(5), new Integer(10), new Integer(15) };
    
    static public String[] DNPCIsOptions = {"Incompetent","Normal","Slightly Less Powerful then the PC","As Powerful as the PC"};
    
    static private Integer[] DNPCIsCostArray = new Integer[] { new Integer(10), new Integer(5), new Integer(0), new Integer(-5) };
    
    static private Object[][] parameterArray = {
        {"DNPCIs","Disadvantage.DNPCIS", String.class, "Incompetent", "DNCP Is", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", DNPCIsOptions},
        {"ActivationRoll","Disadvantage.LEVEL", String.class, "11", "Activation Roll", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", levels},
        {"DNCPMultiplier","Disadvantage.DNCPMULTIPLIER", Integer.class, new Integer(0), "Group DNCP Multiplier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(2)},
        {"UsefulNoncombatPositionorSkills","Disadvantage.USEFULNONCOMBATPOSITIONORSKILLS", Boolean.class, new Boolean(false), "Useful Noncombat Position or Skills", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"UnawareofCharactersAdventuringCareerSecretID","Disadvantage.UNAWAREOFCHARACTERSADVENTURINGCAREERSECRETID", Boolean.class, new Boolean(false), "Unaware of Character's Adventuring Career/Secret ID", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
    };
    
    // Cost Array - See Power.getCostArray()
    static private Object[][] costArray = {
        { "DNPCIs", COMBO_COST, STATIC_RECONFIG, ZERO_RECONFIG, DNPCIsCostArray, DNPCIsOptions },
        { "ActivationRoll", COMBO_COST, STATIC_RECONFIG, ZERO_RECONFIG, levelsCostArray, levels },
        { "DNCPMultiplier", GEOMETRIC_COST, DYNAMIC_RECONFIG, PROPORTIONAL_RECONFIG, new Integer(5), new Integer(1), new Integer(0), new Integer(0) },
        { "UsefulNoncombatPositionorSkills", BOOLEAN_COST, STATIC_RECONFIG, ZERO_RECONFIG, new Integer(-5)},
        { "UnawareofCharactersAdventuringCareerSecretID", BOOLEAN_COST, STATIC_RECONFIG, ZERO_RECONFIG, new Integer(5)},
    };
    // Power Definition Variables
    private static String powerName = "Dependent NPC"; // The Name of the Power
    private static String targetType = "SELF"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "PERSISTENT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "INSTANT"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = -1; // The Point cost of a single Damage Class of this power.
    private static String attackType = null; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = null; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 0; // Indicates the END Multiplier for this power
    
    private static Object[][] patterns = {
        { "APPEARANCE: ([0-9]*)- .*", new Object[] {"ActivationRoll", String.class}},
        { "USEFULNESS: (.*)", new Object[] {"DNPCIs", String.class}},
        { "(UNAWARE).*", new Object[] {"UsefulNoncombatPositionorSkills", Boolean.class}},
        { "(USEFUL).*", new Object[] {"UnawareofCharactersAdventuringCareerSecretID", Boolean.class}},
        { "LEVELS: ([0-9])", new Object[] {"DNCPMultiplier", Integer.class}},
        { "GROUP.*", null }
    };
    
    /** Creates new powerHandToHandAttack */
    public disadvantageDependentNPC()  {
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
        //ability.add("Ability.NORMALLYON", "FALSE", true);
        ability.setNormallyOn(false);
        ability.setDisallowForcedActivation(true);
        
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    
    public String getConfigSummary(Ability ability, int not_used) {
        return "Dependent NPC";
    }
    
    /** Attempt to identify power
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     */
    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();
        
        if ( power != null && power.equals( "DEPENDENTNPC" )){
            return 10;
        }
        return 0;
    }
    
    //
    
    /** Returns the roll necessary to make a skill roll, based upon the ability and target.
     *
     * Calculates the Skill roll based upon the ability/target and the parameters.
     *
     * @param ability Ability to base roll upon.
     * @param target Target to base roll upon.
     * @return Roll necessary to succeed at skill.
     */
    public int getSkillRoll(Ability ability, Target target) {
        String level = ability.getStringValue("Disadvantage.LEVEL");
        return levelToRoll(level);
    }
    
    
    public AttackTreeNode preactivate(BattleEvent be) {
        Ability ability = be.getAbility();
        
        //setMode(SKILL_TARGET);
        if (!ability.getStringValue("Disadvantage.LEVEL").equals("Always")) {
            SkillRollNode node = new SkillRollNode("Skill Roll");
            node.setTargetGroupSuffix("SKILLROLL");
            
            node.setAbility(ability);
            //node.setLimitationIndex(index);
            
            return node;
        }
        return null;
    }
    
    //    public void triggerPower(BattleEvent be, Ability ability, DetailList effectList, Target target, int referenceNumber, String targetGroup) throws BattleEventException {
    //        Effect effect =  new effectStunned();
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
    public static int levelToRoll(String level) {
        if (level.equals("8")) {
            return 8;
        }
        else if (level.equals("11")) {
            return 11;
        }
        else if (level.equals("14")) {
            return 14;
        }
        return -1;
    }
}