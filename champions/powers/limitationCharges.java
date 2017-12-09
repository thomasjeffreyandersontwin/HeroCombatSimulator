 /*
  * advCombatModifier.java
  *
  * Created on October 2, 2000, 10:05 PM
  */

package champions.powers;

import champions.Ability;
import champions.AbilityAction;
import champions.AbilityImport;
import champions.ActivationInfo;
import champions.Battle;
import champions.BattleEvent;
import champions.ChampionsUtilities;
import champions.Chronometer;
import champions.LimitationAdapter;
import champions.PADRoster;
import champions.Target;
import champions.attackTree.AttackTreeNode;
import champions.exception.BattleEventException;
import champions.exceptionWizard.ExceptionWizard;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.ENDSource;
import champions.interfaces.Limitation;
import champions.parameters.ParameterList;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;




/**
 *
 * @author  unknown
 * @version
 *
 * * To Convert from old format limitation, to new format limitation:
 *
 * 1) Add implements ChampionsConstants to class definition.<P>
 * 2) Copy and Fill in Limitation Definition Variables. <P>
 * 3) Move Parameter Information to parameterArray. <P>
 * 4) Delete getParameters method (unless special parameter handling is necessary.<P>
 * 5) Change configurePAD(Ability,DetailList) method to configurePAD(Ability ability, ParameterList parameterList).<P>
 * 6) Edit configurePAD method to use format specified below.<P>
 * 7) Change checkParameter method to checkParameter(Ability ability, <i>int padIndex</i>,
 * String key, Object value, Object oldValue);
 * 8) Edit getConfigSummary method to use parameterList instead of parseParameter methods.<P>
 * 9) Change all instances of parseParameter to getParameterValue.<P>
 * 10) Add getParameterArray method.<P>
 * 11) Add isUnique method.<P>
 * 12) Edit getName method to return limitationName variable.
 * 13) Add patterns array and define import patterns.<P>
 * 14) Add getImportPatterns() method.<P>
 */
public class limitationCharges extends LimitationAdapter implements ChampionsConstants {
    static final long serialVersionUID = -6870520616682472305L;
    
    static private String[] timeLevels = { "Full Phase", "Extra Phase", "1 Turn", "1 Minute", "5 Minutes", "20 Minutes","1 Hour", "6 Hours", "1 Day", "1 Week", "1 Month", "1 Season","1 Year","5 Years","25 Years", "1 Century" };
    static private String[] recoverableLevels = { "1 Day","1 Week", "1 Month", "1 Season", ">1 Season" };
    
    static private double levels[][] = {
        {1, 1, -2},
        {2, 2, -1.5},
        {3, 3, -1.25},
        {4, 4, -1},
        {5, 6, -0.75},
        {7, 8, -0.5},
        {9, 12, -0.25},
        {13, 16, 0},
        {17, 32, 0.25},
        {33, 64, 0.5},
        {65, 125, 0.75},
        {126, 250, 1},
        {251, 500, 1.25},
        {501, 1000, 1.5},
        {1001, 2000, 1.75},
        {2001, 4000, 2},
        {4001, 8000, 2.25},
        {8001, 16000, 2.5},
        {16001, 32000, 2.75},
        {32001, 64000, 3},
    };
    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first have is replaced by
    // "Advantage#" and only the second half is kept.
    static private Object[][] parameterArray = {
        {"Uses","Limitation#.USES", Integer.class, new Integer(16), "Number of Uses", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(1), "MAXIMUM", new Integer(250)},
                //Set the default number of clips to 0 for those charges that don't use clips (02/15/04 kjr)
        {"Clips","Limitation#.CLIPS", Integer.class, new Integer(1), "Number of Clips", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(1)},
//UsesEND has been discontinued
                //{"UsesEND","Limitation#.USESEND", String.class, "FALSE", "Uses END also", BOOLEAN_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        {"Recoverable","Limitation#.RECOVERABLE", Boolean.class, new Boolean(false), "Recoverable Charges", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"RecoveryLength","Limitation#.RECOVERYLENGTH", String.class, "1 Day", "Recovery Time", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", recoverableLevels},
        {"ChargeLength","Limitation#.CHARGELENGTH", String.class, "Full Phase", "Continuing Charge Length", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", timeLevels},
        {"UsesContinuous","Limitation#.USESCONTINUOUS", Boolean.class, new Boolean(false), "Uses Continuous Charges", BOOLEAN_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
        {"EndSourceName", "Limitation#.ENDSOURCENAME", String.class, "", "Name of the Charges End Source", STRING_PARAMETER, HIDDEN, DISABLED, NOTREQUIRED},
    };
    
    
    //Increased Recovery Time
    // Limitation Definition Variables
    public static String limitationName = "Charges"; // The Name of the Limitation
    private static String limitationDescription = "Charges"; // The Description of the Limitation
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "Charges: \\+([0-9]*),.*", new Object[] { "Uses", Integer.class} },
        { "Charges: ([0-9]*),.*", new Object[] { "Uses", Integer.class} },
        { "Charges \\(([0-9]*)\\).*", new Object[] { "Uses", Integer.class} },
        { "Charges \\(\\+([0-9]*)\\).*", new Object[] { "Uses", Integer.class} },
        { "Clips: ([0-9]*)",  new Object[] { "Clips", Integer.class} },
        { "(Recoverable Charges:).*",  new Object[] { "Recoverable", Boolean.class } },
        { "Continuing Charges:.*",  null },
                //HeroDesigner (02/15/04 kjr - added support for all types of continuing charges)
        { "([0-9]*) Continuing Charge.*lasting (.*) \\(.*",  new Object[] { "Uses", Integer.class,"ChargeLength", String.class} },
        { "([0-9]*) Charge.*",  new Object[] { "Uses", Integer.class } },
        { "([0-9]*) (Recoverable) Charge.*",  new Object[] { "Uses", Integer.class,"Recoverable", Boolean.class  } },
                
                //{ "(Costs END).*",  new Object[] { "UsesEND", Boolean.class } }
                
                
    };
    
