/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;

import champions.*;
import champions.interfaces.*;
import champions.exception.*;
import champions.attackTree.*;
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
public class limitationConcentration extends LimitationAdapter implements Limitation, ChampionsConstants {
    static final long serialVersionUID = -6870510616782472306L;
    
    static private String[] levelOptions = {
        "During Activation Only, \u00BD DCV",
        "During Activation Only, 0 DCV",
        "Throughout, \u00BD DCV",
        "Throughout, 0 DCV"
    };
    
    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first have is replaced by
    // "Advantage#" and only the second half is kept.
    static private Object[][] parameterArray = {
        {"ConcentrationLevel","Concentration.LEVEL", String.class, "During Activation Only, \u00BD DCV", "Concentration DCV Modifier", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", levelOptions},
        {"CharacterTotallyUnaware","Concentration.CHARACTERTOTALLYUNAWARE", Boolean.class, new Boolean(false), "Character is totally unaware", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        //Added to support HD xml save file import (04/09/04 9:37 AM) -kjr
        {"ContinuousConcentration","Concentration.CONTINUOUSCONCENTRATION", Boolean.class, new Boolean(false), "Character must Concentrate throughout use of Constant Power", BOOLEAN_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED},
    };
    
    // Limitation Definition Variables
    public static String limitationName = "Concentration"; // The Name of the Limitation
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "Concentrate: (.*),.*", new Object[] { "ConcentrationLevel", String.class}},
        { "Concentration \\((.*)\\):,*", new Object[] { "ConcentrationLevel", String.class}},
        //HD
        { "Concentration (.*, Must Concentrate throughout use of Constant Power).*(Character is totally unaware of nearby events).*", new Object[] { "ConcentrationLevel", String.class, "CharacterTotallyUnaware", Boolean.class}},
        { "Concentration (.* DCV).*", new Object[] { "ConcentrationLevel", String.class}},
    };
    
    
    
    /** Creates new advCombatModifier */
    public limitationConcentration() {
    }
    
    public String getName() {
        return "Concentration";
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
        String level = (String)parameterList.getParameterValue("ConcentrationLevel");
        boolean continuous = (Boolean)parameterList.getParameterValue("ContinuousConcentration");
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        // !!!Limitation has nothing to validate!!!
        
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
        
        // Convert MC to HCS notation...
        if ( level.equals( "\u00BD DCV" ) ) {
            level = "During Activation Only, \u00BD DCV";
            parameterList.setParameterValue("ConcentrationLevel", level );
        }
        if ( level.equals( "0 DCV" ) ) {
            level = "During Activation Only, 0 DCV";
            parameterList.setParameterValue("ConcentrationLevel", level );
        } else if ( level.equals("Throughout & \u00BD DCV") ) {
            level = "Throughout, \u00BD DCV";
            parameterList.setParameterValue("ConcentrationLevel", level );
        } else if ( level.equals("Throughout & 0 DCV") ) {
            level = "Throughout, 0 DCV";
            parameterList.setParameterValue("ConcentrationLevel", level );
            
            
            
        }
        
        
        //convert HD to HCS notation
        //Concentration 0 DCV, Must Concentrate throughout use of Constant Power (-1 1/4), Character is totally unaware of nearby events (-1/4)
        if ( level.equals( "1/2 DCV" ) ) {
            level = "During Activation Only, \u00BD DCV";
            parameterList.setParameterValue("ConcentrationLevel", level );
        }
        //else if ( level.equals( "HALF" ) ) {
        //level = "During Activation Only, \u00BD DCV";
        //parameterList.setParameterValue("ConcentrationLevel", level );
        //}
        if ( level.equals( "0 DCV" ) ) {
            level = "During Activation Only, 0 DCV";
            parameterList.setParameterValue("ConcentrationLevel", level );
        }
        //else if ( level.equals( "ZERO" ) ) {
        //level = "During Activation Only, 0 DCV";
        //parameterList.setParameterValue("ConcentrationLevel", level );
        //}
        else if ( level.equals( "0 DCV, Must Concentrate throughout use of Constant Power" ) ) {
            level = "Throughout, 0 DCV";
            parameterList.setParameterValue("ConcentrationLevel", level );
        } else if ( level.equals( "1/2 DCV, Must Concentrate throughout use of Constant Power" ) ) {
            level = "Throughout, \u00BD DCV";
            parameterList.setParameterValue("ConcentrationLevel", level );
        }
        
        if ( level.equals( "HALF" )  ){
            if ( continuous) {
                level = "Throughout, \u00BD DCV";
                parameterList.setParameterValue("ConcentrationLevel", level );
            } else {
                level = "During Activation Only, \u00BD DCV";
                parameterList.setParameterValue("ConcentrationLevel", level );
            }
        } else if ( level.equals( "ZERO" ) ) {
            if ( continuous ) {
                level = "Throughout, 0 DCV";
                parameterList.setParameterValue("ConcentrationLevel", level );
            } else {
                level = "During Activation Only, 0 DCV";
                parameterList.setParameterValue("ConcentrationLevel", level );
            }
        }
        
        
        double dcvMultiplier;
        dcvMultiplier = (level.indexOf( "\u00BD DCV" ) != -1) ? 0.5 : 0;
        
        // Add Ability Delay information
        ability.addDelayInfo("Concentrating", INTERRUPTIBLE_BY_ANY_DAMAGING_ATTACK, dcvMultiplier, false, TIME_ONE_PHASE, 0, false);
        
        // Update the Stored Description for this Limitation
        setDescription( getConfigSummary() );
        
        ability.setNormallyOn(false);
        // Return True to indicate success in configuringPAD
        return true;
    }
    
    
    public double calculateMultiplier() {
        ParameterList parameterList = getParameterList();
        String level = (String)parameterList.getParameterValue("ConcentrationLevel");
        boolean  unaware = (Boolean)parameterList.getParameterValue("CharacterTotallyUnaware");
        double total;
        
        total = 0;
        if ( level.equals( "During Activation Only, \u00BD DCV" ) || level.equals( "Throughout, \u00BD DCV" )) {
            total = -0.25;
        } else if ( level.equals( "During Activation Only, 0 DCV" ) || level.equals( "Throughout, 0 DCV" )) {
            total = -0.5;
        }
        if ( unaware ) {
            total = total + -0.25;
        }
        if ( level.equals( "Throughout, \u00BD DCV" ) || level.equals( "Throughout, 0 DCV" ) ) {
            total = total*2;
        }
        
        return total;
    }
    
