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

public class limitationLimitedClassOfPowersAvailable extends LimitationAdapter implements ChampionsConstants {
    
    static final long serialVersionUID = -6870520616782472305L;
    
    static private String[] limitationTypes = { "Slightly Limited","Limited","Very Limited"};
    static private Object[][] parameterArray = {
        {"LimitationTypes","Limitation#.LIMITATIONTYPES", String.class, "Slightly Limited", "Limited Powers Option", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", limitationTypes},
    };
    
    public static String limitationName = "Limited Class Of Powers Available"; // The Name of the Limitation
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { limitationName+":.*",null}
    };
    public limitationLimitedClassOfPowersAvailable() {
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
    	if ( ability == null ) return false;
    	int index = ability.addLimitationInfo(this, limitationName, parameterList);
	     
    	setDescription( getConfigSummary() );
    	return true;
    }
    
	public double calculateMultiplier() {
	        String limitationTypes = (String)parameterList.getParameterValue("LimitationTypes");
	       
	        if (limitationTypes.equals("Slightly Limited")){
	            return .25;
	        } 
	        if (limitationTypes.equals("Limited")){
	            return .5;
	        }
	        if (limitationTypes.equals("Very Limited")){
	            return .1;
	        }
	        return 0;
	}
	
	public String getConfigSummary() {
	    return limitationName;
	}
	public Object[][] getImportPatterns() {
    	return patterns;
	}
}