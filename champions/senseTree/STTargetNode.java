/*
 * PADTargetNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.senseTree;

import champions.SenseGroup;
import champions.Target;
import champions.senseFilters.GroupsOnlySenseFilter;
import java.beans.PropertyChangeEvent;
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
public class STTargetNode extends STNode implements PropertyChangeListener {

    /** Holds value of property target. */
    private Target target;
    
//    private static RemoveTargetAction deleteAction = null;
//    private static DebugAction debugAction = null;
//    private static EditAction editAction = null;
    
    /** Creates new PADTargetNode */
    public STTargetNode(Target target) {
        setup(target);
    }
    
    public void setup(Target target) {
        this.target = target;
        
    }
    
    public void buildChildren() {
        removeAllChildren();
        
        if ( target != null ) {
            Iterator it;
            
            it = target.getSenses( new GroupsOnlySenseFilter() );
            while ( it.hasNext() ) {
                SenseGroup sg = (SenseGroup)it.next();
                STNode node = new STSenseGroupNode(target, sg);
                add(node);
            }
        }
        
        if ( getModel() instanceof DefaultTreeTableModel ) {
            ((DefaultTreeTableModel)getModel()).nodeStructureChanged(this);
        }
        
        
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
                this.target.removePropertyChangeListener("SENSES", this);
            }
            
            this.target = target;
            
            updateName();
            buildChildren();
            
            if ( this.target != null ) {
                this.target.addPropertyChangeListener("SENSES", this);
            }
        }
        
    }
    
    private void updateName() {
        //setUserObject(target.getName());
        if ( model instanceof DefaultTreeTableModel ) {
            ((DefaultTreeTableModel)model).nodeChanged(this);
        }
       //Icon icon = p.getIcon();
       // setIcon(icon);
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        buildChildren();
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
   

    
}
