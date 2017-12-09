/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;

import champions.*;
import champions.interfaces.*;
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
public class advantageArmorPiercing extends AdvantageAdapter 
implements ChampionsConstants {
    static final long serialVersionUID = -3750155454162962388L;
    /** Creates new advCombatModifier */
    
    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first have is replaced by
    // "Advantage#" and only the second half is kept.
    static private Object[][] parameterArray = {
    {"Levels","Advantage#.LEVELS", Integer.class, new Integer(1), "Levels", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED}
    };
    
    // Advantage Definition Variables
    public static String advantageName = "Armor Piercing"; // The Name of the Advantage
    private static boolean affectsDC = true; // Indicates the Advantage affects the cost of damage classes
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
     //   { "Armor Piercing: ([0-9]*),.*", new Object[] {"Levels", Integer.class}},
     //   { "Armor Piercing \\(�([0-9]*)\\):.*", new Object[] {"Levels", Integer.class}},
        //HD
      //  { "Armor Piercing \\(x([0-9]*).*", new Object[] {"Levels", Integer.class}},
      //  { "Armor Piercing.*", null}

    };
    
    public advantageArmorPiercing() {
        
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
        Integer levels = (Integer)parameterList.getParameterValue("Levels");
       
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
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

        // Update the Stored Description for this Limitation
        setDescription( getConfigSummary() );
        
        // Return True to indicate success in configuringPAD
        return true;
    }

    public double calculateMultiplier() {
        ParameterList parameterList = getParameterList();
        Integer levels = (Integer)parameterList.getParameterValue("Levels");

        return levels.doubleValue() * 0.5;
    }

    public String getConfigSummary() {
        ParameterList parameterList = getParameterList();
        Integer levels = (Integer)parameterList.getParameterValue("Levels");
        
        return "x" + levels.toString() + " Armor Piercing(+" + Double.toString(levels.doubleValue() * 0.5) + ")";
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
        if ( possibleAdvantage != null && possibleAdvantage.indexOf( getName()  ) != -1 ) {
            return 10;
        }
        
        return 0;
    }

    public boolean checkParameter(String key, Object value, Object oldValue) {
        if ( key.equals( "ArmorPiercingLevels.LEVELS" ) ) {
            if ( ((Integer)value).intValue() < 1 ) return false;
        }
        return true;
    }

    public void prepower(BattleEvent be, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget) {
        //Integer levels = ability.getIndexedIntegerValue(index, "Advantage", "LEVELS" );
        ParameterList pl = getParameterList();
        Integer levels = (Integer)pl.getParameterValue("Levels");
        
        ActivationInfo ai = be.getActivationInfo();
        
        int tindex = ai.getTargetIndex(target, targetGroup);
        DefenseList dl = ai.getDefenseList(tindex);
        
        int dcount = dl.getDefenseCount();
        int dindex;
        int hardenedLevel;
        String description;
        
        for(dindex = 0; dindex < dcount; dindex++) {
            description = dl.getDefenseDescription(dindex);
            hardenedLevel = dl.getHardenedLevel(dindex);
            if ( hardenedLevel < levels.intValue() ) {
                dl.addAdjustmentMultiplier(description, this, 0.5);
            }
        }

       /* if ( ( dindex = effect.findIndexed( "Defense", "NAME", "Hardened" )) != -1 ) {
            Integer dlevels = effect.getIndexedIntegerValue( dindex, "Defense", "LEVELS" );
            // Return if there are too many levels of hardened
            if ( levels.intValue() > dlevels.intValue() ) return;
        } */

        // Reduce Defenses by half
   /*     Integer defense;

        if ( (defense = effect.getIntegerValue("PD.DEFENSE" )) != null ) {
            defense = new Integer(defense.intValue()/2);
            effect.add("PD.DEFENSE",  defense, true);
        }
        if ( (defense = effect.getIntegerValue("ED.DEFENSE" )) != null ) {
            defense = new Integer(defense.intValue()/2);
            effect.add("ED.DEFENSE",  defense, true);
        }
        if ( (defense = effect.getIntegerValue("rPD.DEFENSE" )) != null ) {
            defense = new Integer(defense.intValue()/2);
            effect.add("rPD.DEFENSE",  defense, true);
        }
        if ( (defense = effect.getIntegerValue("rED.DEFENSE" )) != null ) {
            defense = new Integer(defense.intValue()/2);
            effect.add("rED.DEFENSE",  defense, true);
        } */
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