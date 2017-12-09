/*
 * ActivateSubEvent.java
 *
 * Created on November 9, 2007, 11:03 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.undoableEvent;

import champions.Effect;
import champions.Target;
import champions.exception.BattleEventException;
import champions.interfaces.Undoable;
import java.io.Serializable;

/**
 *
 * @author twalker
 */
public class EffectUndoable implements Undoable, Serializable {
    
    protected Effect effect;
    protected boolean added;
    protected Target target;
    
    /** Creates a new instance of ActivateSubEvent */
    public EffectUndoable(Effect effect, Target target, boolean added) {
        this.effect = effect;
        this.added = added;
        this.target = target;
    }

    

    public void undo() throws BattleEventException {
        effect.rollbackEffects(target,added);

    }

    public void redo() throws BattleEventException {
        effect.rollforwardEffects(target,added);
    }

    public Effect getEffect() {
        return effect;
    }

    public boolean isAdded() {
        return added;
    }

    public Target getTarget() {
        return target;
    }
    
}
