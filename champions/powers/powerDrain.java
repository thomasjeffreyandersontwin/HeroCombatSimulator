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
import champions.adjustmentPowers.adjustmentTree.DrainFromAttackTreeNode;
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
public class powerDrain extends AdjustmentPower implements ChampionsConstants {
    static final long serialVersionUID =5295848483348707401L;
    
    static public String[] statOptions = {"STR","CON","DEX","BODY","INT","EGO","PRE","COM","PD",
    "ED","SPD","REC","END","STUN"};
    
    private static Object[][] parameterArray = {
        {"DrainDie","Power.DRAINDIE", String.class, "1d6", "Drain Dice", DICE_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"DrainFrom","DrainFrom*.OBJECT", Object.class, null, "Drain Source", LIST_PARAMETER, VISIBLE, ENABLED, REQUIRED},
        {"ImportDrainFrom","Power.IMPORTDRAINFROM", String.class, "Import Drain From", "Import Drain From", STRING_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED}
        
    };
    
    // Cost Array - See Power.getCostArray()
    static private Object[][] costArray = {
        { "DrainDie", NORMAL_DICE_COST, DYNAMIC_RECONFIG, ALL_RECONFIG, new Integer(10), new Integer(0), new Integer(0) },
    };
    
    // Power Definition Variables
    private static String powerName = "Drain"; // The Name of the Power
    private static String targetType = "SINGLE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "ATTACK"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = 5; // The Point cost of a single Damage Class of this power.
    private static String attackType = null; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = "SPECIAL"; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 1; // Indicates the END Multiplier for this power
    private static boolean dynamic = true;
    private static String description = "Negatively adjusts the number of character points allocated to a group of "
    + "stats and/or abilities.  Stats and abilites drained will, over time, "
    + "fade back to their base cost.";
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "([0-9]*d6).*", new Object[] { "DrainDie", String.class}},
        { ".*: ([0-9]*d6).*", new Object[] { "DrainDie", String.class}},
        { "Affects: Single Power, \\+0", null },
        //hd
        { "Drain (.*) ([0-9]*d6).*", new Object[] { "ImportDrainFrom", String.class,"DrainDie", String.class}},
        { ".* ([0-9]*d6).*", new Object[] { "DrainDie", String.class}},
        
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
    public powerDrain()  {
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
        String die = (String)parameterList.getParameterValue("DrainDie");
        
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
        ability.addDiceInfo( "DrainDie", die, "Drain Amount");
        
        // Add A Damage Class Info
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        ability.add("Ability.DOESKNOCKBACK", "FALSE", true);
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        String die = (String)parameterList.getParameterValue("DrainDie");
        
        return die + " Drain";
    }
    
    /** Attempt to identify power
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     */
    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();
        
        if ( power != null && ( power.equals( "DRAIN" ) || power.equals( "Drain" ) )){
            return 10;
        }
        return 0;
    }
    
    public void triggerPower(BattleEvent be, Ability ability, DetailList effectList, Target target, int refNumber, String targetGroup) throws BattleEventException {
        ParameterList parameterList = getParameterList(ability);
        
        long decayInterval = getDecayInterval(ability);
        int decayRate = getDecayRate(ability);
        
        Dice die = be.getDiceRoll("DrainDie", targetGroup);
        
        
        ActivationInfo ai = be.getActivationInfo();
        int tindex = ai.getTargetIndex(refNumber, targetGroup);
        
        // Grab the New AdjustmentList
        AdjustmentList newList = (AdjustmentList)ai.getIndexedValue(tindex, "Target", "DRAINFROMLIST");
        
        // Grab the effectDrainTracker && Old AdjustmentList
        effectDrainTracker eat = findDrainTracker( be.getSource(), ability);
        AdjustmentList oldList = null;
        if ( eat != null ) {
            oldList = eat.getAdjustmentList(target);
        }
        else {
            // And effectAidTracker didn't exist, so install one here...
            eat = new effectDrainTracker();
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
                        be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Restoring Drained Character Point to " + oldAdjustable + " since only " + maximumAdjustables + " Ability(s) or Stat(s) can be drained a one time.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Restoring Drained Character Point to " + oldAdjustable + " since only " + maximumAdjustables + " Ability(s) or Stat(s) can be drained a one time.", BattleEvent.MSG_NOTICE)); // .addMessage( "Restoring Drained Character Point to " + oldAdjustable + " since only " + maximumAdjustables + " Ability(s) or Stat(s) can be drained a one time.", BattleEvent.MSG_NOTICE);
                        fts[oindex].fadeAdjustment(be, ability, true);
                    }
                }
            }
        }
        
        // Calculate the Drain Adjustment
        double adjustmentAmount = die.getStun().doubleValue();
        
        // Create New Adjustment Effects here
        int index, count;
        count = newList.getAdjustableCount();
        for(index=0;index<count;index++) {
            Adjustable adjustable = newList.getAdjustableObject(index);
            int percentage = newList.getAdjustablePercentage(index);
            double amount = Math.round( adjustmentAmount / 100 * percentage);
            Ability adjust = (Ability) adjustable;
            tirggerAdjustmentsForUnifiedPowers(be, ability, effectList, target, decayInterval, decayRate, newList,
					adjust, amount);
            effectDrain ea = new effectDrain(adjustable, be.getSource(), ability, amount, decayInterval, decayRate);
            effectList.createIndexed("Effect", "EFFECT", ea);
        }
        
        // Change to the New List from the Old list
        be.addUndoableEvent( eat.setAdjustmentList(target, newList) );
    }
    
    
    private void tirggerAdjustmentsForUnifiedPowers(BattleEvent be, Ability ability, DetailList effectList,
			Target target, long decayInterval, int decayRate, AdjustmentList newList, Ability adjust, double amount) {
		String powerSource;
		int i =adjust.findLimitation("Unified Power");
		if(i> -1)
		{
			Limitation up = adjust.getLimitation(i);
			powerSource = up.getParameterList().getParameterStringValue("powerSource");
			ArrayList<Ability> unifiedAbilities =(ArrayList<Ability>) target.getValue("Unified." + powerSource);
			for (int j = 0; j < unifiedAbilities.size(); j++) {
				Ability unified  = unifiedAbilities.get(j); 
		        newList.createIndexed("Adjustable", "ADJUSTABLE", unified);
		        effectDrain ea = new effectDrain(unified, be.getSource(), ability,amount , decayInterval, decayRate);
		    	effectList.createIndexed("Effect", "EFFECT", ea);
			}  		
		}
	}
    public AttackTreeNode getTriggerPowerNode(BattleEvent be, Target target, String TargetGroup, int refNumber) {
        Ability ability = be.getAbility();
        ParameterList parameterList = getParameterList(ability);
        DrainFromAttackTreeNode node = null;
        node = new DrainFromAttackTreeNode("Drain Target");
        node.setTargetReferenceNumber(refNumber);
        
        return node;
    }
    
    // Special Handling is necessary to configure the ability.
    public ParameterList getParameterList(Ability ability, int not_used) {
        ParameterList pl = super.getParameterList(ability,not_used);
        if ( pl.getCustomEditor("DrainFrom") == null) {
            pl.setCustomEditor("DrainFrom", new AdjParameterEditor(ability, pl, "DrainFrom", ADJ_CONFIG_DRAIN_FROM));
        }
        return pl;
    }
    
    public Object[][] getImportPatterns() {
        return patterns;
    }
    

    public Object[][] getCostArray(Ability ability) {
        return costArray;
    }
    
    /** Returns a Description of the Power
     */
    public String getDescription() {
        return description;
    }
    

    public boolean isDynamic() {
        return dynamic;
    }

    public String[] getCaveatArray() {
        return caveats;
    }
    

    public Object[] getCustomAddersArray() {
        return customAdders;
    }
    
    static public effectDrainTracker findDrainTracker(Target source, Ability ability) {
        int index = source.getEffectCount() - 1;
        for(; index >=0; index--) {
            Effect e = source.getEffect(index);
            if ( e instanceof effectDrainTracker && ((effectDrainTracker)e).getAbility().equals(ability) ) {
                return (effectDrainTracker)e;
            }
        }
        return null;
    }
    
    public String getAdjustableParameterName() {
        return "DrainFrom";
    }
    
   
}