/*
 * PADTreeModel.java
 *
 * Created on February 27, 2002, 11:11 PM
 */

package champions.attackTree.sweepTree;

import treeTable.*;
/**
 *
 * @author  twalker
 * @version 
 */
public class SWTModel extends DefaultTreeTableModel {

    /** Title of column */
    private String title;
    
    /** Creates new PADTreeModel */
    public SWTModel(SWTNode root, String title) {
        super(root);
        this.title = title;
        root.setModel(this);
    }
    
    /**
     * Returns the number ofs availible column.
     */
    public int getColumnCount() {
        return 2;
    }

    /**
     * Returns the number of columns the <code>column</code> should span.
     */
    public int getColumnSpan(Object node, int column, TreeTableColumnModel columnModel) {
        return 1;
    }
    
    /**
     * Returns the name for column number <code>column</code>.
     */
    public String getColumnName(int column) {
        switch ( column ) {
            case 1:
                return "#";
            case 0:
                return title;
        }
        return title;
    }
}
