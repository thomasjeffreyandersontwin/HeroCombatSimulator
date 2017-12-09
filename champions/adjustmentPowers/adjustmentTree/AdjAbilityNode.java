/*
 * PADPowerNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.adjustmentPowers.adjustmentTree;

import tjava.ObjectTransferable;
import champions.Power;
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
public class AdjAbilityNode extends AdjTreeTableNode {
    
    /** Holds value of property ability. */
    private Ability ability;
    
    private static Icon checkIcon;
    private static Icon xIcon;
    
    /** Creates new PADPowerNode */
    public AdjAbilityNode(Ability ability) {
        setAbility(ability);
        
        setupIcons();
        
        setTreeTableCellRenderer(1, IconTreeTableCellRenderer.getDefaultRenderer() );
    }
    
    protected void setupIcons() {
        checkIcon = UIManager.getIcon("Checked.DefaultIcon");
        xIcon = UIManager.getIcon("X.DefaultIcon");
    }
    
    public boolean startDrag(TreeTable tree, TreePath path, DragSourceListener listener, DragGestureEvent dge) {
        if (  getAbility() != null ) {
            Transferable t = new ObjectTransferable(ability, Ability.class);
            
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
    
    /** Getter for property ability.
     * @return Value of property ability.
     */
    public Ability getAbility() {
        return ability;
    }
    
    /** Setter for property ability.
     * @param ability New value of property ability.
     */
    public void setAbility(Ability ability) {
        this.ability = ability;
    }
    
  /*  public void handleDoubleClick(AbilityTreeTable tree, TreePath abilityPath) {
        AbilityTreeNode node = (AbilityTreeNode)abilityPath.getLastPathComponent();
   
        while ( node != null && ! ( node instanceof SublistNode ) ) {
            node = (AbilityTreeNode)node.getParent();
        }
   
        if ( node != null ) {
            //  String sublist = ((SublistNode)node).getSublist();
            Ability ability = PADRoster.newPowerInstance(name);
            // ability.setSublist(sublist);
            ((SublistNode)node).addAbility(ability);
        }
    } */
    
    public Object getValueAt(int column) {
        switch (column) {
            case 0:
                return ability.getName();
            case 1:
                Power p = ability.getPower();
                if ( p.isDynamic() == true ) {
                    return checkIcon;
                }
                else {
                    return xIcon;
                }
        }
        return null;
    }
    
}
