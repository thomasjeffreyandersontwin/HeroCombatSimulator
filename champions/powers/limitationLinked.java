/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;

import champions.Ability;
import champions.AbilityImport;
import champions.AbilityListModel;
import champions.LimitationAdapter;
import champions.Target;
import champions.interfaces.ChampionsConstants;
import champions.parameters.ListParameter;
import champions.parameters.ParameterList;
import java.util.List;


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
public class limitationLinked extends LimitationAdapter implements ChampionsConstants {
    
    static final long serialVersionUID = -6870520616782472305L;
    
    
    
    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first have is replaced by
    // "Advantage#" and only the second half is kept.
    static private Object[][] parameterArray = {
        {"LinkedToAbility","LinkedToAbility*.ABILITY", Ability.class, null, "Power linked to", LIST_PARAMETER, VISIBLE, ENABLED, REQUIRED},
        {"ConstantOrUsedMost","Limitation#.CONSTANTORUSEDMOST", Boolean.class, new Boolean(false), "Greater Power is Constant or Used Most of the Time", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"OnlyWhenGreaterAtFull","Limitation#.ONLYWHENGREATERATFULL", Boolean.class, new Boolean(false), "Lesser Power can only be used when character uses greater Power at full value", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"NonProportional","Limitation#.NONPROPORTIONAL", Boolean.class, new Boolean(false), "Lesser Power need not be used proportionally to Power with which it is Linked", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"AnyPhase","Limitation#.ANYPHASE", Boolean.class, new Boolean(false), "Lesser Instant Power can be used in any Phase in which greater Constant Power is in use", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"PartOfCompound", "Limitation#.PARTOFCOMPOUND", Boolean.class, new Boolean(false), "Part of Compound power", BOOLEAN_PARAMETER, HIDDEN, DISABLED, NOTREQUIRED},
        
        
        
    };
    
