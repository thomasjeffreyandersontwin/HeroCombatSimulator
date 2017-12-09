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
public class AttackDelayEvent implements Undoable, Serializable {
    
    protected Target target;
    protected boolean value;

    public AttackDelayEvent(Target target, boolean value) {
        this.target = target;
        this.value = value;
    }

    public Target getTarget() {
        return target;
    }

    public boolean isValue() {
        return value;
    }
    
    
    
    // These events don't seem to do anything...
    public void undo() {
        target.add("Target.ATTACKDELAYED",  (!value ? "TRUE" : "FALSE" ),true,false);
    }

    public void redo() {
        target.add("Target.ATTACKDELAYED",  ( value ? "TRUE" : "FALSE" ),true,false);
    }

   
    
}
