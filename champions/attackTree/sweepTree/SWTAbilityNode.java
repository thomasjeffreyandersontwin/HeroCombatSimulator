/*
 * PADTargetNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.attackTree.sweepTree;

import champions.Ability;
import champions.SweepBattleEvent;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDropEvent;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;
import treeTable.DefaultTreeTableModel;
import treeTable.TreeTable;




/**
 *
 * @author  twalker
 * @version
 *
 * PADTargetNode's hold references to Abilities stored in an Target list.
 * It should not be used for powers/templates which are cached by the PADRoster
 * shared instance mechanism.
 */
public class SWTAbilityNode extends SWTNode {
    
    /** Holds sweep battle event. */
    protected SweepBattleEvent battleEvent;
    
    /** Holds value of property ability. */
    private Ability ability;
    
    /** Holds value of property ability. */
    private Ability maneuver;
    
    /** Holds the number of time this ability/manuever should be executed.
     */
    private int count;
    
    /** Holds this ability/maneuver combinations position in SweepBattleEvent. */
    private int position;
    
    public static SWTAbilityNode SWTNode;
    /** Indicates the delete option should be shown.
     */
    // private boolean deleteEnabled = false;
    
    // private static RemoveTargetAction deleteAction = null;
    // private static DebugAction debugAction = null;
    // private static EditAction editAction = null;
    
    /** Creates new PADTargetNode */
    public SWTAbilityNode(SweepBattleEvent be, Ability ability, Ability maneuver, int position, int count) {
        setBattleEvent(be);
        setAbility(ability);
        setManeuver(maneuver);
        setPosition(position);
        setCount(count);
        SWTNode = this;
    }
    
    /**
     * Returns the value to be displayed for node <code>node</code>,
     * at column number <code>column</code>.
     * @param column The column of the node for which a value should be returned.
     * @return The Value of the column.
     */
    public Object getValueAt(int column) {
        switch ( column ) {
            case 1:
                return "x" + Integer.toString(count);
            case 0:
                if ( maneuver != null ) {
                    return ability.getName() + " w/ " + maneuver.getName();
                }
                else {
                    return ability.getName();
                }
        }
        return null;
    }
    
    /**
     * Returns the Icon to be used when drawing this node.
     *
     * If the Icon is null, the standard open, closed, leaf icons will be used.
     * @param treeTable The treeTable which is currently displaying the node.
     * @param isSelected Whether this node is currently selected.
     * @param expanded Whether this node is currently expanded.
     * @param leaf Whether this node is currently considered a leaf.
     * @param row The row at which the node is current displayed in the TreeTable.
     * @return An Icon which should be used for this node.  Null if default icons should be used.
     */
    public Icon getIcon(TreeTable treeTable, boolean isSelected, boolean expanded, boolean leaf, int row) {
        return (ability != null) ? ability.getPower().getIcon() : null;
    }
    
   /* public boolean startDrag(TreeTable tree, TreePath path, DragSourceListener listener, DragGestureEvent dge) {
        if ( target != null ) {
    
            Target o = (Target)target.clone();
    
            if ( o != null ) {
                Transferable t = new ObjectTransferable(o, Target.class);
    
                Point p = dge.getDragOrigin();
                Rectangle bounds = tree.getPathBounds(path);
                Point offset = new Point(p.x - bounds.x, p.y - bounds.y);
    
                BufferedImage i = ((DefaultTreeTable)tree).buildDragImage(path);
                DefaultTreeTable.startDrag(i, offset);
    
                dge.startDrag(null,i,offset, t, listener);
            }
        }
    } */
    
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
                if ( a == ability ) {
                    // This is a drop of the same ability...
                    battleEvent.addLinkedAbility(a, position, false);
                    event.acceptDrop(DnDConstants.ACTION_MOVE);
                    event.dropComplete(true);
                    return true;
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return false;
    }
    
 /*   public void handleDoubleClick(TargetTreeTable tree, TreePath targetPath) {
        TargetTreeNode node = (TargetTreeNode)targetPath.getLastPathComponent();
  
        while ( node != null && ! ( node instanceof SublistNode ) ) {
            node = (TargetTreeNode)node.getParent();
        }
  
        if ( node != null ) {
            //  String sublist = ((SublistNode)node).getSublist();
            Target a = (Target)target.clone();
            // target.setSublist(sublist);
            ((SublistNode)node).addTarget(a);
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
      /*      if ( editAction == null ) editAction = new EditAction();
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
            } */
        }
        return rv;
    }
    
    /** Getter for property ability.
     * @return Value of property ability.
     *
     */
    public Ability getAbility() {
        return ability;
    }
    
    /** Setter for property ability.
     * @param ability New value of property ability.
     *
     */
    public void setAbility(Ability ability) {
        this.ability = ability;
    }
    
    /** Getter for property maneuver.
     * @return Value of property maneuver.
     *
     */
    public Ability getManeuver() {
        return maneuver;
    }
    
    /** Setter for property maneuver.
     * @param maneuver New value of property maneuver.
     *
     */
    public void setManeuver(Ability maneuver) {
        this.maneuver = maneuver;
    }
    
    /** Getter for property count.
     * @return Value of property count.
     *
     */
    public int getCount() {
        return count;
    }
    
    /** Setter for property count.
     * @param count New value of property count.
     *
     */
    public void setCount(int count) {
        this.count = count;
    }
    
    /** Getter for property position.
     * @return Value of property position.
     *
     */
    public int getPosition() {
        return position;
    }
    
    /** Setter for property position.
     * @param position New value of property position.
     *
     */
    public void setPosition(int position) {
        this.position = position;
    }
    
    public void setInformation(SweepBattleEvent be, Ability ability, Ability maneuver, int position, int count) {
        setBattleEvent(be);
        setAbility(ability);
        setManeuver(maneuver);
        setPosition(position);
        setCount(count);
        
        if ( getModel() instanceof DefaultTreeTableModel ) {
            ((DefaultTreeTableModel)getModel()).nodeChanged(this);
        }
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
        this.battleEvent = battleEvent;
    }
    
}
