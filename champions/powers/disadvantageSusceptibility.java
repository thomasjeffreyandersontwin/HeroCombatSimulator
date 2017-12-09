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
import champions.Disadvantage;
import champions.Effect;
import champions.ProfileTemplate;
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
 */
public class disadvantageSusceptibility extends Disadvantage implements ChampionsConstants {
    static final long serialVersionUID =5295848683348706403L;
    
    static public String[] conditionOptions = {"Uncommon","Common","Very Common"};
    
    static private Integer[] conditionCostArray = new Integer[] { new Integer(0), new Integer(5),new Integer(10)};
    
    static private String[] timeOptions = { "Effect Is Instant","1 Segment","1 Phase", "1 Turn", "1 Minute", "5 Minutes", "20 Minutes","1 Hour", "6 Hours", "1 Day", "1 Week", "1 Month", "1 Season"};
    
    static private Integer[] timeCostArray = new Integer[] { new Integer(0), new Integer(15), new Integer(10), new Integer(5), new Integer(0), new Integer(-5), new Integer(-10), new Integer(-15), new Integer(-20), new Integer(-25), new Integer(-30), new Integer(-30), new Integer(-35), new Integer(-40), new Integer(-45), new Integer(-50), new Integer(-55) };
    
    static public String[] damageOptions = {"BODY","STUN"};

    static private String[] conditionPresentArray = {
        "TRUE",
        "FALSE"
    };
    
    static private Object[][] parameterArray = {
        {"Condition","Disadvantage.SUBSTANCE", String.class, "Very Common", "Condition Is", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", conditionOptions},
        {"Time","Disadvantage.TIME", String.class, "1 Segment", "Time Before Effect", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", timeOptions},
        {"DamageDie","Disadvantage.DAMAGEDIE", String.class, "1d6", "Susceptibility Dice", DICE_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"DamageStat","Disadvantage.DAMAGESTAT", String.class, "STUN", "Damage Stat", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", damageOptions},
        {"ConditionPresent","Disadvantage.CONDITIONPRESENT", String.class, "FALSE", "Condition Present", COMBO_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED, "OPTIONS", conditionPresentArray },
    };
    
    // Cost Array - See Power.getCostArray()
    static private Object[][] costArray = {
        { "Condition", COMBO_COST, STATIC_RECONFIG, ZERO_RECONFIG, conditionCostArray, conditionOptions },
        { "Time", COMBO_COST, STATIC_RECONFIG, ZERO_RECONFIG, timeCostArray, timeOptions },
        { "DamageDie", NORMAL_DICE_COST, DYNAMIC_RECONFIG, ALL_RECONFIG, new Integer(5), new Integer(0), new Integer(0) }
    };
    
    // Power Definition Variables
    private static String powerName = "Susceptibility"; // The Name of the Power
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
        { "DICE: ([0-9]*)d6 damage", new Object[] {"DamageDie", String.class}},
        { "DAMAGE: (.*)", new Object[] {"Time", String.class}},
        { "CONDITION: (.*)", new Object[] {"Condition", String.class}},
        
    };
    
    /** Creates new powerHandToHandAttack */
    public disadvantageSusceptibility()  {
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
        String time = (String)parameterList.getParameterValue("Time");
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure.
        
        // No Validation Necessary
        
        //for HD import
        if (time.equals("Instant")) {
            parameterList.setParameterValue("Time", "Effect Is Instant");
        }
        else if (time.equals("per Segment")) {
            parameterList.setParameterValue("Time", "1 Segment");
        }        
        else if (time.equals("per Phase")) {
            parameterList.setParameterValue("Time", "1 Phase");
        }        
        else if (time.equals("per Turn")) {
            parameterList.setParameterValue("Time", "1 Turn");
        }        
        else if (time.equals("per Minute")) {
            parameterList.setParameterValue("Time", "1 Minute");
        }        
        else if (time.equals("per 5 Minutes")) {
            parameterList.setParameterValue("Time", "5 Minutes");
        }        
        else if (time.equals("per 20 Minutes")) {
            parameterList.setParameterValue("Time", "20 Minutes");
        }        
        else if (time.equals("per Hour")) {
            parameterList.setParameterValue("Time", "1 Hour");
        }        
        else if (time.equals("per 6 Hours")) {
            parameterList.setParameterValue("Time", "6 Hours");
        }        
        else if (time.equals("per Day")) {
            parameterList.setParameterValue("Time", "1 Day");
        }        
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
        return "Susceptibility";
    }
    
    public void triggerPower(BattleEvent be, Ability ability,DetailList effectList, Target target, int refNumber, String targetGroup) throws BattleEventException {
        Effect effect = new effectSusceptibility( ability );
        effectList.createIndexed("Effect","EFFECT", effect)  ;
    }
    
    //    public void addActions(Vector v, final Ability ability) {
    //        Action assignAction = new AbstractAction( "Adjust " + ability.getName() + " State") {
    //
    //            public void actionPerformed(ActionEvent e) {
    //                final Ability currentAbility = ability.getCurrentInstance();
    //
    //                String conditionpresent = currentAbility.getStringValue("Disadvantage.CONDITIONPRESENT");
    //
    //                ParameterList parameterList = getParameterList(ability);
    //                final Integer level = (Integer)parameterList.getParameterValue("Level");
    //
    //                ParameterList pl = new ParameterList();
    //                pl.addBooleanParameter( "ConditionPresent",  "Disadvantage.CONDITIONPRESENT", "Condition Is Present (Only Mark when Constant)", conditionpresent);
    //
    //                PADDialog pd = new PADDialog(null);
    //                PADValueListener pvl = new PADValueListener() {
    //                    public void PADValueChanged(PADValueEvent evt) {
    //                    }
    //
    //                    public boolean PADValueChanging(PADValueEvent evt) {
    //
    //                        return true;
    //                        //nocv.intValue() + ndcv.intValue() + ndc.intValue() * 2 <= level.intValue();
    //                    }
    //                };
    //                int result = pd.showPADDialog( "Adjust " + ability.getName() + " State", pl, currentAbility, pvl);
    //
    //                if ( result == JOptionPane.CANCEL_OPTION ) {
    //                    currentAbility.add("Disadvantage.CONDITIONPRESENT", conditionpresent, true);
    //
    //                }
    //            }
    //        };
    //
    //        v.add(assignAction);
    //    }
    
    /** Attempt to identify power
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     */
    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();
        
        if ( power != null && power.equals( "SUSCEPTIBILITY" )){
            return 10;
        }
        return 0;
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
    
}