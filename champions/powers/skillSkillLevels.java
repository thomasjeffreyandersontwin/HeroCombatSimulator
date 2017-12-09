/*
 * maneuverEscapeFromGrab.java
 *
 * Created on April 22, 2001, 7:36 PM
 */
package champions.powers;

import champions.Ability;
import champions.AbilityImport;
import champions.BattleEvent;
import champions.DetailList;
import champions.Effect;
import champions.PADDialog;
import champions.Skill;
import champions.SkillListModel;
import champions.Target;
import champions.parameters.ListParameter;
import champions.parameters.ParameterList;
import champions.event.PADValueEvent;
import champions.exception.BattleEventException;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.PADValueListener;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

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
public class skillSkillLevels extends Skill implements ChampionsConstants {

    static final long serialVersionUID = 5295828683348707403L;
    static private String[] identifyArray = {
        "Single Skill",
        "Three Related Skills",
        "Group of Similar Skills",
        "All Non-Combat Skills",
        "Overall Level",};
    static private Object[][] parameterArray = {
        {"Level", "Power.SKILLLEVEL", Integer.class, new Integer(1), "Extra Level", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"LevelType", "Power.LEVELTYPE", String.class, "Single Skill", "Skill Level Type", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", identifyArray},
        {"CanUseSL", "CanUseSL*.ABILITY", String.class, null, "Skill Levels are active for", LIST_PARAMETER, VISIBLE, ENABLED, REQUIRED},
        //  {"Overall", "Power.OVERALL", String.class, "None", "Type", STRING_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        {"AllocatedSL", "SkillLevel.ALLOCATEDSL", Integer.class, new Integer(0), "Assigned Skill levels", INTEGER_PARAMETER, VISIBLE, DISABLED, NOTREQUIRED},
        {"OCVLevel", "SkillLevel.OCVBONUS", Integer.class, new Integer(0), "Assigned OCV levels", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"DCVLevel", "SkillLevel.DCVBONUS", Integer.class, new Integer(0), "Assigned DCV levels ", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"DCLevel", "SkillLevel.DCBONUS", Integer.class, new Integer(0), "Assigned DC levels", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED}, //   {"UseasCL","SkillLevel.USEASCL", Boolean.class, new Boolean(false), "USE As CL", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
    };
    // Power Definition Variables
    private static String powerName = "Skill Levels"; // The Name of the Power
    private static String targetType = "SELF"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "PERSISTENT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "INSTANT"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = 5; // The Point cost of a single Damage Class of this power.
    private static String attackType = null; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = null; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 0; // Indicates the END Multiplier for this power
    private static boolean dynamic = true;
    private static String description = "";
    // Known Caveats Array
    private static String[] caveats = {};

    // Import Patterns Definitions
    private static Object[][] patterns = {
        //        { "\\+([0-9]*) (level w/.*)", new Object[] { "Level", Integer.class, "AbilityName", String.class}}
        {"\\+([0-9]*) level w/.*", new Object[]{"Level", Integer.class}},
        {"\\+([0-9]*) Level w/.*", new Object[]{"Level", Integer.class}},
        {"Assigned to OCV: (.*)", new Object[]{"OCVLevel", Integer.class}},
        {"Assigned to DCV: (.*)", new Object[]{"DCVLevel", Integer.class}},
        {"Assigned to DC: (.*)", new Object[]{"DCLevel", Integer.class}},
        {"Use as CL: (.*)", new Object[]{"UseasCL", String.class}},
        //{ "\\+([0-9]*) (with single Characteristic Roll).*", new Object[] { "Level", Integer.class,"LevelType", String.class }},
        //{ "\\+([0-9]*) (with any three related Skills) .*", new Object[] { "Level", Integer.class,"LevelType", String.class }},
        //{ "\\+([0-9]*) (with a group of similar Skills).*", new Object[] { "Level", Integer.class,"LevelType", String.class }},
        //{ "\\+([0-9]*) (with all non-combat Skills).*", new Object[] { "Level", Integer.class,"LevelType", String.class }},
        //{ "\\+([0-9]*) (Overall).*", new Object[] { "Level", Integer.class,"LevelType", String.class }},        
        //HeroDesigner (import pattern for levels -kjr)
        {"LEVELS: ([0-9]*)", new Object[]{"Level", Integer.class}},
        {"(with single Characteristic Roll)", new Object[]{"LevelType", String.class}},
        {"(with any three related Skills)", new Object[]{"LevelType", String.class}},
        {"(with a group of similar Skills)", new Object[]{"LevelType", String.class}},
        {"(with all non-combat Skills)", new Object[]{"LevelType", String.class}},
        {"(Overall)", new Object[]{"LevelType", String.class}},
        {"ROLL:.*", null},};
    // Cost Array - See PowerAdapter.getCostArray()
    static private Object[][] costArray = {
        {"Level", GEOMETRIC_COST, DYNAMIC_RECONFIG, PROPORTIONAL_RECONFIG, new Integer(2), new Integer(1), new Integer(0), new Integer(0)},};

    /** Creates new powerHandToHandAttack */
    public skillSkillLevels() {
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
        if (ability == null) {
            return false;
        }

        // Always Set the ParameterList to the parameterList
        setParameterList(ability, parameterList);

        // Read in any parameters that will be needed to configure the power or
        // Determine the validity of the power configuration.  Read the parameters
        // from the parameterList, instead of directly from the ability, since the
        // Ability isn't configured yet.

        Integer levels = (Integer) parameterList.getParameterValue("Level");
        // String overall = (String) parameterList.getParameterValue("Overall");
        String leveltype = (String) parameterList.getParameterValue("LevelType");
        Integer allocatedSL = (Integer) parameterList.getParameterValue("AllocatedSL");
        Integer ocv = (Integer) parameterList.getParameterValue("OCVLevel");
        Integer dcv = (Integer) parameterList.getParameterValue("DCVLevel");
        Integer dc = (Integer) parameterList.getParameterValue("DCLevel");

        String name = ability.getName();
        if (!ability.getName().startsWith("Level w/ ") && !ability.getName().startsWith("Skill Levels")) {
            ability.setName("Level w/ " + name);
        }

        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure

        // No Validation Necessary
        if (leveltype.equals("with single Characteristic Roll")) {
            parameterList.setParameterValue("LevelType", "Single Skill");
        } else if (leveltype.equals("with any three related Skills")) {
            parameterList.setParameterValue("LevelType", "Three Related Skills");
        } else if (leveltype.equals("with a group of similar Skills")) {
            parameterList.setParameterValue("LevelType", "Group of Similar Skills");
        } else if (leveltype.equals("with all non-combat Skills")) {
            parameterList.setParameterValue("LevelType", "All Non-Combat Skills");
        } else if (leveltype.equals("Overall")) {
            parameterList.setParameterValue("LevelType", "Overall Level");
        }


        // Always copy the configuration parameters directly into the ability.  This will
        // take the parameters stored in the parameter list and copy them into the
        // ability using the appropriate keys and values.
        parameterList.copyValues(ability);

        // Start to Actually Configure the Power.
        // The Add Power Info should always be executed to add information to the ability.
        // All of this information should be set in the Power Definition Variables at the
        // top of this file
        ability.addPowerInfo(this, powerName, targetType, persistenceType, activationTime);
        if (attackType != null) {
            ability.addAttackInfo(attackType, damageType);
            ability.add("Ability.PPDC", new Double(pointsPerDC), true);
        }
        ability.setGenerateDefaultEffects(generateDefaultDamage);
        if (endMultiplier != 1) {
            ability.setENDMultiplier(endMultiplier);
        }

        // Add any dice information which is necessary to use this power.
        //   ability.addDiceInfo( "DamageDie", "", "Attack Damage");

        // Add A Damage Class Info
        //ability.add("Maneuver.DC",  dc, true);

        // Add Extra Value/Pairs used by the Power/BattleEngine
        //ability.setAutosource(true);
        //ability.setTargetSelf(true);
        //ability.setAutoHit(true);
        ability.setCType("SKILL");
        //String leveltype = (String)parameterList.getParameterValue("LevelType");
        if (leveltype.equals("Overall Level")) {
            //   ability.add("Power.OVERALL", "Overall (Always Active)", true);
            parameterList.setVisible("CanUseSL", false);
            parameterList.setVisible("OCVLevel", true);
            parameterList.setVisible("DCVLevel", true);
            parameterList.setVisible("DCLevel", true);

        } else {
            // ability.add("Power.OVERALL", "None", true);
            parameterList.setVisible("CanUseSL", true);
            parameterList.setVisible("OCVLevel", false);
            parameterList.setVisible("DCVLevel", false);
            parameterList.setVisible("DCLevel", false);

            ocv = 0;
            dcv = 0;
            dc = 0;

            allocatedSL = levels;
        }

        if (allocatedSL < 0 || ocv < 0 || dcv < 0 || dc < 0) {
            return false;
        }
        allocatedSL = levels - ocv - dcv - dc * 2;

        parameterList.setParameterValue("AllocatedSL", allocatedSL);
        parameterList.setParameterValue("OCVLevel", ocv);
        parameterList.setParameterValue("DCVLevel", dcv);
        parameterList.setParameterValue("DCLevel", dc);

//        Integer skilllevel = (Integer)parameterList.getParameterValue("Level");
//        ability.add("SkillLevel.ALLOCATEDSL",  skilllevel, true);


        // Update the Ability Description based on the new configuration
        ability.setPowerDescription(getConfigSummary(ability, -1));

        // Return true to indicate success
        return true;
    }

    // Special Handling is necessary to configure the ability.
    public ParameterList getParameterList(Ability ability, int not_used) {
        ParameterList pl = super.getParameterList(ability, not_used);
        ListParameter param = (ListParameter) pl.getParameter("CanUseSL");
        if (param.getModel() == null) {
            param.setModel(new SkillListModel((ability.getSource() == null ? null : ability.getSource().getAbilityList())));
        }
        return pl;
    }

    public void abilitySourceSet(Ability ability, Target oldSource, Target newSource) {
        ParameterList pl = super.getParameterList(ability, -1);
        ListParameter param = (ListParameter) pl.getParameter("CanUseSL");
        param.setModel(new SkillListModel((newSource == null ? null : newSource.getAbilityList())));
    }

    /** Executed for all PADs just prior to an ability being saved.
     * All clean up should be done at this point.
     */
//    public void prepareToSave(Ability ability, int index) {
//        // Make sure you clean up the CanUseSL model, since it will contain pointers to the world and
//        // will cause havoc when reloaded.
//        ParameterList pl = getParameterList(ability,index);
//        if ( pl.getParameterOption("CanUseSL","MODEL") != null) {
//            pl.setParameterOption("CanUseSL","MODEL", null);
//        }
//    }
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        //String stat = (String)parameterList.getParameterValue("BaseStat");
        Integer level = (Integer) parameterList.getParameterValue("Level");

        String s = null; //stat + " based skill with " + levels.toString() + " levels. (SR: " + stat + "/5+" + Integer.toString(levels + 9) + ")";

        return s;
    }

