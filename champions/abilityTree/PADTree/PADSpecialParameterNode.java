/*
 * PADPowerNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.abilityTree.PADTree;

import champions.Ability;
import tjava.ObjectTransferable;
import champions.PADRoster;
import champions.abilityTree.*;
import champions.interfaces.SpecialParameter;
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


 /*
 * @author  twalker
 * @version
 */
public class PADSpecialParameterNode extends PADTreeTableNode {
    
    /** Holds value of property powerName. */
    private String name;
    
    /** Creates new PADPowerNode */
    public PADSpecialParameterNode(String name) {
        setName(name);
        
    }
    
    /** Getter for property powerName.
     * @return Value of property powerName.
     */
    public String getName() {
        return name;
    }
    
    /** Setter for property powerName.
     * @param powerName New value of property powerName.
     */
    public void setName(String name) {
        this.name = name;
        setUserObject(name);
        
        SpecialParameter p = PADRoster.getSharedSpecialParameterInstance(name);
        Icon icon = p.getIcon();
        setIcon(icon);
    }
    
    public boolean startDrag(TreeTable tree, TreePath path, DragSourceListener listener, DragGestureEvent dge) {
        if ( getName() != null ) {
            
            SpecialParameter o = PADRoster.getNewSpecialParameterInstance( name );
            
            if ( o != null ) {
                Transferable t = new ObjectTransferable(o, SpecialParameter.class);
                
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
            SpecialParameter a = PADRoster.getNewSpecialParameterInstance(name );
            ability.addSpecialParameter(a);
        }
    }
    
    /** Returns the PAD (Power, Skill, Adv, Lim, etc) associated with node.
     *
     * This should return a new instance of whatever the node represents.
     *
     * @return null if no PAD is associated, such as in the case of a folder.
     */
    public Object getPAD() {
        return PADRoster.getNewSpecialParameterInstance(name );
    }
}
