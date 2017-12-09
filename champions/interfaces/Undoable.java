/*
 * UndoInfo.java
 *
 * Created on September 1, 2001, 8:51 PM
 */

package champions.interfaces;

import champions.exception.BattleEventException;
import java.io.Serializable;

/**
 *
 * @author  twalker
 * @version 
 */
public interface Undoable extends Serializable {
    public void undo() throws BattleEventException ;
    public void redo() throws BattleEventException ;
}

