/*
 * PADTargetNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.abilityTree2;

import champions.Battle;
import champions.Roster;
import champions.Target;
import tjava.Filter;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import VirtualDesktop.VirtualDesktopNodeListener;
import treeTable.DefaultTreeTableNode;
import treeTable.TreeTable;

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
public class VirtualDesktopActionNode extends ATNode {
    
    private Roster roster;
    private String action;
    
    public VirtualDesktopActionNode(Roster roster, ATNodeFactory nodeFactory, Filter<Object> nodeFilter, boolean pruned, String action) {
        super(nodeFactory, nodeFilter, pruned);
        setup(roster);
        this.action=action;
    }
    
    public void setup(Roster roster) {
        this.roster = roster;
        
        buildNode();
    }
    
    public void buildNode() {
        
    }
    
    public Object getValueAt(int column) {
        if ( column == ATColumn.NAME_COLUMN.ordinal() ) {
            return this.action;
        }
        
        return null;
    }

    public void setNodeFilter(Filter<Object> nodeFilter) {
        // Override the filter so the active target is always shown
        return;
    }
    
    public void handleDoubleClick(ATTree tree, TreePath targetPath) {
        if ( roster != null ) {
        	Target t = VirtualDesktopNodeListener.targetSelected;
        	
        	VirtualDesktop.MessageExporter.exportVirtualDesktopAction(action, t);
        }
    } 
     
    public boolean invokeMenu(TreeTable treeTable,TreePath path, JPopupMenu popup) {
        boolean rv = false;
        return rv;
       }
    
    
    public void destroy() {
        super.destroy();
       
        
    }
    public Roster getRoster() {
        return roster;
    }

    public void setRoster(Roster roster) {
        this.roster = roster;
    }

    public int getPreferredOrder() {
        return 1;
    }
}
