/*
 * ATTargetActionsNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.abilityTree2;

import champions.Effect;
import champions.Target;
import champions.exception.BattleEventException;
import champions.exceptionWizard.ExceptionWizard;
import tjava.Filter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;
import javax.swing.Action;
import javax.swing.JPopupMenu; 
import javax.swing.tree.TreePath;
import treeTable.DefaultTreeTableModel;
import treeTable.TreeTable;
import treeTable.TreeTableColumnModel;

/**
 *
 * @author  twalker
 * @version
 *
 * The ATTargetNode is the root not for a list of all possible abilities
 * and disadvantages that can be used by the target.  This includes the 
 * abilities owned by the target and the default abilities.
 *
 * The ATTargetNode2 should be used in situations where listing of the abilities
 * of a target, not including the default abilities, and any target specific
 * actions are needed.
 */
public class ATTargetActionsNode extends ATNode implements PropertyChangeListener {

    /** Holds value of property target. */
    private Target target;
   
    
//    private static RemoveTargetAction deleteAction = null;
//    private static DebugAction debugAction = null;
//    private static EditAction editAction = null;
    
    /** Creates new PADTargetNode */
    public ATTargetActionsNode(Target target, ATNodeFactory nodeFactory, Filter<Object> nodeFilter, boolean pruned) {
        super(nodeFactory, nodeFilter, pruned);
        setTarget(target);
    }
    
    public void buildNode() {
        removeAndDestroyAllChildren();
        
        if ( target != null ) {
            Vector v = new Vector();
            
            int count = target.getEffectCount();
            for(int i = 0; i < count; i++) {
                Effect e = target.getEffect(i);
                if ( e != null ) {
                    try {
                        e.addActions(v);
                    }
                    catch (BattleEventException bee ) {
                        ExceptionWizard.postException(bee);
                    }
                }
            }
            
            for(int i = 0; i < v.size(); i++) {
                Action action = (Action)v.get(i);
                if ( nodeFilter == null || nodeFilter.includeElement(action)) {
                    ATNode node = nodeFactory.createActionNode(null, action, nodeFilter, pruned);
                    if ( node != null ) {
                        add(node);
                    }
                }
            }
        }
        
        updateName(false);
        
        if ( getModel() instanceof DefaultTreeTableModel ) {
            ((DefaultTreeTableModel)getModel()).nodeStructureChanged(this);
        }
    }

    public int getColumnSpan(int column, TreeTableColumnModel columnModel) {
        return ATColumn.MAX_COLUMNS.ordinal();
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
    
    /** Setter for property target.
     * @param target New value of property target.
     *
     */
    public void setTarget(Target target) {
        if ( this.target != target ) {
            if ( this.target != null ) {
                this.target.removePropertyChangeListener("Target.NAME", this);
            }
            
            this.target = target;
            
            
            if ( this.target != null ) {
                
                rebuildNode();
                this.target.addPropertyChangeListener("Target.NAME", this);
            }
        }
        
    }
    
    protected void updateName(boolean fireChange) {
        Object oldName = getUserObject();
        String newName = target.getName() + "'s Actions";
        
        if ( newName != null && newName.equals(oldName) == false) {
            setUserObject(newName);
            if (fireChange && model instanceof DefaultTreeTableModel ) {
                ((DefaultTreeTableModel)model).nodeChanged(this);
            }
        }
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        updateName(false);
    }
    

        /** Returns the Target associated with node.
     *
     * This should return whatever the node represents.
     *
     * @return null if no Target is associated, such as in the case of a folder.
     */
    public Target getTarget() {
        return target;
    }  
   
    public void destroy() {
        super.destroy();
        
        setTarget(null);
    }
    
}
