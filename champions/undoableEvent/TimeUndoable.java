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
import champions.Chronometer;
import champions.interfaces.Undoable;
import java.io.Serializable;

/**
 *
 * @author twalker
 */
public class TimeUndoable implements Undoable, Serializable {
    
    protected Chronometer oldTime;
    protected Chronometer newTime;

    public TimeUndoable(Chronometer oldTime, Chronometer newTime) {
        this.oldTime = oldTime;
        this.newTime = newTime;
    }

    public Chronometer getNewTime() {
        return newTime;
    }

    public Chronometer getOldTime() {
        return oldTime;
    }
    
    
    
    // These events don't seem to do anything...
    public void undo() {
        Battle.currentBattle.setTime( oldTime );
    }

    public void redo() {
        Battle.currentBattle.setTime( newTime );
    }

   
    
}
