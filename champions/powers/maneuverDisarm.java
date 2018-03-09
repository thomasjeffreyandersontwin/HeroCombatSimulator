/*
 * maneuverDisarm.java
 *
 * Created on September 24, 2000, 5:06 PM
 */

package champions.powers;

import champions.*;
import champions.interfaces.*;
import champions.exception.*;
import champions.attackTree.*;
import champions.parameters.ParameterList;
import champions.battleMessage.GenericSummaryMessage;

/**
 *
 * @author  pnoffke
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
public class maneuverDisarm extends Power
implements ChampionsConstants {
    static final long serialVersionUID =5295848683348705403L;

    static private Object[][] parameterArray = {
     //   {"DC","Maneuver.DC", Double.class, new Double(0), "Extra Damage Classes", DOUBLE_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED },
        {"Strength","Power.STRENGTH", Integer.class, new Integer(0), "Additional Strength", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(0)},
      //  {"OCVModifier","Ability.OCVBONUS", Integer.class, new Integer(-1), "Maneuver OCV Modifier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
      //  {"DCVModifier","Ability.DCVBONUS", Integer.class, new Integer(-2), "Maneuver DCV Modifier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
      //  {"MartialManeuver","Ability.ISMARTIALMANEUVER", String.class, "FALSE", "Martial Arts Skill", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED}        
    };
    
    // Power Definition Variables
    private static String powerName = "Disarm"; // The Name of the Power
    private static String targetType = "SINGLE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "ATTACK"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = 5; // The Point cost of a single Damage Class of this power.
    private static String attackType = "MELEE"; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = "NORMAL"; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 0; // Indicates the END Multiplier for this power

    /** Creates new maneuverDisarm */
    public maneuverDisarm()  {
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
  * later on. If an existing parameterList already exists, it should be
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
        Integer str = (Integer)parameterList.getParameterValue("Strength");
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
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
        //ability.addDiceInfo( "DamageDie", die, "Hand-to-Hand Attack Damage");
        
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
        ParameterList parameterList = getParameterList(ability);
        Integer str = (Integer)parameterList.getParameterValue("Strength");

        if ( str.intValue() != 0 ) {
            return "Disarm (" + ChampionsUtilities.toSignedString( str.intValue() ) + " STR)";
        }
        else {
            return "Disarm";
        }
    }
    
    /** Returns an AttackTreeNode used to gather necessary information to generate effects of Power.
     *
     * getTriggerPowerNode allows the power a chance to create a triggerPower node, which can be used
     * to gather additional information necessary to apply the effect.
     *
     * For each Target with is hit by the power, getTriggerPowerNode will be called once.  If a non-null
     * value is returned, the node will be added to the AttackTree under the effect node for the relevant
     * attack.  Only hit targets will cause getTriggerPowerNode to be called.
     *
     * If a manuever is in use, it will also have an oppertunity to generate an AttackTreeNode, which will
     * be added to the AttackTree.
     */
    public AttackTreeNode getTriggerPowerNode(BattleEvent be, Target target, String TargetGroup, int refNumber) {
        DisarmTriggerNode dtn =  new DisarmTriggerNode("DisarmTriggerNode");
        dtn.setTargetReferenceNumber(refNumber);
        return dtn;
    }
    
    public void triggerPower(BattleEvent be, Ability ability, DetailList effectList,
            Target target, int refNumber, String targetGroup) throws BattleEventException {
        // Check to see if the target resisted with Strength...
        int sindex = be.findSkillChallenge("Strength", "Strength", target, be.getSource());
        if ( sindex != -1 && be.getSkillChallengeWinner(sindex) == target ) {
            be.addBattleMessage( new GenericSummaryMessage(target, " was not disarmed"));
        }
        else {
            effectDisarmed disarmedEffect = new effectDisarmed(be.getSource());
            effectList.createIndexed("Effect", "EFFECT", disarmedEffect);
        }
    }

    public void adjustDice(BattleEvent be, String targetGroup) {
        Integer str;
        ParameterList parameterList;
        
        Ability a = be.getAbility();
        Ability m = be.getManeuver();
        if ( a != null && a.getPower() == this ) {
            parameterList = getParameterList(a);
            str = (Integer)parameterList.getParameterValue("Strength");
        }
        else if ( m != null && m.getPower() == this )  {
            parameterList = getParameterList(m);
            str = (Integer)parameterList.getParameterValue("Strength");
        }
        else {
            str = new Integer(0);
        }
        
        be.add("Maneuver.DC", new Double( ChampionsUtilities.strToDCs(str.intValue())), true);
    }


}