    // Limitation Definition Variables
    public static String limitationName = "Linked"; // The Name of the Limitation
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "Linked .*",null},
        { "Linked:.*",null},
        { "Links to:.*", null},
        
    };
    
    /** Creates new advCombatModifier */
    public limitationLinked() {
    }
    
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
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        // !!!Limitation has nothing to validate!!!
        
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
        
        // Update the Stored Description for this Limitation
        setDescription( getConfigSummary() );
        
        boolean partOfCompound = ability.getBooleanValue("Ability.PARTOFCOMPOUNDPOWER");
        
        if ( partOfCompound ) {
            ability.setLinked(false);
            parameterList.setVisible("LinkedToAbility", false);
            parameterList.setVisible("ConstantOrUsedMost", false);
            parameterList.setVisible("OnlyWhenGreaterAtFull", false);
            parameterList.setVisible("NonProportional", false);
            parameterList.setVisible("AnyPhase", false);
            
            parameterList.setVisible("PartOfCompound", true);
            
            parameterList.setParameterValue("PartOfCompound", true);
        } else {
            ability.setLinked(true);
            
            parameterList.setVisible("LinkedToAbility", true);
            parameterList.setVisible("ConstantOrUsedMost", true);
            parameterList.setVisible("OnlyWhenGreaterAtFull", true);
            parameterList.setVisible("NonProportional", true);
            parameterList.setVisible("AnyPhase", true);
            
            parameterList.setVisible("PartOfCompound", false);
            
            parameterList.setParameterValue("PartOfCompound", false);
            
            
            // Run through the linked to ability list and adjust the LinkedAbility
            // indexes as necessary...
            List linkedToList = parameterList.getIndexedParameterValues("LinkedToAbility");
            
            int size = ability.getLinkedAbilityCount();
            for(int i = 0; i < size; i++) {
                String abilityName = ability.getLinkedAbilityName(i);
                
                
                if ( linkedToList.contains(abilityName) == false ) {
                    ability.removeLinkedAbility(abilityName);
                    i--;
                    size--;
                }
            }
            
            // Now add any that aren't in the list...
            size = linkedToList.size();
            for(int i = 0; i < size; i++) {
                Ability a  =(Ability) linkedToList.get(i);
                ability.addLinkedAbility(a.getName(), 0 );
                if (ability.isPersistent()) {
                    if (a.isConstant()) {
                        ability.setConstant();
                    } else if (a.isInstant()) {
                        ability.setInstant(true);
                    }
                }
            }
        }
        
        // Return True to indicate success in configuringPAD
        return true;
    }
    
    
    
    public double calculateMultiplier() {
        int size = ability.getLinkedAbilityCount();
        ParameterList parameterList = getParameterList();
        boolean constantorusedmost = (Boolean)parameterList.getParameterValue("ConstantOrUsedMost");
        boolean onlywhengreateratfull = (Boolean)parameterList.getParameterValue("OnlyWhenGreaterAtFull");
        boolean nonproportional = (Boolean)parameterList.getParameterValue("NonProportional");
        boolean anyphase = (Boolean)parameterList.getParameterValue("AnyPhase");
        //size = parameterList.getIndexedSize("LinkedToAbility");
        List linkedToList = parameterList.getIndexedParameterValues("LinkedToAbility");
        boolean partOfCompound = parameterList.getParameterBooleanValue("PartOfCompound");
        
        if ( partOfCompound ) {
            return -0.5;
        }
        
        double value = 0.0;
        for(int i = 0; i < linkedToList.size(); i++) {
            Ability a  =(Ability) linkedToList.get(i);
            if (ability.getAPCost() > a.getAPCost()){
                value -=0.25;
                break;
            } else {
                value -=0.5;
                break;
            }
        }
        if (onlywhengreateratfull) {
            value -= 0.25;
        }
        if (constantorusedmost) {
            value +=0.25;
        }
        if (nonproportional) {
            value +=0.25;
        }
        if (anyphase) {
            value +=0.25;
        }
        return value;
    }
    
    public String getConfigSummary() {
        ParameterList parameterList = getParameterList();
        
        
        StringBuffer sb = new StringBuffer();
        
        if ( parameterList.getParameterBooleanValue("PartOfCompound")) {
            sb.append("Linked");
        }
        else {
            sb.append("Linked to ");

            List linkedToList = parameterList.getIndexedParameterValues("LinkedToAbility");
            int size = linkedToList.size();
            for(int i = 0; i < size; i++) {
                Ability a  =(Ability) linkedToList.get(i);
                sb.append(a.getName());
                if ( i < size-1 ) sb.append(", ");
            }
        }
        
        return sb.toString();
    }
    
    public int identifyLimitation(AbilityImport ai,int line) {
        int index,count;
        String possibleLimitation;
        
        possibleLimitation = ai.getImportLine(line);
        if ( possibleLimitation != null && possibleLimitation.indexOf( "Linked" ) != -1 ) {
            return 10;
        } else return 0;
        
    }
    
// Special Handling is necessary to configure the ability.
    public ParameterList getParameterList() {
        ParameterList pl = super.getParameterList();
        ListParameter param = (ListParameter)pl.getParameter("LinkedToAbility");
        if ( param.getModel() == null) {
            param.setModel( new AbilityListModel((ability == null || ability.getSource() == null) ? null : ability.getSource().getAbilityList() ) );
        }
        return pl;
    }
    
    public void abilitySourceSet(Ability ability, Target oldSource, Target newSource) {
        ParameterList pl = getParameterList();
        ListParameter param = (ListParameter)pl.getParameter("LinkedToAbility");
        param.setModel( new AbilityListModel((newSource == null ? null : newSource.getAbilityList() )) );
    }
    
    
    /** Executed for all PADs just prior to an ability being saved.
     * All clean up should be done at this point.
     */
