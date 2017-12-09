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
import champions.parameters.ParameterList;

import java.lang.Math;
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
 * 1) Create costArray. * 2) Add the getCostArray() method, returning costArray.
 * 3) Remove existing calculateCPCost.
 */
public class powerEnhancedSenses extends Power implements ChampionsConstants{
    static final long serialVersionUID =5295848583348707203L;
    
    static public String[] sensesOptions = { "None","Active Sonar","High Range Radio Perception","Infrared Vision","Mental Awareness",
    "Night Vision","N-Ray Perception","Radar Sense","Radio Hearing","Radio Listen and Transmit","Spatial Awareness","Tracking Scent",
    "Ultrasonic Hearing","Ultraviolet Vision","Detect","Normal Sight","Normal Hearing","Normal Taste","Normal Touch","Normal Smell",
    "Sight Group","Hearing Group","Radio Group","Smell/Taste Group","Unusual Senses Group","Mental Group","All Senses"};
    
    //  static public String[] clairsentienceOptions = { "Present","Past","Future","Future and Past"};
    
    static public String[] arcOfPerceptionOptions = { "120 Degrees", "240 Degrees", "360 Degrees"};
    
    static public String[] senseGroupOptions = new String[] {"Sight Group","Hearing Group","Radio Group","Smell/Taste Group","Unusual Senses Group","Mental Group", "No Sense Group"};
    