    private limitationCharges.ResetUsesAction resetUsesAction = null;
    private limitationCharges.ResetClipsAction resetClipsAction = null;
    private powerChangeClip changeClipPower = null;
    private AbilityAction changeClipAction = null;
    private Ability changeClipAbility = null;
    
    /** Creates new advCombatModifier */
    public limitationCharges() {
    }
    
    public String getName() {
        return limitationName;
    }
    
    public Object[][] getParameterArray() {
        return parameterArray;
    }
    
    public boolean isUnique() {
        return unique;
    }
    
    public boolean configurePAD(Ability ability, ParameterList parameterList) {
        // Fail immediately if ability is null
        if ( ability == null ) return false;
        
        // Read in any parameters that will be needed to configure the power or
        // Determine the validity of the power configuration.  Read the parameters
        // from the parameterList, instead of directly from the ability, since the
        // Ability isn't configured yet.
        Integer uses = (Integer)parameterList.getParameterValue("Uses");
        Integer clips = (Integer)parameterList.getParameterValue("Clips");
        //String usesEND = (String)parameterList.getParameterValue("UsesEND");
        String chargelength = (String)parameterList.getParameterValue("ChargeLength");
        // String continuingCharges = (String)parameterList.getParameterValue("ContinuingCharges");
        String endSourceName = (String)parameterList.getParameterValue("EndSourceName");
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        if (chargelength.equals("1 Extra Phase")) {
            parameterList.setParameterValue("ChargeLength", "Extra Phase");
        }
        
        // !!!Limitation has nothing to validate!!!
        
        // Start to Actually Configure the Power.
        // The Add Power Info should always be executed to add information to the ability.
        // All of this information should be set in the Power Definition Variables at the
        // top of this file
        int index = ability.addLimitationInfo(this, limitationName, parameterList);
        
        
        
        // Add Extra Value/Pairs used by the Limitation/BattleEngine
        // Check for the Charges END Source
        if ( endSourceName == null || endSourceName.equals("") ) {
            endSourceName = getENDSourceName(ability);
            parameterList.setParameterValue("EndSourceName", endSourceName);
        }
        
        Target source = ability.getSource();
        if ( source != null ) {
            endSourceCharges esc = (endSourceCharges)source.getENDSource( endSourceName );
            if ( esc == null ) {
                esc = new endSourceCharges();
                
                esc.setTotalClips(clips.intValue());
                esc.setTotalUses(uses.intValue());
                
                source.addENDSource( endSourceName, esc);
            } 
        }
        
        ability.setPrimaryENDSource( endSourceName );
        
        //Limitation hasCostsEND = PADRoster.getSharedLimitationInstance("limitationCostsEND");
        
        if ( ability.hasLimitation( limitationCostsEND.limitationName ) ) {
            ability.setSecondaryENDSource( "Character" );
            ability.reconfigurePower();
        }
        
        if ( ability.isConstant() || ability.isPersistent() ) {
            ability.setChargeContinuingEND(false);
            parameterList.setVisible("ChargeLength", true);
            parameterList.setParameterValue("UsesContinuous", new Boolean(true));
        } else {
            ability.resetChargeContinuingEND();
            parameterList.setVisible("ChargeLength", false);
            parameterList.setParameterValue("UsesContinuous", new Boolean(false));
        }
        
        // Copy the configuration parameters directly into the ability.  This will
        // take the parameters stored in the parameter list and copy them into the
        // ability using the appropriate keys and values.  Make sure that these
        // Value/Pairs are unique to the PAD.  Any that are not, should be copied
        // manually.
        // Note that if the advantage is not unique, the values should always be
        // accessed via the parameterList and not directly.
        //parameterList.copyValues(ability,index);
        
        // Update the Stored Description for this Limitation
        setDescription( getConfigSummary() );
        
        // Return True to indicate success in configuringPAD
        return true;
    }
    
    
    
    public double calculateMultiplier() {
        ParameterList parameterList = getParameterList();
        Integer uses = (Integer)parameterList.getParameterValue("Uses");
        Integer clips = (Integer)parameterList.getParameterValue("Clips");
        //String usesEND = (String)parameterList.getParameterValue("UsesEND");
        boolean recoverable = (Boolean)parameterList.getParameterValue("Recoverable");
        String recoveryLength = (String)parameterList.getParameterValue("RecoveryLength");
        boolean usesContinuous = (Boolean)parameterList.getParameterValue("UsesContinuous");
        String chargeLength = (String)parameterList.getParameterValue("ChargeLength");
        double total;
        total = 0.0;
        
        int level = getLevelForUses(uses.intValue());
        int maxClip = 1;
        while ( clips.intValue() > maxClip ) {
            if ( getCostForLevel(++level) > 0 ) maxClip *= 4;
            else maxClip *= 2;
        }
        if ( recoverable ) level += 2;
        
        if ( usesContinuous ) {
            level += timeLevelToInt(chargeLength);
        }
        total = getCostForLevel(level);
        
        if (recoveryLength.equals("1 Week")) total -= 0.5;
        else if (recoveryLength.equals("1 Month")) total -= 1.0;
        else if (recoveryLength.equals("1 Season")) total -= 1.5;
        else if (recoveryLength.equals(">1 Season")) total -= 2.0;
        return total;
        
        //return getCostForLevel(level) ;//+ ( usesEND.equals("TRUE") ? -0.5 : 0 );
    }
    
