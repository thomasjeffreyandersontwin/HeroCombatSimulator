/*
 * PADPowerNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.abilityTree.PADTree;

import champions.Ability;
import champions.CombinedAbility;
import tjava.ObjectTransferable;
import champions.abilityTree.*;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.image.BufferedImage;
import javax.swing.UIManager;
import javax.swing.tree.TreePath;
import treeTable.DefaultTreeTable;
import treeTable.TreeTable;



/**
 *
 * @author  twalker
 * @version
 *
 * PADPowerNode's hold references to powers/templates stored in the PADRoster.
 * It should not be used for abilities which come from an AbilityList.
 */
public class PADCombinedAbilityNode extends PADTreeTableNode {
    
    /** Holds value of property powerName. */
    private String name;
    
    /** Creates new PADPowerNode */
    public PADCombinedAbilityNode() {
        setUserObject("Combined Power");
        setIcon(UIManager.getIcon("Power.DefaultIcon"));
    }    
    
    public boolean startDrag(TreeTable tree, TreePath path, DragSourceListener listener, DragGestureEvent dge) {

        Ability o = new CombinedAbility("Combined Power");

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
        
        return false;
    }
    
    public void handleDoubleClick(champions.abilityTree.PADTree.PADTreeTable padTree, AbilityTreeTable tree, TreePath abilityPath) {
        AbilityTreeNode node = (AbilityTreeNode)abilityPath.getLastPathComponent();
        
        while ( node != null && ! ( node instanceof AbilityListNode ) ) {
            node = (AbilityTreeNode)node.getParent();
        }
        
        if ( node != null ) {
            Ability ability = new CombinedAbility("Combined Power");
            ((AbilityListNode)node).addAbility(ability);
        }
    }
    
    /** Returns the PAD (Power, Skill, Adv, Lim, etc) associated with node.
     *
     * This should return a new instance of whatever the node represents.
     *
     * @return null if no PAD is associated, such as in the case of a folder.
     */
    public Object getPAD() {
        return null;
    }
    
}
