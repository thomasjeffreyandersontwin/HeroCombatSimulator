/*
 * powerHandToHandAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
 */

package champions.powers;

import champions.Ability;
import champions.Perk;
import champions.interfaces.Advantage;
import champions.interfaces.ChampionsConstants;
import champions.parameters.ParameterList;

import champions.Ability;
import champions.AbilityImport;
import champions.BattleEvent;
import champions.ChampionsUtilities;
import champions.CharacterImport;
import champions.ProfileTemplate;
import champions.Skill;
import champions.Target;
import champions.attackTree.AttackTreeNode;
import champions.attackTree.SkillRollNode;
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
public class PerkContact extends Perk
        implements ChampionsConstants {
    static final long serialVersionUID = 7766760921220027676L;
    
    // Custom Adders
    private static Object[] customAdders = {
        Advantage.class, advantageOrganizationContact.advantageName,
        Advantage.class, advantageSpiritContact.advantageName,
    };
    
    
    static private String[] resourcesArray = {
        "Typical Skills or Resources","Useful Skills or Resources", "Very Useful Skills or Resources","Extremely Useful Skills or Resources"
    };
    
    static private String[] relationshipArray = {
        "Indifferent","Good", "Very Good"
    };
    
    
    static private Integer[] resourcesCostArray = new Integer[] { new Integer(0), new Integer(1), new Integer(2), new Integer(3) };
    static private Integer[] relationshipCostArray = new Integer[] { new Integer(0), new Integer(1), new Integer(2) };
    
    static private Object[][] parameterArray = {
        {"Cost","Power.COST", Integer.class, new Integer(1),  "Points For Roll", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"Roll","Power.ROLL", Integer.class, new Integer(8), "Roll", INTEGER_PARAMETER, VISIBLE, DISABLED, NOTREQUIRED},
        {"LimitedbyID","Power.LIMITEDBYID", Boolean.class, new Boolean(false), "Limited By Identity", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"Resources","Power.RESOURCES", String.class, "Typical Skills or Resources", "Contact Has", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", resourcesArray},
        {"AccessToInstitutions","Power.ACCESSTOINSTITUTIONS", Boolean.class, new Boolean(false), "Contact Has Access To Major Institutions", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"ContactHasContacts","Power.CONTACTHASCONTACTS", Boolean.class, new Boolean(false), "Contact Has Significant Contacts Of His Own", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"Relationship","Power.RELATIONSHIP", String.class, "Indifferent", "Relationship with Contact", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", relationshipArray},
        {"SlavishlyLoyal","Power.SLAVISHLYLOYAL", Boolean.class, new Boolean(false), "Contact Is Slavishly Loyal To Character", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"Blackmailed","Power.BLACKMAILED", Boolean.class, new Boolean(false), "Contact Has Been Blackmailed By The Character", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        
//{"Levels","Power.LEVELS", Integer.class, new Integer(1), "Levels", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        
        
    };
    
    // Power Definition Variables
    private static String powerName = "Access"; // The Name of the Power
    private static String targetType = "SELF"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "INSTANT"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = -1; // The Point cost of a single Damage Class of this power.
    private static String attackType = null; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = null; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 0; // Indicates the END Multiplier for this power
    
    
    // Cost Array - See PowerAdapter.getCostArray()
    static private Object[][] costArray = {
        { "Cost", GEOMETRIC_COST, DYNAMIC_RECONFIG, PROPORTIONAL_RECONFIG, new Integer(1), new Integer(1), new Integer(0), new Integer(0) },
        { "LimitedbyID", BOOLEAN_COST, STATIC_RECONFIG, ZERO_RECONFIG, new Integer(-1), new Integer(0), new Integer(0) },
        { "Resources", COMBO_COST, STATIC_RECONFIG, ZERO_RECONFIG, resourcesCostArray, resourcesArray },           { "AccessToInstitutions", BOOLEAN_COST, STATIC_RECONFIG, ZERO_RECONFIG, new Integer(1), new Integer(0), new Integer(0) },
        { "ContactHasContacts", BOOLEAN_COST, STATIC_RECONFIG, ZERO_RECONFIG, new Integer(1), new Integer(0), new Integer(0) },
        { "Relationship", COMBO_COST, STATIC_RECONFIG, ZERO_RECONFIG, relationshipCostArray, relationshipArray },        { "SlavishlyLoyal", BOOLEAN_COST, STATIC_RECONFIG, ZERO_RECONFIG, new Integer(3), new Integer(0), new Integer(0) },
        { "Blackmailed", BOOLEAN_COST, STATIC_RECONFIG, ZERO_RECONFIG, new Integer(-2), new Integer(0), new Integer(0) },
        
    };
    
    
    /** Creates new powerHandToHandAttack */
    public PerkContact()  {
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
        
        Integer roll = (Integer) parameterList.getParameterValue("Roll");
        Integer cost = (Integer) parameterList.getParameterValue("Cost");
        
        if (cost == 1) {
            roll = 8;
        } else if (cost == 2) {
            roll = 11;
        } else {
            roll = cost + 9;
        }
        
        parameterList.setParameterValue("Roll", roll);
        parameterList.copyValues(ability);
        
        
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
        //ability.addDiceInfo( "DamageDie", die, "Energy Blast Damage");
        
        // Add A Damage Class Info
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        ability.setCType("PERK");

        
        // Return true to indicate success
        return true;
    }
    
//    public int calculateCPCost(Ability ability) {
//        ParameterList parameterList = getParameterList(ability);
//        Integer levels = (Integer)parameterList.getParameterValue("Levels");
//        String options = (String)parameterList.getParameterValue("Options");
//        int cost = 0;
//        int multiplier = 10;
//        if ("15 Points/Level".equals(options)) {
//            multiplier = 15;
//        }
//        cost = levels.intValue() * multiplier;
//        return cost;
//    }
    
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
    
    /** Returns an Array of Objects, representing custom/special advantages, limitations, special parameters usable with the power.
     * The Array should be in the format of class type (limitation, advantage, special) followed by the class name, repeated for
     * each additional custom added.  For example:
     *  array[] = { Limitation.class, "limitationLimitedSpecialFX", Advantage.class, "advantageVariableEffect" };
     */
    public Object[] getCustomAddersArray() {
        return customAdders;
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
        Integer roll = (Integer)parameterList.getParameterValue("Roll");
        if ( target == null ) return Integer.MIN_VALUE;
        return roll.intValue();
    }
    
    public AttackTreeNode getTriggerPowerNode(BattleEvent be, Target target, String TargetGroup, int refNumber) {
        Ability ability = be.getAbility();
        ParameterList parameterList = getParameterList(ability);
        SkillRollNode node = new SkillRollNode("Contact Roll");
        node.setAbility(ability);
        
        return node;
    }
    
    
    /** Initializes the power when it is first loaded by the Simulator.
     *
     * The <code>initialize</code> method is gauranteed to be called at least once prior to
     * any use of the power.  However, it is possible that the method would be called
     * multiple times.  If the initialization code can only be run one, the power should
     * track whether it has been initialized already.
     */
    public void initialize() {
        ProfileTemplate pt = ProfileTemplate.getDefaultProfileTemplate();
        pt.addOption( "Contact Roll", "SHOW_SKILL_PANEL",
                "This Description is cool.",
                "AttackTree.toHitIcon", null);
    }
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        
        return "Contact";
    }
    
}