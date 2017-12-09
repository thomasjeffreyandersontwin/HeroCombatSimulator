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
public class RemoveFadeTrackerUndoable implements Undoable, Serializable {

    private Target target;
    private FadeTracker tracker;
    /** Creates new AddFadeTrackerUndoable */
    public RemoveFadeTrackerUndoable(Target target, FadeTracker tracker) {
        this.target = target;
        this.tracker = tracker;
    }

    public void undo() {
        if ( target.findIndexed("FadeTracker", "FADETRACKER", tracker ) == -1 ) {
            target.createIndexed("FadeTracker", "FADETRACKER", tracker);
        }
    }
    
    public void redo() {
        int index = target.findIndexed("FadeTracker", "FADETRACKER", tracker );
        if ( index != -1 ) {
           target.removeAllIndexed(index, "FadeTracker");
        }
    }
    
}
