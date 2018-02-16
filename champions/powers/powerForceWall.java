/*
 * powerHandToHandAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
 */

package champions.powers;

import champions.Ability;
import champions.AbilityImport;
import champions.Battle;
import champions.BattleEvent;
import champions.DetailList;
import champions.Effect;
import champions.LinkedEffect;
import champions.ObjectTarget;
import champions.Power;
import champions.Roster;
import champions.Target;
import champions.battleMessage.EffectSummaryMessage;
import champions.battleMessage.GenericSummaryMessage;
import champions.exception.BattleEventException;
import champions.interfaces.ChampionsConstants;
import champions.parameters.ParameterList;
import java.util.Iterator;


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
public class powerForceWall extends Power
implements ChampionsConstants{
    static final long serialVersionUID =5295848683348505403L;
    
    
    static private String[] sensesArray = {
        "None", "Normal Sight","IR","UV","Unusual detect/awareness","All Sight",
        "Normal Hearing","Ultrasonic Hearing","Sonar","Unusual detect/awareness","All Hearing",
        "Radio Listen","Radio Listen & Transmit","High Range Radio","Radar","Unusual detect/awareness","All Radio",
        "Normal Smell","Discriminatory Smell","Tracking Scent","Normal Taste","Discriminatory Taste","Unusual detect/awareness","All Smell",
        "Unusual detect/awareness","N-Ray Vision","All Unusual",
        "Mind Scan","Mental Awareness","Unusual detect/awareness","All Mental",
    };
    
    static private String[] senseGroupArray = {
        "None", "All Sight", "All Hearing", "All Radio", "All Smell", "All Unusual", "All Mental"
    };
    
    
    static private Object[][] parameterArray = {
        {"ForceWallPD","Power.FORCEWALLPD", Integer.class, new Integer(5), "PD Defense", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"ForceWallED","Power.FORCEWALLED", Integer.class, new Integer(5), "ED Defense", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"ForceWallMD","Power.FORCEWALLMD", Integer.class, new Integer(0), "MD Defense", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"WidthLevel","Power.WIDTHLEVEL", Integer.class, new Integer(0), "Additional Width", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"HeightLevel","Power.HEIGHTLEVEL", Integer.class, new Integer(0), "Additional Height", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"WidthImport","Power.WIDTHIMPORT", Integer.class, new Integer(1), "Width Import", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        {"Senses","Senses*.SENSE", String.class, null, "Opaque to Sense", LIST_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", sensesArray},
        {"SensesTotal","Power.SENSESTOTAL", Integer.class, new Integer(0), "Total Opaque to Senses", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        {"SenseGroup","SenseGroup*.SENSEGROUP", String.class, null, "Opaque to Sense Group", LIST_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", senseGroupArray},
        {"SenseImport","Power.SENSEIMPORT", String.class, "Default", "Senses", STRING_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"SenseGroupTotal","Power.SENSEGROUPTOTAL", Integer.class, new Integer(0), "Total Opaque to Sense Groups", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        
    };
    
    // Cost Array - See Power.getCostArray()
    static private Object[][] costArray = {
        //        { "Base", BASE_COST, STATIC_RECONFIG, ZERO_RECONFIG, new Integer(10) },
        { "ForceWallPD", GEOMETRIC_COST, DYNAMIC_RECONFIG, PROPORTIONAL_RECONFIG, new Integer(5), new Integer(2), new Integer(0), new Integer(0) },
        { "ForceWallED", GEOMETRIC_COST, DYNAMIC_RECONFIG, PROPORTIONAL_RECONFIG, new Integer(5), new Integer(2), new Integer(0), new Integer(0) },
        { "ForceWallMD", GEOMETRIC_COST, DYNAMIC_RECONFIG, PROPORTIONAL_RECONFIG, new Integer(5), new Integer(2), new Integer(0), new Integer(0) },
        { "WidthLevel", GEOMETRIC_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(2), new Integer(1), new Integer(0), new Integer(0) },
        { "HeightLevel", GEOMETRIC_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(2), new Integer(1), new Integer(0), new Integer(0) },
        { "SensesTotal", GEOMETRIC_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(5), new Integer(1), new Integer(0), new Integer(0) },
        { "SenseGroupTotal", GEOMETRIC_COST, DYNAMIC_RECONFIG, ZERO_RECONFIG, new Integer(10), new Integer(1), new Integer(0), new Integer(0) }
    };
    
    
    // Known Caveats Array
    private static String[] caveats = {
        
    };
    
    // Power Definition Variables
    private static String powerName = "Force Wall"; // The Name of the Power
    private static String targetType = "SINGLE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "CONSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "INSTANT"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = -1; // The Point cost of a single Damage Class of this power.
    private static String attackType = "RANGED"; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = "SPECIAL"; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 1; // Indicates the END Multiplier for this power
    private static boolean dynamic = true;
    private static String description = "Force Wall";
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { ".*\\(([0-9]*) PD/([0-9]*) ED\\).*", new Object[] { "ForceWallPD", Integer.class, "ForceWallED", Integer.class}},
        { "Width: .*, \\+([0-9]*)", new Object[] { "WidthImport", Integer.class }},
        { "Force Wall Feedback: .*", null},
        { "Transparent to Energy: .*", null},
        { "Transparent to Physical: .*", null},
    };
    
    /** Creates new powerHandToHandAttack */
    public powerForceWall()  {
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
        Integer width = (Integer)parameterList.getParameterValue("WidthLevel");
        Integer defensepd = (Integer)parameterList.getParameterValue("ForceWallPD");
        Integer defenseed = (Integer)parameterList.getParameterValue("ForceWallED");
        Integer defensemd = (Integer)parameterList.getParameterValue("ForceWallMD");
        Integer widthimport = (Integer)parameterList.getParameterValue("WidthImport");
        Integer sensestotal = parameterList.getIndexedParameterSize("Senses" );
        Integer sensegrouptotal = parameterList.getIndexedParameterSize("SenseGroup" );
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure.
        
        if (widthimport.intValue() > 0) {
            parameterList.setParameterValue("WidthImport", new Integer(-1) );
            parameterList.setParameterValue("WidthLevel", new Integer((widthimport.intValue()/5)*2) );
        }
        if (sensestotal != null || sensegrouptotal !=null ) {
            parameterList.setParameterValue("SensesTotal", new Integer( sensestotal.intValue() ) );
        }
        if ( sensegrouptotal !=null ) {
            parameterList.setParameterValue("SenseGroupTotal", new Integer( sensegrouptotal.intValue() ) );
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
        //ability.addDiceInfo( "DamageDie", die, "Energy Blast Damage");
        
        // Add A Damage Class Info
        //ability.add("Base.DC",  new Double(ChampionsUtilities.StringToNormalDC(die)), true);
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        Integer width = (Integer)parameterList.getParameterValue("WidthLevel");
        Integer height = (Integer)parameterList.getParameterValue("HeightLevel");
        Integer defensepd = (Integer)parameterList.getParameterValue("ForceWallPD");
        Integer defenseed = (Integer)parameterList.getParameterValue("ForceWallED");
        int totalwidth = width.intValue()+3;
        int totalheight = height.intValue()+1;
       return "Force Wall (" + defensepd.toString() + "PD, " + defenseed.toString() + "ED, " + totalwidth + "\" Wide by " + totalheight + "\" High)";
    }
    
            /** Attempt to identify power
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     */
    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();
        
        if ( power != null && ( power.equals( "FORCEWALL" ) || power.equals( "Force Wall" ) )){
            return 10;
        }
        return 0;
    }
        
    public void triggerPower(BattleEvent be, Ability ability,DetailList effectList, Target target, int refNumber, String targetGroup) throws BattleEventException {

        ParameterList parameterList = getParameterList(ability);
        Integer width = (Integer)parameterList.getParameterValue("WidthLevel");
        Integer defensepd = (Integer)parameterList.getParameterValue("ForceWallPD");
        Integer defenseed = (Integer)parameterList.getParameterValue("ForceWallED");
        Integer defensemd = (Integer)parameterList.getParameterValue("ForceWallMD");
        
        Effect e = new effectForceWall(ability, defensepd.intValue(), defenseed.intValue(), defensemd.intValue() );
        int index = effectList.createIndexed("Effect","EFFECT",e,false );
        effectList.addIndexed( index,  "Effect","TTYPE","SOURCE", true);
        
        
        effectForceWall effectForce = (effectForceWall)e;
    	TargetForceWall targetForce = effectForce.getTargetForceWall();
    	
    	targetForce.AddApropriateModifersFromParentAbility(ability, be);

    }
    
/*    public int calculateCPCost(Ability ability) {
        ParameterList parameterList = getParameterList(ability);
        Integer width = (Integer)parameterList.getParameterValue("Width");
        Integer defensepd = (Integer)parameterList.getParameterValue("ForceWallPD");
        Integer defenseed = (Integer)parameterList.getParameterValue("ForceWallED");
 
        return (defensepd.intValue() / 2) * 5 + (defenseed.intValue() / 2) * 5 + (width.intValue() /2) * 5;
    }
 */
    public boolean checkParameter(Ability ability, String key, Object value, Object oldValue) {
        ParameterList parameterList = getParameterList(ability);
        Integer pddefense = (Integer)parameterList.getParameterValue("ForceWallPD",key,value);
        Integer eddefense = (Integer)parameterList.getParameterValue("ForceWallED",key,value);
        Integer extrawidth = (Integer)parameterList.getParameterValue("WidthLevel",key,value);
        
        if ( pddefense.intValue() < 0 || eddefense.intValue() < 0 || extrawidth.intValue() < 0 ) {
            return false;
        }
        return true;
    }
    
    public class effectForceWall extends LinkedEffect {
        
        public effectForceWall( Ability ability, int pddef, int eddef, int mddef) {
            super( ability.getName() + " link", ability);
            setHidden(true);
            
            TargetForceWall targetForceWall = new TargetForceWall(ability.getSource() + "'s " + ability.getName(), this, ability.getSource(), pddef, eddef, mddef );
            add("Effect.TARGETFORCEWALL",  targetForceWall,  true);
            wall = targetForceWall;
        }
        
        TargetForceWall wall = null;
        public TargetForceWall getTargetForceWall(){
        	return wall;
        }
        public boolean addEffect(BattleEvent be, Target target) throws BattleEventException {
            if ( ! super.addEffect(be,target) ) return false;
            
            Object o = getValue("Effect.TARGETFORCEWALL");
            if ( o == null ) return false;
            
            TargetForceWall targetForceWall = (TargetForceWall)o;
            
            Iterator i = Battle.currentBattle.getRosters().iterator();
            if ( i.hasNext() ) {
                Roster r = (Roster)i.next();
                r.add(targetForceWall,false);
                be.addRosterEvent(r,(Target)targetForceWall,true);
                be.addBattleMessage( new GenericSummaryMessage(target,  " created " + targetForceWall.getName())); 
                targetForceWall.setForceWallRoster(r);
                wall =targetForceWall;
            }
            else {
                return false;
            }
            
            return true;
        }
        
        public void removeEffect(BattleEvent be, Target target) throws BattleEventException {
            super.removeEffect(be,target);
            Object o = getValue("Effect.TARGETFORCEWALL");
            if ( o == null ) return;
            
            TargetForceWall targetForceWall = (TargetForceWall)o;
            
            targetForceWall.triggerRemove(be,false);
            
            be.addBattleMessage( new GenericSummaryMessage(target, " deactivates " + getAbility().getName()));
            
        }
    }
    
    public class TargetForceWall extends ObjectTarget {
        
        public TargetForceWall(String name, Effect effect, Target target, int pddef, int eddef, int mddef) {
            super(name, 0, pddef);
            setForceWallEffect(effect);
            setForceWallTarget(target);
            setBaseStat("BODY",0);
            setCurrentStat("BODY", 0);
            setBaseStat("PD", pddef);
            setCurrentStat("PD", pddef);
            setBaseStat("ED", eddef);
            setCurrentStat("ED", eddef);
            setBaseStat("MD", mddef);
            setCurrentStat("MD", mddef);
            setBaseStat("rPD", pddef);
            setCurrentStat("rPD", pddef);
            setBaseStat("rED", eddef);
            setCurrentStat("rED", eddef);
            
            add("Target.ISALIVE",  "FALSE",  true);
            add("Target.CANBEKNOCKEDBACK",  "FALSE",  true);
            add("Target.USESHITLOCATION",  "FALSE",  true);
            add("Target.HASSENSES",  "FALSE",  true);
            add("Target.ISOBSTRUCTION", "TRUE", true);
            
            add("Target.HASDEFENSES",  "TRUE",  true);
        }
        
        public void setForceWallTarget(Target t) {
            add("ForceWall.TARGET",  t, true );
        }
        
        public void setForceWallEffect(Effect e) {
            add("ForceWall.EFFECT",  e, true );
        }
        
        public void setForceWallRoster(Roster e) {
            add("ForceWall.ROSTER",  e, true );
        }
        
        public void posteffect(BattleEvent be, Effect newEffect) throws BattleEventException {
            Ability ability = be.getAbility();
            int bodyIndex;
            
            if ( (bodyIndex = newEffect.findIndexed("Subeffect","VERSUS","BODY")) == -1 ) return;
            //Double value = newEffect.getIndexedDoubleValue( bodyIndex, "Subeffect", "VALUE" );
            
            // Find the remaining amount of Body damage
            double value = newEffect.getSubeffectValue(bodyIndex);
            
            if ( value > 0 ) {
                be.addBattleMessage( new GenericSummaryMessage(this, " was destroyed")); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Damage to " + this.getName() + " exceeded defenses.  " + this.getName() + " was destroyed.", BattleEvent.MSG_NOTICE)); // .addMessage( "Damage to " + this.getName() + " exceeded defenses.  " + this.getName() + " was destroyed.", BattleEvent.MSG_NOTICE);
                
                triggerRemove(be,true);
            }
        }
        
        public int getMinimumStat(String stat) {
            int minimum;
            
            if ( stat.equals("BODY") || stat.equals("STUN") ) {
                minimum = 0;
            }
            else {
                minimum = super.getMinimumStat(stat);
            }
            
            return minimum;
        }
        
        public void triggerRemove(BattleEvent be, boolean removeEffect) throws BattleEventException {
            
            
            if ( removeEffect ) {
                Object target = getValue("ForceWall.TARGET");
                Object effect = getValue("ForceWall.EFFECT");
                if ( effect == null || target == null ) return;
                
                ((Effect)effect).removeEffect(be, (Target)target);
            }
            else {
                
                Object roster = getValue("ForceWall.ROSTER");
                
                if ( roster != null ) {
                    ((Roster)roster).removeTarget(be,this);
                    //   ((Roster)roster).remove(this,false);
                    //  be.addRosterEvent((Roster)roster,this,false);
                    //  be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( this.getName() + " was removed from roster " + ((Roster)roster).getName() + ".", BattleEvent.MSG_ROSTER)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( this.getName() + " was removed from roster " + ((Roster)roster).getName() + ".", BattleEvent.MSG_ROSTER)); // .addMessage( this.getName() + " was removed from roster " + ((Roster)roster).getName() + ".", BattleEvent.MSG_ROSTER);
                }
            }
            // be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( ((Target)target).getName() + " is no longer entangled.", BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( ((Target)target).getName() + " is no longer entangled.", BattleEvent.MSG_NOTICE )); // .addMessage( ((Target)target).getName() + " is no longer entangled.", BattleEvent.MSG_NOTICE );
            //if ( Battle.currentBattle != null ) Battle.currentBattle.addCompletedEvent(be);
        }
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