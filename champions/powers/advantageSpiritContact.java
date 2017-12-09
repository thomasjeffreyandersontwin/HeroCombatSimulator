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
 */
public class advantageSpiritContact extends AdvantageAdapter
implements ChampionsConstants {
    static final long serialVersionUID = -6399128355213332419L;
    /** Creates new advCombatModifier */
    //static private String[] typeOptions = { "+\u00BC", "+\u00BD", "+\u00BE", "+1", "+1\u00BC", "+1\u00BD", "+1\u00BE", "+2", "+2\u00BC", "+2\u00BD", "+2\u00BE", "+3" };
    
    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first have is replaced by
    // "Advantage#" and only the second half is kept.
    static private Object[][] parameterArray = {
        {"Multiplier","Advantage#.MULTIPLIER", Double.class, 1.0, "Multiplier", DOUBLE_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
    };
    
    // Advantage Definition Variables
    public static String advantageName = "Spirit Contact"; // The Name of the Advantage
    private static boolean affectsDC = true; // Indicates the Advantage affects the cost of damage classes
    private static boolean unique = false; // Indicates whether multiple copies can be added to ability
    
    public advantageSpiritContact() {
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
        double multiplier = (Double)parameterList.getParameterValue("Multiplier");
        //String description = (String)parameterList.getParameterValue("Description");
        //boolean  affectsDCparam = (Boolean)parameterList.getParameterValue("AffectsDC");
        
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
        //this.setAffectsDC(affectsDCparam);
        
        // Update the Stored Description for this Limitation
        setDescription( getConfigSummary() );
        
        // Return True to indicate success in configuringPAD
        return true;
        
    }
    
    
    public String getConfigSummary() {
        return "Spirit Contact";

    }
    
    public double calculateMultiplier() {
        ParameterList parameterList = getParameterList();
        //String multiplier = (String)parameterList.getParameterValue("Multiplier");
        //String description = (String)parameterList.getParameterValue("Description");
        //boolean  affectsDCparam = (Boolean)parameterList.getParameterValue("AffectsDC");
        double cost = (Double)parameterList.getParameterValue("Multiplier");
        
//        int i;
//        for (i = 0;i< typeOptions.length;i++) {
//            if ( multiplier.equals(typeOptions[i] )) break;
//        }
//        
//        cost = (i+1) * .25;
        
        return cost;
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
//        int index,count;
//        String possibleAdvantage;
//        
//        possibleAdvantage = ai.getImportLine(line);
//        System.out.println(possibleAdvantage);
//        //remove the space in " -" for compatibility for HD.  Very week expression
//        if ( possibleAdvantage != null && possibleAdvantage.indexOf("+") != -1) return 1;
        
        return 0;
    }
    
    /** Attempt to Import Advantage setting from AbilityImport information.
     * @param ability Ability into which to import.
     * @param ai AbilityImport from which to grab import information.
     */
//    public void importAdvantage(Ability ability, int padIndex, AbilityImport ai, int lineIndex) {
//        int index, count;
//        count = ai.getImportLineCount();
//        String line;
//        boolean found;
//        
//        ParameterList parameterList = this.getParameterList();
//        
//        // Search for Name, Damage Die
//        found = false;
//        for(index=lineIndex;index<count;index++) {
//            line = ai.getImportLine(index);
//            
//            if ( ChampionsMatcher.matches( "(.*) (\\+.*)", line ) || ChampionsMatcher.matches( "(.*), (\\+.*)", line ) || ChampionsMatcher.matches( "(.*): (\\+.*)", line ) || ChampionsMatcher.matches( "(.*) \\((\\+.*)\\)", line ) ) {
//                // Most Constrained Form
//                String name = ChampionsMatcher.getMatchedGroup(1);
//                String size = ChampionsMatcher.getMatchedGroup(2);
//                
//                if (size.endsWith("1/4")) {
//                    size = new String(size.replaceFirst("1/4","\u00BC"));
//                }
//                else if (size.endsWith("1/2")) {
//                    size = new String(size.replaceFirst("1/2","\u00BD"));
//                } 
//                   
//                 else if (size.endsWith("3/4")) {
//                    size = new String(size.replaceFirst("3/4","\u00BE"));
//                } 
//                if (size.endsWith(" 1/4")) {
//                    size = new String(size.replaceFirst(" 1/4","\u00BC"));
//                }
//                else if (size.endsWith(" 1/2")) {
//                    size = new String(size.replaceFirst(" 1/2","\u00BD"));
//                } 
//                   
//                 else if (size.endsWith(" 3/4")) {
//                    size = new String(size.replaceFirst(" 3/4","\u00BE"));
//                }                    
//                    parameterList.setParameterValue( "Description", name);
//                
//                double value = ChampionsUtilities.getDoubleValue(size);
//                int position = (int) Math.round(value / 0.25 - 1);
//                if ( position >= 0 && position < typeOptions.length ) {
//                    parameterList.setParameterValue( "Multiplier", typeOptions[position] );
//                    
//                    ai.setLineUsed(index, this);
//                }
//                found = true;
//            }
//            
//            if ( found == true ) break;
//        }
//    }
}