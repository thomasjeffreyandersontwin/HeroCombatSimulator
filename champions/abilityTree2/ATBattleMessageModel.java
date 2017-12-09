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
public class ATBattleMessageModel extends ATModel {
    
    protected  static final int[] visibleColumns = {
        ATColumn.NAME_COLUMN.ordinal()
    };
    
    /** Creates a new instance of ATConfigureBattleModel */
    public ATBattleMessageModel(ATNode root) {
        super(root, "Messages");
    }

    public int[] getVisibleColumns() {
        return visibleColumns;
    }

    public String getColumnName(int column) {
        if ( ATColumn.NAME_COLUMN.ordinal() == column ) {
            return "Messages";
        }
        else {
            return super.getColumnName(column);
        }
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
        return isColumnAllowed(modelIndex);
    }

}
