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
import champions.parameters.ListParameter;
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

public class powerChangeEnvironment extends Power implements ChampionsConstants {
    static final long serialVersionUID =5295848683348706403L;
    
    static private String[] sensesArray = {
        "None", "Normal Sight","IR","UV","Unusual detect/awareness","All Sight",
        "Normal Hearing","Ultrasonic Hearing","Sonar","Unusual detect/awareness","All Hearing",
        "Radio Listen","Radio Listen & Transmit","High Range Radio","Radar","Unusual detect/awareness","All Radio",
        "Normal Smell","Discriminatory Smell","Tracking Scent","Normal Taste","Discriminatory Taste","Unusual detect/awareness","All Smell",
        "Unusual detect/awareness","N-Ray Vision","All Unusual",
        "Mind Scan","Mental Awareness","Unusual detect/awareness","All Mental",
    };
    
    static private String[] senseGroupArray = {
        "None", "All Sight", "All Hearing", "All Radio", "All Smell", "All Unusual", "All Mental", "Hearing Group"
    };
    
    static private String[] faderateOptions = {"5 minutes", "1 hour", "5 hours", "1 day", "1 week", "1 month", "1 season", "1 year", "5 years", "1 decade", "5 decades","1 Century" };
    
    static public String[] statskillOptions = {"STR","CON","DEX","BODY","INT","EGO","PRE","COM","Breakfall"};
    
    static public String[] statOptions = {"STR","CON","DEX","BODY","INT","EGO","PRE","COM"};
    
    static public String[] movementOptions = {"Extra-Dimensional Movement","Faster-Than-Light Travel","Flight","Gliding","Leaping","Running","Swimming","Swinging","Teleportation","Tunneling"};
    
