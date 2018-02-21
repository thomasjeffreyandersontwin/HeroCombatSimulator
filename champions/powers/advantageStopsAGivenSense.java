/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;

import champions.Ability;
import champions.AbilityImport;
import champions.AdvantageAdapter;
import champions.Sense;
import champions.interfaces.ChampionsConstants;
import champions.parameters.ParameterList;


public class advantageStopsAGivenSense extends AdvantageAdapter
implements ChampionsConstants {
    static final long serialVersionUID = -6399128355213532412L;
    

 
    static private Object[][] parameterArray = {
            {"Sense","Sense*.SENSE", Sense.class, null, "Sense", LIST_PARAMETER, VISIBLE, ENABLED, REQUIRED}
    };
    
    // Advantage Definition Variables
    public static String advantageName = "Stops a given sense"; // The Name of the Advantage
    private static boolean affectsDC = true; // Indicates the Advantage affects the cost of damage classes
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "Targeting Sense Group \\((.*)\\).*", new Object[] {"Sense", String.class }},
        //hd
        { "(.*) .* ([0-9]*d6),.*", new Object[] { "Sense", String.class,"DamageDie", String.class }},
        
    };
    
    //Area Of Effect (Cone): 9" Long, +1
    public advantageStopsAGivenSense() {
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
        
        setParameterList(parameterList);
        
        int index = ability.addAdvantageInfo(this, advantageName, parameterList);
        this.setAffectsDC(affectsDC);
        
        setDescription( getConfigSummary() );
        return true;
    }
    
    public Sense getSense()
    {
    	return (Sense)parameterList.getParameterValue("Sense");
    }
    
    public String getConfigSummary() {
         
        StringBuffer sb = new StringBuffer();
        sb.append( advantageName + "(" + getSense().getSenseName() + ")" );      
        return sb.toString();
    }
    
    public double calculateMultiplier() {
       return 0;
    }
  
    public int identifyAdvantage(AbilityImport ai, int line) {
        int index,count;
        String possibleAdvantage;
        
        possibleAdvantage = ai.getImportLine(line);
        if ( possibleAdvantage != null && possibleAdvantage.indexOf( "AOE"  ) != -1 ) {
            return 10;
        }
        if ( possibleAdvantage != null && possibleAdvantage.indexOf( getName()  ) != -1 ) {
            return 10;
        }
        if ( possibleAdvantage != null && possibleAdvantage.indexOf( "Area Of Effect" ) != -1 ) {
            return 10;
        }
        
        return 0;
    }
    
    public Object[][] getImportPatterns() {
        return patterns;
    }
    
    
}