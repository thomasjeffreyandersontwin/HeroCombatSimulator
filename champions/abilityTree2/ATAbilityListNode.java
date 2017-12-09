/*
 * PADAbilityListNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.abilityTree2;

import champions.Ability;
import champions.interfaces.AbilityList;
import tjava.Filter;
import champions.interfaces.Framework;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.Comparator;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreePath;
import treeTable.DefaultTreeTableModel;
import treeTable.DefaultTreeTableNode;
import treeTable.TreeTable;
import treeTable.TreeTableColumnModel;




/**
 *
 * @author  twalker
 * @version
 *
 * PADAbilityListNode's hold references to Abilities stored in an AbilityList list.
 * It should not be used for powers/templates which are cached by the PADRoster
 * shared instance mechanism.
 */
public class ATAbilityListNode extends ATNode implements ChangeListener {
    
    /** Holds value of property abilityList. */
    private AbilityList abilityList;
    
    /** Indicates this should be a flat structure without sublists */
    protected boolean flat;
   
    /** Indicates the delete option should be shown.
     */
    private boolean deleteEnabled = false; 
    
    private int frameworkConfiguredPoints;
    private int frameworkPoolSize;
    
    static private Icon frameworkOpenIcon = null;
    static private Icon frameworkClosedIcon = null;
    
   // private static RemoveAbilityListAction deleteAction = null;
   // private static DebugAction debugAction = null;
    
//    /** Creates new PADAbilityListNode */
//    public ATAbilityListNode(ATNodeFactory nodeFactory, AbilityList abilityList) {
//        this(nodeFactory, abilityList, null, false, false);
//    }
//    
//    /** Creates new PADAbilityListNode */
//    public ATAbilityListNode(ATNodeFactory nodeFactory, AbilityList abilityList, Filter<Object> nodeFilter) {
//        this(nodeFactory, abilityList, nodeFilter, false, false);
//    } 
    
    /** Creates new PADAbilityListNode */
    public ATAbilityListNode(AbilityList abilityList, boolean flat, ATNodeFactory nodeFactory, Filter<Object> nodeFilter, boolean pruned) {
        super(nodeFactory, nodeFilter, pruned);
        this.flat = flat;
        setAbilityList(abilityList);
        setupIcons();
    }
    
    protected void setupIcons() {
        if ( frameworkOpenIcon == null ) frameworkOpenIcon = UIManager.getIcon("Framework.openIcon");
        if ( frameworkClosedIcon == null ) frameworkClosedIcon = UIManager.getIcon("Framework.closedIcon");
    }
    
    protected void buildNode() {
        removeAndDestroyAllChildren();
        
        Iterator i;
        
        if ( abilityList != null ) {
        
            if ( ! flat ) {
                for(int index = 0; index < abilityList.getSublistCount(); index++) {
                    AbilityList sublist = abilityList.getSublist(index);
                    ATNode node = nodeFactory.createAbilityListNode(sublist, flat, nodeFilter, pruned);
                    if ( node != null ) {
                        if ( !isPruned() || node.getChildCount() != 0 ) {
                            add(node);
                        }
                        else {
                            node.destroy();
                        }
                    }
                }
            }

            i = abilityList.getAbilities(flat);
            while ( i.hasNext() ) {
                Ability ability = (Ability)i.next();
                if ( nodeFilter == null || nodeFilter.includeElement(ability) ) {
                    ATNode node = null;

                    node = nodeFactory.createAbilityInstanceGroupNode(ability, abilityList, nodeFilter, pruned);
                    
                    add(node);
                }
            }
        }
        
        if ( model instanceof DefaultTreeTableModel ) {
            ((DefaultTreeTableModel)model).nodeStructureChanged(this);
        }
    }
   /* public boolean startDrag(TreeTable tree, TreePath path, DragSourceListener listener, DragGestureEvent dge) {
        if ( abilityList != null ) {
            
            AbilityList o = (AbilityList)abilityList.clone();
            
            if ( o != null ) {
                Transferable t = new ObjectTransferable(o, AbilityList.class);
                
                Point p = dge.getDragOrigin();
                Rectangle bounds = tree.getPathBounds(path);
                Point offset = new Point(p.x - bounds.x, p.y - bounds.y);
                
                BufferedImage i = ((DefaultTreeTable)tree).buildDragImage(path);
                DefaultTreeTable.startDrag(i, offset);
                
                dge.startDrag(null,i,offset, t, listener);
            }
        }
    } */
    
