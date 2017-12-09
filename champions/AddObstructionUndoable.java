/*
 * ObstructionUndoable.java
 *
 * Created on January 17, 2002, 9:23 PM
 */

package champions;

import champions.interfaces.*;
import java.io.Serializable;
/**
 *
 * @author  twalker
 * @version 
 */
public class AddObstructionUndoable implements Undoable, Serializable {

    private ObstructionList obstructionList;
    private Target obstruction;
    private int position;
    
    /** Creates new ObstructionUndoable */
    public AddObstructionUndoable(ObstructionList ol, Target obstruction, int position) {
        this.obstructionList = ol;
        this.obstruction = obstruction;
        this.position = position;
    }

    public void redo() {
        obstructionList.addObstruction(obstruction, position);
    }
    
    public void undo() {
        obstructionList.removeObstruction(position);
    }
    
    public String toString() {
        return "AddObstructionUndoable (" + obstruction.toString() + ", " + Integer.toString(position) + ")";
    }
    
}
