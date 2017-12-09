/*
 * PADTargetNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.abilityTree2;

import champions.Battle;
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
public class ATStatNode extends ATNode {
    
    private Target target;
    private String stat;
    private boolean needsUpdate = false;
    /** Creates new PADTargetNode */
//    public ATStatNode() {
//        this(null, null, null, false);
//    }
//    
//    public ATStatNode(Target target, String stat) {
//        this(target, stat, null, false);
//    }
    
    public ATStatNode(Target target, String stat, ATNodeFactory nodeFactory, Filter<Object> nodeFilter, boolean pruned) {
        super(nodeFactory, nodeFilter, pruned);
        setup(target, stat);
        
    }
    
    public void setup(Target target, String stat) {
        this.setTarget(target);
        this.setStat(stat);
        
        buildNode();
        
    }
    
    public void buildNode() {
    }
    
    public Object getValueAt(int column) {
        if ( target != null && target.hasStat(stat)) {
        if ( column == ATColumn.NAME_COLUMN.ordinal() ) {
            
                return stat;
                
            }
        else {
            return target.getCurrentStat(stat);
        }
        }
        
        
        return null;
    }
    
    public int getColumnSpan(int modelIndex, TreeTableColumnModel columnModel) {
        
        if ( modelIndex == ATColumn.NAME_COLUMN.ordinal() ) {
            return 1;
        }
        else {
            int span = 1;
            int columnIndex = columnModel.getColumnIndex(modelIndex);
            if ( columnIndex != -1 ) {
                for(int i = columnIndex+1; i < columnModel.getColumnCount();i++) {
                    int mi = columnModel.getColumn(i).getModelIndex();
                    if (  mi == ATColumn.NAME_COLUMN.ordinal() ) {
                        break;
                    }
                    span++;
                }
            }
        
            return span;
        }
    }
    
    public void destroy() {
        super.destroy();
        
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        if( this.target != target ) {
            this.target = target;
            
            triggerUpdate();
        }
    }
    
    public void triggerUpdate() {
        if ( target != null && stat != null ) {
            if ( getModel() instanceof DefaultTreeTableModel && Battle.getCurrentBattle() != null && Battle.currentBattle.isProcessing() == false ) {
                ((DefaultTreeTableModel)getModel()).nodeChanged(this);
                needsUpdate = false;
            }
            else {
                needsUpdate = true;
            }
        }
    }

    public void updateTree() {
        triggerUpdate();
        
        super.updateTree();
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        if ( this.stat != stat ) {
            this.stat = stat;
            
            triggerUpdate();
        }
    }

    public boolean isCellEditable(int column) {
        if ( column == ATColumn.NAME_COLUMN.ordinal() ) {
            return false;
        }
        else {
            return true;
        }
    }

    public void setValueAt(int column, Object aValue) {
        if ( column != ATColumn.NAME_COLUMN.ordinal() ) {
            try {
                int newValue = 0;
                
                if ( aValue instanceof String ) {
                    newValue = Integer.parseInt((String)aValue);
                }
                else if ( aValue instanceof Integer ) {
                    newValue = ((Integer)aValue).intValue();
                }
                
                target.setCurrentStat(stat, newValue);
            } catch (NumberFormatException ex) {
            }
        }
    }
    

    
    
}
