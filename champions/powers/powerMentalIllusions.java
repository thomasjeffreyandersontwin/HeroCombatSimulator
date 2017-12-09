/*
 * powerHandToHandAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
 */

package champions.powers;

import champions.Ability;
import champions.AbilityImport;
import champions.ActivationInfo;
import champions.ArrayListModel;
import champions.BattleEvent;
import champions.ChampionsUtilities;
import champions.DetailList;
import champions.Dice;
import champions.Effect;
import champions.MentalEffectInfo;
import champions.Power;
import champions.Target;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.Limitation;
import champions.interfaces.MutableListModel;
import champions.parameters.MutableListParameter;
import champions.parameters.ParameterList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import champions.exception.BattleEventException;
import champions.filters.TargetIsAliveFilter;
import tjava.Filter;
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
public class powerMentalIllusions extends Power implements ChampionsConstants {
    static final long serialVersionUID =5295848683348706403L;
    
    //creates the default list of classes for use in the power gui
    static private String[] classOptions = {
        "Human", "Animal", "Machine","Alien"
    };
    
    static public String[] effectLevelOptions = {
        "Greater than STAT (Cosmetic Changes To Setting)",
        "STAT + 10 (Major Changes To Setting)",
        "STAT + 20 (Completely Alters Setting)",
        "STAT + 30 (Character No Longer Interacts With Real Environment)"
    };    
    
    
    //    //creates the list of effect level modifiers
    static public String[] effectModifierOptions = {
        "-10 (Illusion Matches Target's Psychological Limitation)",
        "+10 (Illusion Contradicts Target's Psychological Limitation)",
        "+10 (Target Will Remember The Illusion As Being Real After It Has Ended)",
        "+10 (Target takes STUN from Illusionary Attacks)",
        "+20 (Target takes STUN and BODY from Illusionary Attacks)"
    };
    
