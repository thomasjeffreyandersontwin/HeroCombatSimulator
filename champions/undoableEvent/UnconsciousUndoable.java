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
public class UnconsciousUndoable implements Undoable, Serializable {
    
    private Target target;
    private boolean oldUnconscious;
    private boolean newUnconscious;
    
    public UnconsciousUndoable(Target target, boolean oldUnconscious, boolean newUnconscious) {
        this.target = target;
        this.oldUnconscious = oldUnconscious;
        this.newUnconscious = newUnconscious;
    }
    
    // These events don't seem to do anything...
    public void undo() {
        target.setUnconscious(oldUnconscious);
    }

    public void redo() {
        target.setUnconscious(newUnconscious);
    }

   
    
}
