/*
 * ActivateSubEvent.java
 *
 * Created on November 9, 2007, 11:03 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.undoableEvent;

import champions.Battle;
import champions.Target;
import champions.interfaces.Undoable;
import java.io.Serializable;

/**
 *
 * @author twalker
 */
public class ActiveTargetUndoable implements Undoable, Serializable {
    
    protected Target oldTarget;
    protected Target newTarget;

    public ActiveTargetUndoable(Target oldTarget, Target newTarget) {
        this.oldTarget = oldTarget;
        this.newTarget = newTarget;
    }

    public Target getNewTarget() {
        return newTarget;
    }

    public Target getOldTarget() {
        return oldTarget;
    }
    
    // These events don't seem to do anything...
    public void undo() {
        Battle.currentBattle.setSelectedTarget( oldTarget );
    }

    public void redo() {
        Battle.currentBattle.setSelectedTarget( newTarget );
    }

   
    
}
