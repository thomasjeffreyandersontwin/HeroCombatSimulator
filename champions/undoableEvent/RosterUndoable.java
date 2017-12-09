/*
 * ActivateSubEvent.java
 *
 * Created on November 9, 2007, 11:03 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.undoableEvent;

import champions.Roster;
import champions.interfaces.Undoable;
import java.io.Serializable;
import champions.Target;

/**
 *
 * @author twalker
 */
public class RosterUndoable implements Undoable, Serializable {
    
    protected Roster roster;
    protected Target target;
    protected boolean added;

    public RosterUndoable(Roster roster, Target target, boolean added) {
        this.roster = roster;
        this.target = target;
        this.added = added;
    }

    public boolean isAdded() {
        return added;
    }

    public Roster getRoster() {
        return roster;
    }

    public Target getTarget() {
        return target;
    }
    
    
    // These events don't seem to do anything...
    public void undo() {
        if ( added ) {
            roster.remove(target,false);
        } else {
            roster.add(target,false);
        }
    }

    public void redo() {
        if ( added == false ) {
            roster.remove(target,false);
        } else {
            roster.add(target,false);
        }
    }

   
    
}
