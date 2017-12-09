/*
 * AbilityImportNode.java
 *
 * Created on June 11, 2001, 8:11 PM
 */

package champions.abilityTree;

import champions.*;
import champions.interfaces.*;

import treeTable.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.util.*;
import java.beans.*;
/**
 *
 * @author  twalker
 * @version
 */
public class LineGroupNode extends AbilityTreeNode
implements PropertyChangeListener {
    /** Holds value of property abilityImport. */
    private AbilityImport abilityImport;
    
    /** Holds value of property ability. */
    private Ability ability;
    
    private static MarkAllLinesUsedAction markAction;
    
    /** Creates new AbilityNode */
    public LineGroupNode(AbilityTreeTableModel model, MutableTreeNode parent,AbilityImport abilityImport) {
        setModel(model);
        setParent(parent);
        setAbilityImport(abilityImport);
        
        updateChildren();
        
        setupActions();
    }
    
    public void setupActions() {
        if ( markAction == null ) markAction = new LineGroupNode.MarkAllLinesUsedAction();
    }
    
    
    
    /** Getter for property abilityImport.
     * @return Value of property abilityImport.
     */
    public AbilityImport getAbilityImport() {
        return abilityImport;
    }
    
    /** Setter for property abilityImport.
     * @param abilityImport New value of property abilityImport.
     */
    public void setAbilityImport(AbilityImport abilityImport) {
        this.abilityImport = abilityImport;
    }
    
    public Icon getIcon(JTree tree,boolean selected,boolean expanded,boolean leaf,int row,boolean hasFocus) {
        return null;
    }
    
    public TreeCellEditor getTreeCellEditor(JTree tree) {
        return null;
    }
    
    public TreeCellRenderer getTreeCellRenderer(JTree tree) {
        return null;
    }
    
    public int getNodeStatus() {
        return OKAY_STATUS;
    }
    
    /** Called to destroy the object.  All references should be set to null.
     * Once destroy has been called, the object will not be used anymore.
     */
    public void destroy() {
        setAbilityImport(null);
    }
    
    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
    }
    
    public void updateChildren() {
        int acount, aindex, ncount, nindex, total;
        LineNode ln;
        String lineString;
        boolean parameterVisible, parameterEnabled, found, fireChange;
        
        Vector newChildren = new Vector();
        
        ncount = ( children != null ) ? children.size() : 0;
        total = 0;
        fireChange = false;
        
        acount = (abilityImport == null) ? 0 : abilityImport.getIndexedSize("Line");
        for( aindex = 0; aindex < acount; aindex++) {
            lineString = abilityImport.getIndexedStringValue(aindex,"Line","STRING");
            // Try to find it in the children array.
            if ( lineString != null ) {
                found = false;
                for(nindex=0;nindex<ncount;nindex++) {
                    if ( children.get(nindex) instanceof ParameterNode ) {
                        ln = (LineNode) children.get(nindex);
                        if ( ln.getLineIndex() == aindex) {
                            found = true;
                            // Move the ability node from the childern list to the newChildern list
                            newChildren.add(ln);
                            if ( nindex != total ) fireChange = true;
                            children.set(nindex, null);
                            
                            break;
                        }
                    }
                }
                
                if ( found == false ) {
                    ln = new LineNode(model, this, abilityImport, aindex);
                    newChildren.add(ln);
                    fireChange = true;
                }
                total++;
            }
        }
        
        Vector oldChildren = children;
        children = newChildren;
        
        // Now that everything is done, anything not-null in oldChildren should be destroyed
        // and references to it released.
        if ( oldChildren != null ) {
            for(nindex=0;nindex<oldChildren.size();nindex++) {
                if ( oldChildren.get(nindex) != null ) {
                    ((AbilityTreeNode)oldChildren.get(nindex)).destroy();
                    oldChildren.set(nindex,null);
                    fireChange = true;
                }
            }
        }
        
        if ( fireChange && model != null ) model.nodeStructureChanged(this);   
    }
    
    public String toString() {
        return "Imported Configuration Lines";
    }
    
    public boolean invokeMenu(JPopupMenu popup, JTree tree, TreePath path) {
        markAction.setupAction(model, this, abilityImport);
        popup.add(markAction);
        return true;
    }
    
    private class MarkAllLinesUsedAction extends AbstractAction {
        
        /** Holds value of property abilityImport. */
        private AbilityImport abilityImport;
        
        /** Holds value of property node. */
        private AbilityTreeNode node;
        
        /** Holds value of property model. */
        private AbilityTreeTableModel model;
        
        public MarkAllLinesUsedAction () {
            super( "Ignore ALL Import Lines");
        }
        
        public void setupAction(AbilityTreeTableModel model, AbilityTreeNode node, AbilityImport ai) {
            setModel(model);
            setNode(node);
            setAbilityImport(ai);
        }
        
        /** Getter for property abilityImport.
         * @return Value of property abilityImport.
 */
        public AbilityImport getAbilityImport() {
            return abilityImport;
        }
        
        /** Setter for property abilityImport.
         * @param abilityImport New value of property abilityImport.
 */
        public void setAbilityImport(AbilityImport abilityImport) {
            this.abilityImport = abilityImport;
        }
        
        public boolean isEnabled() {
            return ( abilityImport != null );
        }
        
        public void actionPerformed(ActionEvent e) {
            if ( abilityImport != null ) {
                int index, count;
                count = abilityImport.getImportLineCount();
                for(index=0;index<count;index++) {
                    if ( abilityImport.isLineUsed(index) == false ) {
                        abilityImport.setLineUsed(index, "USER OVERRIDE");
                    }
                }
                    
                if ( model != null ) {
                    model.nodeChanged(node);
                    Iterator i = children.iterator();
                    while(i.hasNext()) {
                        model.nodeChanged( (AbilityTreeNode) i.next());
                    }
                    
                }
            }
            
        }
        
        /** Getter for property node.
         * @return Value of property node.
 */
        public AbilityTreeNode getNode() {
            return node;
        }
        
        /** Setter for property node.
         * @param node New value of property node.
 */
        public void setNode(AbilityTreeNode node) {
            this.node = node;
        }
        
        /** Getter for property model.
         * @return Value of property model.
 */
        public AbilityTreeTableModel getModel() {
            return model;
        }
        
        /** Setter for property model.
         * @param model New value of property model.
 */
        public void setModel(AbilityTreeTableModel model) {
            this.model = model;
        }
        
    }
}
