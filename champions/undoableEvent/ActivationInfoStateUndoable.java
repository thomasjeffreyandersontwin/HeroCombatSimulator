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
import champions.interfaces.Undoable;
import java.io.Serializable;

/**
 *
 * @author twalker
 */
public class ActivationInfoStateUndoable implements Undoable, Serializable {
    
    protected ActivationInfo activationInfo;
    protected String oldState;
    protected String newState;

    public ActivationInfoStateUndoable(ActivationInfo activationInfo, String oldState, String newState) {
        this.activationInfo = activationInfo;
        this.oldState = oldState;
        this.newState = newState;
    }

    public ActivationInfo getActivationInfo() {
        return activationInfo;
    }

    public String getNewState() {
        return newState;
    }

    public String getOldState() {
        return oldState;
    }
    
    // These events don't seem to do anything...
    public void undo() {
        activationInfo.setState( oldState );
    }

    public void redo() {
        activationInfo.setState( newState );
    }

   
    
}
