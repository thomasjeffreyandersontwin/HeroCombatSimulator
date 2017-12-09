/*
 * effectDying.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.BattleEvent;
import champions.Effect;
import champions.Target;
import champions.battleMessage.EffectSummaryMessage;
import champions.event.BattleMessageEvent;
import champions.exception.BattleEventException;
import java.awt.Color;

/**
 *
 * @author  unknown
 * @version
 */
public class effectDying extends Effect {

    /** Creates new effectUnconscious */
    public effectDying() {
        super ("Dying", "PERSISTENT", true );
        setUnique(true);
        setCritical(true);
        setEffectColor(Color.red);
    }

    public boolean addEffect(BattleEvent be, Target t) 
    throws BattleEventException {
        
        if ( t.hasEffect("effectDoesNotBleed")) return false;
        
        if ( t.hasEffect("Dying" ) ) {
            return false;
        }
        if ( t.isDead() ) {
            return false;
        }
        //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is dying from injuries!", BattleMessageEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is dying from injuries!", BattleMessageEvent.MSG_NOTICE)); // .addMessage(t.getName() + " is dying from injuries!", BattleMessageEvent.MSG_NOTICE);
        
        be.addBattleMessage( new EffectSummaryMessage(t, this, true));
        return super.addEffect(be,t);
        
    } 

    public void removeEffect(BattleEvent be, Target t) 
    throws BattleEventException {
        super.removeEffect(be,t);

        //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is no longer dying from injuries!", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is no longer dying from injuries!", BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " is no longer dying from injuries!", BattleEvent.MSG_NOTICE);
        
        be.addBattleMessage( new EffectSummaryMessage(t, this, false));
    }

    public boolean postturn(BattleEvent be, Target t) 
    throws BattleEventException {

        be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + " is dying from injuries.",        BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + " is dying from injuries.",        BattleEvent.MSG_NOTICE)); // .addMessage( t.getName() + " is dying from injuries.",        BattleEvent.MSG_NOTICE);
        new effectBleeding().addEffect( be, t);
    
        return false;
    }

    public class effectBleeding extends Effect {
        public effectBleeding() {
            super ("Bleeding", "INSTANT" );
            //addSubeffectInfo( "Bleeding", "DAMAGE", "NONE", "NONE", "STAT", "BODY", new Integer(1) );
            addDamageSubeffect("BodyFromDying", "BODY", 1, "NONE", "NONE");
        }
    }
}