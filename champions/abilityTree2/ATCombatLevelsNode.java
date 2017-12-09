/*
 * PADCombatLevelListNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.abilityTree2;

import champions.CombatLevelList;
import champions.CombatLevelListEntry;
import tjava.Filter;
import javax.swing.UIManager;
import treeTable.DefaultTreeTableModel;
import treeTable.TreeTableColumnModel;



/**
 *
 * @author  twalker
 * @version
 */
public class ATCombatLevelsNode extends ATNode {

    private CombatLevelList combatLevelsList = null;
    
    public ATCombatLevelsNode(CombatLevelList combatLevelList, ATNodeFactory nodeFactory, Filter<Object> nodeFilter, boolean pruned) {
        super(nodeFactory, nodeFilter, pruned);
        setup(combatLevelList);
        
    }
    
    public void setup(CombatLevelList combatLevelsList) {
        this.setPruned(pruned);
        this.setCombatLevelsList(combatLevelsList);
        
        setIcon( UIManager.getIcon("AbilityTree.rosterIcon"));
        
        buildNode();
    }
    
    public void buildNode() {
        removeAndDestroyAllChildren();
        
        if ( getCombatLevelsList() != null ) {
            for (CombatLevelListEntry combatLevelEntry : combatLevelsList) {
                ATCombatLevelNode node = new ATCombatLevelNode(combatLevelEntry, nodeFactory, nodeFilter,pruned);
                add(node);
            }
            
            if ( model instanceof DefaultTreeTableModel ) {
                ((DefaultTreeTableModel)model).nodeStructureChanged(this);
            }
        }
    }

    public int getColumnSpan(int column, TreeTableColumnModel columnModel) {
        if ( column == ATColumn.NAME_COLUMN.ordinal() ) {
            return ATColumn.MAX_COLUMNS.ordinal();
        }
        return 0;
    }
   
    public void destroy() {
        super.destroy();
        
        setCombatLevelsList(null);
    }

    /**
     * @return the combatLevelList
     */
    public CombatLevelList getCombatLevelsList() {
        return combatLevelsList;
    }

    /**
     * @param combatLevelList the combatLevelList to set
     */
    public void setCombatLevelsList(CombatLevelList combatLevelsList) {
        if ( this.combatLevelsList != combatLevelsList ) {
            this.combatLevelsList = combatLevelsList;
            buildNode();
        }
    }

    
     
}
