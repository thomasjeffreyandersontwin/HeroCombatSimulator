/*
 * SimpleTreeTableModel.java
 *
 * Created on February 17, 2002, 4:53 PM
 */

package treeTable;

import javax.swing.Icon;


/**
 *
 * @author  twalker
 * @version 
 */
public class SimpleTreeTableModel extends DefaultTreeTableModel{

    protected String[] columnNames = { "Node", "Letter", "Number" };
    /** Creates new SimpleTreeTableModel */
    public SimpleTreeTableModel(SimpleTreeTableNode root) {
        super(root);
    }

    /**
     * Returns the value to be displayed for node <code>node</code>,
     * at column number <code>column</code>.
     */
    public Object getValueAt(Object node, int column) {
        if ( node instanceof SimpleTreeTableNode ) {
            return ((SimpleTreeTableNode)node).getColumnValue(column);
        }
        else {
            return null;
        }
    }
    
    /**
     * Returns the type for column number <code>column</code>.
     */
    public Class getColumnClass(int column) {
        return ((SimpleTreeTableNode)root).getColumnValue(column).getClass();
    }
    
    public Icon getIcon(Object node) {
        return null;
    }
    
    public int getColumnCount() {
        return ((SimpleTreeTableNode)root).getColumnCount();
    }
    
    public String getColumnName(int index) {
        return columnNames[index];
    }
    
    /** Returns the renderer to be used for this node and column.
     */
    public TreeTableCellRenderer getCellRenderer(Object node, int column) {
            return null;
    }
    
    /** Returns the editor to be used for this node and column.
     */
    public TreeTableCellEditor getCellEditor(Object node, int column) {
        return null;
    }
    
    public boolean isCellEditable(Object node, int column) {
        return true;
    }
    
    public void setValueAt(Object node, int column, Object value) {
        //System.out.println("SimpleTreeTableModel.setValueAt(" + node + ", " + value + ", " + Integer.toString(column) +")");
        
        SimpleTreeTableNode sttn = (SimpleTreeTableNode)node;
        sttn.setColumnValue(column, value);
    }
    
}
