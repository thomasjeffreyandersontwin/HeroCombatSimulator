/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;

import champions.*;
import champions.interfaces.*;
import champions.parameters.ParameterList;

/**
 *
 * @author  unknown
 * @version
 */
public class advantageFadeRate extends AdvantageAdapter implements ChampionsConstants{
    static final long serialVersionUID = -6399128355213532419L;
    /** Creates new advCombatModifier */
    static private String[] faderateOptions = {"1 Turn", "1 Minute", "5 Minutes", "20 Minutes", "1 Hour", "6 Hours", "1 Day",
    "1 Week", "1 Month", "1 Season", "1 Year", "5 Years", "25 Years", "1 Century" };
    
    static private Object[][] parameterArray = {
        //        {"Multiplier","CEV.MULTIPLIER", String.class, "TRUE"},
        {"FadeRate", "Advantage#.FADERATE", String.class, "1 Turn", "Fade Rate Level", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", faderateOptions }
    };
    
    // Advantage Definition Variables
    public static String advantageName = "Delayed Return Rate"; // The Name of the Advantage
    private static boolean affectsDC = true; // Indicates the Advantage affects the cost of damage classes
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
//        { ".* Fade/(.*), .*", new Object[] { "FadeRate", String.class }}
        { "Delayed Return Rate: (.*),.*", new Object[] { "FadeRate", String.class }},
        { "Delayed Return Rate \\((.*)\\).*", new Object[] { "FadeRate", String.class }}
    };
    
    public advantageFadeRate() {
    }
    
    public String getName() {
        return advantageName;
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
        String faderate = (String)parameterList.getParameterValue("FadeRate");
        
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        if (faderate.equals("MINUTE")) {
            parameterList.setParameterValue("FadeRate", "1 Minute");
        }
        else if (faderate.equals("FIVEMINUTES")) {
            parameterList.setParameterValue("FadeRate", "5 Minutes");
        }
        else if (faderate.equals("20MINUTES")) {
            parameterList.setParameterValue("FadeRate", "20 Minutes");
        }
        else if (faderate.equals("HOUR")) {
            parameterList.setParameterValue("FadeRate", "1 Hour");
        }
        else if (faderate.equals("6HOURS")) {
            parameterList.setParameterValue("FadeRate", "6 Hours");
        }
        else if (faderate.equals("DAY")) {
            parameterList.setParameterValue("FadeRate", "1 Day");
        }
        else if (faderate.equals("WEEK")) {
            parameterList.setParameterValue("FadeRate", "1 Week");
        }
        else if (faderate.equals("MONTH")) {
            parameterList.setParameterValue("FadeRate", "1 Month");
        }
        else if (faderate.equals("SEASON")) {
            parameterList.setParameterValue("FadeRate", "1 Season");
        }
        else if (faderate.equals("YEAR")) {
            parameterList.setParameterValue("FadeRate", "1 Year");
        }
        else if (faderate.equals("FIVEYEARS")) {
            parameterList.setParameterValue("FadeRate", "5 Years");
        }
        else if (faderate.equals("TWENTYFIVEYEARS")) {
            parameterList.setParameterValue("FadeRate", "25 Years");
        }
        else if (faderate.equals("CENTURY")) {
            parameterList.setParameterValue("FadeRate", "1 Century");
        }
                
        // AdvantageAreaEffect has nothing to validate.
        
        // Start to Actually Configure the Power.
        // The Add Power Info should always be executed to add information to the ability.
        // All of this information should be set in the Power Definition Variables at the
        // top of this file
        int index = ability.addAdvantageInfo(this, advantageName, parameterList);
        this.setAffectsDC(affectsDC);
        
        // Copy the configuration parameters directly into the ability.  This will
        // take the parameters stored in the parameter list and copy them into the
        // ability using the appropriate keys and values.  Make sure that these
        // Value/Pairs are unique to the PAD.  Any that are not, should be copied
        // manually.
        // Note that if the advantage is not unique, the values should always be
        // accessed via the parameterList and not directly.
        //parameterList.copyValues(ability,index);
        
        // Add Extra Value/Pairs used by the Advantage/BattleEngine
        ability.add("Ability.DECAYINTERVAL", new Long( getDelayInterval(ability, index) ), true);
       
        // Update the Stored Description for this Limitation
        setDescription( getConfigSummary() );
        
        // Return True to indicate success in configuringPAD
        return true;
        
    }
    
    
    public String getConfigSummary() {
        ParameterList parameterList = getParameterList();
        String faderate = (String)parameterList.getParameterValue("FadeRate");
        
        StringBuffer sb = new StringBuffer();
        sb.append( "fade/" + faderate );
        
        return sb.toString();
    }
     
    
    public double calculateMultiplier() {
        ParameterList parameterList = getParameterList();
        String faderate = (String)parameterList.getParameterValue("FadeRate");
        double cost = 0.0;
        
        int i;
        
        for (i = 0;i< faderateOptions.length;i++) {
            if ( faderate.equals(faderateOptions[i] )) break;
        }
        
        cost = (i) * .25;
        
        return cost;
    }
    
    private long getDelayInterval(Ability ability, int index) {
        ParameterList parameterList = getParameterList();
        String faderate = (String)parameterList.getParameterValue("FadeRate");
        double cost = 0.0;
        
        int i;
        
        for (i = 0;i< faderateOptions.length;i++) {
            if ( faderate.equals(faderateOptions[i] )) break;
        
        }
        
        i = i + 2; // Skip the Segment time rate and Phase time rate
        
        // After the above calculation, i should correspond to the time chart in the ChampionsConstants file.
        return ChampionsUtilities.calculateSeconds(i, 1);
    }

        /** Attempt to identify Advantage
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     * @param ai AbilityImport which is currently being imported.
     * @return Value indicating likelyhood that AbilityImport is this kind of power.
     * 0 indicates there was no recognition.
     * 5 indicates probable match.
     * 10 indicates almost certain recognition.
     *
     */
    public int identifyAdvantage(AbilityImport ai, int line) {
        int index,count;
        String possibleAdvantage;
        
        possibleAdvantage = ai.getImportLine(line);
        if ( possibleAdvantage != null && possibleAdvantage.indexOf( getName()  ) != -1 ) {
            return 10;
        }
        
        return 0;
    }
    
    
    public Object[][] getImportPatterns() {
        return patterns;
    }
    
    public void removeAdvantage() {
        ability.remove("Ability.DECAYINTERVAL");
    }
    
}