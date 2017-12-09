/*
 * CompountUndoable.java
 *
 * Created on March 7, 2002, 12:22 PM
 */

package champions;

import champions.exception.BattleEventException;
import champions.interfaces.Undoable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author  Trevor Walker
 * @version 
 */
public class CompoundUndoable implements Undoable, Serializable {

    List<Undoable> undoables = new ArrayList<Undoable>();
    
    /** Creates new CompountUndoable */
    public CompoundUndoable() {
    }
    
    public CompoundUndoable(Undoable ... undoables) {
        for(Undoable u : undoables) {
            addUndoable(u);
        }
    }
    
    public void addUndoable(Undoable undoable) {
        undoables.add(undoable);
    }

    public void redo() throws BattleEventException {
                // redo, starting with first first...
        int count = undoables.size();
        int index = 0;
        for(; index < count; index++) {
            Undoable u = (Undoable)undoables.get(index);
            u.redo();
        }
    }
    
    public void undo() throws BattleEventException {
        // Undo, starting with last first...
        int index = undoables.size() - 1;
        for(; index >= 0; index--) {
            Undoable u = (Undoable)undoables.get(index);
            u.undo();
        }
    }
    
    public String toString() {
        String s = "CompoundUndoable [";
        int count = undoables.size();
        int index = 0;
        for(; index < count; index++) {
            Undoable u = (Undoable)undoables.get(index);
            s = s + u.toString();
        }
        s = s+"]";
        
        return s;
    }
    
}
