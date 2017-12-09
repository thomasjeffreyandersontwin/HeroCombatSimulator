/*
 * EffectPanelTreeTableRenderer.java
 *
 * Created on January 29, 2008, 11:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions;

import champions.abilityTree2.ATTargetEffectNode;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;
import treeTable.TreeTable;
import treeTable.TreeTableCellRenderer;
import treeTable.TreeTableNode;
import treeTable.TreeTablePreferredHeightProvider;

/**
 *
 * @author twalker
 */
public class EffectPanelTreeTableRenderer extends EffectPanel2 implements TreeTableCellRenderer, TreeTablePreferredHeightProvider {
    
    protected static EffectPanelTreeTableRenderer defaultRenderer = null;
    
    protected TreeTable treeTable;
    protected int row;
    protected int column;
    protected int span;
    
    /** Creates a new instance of EffectPanelTreeTableRenderer */
    public EffectPanelTreeTableRenderer() {
    }
    
    public static EffectPanelTreeTableRenderer getDefaultRenderer() {
        if ( defaultRenderer == null ) defaultRenderer = new EffectPanelTreeTableRenderer();
        
        defaultRenderer.setMultiline(false);
        defaultRenderer.setTitle(null);
        
        return defaultRenderer;
    }

    public Component getTreeTableCellRendererComponent(TreeTable treeTable, Object node, boolean isSelected, boolean expanded, boolean leaf, int row, int column, boolean hasFocus) {
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
        
        return this;
    }

    public Dimension getPreferredSize() {
        if ( treeTable != null ) {
            Graphics2D g2 = (Graphics2D)treeTable.getGraphics();
            int columnWidth = treeTable.getColumnWidth(treeTable.convertColumnIndexToView(column),span);
            Dimension size = getPreferredSize(g2,columnWidth);
            return size;
        }
        
        return null;
        
    }

    public int getPreferredHeight(int preferredWidth, TreeTable treeTable, Object node, boolean isSelected, boolean expanded, boolean leaf, int row, int column, boolean hasFocus) {
        if ( treeTable != null ) {
            getTreeTableCellRendererComponent(treeTable,node,isSelected,expanded,leaf,row,column,hasFocus);
            Graphics2D g2 = (Graphics2D)treeTable.getGraphics();
            Dimension size = getPreferredSize(g2,preferredWidth);
            return size.height;
        }
        
        return 15;
    }
    

    
}
