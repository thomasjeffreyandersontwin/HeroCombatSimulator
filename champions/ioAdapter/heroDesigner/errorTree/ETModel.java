/*
 * IETModel.java
 *
 * Created on September 27, 2007, 9:21 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.ioAdapter.heroDesigner.errorTree;

import treeTable.DefaultTreeTableModel;
import treeTable.TreeTableColumnModel;

/**
 *
 * @author twalker
 */
public class ETModel extends DefaultTreeTableModel {
    
    public static final int COLUMN_NAME = 0;
    public static final int MAX_COLUMNS = 1;
    
    protected String[] columnTitles = {"Error"};
    protected int[] columnDefaultWidths = { 250 };
    protected String title = "Character Import Errors";
    
    /** Creates a new instance of IETModel */
    public ETModel(ETNode root, String title) {
        super(root);
        this.title = title;
        root.setModel(this);
    }
    
    
    
       /**
     * Returns the number ofs availible column.
     */
    public int getColumnCount() {
        return MAX_COLUMNS;
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
        if ( column == 0) {
            if ( title == null || title.equals("")) {
                return columnTitles[column];
            }
        return title;
    }
        return columnTitles[column];
    }
    
    public int getColumnPreferredWidth(int column) {
        return columnDefaultWidths[column];
    }
    
    public ETNode getRoot() {
        return (ETNode)super.getRoot();
    }
    
}
