/*
 * ActivateSubEvent.java
 *
 * Created on November 9, 2007, 11:03 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.undoableEvent;

import champions.Target;
import champions.interfaces.Undoable;
import java.io.Serializable;

/**
 *
 * @author twalker
 */
public class StunnedUndoable implements Undoable, Serializable {
    
    private Target target;
    private boolean oldStunned;
    private boolean newStunned;
    
    public StunnedUndoable(Target target, boolean oldStunned, boolean newStunned) {
        this.target = target;
        this.oldStunned = oldStunned;
        this.newStunned = newStunned;
    }
    
    // These events don't seem to do anything...
    public void undo() {
        target.setStunned(oldStunned);
    }

    public void redo() {
        target.setStunned(newStunned);
    }

   
    
}
