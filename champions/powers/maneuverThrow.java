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
import champions.attackTree.*;
import champions.parameters.ParameterList;

import javax.swing.*;
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
public class maneuverThrow extends Power 
implements ChampionsConstants {
    static final long serialVersionUID =5295848683348607403L;

    static private Object[][] parameterArray = {
        {"DC","Maneuver.DC", Double.class, new Double(0), "Additional Damage Classes", DOUBLE_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED }
    };
    
    // Power Definition Variables
    private static String powerName = "Throw"; // The Name of the Power
    private static String targetType = "SINGLE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "ATTACK"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = 5; // The Point cost of a single Damage Class of this power.
    private static String attackType = "MELEE"; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = "NORMAL"; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = true; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 0; // Indicates the END Multiplier for this power
    
    /** Creates new powerHandToHandAttack */
    public maneuverThrow()  {
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
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure

        
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
     //   ability.addDiceInfo( "DamageDie", "", "Maneuver Damage");
        
        // Add A Damage Class Info
        //ability.add("Base.DC",  new Double(ChampionsUtilities.StringToNormalDC(die)), true);
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        ability.setIs("MELEEMANEUVER",true);
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }

    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList pl = getParameterList(ability);
        Double dc = (Double)pl.getParameterValue("DC");

        if ( dc.intValue() != 0 ) {
            return "Throw (" + ChampionsUtilities.toSignedString(dc.intValue() * 5 ) + " Str)";
        }
        else {
            return "Throw";
        }
    }

    public void triggerPower(BattleEvent be, Ability ability,DetailList effectList, Target target, int refNumber, String targetGroup) throws BattleEventException {
        effectList.createIndexed( "Effect","EFFECT", new effectKnockedDown() );
    }
    
    /** Returns an AttackTreeNode used to gather necessary information configure a Power.
     *
     * getSetupPowerNode allows the power a chance to create a setPower node, which can be used
     * to gather additional information necessary to apply the effect.  
     * 
     * getSetupPowerNode is called for both the ability and the maneuver of a power and can be used to display
     * panels necessary to gather information for the ability/maneuver.
     *
     * If the node provided by getSetupPowerNode is non-null, it will be inserted as children of the
     * AttackParameters panel.
     */
    public AttackTreeNode getSetupPowerNode(BattleEvent be, Target source) {
        
        ParameterList pl = new ParameterList();
        pl.addIntegerParameter("velocity", "Power.VELOCITY", "Velocity", new Integer(0) );
        
        GenericParameterNode node = new GenericParameterNode("Throw Parameters", pl, be, "Enter the Velocity at which " + be.getSource().getName() + " is moving...");
        return node;
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

        Integer integer;
        int velocity;
        if ( (integer = be.getIntegerValue("Power.VELOCITY")) != null ) {
            velocity = integer.intValue()/5;
            dc = new Double ( dc.doubleValue() + velocity );
        }

        be.add("Maneuver.DC",  dc, true);
    }
     public void importPower(Ability ability, AbilityImport ai) {
        int index, count;
        String line;
        
        String matchPattern = ".*\\(OCV ([-+][0-9]*), DCV ([-+][0-9]*),.*\\)";
        
        count = ai.getImportLineCount();
        for(index=0;index<count;index++) {
            if ( ai.isLineUsed(index) == true ) continue;
            
            line = ai.getImportLine(index);
            
            if ( ChampionsMatcher.matches(matchPattern, line) ) {
                // Set OCV/DCV modifiers
                
                int ocv = ChampionsMatcher.getIntMatchedGroup(1);
                int dcv = ChampionsMatcher.getIntMatchedGroup(2);
                
                ability.setOCVModifier(ocv);
                ability.setDCVModifier(dcv);
                
                ai.setLineUsed(index, this);
                break;
            }
        }
        
        super.importPower(ability, ai);
    }
   
}