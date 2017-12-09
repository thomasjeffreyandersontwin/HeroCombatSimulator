/*
 * PADTargetNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.senseTree;

import champions.Sense;
import champions.SenseGroup;
import champions.Target;
import champions.senseFilters.GroupNameSenseFilter;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;
import treeTable.DefaultTreeTableModel;
import treeTable.TreeTable;





/**
 *
 * @author  twalker
 * @version
 *
 */
public class STSenseGroupNode extends STSenseNode implements PropertyChangeListener {
    
    /** Creates new PADTargetNode */
    public STSenseGroupNode(Target target, SenseGroup sense) {
        super(target, sense);
    }
    
    public void buildChildren() {
        removeAllChildren();
        
        if ( target != null && sense != null ) {
            Iterator it;
            
            it = target.getSenses( new GroupNameSenseFilter( sense.getSenseName() ) );
            while ( it.hasNext() ) {
                Sense sg = (Sense)it.next();
                STNode node = new STSenseNode(target, sg);
                add(node);
            }
        }
        
        if ( getModel() instanceof DefaultTreeTableModel ) {
            ((DefaultTreeTableModel)getModel()).nodeStructureChanged(this);
        }
    }
    
    public Object getValueAt(int column) {
        switch (column) {
            case STModel.NAME_COLUMN:
                return sense.getSenseName();
//            case STModel.RANGED_COLUMN:
//                return (sense.isRangedSense()?"Yes":"No");
//            case STModel.TARGETTING_COLUMN:
//                return (sense.isTargettingSense()?"Yes":"No");
        }
        
        return null;
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
    
   
    
}
