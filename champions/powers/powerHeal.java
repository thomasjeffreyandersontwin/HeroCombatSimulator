/*
 * powerHandToHandAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
 */

package champions.powers;

import champions.Ability;
import champions.AbilityImport;
import champions.ActivationInfo;
import champions.AdjustmentList;
import champions.BattleEvent;
import champions.ChampionsUtilities;
import champions.DetailList;
import champions.Dice;
import champions.Effect;
import champions.Target;
import champions.adjustmentPowers.Adjustable;
import champions.adjustmentPowers.AdjustmentPower;
import champions.adjustmentPowers.adjustmentTree.AdjParameterEditor;
import champions.adjustmentPowers.adjustmentTree.HealToAttackTreeNode;
import champions.attackTree.AttackTreeNode;
import champions.exception.BadDiceException;
import champions.exception.BattleEventException;
import champions.interfaces.Advantage;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.Limitation;
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
public class powerHeal extends AdjustmentPower implements ChampionsConstants {
    static final long serialVersionUID = -2429064681241102920L;
    
    static final private String[] healingTypes = { "Normal", "Simplified Healing", "Flash Healing", "Regeneration" };
    
    static final private String[] extraTimeOptions = { "Turn", "Minute", "Five Minutes", "Twenty Minutes", "One Hour", "Six Hours", "One Day",
    "One Week", "One Month", "One Season", "One Year", "Five Years", "25 Years" };
    
    
    static final private Object[][] parameterArray = {
        { "Type","Power.TYPE", String.class, "Normal", "Healing Type", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", healingTypes },
        { "HealDie", "Power.HEALDIE", String.class, "1d6", "Heal Dice", DICE_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED },
        { "HealTo","HealTo*.OBJECT", Object.class, null, "Heal Destination", LIST_PARAMETER, VISIBLE, ENABLED, REQUIRED},
        // Resurrection Only Parameters
        { "Rate","Power.RATE", String.class, "Turn", "Rate (Extra Time)", COMBO_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED, "OPTIONS", extraTimeOptions },
        { "CanHealLimbs","Power.HEALLIMBS", Boolean.class, new Boolean(false), "Can Heal Limbs", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED },
        { "Resurrection","Power.RESURRECTION", Boolean.class, new Boolean(false), "Resurrection", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED },
    };
    
    // Cost Array - See Power.getCostArray()
    static final private Object[][] costArray = {
        { "HealDie", NORMAL_DICE_COST, DYNAMIC_RECONFIG, ALL_RECONFIG, new Integer(10), new Integer(0), new Integer(0) },
        { "CanHealLimbs", BOOLEAN_COST, STATIC_RECONFIG, ZERO_RECONFIG, new Integer(5), new Integer(0), new Integer(0) },
        { "Resurrection", BOOLEAN_COST, STATIC_RECONFIG, ZERO_RECONFIG, new Integer(20), new Integer(0), new Integer(0) },
    };
    // Regeneration Cost Array - See Power.getCostArray()
//    static final private Object[][] regenerationCostArray = {
//        { "HealDie", NORMAL_DICE_COST, DYNAMIC_RECONFIG, ALL_RECONFIG, new Integer(10), new Integer(0), new Integer(0) },
//        { "CanHealLimbs", BOOLEAN_COST, STATIC_RECONFIG, ZERO_RECONFIG, new Integer(20), new Integer(0), new Integer(0) },
//        { "Resurrection", BOOLEAN_COST, STATIC_RECONFIG, ZERO_RECONFIG, new Integer(5), new Integer(0), new Integer(0) },
//    };
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        //  { "([0-9]*d6) .*, Max. ([0-9]*).*", new Object[] { "HealDie", String.class, "ImportMaximum", Integer.class}},
       // { "Affects: Single Power, \\+0", null }
        
        //hd
        { ".* ([0-9]*d6),.*", new Object[] { "HealDie", String.class}},
        { ".* ([0-9]*d6) (.*)", new Object[] { "HealDie", String.class, "HealTo", Object.class }},
        
    };
    
    // Known Caveats Array
    private static String[] caveats = {
        
    };
    
    // Custom Adders
    private static Object[] customAdders = {
        Advantage.class, "Variable Effect",
        Limitation.class, "Self Only",
        Limitation.class, "Others Only",
        Advantage.class, "Reduced Endurance",
        Limitation.class, "Resurrection Only",
    };
    