    public void triggerPower(BattleEvent be, Ability ability, DetailList effectList, Target target, int referenceNumber, String targetGroup) throws BattleEventException {
//        ParameterList parameterList = getParameterList(ability);
//        String overall = (String)parameterList.getParameterValue("Overall");
//        String useascl = (String)parameterList.getParameterValue("UseasCL");
//
//        if (overall.equals("Overall (Always Active)") && useascl.equals("TRUE") ) {
//            Effect effect = new effectSkillLevel(ability.getName(),ability);
//            effectList.createIndexed("Effect","EFFECT",effect) ;
//                }
//        else {
        Effect effect = new effectSkillLevel(ability.getName(), ability, target);
        int index = effectList.createIndexed("Effect", "EFFECT", effect);
        effectList.addIndexed(index, "Effect", "TTYPE", "SOURCE", true, false);
//        }
    }
//    

    public boolean checkParameter(Ability ability, int index, String key, Object value, Object oldValue) {
        ParameterList parameterList = getParameterList(ability);
        Integer level = (Integer) parameterList.getParameterValue("Level", key, value);
        //    String overall = (String) parameterList.getParameterValue("Overall");
        Integer ocv = (Integer) parameterList.getParameterValue("OCVLevel", key, value);
        Integer dcv = (Integer) parameterList.getParameterValue("DCVLevel", key, value);
        Integer dc = (Integer) parameterList.getParameterValue("DCLevel", key, value);

        if (level + ocv < 0 || dc < 0 || dcv < 0 || ocv + dcv + (dc * 2) > level) {
            return false;
        }
        return true;
    }

    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();