//    public void prepareToSave() {
//        // Make sure you clean up the LinkedToAbility model, since it will contain pointers to the world and
//        // will cause havoc when reloaded.
//        ParameterList pl = getParameterList();
//        if ( pl.getParameterOption("LinkedToAbility","MODEL") != null) {
//            pl.setParameterOption("LinkedToAbility","MODEL", null);
//        }
//    }
    
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
    
    public void importLimitation(Ability ability, int limitationIndex, AbilityImport ai, int startLine) {
        // First Do the standard import.  This will parse the RegExp andpopulate
//        super.importLimitation(ability,limitationIndex, ai, startLine);
//
//        ParameterList parameterList = getParameterList();
//
//        boolean found = false;
//
//        String stringValue;
//        String line;
//
//        for(int lineIndex = startLine; lineIndex < ai.getImportLineCount(); lineIndex++) {
//            line = ai.getImportLine(lineIndex);
//            if ( ChampionsMatcher.matches( "Links to: (.*)", line ) ) {
//                stringValue = ChampionsMatcher.getMatchedGroup(1);
//
//                int cindex = parameterList.createIndexed("LinkedToAbility", "NAME", stringValue, false);
//                parameterList.addIndexed(cindex, "LinkedToAbility", "LINE", new Integer(lineIndex), true, false );
//
//                ai.setLineUsed(lineIndex, this);
//
//                break;
//            }
//        }
        
    }
    
    /* Finishes Importing Ability.
     *
     * This method is called after the character has been completely built, with all abilities
     * that are going to be added already added.  This method can be used to finalize any necessary
     * ability changes, such as translating from Strings to actual Ability objects.
     *
     * This method should return true if it wants the configurePAD to be run once it is done
     * finalizing the method.
     */
    public boolean finalizeImport(Ability ability, int limitationIndex, AbilityImport ai) {
//        ParameterList parameterList = getParameterList();
//        Target source = ability.getSource();
//        boolean set = false;
//
//        int count = parameterList.getIndexedSize("LinkedToAbility");
//        for ( int index = 0; index < count; index++) {
//            Ability a = (Ability) parameterList.getIndexedValue(index, "LinkedToAbility", "ABILITY");
//            String name = parameterList.getIndexedStringValue(index, "LinkedToAbility", "NAME");
//
//            if ( a == null && name != null ) {
//                // Here is a name with no associated ability...see if you can find an ability...
//                a = source.getAbility(name);
//                if ( a != null ) {
//                    parameterList.addIndexed(index, "LinkedToAbility", "ABILITY", a, true, false);
//                    parameterList.removeIndexed(index, "LinkedToAbility", "NAME", false);
//                    parameterList.removeIndexed(index, "LinkedToAbility", "LINE", false);
//                } else {
//                    a = Battle.getDefaultAbilities().getAbility(name, true);
//                    if ( a != null ) {
//                        parameterList.addIndexed(index, "LinkedToAbility", "ABILITY", a, true, false);
//                        parameterList.removeIndexed(index, "LinkedToAbility", "NAME", false);
//                        parameterList.removeIndexed(index, "LinkedToAbility", "LINE", false);
//                    } else {
//                        Integer line = parameterList.getIndexedIntegerValue(index, "LinkedToAbility", "LINE");
//                        ai.setLineUnused(line.intValue());
//                        parameterList.removeAllIndexed(index, "LinkedToAbility", false);
//                        index--;
//                    }
//
//                }
//            }
//        }
//
//        count = parameterList.getIndexedSize("LinkedToAbility");
//        parameterList.setParameterOption("LinkedToAbility", "SET", (count > 0) ? "TRUE" : "FALSE" );
//
        return true;
    }
    
    public void removeLimitation() {
        // Remove the limitation linked and all of the linked abilities...
        int i = ability.getLinkedAbilityCount()-1;
        for(; i>= 0; i--) {
            ability.removeLinkedAbility(i);
        }
        
        ability.setLinked(false);
    }
}