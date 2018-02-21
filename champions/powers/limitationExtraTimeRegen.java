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

public class limitationExtraTimeRegen extends LimitationAdapter
implements ChampionsConstants {
    static final long serialVersionUID = -6870520416782472305L;
    
    static private String[] levels = { "1 Minute", "5 Minutes", "20 Minutes", "1 Hour", "6 Hours", "1 Day", "1 Week", "1 Month", "1 Season", "1 Year", "5 Years", "25 Years" };
    static private Double[] interruptvalue = { new Double(0),new Double(0.25),new Double(0.50),new Double(0.75),new Double(1.0) };
    
    static private Object[][] parameterArray = {
        {"ExtraTimeLevel","Limitation#.LEVEL", String.class, "Full Phase", "Amount of Extra Time", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", levels},
        {"ExtraTimeExclusive","Limitation#.EXTRATIMEEXCLUSIVE", Boolean.class, new Boolean(false), "Character can do nothing else during activation", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"DelayedPhase","Limitation#.DelayedPhase", Boolean.class, new Boolean(false), "Delayed Phase", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"NotInterruptible","Limitation#.NOTINTERRUPTIBLE", Boolean.class, new Boolean(false), "Power Not Interruptible", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"NotInterruptibleLimitation","Limitation#.NOTINTERRUPTIBLELIMITATION", Double.class, new Double(0), "Power Not Interruptible Limitation", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", interruptvalue},
        
    };
    
    // Limitation Definition Variables
    public static String limitationName = "Extra Time (Regeneration Only)"; // The Name of the Limitation
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "Extra Time: (.*),.*", new Object[] { "ExtraTimeLevel", String.class} },
        { "Extra Time \\((.*) \\(.*\\)",new Object[] {"ExtraTimeLevel", String.class} },
        
    };
    
    
    /** Creates new advCombatModifier */
    public limitationExtraTimeRegen() {
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
        if ( ability == null ) return false;
        String level = (String)parameterList.getParameterValue("ExtraTimeLevel");
        boolean extratimeexclusive = (Boolean)parameterList.getParameterValue("ExtraTimeExclusive");
        boolean notinterruptible = (Boolean)parameterList.getParameterValue("NotInterruptible");
        boolean delayedphase = (Boolean)parameterList.getParameterValue("DelayedPhase");

        if (level.equals("TURN") ){
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
        
        int index = ability.addLimitationInfo(this, limitationName, parameterList);
        if ( ability.getInstanceGroup() == null || 
            ! ( ability.getInstanceGroup().getBaseInstance() instanceof FrameworkAbility ) ) {
            
            if (level.equals("Full Phase") && ability.getActivationTime().equals("ATTACK") == false ) {
                ability.setActivationTime("FULLMOVE", true);
            }
        
            int interruptibleValue = notinterruptible ? NOT_INTERRUPTIBLE : INTERRUPTIBLE_BY_ANY_DAMAGING_ATTACK;
            boolean exclusiveValue = extratimeexclusive;        
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
        setDescription( getConfigSummary() );
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
        }
        return 0;
    }
    public Object[][] getImportPatterns() {
        return patterns;
    }
}