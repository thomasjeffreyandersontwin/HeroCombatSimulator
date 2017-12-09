/*
 * AddFadeTrackerUndoable.java
 *
 * Created on March 7, 2002, 12:29 PM
 */

package champions;

import champions.interfaces.Undoable;
import java.io.Serializable;

/**
 *
 * @author  Trevor Walker
 * @version 
 */
public class AddFadeTrackerUndoable implements Undoable, Serializable {

    private Target target;
    private FadeTracker tracker;
    /** Creates new AddFadeTrackerUndoable */
    public AddFadeTrackerUndoable(Target target, FadeTracker tracker) {
        this.target = target;
        this.tracker = tracker;
    }

    public void redo() {
        if ( target.findIndexed("FadeTracker", "FADETRACKER", tracker ) == -1 ) {
            target.createIndexed("FadeTracker", "FADETRACKER", tracker);
        }
    }
    
    public void undo() {
        int index = target.findIndexed("FadeTracker", "FADETRACKER", tracker );
        if ( index != -1 ) {
           target.removeAllIndexed(index, "FadeTracker");
        }
    }
    
}
