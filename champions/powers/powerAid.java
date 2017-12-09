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
import champions.DetailList;
import champions.Dice;
import champions.Effect;
import champions.FadeTracker;
import champions.Target;
import champions.adjustmentPowers.Adjustable;
import champions.adjustmentPowers.AdjustmentPower;
import champions.adjustmentPowers.adjustmentTree.AdjParameterEditor;
import champions.adjustmentPowers.adjustmentTree.AidToAttackTreeNode;
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
public class powerAid extends AdjustmentPower implements ChampionsConstants {
    static final long serialVersionUID = -2429064681241102920L;
    
    static private String[] statOptions = {"STR","CON","DEX","BODY","INT","EGO","PRE","COM","PD",
    "ED","SPD","REC","END","STUN","MD","rPD","rED", "OCV", "DCV", "ECV"};
    
    static private Object[][] parameterArray = {
        { "AidDie", "Power.AIDDIE", String.class, "1d6", "Aid Dice", DICE_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED },
        { "IncreasedMax","Power.INCREASEMAX", Integer.class, new Integer(0), "Increased Maximum Aid", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        { "ImportMaximum","Power.IMPORTMAXIMUM", Integer.class, new Integer(0), "Maximum Aid From Import", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        { "ImportAidFrom","Power.IMPORTAIDFROM", String.class, "Import Aid From", "Import Aid From", STRING_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        { "Succor","Power.Succor", Boolean.class, new Boolean(false), "Succor", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED },
        { "AidTo","AidTo*.OBJECT", Object.class, null, "Aid Destination", LIST_PARAMETER, VISIBLE, ENABLED, REQUIRED},

    };
    
    // Cost Array - See Power.getCostArray()
    static private Object[][] costArray = {
        { "AidDie", NORMAL_DICE_COST, DYNAMIC_RECONFIG, ALL_RECONFIG, new Integer(10), new Integer(0), new Integer(0) },
        { "IncreasedMax", GEOMETRIC_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(1), new Integer(2), new Integer(0), new Integer(0) },


    };
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "([0-9]*d6) .*, Max. ([0-9]*).*", new Object[] { "AidDie", String.class, "ImportMaximum", Integer.class}},
        { "Affects: Single Power, \\+0", null },
        //hd
        { ".* ([0-9]*d6).*", new Object[] { "AidDie", String.class}},
        { "AID ([0-9]*d6) (.*).*", new Object[] { "AidDie", String.class, "AidTo", Object.class}},
    };
    
    // Known Caveats Array
    private static String[] caveats = {
        
    };
    
    // Custom Adders
    private static Object[] customAdders = {
        Advantage.class, "Variable Effect",
        Limitation.class, "Limited Special Effect",
        Limitation.class, "Self Only",
        Limitation.class, "Others Only",
    };
    
    // Power Definition Variables
    private static String powerName = "Aid"; // The Name of the Power
    private static String targetType = "SINGLE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "ATTACK"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = -1; // The Point cost of a single Damage Class of this power.
    private static String attackType = null; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = null; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 0; // Indicates the END Multiplier for this power
    private static boolean dynamic = true;
    private static String description = "Positively adjusts the number of character points allocated to a group of "
    + "stats and/or abilities.  Stats and abilites aided beyond their base character point cost will, over time, "
    + "fade back to their base cost.";
    
    /** Creates new powerHandToHandAttack */
    public powerAid()  {
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
        String die = (String)parameterList.getParameterValue("AidDie");
        boolean isSuccor = (Boolean)parameterList.getParameterValue("Succor");
        Integer increasedMax = (Integer)parameterList.getParameterValue("IncreasedMax");
        Integer increasedMaxImport = (Integer)parameterList.getParameterValue("ImportMaximum");
        //boolean isSuccor = succor.equals("TRUE");
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure.
        
        // No Validation Necessary
        
        // Since the Imported Maximum equals max dice roll + increased max, figure increased max based
        // on the Dice roll...
        if ( increasedMaxImport != null && increasedMaxImport.intValue() > 0) {
            try {
                Dice d = new Dice( die );
                parameterList.setParameterValue("IncreasedMax", new Integer( (increasedMaxImport.intValue() - d.getD6()*6) ) );
                parameterList.setParameterValue("ImportMaximum", new Integer(0) );
                increasedMax = (Integer)parameterList.getParameterValue("IncreasedMax");

            }
            catch (BadDiceException bde) {
            }
        }
        
        // Always copy the configuration parameters directly into the ability.  This will
        // take the parameters stored in the parameter list and copy them into the
        // ability using the appropriate keys and values.
        parameterList.copyValues(ability);
        
     //   parameterList.setVisible("AidTo", ! ability.hasAdvantage("Variable Effect"));
        
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
        ability.addDiceInfo( "AidDie", die, "Aid Amount");
        
        // Add A Damage Class Info
        //ability.add("Base.DC",  new Double(ChampionsUtilities.StringToNormalDC(die)), true);
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        try {
            Dice d = new Dice(die, false);
            ability.add( "Power.MAXIMUMAID", new Integer( d.getD6() * 6 + increasedMax.intValue() ), true);
        }
        catch ( BadDiceException bde ) {
            ability.add( "Power.MAXIMUMAID", new Integer( 0 ), true);
        }
        
        
        
   /*     if ( ability.hasAdvantage( PADRoster.getAdvantageInstance("Adjustment Affects") ) == false ) {
            ability.addPAD( PADRoster.newAdvantageInstance("Adjustment Affects"), null);
        } */
        
        // If character abilities are being modified, set targetType = SELF
        //  ability.setTargetSelf( ability.getIndexedSize("AidTo") > 0 );
        
        // Show/Hide the Stat/Ability list if healing is used/not used.
        if ( isSuccor ) {
            ability.setPType("CONSTANT", true);
            ability.setENDMultiplier(1);
            ability.add("Ability.DECAYRATE", new Integer(0), true);
        }
        else {
            ability.setPType("INSTANT", true);
            //ability.add("Ability.PTYPE", "INSTANT", true, false);
            ability.setENDMultiplier(0);
            if ( getDecayRate(ability) == 0 ) ability.remove("Ability.DECAYRATE");
        }
        
        ability.add("Ability.DOESKNOCKBACK", "FALSE", true);
        
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    
    public void triggerPower(BattleEvent be, Ability ability, DetailList effectList, Target target, int refNumber, String targetGroup) throws BattleEventException {
        ParameterList parameterList = getParameterList(ability);
        
        
        Dice aidDice;
        // Integer stun = new Integer(bodyDice.getStun().intValue());
        long decayInterval = getDecayInterval(ability);
        int decayRate = getDecayRate(ability);
        
        aidDice = be.getDiceRoll("AidDie", targetGroup);
        
        // This is a real aid!!!
        int maximumAid = getMaximumAdjustables(ability);

        ActivationInfo ai = be.getActivationInfo();
        int tindex = ai.getTargetIndex(refNumber, targetGroup);

        // Grab the New AdjustmentList
        AdjustmentList newList = (AdjustmentList)ai.getIndexedValue(tindex, "Target", "AIDTOLIST");

        // Grab the effectAidTracker && Old AdjustmentList
        effectAidTracker eat = findAidTracker( be.getSource(), ability);
        AdjustmentList oldList = null;
        if ( eat != null ) {
            oldList = eat.getAdjustmentList(target);
        }
        else {
            // And effectAidTracker didn't exist, so install one here...
            eat = new effectAidTracker();
            eat.setAbility(ability);
            eat.addEffect(be,be.getSource()); // This will take care of the undoable!
        }
        
        if ( oldList != newList ) {
            be.addUndoableEvent( eat.setAdjustmentList(target, newList) );
        }

        if ( oldList != null && getAdjustmentLevel(ability) != ADJ_VARIABLE_ALL_ADJUSTMENT ) {
            // In this case, there can be only a single adjustment, and the old adjustment must
            // immediately fade!
            
            int maximumAdjustables = getMaximumAid(ability);
            
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
                        be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Removing Aided Character Point to " + oldAdjustable + " since only " + maximumAdjustables + " Ability(s) or Stat(s) can be aided a one time.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Removing Aided Character Point to " + oldAdjustable + " since only " + maximumAdjustables + " Ability(s) or Stat(s) can be aided a one time.", BattleEvent.MSG_NOTICE)); // .addMessage( "Removing Aided Character Point to " + oldAdjustable + " since only " + maximumAdjustables + " Ability(s) or Stat(s) can be aided a one time.", BattleEvent.MSG_NOTICE);
                        fts[oindex].fadeAdjustment(be, ability, true);
                    }
                }
            }
        }