    private int getLevelForUses(int uses) {
        int level = 0;
        for ( level = 0; level < levels.length; level ++) {
            if ( uses >= levels[level][0] && uses <= levels[level][1] ) break;
        }
        return level;
    }
    
    private double getCostForLevel(int level) {
        return levels[level][2];
    }
    
    private int timeLevelToInt(String level) {
        for (int i = 0; i < timeLevels.length; i++) {
            if ( level.equals( timeLevels[i] ) ) {
                return i;
            }
        }
        return 0;
    }
    
    private String getENDSourceName(Ability ability) {
        return ability.getName() + ".Charges";
    }
    
    public String getConfigSummary() {
        if ( ability == null ) return "Error: Null Ability";
        
        ParameterList parameterList = getParameterList();
        Integer uses = (Integer)parameterList.getParameterValue("Uses");
        Integer clips = (Integer)parameterList.getParameterValue("Clips");
        //String usesEND = (String)parameterList.getParameterValue("UsesEND");
        boolean recoverable = (Boolean)parameterList.getParameterValue("Recoverable");
        
        StringBuffer sb = new StringBuffer();
        
        sb.append( "Charges: " );
        sb.append( uses.toString() );
        
        if ( clips.intValue() > 1 )  {
            sb.append( "; Clips: " );
            sb.append( clips.toString() );
        }
        
        if ( recoverable) {
            sb.append( "; Recoverable Charges");
        }
        
        return sb.toString();
    }
    
    /** Removes Special Configurations from the Ability which the limitation might have added.
     *
     * Removelimitation is called prior to an advantage being removed from an ability.  The
     * removelimitation method should remove any value pairs that it specifically added to the
     * ability.
     *
     * The ability will take care of removing the limitation's configuration, object, and parameter
     * lists under the limitation#.* value pairs.
     */
    public void removeLimitation(){
        ability.resetChargeContinuingEND();
    }
    
    
    /** Called when a new source has been set for the ability.
     * Used to notify the power that a new source was set and any appropriate actions should be taken.
     * @param source New source of the Ability.
     */
    public void abilitySourceSet(Ability ability, Target oldSource,Target newSource) {
        if ( oldSource != newSource ) {
            //int index = ability.findExactIndexed("Limitation","LIMITATION",this);
            int index = ability.findExactLimitation(this);
            ParameterList parameterList = getParameterList();
            Integer uses = (Integer)parameterList.getParameterValue("Uses");
            Integer clips = (Integer)parameterList.getParameterValue("Clips");
            String endSourceName = (String)parameterList.getParameterValue("EndSourceName");
            
            endSourceCharges esc = null;
            
            if ( oldSource != null ) {
                esc = (endSourceCharges)oldSource.getENDSource( endSourceName );
                oldSource.removeENDSource( endSourceName );
            }
            
            if ( newSource != null ) {
                if ( esc == null ) esc = new endSourceCharges();
                
                esc.setTotalClips(clips.intValue());
                esc.setTotalUses(uses.intValue());
                
                newSource.addENDSource( endSourceName, esc);
            }
        }
    }
    
    /** Called when the ability's name is changed.
     * Used to notify the PAD that the ability's name was changed and that appropriate actions should
     * be taken.
     * @param oldName String containing the old name
     * @param newName String containing the new name
     */
    public void abilityNameSet(Ability ability, String oldName, String newName) {
        endSourceCharges esc = null;
        Target source = ability.getSource();
        //int index = ability.findExactIndexed("Limitation","LIMITATION",this);
        int index = ability.findExactLimitation(this);
        ParameterList parameterList = getParameterList();
        String oldEndSourceName = (String)parameterList.getParameterValue("EndSourceName");
        
        if ( oldName != null && oldName.equals(newName) == false) {
            
            if ( source != null ) {
                esc = (endSourceCharges)source.getENDSource( oldEndSourceName );
                if ( esc != null ) {
                    source.removeENDSource( oldEndSourceName );
                }
            }
        }
        
        if ( newName != null ) {
            String newEndSourceName = getENDSourceName(ability);
            parameterList.setParameterValue("EndSourceName", newEndSourceName);
            
            if ( source != null ) {
                Integer uses = (Integer)parameterList.getParameterValue("Uses");
                Integer clips = (Integer)parameterList.getParameterValue("Clips");
                
                
                if ( esc == null ) {
                    esc = new endSourceCharges();
                }
                
                esc.setTotalClips(clips.intValue());
                esc.setTotalUses(uses.intValue());
                
                source.addENDSource( newEndSourceName, esc);
            }
        }
    }
    
    /** Provides hook to add menu items to the Ability right-click menu
     * @param menu
     * @param ability
     * @return true if menus where actually added.
     */
    public boolean invokeMenu(JPopupMenu menu) {
        if ( resetUsesAction == null ) resetUsesAction = new limitationCharges.ResetUsesAction();
        if ( resetClipsAction == null ) resetClipsAction = new limitationCharges.ResetClipsAction();
        createChangeClipAction();
        
        Target source = ability.getSource();
        
        ParameterList pl = this.getParameterList();
        String endSourceName = (String)pl.getParameterValue("EndSourceName");
        Integer clips = (Integer)pl.getParameterValue("Clips");
        
        if ( source != null ) {
            endSourceCharges esc = (endSourceCharges)source.getENDSource( endSourceName );
            
            if ( esc != null ) {
                if ( clips.intValue() > 1 ) {
                    changeClipPower.setENDSource(changeClipAbility, esc);
                    menu.add(changeClipAction);
                }
                
                resetUsesAction.setEndSource(esc);
                menu.add(resetUsesAction);
                
                if ( clips.intValue() > 1 ) {
                    resetClipsAction.setEndSource(esc);
                    menu.add(resetClipsAction);
                }
                
                return true;
            }
            
            
        }
        return false;
    }
    
