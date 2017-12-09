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
import champions.Target;
import champions.interfaces.Undoable;
import java.io.Serializable;

/**
 *
 * @author twalker
 */
public class TargetActivationInfoLinkUndoable implements Undoable, Serializable {

    protected Target target;
    protected ActivationInfo activationInfo;
    protected boolean added;

    public TargetActivationInfoLinkUndoable(Target target, ActivationInfo activationInfo, boolean added) {
        this.target = target;
        this.activationInfo = activationInfo;
        this.added = added;
    }

    public Target getTarget() {
        return target;
    }

    public ActivationInfo getActivationInfo() {
        return activationInfo;
    }

    public boolean isAdded() {
        return added;
    }

    public void undo() {
        if (added) {
            activationInfo.removeSource();
        } else {
            activationInfo.setSource(target);
        }
    }

    public void redo() {
        if (!added) {
            activationInfo.removeSource();
        } else {
            activationInfo.setSource(target);
        }
    }
}
