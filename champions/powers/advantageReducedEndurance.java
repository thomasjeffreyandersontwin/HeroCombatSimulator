/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;

import champions.*;
import champions.interfaces.*;
import champions.exception.*;
import champions.parameters.ParameterList;

/**
 *
 * @author  unknown
 * @version
 *
 * To Convert from old format powers, to new format powers:
 *
 * 1) Add implements ChampionsConstants to class definition.<P>
 * 2) Copy and Fill in Advantage Definition Variables. <P>
 * 3) Move Parameter Information to parameterArray. <P>
 * 4) Delete getParameters method (unless special parameter handling is necessary.<P>
 * 5) Change configurePAD(Ability,DetailList) method to configurePAD(Ability ability, ParameterList parameterList).<P>
 * 6) Edit configurePAD method to use format specified below.<P>
 * 7) Change checkParameter method to checkParameter(Ability ability, <i>int padIndex</i>,
 * String key, Object value, Object oldValue);
 * 8) Edit getConfigSummary method to use parameterList instead of parseParameter methods.<P>
 * 9) Change all instances of parseParameter to getParameterValue.<P>
 * 10) Add getParameterArray method.<P>
 * 11) Add isUnique method.<P>
 * 12) Edit getName method to return advantageName variable.
 * 13) Change serialVersionUID by some amount.
 * 14) Add patterns array and define import patterns.<P>
 * 15) Add getImportPatterns() method.<P>
 */
public class advantageReducedEndurance extends AdvantageAdapter
implements ChampionsConstants {
    static final long serialVersionUID = -6399118355213532419L;
    
    static private String[] options = { "Half", "Zero" };
    
    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first have is replaced by
    // "Advantage#" and only the second half is kept.
    static private Object[][] parameterArray = {
        {"Level","ReducedEndurance.LEVEL", String.class, "Half", "Level", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", options},
      //  {"Persistent", "ReducedEndurance.PERSISTENT", String.class, "FALSE", "Persistent", BOOLEAN_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED}
    };
    
    // Advantage Definition Variables
    public static String advantageName = "Reduced Endurance"; // The Name of the Advantage
    private static boolean affectsDC = false; // Indicates the Advantage affects the cost of damage classes
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "Reduced END: (Zero) & (Persistent),.*", new Object[] {"Level", String.class, "Persistent", Boolean.class}},
        { "Reduced END: (.*),.*", new Object[] {"Level", String.class}},
        { "Reduced Endurance \\((.*) Endurance\\).*", new Object[] {"Level", String.class}},
        { "Reduced Endurance (Half).*",new Object[] {"Level", String.class}},
        //hd 
        { "Reduced Endurance \\((.* END);.*",new Object[] {"Level", String.class}},
        { "REDUCEDEND: (HALFEND|ZERO) .*",new Object[] {"Level", String.class}}
    };
    
    /** Creates new advCombatModifier */
    public advantageReducedEndurance() {
    }
    
    public String getName() {
        return advantageName;
    }
    
    public Object[][] getParameterArray() {
        return parameterArray;
    }
    
    public boolean isUnique() {
        return unique;
    }
    
    public boolean configurePAD(Ability ability, ParameterList parameterList) {
        // Fail immediately if ability is null
        if ( ability == null ) return false;
        
        // Read in any parameters that will be needed to configure the power or
        // Determine the validity of the power configuration.  Read the parameters
        // from the parameterList, instead of directly from the ability, since the
        // Ability isn't configured yet.
        String level = (String)parameterList.getParameterValue("Level");
      //  String persistent = (String)parameterList.getParameterValue("Persistent");
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        if ( level.equals( "0" ) ) {
            parameterList.setParameterValue("Level", "Zero");
            level = new String("Zero");
        }
        else if ( level.equals( "ZERO" ) ) {
            parameterList.setParameterValue("Level", "Zero");
            level = new String("Zero");
        }        
        else if ( level.equals( "HALFEND" ) ) {
            parameterList.setParameterValue("Level", "Half");
            level = new String("Half");
        }

        // AdvantageAreaEffect has nothing to validate.
        
        // Start to Actually Configure the Power.
        // The Add Power Info should always be executed to add information to the ability.
        // All of this information should be set in the Power Definition Variables at the
        // top of this file
        int index = ability.addAdvantageInfo(this, advantageName, parameterList);
        this.setAffectsDC(affectsDC);
        
        // Copy the configuration parameters directly into the ability.  This will
        // take the parameters stored in the parameter list and copy them into the
        // ability using the appropriate keys and values.  Make sure that these
        // Value/Pairs are unique to the PAD.  Any that are not, should be copied
        // manually.
        // Note that if the advantage is not unique, the values should always be
        // accessed via the parameterList and not directly.
        //parameterList.copyValues(ability,index);
        
        // Add Extra Value/Pairs used by the Advantage/BattleEngine
        if ( level.equals( "Half" ) ) {
            ability.setENDMultiplier(0.5, this);
        //    parameterList.setParameterValue("Persistent","FALSE");
       //     parameterList.setVisible("Persistent",false);
        }
        else {
            ability.setENDMultiplier(0, this);
            String ptype = ability.getPType();  //ability.getStringValue("Ability.PTYPE");
//            if (ptype.equals("CONSTANT") || ptype.equals("PERSISTENT") ) {
//                parameterList.setVisible("Persistent",true);
//            }
//            else {
//                parameterList.setVisible("Persistent",false);
//            }
        }
        
     //   ability.add("Ability.ORIGINALPTYPE", ability.getStringValue("Ability.PTYPE"), false );
//        if ( persistent.equals("TRUE") ) {
//            ability.add("Ability.PTYPE", "PERSISTENT", true, false);
//        }
//        else {
//            String originalPType = ability.getStringValue("Ability.ORIGINALPTYPE");
//            ability.add("Ability.PTYPE", originalPType, true, false);
//        }
        
        ability.reconfigurePower();
        // Update the Stored Description for this Limitation
        setDescription( getConfigSummary() );
        
        // Return True to indicate success in configuringPAD
        return true;
    }
    
    public double calculateMultiplier() {
        ParameterList parameterList = getParameterList();
        String level = (String)parameterList.getParameterValue("Level");
      //  String persistent = (String)parameterList.getParameterValue("Persistent");
        
        double cost = 0;
        
        if ( level.equals( "Half" ) ) {
            cost = .25;
        }
        else if ( level.equals( "Zero" ) ) {
            cost = .5;
        }
        
//        if ( persistent.equals("TRUE") ) {
//            cost += 0.5;
//        }
        
        if ( ability.getBooleanValue("Ability.ISAUTOFIRE" ) ) {
            cost *= 2;
        }
        
        return cost;
    }
    
    public String getConfigSummary() {
        ParameterList parameterList = getParameterList();
        String level = (String)parameterList.getParameterValue("Level");
        //String persistent = (String)parameterList.getParameterValue("Persistent");
        
        String s;
        if ( level.equals("Zero")) {
            s = "0 END";
        }
        else {
            s = "\u00BD END"; 
        }
        
//        if ( persistent.equals("TRUE") ){
//            s = s + ", Persistent";
//        }
        return s;
    }
    
    