        if (power != null) {
            for (int i = 0; i < identifyArray.length; i++) {
                if (power.equals(identifyArray[i])) {
                    return 10;
                }
            }
        }
        if (power != null && (power.equals("Skill Levels"))) {
            return 10;
        }
        if (power != null && (power.equals("SKILL_LEVELS"))) {
            return 10;
        } else if (power != null && (power.equals("with single Characteristic Roll"))) {
            return 10;
        } else if (power != null && (power.equals("with any three related Skills"))) {
            return 10;
        } else if (power != null && (power.equals("with a group of similar Skills"))) {
            return 10;
        } else if (power != null && (power.equals("with all non-combat Skills"))) {
            return 10;
        } else if (power != null && (power.equals("Overall"))) {
            return 10;
        }


        return 0;
    }

//    public void importPower(Ability ability, AbilityImport ai) {
//        // First Do the standard import.  This will parse the RegExp andpopulate
//        super.importPower(ability,ai);
//        
//        ParameterList parameterList = getParameterList(ability);
//        
//        String powername = (String)ai.getValue("AbilityImport.POWERNAME");
//        if (!powername.equals("Skill Levels")) {
//            String leveltype = (String)ai.getValue("AbilityImport.POWERNAME");
//            //parameterList.setParameterValue("LevelType", leveltype);
//        }
//        
//        //String leveltype = (String)ai.getValue("AbilityImport.POWERNAME");
//        String leveltype = (String)parameterList.getParameterValue("LevelType");
//        Integer level = (Integer)parameterList.getParameterValue("Level");
//        Integer ocv = (Integer)parameterList.getParameterValue("OCVLevel");
//        Integer dcv = (Integer)parameterList.getParameterValue("DCVLevel");
//        Integer dc = (Integer)parameterList.getParameterValue("DCLevel");
//        
//        parameterList.setParameterValue("LevelType", leveltype);
//        if ( ocv == 0 && dcv == 0 && dc == 0) {
//            parameterList.setParameterValue("Level", level );
//        }
//        
//        boolean found = false;
//        
//        String stringValue;
//        String line;
//        
//        for(int lineIndex = 0; lineIndex < ai.getImportLineCount(); lineIndex++) {
//            line = ai.getImportLine(lineIndex);
//            if ( ChampionsMatcher.matches( "Attached to: (.*)", line ) ) {
//                stringValue = ChampionsMatcher.getMatchedGroup(1);
//                
//                int cindex = parameterList.createIndexed("CanUseSL", "NAME", stringValue, false);
//                parameterList.addIndexed(cindex, "CanUseSL", "LINE", new Integer(lineIndex), true, false );
//                
//                ai.setLineUsed(lineIndex, this);
//            }
//        }
//        
//    }
//    
//
//    
//    /* Finishes Importing Ability.
//     *
//     * This method is called after the character has been completely built, with all abilities
//     * that are going to be added already added.  This method can be used to finalize any necessary
//     * ability changes, such as translating from Strings to actual Ability objects.
//     *
//     * This method should return true if it wants the configurePAD to be run once it is done
//     * finalizing the method.
//     */
//    public boolean finalizeImport(Ability ability, AbilityImport ai) {
//        ParameterList parameterList = getParameterList(ability);
//        Target source = ability.getSource();
//        boolean set = false;
//        
//        int count = parameterList.getIndexedSize("CanUseSL");
//        for ( int index = 0; index < count; index++) {
//            Ability a = (Ability) parameterList.getIndexedValue(index, "CanUseSL", "ABILITY");
//            String name = parameterList.getIndexedStringValue(index, "CanUseSL", "NAME");
//            
//            if ( a == null && name != null ) {
//                // Here is a name with no associated ability...see if you can find an ability...
//                a = source.getAbility(name);
//                if ( a != null ) {
//                    parameterList.addIndexed(index, "CanUseSL", "ABILITY", a, true, false);
//                    parameterList.removeIndexed(index, "CanUseSL", "NAME", false);
//                    parameterList.removeIndexed(index, "CanUseSL", "LINE", false);
//                }
//                else {
//                    a = Battle.getDefaultAbilities().getAbility(name, true);
//                    if ( a != null ) {
//                        parameterList.addIndexed(index, "CanUseSL", "ABILITY", a, true, false);
//                        parameterList.removeIndexed(index, "CanUseSL", "NAME", false);
//                        parameterList.removeIndexed(index, "CanUseSL", "LINE", false);
//                    }
//                    else {
//                        Integer line = parameterList.getIndexedIntegerValue(index, "CanUseSL", "LINE");
//                        ai.setLineUnused(line);
//                        parameterList.removeAllIndexed(index, "CanUseSL", false);
//                        index--;
//                    }
//                    
//                }
//            }
//        }
//        
//        count = parameterList.getIndexedSize("CanUseSL");
//        parameterList.setParameterOption("CanUseSL", "SET", (count > 0) ? "TRUE" : "FALSE" );
//        
//        return true;
//    }
//    
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

    public int calculateCPCost(Ability ability) {
        ParameterList parameterList = getParameterList(ability);
        Integer level = (Integer) parameterList.getParameterValue("Level");
        //        String overall = (String)parameterList.getParameterValue("Overall");
        String leveltype = (String) parameterList.getParameterValue("LevelType");

        if (leveltype.equals("Single Skill")) {
            return level * 2;
        } else if (leveltype.equals("Three Related Skills")) {
            return level * 3;
        } else if (leveltype.equals("Group of Similar Skills")) {
            return level * 5;
        } else if (leveltype.equals("All Non-Combat Skills")) {
            return level * 8;
        } else if (leveltype.equals("Overall Level")) {
            return level * 10;
        }
        return 0;
    }

    public void addActions(Vector v, final Ability ability) {
        Action assignAction = new AbstractAction("Adjust Levels for " + ability.getName()) {

            public void actionPerformed(ActionEvent e) {
                final Ability currentAbility = ability.getInstanceGroup().getCurrentInstance();

                ParameterList parameterList = currentAbility.getPowerParameterList();

                final int level = (Integer) parameterList.getParameterValue("Level");
                int startingAllocatedSkillLevels = (Integer) parameterList.getParameterValue("AllocatedSL");
                int startingOCV = (Integer) parameterList.getParameterValue("OCVLevel");
                int startingDCV = (Integer) parameterList.getParameterValue("DCVLevel");
                int startingDC = (Integer) parameterList.getParameterValue("DCLevel");

                ParameterList pl = new ParameterList();
                pl.addIntegerParameter("ALLOCATEDSL", "SkillLevel.ALLOCATEDSL", "Skill Levels", startingAllocatedSkillLevels);
                pl.addIntegerParameter("OCV", "SkillLevel.OCVBONUS", "OCV Levels", startingOCV);
                pl.addIntegerParameter("DCV", "SkillLevel.DCVBONUS", "DCV Levels", startingDCV);
                pl.addIntegerParameter("DC", "SkillLevel.DCBONUS", "Damage Classes", startingDC);

                PADDialog pd = new PADDialog(null);
                PADValueListener pvl = new PADValueListener() {

                    public void PADValueChanged(PADValueEvent evt) {
                        ParameterList parameterList = currentAbility.getPowerParameterList();

                        if (evt.getKey().equals("SkillLevel.OCVBONUS")) {
                            parameterList.setParameterValue("OCVLevel", evt.getValue());
                        } else if (evt.getKey().equals("SkillLevel.DCVBONUS")) {
                            parameterList.setParameterValue("DCVLevel", evt.getValue());
                        } else if (evt.getKey().equals("SkillLevel.DCBONUS")) {
                            parameterList.setParameterValue("DCLevel", evt.getValue());
                        } else if (evt.getKey().equals("SkillLevel.ALLOCATEDSL")) {
                            parameterList.setParameterValue("AllocatedSL", evt.getValue());
                        }

                    }

                    public boolean PADValueChanging(PADValueEvent evt) {
                        ParameterList parameterList = currentAbility.getPowerParameterList();

                        int newAllocatedSkillLevels = (Integer) parameterList.getParameterValue("AllocatedSL");
                        int newOCV = (Integer) parameterList.getParameterValue("OCVLevel");
                        int newDCV = (Integer) parameterList.getParameterValue("DCVLevel");
                        int newDC = (Integer) parameterList.getParameterValue("DCLevel");


                        if (evt.getKey().equals("SkillLevel.OCVBONUS")) {
                            newOCV = (Integer) evt.getValue();
                        } else if (evt.getKey().equals("SkillLevel.DCVBONUS")) {
                            newDCV = (Integer) evt.getValue();
                        } else if (evt.getKey().equals("SkillLevel.DCBONUS")) {
                            newDC = (Integer) evt.getValue();
                        } else if (evt.getKey().equals("SkillLevel.ALLOCATEDSL")) {
                            newAllocatedSkillLevels = (Integer) evt.getValue();
                        }

                        if (newOCV < 0 || newDCV < 0 || newAllocatedSkillLevels < 0 || newDC < 0) {
                            return false;
                        }
                        return newAllocatedSkillLevels + newOCV + newDCV + newDC * 2 <= level;
                    }
                };
                int result = PADDialog.showPADDialog("Assign Skill Levels", pl, currentAbility, pvl);

                if (result == JOptionPane.CANCEL_OPTION) {
                    parameterList.setParameterValue("OCVLevel", startingOCV);
                    parameterList.setParameterValue("DCVLevel", startingDCV);
                    parameterList.setParameterValue("DCLevel", startingDC);
                    parameterList.setParameterValue("AllocatedSL", startingAllocatedSkillLevels);
                }
            }
        };

        v.add(assignAction);
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