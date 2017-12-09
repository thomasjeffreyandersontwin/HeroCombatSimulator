/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champions;

import champions.exception.BattleEventException;

/**
 *
 * @author twalker
 */
public abstract class RunnableBattleEvent extends BattleEvent {

    public RunnableBattleEvent() {
        super(RUNNABLE_EVENT);
    }

    /** Process the battle event.
     *
     * @throws champions.exception.BattleEventException
     */
    public abstract void processEvent() throws BattleEventException;
}
