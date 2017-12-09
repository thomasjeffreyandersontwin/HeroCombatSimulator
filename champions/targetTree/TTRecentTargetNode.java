/*
 * PADTargetListNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.targetTree;

import champions.Battle;
import champions.Target;
import champions.TargetList;
import tjava.Filter;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreePath;
import treeTable.DefaultTreeTableModel;
import treeTable.TreeTable;
import treeTable.TreeTableModel;


/**
 *
 * @author  twalker
 * @version
 *
 * PADTargetListNode's hold references to Abilities stored in an TargetList list.
 * It should not be used for powers/templates which are cached by the PADRoster
 * shared instance mechanism.
 */
public class TTRecentTargetNode extends TTNode
        implements PropertyChangeListener, ChangeListener {
    
    /** Holds value of property targetList. */
    private TargetList targetList;
    
    /** Indicates the delete option should be shown.
     */
    private boolean deleteEnabled = false;
    
    private static RemoveTargetListAction deleteAction = null;
    private static DebugAction debugAction = null;
    
    /** Creates new PADTargetListNode */
    public TTRecentTargetNode(TreeTableModel model, TargetList targetList) {
        super("Recent Targets");
        setModel(model);
        setTargetList(targetList);
    }
    
    protected void buildNode() {
        if ( model == null ) return;
        
        removeAndDestroyAllChildren();
        
        Iterator i;
        
//        // There shouldn't be any sublists in the recent target list...
//        for(int index = 0; index < targetList.getSublistCount(); index++) {
//            TargetList sublist = targetList.getSublist(index);
//            TTTargetListNode node = new TTTargetListNode(sublist);
//            node.setDeleteEnabled(isDeleteEnabled());
//            add(node);
//        }
        
        if ( targetList != null ) {
            Filter<Target> filter = getModel() != null ? ((TTModel)getModel()).getFilter() : null;
            
            i = targetList.getTargets(false);
            while ( i.hasNext() ) {
                Target target = (Target)i.next();
                if ( filter == null || filter.includeElement(target) ) {
                    TTTargetNode node = null;
                    
                    node = new TTTargetNode(model, target, targetList);
                    node.setDeleteEnabled(isDeleteEnabled());
                    
                    add(node);
                }
            }
        }
        
        if ( model instanceof DefaultTreeTableModel ) {
            ((DefaultTreeTableModel)model).nodeStructureChanged(this);
        }
    }
   /* public boolean startDrag(TreeTable tree, TreePath path, DragSourceListener listener, DragGestureEvent dge) {
        if ( targetList != null ) {
    
            TargetList o = (TargetList)targetList.clone();
    
            if ( o != null ) {
                Transferable t = new ObjectTransferable(o, TargetList.class);
    
                Point p = dge.getDragOrigin();
                Rectangle bounds = tree.getPathBounds(path);
                Point offset = new Point(p.x - bounds.x, p.y - bounds.y);
    
                BufferedImage i = ((DefaultTreeTable)tree).buildDragImage(path);
                DefaultTreeTable.startDrag(i, offset);
    
                dge.startDrag(null,i,offset, t, listener);
            }
        }
    } */
    
 /*   public void handleDoubleClick(TargetListTreeTable tree, TreePath targetListPath) {
        TargetListTreeNode node = (TargetListTreeNode)targetListPath.getLastPathComponent();
  
        while ( node != null && ! ( node instanceof SublistNode ) ) {
            node = (TargetListTreeNode)node.getParent();
        }
  
        if ( node != null ) {
            //  String sublist = ((SublistNode)node).getSublist();
            TargetList a = (TargetList)targetList.clone();
            // targetList.setSublist(sublist);
            ((SublistNode)node).addTargetList(a);
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
            if ( deleteEnabled ) {
                if ( deleteAction == null ) deleteAction = new RemoveTargetListAction();
                
                if ( targetList.getParent() != null ) {
                    deleteAction.setTargetList(targetList);
                    popup.add(deleteAction);
                    rv = true;
                }
            }
            
            if ( Battle.debugLevel >= 0 ) {
                if ( rv ) popup.addSeparator();
                
                if ( debugAction == null ) debugAction = new DebugAction();
                debugAction.setTargetList(targetList);
                popup.add(debugAction);
                rv = true;
            }
        }
        return rv;
    }
    
    
    
    /** Getter for property targetList.
     * @return Value of property targetList.
     *
     */
    public TargetList getTargetList() {
        return targetList;
    }
    
    /** Setter for property targetList.
     * @param targetList New value of property targetList.
     *
     */
    public void setTargetList(TargetList targetList) {
        if ( this.targetList != targetList ) {
            if ( this.targetList != null ) {
                this.targetList.removePropertyChangeListener("TargetList.NAME", this);
                // this.targetList.removeChangeListener(this);
            }
            
            this.targetList = targetList;
            
            
            if ( this.targetList != null ) {
                
                updateName();
                buildNode();
                
                this.targetList.addPropertyChangeListener("TargetList.NAME", this);
                // this.targetList.addChangeListener(this);
            }
        }
        
    }
    
    private void updateName() {
        setUserObject(targetList.getName());
        
     /*   Power p = targetList.getPower();
        Icon icon = p.getIcon();
        setIcon(icon); */
    }
    
    public boolean isLeaf() {
        return false;
    }
    
    public boolean getAllowsChildren() {
        return true;
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        updateName();
    }
    
    
    /** Getter for property deleteEnabled.
     * @return Value of property deleteEnabled.
     *
     */
    public boolean isDeleteEnabled() {
        return deleteEnabled;
    }
    
    /** Setter for property deleteEnabled.
     * @param deleteEnabled New value of property deleteEnabled.
     *
     */
    public void setDeleteEnabled(boolean deleteEnabled) {
        this.deleteEnabled = deleteEnabled;
    }
    
    public void stateChanged(javax.swing.event.ChangeEvent e) {
        if ( e.getSource() == targetList ) {
            buildNode();
        }
    }
    
    public void destroy() {
        super.destroy();
        
        setTargetList(null);
    }
    
    private static class DebugAction extends AbstractAction {
        private TargetList targetList;
        public DebugAction() {
            super("Debug Sublist...");
        }
        
        public void actionPerformed(ActionEvent e) {
            if (targetList != null ) {
                targetList.displayDebugWindow();
            }
        }
        
        public void setTargetList(TargetList targetList) {
            this.targetList = targetList;
        }
    }
    
    public static class RemoveTargetListAction extends AbstractAction {
        private TargetList targetList;
        public RemoveTargetListAction() {
            super("Delete Sublist");
        }
        
        public void actionPerformed(ActionEvent e ) {
            
            if ( targetList != null && targetList.getParent() != null ) {
                targetList.getParent().removeSublist(targetList);
            }
        }
        
        public void setTargetList(TargetList targetList) {
            this.targetList = targetList;
        }
    }
}
