/*
 * powerHandToHandAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
 */

package champions.powers;

import champions.*;
import champions.Power;
import champions.SpecialEffect;
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
public class powerSuppress extends AdjustmentPower implements ChampionsConstants {
    static final long serialVersionUID =5295848483348707401L;
    
    
    private static Object[][] parameterArray = {
        {"DrainDie","Power.DRAINDIE", String.class, "1d6", "Drain Dice", DICE_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"DrainFrom","DrainFrom*.OBJECT", Object.class, null, "Drain Source", LIST_PARAMETER, VISIBLE, ENABLED, REQUIRED},
        {"ImportDrainFrom","Power.IMPORTDRAINFROM", String.class, "Import Drain From", "Import Drain From", STRING_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED}
        
    };
    
    // Cost Array - See Power.getCostArray()
    static private Object[][] costArray = {
        { "DrainDie", NORMAL_DICE_COST, DYNAMIC_RECONFIG, ALL_RECONFIG, new Integer(5), new Integer(0), new Integer(0) },
    };
    
    // Power Definition Variables
    private static String powerName = "Supress"; // The Name of the Power
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
//        Limitation.class, "Limited Special Effect",
//        Limitation.class, "Self Only",
//        Limitation.class, "Others Only",
//        Limitation.class, "PD Applies",
//        Limitation.class, "ED Applies",
    };
    
    // Known Caveats Array
    private static String[] caveats = {
    };
    
    /** Creates new powerHandToHandAttack */
    public powerSuppress()  {
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
        
        return die + " Suppress";
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
            
            effectDrain ea = new effectDrain(adjustable, be.getSource(), ability, Math.round( adjustmentAmount / 100 * percentage), decayInterval, decayRate);
            effectList.createIndexed("Effect", "EFFECT", ea);
        }
        
        // Change to the New List from the Old list
        be.addUndoableEvent( eat.setAdjustmentList(target, newList) );
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
    
