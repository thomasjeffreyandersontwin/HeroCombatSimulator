/*
 * effectUnconscious.java
 *
 * Created on October 2, 2000, 4:06 PM
 */
package champions.powers;

import champions.Ability;
import champions.BattleEvent;
import champions.LinkedEffect;
import champions.Target;
import champions.battleMessage.GainedEffectSummaryMessage;
import champions.exception.BattleEventException;


/**
 *
 * @author  unknown
 * @version
 */
public class effectGained extends LinkedEffect{

    private Ability ability;
    /** Creates new effectUnconscious */
    public effectGained(String effectName) {
        super(effectName, "PERSISTENT", false);
    }

    public effectGained(Ability ability) {
        super(ability.getName(), "LINKED", false);
        setAbility(ability);
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
            be.addBattleMessage(new GainedEffectSummaryMessage(target, this, true));
            return true;
        }
        return false;
    }

    public void removeEffect(BattleEvent be, Target target)
            throws BattleEventException {
        super.removeEffect(be, target);
        be.addBattleMessage(new GainedEffectSummaryMessage(target, this, false));
    }

    public Ability getAbility() {
        return ability;
    }

    public void setAbility(Ability ability) {
        this.ability = ability;
    }
}