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


public class limitationAlwaysDirect extends LimitationAdapter implements ChampionsConstants {
    
    static final long serialVersionUID = -6870520616782472305L;
    
    static private Object[][] parameterArray = {
    };

    public static String limitationName = "Always Direct"; 
    private static boolean unique = true; 
    private static Object[][] patterns = {
        { limitationName +":.*",null}
    };
    
    public limitationAlwaysDirect() {
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