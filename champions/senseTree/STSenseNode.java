/*
 * PADTargetNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.senseTree;

import champions.ChampionsUtilities;
import champions.Sense;
import champions.Target;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.tree.TreePath;
import treeTable.DefaultTreeTableModel;
import treeTable.TreeTable;




/**
 *
 * @author  twalker
 * @version
 *
 */
public class STSenseNode extends STNode implements PropertyChangeListener {

    /** Holds value of property target. */
    protected Target target;
    
    protected Sense sense;
    
    protected static Icon trueIcon = null;
    protected static Icon falseIcon = null;
    
//    private static RemoveTargetAction deleteAction = null;
//    private static DebugAction debugAction = null;
//    private static EditAction editAction = null;
    
    /** Creates new PADTargetNode */
    public STSenseNode(Target target, Sense sense) {
        setTarget(target);
        setSense(sense);
        setupIcons();
    }
    
    protected void setupIcons() {
        if ( trueIcon == null ) trueIcon = UIManager.getIcon("SenseTree.trueIcon");
        if ( falseIcon == null ) falseIcon = UIManager.getIcon("SenseTree.falseIcon");
    }
    
    public Object getValueAt(int column) {
        switch (column) {
            case STModel.NAME_COLUMN:
                return sense.getSenseName();
            case STModel.RANGED_COLUMN:
                return (sense.isRangedSense()?trueIcon:falseIcon);
            case STModel.TARGETTING_COLUMN:
                return (sense.isTargettingSense()?trueIcon:falseIcon);
            case STModel.PERCEPTION_MODIFIER_COLUMN:
                int ep = sense.getEnhancedPerceptionLevel();
                if ( ep != 0 ) return ChampionsUtilities.toSignedString(ep);
                return "0";
            case STModel.PENALTY_COLUMN:
                return sense.getPenaltyString();
        }
        
        return null;
    }
    
    public void buildChildren() {
//        removeAllChildren();

        
//        if ( target != null ) {
//            AbilityList al = target.getAbilityList();
//            STAbilityListNode node = new STAbilityListNode(al, abilityFilter, pruned, flat);
//            node.setName( target.getName() + "'s Abilities");
//            if ( node.getChildCount() != 0 ){
//                add(node);
//            }
//        }
//        
//        AbilityList al = Battle.getDefaultAbilities();
//        STAbilityListNode node = new STAbilityListNode(al, abilityFilter, pruned, flat);
//        node.setName("Default Abilities");
//        if ( node.getChildCount() != 0 ){
//            add(node);
//        }
        
//        if ( getModel() instanceof DefaultTreeTableModel ) {
//            ((DefaultTreeTableModel)getModel()).nodeStructureChanged(this);
//        }
    }

    public Icon getIcon(TreeTable treeTable, boolean isSelected, boolean expanded, boolean leaf, int row) {
        return (sense.isFunctioning()?trueIcon:falseIcon);
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
              //  this.target.removePropertyChangeListener("Target.NAME", this);
            }
            
            this.target = target;
            
            updateName();
            buildChildren();
            
            if ( this.target != null ) {
               // this.target.addPropertyChangeListener("Target.NAME", this);
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
        updateName();
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
   
    /**
     * Getter for property sense.
     * @return Value of property sense.
     */
    public Sense getSense() {
        return sense;
    }    

    /**
     * Setter for property sense.
     * @param sense New value of property sense.
     */
    public void setSense(Sense sense) {
        if ( this.sense != sense ) {
            if ( this.sense != null ) {
              //  this.target.removePropertyChangeListener("Target.NAME", this);
            }
            
            this.sense = sense;
            
            updateName();
            buildChildren();
            
            if ( this.sense != null ) {
               // this.target.addPropertyChangeListener("Target.NAME", this);
            }
        }
    }    
    
}
