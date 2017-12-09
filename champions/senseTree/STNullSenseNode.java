/*
 * STNullSenseNode.java
 *
 * Created on August 18, 2006, 6:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.senseTree;

import javax.swing.Icon;
import javax.swing.UIManager;
import treeTable.TreeTable;

/**
 *
 * @author 1425
 */
public class STNullSenseNode extends STNode {
    
    private static Icon falseIcon;
    
    /** Creates a new instance of STNullSenseNode */
    public STNullSenseNode() {
       setupIcons();
    }
    
    protected void setupIcons() {
        if ( falseIcon == null ) falseIcon = UIManager.getIcon("SenseTree.falseIcon");
    }
    
    public Object getValueAt(int column) {
        switch (column) {
            case STModel.NAME_COLUMN:
                return "None";
                
        }
        
        return null;
    }
    
    public Icon getIcon(TreeTable treeTable, boolean isSelected, boolean expanded, boolean leaf, int row) {
        return falseIcon;
    }
}
