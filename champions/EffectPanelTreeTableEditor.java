/*
 * EffectPanelTreeTableRenderer.java
 *
 * Created on January 29, 2008, 11:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.List;
import javax.swing.event.CellEditorListener;
import treeTable.TreeTable;
import treeTable.TreeTableCellEditor;
import treeTable.TreeTableNode;

/**
 *
 * @author twalker
 */
public class EffectPanelTreeTableEditor extends EffectPanelTreeTableRenderer implements TreeTableCellEditor, ActionListener {
    
    protected static EffectPanelTreeTableEditor defaultEditor = null;
    
    protected ActionListener listener = null;
    
    /** Creates a new instance of EffectPanelTreeTableRenderer */
    public EffectPanelTreeTableEditor() {
        addActionListener(this);
    }
    
    public static EffectPanelTreeTableEditor getDefaultEditor() {
        if ( defaultEditor == null ) defaultEditor = new EffectPanelTreeTableEditor();
        
        defaultEditor.setMultiline(false);
        defaultEditor.setTitle(null);
        
        return defaultEditor;
    }

     public Component getTreeTableCellEditorComponent(TreeTable treeTable, Object node, boolean isSelected, boolean expanded, boolean leaf, int row, int column) {
        List<Effect> effects = null;
        
        this.treeTable = treeTable;
        this.row = row;
        this.column = column;
        this.span = 1;
        
        if ( node instanceof TreeTableNode ) {
            Object v = ((TreeTableNode)node).getValueAt(column);
            if ( v instanceof List ) {
                effects = (List<Effect>)v;
            }
            
            setForeground(treeTable.getForeground());
            setFont(treeTable.getFont());
            
            span = ((TreeTableNode)node).getColumnSpan(column, treeTable.getTreeTableHeader().getColumnModel());
        }
            
        setEffectList(effects);
        
        if ( node instanceof ActionListener ) {
            listener = (ActionListener)node;
        }
        else {
            listener = null;
        }
        
        return this;
    }


    public boolean canEditImmediately(EventObject event, TreeTable treeTable, Object node, int row, int column, int offset) {
        return true;
    }

    public void selectionStateChanged(boolean isSelected) {
    }

    public Object getCellEditorValue() {
        return null;
    }

    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    public boolean shouldSelectCell(EventObject anEvent) {
        return false;
    }

    public boolean stopCellEditing() {
        return true;
    }

    public void cancelCellEditing() {
    }

    public void addCellEditorListener(CellEditorListener l) {
    }

    public void removeCellEditorListener(CellEditorListener l) {
    }

    public void actionPerformed(ActionEvent e) {

        if ( treeTable != null ) {
            treeTable.stopEditing();
        }
        
        if ( listener != null ) {
            listener.actionPerformed(e);
        }
        
    }
    
}
