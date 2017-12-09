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
import champions.interfaces.ENDSource;
import champions.interfaces.Undoable;
import java.io.Serializable;

/**
 *
 * @author twalker
 */
public class ENDSourceUndoable implements Undoable, Serializable {
    
    protected String endSourceName;
    protected Target target;
    protected ENDSource endSource;
    protected boolean added;
    
    public ENDSourceUndoable(Target target, String endSourceName, ENDSource endSource, boolean added) {
        this.target = target;
        this.endSourceName = endSourceName;
        this.endSource = endSource;
        this.added = added;
    }
    
    // These events don't seem to do anything...
    public void undo() {
        if ( added ) {
            target.removeENDSource(endSourceName);
        }
        else {
            target.addENDSource(endSourceName, endSource);
        }
    }

    public void redo() {
        if ( added == false ) {
            target.removeENDSource(endSourceName);
        }
        else {
            target.addENDSource(endSourceName, endSource);
        }
        
    }


    
}
