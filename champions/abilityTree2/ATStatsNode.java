/*
 * PADTargetNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.abilityTree2;

import champions.CharacteristicPrimary;
import champions.Target;
import tjava.Filter;
import treeTable.DefaultTreeTableModel;
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
public class ATStatsNode extends ATNode {
    
    private Target target;
    private boolean built;
//    /** Creates new PADTargetNode */
//    public ATStatsNode() {
//        this(null, null, false);
//    }
//    
//    public ATStatsNode(Target target) {
//        this(target, null, false);
//    }
    
    public ATStatsNode(Target target, ATNodeFactory nodeFactory, Filter<Object> nodeFilter, boolean pruned) {
        super(nodeFactory, nodeFilter, pruned);
        setup(target);
        
    }
    
    public void setup(Target target) {
        this.setTarget(target);
        
        setExpandedByDefault(false);
    }
    
    public void buildNode() {
        removeAndDestroyAllChildren();
        
        if ( target != null ) {
            for(String stat : CharacteristicPrimary.characteristicNames) {
                ATNode sn = nodeFactory.createStatNode(target, stat, nodeFilter, isPruned());
                if ( sn != null ) {
                    add(sn);
                }
            }
        }
        
        //sortNode(false);
        
        built = true;
        
        if ( getModel() instanceof DefaultTreeTableModel ) {
            ((DefaultTreeTableModel)getModel()).nodeStructureChanged(this);
        }
    }
    
    public Object getValueAt(int column) {
        if ( column == ATColumn.NAME_COLUMN.ordinal() ) {
            return "Stats";
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
        setTarget(null);
        
        super.destroy();
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        if( this.target != target ) {
            this.target = target;
            
            rebuildNode();
        }
    }

    public void nodeWillExpand() {
        if ( built == false ) {
            buildNode();
        }
    }

    public boolean rebuildNode() {
        if ( built == true ) return super.rebuildNode();
        
        return false;
    }

    public boolean isLeaf() {
        return false;
    }
    


    

}