    static public int[] effectModifierValueOptions = {-10,-5,5,10,10,20};
    
    
    private static Object[][] parameterArray = {
        //# of dice of power
        {"DamageDie","Power.DAMAGEDIE", String.class, "1d6", "Damage Dice", DICE_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        //lists powers chooses classes
        {"AdditionalClasses","AdditionalClasses*.CLASS", String.class, null, "Class of Minds", MUTABLE_LIST_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED
         , "OPTIONS", classOptions, "BUTTON1", "Add Class..."},
         //tracks the total number of classes selected for cost purposes
         {"ClassTotal","Power.CLASSTOTAL", Integer.class, new Integer(0), "Total Classes", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
    };
    
    // Cost Array - See Power.getCostArray()
    static private Object[][] costArray = {
        { "DamageDie", NORMAL_DICE_COST, DYNAMIC_RECONFIG, ALL_RECONFIG, new Integer(5), new Integer(0), new Integer(0) },
        { "ClassTotal", GEOMETRIC_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(10), new Integer(1), new Integer(0), new Integer(0) },
    };
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { ".* ([0-9]*d6).*", new Object[] { "DamageDie", String.class}},
        { "([0-9]*d6).*", new Object[] { "DamageDie", String.class}},
        { "Communication.*", null}
    };
    
    // Known Caveats Array
    private static String[] caveats = {
    };

    // Custom Adders
    private static Object[] customAdders = {
        Limitation.class, "Self Only",
        Limitation.class, "Limited By Senses"
    };
    
    // Power Definition Variables
    private static String powerName = "Mental Illusions"; // The Name of the Power
    private static String targetType = "SINGLE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "ATTACK"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = 10; // The Point cost of a single Damage Class of this power.
    private static String attackType = "RANGED"; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = "SPECIAL"; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 1; // Indicates the END Multiplier for this power
    private static boolean dynamic = true;
    private static String description = "Mental Illusions";
    
    /** Creates new powerHandToHandAttack */
    public powerMentalIllusions()  {
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
        //grab the number of classes choosen in power
        Integer classtotal = parameterList.getIndexedParameterSize("AdditionalClasses" );
        
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
        ability.addDiceInfo( "DamageDie", die, "Mental Illusions Effect Roll");
        
        // Add A Damage Class Info
        ability.add("Base.DC",  new Double(ChampionsUtilities.StringToNormalDC(die)), true);
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        ability.add("Ability.CANSPREAD", "FALSE" , true );
        ability.add("Ability.DOESBODY", "FALSE", true);
        ability.add("Power.DEFENSE", "MD", false);
        ability.add("Ability.CVTYPE", "EGO", true);
        ability.add("Ability.USESHITLOCATION",  "FALSE",  true);
        ability.setHasRangeModifier(false);
        ability.setRequiresMentalPanel(true);
        
        if ( classtotal !=null ) {
            parameterList.setParameterValue("ClassTotal", new Integer( classtotal.intValue()-1 ) );
        }
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    
    
    // Special Handling is necessary to configure the ability.
    public ParameterList getParameterList(Ability ability, int index) {
        ParameterList pl = super.getParameterList(ability,index);
        MutableListParameter param = (MutableListParameter)pl.getParameter("AdditionalClasses");
        if ( param.getModel() == null && ability.getSource() != null) {
            param.setModel( buildListModel(ability.getSource(), pl) );
        }
        
        AddClassActionListener acal = (AddClassActionListener)param.getActionListener1();
        if ( acal == null ) {
            acal = new AddClassActionListener();
            param.setActionListener1(acal);
        }
        
        acal.setListModel((MutableListModel) param.getModel() );
        
        return pl;
    }
    
    public void abilitySourceSet(Ability ability, Target oldSource, Target newSource) {
        ParameterList pl = getParameterList(ability);
        MutableListModel lm = buildListModel(newSource, pl);
        MutableListParameter param = (MutableListParameter)pl.getParameter("AdditionalClasses");
        param.setModel(lm);
        AddClassActionListener acal = (AddClassActionListener)param.getActionListener1();
        acal.setListModel(lm);
    }
    
    
    /** Executed for all PADs just prior to an ability being saved.
     * All clean up should be done at this point.
     */
//    public void prepareToSave(Ability ability, int index) {
//        // Make sure you clean up the MUTABLEMODEL and ACTIONLISTENER1 model, since it will contain pointers to the world and
//        // will cause havoc when reloaded.
//        ParameterList pl = getParameterList(ability,index);
//        if ( pl.getParameterOption("AdditionalClasses","MUTABLEMODEL") != null) {
//            pl.setParameterOption("AdditionalClasses","MUTABLEMODEL", null);
//        }
//        if ( pl.getParameterOption("AdditionalClasses","ACTIONLISTENER1") != null) {
//            pl.setParameterOption("AdditionalClasses","ACTIONLISTENER1", null);
//        }
//        
//    }
    
    private MutableListModel buildListModel(Target source, ParameterList pl) {
        if ( source == null ) {
            return new ArrayListModel();
        }
        else {
            //int size = pl.getIndexedSize("AdditionalClasses");
            List additionalClassesList = pl.getIndexedParameterValues("AdditionalClasses");
            Object a[] = additionalClassesList.toArray();
            
            return new ArrayListModel(a);
        }
    }
    
    public void triggerPower(BattleEvent be, Ability ability,DetailList effectList, Target target, int refNumber, String targetGroup) throws BattleEventException {
        
        Dice dice;
        
        ParameterList parameterList = getParameterList(ability);
        String die = (String)parameterList.getParameterValue("DamageDie");
        
        // int dindex = be.findIndexed("Die","NAME","DamageDie");
        int dindex = be.getDiceIndex( "DamageDie", targetGroup );
        
        if ( dindex != -1 ) {
            dice = be.getDiceRoll(dindex);
            //couldn't just pass dice.getStun() without throwing errors even after
            //I removed any other parameters in effectMindControl()
            ActivationInfo ai = be.getActivationInfo();
            
            int tindex = ai.getTargetIndex(refNumber, targetGroup);
            
            if ( tindex != -1 ) {
                MentalEffectInfo mei = (MentalEffectInfo)ai.getIndexedValue(tindex, "Target","MENTALEFFECTINFO");
                Effect effect = new effectMentalIllusions( dice.getStun(), mei );
                effectList.createIndexed(  "Effect","EFFECT",effect) ;
            }
        }
        //        Effect effect = new effectMindControl( dice.getStun(), mei );
        //        effectList.createIndexed(  "Effect","EFFECT",effect) ;
    }
    
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        String die = (String)parameterList.getParameterValue("DamageDie");
        //String sense = (String)parameterList.getParameterValue("Sense");
        //    String targeting = (String)parameterList.getParameterValue("Targeting");
        
        
        return die + " Mental Illusions";
    }
    
    
    /** Attempt to identify power
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     */
    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();
        
        if ( power != null && ( power.equals( "MENTALILLUSIONS" ) || power.equals( "Mental Illusions" ) )){
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
    
     public Filter<Target> getTargetFilter(Ability ability) {
        return new TargetIsAliveFilter();
    }
    
    static class AddClassActionListener implements ActionListener {
        
        private MutableListModel listModel = null;
        
        AddClassActionListener() {
            
        }
        
        public void actionPerformed(ActionEvent e) {
            if ( listModel != null ) {
                String newClass = JOptionPane.showInputDialog(null, "Enter New Class", "<New Class>");
                
                if ( newClass != null ) {
                    listModel.addElement(newClass, -1);
                }
                
            }
        }
        /** Getter for property listModel.
         * @return Value of property listModel.
         *
         */
        public MutableListModel getListModel() {
            return listModel;
        }
        
        /** Setter for property listModel.
         * @param listModel New value of property listModel.
         *
         */
        public void setListModel(MutableListModel listModel) {
            this.listModel = listModel;
        }
        
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