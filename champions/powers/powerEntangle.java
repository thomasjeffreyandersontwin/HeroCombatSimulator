/*
 * powerHandToHandAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
 */

package champions.powers;

import java.util.List;

import champions.Ability;
import champions.AbilityImport;
import champions.BattleEvent;
import champions.ChampionsUtilities;
import champions.DefaultAbilityList;
import champions.DetailList;
import champions.Dice;
import champions.Effect;
import champions.PADRoster;
import champions.Power;
import champions.Sense;
import champions.SpecialEffect;
import champions.Target;
import champions.exception.BattleEventException;
import champions.interfaces.Advantage;
import champions.interfaces.ChampionsConstants;
import tjava.Filter;
import champions.interfaces.Limitation;
import champions.parameters.ParameterList;


public class powerEntangle extends Power implements ChampionsConstants {
    static final long serialVersionUID =5295848683348707203L;
    
    static private Object[][] parameterArray = {
        {"EntangleDie","Power.ENTANGLEDIE", String.class, "1d6", "Entangle Dice", DICE_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"Senses","Sense*.SENSE", Sense.class, null, "Senses", LIST_PARAMETER, VISIBLE, ENABLED, REQUIRED}
    };

    // Cost Array - See Power.getCostArray()
    static private Object[][] costArray = {
        { "EntangleDie", NORMAL_DICE_COST, DYNAMIC_RECONFIG, ALL_RECONFIG, new Integer(10), new Integer(0), new Integer(0) }
    };
    
    // Custom Adders
    private static Object[] customAdders = {
        Advantage.class, advantageTakesNoDamageFromAttacks.advantageName,
        Advantage.class, advantageWorksAgainstEgoNotStr.advantageName,
        Limitation.class, limitationCannotFormBarriers.limitationName
    };
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "([0-9]*d6) .*", new Object[] { "EntangleDie", String.class}},
        { ".*: ([0-9]*d6) .*", new Object[] { "EntangleDie", String.class}},
        { "LEVELS: ([0-9]*)", new Object[] { "EntangleDie", String.class}},
        { "Entangle Backlash: .*", null },
        { "Entangle Damage: .*", null },
        { "Entangle Stops: .*", null },
        { "Entangle with 1 BODY: .*", null },
        { "Entangle with No DEF: .*", null },
        { "Stops Sense: .*", null },
        { "Stops Sense Group: .*", null },
                //hd
        { ".* ([0-9]*d6).*", new Object[] { "EntangleDie", String.class}},
    };
    
    // Known Caveats Array
    private static String[] caveats = {
        "None known."
    };
    
    // Power Definition Variables
    private static String powerName = "Entangle"; // The Name of the Power
    private static String targetType = "SINGLE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "ATTACK"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = 10; // The Point cost of a single Damage Class of this power.
    private static String attackType = "RANGED"; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = "SPECIAL"; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 1; // Indicates the END Multiplier for this power
    private static boolean dynamic = true;
    private static String description = "Dude, its a Entangle, ok?";
    
    /** Creates new powerHandToHandAttack */
    public powerEntangle()  {
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
    

    public boolean configurePAD(Ability ability, ParameterList parameterList) {
        // Fail immediately if ability is null
        if ( ability == null ) return false;

        setParameterList(ability,parameterList);
        
        String die = (String)parameterList.getParameterValue("EntangleDie");
        
        if ( Dice.isValid(die) == false ) {
            return false;
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
        ability.addDiceInfo( "EntangleDie", die, "Entangle Strength");
        
        // Add A Damage Class Info
        ability.add("Base.DC",  new Double(ChampionsUtilities.StringToNormalDC(die)), true);
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        if (!die.endsWith("d6")) {
            parameterList.setParameterValue("EntangleDie", die + "d6");
        }
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        return true;
    }
    
    public void triggerPower(BattleEvent be, Ability ability,DetailList effectList, Target target, int refNumber, String targetGroup) throws BattleEventException {
        
        Dice dice;
        
        //  int dindex = be.findIndexed("Die","NAME","EntangleDie");
        int dindex = be.getDiceIndex( "EntangleDie", targetGroup );
        
        if ( dindex != -1 ) {
            //dice = be.getIndexedDiceValue(dindex,"Die","DIEROLL");
            dice = be.getDiceRoll(dindex);
            Effect effect = new effectEntangle( ability, dice );
            effectList.createIndexed(  "Effect","EFFECT",effect) ;
            
            effectEntangle effectEnt = (effectEntangle)effect;
        	TargetEntangle targetEntangle= effectEnt.getTargetEntangle();
        	
        	targetEntangle.AddApropriateModifersFromParentAbility(ability, be);
        	
        	effectEnt.Target = target;
        	effectEnt.preventTargetsSensesFromWorkingIfEntangleBLocksSenses();
        	
        }
    }
    
    
    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        String die = (String)parameterList.getParameterValue("EntangleDie");
        
        return die + " Entangle";
    }
    
    /** Attempt to identify power
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     */
    public int identifyPower(Ability template, AbilityImport ai) {
        String power = ai.getPowerName();
        
        if ( power != null && ( power.equals( "ENTANGLE" ) || power.equals( "Entangle" ) )){
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
    
    /** Returns a Description of the Power
     */
    public String getDescription() {
        return description;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public Object[] getCustomAddersArray() {
        return customAdders;
    }

    public Filter<Target> getTargetFilter(Ability ability) {
        return new Filter<Target>() {
            public boolean includeElement(Target filterObject) {
                return filterObject instanceof TargetEntangle == false;
            }
            
        };
    }
    

    public String[] getCaveatArray() {
        return caveats;
    }
    
   


}