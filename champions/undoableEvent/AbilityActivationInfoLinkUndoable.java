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
import champions.ActivationInfo;
import champions.interfaces.Undoable;
import java.io.Serializable;

/**
 *
 * @author twalker
 */
public class AbilityActivationInfoLinkUndoable implements Undoable, Serializable {
    
    protected Ability ability;
    protected ActivationInfo activationInfo;
    protected boolean added;

    public AbilityActivationInfoLinkUndoable(Ability ability, ActivationInfo activationInfo, boolean added) {
        this.ability = ability;
        this.activationInfo = activationInfo;
        this.added = added;
    }

    public Ability getAbility() {
        return ability;
    }

    public ActivationInfo getActivationInfo() {
        return activationInfo;
    }

    public boolean isAdded() {
        return added;
    }
    
    // These events don't seem to do anything...
    public void undo() {
        if ( added ) {
            activationInfo.removeAbilityLink(ability);

        } else {
            activationInfo.addAbilityLink(ability);
        }
    }

    public void redo() {
        if ( ! added ) {
            activationInfo.removeAbilityLink(ability);

        } else {
            activationInfo.addAbilityLink(ability);
        }
    }
    
}
