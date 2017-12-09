/*
 * powerLifeSupport.java
 *
 * Created on August 11, 2002
 */

package champions.powers;

import champions.Ability;
import champions.AbilityImport;
import champions.ArrayListModel;
import champions.BattleEvent;
import champions.DetailList;
import champions.Effect;
import champions.Power;
import champions.Target;
import champions.exception.BattleEventException;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.MutableListModel;
import champions.parameters.MutableListParameter;
import champions.parameters.ParameterList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import javax.swing.JOptionPane;



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
 * 1) Create costArray. * 2) Add the getCostArray() method, returning costArray.
 * 3) Remove existing calculateCPCost.
 */
public class powerLifeSupport extends Power implements ChampionsConstants{
    static final long serialVersionUID = 2002081130347033561L;
    
    private static final String[] longevityOptions =  {
        "Normal","200 Years", "400 Years", "800 Years", "1600 Years",
        "Immortal"
    };
    
    static private Immunity[] immunityOptions = {
        new Immunity("Alcohol", 3),
        new Immunity("Asphyxiants", 3),
        new Immunity("Bacterial Infections", 3),
        new Immunity("Blistering Agents", 3),
        new Immunity("Common Cold/Flu", 2),
        new Immunity("Fungal Infections", 1),
        new Immunity("Haemotoxins", 3),
        new Immunity("Malaria", 2),
        new Immunity("Microbe Toxins", 2),
        new Immunity("Nerve Gases", 3),
        new Immunity("Neuro Toxins", 2),
        new Immunity("Neutralizing Agents", 3),
        new Immunity("Ophidotoxins", 3),
        new Immunity("Phytotoxins", 5),
        new Immunity("Rabies", 1),
        new Immunity("Rickets Infections", 2),
        new Immunity("Tetanus",3),
        new Immunity("Viral Infections", 4),
        new Immunity("Venom from rare plant/animal", 1),
        new Immunity("Venom from common plant/animal", 2),
        new Immunity("Venom from very common plant/animal",3),
        new Immunity("Zootoxins", 5),
        new Immunity("All terrestrial diseases and biowarfare agents", 10),
        new Immunity("All terrestrial poisons and chemical warfare agents",10)
    };
    
    private static final String[] eatingOptions =  {
        "Normal","Character only has to eat once per week","Character only has to eat once per year", "Character does not eat"
    };
    
    private static final String[] extendedBreathingOptions =  {
        "Normal","1 END per Turn", "1 END per Minute", "1 END per 5 Minutes", "1 END per 20 Minutes"
    };
    
    private static final String[] sleepingOptions =  {
        "Normal","Character only has to sleep 8 hours per week", "Character only has to sleep 8 hours per year", "Character does not sleep"
    };
    
    private static String[] expandedBreathingOptions = {};
    
