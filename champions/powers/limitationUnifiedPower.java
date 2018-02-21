/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;

import java.util.ArrayList;

import champions.*;
import champions.interfaces.*;
import champions.event.*;
import champions.parameters.ParameterList;

public class limitationUnifiedPower extends LimitationAdapter implements ChampionsConstants {
    
    static final long serialVersionUID = -6870520616782472305L;
    
    
    
    
       
    
    public static String limitationName = "Unified Power"; 
    private static boolean unique = true; 
    
    private static Object[][] patterns = {
        { limitationName + ".*",null}
    };
    
    static private Object[][] parameterArray = {
            {"powerSource", "Limitation#.PowerSource", String.class, "", "powerSource", STRING_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED },       
        
    };
    
    public limitationUnifiedPower() {
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
        Target target = ability.getSource();
        String sourcePower = (String)parameterList.getParameterValue("powerSource");
        
        if(target!=null && sourcePower!="")
        {
        	ArrayList<Ability> unifiedPowers  = (ArrayList<Ability>) target.getValue("Unified." + sourcePower);
        
	        if(unifiedPowers==null)
	        {
	        	unifiedPowers = new ArrayList<Ability>();
	        	target.add("Unified." + sourcePower, unifiedPowers);
	        }
	        if(unifiedPowers.contains(ability)==false)
	        {
	        	
	        unifiedPowers.add(ability);
	        }
        }
        parameterList.setParameterValue("powerSource", sourcePower);
        
        return true;
    }

    
    

	public double calculateMultiplier() {
       return .25;
     
	}
	
	 public String getConfigSummary() {
	        ParameterList parameterList = getParameterList();
	        String source = (String)parameterList.getParameterValue("powerSource");
	        
	        return "Unified Power(" + source + ")";
	    }
	    


	
	 
	public Object[][] getImportPatterns() {
	    return patterns;
	}
}