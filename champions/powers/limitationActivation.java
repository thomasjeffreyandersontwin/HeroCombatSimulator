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
import champions.FrameworkAbility;
import champions.LimitationAdapter;
import champions.ProfileTemplate;
import champions.Target;
import champions.attackTree.AttackTreeNode;
import champions.attackTree.LimitationActivationNode;
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
 * 13) Add patterns array and define import patterns.<P>
 * 14) Add getImportPatterns() method.<P>
 */
public class limitationActivation extends LimitationAdapter
implements ChampionsConstants {
    static final long serialVersionUID = -6870520616682472305L;
    
    private static final String[] levels =  {
        "8", "9", "10", "11",
        "12", "13","14", "15"
    };
    
    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first have is replaced by
    // "Advantage#" and only the second half is kept.
    static private Object[][] parameterArray = {
        {"ActivationRoll","Limitation#.LEVEL", String.class, "8", "Activation Roll", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", levels},
       // {"AAutoroll","Limitation#.AAUTOROLL", String.class, "FALSE", "Autoroll Activation Roll", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
       // {"MAutoroll","Limitation#.MAUTOROLL", String.class, "FALSE", "Autoroll Constant Power Activation Roll", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"CanBurnout","Limitation#.CANBURNOUT", Boolean.class, new Boolean(false) , "Ability can burn out", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"CanJam","Limitation#.CANJAM", Boolean.class, new Boolean(false) , "Ability can jam", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        
    };
    
    // Limitation Definition Variables
    public static String limitationName = "Activation Roll"; // The Name of the Limitation
    private static String limitationDescription = "Activation Roll"; // The Description of the Limitation
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        //hd
        { "Activation Roll ([0-9]*)-, (Jammed) .*", new Object[] { "ActivationRoll", String.class,"CanJam", Boolean.class} },
        //Activation Roll 10-, Jammed (-1 3/4)
//        { "Activation Roll ([0-9]*)- .*", new Object[] { "ActivationRoll", String.class} },
//
//        { "Activation.*([0-9]*)-,.*", new Object[] { "ActivationRoll", String.class} },
//        { "Activation.*\\(([0-9]*)-\\).*", new Object[] { "ActivationRoll", String.class} },
//        { "(Burnout:) ([0-9]*)-,.*",  new Object[] { "CanBurnout", Boolean.class, "ActivationRoll", String.class} },
//        { "(Burnout) \\(([0-9]*)-\\).*",  new Object[] { "CanBurnout", Boolean.class, "ActivationRoll", String.class} },

        //   { "Activation Auto Roll for instant power.*: (.*)",  new Object[] { "AAutoroll", String.class} },
      //  { "Activation Auto Roll for Constant power.*: (.*)",  new Object[] { "MAutoroll", String.class} }
        
    };
    
    /** Creates new advCombatModifier */
    public limitationActivation() {
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
        String level = (String)parameterList.getParameterValue("ActivationRoll");
      //  String aautoroll = (String)parameterList.getParameterValue("AAutoroll");
      //  String mautoroll = (String)parameterList.getParameterValue("MAutoroll");
        boolean canburnout = (Boolean)parameterList.getParameterValue("CanBurnout");
        boolean canjam = (Boolean)parameterList.getParameterValue("CanJam");
        
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
        ability.reconfigurePower();
        
        // Update the Stored Description for this Limitation
        setDescription( getConfigSummary() );
        
        // Return True to indicate success in configuringPAD
        return true;
    }
    
    
    
    public double calculateMultiplier() {
        ParameterList parameterList = getParameterList();
        String level = (String)parameterList.getParameterValue("ActivationRoll");
        boolean canburnout = (Boolean)parameterList.getParameterValue("CanBurnout");
        boolean canjam = (Boolean)parameterList.getParameterValue("CanJam");
        double total;
        
        total = 0;
        
        if (levelToInt(level) == 1) {
            if (canburnout) {
                total = 1.5;
            } else total = 2 ;
        }
        else if (levelToInt(level) >1 && levelToInt(level) <7) {
            if (canburnout ) {
                total = 2 - 0.25 * (levelToInt(level)+1 );
            } else  {
                total = 2 - 0.25 * (levelToInt(level) );
            }
        } else if (levelToInt(level) == 7 ) {
            if (canburnout ) {
                total =0.25;
            } else total = 0.5 ;
            
            
        } else if (levelToInt(level) == 8 ) {
            if (canburnout ) {
                total = 0;
            } else total = 0.25;
        }
        if (canjam ) total = total + 0.50;
        return -total;
    }
    
    
    
           /*
            
        if (canburnout.equals("TRUE") ) {
            total = 2 - 0.25 * (levelToInt(level)+1);
        }    else {
            total = 2 - 0.25 * (levelToInt(level));
        }
            
         //        if (levelToInt(level) < 2) {
//            if (canburnout.equals("FALSE") ){
//                total = total + 0.25;
//            }
//       }
        if (canjam.equals("TRUE") ) total = total + 0.50;
            
        if (levelToInt(level) < 6) {
            total = total - -.25;
        }*/
    
    public String getConfigSummary() {
        if ( ability == null ) return "Error: Null Ability";
        
        ParameterList parameterList = getParameterList();
        String level = (String)parameterList.getParameterValue("ActivationRoll");
        boolean canburnout = (Boolean)parameterList.getParameterValue("CanBurnout");
        boolean canjam = (Boolean)parameterList.getParameterValue("CanJam");
        
        String s;
        
        s = "Activation(" + level + ")";
        if (canjam ) {
            s = s + " with jam";
        }
        if (canburnout ) {
            s = s + " with burnout";
        }
        return s;
    }
    
    public static int levelToInt(String level) {
        for (int i = 0; i < levels.length; i++) {
            if ( level.equals( levels[i] ) ) return i + 1;
        }
        return 0;
    }
    
    public static int levelToRoll(String level) {
        for (int i = 0; i < levels.length; i++) {
            if ( level.equals( levels[i] ) ) return 8 + i;
        }
        return 0;
    }
    
    public AttackTreeNode preactivate(BattleEvent be) {
        if ( ability instanceof FrameworkAbility ) return null;
        
        ParameterList parameterList = getParameterList();
        String level = (String)parameterList.getParameterValue("ActivationRoll");
        
        LimitationActivationNode node = new LimitationActivationNode("Activation Roll");

        node.setAbility(ability);
        node.setLimitationIndex( ability.findExactLimitation(this) );
        
        return node;
    }
    
    
   /* public AttackTreeNode preactivate(BattleEvent be) {
        Target source = be.getSource();
        
        
        ActivationInfo ai = be.getActivationInfo();
        ParameterList parameterList = getParameterList();
        String level = (String)parameterList.getParameterValue("ActivationRoll");
        String aautoroll = (String)parameterList.getParameterValue("AAutoroll");
        String mautoroll = (String)parameterList.getParameterValue("MAutoroll");
        String canburnout = (String)parameterList.getParameterValue("CanBurnout");
        String canjam = (String)parameterList.getParameterValue("CanJam");
        
        int index2,count;
        Effect effectcheck;
        count = source.getIndexedSize("Effect");
        
        if (ai != null) {
            Dice roll = null;
            
            if ( ! ai.isActivated() ) {
                // Constant power maintenence roll
                
                // I had to add both aa and mm to this so instant powers & the first activation
                //of a constant power could autoroll--pete
                if ( aautoroll.equals("TRUE") || mautoroll.equals("TRUE")) {
                    roll = new Dice(3);
                }
                else {
                    roll = DiceDialog.showDiceDialog( source.getName() + "'s " + ability.getName() + " requires an activation roll.", "3d6", true, false);
                }
            }
            else {
                // Starting power roll
                
                //I changed this to ma so that constant powers that are already activated can autoroll
                if ( mautoroll.equals("TRUE") ) {
                    roll = new Dice(3);
                }
                else {
                    roll = DiceDialog.showDiceDialog( source.getName() + "'s " + ability.getName() + " requires an activation roll"
                    + " to maintain ability this phase.", "3d6", true, false);
                }
            }
            
            if ( roll == null ) throw new BattleEventException("Activation of ability " + ability.getName() + " cancelled.");
            if ( roll.getStun().intValue() <= levelToRoll( level ) ) {
                be.addBattleMessage( new champions.battleMessage.SimpleBattleMessage(source.getName() + "'s activation roll for " + ability.getName() + " was successful.  Needed " + level + ".  Got " + roll.getStun().toString() + ".", BattleEvent.MSG_HIT)); // .addBattleMessage( new champions.battleMessage.SimpleBattleMessage(source.getName() + "'s activation roll for " + ability.getName() + " was successful.  Needed " + level + ".  Got " + roll.getStun().toString() + ".", BattleEvent.MSG_HIT)); // .addMessage(source.getName() + "'s activation roll for " + ability.getName() + " was successful.  Needed " + level + ".  Got " + roll.getStun().toString() + ".", BattleEvent.MSG_HIT);
                return true;
            }
            else {
                
                //be.addBattleMessage( new champions.battleMessage.SimpleBattleMessage(source.getName() + "'s activation roll for " + ability.getName() + " was unsuccessful.  Needed " + level + ".  Got " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS)); // .addBattleMessage( new champions.battleMessage.SimpleBattleMessage(source.getName() + "'s activation roll for " + ability.getName() + " was unsuccessful.  Needed " + level + ".  Got " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS)); // .addMessage(source.getName() + "'s activation roll for " + ability.getName() + " was unsuccessful.  Needed " + level + ".  Got " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS);
                
                if (canjam.equals("TRUE") ) {
                    Effect effect =  new effectJammed(ability);
                    
                    // Add the Effect to the Target
                    effect.addEffect(be, source);
                    
                    be.addBattleMessage( new champions.battleMessage.SimpleBattleMessage("ABILITY JAMMED!! " + source.getName() + "'s activation roll for " + ability.getName() + " was unsuccessful.  Needed " + level + ".  Got " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS)); // .addBattleMessage( new champions.battleMessage.SimpleBattleMessage("ABILITY JAMMED!! " + source.getName() + "'s activation roll for " + ability.getName() + " was unsuccessful.  Needed " + level + ".  Got " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS)); // .addMessage("ABILITY JAMMED!! " + source.getName() + "'s activation roll for " + ability.getName() + " was unsuccessful.  Needed " + level + ".  Got " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS);
                } else
                    if (canburnout.equals("TRUE") ) {
                        Effect effect =  new effectBurnedout(ability);
                        
                        // Add the Effect to the Target
                        effect.addEffect(be, source);
                        
                        be.addBattleMessage( new champions.battleMessage.SimpleBattleMessage("ABILITY BURNED OUT WITH THIS USE!! " + source.getName() + "'s activation roll for " + ability.getName() + " was unsuccessful.  Needed " + level + ".  Got " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS)); // .addBattleMessage( new champions.battleMessage.SimpleBattleMessage("ABILITY BURNED OUT WITH THIS USE!! " + source.getName() + "'s activation roll for " + ability.getName() + " was unsuccessful.  Needed " + level + ".  Got " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS)); // .addMessage("ABILITY BURNED OUT WITH THIS USE!! " + source.getName() + "'s activation roll for " + ability.getName() + " was unsuccessful.  Needed " + level + ".  Got " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS);
                        return true;
                    }
                
                    else {
                        be.addBattleMessage( new champions.battleMessage.SimpleBattleMessage(source.getName() + "'s activation roll for " + ability.getName() + " was unsuccessful.  Needed " + level + ".  Got " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS)); // .addBattleMessage( new champions.battleMessage.SimpleBattleMessage(source.getName() + "'s activation roll for " + ability.getName() + " was unsuccessful.  Needed " + level + ".  Got " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS)); // .addMessage(source.getName() + "'s activation roll for " + ability.getName() + " was unsuccessful.  Needed " + level + ".  Got " + roll.getStun().toString() + ".", BattleEvent.MSG_MISS);
                    }
            }
        }
        return false;
    } */
    
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
    
    
    public boolean isEnabled(Target source) {
        
        if ( ability instanceof FrameworkAbility ) return true;
        
        int index2,count;
        Effect effectcheck;
        count = source.getEffectCount();
        
        for(index2=0;index2<count;index2++) {
            effectcheck = (Effect)source.getEffect(index2);
            
            if ( effectcheck instanceof effectJammed ) {
                
                if ( ((effectJammed)effectcheck).getName().equals( ability.getName() + " Jammed" )   ){
                    ability.setEnableMessage(ability.getName() + " is currently jammed.  It must be unjammed before it can be used.");
                    return false;
                }
            }
            
            if ( effectcheck instanceof effectBurnedout ) {
                
                if ( ((effectBurnedout)effectcheck).getName().equals( ability.getName() + " Burned out" )   ){
                    ability.setEnableMessage(ability.getName() + " is currently burned out.  It will not be usable for the rest of the battle.");
                    return false;
                }
            }
        }
        return true;
    }
    
    /** Initializes the Limitation when it is first loaded by the Simulator.
     *
     * The <code>initialize</code> method is gauranteed to be called at least once prior to  
     * any use of the Limitation.  However, it is possible that the method would be called
     * multiple times.  If the initialization code can only be run one, the limitation should
     * track whether it has been initialized already.
     */
    public void initialize() {
        ProfileTemplate pt = ProfileTemplate.getDefaultProfileTemplate();
        
        pt.addOption( "Initial Activation Roll", "SHOW_INITIAL_ACTIVATION_ROLL_PANEL", 
        "This Description is located in the limitationActivation.initialize() method.", 
        "AttackTree.toHitIcon", null);
        
        pt.addOption( "Continuing Activation Roll", "SHOW_CONTINUING_ACTIVATION_ROLL_PANEL", 
        "This Description is located in the limitationActivation.initialize() method.", 
        "AttackTree.toHitIcon", null);
    }
    
    
    
    
    
    
    public int identifyLimitation(AbilityImport ai,int line) {
        int index,count;
        String possibleLimitation;
        
        possibleLimitation = ai.getImportLine(line);
        if ( possibleLimitation != null && possibleLimitation.indexOf( "Burnout" ) != -1 ) {
            return 10;
        }
        else if ( possibleLimitation != null && possibleLimitation.indexOf( "Jammed" ) != -1 ) {
            return 10;
        }
        else if ( possibleLimitation != null && possibleLimitation.indexOf( "Activation" ) != -1 ) {
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
}