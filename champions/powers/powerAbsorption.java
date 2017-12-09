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
import champions.PADDialog;
import champions.Target;
import champions.adjustmentPowers.AdjustmentPower;
import champions.adjustmentPowers.adjustmentTree.AbsorbToAttackTreeNode;
import champions.adjustmentPowers.adjustmentTree.AdjParameterEditor;
import champions.attackTree.AttackTreeNode;
import champions.event.PADValueEvent;
import champions.exception.BadDiceException;
import champions.exception.BattleEventException;
import champions.interfaces.Advantage;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.IndexIterator;
import champions.interfaces.Limitation;
import champions.interfaces.PADValueListener;
import champions.interfaces.Undoable;

import champions.parameters.ParameterList;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;



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
public class powerAbsorption extends AdjustmentPower implements ChampionsConstants {
    static final long serialVersionUID = -2429064681241102920L;
    
    static public String[] DamageTypeOptions = { "Physical","Energy"};
    
    static private Object[][] parameterArray = {
        { "AbsorbDie", "Power.ABSORBDIE", String.class, "1d6", "Absorb Dice", DICE_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED },
        { "IncreasedMax","Power.INCREASEMAX", Integer.class, new Integer(0), "Increased Maximum Absorb", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        { "ImportMaximum","Power.IMPORTMAXIMUM", Integer.class, new Integer(0), "Maximum Absorb From Import", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        { "DamageType","Power.DAMAGETYPE", String.class, "Energy", "Damage Type", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", DamageTypeOptions },
        { "AbsorbTo","AbsorbTo*.OBJECT", Object.class, null, "Absorb Destination", LIST_PARAMETER, VISIBLE, ENABLED, REQUIRED}
    };
    
    // Cost Array - See Power.getCostArray()
    static private Object[][] costArray = {
        { "AbsorbDie", NORMAL_DICE_COST, DYNAMIC_RECONFIG, ALL_RECONFIG, new Integer(5), new Integer(0), new Integer(0) },
        { "IncreasedMax", GEOMETRIC_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(1), new Integer(2), new Integer(0), new Integer(0) }
    };
    
    
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "([0-9]*d6) .*, Max. ([0-9]*).*", new Object[] { "AbsorbDie", String.class, "ImportMaximum", Integer.class}},
        { "Type.*: (.*)", new Object[] { "DamageType", String.class} },
        { "Affects: Single Power, \\+0", null },
        //HeroDesigner
        //Absorption 2d6  (energy, STR)
        //Absorption 2d6  (physical, BODY)
        //Increased Maximum (+2 points)
        { ".* ([0-9]*d6) .*", new Object[] { "AbsorbDie", String.class }},
        { "Option: (.*)", new Object[] { "DamageType", String.class }},
        { "INCREASEDMAX ([0-9]*).*", new Object[] { "ImportMaximum", Integer.class }},
        { ".* ([0-9]*d6).*\\((.*), (.*)\\).*", new Object[] { "AbsorbDie", String.class, "DamageType", String.class, "AbsorbTo", String.class }},
        { ".* (\\+1).*\\((.*), (.*)\\).*", new Object[] { "AbsorbDie", String.class, "DamageType", String.class, "AbsorbTo", String.class }},
        { ".* ([0-9]* 1/2d6).*\\((.*), (.*)\\).*", new Object[] { "AbsorbDie", String.class, "DamageType", String.class, "AbsorbTo", String.class }},
        { "Absorption ([0-9]*d6\\+1).*\\((.*), (.*)\\).*", new Object[] { "AbsorbDie", String.class, "DamageType", String.class, "AbsorbTo", String.class }},
        { "Increased Maximum \\(\\+([0-9]*).*", new Object[] { "ImportMaximum", Integer.class }},
        { "\\+1/2 d6.*", null },
        { "\\+1 pip.*", null },
        { "\\+1d6 -1.*", null },
        { "LEVELS:.*", null },
    };
    
    // Known Caveats Array
    private static String[] caveats = {
        "Absorbed Character Points are applied immediately to the Absorption target, instead of waiting to the end of segment, " +
        "as dictated by 5th Edition Rules.  This is espeically noticable when Points are being applied to defenses.",
        "Multiple Absorptions can absorb from the same attack.  According to 5th Edition Rules, only one Absorption power can "  +
        "can absorb from any single attack.  However, HCS does guarantee that two or more Absorption powers won't absorb the same " +
        "body from a single attack.",
        "Area Of Effect is not supported with regards to Absorption powers.",
        "Absorption of Autofire damage is not compliant with 5th Edition Rules.  Specifically, only the large shot of an autofire " +
        "attack should be absorbed directly.  Additional attacks should add +1 to the absorption amount.  Current, every attack " +
        "is absorbed fully."
    };
    
    // Custom Adders
    private static Object[] customAdders = {
        Advantage.class, "Variable Effect",
        Advantage.class, "Varying Effect",
        Limitation.class, "Limited Phenomena"
    };
    
    // Power Definition Variables
    private static String powerName = "Absorption"; // The Name of the Power
    private static String targetType = "SINGLE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "PERSISTENT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "INSTANT"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = -1; // The Point cost of a single Damage Class of this power.
    private static String attackType = null; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = null; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 0; // Indicates the END Multiplier for this power
    private static boolean dynamic = true;
    private static String description = "Absorbs a portion of the body damage applied against an individual and applies a "
    + "corresponding number of character points to a group of "
    + "stats and/or abilities.  Absorbed character point will, over time, "
    + "fade back to their base cost.";
    
    transient private Action assignAction = null;
    
    /** Creates new powerHandToHandAttack */
    public powerAbsorption()  {
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
        String die = (String)parameterList.getParameterValue("AbsorbDie");
        Integer increasedMax = (Integer)parameterList.getParameterValue("IncreasedMax");
        Integer increasedMaxImport = (Integer)parameterList.getParameterValue("ImportMaximum");
        String damagetype = (String)parameterList.getParameterValue("DamageType");
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure.
        
        if (die.endsWith(" 1/2d6")) {
            die = new String(die.replaceFirst(" 1/2d6","�d6"));
            parameterList.setParameterValue("AbsorbDie", die);
        }
        if (die.endsWith(" +1")) {
            die = new String(die.replaceFirst("d6 ","d6"));
            parameterList.setParameterValue("AbsorbDie", die);
        }

        
        if ( Dice.isValid(die) == false ) {
            //System.out.println( die );
            return false;
        }
        
        // No Validation Necessary
        
        //HeroDesigner Import addition to convert the OPTION information to the proper case (03/02/04 -kjr)
        if ( damagetype.equals("ENERGY") || damagetype.equals("energy")) {
            parameterList.setParameterValue("DamageType", "Energy");
        }
        else if ( damagetype.equals("PHYSICAL") || damagetype.equals("physical")) {
            parameterList.setParameterValue("DamageType", "Physical");

        }
        
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
        ability.addDiceInfo( "AbsorbDie", die, "Amount Absorbed by " + ability.getName() + " this Phase", STUN_ONLY, "Absorbed Amount", "");
        // Add A Damage Class Info
        //ability.add("Base.DC",  new Double(ChampionsUtilities.StringToNormalDC(die)), true);
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        try {
            Dice d = new Dice(die, false);
            ability.add( "Power.MAXIMUMADJUSTMENT", new Integer( d.getD6() * 6 + increasedMax.intValue() ), true);
        }
        catch ( BadDiceException bde ) {
            ability.add( "Power.MAXIMUMADJUSTMENT", new Integer( 0 ), true);
        }
        
        // If character abilities are being modified, set targetType = SELF
        ability.setTargetSelf( true );
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    
    public void triggerPower(BattleEvent be, Ability ability, DetailList effectList, Target target, int refNumber, String targetGroup) throws BattleEventException {
        ParameterList parameterList = getParameterList(ability);
        
        if ( be.getActivationInfo().isContinuing() == false ) {
            ActivationInfo ai = be.getActivationInfo();
            int tindex = ai.getTargetIndex(refNumber, targetGroup);
            
            // Grab the New AdjustmentList
            AdjustmentList newList = (AdjustmentList)ai.getIndexedValue(tindex, "Target", "ABSORBTOLIST");
            
            // Grab the effectAbsorbTracker && Old AdjustmentList
            effectAbsorption effect = new effectAbsorption(ability,newList);
            
            Dice d = be.getDiceRoll("AbsorbDie", targetGroup);
            effect.setMaximumAbsorptionForPhase(d.getStun().intValue());
            
            effectList.createIndexed("Effect", "EFFECT", effect);
            
        }
        else {
            effectAbsorption effect = findEffectAbsorption(target, ability);
            
            if ( effect != null ) {
                Dice d = be.getDiceRoll("AbsorbDie", targetGroup);
                Undoable u = effect.setMaximumAbsorptionForPhase(d.getStun().intValue());
                be.addUndoableEvent(u);
            }
        }
    }
    
    public AttackTreeNode getTriggerPowerNode(BattleEvent be, Target target, String TargetGroup, int refNumber) {
        Ability ability = be.getAbility();
        
        AbsorbToAttackTreeNode node = null;
        node = new AbsorbToAttackTreeNode("Absorb Target");
        node.setTargetReferenceNumber(refNumber);
        
        return node;
        
    }
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        String die = (String)parameterList.getParameterValue("AbsorbDie");
        Integer increasedMax = (Integer)parameterList.getParameterValue("IncreasedMax");
        
        String s = die + " Absorb to: ";
        int index = 0;
        int count = 0;
        
        if (increasedMax.intValue() > 0) {
            try {
                Dice d = new Dice( die );
                s = s + "absorb a maximum of " + (d.getD6() *6 + 2 * increasedMax.intValue());
            }
            catch (BadDiceException bde) {
                s = "error";
            }
        }
        else {
            try {
                Dice d = new Dice( die );
                s = s + "absorb a maximum of " + d.getD6() *6;
            }
            catch (BadDiceException bde) {
                s = "error";
            }
        }
        
        return s;
    }
    
    
        /** Attempt to identify power
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     */
    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();
        
        if ( power != null && ( power.equals( "ABSORPTION" ) || power.equals( "Absorption" ) )){
            return 10;
        }
        return 0;
    }

