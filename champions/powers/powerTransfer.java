/*
 * powerHandToHandAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
 */

package champions.powers;

import champions.*;
import champions.adjustmentPowers.Adjustable;
import champions.adjustmentPowers.AdjustmentPower;
import champions.adjustmentPowers.adjustmentTree.AdjParameterEditor;
import champions.adjustmentPowers.adjustmentTree.TransferFromAttackTreeNode;
import champions.adjustmentPowers.adjustmentTree.TransferPostTriggerNode;
import champions.adjustmentPowers.adjustmentTree.TransferToAttackTreeNode;
import champions.exceptionWizard.ExceptionWizard;
import champions.interfaces.*;
import champions.exception.*;
import champions.attackTree.*;
import champions.parameters.ParameterList;

import java.util.*;
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
 *
 * The Following Steps must be performed to upgrade Power to Reconfigurable Format:
 * 1) Create costArray.
 * 2) Add the getCostArray() method, returning costArray.
 * 3) Remove existing calculateCPCost.
 *
 * The Following Steps must be performed to upgrade Power to PowerInfo format:
 * 1) Create caveatArray.  If there are no caveats, create an empty caveat array.
 * 2) Add the dynamic Power Definition Variable.
 * 3) Add the description Power Definition Variable.
 * 4) Add the getCaveat(), getDescription(), and isDynamic() methods.
 */
public class powerTransfer extends AdjustmentPower implements ChampionsConstants {
    static final long serialVersionUID =5295848483348707401L;
    
    static public String[] statOptions = {"STR","CON","DEX","BODY","INT","EGO","PRE","COM","PD",
    "ED","SPD","REC","END","STUN"};
    
    private static Object[][] parameterArray = {
        {"TransferDie","Power.TRANSFERDIE", String.class, "1d6", "Transfer Dice", DICE_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"TransferFrom","TransferFrom*.OBJECT", Object.class, null, "Transfer From Source", LIST_PARAMETER, VISIBLE, ENABLED, REQUIRED},
        {"TransferTo","TransferTo*.OBJECT", Object.class, null, "Transfer To Destination", LIST_PARAMETER, VISIBLE, ENABLED, REQUIRED}
    };
    
    // Cost Array - See Power.getCostArray()
    static private Object[][] costArray = {
        { "TransferDie", NORMAL_DICE_COST, DYNAMIC_RECONFIG, ALL_RECONFIG, new Integer(15), new Integer(0), new Integer(0) },
    };
    
