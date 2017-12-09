/*
 * PADTargetNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.abilityTree2;

import champions.Battle;
import champions.Effect;
import champions.EffectDetail;
import champions.EffectPanelTreeTableEditor;
import champions.EffectPanelTreeTableRenderer;
import tjava.Filter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import treeTable.DefaultTreeTableModel;
import treeTable.TreeTableCellEditor;
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
public class ATTargetEffectNode extends ATNode implements ActionListener {
    
    private List<Effect> effectList;
    private boolean needsUpdate = false;
    
    /** Creates new PADTargetNode */
//    public ATTargetEffectNode() {
//        this(null, null, false);
//    }
    
    
    public ATTargetEffectNode(List<Effect> effectList, ATNodeFactory nodeFactory, Filter<Object> nodeFilter, boolean pruned) {
        super(nodeFactory, nodeFilter, pruned);
        setup(effectList);
    }
    
    public void setup(List<Effect> effectList) {
        setEffectList(effectList);
        
        buildNode();
    }
    
    public void buildNode() {
    }
    
    public Object getValueAt(int column) {
        if ( column == ATColumn.NAME_COLUMN.ordinal() ) {
            return getEffectList();
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
        setEffectList(null);
        
        super.destroy();
        
    }

    public List<Effect> getEffectList() {
        return effectList;
    }

    public void setEffectList(List<Effect> effectList) {
        this.effectList = effectList;
    }
    
    public void triggerUpdate() {
        
        if ( getModel() instanceof DefaultTreeTableModel && Battle.getCurrentBattle() != null && Battle.currentBattle.isProcessing() == false ) {
            ((DefaultTreeTableModel)getModel()).nodeChanged(this);
            needsUpdate = false;
        }
        else {
            needsUpdate = true;
        }
    }
    
    public void updateTree() {
        if ( needsUpdate ) triggerUpdate();
        
        super.updateTree();
    }
    
    public TreeTableCellRenderer getTreeTableCellRenderer(int columnIndex) {
        if ( columnIndex == ATColumn.NAME_COLUMN.ordinal() ) {
            EffectPanelTreeTableRenderer r = EffectPanelTreeTableRenderer.getDefaultRenderer();
            r.setMultiline(true);
            r.setTitle("Effects: ");
            return r;
        }
        
        return null;
    }

    public TreeTableCellEditor getTreeTableCellEditor(int columnIndex) {
        if ( columnIndex == ATColumn.NAME_COLUMN.ordinal() ) {
            EffectPanelTreeTableEditor e =  EffectPanelTreeTableEditor.getDefaultEditor();
            e.setMultiline(true);
            e.setTitle("Effects: ");
            return e;
        }
        
        return null;
    }
    
    public boolean isCellEditable(int column) {
        if ( effectList == null ) return false;
        
        ATColumn c = ATColumn.values()[column];
        
        switch(c) {
            case NAME_COLUMN:
                return true;
        }
        
        return false;
    }
    
    public void actionPerformed(ActionEvent e) {
        if ( e.getSource() instanceof Effect ) {
            Effect effect = (Effect)e.getSource();
            EffectDetail ed = new EffectDetail(effect);
            ed.showEffectDetail(null);
        }
    }

    public int getPreferredOrder() {
        return -2;
    }
    

}
