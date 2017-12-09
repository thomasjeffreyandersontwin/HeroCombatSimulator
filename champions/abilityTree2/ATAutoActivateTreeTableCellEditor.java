/*
 * IconButtonTreeTableCellEditor.java
 *
 * Created on September 18, 2007, 10:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.abilityTree2;

import tjava.ContextMenu;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.ref.WeakReference; 
import java.util.EventObject;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import treeTable.TreeTable;
import treeTable.TreeTableCellEditor;
import treeTable.TreeTableCellRenderer;

/**
 *
 * @author twalker
 */
public class ATAutoActivateTreeTableCellEditor extends JLabel implements TreeTableCellEditor, TreeTableCellRenderer, MouseListener {
    
    private WeakReference<ATNode> editorNode;
    private WeakReference<ATNode> rendererNode;
    
    private Icon activateOnIcon = null;
    private Icon activateOffIcon = null;
    
    /** Holds the tree table the editor current is in. */
    private TreeTable editorTree;
    
    /** Creates a new instance of IconButtonTreeTableCellEditor */
    public ATAutoActivateTreeTableCellEditor() {
        addMouseListener(this);
        
        if ( activateOnIcon == null ) activateOnIcon = UIManager.getIcon("AbilityButton.autoActivateOnIcon");
        if ( activateOffIcon == null ) activateOffIcon = UIManager.getIcon("AbilityButton.autoActivateOffIcon");
        
        ContextMenu.addContextMenu(this);
    }
    
    public Component getTreeTableCellRendererComponent(TreeTable treeTable, Object node, boolean isSelected, boolean expanded, boolean leaf, int row, int column, boolean hasFocus) {

        
        if ( node instanceof ATNode ) {
            ATNode rn = (ATNode)node;
            this.setRendererNode(rn);
            
            Boolean value = (Boolean)rn.getValueAt(column);
            
            if ( value == null || value == false ) {
                setIcon(activateOffIcon);
            }
            else {
                setIcon(activateOnIcon);
            }
        }
        
        return this;
    }
    
    public Component getTreeTableCellEditorComponent(TreeTable treeTable, Object node, boolean isSelected, boolean expanded, boolean leaf, int row, int column) {
        
        editorTree = treeTable;
        
        if ( node instanceof ATNode ) {
            ATNode en = (ATNode)node;
            this.setEditorNode(en);
            Boolean value = (Boolean)en.getValueAt(column);
            
            if ( value == null || value == false ) {
                setIcon(activateOffIcon);
            }
            else {
                setIcon(activateOnIcon);
            }
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
    
    public void mouseClicked(MouseEvent e) {
        
    }
    
    public void mousePressed(MouseEvent e) {
    }
    
    public void mouseReleased(MouseEvent e) {
        //if ( e.getClickCount() == 1 ) {
            ATNode oldNode = getEditorNode();
            if ( editorTree != null ) {
                if ( ! editorTree.stopEditing() ) {
                    editorTree.cancelEditing();
                }
            }
            if ( oldNode instanceof ATAbilityNode ) ((ATAbilityNode)oldNode).toggleAutoActivate();
       // }
    }
    
    public void mouseEntered(MouseEvent e) {
    }
    
    public void mouseExited(MouseEvent e) {
    }

    public ATNode getEditorNode() {
        if ( editorNode == null ) return null;
        return editorNode.get();
    }

    public void setEditorNode(ATNode editorNode) {
        if ( editorNode == null ) this.editorNode = null;
        this.editorNode = new WeakReference<ATNode>(editorNode);
    }

    public ATNode getRendererNode() {
        if ( rendererNode == null ) return null;
        return rendererNode.get();
    }

    public void setRendererNode(ATNode rendererNode) {
        if ( rendererNode == null ) this.rendererNode = null;
        this.rendererNode = new WeakReference<ATNode>(rendererNode);
    }
    
    
    
}
