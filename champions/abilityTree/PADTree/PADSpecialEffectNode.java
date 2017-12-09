/*
 * PADPowerNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.abilityTree.PADTree;

import tjava.ObjectTransferable;
import champions.SpecialEffect;
import champions.abilityTree.*;
import treeTable.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import champions.*;
import champions.interfaces.*;
/**
 *
 * @author  twalker
 * @version
 */
public class PADSpecialEffectNode extends PADTreeTableNode {
    
    /** Holds value of property powerName. */
    private String name;
    
    /** Creates new PADPowerNode */
    public PADSpecialEffectNode(String powerName) {
        setName(powerName);
        
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
        
        SpecialEffect se = PADRoster.getSharedSpecialEffectInstance(name);
        Icon icon = se.getIcon();
        setIcon(icon);
    }
    
    public boolean startDrag(TreeTable tree, TreePath path, DragSourceListener listener, DragGestureEvent dge) {
        if ( getName() != null ) {
            
            SpecialEffect o = PADRoster.getNewSpecialEffectInstance( name );
            
            if ( o != null ) {
                Transferable t = new ObjectTransferable(o, SpecialEffect.class);
                
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
            SpecialEffect l = PADRoster.getNewSpecialEffectInstance( name );
            if ( ability.hasSpecialEffect( l.getName() ) == false ) {
                ability.addSpecialEffect( l );
            }
        }
    }
    
    /** Returns the PAD (Power, Skill, Adv, Lim, etc) associated with node.
     *
     * This should return a new instance of whatever the node represents.
     *
     * @return null if no PAD is associated, such as in the case of a folder.
     */
    public Object getPAD() {
        return PADRoster.getNewSpecialEffectInstance( name );
    }
    
}
