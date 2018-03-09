 /*
  * powerHandToHandAttack.java
  *
  * Created on September 24, 2000, 5:06 PM
  */

package champions.powers;

import org.junit.platform.commons.util.StringUtils;

import champions.*;
import champions.interfaces.*;
import champions.exception.*;
import champions.event.*;
import champions.attackTree.*;
import champions.parameters.ParameterList;

public class powerEB extends Power implements ChampionsConstants {
    static final long serialVersionUID =5295848483348707401L;
    
    private static Object[][] parameterArray = {
        {"DamageDie","Power.DAMAGEDIE", String.class, "1d6", "Damage Dice", DICE_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"Defense","Power.DEFENSE", String.class, "ED", "Defense", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", new String[] {"PD","ED"}},
        {"StunOnly","Power.STUNONLY", Boolean.class, new Boolean(false), "Stun Only", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED}
    };
    
    // Cost Array - See Power.getCostArray()
    static private Object[][] costArray = {
        { "DamageDie", NORMAL_DICE_COST, DYNAMIC_RECONFIG, ALL_RECONFIG, new Integer(5), new Integer(0), new Integer(0) },
    };
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        //    { "([0-9]*d6) Energy Blast: *.*", new Object[] { "DamageDie", String.class}},
        { "([0-9]*d6) *.*", new Object[] { "DamageDie", String.class}},
        { ".*: ([0-9]*d6) *.*", new Object[] { "DamageDie", String.class}},
        { "Versus: ([PE]D)", new Object[] { "Defense", String.class } },
        { "(STUN Only).*", new Object[] { "StunOnly", Boolean.class } },
        //HD
        { ".* ([0-9]*d6) \\(vs. ([PE]D)\\).*", new Object[] { "DamageDie", String.class,"Defense", String.class }},
        { ".* ([0-9]*d6).*", new Object[] { "DamageDie", String.class}},
        { "LEVELS: ([0-9]*)", new Object[] { "DamageDie", String.class}},
        
    };
    
    // Known Caveats Array
    private static String[] caveats = {
        "None known."
    };
    
    // Power Definition Variables
    private static String powerName = "Energy Blast"; // The Name of the Power
    private static String targetType = "SINGLE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "ATTACK"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = 5; // The Point cost of a single Damage Class of this power.
    private static String attackType = "RANGED"; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = "NORMAL"; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = true; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 1; // Indicates the END Multiplier for this power
    private static boolean dynamic = true;
    private static String description = "Dude, its an Energy Blast, ok?";
    
    
    
    
    /** Creates new powerHandToHandAttack */
    public powerEB()  {
    }

    public Object[][] getParameterArray() {
        return parameterArray;
    }
    
    public String getName() {
        return powerName;
    }

    public boolean configurePAD(Ability ability, ParameterList parameterList) {
        if ( ability == null ) return false;    
        setParameterList(ability,parameterList);
        

        String die = (String)parameterList.getParameterValue("DamageDie");
        String defense = (String)parameterList.getParameterValue("Defense");
        boolean stunOnly = (Boolean)parameterList.getParameterValue("StunOnly");
       
        String beamattackoverride = ability.getStringValue("Ability.BEAMATTACKOVERRIDE");
        

        String keyword = "d6";
        int index = die.indexOf(keyword);
        int count =1;
        int last =0;
        while (index >=0){
            index = die.indexOf(keyword, index+keyword.length())   ;
            if (index >=0) {
            	count++;
            	last=index;
            }
        }
        if (count >1) {
        	die =die.substring(0, last);
        }
        if ( Dice.isValid(die) == false ) {
            return false;
        }
        
        if (!die.endsWith("d6")) {
            parameterList.setParameterValue("DamageDie", die + "d6");
        }
        parameterList.copyValues(ability);
        
        ability.addPowerInfo( this, powerName, targetType, persistenceType, activationTime);
        if ( attackType != null ) {
            ability.addAttackInfo( attackType,damageType );
            ability.add("Ability.PPDC", new Double(pointsPerDC), true);
        }
        ability.setGenerateDefaultEffects(generateDefaultDamage);
        if ( endMultiplier != 1 ) ability.setENDMultiplier(endMultiplier);
        
        // Add any dice information which is necessary to use this power.
        //  ability.addDiceInfo( "DamageDie", die, "Energy Blast Damage");
        
        // Add A Damage Class Info
        ability.add("Base.DC",  new Double(ChampionsUtilities.StringToNormalDC(die)), true);
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        
        
        //remove if/ese when there is a better mechanism for spreading and beam limitation
        if (beamattackoverride != null && beamattackoverride.equals("TRUE") ) {
            ability.add("Ability.CANSPREAD", "FALSE" , true );
        }
        else {
            ability.add("Ability.CANSPREAD", "TRUE" , true );
        }
        ability.add("Ability.DOESBODY", stunOnly ? "FALSE" : "TRUE", true);
        
        // The code below fixes up the defense in cases where Based on Ego Combat Value and MD
        // defense is being used.  Normally we don't want to do this kind of fixup, but no
        // other clean solution presented itself.  Note, this is due to the fact that the advantage
        // is not always configured when the power is, so the MD value can be reset incorrectly.
        //
        // Hide/Showing the defense parameter is just icing.
        boolean usesMD = false;
        int aIndex;
        if ( (aIndex = ability.findAdvantage("Based on Ego Combat Value")) != -1) {
            Advantage a = ability.getAdvantage(aIndex);
            //ParameterList apl = (ParameterList)ability.getIndexedValue(aIndex,"Advantage","PARAMETERLIST");
            ParameterList apl = a.getParameterList();
            if ( apl != null ) {
                Object vsMD = apl.getParameterValue("vsMD");
                if ( vsMD != null && vsMD.equals("TRUE") ) {
                    usesMD = true;
                }
            }
        }
        
        if ( usesMD ) {
            parameterList.setVisible("Defense", false);
            ability.add("Power.DEFENSE", "MD", true);
        }
        else {
            parameterList.setVisible("Defense", true);
        }
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }
    

    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        String die = (String)parameterList.getParameterValue("DamageDie");
        String defense = ability.getStringValue("Power.DEFENSE");
        boolean stunOnly = (Boolean)parameterList.getParameterValue("StunOnly");
        
        String stunString = (stunOnly) ? ", stun only":"";
        
        return die + " EB (" + defense + stunString + ")";
    }
    

    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();
        
        if ( power != null && ( power.equals( "ENERGYBLAST" ) || power.equals( "Energy Blast" ) )){
            return 10;
        }
        return 0;
    }
    
        
    public Object[][] getImportPatterns() {
        return patterns;
    }
    public Object[][] getCostArray(Ability ability) {
        return costArray;
    }
    
    public String getDescription() {
        return description;
    }
    

    public boolean isDynamic() {
        return dynamic;
    }
    
    public String[] getCaveatArray() {
        return caveats;
    }
}