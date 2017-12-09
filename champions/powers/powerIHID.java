/*
 * powerHandToHandAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
 */

package champions.powers;

import champions.Ability;
import champions.AbilityImport;
import champions.ActivationInfo;
import champions.Battle;
import champions.BattleEvent;
import champions.DetailList;
import champions.PADRoster;
import champions.Power;
import champions.Target;
import champions.exception.BattleEventException;
import champions.interfaces.AbilityIterator;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.Limitation;
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
 * 11) Edit getName method to return powerName variable.<P>
 * 12) Change serialVersionUID by some amount.<P>
 * 13) Add patterns array and define import patterns.<P>
 * 14) Add getImportPatterns() method.<P>
 */
public class powerIHID extends Power implements ChampionsConstants {
    
    static final long serialVersionUID = -4093385577202136604L;
    
    static private Object[][] parameterArray = {
        {"HeroIDName","Power.HEROIDNAME", String.class, "Hero_ID_Name", "Hero ID Name", STRING_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        //{"ImportName","Power.IMPORTNAME", String.class, null, "Import Name", STRING_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED}
    };
    
    static public String NO_ID_STRING = "Character Has No Hero IDs";
    
    // Power Definition Variables
    private static String powerName = "In Hero ID"; // The Name of the Power
    private static String targetType = "SELF"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "PERSISTENT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "INSTANT"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = 5; // The Point cost of a single Damage Class of this power.
    private static String attackType = null; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = null; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 0; // Indicates the END Multiplier for this power
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "Hero ID.*", null},
        { "Hero ID: (.*)", new Object[] { "ImportName", String.class}},
        { "Hero ID.*", null},
    };
    /** Creates new powerHandToHandAttack */
    public powerIHID()  {
        
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
        String heroID = (String)parameterList.getParameterValue("HeroIDName");
        //String importName = (String)parameterList.getParameterValue("ImportName");
        
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
//        if ( importName != null ) {
//            heroID = importName;
//            parameterList.setParameterValue("HeroIDName", heroID);
//            parameterList.setParameterValue("ImportName", null);
//        }
//        
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
        Target source = ability.getSource();
        if ( source != null ) {
            addHeroIDToSource(heroID, ability, source);
        }
        
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    
    public void triggerPower(BattleEvent be, Ability ability, DetailList effectList, Target target, int refNumber, String targetGroup) throws BattleEventException {
        if ( be.getActivationInfo().isContinuing() == false ) {
            ParameterList pl = this.getParameterList(ability);
            String ihidName = (String)pl.getParameterValue("HeroIDName");
            
            String oihidLimitationName = limitationOnlyinHeroID.limitationName;
            
            Ability targetAbility;
            
            AbilityIterator ai = target.getAbilities();
            while ( ai.hasNext() ) {
                targetAbility = ai.nextAbility();
                int index = targetAbility.findLimitation(oihidLimitationName);
                if ( index != -1 ) {
                    // System.out.println("oihid" + OIHID);
                    // Find some ability that is both activated and has limitation Only In Hero ID...
                    // assuming that the ability is in variable ability...
                    //Ability ability = null;
                    
                    // Search for all the activationInfo records which indicate the
                    // ability is activated by the source.  In some cercumstances,
                    // an ability may be activated multiply times by the same source,
                    // and hence will have multiple activationInfo records with that
                    // source.
                    
                    Limitation l = targetAbility.getLimitation(index);
                    ParameterList pl2 = l.getParameterList();
                    String oihidName = (String)pl2.getParameterValue("HeroIDName");
                    
                    if (ihidName.equals(oihidName) && targetAbility.isNormallyOn() && ! targetAbility.isActivated(target)) {
                        BattleEvent newBe;
                        newBe = new BattleEvent(targetAbility);
                        Battle.currentBattle.addEvent( newBe );
                    }
                }
            }
        }
    }
    
    
    public void shutdownPower(BattleEvent be,Target source) throws
            BattleEventException {
        //  for ( abilities that are activated and have limitation ) {
        Ability ability = be.getAbility();
        ParameterList pl = this.getParameterList(ability);
        String ihidName = (String)pl.getParameterValue("HeroIDName");
        
        String oihidLimitationName = limitationOnlyinHeroID.limitationName;
        
        Ability targetAbility;
        
        AbilityIterator iterator = source.getAbilities();
        while ( iterator.hasNext() ) {
            targetAbility = iterator.nextAbility();
            if ( targetAbility.isActivated(source) ) {
                int index = targetAbility.findLimitation(oihidLimitationName);
                if ( index != -1 ) {
                    // Find some ability that is both activated and has limitation Only In Hero ID...
                    // assuming that the ability is in variable ability...
                    
                    // Search for all the activationInfo records which indicate the
                    // ability is activated by the source.  In some cercumstances,
                    // an ability may be activated multiply times by the same source,
                    // and hence will have multiple activationInfo records with that
                    // source.
                    Limitation l = targetAbility.getLimitation(index);
                    
                    ParameterList pl2 = l.getParameterList();
                    String oihidName = (String)pl2.getParameterValue("HeroIDName");
                    
                    int aiIndex = -1;
                    
                    Ability realAbility = targetAbility;
                    // Make sure we are talking about the right ability
                    if ( targetAbility.isBaseInstance() && ! targetAbility.isCurrentInstance() ) {
                        realAbility = targetAbility.getInstanceGroup().getCurrentInstance();
                    }
                    
                    if ( ihidName.equals( oihidName ) ) {

                        Iterator<ActivationInfo> it = realAbility.getActivations( realAbility.getSource());
                        while(it.hasNext()) {
                            ActivationInfo ai = it.next();
                            BattleEvent newBe = new BattleEvent(BattleEvent.DEACTIVATE, ai);
                            Battle.currentBattle.addEvent(newBe);
                        }
                        
                    }
                }
            }
        }
    }
    
    /** Called when a new source has been set for the ability.
     * Used to notify the power that a new source was set and any appropriate actions should be taken.
     * @param source New source of the Ability.
     */
    public void abilitySourceSet(Ability ability, Target oldSource,Target newSource) {
        ParameterList parameterList = getParameterList(ability,-1);
        String heroIDName = (String)parameterList.getParameterValue("HeroIDName");
        
        if ( oldSource != newSource ) {
            
            if ( oldSource != null ) {
                removeHeroIDFromSource(heroIDName, ability, oldSource);
            }
            
            if ( newSource != null ) {
                addHeroIDToSource(heroIDName, ability, newSource);
            }
        }
    }
    
    static public void removeHeroIDFromSource(String idName, Ability ability, Target source) {
        int index;
        
        if ( ( index = source.findExactIndexed("HeroID","ABILITY",ability) ) != -1 ) {
            source.removeAllIndexed(index, "HeroID");
        }
    }
    
    static public  void addHeroIDToSource(String idName, Ability ability, Target source) {
        int index;
        
        if ( idName == null || idName.equals(powerIHID.NO_ID_STRING) ) return;
        
        index = source.findIndexed("HeroID", "NAME", idName);
        if ( index == -1 ) {
            index = source.createIndexed( "HeroID", "NAME", idName, false );
            checkOIHIDNames(ability,source,idName,null);
        }
        
        if ( ability != null ) {
            // Associate the correct ability with this id...
            source.addIndexed(index, "HeroID", "ABILITY", ability, true, false);
            
            int oldIndex = source.findIndexed("HeroID", "ABILITY", ability);
            while ( oldIndex != -1 ) {
                if ( oldIndex != index ) {
                    // This is an old entry, so get rid of it and switch all of the OIHID powers to use
                    // the new name
                    String oldName = source.getIndexedStringValue(oldIndex,"HeroID","NAME");
                    if ( oldName != null && oldName.equals(idName) == false) {
                        checkOIHIDNames(ability,source,idName,oldName);
                    }
                    
                    source.removeAllIndexed(oldIndex, "HeroID");
                }
                
                // Grab the next ability occurance
                oldIndex = source.findIndexed(oldIndex + 1, "HeroID", "ABILITY", ability);
            }
        }
        
        source.fireIndexedChanged("HeroID");
    }
    
    static public void checkOIHIDNames(Ability ability, Target source, String newName, String oldName) {
        
        int aindex, lindex, count;
        ParameterList pl;
        String s;
        Ability a;
        
        String oihidLimitationName = limitationOnlyinHeroID.limitationName;
        
        AbilityIterator ai = source.getAbilities();
        while ( ai.hasNext() ) {
            a = ai.nextAbility();
            //if ( a != null && (lindex = a.findIndexed("Limitation","LIMITATION",oihid)) != -1 ) {
            if ( a != null && (lindex = a.findLimitation(oihidLimitationName)) != -1 ) {
                //Limitation l = (Limitation)a.getIndexedValue(lindex, "Limitation", "LIMITATION");
                Limitation l = a.getLimitation(lindex);
                
                pl = l.getParameterList();
                s = (String)pl.getParameterValue("HeroIDName");
                if ( s == null || s.equals(powerIHID.NO_ID_STRING) || s.equals(oldName) ) {
                    pl.setParameterValue("HeroIDName",newName);
                    l.configurePAD(a,pl);
                }
            }
        }
    }
    
    static public String getValidHeroID(Target source) {
        if (source == null || source.getIndexedSize("HeroID") == 0 ) {
            return powerIHID.NO_ID_STRING;
        } else {
            return source.getIndexedStringValue(0, "HeroID", "NAME");
        }
    }
    
    
    public int calculateCPCost(Ability ability) {
        ParameterList parameterList = getParameterList(ability);
        return 0;
    }
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        return "In Hero ID";
        
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
     *
     * public Object[][] getImportPatterns() {
     * return patterns;
     * }
     */
    
    public Object[][] getImportPatterns() {
        return patterns;
    }
    
    public void importPower(Ability ability, AbilityImport ai) {
        super.importPower(ability, ai);
        ParameterList pl = ability.getPower().getParameterList(ability, -1);
        String importName = (String)pl.getParameterValue("ImportName");
        
        if ( importName == null ) {
            pl.setParameterValue("ImportName", ai.getName());
        }
    }
}