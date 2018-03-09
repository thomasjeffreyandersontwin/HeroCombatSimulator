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

    static private Object[][] parameterArray = {
    		{"UsableOption","Power.USABLEOPTION", String.class, "Usable By Other", "UsableOption", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", 
    			new String[] {"Usable By Other","Usable Simultaneously", "Usable As Attack"}},
    		{"Targets","Power.TARGETS", String.class, 1, "Targets", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
            
            
    };
    
    // Advantage Definition Variables
    public static String advantageName = "Usable By Others"; // The Name of the Advantage
    private static boolean affectsDC = false; // Indicates the Advantage affects the cost of damage classes
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability

   // Import Patterns Definitions
    private static Object[][] patterns = {
        { advantageName +".*", null},
        {"Usable By Options: (.*)", new Object[]{"UsableOption", String.class}},
        { "([0-9]*d6) *.*", new Object[] { "Targets", String.class}},
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
        
        String option = (String)parameterList.getParameterValue("UsableOption");
        if(option.equals("Usable By Other"))
        	ability.setAType("SINGLE",true);
        
        if(option.equals("Usable Simultaneously")){
        	Integer targets = (Integer)parameterList.getParameterValue("Targets");
        	ability.setAType("SINGLE",true);
        	ability.add("Ability.ISAUTOFIRE", "TRUE" , true );
            ability.add("Ability.MAXSHOTS",  targets , true);
        	//parameterList.addIntegerParameter("MaxShots", "MaxShots", null, targets);
  
        	
        }
        
        ability.setActivationTime("ATTACK",true);
        
        ability.setRequiresTarget(true);
        setDescription( getConfigSummary() );
        return true;
    }


    public double calculateMultiplier() {
    	String option = (String)parameterList.getParameterValue("UsableOption");
    	if(option.equals("Usable As Attack")){
        	Integer targets = (Integer)parameterList.getParameterValue("Targets");
        	double levels = Math.log(targets)/Math.log(2);
        	return levels *.25 +1;
        }
        if(option.equals("Usable Simultaneously")){
        	Integer targets = (Integer)parameterList.getParameterValue("Targets");
        	double levels = Math.log(targets)/Math.log(2);
        	return levels *.25 +.5;
        }
        else {
        	return .25;
        }
        	
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