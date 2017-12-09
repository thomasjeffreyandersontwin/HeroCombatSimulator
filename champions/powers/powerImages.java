 /*
  * powerInvisibility.java
  *
  * Created on September 24, 2000, 5:06 PM
  */

package champions.powers;


import champions.Ability;
import champions.Power;
import champions.Sense;
import champions.SenseListModel;
import champions.interfaces.ChampionsConstants;
import champions.parameters.ListParameter;
import champions.parameters.ParameterList;
import champions.interfaces.Limitation;






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
public class powerImages extends Power implements ChampionsConstants {
    static final long serialVersionUID =5295848483348707401L;
    
    //private static String[] fringeOptions = new String[] {"Normal Fringe","Bright Fringe","No Fringe"};
    // private static Integer[] fringeCosts = new Integer[] { new Integer(0), new Integer(0), new Integer(10)};
    
    // This should be replaced with a model that is intelligent and adjusts according
    // to what is selected....
    private static String[] senseOptions = new String[] {"BROKEN"};
    
    private static Object[][] parameterArray = {
        {"Senses","Sense*.SENSE", Sense.class, null, "Senses", LIST_PARAMETER, VISIBLE, ENABLED, REQUIRED},
        {"Level","Power.LEVEL", Integer.class, new Integer(0), "+/- PER Roll", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"AlterableSize","Power.ALTERABLESIZE", Boolean.class, new Boolean(false), "Alterable Size", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
//        {"NotAttacking","Power.NOTATTACKING", Boolean.class, new Boolean(false), "Only When Not Attacking", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
//        {"NoFringe","Power.NOFRINGE", Boolean.class, new Boolean(false), "No Fringe", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
    };
    
    // Cost Array - See Power.getCostArray()
    static private Object[][] costArray = {
//        { "Base", BASE_COST, STATIC_RECONFIG, ZERO_RECONFIG, new Integer(20), new Integer(0), new Integer(0) },
//        { "Fringe", COMBO_COST, STATIC_RECONFIG, ZERO_RECONFIG, fringeCosts, fringeOptions },
    };
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        
    };
    
    // Known Caveats Array
    private static String[] caveats = {
        "None known."
    };
    
