/*
 * ParameterNode.java
 *
 * Created on June 11, 2001, 7:53 PM
 */

package champions.abilityTree;

import champions.event.*;
import champions.*;
import champions.interfaces.*;
import champions.parameterEditor.*;

import treeTable.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.util.*;
/**
 *
 * @author  twalker
 * @version
 */
public class LineNode extends AbilityTreeNode
implements ChampionsConstants
{
    
    
    static private Icon usedIcon = null;
    static private Icon unusedIcon = null;
    
    static private LineNode.MarkLineUsedAction markAction = null;
    
    /** Holds value of property abilityImport. */
    private AbilityImport abilityImport;
    
    /** Holds value of property lineIndex. */
    private int lineIndex;
    
    /** Creates new ParameterNode */
    public LineNode(AbilityTreeTableModel model, MutableTreeNode parent, AbilityImport abilityImport, int line) {
        setModel(model);
        setParent(parent);
        setAbilityImport(abilityImport);
        setLineIndex(line);
        
        if ( unusedIcon == null ) unusedIcon = UIManager.getIcon( "ImportLine.UnusedIcon");
        if ( usedIcon == null ) usedIcon = UIManager.getIcon( "ImportLine.UsedIcon");
        
        setupActions();
    }
    
    public void setupActions() {
        if ( markAction == null ) markAction = new LineNode.MarkLineUsedAction();
    }
    
    public Icon getIcon(JTree tree,boolean selected,boolean expanded,boolean leaf,int row,boolean hasFocus) {
        if ( abilityImport != null ) {
            if ( abilityImport.isLineUsed(lineIndex) == true ) {
                return usedIcon;
            }
        }
        return unusedIcon;
    }
    
    public TreeCellEditor getTreeCellEditor(JTree tree) {
        return null;
    }
    
    public TreeCellRenderer getTreeCellRenderer(JTree tree) {
        return null;
    }
    
    public int getNodeStatus() {
        if ( abilityImport != null ) {
            if ( abilityImport.isLineUsed(lineIndex) == false ) {
                return QUESTION_STATUS;
            }
        }
        return OKAY_STATUS;
    }
    
    /** Called to destroy the object.  All references should be set to null.
     * Once destroy has been called, the object will not be used anymore.
     */
    public void destroy() {
        setAbilityImport(null);
    }
    
    public String toString() {
        if ( abilityImport != null ) {
            return abilityImport.getImportLine(lineIndex);
        }
        return "LineNode Error";
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
    
    /** Getter for property lineIndex.
     * @return Value of property lineIndex.
     */
    public int getLineIndex() {
        return lineIndex;
    }
    
    /** Setter for property lineIndex.
     * @param lineIndex New value of property lineIndex.
     */
    public void setLineIndex(int lineIndex) {
        this.lineIndex = lineIndex;
    }
    
        public boolean invokeMenu(JPopupMenu popup, JTree tree, TreePath path) {
        markAction.setupAction(model, this, abilityImport, lineIndex);
        popup.add(markAction);
        return true;
    }
    
    private static  class MarkLineUsedAction extends AbstractAction {
        
        /** Holds value of property abilityImport. */
        private AbilityImport abilityImport;
        
        /** Holds value of property line. */
        private int line;
        
        /** Holds value of property node. */
        private AbilityTreeNode node;
        
        /** Holds value of property model. */
        private AbilityTreeTableModel model;
        
        public MarkLineUsedAction () {
            super( "Ignore Import Line");
        }
        
        public void setupAction(AbilityTreeTableModel model, AbilityTreeNode node, AbilityImport ai, int line) {
            setModel(model);
            setNode(node);
            setAbilityImport(ai);
            setLine(line);
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
        
        /** Getter for property line.
         * @return Value of property line.
 */
        public int getLine() {
            return line;
        }
        
        /** Setter for property line.
         * @param line New value of property line.
 */
        public void setLine(int line) {
            this.line = line;
        }
        
        public boolean isEnabled() {
            return ( abilityImport != null );
        }
        
        public void actionPerformed(ActionEvent e) {
            if ( abilityImport != null ) {
                abilityImport.setLineUsed(line, "USER OVERRIDE");
                
                if ( model != null ) {
                    model.nodeChanged(node);
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