    // Power Definition Variables
    private static String powerName = "Healing"; // The Name of the Power
    private static String targetType = "SINGLE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "ATTACK"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = -1; // The Point cost of a single Damage Class of this power.
    private static String attackType = null; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = null; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 1; // Indicates the END Multiplier for this power
    private static boolean dynamic = true;
    private static String description = "";
    
    /** Creates new powerHandToHandAttack */
    public powerHeal()  {
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
        String die = (String)parameterList.getParameterValue("HealDie");
        String type = (String)parameterList.getParameterValue("Type");
        String rate = (String)parameterList.getParameterValue("Rate");
        
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
        
        // Based on Type, change the available options
        if ( type.equals( "Regeneration" ) ) {
            if ( "INSTANT".equals(ability.getPType()) ) {
                ability.setPType("CONSTANT", true);
            }
            ability.setActivationTime("INSTANT", true);
            ability.setNormallyOn(true);
            
            removeDice(ability);
            
            // If we want to handle flash here, we should do 
            // some extra parameter that contains the flash information...
            parameterList.setVisible("HealTo", false);
            parameterList.setVisible("Rate", true);
            
            if ( ability.hasLimitation( limitationSelfOnly.limitationName ) == false ) {
                ability.addPAD( new limitationSelfOnly(), null );
            }
        }
        else if ( type.equals( "Simplified Healing" ) ) {
            if ( "CONSTANT".equals(ability.getPType()) ) {
                ability.setPType("INSTANT", true);
            }
            ability.setActivationTime("ATTACK", true);
            
            setupDice(ability, die);
            
            parameterList.setVisible("HealTo", false);
            parameterList.removeAllIndexedParameterValues("HealTo");
            
            parameterList.setVisible("Rate", false);
            
            
        }
        else if ( type.equals( "Flash Healing" ) ) {
            ability.setPType("INSTANT", true);
            ability.setActivationTime("ATTACK", true);
            
            setupDice(ability, die);
            
            parameterList.setVisible("HealTo", false);
            parameterList.removeAllIndexedParameterValues("HealTo");
            
            parameterList.setVisible("Rate", false);
        }
        else { // Normal healing
            ability.setPType( "INSTANT", true);
            ability.setActivationTime("ATTACK", true);
            
            setupDice(ability, die);
            
            parameterList.setVisible("HealTo", true);
            parameterList.setVisible("Rate", false);
        }
        
        ability.add("Ability.DOESKNOCKBACK", "FALSE", true);
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    
    private void setupDice(Ability ability, String die) {
     // Add any dice information which is necessary to use this power.
        ability.addDiceInfo( "HealDie", die, "Heal Amount");
        
        // Add A Damage Class Info
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        try {
            Dice d = new Dice(die, false);
            ability.add( "Power.MAXIMUMHEAL", new Integer( d.getD6() * 6 ), true);
            
            // Add the simple healing values just for the hell of it.
            ability.add( "Power.MAXIMUMSTUN", new Integer( d.getD6() * 6 ), true);
            ability.add( "Power.MAXIMUMBODY", new Integer( d.getD6() * 2 ), true);
        }
        catch ( BadDiceException bde ) {
            ability.add( "Power.MAXIMUMHEAL", new Integer( 0 ), true);
            
            // Add the simple healing values just for the hell of it.
            ability.add( "Power.MAXIMUMSTUN", new Integer( 0 ), true);
            ability.add( "Power.MAXIMUMBODY", new Integer( 0 ), true);
        }   
    }
    
    private void removeDice(Ability ability){
        ability.removeDiceInfo("HealDie");
    }
    
    public void triggerPower(BattleEvent be, Ability ability, DetailList effectList, Target target, int refNumber, String targetGroup) throws BattleEventException {
        ParameterList parameterList = getParameterList(ability);
        String type = (String)parameterList.getParameterValue("Type");
        
        if ( type.equals( "Regeneration" ) ) {
            effectRegeneration er = new effectRegeneration(target, ability);
            effectList.createIndexed("Effect", "EFFECT", er);
        }
        else if ( type.equals( "Simplified Healing" ) ) {
            triggerSimpleHeal(be, ability, effectList, target, refNumber, targetGroup);
        }
        else if ( type.equals( "Flash Healing" ) ) {
        }
        else {
            triggerHeal(be, ability, effectList, target, refNumber, targetGroup);
        }
    }
    
    private void triggerHeal(BattleEvent be, Ability ability, DetailList effectList, Target target, int refNumber, String targetGroup) throws BattleEventException {
        
        Dice healDice;
        
        healDice = be.getDiceRoll("HealDie", targetGroup);
        
        // This is a real Heal!!!
        int maximumHeal = getMaximumHeal(ability);
        
        ActivationInfo ai = be.getActivationInfo();
        int tindex = ai.getTargetIndex(refNumber, targetGroup);
        
        // Grab the New AdjustmentList
        AdjustmentList newList = (AdjustmentList)ai.getIndexedValue(tindex, "Target", "HEALTOLIST");
        
        // Grab the effectHealTracker && Old AdjustmentList
        effectHealTracker eat = findHealTracker( be.getSource(), ability);
        AdjustmentList oldList = null;
        if ( eat != null ) {
            oldList = eat.getAdjustmentList(target);
        }
        else {
            // And effectHealTracker didn't exist, so install one here...
            eat = new effectHealTracker();
            eat.setAbility(ability);
            eat.addEffect(be,be.getSource()); // This will take care of the undoable!
        }
        
        if ( oldList != newList ) {
            be.addUndoableEvent( eat.setAdjustmentList(target, newList) );
        }
        
        // Calculate the Heal Adjustment
        double adjustmentAmount = healDice.getStun().doubleValue();
        
        // Create New Adjustment Effects here
        int index, count;
        count = newList.getAdjustableCount();
        for(index=0;index<count;index++) {
            Adjustable adjustable = newList.getAdjustableObject(index);
            int percentage = newList.getAdjustablePercentage(index);
            
            effectHeal ea = new effectHeal(adjustable, be.getSource(), ability, Math.round( adjustmentAmount / 100 * percentage), maximumHeal);
            effectList.createIndexed("Effect", "EFFECT", ea);
        }
        
        // Change to the New List from the Old list
        be.addUndoableEvent( eat.setAdjustmentList(target, newList) );
    }
    
    private void triggerSimpleHeal(BattleEvent be, Ability ability, DetailList effectList, Target target, int refNumber, String targetGroup) throws BattleEventException {
        
        Dice healDice;
        
        healDice = be.getDiceRoll("HealDie", targetGroup);
        
        // Calculate the Heal Adjustment
        double stunAmount = healDice.getStun().doubleValue();
        double bodyAmount = healDice.getBody().doubleValue();
        
        
        effectSimpleHeal ea = new effectSimpleHeal(be.getSource(), ability, stunAmount, bodyAmount, getMaximumStunHeal(ability), getMaximumStunHeal(ability));
        effectList.createIndexed("Effect", "EFFECT", ea);
    }
    
    public AttackTreeNode getTriggerPowerNode(BattleEvent be, Target target, String  targetGroup, int refNumber) {
        Ability ability = be.getAbility();
        ParameterList parameterList = getParameterList(ability);
        String type = (String)parameterList.getParameterValue("Type");
        
        if ( type.equals( "Regeneration" ) ) {
        }
        else if ( type.equals( "Simplified Healing" ) ) {
            // Nothing need...
        }
        else if ( type.equals( "Flash Healing" ) ) {
        }
        else {
            // Normal healing
            return getTriggerHealNode(be, target, targetGroup, refNumber);
        }
        
        return null;
    }
    
    private AttackTreeNode getTriggerHealNode(BattleEvent be, Target target, String targetGroup, int refNumber) {
        Ability ability = be.getAbility();
        ParameterList parameterList = getParameterList(ability);
        
        HealToAttackTreeNode node = null;
        node = new HealToAttackTreeNode("Healing Destination");
        node.setTargetReferenceNumber(refNumber);
        
        return node;
    }
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        String type = (String)parameterList.getParameterValue("Type");
        
        StringBuffer sb = new StringBuffer();
        
        if ( type.equals( "Regeneration" ) ) {
            String die = (String)parameterList.getParameterValue("HealDie");
            String rate = (String)parameterList.getParameterValue("Rate");
            int body = 0;
            try {
               body = new Dice(die).getD6();
            }
            catch ( BadDiceException bde ) {   
            }
            
            sb.append( "Regeneration, " ).append(body).append(" Body every ").append(rate);
        }
        else if ( type.equals( "Simplified Healing" ) ) {
            String die = (String)parameterList.getParameterValue("HealDie");
            sb.append(die);
            sb.append(" Simplified Healing");
        }
        else if ( type.equals( "Flash Healing" ) ) {
            String die = (String)parameterList.getParameterValue("HealDie");
            sb.append(die);
            sb.append(" Flash Healing");
        }
        else {
            // Normal healing
            String die = (String)parameterList.getParameterValue("HealDie");
            
            sb.append(die);
            sb.append(" Healing");
        }
        
        boolean limbs = (Boolean)parameterList.getParameterValue("CanHealLimbs");
        boolean resurrection = (Boolean)parameterList.getParameterValue("Resurrection");
        
        if ( limbs ) {
            sb.append(", Can Heal Limbs");
        }
        
        if ( resurrection ) {
            sb.append(", Resurrection");
        }
        
        return sb.toString();
    }
    