    // Custom Adders
    private static Object[] customAdders = {
        Limitation.class, "Only When Not Attacking",
        Limitation.class, "Chameleon",
        Limitation.class, "Bright Fringe",
    };
    
    
    // Power Definition Variables
    private static String powerName = "Images"; // The Name of the Power
    private static String targetType = "SELF"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "CONSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "INSTANT"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = 5; // The Point cost of a single Damage Class of this power.
    private static String attackType = "RANGED"; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = "NORMAL"; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 1; // Indicates the END Multiplier for this power
    private static boolean dynamic = false;
    private static String description = "You can't see this.  It is invisible.";
    
    
    
    
    /** Creates new powerHandToHandAttack */
    public powerImages()  {
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
//        String die = (String)parameterList.getParameterValue("DamageDie");
//        String defense = (String)parameterList.getParameterValue("Defense");
//        String stunOnly = (String)parameterList.getParameterValue("StunOnly");
        
        //remove when there is a better mechanism for spreading and beam limitation
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        if ( ability.hasLimitation( limitationBrightFringe.limitationName ) ) {
            // Set the fringe to true and hide it...
            //parameterList.setVisible("NoFringe", false);
           //parameterList.setParameterValue("NoFringe", false);
        } else {
            // Make sure you show the fringe parameter
            // if the bright fringe lim isn't added
            //parameterList.setVisible("NoFringe", true);
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
        //  ability.addDiceInfo( "DamageDie", die, "Energy Blast Damage");
        
        // Add A Damage Class Info
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        
        
        //int count = parameterList.getIndexedSize("Sense");
        
//        if (  parameterList != null) {
//            if (parameterList.isParameterIndexed("Senses")) {
//                int count = 0;
//                Sense s = null;
//                count = parameterList.getIndexedParameterSize("Senses");
//                for (int i = 0;i < count;i++) {
//                    s = (Sense) parameterList.getIndexedParameterValue("Senses", i);
//                    sense.getCost();
//                }
//            }
//
//        }
        
        //for loop
        //get indexparametervalue
        //sense s = (sense) parametrlist.geindexparametervalue(param, index)
        //sense.getCost();
        
        
        //remove if/ese when there is a better mechanism for spreading and beam limitation
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    
    /** Returns the Character Power Cost of currently configured parameters.
     * Only the Power cost should be considered by this method.  Advantage/Limitation modifications will
     * be applied later.
     * @param ability Ability to calculate CP cost for.
     * @return integer representing Character Power Cost of the Power for Ability.
     */
    public int calculateCPCost(Ability ability) {
        ParameterList parameterList = getParameterList(ability);
        boolean alterablesize = (Boolean)parameterList.getParameterValue("AlterableSize");
       // 
        Integer level = (Integer)parameterList.getParameterValue("Level");
        int cost = 0;
        boolean isFirstSenseGroupTargetting = false;
        
        
        if (  parameterList != null) {
            if (parameterList.isParameterIndexed("Senses")) {
                int count = 0;
                Sense s = null;
                count = parameterList.getIndexedParameterSize("Senses");
                for (int i = 0;i < count;i++) {
                    s = (Sense) parameterList.getIndexedParameterValue("Senses", i);
//                    if ( s.isSenseGroup() && s.isTargettingSense() ) {
//                        isFirstSenseGroupTargetting = true;
//                        cost = cost + 10;
//                    } else if ( s.isSenseGroup() || s.isTargettingSense() ) {
//                        cost = cost + 5;
//                    } else {
//                        cost = cost + 3;
//                    }
                    cost = cost + s.getCost();
                }
                //sense.getCost();
            }
        }
//        if (isFirstSenseGroupTargetting) {
//            cost += 10;
//        } else {
//            cost += 5;
//        }
        
        cost = cost + level.intValue() * 3;
        
        if ( alterablesize ) {
            cost += 5;
        }
        return cost; //c.getCost();
    }
    
    
    
    
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        //boolean chameleon = (Boolean)parameterList.getParameterValue("Chameleon");
        //boolean fringe = (Boolean)parameterList.getParameterValue("NoFringe");
        
        boolean hasParen = false;
        StringBuffer sb = new StringBuffer("Images");
//        if ( chameleon ) {
//            sb.append( "(Chameleon" );
//            hasParen = true;
//        }
//
//        if ( ! ( "Normal Fringe".equals(fringe) ) ) {
//            if ( hasParen == false ) {
//                sb.append("(");
//            } else {
//                sb.append(", ");
//            }
//            sb.append(fringe);
//            hasParen = true;
//        }
        
        if ( hasParen == true ) sb.append(")");
        return sb.toString();
    }
    
    
// Special Handling is necessary to configure the ability.
    public ParameterList getParameterList(Ability ability, int index) {
        boolean showOnlyGroups = false;
        ParameterList pl = super.getParameterList(ability,index);
        ListParameter param = (ListParameter)pl.getParameter("Senses");
        if ( param.getModel() == null) {
            param.setModel( new SenseListModel(pl, "Senses", showOnlyGroups) );
        }
        return pl;
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
//    public double getLimitationMultiplier(Ability ability) {
//        ParameterList parameterList = getParameterList(ability);
//        boolean chameleon = (Boolean)parameterList.getParameterValue("Chameleon");
//        boolean notattacking = (Boolean)parameterList.getParameterValue("NotAttacking");
//        String fringe = (String)parameterList.getParameterValue("Fringe");
//
//        double lim = 0;
//
//        if (chameleon) lim += -0.5;
//        if (notattacking) lim += -0.5;
//        if ( "Bright Fringe".equals(fringe)) lim += -0.25;
//
//        return lim;
//    }
    
    /** Attempt to identify power
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     */
//    public int identifyPower(Ability template, AbilityImport ai) {
//        String power = ai.getPowerName();
//
//        if ( power != null && ( power.equals( "ENERGYBLAST" ) || power.equals( "Energy Blast" ) )){
//            return 10;
//        }
//        return 0;
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
    
    /** Returns an Array of Objects, representing custom/special advantages, limitations, special parameters usable with the power.
     * The Array should be in the format of class type (limitation, advantage, special) followed by the class name, repeated for
     * each additional custom added.  For example:
     *  array[] = { Limitation.class, "limitationLimitedSpecialFX", Advantage.class, "advantageVariableEffect" };
     */
    public Object[] getCustomAddersArray() {
        return customAdders;
    }
}