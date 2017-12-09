/*
 * effectUnconscious.java
 *
 * Created on October 2, 2000, 4:06 PM
 */
package champions.powers;

import champions.BattleEvent;
import champions.Effect;
import champions.Target;
import champions.battleMessage.EffectSummaryMessage;
import champions.exception.BattleEventException;

/**
 *
 * @author  unknown
 * @version
 */
public class effectGeneric extends Effect {

    /** Creates new effectUnconscious */
    public effectGeneric(String effectName) {
        super(effectName, "PERSISTENT", true);
    }

    public void setDescription(String description) {
        this.add("Effect.DESCRIPTION", description);
    }

    public String getDescription() {
        return this.getStringValue("Effect.DESCRIPTION");
    }

    public boolean addEffect(BattleEvent be, Target target)
            throws BattleEventException {
        if (super.addEffect(be, target)) {
            //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " is now " + getName(), BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " is now " + getName(), BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " is now " + getName(), BattleEvent.MSG_NOTICE );
            be.addBattleMessage(new EffectSummaryMessage(target, this, true));
            return true;
        }
        return false;
    }

    public void removeEffect(BattleEvent be, Target target)
            throws BattleEventException {
        super.removeEffect(be, target);
        //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " is no longer " + getName(), BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " is no longer " + getName(), BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " is no longer " + getName(), BattleEvent.MSG_NOTICE );
        be.addBattleMessage(new EffectSummaryMessage(target, this, false));
    }
}