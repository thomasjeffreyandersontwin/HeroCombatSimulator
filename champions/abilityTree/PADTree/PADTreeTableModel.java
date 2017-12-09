/*
 * PADTreeModel.java
 *
 * Created on February 27, 2002, 11:11 PM
 */

package champions.abilityTree.PADTree;

import treeTable.*;
/**
 *
 * @author  twalker
 * @version 
 */
public class PADTreeTableModel extends DefaultTreeTableModel {

    /** Title of column */
    private String title;
    
    /** Creates new PADTreeModel */
    public PADTreeTableModel(PADTreeTableNode root, String title) {
        super(root);
        this.title = title;
        root.setModel(this);

        setLegacyDnDSupport(true);
    }
    
    /**
     * Returns the number ofs availible column.
     */
    public int getColumnCount() {
        return 1;
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
        return title;
    }


}
