/*
 * PADTargetNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.abilityTree2;

import champions.Target;
import champions.battleMessage.BattleMessage;
import champions.battleMessage.SummaryMessage;
import tjava.Filter;
import java.util.ArrayList;
import java.util.List;
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
public class ATBattleMessageRootNode extends ATNode {
    
    protected BattleMessage battleMessage;
    /** Creates new PADTargetNode */
//    public ATMessageNode() {
//        this("", null, false);
//    }
//    
//    public ATMessageNode(String message) {
//        this(message, null, false);
//    }
    
    public ATBattleMessageRootNode(BattleMessage battleMessage, Filter<Object> nodeFilter) {
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
        
        ATNode node;
        
        node = new ATBattleMessageNode(battleMessage, nodeFilter);
        add(node);
        
        ATNode summaryNode = new ATBattleMessageSummaryNode(battleMessage, nodeFilter);
        if ( summaryNode.getChildCount() > 0 ) {
            node = new ATBlankLineNode();
            add(node);
        
            add(summaryNode);
        }
        else {
            summaryNode.destroy();
        }
        
    }
    
    public Object getValueAt(int column) {
        if ( column == ATColumn.NAME_COLUMN.ordinal() ) {
            return "Battle Message Root Node (You should never see this)";
        }
        
        return null;
    }
    
    public int getColumnSpan(int column, TreeTableColumnModel columnModel) {
        if ( column == ATColumn.NAME_COLUMN.ordinal() ) {
            return ATColumn.MAX_COLUMNS.ordinal();
        }
        return 1;
    }

    
    public void destroy() {
        super.destroy();
        
    }
    
    
}
