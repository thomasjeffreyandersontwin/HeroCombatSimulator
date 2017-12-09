/*
 * ActivateSubEvent.java
 *
 * Created on November 9, 2007, 11:03 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.undoableEvent;

import champions.interfaces.Undoable;
import java.io.Serializable;

/**
 *
 * @author twalker
 */
public class TemplateUndoable implements Undoable, Serializable {
    
    
    
    // These events don't seem to do anything...
    public void undo() {
    }

    public void redo() {
    }

   
    
}
