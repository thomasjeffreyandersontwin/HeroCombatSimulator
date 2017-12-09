/*
 * SimpleTreeTableNode.java
 *
 * Created on February 17, 2002, 4:47 PM
 */

package treeTable;

import javax.swing.*;
import javax.swing.tree.*;
/**
 *
 * @author  twalker
 * @version 
 */
public class SimpleTreeTableNode extends DefaultMutableTreeNode {
    protected Object[] data;
    
    /** Creates new SimpleTreeTableNode */
    public SimpleTreeTableNode(Object[] data) {
        this.data = data;
    }
    
    public Object getColumnValue(int index) {
        return ( index < data.length ) ? data[index] : null;
    }
    
    public void setColumnValue(int index, Object value) {
        if ( index < data.length ) {
            data[index] = value;
        }
    }
    
    public int getColumnCount() {
        return data.length;
    }
    
    public String toString() {
        return "[" +  data[0] + "]";
    }

}