    public String getConfigSummary() {
        ParameterList parameterList = getParameterList();
        String level = (String)parameterList.getParameterValue("ConcentrationLevel");
        
        StringBuffer sb = new StringBuffer();
        sb.append( "Concentration: ");
        sb.append( level );
        
        return sb.toString();
        
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
        effectInterruptible ei = findAnyConcentratingEffect(source);
        
        if ( ei != null && ei.getInterruptibleAbility() != getAbility() ) {
            ability.setEnableMessage( ability.getName() + " requires concentration.<P><P>"
                    + "Only a single ability requiring concentration can be activated or used at the same time.<P><P>"
                    + source.getName() + " is already concentrating on " + ei.getInterruptibleAbility().getName() + ".");
            return false;
        }
        return true;
    }
    
    public AttackTreeNode preactivate(final BattleEvent be) {
        ExecuteRunnableNode node = new ExecuteRunnableNode( "Incantation Node", new Runnable() {
            public void run() {
                ParameterList parameterList = getParameterList();
                String level = (String)parameterList.getParameterValue("ConcentrationLevel");
                
                if ( level.startsWith("Throughout" ) ) {
                    effectInterruptible ei = null;

                    ei = findConcentratingEffect(ability, be.getSource());
                    if ( ei == null ) {
                        double dcvMultiplier = (level.indexOf( "\u00BD DCV" ) != -1) ? 0.5 : 0;

                        ei = new effectInterruptible( "Concentrating", ability, be, INTERRUPTIBLE_BY_ANY_DAMAGING_ATTACK, dcvMultiplier,0);
                        try {
                            ei.addEffect(be, be.getSource() );
                        } catch ( BattleEventException bee ) {

                        }
                    }
                }
            }
        });
        
        return node;
    }
    
//    public void postpower(BattleEvent be, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget) throws BattleEventException {
//        
//        ParameterList pl = getParameterList();
//        String level = (String)pl.getParamaterValue("ConcentrationLevel");
//        
//        if ( level.startsWith("During Activation Only") ) {
//            effectInterruptible ei = findConcentratingEffect(ability, be.getSource());
//            if ( ei != null ) {
//                ei.removeEffect(be, source);
//            }
//        }
//    }
    
    
    
    
    
    /** Notifies the limitation that the Ability has been shutdown.
     *
     * This is called during the Ability deactivation process.
     */
    public void shutdownAbility(BattleEvent be, Target source) throws BattleEventException {
        
        effectInterruptible ei = null;
        
        ei = findConcentratingEffect(ability, be.getSource());
        if ( ei != null ) {
            ei.removeEffect(be, source);
        }
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
        if ( possibleLimitation != null && possibleLimitation.indexOf( "Concentrate:.*" ) != -1 ) {
            return 10;
        } else if ( possibleLimitation != null && possibleLimitation.indexOf( "Concentration" ) != -1 ) {
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
    
    static public effectInterruptible findConcentratingEffect(Ability ability, Target source) {
        Effect effect;
        int index = source.getEffectCount() - 1;
        for(; index >=0; index--) {
            effect = source.getEffect(index);
            if ( effect instanceof effectInterruptible ) {
                if ( ((effectInterruptible)effect).getInterruptibleAbility() == ability && effect.getName().equals("Concentrating") ) {
                    return (effectInterruptible)effect;
                }
            }
        }
        
        return null;
    }
    
    static public effectInterruptible findAnyConcentratingEffect(Target source) {
        Effect effect;
        int index = source.getEffectCount() - 1;
        for(; index >=0; index--) {
            effect = source.getEffect(index);
            if ( effect instanceof effectInterruptible ) {
                if (  effect.getName().indexOf("Concentrating") != -1 ) {
                    return (effectInterruptible)effect;
                }
            }
        }
        
        return null;
    }
}