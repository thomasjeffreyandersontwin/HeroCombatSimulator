/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;

import champions.Ability;
import champions.AbilityImport;
import champions.AdvantageAdapter;
import champions.interfaces.ChampionsConstants;
import champions.parameters.ParameterList;



public class advantageUsableByOthers extends AdvantageAdapter implements ChampionsConstants {
    static final long serialVersionUID = -6399128353213532419L;

    static private Object[][] parameterArray = {};
    
    // Advantage Definition Variables
    public static String advantageName = "Advantage Usable By Others"; // The Name of the Advantage
    private static boolean affectsDC = false; // Indicates the Advantage affects the cost of damage classes
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability

   // Import Patterns Definitions
    private static Object[][] patterns = {
        { advantageName +".*", null}
    };
    
    /** Creates new advCombatModifier */
    public advantageUsableByOthers() {
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
        
        int index = ability.addAdvantageInfo(this, advantageName, parameterList);
        this.setAffectsDC(affectsDC);
        
       /* String ptype;
        if ( (ptype = ability.getPType()) != null ) {
            if ( ptype.equals( "INSTANT" ) ) {
                ability.setPType("CONSTANT", true);
            }
        }
        */
        ability.setRequiresTarget(true);
        setDescription( getConfigSummary() );
        return true;
    }


    public double calculateMultiplier() {

        return .25;
    }

    public String getConfigSummary() {
        return "Continuous";

    }
    public int identifyAdvantage(AbilityImport ai, int line) {
        int index,count;
        String possibleAdvantage;
        
        possibleAdvantage = ai.getImportLine(line);
        if ( possibleAdvantage != null && (possibleAdvantage.indexOf("USABLEBYOTHER") != -1 || possibleAdvantage.indexOf("Continuous") != -1)) return 1;
        return 0;
    }
    

    public Object[][] getImportPatterns() {
        return patterns;
    }
    
     
    public void removeAdvantage() {
        // Remove the PType so it will be reset correctly by the power
        //ability.remove("Ability.PTYPE");
       // ability.clearPType();
    }

}