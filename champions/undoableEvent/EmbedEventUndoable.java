/*
 * ActivateSubEvent.java
 *
 * Created on November 9, 2007, 11:03 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.undoableEvent;

import champions.BattleEvent;
import champions.exception.BattleEventException;
import champions.interfaces.Undoable;
import java.io.Serializable;

/**
 *
 * @author twalker
 */
public class EmbedEventUndoable implements Undoable, Serializable {
    
    protected BattleEvent battleEvent;
    protected BattleEvent embeddedBattleEvent;

    public EmbedEventUndoable(BattleEvent battleEvent, BattleEvent embeddedBattleEvent) {
        this.battleEvent = battleEvent;
        this.embeddedBattleEvent = embeddedBattleEvent;
    }

    public BattleEvent getEmbeddedBattleEvent() {
        return embeddedBattleEvent;
    }
    
    
    public void undo() throws BattleEventException{
        embeddedBattleEvent.rollbackBattleEvent();
    }

    public void redo() throws BattleEventException {
        embeddedBattleEvent.rollforwardBattleEvent();
    }

   
    
}
