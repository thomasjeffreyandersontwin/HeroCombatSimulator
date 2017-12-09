/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;

import champions.Ability;
import champions.AbilityImport;
import champions.BattleEvent;
import champions.Effect;
import champions.LimitationAdapter;
import champions.Target;
import champions.attackTree.AttackTreeNode;
import champions.attackTree.ExecuteRunnableNode;
import champions.exception.BattleEventException;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.Limitation;
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
 * 13) Add patterns array and define import patterns.<P>
 * 14) Add getImportPatterns() method.<P>
 */
public class limitationGestures extends LimitationAdapter implements Limitation, ChampionsConstants {
    static final long serialVersionUID = -6870510616782472305L;
    
    static private String[] levelOptions = {"During Activation Only","Throughout"};
    
    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first have is replaced by
    // "Advantage#" and only the second half is kept.
    static private Object[][] parameterArray = {
        {"GesturesLevel","Gestures.LEVEL", String.class, "During Activation Only", "Gestures Level", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", levelOptions},
        {"RequiresBothHands","Gestures.REQUIRESBOTHHANDS", Boolean.class, new Boolean(false), "Both hands are required", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED },
        {"Complex","Gestures.COMPLEX", Boolean.class, new Boolean(false), "Complex", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED },
        //{"Throughout","Gestures.THROUGHOUT", String.class, "FALSE", "Requires gestures throughout", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED },

    };
    
    // Limitation Definition Variables
    public static String limitationName = "Gestures"; // The Name of the Limitation
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "Gestures.*: (.*),.*", new Object[] { "GesturesLevel", String.class}},
        { "Gestures.*: (.*),.*", new Object[] { "GesturesLevel", String.class}},
        