 /**
     * Returns the value to be displayed for node <code>node</code>,
     * at column number <code>column</code>.
     * @param column The column of the node for which a value should be returned.
     * @return The Value of the column.
     */
    public Object getValueAt(int column) {
        if ( abilityList == null ) return null;
        ATColumn atc = ATColumn.values()[column];
        switch(atc){
            case NAME_COLUMN:
                String s = abilityList.getName();
                if ( s == null || "".equals(s) ) {
                    
                    if ( abilityList.getSource() != null ) {
                        s = abilityList.getSource().getName() + "'s Abilities";
                    }
                    else {
                        s = "Abilities";
                    }
                }
                if ( s == null ) {
                    s = "";
                }
                return s;
            case END_COLUMN:
                return "";
            case DCV_COLUMN:
                return "";
            case OCV_COLUMN:
                return "";
            case CP_COLUMN:
                Framework fm = abilityList.getFramework();
                if ( fm != null ) {
                    frameworkConfiguredPoints = fm.getFrameworkConfiguredPoints();
                    frameworkPoolSize = fm.getFrameworkPoolSize();
                    
                    if ( frameworkConfiguredPoints >= 0 ) {
                        return Integer.toString(frameworkConfiguredPoints) + "/" + Integer.toString(frameworkPoolSize);
                    }
                }
                return "";
            default:
                return null;
                
        }
    }

    public int getColumnSpan(int column, TreeTableColumnModel columnModel) {
        if ( column == ATColumn.NAME_COLUMN.ordinal() ) {
            return ATColumn.MAX_COLUMNS.ordinal();
        }
        return 1;
    }
    

    
/** Tells the renderer/editor what color the node thinks is appropriate for the column.
     *
     */
    public Color getColumnColor(int column) {
        //
        // THIS METHOD DOES WORK BECAUSE ABILITYLIST DO NOT USE A
        // RENDERER THAT KNOWS ABOUT ATNODES COLORS
        //
        if ( abilityList == null ) return null;
        
        Color c = null;
        if ( column == ATColumn.CP_COLUMN.ordinal() ) {
            Framework fm = abilityList.getFramework();
            if ( fm != null ) {
                frameworkConfiguredPoints = fm.getFrameworkConfiguredPoints();
                frameworkPoolSize = fm.getFrameworkPoolSize();

                if ( frameworkConfiguredPoints > 0 && frameworkPoolSize < frameworkConfiguredPoints) {
                    c = Color.RED;
                }
            }
        }
        return c;
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
        //return (ability != null) ? ability.getPower().getIcon() : null;
        if ( abilityList == null ) return null;
        
        if ( abilityList.getFramework() != null ) {
            if ( expanded ) {
                return frameworkOpenIcon;
            }
            else {
                return frameworkClosedIcon;
            }
        }
        return null;
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
      /*      if ( deleteEnabled ) {
                if ( deleteAction == null ) deleteAction = new RemoveAbilityListAction();

                if ( abilityList.getParent() != null ) {
                    deleteAction.setAbilityList(abilityList);
                    popup.add(deleteAction);
                    rv = true;
                }
            }
            
            if ( Battle.debugLevel >= 0 ) {
                if ( rv ) popup.addSeparator();
                
                if ( debugAction == null ) debugAction = new DebugAction();
                debugAction.setAbilityList(abilityList);
                popup.add(debugAction);
                rv = true;
            }*/
        }
        return rv;
    }
    
    
    
    /** Getter for property abilityList.
     * @return Value of property abilityList.
     *
     */
    public AbilityList getAbilityList() {
        return abilityList;
    }
    
    /** Setter for property abilityList.
     * @param abilityList New value of property abilityList.
     *
     */
    public void setAbilityList(AbilityList abilityList) {
        if ( this.abilityList != abilityList ) {
            if ( this.abilityList != null ) {
                this.abilityList.removeChangeListener(this);
            }
            
            this.abilityList = abilityList;
            
            if ( this.abilityList != null ) {
                rebuildNode();
                this.abilityList.addChangeListener(this);
            }
        }
        
    }
    
    protected void updateName() {
        if ( abilityList != null && abilityList.getName() != null ) setName( abilityList.getName() ) ;
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
    
    public void stateChanged(ChangeEvent e) {
        if ( e.getSource() == abilityList ) {
            rebuildNode();
        }
    }
    
    public void destroy() {
        super.destroy();
        
        setAbilityList(null);
    }
    
    
    public static class RemoveAbilityListAction extends AbstractAction {
        private AbilityList abilityList;
        public RemoveAbilityListAction() {
            super("Delete Sublist");
        }
        
        public void actionPerformed(ActionEvent e ) {
            
            if ( abilityList != null && abilityList.getAbilityList() != null ) {            
                abilityList.getAbilityList().removeSublist(abilityList);
            }
        }
        
        public void setAbilityList(AbilityList abilityList) {
            this.abilityList = abilityList;
        }
    }
    
    public String toString() {
        if ( abilityList == null ) {
            return super.toString();
        }
        else {
            return abilityList.getName();
        }
    }

    public int getPreferredOrder() {
        // Put the abilityList at the top no matter what
        return -2;
        
    }

    public Comparator<DefaultTreeTableNode> getSortComparator(int sortColumn, boolean ascending) {
        final Comparator<DefaultTreeTableNode> superCompare = super.getSortComparator(sortColumn, ascending);
        return new Comparator<DefaultTreeTableNode>() {
            
            public int compare(DefaultTreeTableNode o1, DefaultTreeTableNode o2) {
                if ( o1 instanceof ATAbilityListNode && o2 instanceof ATAbilityListNode ) {
                    return 0;
                }
                else {
                    return superCompare.compare(o1,o2);
                }
            }
        };
    }
    

}
