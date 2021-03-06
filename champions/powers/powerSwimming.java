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
/**
 *
 * @author  unknown
 * @version
 *
 * To Convert from old format powers, to new format powers:
 *
 * 1) Add implements ChampionsConstants to class definition.<P>
 * 2) Copy and Fill in Power Definition Variables. <P>
 * 3) Move Parameter Information to parameterArray. <P>
 * 4) Delete getParameters method (unless special parameter handling is necessary.<P>
 * 5) Change configurePAD(Ability,DetailList) method to configurePAD(Ability ability, ParameterList parameterList).<P>
 * 6) Edit configurePAD method to use format specified below.<P>
 * 7) Change checkParameter method to checkParameter(Ability ability, <i>int padIndex</i>,
 * String key, Object value, Object oldValue);
 * 8) Edit getConfigSummary method to use parameterList instead of parseParameter methods.<P>
 * 9) Change all instances of parseParameter to getParameterValue.<P>
 * 10) Add getParameterArray method.<P>
 * 11) Edit getName method to return powerName variable.
 * 12) Change serialVersionUID by some amount.
 * 13) Add patterns array and define import patterns.<P>
 * 14) Add getImportPatterns() method.<P>
 */
public class powerSwimming extends Power
implements ChampionsConstants {
    static final long serialVersionUID = 7666760941350027676L;
    
    static private Object[][] parameterArray = {
        {"DistanceFromCollision","Ability.MOVEDISTANCE", Integer.class, new Integer(2), "Swimming DistanceFromCollision", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(0)},
        {"NoncombatX","Power.NONCOMBATX", Integer.class, new Integer(2), "Non-Combat Multiplier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(2)},
        {"Base","Power.ISBASE", Boolean.class, new Boolean(false), "Character's Base Running", BOOLEAN_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        {"AddsToBase","Power.ADDSTOBASE", Boolean.class, new Boolean(false), "Include Base Running", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        
    };
    
    // Power Definition Variables
    private static String powerName = "Swimming"; // The Name of the Power
    private static String targetType = "MOVE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "HALFMOVE"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = -1; // The Point cost of a single Damage Class of this power.
    private static String attackType = null; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = null; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 1; // Indicates the END Multiplier for this power
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
    //    { ".*\\(\\+.*\", ([0-9]*)\", .*", new Object[] { "DistanceFromCollision", Integer.class}},
    //    { "Non-Combat Multiplier: �([0-9]*),.*", new Object[] { "NoncombatX", Integer.class}},
    //    { "Non-Combat \\(MPH\\).*",null}
    };
    
    /** Creates new powerHandToHandAttack */
    public powerSwimming()  {
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
    
 /** Configures the ability according to the parameters in parameterList.
  * The parameterList should be stored with the ability for configuration
  * later on. If an existing parameterList alread exists, it should be
  * replaced with this one.
  *
  * All value/pairs should be copied into the ability for direct access.
  */
    public boolean configurePAD(Ability ability, ParameterList parameterList) {
        // Fail immediately if ability is null
        if ( ability == null ) return false;
        
        // Always Set the ParameterList to the parameterList
        setParameterList(ability,parameterList);
        
        // Read in any parameters that will be needed to configure the power or
        // Determine the validity of the power configuration.  Read the parameters
        // from the parameterList, instead of directly from the ability, since the
        // Ability isn't configured yet.
        Integer distance = null;
        if(parameterList.contains("DistanceFromCollision"))
		{
        	 distance = (Integer)parameterList.getParameterValue("DistanceFromCollision");
             
		}
        else {
        	distance = (Integer)parameterList.getParameterValue("Distance");
        }
        Integer noncombatx = (Integer)parameterList.getParameterValue("NoncombatX");
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure.
        
        // No Validation Necessary
        
        // Always copy the configuration parameters directly into the ability.  This will
        // take the parameters stored in the parameter list and copy them into the
        // ability using the appropriate keys and values.
        parameterList.copyValues(ability);
        
        // Start to Actually Configure the Power.
        // The Add Power Info should always be executed to add information to the ability.
        // All of this information should be set in the Power Definition Variables at the
        // top of this file
        ability.addPowerInfo( this, powerName, targetType, persistenceType, activationTime);
        if ( attackType != null ) {
            ability.addAttackInfo( attackType,damageType );
            ability.add("Ability.PPDC", new Double(pointsPerDC), true);
        }
        ability.setGenerateDefaultEffects(generateDefaultDamage);
        if ( endMultiplier != 1 ) ability.setENDMultiplier(endMultiplier);
        
        // Add any dice information which is necessary to use this power.
        //ability.addDiceInfo( "DamageDie", die, "Energy Blast Damage");
        
        // Add A Damage Class Info
        //ability.add("Base.DC",  new Double(ChampionsUtilities.StringToNormalDC(die)), true);
        boolean base = (Boolean)parameterList.getParameterValue("Base");
        
        parameterList.setVisible("AddsToBase", !base);
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        ability.add("Ability.ISMOVEMENT", "TRUE", true);
        ability.add("Ability.MOVETYPE", "Flight", true);
        
        ability.setRequiresTarget(false);
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        // Return true to indicate success
        return true;
    }
    
    public int calculateCPCost(Ability ability) {
        ParameterList parameterList = getParameterList(ability);
        Integer distance = null;
        if(parameterList.contains("DistanceFromCollision"))
		{
        	 distance = (Integer)parameterList.getParameterValue("DistanceFromCollision");
             
		}
        else {
        	distance = (Integer)parameterList.getParameterValue("Distance");
        };
        Integer noncombatX = (Integer)parameterList.getParameterValue("NoncombatX");
        boolean base = (Boolean)parameterList.getParameterValue("Base");
        
        int cost = 0;
        
        if ( base ) {
            if ( distance.intValue() > 2 ) cost += (distance.intValue() - 2) * 1;
        }
        else {
            cost += (distance.intValue()) * 1;
        }
            
        cost += Math.round(Math.log(noncombatX.intValue())/Math.log(2) - 1) * 5;
        
        return cost;
    }
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        Integer distance = null;
        if(parameterList.contains("DistanceFromCollision"))
		{
        	 distance = (Integer)parameterList.getParameterValue("DistanceFromCollision");
             
		}
        else {
        	distance = (Integer)parameterList.getParameterValue("Distance");
        }
        Integer noncombatX = (Integer)parameterList.getParameterValue("NoncombatX");
        boolean base = (Boolean)parameterList.getParameterValue("Base");
        boolean addsToBase = (Boolean)parameterList.getParameterValue("AddsToBase");
        
        if ( base ) {
            return "Swimming (" + Integer.toString( distance.intValue()) + "\", " + distance.toString() + "\", NC: " + Integer.toString( distance.intValue() * noncombatX.intValue() ) + "\")";
        }
        else {
            return "Swimming (" + Integer.toString( distance.intValue()  ) + "\"" + ( addsToBase ? " + base" : "") + ", NC: " + Integer.toString( distance.intValue() * noncombatX.intValue() ) + "\")";            
        }    
    }
    
    public int getMovementDistance(Ability ability) {
        ParameterList parameterList = getParameterList(ability);
        Integer distance = null;
        if(parameterList.contains("DistanceFromCollision"))
		{
        	 distance = (Integer)parameterList.getParameterValue("DistanceFromCollision");
             
		}
        else {
        	distance = (Integer)parameterList.getParameterValue("Distance");
        };
       // Integer noncombatX = (Integer)parameterList.getParameterValue("NoncombatX");
        boolean base = (Boolean)parameterList.getParameterValue("Base");
        boolean addsToBase = (Boolean)parameterList.getParameterValue("AddsToBase");
        
        int totalDistance = distance.intValue();
        if ( addsToBase ) {
            Target source = ability.getSource();
            if ( source != null ) {
                Ability baseSwimming = source.getAbilityList().getAbility("Swimming", true);
                if ( baseSwimming.getPower() != this ) {
                    totalDistance += baseSwimming.getRange();
                }
            }
        }
        
        return totalDistance;
    }
    
     /** Attempt to identify power
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     */
    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();
        
        if ( power != null && ( power.equals( "SWIMMING" ) || power.equals( "Swimming" ) )){
            return 10;
        }
        return 0;
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