        //HD
        { "Gestures, Requires Gestures (throughout).*(Requires both hands).*", new Object[] { "GesturesLevel", String.class, "RequiresBothHands", Boolean.class}},
        { "Gestures .*(Requires both hands).*", new Object[] { "RequiresBothHands", Boolean.class}},
        { "Gestures.*", null},
        { "GESTURES: (BOTHHAND)", new Object[] { "RequiresBothHands", Boolean.class}},
        { "GESTURES.*", null},
          
        
    };
    
    /** Creates new advCombatModifier */
    public limitationGestures() {
    }
    
    public String getName() {
        return "Gestures";
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
        String level = (String)parameterList.getParameterValue("GesturesLevel");
        //String throughout = (String)parameterList.getParameterValue("Throughout");
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        // !!!Limitation has nothing to validate!!!
        if (level.equals("Constant Power") ){
            parameterList.setParameterValue( "GesturesLevel", "Throughout");
            level = (String)parameterList.getParameterValue("GesturesLevel");
            ability.reconfigurePower();
        }
        
        String ptype;
        if ( (ptype = ability.getPType()) != null ) {
            if ( ptype.equals( "INSTANT" ) ) {
                parameterList.setVisible( "GesturesLevel", false);
                //                ability.reconfigurePower();
            }
            else {
                parameterList.setVisible( "GesturesLevel", true);
                //                ability.reconfigurePower();
            }
        }
        
        if (level.equals("throughout")) {
            parameterList.setParameterValue("GesturesLevel", "Throughout" );
        }
        
//        if (throughout.equals("TRUE")) {
//            parameterList.setParameterValue("GesturesLevel", "Throughout" );
//        }
//        else if (throughout.equals("FALSE")) {
//            parameterList.setParameterValue("GesturesLevel","During Activation Only");
//        }
        
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
        level = (String)parameterList.getParameterValue("GesturesLevel");
        
        // Add Ability Delay information
        ability.addDelayInfo("Gesturing", INTERRUPTIBLE_BY_ANY_DAMAGING_ATTACK, 1, false, TIME_ONE_SEGMENT, 0, false);
        
        // Update the Stored Description for this Limitation
        setDescription( getConfigSummary() );
        
        // Return True to indicate success in configuringPAD
        return true;
    }
    
    /* Returns whether an ability with advantage is enabled.
     * Returns TRUE if the ability is enabled, or FALSE if the ability is disabled
     * specifically because of this advantage.
     *
     * If the ability is not enabled, the ability.setEnableMessage method should
     * be used to set the message that is displayed in the popup tooltip when the
     * mouse is held over the disabled ability.
     */
    public boolean isEnabled(Target source) {
        effectInterruptible ei = findAnyGesturingEffect(source);
        
        if ( ei != null ) {
            ability.setEnableMessage( ability.getName() + " requires gestures.<P><P>"
            + "Only a single ability requiring gestures can be activated or used at the same time.<P><P>"
            + source.getName() + " is already gesturing for " + ei.getInterruptibleAbility().getName() + ".");
            return false;
        }
        return true;
    }
    
    public AttackTreeNode preactivate(final BattleEvent be){
        ExecuteRunnableNode node = new ExecuteRunnableNode( "Incantation Node", new Runnable() {
            public void run() {
                ParameterList parameterList = getParameterList();
                String level = (String)parameterList.getParameterValue("GesturesLevel");
                
                if ( level.startsWith("Throughout") ) {
                    effectInterruptible ei = null;
                    
                    ei = findGesturingEffect(ability, be.getSource());
                    if ( ei == null ) {
                        ei = new effectInterruptible( "Gesturing", ability, be, INTERRUPTIBLE_BY_ANY_DAMAGING_ATTACK, 1,0);
                        try {
                            ei.addEffect(be, be.getSource() );
                        } catch (BattleEventException bee) {
                            
                        }
                        
                    }
                }
            }});
            
            return node;
    }
    
    /** Notifies the limitation that the Ability has been shutdown.
     *
     * This is called during the Ability deactivation process.
     */
    public void shutdownAbility(BattleEvent be, Target source) throws BattleEventException {
        ParameterList parameterList = getParameterList();
        String level = (String)parameterList.getParameterValue("GesturesLevel");
        
        if ( level.startsWith("Throughout") ) {
            effectInterruptible ei = null;
            
            ei = findGesturingEffect(ability, be.getSource());
            if ( ei != null ) {
                ei.removeEffect(be, source);
            }
        }
    }
    
    
    public double calculateMultiplier() {
        ParameterList parameterList = getParameterList();
        String level = (String)parameterList.getParameterValue("GesturesLevel");
        boolean hands = (Boolean)parameterList.getParameterValue("RequiresBothHands");
        boolean complex = (Boolean)parameterList.getParameterValue("Complex");
        double total = -0.25;
        String extratime;
        
        //total = -0.25;
                
        if ( hands) {
            total = total + -0.25;
        }
        if ( complex){
            total = total + -0.25;
        }
        if ( level.equals( "Throughout" ) || (extratime = ability.getStringValue("ExtraTime.LEVEL")) != null ) {
            total = total * 2;
        }
        else total = total; {
        
        }

/*
        String extratime;
        if ( (extratime = ability.getStringValue("ExtraTime.LEVEL")) != null ) {
            total = total + -0.25;
        }
 */
        return total;
    }
    
    public String getConfigSummary() {
        ParameterList parameterList = getParameterList();
        String level = (String)parameterList.getParameterValue("GesturesLevel");
        
        StringBuffer sb = new StringBuffer();
        sb.append( "Gestures: ");
        
        sb.append( level );
        
        return sb.toString();
        
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
    public int identifyLimitation(AbilityImport ai,int line) {
        int index,count;
        String possibleLimitation;
        
        possibleLimitation = ai.getImportLine(line);
        if ( possibleLimitation != null && possibleLimitation.indexOf( "Gestures" ) != -1 ) {
            return 10;
        }
        else if ( possibleLimitation != null && possibleLimitation.indexOf( "GESTURES" ) != -1 ) {
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
    
    static public effectInterruptible findGesturingEffect(Ability ability, Target source) {
        Effect effect;
        int index = source.getEffectCount() - 1;
        for(; index >=0; index--) {
            effect = source.getEffect(index);
            if ( effect instanceof effectInterruptible ) {
                if ( ((effectInterruptible)effect).getInterruptibleAbility() == ability && effect.getName().equals("Gesturing") ) {
                    return (effectInterruptible)effect;
                }
            }
        }
        
        return null;
    }
    
    static public effectInterruptible findAnyGesturingEffect(Target source) {
        Effect effect;
        int index = source.getEffectCount() - 1;
        for(; index >=0; index--) {
            effect = source.getEffect(index);
            if ( effect instanceof effectInterruptible ) {
                if (  effect.getName().indexOf("Gesturing") != -1 ) {
                    return (effectInterruptible)effect;
                }
            }
        }
        
        return null;
    }
}