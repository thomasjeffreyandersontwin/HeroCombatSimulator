/*
 * CreateIndexedUndoable.java
 *
 * Created on August 5, 2004, 3:49 PM
 */

package champions;

import champions.interfaces.Undoable;
import java.io.Serializable;
/**
 *
 * @author  1425
 */
public class CreateIndexedUndoable implements Undoable, Serializable {
    
    protected DetailList detailList;
    protected String indexName;
    protected String indexType;
    protected Object value;
    protected int index;
    
    /** Creates a new instance of CreateIndexedUndoable */
    public CreateIndexedUndoable(DetailList detailList, int index, String indexName, String indexType, Object value) {
        this.detailList = detailList;
        this.indexName = indexName;
        this.indexType = indexType;
        this.value = value;
        this.index = index;
    }
    
    public void redo() {
        index = detailList.createIndexed(indexName, indexType, value);
    }
    
    public void undo() {
        detailList.removeAllIndexed(index, indexName);
    }
    
}
