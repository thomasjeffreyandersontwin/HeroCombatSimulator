/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package champions.undoableEvent;

import champions.BattleEvent;
import champions.interfaces.Undoable;
import java.io.Serializable;

public class FinishedProcessingUndoable implements Undoable, Serializable {

    private BattleEvent be;
    private boolean oldState;
    private boolean newState;

    /** Creates new MessageUndoable */
    public FinishedProcessingUndoable(BattleEvent be, boolean oldState, boolean newState) {
        this.be = be;
        this.oldState = oldState;
        this.newState = newState;
    }

    public BattleEvent getBe() {
        return be;
    }

    public boolean isNewState() {
        return newState;
    }

    public boolean isOldState() {
        return oldState;
    }

    public void redo() {
        be.setFinishedProcessingEvent(newState);
    }

    public void undo() {
        be.setFinishedProcessingEvent(oldState);
    }

    public String toString() {
        return "FinishedProcessingUndoable [OldState: " + (oldState ? "TRUE" : "FALSE") + ", NewState: " + (newState ? "TRUE" : "FALSE") + "]";
    }
    }
