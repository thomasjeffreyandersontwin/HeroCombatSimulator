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
import champions.interfaces.Undoable;
import java.io.Serializable;

/**
 *
 * @author twalker
 */
public class ActivateUndoable implements Undoable, Serializable {
    
    protected Ability ability;
    protected boolean activated;
    
    /** Creates a new instance of ActivateSubEvent */
    public ActivateUndoable(Ability ability, boolean activated) {
        this.ability = ability;
        this.activated = activated;
    }

    public Ability getAbility() {
        return ability;
    }

    public void setAbility(Ability ability) {
        this.ability = ability;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public void undo() {
        ability.add("Ability.ACTIVATED",  (activated ? "FALSE" : "TRUE" ), true );
    }

    public void redo() {
        ability.add("Ability.ACTIVATED",  (!activated ? "FALSE" : "TRUE" ), true );
    }
    
}
