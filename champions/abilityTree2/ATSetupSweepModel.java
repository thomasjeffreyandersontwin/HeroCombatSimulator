/*
 * ATConfigureBattleModel.java
 *
 * Created on November 21, 2007, 7:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.abilityTree2;

/**
 *
 * @author twalker
 */
public class ATSetupSweepModel extends ATModel {
    
    protected  static final int[] visibleColumns = {
        ATColumn.NAME_COLUMN.ordinal(),
        ATColumn.END_COLUMN.ordinal(),
        ATColumn.OCV_COLUMN.ordinal(),
        ATColumn.DCV_COLUMN.ordinal()
    };
    
    /** Creates a new instance of ATConfigureBattleModel */
    public ATSetupSweepModel(ATNode root) {
        super(root, "Name");
    }

    public int[] getVisibleColumns() {
        return visibleColumns;
    }

    public boolean isColumnAllowed(int modelIndex) {
        // Allow only the columns in visibleColumns...
        for(int i : visibleColumns) {
            if ( modelIndex == i ) return true;
        }
        
        return false;
    }


    public boolean isColumnRequired(int modelIndex) {
        // Require the name column to be shown...
        return modelIndex == ATColumn.NAME_COLUMN.ordinal();
    }

//    public TreeTableCellRenderer getCellRenderer(Object node, int column) {
//        if ( ATColumn.NAME_COLUMN.ordinal() == column ) {
//            return null;
//        }
//        else {
//            return super.getCellRenderer(node,column);
//        }
//    }
//
//    public TreeTableCellEditor getCellEditor(Object node, int column) {
//        if ( ATColumn.NAME_COLUMN.ordinal() == column ) {
//            return null;
//        }
//        else {
//            return super.getCellEditor(node,column);
//        }
//    }
    


}
