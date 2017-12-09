/*
 * ParameterNode.java
 *
 * Created on June 11, 2001, 7:53 PM
 */

package champions.abilityTree;

import champions.*;
import champions.interfaces.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.tree.*;
import treeTable.ButtonTreeTableCellEditor;
import treeTable.TreeTableButtonNode;
import treeTable.TreeTableCellEditor;
import treeTable.TreeTableCellRenderer;

/**
 *
 * @author  twalker
 * @version
 */
public class CreateVariableInstanceNode extends AbilityTreeNode
        implements ChampionsConstants, TreeTableButtonNode {
    private Ability ability;
    
    static private Icon createIcon = null;
    
    static private ButtonTreeTableCellEditor cellEditor = null;
    
    /** Creates new ParameterNode */
    public CreateVariableInstanceNode(AbilityTreeTableModel model, MutableTreeNode parent, Ability ability) {
        setModel(model);
        setParent(parent);
        setAbility(ability);
        
        setupIcons();
        setupActions();
        
        setupEditor();
    }
    
    private void setupActions() {
    }
    
    private void setupEditor() {
        if ( cellEditor == null ) {
            cellEditor = new ButtonTreeTableCellEditor();
        }
    }
    
    public TreeTableCellRenderer getTreeTableCellRenderer(int columnIndex) {
        if(columnIndex == 0) return cellEditor;
        return null;
    }
    
    public TreeTableCellEditor getTreeTableCellEditor(int columnIndex) {
        if(columnIndex == 0) return cellEditor;
        return null;
    }
    
    /** Called to destroy the object.  All references should be set to null.
     * Once destroy has been called, the object will not be used anymore.
     */
    public void destroy() {
        setAbility(null);
    }
    
    
    public Ability getAbility() {
        return ability;
    }
    
    public void setAbility(Ability ability) {
        this.ability = ability;
    }

    public void actionPerformed(int column, ActionEvent e) {
        // The button action was performed, so create an new instances...
        Ability newAbility = ability.getInstanceGroup().createNewInstance(true);
        //ability.getInstanceGroup().setVariationInstance(newAbility, true);
    }

    public boolean isEditable(int column) {
        return column == 0;
    }

    public boolean isColumnEnabled(int column) {
        return column == 0;
    }

    public Color getColumnColor(int column) {
        return new Color(0,0,238);
    }

    public Font getColumnFont(int column) {
        return null;
    }
    
    public String toString() {
        return "New Variation...";
    }

    public Object getValue(int columnIndex) {
        if ( columnIndex == 0 ) {
            return "New Variation...";
        }
        else{
            return null;
        }
            
    }

    private void setupIcons() {
        if ( createIcon == null ) createIcon = UIManager.getIcon("AbilityTree.createVariationIcon");
    }

    public Icon getIcon(JTree tree, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        return createIcon;
    }
    



    
}
