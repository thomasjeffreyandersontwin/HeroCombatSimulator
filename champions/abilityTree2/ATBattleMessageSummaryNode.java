/*
 * PADTargetNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.abilityTree2;

import champions.ChampionsUtilities;
import champions.Target;
import champions.battleMessage.BattleMessage;
import champions.battleMessage.BattleMessageGroup;
import champions.battleMessage.SummaryMessage;
import champions.battleMessage.SummaryMessageGroup;
import tjava.Filter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;
import treeTable.DefaultTreeTableCellRenderer;
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
public class ATBattleMessageSummaryNode extends ATNode {
    
    protected BattleMessage battleMessage;
    private static final DefaultTreeTableCellRenderer renderer = new DefaultTreeTableCellRenderer();
    /** Creates new PADTargetNode */
//    public ATMessageNode() {
//        this("", null, false);
//    }
//
//    public ATMessageNode(String message) {
//        this(message, null, false);
//    }
    
    public ATBattleMessageSummaryNode(BattleMessage battleMessage, Filter<Object> nodeFilter) {
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
            
            List<SummaryMessage> summaryMessages = bmg.getSummaryOfEffects();
            if ( summaryMessages != null ) {
                List<Target> affectedTargets = new ArrayList<Target>();
                for(SummaryMessage sm : summaryMessages) {
                    if ( affectedTargets.contains(sm.getTarget() ) == false ) {
                        affectedTargets.add( sm.getTarget() );
                    }
                }
                
                for( Target target : affectedTargets ) {
                    List<BattleMessage> relevantBattleMessages = bmg.getSummaryChildren(target);
                    if ( relevantBattleMessages != null && relevantBattleMessages.size() > 0) {
                        SummaryMessageGroup smg = new SummaryMessageGroup(target);
                        for(BattleMessage bm : relevantBattleMessages) {
                            smg.addMessage(bm);
                        }
                        ATBattleMessageNode node = new ATBattleMessageNode(smg, nodeFilter);
                        add(node);
                    }
                }
            }
        }
    }
    
    public Object getValueAt(int column) {
        if ( column == ATColumn.NAME_COLUMN.ordinal() ) {
            return ChampionsUtilities.createHTMLString("Summary");
        }
        
        return null;
    }
    
    public int getColumnSpan(int column, TreeTableColumnModel columnModel) {
        if ( column == ATColumn.NAME_COLUMN.ordinal() ) {
            return ATColumn.MAX_COLUMNS.ordinal();
        }
        return 1;
    }
    
    public TreeTableCellRenderer getTreeTableCellRenderer(int columnIndex) {
        return renderer;
    }
    
    
    
    
    public void destroy() {
        super.destroy();
        
    }
    
    
}
