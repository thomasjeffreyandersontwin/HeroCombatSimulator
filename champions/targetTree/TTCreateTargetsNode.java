/*
 * PADTargetListNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.targetTree;

import champions.ObjectDebugger;
import champions.ObjectTarget;
import champions.PADRoster;
import champions.Target;
import champions.TargetList;
import tjava.Filter;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import treeTable.DefaultTreeTableModel;

/**
 *
 * @author  twalker
 * @version
 *
 * PADTargetListNode's hold references to Abilities stored in an TargetList list.
 * It should not be used for powers/templates which are cached by the PADRoster
 * shared instance mechanism.
 */
public class TTCreateTargetsNode extends TTNode {
    
    /** Holds the Filter, if any, to filter which targets are displayed.
     *
     */
    private Filter<Target> targetFilter;
    
    /** Indicates the delete option should be shown.
     */
    private boolean deleteEnabled = false; 
    
    /** Creates new PADTargetListNode */
    
    public TTCreateTargetsNode() {
        super("Create Target...");
        setExpandedByDefault(false);
        buildNode();
    }
    
    protected void buildNode() {
        if ( model == null ) return;
        
        removeAndDestroyAllChildren();
        
        TTTargetListNode preset = new TTTargetListNode(model, PADRoster.getPresetTargets() );
        preset.setUserObject("Object Templates (Create New)");
        preset.setPreset(true);
        add( preset );
        
        TTTargetNode genericObject = new TTTargetNode(model, new ObjectTarget("Generic Object", -1, -1), null);
        genericObject.setUserObject("Generic Object (Create New)");
        genericObject.setPreset(true);
        add( genericObject );
        
        if ( model instanceof DefaultTreeTableModel ) {
            ((DefaultTreeTableModel)model).nodeStructureChanged(this);
        }
    }

    public void destroy() {
        super.destroy();
        
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
    


    
    public boolean isLeaf() {
        return false;
    }
    
   public boolean getAllowsChildren() {
        return true;
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
    
    public Filter<Target> getTargetFilter() {
        return targetFilter;
    }

    public void setTargetFilter(Filter<Target> targetFilter) {
        this.targetFilter = targetFilter;
        
        buildNode();
    }
    
    private static class DebugAction extends AbstractAction {
        private TargetList targetList;
        public DebugAction() {
            super("Debug Sublist...");
        }
        
        public void actionPerformed(ActionEvent e) {
            if (targetList != null ) {
                //targetList.debugDetailList( "TargetList Debugger" );
                ObjectDebugger.displayDebugWindow("TargetList Debugger", targetList);
            }
        }
        
        public void setTargetList(TargetList targetList) {
            this.targetList = targetList;
        }
    }
   



    
}
