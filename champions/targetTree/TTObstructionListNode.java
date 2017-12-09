/*
 * PADObstructionListNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.targetTree;

import champions.Target;
import champions.ObstructionList;
import tjava.Filter;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import treeTable.DefaultTreeTableModel;
import treeTable.TreeTableModel;

/**
 *
 * @author  twalker
 * @version
 *
 * PADObstructionListNode's hold references to Abilities stored in an ObstructionList list.
 * It should not be used for powers/templates which are cached by the PADRoster
 * shared instance mechanism.
 */
public class TTObstructionListNode extends TTNode
        implements PropertyChangeListener {
    
    /** Holds value of property obstructionList. */
    private ObstructionList obstructionList;
    
    
    /** Creates new PADObstructionListNode */
    public TTObstructionListNode(TreeTableModel model, ObstructionList obstructionList) {
        setModel(model);
        setObstructionList(obstructionList);
    }
    
    protected void buildNode() {
        if ( model == null ) return;
        
        removeAndDestroyAllChildren();
        
        Filter<Target> filter = getModel() != null ? ((TTModel)getModel()).getFilter() : null;
        
        if ( obstructionList != null ) {
            int count = obstructionList.getObstructionCount();
            for(int i = 0; i < count; i++ ) {
                Target target = (Target)obstructionList.getObstruction(i);
                
                if ( filter == null || filter.includeElement(target) ) {
                    TTTargetNode node = null;
                    
                    node = new TTTargetNode(model, target, null);
                    node.setDeleteEnabled(false);
                    
                    add(node);
                }
            }
        }
        if ( model instanceof DefaultTreeTableModel ) {
            ((DefaultTreeTableModel)model).nodeStructureChanged(this);
        }
    }
   /* public boolean startDrag(TreeTable tree, TreePath path, DragSourceListener listener, DragGestureEvent dge) {
        if ( obstructionList != null ) {
    
            ObstructionList o = (ObstructionList)obstructionList.clone();
    
            if ( o != null ) {
                Transferable t = new ObjectTransferable(o, ObstructionList.class);
    
                Point p = dge.getDragOrigin();
                Rectangle bounds = tree.getPathBounds(path);
                Point offset = new Point(p.x - bounds.x, p.y - bounds.y);
    
                BufferedImage i = ((DefaultTreeTable)tree).buildDragImage(path);
                DefaultTreeTable.startDrag(i, offset);
    
                dge.startDrag(null,i,offset, t, listener);
            }
        }
    } */
    
 /*   public void handleDoubleClick(ObstructionListTreeTable tree, TreePath obstructionListPath) {
        ObstructionListTreeNode node = (ObstructionListTreeNode)obstructionListPath.getLastPathComponent();
  
        while ( node != null && ! ( node instanceof SublistNode ) ) {
            node = (ObstructionListTreeNode)node.getParent();
        }
  
        if ( node != null ) {
            //  String sublist = ((SublistNode)node).getSublist();
            ObstructionList a = (ObstructionList)obstructionList.clone();
            // obstructionList.setSublist(sublist);
            ((SublistNode)node).addObstructionList(a);
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
  /*  public boolean invokeMenu(TreeTable treeTable, TreePath path, JPopupMenu popup) {
        boolean rv = false;
        if ( path.getLastPathComponent() == this ) {
            if ( deleteEnabled ) {
                if ( deleteAction == null ) deleteAction = new RemoveObstructionListAction();
   
                if ( obstructionList.getParent() != null ) {
                    deleteAction.setObstructionList(obstructionList);
                    popup.add(deleteAction);
                    rv = true;
                }
            }
   
            if ( Battle.debugLevel >= 0 ) {
                if ( rv ) popup.addSeparator();
   
                if ( debugAction == null ) debugAction = new DebugAction();
                debugAction.setObstructionList(obstructionList);
                popup.add(debugAction);
                rv = true;
            }
        }
        return rv;
    }
   
   */
    
    /** Getter for property obstructionList.
     * @return Value of property obstructionList.
     *
     */
    public ObstructionList getObstructionList() {
        return obstructionList;
    }
    
    /** Setter for property obstructionList.
     * @param obstructionList New value of property obstructionList.
     *
     */
    public void setObstructionList(ObstructionList obstructionList) {
        if ( this.obstructionList != obstructionList ) {
            if ( this.obstructionList != null ) {
                this.obstructionList.removePropertyChangeListener(this);
            }
            
            this.obstructionList = obstructionList;
            
            
            
            if ( this.obstructionList != null ) {
                buildNode();
                this.obstructionList.addPropertyChangeListener(this);
            }
        }
        
    }
    
    public void destroy() {
        super.destroy();
        
        setObstructionList(null);
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        buildNode();
    }
    
    private static class DebugAction extends AbstractAction {
        private ObstructionList obstructionList;
        public DebugAction() {
            super("Debug Sublist...");
        }
        
        public void actionPerformed(ActionEvent e) {
            if (obstructionList != null ) {
                obstructionList.debugDetailList( "ObstructionList Debugger" );
            }
        }
        
        public void setObstructionList(ObstructionList obstructionList) {
            this.obstructionList = obstructionList;
        }
    }
    
    
    
}