    /**
     * @param menu
     * @param ability
     * @return true if menus where actually added.
     */
    public void addActions(Vector v) {
        if ( ability.getInstanceGroup() == null ) return;
        
        if ( resetUsesAction == null ) resetUsesAction = new limitationCharges.ResetUsesAction();
        if ( resetClipsAction == null ) resetClipsAction = new limitationCharges.ResetClipsAction();
        createChangeClipAction();
        
        Target source = ability.getSource();
        
        ParameterList pl = this.getParameterList();
        String endSourceName = (String)pl.getParameterValue("EndSourceName");
        Integer clips = (Integer)pl.getParameterValue("Clips");
        
        if ( source != null ) {
            ENDSource s = source.getENDSource( endSourceName );
            
            if ( ! (s instanceof endSourceCharges)) {
                ExceptionWizard.postException(
                        new ClassCastException("I expected a endSourceCharges.  Instead I got a " + (s == null ? "null" : s.getClass().toString()) + ".  Please check character debug for end source of " + endSourceName + ".  This exception is just a warning.") );
                return;
            }
            
            endSourceCharges esc = (endSourceCharges)s;
            
            if ( esc != null ) {
                if ( clips.intValue() > 1 ) {
                    changeClipPower.setENDSource(changeClipAbility, esc);
                    v.add(changeClipAction);
                }
                
                resetUsesAction.setEndSource(esc);
                v.add(resetUsesAction);
                
                if ( clips.intValue() > 1 ) {
                    resetClipsAction.setEndSource(esc);
                    v.add(resetClipsAction);
                }
            }
        }
    }
    
    private void createChangeClipAction() {
        if ( changeClipAction == null ) {
            changeClipPower = new powerChangeClip();
            changeClipAbility = new Ability("Change Clip");
            changeClipAbility.addPAD(changeClipPower, null);
            changeClipAction = new AbilityAction( "Change Clip", changeClipAbility);
        }
    }
    
    public int identifyLimitation(AbilityImport ai,int line) {
        int index,count;
        String possibleLimitation;
        
        possibleLimitation = ai.getImportLine(line);
        if ( possibleLimitation != null && possibleLimitation.indexOf( "Charge" ) != -1 ) {
            return 10;
        }
        return 0;
    }
    
    
    
    /** Returns the patterns necessary to import the Power from CW.
     * The Object[][] returned should be in the following format:
     * patterns = Object[][] {
     * { "PATTERN" , new Object[] { "PARAMETER1", parameter1.class, "PARAMETER2", parameter2.class, ... },
     * ...
     * }
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
    
    /** Preactivates the limitation.
     *
     * For continuous charges, preactivate is used to setup the reoccuring END charge.
     * This is necessary since the END might not be charged at the same time the character takes his action.
     *
     */
    public AttackTreeNode preactivate(BattleEvent be) {
        //boolean usesContinuous = ability.getIndexedBooleanValue(index, "Limitation", "USESCONTINUOUS");
        ParameterList pl = getParameterList();
        boolean usesContinuous = (Boolean)pl.getParameterValue("UsesContinuous");
        if ( usesContinuous ) {
            // Check if this is the first activation or if this is the END charge event
            ActivationInfo ai = be.getActivationInfo();
            if ( ai.isContinuing() == false || be.getBooleanValue("ContinuingCharges.ENDCHARGE") == true ) {
                
                int timeLevel = timeLevelToInt((String)pl.getParameterValue("ChargeLength"));
                // Add a new delayed event to charge the next end for this ability
                long seconds;
                
                // The Time table is not exactly according to the ChampionsConstants ones, so
                // treat the first two levels specially...
                if ( timeLevel == 0 ) {
                    // One Full Phase
                    seconds = ChampionsUtilities.calculateSeconds(TIME_ONE_PHASE, 1, ai.getSource());
                } else if ( timeLevel == 1 ) {
                    // Two Full Phases
                    seconds = ChampionsUtilities.calculateSeconds(TIME_ONE_PHASE, 2, ai.getSource());
                } else {
                    // All the other ones line up correctly...
                    seconds = ChampionsUtilities.calculateSeconds(timeLevel, 1, ai.getSource());
                }
                
                Chronometer time = (Chronometer)Battle.getCurrentBattle().getTime().clone();
                time.incrementSegment( seconds );
                
                BattleEvent newBe;
                
                if (time.isActivePhase(ai.getSource().getEffectiveSpeed(time) ) ) {
                    // Since this is a segment in which this character goes, charge the END
                    // immediately before the character goes...
                    newBe = new BattleEvent(BattleEvent.CHARGE_END, ai, time, SEQUENCE_BEFORE_TARGET);
                    newBe.setDelayTarget(ai.getSource());
                } else {
                    // This isn't going to be the characters phase, so go on this characters DEX in the correct segment...
                    newBe = new BattleEvent(BattleEvent.CHARGE_END, ai, time, ai.getSource().getCurrentStat("DEX"));
                }
                newBe.add("ContinuingCharges.ENDCHARGE", "TRUE", true, false);
                
                // Add the Delayed Event to the Battle...
                Battle.getCurrentBattle().addDelayedEvent(newBe);
                
                // Record the delayed event in the activation info, so it can be removed if the
                // ability is shut down...
                ai.add("LimitationCharges.DELAYEDENDBE", newBe, true, false);
            }
            
        }
        
        return null;
    }
    
