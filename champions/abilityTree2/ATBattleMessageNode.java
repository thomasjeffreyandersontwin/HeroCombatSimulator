/*
 * PADTargetNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.abilityTree2;

import champions.ChampionsUtilities;
import champions.battleMessage.BattleMessage;
import champions.battleMessage.BattleMessageGroup;
import champions.battleMessage.SummaryMessageGroup;
import tjava.Filter;
import javax.swing.Icon;
import javax.swing.table.DefaultTableCellRenderer;
import treeTable.DefaultTreeTableCellRenderer;
import treeTable.TreeTable;
import treeTable.TreeTableCellRenderer;
import treeTable.TreeTableColumnModel;

/**
 *
 * @author  twalker
 * @version
 *
 * The ATTargetNode is the root node for a list of all possible abilities
 * and disadvantages that can be used by the target.  This includes the
 * abilities owned by the target and the default abilities.
 *
 * The ATTargetNode2 should be used in situations where listing of the abilities
 * of a target, not including the default abilities, and any target specific
 * actions are needed.
 */
public class ATBattleMessageNode extends ATNode {
    
    protected BattleMessage battleMessage;
    
    protected static TreeTableCellRenderer renderer = new DefaultTreeTableCellRenderer();
    /** Creates new PADTargetNode */
//    public ATMessageNode() {
//        this("", null, false);
//    }
//    
//    public ATMessageNode(String message) {
//        this(message, null, false);
//    }
    
    public ATBattleMessageNode(BattleMessage battleMessage, Filter<Object> nodeFilter) {
        super(null, nodeFilter, false);
        setup(battleMessage);
        
    }
    
    public void setup(BattleMessage battleMessage) {
        this.battleMessage = battleMessage;
        
        //setIcon( UIManager.getIcon("AbilityTree.rosterIcon"));
        
        
        buildNode();
        
    }
    
    public void buildNode() {
        removeAndDestroyAllChildren();
        
        if ( battleMessage instanceof BattleMessageGroup ) {
            BattleMessageGroup bmg = (BattleMessageGroup)battleMessage;
            
            for(int i = 0; i < bmg.getChildCount(); i++) {
                BattleMessage bm = bmg.getChild(i);
                ATNode node = new ATBattleMessageNode(bm, nodeFilter);
                add(node);
            }
        }
    }
    
    public Object getValueAt(int column) {
        if ( column == ATColumn.NAME_COLUMN.ordinal() && battleMessage != null) {
            return ChampionsUtilities.createHTMLString(battleMessage.getMessage());
        }
        
        return null;
    }
    
    public int getColumnSpan(int column, TreeTableColumnModel columnModel) {
        if ( column == ATColumn.NAME_COLUMN.ordinal() ) {
            return ATColumn.MAX_COLUMNS.ordinal();
        }
        return 1;
    }

    public Icon getIcon(TreeTable treeTable, boolean isSelected, boolean expanded, boolean leaf, int row) {
        if ( battleMessage != null ) {
            return battleMessage.getMessageIcon();
        }
        else return null;
    }

    public TreeTableCellRenderer getTreeTableCellRenderer(int columnIndex) {
        return renderer;
    }
    
    public void destroy() {
        super.destroy();
        
    }

    public boolean isExpandedByDefault() {
        if ( battleMessage instanceof BattleMessageGroup ) {
            return ((BattleMessageGroup)battleMessage).isExpandedByDefault();
        }
        else {
            return false;
        }
    }
    

    
    
}