        // Calculate the Aid Adjustment
        double adjustmentAmount = aidDice.getStun().doubleValue();

        // Create New Adjustment Effects here
        int index, count;
        count = newList.getAdjustableCount();
        for(index=0;index<count;index++) {
            Adjustable adjustable = newList.getAdjustableObject(index);
            int percentage = newList.getAdjustablePercentage(index);

            effectAid ea = new effectAid(adjustable, be.getSource(), ability, Math.round( adjustmentAmount / 100 * percentage), getMaximumAid(ability) , decayInterval, decayRate);
            effectList.createIndexed("Effect", "EFFECT", ea);
        }

        // Change to the New List from the Old list
        be.addUndoableEvent( eat.setAdjustmentList(target, newList) );
    }
    
    public AttackTreeNode getTriggerPowerNode(BattleEvent be, Target target, String TargetGroup, int refNumber) {
        Ability ability = be.getAbility();
        ParameterList parameterList = getParameterList(ability);
        
        AidToAttackTreeNode node = null;
        node = new AidToAttackTreeNode("Aid Target");
        node.setTargetReferenceNumber(refNumber);
        
        return node;
        
    }
    
    /** Indicates power is shutting down.
     * All powers are shutdown prior to them deactivating.
     * @param be BattleEvent of event currently being processed.
     * @param source
     * @throws BattleEventException
     */    
    public void shutdownPower(BattleEvent be, Target source) throws BattleEventException {
        ActivationInfo ai = be.getActivationInfo();
        Ability ability = ai.getAbility();
        ParameterList pl = getParameterList(ability);
        boolean  succor = (Boolean)pl.getParameterValue("Succor");
        
        
        if ( succor ) {
            // Run through all the targets in the aidTracker...
            effectAidTracker eat = findAidTracker( be.getSource(), ability);
            if ( eat != null ) {
                int tcount = eat.getAdjustmentListCount();
                for(int tindex = 0; tindex < tcount; tindex++) {
                    Target target = eat.getAdjustmentListTarget(tindex);
                    
                    FadeTracker[] fts = target.getFadeTrackers(ability);
                    for(int oindex = 0; oindex < fts.length; oindex++) {
                        Object adjustable = fts[oindex].getAdjustable();

                        be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Removing Succored Character Point from " + adjustable + ".", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Removing Succored Character Point from " + adjustable + ".", BattleEvent.MSG_NOTICE)); // .addMessage( "Removing Succored Character Point from " + adjustable + ".", BattleEvent.MSG_NOTICE);
                        fts[oindex].fadeAdjustment(be, ability, true);
                    }
                }
                
                // Remove the AidTracker now that we are done with it...
                eat.removeEffect(be, source);
            }
       }
    }
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        String die = (String)parameterList.getParameterValue("AidDie");
        boolean  succor = (Boolean)parameterList.getParameterValue("Succor");
        Integer increasedMax = (Integer)parameterList.getParameterValue("IncreasedMax");
        
        String s = die + " Aid"; 
        
        if ( succor) {
            s = s + "(Succor)";
        } 
        s = s + ", maximum CP " + getMaximumAid(ability);
        
        return s;
    }
    
            /** Attempt to identify power
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     */
    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();
        
        if ( power != null && ( power.equals( "Aid" ) || power.equals( "AID" ) )){
            return 10;
        }
        return 0;
    }

    // Special Handling is necessary to configure the ability.
    public ParameterList getParameterList(Ability ability, int not_used) {
        ParameterList pl = super.getParameterList(ability,not_used);
        if ( pl.getCustomEditor("AidTo") == null) {
            pl.setCustomEditor("AidTo", new AdjParameterEditor(ability, pl, "AidTo", ADJ_CONFIG_AID_TO));
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
    
    public void importPower(Ability ability, AbilityImport ai) {
        // First Do the standard import.  This will parse the RegExp andpopulate
//        super.importPower(ability,ai);
//        
//        ParameterList pl = getParameterList(ability);
//        
//        String stringValue, affectsType;
//        String line;
//        
//        for(int lineIndex = 0; lineIndex < ai.getImportLineCount(); lineIndex++) {
//            
//            //this was commented out to allow import for hd export.  Likely this can be put back in
//           // if ( ai.isLineUsed(lineIndex) ) continue;
//            
//            line = ai.getImportLine(lineIndex);
//            if ( ChampionsMatcher.matches( "Aid  (.*) [0-9]d6,(.*)", line ) || ChampionsMatcher.matches( "Affects \\((.*)\\): (.*)", line ) || ChampionsMatcher.matches( ".*any \\[(.*)\\](.*)", line) ) {
//
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
//                            if ( pl.findIndexed("AidTo", "OBJECT", c) == -1 ) {
//                                // This is a new one, so add it to the parameter list...
//                                pl.createIndexed("AidTo", "OBJECT", c, false);
//                                pl.setParameterSet("AidTo", true);
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
//                                if ( pl.findIndexed("AidTo", "OBJECT", p) == -1 ) {
//                                    // This is a new one, so add it to the parameter list...
//                                    pl.createIndexed("AidTo", "OBJECT", p, false);
//                                    pl.setParameterSet("AidTo", true);
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
//                                if ( pl.findIndexed("AidTo", "OBJECT", s) == -1 ) {
//                                    // This is a new one, so add it to the parameter list...
//                                    pl.createIndexed("AidTo", "OBJECT", s, false);
//                                    pl.setParameterSet("AidTo", true);
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
//                        int cindex = pl.createIndexed("AidTo", "NAME", stringValue, false);
//                        pl.addIndexed(cindex, "AidTo", "LINE", new Integer(lineIndex), true, false );
//                    }
//                }
//                
//                // If all the specified AidTo types were used, mark the line as used.
//                // Only mark Single Power affects lines as used, since the Advantage will
//                // need to use the line also.
//                if ( used && affectsType.equals("Single Power, +0") ) {
//                    ai.setLineUsed(lineIndex, this);
//                }
//            }
//        }
    
    }
        
    /* Finishes Importing Ability.
     *
     * This method is called after the character has been completely built, with all abilities
     * that are going to be added already added.  This method can be used to finalize any necessary
     * ability changes, such as translating from Strings to actual Ability objects.
     *
     * This method should return true if it wants the configurePAD to be run once it is done
     * finalizing the method.
     */
    public boolean finalizeImport(Ability ability, AbilityImport ai) {
//        ParameterList parameterList = getParameterList(ability);
//        Target source = ability.getSource();
//        boolean set = false;
//        
//        int count = parameterList.getIndexedSize("AidTo");
//        for ( int index = 0; index < count; index++) {
//            Object o = (Object) parameterList.getIndexedValue(index, "AidTo", "OBJECT");
//            String name = parameterList.getIndexedStringValue(index, "AidTo", "NAME");
//            
//            if ( o == null && name != null ) {
//                // Here is a name with no associated ability...see if you can find an ability...
//                Ability a = source.getAbility(name);
//                if ( a != null ) {
//                    parameterList.addIndexed(index, "AidTo", "OBJECT", a, true, false);
//                    parameterList.removeIndexed(index, "AidTo", "NAME", false);
//                    parameterList.removeIndexed(index, "AidTo", "LINE", false);
//                }
//                else {
//                    a = Battle.getDefaultAbilities().getAbility(name, true);
//                    if ( a != null ) {
//                        parameterList.addIndexed(index, "AidTo", "OBJECT", a, true, false);
//                        parameterList.removeIndexed(index, "AidTo", "NAME", false);
//                        parameterList.removeIndexed(index, "AidTo", "LINE", false);
//                    }
//                    else {
//                        Integer line = parameterList.getIndexedIntegerValue(index, "AidTo", "LINE");
//                        ai.setLineUnused(line.intValue());
//                        parameterList.removeAllIndexed(index, "AidTo", false);
//                        index--;
//                    }
//                    
//                }
//            }
//        }
//        
//        count = parameterList.getIndexedSize("AidTo");
//        parameterList.setParameterSet("AidTo", count > 0);
//        
        return true;
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
    
    static public effectAidTracker findAidTracker(Target source, Ability ability) {
        int index = source.getEffectCount() - 1;
        for(; index >=0; index--) {
            Effect e = source.getEffect(index);
            if ( e instanceof effectAidTracker && ((effectAidTracker)e).getAbility().equals(ability) ) {
                return (effectAidTracker)e;
            }
        }
        return null;
    }
    
    public int getMaximumAid(Ability ability) { 
        
        return ability.getIntegerValue("Power.MAXIMUMAID").intValue();
    }
    

    public String getAdjustableParameterName() {
        return "AidTo";
    }
}