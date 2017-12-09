/*
 * AdjAttackDestinationsNode.java
 *
 * Created on March 6, 2002, 10:58 PM
 */

package champions.adjustmentPowers.adjustmentTree;

import champions.Ability;
import champions.AdjustmentList;
import champions.Characteristic;
import champions.Target;
import champions.adjustmentPowers.Adjustable;
import champions.adjustmentPowers.AdjustmentPower;
import champions.interfaces.ChampionsConstants;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.tree.TreePath;
import treeTable.DefaultTreeTableModel;
import treeTable.DefaultTreeTableNode;
import treeTable.TreeTable;


/**
 *
 * @author  Trevor Walker
 * @version
 */
public class AdjAttackDestinationsNode extends DefaultTreeTableNode
implements ChampionsConstants, PropertyChangeListener{
    
    /** Holds value of property target. */
    private Target target;
    
    /** Holds value of property sourceAbility. */
    private Ability sourceAbility;
    
    /** Holds value of property adjustmentList. */
    private AdjustmentList adjustmentList;
    
    /** Holds value of property adjustmentType. */
    private int adjustmentType;
    
    // Hold Data Flavors
    static private DataFlavor characteristicFlavor, abilityFlavor;
    static private RemoveAdjustableAction removeAction;
    
    /** Creates new AdjAttackDestinationsNode */
    public AdjAttackDestinationsNode(Ability sourceAbility, Target target, int adjustmentType, AdjustmentList adjustmentList) {
        setSourceAbility(sourceAbility);
        setTarget(target);
        setAdjustmentType(adjustmentType);
        setAdjustmentList(adjustmentList);
        
        updateChildren();
        
        setupFlavors();
        
        setupActions();
    }
    
    protected void setupFlavors() {
        try {
            if (abilityFlavor == null ) abilityFlavor = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType + "; class=" + Ability.class.getName() );
            if (characteristicFlavor == null ) characteristicFlavor = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType + "; class=" + Characteristic.class.getName() );
        }
        catch ( Exception e) {
            System.out.println(e);
        }
    }
    
    protected void setupActions() {
        if ( removeAction == null ) removeAction = new RemoveAdjustableAction("Remove");
    }
    
    protected void updateChildren() {
        if ( adjustmentList == null ) {
            // Children should really be destroyed as they are removed, but we will leave this for now.
            this.removeAllChildren();
            if ( model != null ) ((DefaultTreeTableModel)model).nodeStructureChanged(this);
        }
        else {
            boolean fireChange = false;
            boolean found;
            // Make sure the abilities in the source match the parameterListNodes that are childern
            Vector newChildren = new Vector();
            int aindex, acount;
            int nindex, ncount;
            int total = 0;
            Object o;
            AdjAttackDestinationAbilityNode an;
            AdjAttackDestinationStatNode sn;
            
            
            ncount = ( children != null ) ? children.size() : 0;
            
            int index, count;
            count = adjustmentList.getAdjustableCount();
            for(index = 0; index < count; index++) {
                if ( adjustmentList.getAdjustableTarget() == target ) {
                    o = adjustmentList.getAdjustableObject(index);
                    
                    if ( o instanceof Ability ) {
                        Ability a = (Ability)o;
                        // Try to find it in the children array.
                        found = false;
                        for(nindex=0;nindex<ncount;nindex++) {
                            if ( children.get(nindex) instanceof AdjAttackDestinationAbilityNode ) {
                                an = (AdjAttackDestinationAbilityNode) children.get(nindex);
                                if ( an.getTargetAbility().equals(a) && an.getTarget() == target ) {
                                    found = true;
                                    // Move the parameterList node from the childern list to the newChildern list
                                    newChildren.add(an);
                                    if ( nindex != total ) fireChange = true;
                                    children.set(nindex, null);
                                    break;
                                }
                            }
                        }
                        
                        if ( found == false ) {
                            an = new AdjAttackDestinationAbilityNode(sourceAbility, a, target, adjustmentList.getAdjustablePercentage(index), adjustmentList);
                            newChildren.add(an);
                            an.setParent(this);
                            an.setModel(model);
                            fireChange = true;
                        }
                    }
                    else if ( o instanceof Characteristic ) {
                        Characteristic c = (Characteristic)o;
                        // Try to find it in the children array.
                        found = false;
                        for(nindex=0;nindex<ncount;nindex++) {
                            if ( children.get(nindex) instanceof AdjAttackDestinationStatNode ) {
                                sn = (AdjAttackDestinationStatNode) children.get(nindex);
                                if ( sn.getStat().equals( c ) && sn.getTarget() == target ) {
                                    found = true;
                                    // Move the parameterList node from the childern list to the newChildern list
                                    newChildren.add(sn);
                                    if ( nindex != total ) fireChange = true;
                                    children.set(nindex, null);
                                    break;
                                }
                            }
                        }
                        
                        if ( found == false ) {
                            sn = new AdjAttackDestinationStatNode(sourceAbility, c, target, adjustmentList.getAdjustablePercentage(index), adjustmentList) ;
                            newChildren.add(sn);
                            sn.setParent(this);
                            sn.setModel(model);
                            fireChange = true; 
                        }
                    }
                    
                    total++;
                }
            }
            
            Vector oldChildren = children;
            children = newChildren;
            
            // Now that everything is done, anything level not-null in oldChildren should be destroyed
            // and references to it released.
            if ( oldChildren != null ) {
                for(nindex=0;nindex<oldChildren.size();nindex++) {
                    if ( oldChildren.get(nindex) != null ) {
                        ((DefaultTreeTableNode)oldChildren.get(nindex)).destroy();
                        oldChildren.set(nindex,null);
                        fireChange = true;
                    }
                }
            }
            
            if ( fireChange && model != null ) ((DefaultTreeTableModel)model).nodeStructureChanged(this);
        }
    }
    
    /** Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        return target;
    }
    
    /** Setter for property target.
     * @param target New value of property target.
     */
    public void setTarget(Target target) {
        this.target = target;
    }
    
    /** Getter for property sourceAbility.
     * @return Value of property sourceAbility.
     */
    public Ability getSourceAbility() {
        return sourceAbility;
    }
    
    /** Setter for property sourceAbility.
     * @param sourceAbility New value of property sourceAbility.
     */
    public void setSourceAbility(Ability sourceAbility) {
        this.sourceAbility = sourceAbility;
    }
    
    /** Getter for property adjustmentList.
     * @return Value of property adjustmentList.
     */
    public AdjustmentList getAdjustmentList() {
        return adjustmentList;
    }
    
    /** Setter for property adjustmentList.
     * @param adjustmentList New value of property adjustmentList.
     */
    public void setAdjustmentList(AdjustmentList adjustmentList) {
        if ( adjustmentList != this.adjustmentList ) {
            if ( this.adjustmentList != null ) {
                this.adjustmentList.removePropertyChangeListener(this);
            }
            
            this.adjustmentList = adjustmentList;
            
            if ( this.adjustmentList != null ) {
                this.adjustmentList.addPropertyChangeListener(this);
            }
        }
        
    }
    
    /** Getter for property adjustmentType.
     * @return Value of property adjustmentType.
     */
    public int getAdjustmentType() {
        return adjustmentType;
    }
    
    /** Setter for property adjustmentType.
     * @param adjustmentType New value of property adjustmentType.
     */
    public void setAdjustmentType(int adjustmentType) {
        this.adjustmentType = adjustmentType;
    }
    
    public TreePath willHandleDrop(TreeTable treeTable, TreePath path, DropTargetDragEvent event) {
        TreePath dropPath = null;
        
        AdjustmentPower power = (AdjustmentPower)sourceAbility.getPower();
        
        if ( isFlavorHandled(event) ) {
            if ( power.getAdjustmentLevel(sourceAbility) != ADJ_SINGLE_ADJUSTMENT || children == null || children.size() == 0 ) {
                if ( children != null && children.size() > 0 ) {
                    DefaultTreeTableNode lastChild = (DefaultTreeTableNode)children.get( children.size() - 1 );
                    dropPath = new TreePath( lastChild.getPath() );
                }
                else {
                    dropPath = new TreePath( getPath() );
                }
            }
        }
        
        return dropPath;
    }
    
    public boolean handleDrop(TreeTable treeTable, TreePath path, DropTargetDropEvent event) {
        AdjustmentPower power = (AdjustmentPower)sourceAbility.getPower();
        if ( isFlavorHandled(event) && (power.getAdjustmentLevel(sourceAbility) != ADJ_SINGLE_ADJUSTMENT || children == null || children.size() == 0) ) {
            try {
                Transferable tr = event.getTransferable();
                Object o = null;
                if( event.isDataFlavorSupported(characteristicFlavor)  ) {
                    o = tr.getTransferData(characteristicFlavor);
                }
                else if( event.isDataFlavorSupported(abilityFlavor)  ) {
                    o = tr.getTransferData(abilityFlavor);
                }
                
                if ( o != null ) {
                    event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    
                    Adjustable a = (Adjustable)o;
                    addDestinationToAdjustmentList(a);
                    
                    event.dropComplete(true);
                    return true;
                }
                
            }
            catch(IOException ioe) {
                ioe.printStackTrace();
            }
            catch(UnsupportedFlavorException ufe) {
                ufe.printStackTrace();
            }
        }
        
        return false;
    }
    
    protected boolean isFlavorHandled(DropTargetDragEvent event) {
        return  ( event.isDataFlavorSupported(abilityFlavor)
        || event.isDataFlavorSupported(characteristicFlavor) );
    }
    
    protected boolean isFlavorHandled(DropTargetDropEvent event) {
        return  ( event.isDataFlavorSupported(abilityFlavor)
        || event.isDataFlavorSupported(characteristicFlavor));
    }
    
    private void addDestinationToAdjustmentList(Adjustable o ) {
        adjustmentList.addAdjustable(o, 100);
    }
    
    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        updateChildren();
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
    public boolean invokeMenu(TreeTable treeTable,TreePath path, javax.swing.JPopupMenu popup) {
        boolean result = false;
        DefaultTreeTableNode node = (DefaultTreeTableNode)path.getLastPathComponent();
        if ( node instanceof AdjAttackDestinationAbilityNode ) {
            removeAction.setupAction( ((AdjAttackDestinationAbilityNode)node).getTargetAbility(), adjustmentList );
            result = true;
        }
        else if ( node instanceof AdjAttackDestinationStatNode ) {
            removeAction.setupAction( ((AdjAttackDestinationStatNode)node).getStat(), adjustmentList );
            result = true;
        }
        
        if ( result ) {
            popup.add( removeAction );
        }
        
        return result;
    }
    
    public static class RemoveAdjustableAction extends AbstractAction {
        Adjustable adjustable;
        AdjustmentList list;
        
        public RemoveAdjustableAction(String name) {
            super(name);
        }
        
        public void setupAction(Adjustable adjustable, AdjustmentList list) {
            this.adjustable = adjustable;
            this.list = list;
        }
        
        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e) {
            if ( list != null ) {
                list.removeAdjustable(adjustable);
            }
        }
        
    }
}
