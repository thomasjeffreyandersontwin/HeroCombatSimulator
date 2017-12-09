/*
 * valuePairUndoable.java
 *
 * Created on March 26, 2003, 1:06 PM
 */

package champions;

import champions.interfaces.Undoable;
import java.io.Serializable;

/**
 *
 * @author  Trevor Walker
 */
public class ValuePairUndoable implements Undoable, Serializable {
    private DetailList detailList;
    private String key;
    private Object oldValue;
    private Object newValue;
    
    /** Creates a new instance of valuePairUndoable */
    public ValuePairUndoable(DetailList dl, String key, Object newValue) {
        detailList = dl;
        this.key = key;
        newValue = newValue;
        oldValue = dl.getValue(key);
    }
    
    /** Creates a new instance of valuePairUndoable */
    public ValuePairUndoable(DetailList dl, String key, Object oldValue, Object newValue) {
        detailList = dl;
        this.key = key;
        newValue = newValue;
        oldValue = oldValue;
    }
    
    public void redo() {
        if ( newValue == null ) {
            detailList.remove(key);
        }
        else {
            detailList.add(key, newValue, true);
        }
    }
    
    public void undo() {
        if ( oldValue == null ) {
            detailList.remove(key);
        }
        else {
            detailList.add(key, oldValue, true);
        }
    }
    
}