    static private Object[][] parameterArray = {
        {"Level","Power.LEVEL", Integer.class, new Integer(1), "Level", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"Radius","Power.RADIUS", Integer.class, new Integer(1), "Radius", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        {"RadiusImport","Power.RADIUSIMPORT", Integer.class, new Integer(-1), "Radius Import", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        {"PERmodifier","Power.PERMODIFIER", Integer.class, new Integer(0), "PER roll modifier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"Senses","Senses*.SENSE", String.class, null, "Senses", LIST_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED, "OPTIONS", sensesArray},
        {"SenseGroup","SenseGroup*.SENSEGROUP", String.class, null, "Sense Group", LIST_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED, "OPTIONS", senseGroupArray},
        {"SenseImport","Power.SENSEIMPORT", String.class, "Default", "Senses", STRING_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        {"STATSkillModifier","Power.STATSKILLMODIFIER", Integer.class, new Integer(0), "Characteristic or Skill Roll Modifier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"StatSkill","Power.STATSKILL", String.class, "STR", "Affected Characteristic or Skill Roll", COMBO_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED, "OPTIONS", statOptions},
        {"MovementModifier","Power.MOVEMENTMODIFIER", Integer.class, new Integer(0), "Movement Modifier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"MovementAffected","Power.MOVEMENTAFFECTED", String.class, "Running", "Movement Affected", LIST_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED, "OPTIONS", movementOptions},
        {"TemperatureAffected","Power.TEMPERATUREAFFECTED", Integer.class, new Integer(0), "Temperature Affected", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"STATmodifier","Power.STATMODIFIER", Integer.class, new Integer(0), "Characteristic AND Skill Roll Modifier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"Stat","Power.STAT", String.class, "STR", "Affected Characteristic Based Roll", COMBO_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED, "OPTIONS", statOptions},
        {"OCVmodifier","Power.OCVMODIFIER", Integer.class, new Integer(0), "OCV Modifier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"DCVmodifier","Power.DOCVMODIFIER", Integer.class, new Integer(0), "DCV Modifier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"DamagingModifier","Power.DAMAGINGMODIFIER", Integer.class, new Integer(0), "Damaging Modifier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"DamagingAbility","DamagingAbility*.ABILITY", String.class, null, "Damaging Ability", LIST_PARAMETER, HIDDEN, ENABLED, REQUIRED},
        {"TelekinesisSTR","Power.TELEKINESISSTR", Integer.class, new Integer(0), "Telekinesis STR", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"FadeRate", "Power.FADERATE", String.class, "5 minute", "Fade Rate Level", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", faderateOptions },
        {"FadeRateLevel", "Power.FADERATE", Integer.class, new Integer(0), "Telekinesis STR", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        //Added to determine if temperature is positive or negative
        {"TemperatureNegative","Adjustment.TEMPERATURENEGATIVE",  Boolean.class, new Boolean(false), "Positive or Negative Temperature", BOOLEAN_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        
    };
    
    // Power DefinitRadiuson Variables
    private static String powerName = "Change Environment"; // The Name of the Power
    private static String targetType = "SINGLE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "CONSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "ATTACK"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = 5; // The Point cost of a single Damage Class of this power.
    private static String attackType = "RANGED"; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = "SPECIAL"; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 1; // Indicates the END Multiplier for this power
    private static boolean dynamic = true;
    private static String description = "Dude, its just Change Environment, ok?";
    
    // Cost Array - See Power.getCostArray()
    static private Object[][] costArray = {
        { "Level", GEOMETRIC_COST, DYNAMIC_RECONFIG, PROPORTIONAL_RECONFIG, new Integer(5), new Integer(1), new Integer(0), new Integer(0) },
        { "FadeRateLevel", GEOMETRIC_COST, DYNAMIC_RECONFIG, PROPORTIONAL_RECONFIG, new Integer(5), new Integer(1), new Integer(0), new Integer(0) },
    };
    
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { ".* \\(([0-9]*).*", new Object[] { "RadiusImport", Integer.class }},
        { "Effect: Fixed.*", null},
        { "LEVELS: ([0-9]*).*", new Object[] { "Level", Integer.class }},
        { "Long\\-Lasting (.*)", new Object[] { "FadeRate", String.class }},
        { "(\\-[0-9]*) PER Roll.*", new Object[] { "PERmodifier", Integer.class }},
        { "(\\-[0-9]*) OCV.*", new Object[] { "OCVmodifier", Integer.class }},
        { "(\\-[0-9]*) DCV.*", new Object[] { "DCVmodifier", Integer.class }},
        { "(\\-[0-9]*).*Movement.*", new Object[] { "MovementModifier", Integer.class }},
        { "([+-][0-9]*) Temperature.*", new Object[] { "TemperatureAffected", Integer.class }},
        { "(\\+[0-9]*) Points of Damage.*", new Object[] { "DamagingModifier", Integer.class }},
        { "(\\-[0-9]*) to Characteristic Roll.*", new Object[] { "STATSkillModifier", Integer.class }},
        { ".* PER Roll (.*)", new Object[] { "Sense", String.class }},
        { ".* PER Roll (.*)", new Object[] { "SenseGroup", String.class }},
    };
    
    // Known Caveats Array
    private static String[] caveats = {
        "None known."
    };
    
    /** Creates new powerHandToHandAttack */
    public powerChangeEnvironment()  {
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
    
    public boolean configurePAD(Ability ability, ParameterList parameterList) {
        // Fail immediately if ability is null
        if ( ability == null ) return false;
        
        // Always Set the ParameterList to the parameterList
        setParameterList(ability,parameterList);
        
        // Read in any parameters that will be needed to configure the power or
        // Determine the validity of the power configuration.  Read the parameters
        // from the parameterList, instead of directly from the ability, since the
        // Ability isn't configured yet.
        Integer level = (Integer)parameterList.getParameterValue("Level");
        Integer Radius= (Integer)parameterList.getParameterValue("Radius");
        Integer radiusimport = (Integer)parameterList.getParameterValue("RadiusImport");
        Integer permodifier = (Integer)parameterList.getParameterValue("PERmodifier");
        Integer statskillmodifier = (Integer)parameterList.getParameterValue("STATSkillModifier");
        Integer movementmodifier = (Integer)parameterList.getParameterValue("MovementModifier");
        Integer temperatureaffected = (Integer)parameterList.getParameterValue("TemperatureAffected");
        Integer statmodifier = (Integer)parameterList.getParameterValue("STATmodifier");
        Integer OCVmodifier = (Integer)parameterList.getParameterValue("OCVmodifier");
        Integer DCVmodifier = (Integer)parameterList.getParameterValue("DCVmodifier");
        Integer damagingmodifier = (Integer)parameterList.getParameterValue("DamagingModifier");
        Integer telekinesisstr = (Integer)parameterList.getParameterValue("TelekinesisSTR");
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        int index, radiusvalue = 0;
        if (radiusimport.intValue() > 0) {
            parameterList.setParameterValue("RadiusImport", new Integer(-1) );
            parameterList.setParameterValue("Radius", new Integer(radiusimport.intValue() ) );
            radiusvalue = radiusimport.intValue();
            
            for (index =0;radiusvalue > 0; index++) {
                radiusvalue = radiusvalue /2;
            }
            parameterList.setParameterValue("Level", new Integer(index) );
            
        }
        else {
            int radiustemp = 0;
            for (index = 0;index < level.intValue();index++) {
                radiustemp= radiustemp *2;
                if (radiustemp ==0) {
                    radiustemp = 1;
                }
            }
            parameterList.setParameterValue("Radius", new Integer(radiustemp ) );
        }
        
        if (permodifier.intValue() != 0 ) {
            parameterList.setVisible( "Senses", true);
            parameterList.setVisible( "SenseGroup", true);
        }
        else {
            parameterList.setVisible( "Senses", false);
            parameterList.setVisible( "SenseGroup", false);
        }
        
/*        if (statskillmodifier.intValue() != 0 ) {
            parameterList.setVisible( "StatSkill", true);
        }
        else {
            parameterList.setVisible( "StatSkill", false);
        }
        if (movementmodifier.intValue() != 0 ) {
            parameterList.setVisible( "MovementAffected", true);
        }
        else {
            parameterList.setVisible( "MovementAffected", false);
        }
 
        if (statmodifier.intValue() != 0 ) {
            parameterList.setVisible( "Stat", true);
        }
        else {
            parameterList.setVisible( "stat", false);
        }
        if (damagingmodifier.intValue() != 0 ) {
            parameterList.setVisible( "DamagingAbility", true);
        }
        else {
            parameterList.setVisible( "DamagingAbility", false);
        }
 
 */
        // Always copy the configuration parameters directly into the ability.  This will
        // take the parameters stored in the parameter list and copy them into the
        // ability using the appropriate keys and values.
        parameterList.copyValues(ability);
        ability.add("Ability.ISAE", "TRUE" , true );
        ability.add("Ability.ISSELECTIVEAE", "FALSE", true);
        ability.add("Ability.ISNONSELECTIVEAE", "FALSE", true);
        
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
        //        ability.add("Base.DC",  new Double(ChampionsUtilities.StringToNormalDC(radius)), true);
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription(  getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    
    public void triggerPower(BattleEvent be, Ability ability,DetailList effectList, Target target, int refNumber, String targetGroup) throws BattleEventException {
        Effect effect = new effectChangeEnvironment( ability );
        effectList.createIndexed(  "Effect","EFFECT",effect) ;
    }
    
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        Integer radius = (Integer)parameterList.getParameterValue("Radius");
        //String sense = (String)parameterList.getParameterValue("Sense");
        //String targeting = (String)parameterList.getParameterValue("Targeting");

        return "Change Environment (" + radius + "\" Radius)";
    }
    
        /** Attempt to identify power
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     */
    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();
        
        if ( power != null && ( power.equals( "CHANGEENVIRONMENT" ) || power.equals( "Change Environment" ) )){
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
    
    public boolean checkParameter(Ability ability, int index, String key, Object value, Object oldValue) {
        ParameterList parameterList = getParameterList(ability);
        Integer level = (Integer)parameterList.getParameterValue("Level",key,value);
        Integer Radius= (Integer)parameterList.getParameterValue("Radius",key,value);
        Integer radiusimport = (Integer)parameterList.getParameterValue("RadiusImport",key,value);
        Integer permodifier = (Integer)parameterList.getParameterValue("PERmodifier",key,value);
        Integer statskillmodifier = (Integer)parameterList.getParameterValue("STATSkillModifier",key,value);
        Integer movementmodifier = (Integer)parameterList.getParameterValue("MovementModifier",key,value);
        Integer temperatureaffected = (Integer)parameterList.getParameterValue("TemperatureAffected",key,value);
        Integer statmodifier = (Integer)parameterList.getParameterValue("STATmodifier",key,value);
        Integer OCVmodifier = (Integer)parameterList.getParameterValue("OCVmodifier",key,value);
        Integer DCVmodifier = (Integer)parameterList.getParameterValue("DCVmodifier",key,value);
        Integer damagingmodifier = (Integer)parameterList.getParameterValue("DamagingModifier",key,value);
        Integer telekinesisstr = (Integer)parameterList.getParameterValue("TelekinesisSTR",key,value);
        
        if ( level.intValue() < 0 || permodifier.intValue() > 0 || statskillmodifier.intValue() > 0 || movementmodifier.intValue() > 0 || statmodifier.intValue() > 0 || OCVmodifier.intValue() > 0 || DCVmodifier.intValue() > 0 ){
            return false;
        }
        return true;
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
    // Special Handling is necessary to configure the ability.
    public ParameterList getParameterList(Ability ability, int not_used) {
        ParameterList pl = super.getParameterList(ability,not_used);
        ListParameter param = (ListParameter)pl.getParameter("DamagingAbility");
        if ( param.getModel() == null) {
            param.setModel( new AbilityListModel((ability.getSource() == null ? null : ability.getSource().getAbilityList() )) );
        }
        return pl;
    }
    
    public void abilitySourceSet(Ability ability, Target oldSource, Target newSource) {
        ParameterList pl = super.getParameterList(ability,-1);
        ListParameter param = (ListParameter)pl.getParameter("DamagingAbility");
        param.setModel( new AbilityListModel((newSource == null ? null : newSource.getAbilityList() )) );
    }
    
    /** Executed for all PADs just prior to an ability being saved.
     * All clean up should be done at this point.
     */
//    public void prepareToSave(Ability ability, int index) {
//        // Make sure you clean up the DamagingAbility model, since it will contain pointers to the world and
//        // will cause havoc when reloaded.
//        ParameterList pl = getParameterList(ability,index);
//        if ( pl.getParameterOption("DamagingAbility","MODEL") != null) {
//            pl.setParameterOption("DamagingAbility","MODEL", null);
//        }
//    }
}