/*
 * powerHandToHandAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
 */

package champions.powers;


import champions.Ability;
import champions.AbilityImport;
import champions.BattleEvent;
import champions.ChampionsUtilities;
import champions.DetailList;
import champions.Dice;
import champions.Effect;
import champions.Power;
import champions.Sense;
import champions.SenseListModel;
import champions.Target;
import champions.exception.BattleEventException;
import champions.interfaces.ChampionsConstants;
import champions.parameters.ListParameter;
import champions.parameters.ParameterList;
import java.util.List;

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
public class powerFlash extends Power
implements ChampionsConstants {
    static final long serialVersionUID =5295848683348706403L;

    static private Object[][] parameterArray = {
        {"DamageDie","Power.DAMAGEDIE", String.class, "1d6", "Flash Dice", DICE_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"Senses","Sense*.SENSE", Sense.class, null, "Senses", LIST_PARAMETER, VISIBLE, ENABLED, REQUIRED}
      //  {"Targeting","Power.TARGETING", String.class, "TRUE", "Targeting", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED}
    };
    
    // Cost Array - See Power.getCostArray()
    static private Object[][] costArray = {
        { "DamageDie", NORMAL_DICE_COST, DYNAMIC_RECONFIG, ALL_RECONFIG, new Integer(5), new Integer(0), new Integer(0) }
    };

    // Known Caveats Array
    private static String[] caveats = {
        
    };    
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "([0-9]*d6) .*\\((.*)\\)", new Object[] { "DamageDie", String.class, "Sense", String.class }},
        { "Flash: ([0-9]*d6).*", new Object[] { "DamageDie", String.class}},
        { "Targeting Sense Group \\((.*)\\).*", new Object[] {"Sense", String.class }},
        //hd
        { "(.*) .* ([0-9]*d6),.*", new Object[] { "Sense", String.class,"DamageDie", String.class }},
        //{ "LEVELS: ([0-9]*).*", new Object[] { "DamageDie", String.class}},
        //Hero Designer (02/15/04 kjr - added ability to recognize dice)
        { "(.*) Flash ([0-9]*d6),.*", new Object[] { "Sense", String.class,"DamageDie", String.class }},       
    };
    

    // Power Definition Variables
    private static String powerName = "Flash"; // The Name of the Power
    private static String targetType = "SINGLE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "ATTACK"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = 10; // The Point cost of a single Damage Class of this power.
    private static String attackType = "RANGED"; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = "SPECIAL"; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 1; // Indicates the END Multiplier for this power
    private static boolean dynamic = true;
    private static String description = "Flash";
    
    /** Creates new powerHandToHandAttack */
    public powerFlash()  {
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
     //   String sense = (String)parameterList.getParameterValue("Sense");
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
        ability.addDiceInfo( "DamageDie", die, "Flash Duration");
        
        // Add A Damage Class Info
        ability.add("Base.DC",  new Double(ChampionsUtilities.StringToNormalDC(die)), true);
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }

    public void triggerPower(BattleEvent be, Ability ability,DetailList effectList, Target target, int refNumber, String targetGroup) throws BattleEventException {

        Dice dice;

        ParameterList parameterList = getParameterList(ability);
        String die = (String)parameterList.getParameterValue("DamageDie");
     //   String sense = (String)parameterList.getParameterValue("Sense");
     //   String targeting = (String)parameterList.getParameterValue("Targeting");


       // int dindex = be.findIndexed("Die","NAME","DamageDie");
        int dindex = be.getDiceIndex( "DamageDie", targetGroup );
        
        if ( dindex != -1 ) {
            dice = be.getDiceRoll(dindex);
            
            List<Sense> senses = parameterList.getIndexedParameterValues("Senses");
            for(Sense sense : senses) {
            	effectFlash effect = new effectFlash( dice.getBody().intValue(), sense);
            	
                effectList.createIndexed(  "Effect","EFFECT",effect) ;
            }
        }
       
        
    }
   
    
/*    public int calculateCPCost(Ability ability) {
        ParameterList parameterList = getParameterList(ability);
        String die = (String)parameterList.getParameterValue("DamageDie");
    //    String sense = (String)parameterList.getParameterValue("Sense");
    //    String targeting = (String)parameterList.getParameterValue("Targeting");
        try {
            Dice d = new Dice( die );
            return d.getD6() * 10;
        }
        catch (BadDiceException bde) {
            return 0;
        }
    } */

    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        String die = (String)parameterList.getParameterValue("DamageDie");
        //String sense = (String)parameterList.getParameterValue("Sense");
    //    String targeting = (String)parameterList.getParameterValue("Targeting");
        StringBuffer sb = new StringBuffer();
        sb.append(die);
        sb.append(" Flash (");
        
        int count = parameterList.getIndexedParameterSize("Senses");
        for(int i = 0; i < count; i++) {
            Sense s = (Sense)parameterList.getIndexedParameterValue("Senses", i);
            sb.append(s.getSenseName());
            if ( i+1 < count ) sb.append(", ");
        }
        sb.append(")");

        return sb.toString();
    }
    
        // Special Handling is necessary to configure the ability.
    public ParameterList getParameterList(Ability ability, int index) {
        ParameterList pl = super.getParameterList(ability,index);
        boolean showOnlyGroups = false;
        ListParameter param = (ListParameter)pl.getParameter("Senses");
        if ( param.getModel() == null) {
            param.setModel( new SenseListModel(pl, "Senses", showOnlyGroups) );
        }
        return pl;
    }
    
     
     /** Executed for all PADs just prior to an ability being saved.
      * All clean up should be done at this point.
      */
//    public void prepareToSave(Ability ability, int index) {
//        // Make sure you clean up the LinkedToAbility model, since it will contain pointers to the world and
//        // will cause havoc when reloaded.
//        ParameterList pl = getParameterList(ability,index);
//        if ( pl.getParameterOption("Senses","MODEL") != null) {
//            pl.setParameterOption("Senses","MODEL", null);
//        }
//    }

            /** Attempt to identify power
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     */
    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();
        
        if ( power != null && ( power.equals( "FLASH" ) || power.equals( "Flash" ) )){
            return 10;
        }
        return 0;
    }
    
    
    
    public Object[][] getImportPatterns() {
        return patterns;
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
}