    /** Notifies the limitation that the Ability has been shutdown.
     *
     * For continuous charges, this makes sure we clean up an outstanding END charges when the ability is shut down.
     */
    public void shutdownAbility(BattleEvent be, Target source) throws BattleEventException {
        //boolean usesContinuous = ability.getIndexedBooleanValue(index, "Limitation", "USESCONTINUOUS");
        ParameterList pl = getParameterList();
        boolean usesContinuous = (Boolean)pl.getParameterValue("UsesContinuous");
        
        if ( usesContinuous ) {
            ActivationInfo ai = be.getActivationInfo();
            
            // If there is an outstanding END Charge in delayed events, remove it.
            BattleEvent newBe = (BattleEvent)ai.getValue("LimitationCharges.DELAYEDENDBE");
            if ( newBe != null ) {
                // Remove it from the queue
                Battle.getCurrentBattle().removeDelayedEvent(newBe);
            }
            
        }
    }
    
    private static class ResetUsesAction extends AbstractAction {
        
        /** Holds value of property endSource. */
        private endSourceCharges endSource;
        
        public ResetUsesAction() {
            super("Reset Used Charges");
        }
        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e) {
            if ( endSource != null ) {
                endSource.setRemainingUses( endSource.getTotalUses() );
            }
        }
        
        /** Getter for property endSource.
         * @return Value of property endSource.
         */
        public endSourceCharges getEndSource() {
            return endSource;
        }
        
        /** Setter for property endSource.
         * @param endSource New value of property endSource.
         */
        public void setEndSource(endSourceCharges endSource) {
            this.endSource = endSource;
        }
        
