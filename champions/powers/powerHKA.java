/*
 * powerHandToHandAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
 */

package champions.powers;

import champions.*;
import champions.interfaces.*;
import champions.exception.*;
import champions.event.*;
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
 */
public class powerHKA extends Power implements ChampionsConstants {
    static final long serialVersionUID = 7766760920350027676L;
    
    static private Object[][] parameterArray = {
        {"DamageDie","Power.DAMAGEDIE", String.class, "1d6", "Damage Dice", DICE_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"Defense","Power.DEFENSE", String.class, "PD", "Defense Type", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", new String[] {"PD","ED"}}

    };
    
    // Cost Array - See Power.getCostArray()
    static private Object[][] costArray = {
        { "DamageDie", KILLING_DICE_COST, DYNAMIC_RECONFIG, ALL_RECONFIG, new Integer(5), new Integer(0), new Integer(0) },
    };
     
    // Known Caveats Array
    private static String[] caveats = {
        
    };
     
    // Power Definition Variables
    private static String powerName = "Hand-To-Hand Killing Attack"; // The Name of the Power
    private static String targetType = "SINGLE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "ATTACK"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = 5; // The Point cost of a single Damage Class of this power.
    private static String attackType = "MELEE"; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = "KILLING"; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = true; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 1; // Indicates the END Multiplier for this power
    private static boolean dynamic = true;
    private static String description = "HtH Killing Attack";
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        
        //{ "([0-9]*d6\\+.).*", new Object[] { "DamageDie", String.class}},
        //{ "([0-9]*d6).*", new Object[] { "DamageDie", String.class}},
        { "(\\+1).*", new Object[] { "DamageDie", String.class}},
        { "([0-9]*\\?d6\\+?[0-9]?).*", new Object[] { "DamageDie", String.class}},
        // { "([0-9]*\\?d6).*", new Object[] { "DamageDie", String.class}}
        { "Versus: ([PE]D)", new Object[] { "Defense", String.class } },
        { "(\\+1).*", new Object[] { "DamageDie", String.class}},
        { "([0-9]*\\?d6\\+?[0-9]?).*", new Object[] { "DamageDie", String.class}},
        { "([0-9]* \\?d6\\+?[0-9]?).*", new Object[] { "DamageDie", String.class}},
        { ".*: ([0-9]*\\?d6\\+?[0-9]?).*", new Object[] { "DamageDie", String.class}},
        { ".*: ([0-9]* \\?d6\\+?[0-9]?).*", new Object[] { "DamageDie", String.class}},
        { "Versus: ([PE]D)", new Object[] { "Defense", String.class } },
        //hd
        //{ ".* ([0-9]* 1/2d6\\+?[0-9]?).*", new Object[] { "DamageDie", String.class}},
        { ".* ([0-9]*d6\\+1).*\\(vs. ([PE]D)\\).*", new Object[] { "DamageDie", String.class,"Defense", String.class }},
        { ".* ([0-9]*d6\\-1).*\\(vs. ([PE]D)\\).*", new Object[] { "DamageDie", String.class,"Defense", String.class }},
        { ".* ([0-9]*d6).*\\(vs. ([PE]D).*\\).*", new Object[] { "DamageDie", String.class,"Defense", String.class }},
        { ".* ([0-9]* 1/2d6).*\\(vs. ([PE]D)\\).*", new Object[] { "DamageDie", String.class,"Defense", String.class }},
        { ".*( 1/2d6).*\\(vs. ([PE]D)\\).*", new Object[] { "DamageDie", String.class,"Defense", String.class }}, 
        { ".* ([0-9]*d6\\+1).*", new Object[] { "DamageDie", String.class }},
        { ".* ([0-9]*d6\\-1).*", new Object[] { "DamageDie", String.class }},
        { ".* ([0-9]*d6).*", new Object[] { "DamageDie", String.class }},
        { ".* ([0-9]* 1/2d6).*", new Object[] { "DamageDie", String.class }},
        { ".*( 1/2d6).*", new Object[] { "DamageDie", String.class }},
        { ".*\\(vs. ([PE]D)\\).*", new Object[] { "Defense", String.class }},
        { "\\+1/2 d6.*", null },
        { "\\+1 pip.*", null },
        { "\\+1 OCV.*", null },
        { "\\+1d6 -1.*", null },
        { "LEVELS.*", null },
        { ".* ([0-9]* 1/2d6).*", new Object[] { "DamageDie", String.class,"Defense", String.class }},
        { "LEVELS:.*", null },
        //Killing Attack - Hand-To-Hand 1 1/2d6 (3d6 + 1 w/STR),        
        //Killing Attack - Hand-To-Hand
    };
    
