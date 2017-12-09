/*
 * effectDying.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.interfaces.*;
import champions.*;
import champions.event.*;
import champions.exception.*;

import java.awt.Color;
/**
 *
 * @author  unknown
 * @version
 */
public class effectDestroyed extends Effect {
    
    /** Creates new effectUnconscious */
    public effectDestroyed() {
        super( "Destroyed", "PERSISTENT", true);
        setUnique(true);
        setCritical(true);
        setEffectColor(Color.red);
    }
    
    public boolean addEffect(BattleEvent be, Target t)
    throws BattleEventException {
        
        if ( super.addEffect(be,t) ) {
            
            t.removeEffect(be,"Unconscious");
            t.removeEffect(be,"Stunned");
            t.removeEffect(be,"Bleeding");
            t.removeEffect(be,"Dying");
            
            be.addCombatStateEvent(t, t.getCombatState(), CombatState.STATE_INACTIVE);
            t.setCombatState(CombatState.STATE_INACTIVE);
            
            
            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is Destroyed!", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is Destroyed!", BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " is Destroyed!", BattleEvent.MSG_NOTICE);
        }
        return true;
    }
    
    public void removeEffect(BattleEvent be, Target t)
    throws BattleEventException {
        super.removeEffect(be,t);
        
        t.setCombatState(CombatState.STATE_FIN);
        be.addCombatStateEvent(t, t.getCombatState(), CombatState.STATE_FIN);
        
        be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is no longer Destroyed!", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is no longer Destroyed!", BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " is no longer Destroyed!", BattleEvent.MSG_NOTICE);
    }
    
    public void addECVAttackModifiers(CVList cvList, Ability attack ) {
        cvList.addSourceCVSet( "Destroyed", 0);
    }
    
    public void addECVDefenseModifiers(CVList cvList , Ability attack) {
        cvList.addTargetCVSet( "Destroyed", 0);
    }
    
    public void addOCVAttackModifiers( CVList cvList , Ability attack) {
        cvList.addSourceCVSet( "Destroyed", 0);
    }
    
    public void addDCVDefenseModifiers( CVList cvList, Ability attack ) {
        cvList.addTargetCVSet( "Destroyed", 0);
    }
    
}