    /** Attempt to identify power
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     */
    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();
        
        if ( power != null && ( power.equals( "HEALING" ) || power.equals( "Healing" ) )){
            return 10;
        }
        return 0;
    }
    // Special Handling is necessary to configure the ability.
    public ParameterList getParameterList(Ability ability, int not_used) {
        ParameterList pl = super.getParameterList(ability,not_used);
        if ( pl.getCustomEditor("HealTo") == null) {
            pl.setCustomEditor("HealTo", new AdjParameterEditor(ability, pl, "HealTo", ADJ_CONFIG_AID_TO));
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
//        for(int lineIndex = 0; lineIndex < ai.getImportLineCount(); lineIndex++) {
//            if ( ai.isLineUsed(lineIndex) ) continue;
//            
//            line = ai.getImportLine(lineIndex);
//            if ( ChampionsMatcher.matches( "Affects \\((.*)\\): (.*)", line ) ) {
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
//                    for(int i = 0; i< Characteristic.characteristicNames.length; i++) {
//                        if ( token.equals(Characteristic.characteristicNames[i]) ) {
//                            Characteristic c = new Characteristic(token);
//                            if ( pl.findIndexed("HealTo", "OBJECT", c) == -1 ) {
//                                // This is a new one, so add it to the parameter list...
//                                pl.createIndexed("HealTo", "OBJECT", c, false);
//                                pl.setParameterSet("HealTo", true);
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
//                                if ( pl.findIndexed("HealTo", "OBJECT", p) == -1 ) {
//                                    // This is a new one, so add it to the parameter list...
//                                    pl.createIndexed("HealTo", "OBJECT", p, false);
//                                    pl.setParameterSet("HealTo", true);
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
//                                if ( pl.findIndexed("HealTo", "OBJECT", s) == -1 ) {
//                                    // This is a new one, so add it to the parameter list...
//                                    pl.createIndexed("HealTo", "OBJECT", s, false);
//                                    pl.setParameterSet("HealTo", true);
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
//                        int cindex = pl.createIndexed("HealTo", "NAME", stringValue, false);
//                        pl.addIndexed(cindex, "HealTo", "LINE", new Integer(lineIndex), true, false );
//                    }
//                }
//                
//                // If all the specified HealTo types were used, mark the line as used.
//                // Only mark Single Power affects lines as used, since the Advantage will
//                // need to use the line also.
//                if ( used && affectsType.equals("Single Power, +0") ) {
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
//        int count = parameterList.getIndexedSize("HealTo");
//        for ( int index = 0; index < count; index++) {
//            Object o = (Object) parameterList.getIndexedValue(index, "HealTo", "OBJECT");
//            String name = parameterList.getIndexedStringValue(index, "HealTo", "NAME");
//            
//            if ( o == null && name != null ) {
//                // Here is a name with no associated ability...see if you can find an ability...
//                Ability a = source.getAbility(name);
//                if ( a != null ) {
//                    parameterList.addIndexed(index, "HealTo", "OBJECT", a, true, false);
//                    parameterList.removeIndexed(index, "HealTo", "NAME", false);
//                    parameterList.removeIndexed(index, "HealTo", "LINE", false);
//                }
//                else {
//                    a = Battle.getDefaultAbilities().getAbility(name, true);
//                    if ( a != null ) {
//                        parameterList.addIndexed(index, "HealTo", "OBJECT", a, true, false);
//                        parameterList.removeIndexed(index, "HealTo", "NAME", false);
//                        parameterList.removeIndexed(index, "HealTo", "LINE", false);
//                    }
//                    else {
//                        Integer line = parameterList.getIndexedIntegerValue(index, "HealTo", "LINE");
//                        ai.setLineUnused(line.intValue());
//                        parameterList.removeAllIndexed(index, "HealTo", false);
//                        index--;
//                    }
//                    
//                }
//            }
//        }
//        
//        count = parameterList.getIndexedSize("HealTo");
//        parameterList.setParameterSet("HealTo", count > 0);
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
//        ParameterList pl = getParameterList(ability,-1);
//        String type = (String)pl.getParameterValue("Type");
//        if ( "Regeneration".equals(type) ) {
//            return regenerationCostArray;
//        }
        
        return costArray;
    }
    
        /** Returns the amount of Advantage configured in the power.
     *
     * Some Powers have inherent advantages or limitation built into them.  
     * Sometimes, it is easier to configure those with the power GUI.  This
     * method allows you to reflect those advantage/limitation multiplier costs
     * directly from the power.
     *
     * This should only return multiplier costs built into the ability.  External
     * advantages/limitation will be counted seperately.
     */
    public double getAdvantageMultiplier(Ability ability) {
        return 0;
    }
    
    /** Returns the amount of Limitation configured in the power.
     *
     * Some Powers have inherent advantages or limitation built into them.  
     * Sometimes, it is easier to configure those with the power GUI.  This
     * method allows you to reflect those advantage/limitation multiplier costs
     * directly from the power.
     *
     * This should only return multiplier costs built into the ability.  External
     * advantages/limitation will be counted seperately.
     */
    public double getLimitationMultiplier(Ability ability) {
        ParameterList pl = getParameterList(ability);
        String type = (String)pl.getParameterValue("Type");
        if ( "Regeneration".equals(type) ) {
            double limCost = -1;
            
            String rate = (String)pl.getParameterValue("Rate");
            int timeStep = ChampionsUtilities.getTimeStep( rate ) - ChampionsUtilities.getTimeStep( "Turn" );
        
            limCost -= .25 * timeStep;
            return limCost;
        }
        
        return 0;
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
    
    public String getDamagePrefix(BattleEvent be,Ability maneuver) throws BattleEventException{
        Ability ability = be.getAbility();
        ParameterList pl = getParameterList(ability);
        String type = (String)pl.getParameterValue("Type");
        if ( "Regeneration".equals(type) ) {
            return "";
        }
        
        return super.getDamagePrefix(be, maneuver);
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
    
    static public effectHealTracker findHealTracker(Target source, Ability ability) {
        int index = source.getEffectCount() - 1;
        for(; index >=0; index--) {
            Effect e = source.getEffect(index);
            if ( e instanceof effectHealTracker && ((effectHealTracker)e).getAbility().equals(ability) ) {
                return (effectHealTracker)e;
            }
        }
        return null;
    }

    
    /** Returns the Maximum amount of character points which can be healed using normal healing rules.
     */
    public int getMaximumHeal(Ability ability) {
        return ability.getIntegerValue("Power.MAXIMUMHEAL").intValue();
    }
    
    /** Returns the Maximum amount of Body which can be healed using Simple healing rules.
     */
    public int getMaximumBodyHeal(Ability ability) {
        return ability.getIntegerValue("Power.MAXIMUMBODY").intValue();
    }
    
    /** Returns the Maximum amount of Stun which can be healed using Simple healing rules.
     */
    public int getMaximumStunHeal(Ability ability) {
        return ability.getIntegerValue("Power.MAXIMUMSTUN").intValue();
    }
    
    /*static public Vector getAvailableAdjustables(Ability sourceAbility, Target target) {
        Vector v = new Vector();
        
        // First Grab all of the Abilities
        AbilityIterator ai = target.getAbilities();
        int index, count;
        
        String indexName = "HealTo";
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
    } */
    
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

    public String getAdjustableParameterName() {
        return "HealTo";
    }
}