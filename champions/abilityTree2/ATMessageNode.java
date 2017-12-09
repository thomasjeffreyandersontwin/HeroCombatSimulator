/*
 * PADTargetNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.abilityTree2;

import tjava.Filter;
import javax.swing.UIManager;
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
public class ATMessageNode extends ATNode {
    
    protected String message;
    /** Creates new PADTargetNode */
//    public ATMessageNode() {
//        this("", null, false);
//    }
//    
//    public ATMessageNode(String message) {
//        this(message, null, false);
//    }
    
    public ATMessageNode(String message, ATNodeFactory nodeFactory, Filter<Object> nodeFilter, boolean pruned) {
        super(nodeFactory, nodeFilter, pruned);
        setup(message);
        
    }
    
    public void setup(String message) {
        this.message = message;
        
        setIcon( UIManager.getIcon("AbilityTree.rosterIcon"));
        
        
        buildNode();
        
    }
    
    public void buildNode() {
    }
    
    public Object getValueAt(int column) {
        if ( column == ATColumn.NAME_COLUMN.ordinal() ) {
            return message;
        }
        
        return null;
    }
    
    public int getColumnSpan(int column, TreeTableColumnModel columnModel) {
        if ( column == ATColumn.NAME_COLUMN.ordinal() ) {
            return ATColumn.MAX_COLUMNS.ordinal();
        }
        return 0;
    }

    
    public void destroy() {
        super.destroy();
        
        
        
    }
    
    
}
