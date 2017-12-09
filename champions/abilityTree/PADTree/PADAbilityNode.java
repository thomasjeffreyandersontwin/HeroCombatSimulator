/*
 * PADAbilityNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.abilityTree.PADTree;

import champions.Ability;
import champions.Battle;
import tjava.ObjectTransferable;
import champions.Power;
import champions.abilityTree.*;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;
import tjava.SharedPopupAction;
import treeTable.DefaultTreeTable;
import treeTable.TreeTable;


/**
 *
 * @author  twalker
 * @version
 *
 * PADAbilityNode's hold references to Abilities stored in an Ability list.
 * It should not be used for powers/templates which are cached by the PADRoster
 * shared instance mechanism.
 */
public class PADAbilityNode extends PADTreeTableNode
implements PropertyChangeListener {
    
    /** Holds value of property ability. */
    private Ability ability;
    
    /** Indicates the delete option should be shown.
     */
    private boolean deleteEnabled = false; 
    
    private static PADAbilityNode.RemoveAbilityAction deleteAction = null;
    private static PADAbilityNode.DebugAction debugAction = null;
    
    /** Creates new PADAbilityNode */
    public PADAbilityNode(Ability ability) {
        setAbility(ability);
    }
    
    public boolean startDrag(TreeTable tree, TreePath path, DragSourceListener listener, DragGestureEvent dge) {
        if ( ability != null ) {
            
            Ability o = (Ability)ability.clone();
            
            if ( o != null ) {
                Transferable t = new ObjectTransferable(o, Ability.class);
                
                Point p = dge.getDragOrigin();
                Rectangle bounds = tree.getPathBounds(path);
                Point offset = new Point(p.x - bounds.x, p.y - bounds.y);
                
                BufferedImage i = ((DefaultTreeTable)tree).buildDragImage(path);
                DefaultTreeTable.startDrag(i, offset);
                
                dge.startDrag(null,i,offset, t, listener);
                return true;
            }
        }
        return false;
    }
    
    public void handleDoubleClick(champions.abilityTree.PADTree.PADTreeTable padTree, AbilityTreeTable tree, TreePath abilityPath) {
        AbilityTreeNode node = (AbilityTreeNode)abilityPath.getLastPathComponent();
        
        while ( node != null && ! ( node instanceof AbilityListNode ) ) {
            node = (AbilityTreeNode)node.getParent();
        }
        
        if ( node != null ) {
            //  String sublist = ((SublistNode)node).getSublist();
            Ability a = (Ability)ability.clone();
            // ability.setSublist(sublist);
            ((AbilityListNode)node).addAbility(a);
        }
    }
    
    /** Returns the PAD (Power, Skill, Adv, Lim, etc) associated with node.
     *
     * This should return a new instance of whatever the node represents.
     *
     * @return null if no PAD is associated, such as in the case of a folder.
     */
    public Object getPAD() {
        return (Ability)ability.clone();
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
            if ( deleteEnabled ) {
                if ( deleteAction == null ) deleteAction = new PADAbilityNode.RemoveAbilityAction();

                if ( ability != null && ability.getAbilityList() != null ) {
                    deleteAction.setAbility(ability);
                    popup.add(deleteAction);
                    rv = true;
                }
            }
            
            if ( Battle.debugLevel >= 0 ) {
                if ( rv ) popup.addSeparator();
                
                if ( debugAction == null ) debugAction = new DebugAction();
                debugAction.setAbility(ability);
                popup.add(debugAction);
                rv = true;
            }
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
        if ( this.ability != ability ) {
            if ( this.ability != null ) {
                this.ability.removePropertyChangeListener("Ability.NAME", this);
            }
            
            this.ability = ability;
            
            updateName();
            
            if ( this.ability != null ) {
                this.ability.addPropertyChangeListener("Ability.NAME", this);
            }
        }
        
    }
    
    private void updateName() {
        setUserObject(ability.getName());
        
        Power p = ability.getPower();
        Icon icon = p.getIcon();
        setIcon(icon);
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
    
    private static class DebugAction extends SharedPopupAction {
        private Ability ability;
        public DebugAction() {
            super("Debug Ability...");
        }
        
        public void actionPerformed(ActionEvent e) {
            if (ability != null ) {
                ability.debugDetailList( "Ability Debugger" );
            }
        }
        
        public void setAbility(Ability ability) {
            this.ability = ability;
        }
    }
    
    public static class RemoveAbilityAction extends SharedPopupAction {
        private Ability ability;
        public RemoveAbilityAction() {
            super("Delete Ability");
        }
        
        public void actionPerformed(ActionEvent e ) {
            
            if ( ability != null && ability.getAbilityList() != null ) {            
                ability.getAbilityList().removeAbility(ability);
            }
        }
        
        public void setAbility(Ability ability) {
            this.ability = ability;
        }
    }
}
