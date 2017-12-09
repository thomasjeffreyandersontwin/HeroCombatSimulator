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
import champions.Effect;
import champions.Target;
import champions.interfaces.Undoable;
import java.io.Serializable;

/**
 *
 * @author twalker
 */
public class PrephaseDoneUndoable implements Undoable, Serializable {
    
    protected boolean state;
    protected Target target;

    public PrephaseDoneUndoable(Target target, boolean state) {
        this.state = state;
        this.target = target;
    }

    public Target getTarget() {
        return target;
    }

    public boolean isState() {
        return state;
    }
    
    // These events don't seem to do anything...
    public void undo() {
        target.setPrephaseDone( ! state );
    }

    public void redo() {
        target.setPrephaseDone( state );
    }

   
    
}
