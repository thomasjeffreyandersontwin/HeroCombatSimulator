/*
 * ActivateSubEvent.java
 *
 * Created on November 9, 2007, 11:03 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.undoableEvent;

import champions.Battle;
import champions.BattleEvent;
import champions.interfaces.Undoable;
import java.io.Serializable;

/**
 *
 * @author twalker
 */
public class AddEventUndoable implements Undoable, Serializable {
    
    protected BattleEvent battleEvent;
    protected boolean added;

    public AddEventUndoable(BattleEvent battleEvent, boolean added) {
        this.battleEvent = battleEvent;
        this.added = added;
    }

    public boolean isAdded() {
        return added;
    }

    public BattleEvent getBattleEvent() {
        return battleEvent;
    }
    
    // These events don't seem to do anything...
    public void undo() {
        if ( ! added ) {
            Battle.currentBattle.addEvent( battleEvent );
        } else {
            Battle.currentBattle.removeEvent( battleEvent );
        }
    }

    public void redo() {
        if ( added ) {
            Battle.currentBattle.addEvent( battleEvent );
        } else {
            Battle.currentBattle.removeEvent( battleEvent );
        }
    }

   
    
}
