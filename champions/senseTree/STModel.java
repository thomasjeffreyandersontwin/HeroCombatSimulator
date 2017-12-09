/*
 * PADTreeModel.java
 *
 * Created on February 27, 2002, 11:11 PM
 */

package champions.senseTree;

import treeTable.*;
/**
 *
 * @author  twalker
 * @version 
 */
public class STModel extends DefaultTreeTableModel {
   /** Title of columns */
    protected static String[] columnTitles = {"Sense/Group", "Ranged", "Targetting", "Adjustment", "Negative Modifiers"};
    
    public static final int NAME_COLUMN = 0;
    public static final int RANGED_COLUMN = 1;
    public static final int TARGETTING_COLUMN = 2;
    public static final int PERCEPTION_MODIFIER_COLUMN = 3;
    public static final int PENALTY_COLUMN = 4;
    public static final int MAX_COLUMNS = columnTitles.length;
    
    
    protected String title = columnTitles[0];
    
    protected int visibleColumns = 0xFFFFFFFF; 
     
    /** Determines the capabilities of the tree. */
    protected boolean simpleTree = false;
    
    /** Current Number of columns. */
    protected int columnCount;
    
    /** Creates new PADTreeModel
     * @param root
     */
    public STModel(STNode root) {
        super(root);
        root.setModel(this);
        root.buildChildren();
        
        update();
    }
    
    /**
     * Returns the number ofs availible column.
     */
    @Override
    public int getColumnCount() {
        return columnCount;
    }
    
    /**
     * Returns the name for column number <code>column</code>.
     */
    @Override
    public String getColumnName(int column) {
        if ( column == 0) {
            return title;
        }
        return columnTitles[column];
    }
    
    @Override
     public int getColumnPreferredWidth(int column) {
        switch(column) {
            case NAME_COLUMN:
                return 180;
            case PENALTY_COLUMN:
                return 400;
            default:
                return 25;
        }
    }

    @Override
    public int getColumnMinimumWidth(int column) {
        switch(column) {
            case NAME_COLUMN:
                return 180;
            default:
                return -1;
        }
    }
    
    /** Changes the visible status on the columns. 
     *
     * @param column
     * @param visible
     */
    public void setColumnVisible(int column, boolean visible) {
        boolean update = false;
        if ( visible  ) {
            int mask = getColumnMask(column);
            
            int oldVisible = visibleColumns;
            visibleColumns = visibleColumns | mask;
            
            if ( oldVisible != visibleColumns ) {
                update = true;
            }
        }
        else {
             int mask = getColumnMask(column);
            
            int oldVisible = visibleColumns;
            visibleColumns = visibleColumns & (~mask);
            
            if ( oldVisible != visibleColumns ) {
                update = true;
            }           
        }
        
        if ( update ) {
            
        }
    }
    
    protected void update() {
        columnCount = countVisibleColumns();
    }
    
    public boolean isColumnVisible(int column) {
        return ((visibleColumns & getColumnMask(column)) != 0);
    }
    
    private int getColumnMask(int column) {
        return (int)Math.pow(2,column);
    }
    
    private int countVisibleColumns() {
        int count = 0;
        for(int i = 0; i < MAX_COLUMNS; i++) {
            if ( isColumnVisible(i) ) count ++;
        }
        return count;
    }
}
