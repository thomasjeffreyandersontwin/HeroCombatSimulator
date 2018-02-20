/*
 * powerHandToHandAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
 */

package champions.powers;

import champions.*;
import champions.PADRoster.AbilityTemplate;
import champions.adjustmentPowers.Adjustable;
import champions.adjustmentPowers.AdjustmentPower;
import champions.adjustmentPowers.adjustmentTree.AdjParameterEditor;
import champions.adjustmentPowers.adjustmentTree.DrainFromAttackTreeNode;
import champions.interfaces.*;
import champions.exception.*;
import champions.attackTree.*;
import champions.parameters.ParameterList;

import java.util.*;


public class powerDispel extends AdjustmentPower implements ChampionsConstants {
    static final long serialVersionUID =5295848483348707401L;
    
    
    private static Object[][] parameterArray = {
        {"DrainDie","Power.DRAINDIE", String.class, "1d6", "Drain Dice", DICE_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"DrainFrom","DrainFrom*.OBJECT", Object.class, null, "Drain Source", LIST_PARAMETER, VISIBLE, ENABLED, REQUIRED},
        {"ImportDrainFrom","Power.IMPORTDRAINFROM", String.class, "Import Drain From", "Import Drain From", STRING_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED}
        
    };

    static private Object[][] costArray = {
        { "DrainDie", NORMAL_DICE_COST, DYNAMIC_RECONFIG, ALL_RECONFIG, new Integer(3), new Integer(0), new Integer(0) },
    };
    
    // Power Definition Variables
    private static String powerName = "Dispel"; // The Name of the Power
    private static String targetType = "SINGLE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "ATTACK"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = 3; // The Point cost of a single Damage Class of this power.
    private static String attackType = null; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = "SPECIAL"; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 1; // Indicates the END Multiplier for this power
    private static boolean dynamic = true;
    private static String description = "Negatively adjusts the number of character points allocated to a group of "
    + "stats and/or abilities.  Stats and abilites Dispeled will, over time, "
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
    };
    
    // Known Caveats Array
    private static String[] caveats = {
    };
    
    /** Creates new powerHandToHandAttack */
    public powerDispel()  {
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
        setParameterList(ability,parameterList);
        
        String die = (String)parameterList.getParameterValue("DrainDie");
        parameterList.copyValues(ability);
        
        ability.addPowerInfo( this, powerName, targetType, persistenceType, activationTime);
        if ( attackType != null ) {
            ability.addAttackInfo( attackType,damageType );
            ability.add("Ability.PPDC", new Double(pointsPerDC), true);
        }
        ability.setGenerateDefaultEffects(generateDefaultDamage);
        if ( endMultiplier != 1 ) ability.setENDMultiplier(endMultiplier);

        ability.addDiceInfo( "DrainDie", die, "Drain Amount");
        ability.add("Ability.DOESKNOCKBACK", "FALSE", true);
        ability.setPowerDescription( getConfigSummary(ability, -1));

        return true;
    }
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        String die = (String)parameterList.getParameterValue("DrainDie");
        
        return die + " Dispel";
    }
    
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

      //add any unified powers
        String powerSource;
        int index, count;
        count = newList.getAdjustableCount();
        for(index=0;index<count;index++) {
            Adjustable adjustable = newList.getAdjustableObject(index);
        	Ability adjust = (Ability) adjustable;
        	int percentage = newList.getAdjustablePercentage(index);
        	double amount = Math.round( adjustmentAmount / 100 * percentage);
        	tirggerAdjustmentsForUnifiedPowers(be, ability, effectList, target, decayInterval, decayRate, newList,
					adjust, amount);
        	effectDrain ea = new effectDrain(adjustable, be.getSource(), ability,amount , decayInterval, decayRate);
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