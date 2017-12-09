/*
 * effectDying.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.Ability;
import champions.BattleEvent;
import champions.CVList;
import champions.CombatState;
import champions.Effect;
import champions.Target;
import champions.battleMessage.EffectSummaryMessage;
import champions.exception.BattleEventException;
import java.awt.Color;


/**
 *
 * @author  unknown
 * @version
 */
public class effectDead extends Effect {
    
    /** Creates new effectUnconscious */
    public effectDead() {
        super( "Dead", "PERSISTENT", true);
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
            
            t.stopAdjustmentFading(be);
            
            be.addCombatStateEvent(t, t.getCombatState(), CombatState.STATE_INACTIVE);
            t.setCombatState(CombatState.STATE_INACTIVE);
            
            //be.addBattleMessage( new champions.battleMessage.SimpleBattleMessage(t.getName() + " is Dead!", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.SimpleBattleMessage(t.getName() + " is Dead!", BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " is Dead!", BattleEvent.MSG_NOTICE);
            
            be.addBattleMessage( new EffectSummaryMessage(t, this, true));
        }
        return true;
    }
    
    public void removeEffect(BattleEvent be, Target t)
    throws BattleEventException {
        super.removeEffect(be,t);
        
        t.setCombatState(CombatState.STATE_FIN);
        be.addCombatStateEvent(t, t.getCombatState(), CombatState.STATE_FIN);
        
        //be.addBattleMessage( new champions.battleMessage.SimpleBattleMessage(t.getName() + " is no longer Dead!", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.SimpleBattleMessage(t.getName() + " is no longer Dead!", BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " is no longer Dead!", BattleEvent.MSG_NOTICE);
        
        be.addBattleMessage( new EffectSummaryMessage(t, this, false));
        
        if ( t.hasStat("STUN") && t.getCurrentStat("STUN") < 0 ) {
            new effectUnconscious().addEffect(be, t);
        }
        
        t.startAdjustmentFading(be);
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
    
    public void addECVAttackModifiers(CVList cvList, Ability attack ) {
        cvList.addSourceCVSet( "Dead", 0);
    }
    
    public void addECVDefenseModifiers(CVList cvList , Ability attack) {
        cvList.addTargetCVSet( "Dead", 0);
    }
    
    public void addOCVAttackModifiers( CVList cvList , Ability attack) {
        cvList.addSourceCVSet( "Dead", 0);
    }
    
    public void addDCVDefenseModifiers( CVList cvList, Ability attack ) {
        cvList.addTargetCVSet( "Dead", 0);
    }
    
}