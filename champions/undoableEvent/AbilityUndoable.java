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
public class AbilityUndoable implements Undoable, Serializable {
    
    protected Ability ability;
    protected boolean added;
    protected Target target;
    
    /** Creates a new instance of ActivateSubEvent */
    public AbilityUndoable(Ability ability, Target target, boolean added) {
        this.ability = ability;
        this.added = added;
        this.target = target;
    }

    
    // These events don't seem to do anything...
    public void undo() {
        
    }

    public void redo() {
        
    }

    public boolean isAdded() {
        return added;
    }

    public Target getTarget() {
        return target;
    }

    public Ability getAbility() {
        return ability;
    }
    
}
