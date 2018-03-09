/*
 * powerHandToHandAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
 */

package champions.powers;

import champions.Ability;
import champions.BattleEvent;
import champions.CVList;
import champions.ChampionsUtilities;
import champions.Power;
import champions.Target;
import champions.attackTree.AttackTreeNode;
import champions.attackTree.GenericParameterNode;
import champions.attackTree.MovementManeuverSetupNode;
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
 * 13) Add patterns array and define import patterns.<P>
 * 14) Add getImportPatterns() method.<P>
 */
public class maneuverMoveThrough extends MovementManeuver
implements ChampionsConstants {
    static final long serialVersionUID =5295848683348507403L;
    
    static private Object[][] parameterArray = {
        {"DamageDie","Power.DAMAGEDIE", String.class, "0d6", "Extra Damage Dice", DICE_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
     //   {"OCVModifier","Ability.OCVBONUS", Integer.class, new Integer(0), "Maneuver OCV Modifier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
      //  {"DCVModifier","Ability.DCVBONUS", Integer.class, new Integer(-3), "Maneuver DCV Modifier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
     //   {"MartialManeuver","Ability.ISMARTIALMANEUVER", String.class, "FALSE", "Martial Arts Skill", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
      //  {"Velocity","Power.VELOCITY", Integer.class, new Integer(0), "Velocity", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED}
    };
    
    // Power Definition Variables
    private static String powerName = "Move Through"; // The Name of the Power
    private static String targetType = "SINGLE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "ATTACK"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = 5; // The Point cost of a single Damage Class of this power.
    private static String attackType = "MELEE"; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = "NORMAL"; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = true; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 0; // Indicates the END Multiplier for this power
    
        // Import Patterns Definitions
    private static Object[][] patterns = {
    };
    
    /** Creates new powerHandToHandAttack */
    public maneuverMoveThrough()  {
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
     //   ability.addDiceInfo( "DamageDie", die, "Move Through Attack Damage");
        
        // Add A Damage Class Info
        //ability.add("Base.DC",  new Double(ChampionsUtilities.StringToNormalDC(die)), true);
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        ability.add("Power.UNLIMITEDDC", "TRUE",  true);
        ability.setIs( "MELEEMANEUVER",true);
        ability.setIs( "MOVETHROUGH" ,true);
        
        if ( ability.getDCVModifier() == 0 ) {
            ability.setDCVModifier(-3);
        }
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        String die = (String)parameterList.getParameterValue("DamageDie");

        if ( die.equals("0d6") == false && die.equals("0D6") == false ) {
            return "Move-Through (+" + die + ", OCV +v/5)";
        }
        else {
            return "Move-Through";
        }
    }
    
    
    public void adjustDice(BattleEvent be, String targetGroup) throws BattleEventException {
        Target source = be.getSource();
        
        ParameterList parameterList;
        
        int velocity;
        Integer integer;
        double dc;
        String damageDie;
        Ability a = be.getAbility();
        Ability m = be.getManeuver();
        Ability move;
        
        velocity = getVelocity(be);
        
        if ( a != null && a.getPower() == this ) {
            move = a;
            parameterList = getParameterList(a);
            damageDie = (String)parameterList.getParameterValue("DamageDie");
            dc = ChampionsUtilities.StringToNormalDC(damageDie);
        }
        else if ( m != null && m.getPower() == this )  {
            move = m;
            parameterList = getParameterList(m);
            damageDie = (String)parameterList.getParameterValue("DamageDie");
            dc = ChampionsUtilities.StringToNormalDC(damageDie);
            
            
        }
        else {
            dc = 0;
        }
        
        dc =  dc + velocity/3;
        be.add("Movement.DC",  new Double(dc), true);
        
        be.add("Multiplier.STR",  new Double ( 1 ), true);
    }
    
    public void addOCVAttackModifiers(BattleEvent be, CVList cvList, Ability attack ) {
        int ocvMod;
        Integer integer;
        
        int velocity = getVelocity(be);
        ocvMod = -1 * velocity/5;
        cvList.addSourceCVModifier( "Move-Through Velocity", ocvMod );
        
    }
    
    public String getDamagePrefix(BattleEvent be, Ability maneuver) throws BattleEventException{
        Ability ability = be.getAbility();
        Target source = be.getSource();
        String name = "";
        if ( source != null ) {
            name = ChampionsUtilities.DCToNormalString(  ChampionsUtilities.strToDCs( source.getCurrentStat("STR") ) ) + "+v/3";
        }
        return name;
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
        
       MovementManeuverSetupNode node = new MovementManeuverSetupNode("Move-Through parameters");
       return node;
    }

//    public AttackTreeNode getPostTriggerPowerNode(BattleEvent be, Target target, String TargetGroup, int refNumber) {
//        MoveThrough
//        
//    }


    

    
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


	

    
}