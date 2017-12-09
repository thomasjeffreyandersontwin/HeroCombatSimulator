/*
 * RemoveObstructionUndoable.java
 *
 * Created on January 17, 2002, 9:29 PM
 */

package champions;

import champions.interfaces.*;
import java.io.Serializable;
/**
 *
 * @author  twalker
 * @version 
 */
public class RemoveObstructionUndoable implements Undoable, Serializable {

    private ObstructionList obstructionList;
    private Target obstruction;
    private int position;
    
    /** Creates new RemoveObstructionUndoable */
    public RemoveObstructionUndoable(ObstructionList ol, Target obstruction, int position) {
        this.obstructionList = ol;
        this.obstruction = obstruction;
        this.position = position;
    }

    public void redo() {
        obstructionList.removeObstruction(position);
    }
    
    public void undo() {
        obstructionList.addObstruction(obstruction, position);
    }
    
    public String toString() {
        return "RemoveObstructionUndoable (" + obstruction.toString() + ", " + Integer.toString(position) + ")";
    }
    
}
