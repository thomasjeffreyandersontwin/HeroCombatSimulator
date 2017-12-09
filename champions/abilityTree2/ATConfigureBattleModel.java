/*
 * ATConfigureBattleModel.java
 *
 * Created on November 21, 2007, 7:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.abilityTree2;

import champions.attackTree.ConfigureBattleActivationList;
import javax.swing.tree.TreeNode;

/**
 *
 * @author twalker
 */
@SuppressWarnings("serial")
public class ATConfigureBattleModel extends ATModel {
    
    protected  static final int[] visibleColumns = {
        ATColumn.NAME_COLUMN.ordinal(),
        ATColumn.LAUNCH_COLUMN.ordinal(),
        ATColumn.AUTO_ACTIVATE_COLUMN.ordinal()
    };
    
    private ConfigureBattleActivationList activationList;
    private boolean startOfBattle;
    
    /** Creates a new instance of ATConfigureBattleModel */
    public ATConfigureBattleModel(ATNode root, ConfigureBattleActivationList activationList, boolean startOfBattle) {
        super(root, "Name");
        
        setActivationList(activationList);
        setStartOfBattle(startOfBattle);
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

    public boolean isCellEditable(ATNode node, int column) {
        return ( column == ATColumn.LAUNCH_COLUMN.ordinal() ) || super.isCellEditable(node,column);
    }

    public boolean isColumnRequired(int modelIndex) {
        // Require the name column to be shown...
        return modelIndex == ATColumn.NAME_COLUMN.ordinal();
    }

    public ConfigureBattleActivationList getActivationList() {
        return activationList;
    }

    public void setActivationList(ConfigureBattleActivationList activationList) {
        this.activationList = activationList;
    }

    public boolean isStartOfBattle() {
        return startOfBattle;
    }

    public void setStartOfBattle(boolean startOfBattle) {
        this.startOfBattle = startOfBattle;
    }
    

    
}
