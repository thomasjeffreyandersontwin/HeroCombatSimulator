/*
 * PADTargetNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.abilityTree2;

import champions.Battle;
import champions.BattleChangeEvent;
import champions.Target;
import champions.event.SegmentAdvancedEvent;
import champions.event.SequenceChangedEvent;
import champions.event.TargetSelectedEvent;
import champions.interfaces.BattleListener;
import tjava.Filter;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.TreePath;
import treeTable.DefaultTreeTableModel;
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
public class ATActiveTargetNode extends ATNode implements BattleListener {
    
    

    public ATActiveTargetNode(ATNodeFactory nodeFactory, Filter<Object> nodeFilter, boolean pruned) {
        super(nodeFactory, nodeFilter, pruned);
        setup();
    }
    
    public void setup() {
        this.setPruned(pruned);
        this.nodeFilter = nodeFilter;
        
        Battle.addBattleListener(this);
        
        setIcon( UIManager.getIcon("AbilityTree.rosterIcon"));
        
        buildNode();
        
    }
    
    public void buildNode() {
        removeAndDestroyAllChildren();
        
        if ( Battle.currentBattle != null ) {
            if ( Battle.currentBattle.isStopped() ) {
                ATNode n = nodeFactory.createMessageNode( "Battle Not Started (Press play button to start battle)", nodeFilter, pruned);
                if ( n != null ) {
                    add(n);
                }
            }
            else {
                Target target = Battle.currentBattle.getActiveTarget();
                if ( target != null ) {
                    ATTargetNode n = nodeFactory.createTargetNode(target, false, nodeFilter, pruned);
                    if ( n != null ) {
                        n.setHighlightActiveTarget(true);
                        n.setIncludeAbilityNode(false);
                        n.setExpandedByDefault(true);
                        add(n);
                    }
                }
                else {
                    ATNode n = nodeFactory.createMessageNode( "No active character (All characters are ineligible or time needs to be advanced)", nodeFilter, pruned);
                    if ( n != null ) {
                        add(n);
                    }
                }
            }
        }
        
        if ( model instanceof DefaultTreeTableModel ) {
                ((DefaultTreeTableModel)model).nodeStructureChanged(this);
            }
    }
    
    public Object getValueAt(int column) {
        if ( column == ATColumn.NAME_COLUMN.ordinal() ) {
            return "Active Character";
        }
        
        return null;
    }

    public void setNodeFilter(Filter<Object> nodeFilter) {
        // Override the filter so the active target is always shown
        return;
    }

    public int getPreferredOrder() {
        return -1;
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
        Battle.removeBattleListener(this);
        
        super.destroy();
        
        
        
    }
    
    public void battleTargetSelected(TargetSelectedEvent event) {
        rebuildNode();
    }
    
    public void battleSegmentAdvanced(SegmentAdvancedEvent event) {
    }
    
    public void battleSequenceChanged(SequenceChangedEvent event) {
    }
    
    public void stateChanged(BattleChangeEvent e) {
        
    }
    
    public void eventNotification(ChangeEvent event) {
    }
    
    public void combatStateChange(ChangeEvent event) {
    }
    
    public void processingChange(BattleChangeEvent event) {
    }
    
    
}