    /** Creates new powerHandToHandAttack */
    public powerHKA()  {
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
        String die = (String)parameterList.getParameterValue("DamageDie");
        String defense = (String)parameterList.getParameterValue("Defense");
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        if (die.endsWith(" 1/2d6")) {
            die = new String(die.replaceFirst(" 1/2d6","\u00BDd6"));
            parameterList.setParameterValue("DamageDie", die);
        }
        if (die.endsWith(" +1")) {
            die = new String(die.replaceFirst("d6 ","d6"));
            parameterList.setParameterValue("DamageDie", die);
        }

        
        if ( Dice.isValid(die) == false ) {
            //System.out.println( die );
            return false;
        }
       /* else { */
      /*  try {
            Dice d = new Dice(die,false);
            die = d.toString();
            parameterList.setParameterValue("DamageDie", die);
        }
        catch ( BadDiceException bde ) {
            return false;
        } */
        
//        if ( !defense.equals("PD") && !defense.equals("ED") ) {
//            return false;
//        }
        
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
     //   ability.addDiceInfo( "DamageDie", die, "Hand-to-Hand Killing Attack Damage", STUN_ONLY, "Body Damage", null);
      //  ability.addDiceInfo( "StunDie", "1D6-1", "Hand-to-Hand Killing Attack Stun Multiplier", STUN_ONLY, "Stun Multiplier", null);
        
        // Add A Damage Class Info
        ability.add("Base.DC",  new Double(ChampionsUtilities.StringToKillingDC(die)), true);
        
        
        // The code below fixes up the defense in cases where Based on Ego Combat Value and MD
        // defense is being used.  Normally we don't want to do this kind of fixup, but no
        // other clean solution presented itself.  Note, this is due to the fact that the advantage
        // is not always configured when the power is, so the MD value can be reset incorrectly.
        //
        // Hide/Showing the defense parameter is just icing.
        boolean usesMD = false;
        int aIndex;
        if ( (aIndex = ability.findAdvantage("Based on Ego Combat Value")) != -1) {
            Advantage a = ability.getAdvantage(aIndex);
            //ParameterList apl = (ParameterList)ability.getIndexedValue(aIndex,"Advantage","PARAMETERLIST");
            ParameterList apl = a.getParameterList();
            if ( apl != null ) {
                Object vsMD = apl.getParameterValue("vsMD");
                if ( vsMD != null && vsMD.equals("TRUE") ) {
                    usesMD = true;
                }
            }
        }
        
        if ( usesMD ) {
            parameterList.setVisible("Defense", false);
            ability.add("Power.DEFENSE", "MD", true);
        }
        else {
            parameterList.setVisible("Defense", true);
        }
        
        /*
        if (ability.getValue("Ability.STUNMULTIPLIER") != null) {
            Integer stunmultiplier = (Integer)ability.getValue("Ability.STUNMULTIPLIER");
            Integer finalmultiplier = new Integer(stunmultiplier.intValue() -1 );
            ability.addDiceInfo( "StunDie", "1D6+" + finalmultiplier.toString(), "Ranged Killing Attack Stun Multiplier");
        }
        
        else {
            ability.addDiceInfo( "StunDie", "1D6-1", "Ranged Killing Attack Stun Multiplier");
            
        } */
         
        // Add Extra Value/Pairs used by the Power/BattleEngine
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    
/*    public int calculateCPCost(Ability ability) {
        ParameterList parameterList = getParameterList(ability);
        String die = (String)parameterList.getParameterValue("DamageDie");
        String defense = (String)parameterList.getParameterValue("Defense");
      //  try {
            double levels = ChampionsUtilities.StringToKillingDC(die);
            
          //  Dice d = new Dice( die );
            return (int)levels * 5;
       // }
        //catch (BadDiceException bde) {
          //  return 0;
        }//
    }
*/    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        String die = (String)parameterList.getParameterValue("DamageDie");
        //String defense = (String)parameterList.getParameterValue("Defense");
        String defense = ability.getStringValue("Power.DEFENSE");
        return die + "HKA (" + defense + ")";
    }
    
    /** Attempt to identify power
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     */
    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();
        
        if ( power != null && ( power.equals( "HKA" ) || power.equals( "RHKA" ) || power.startsWith("Killing Attack - Hand-To-Hand" ) || power.equals( "Killing Attack (HTH)" )) ){
            return 10;
        }
        return 0;
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
}