/*
 * effectUnconscious.java
 *
 * Created on October 2, 2000, 4:06 PM
 */
package champions.powers;

import champions.Ability;
import champions.AbilityAction;
import champions.BattleEvent;
import champions.CVList;
import champions.Effect;
import champions.Target;
import champions.Battle;
import champions.battleMessage.EffectSummaryMessage;
import champions.exception.BattleEventException;
import java.util.Vector;
import java.awt.Color;

/**
 *
 * @author  unknown
 * @version
 */
public class effectHipshot extends Effect {

    protected int duration = 1;

    public effectHipshot() {
        super("Hipshot", "PERSISTENT", true);
        setUnique(true);
    }

    public boolean addEffect(BattleEvent be, Target t) throws BattleEventException {

        boolean added = super.addEffect(be, t);
        if (added) {
            //t.applyIncreaseToAdjustedStat("DEX",1,0,false);
            t.adjustEffectiveDex(1);
            be.addBattleMessage(new EffectSummaryMessage(t, this, true));
        }
        return added;
    }

    public void removeEffect(BattleEvent be, Target t) throws BattleEventException {
        super.removeEffect(be, t);
        t.adjustEffectiveDex(-1);
        be.addBattleMessage(new EffectSummaryMessage(t, this, false));
    }

    public void addOCVAttackModifiers(CVList cvList, Ability attack) {
        cvList.addSourceCVModifier("Hipshot Penalty", -1);
    }

    public boolean prephase(BattleEvent be, Target t)
            throws BattleEventException {

        if (getDuration() == 0) {
            //t.applyDecreaseToAdjustedStat("DEX",1,0,false); // moved adjustment to removeEffect
            return true;
        }
        
        if (Battle.currentBattle.getActiveTarget() == t) {
            setDuration(0);
        }
        
        return false;
    }

    /** Getter for property duration.
     * @return Value of property duration.
     */
    public int getDuration() {
        return duration;
    }

    /** Setter for property duration.
     * @param duration New value of property duration.
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }
}