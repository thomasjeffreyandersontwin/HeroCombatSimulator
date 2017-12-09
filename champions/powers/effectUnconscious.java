/*
 * effectUnconscious.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.Ability;
import champions.AbilityAction;
import champions.ActivationInfo;
import champions.Battle;
import champions.BattleEvent;
import champions.CVList;
import champions.Chronometer;
import champions.Effect;
import champions.Target;
import champions.battleMessage.EffectSummaryMessage;
import champions.exception.BattleEventException;
import champions.undoableEvent.UnconsciousUndoable;

import java.awt.Color;
import java.util.Vector;
/**
 *
 * @author  unknown
 * @version
 */
public class effectUnconscious extends Effect {
    
    public static Ability recovery;
    public static Ability pass;
    
    /** Creates new effectUnconscious */
    public effectUnconscious() {
        super( "Unconscious", "PERSISTENT", true );
        setUnique(true);
        setCritical(true);
        setEffectColor(Color.red);
    }
    
    public boolean addEffect(BattleEvent be, Target t)  throws BattleEventException {
        
        boolean oldUnconscious = t.isUnconscious();
        t.setUnconscious(true);
        
        be.addUndoableEvent( new UnconsciousUndoable(t, oldUnconscious, true) );
        
        //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is unconscious!", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is unconscious!", BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " is unconscious!", BattleEvent.MSG_NOTICE);

        be.addBattleMessage( new EffectSummaryMessage(t, this, true));
        
        Ability ability;
        
        int index, count;
        count = t.getIndexedSize( "ActivationInfo" );
        for ( index = 0; index < count; index++ ) {
            ActivationInfo ai = (ActivationInfo)t.getIndexedValue(index, "ActivationInfo","ACTIVATIONINFO" );
            if ( ai.isActivated() ) {
                
                if ( ai.getIndexedSize( "Ability" ) > 0
                && (ability = (Ability)ai.getIndexedValue(0,"Ability","ABILITY") ) != null
                && ability.getPType().equals("CONSTANT") ) {
                    BattleEvent newBe = new BattleEvent(BattleEvent.DEACTIVATE, ai,
                    (Chronometer)Battle.currentBattle.getTime().clone(), -1);
                    
                    be.addUndoableEvent(Battle.currentBattle.addDelayedEvent(newBe) );
                }
            }
        }
        
        setEffectPriority(4);
        boolean success = super.addEffect(be,t);
        
        Effect kd = new effectKnockedDown();
        kd.addEffect(be,t);
        
        return success;
        
    }
    
    public void removeEffect(BattleEvent be,Target t)  throws BattleEventException {
        super.removeEffect(be,t);
        
        boolean oldUnconscious = t.isUnconscious();
        t.setUnconscious(false);
        be.addUndoableEvent( new UnconsciousUndoable(t, oldUnconscious, false) );
        
        if ( t.hasStat( "STUN") && t.hasStat("END") ) {
            int stun = t.getCurrentStat("STUN");
            if ( stun > 0 ) {
                Effect c = new Effect("END after Unconsciousness");
                c.addSetSubeffect("UNCONSCIOUSEnd", "STAT", "END", stun, CURRENT_STAT);
                c.addEffect(be,t);
            }
        }
        //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is no longer unconscious!", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is no longer unconscious!", BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " is no longer unconscious!", BattleEvent.MSG_NOTICE);
        
        be.addBattleMessage( new EffectSummaryMessage(t, this, false));
    }
    
    public void addDCVDefenseModifiers( CVList cvList , Ability attack) {
        cvList.addTargetCVSet( "Unconscious", 0);
    }
    
    public void addECVDefenseModifiers( CVList cvList , Ability attack) {
        cvList.addTargetCVSet( "Unconscious", 0);
    }
    public void predefense(BattleEvent be, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget)
    throws BattleEventException {
        int i, count;
        
        String type, versustype;
        Object versus;
        Double value;
        
        count = effect.getIndexedSize("Subeffect");
        for (i=0;i<count;i++) {
            if (
            ( type = effect.getIndexedStringValue( i, "Subeffect", "EFFECTTYPE" )) == null
            || (value = effect.getIndexedDoubleValue( i, "Subeffect", "VALUE" )) == null
            || (versustype = effect.getIndexedStringValue( i, "Subeffect", "VERSUSTYPE" )) == null
            || (versus = effect.getIndexedValue( i, "Subeffect", "VERSUS" )) == null
            ){
                continue;
            }
            
            if ( versus.equals("STUN") && type.equals("DAMAGE") && value.intValue() > 0 ) {
                be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage("Stun damage doubled due to unconsciousness.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage("Stun damage doubled due to unconsciousness.", BattleEvent.MSG_NOTICE)); // .addMessage("Stun damage doubled due to unconsciousness.", BattleEvent.MSG_NOTICE);
                effect.addIndexed( i,  "Subeffect","VALUE", new Double( value.intValue() * 2 ), true);
            }
        }
    }
    
    public String getDescription() {
        String desc = "This character is current unconscious.  They will remain unconscious " +
                      "until their stun is greater then or equal to zero.\n\n" +
                      "While uncounscious, the character is only allowed to take post-turn " +
                      "recoveries.  The rate of the recoveries is determined by the their " +
                      "current stun.";
        return desc;
    }
    
    public void addActions(Vector actions) {
        if ( recovery == null ) {
            recovery = new Ability("Recovery");
            recovery.addPAD( new powerRecovery(), null );
        }
        
        if ( recovery.isEnabled( getTarget() ) ) {
                AbilityAction action = new AbilityAction( "Recovery", recovery );
                action.putValue( "TOOLTIPTEXT","Recovery - Recover STUN to return to consciousness.");
                action.setType(  BattleEvent.ACTIVATE );
                action.setSource( this.getTarget() );
                
                actions.add(action);
        }
        else {
            if ( pass == null ) {
                pass = new Ability("Pass Turn");
                pass.addPAD( new powerPass(), null );
            }
                AbilityAction action = new AbilityAction( "Pass Turn", pass );
                action.putValue( "TOOLTIPTEXT","Pass Turn - STUN too low for recovery.  Must pass.");
                action.setType(  BattleEvent.ACTIVATE );
                action.setSource( this.getTarget() );
                
                actions.add(action);
        }
    }
}