    // Power Definition Variables
    private static String powerName = "Transfer"; // The Name of the Power
    private static String targetType = "SINGLE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "ATTACK"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = 15; // The Point cost of a single Damage Class of this power.
    private static String attackType = null; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = "SPECIAL"; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 1; // Indicates the END Multiplier for this power
    private static boolean dynamic = true;
    private static String description = "Transfers Character Points from a Target's Stats or Abilities to the attacker.";
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        //    { "([0-9]*d6) Energy Blast: *.*", new Object[] { "DamageDie", String.class}},
        { "([0-9]*d6) *.*", new Object[] { "TransferDie", String.class}}
    };
    
    // Custom Adders
    private static Object[] customAdders = {
        Advantage.class, "Variable Effect",
        Limitation.class, "Limited Special Effect",
        Limitation.class, "Self Only",
        Limitation.class, "Others Only",
        Limitation.class, "PD Applies",
        Limitation.class, "ED Applies",
    };
    
    // Known Caveats Array
    private static String[] caveats = {
    };
    
    /** Creates new powerHandToHandAttack */
    public powerTransfer()  {
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
        String die = (String)parameterList.getParameterValue("TransferDie");
        
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
        ability.addDiceInfo( "TransferDie", die, "Transfer Amount");
        
        // Add A Damage Class Info
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        ability.add("Ability.DOESKNOCKBACK", "FALSE", true);
        
        try {
            Dice d = new Dice(die, false);
            ability.add( "Power.MAXIMUMADJUSTMENT", new Integer( d.getD6() * 6), true);
        }
        catch ( BadDiceException bde ) {
            ability.add( "Power.MAXIMUMADJUSTMENT", new Integer( 0 ), true);
        }
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        String die = (String)parameterList.getParameterValue("TransferDie");
        
        return die + " Transfer";
    }
            /** Attempt to identify power
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     */
    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();
        
        if ( power != null && ( power.equals( "TRANSFER" ) || power.equals( "Fransfer" ) )){
            return 10;
        }
        return 0;
    }    
    public void triggerPower(BattleEvent be, Ability ability, DetailList effectList, Target target, int refNumber, String targetGroup) throws BattleEventException {
        ParameterList parameterList = getParameterList(ability);
        
        long decayInterval = getDecayInterval(ability);
        int decayRate = getDecayRate(ability);
        
        Dice die = be.getDiceRoll("TransferDie", targetGroup);
        
        ActivationInfo ai = be.getActivationInfo();
        int tindex = ai.getTargetIndex(refNumber, targetGroup);
        
        // Grab the New AdjustmentList
        AdjustmentList newList = (AdjustmentList)ai.getIndexedValue(tindex, "Target", "TRANSFERFROMLIST");
        
        // Grab the effectTransferTracker && Old AdjustmentList
        effectTransferTracker eat = findTransferTracker( be.getSource(), ability);
        AdjustmentList oldList = null;
        if ( eat != null ) {
            oldList = eat.getAdjustmentList(target);
        }
        else {
            // And effectAidTracker didn't exist, so install one here...
            eat = new effectTransferTracker();
            eat.setAbility(ability);
            eat.addEffect(be,be.getSource()); // This will take care of the undoable!
        }
        
        if ( oldList != newList ) {
            be.addUndoableEvent( eat.setAdjustmentList(target, newList) );
        }

        if ( oldList != null && getAdjustmentLevel(ability) != ADJ_VARIABLE_ALL_ADJUSTMENT ) {
            // In this case, there can be only a single adjustment, and the old adjustment must
            // immediately fade!
            int maximumAdjustables = getMaximumAdjustables(ability);
            
            FadeTracker[] fts = target.getFadeTrackers(ability);
            
            if ( fts.length + newList.getAdjustableCount() > maximumAdjustables ) {
                // We have too many adjusted, so turn off the any that aren't in the new list...

                for(int oindex = 0; oindex < fts.length; oindex++) {
                    Object oldAdjustable = fts[oindex].getAdjustable();

                    boolean found = false;
                    int nindex = newList.getAdjustableCount();
                    for(; nindex >= 0; nindex--) {
                        Adjustable newAdjustable = newList.getAdjustableObject(oindex);
                        if ( oldAdjustable.equals(newAdjustable) ) {
                            found = true;
                            break;
                        }
                    }

                    if ( ! found ) {
                        be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Restoring Transferred Character Point to " + oldAdjustable + " since only " + maximumAdjustables + " Ability(s) or Stat(s) can be Transferred a one time.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Restoring Transferred Character Point to " + oldAdjustable + " since only " + maximumAdjustables + " Ability(s) or Stat(s) can be Transferred a one time.", BattleEvent.MSG_NOTICE)); // .addMessage( "Restoring Transferred Character Point to " + oldAdjustable + " since only " + maximumAdjustables + " Ability(s) or Stat(s) can be Transferred a one time.", BattleEvent.MSG_NOTICE);
                        fts[oindex].fadeAdjustment(be, ability, true);
                    }
                }
            }
        }
        
        // Calculate the Transfer Adjustment
        double adjustmentAmount = die.getStun().doubleValue();
 
        // Create a effectTransferTo to pass into effectTransferFrom...
        effectTransferTo ett = (effectTransferTo)be.getValue("Power.EFFECTTRANSFERTO");

        
        // Grab the number of targets that are hit in the target group...
        IndexIterator ii = ai.getTargetGroupIterator(targetGroup);
        int targetCount = 0;
        while (ii.hasNext() ) {
            if ( ai.getTargetHit( ii.nextIndex() ) ) {
                targetCount ++;
            }
        }
        
        adjustmentAmount = ChampionsUtilities.roundValue( adjustmentAmount / targetCount , true );
        
          
        // Create New Adjustment Effects here
        int index, count;
        count = newList.getAdjustableCount();
        for(index=0;index<count;index++) {
            Adjustable adjustable = newList.getAdjustableObject(index);
            int percentage = newList.getAdjustablePercentage(index);
            
            effectTransferFrom ea = new effectTransferFrom(adjustable, be.getSource(), ett, ability, Math.round( adjustmentAmount / 100 * percentage), decayInterval, decayRate);
            effectList.createIndexed("Effect", "EFFECT", ea);
        }
        
        // Change to the New List from the Old list
        be.addUndoableEvent( eat.setAdjustmentList(target, newList) );
    }
    
    /** Executed after triggerPower and before apply defenses.
     *
     * This power method is called at the same time that the prepower methods are called
     * for advantages and limitations.
     *
     */
    public void postpower(BattleEvent be, Ability ability, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean isFinalTarget, int priority) {
        if ( priority == 3 ) {
            // Make sure the transferFrom effect won't overload the maximum amount of adjustment...
            effectTransferTo ett = (effectTransferTo)be.getValue("Power.EFFECTTRANSFERTO");

            double available = ett.getAvailableCharacterPoints();
            
            effectTransferFrom etf = (effectTransferFrom)effect;
            double amount = effect.getSubeffectValue(0);
            
            if ( amount > available ) {
                // Adjust the TransferFrom if we have hit the maximum transfer amount...
                amount = available;
                effect.setSubeffectValue(0, amount);
            }
            
            // Update the effectTransferTo to include this amount...
            double ettAmount = ett.getTotalAdjustmentAmount();
            be.addUndoableEvent( ett.setTotalAdjustmentAmount( ettAmount + amount ) );
        }
    }
    
    /** Generates the Transfer To effects based upon the transfer from information.
     *
     *  This will generate the transfer to effect to apply the transferred points
     *  to the source targets appropriate adjustable.
     */
    public void generateTransferEffects(BattleEvent battleEvent) {
        // Add the transferTo effect to the source
        try {
            effectTransferTo ett = (effectTransferTo)battleEvent.getValue("Power.EFFECTTRANSFERTO");
            if ( ett != null ) {
                ett.addEffect(battleEvent, battleEvent.getSource());
            }
        }
        catch ( BattleEventException bee ) {
            ExceptionWizard.postException(bee);
        }
    }
    
    /** Returns an AttackTreeNode which is placed immediately after the target effects for each target.
     *
     * getPostTriggerPowerNode allows the power a chance to create a node, which can be used
     * to perform actions after the effects of the power have been applied.  
     * 
     * For each Target with is hit by the power, getPostTriggerPowerNode will be called once.  If a non-null
     * value is returned, the node will be added to the AttackTree under the effect node for the relevant 
     * attack.  Only hit targets will cause getPostTriggerPowerNode to be called.
     *
     * If a manuever is in use, it will also have an oppertunity to generate an AttackTreeNode, which will
     * be added to the AttackTree.
     */
    public AttackTreeNode getPostTriggerPowerNode(BattleEvent be, Target target, String TargetGroup, int refNumber) {
        return new TransferPostTriggerNode("TransferPostTriggerNode");
    }
    
    public AttackTreeNode getTriggerPowerNode(BattleEvent be, Target target, String TargetGroup, int refNumber) {
        Ability ability = be.getAbility();
        ParameterList parameterList = getParameterList(ability);
        TransferFromAttackTreeNode node = null;
        node = new TransferFromAttackTreeNode("Transfer Target");
        node.setTargetReferenceNumber(refNumber);
        
        return node;
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
        Ability ability = be.getAbility();
        ParameterList parameterList = getParameterList(ability);
        TransferToAttackTreeNode node = null;
        node = new TransferToAttackTreeNode("Transfer To"); 
        return node;
    }
    
    // Special Handling is necessary to configure the ability.
    public ParameterList getParameterList(Ability ability, int not_used) {
        ParameterList pl = super.getParameterList(ability,not_used);
        if ( pl.getCustomEditor("TransferFrom") == null) {
            pl.setCustomEditor("TransferFrom", new AdjParameterEditor(ability, pl, "TransferFrom", ADJ_CONFIG_TRANSFER_FROM));
        }
        
        if ( pl.getCustomEditor("TransferTo") == null) {
            pl.setCustomEditor("TransferTo", new AdjParameterEditor(ability, pl, "TransferTo", ADJ_CONFIG_TRANSFER_TO));
        }
        return pl;
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
    
    /** Returns Power Cost array for this Power.
     *
     * The Power cost array is an Object[] array, which contains information detailing how to
     * calculate the cost of a power and reconfigure a power when the CP for an ability is adjusted.
     *
     * It is in the follow format:
     * Object[][] costArray = {
     * { Parameter, Type, Dynamic, ReconfigPercent, Type Options ... },
     * ...
     * }
     *
     * Where:
     * Parameter -> String representing the parameterName.  Must be parameter from getParameterArray() array.
     * Type -> Type of Cost Calculation: NORMAL_DICE_COST, KILLING_DICE_COST, GEOMETRIC_COST, LOGRITHMIC_COST,
     *     LIST_COST, BOOLEAN_COST, COMBO_COST.
     * Dynamic -> Indicater of Dynamic or Static reconfigurability: DYNAMIC_RECONFIG or STATIC_RECONFIG.
     * ReconfigPercent -> Integer indicate what percent of reconfigured CP should be allocated to this parameter
     *     by default.  Can be 0 to 100 or PROPORTIONAL_RECONFIG.  PROPORTIONAL_RECONFIG will base the proportion
     *     on the configuration of the base power.
     * Type Options -> Custom options depending on the specified type, as follows:
     *     NORMAL_DICE_COST -> PtsPerDC:Integer, Base:Integer, Minimum:Integer.
     *     KILLING_DICE_COST -> PtsPerDC:Integer, Base:Integer, Minimum:Integer.
     *     GEOMETRIC_COST -> X:Integer, Y:Integer, Base:Integer, Minimum:Integer.
     *     LOGRITHMIC_COST -> PtsPerMultiple:Integer, Multiple:Integer, Base:Integer, Minimum:Integer.
     *     LIST_COST -> PtsPerItem:Integer, Base:Integer.
     *     BOOLEAN_COST -> PtsForOption:Integer.
     *     COMBO_COST -> OptionCostArray:Integer[], OptionNames:String[].
     *
     */
    public Object[][] getCostArray(Ability ability) {
        return costArray;
    }
    
    /** Returns a Description of the Power
     */
    public String getDescription() {
        return description;
    }
    
    /** Returns whether power can be dynamcially reconfigured.
     */
    public boolean isDynamic() {
        return dynamic;
    }
    
    /** Returns a String[] of Caveats about the Power
     * Power uses this method to automatically build the getCaveats()
     * String.  The Strings returns by getCaveatArray() will be assembled into
     * list form and returned via getCaveats().
     *
     * Return an empty array if there are no known caveats for this power.
     */
    public String[] getCaveatArray() {
        return caveats;
    }
    
    /** Returns an Array of Objects, representing custom/special advantages, limitations, special parameters usable with the power.
     * The Array should be in the format of class type (limitation, advantage, special) followed by the class name, repeated for
     * each additional custom added.  For example:
     *  array[] = { Limitation.class, "limitationLimitedSpecialFX", Advantage.class, "advantageVariableEffect" };
     */
    public Object[] getCustomAddersArray() {
        return customAdders;
    }
    
    static public effectTransferTracker findTransferTracker(Target source, Ability ability) {
        int index = source.getEffectCount() - 1;
        for(; index >=0; index--) {
            Effect e = source.getEffect(index);
            if ( e instanceof effectTransferTracker && ((effectTransferTracker)e).getAbility().equals(ability) ) {
                return (effectTransferTracker)e;
            }
        }
        return null;
    }
    
    public ArrayList<Adjustable> getAvailableFromAdjustables(Ability sourceAbility, Target target) {
        return getAvailableAdjustables(sourceAbility, target, "TransferFrom");
    }
    
    /*static public Vector getAvailableFromAdjustables(Ability sourceAbility, Target target) {
        Vector v = new Vector();
        
        // First Grab all of the Abilities
        AbilityIterator ai = target.getAbilities();
        int index, count;
        
        String indexName = "TransferFrom";
        count = sourceAbility.getIndexedSize( indexName );
        
        Object[] adjustables = new Object[count];
        
        for( index = 0; index < count; index++ ) {
            adjustables[index] = sourceAbility.getIndexedValue(index, indexName, "OBJECT");
        }
        
        while ( ai.hasNext() ) {
            Ability targetAbility = ai.nextAbility();
            
            for(index = 0; index < count; index++) {
                if ( isAbilityAdjustable(targetAbility, adjustables[index]) ) {
                    v.add( targetAbility );
                    break;
                }
            }
        }
        
        // Now Grab all of the available stats...
        for( index = 0; index < count; index++ ) {
            if ( adjustables[index] instanceof Characteristic ) {
                if ( target.hasStat( ((Characteristic)adjustables[index]).getName() ) ) {
                    v.add( adjustables[index] );
                }
            }
        }
        
        return v;
    }*/
    
    public ArrayList<Adjustable> getAvailableToAdjustables(Ability sourceAbility, Target target) {
        return getAvailableAdjustables(sourceAbility, target, "TransferTo");
    }
    
    /*static public Vector getAvailableToAdjustables(Ability sourceAbility, Target target) {
        Vector v = new Vector();
        
        // First Grab all of the Abilities
        AbilityIterator ai = target.getAbilities();
        int index, count;
        
        String indexName = "TransferTo";
        count = sourceAbility.getIndexedSize( indexName );
        
        Object[] adjustables = new Object[count];
        
        for( index = 0; index < count; index++ ) {
            adjustables[index] = sourceAbility.getIndexedValue(index, indexName, "OBJECT");
        }
        
        while ( ai.hasNext() ) {
            Ability targetAbility = ai.nextAbility();
            
            for(index = 0; index < count; index++) {
                if ( isAbilityAdjustable(targetAbility, adjustables[index]) ) {
                    v.add( targetAbility );
                    break;
                }
            }
        }
        
        // Now Grab all of the available stats...
        for( index = 0; index < count; index++ ) {
            if ( adjustables[index] instanceof Characteristic ) {
                if ( target.hasStat( ((Characteristic)adjustables[index]).getName() ) ) {
                    v.add( adjustables[index] );
                }
            }
        }
        
        return v;
    } */
    
    /*static private boolean isAbilityAdjustable(Ability targetAbility, Object adjustable) {
        boolean rv = false;
        
        if ( adjustable instanceof Ability ) {
            rv = ( targetAbility.equals(adjustable) );
        }
        else if ( adjustable instanceof SpecialEffect ) {
            rv = ( targetAbility.hasSpecialEffect( ((SpecialEffect)adjustable).getName() ) );
        }
        else if ( adjustable instanceof Power ) {
            rv = ( targetAbility.getPower().getClass().equals( adjustable.getClass() ) );
        }
        
        return rv;
    }*/
    
    /** Returns the Maximum Number of Abilities/Stats which can be affected at a single time.
     */
    /*static private int getMaximumAdjustables(Ability sourceAbility) {
        switch ( sourceAbility.getAdjustmentLevel() ) {
            case ADJ_SINGLE_ADJUSTMENT:
                return 1;
            case ADJ_VARIABLE_1_ADJUSTMENT:
                return 1;
            case ADJ_VARIABLE_2_ADJUSTMENT:
                return 2;
            case ADJ_VARIABLE_4_ADJUSTMENT:
                return 4;
            case ADJ_VARIABLE_ALL_ADJUSTMENT:
                return Integer.MAX_VALUE;
        }  
        return 1;
    } */
    
    static public int getMaximumAdjustment(Ability ability) { 
        return ability.getIntegerValue("Power.MAXIMUMADJUSTMENT").intValue();
    }

    public String getAdjustableParameterName() {
        throw new UnsupportedOperationException("Transfer has multiple parameter names and should not be using this method.");
    }
}