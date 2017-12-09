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
public class ATCreateCharacterNode extends ATNode {
    
    private Roster roster;
    
//    /** Creates new PADTargetNode */
//    public ATCreateCharacterNode(Roster roster) {
//        this(roster, null, false);
//    }
    
    public ATCreateCharacterNode(Roster roster, ATNodeFactory nodeFactory, Filter<Object> nodeFilter, boolean pruned) {
        super(nodeFactory, nodeFilter, pruned);
        setup(roster);
        
        
       // setIcon( UIManager.getIcon("AbilityTree.rosterIcon"));
    }
    
    public void setup(Roster roster) {
        this.roster = roster;
        
        buildNode();
    }
    
    public void buildNode() {
        
    }
    
    public Object getValueAt(int column) {
        if ( column == ATColumn.NAME_COLUMN.ordinal() ) {
            return "<Create new character>";
        }
        
        return null;
    }

    public void setNodeFilter(Filter<Object> nodeFilter) {
        // Override the filter so the active target is always shown
        return;
    }
    
    public void handleDoubleClick(ATTree tree, TreePath targetPath) {
        if ( roster != null ) {
            String name = "New Character";
            if ( Battle.currentBattle != null ) {
                name = Battle.currentBattle.getUniqueName(name);
            }
            Target target = new champions.Character(name);
            target.editTarget();
            roster.add(target);
        }
    } 

    
    /** Allows the node to provide context menu feedback.
     *
     * invokeMenu is called whenever an event occurs which would cause a context menu to be displayed.
     * If the node has context menu items to display, it should add them to the JPopupMenu <code>popup</code>
     * and return true.
     * @param treeTable Indicates the TreeTable which is currently displaying the node.
     * @param path The TreePath which the cursor was over when the context menu
     * event occurred.
     * @param popup The JPopupMenu which is being built.  Always non-null and initialized.
     * @return True if menus where added, False if menus weren't.
     */
    public boolean invokeMenu(TreeTable treeTable,TreePath path, JPopupMenu popup) {
        boolean rv = false;
        if ( path.getLastPathComponent() == this ) {
          /*  if ( editAction == null ) editAction = new EditAction();
            if ( target != null ) {
                editAction.setTarget(target);
                popup.add(editAction);
                rv = true;
            }
           
            if ( deleteEnabled ) {
                if ( deleteAction == null ) deleteAction = new RemoveTargetAction();
           
                if ( target != null && getTargetList() != null ) {
                    deleteAction.setTarget(target);
                    deleteAction.setTargetList(targetList);
                    popup.add(deleteAction);
                    rv = true;
                }
            }
           
            if ( Battle.debugLevel >= 0 ) {
                if ( rv ) popup.addSeparator();
           
                if ( debugAction == null ) debugAction = new DebugAction();
                debugAction.setTarget(target);
                popup.add(debugAction);
                rv = true;
            }*/
        }
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
