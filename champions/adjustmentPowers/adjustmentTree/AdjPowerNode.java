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
public class AdjPowerNode extends AdjTreeTableNode {
    
    private static Icon checkIcon;
    private static Icon xIcon;
    
    /** Holds value of property powerName. */
    private String name;
    
    /** Holds value of property power. */
    private Power power;
    
    /** Creates new PADPowerNode */
    public AdjPowerNode(String powerName) {
        setName(powerName);
        
        Power p = PADRoster.getPower(name);
        setPower(p);
        
        setupIcons();
        
        setTreeTableCellRenderer(1, IconTreeTableCellRenderer.getDefaultRenderer() );
    }
    
    /** Creates new PADPowerNode */
    public AdjPowerNode(Power power) {
        setPower(power);
        setName(power.getName());
        
        setupIcons();
        
        setTreeTableCellRenderer(1, IconTreeTableCellRenderer.getDefaultRenderer() );
    }
    
    protected void setupIcons() {
        checkIcon = UIManager.getIcon("Checked.DefaultIcon");
        xIcon = UIManager.getIcon("X.DefaultIcon");
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
        
        Power p = PADRoster.getPower(name);
        setPower(p);
        
    }
    
    public boolean startDrag(TreeTable tree, TreePath path, DragSourceListener listener, DragGestureEvent dge) {
        if ( power != null ) {
            Transferable t = new ObjectTransferable(getPower(), Power.class);

            Point pt = dge.getDragOrigin();
            Rectangle bounds = tree.getPathBounds(path);
            Point offset = new Point(pt.x - bounds.x, pt.y - bounds.y);

            BufferedImage i = ((DefaultTreeTable)tree).buildDragImage(path);
            DefaultTreeTable.startDrag(i, offset);

            dge.startDrag(null, i, offset, t, listener);
            return true;
        }
        return false;
    }
    
  /*  public void handleDoubleClick(AbilityTreeTable tree, TreePath abilityPath) {
      / AbilityTreeNode node = (AbilityTreeNode)abilityPath.getLastPathComponent();
   
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
    
   /* public int getColumnSpan(int column) {
        switch ( column ) {
            case 0:
                return 2;
        }
        return 1;
    } */
    
    public Object getValueAt(int column) {
        switch ( column ) {
            case 0:
                return getName();
            case 1:
                Power p = PADRoster.getPower(name);
                if ( p.isDynamic() == true ) {
                    return checkIcon;
                }
                else {
                    return xIcon;
                }
        }
        return null;
    }
    
    /** Getter for property power.
     * @return Value of property power.
     */
    public Power getPower() {
        return power;
    }    
    
    /** Setter for property power.
     * @param power New value of property power.
     */
    public void setPower(Power power) {
        this.power = power;
        
        if ( power != null ) {
            Icon icon = power.getIcon();
            setIcon(icon);
        }
    }    
    
}
