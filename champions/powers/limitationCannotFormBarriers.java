/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;

import champions.Ability;
import champions.LimitationAdapter;
import champions.Target;
import champions.filters.SingleTargetFilter;
import champions.interfaces.ChampionsConstants;
import tjava.Filter;
import champions.parameters.ParameterList;


public class limitationCannotFormBarriers extends LimitationAdapter implements ChampionsConstants {
    static final long serialVersionUID = -6870520616682472305L;

    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first have is replaced by
    // "Advantage#" and only the second half is kept.
    static private Object[][] parameterArray = {
   };

    // Limitation Definition Variables
    public static String limitationName = "Cannot Form Barriers"; // The Name of the Limitation
    private static String limitationDescription = "Cannot form barriers"; // The Description of the Limitation
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability

    // Import Patterns Definitions
    private static Object[][] patterns = {
    };
    
    /** Creates new advCombatModifier */
    public limitationCannotFormBarriers() {
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
        
  
        int index = ability.addLimitationInfo(this, limitationName, parameterList);
        

      //  ability.setTargetSelf(true);

        // Update the Stored Description for this Limitation
        setDescription( getConfigSummary() );
        
        // Return True to indicate success in configuringPAD
        return true;
    }

    public double calculateMultiplier() {
        return -0.25;
    }

    public String getConfigSummary() {
        return "Cannot form barriers";
    }

    
    public Object[][] getImportPatterns() {
        return patterns;
    }

    
}