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
public class advantageNoRangePenalty extends AdvantageAdapter implements ChampionsConstants {
    static final long serialVersionUID = -6399128355211532419L;

    static private Object[][] parameterArray = {
    };
    
    // Advantage Definition Variables
    public static String advantageName = "No Range Penalty"; // The Name of the Advantage
    private static boolean affectsDC = true; // Indicates the Advantage affects the cost of damage classes
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability

    private static Object[][] patterns = {
        { "No Range Penalty:.*", null}
    };
   
    public advantageNoRangePenalty() {
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
        int index = ability.addAdvantageInfo(this, advantageName, parameterList);
        this.setAffectsDC(affectsDC);
        ability.add("Ability.ISNORANGE", "TRUE", true);
        setDescription( getConfigSummary() );
        return true;
    }

    public double calculateMultiplier() {
        return 0.5;
    }

    public String getConfigSummary() {       
        return "No Range Penalty";
    }
    

    public Object[][] getImportPatterns() {
        return patterns;
    }
}