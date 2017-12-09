/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;

import champions.*;
import champions.interfaces.*;
import champions.event.*;
import champions.parameters.ParameterList;

/**
 *
 * @author  unknown
 * @version
 *
 * * To Convert from old format limitation, to new format limitation:
 *
 * 1) Add implements ChampionsConstants to class definition.<P>
 * 2) Copy and Fill in Limitation Definition Variables. <P>
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
 * 12) Edit getName method to return limitationName variable.
 * 13) Add patterns array and define import patterns.<P>
 * 14) Add getImportPatterns() method.<P>
 */
public class limitationFocus extends LimitationAdapter
implements ChampionsConstants {
    
    static final long serialVersionUID = -6870520616782472305L;
    
    private static String[] types = {
        "Inobvious, Inaccessible",
        "Obvious, Inaccessible",
        "Inobvious, Accessible",
        "Obvious, Accessible"
    };
    
    private static String[] mobileOptions = {"None","Bulky","Immobile"};
    private static String[] expendableOptions = {"None","Hard to Acquire","Extremely Hard to Acquire","Dangerous to Acquire"};
    private static String[] applicabilityOptions = {"Personal","Universal"};
    private static String[] breakabilityOptions = {"Breakable","Unbreakable"};
   // private static String[] typeOptions = {"Personal","Universal"};
    
    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first have is replaced by
    // "Advantage#" and only the second half is kept.
    static private Object[][] parameterArray = {
        {"Object","Limitation#.OBJECT", String.class, "", "Focus Object", STRING_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "FIELDWIDTH", new Integer(120)},
        {"Type","Limitation#.TYPE", String.class, "Inobvious, Inaccessible", "Focus Type", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", types},
        {"Fragile","Limitation#.FRAGILE", Boolean.class, new Boolean(false), "Fragile Focus", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"Mobility","Limitation#.MOBILITY", String.class, "None", "Mobility", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", mobileOptions},
        {"Applicability","Limitation#.APPLICABILITY", String.class, "None", "Applicability", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", applicabilityOptions},
        {"Breakability","Limitation#.BREAKABILITY", String.class, "None", "Breakability", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", breakabilityOptions},
        {"Expendable","Limitation#.EXPENDABLE", String.class, "Not Expendable", "Expendability", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", expendableOptions},
       // {"ImportType","Limitation#.IMPORTTYPE", String.class, "None", "Focus Type", STRING_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", types},
    };
    
    // Limitation Definition Variables
    public static String limitationName = "Focus"; // The Name of the Limitation
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] startPatterns = {
        //        { "Focus: (.*),.*", null},
        { "(..F):.*", new Object[] { "Type", String.class}},
        { "Focus: (.*), .*", new Object[] { "Type", String.class}},
        { "Focus \\((.*)\\).*", new Object[] { "Type", String.class}},
        { "Focus \\((.*)\\): (.*),.*", new Object[] { "Object", String.class,  "Type", String.class}},
        //hd
        { "(..F) \\(.*", new Object[] { "Type", String.class}},
        
    };
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        //        { "Focus: (.*),.*", null},
        //{ "(..F):.*", new Object[] { "Type", String.class}},
        { "Focus Applicability: (.*)", new Object[] { "Applicability", String.class}},
        { "Focus Breakability: (.*)", new Object[] { "Breakability", String.class}},
        { "Focus Expendability: (.*),.*", new Object[] { "Expendable", String.class}},
        { "Focus Mobility: (.*),.*", new Object[] { "Mobility", String.class}},
      //  { "Focus Type: (.*),.*", null},
        { "(Fragile Focus:).*", new Object[] { "Fragile", Boolean.class}}
    };
    
    
    public String getName() {
        return limitationName;
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
        String type = (String)parameterList.getParameterValue("Type");
        String mobile = (String)parameterList.getParameterValue("Mobility");
        boolean fragile = (Boolean)parameterList.getParameterValue("Fragile");
        String expendable = (String)parameterList.getParameterValue("Expendable");
        String object = (String)parameterList.getParameterValue("Object");
        String breakability = (String)parameterList.getParameterValue("Breakability");
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        // !!!Limitation has nothing to validate!!!
        if (type.equals("OAF") || type.equals("Obvious Accessible")){
            type = "Obvious, Accessible";
            parameterList.setParameterValue("Type", type);
        }
        else if (type.equals("OIF") || type.equals("Obvious Inaccessible")){
            type = "Obvious, Inaccessible";
            parameterList.setParameterValue("Type", type);
        }
        else if (type.equals("IAF") || type.equals("Inobvious Accessible")){
            type = "Inobvious, Accessible";
            parameterList.setParameterValue("Type", type);
        }
        else if (type.equals("IIF") || type.equals("Inobvious Inaccessible")){
            type = "Inobvious, Inaccessible";
            parameterList.setParameterValue("Type",type);
        }
        
        if (mobile.equals("BULKY")){
            parameterList.setParameterValue("Mobility", "Bulky");
        }
        else if (mobile.equals("IMMOBILE")){
            parameterList.setParameterValue("Mobility", "Immobile");
        }
        
        if (breakability.equals("FRAGILE")){
            parameterList.setParameterValue("Fragile", new Boolean(true));
        }
        else if (breakability.equals("DURABLE")){
            parameterList.setParameterValue("Breakability", "Breakable");
        }
        else if (breakability.equals("UNBREAKABLE")){
            parameterList.setParameterValue("Breakability", "Unbreakable");
        }
        
        if (expendable.equals("DIFFICULT")){
            parameterList.setParameterValue("Expendable", "Hard to Acquire");
        }
        else if (expendable.equals("VERYDIFFICULT")){
            parameterList.setParameterValue("Expendable", "Extremely Hard to Acquire");
        }
        else if (expendable.equals("EXTREMELYDIFFICULT")){
            parameterList.setParameterValue("Expendable", "Dangerous to Acquire");
        }
        
        // Start to Actually Configure the Power.
        // The Add Power Info should always be executed to add information to the ability.
        // All of this information should be set in the Power Definition Variables at the
        // top of this file
        int index = ability.addLimitationInfo(this, limitationName, parameterList);
        
        
        // Copy the configuration parameters directly into the ability.  This will
        // take the parameters stored in the parameter list and copy them into the
        // ability using the appropriate keys and values.  Make sure that these
        // Value/Pairs are unique to the PAD.  Any that are not, should be copied
        // manually.
        // Note that if the advantage is not unique, the values should always be
        // accessed via the parameterList and not directly.
        //parameterList.copyValues(ability,index);
        
        // Add Extra Value/Pairs used by the Limitation/BattleEngine
        ability.reconfigurePower();
        
        // Update the Stored Description for this Limitation
        setDescription( getConfigSummary() );
        
        // Return True to indicate success in configuringPAD
        return true;
    }
    
    
    
    public double calculateMultiplier() {
        double total = 0;
        
        ParameterList parameterList = getParameterList();
        String type = (String)parameterList.getParameterValue("Type");
        String mobile = (String)parameterList.getParameterValue("Mobility");
        boolean fragile = (Boolean)parameterList.getParameterValue("Fragile");
        String expendable = (String)parameterList.getParameterValue("Expendable");
        String object = (String)parameterList.getParameterValue("Object");
        
        if ( type.equals( "Inobvious, Inaccessible" ) ) total += -0.25;
        if ( type.equals( "Inobvious, Accessible" ) ) total += -0.50;
        if ( type.equals( "Obvious, Inaccessible" ) ) total += -0.50;
        if ( type.equals( "Obvious, Accessible" ) ) total += -1;
        
        if ( mobile.equals( "Bulky" ) ) total += -0.50;
        if ( mobile.equals( "Immobile" ) ) total += -1;
        if ( fragile ) total += -0.25;
        if ( expendable.equals( "Hard to Acquire" ) ) total += -0.25;
        if ( expendable.equals( "Extremely Hard to Acquire" ) ) total += -0.50;
        if ( expendable.equals( "Dangerous to Acquire" ) ) total += -1;
        
        return total;
    }
    
    public String getConfigSummary() {
        ParameterList parameterList = getParameterList();
        String type = (String)parameterList.getParameterValue("Type");
        String mobile = (String)parameterList.getParameterValue("Mobility");
        boolean fragile = (Boolean)parameterList.getParameterValue("Fragile");
        String expendable = (String)parameterList.getParameterValue("Expendable");
        String object = (String)parameterList.getParameterValue("Object");
        
        StringBuffer s = new StringBuffer();
        s.append( toShortType(type) );
        s.append( "(" );
        s.append(Double.toString(calculateMultiplier()));
        s.append(")");
        
        if ( ! mobile.equals( "None" ) ) s.append( ", " + mobile);
        if ( fragile ) s.append( ", fragile");
        if ( !expendable.equals( "Not Expendable" ) ) s.append( ", " + expendable);
        
        if ( object != null && object.equals("") == false) {
            s.append(" \"");
            s.append(object);
            s.append("\"");
        }
        return s.toString();
    }
    
    
    
    public String toShortType(String type) {
        if ( type.equals( "Inobvious, Inaccessible" ) ) return "IIF";
        if ( type.equals( "Inobvious, Accessible" ) ) return "IAF";
        if ( type.equals( "Obvious, Inaccessible" ) ) return "OIF";
        if ( type.equals( "Obvious, Accessible" ) ) return "OAF";
        return null;
    }
    
    /** Attempt to Import Advantage setting from AbilityImport information.
     * @param ability Ability into which to import.
     * @param ai AbilityImport from which to grab import information.
     */
/*    public void importLimitation(Ability ability, int padIndex, AbilityImport ai, int lineIndex) {
        int index, count;
        count = ai.getImportLineCount();
        String line;
        boolean found;
 
        ParameterList parameterList = this.getParameterList();
 
        // Search for Name, Damage Die
        found = false;
        for(index=lineIndex;index<count;index++) {
            line = ai.getImportLine(index);
            if ( ChampionsMatcher.matches( "Focus: *(.*) (.*),.*", line ) ) {
                // Most Constrained Form
                String type1 = ChampionsMatcher.getMatchedGroup(1);
                String type2 = ChampionsMatcher.getMatchedGroup(2);
                parameterList.setParameterValue( "Type", type1 + ", " + type2);
                ai.setLineUsed(index, this);
                found = true;
            }
 
            if ( found == true ) break;
        }
    }
 */
    public int identifyLimitation(AbilityImport ai,int line) {
        int pindex,pcount;
        String possibleLimitation, pattern;
        Object[] parameters;
        
        possibleLimitation = ai.getImportLine(line);
        if ( possibleLimitation != null ) {
            // First search the startFocusPattern lines
            pcount = startPatterns.length;
            for (pindex=0;pindex<pcount;pindex++) {
                pattern = (String)startPatterns[pindex][0];
                parameters = (Object[])startPatterns[pindex][1];
                
                if ( ChampionsMatcher.matches( pattern, possibleLimitation ) == true ) {
                    // The line matched a start line
                    return 10;
                }
            }
        }
        return 0;
    }
    
    /** Attempt to Import Advantage setting from AbilityImport information.
     * @param limitationIndex Index of the new Limitation.
     * @param line Line index which contained the first recognized line for this ability.
     * @param ability Ability into which to import.
     * @param ai AbilityImport from which to grab import information.
     */
    public void importLimitation(Ability ability, int limitationIndex, AbilityImport ai, int notUsed) {
        int index, count;
        int pindex, pcount;
        String line;
        boolean found, done;
        String pattern;
        Object[] parameters;
        int startLine = -1;
        
        ParameterList parameterList = this.getParameterList();
        
        // line is the starting line.  Basically we want to match all patterns until we find a new focus start line.
        done = false;
        
        count = ai.getImportLineCount();
        for(index = 0; index < count; index++) {
            if ( ai.isLineUsed(index) == false ) {
                line = ai.getImportLine(index);
                
                found = false;
                
                // First search the startFocusPattern lines
                pcount = startPatterns.length;
                for (pindex=0;pindex<pcount;pindex++) {
                    pattern = (String)startPatterns[pindex][0];
                    parameters = (Object[])startPatterns[pindex][1];
                    
                    if ( ChampionsMatcher.matches( pattern, line ) == true ) {
                        // The line matched a start line
                        if ( startLine == -1) {
                            // This is the focus start line, so import it
                            startLine = index;
                            found = true;
                            ai.importParametersForLine(pattern, parameters, ability, index, this, parameterList);
                            break;
                        }
                        else {
                            // This was not the original start line, so it must be the start of a new focus
                            // limitation.
                            done = true;
                            break;
                        }
                    }
                }
                
                // Break out of the loop completely if a new start line was found
                if ( done ) break;
                
                if ( ! found ) {
                    // If the line was not found, search the other patterns.
                    pcount = patterns.length;
                    for (pindex=0;pindex<pcount;pindex++) {
                        pattern = (String)patterns[pindex][0];
                        parameters = (Object[])patterns[pindex][1];
                        
                        if ( ai.importParametersForLine(pattern, parameters, ability, index, this, parameterList) == true ) {
                            // It was a match
                            found = true;
                            break;
                        }
                    }
                }
                
                // Do some clean up
                if ( found ) {
                    // Line was found and processed, so mark it used
                    ai.setLineUsed(index, this);
                }
            }
        }
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