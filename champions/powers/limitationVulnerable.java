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

public class limitationVulnerable extends LimitationAdapter implements ChampionsConstants {
    
    static final long serialVersionUID = -6870520616782472305L;
    
    
    
    static public String[] attackType = {"Very Common","Common","Uncommon"};
       
    
    public static String limitationName = "Vulnerable"; 
    private static boolean unique = true; 
    
    private static Object[][] patterns = {
        { limitationName + ".*",null}
    };
    
    static private Object[][] parameterArray = {
            {"attackType","Limitation#.ATTACKTYPE", String.class, "Very Common", "Attack Type", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", attackType},
            {"effect", "Limitation#.Effect", String.class, "", "Effect", STRING_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED },       

    };
    
    public limitationVulnerable() {
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
    private String vulnerability;
    public String getVulnerability() {
    	return vulnerability;
    }
    public void setVulernability(String v){
    	vulnerability =v;
    }
    
    public boolean configurePAD(Ability ability, ParameterList parameterList) {
        if ( ability == null ) return false;
        int index = ability.addLimitationInfo(this, limitationName, parameterList);
        setDescription( getConfigSummary() );
        return true;
    }

    public String getConfigSummary() {
        ParameterList parameterList = getParameterList();
        String v = (String)parameterList.getParameterValue("effect");
        
        return limitationName +"(" + v + ")";
    }
    
    

	public double calculateMultiplier() {
        String attackType = (String)parameterList.getParameterValue("attackType");
       
        if (attackType.equals("Uncommon")){
            return .25;
        } 
        if (attackType.equals("Common")){
            return .5;
        }
        if (attackType.equals("Very Common")){
            return 1;
        }
        return 0;
}


	
	 
	public Object[][] getImportPatterns() {
	    return patterns;
	}
}