        public boolean isEnabled() {
            if ( endSource == null ) return false;
            
            return endSource.getRemainingUses() < endSource.getTotalUses();
        }
        
    }
    
    private static class ResetClipsAction extends AbstractAction {
        
        /** Holds value of property endSource. */
        private endSourceCharges endSource;
        
        public ResetClipsAction() {
            super("Reset Used Clips");
        }
        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e) {
            if ( endSource != null ) {
                endSource.setRemainingClips( endSource.getTotalClips() - 1 );
            }
        }
        
        /** Getter for property endSource.
         * @return Value of property endSource.
         */
        public endSourceCharges getEndSource() {
            return endSource;
        }
        
        /** Setter for property endSource.
         * @param endSource New value of property endSource.
         */
        public void setEndSource(endSourceCharges endSource) {
            this.endSource = endSource;
        }
        
        public boolean isEnabled() {
            if ( endSource == null ) return false;
            
            return endSource.getRemainingClips() < endSource.getTotalClips();
        }
        
    }
    
    private static class ChangeClipAction extends AbstractAction {
        
        /** Holds value of property endSource. */
        private endSourceCharges endSource;
        
        public ChangeClipAction() {
            super("Reset Used Clips");
        }
        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e) {
            if ( endSource != null ) {
                endSource.setRemainingClips( endSource.getTotalClips() - 1 );
            }
        }
        
        /** Getter for property endSource.
         * @return Value of property endSource.
         */
        public endSourceCharges getEndSource() {
            return endSource;
        }
        
        /** Setter for property endSource.
         * @param endSource New value of property endSource.
         */
        public void setEndSource(endSourceCharges endSource) {
            this.endSource = endSource;
        }
        
        public boolean isEnabled() {
            if ( endSource == null ) return false;
            
            return endSource.getRemainingClips() < endSource.getTotalClips() - 1;
        }
        
    }
    
    public static class limitationFrameworkCharges extends LimitationAdapter implements ChampionsConstants {
        static final long serialVersionUID = -6870520616689272305L;
        
        static private String[] timeLevels = { "Full Phase", "Extra Phase", "1 Turn", "1 Minute", "5 Minutes", "20 Minutes","1 Hour", "6 Hours", "1 Day", "1 Week", "1 Month", "1 Season","1 Year","5 Years","25 Years", "1 Century" };
        
        static private double levels[][] = {
            {1, 1, -2},
            {2, 2, -1.5},
            {3, 3, -1.25},
            {4, 4, -1},
            {5, 6, -0.75},
            {7, 8, -0.5},
            {9, 12, -0.25},
            {13, 16, 0},
            {17, 32, 0.25},
            {33, 64, 0.5},
            {65, 125, 0.75},
            {126, 250, 1},
            {251, 500, 1.25},
            {501, 1000, 1.5},
            {1001, 2000, 1.75},
            {2001, 4000, 2},
            {4001, 8000, 2.25},
            {8001, 16000, 2.5},
            {16001, 32000, 2.75},
            {32001, 64000, 3},
        };
        // Parameter Definitions
        // Note: The second half of the key must be unique.  During copying, the first have is replaced by
        // "Advantage#" and only the second half is kept.
        static private Object[][] parameterArray = {
            {"Uses","Limitation#.USES", Integer.class, new Integer(16), "Number of Uses", INTEGER_PARAMETER, VISIBLE, DISABLED, NOTREQUIRED, "MINIMUM", new Integer(1), "MAXIMUM", new Integer(250)},
                    //Set the default number of clips to 0 for those charges that don't use clips (02/15/04 kjr)
            {"Clips","Limitation#.CLIPS", Integer.class, new Integer(1), "Number of Clips", INTEGER_PARAMETER, VISIBLE, DISABLED, NOTREQUIRED, "MINIMUM", new Integer(1)},
                    //UsesEND has been discontinued
                    //{"UsesEND","Limitation#.USESEND", String.class, "FALSE", "Uses END also", BOOLEAN_PARAMETER, HIDDEN, DISABLED, NOTREQUIRED},
            {"RecoveryLength","Limitation#.RECOVERYLENGTH", String.class, "1 Day", "Recovery Time", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", recoverableLevels},
            {"Recoverable","Limitation#.RECOVERABLE", String.class, "FALSE", "Recoverable Charges", BOOLEAN_PARAMETER, VISIBLE, DISABLED, NOTREQUIRED},
            {"ChargeLength","Limitation#.CHARGELENGTH", String.class, "Full Phase", "Continuing Charge Length", COMBO_PARAMETER, VISIBLE, DISABLED, NOTREQUIRED, "OPTIONS", timeLevels},
            {"UsesContinuous","Limitation#.USESCONTINUOUS", String.class, "FALSE", "Uses Continuous Charges", BOOLEAN_PARAMETER, HIDDEN, DISABLED, NOTREQUIRED},
            {"EndSourceName", "Limitation#.ENDSOURCENAME", String.class, "", "Name of the Charges End Source", STRING_PARAMETER, HIDDEN, DISABLED, NOTREQUIRED},
        };
        
        // Limitation Definition Variables
        public static String limitationName = "Charges"; // The Name of the Limitation
        private static String limitationDescription = "Charges"; // The Description of the Limitation
        private static boolean unique = true; // Indicates whether multiple copies can be added to ability
        
        // Import Patterns Definitions
        private static Object[][] patterns = {
            
        };
        
        /** Creates new advCombatModifier */
        public limitationFrameworkCharges() {
        }
        
        public String getName() {
            return limitationName;
        }
        
        public Object[][] getParameterArray() {
            return parameterArray;
        }
        
        public boolean isUnique() {
            return unique;
        }
        
        public boolean configurePAD(Ability ability, ParameterList parameterList) {
            // Fail immediately if ability is null
            if ( ability == null ) return false;
            
            // Read in any parameters that will be needed to configure the power or
            // Determine the validity of the power configuration.  Read the parameters
            // from the parameterList, instead of directly from the ability, since the
            // Ability isn't configured yet.
            Integer uses = (Integer)parameterList.getParameterValue("Uses");
            Integer clips = (Integer)parameterList.getParameterValue("Clips");
            //String usesEND = (String)parameterList.getParameterValue("UsesEND");
            String chargelength = (String)parameterList.getParameterValue("ChargeLength");
            // String continuingCharges = (String)parameterList.getParameterValue("ContinuingCharges");
            String endSourceName = (String)parameterList.getParameterValue("EndSourceName");
            
            // Check for the validity of the parameters that will be set.  If the parameters
            // Fail for any reason, return false from the method immediately, indicating a
            // failure to configure
            
            // Start to Actually Configure the Power.
            // The Add Power Info should always be executed to add information to the ability.
            // All of this information should be set in the Power Definition Variables at the
            // top of this file
            int index = ability.addLimitationInfo(this, limitationName, parameterList);
            
            // Add Extra Value/Pairs used by the Limitation/BattleEngine
            // Check for the Charges END Source
//            Target source = ability.getSource();
//            if ( source != null ) {
//                endSourceCharges esc = (endSourceCharges)source.getENDSource( ability.getName() + ".Charges" );
//                if ( esc == null ) esc = new endSourceCharges();
//
//                esc.setTotalClips(clips.intValue());
//                esc.setTotalUses(uses.intValue());
//
//                source.addENDSource( ability.getName() + ".Charges", esc);
//            }
//
            ability.setPrimaryENDSource( endSourceName );
            
            //Limitation hasCostsEND = PADRoster.getSharedLimitationInstance("limitationCostsEND");
            if ( ability.hasLimitation( limitationCostsEND.limitationName ) ) {
                ability.setSecondaryENDSource( "Character" );
                ability.reconfigurePower();
            }
            
            if ( ability.isConstant() || ability.isPersistent() ) {
                ability.setChargeContinuingEND(false);
                parameterList.setVisible("ChargeLength", true);
            } else {
                ability.resetChargeContinuingEND();
                parameterList.setVisible("ChargeLength", false);
            }
            
            // Copy the configuration parameters directly into the ability.  This will
            // take the parameters stored in the parameter list and copy them into the
            // ability using the appropriate keys and values.  Make sure that these
            // Value/Pairs are unique to the PAD.  Any that are not, should be copied
            // manually.
            // Note that if the advantage is not unique, the values should always be
            // accessed via the parameterList and not directly.
            //parameterList.copyValues(ability,index);
            
            // Update the Stored Description for this Limitation
            setDescription( getConfigSummary() );
            
            parameterList.setParameterEnabled("Uses", false);
            parameterList.setParameterEnabled("Clips", false);
            parameterList.setParameterEnabled("Recoverable", false);
            parameterList.setParameterEnabled("UsesContinuous", false);
            parameterList.setParameterEnabled("ChargeLength", false);
            
            // Return True to indicate success in configuringPAD
            return true;
        }
        
        public double calculateMultiplier() {
            ParameterList parameterList = getParameterList();
            Integer uses = (Integer)parameterList.getParameterValue("Uses");
            Integer clips = (Integer)parameterList.getParameterValue("Clips");
            //String usesEND = (String)parameterList.getParameterValue("UsesEND");
            boolean recoverable = (Boolean)parameterList.getParameterValue("Recoverable");
            String recoveryLength = (String)parameterList.getParameterValue("RecoveryLength");
            boolean usesContinuous = (Boolean)parameterList.getParameterValue("UsesContinuous");
            String chargeLength = (String)parameterList.getParameterValue("ChargeLength");
            double total;
            total = 0.0;
            
            int level = getLevelForUses(uses.intValue());
            
            int maxClip = 1;
            while ( clips.intValue() > maxClip ) {
                if ( getCostForLevel(++level) > 0 ) maxClip *= 4;
                else maxClip *= 2;
            }
            
            if ( recoverable ) level += 2;
            
            if ( usesContinuous ) {
                level += timeLevelToInt(chargeLength);
            }
            if (recoveryLength.equals("1 Week")) total -= 0.5;
            else if (recoveryLength.equals("1 Month")) total -= 1.0;
            else if (recoveryLength.equals("1 Season")) total -= 1.5;
            else if (recoveryLength.equals(">1 Season")) total -= 2.0;
            return total;
            
            
            
            // return getCostForLevel(level); // + ( usesEND.equals("TRUE") ? -0.5 : 0 );
        }
        
        private int getLevelForUses(int uses) {
            int level = 0;
            for ( level = 0; level < levels.length; level ++) {
                if ( uses >= levels[level][0] && uses <= levels[level][1] ) break;
            }
            return level;
        }
        
        private double getCostForLevel(int level) {
            return levels[level][2];
        }
        
        private int timeLevelToInt(String level) {
            for (int i = 0; i < timeLevels.length; i++) {
                if ( level.equals( timeLevels[i] ) ) {
                    return i;
                }
            }
            return 0;
        }
        
        
        public String getConfigSummary() {
            if ( ability == null ) return "Error: Null Ability";
            
            ParameterList parameterList = getParameterList();
            Integer uses = (Integer)parameterList.getParameterValue("Uses");
            Integer clips = (Integer)parameterList.getParameterValue("Clips");
            //String usesEND = (String)parameterList.getParameterValue("UsesEND");
            boolean recoverable = (Boolean)parameterList.getParameterValue("Recoverable");
            
            StringBuffer sb = new StringBuffer();
            
            sb.append( "Charges: " );
            sb.append( uses.toString() );
            
            if ( clips.intValue() > 1 )  {
                sb.append( "; Clips: " );
                sb.append( clips.toString() );
            }
            
            if ( recoverable ) {
                sb.append( "; Recoverable Chages");
            }
            
            return sb.toString();
        }
        
        /** Removes Special Configurations from the Ability which the limitation might have added.
         *
         * Removelimitation is called prior to an advantage being removed from an ability.  The
         * removelimitation method should remove any value pairs that it specifically added to the
         * ability.
         *
         * The ability will take care of removing the limitation's configuration, object, and parameter
         * lists under the limitation#.* value pairs.
         */
        public void removeLimitation(){
            ability.resetChargeContinuingEND();
        }
        
        
        /** Called when a new source has been set for the ability.
         * Used to notify the power that a new source was set and any appropriate actions should be taken.
         * @param source New source of the Ability.
         */
//        public void abilitySourceSet(Ability ability, Target oldSource,Target newSource) {
//            if ( oldSource != newSource ) {
//                int index = ability.findExactIndexed("Limitation","LIMITATION",this);
//                ParameterList parameterList = getParameterList();
//                Integer uses = (Integer)parameterList.getParameterValue("Uses");
//                Integer clips = (Integer)parameterList.getParameterValue("Clips");
//
//                endSourceCharges esc = null;
//
//                if ( oldSource != null ) {
//                    esc = (endSourceCharges)oldSource.getENDSource( ability.getName() + ".Charges" );
//                    oldSource.removeENDSource( ability.getName() + ".Charges");
//                }
//
//                if ( newSource != null ) {
//                    if ( esc == null ) esc = new endSourceCharges();
//
//                    esc.setTotalClips(clips.intValue());
//                    esc.setTotalUses(uses.intValue());
//
//                    newSource.addENDSource( ability.getName() + ".Charges", esc);
//                }
//
//
//
//            }
//        }
        
        /** Called when the ability's name is changed.
         * Used to notify the PAD that the ability's name was changed and that appropriate actions should
         * be taken.
         * @param oldName String containing the old name
         * @param newName String containing the new name
         */
//        public void abilityNameSet(Ability ability, String oldName, String newName) {
//            if ( oldName != null && oldName.equals(newName) == false) {
//                Target source = ability.getSource();
//                endSourceCharges esc = (endSourceCharges)source.getENDSource( oldName + ".Charges" );
//                if ( esc != null ) {
//                    source.removeENDSource( oldName + ".Charges" );
//                    source.addENDSource( newName + ".Charges", esc );
//                }
//
//            }
//            else if ( newName != null ) {
//                Target source = ability.getSource();
//
//                int index = ability.findExactIndexed("Limitation","LIMITATION",this);
//                ParameterList parameterList = getParameterList();
//                Integer uses = (Integer)parameterList.getParameterValue("Uses");
//                Integer clips = (Integer)parameterList.getParameterValue("Clips");
//
//                endSourceCharges esc = new endSourceCharges();
//
//                esc.setTotalClips(clips.intValue());
//                esc.setTotalUses(uses.intValue());
//
//                source.addENDSource( newName + ".Charges", esc);
//            }
//        }
        
        /** Provides hook to add menu items to the Ability right-click menu
         * @param menu
         * @param ability
         * @return true if menus where actually added.
         */
//        public boolean invokeMenu(JPopupMenu menu) {
//            if ( resetUsesAction == null ) resetUsesAction = new limitationCharges.ResetUsesAction();
//            if ( resetClipsAction == null ) resetClipsAction = new limitationCharges.ResetClipsAction();
//            createChangeClipAction();
//
//            Target source = ability.getSource();
//
//            if ( source != null ) {
//                endSourceCharges esc = (endSourceCharges)source.getENDSource( ability.getName() + ".Charges" );
//
//                if ( esc != null ) {
//                    changeClipPower.setENDSource(changeClipAbility, esc);
//                    menu.add(changeClipAction);
//
//                    resetUsesAction.setEndSource(esc);
//                    menu.add(resetUsesAction);
//
//                    resetClipsAction.setEndSource(esc);
//                    menu.add(resetClipsAction);
//
//                    return true;
//                }
//
//
//            }
//            return false;
//        }
        
//        private void createChangeClipAction() {
//            if ( changeClipAction == null ) {
//                changeClipPower = new powerChangeClip();
//                changeClipAbility = new Ability("Change Clip");
//                changeClipAbility.addPAD(changeClipPower, null);
//                changeClipAction = new AbilityAction( "Change Clip", changeClipAbility);
//            }
//        }
        
        /** Returns the patterns necessary to import the Power from CW.
         * The Object[][] returned should be in the following format:
         * patterns = Object[][] {
         * { "PATTERN" , new Object[] { "PARAMETER1", parameter1.class, "PARAMETER2", parameter2.class, ... },
         * ...
         * }
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
        
        /** Preactivates the limitation.
         *
         * For continuous charges, preactivate is used to setup the reoccuring END charge.
         * This is necessary since the END might not be charged at the same time the character takes his action.
         *
         */
        public AttackTreeNode preactivate(BattleEvent be) {
            //boolean usesContinuous = ability.getIndexedBooleanValue(index, "Limitation", "USESCONTINUOUS");
            ParameterList pl = getParameterList();
            boolean usesContinuous = (Boolean)pl.getParameterValue("UsesContinuous");
            
            if ( usesContinuous ) {
                // Check if this is the first activation or if this is the END charge event
                ActivationInfo ai = be.getActivationInfo();
                if ( ai.isContinuing() == false || be.getBooleanValue("ContinuingCharges.ENDCHARGE") == true ) {
                    //ParameterList pl = getParameterList();
                    int timeLevel = timeLevelToInt((String)pl.getParameterValue("ChargeLength"));
                    // Add a new delayed event to charge the next end for this ability
                    long seconds;
                    
                    // The Time table is not exactly according to the ChampionsConstants ones, so
                    // treat the first two levels specially...
                    if ( timeLevel == 0 ) {
                        // One Full Phase
                        seconds = ChampionsUtilities.calculateSeconds(TIME_ONE_PHASE, 1, ai.getSource());
                    } else if ( timeLevel == 1 ) {
                        // Two Full Phases
                        seconds = ChampionsUtilities.calculateSeconds(TIME_ONE_PHASE, 2, ai.getSource());
                    } else {
                        // All the other ones line up correctly...
                        seconds = ChampionsUtilities.calculateSeconds(timeLevel, 1, ai.getSource());
                    }
                    
                    Chronometer time = (Chronometer)Battle.getCurrentBattle().getTime().clone();
                    time.incrementSegment( seconds );
                    
                    BattleEvent newBe;
                    
                    if (time.isActivePhase(ai.getSource().getEffectiveSpeed(time) ) ) {
                        // Since this is a segment in which this character goes, charge the END
                        // immediately before the character goes...
                        
                        newBe = new BattleEvent(BattleEvent.CHARGE_END, ai, time, SEQUENCE_BEFORE_TARGET);
                        newBe.setDelayTarget(ai.getSource());
                    } else {
                        // This isn't going to be the characters phase, so go on this characters DEX in the correct segment...
                        newBe = new BattleEvent(BattleEvent.CHARGE_END, ai, time, ai.getSource().getCurrentStat("DEX"));
                    }
                    newBe.add("ContinuingCharges.ENDCHARGE", "TRUE", true, false);
                    
                    // Add the Delayed Event to the Battle...
                    Battle.getCurrentBattle().addDelayedEvent(newBe);
                    
                    // Record the delayed event in the activation info, so it can be removed if the
                    // ability is shut down...
                    ai.add("LimitationCharges.DELAYEDENDBE", newBe, true, false);
                }
                
            }
            
            return null;
        }
        
        /** Notifies the limitation that the Ability has been shutdown.
         *
         * For continuous charges, this makes sure we clean up an outstanding END charges when the ability is shut down.
         */
        public void shutdownAbility(BattleEvent be, Target source) throws BattleEventException {
            //boolean usesContinuous = ability.getIndexedBooleanValue(index, "Limitation", "USESCONTINUOUS");
            
            ParameterList pl = getParameterList();
            boolean usesContinuous = (Boolean)pl.getParameterValue("UsesContinuous");
        
            if ( usesContinuous ) {
                ActivationInfo ai = be.getActivationInfo();
                
                // If there is an outstanding END Charge in delayed events, remove it.
                BattleEvent newBe = (BattleEvent)ai.getValue("LimitationCharges.DELAYEDENDBE");
                if ( newBe != null ) {
                    // Remove it from the queue
                    Battle.getCurrentBattle().removeDelayedEvent(newBe);
                }
                
            }
        }
        
        
    }
}