    static private Object[][] parameterArray = {
        {"ExtendedBreathing","Power.EXTENDEDBREATHING", String.class, "Normal", "Extended Breathing", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", extendedBreathingOptions},
        {"ExtendedBreathingTotal","Power.EXTENDEDBREATHINGTOTAL", Integer.class, new Integer(0), "Total Extended Breathing", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        //{"ExpandedBreathing","Power.EXPANDEDBREATHING", Boolean.class, new Boolean(false), "Expanded Breathing", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"ExpandedBreathing","ExpandedBreathing*.CLASS", String.class, null, "Expanded Breathing", MUTABLE_LIST_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, /*"OPTIONS", expandedBreathingOptions,*/ "BUTTON1", "Add Expanded Breathing..."},
        {"ExpandedBreathingTotal","Power.EXPANDEDBREATHINGTOTAL", Integer.class, new Integer(0), "Total Expanded Breathing", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        {"SelfContainedBreathing","Power.SELFCONTAINEDBREATHING", Boolean.class, new Boolean(false), "Self-Contained Breathing", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"DiminishedEating","Power.DIMINISHEDEATING", String.class, "Normal", "Diminished Eating", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", eatingOptions},
        {"DiminishedEatingTotal","Power.DIMINISHEDEATINGTOTAL", Integer.class, new Integer(0), "Diminished Eating Total", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        {"DiminishedSleep","Power.DIMINISHEDSLEEPING", String.class, "Normal", "Diminished Sleep", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", sleepingOptions},
        {"DiminishedSleepTotal","Power.DIMINISHEDSLEEPTOTAL", Integer.class, new Integer(0), "Diminished Sleep Total", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        {"SafeEnvironmentLowPressure","Power.SAFEENVIRONMENTLOWPRESSURE", Boolean.class, new Boolean(false), "Safe Environment: Character is safe in Low Pressure/Vacuum", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"SafeEnvironmentHighPressure","Power.SAFEENVIRONMENTHIGHPRESSURE", Boolean.class, new Boolean(false), "Safe Environment: Character is safe in High Pressure", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"SafeEnvironmentHighRadiation","Power.SAFEENVIRONMENTHIGHRADIATION", Boolean.class, new Boolean(false), "Safe Environment: Character is safe in High Radiation", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"SafeEnvironmentIntenseCold","Power.SAFEENVIRONMENTINTENSECOLD", Boolean.class, new Boolean(false), "Safe Environment: Character is safe in Intense Cold", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"SafeEnvironmentIntenseHeat","Power.SAFEENVIRONMENTINTENSEHEAT", Boolean.class, new Boolean(false), "Safe Environment: Character is safe in Intense Heat", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"SafeEnvironmentZeroGravity","Power.SAFEENVIRONMENTZEROGRAVITY", Boolean.class, new Boolean(false), "Safe Environment: Character is safe in Zero Gravity", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"Longevity","Power.LONGEVITY", String.class, "Normal", "Longevity", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", longevityOptions},
        {"LongevityTotal","Power.LONGEVITYTOTAL", Integer.class, new Integer(0), "Longevity Level", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        {"Immunity","IMMUNITY*.CLASS", Immunity.class, null, "Immunity", MUTABLE_LIST_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, /*"OPTIONS", immunityOptions,*/ "BUTTON1", "Add New Immunity..."},
        {"ImmunityTotal","Power.IMMUNITYTOTAL", Integer.class, new Integer(0), "Immunity Total", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        {"LifeSupportImport","Power.LIFESUPPORTIMPORT", String.class, "", "", STRING_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
    };
    
    
        /* Returns an array which can be used to create the parameterList.
         */
    public Object[][] getParameterArray() {
        return parameterArray;
    }
    
    private boolean importDone = false;
    
    
    // Power Definition Variables
    private static String powerName = "Life Support"; // The Name of the Power
    private static String targetType = "SELF"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "PERSISTENT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "INSTANT"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = -1; // The Point cost of a single Damage Class of this power.
    private static String attackType = null; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = null; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 0; // Indicates the END Multiplier for this power
    private static String description = "Life Support";
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        //        { "(Detect) .*+([0-9]*).*", new Object[] { "LifeSupport", String.class, "EnhancedPerception", Integer.class }},
        
        { "Breathe in Unusual Environment.*", new Object[] { "ExpandedBreathing", Boolean.class }},
        { "Doesn't Eat, Excrete or Sleep", new Object[] { "DiminishedEating", Integer.class }},
        { "Immune to Aging", new Object[] { "Longevity", Integer.class }},
        { "Immune to Disease", new Object[] { "Immunity", Integer.class }},
        //{ "Life Support (total)", new Object[] { "TotalLifeSupport", String.class }},
        { "Life Support: High Pressure/Vacuum", new Object[] { "SafeEnvironmentLowPressure", Boolean.class , "SafeEnvironmentHighPressure", Boolean.class }},
        { "Life Support: High Radiation", new Object[] { "SafeEnvironmentHighRadiation", Boolean.class }},
        { "Life Support: Intense Heat/Cold", new Object[]
          { "SafeEnvironmentIntenseHeat", Boolean.class , "SafeEnvironmentIntenseCold", Boolean.class }},
          { "Need Not Breathe", new Object[] { "SelfContainedBreathing", Boolean.class }},
          //hd
          { "Life Support  \\(Longevity:  (.*)\\)", new Object[] { "Longevity", String.class }},
          { "Life Support.*", null },
          { "LS.*", null },
          { "(Expanded Breathing).*", new Object[] { "ExpandedBreathing", Boolean.class }},
          { "(Safe in Low Pressure/Vacuum)", new Object[] { "SafeEnvironmentLowPressure", Boolean.class }},
          
    };
    
    public Object[][] getImportPatterns() {
        System.out.println("Got here (Life Support).");
        System.out.println("patterns: " + patterns);
        return patterns;
    }
    
    // Cost Array - See Power.getCostArray()
    static private Object[][] costArray = {
        //        { "Base", BASE_COST, STATIC_RECONFIG, ZERO_RECONFIG, new Integer(10) },
        { "ExtendedBreathingTotal", GEOMETRIC_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(1), new Integer(1), new Integer(0), new Integer(0) },
        { "ExpandedBreathingTotal", GEOMETRIC_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(5), new Integer(1), new Integer(0), new Integer(0) },
        { "SelfContainedBreathing", BOOLEAN_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(10)},
        { "DiminishedEatingTotal", GEOMETRIC_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(1), new Integer(1), new Integer(0), new Integer(0)},
        { "DiminishedSleepTotal", GEOMETRIC_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(1), new Integer(1), new Integer(0), new Integer(0)},
        { "SafeEnvironmentLowPressure", BOOLEAN_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(2)},
        { "SafeEnvironmentHighPressure", BOOLEAN_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(1)},
        { "SafeEnvironmentHighRadiation", BOOLEAN_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(2)},
        { "SafeEnvironmentIntenseCold", BOOLEAN_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(2)},
        { "SafeEnvironmentIntenseHeat", BOOLEAN_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(2)},
        { "SafeEnvironmentZeroGravity", BOOLEAN_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(1)},
        { "LongevityTotal", GEOMETRIC_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(1), new Integer(1), new Integer(0), new Integer(0) },
        //{ "Immunity", GEOMETRIC_COST, DYNAMIC_RECONFIG, ALL_RECONFIG, new Integer(1), new Integer(1), new Integer(0), new Integer(0)},
        { "ImmunityTotal", GEOMETRIC_COST, STATIC_RECONFIG, ZERO_RECONFIG, new Integer(1), new Integer(1), new Integer(0), new Integer(0) },
     };

    
    public Object[][] getCostArray(Ability ability) {
        return costArray;
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
        //  System.out.println(ability.getName());
        String longevity = (String)parameterList.getParameterValue("Longevity");
        Integer Longevitytotal = (Integer)parameterList.getParameterValue("LongevityTotal");
        Integer diminishedeatinglevel = (Integer)parameterList.getParameterValue("DiminishedEatingTotal");
        String diminishedeating = (String)parameterList.getParameterValue("DiminishedEating");
        //Integer extendedbreathinglevel = (Integer)parameterList.getParameterValue("ExtendedBreathingLevel");
        String extendedbreathing = (String)parameterList.getParameterValue("ExtendedBreathing");
      //  Integer diminishedsleeplevel = (Integer)parameterList.getParameterValue("DiminishedSleepLevel");
        String diminishedsleep = (String)parameterList.getParameterValue("DiminishedSleep");
        
        
        
        
        for (int i = 0; i < eatingOptions.length; i++) {
            if ( diminishedeating.equals(eatingOptions[i])) {
                parameterList.setParameterValue("DiminishedEatingTotal", new Integer(i));
            }
        }
        
        for (int i = 0; i < extendedBreathingOptions.length; i++) {
            if ( extendedbreathing.equals(extendedBreathingOptions[i])) {
                parameterList.setParameterValue("ExtendedBreathingTotal", new Integer(i));
            }
        }
        
        for (int i = 0; i < sleepingOptions.length; i++) {
            if ( diminishedsleep.equals(sleepingOptions[i])) {
                parameterList.setParameterValue("DiminishedSleepTotal", new Integer(i));
            }
        }
        
        for (int i = 0; i < longevityOptions.length; i++) {
            if ( longevity.equals(longevityOptions[i])) {
                parameterList.setParameterValue("LongevityTotal", new Integer(i));
            }
        }
        
        
        
        //}
        // Read in any parameters that will be needed to configure the power or
        // Determine the validity of the power configuration.  Read the parameters
        // from the parameterList, instead of directly from the ability, since the
        // Ability isn't configured yet.
        String lifeSupportImport = (String)parameterList.getParameterValue("LifeSupportImport");
        
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
        
        // Add any dice information which is necessary to use this power.
        //ability.addDiceInfo( "DamageDie", die, "Energy Blast Damage");
        
        // Add A Damage Class Info
        //ability.add("Base.DC",  new Double(ChampionsUtilities.StringToNormalDC(die)), true);
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        Integer classtotal = parameterList.getIndexedParameterSize("ExpandedBreathing" );
        if ( classtotal != null ) {
            parameterList.setParameterValue("ExpandedBreathingTotal", classtotal );
        }
        
        int immunityCount = parameterList.getIndexedParameterSize("Immunity" );
        int immunityTotal = 0;
        for(int i = 0; i < immunityCount; i++) {
            Immunity immunity = (Immunity) parameterList.getIndexedParameterValue("Immunity", i);
            immunityTotal += immunity.getCost();
        }
        
        parameterList.setParameterValue("ImmunityTotal", immunityTotal );
        
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        if (!importDone) {
            specificImport(parameterList, lifeSupportImport);
        }
        
        // Return true to indicate success
        return true;
    }

    @Override
    public void triggerPower(BattleEvent be, Ability ability, DetailList effectList, Target target, int referenceNumber, String targetGroup) throws BattleEventException {
        Effect effect = new effectGained(ability);
        effectList.createIndexed(   "Effect","EFFECT", effect,  false );
    
    }
    
    
    
    // Special Handling is necessary to configure the ability.
    public ParameterList getParameterList(Ability ability, int index) {
        ParameterList pl = super.getParameterList(ability,index);
        
        MutableListParameter param = (MutableListParameter)pl.getParameter("ExpandedBreathing");
        if ( param.getModel() == null && ability.getSource() != null) {
            param.setModel( buildExpandedBreathingListModel(ability.getSource(), pl) );
        }
        
        if ( param.getOptions() == null ) {
            param.setOptions( expandedBreathingOptions);
        }
        
        AddExpandedBreathingClassActionListener acal;
        if ( (acal = (AddExpandedBreathingClassActionListener)param.getActionListener1()) == null ) {
            acal = new AddExpandedBreathingClassActionListener();
            param.setActionListener1(acal);
        }
        
        acal.setListModel(param.getModel());
        
        
        /// Setup the Immunity Class editor....
        MutableListParameter param2 = (MutableListParameter)pl.getParameter("Immunity");
        if ( param2.getModel() == null && ability.getSource() != null) {
            param2.setModel( buildImmunityListModel(ability.getSource(), pl) );
        }
        
        if ( param2.getOptions() == null ) {
            param2.setOptions(immunityOptions);
        }
        
        AddImmunityClassActionListener aical;
        if ( (aical = (AddImmunityClassActionListener)param2.getActionListener1()) == null ) {
            aical = new AddImmunityClassActionListener();
            param2.setActionListener1(aical);
        }
        
        aical.setListModel(param2.getModel());
        
        return pl;
    }
    
    public void abilitySourceSet(Ability ability, Target oldSource, Target newSource) {
        ParameterList pl = getParameterList(ability);
        MutableListModel lm;
        
        lm = buildExpandedBreathingListModel(newSource, pl);
        MutableListParameter param = (MutableListParameter)pl.getParameter("ExpandedBreathing");
        param.setModel( lm );
        AddExpandedBreathingClassActionListener acal = (AddExpandedBreathingClassActionListener)param.getActionListener1();
        acal.setListModel(lm);
        
        // Setup ListModel for immunity too...
        lm = buildImmunityListModel(newSource, pl);
        MutableListParameter param2 = (MutableListParameter)pl.getParameter("Immunity");
        param2.setModel( lm );
        AddImmunityClassActionListener aical = (AddImmunityClassActionListener)param2.getActionListener1();
        aical.setListModel(lm);
    }

    /** Executed for all PADs just prior to an ability being saved.
     * All clean up should be done at this point.
     */
//    public void prepareToSave(Ability ability, int index) {
//        // Make sure you clean up the MUTABLEMODEL and ACTIONLISTENER1 model, since it will contain pointers to the world and
//        // will cause havoc when reloaded.
//        ParameterList pl = getParameterList(ability,index);
//
//        pl.removeParameterOption("ExpandedBreathing","MUTABLEMODEL");
//        pl.removeParameterOption("ExpandedBreathing","OPTIONS");
//        pl.removeParameterOption("ExpandedBreathing","ACTIONLISTENER1");
//        
//        pl.removeParameterOption("Immunity","MUTABLEMODEL");
//        pl.removeParameterOption("Immunity","ACTIONLISTENER1");
//        pl.removeParameterOption("Immunity","OPTIONS");
//        
//    }
    
    private MutableListModel buildExpandedBreathingListModel(Target source, ParameterList pl) {
        if ( source == null ) {
            return new ArrayListModel();
        } else {
            int size = pl.getIndexedParameterSize("ExpandedBreathing");
            Object a[] = new Object[size];
            for(int i = 0; i < size; i++) {
                a[i] = pl.getIndexedParameterValue("ExpandedBreathing", i);
            }
            
            return new ArrayListModel(a);
        }
    }
    
    private MutableListModel buildImmunityListModel(Target source, ParameterList pl) {
        if ( source == null ) {
            return new ArrayListModel();
        } else {
            int size = pl.getIndexedParameterSize("Immunity");
            Object a[] = new Object[size];
            for(int i = 0; i < size; i++) {
                a[i] = pl.getIndexedParameterValue("Immunity", i);
            }
            
            return new ArrayListModel(a);
        }
    }
    
    
    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();
        
        if ( power != null && (power.equals( "LIFESUPPORT" )
        ||  power.equals( "Life Support" )
        ||  power.equals( "LS" )
        ||  power.equals( "Breathe in Unusual Environment" )
        ||  power.equals( "Doesn't Eat, Excrete or Sleep" )
        ||  power.equals( "Immune to Aging" )
        ||  power.equals( "Immune to Disease" )
        ||  power.equals( "Life Support (total)" )
        ||  power.equals( "Life Support: High Pressure/Vacuum" )
        ||  power.equals( "Life Support: High Radiation" )
        ||  power.equals( "Life Support: Intense Heat/Cold" )
        ||  power.equals( "Need Not Breathe" )  ) ) {
            return 10;
        }
        return 0;
    }
    
    public void importPower(Ability ability, AbilityImport ai) {
        // First Do the standard import.  This will parse the RegExp andpopulate
        super.importPower(ability,ai);
        
        ParameterList parameterList = getParameterList(ability);
        
        String powername = (String)ai.getValue("AbilityImport.POWERNAME");
        parameterList.setParameterValue("LifeSupportImport", powername);
        
    }
    
    
    public boolean checkParameter(Ability ability, int not_used, String key, Object value, Object oldValue) {
        ParameterList parameterList = getParameterList(ability);
        //Integer extendedBreathing = (Integer)parameterList.getParameterValue("ExtendedBreathing", key, value);
        //Integer diminishedEating = (Integer)parameterList.getParameterValue("DiminishedEating", key, value);
        //Integer diminishedSleep = (Integer)parameterList.getParameterValue("DiminishedSleep", key, value);
        Integer LongevityTotal = (Integer)parameterList.getParameterValue("LongevityTotal", key, value);
        //Integer immunity = (Integer)parameterList.getParameterValue("Immunity", key, value);
        
//        if ( extendedBreathing.intValue() < 0 || extendedBreathing.intValue() > 4
//                //|| diminishedEating.intValue() < 0 || diminishedEating.intValue() > 3
//                || diminishedSleep.intValue() < 0 || diminishedSleep.intValue() > 3
//                || LongevityTotal.intValue() < 0 || LongevityTotal.intValue() > 5
//                //|| immunity.intValue() < 0 || immunity.intValue() > 10
//                ) {
//            return false;
//        }
        return true;
    }
    
    public void specificImport(ParameterList parameterList, String lifeSupportImport) {
        if (lifeSupportImport.equals("")) {
            return;
        }
        importDone = true;
        if (lifeSupportImport.equals("Breathe in Unusual Environment")) {
            parameterList.setParameterValue("ExpandedBreathing", new Boolean(true) );
        } else if (lifeSupportImport.equals("Doesn't Eat, Excrete or Sleep")) {
            parameterList.setParameterValue("DiminishedEating", new Integer(3) );
        } else if (lifeSupportImport.equals("Immune to Aging")) {
            parameterList.setParameterValue("Longevity", new Integer(5) );
        } else if (lifeSupportImport.equals("Immune to Disease")) {
            parameterList.setParameterValue("Immunity", new Integer(10) );
        } else if (lifeSupportImport.equals("Life Support (total)")) {
            parameterList.setParameterValue("SelfContainedBreathing", new Boolean(true) );
            parameterList.setParameterValue("DiminishedEating", new Integer(3) );
            parameterList.setParameterValue("DiminishedSleep", new Integer(3) );
            parameterList.setParameterValue("SafeEnvironmentHighPressure", new Boolean(true) );
            parameterList.setParameterValue("SafeEnvironmentLowPressure", new Boolean(true) );
            parameterList.setParameterValue("SafeEnvironmentHighRadiation", new Boolean(true) );
            parameterList.setParameterValue("SafeEnvironmentIntenseHeat", new Boolean(true) );
            parameterList.setParameterValue("SafeEnvironmentIntenseCold", new Boolean(true) );
            parameterList.setParameterValue("Immunity", new Integer(20) );
        } else if (lifeSupportImport.equals("Life Support: High Pressure/Vacuum")) {
            parameterList.setParameterValue("SafeEnvironmentHighPressure", new Boolean(true) );
            parameterList.setParameterValue("SafeEnvironmentLowPressure", new Boolean(true) );
        } else if (lifeSupportImport.equals("Life Support: High Radiation")) {
            parameterList.setParameterValue("SafeEnvironmentHighRadiation", new Boolean(true) );
        } else if (lifeSupportImport.equals("Life Support: Intense Heat/Cold")) {
            parameterList.setParameterValue("SafeEnvironmentIntenseHeat", new Boolean(true) );
            parameterList.setParameterValue("SafeEnvironmentIntenseCold", new Boolean(true) );
        } else if (lifeSupportImport.equals("Need Not Breathe")) {
            parameterList.setParameterValue("SelfContainedBreathing", new Boolean(true) );
        }
    }
    
    /** Get the English name of the PAD.
     * @return name of PAD
     */
    public String getName() {
        return powerName;
    }
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        return "Life Support";
    }
    
    static class AddExpandedBreathingClassActionListener implements ActionListener {
        
        private MutableListModel listModel = null;
        
        AddExpandedBreathingClassActionListener() {
            
        }
        
        public void actionPerformed(ActionEvent e) {
            if ( listModel != null ) {
                String newClass = JOptionPane.showInputDialog(null, "Enter New Expanded Breathing Type", "Breathing in <...>");
                
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
    
    static class AddImmunityClassActionListener implements ActionListener {
        
        private MutableListModel listModel = null;
        
        AddImmunityClassActionListener() {
            
        }
        
        public void actionPerformed(ActionEvent e) {
            if ( listModel != null ) {
                
                Immunity immunity = LifeSupportCreateImmunityDialog.showDialog(null);
                
                if ( immunity != null ) {
                    listModel.addElement(immunity, -1);
                    
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
    
    public static class Immunity implements Serializable {
        private String name;
        private int cost;
        
        public Immunity(String name, int cost) {
            this.setName(name);
            this.setCost(cost);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }
        
        public String toString() {
            if ( cost == 1 ) {
                return name + " (" + cost + " pt)";
            }
            else {
                return name + " (" + cost + " pts)";
            }
        }
        
        public boolean equals(Object that) {
            return that instanceof Immunity && ((Immunity)that).name.equals(name) && ((Immunity)that).cost == cost;
        }
    }
    
}