    // Special Handling is necessary to configure the ability.
    public ParameterList getParameterList(Ability ability, int not_used) {
        ParameterList pl = super.getParameterList(ability,not_used);
        if ( pl.getCustomEditor("AbsorbTo") == null) {
            pl.setCustomEditor("AbsorbTo", new AdjParameterEditor(ability, pl, "AbsorbTo", ADJ_CONFIG_ABSORB_TO));
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
//                            List absorbToList = pl.getIndexedParameterValues("AbsorbTo");
//                            if ( absorbToList.contains(c) == false ) {
//                                // This is a new one, so add it to the parameter list...
//                                //pl.createIndexed("AbsorbTo", "OBJECT", c, false);
//                                pl.addIndexedParameterValue("AbsorbTo", c);
//                                pl.setParameterSet("AbsorbTo", true);
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
//                                List absorbToList = pl.getIndexedParameterValues("AbsorbTo");
//                                if ( absorbToList.contains(p) == false ) {
//                                    // This is a new one, so add it to the parameter list...
//                                    //pl.createIndexed("AbsorbTo", "OBJECT", p, false);
//                                    pl.addIndexedParameterValue("AbsorbTo", p);
//                                    pl.setParameterSet("AbsorbTo", true);
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
//                                List absorbToList = pl.getIndexedParameterValues("AbsorbTo");
//                                if ( absorbToList.contains(s) == false ) {
//                                    // This is a new one, so add it to the parameter list...
//                                    //pl.createIndexed("AbsorbTo", "OBJECT", s, false);
//                                    pl.addIndexedParameterValue("AbsorbTo", s);
//                                    pl.setParameterSet("AbsorbTo", true);
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
//                        int cindex = pl.createIndexed("AbsorbTo", "NAME", stringValue, false);
//                        pl.addIndexed(cindex, "AbsorbTo", "LINE", new Integer(lineIndex), true, false );
//                    }
//                }
//                
//                // If all the specified AbsorbTo types were used, mark the line as used.
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
//        int count = parameterList.getIndexedSize("AbsorbTo");
//        for ( int index = 0; index < count; index++) {
//            Object o = (Object) parameterList.getIndexedValue(index, "AbsorbTo", "OBJECT");
//            String name = parameterList.getIndexedStringValue(index, "AbsorbTo", "NAME");
//            
//            if ( o == null && name != null ) {
//                // Here is a name with no associated ability...see if you can find an ability...
//                Ability a = source.getAbility(name);
//                if ( a != null ) {
//                    parameterList.addIndexed(index, "AbsorbTo", "OBJECT", a, true, false);
//                    parameterList.removeIndexed(index, "AbsorbTo", "NAME", false);
//                    parameterList.removeIndexed(index, "AbsorbTo", "LINE", false);
//                }
//                else {
//                    a = Battle.getDefaultAbilities().getAbility(name, true);
//                    if ( a != null ) {
//                        parameterList.addIndexed(index, "AbsorbTo", "OBJECT", a, true, false);
//                        parameterList.removeIndexed(index, "AbsorbTo", "NAME", false);
//                        parameterList.removeIndexed(index, "AbsorbTo", "LINE", false);
//                    }
//                    else {
//                        Integer line = parameterList.getIndexedIntegerValue(index, "AbsorbTo", "LINE");
//                        ai.setLineUnused(line.intValue());
//                        parameterList.removeAllIndexed(index, "AbsorbTo", false);
//                        index--;
//                    }
//                    
//                }
//            }
//        }
//        
//        count = parameterList.getIndexedSize("AbsorbTo");
//        parameterList.setParameterSet("AbsorbTo", count > 0);
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
    
    /**
     * Returns a String[] of Caveats about the Power
     * Power uses this method to automatically build the getCaveats()
     * String.  The Strings returns by getCaveatArray() will be assembled into
     * list form and returned via getCaveats().
     * 
     * Return an empty array if there are no known caveats for this power.
     */
    public String[] getCaveatArray() {
        return caveats;
    }
    
    public void addActions(Vector v, final Ability ability) {
        if ( ability.hasAdvantage("Varying Effect") ) {
            if ( assignAction == null ) assignAction = new powerAbsorption.ChangeAbsorptionTypeAction(ability.getName() + " Damage Type", ability);
            v.add(assignAction);
        }
    }
    
    public boolean invokeMenu( JPopupMenu popup, final Ability ability) {
        if ( ability.hasAdvantage("Varying Effect") ) {
            if ( assignAction == null ) assignAction = new powerAbsorption.ChangeAbsorptionTypeAction(ability.getName() + " Damage Type", ability);
            JMenuItem menu = new JMenuItem(assignAction);
            popup.add( menu );
            return true;
        }
        
        return false;
    }
        
    
    /** Returns an Array of Objects, representing custom/special advantages, limitations, special parameters usable with the power.
     * The Array should be in the format of class type (limitation, advantage, special) followed by the class name, repeated for
     * each additional custom added.  For example:
     *  array[] = { Limitation.class, "limitationLimitedSpecialFX", Advantage.class, "advantageVariableEffect" };
     */
    public Object[] getCustomAddersArray() {
        return customAdders;
    }
    
    public int getMaximumAdjustment(Ability ability) {
        return ability.getIntegerValue("Power.MAXIMUMADJUSTMENT").intValue();
    }
    
    
    
    static private effectAbsorption findEffectAbsorption(Target target, Ability absorptionAbility ) {
        int eindex = target.getEffectCount() - 1;
        for (; eindex >= 0; eindex--) {
            Effect effect = target.getEffect(eindex);
            if ( effect instanceof effectAbsorption ) {
                // Try to find the Target effectAbsorption that belongs to this ability.
                effectAbsorption ea = (effectAbsorption)effect;
                if ( ea.getAbility().equals( absorptionAbility ) ) {
                    return ea;
                }
            }
        }
        
        return null;
    }
    
    /* The follow section provides access methods for the Dice Rolls needed by absorption every time a
     * target is hit.
     */
    
    /* Finds the Index of the approparite AbsorptionRoll.
     */
    public static int getAbsorptionAmountIndex(BattleEvent be, String targetGroup, int targetReferenceNumber, Ability absorbAbility) {
        IndexIterator ii = be.getIteratorForIndex("AbsorptionAmount",
        new Object[][]  {
            {"TARGETGROUP", targetGroup},
            {"REFERENCENUMBER", new Integer(targetReferenceNumber) },
            {"ABILITY", absorbAbility }
        });
        
        int aindex = -1;
        
        if ( ii.hasNext() ) {
            aindex = ii.nextIndex();
        }
        
        return aindex;
    }
    
    public static int addAbsorptionAmountEntry(BattleEvent be, String targetGroup, int targetReferenceNumber, Ability absorbAbility) {
        int aindex = getAbsorptionAmountIndex(be, targetGroup, targetReferenceNumber, absorbAbility);
        if ( aindex == -1 ) {
            aindex = be.createIndexed("AbsorptionAmount", "TARGETGROUP", targetGroup, false);
            be.addIndexed(aindex, "AbsorptionAmount", "REFERENCENUMBER", new Integer(targetReferenceNumber), true, false);
            be.addIndexed(aindex, "AbsorptionAmount", "ABILITY", absorbAbility, true, false);
        }
        
        return aindex;
    }
    
    public static int addAbsorptionAmountAbsorbed(BattleEvent be, String targetGroup, int targetReferenceNumber, Ability absorbAbility, int amount) {
        int aindex = getAbsorptionAmountIndex(be, targetGroup, targetReferenceNumber, absorbAbility);
        if ( aindex == -1 ) {
            aindex = be.createIndexed("AbsorptionAmount", "TARGETGROUP", targetGroup, false);
            be.addIndexed(aindex, "AbsorptionAmount", "REFERENCENUMBER", new Integer(targetReferenceNumber), true, false);
            be.addIndexed(aindex, "AbsorptionAmount", "ABILITY", absorbAbility, true, false);
        }
        
        be.addIndexed(aindex, "AbsorptionAmount", "AMOUNTABSORBED", new Integer(amount), true, false);
        
        return aindex;
    }
    
    public static void setAbsorptionAmountAbsorbed(BattleEvent be, String targetGroup, int targetReferenceNumber, Ability absorbAbility, int amount) {
        int aindex = addAbsorptionAmountEntry(be,targetGroup,targetReferenceNumber,absorbAbility);
        if ( aindex != -1 ) {
            be.addIndexed(aindex, "AbsorptionAmount", "AMOUNTABSORBED", new Integer(amount), true, false);
        }
    }
    
    public static int getAbsorptionAmountAbsorbed(BattleEvent be, String targetGroup, int targetReferenceNumber, Ability absorbAbility) {
        int aindex = getAbsorptionAmountIndex(be, targetGroup, targetReferenceNumber, absorbAbility);
        if ( aindex != -1 ) {
            Integer i = be.getIndexedIntegerValue(aindex, "AbsorptionAmount", "AMOUNTABSORBED");
            return ( i == null ) ? 0 : i.intValue();
        }
        else {
            return 0;
        }
    }
    
    public static void setAbsorptionAbsorbedBody(BattleEvent be, String targetGroup, int targetReferenceNumber, Ability absorbAbility, int amount, int eindex) {
        int aindex = addAbsorptionAmountEntry(be,targetGroup,targetReferenceNumber,absorbAbility);
        if ( aindex != -1 ) {
            be.addIndexed(aindex, "AbsorptionAmount", "ABSORBEDBODY", new Integer(amount), true, false);
            be.addIndexed(aindex, "AbsorptionAmount", "ABSORBEDBODYSUBEFFECTINDEX", new Integer(eindex), true, false);
        }
    }
    
    public static int getAbsorptionAbsorbedBody(BattleEvent be, String targetGroup, int targetReferenceNumber, Ability absorbAbility) {
        int aindex = getAbsorptionAmountIndex(be, targetGroup, targetReferenceNumber, absorbAbility);
        if ( aindex != -1 ) {
            Integer i = be.getIndexedIntegerValue(aindex, "AbsorptionAmount", "ABSORBEDBODY");
            return ( i == null ) ? 0 : i.intValue();
        }
        else {
            return 0;
        }
    }
    
    public static int getAbsorptionAbsorbedBodySubeffectIndex(BattleEvent be, String targetGroup, int targetReferenceNumber, Ability absorbAbility) {
        int aindex = getAbsorptionAmountIndex(be, targetGroup, targetReferenceNumber, absorbAbility);
        if ( aindex != -1 ) {
            Integer i = be.getIndexedIntegerValue(aindex, "AbsorptionAmount", "ABSORBEDBODYSUBEFFECTINDEX");
            return ( i == null ) ? -1 : i.intValue();
        }
        else {
            return -1;
        }
    }

    public String getAdjustableParameterName() {
        return "AbsorbTo";
    }
    
    public class ChangeAbsorptionTypeAction extends AbstractAction {
        
        /** Holds value of property ability. */
        private Ability ability;     
        
        public ChangeAbsorptionTypeAction(String name, Ability ability) {
            super(name);
            setAbility(ability);
        }
        
        public void actionPerformed(ActionEvent e) {
            final Ability currentAbility = ability.getInstanceGroup().getCurrentInstance();
            String damagetype = currentAbility.getStringValue("Power.DAMAGETYPE");

            ParameterList parameterList = getParameterList(ability);

            ParameterList pl = new ParameterList();
            pl.addComboParameter( "DamageType", "Power.DAMAGETYPE", "Damage Type", damagetype, new String[] {"Physical", "Energy"} );

            PADDialog pd = new PADDialog(null);
            PADValueListener pvl = new PADValueListener() {
                public void PADValueChanged(PADValueEvent evt) {
                    currentAbility.add("Power.DAMAGETYPE", evt.getValue(), true);
                }

                public boolean PADValueChanging(PADValueEvent evt) {
                    return true;
                }
            };
            int result = pd.showPADDialog( "Adjust Absorption Damage Type", pl, currentAbility, pvl);

            
            // Put back the original value if the user cancels.
            if ( result != JOptionPane.OK_OPTION ) {
                currentAbility.add("Power.DAMAGETYPE", damagetype, true);
            }
        }   
        
        /** Getter for property ability.
         * @return Value of property ability.
         */
        public Ability getAbility() {
            return this.ability;
        }
        
        /** Setter for property ability.
         * @param ability New value of property ability.
         */
        public void setAbility(Ability ability) {
            this.ability = ability;
        }
        
    }
}