/*
 * PADSweepBattleEventNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.attackTree.sweepTree;

import champions.Ability;
import champions.SweepBattleEvent;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;
import treeTable.DefaultTreeTableModel;
import treeTable.DefaultTreeTableNode;
import treeTable.TreeTable;




/**
 *
 * @author  twalker
 * @version
 *
 * PADSweepBattleEventNode's hold references to Abilities stored in an SweepBattleEvent list.
 * It should not be used for powers/templates which are cached by the PADRoster
 * shared instance mechanism.
 */
public class SWTBattleEventNode extends SWTNode implements PropertyChangeListener {
    
    /** Holds value of property battleEvent. */
    private SweepBattleEvent battleEvent;
    
    /** Indicates the delete option should be shown.
     */
    private boolean deleteEnabled = false;
    
    private static DebugAction debugAction = null;
    
    /** Creates new PADSweepBattleEventNode */
    public SWTBattleEventNode(SweepBattleEvent battleEvent) {
        setBattleEvent(battleEvent);
    }
    
    private void buildChildren() {
        //removeAllChildren();
        
        Iterator i;
        
        Ability ability = null;
        Ability lastAbility = null;
        
        Ability maneuver = null;
        Ability lastManeuver = null;
        
        boolean fireStructureChanged = false;
        
        int count = 0;
        int position = 0;
        int nodeIndex = 0;
        
        int linkedAbilityCount = ( battleEvent == null ? 0 : battleEvent.getLinkedAbilityCount() );
        
        for(int index = 0; index < linkedAbilityCount; index++) {
            ability = battleEvent.getLinkedAbility(index);
            maneuver = battleEvent.getLinkedManeuver(index);
            
            if ( ability.equals(lastAbility) == false || (maneuver != null && maneuver.equals(lastManeuver) == false)) {
                if ( lastAbility != null ) {
                    SWTAbilityNode node;
                    if ( nodeIndex >= getChildCount() ) {
                        node = new SWTAbilityNode(battleEvent, lastAbility, lastManeuver, position, count);
                        add(node);
                        fireStructureChanged = true;
                    }
                    else {
                        node = (SWTAbilityNode)getChildAt(nodeIndex);
                        node.setInformation(battleEvent, lastAbility, lastManeuver, position, count);
                    }
                    nodeIndex++;
                }
                
                lastAbility = ability;
                lastManeuver = maneuver;
                count = 1;
                position = index;
            }
            else {
                count++;
            }
        }
        
        if ( lastAbility != null ) {
            SWTAbilityNode node;
            if ( nodeIndex >= getChildCount() ) {
                node = new SWTAbilityNode(battleEvent, lastAbility, lastManeuver, position, count);
                add(node);
                fireStructureChanged = true;
            }
            else {
                node = (SWTAbilityNode)getChildAt(nodeIndex);
                node.setInformation(battleEvent, lastAbility, lastManeuver, position, count);
            }
            nodeIndex++;
        }
        
        while ( nodeIndex < getChildCount() ) {
            remove(nodeIndex);
            fireStructureChanged = true;
        }
        
        if ( fireStructureChanged && model instanceof DefaultTreeTableModel ) {
            ((DefaultTreeTableModel)model).nodeStructureChanged(this);
        }
    }
    
