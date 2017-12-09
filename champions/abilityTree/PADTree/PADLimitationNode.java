/*
 * PADPowerNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.abilityTree.PADTree;

import champions.Ability;
import tjava.ObjectTransferable;
import champions.PADRoster;
import champions.abilityTree.AbilityNode;
import champions.abilityTree.AbilityTreeNode;
import champions.abilityTree.AbilityTreeTable;
import champions.interfaces.Limitation;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.tree.TreePath;
import treeTable.DefaultTreeTable;
import treeTable.TreeTable;


/**
 *
 * @author  twalker
 * @version
 */
public class PADLimitationNode extends PADTreeTableNode {
    
    /** Holds value of property powerName. */
    private String name;
    
    /** Creates new PADPowerNode */
    public PADLimitationNode(String name) {
        setName(name);
        
    }
    
    public String getName() {
        return name;
    }
    /** Getter for property powerName.
     * @return Value of property powerName.
     */
    public void setName(String name) {
        this.name = name;
        setUserObject(name);
        
        Limitation p = PADRoster.getSharedLimitationInstance(name);
        if ( p != null ) {
            Icon icon = p.getIcon();
            setIcon(icon);
        }
    }
    
    public boolean startDrag(TreeTable tree, TreePath path, DragSourceListener listener, DragGestureEvent dge) {
        if ( getName() != null ) {
            
            Limitation o = PADRoster.getNewLimitationInstance( name );
            
            if ( o != null ) {
                Transferable t = new ObjectTransferable(o, Limitation.class);
                
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
        
        while ( node != null && ! ( node instanceof AbilityNode ) ) {
            node = (AbilityTreeNode)node.getParent();
        }
        
        if ( node != null ) {
            Ability ability  = ((AbilityNode)node).getAbility();
            Limitation l = PADRoster.getNewLimitationInstance(name );
            ability.addPAD(l, null);
        }
    }
    
    /** Returns the PAD (Power, Skill, Adv, Lim, etc) associated with node.
     *
     * This should return a new instance of whatever the node represents.
     *
     * @return null if no PAD is associated, such as in the case of a folder.
     */
    public Object getPAD() {
        return PADRoster.getNewLimitationInstance(name );
    }
}
