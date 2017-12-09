/*
 * powerEgoAttack.java
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
import champions.Effect;
import champions.MentalEffectInfo;
import champions.Power;
import champions.Preferences;
import champions.Target;
import champions.filters.TargetIsAliveFilter;
import champions.interfaces.ChampionsConstants;
import tjava.Filter;
import champions.interfaces.IndexIterator;
import champions.interfaces.MutableListModel;
import champions.parameters.MutableListParameter;
import champions.parameters.ParameterList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
 * 1) Create costArray.
 * 2) Add the getCostArray() method, returning costArray.
 * 3) Remove existing calculateCPCost.
 */
public class powerEgoAttack extends Power implements ChampionsConstants {
    static final long serialVersionUID =5295848463348707401L;
    
    //creates the default list of classes for use in the power gui
    static private String[] classOptions = {
        "Human", "Animal", "Machine","Alien"
    };
    
    private static Object[][] parameterArray = {
        {"DamageDie","Power.DAMAGEDIE", String.class, "1d6", "Damage Dice", DICE_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
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
    private static String powerName = "Ego Attack"; // The Name of the Power
    private static String targetType = "SINGLE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "ATTACK"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = 10; // The Point cost of a single Damage Class of this power.
    private static String attackType = "RANGED"; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = "NORMAL"; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = true; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 1; // Indicates the END Multiplier for this power
    private static boolean dynamic = true;
    private static String description = "Ego Attack";
    
    /** Creates new powerEgoAttack */
    public powerEgoAttack()  {
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
        if ( ability == null ) return false;
        
        // Always Set the ParameterList to the parameterList
        setParameterList(ability,parameterList);
        //grab the number of classes choosen in power
        //        int grouptotal = parameterList.getIndexedSize("SenseGroup" );
        
        //int classtotal = parameterList.getIndexedSize("AdditionalClasses");
        int classtotal = parameterList.getIndexedParameterSize("AdditionalClasses");
        if (classtotal > 0) {
            parameterList.setParameterValue("ClassTotal", new Integer( classtotal -1 ) );
        }
        else {
            parameterList.setParameterValue("ClassTotal", new Integer( classtotal) );
        }
        
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
        ability.addDiceInfo( "DamageDie", die, "Ego Attack");
        
        // Add A Damage Class Info
        ability.add("Base.DC",  new Double(ChampionsUtilities.StringToNormalDC(die)), true);
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        ability.add("Ability.CANSPREAD", "FALSE" , true );
        ability.add("Ability.DOESBODY", "FALSE", true);
        ability.add("Power.DEFENSE", "MD", false);
        ability.add("Ability.CVTYPE", "EGO", true);
        ability.add("Ability.USESHITLOCATION",  "FALSE",  true);
        ability.add("Ability.OTHERCLASSOFMINDDAMAGEPENALTY", "TRUE", true);
        ability.setHasRangeModifier(false);
        ability.setRequiresMentalPanel(false);
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    
    public void prepower(BattleEvent be, Ability ability, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean isFinalTarget, int priority) {
        if ( priority == 2 ) {
            int classofminddamagevalue = 0;
            IndexIterator ii = effect.getSubeffectIterator();
            
            while( ii.hasNext() ) {
                int sindex = ii.nextIndex();
                String effectType = effect.getSubeffectEffectType(sindex);
                
                if ( (effectType != null) && effectType.equals("DAMAGE") ) {
                    // We have found some damage. Now make sure it is versus stun
                    String versusStat = (String)effect.getSubeffectVersusObject(sindex);
                    if ( (versusStat != null) && versusStat.equals("STUN")) {
                        Integer effectroll = new Integer((int)Math.round(effect.getSubeffectValue(sindex)));
                        if ( ability.hasClassOfMindDamagePenalty() ) {
                            classofminddamagevalue = setClassOfMindDamageValue(be, targetGroup,targetReferenceNumber, effect);
                            effect.setSubeffectValue(sindex, effectroll.doubleValue() + classofminddamagevalue);
                        }
                    }
                }
            }
        }
    }
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        String die = (String)parameterList.getParameterValue("DamageDie");
        //  String defense = (String)parameterList.getParameterValue("Defense");
        //   String stunOnly = (String)parameterList.getParameterValue("StunOnly");
        
        //  String stunString = (stunOnly.equals("TRUE")) ? ", stun only":"";
        
        return die + " Ego Attack";
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
    
    private MutableListModel buildListModel(Target source, ParameterList pl) {
        if ( source == null ) {
            return new ArrayListModel();
        }
        else {
            Object a[] = pl.getIndexedParameterValues("AdditionalClasses").toArray();
            
            return new ArrayListModel(a);
        }
    }
    
    /** Attempt to identify power
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     */
    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();
        
        if ( power != null && ( power.equals( "EGOATTACK" ) || power.equals( "Ego Attack" ) )){
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

    public Filter<Target> getTargetFilter(Ability ability) {
        return new TargetIsAliveFilter();
    }
    

    
    static public boolean checkClassOfMindEquality(BattleEvent be, String targetGroup, int targetReferenceNumber) {
        boolean match = false;
        
        ActivationInfo ai = be.getActivationInfo();
        boolean allowotherclassofmind = (Boolean)Preferences.getPreferenceList().getParameterValue("MentalEffectAllowOtherClasseOfMind" );
        
        
        int tindex = ai.getTargetIndex(targetReferenceNumber, targetGroup);
        
        if ( tindex != -1 ) {
            
            Target target = ai.getTarget(tindex);
            Target source = ai.getAbility().getSource();
            
            // Look up the info...
            MentalEffectInfo mei = (MentalEffectInfo)ai.getIndexedValue(tindex, "Target","MENTALEFFECTINFO");
            if (mei == null ) {
                mei = new MentalEffectInfo();
                
                // Put it in the ActivationInfo for later...
                ai.addIndexed(tindex, "Target", "MENTALEFFECTINFO", mei, true);
            }
            //get the size of the list of strings of class of mind for both the ability and the target
            int targetcmsize = target.getIndexedSize("ClassOfMind");
            int abilitycmsize = ai.getAbility().getIndexedSize("AdditionalClasses");
            for (int index = 0; index < abilitycmsize; index++) {
                for (int index2 = 0; index2 < targetcmsize; index2++) {
                    //compare the classes the power affects to the class(es) of mind of the target...look for a match
                    if (ai.getAbility().getIndexedStringValue(index,"AdditionalClasses","CLASS" ).equals(target.getIndexedStringValue(index2,"ClassOfMind","NAME" ))) {
                        match = true;
                    }
                }
                
                
            }
        }
        return match;
    }
    
    static public int setClassOfMindDamageValue(BattleEvent be, String targetGroup, int targetReferenceNumber, Effect effect ) {
        Ability ability = be.getAbility();
        //grab the value
        String otherclassofminddamagepenalty = ability.getStringValue("Ability.OTHERCLASSOFMINDDAMAGEPENALTY");
        if ( otherclassofminddamagepenalty.equals("TRUE")) {
            boolean match = checkClassOfMindEquality(be, targetGroup, targetReferenceNumber);
            if (match == true ) {
                return 0;
            }
            else {
                boolean allowotherclassofmind = (Boolean)Preferences.getPreferenceList().getParameterValue("MentalEffectAllowOtherClasseOfMind" );
                if ( allowotherclassofmind) {
                    ActivationInfo ai = be.getActivationInfo();
                    int tindex = ai.getTargetIndex(targetReferenceNumber, targetGroup);
                    Target target = ai.getTarget(tindex);
                    be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage("Due to a different class of mind " + target.getName() + " cannot be affected normally by " + ai.getAbility().getName() + ". Effect has been reduced by -5.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage("Due to a different class of mind " + target.getName() + " cannot be affected normally by " + ai.getAbility().getName() + ". Effect has been reduced by -5.", BattleEvent.MSG_NOTICE)); // .addMessage("Due to a different class of mind " + target.getName() + " cannot be affected normally by " + ai.getAbility().getName() + ". Effect has been reduced by -5.", BattleEvent.MSG_NOTICE);
                    return -5;
                    
                }
                else {
                    ActivationInfo ai = be.getActivationInfo();
                    int tindex = ai.getTargetIndex(targetReferenceNumber, targetGroup);
                    Target target = ai.getTarget(tindex);
                    be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " cannot be affected by " + ai.getAbility().getName() + ". Target  has different class of mind.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " cannot be affected by " + ai.getAbility().getName() + ". Target  has different class of mind.", BattleEvent.MSG_NOTICE)); // .addMessage(target.getName() + " cannot be affected by " + ai.getAbility().getName() + ". Target  has different class of mind.", BattleEvent.MSG_NOTICE);
                    return 0;
                    
                }
            }
        }
        return 0;
    }
}