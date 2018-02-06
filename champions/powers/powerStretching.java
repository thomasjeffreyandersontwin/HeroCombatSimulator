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

 
public class powerStretching extends Power
implements ChampionsConstants {
    static final long serialVersionUID = 7766760941340027676L;
    
    static private Object[][] parameterArray = {
        {"Distance","Power.DISTANCE", Integer.class, new Integer(5), "Stretching Distance", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(0)},
    };
    
    // Power Definition Variables
    private static String powerName = "Stretching"; // The Name of the Power
    private static String targetType = "SELF"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "PERSISTENT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "INSTANT"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = -1; // The Point cost of a single Damage Class of this power.
    private static String attackType = null; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = null; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 0; // Indicates the END Multiplier for this power
   
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "Distance: ([0-9]*),.*", new Object[] { "Distance", Integer.class}},
    };
    
    /** Creates new powerHandToHandAttack */
    public powerStretching()  {
    }
    
    /* Returns an array which can be used to create the parameterList.
     */
    public Object[][] getParameterArray() {
        return parameterArray;
    }
    
    /** Get the English name of the PAD.
     * @return name of PAD
     */
    public String getName() {
        return powerName;
    }
    
  
    public boolean configurePAD(Ability ability, ParameterList parameterList) {
        if ( ability == null ) return false;

        setParameterList(ability,parameterList);
        Integer distance = (Integer)parameterList.getParameterValue("Distance");
        
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
        Integer distance = (Integer)parameterList.getParameterValue("Distance");
         int cost = 0;
        
        if ( distance.intValue() > 0 ) cost += distance.intValue() * 5;
        return cost;
    }
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        Integer distance = (Integer)parameterList.getParameterValue("Distance");
        int total = getMovementDistance(ability);
        return "Stretching (+" + Integer.toString( distance.intValue() ) + ")";
    }
    
    public int getMovementDistance(Ability ability) {
        ParameterList parameterList = getParameterList(ability);
        Integer distance = (Integer)parameterList.getParameterValue("Distance");
        
        return distance.intValue();
    }
    
 /** Returns the patterns necessary to import the Power from CW.
  * The Object[][] returned should be in the following format:
  * patterns = Object[][] {
  *  { "PATTERN" , new Object[] { "PARAMETER1", parameter1.class, "PARAMETER2", parameter2.class, ... },
  *  ...
  *  }
  *
  * PATTERN should be a regular expression pattern.  For every PARAMETER* where should be one
  * parathesis group in the expression. The PARAMETERS sub-array can be null, if the line has no parameter
  * and is just informational.
  *
  * By default, the importPower will check each line of the getImportPatterns() array and if a match is
  * found, the specified parameters will be set in the powers parameter list.  It is assumed that each
  * PATTERN will only occur once.  If the pattern can occur multiple times in a valid import, a custom
  * importPower method will have to be used.
  */
    public Object[][] getImportPatterns() {
        return patterns;
    }
    
}