/*
 * maneuverThrowFromGrab.java
 *
 * Created on April 22, 2001, 7:33 PM
 */

package champions.powers;


import champions.Ability;
import champions.ActivationInfo;
import champions.BattleEvent;
import champions.DetailList;
import champions.Effect;
import champions.Power;
import champions.Target;
import champions.attackTree.AttackTreeNode;
import champions.attackTree.ThrowNode;
import champions.attackTree.ThrowNode;
import champions.exception.BattleEventException;
import champions.interfaces.ChampionsConstants;
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
public class maneuverThrowFromGrab  extends Power
implements ChampionsConstants{
    static final long serialVersionUID =5275848683348707403L;
    
    static private Object[][] parameterArray = {
    };
    
    // Power Definition Variables
    private static String powerName = "Throw from Grab"; // The Name of the Power
    private static String targetType = "SINGLE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "ATTACK"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = 5; // The Point cost of a single Damage Class of this power.
    private static String attackType = "RANGED"; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = "NORMAL"; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = true; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 0; // Indicates the END Multiplier for this power
    
    /** Creates new powerHandToHandAttack */
    public maneuverThrowFromGrab()  {
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
        //String die = (String)parameterList.getParameterValue("DamageDie");
        
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
        ability.addDiceInfo( "DamageDie", "", "Throw Damage");
        
        // Add A Damage Class Info
        //ability.add("Base.DC",  new Double(ChampionsUtilities.StringToNormalDC(die)), true);
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        ability.setAutoSource(true);
        ability.setAutoHit(true);
        
        ability.setStrengthAddsToDC(true);
        ability.setIsThrow(true);
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    
    public String getConfigSummary(Ability ability, int not_used) {
            return "Throw held object";
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
        return new ThrowNode("Thrown Object");
    }
    
    public void triggerPower(BattleEvent be, Ability ability,DetailList effectList, Target target, int refNumber, String targetGroup) throws BattleEventException {
        effectList.createIndexed( "Effect","EFFECT", new effectKnockedDown() );
    }
    
    public boolean postTrigger(BattleEvent be, Target target) throws BattleEventException {
        Effect grabbedEffect = maneuverGrab.findGrabbedEffect( be.getSource(), getThrownObject(be) );
        if ( grabbedEffect != null ) {
            grabbedEffect.removeEffect(be, target);
        }
        return true;
    }
    
    protected Target getThrownObject(BattleEvent be) {
        ActivationInfo ai = be.getActivationInfo();
        Target target = (Target)ai.getValue("ActivationInfo.THROWNOBJECT");
        
        if ( target == null ) {
            target = (Target)be.getValue("BattleEvent.THROWNOBJECT");
        }
        
        return target;
    }
}