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

public class limitationLimitedRange extends LimitationAdapter implements ChampionsConstants {
    
    static final long serialVersionUID = -6870520616782472305L;
    
    
    static private Object[][] parameterArray = {};
       
    
    public static String limitationName = "Limited Range"; // The Name of the Limitation
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    private static Object[][] patterns = {
        { "Limited Range:.*",null}
    };
    
    public limitationLimitedRange() {
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
	    return -0.25;
	}

	public String getConfigSummary() {
	    return limitationName;
	}
	
	
	 
	public Object[][] getImportPatterns() {
	    return patterns;
	}
}