/*
 * ImportTree.java
 *
 * Created on July 4, 2001, 10:36 AM
 */

package champions.abilityTree;

import tjava.ContextMenu;
import tjava.ContextMenuListener;
import champions.*;
import champions.interfaces.*;

import treeTable.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;

/**
 *
 * @author  twalker
 * @version
 */
public class ImportTree extends DefaultTreeTable
implements ContextMenuListener {
    
    /** Holds value of property characterImport. */
    private CharacterImport characterImport;
    
    /** Creates new ImportTree */
    public ImportTree() {
        
        setupTree();
    }
    
    public void setupTree() {
//        AbilityTreeCellRenderer atcr = new AbilityTreeCellRenderer();
//        this.setCellRenderer( atcr );
//        
//        AbilityTreeCellEditor atce = new AbilityTreeCellEditor();
//        ImmediateEditor ie = new ImmediateEditor(this, atcr, atce);
//        this.setCellEditor( ie );
//        
        AbilityExpansionListener ael = new AbilityExpansionListener();
        this.addTreeExpansionListener(ael);
        
      //  AbilityTreeDnDListener atdtl = new AbilityTreeDnDListener(this); 
        
        this.putClientProperty("JTree.lineStyle", "Angled");
        this.setShowsRootHandles(false);
        this.setRootVisible(false);
        this.setEditable(true);
        this.setInvokesStopCellEditing(true);
        
        ContextMenu.addContextMenu(this);
        
        Color c;
        c = UIManager.getColor( "AbilityEditor.foreground" );
        if ( c != null ) this.setForeground(c);
        c = UIManager.getColor( "AbilityEditor.background" );
        if ( c != null ) this.setBackground(c);
    }
    
    /** Getter for property characterImport.
     * @return Value of property characterImport.
     */
    public CharacterImport getCharacterImport() {
        return characterImport;
    }
    
    /** Setter for property characterImport.
     * @param characterImport New value of property characterImport.
     */
    public void setCharacterImport(CharacterImport characterImport) {
        if ( this.characterImport != characterImport ) {
            
            this.characterImport = characterImport;
            ImportSublistNode treeRoot = new ImportSublistNode(characterImport,null,null);
            ImportTreeTableModel atm = new ImportTreeTableModel( treeRoot );
            treeRoot.setModel(atm);
            treeRoot.setTree(this);
            setTreeTableModel(atm); 
        }
    }
    
    public boolean invokeMenu(JPopupMenu popup,Component inComponent,Point inPoint) {
        TreePath path = getClosestPathForLocation(inPoint.x,inPoint.y);
        boolean rv = false;
        Object node;
        
        int index;
        for(index = path.getPathCount() - 1;index >= 0;index--) {
            node = path.getPathComponent(index);
            if ( node instanceof AbilityTreeNode ) {
                if ( ((AbilityTreeNode)node).invokeMenu( popup, this, path) ) {
                    rv = true;
                }
            }
        }
        
        return rv;
    }
    
}
