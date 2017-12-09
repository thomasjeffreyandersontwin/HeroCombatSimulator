/*
 * ActivateSubEvent.java
 *
 * Created on November 9, 2007, 11:03 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.undoableEvent;

import champions.ActivationInfo;
import champions.Chronometer;
import champions.interfaces.Undoable;
import java.io.Serializable;

/**
 *
 * @author twalker
 */
public class ActivationInfoLastTimeProcessUndoable implements Undoable, Serializable {
    
    protected ActivationInfo activationInfo;
    protected Chronometer oldTime;
    protected Chronometer newTime;

    public ActivationInfoLastTimeProcessUndoable(ActivationInfo activationInfo, Chronometer oldTime, Chronometer newTime) {
        this.activationInfo = activationInfo;
        this.oldTime = oldTime;
        this.newTime = newTime;
    }

    public ActivationInfo getActivationInfo() {
        return activationInfo;
    }

    public Chronometer getNewTime() {
        return newTime;
    }

    public Chronometer getOldTime() {
        return oldTime;
    }
    
    // These events don't seem to do anything...
    public void undo() {
        activationInfo.setLastProcessedTime( oldTime );
    }

    public void redo() {
        activationInfo.setLastProcessedTime( newTime );
    }

   
    
}
