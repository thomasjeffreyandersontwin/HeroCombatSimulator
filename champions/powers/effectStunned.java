/*
 * effectUnconscious.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.*;
import champions.battleMessage.EffectSummaryMessage;
import champions.exception.BattleEventException;
import champions.undoableEvent.StunnedUndoable;
import java.awt.Color;

import java.util.Vector;

/**
 *
 * @author  unknown
 * @version
 */
public class effectStunned extends Effect {
    
    private static Ability stunRecovery = null;
    /** Creates new effectUnconscious */
    public effectStunned() {
        super("Stunned", "PERSISTENT", true);
        setUnique(true);
                setCritical(true);
        setEffectColor(new Color(153,0,153));
    }
    
    public boolean addEffect(BattleEvent be, Target t)  throws BattleEventException {
        
        Ability ability;
        
        if ( t.hasEffect( effectCannotBeStunned.class )) return false;
        
        boolean oldStunned = t.isStunned();
        t.setStunned(true);
        be.addUndoableEvent( new StunnedUndoable(t,oldStunned,true) );
        
        
       // be.addBattleMessage( new champions.battleMessage.SimpleBattleMessage(t.getName() + " is stunned!", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.SimpleBattleMessage(t.getName() + " is stunned!", BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " is stunned!", BattleEvent.MSG_NOTICE);
        
        be.addBattleMessage( new EffectSummaryMessage(t, this, true));
        
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
                    
                    be.addUndoableEvent( Battle.currentBattle.addDelayedEvent(newBe) );
                }
            }
        }
        return super.addEffect(be,t);
        
    }
    
    public void removeEffect(BattleEvent be, Target t)  throws BattleEventException {
        super.removeEffect(be,t);
        boolean oldStunned = t.isStunned();
        t.setStunned(false);
        be.addUndoableEvent( new StunnedUndoable(t,oldStunned,false) );
        
        //be.addBattleMessage( new champions.battleMessage.SimpleBattleMessage(t.getName() + " is no longer stunned!", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.SimpleBattleMessage(t.getName() + " is no longer stunned!", BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " is no longer stunned!", BattleEvent.MSG_NOTICE);
        
        be.addBattleMessage( new EffectSummaryMessage(t, this, false));
    }
    
    
    public void addDCVDefenseModifiers( CVList cvList , Ability attack) {
        cvList.addTargetCVMultiplier( "Stunned", 0.5);
    }
    
    public void addActions(Vector actions) {
        if ( stunRecovery == null ) {
            stunRecovery = new Ability("Stun Recovery");
            stunRecovery.addPAD( new powerUnstun(), null );
        }
        
        AbilityAction action = new AbilityAction( "Stun Recovery", stunRecovery );
        action.putValue( "TOOLTIPTEXT","Stun Recovery - Recover from being stunned.");
        action.setType(  BattleEvent.ACTIVATE );
        action.setSource( this.getTarget() );
        
        actions.add(action);
    }
}