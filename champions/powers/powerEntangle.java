/*
 * powerHandToHandAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
 */

package champions.powers;

import champions.Ability;
import champions.AbilityImport;
import champions.BattleEvent;
import champions.ChampionsUtilities;
import champions.DetailList;
import champions.Dice;
import champions.Effect;
import champions.Power;
import champions.Target;
import champions.exception.BattleEventException;
import champions.interfaces.Advantage;
import champions.interfaces.ChampionsConstants;
import tjava.Filter;
import champions.interfaces.Limitation;
import champions.parameters.ParameterList;




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
 * 11) Edit getName method to return powerName variable.
 * 12) Change serialVersionUID by some amount.
 * 13) Add patterns array and define import patterns.<P>
 * 14) Add getImportPatterns() method.<P>
 *
 * The Following Steps must be performed to upgrade Power to Reconfigurable Format:
 * 1) Create costArray.
 * 2) Add the getCostArray() method, returning costArray.
 * 3) Remove existing calculateCPCost.
 */
public class powerEntangle extends Power implements ChampionsConstants {
    static final long serialVersionUID =5295848683348707203L;
    
    static private Object[][] parameterArray = {
        {"EntangleDie","Power.ENTANGLEDIE", String.class, "1d6", "Entangle Dice", DICE_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED}
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
        String die = (String)parameterList.getParameterValue("EntangleDie");
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        if ( Dice.isValid(die) == false ) {
            return false;
        }
        
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
        ability.addDiceInfo( "EntangleDie", die, "Entangle Strength");
        
        // Add A Damage Class Info
        ability.add("Base.DC",  new Double(ChampionsUtilities.StringToNormalDC(die)), true);
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        if (!die.endsWith("d6")) {
            parameterList.setParameterValue("EntangleDie", die + "d6");
        }
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
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
     */
    
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
    
    /** Returns whether power can be dynamcially reconfigured.
     */
    public boolean isDynamic() {
        return dynamic;
    }

    /** Returns an Array of Objects, representing custom/special advantages, limitations, special parameters usable with the power.
     * The Array should be in the format of class type (limitation, advantage, special) followed by the class name, repeated for
     * each additional custom added.  For example:
     *  array[] = { Limitation.class, "limitationLimitedSpecialFX", Advantage.class, "advantageVariableEffect" };
     */
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
    


    
    /**
     * Returns a String[] of Caveats about the Power
     * Power uses this method to automatically build the getCaveats()
     * String.  The Strings returns by getCaveatArray() will be assembled into
     * list form and returned via getCaveats().
     * 
     * Return an empty array if there are no known caveats for this power.
     */
    public String[] getCaveatArray() {
        return caveats;
    }
    
//  Everything below here was put into seperate files: effectEntangle and targetEntangle
    
//    public class effectEntangle extends Effect {
//        
//        public effectEntangle( Ability ability, Dice dice ) {
//            super( ability.getName(), "PERSISTENT" );
//            
//            int def;
//            int body;
//            
//            // String die = (String)ability.ge(parameterArray, "EntangleDie");
//            String die = ability.getStringValue("Power.ENTANGLEDIE");
//            
//            // Figure out the defenses
//            try {
//                Dice d = new Dice( die );
//                def = d.getD6();
//            } catch (BadDiceException bde) {
//                def = 0;
//            }
//            
//            body = dice.getBody().intValue();
//            
//            powerEntangle.TargetEntangle targetEntangle = new powerEntangle.TargetEntangle( this, body, def );
//            add("Effect.TARGETENTANGLE",  targetEntangle,  true);
//        }
//        
//        public boolean addEffect(BattleEvent be, Target target) throws BattleEventException {
//            if ( ! super.addEffect(be,target) ) return false;
//            
//            Object o = getValue("Effect.TARGETENTANGLE");
//            if ( o == null ) return false;
//            
//            powerEntangle.TargetEntangle targetEntangle = (powerEntangle.TargetEntangle)o;
//            targetEntangle.setEntangleTarget(target);
//            
//            HashSet rosters = Battle.currentBattle.getRosters();
//            
//            Iterator i = rosters.iterator();
//            while ( i.hasNext() ) {
//                Roster r = (Roster)i.next();
//                Vector targets = r.getCombatants();
//                
//                if ( targets.contains( target ) ) {
//                    // Found the correct roster...
//                    r.add( (Target)targetEntangle, false );
//                    be.addRosterEvent(r,(Target)targetEntangle,true);
//                    
//                    targetEntangle.setEntangleRoster(r);
//                    
//                    break;
//                }
//            }
//            
//            be.addBattleMessage( new champions.battleMessage.SimpleBattleMessage( target.getName() + " has been entangled!", BattleEvent.MSG_ABILITY )); // .addBattleMessage( new champions.battleMessage.SimpleBattleMessage( target.getName() + " has been entangled!", BattleEvent.MSG_ABILITY )); // .addMessage( target.getName() + " has been entangled!", BattleEvent.MSG_ABILITY );
//            
//            Undoable u = target.addObstruction(targetEntangle);
//            if ( u != null ) be.addUndoableEvent(u);
//            
//            return true;
//        }
//        
//        public void removeEffect(BattleEvent be, Target target) throws BattleEventException {
//            super.removeEffect(be,target);
//            
//            Undoable u = target.removeObstruction(getTargetEntangle());
//            if ( u != null ) be.addUndoableEvent(u);
//        }
//        
//        public String getDescription() {
//            Object target = getValue("Effect.TARGET");
//            Object targetEntangle = getValue("Effect.TARGETENTANGLE");
//            if ( target == null|| targetEntangle == null ) return "Effect misconfigured.";
//            
//            String desc = ((Target)target).getName() + " is entangled by " + this.getName() + ".\n";
//            
//            
//            int body = ((Target)targetEntangle).getCurrentStat("BODY");
//            int pd = ((Target)targetEntangle).getCurrentStat("PD");
//            int ed = ((Target)targetEntangle).getCurrentStat("ED");
//            
//            desc = desc  + "Entange Stats:\n" + " Body: " + Integer.toString(body) + "\n";
//            desc = desc  + " PD: " + Integer.toString(pd) + "\n";
//            desc = desc   + " ED: " + Integer.toString(ed) + "\n";
//            
//            return desc;
//            
//        }
//        
//        public void addDCVDefenseModifiers(CVList cvList, Ability attack) {
//            cvList.addTargetCVSet( "Entangled", 0 );
//        }
//        
//        public TargetEntangle getTargetEntangle() {
//            return (TargetEntangle)getValue("Effect.TARGETENTANGLE");
//        }
//    }
    
//    public class TargetEntangle extends Target {
//        
//        public TargetEntangle(Effect effect, int body, int def) {
//            super();
//            setEntangleEffect(effect);
//            
//            createCharacteristic("BODY");
//            setBaseStat("BODY",body);
//            setCurrentStat("BODY", body);
//            
//            createCharacteristic("PD");
//            setBaseStat("PD", def);
//            setCurrentStat("PD", def);
//            
//            createCharacteristic("rPD");
//            setBaseStat("rPD", def);
//            setCurrentStat("rPD", def);
//            
//            createCharacteristic("ED");
//            setBaseStat("ED", def);
//            setCurrentStat("ED", def);
//            
//            createCharacteristic("rED");
//            setBaseStat("rED", def);
//            setCurrentStat("rED", def);
//            
//            add("Target.ISALIVE",  "FALSE",  true);
//            add("Target.CANBEKNOCKEDBACK",  "FALSE",  true);
//            add("Target.USESHITLOCATION",  "FALSE",  true);
//            add("Target.HASSENSES",  "FALSE",  true);
//            add("Target.ISOBSTRUCTION", "TRUE", true);
//            
//            add("Target.HASDEFENSES",  "TRUE",  true);
//        }
//        
//        public int getMinimumStat(String stat) {
//            int minimum;
//            
//            if ( stat.equals("BODY") || stat.equals("STUN") ) {
//                minimum = 0;
//            } else {
//                minimum = super.getMinimumStat(stat);
//            }
//            
//            return minimum;
//        }
//        
//        public void setEntangleTarget(Target t) {
//            add("Entangle.TARGET",  t, true );
//            setName( t.getName() + " Entangle" );
//        }
//        
//        public void setEntangleEffect(Effect e) {
//            add("Entangle.EFFECT",  e, true );
//        }
//        
//        public void setEntangleRoster(Roster e) {
//            add("Entangle.ROSTER",  e, true );
//        }
//        
//        public void posteffect(BattleEvent be, Effect newEffect) throws BattleEventException {
//            Ability ability = be.getAbility();
//            if ( getCurrentStat("BODY") <= 0 ) {
//                int bodyEffect = newEffect.findIndexed("Subeffect","VERSUS","BODY");
//                
//                if ( bodyEffect == -1 ) return;
//                // Double value = newEffect.getIndexedDoubleValue( bodyEffect, "Subeffect", "VALUE" );
//                double value = newEffect.getSubeffectAdjustedAmount(bodyEffect);
//                int startBody = getCurrentStat("BODY") + (int)value;
//                if ( value > startBody * 2 ) {
//                    be.addBattleMessage( new champions.battleMessage.SimpleBattleMessage( "Damage to " + this.getName() + " did twice remaining body.  No action used.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.SimpleBattleMessage( "Damage to " + this.getName() + " did twice remaining body.  No action used.", BattleEvent.MSG_NOTICE)); // .addMessage( "Damage to " + this.getName() + " did twice remaining body.  No action used.", BattleEvent.MSG_NOTICE);
//                    be.add("Ability.TEMPTIME", "INSTANT");
//                } else if ( value > startBody  ) {
//                    be.addBattleMessage( new champions.battleMessage.SimpleBattleMessage( "Damage to " + this.getName() + " did remaining body.  Half action used.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.SimpleBattleMessage( "Damage to " + this.getName() + " did remaining body.  Half action used.", BattleEvent.MSG_NOTICE)); // .addMessage( "Damage to " + this.getName() + " did remaining body.  Half action used.", BattleEvent.MSG_NOTICE);
//                    be.add("Ability.TEMPTIME", "HALFMOVE");
//                }
//                
//                triggerRemove(be);
//            }
//        }
//        
//        public void triggerRemove(BattleEvent be) throws BattleEventException {
//            Object target = getValue("Entangle.TARGET");
//            Object effect = getValue("Entangle.EFFECT");
//            Object roster = getValue("Entangle.ROSTER");
//            
//            if ( roster != null ) {
//                ((Roster)roster).removeTarget(be, this);
//                //be.addRosterEvent((Roster)roster,this,false);
//            }
//            
//            if ( effect == null || target == null ) return;
//            
//            ((Effect)effect).removeEffect(be, (Target)target);
//            be.addBattleMessage( new champions.battleMessage.SimpleBattleMessage( ((Target)target).getName() + " is no longer entangled.", BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.SimpleBattleMessage( ((Target)target).getName() + " is no longer entangled.", BattleEvent.MSG_NOTICE )); // .addMessage( ((Target)target).getName() + " is no longer entangled.", BattleEvent.MSG_NOTICE );
//            //if ( Battle.currentBattle != null ) Battle.currentBattle.addCompletedEvent(be);
//        }
//    }
//    


}