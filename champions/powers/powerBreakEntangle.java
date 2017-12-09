/*
 * powerEgoAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
 */

package champions.powers;

import champions.*;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.MutableListModel;
import champions.parameters.ParameterList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import champions.exception.*;
import champions.interfaces.*;
import champions.*;
import champions.event.*;
import champions.exception.*;
import champions.exception.BadDiceException;
import champions.exception.BattleEventException;
import champions.exceptionWizard.ExceptionWizard;
import champions.interfaces.*;
import champions.powers.effectAdjusted;
import champions.powers.effectUnconscious;
import champions.senseFilters.SensesOnlySenseFilter;
import champions.senseTree.STSensePanel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.io.Serializable;


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
 */
public class powerBreakEntangle extends Power implements ChampionsConstants {
    static final long serialVersionUID =5295848463348707401L;
    
    //creates the default list of classes for use in the power gui
    static private String[] classOptions = {
        "Human", "Animal", "Machine","Alien"
    };
    
    private static Object[][] parameterArray = {
        {"DamageDie","Power.DAMAGEDIE", String.class, "0d6", "Damage Dice", DICE_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
                //lists powers chooses classes
        {"AdditionalClasses","AdditionalClasses*.CLASS", String.class, null, "Class of Minds", MUTABLE_LIST_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED
                 , "OPTIONS", classOptions, "BUTTON1", "Add Class..."},
                //  {"StunOnly","Power.STUNONLY", String.class, "TRUE", "Stun Only", BOOLEAN_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED}
                 {"ClassTotal","Power.CLASSTOTAL", Integer.class, new Integer(0), "Total Classes", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
    };
    
    // Cost Array - See Power.getCostArray()
    static private Object[][] costArray = {
        { "DamageDie", NORMAL_DICE_COST, DYNAMIC_RECONFIG, ALL_RECONFIG, new Integer(10), new Integer(0), new Integer(0) },
        { "ClassTotal", GEOMETRIC_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(10), new Integer(1), new Integer(0), new Integer(0) },
    };
    
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        //hd
        { ".* ([0-9]*d6),.*", new Object[] { "DamageDie", String.class}},
        { "([0-9]*d6).*", new Object[] { "DamageDie", String.class}}
    };
    
    // Known Caveats Array
    private static String[] caveats = {
        "None known."
    };
    
    // Custom Adders
    private static Object[] customAdders = {
        //        Limitation.class, "Self Only",
        //        Limitation.class, "Limited By Senses"
    };
    // Power Definition Variables
    private static String powerName = "Break Entangle"; // The Name of the Power
    private static String targetType = "SINGLE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "ATTACK"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = -1; // The Point cost of a single Damage Class of this power.
    private static String attackType = "RANGED"; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = "NORMAL"; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 0; // Indicates the END Multiplier for this power
    private static boolean dynamic = true;
    private static String description = "Ego Attack";
    
    /** Creates new powerEgoAttack */
    public powerBreakEntangle()  {
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
    
    //    /** Configures the ability according to the parameters in parameterList.
    //     * The parameterList should be stored with the ability for configuration
    //     * later on. If an existing parameterList alread exists, it should be
    //     * replaced with this one.
    //     *
    //     * All value/pairs should be copied into the ability for direct access.
    //     */
    //    public boolean configurePAD(Ability ability, ParameterList parameterList) {
    //        // Fail immediately if ability is null
    //        if ( ability == null ) return false;
    //
    //        // Always Set the ParameterList to the parameterList
    //        setParameterList(ability,parameterList);
    //
    //        // Read in any parameters that will be needed to configure the power or
    //        // Determine the validity of the power configuration.  Read the parameters
    //        // from the parameterList, instead of directly from the ability, since the
    //        // Ability isn't configured yet.
    //        String die = (String)parameterList.getParameterValue("DamageDie");
    //        //String defense = (String)parameterList.getParameterValue("Defense");
    //        String stunOnly = (String)parameterList.getParameterValue("StunOnly");
    //
    //        // Check for the validity of the parameters that will be set.  If the parameters
    //        // Fail for any reason, return false from the method immediately, indicating a
    //        // failure to configure
    //        if ( Dice.isValid(die) == false ) {
    //            return false;
    //        }
    //        /*
    //        if ( !defense.equals("PD") && !defense.equals("ED") ) {
    //            return false;
    //        }*/
    //
    //        // Always copy the configuration parameters directly into the ability.  This will
    //        // take the parameters stored in the parameter list and copy them into the
    //        // ability using the appropriate keys and values.
    //        parameterList.copyValues(ability);
    //
    //        // Start to Actually Configure the Power.
    //        // The Add Power Info should always be executed to add information to the ability.
    //        // All of this information should be set in the Power Definition Variables at the
    //        // top of this file
    //        ability.addPowerInfo( this, powerName, targetType, persistenceType, activationTime);
    //        if ( attackType != null ) {
    //            ability.addAttackInfo( attackType,damageType );
    //            ability.add("Ability.PPDC", new Double(pointsPerDC), true);
    //        }
    //        ability.setGenerateDefaultEffects(generateDefaultDamage);
    //        if ( endMultiplier != 1 ) ability.setENDMultiplier(endMultiplier);
    //
    //        // Add any dice information which is necessary to use this power.
    //        ability.addDiceInfo( "DamageDie", die, "Ego Attack Damage");
    //
    //        // Add A Damage Class Info
    //        ability.add("Base.DC",  new Double(ChampionsUtilities.StringToNormalDC(die)), true);
    //
    //        // Add Extra Value/Pairs used by the Power/BattleEngine
    //        ability.add("Ability.CANSPREAD", "FALSE" , true );
    //        ability.add("Ability.DOESBODY", "FALSE", true);
    //        ability.add("Power.DEFENSE", "MD", false);
    //        ability.add("Ability.CVTYPE", "EGO", true);
    //        //        ability.add("Target.USESHITLOCATION",  "FALSE",  true);
    //        ability.add("Ability.USESHITLOCATION",  "FALSE",  true);
    //        ability.setHasRangeModifier(false);
    //
    //        // Update the Ability Description based on the new configuration
    //        ability.setPowerDescription( getConfigSummary(ability, -1));
    //
    //        // Return true to indicate success
    //        return true;
    //    }
    
    public boolean configurePAD(Ability ability, ParameterList parameterList) {
        // Fail immediately if ability is null
        //if ( ability == null ) return false;
        ability.addDiceInfo("EntangleDamage", "0d6","Entangle Damage");
        
        // Always Set the ParameterList to the parameterList
        setParameterList(ability,parameterList);
        //grab the number of classes choosen in power
        //        int grouptotal = parameterList.getIndexedSize("SenseGroup" );
        
        
        // Read in any parameters that will be needed to configure the power or
        // Determine the validity of the power configuration.  Read the parameters
        // from the parameterList, instead of directly from the ability, since the
        // Ability isn't configured yet.
        String die = (String)parameterList.getParameterValue("DamageDie");
        //    String targeting = (String)parameterList.getParameterValue("Targeting");
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        // No validation necessary
        
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
        
        // Add A Damage Class Info
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        ability.setAutoSource(true);
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    
    
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        String die = (String)parameterList.getParameterValue("DamageDie");
        //  String defense = (String)parameterList.getParameterValue("Defense");
        //   String stunOnly = (String)parameterList.getParameterValue("StunOnly");
        
        //  String stunString = (stunOnly.equals("TRUE")) ? ", stun only":"";
        
        return die + " Break Entangle";
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
    
    public void adjustDice(BattleEvent be, String targetGroup) {
        String die;
        //    static public Dice egoToDamage(int activeCost, Dice base, int Ego, boolean unlimitedDC) {
        //name = source.adjustDamageClass(0, "0d6", true, (int)(source.getCurrentStat("STR") *1.5));
        
        //(int)be.getSource().getCurrentStat("EGO")
        
        //  static public double egoToDCs(int Str) {
        Ability a = be.getAbility();
        ParameterList parameterList = getParameterList(a);
        //Dice base = new Dice(0);
        //Dice dice = ChampionsUtilities.egoToDamage(0,base,(int)be.getSource().getCurrentStat("EGO"),true);
        
        //double egoDCs = ChampionsUtilities.egoToDCs(be.getSource().getCurrentStat("EGO"));
        //die = (String)parameterList.getParameterValue("DamageDie");
        //be.add("Maneuver.DC", new Double(egoDCs), true);
        
        Target source = be.getSource();
        if (source.hasStat("EGO")) {
            double ego = source.getCurrentStat("EGO");
            double amount = ChampionsUtilities.roundHalf(ego / 5);
            String s;
            if (amount == (int)amount){
                s = Integer.toString((int)amount)+ "d6";
            } else {
                s = Double.toString(amount)+ "d6";
            }
            
            int d = be.getDiceIndex("EntangleDamage", targetGroup);
            be.setDiceSize(d,s);
            
        }
        
    }
    public void adjustTarget(BattleEvent be, Target source, int referenceNumber, String targetGroup, int primaryTargetNumber) {
        Target target = (Target)be.getValue("BattleEvent.SQUEEZEDOBJECT");
        
        if ( primaryTargetNumber > 0 && target != null ) {
            int index = be.getActivationInfo().addTarget(target, targetGroup, referenceNumber);
            be.getActivationInfo().setTargetFixed(index, true, target.getName() + " is being squeezed by " + source.getName());
            be.getActivationInfo().setTargetHitOverride(index, true, "Automatic hit: Follow-up to grab", source.getName() + " is squeezing " + target.getName() + ".  " + target.getName() + " was previously grabbed, so hit is automatic.");
            
        }
    }
    
}