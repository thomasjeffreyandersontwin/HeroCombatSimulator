/*
 * powerHandToHandAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
 */

package champions.powers;

import champions.*;
import champions.interfaces.*;
import champions.event.*;
import champions.exception.*;

import champions.attackTree.*;
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
 */
public class powerRecovery extends Power
implements ChampionsConstants {
    static final long serialVersionUID = 2367915068243652953L;
    
    static private Object[][] parameterArray = {
    };
    
    // Power Definition Variables
    private static String powerName = "Recovery"; // The Name of the Power
    private static String targetType = "QUICKSELF"; // The Type of Attack: Either "SINGLE" or "SELF" or "QUICKSELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "FULLMOVE"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = -1; // The Point cost of a single Damage Class of this power.
    private static String attackType = null; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = null; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 0; // Indicates the END Multiplier for this power
    
    /** Creates new powerHandToHandAttack */
    public powerRecovery()  {
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
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
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
        //ability.addDiceInfo( "DamageDie", die, "Hand-to-Hand Killing Attack Damage");
        
        
        // Add A Damage Class Info
        //ability.add("Base.DC",  new Double(ChampionsUtilities.StringToNormalDC(die)), true);
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        ability.add("Ability.ISRECOVERY", "TRUE", true, false);
        ability.setAutoSource(true);
        
        ability.addDelayInfo("Recovering", INTERRUPTIBLE_BY_DAMAGE, 0.5, true, TIME_ONE_PHASE, 1, false);
        ability.setDisallowForcedActivation(true);
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription("Recovery");
        
        // Return true to indicate success
        return true;
    }
    
    public void triggerPower(BattleEvent be, Ability ability,DetailList effectList, Target target, int refNumber, String targetGroup) throws BattleEventException{
        
        Effect effect = new Effect( "Recovery", "INSTANT" );
        
        int index;
        
        // Add Effects
        //effect.addSubeffectInfo( "RecStun", "LIMITED_AID", "NONE", "NONE", "STAT", "STUN", new Integer(source.getCurrentStat("REC")));
        effect.addRecoverySubeffect( "RecStun", "STUN", target.getCurrentStat("REC"));
        
        
        if ( target.isUnconscious() == false) {
            //effect.addSubeffectInfo( "RecEnd" , "LIMITED_AID", "NONE", "NONE", "STAT", "END", new Integer(source.getCurrentStat("REC")));
            effect.addRecoverySubeffect( "RecEnd", "END", target.getCurrentStat("REC"));
        }
        
        effectList.createIndexed( "Effect","EFFECT", effect) ;
    }
    
    public AttackTreeNode predelay(final BattleEvent be) {
        final Target source = be.getSource();
        
        GenericAttackTreeNode node = null;
        
        if ( source.isPostTurn() == false ) {
            // Deactivate all END using powers
            node = new GenericAttackTreeNode("Recovery Predelay Node");
            
            node.addChild( new ExecuteRunnableNode("Message Node", new Runnable() {
                public void run() {
                    be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( source.getName() + " deactivates all END using powers while taking a recovery." , BattleEvent.MSG_ABILITY)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( source.getName() + " deactivates all END using powers while taking a recovery." , BattleEvent.MSG_ABILITY)); // .addMessage( source.getName() + " deactivates all END using powers while taking a recovery." , BattleEvent.MSG_ABILITY);
                }
            }));
            
            Ability ability;
            
            int index, count;
            count = source.getIndexedSize( "ActivationInfo" );
            ActivationInfo[] ais = new ActivationInfo[count];
            for ( index=0;index<count;index++) {
                ais[index] = (ActivationInfo)source.getIndexedValue(index,"ActivationInfo","ACTIVATIONINFO" );
            }
            
            for ( index = 0; index < count; index++ ) {
                ActivationInfo ai = ais[index];
                if ( ai == null ) {
                    System.out.println("ACTIVATIONINFO was null at Index " + Integer.toString(index) + ":\n" + source.dumpDetailList() );
                }
                else if ( ai.isActivated() ) {
                    
                    ability = ai.getAbility();
                    
                    if ( ability != null && ( ability.getENDCost(true) > 0 || ability.isPersistent() == false) ) {
                        
                        BattleEvent newBe = new BattleEvent(BattleEvent.DEACTIVATE, ai);
                        node.addChild( new ProcessEmbeddedEventNode("Process Embedded Event", newBe) );
                        // Battle.getCurrentBattle().getBattleEngine().processEvent( newBe );
                        // be.embedBattleEvent(newBe);
                        
                    }
                }
            }
        }
        
        return node;
    }
    
    public boolean isEnabled(Ability ability, Target source) {
        int stun = 0;
        if ( Battle.currentBattle != null ) {
            
            
            //disable recovery if susceptibility is occuring
            int count = source.getEffectCount();
            Effect effect = null;
            for(int index=0;index<count;index++) {
                effect = source.getEffect(index);
                String conditionpresent = effect.getStringValue("Effect.CONDITIONPRESENT");
                if (conditionpresent != null && conditionpresent.equals("TRUE")) {
                    ability.setEnableMessage("Disabled due to Susceptibility in environment");
                    return false;
                }
                String substanceavailable = effect.getStringValue("Effect.SUBSTANCEAVAILABLE");
                if (substanceavailable != null && substanceavailable.equals("FALSE")) {
                    ability.setEnableMessage("Disabled due to Dependence substance unavailable");
                    return false;
                }
                
            }
            
            Chronometer time = Battle.currentBattle.getTime();
            
            if ( Battle.currentBattle.getActiveTarget().isStunned() && time.isTurnEnd() == false ) return false;
            
            stun = Battle.currentBattle.getActiveTarget().getCurrentStat( "STUN" );
            
            
            if ( stun >= -10 ) return true;
            if ( stun >= -20 && time.isTurnEnd() ) return true;
            if ( stun >= -30 && time.isTurnEnd() && time.getTime() % 60 == 0 ) return true;
            
            
            
        }
        return false;
        
    }
    
    //    public boolean isEnabled(Ability ability, Target source) {
    //
    //        int count = source.getEffectCount();
    //        Effect effect = null;
    //        for(int index=0;index<count;index++) {
    //            effect = source.getEffect(index);
    //            if ( (effect.getStringValue("Effect.CONDITIONPRESENT").equals("TRUE")) {
    //                return false;
    //            }
    //        }
    //        if ( source != null && source.hasEffect("Susceptibility") )
    //            return false;
    //        else
    //            return true;
    //    }
    //
    
    
    
    public int calculateCPCost(Ability ability, int not_used) {
        return 0;
    }
    
    public String getConfigSummary(Ability ability, int not_used) {
        return "Recovery";
    }


    
}