//    public void importPower(Ability ability, AbilityImport ai) {
//        // First Do the standard import.  This will parse the RegExp andpopulate
//        super.importPower(ability,ai);
//        
//        ParameterList pl = getParameterList(ability);
//        
//        String stringValue, affectsType;
//        String line;
//        
//        
//        for(int lineIndex = 0; lineIndex < ai.getImportLineCount(); lineIndex++) {
//            
//            //commented out this line to allow the import to pull drainfrom information
//            //don't know the ramifications of doing this in the long run
//            // if ( ai.isLineUsed(lineIndex) ) continue;
//            
//            line = ai.getImportLine(lineIndex);
//
//            //first match is for HD drainfrom within the power description...rest are MC
//            if ( ChampionsMatcher.matches( "(.*) [0-9]*d6.*vs. (.*)\\).*", line ) || ChampionsMatcher.matches( "Drain (.*) [0-9]*d6.*\\((.*)\\).*", line ) || ChampionsMatcher.matches( "Adjust Single Power/Stat \\((.*)\\): (.*)", line ) || ChampionsMatcher.matches( "Affects \\((.*)\\): (.*)", line ) || ChampionsMatcher.matches( "Variable Effect \\((.*)\\) \\((.*)", line ) ) {
//                stringValue = ChampionsMatcher.getMatchedGroup(1);
//                affectsType = ChampionsMatcher.getMatchedGroup(2);
//                
//                StringTokenizer st = new StringTokenizer(stringValue, ",");
//                
//                boolean used = true;
//                while ( st.hasMoreTokens() ) {
//                    String token = st.nextToken();
//                    
//                    // Cut out beginning and ending spaces, if any
//                    if ( token.startsWith( " " ) ) {
//                        token = token.substring(1);
//                    }
//                    
//                    if ( token.endsWith( " " ) ) {
//                        token = token.substring(0, token.length()-1);
//                    }
//                    
//                    // Search the Possible Sources for this Token...
//                    
//                    boolean found = false;
//                    
//                    // First look for a Stat...
//                    
//                    for(int i = 0; i< Characteristic.characteristicNames.length; i++) {
//                        if ( token.equals(Characteristic.characteristicNames[i]) ) {
//                            Characteristic c = new Characteristic(token);
//                            if ( pl.findIndexed("DrainFrom", "OBJECT", c) == -1 ) {
//                                // This is a new one, so add it to the parameter list...
//                                pl.createIndexed("DrainFrom", "OBJECT", c, false);
//                                pl.setParameterSet("DrainFrom", true);
//                            }
//                            found = true;
//                        }
//                    }
//                    
//                    if ( ! found ) {
//                        // Not a stat, so iterate through the Powers Types...
//                        Iterator i = PADRoster.getAbilityIterator();
//                        while(i.hasNext()) {
//                            String name = (String)i.next();
//                            if ( name.equals( token ) ){
//                                Ability a = PADRoster.getSharedAbilityInstance(name);
//                                Power p = a.getPower();
//                                if ( pl.findIndexed("DrainFrom", "OBJECT", p) == -1 ) {
//                                    // This is a new one, so add it to the parameter list...
//                                    pl.createIndexed("DrainFrom", "OBJECT", p, false);
//                                    pl.setParameterSet("DrainFrom", true);
//                                }
//                                found = true;
//                            }
//                        }
//                    }
//                    
//                    if ( ! found ) {
//                        // Not a power, so iterate through the known Special Effects...
//                        Iterator i = PADRoster.getSpecialEffectIterator();
//                        while(i.hasNext()) {
//                            String name = (String)i.next();
//                            if ( name.equals( token ) ){
//                                SpecialEffect s = PADRoster.getSharedSpecialEffectInstance(name);
//                                if ( pl.findIndexed("DrainFrom", "OBJECT", s) == -1 ) {
//                                    // This is a new one, so add it to the parameter list...
//                                    pl.createIndexed("DrainFrom", "OBJECT", s, false);
//                                    pl.setParameterSet("DrainFrom", true);
//                                }
//                                found = true;
//                            }
//                        }
//                    }
//                    
//                    if ( ! found ) {
//                        // It could be a specific ability, so store the ability information and
//                        // clean it up in the finalizeImport method, after all the other abilities
//                        // have been loaded.
//                        int cindex = pl.createIndexed("DrainFrom", "NAME", stringValue, false);
//                        pl.addIndexed(cindex, "DrainFrom", "LINE", new Integer(lineIndex), true, false );
//                    }
//                }
//                
//                // If all the specified DrainFrom types were used, mark the line as used.
//                // Only mark Single Power affects lines as used, since the Advantage will
//                // need to use the line also.
//                if ( used && affectsType.equals("Single Power, +0") || affectsType.equals("Single Power): +0") || affectsType.equals("+0" ) ){
//                    ai.setLineUsed(lineIndex, this);
//                }
//            }
//        }
//        
//    }
//    
//    /* Finishes Importing Ability.
//     *
//     * This method is called after the character has been completely built, with all abilities
//     * that are going to be added already added.  This method can be used to finalize any necessary
//     * ability changes, such as translating from Strings to actual Ability objects.
//     *
//     * This method should return true if it wants the configurePAD to be run once it is done
//     * finalizing the method.
//     */
//    public boolean finalizeImport(Ability ability, AbilityImport ai) {
//        ParameterList parameterList = getParameterList(ability);
//        Target source = ability.getSource();
//        boolean set = false;
//        
//        int count = parameterList.getIndexedSize("DrainFrom");
//        for ( int index = 0; index < count; index++) {
//            Object o = (Object) parameterList.getIndexedValue(index, "DrainFrom", "OBJECT");
//            String name = parameterList.getIndexedStringValue(index, "DrainFrom", "NAME");
//            
//            if ( o == null && name != null ) {
//                // Here is a name with no associated ability...see if you can find an ability...
//                Ability a = source.getAbility(name);
//                if ( a != null ) {
//                    parameterList.addIndexed(index, "DrainFrom", "OBJECT", a, true, false);
//                    parameterList.removeIndexed(index, "DrainFrom", "NAME", false);
//                    parameterList.removeIndexed(index, "DrainFrom", "LINE", false);
//                }
//                else {
//                    a = Battle.getDefaultAbilities().getAbility(name, true);
//                    if ( a != null ) {
//                        parameterList.addIndexed(index, "DrainFrom", "OBJECT", a, true, false);
//                        parameterList.removeIndexed(index, "DrainFrom", "NAME", false);
//                        parameterList.removeIndexed(index, "DrainFrom", "LINE", false);
//                    }
//                    else {
//                        Integer line = parameterList.getIndexedIntegerValue(index, "DrainFrom", "LINE");
//                        ai.setLineUnused(line.intValue());
//                        parameterList.removeAllIndexed(index, "DrainFrom", false);
//                        index--;
//                    }
//                    
//                }
//            }
//        }
//        
//        count = parameterList.getIndexedSize("DrainFrom");
//        parameterList.setParameterSet("DrainFrom", count > 0);
//        
//        return true;
//    }
//    
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
    
    /*static public Vector getAvailableAdjustables(Ability sourceAbility, Target target) {
        Vector v = new Vector();
        
        // First Grab all of the Abilities
        AbilityIterator ai = target.getAbilities();
        int index, count;
        
        String indexName = "DrainFrom";
        
        ParameterList pl = sourceAbility.getPower().getParameterList(sourceAbility, -1);
        count = pl.getIndexedParameterSize( indexName );
        
        Object[] adjustables = new Object[count];
        
        for( index = 0; index < count; index++ ) {
            // Why do we do this?  Wouldn't it be better to 
            // do a double iteration with this as the outer loop?
            // The only thing we are saving this way is that we 
            // only have to walk the ability tree once...
            adjustables[index] = pl.getIndexedParameterValue(indexName, index);
        }
        
        // Now Grab all of the available stats...
        for( index = 0; index < count; index++ ) {
            if ( adjustables[index] instanceof Characteristic ) {
                if ( target.hasStat( ((Characteristic)adjustables[index]).getName() ) ) {
                    v.add( adjustables[index] );
                }
                adjustables[index] = null;
            }
        }
        
        while ( ai.hasNext() ) {
            Ability targetAbility = ai.nextAbility();
            
            for(index = 0; index < count; index++) {
                if ( adjustables[index] != null  && isAbilityAdjustable(targetAbility, adjustables[index]) ) {
                    v.add( targetAbility );
                    break;
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
}