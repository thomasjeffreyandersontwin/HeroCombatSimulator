/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;

import champions.Ability;
import champions.AbilityImport;
import champions.FrameworkAbility;
import champions.LimitationAdapter;
import champions.interfaces.ChampionsConstants;
import champions.parameters.ParameterList;



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
 */
public class limitationExtraTime extends LimitationAdapter
implements ChampionsConstants {
    static final long serialVersionUID = -6870520416782472305L;
    
    static private String[] levels = { "Extra Segment", "Full Phase", "Extra Phase", "1 Turn", "1 Minute", "5 Minutes", "20 Minutes", "1 Hour", "6 Hours", "1 Day", "1 Week", "1 Month", "1 Season", "1 Year", "5 Years", "25 Years" };
    static private Double[] interruptvalue = { new Double(0),new Double(0.25),new Double(0.50),new Double(0.75),new Double(1.0) };
    
    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first have is replaced by
    // "Advantage#" and only the second half is kept.
    static private Object[][] parameterArray = {
        {"ExtraTimeLevel","Limitation#.LEVEL", String.class, "Full Phase", "Amount of Extra Time", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", levels},
        {"ExtraTimeExclusive","Limitation#.EXTRATIMEEXCLUSIVE", Boolean.class, new Boolean(false), "Character can do nothing else during activation", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"DelayedPhase","Limitation#.DelayedPhase", Boolean.class, new Boolean(false), "Delayed Phase", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"NotInterruptible","Limitation#.NOTINTERRUPTIBLE", Boolean.class, new Boolean(false), "Power Not Interruptible", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"NotInterruptibleLimitation","Limitation#.NOTINTERRUPTIBLELIMITATION", Double.class, new Double(0), "Power Not Interruptible Limitation", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", interruptvalue},
        
    };
    
    // Limitation Definition Variables
    public static String limitationName = "Extra Time"; // The Name of the Limitation
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "Extra Time: (.*),.*", new Object[] { "ExtraTimeLevel", String.class} },
        { "Extra Time \\((.*) \\(.*\\)",new Object[] {"ExtraTimeLevel", String.class} },
        
    };
    
    
    /** Creates new advCombatModifier */
    public limitationExtraTime() {
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
        String level = (String)parameterList.getParameterValue("ExtraTimeLevel");
        boolean extratimeexclusive = (Boolean)parameterList.getParameterValue("ExtraTimeExclusive");
        boolean notinterruptible = (Boolean)parameterList.getParameterValue("NotInterruptible");
        boolean delayedphase = (Boolean)parameterList.getParameterValue("DelayedPhase");
        
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        // !!!Limitation has nothing to validate!!!
        
        
        if (level.equals("PHASE") ){
            parameterList.setParameterValue("DelayedPhase", new Boolean(true));
        } else if (level.equals("SEGMENT") ){
            parameterList.setParameterValue("ExtraTimeLevel","Extra Segment");
        } else if (level.equals("FULL") ){
            parameterList.setParameterValue("ExtraTimeLevel","Full Phase");
        } else if (level.equals("EXTRA") ){
            parameterList.setParameterValue("ExtraTimeLevel","Extra Phase");
        } else if (level.equals("TURN") ){
            parameterList.setParameterValue("ExtraTimeLevel","1 Turn");
        } else if (level.equals("1MINUTE") ){
            parameterList.setParameterValue("ExtraTimeLevel","1 Minute");
        } else if (level.equals("5MINUTES") ){
            parameterList.setParameterValue("ExtraTimeLevel","5 Minutes");
        } else if (level.equals("20MINUTES") ){
            parameterList.setParameterValue("ExtraTimeLevel","20 Minutes");
        } else if (level.equals("1HOUR") ){
            parameterList.setParameterValue("ExtraTimeLevel","1 Hour");
        } else if (level.equals("6HOURS") ){
            parameterList.setParameterValue("ExtraTimeLevel","6 Hours");
        } else if (level.equals("1DAY") ){
            parameterList.setParameterValue("ExtraTimeLevel","1 Day");
        } else if (level.equals("1WEEK") ){
            parameterList.setParameterValue("ExtraTimeLevel","1 Week");
        } else if (level.equals("1MONTH") ){
            parameterList.setParameterValue("ExtraTimeLevel","1 Month");
        } else if (level.equals("1SEASON") ){
            parameterList.setParameterValue("ExtraTimeLevel","1 Season");
        } else if (level.equals("1YEAR") ){
            parameterList.setParameterValue("ExtraTimeLevel","1 Year");
        } else if (level.equals("5YEARS") ){
            parameterList.setParameterValue("ExtraTimeLevel","5 Years");
        } else if (level.equals("25YEARS") ){
            parameterList.setParameterValue("ExtraTimeLevel","25 Years");
        }
        
        if (level.equals("full phase") ){
            parameterList.setParameterValue("ExtraTimeLevel","Full Phase");
        } else if (level.equals("1 turn") ){
            parameterList.setParameterValue("ExtraTimeLevel","1 Turn");
        } else if (level.equals("1 min.") ){
            parameterList.setParameterValue("ExtraTimeLevel","1 Minute");
        } else if (level.equals("5 min.") ){
            parameterList.setParameterValue("ExtraTimeLevel","5 Minutes");
        } else if (level.equals("1 hour") ){
            parameterList.setParameterValue("ExtraTimeLevel","1 Hour");
        } else if (level.equals("6 hrs") ){
            parameterList.setParameterValue("ExtraTimeLevel","6 Hours");
        } else if (level.equals("1 day") ){
            parameterList.setParameterValue("ExtraTimeLevel","1 Day");
        } else if (level.equals("1 week") ){
            parameterList.setParameterValue("ExtraTimeLevel","1 Week");
        } else if (level.equals("1 month") ){
            parameterList.setParameterValue("ExtraTimeLevel","1 Month");
        } else if (level.equals("1 season") ){
            parameterList.setParameterValue("ExtraTimeLevel","1 Season");
        }
        
        // Don't apply this to the activation of a framework.  It should only be 
        // applied to the reconfiguration time of a framework
        if (ability.getActivationTime().equals("ATTACK") ) {
            parameterList.setVisible( "ExtraTimeExclusive", false);
            parameterList.setParameterValue("ExtraTimeExclusive", new Boolean(true));
        }
        else {
            parameterList.setVisible( "ExtraTimeExclusive", true);
        }
        
        if (notinterruptible ) {
            parameterList.setVisible( "NotInterruptibleLimitation", true);
        }
        else {
            parameterList.setVisible( "NotInterruptibleLimitation", false);
        }
        
        
        
        // Start to Actually Configure the Power.
        // The Add Power Info should always be executed to add information to the ability.
        // All of this information should be set in the Power Definition Variables at the
        // top of this file
        int index = ability.addLimitationInfo(this, limitationName, parameterList);
        
        // Copy the configuration parameters directly into the ability.  This will
        // take the parameters stored in the parameter list and copy them into the
        // ability using the appropriate keys and values.  Make sure that these
        // Value/Pairs are unique to the PAD.  Any that are not, should be copied
        // manually.
        // Note that if the advantage is not unique, the values should always be
        // accessed via the parameterList and not directly.
        //parameterList.copyValues(ability,index);
        
        // Add Extra Value/Pairs used by the Limitation/BattleEngine
        // Add Ability Delay information
        if ( ability.getInstanceGroup() == null || 
            ! ( ability.getInstanceGroup().getBaseInstance() instanceof FrameworkAbility ) ) {
            
            if (level.equals("Full Phase") && ability.getActivationTime().equals("ATTACK") == false ) {
                // Don't switch this since ATTACK >= FULLMOVE.
                //ability.add("Ability.TIME", "FULLMOVE" , true );
                ability.setActivationTime("FULLMOVE", true);
            }
        
            int interruptibleValue = notinterruptible ? NOT_INTERRUPTIBLE : INTERRUPTIBLE_BY_ANY_DAMAGING_ATTACK;
            boolean exclusiveValue = extratimeexclusive;
            
            //String description = "Activating " + ability.getName() + " (" + level + ")";
            
            if (level.equals("Extra Segment") ) {

                ability.addDelayInfo("Extra Time", interruptibleValue, 1, exclusiveValue, TIME_ONE_SEGMENT, 1, false );
            }
            else if (level.equals("Extra Phase") ) {
                ability.addDelayInfo("Extra Time", interruptibleValue, 1, exclusiveValue, TIME_ONE_PHASE, 1, false);
            }
            else {
                ability.addDelayInfo("Extra Time", interruptibleValue, 1, exclusiveValue, levelToInt(level), 1, (levelToInt(level) == TIME_ONE_PHASE));
            }
        }
        // Update the Stored Description for this Limitation
        setDescription( getConfigSummary() );
        
        // Return True to indicate success in configuringPAD
        return true;
    }
    
    
    public double calculateMultiplier() {
        ParameterList parameterList = getParameterList();
        String level = (String)parameterList.getParameterValue("ExtraTimeLevel");
        boolean extratimeexclusive = (Boolean)parameterList.getParameterValue("ExtraTimeExclusive");
        boolean notinterruptible = (Boolean)parameterList.getParameterValue("NotInterruptible");
        boolean delayedphase = (Boolean)parameterList.getParameterValue("DelayedPhase");
        Double notinterruptiblelimitation = (Double)parameterList.getParameterValue("NotInterruptibleLimitation");
        
        double total = 0;
        if ( extratimeexclusive && !ability.getActivationTime().equals("ATTACK") ) {
            total = total + -0.25;
        }
        if ( delayedphase ) {
            total = total + -0.25;
        }
        
        if (level.equals("Extra Segment") ) {
            total = total + - .50;
        }
        else if (level.equals("Full Phase") ) {
            total = total + - .50;
        }
        else if (level.equals("Extra Phase") ) {
            total = total + - .75;
        }
        else if (level.equals("1 Turn") ) {
            total = total + - 1.25;
        }
        else {
            total = total + (-0.5 * levelToInt(level) );
        }
        
        if (notinterruptible ) {
            total = total + notinterruptiblelimitation.doubleValue();
        }
        return total;
    }
    
    public String getConfigSummary() {
        ParameterList parameterList = getParameterList();
        String level = (String)parameterList.getParameterValue("ExtraTimeLevel");
        
        return "Extra Time(" + level + ")";
    }
    
    public int identifyLimitation(AbilityImport ai,int line) {
        int index,count;
        String possibleLimitation;
        
        possibleLimitation = ai.getImportLine(line);
        if ( possibleLimitation != null && possibleLimitation.indexOf( "Extra Time" ) != -1 ) {
            return 10;
        }
        return 0;
    }
    
    private int levelToInt(String level) {
        if (level.equals("Full Phase")) return 1;
        for (int i = 0; i < levels.length; i++) {
            if ( level.equals( levels[i] ) ) return i - 1;
            //System.out.println("i: " + i + "level: " + level);
        }
        return 0;
    }
    public Object[][] getImportPatterns() {
        return patterns;
    }
}