//    public boolean checkParameter(String key, Object value, Object oldValue) {
//        if ( key.equals("ReducedEndurance.PERSISTENT") && value.equals("TRUE") ) {
//            ParameterList parameterList = getParameterList();
//            String level = (String)parameterList.getParameterValue("Level");
//            String ptype = ability.getStringValue("Ability.PTYPE");
//            
//            if ( ptype == null || ( ! ptype.equals("CONSTANT") && ! ptype.equals("PERSISTENT") ) ) return false;
//            if ( level.equals("Half") ) return false;
//        }
//        
//        return true;
//    }
    
    /** Removes Special Configurations from the Ability which the advantage might have added.
     *
     * RemoveAdvantage is called prior to an advantage being removed from an ability.  The
     * removeAdvantage method should remove any value pairs that it specifically added to the
     * ability.  
     *
     * The ability will take care of removing the advantages configuration, object, and parameter
     * lists under the advantage#.* value pairs.
     */
    public void removeAdvantage() {
        ability.removeENDMultiplier(this);
    }

    /** Attempt to identify Advantage
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     * @param ai AbilityImport which is currently being imported.
     * @return Value indicating likelyhood that AbilityImport is this kind of power.
     * 0 indicates there was no recognition.
     * 5 indicates probable match.
     * 10 indicates almost certain recognition.
     *
     */
    
    public int identifyAdvantage(AbilityImport ai, int line) {
        int index,count;
        String possibleAdvantage;
        
        possibleAdvantage = ai.getImportLine(line);
        if ( possibleAdvantage != null && (possibleAdvantage.indexOf("REDUCEDEND") != -1 || possibleAdvantage.indexOf("Reduced Endurance") != -1 || possibleAdvantage.indexOf("Reduced END") != -1)) return 1;
        return 0;
    }    
    
    /** Returns the patterns necessary to import the Power from CW.
     * The Object[][] returned should be in the following format:
     * patterns = Object[][] {
     * { "PATTERN" , new Object[] { "PARAMETER1", parameter1.class, "PARAMETER2", parameter2.class, ... },
     * ...
     * }
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
}