    /** Notifies Node of a drop event occuring in or below the node.
     * The Node can use the handleDrop method to perform actions based on the drop of a DetailList on
     * the Node Jtree.  If the node wishes to handles the event, it should call acceptDrop and dropComplete
     * on the event, then return true to indicate the event was handled.
     * @returns True if event was handled.  False if additional handling
     * should be done.
     * @return True if the event was handled, false if it wasn't.
     * @param treeTable Indicates the TreeTable which is currently displaying the node.
     * @param dropPath Indicates the location of the drop in the Tree hierarchy.
     * @param event The Drop event that is being handled.
     */
    public boolean handleDrop(TreeTable treeTable, TreePath dropPath, DropTargetDropEvent event) {
        try {
            Transferable t = event.getTransferable();
            if ( t.isDataFlavorSupported(abilityFlavor) ) {
                // See if this is the same ability...
                Ability a = (Ability)t.getTransferData(abilityFlavor);
                
                // This is a drop of the same ability...
                int position = -1;
                if ( dropPath.getLastPathComponent() instanceof SWTAbilityNode ) {
                    position = ((SWTAbilityNode)dropPath.getLastPathComponent()).getPosition();
                }
                
                battleEvent.addLinkedAbility(a, position, false);
                
                event.acceptDrop(DnDConstants.ACTION_MOVE);
                event.dropComplete(true);
                return true;
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return false;
    }
    
    /** Called to check if a node would handle a drop if it occurred.
     * @return Returns the TreePath after which a feedback line could be drawn indicating where the drop will be placed.
     * @param treeTable Indicates the TreeTable which is currently displaying the node.
     * @param dropPath Indicates the location of the drop in the Tree hierarchy.
     * @param event The Drop event that is being handled.
     */
    public TreePath willHandleDrop(TreeTable treeTable, TreePath dropPath, DropTargetDragEvent event) {
        if ( event.isDataFlavorSupported(abilityFlavor) ) {
            
            if ( dropPath.getLastPathComponent() == this ) {
                try {
                    DefaultTreeTableNode node = (DefaultTreeTableNode)((DefaultTreeTableNode)dropPath.getLastPathComponent()).getLastLeaf();
                    return new TreePath(node.getPath());
                }
                catch ( NoSuchElementException nsee ){
                    return new TreePath(this.getPath());
                }
            }
            else if ( dropPath.getLastPathComponent() instanceof DefaultTreeTableNode ) {
                DefaultTreeTableNode node = (DefaultTreeTableNode)((DefaultTreeTableNode)dropPath.getLastPathComponent()).getPreviousNode();
                
                if ( node != null) return new TreePath( node.getPath());
            }
            
            return dropPath;
            
        }
        return null;
    }
    
 /*   public void handleDoubleClick(SweepBattleEventTreeTable tree, TreePath battleEventPath) {
        SweepBattleEventTreeNode node = (SweepBattleEventTreeNode)battleEventPath.getLastPathComponent();
  
        while ( node != null && ! ( node instanceof SublistNode ) ) {
            node = (SweepBattleEventTreeNode)node.getParent();
        }
  
        if ( node != null ) {
            //  String sublist = ((SublistNode)node).getSublist();
            SweepBattleEvent a = (SweepBattleEvent)battleEvent.clone();
            // battleEvent.setSublist(sublist);
            ((SublistNode)node).addSweepBattleEvent(a);
        }
    } */
    
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
         /*   if ( deleteEnabled ) {
                if ( deleteAction == null ) deleteAction = new RemoveSweepBattleEventAction();
          
                if ( battleEvent.getParent() != null ) {
                    deleteAction.setBattleEvent(battleEvent);
                    popup.add(deleteAction);
                    rv = true;
                }
            }
          
            if ( Battle.debugLevel >= 0 ) {
                if ( rv ) popup.addSeparator();
          
                if ( debugAction == null ) debugAction = new DebugAction();
                debugAction.setBattleEvent(battleEvent);
                popup.add(debugAction);
                rv = true;
            } */
        }
        return rv;
    }
    
    
    
    /** Getter for property battleEvent.
     * @return Value of property battleEvent.
     *
     */
    public SweepBattleEvent getBattleEvent() {
        return battleEvent;
    }
    
    /** Setter for property battleEvent.
     * @param battleEvent New value of property battleEvent.
     *
     */
    public void setBattleEvent(SweepBattleEvent battleEvent) {
        if ( this.battleEvent != battleEvent ) {
            if ( this.battleEvent != null ) {
                this.battleEvent.removePropertyChangeListener( this);
                
            }
            
            this.battleEvent = battleEvent;
            
            buildChildren();
            
            if ( this.battleEvent != null ) {
                this.battleEvent.addPropertyChangeListener( this);
            }
        }
        
    }
    
    
    
    public boolean isLeaf() {
        return false;
    }
    
    public boolean getAllowsChildren() {
        return true;
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        buildChildren();
    }
    
    
    
    
    private static class DebugAction extends AbstractAction {
        private SweepBattleEvent battleEvent;
        public DebugAction() {
            super("Debug Sublist...");
        }
        
        public void actionPerformed(ActionEvent e) {
            if (battleEvent != null ) {
                battleEvent.debugDetailList( "SweepBattleEvent Debugger" );
            }
        }
        
        public void setBattleEvent(SweepBattleEvent battleEvent) {
            this.battleEvent = battleEvent;
        }
    }
    
}