    static private Object[][] parameterArray = {
        { "EnhancedSense","Power.ENHANCEDSENSE", String.class, "None", "Sense", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", sensesOptions },
        { "SenseChange","Power.SENSECHANGE", String.class, "Enhanced Sense", "Used to detect sense change", STRING_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED },
        
        { "SenseGroup", "Power.SENSEGROUP", String.class, "Normal Sight", "Sense Group", COMBO_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED, "OPTIONS", senseGroupOptions },
        // {"SenseImport","Power.SENSEIMPORT", String.class, "Default", "Senses", STRING_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        { "ArcOfPerception", "Power.ARCOFPERCEPTION", String.class, "120 Degrees", "Arc Of Perception", COMBO_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED, "OPTIONS", arcOfPerceptionOptions },
        { "Detect", "Power.DETECT", Boolean.class, new Boolean(false), "Detect", BOOLEAN_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED },
        { "DetectSense", "Power.DETECTSENSE", Boolean.class, new Boolean(false), "Detect is a sense", BOOLEAN_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED },
        { "Discriminatory","Power.DISCRIMINATORY", Boolean.class, new Boolean(false), "Discriminatory", BOOLEAN_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        { "Analyze","Power.ANALYZE", Boolean.class, new Boolean(false), "Analyze", BOOLEAN_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        { "EnhancedPerception","Power.ENHANCEDPERCEPTION", Integer.class, new Integer(0) , "Enhanced PER for sense(s)", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        // {"Merge","Power.MERGE", Boolean.class, new Boolean(false), "Merge with base sense", BOOLEAN_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        // {"MicroscopicSense","Power.MICROSCOPICSENSE", Boolean.class, new Boolean(false), "Microscopic", BOOLEAN_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        { "Magnification","Power.MAGNIFICATION", Integer.class, new Integer(0) , "Microscopic(x10)", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(0)},
        //  {"MagnificationImport","Power.MAGNIFICATIONIMPORT", Integer.class, new Integer(0) , "Levels of Magnification", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(0)},
        //  {"RangeImport","Power.RANGEIMPORT", Integer.class, new Integer(0) , "Range Multiplier import", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(0)},
        //{ "RangeMultiplier","Power.RANGEMULTIPLIER", Integer.class, new Integer(0) , "Range Multiplier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(0)},
        { "RapidLevel","Power.RAPIDLEVEL", Integer.class, new Integer(0) , "Rapid(x10)", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(0)},
        //  {"TelescopicSense","Power.TELESCOPICSENSE", Boolean.class, new Boolean(false), "Telescopic", BOOLEAN_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        //  {"PurchaseMultiples","Power.PURCHASEMULTIPLES", Integer.class, new Integer(0) , "Purchase multiplies of above sense", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(0)},
        { "Targeting","Power.TARGETING", Boolean.class, new Boolean(false), "Sense is a targeting sense", BOOLEAN_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED },
        { "TelescopicLevel","Power.TELESCOPICLEVEL", Integer.class, new Integer(0), "Telescopic Level", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED },
        { "Ranged","Power.RANGED", Boolean.class, new Boolean(false), "Sense can be used at range", BOOLEAN_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED },
        { "Tracking", "Power.TRACKING", Boolean.class, new Boolean(false), "Tracking", BOOLEAN_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED },
        { "Transmit", "Power.TRANSMIT", Boolean.class, new Boolean(false), "Transmit", BOOLEAN_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED },
        // { "DetectSense", "Power.DETECTSENSE", Boolean.class, new Boolean(false), "Detect is a sense", BOOLEAN_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED },
        
        //        { "Clairsentience", "Power.CLAIRSENTIENCE", Boolean.class, new Boolean(false), "Clairsentience", BOOLEAN_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED },
//        {"Dimensions","Power.DIMENSIONS", Boolean.class, new Boolean(false), "See into Dimensions", BOOLEAN_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
//        {"SeeInTime","Power.SEEINTIME", String.class, "Present", "See into", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", clairsentienceOptions },
        
    };
    
    // Power Definition Variables
    private static String powerName = "Enhanced Senses"; // The Name of the Power
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
        //        { "(Detect) .*+([0-9]*).*", new Object[] { "EnhancedSense", String.class, "EnhancedPerception", Integer.class }},
        
        
        { "See: (.*), .*", new Object[] { "SeeInTime", String.class }},
        { ".* \\(\\+([0-9]*) to .*", new Object[] { "EnhancedPerception", Integer.class}},
        { ".* \\(([0-9]*)\\)", new Object[] { "MagnificationImport", Integer.class}},
        { ".* \\((.*), \\+([0-9]*) to .*", new Object[] { "SenseImport", String.class, "EnhancedPerception", Integer.class}},
        { "Time Required: (Instant), .*", new Object[] { "DetectSense", Boolean.class }},
        { "Time Required: .*", null },
        { "Range.* (Ranged).*", new Object[] { "Ranged", Boolean.class }},
        { "Range \\((.*)\\)", new Object[] { "SenseImport", String.class }},
        //for clarisentience
        { "Range: ([0-9]*).*", new Object[] { "RangeImport", Integer.class}},
        { "Dimensions: (Other), .*", new Object[] { "Dimensions", Boolean.class }},
        { ".* \\((.*)\\)", new Object[] { "SenseImport", String.class }},
        { "(.*)", null },
        
    };
    
    
    
    public powerEnhancedSenses()  {
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
        String enhancedSense = (String)parameterList.getParameterValue("EnhancedSense");
//        String senseimport = (String)parameterList.getParameterValue("SenseImport");
        String senseChange = (String)parameterList.getParameterValue("SenseChange");
//        String merge = (String)parameterList.getParameterValue("Merge");
        boolean discriminatory = (Boolean)parameterList.getParameterValue("Discriminatory");
//        String microscopicsense = (String)parameterList.getParameterValue("MicroscopicSense");
//        String telescopicsense = (String)parameterList.getParameterValue("TelescopicSense");
//        String arcOfPerception = (String)parameterList.getParameterValue("ArcOfPerception");
//        String ranged = (String)parameterList.getParameterValue("Ranged");
//        String detectsense = (String)parameterList.getParameterValue("DetectSense");
//        String targeting = (String)parameterList.getParameterValue("Targeting");
//        Integer telescopicLevel = (Integer)parameterList.getParameterValue("TelescopicLevel");
//        Integer enhancedperception = (Integer)parameterList.getParameterValue("EnhancedPerception");
//        Integer magnification = (Integer)parameterList.getParameterValue("Magnification");
//        Integer magnificationimport = (Integer)parameterList.getParameterValue("MagnificationImport");
//        Integer rangemultiplier = (Integer)parameterList.getParameterValue("RangeMultiplier");
//        Integer rangeimport = (Integer)parameterList.getParameterValue("RangeImport");
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure.
        
        // Setup the necessary visible parameters if the sense has changed...
        if ( enhancedSense == null || enhancedSense.equals(senseChange) == false ) {
            updateParameters(ability,parameterList,enhancedSense);
            parameterList.setParameterValue("SenseChange", enhancedSense);
        }
        
        if ( "TRUE".equals(discriminatory)) {
            parameterList.setVisible("Analyze", true);
        } else {
            parameterList.setVisible("Analyze", false);
            parameterList.setParameterValue("Analyze", "FALSE");
        }
        
        // No Validation Necessary
//        int i;
//        if (magnificationimport.intValue() > 0) {
//            int mag = magnificationimport.intValue();
//            for (i=0;mag > 1;i++) {
//                mag = mag /10;
//            }
//            parameterList.setParameterValue("Magnification", new Integer(i) );
//            parameterList.setParameterValue("MagnificationImport", new Integer(0) );
//
//            magnification = (Integer)parameterList.getParameterValue("Magnification");
//            magnificationimport = (Integer)parameterList.getParameterValue("MagnificationImport");
//
//
//        }
        
//        int j;
//        int total;
//        total = 0;
//        if (clairsentience.equals("TRUE") ){
//            total = total +20;
//
//            if (rangeimport.intValue() > 0) {
//                int range = rangeimport.intValue();
//
//                if (seeintime.equals("Future"))  {
//                    total = total +20;
//                }
//                if (seeintime.equals("Past") ) {
//                    total = total +20;
//                }
//                if (seeintime.equals("Future and Past") ) {
//                    total = total +40;
//                }
//                if (dimensions.equals("TRUE") ) {
//                    total = total +20;
//                }
//                if ( enhancedSense.equals("Sight Group")
//                || enhancedSense.equals("Hearing Group")
//                || enhancedSense.equals("Radio Group")
//                || enhancedSense.equals("Smell/Taste Group")
//                || enhancedSense.equals("Mental Group")
//                || enhancedSense.equals("Unusual Senses Group") ) {
//                    total = total + 10;
//                }
//            }
//
//            //            rangemultiplier = rangeimport.intValue()/total;
//
//            total = total *5;
//            int k;
//            //Math.pow(10, magnification.intValue() )
//            for (k = 0;total < rangeimport.intValue();k++) {
//                total = total * 2;
//            }
//
//            System.out.println("range import: " +rangeimport + "total: " + total);
//            parameterList.setParameterValue("RangeMultiplier", new Integer(k) );
//            parameterList.setParameterValue("RangeImport", new Integer(0) );
//
//            rangemultiplier = (Integer)parameterList.getParameterValue("RangeMultiplier");
//            rangeimport = (Integer)parameterList.getParameterValue("RangeImport");
//
//
//        }
        
        
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
        //ability.add("Ability.NAME", enhancedSense , true);
        
        // static public String[] sensesOptions = {"None","Active Sonar"
        //,"High Range Radio Perception","Infrared Vision","Mental Awareness","Microscopic Vision","N-Ray Vision",
        //"Radar Sense","Radio Hearing","Radio Listen and Transmit",
        //"Spatial Awareness","Tracking Scent","Ultrasonic Hearing","Ultraviolet Vision",
        //"360 Degree Sensing","Normal Sight","Normal Hearing","Normal Taste","Normal Touch","Normal Smell","Detect"};
        
//        if (microscopicsense.equals("TRUE") ) {
//            //parameterList.setParameterValue("Magnification", new Integer(0) );
//            parameterList.setVisible( "Magnification", true);
//        }
//        else {
//            if (!enhancedSense.equals("Microscopic Vision") ){
//                parameterList.setParameterValue("Magnification", new Integer(0) );
//                parameterList.setVisible( "Magnification", false);
//            }
//        }
//
//        if (telescopicsense.equals("TRUE") ) {
//            //parameterList.setParameterValue("TelescopicLevel", new Integer(0) );
//            parameterList.setVisible( "TelescopicLevel", true);
//        }
//        else {
//            parameterList.setParameterValue("TelescopicLevel", new Integer(0) );
//            parameterList.setVisible( "TelescopicLevel", false);
//        }
//
//        if (clairsentience.equals("TRUE") ) {
//            parameterList.setVisible( "Dimensions", true);
//            parameterList.setVisible( "SeeInTime", true);
//        }
//        else {
//            parameterList.setVisible( "Clairsentience", true);
//            parameterList.setVisible( "Dimensions", false);
//            //parameterList.setParameterValue("Dimensions", "FALSE" );
//            parameterList.setVisible( "SeeInTime", false);
//            //parameterList.setParameterValue("SeeInTime", "FALSE" );
//        }
//
//
//        ////System.out.println("telescopicLevel: " + telescopicLevel);
//        changeShown(parameterList, enhancedSense, senseChange, senseimport, enhancedperception, ranged, telescopicsense, microscopicsense, telescopicLevel, merge, clairsentience);
        
        
        
        ////System.out.println(enhancedSense + " " + senseChange);
        
//        enhancedSense = (String)parameterList.getParameterValue("EnhancedSense");
//        parameterList.setParameterValue("SenseChange", enhancedSense );
        ////System.out.println(enhancedSense + " " + senseChange);
        
        parameterList.copyValues(ability);
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    
    private static Integer zero = new Integer(0);
    
    private static String[] senseNames = new String[] {
        "None",
        "Active Sonar",
        "High Range Radio Perception",
        "Infrared Vision",
        "Mental Awareness",
        "Night Vision",
        "N-Ray Perception",
        "Radar Sense",
        "Radio Listen and Transmit",
        "Radio Hearing",
        "Spatial Awareness",
        "Ultrasonic Hearing",
        "Ultraviolet Vision",
        "Danger Sense",
        "Combat Sense",
        "Detect",
        "Spactial Awareness"
                
    };
    
    private static String[] parameterNames = new String[] {
        "ArcOfPerception", "Detect", "DetectSense", "Discriminatory", "Analyze", "EnhancedPerception",
        "Magnification", "RapidLevel", "Targeting", "TelescopicLevel",
        "Ranged", "Tracking", "Transmit", "SenseGroup"
    };
    
    private static boolean[][] visibleParameters = new boolean[][] {
        // ARC, DETCT, SENSE, DISCR, ANALY, ENPER, MAGNI, RAPID, TARGT, TELEX, RANGD, TRACK, TRANS, GROUP
        {false, false, false, false, false, false, false, false, false, false, false, false, false, false}, // None
        { true, false, false,  true, false,  true,  true,  true,  true,  true,  true,  true,  true,  true},
        { true, false, false,  true, false,  true,  true,  true,  true,  true,  true,  true,  true,  true},
        { true, false, false,  true, false,  true,  true,  true,  true,  true,  true,  true,  true,  true},
        { true, false, false,  true, false,  true,  true,  true,  true,  true,  true,  true,  true,  true},
        { true, false, false,  true, false,  true,  true,  true,  true,  true,  true,  true,  true,  true},
        { true, false, false,  true, false,  true,  true,  true,  true,  true,  true,  true,  true,  true},
        { true, false, false,  true, false,  true,  true,  true,  true,  true,  true,  true,  true,  true},
        { true, false, false,  true, false,  true,  true,  true,  true,  true,  true,  true,  true,  true},
        { true, false, false,  true, false,  true,  true,  true,  true,  true,  true,  true,  true,  true},
        { true, false, false,  true, false,  true,  true,  true,  true,  true,  true,  true,  true,  true},
        { true, false, false,  true, false,  true,  true,  true,  true,  true,  true,  true,  true,  true},
        { true, false, false,  true, false,  true,  true,  true,  true,  true,  true,  true,  true,  true},
        { true, false, false,  true, false,  true,  true,  true,  true,  true,  true,  true,  true,  true},
        { true, false, false,  true, false,  true,  true,  true,  true,  true,  true,  true,  true,  true},
        { true, true,  false,  true, false,  true,  true,  true,  true,  true,  true,  true,  true,  true},
        { true, false, false,  true, false,  true,  true,  true,  true,  true,  true,  true,  true,  true},
    };
    
    private static boolean[][] enableParmeters = new boolean[][] {
        // ARC, DETCT, SENSE, DISCR, ANALY, ENPER, MAGNI, RAPID, TARGT, TELEX, RANGD, TRACK, TRANS, GROUP
        {false, false, false, false, false, false, false, false, false, false, false, false, false, false}, // None
        { true, false, false,  true,  true,  true,  true,  true, false,  true, false,  true,  true,  true},
        {false, false, false,  true,  true,  true,  true,  true, false,  true, false,  true, false,  true},
        { true, false, false,  true,  true,  true,  true,  true, false,  true, false,  true,  true,  true},
        { true, false, false,  true,  true,  true,  true,  true,  true,  true, false,  true,  true, false},
        { true, false, false,  true,  true,  true,  true,  true, false,  true, false,  true,  true, false},
        { true, false, false,  true,  true,  true,  true,  true, false,  true, false,  true,  true,  true},
        { true, false, false,  true,  true,  true,  true,  true, false,  true, false,  true,  true,  true},
        {false, false, false,  true,  true,  true,  true,  true,  true,  true, false,  true, false,  true},
        {false, false, false,  true,  true,  true,  true,  true,  true,  true, false,  true,  true,  true},
        { true, false, false,  true,  true,  true,  true,  true, false,  true,  true,  true,  true,  true},
        { true, false, false,  true,  true,  true,  true,  true,  true,  true, false,  true,  true,  true},
        { true, false, false,  true,  true,  true,  true,  true, false,  true, false,  true,  true,  true},
        { true, false, false,  true,  true,  true,  true,  true, false,  true,  true,  true,  true,  true},
        { true, false, false,  true,  true,  true,  true,  true, false,  true,  true,  true,  true,  true},
        { true, false, false,  true,  true,  true,  true,  true,  true,  true, false,  true,  true,  true},
        { true, false, false,  true,  true,  true,  true,  true, true,  true, false,  true,  true,  true},
    };
    
    private static Object[][] defaultValues = new Object[][] {
        //         ARC,   DETCT,   SENSE,   DISCR,   ANALY,  PER, MAGN,RAPID, TARG,  TELEX,   RANGD,   TRACK,    TRANS,         GROUP
        {"120 Degrees", "FALSE", "FALSE", "FALSE", "FALSE", zero, zero, zero, "FALSE", zero, "FALSE", "FALSE", "FALSE", "Sight Group"}, // None
        {"120 Degrees", "FALSE",  "TRUE", "FALSE", "FALSE", zero, zero, zero,  "TRUE", zero,  "TRUE", "FALSE", "FALSE", "Hearing Group"},
        {"360 Degrees", "FALSE",  "TRUE", "FALSE", "FALSE", zero, zero, zero,  "TRUE", zero,  "TRUE", "FALSE",  "TRUE", "Radio Group"},
        {"120 Degrees", "FALSE",  "TRUE", "FALSE", "FALSE", zero, zero, zero,  "TRUE", zero,  "TRUE", "FALSE", "FALSE", "Sight Group"},
        {"120 Degrees", "FALSE",  "TRUE", "FALSE", "FALSE", zero, zero, zero, "FALSE", zero,  "TRUE", "FALSE", "FALSE", "Mental Group"},
        {"120 Degrees", "FALSE",  "TRUE", "FALSE", "FALSE", zero, zero, zero,  "TRUE", zero,  "TRUE", "FALSE", "FALSE", "Sight Group"},
        {"120 Degrees", "FALSE",  "TRUE", "FALSE", "FALSE", zero, zero, zero,  "TRUE", zero,  "TRUE", "FALSE", "FALSE", "Sight Group"},
        {"120 Degrees", "FALSE",  "TRUE", "FALSE", "FALSE", zero, zero, zero,  "TRUE", zero,  "TRUE", "FALSE", "FALSE", "Radio Group"},
        {"360 Degrees", "FALSE",  "TRUE", "FALSE", "FALSE", zero, zero, zero, "FALSE", zero,  "TRUE", "FALSE",  "TRUE", "Radio Group"},
        {"360 Degrees", "FALSE",  "TRUE", "FALSE", "FALSE", zero, zero, zero, "FALSE", zero,  "TRUE", "FALSE", "FALSE", "Radio Group"},
        {"120 Degrees", "FALSE",  "TRUE", "FALSE", "FALSE", zero, zero, zero,  "TRUE", zero, "FALSE", "FALSE", "FALSE", "Unusual Group"},
        {"120 Degrees", "FALSE",  "TRUE", "FALSE", "FALSE", zero, zero, zero, "FALSE", zero,  "TRUE", "FALSE", "FALSE", "Hearing Group"},
        {"120 Degrees", "FALSE",  "TRUE", "FALSE", "FALSE", zero, zero, zero,  "TRUE", zero,  "TRUE", "FALSE", "FALSE", "Sight Group"},
        {"120 Degrees", "FALSE",  "TRUE", "FALSE", "FALSE", zero, zero, zero,  "TRUE", zero,  "TRUE", "FALSE", "FALSE", "Sight Group"},
        {"120 Degrees", "FALSE",  "TRUE", "FALSE", "FALSE", zero, zero, zero,  "TRUE", zero,  "TRUE", "FALSE", "FALSE", "Sight Group"},
        {"120 Degrees", "FALSE",  "TRUE", "FALSE", "FALSE", zero, zero, zero,  "TRUE", zero,  "TRUE", "FALSE", "FALSE", "Sight Group"},
        {"120 Degrees", "FALSE",  "TRUE", "FALSE", "FALSE", zero, zero, zero,  "TRUE", zero,  "TRUE", "FALSE", "FALSE", "Unusual Group"},
    };
    
    protected void updateParameters(Ability ability, ParameterList pl, String enhancedSense) {
        if ( enhancedSense == null ) return;
        
//        "None","Active Sonar","High Range Radio Perception","Infrared Vision","Mental Awareness"
//    ,"N-Ray Vision","Radar Sense","Radio Hearing","Radio Listen and Transmit","Spatial Awareness","Tracking Scent"
//    ,"Ultrasonic Hearing","Ultraviolet Vision","Detect","Normal Sight","Normal Hearing","Normal Taste","Normal Touch","Normal Smell"
//    ,"Sight Group","Hearing Group","Radio Group","Smell/Taste Group","Unusual Group","Mental Group","All Senses"};
        
        int senseNumber = 0;
        for(int i = 0; i < senseNames.length; i++) {
            if ( senseNames[i].equals(enhancedSense)) {
                senseNumber = i;
                break;
            }
        }
        
        
        for(int i = 0; i < parameterNames.length; i++) {
            
            pl.setVisible(parameterNames[i], visibleParameters[senseNumber][i]);
            pl.setEnabled(parameterNames[i], enableParmeters[senseNumber][i]);
            pl.setParameterValue(parameterNames[i], defaultValues[senseNumber][i]);
        }
    }
    
    public void triggerPower(BattleEvent be, Ability ability,DetailList effectList, Target target, int refNumber, String targetGroup) throws BattleEventException {
        
        ParameterList parameterList = getParameterList(ability);
        String senseName = (String)parameterList.getParameterValue("EnhancedSense");
        if ( senseName == null || senseName.equals("None")) return;
        
        Effect e = new effectEnhancedSense(ability.getName(), ability);
        effectList.createIndexed("Effect","EFFECT",e);
    }
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        String enhancedSense = (String)parameterList.getParameterValue("EnhancedSense");
//        String senseChange = (String)parameterList.getParameterValue("SenseChange");
//        String discriminatory = (String)parameterList.getParameterValue("Discriminatory");
//        String merge = (String)parameterList.getParameterValue("Merge");
//        //String microscopicsense = (String)parameterList.getParameterValue("MicroscopicSense");
//        Integer magnification = (Integer)parameterList.getParameterValue("Magnification");
//        String telescopicsense = (String)parameterList.getParameterValue("TelescopicSense");
//        String ranged = (String)parameterList.getParameterValue("Ranged");
//        String targeting = (String)parameterList.getParameterValue("Targeting");
//        Integer telescopicLevel = (Integer)parameterList.getParameterValue("TelescopicLevel");
//        Integer enhancedperception = (Integer)parameterList.getParameterValue("EnhancedPerception");
//        String arcOfPerception = (String)parameterList.getParameterValue("ArcOfPerception");
//        String detectsense = (String)parameterList.getParameterValue("DetectSense");
//        String detect = (String)parameterList.getParameterValue("Detect");
        
        StringBuffer sb = new StringBuffer();
        sb.append( "Sense: " + enhancedSense );
        
//        if (discriminatory.equals("TRUE")){
//            sb.append(", discriminatory");
//        }
//        if (microscopicsense.equals("TRUE")){
//            sb.append(", " + ((int)Math.pow(10, magnification.intValue() ) ) + " microscopic magnification");
//        }
//        if (telescopicsense.equals("TRUE")){
//            sb.append(", " + telescopicLevel.intValue() + "\" telescopic range penalty reduction");
//        }
//        if (ranged.equals("TRUE")){
//            sb.append(", ranged");
//        }
//        if (targeting.equals("TRUE")){
//            sb.append(", targeting");
//        }
//        if (arcOfPerception.equals("TRUE")){
//            sb.append(", 360 degree sensing");
//        }
//        if (enhancedperception.intValue() > 0){
//            sb.append(", +" + enhancedperception + "\" PER");
//        }
//
//        if (enhancedSense.equals("Detect")){
//            sb.append(", Detect: " + detect );
//        }
//        if (detectsense.equals("TRUE")){
//            sb.append(", sense" );
//        }
        
        return sb.toString();
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
    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();
        
        if ( power != null && (power.equals( "Active Sonar" )
        ||  power.equals( "High Range Radio Perception" )
        ||  power.equals( "Infrared Vision" )
        ||  power.equals( "Mental Awareness" )
        ||  power.equals( "Microscopic Vision" )
        ||  power.equals( "N-Ray Vision" )
        ||  power.equals( "Radar Sense" )
        ||  power.equals( "Radio Hearing" )
        ||  power.equals( "Radio Listen and Transmit" )
        ||  power.equals( "Spatial Awareness" )
        ||  power.equals( "Tracking Scent" )
        ||  power.equals( "Ultrasonic Hearing" )
        ||  power.equals( "Ultraviolet Vision" )
        ||  power.equals( "360 Degree Sensing" )
        ||  power.equals( "Normal Sight" )
        ||  power.equals( "Normal Hearing" )
        ||  power.equals( "Normal Taste" )
        ||  power.equals( "Normal Touch" )
        ||  power.equals( "Normal Smell" )
        ||  power.equals("Range")
        ||  power.equals("360-Degree Sensing")
        ||  power.equals("Discriminatory Sense")
        ||  power.equals("Enhanced Perception")
        ||  power.equals("Enhanced Perception (all)")
        ||  power.equals("Telescopic Sense")
        ||  power.equals("Targeting Sense")
        ||  power.equals("Sense")
        ||  power.equals("Clairsentience")
        ||  power.equals( "Detect" )  ) ) {
            return 10;
        }
        return 0;
    }
    
    
    // static public String[] sensesOptions = {"None","Active Sonar"
    //,"High Range Radio Perception","Infrared Vision","Mental Awareness","Microscopic Vision","N-Ray Vision",
    //"Radar Sense","Radio Hearing","Radio Listen and Transmit",
    //"Spatial Awareness","Tracking Scent","Ultrasonic Hearing","Ultraviolet Vision",
    //"360 Degree Sensing","Normal Sight","Normal Hearing","Normal Taste","Normal Touch","Normal Smell","Detect"};
    
    public void importPower(Ability ability, AbilityImport ai) {
        // First Do the standard import.  This will parse the RegExp andpopulate
        super.importPower(ability,ai);
        
        ParameterList parameterList = getParameterList(ability);
        
        String powername = (String)ai.getValue("AbilityImport.POWERNAME");
        parameterList.setParameterValue("EnhancedSense", powername);
        
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
    //    public Object[][] getCostArray(Ability ability) {
    //        return costArray;
    //    }
    
    
    public int calculateCPCost(Ability ability) {
        ParameterList parameterList = getParameterList(ability);
        String enhancedSense = (String)parameterList.getParameterValue("EnhancedSense");
//        String senseChange = (String)parameterList.getParameterValue("SenseChange");
//        String merge = (String)parameterList.getParameterValue("Merge");
//        String discriminatory = (String)parameterList.getParameterValue("Discriminatory");
//        String microscopicsense = (String)parameterList.getParameterValue("MicroscopicSense");
//        Integer magnification = (Integer)parameterList.getParameterValue("Magnification");
//        String telescopicsense = (String)parameterList.getParameterValue("TelescopicSense");
//        String ranged = (String)parameterList.getParameterValue("Ranged");
//        String targeting = (String)parameterList.getParameterValue("Targeting");
//        Integer telescopicLevel = (Integer)parameterList.getParameterValue("TelescopicLevel");
//        Integer enhancedperception = (Integer)parameterList.getParameterValue("EnhancedPerception");
//        String arcOfPerception = (String)parameterList.getParameterValue("ArcOfPerception");
//        String detectsense = (String)parameterList.getParameterValue("DetectSense");
//        String clairsentience = (String)parameterList.getParameterValue("Clairsentience");
//        String dimensions = (String)parameterList.getParameterValue("Dimensions");
//        String seeintime = (String)parameterList.getParameterValue("SeeInTime");
        
        
        int total = 0;
        //double offset = 0;
        
        //total = (int)offset;
        
//        if (merge.equals("FALSE") ) {
//            if (enhancedSense.equals("Active Sonar") ) {
//                total = 15;
//
//            }
//            else if (enhancedSense.equals("High Range Radio Perception") ) {
//                total = 10;
//
//            }
//            else if (enhancedSense.equals("Infrared Vision") ) {
//                total = 5;
//
//            }
//            else if (enhancedSense.equals("Mental Awareness") ) {
//                total = 3;
//
//            }
//            else if (enhancedSense.equals("N-Ray Vision") ) {
//                total = 20;
//
//            }
//            else if (enhancedSense.equals("Radar Sense") ) {
//                total = 15;
//
//            }
//            else if (enhancedSense.equals("Radio Hearing") ) {
//                total = 3;
//
//            }
//            else if (enhancedSense.equals("Radio Listen and Transmit") ) {
//                total = 5;
//
//            }
//            else if (enhancedSense.equals("Spatial Awareness") ) {
//                total = 25;
//
//            }
//            else if (enhancedSense.equals("Tracking Scent") ) {
//                total = 10;
//            }
//            else if (enhancedSense.equals("Ultrasonic Hearing") ) {
//                total = 3;
//            }
//            else if (enhancedSense.equals("Ultraviolet Vision") ) {
//                total = 5;
//            }
//            else if (enhancedSense.equals("Detect") ) {
//                total = 3;
//                //                if (detectsense.equals("TRUE") ) {
//                //                    total = total +2;
//                //                }
//
//            }
//
//        }
//        if (targeting.equals("TRUE")
//        && !enhancedSense.equals("Normal Sight")
//        && !enhancedSense.equals("Active Sonar")
//        && !enhancedSense.equals("Radar Sense")
//        && !enhancedSense.equals("Spatial Awareness") ) {
//            total = total + 20;
//        }
//
//        if (discriminatory.equals("TRUE")
//        && !enhancedSense.equals("Normal Sight")
//        && !enhancedSense.equals("Normal Hearing") ) {
//            total = total + 5;
//        }
//
//        if (microscopicsense.equals("TRUE") ) {
//            total = total + (3 * magnification.intValue() );        }
//
//        if (telescopicsense.equals("TRUE") ) {
//            ////System.out.println( telescopicLevel );
//            total = total + (int)(1.5 * telescopicLevel.intValue() );
//        }
//
//        if (ranged.equals("TRUE")
//        && (enhancedSense.equals("Normal Touch") || enhancedSense.equals("Normal Taste")|| enhancedSense.equals("Detect")) ) {
//            total = total + 5;
//        } else
//            if ((ranged.equals("TRUE") && merge.equals("FALSE") )
//            && ( enhancedSense.equals("Smell/Taste Group") || enhancedSense.equals("Unusual Senses Group") )) {
//                total = total + 10;
//            }
//
//
//        if (enhancedSense.equals("All Senses") ) {
//            total = total + (3 * enhancedperception.intValue() );
//        } else {
//            total = total + (2 * enhancedperception.intValue() );
//        }
//
//
//
//        if (enhancedSense.equals("All Senses") && arcOfPerception.equals("TRUE") ) {
//            total = total + 25;
//        } else if (arcOfPerception.equals("TRUE") ){
//            total = total + 10;
//
//        }
//
//        if (detectsense.equals("TRUE") ) {
//            total = total +2;
//        }
//        if (clairsentience.equals("TRUE") ){
//            total = total +20;
//            if (seeintime.equals("Future"))  {
//                total = total +20;
//            }
//            if (seeintime.equals("Past") ) {
//                total = total +20;
//            }
//            if (seeintime.equals("Future and Past") ) {
//                total = total +40;
//            }
//            if (dimensions.equals("TRUE") ) {
//                total = total +20;
//            }
//            if ( enhancedSense.equals("Sight Group")
//            || enhancedSense.equals("Hearing Group")
//            || enhancedSense.equals("Radio Group")
//            || enhancedSense.equals("Smell/Taste Group")
//            || enhancedSense.equals("Mental Group")
//            || enhancedSense.equals("Unusual Senses Group") ) {
//                total = total + 10;
//            }
//        }
        return total;
    }
    
    
}