/*
 * maneuverEscapeFromGrab.java
 *
 * Created on April 22, 2001, 7:36 PM
 */

package champions.powers;

import champions.parameters.ParameterList;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;
import champions.*;
import champions.exception.*;
import champions.interfaces.*;
import java.util.Vector;
import champions.attackTree.*;
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
public class maneuverAcrobatics extends Power implements ChampionsConstants {
    static final long serialVersionUID =5295828683348707403L;
    
    static private Object[][] parameterArray = {
        {"SkillRoll","Power.LEVEL", Integer.class, new Integer(0), "Extra Level", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"OCVBonus","Effect.OCVBONUS", Integer.class, new Integer(5)},
        {"DCVBonus","Effect.DCVBONUS", Integer.class, new Integer(5)}
    };
    
    // Power Definition Variables
    private static String powerName = "Acrobatics"; // The Name of the Power
    private static String targetType = "SELF"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "HALF"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = 5; // The Point cost of a single Damage Class of this power.
    private static String attackType = "NULL"; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = "NORMAL"; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 1; // Indicates the END Multiplier for this power
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        // { ".*\\(OCV ([-+][0-9]*), DCV ([-+][0-9]*), STR ([0-9]*)\\)", new Object[] {"OCVModifier", Integer.class, "DCVModifier", Integer.class}}
    };
    
    /** Creates new powerHandToHandAttack */
    public maneuverAcrobatics()  {
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
        if ( attackType != null ) {
            ability.addAttackInfo( attackType,damageType );
            ability.add("Ability.PPDC", new Double(pointsPerDC), true);
        }
        ability.setGenerateDefaultEffects(generateDefaultDamage);
        if ( endMultiplier != 1 ) ability.setENDMultiplier(endMultiplier);
        
        // Add any dice information which is necessary to use this power.
        //   ability.addDiceInfo( "DamageDie", "", "Attack Damage");
        
        // Add A Damage Class Info
        //ability.add("Maneuver.DC",  dc, true);
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        ability.setAutoSource(true);
        ability.setTargetSelf(true);
        ability.setAutoHit(true);
        
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    
    public String getConfigSummary(Ability ability, int not_used) {
        return "Skill Roll";
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
/*    public AttackTreeNode getTriggerPowerNode(BattleEvent be, Target target, String TargetGroup, int refNumber) {
        EscapeTriggerNode etn =  new EscapeTriggerNode("EscapeTriggerNode");
        etn.setGrabEffect( getGrabbedEffect(be) );
        return etn;
    }
 
    public void triggerPower(BattleEvent be, Ability ability,DetailList effectList, Target target, int refNumber, String targetGroup) throws BattleEventException {
        // Target is actually the grabber and source is the grabbed person.
        effectGrabbed grabEffect = getGrabbedEffect(be);
 
        Target challenger = grabEffect.getTarget();
        Target challengee = grabEffect.getGrabber();
 
        int sindex = be.findSkillChallenge("Strength", "Strength", challenger, challengee);
        if ( sindex != -1 && be.getSkillChallengeWinner(sindex) == challenger ) {
            be.addBattleMessage( new champions.battleMessage.SimpleBattleMessage( challenger.getName() + " escaped from " + challengee.getName() + "'s grab.", BattleEvent.MSG_HIT )); // .addBattleMessage( new champions.battleMessage.SimpleBattleMessage( challenger.getName() + " escaped from " + challengee.getName() + "'s grab.", BattleEvent.MSG_HIT )); // .addMessage( challenger.getName() + " escaped from " + challengee.getName() + "'s grab.", BattleEvent.MSG_HIT );
            grabEffect.removeEffect(be,challenger);
        }
        else {
            // Didn't escape...
            be.addBattleMessage( new champions.battleMessage.SimpleBattleMessage( challenger.getName() + " failed to escape from " + challengee.getName() + "'s grab.", BattleEvent.MSG_MISS )); // .addBattleMessage( new champions.battleMessage.SimpleBattleMessage( challenger.getName() + " failed to escape from " + challengee.getName() + "'s grab.", BattleEvent.MSG_MISS )); // .addMessage( challenger.getName() + " failed to escape from " + challengee.getName() + "'s grab.", BattleEvent.MSG_MISS );
        }
 
    }
 */
/*    public AttackTreeNode preactivate(BattleEvent be) {
        EscapeSetupNode etn =  new EscapeSetupNode("Escape Parameters");
 
        return etn;
    }
    public AttackTreeNode preactivate(BattleEvent be) {
        //ParameterList parameterList = getParameterList(ability,index);
 
        SkillRollNode node = new SkillRollNode("Skill Roll");
 
        //node.setAbility(ability);
        //node.setLimitationIndex(index);
 
        return node;
    }
 */
    
    public AttackTreeNode getTriggerPowerNode(BattleEvent be, Target target, String TargetGroup, int refNumber) {
        Ability ability = be.getAbility();
        ParameterList parameterList = getParameterList(ability);
        SkillRollNode node = new SkillRollNode("Skill Roll");
        node.setAbility(ability);
        
        return node;
    }
    
    /** Initializes the power when it is first loaded by the Simulator.
     *
     * The <code>initialize</code> method is gauranteed to be called at least once prior to
     * any use of the power.  However, it is possible that the method would be called
     * multiple times.  If the initialization code can only be run one, the power should
     * track whether it has been initialized already.
     */
    public void initialize() {
        ProfileTemplate pt = ProfileTemplate.getDefaultProfileTemplate();
        System.out.println("what is this!???");
        pt.addOption( "Skill Roll", "SHOW_SKILL_PANEL",
        "This Description is cool.",
        "AttackTree.toHitIcon", null);
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
