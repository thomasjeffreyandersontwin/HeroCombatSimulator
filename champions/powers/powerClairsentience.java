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
 * 11) Edit getName method to return powerName variable.<P>
 * 12) Change serialVersionUID by some amount.<P>
 * 13) Add patterns array and define import patterns.<P>
 * 14) Add getImportPatterns() method.<P>
 *
 * NOTE: Clairsentience needs to be updated to use sense groups and sense combinations in some way.  
 * Until Sense/Sense Group are supported by BattleEngine, I am not going to attempt 
 * to finish this completely or add import patterns.
 */
public class powerClairsentience extends Power 
implements ChampionsConstants {
    static final long serialVersionUID =5295848681348707403L;
    
    static private Object[][] parameterArray = {
        {"Senses", "Senses.ABILITY", "Clairsentience Senses", "Normal Sight", LIST_PARAMETER, VISIBLE, ENABLED, REQUIRED},
        {"IncreaseRange","Power.INCREASERANGE", Integer.class, new Integer(0) , "Increased Range Multiplier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(1)},
        {"Range","Power.RANGE", Integer.class, new Integer(0) , "Range", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(1)},
        {"Dimensions","Power.DIMENSIONS", Boolean.class, new Boolean(false), "See into Dimensions", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"Future","Power.FUTURE",  Boolean.class, new Boolean(false), "See into Future (Precognition)", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"Past","Power.PAST",  Boolean.class, new Boolean(false), "See into Past (Retrocognition)", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
    };
    
    // Power Definition Variables
    private static String powerName = "Clairsentience"; // The Name of the Power
    private static String targetType = "SELF"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "CONSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "INSTANT"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = 5; // The Point cost of a single Damage Class of this power.
    private static String attackType = null; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = null; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 0; // Indicates the END Multiplier for this power
    
    // Import Patterns Definitions
    private static Object[][] patterns = {

    };
    
    /** Creates new powerHandToHandAttack */
    public powerClairsentience()  {
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
       // ability.addDiceInfo( "DamageDie", die, "Energy Blast Damage");
        
        // Add A Damage Class Info
       // ability.add("Base.DC",  new Double(ChampionsUtilities.StringToNormalDC(die)), true);
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    
    public void triggerPower(BattleEvent be, Ability ability,DetailList effectList, Target target, int refNumber, String targetGroup) throws BattleEventException {
        Effect effect = new effectClairsentience( ability, ability.getName() );
        effectList.createIndexed(  "Effect","EFFECT",effect) ;
    }
    
    public int calculateCPCost(Ability ability) {
/*        String increaserange = (String)ability.parseParameter(parameterArray, "IncreaseRange" );
        String dimensions = (String)ability.parseParameter(parameterArray, "Dimensions");
        String future = (String)ability.parseParameter(parameterArray, "Future");
        String past = (String)ability.parseParameter(parameterArray, "Past");
        
        int cost = 15;
        int index = 0;
        int count = 0;
        
        
        
        count = ability.getIndexedSize( "Senses" );
        
        for ( index=0;index<count;index++) {
            if (ability.getIndexedStringValue(index,"Senses","ABILITY").endsWith("Group") ) {
                cost = cost +10;
            } else {
                cost = cost +5;
            }
        }
        if (future.equals("TRUE"))  {
            cost = cost +20;
        }
        if (past.equals("TRUE") ) {
            cost = cost +20;
        }
        if (dimensions.equals("TRUE") ) {
            cost = cost +20;
        }
        if (increaserange.equals("TRUE") ) {
            cost = cost +5;
        }
        
        if (cost < 20 ) {
            return 20;
        } else {
            return cost;
        }
 **/
        return 0;
    }
    
    
    
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        String increaserange = (String)parameterList.getParameterValue("IncreaseRange");
        boolean dimensions = (Boolean)parameterList.getParameterValue("Dimensions");
        boolean future = (Boolean)parameterList.getParameterValue("Future");
        boolean past = (Boolean)parameterList.getParameterValue("Past");
        //String increaserange = (String)ability.parseParameter(parameterArray, "IncreaseRange" );
        //String dimensions = (String)ability.parseParameter(parameterArray, "Dimensions");
        //String future = (String)ability.parseParameter(parameterArray, "Future");
        //String past = (String)ability.parseParameter(parameterArray, "Past");
        
        StringBuffer sb = new StringBuffer();
        sb.append( "Clairsentience with " );
        
        int index = 0;
        int count = 0;
        count = ability.getIndexedSize( "Senses" );
        for ( index=0;index<count;index++) {
            if (index > 0 ) {
                sb.append(", ");
            }
            sb.append(ability.getIndexedStringValue(index, "Senses", "ABILITY" ));
        }
        //if (dimensions.equals("TRUE") || past.equals("TRUE") || future.equals("TRUE") || increaserange.equals("TRUE") ) {
        //    sb.append(", ");
        //}
/*        if (dimensions.equals("TRUE")){
            sb.append(", See into dimensions");
        }
        if (past.equals("TRUE")) {
            sb.append(", Retrocognition");
        }
        if (future.equals("TRUE")) {
            sb.append(", Precognition");
        }
        if (increaserange.equals("TRUE")) {
            sb.append(", Double maximum range");
        }
  */      
        return sb.toString();
    }
    
   /* public ParameterList getParameters(Ability ability, int not_used) {
        ParameterList parameters = new ParameterList();
        
        String increaserange = (String)ability.parseParameter(parameterArray, "IncreaseRange" );
        String dimensions = (String)ability.parseParameter(parameterArray, "Dimensions");
        String future = (String)ability.parseParameter(parameterArray, "Future");
        String past = (String)ability.parseParameter(parameterArray, "Past");
        
        SensesListModel senseslm = new SensesListModel();
        parameters.addListParameter("Senses", "Senses.ABILITY", "Clairsentience Senses", senseslm, ability );
        
        parameters.addBooleanParameter( "Dimensions", "Power.DIMENSIONS","See into Dimensions", dimensions);
        parameters.addBooleanParameter( "Future", "Power.FUTURE","See into Future (Precognition)", future);
        parameters.addBooleanParameter( "Past", "Power.PAST","See into Past (Retrocognition)", past);
        parameters.addBooleanParameter( "IncreaseRange", "Power.INCREASERANGE", "Double Maximum Range", increaserange);
        
        
        return parameters;
    } */
        /** Attempt to identify power
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     */
    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();
        
        if ( power != null && ( power.equals( "CLAIRSENTIENCE" ) || power.equals( "Clairsentience" ) )){
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
}