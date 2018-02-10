/*
 * powerHandToHandAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
 */

package champions.powers;

import champions.*;
import champions.interfaces.*;
import champions.exception.*;
import champions.event.*;
import champions.parameters.ParameterList;


public class powerUnidentified extends Power
implements ChampionsConstants {
    static final long serialVersionUID = 7766760941340027676L;
    
    static private Object[][] parameterArray = {
        {"Amount","Power.AMOUNT", Integer.class, new Integer(5), "Amount", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(0)},
        {"Name","Power.NAME", String.class, new String(), "Name", STRING_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED}
    };
    
    private static String powerName = "Unidentified"; // The Name of the Power
    private static String targetType = "SELF"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "PERSISTENT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "INSTANT"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = -1; // The Point cost of a single Damage Class of this power.
    private static String attackType = null; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = null; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 0; // Indicates the END Multiplier for this power
   
    private static Object[][] patterns = {
        { "Amount: ([0-9]*),.*", new Object[] { "Amount", Integer.class}},
        { "Name: \\\\((.*)\\\\): .*", new Object[] { "Amount", String.class}}
    };
    
    public powerUnidentified()  {
    }
    public Object[][] getParameterArray() {
        return parameterArray;
    }
    public String getName() {
        return powerName;
    }
    
  
    public boolean configurePAD(Ability ability, ParameterList parameterList) {
        if ( ability == null ) return false;

        setParameterList(ability,parameterList);
        Integer distance = (Integer)parameterList.getParameterValue("Amount");
        String name = (String) parameterList.getParameterValue("Name");
        parameterList.copyValues(ability);
        
        ability.addPowerInfo( this, powerName, targetType, persistenceType, activationTime);
        if ( attackType != null ) {
            ability.addAttackInfo( attackType,damageType );
            ability.add("Ability.PPDC", new Double(pointsPerDC), true);
        }
        ability.setGenerateDefaultEffects(generateDefaultDamage);
        if ( endMultiplier != 1 ) ability.setENDMultiplier(endMultiplier);
        
        ability.setPowerDescription( getConfigSummary(ability, -1));
        return true;
    }
    
    public int calculateCPCost(Ability ability) {
        ParameterList parameterList = getParameterList(ability);
        Integer amount = (Integer)parameterList.getParameterValue("Amount");
         int cost = 0;
        
        if ( amount.intValue() > 0 ) cost += amount.intValue();
        return cost;
    }
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        Integer distance = (Integer)parameterList.getParameterValue("Amount");
        String name = (String) parameterList.getParameterValue("Name");
        int total = getMovementDistance(ability);
        return "Unidentied (+" + Integer.toString( distance.intValue() ) + ")";
    }
    
    public int getAmount(Ability ability) {
        ParameterList parameterList = getParameterList(ability);
        Integer distance = (Integer)parameterList.getParameterValue("Amount");
        
        return distance.intValue();
    }
    
    public String geName(Ability ability) {
        ParameterList parameterList = getParameterList(ability);
        String name = (String)parameterList.getParameterValue("Name");
        return name;
    }
   
    public Object[][] getImportPatterns() {
        return patterns;
    }
    
}