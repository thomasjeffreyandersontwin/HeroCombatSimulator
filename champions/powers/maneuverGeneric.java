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
 */
public class maneuverGeneric extends Power
implements ChampionsConstants {
    static final long serialVersionUID =5295848683348704403L;

    static private Object[][] parameterArray = {
        {"DC","Maneuver.DC", Double.class, new Double(1), "Damage Classes", DOUBLE_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED },
        {"KTYPE","Ability.KTYPE", String.class, "KILLING" , "Attack Type", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", new String[] {"KILLING","NORMAL"}}
    };
    
    // Power Definition Variables
    private static String powerName = "Generic Attack Maneuver"; // The Name of the Power
    private static String targetType = "SINGLE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "ATTACK"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = 5; // The Point cost of a single Damage Class of this power.
    private static String attackType = "MELEE"; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = "KILLING"; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = true; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 0; // Indicates the END Multiplier for this power
    
    /** Creates new powerHandToHandAttack */
    public maneuverGeneric()  {
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
        Double dc = (Double)parameterList.getParameterValue("DC");
        String type = (String)parameterList.getParameterValue("KTYPE");
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        // No Validation necessary
        
        // Always copy the configuration parameters directly into the ability.  This will
        // take the parameters stored in the parameter list and copy them into the
        // ability using the appropriate keys and values.
        parameterList.copyValues(ability);
        
        // Start to Actually Configure the Power.
        // The Add Power Info should always be executed to add information to the ability.
        // All of this information should be set in the Power Definition Variables at the
        // top of this file
        ability.addPowerInfo( this, powerName, targetType, persistenceType, activationTime);
        
        // This is a Special Power where the Attack Type can change.  Do not use this setup
        // in newly created powers.
        ability.addAttackInfo( "MELEE", type );
        ability.add("Ability.PPDC", new Double(5), true);

        ability.setGenerateDefaultEffects(generateDefaultDamage);
        if ( endMultiplier != 1 ) ability.setENDMultiplier(endMultiplier);
        
        // Add any dice information which is necessary to use this power.
  //      ability.addDiceInfo( "DamageDie", "", "Attack Damage");
  //      if ( type.equals ("KILLING") ) {
  //          ability.addDiceInfo( "StunDie", "1d6-1", "Attack Stun Multiplier");
   //     }
        
        // Add A Damage Class Info
        ability.add("Maneuver.DC",  dc, true);
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        ability.setIs( "MELEEMANEUVER",true);
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }

    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        Double dc = (Double)parameterList.getParameterValue("DC");
        String type = (String)parameterList.getParameterValue("KTYPE");

        if ( type.equals( "NORMAL" ) ) {
            return ChampionsUtilities.DCToNormalString(dc.doubleValue() ) + "N Maneuver";
        }
        else {
            return ChampionsUtilities.DCToKillingString(dc.intValue() ) + "K Maneuver";
        }
    }
    
    public void adjustDice(BattleEvent be, String targetGroup) {
        Double dc;
        ParameterList parameterList;
        
        Ability a = be.getAbility();
        Ability m = be.getManeuver();
        if ( a != null && a.getPower() == this ) {
            parameterList = getParameterList(a);
            dc = (Double)parameterList.getParameterValue("DC");
        }
        else if ( m != null && m.getPower() == this )  {
            parameterList = getParameterList(m);
            dc = (Double)parameterList.getParameterValue("DC");
        }
        else {
            dc = new Double(0);
        }
        be.add("Maneuver.DC",  dc, true);
    } 

    
}