/*
 * ActivateSubEvent.java
 *
 * Created on November 9, 2007, 11:03 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.undoableEvent;

import champions.Ability;
import champions.CombatState;
import champions.Effect;
import champions.Target;
import champions.interfaces.Undoable;
import java.io.Serializable;

/**
 *
 * @author twalker
 */
public class CombatStateUndoable implements Undoable, Serializable {
    
    protected Target target;

    protected CombatState oldState;
    protected CombatState newState;

    public CombatStateUndoable(Target target, CombatState oldState, CombatState newState) {
        this.target = target;
        this.oldState = oldState;
        this.newState = newState;
    }
    
    // These events don't seem to do anything...
    public void undo() {
        target.setCombatState( oldState );
    }

    public void redo() {
        target.setCombatState( newState );
    }
    
    
    public CombatState getNewState() {
        return newState;
    }

    public CombatState getOldState() {
        return oldState;
    }

    public Target getTarget() {
        return target;
    }
    
    public String toString() {
        return "CombatStateUndoable [Target = " + target.getName() + ", OldState = " + oldState + ", NewState = " +  newState + "]";
    }

}
