/*
 * PADPowerNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.adjustmentPowers.adjustmentTree;

import tjava.ObjectTransferable;
import champions.SpecialEffect;
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
public class AdjSpecialEffectNode extends AdjTreeTableNode {
    
    private static Icon checkIcon;
    private static Icon xIcon;
    
    /** Holds value of property powerName. */
    private String name;
    
    /** Holds value of property specialEffect. */
    private SpecialEffect specialEffect;
    
    /** Creates new PADPowerNode */
    public AdjSpecialEffectNode(String name) {
        setName(name);
        
        SpecialEffect se = PADRoster.getSharedSpecialEffectInstance(name);
        setSpecialEffect(se);
        
        setupIcons();
        setTreeTableCellRenderer(1, IconTreeTableCellRenderer.getDefaultRenderer());
    }
    
        /** Creates new PADPowerNode */
    public AdjSpecialEffectNode(SpecialEffect specialEffect) {
        
        setSpecialEffect(specialEffect);
        setName(specialEffect.getName());
        
        setupIcons();
        setTreeTableCellRenderer(1, IconTreeTableCellRenderer.getDefaultRenderer());
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
        
    }
    
    public boolean startDrag(TreeTable tree, TreePath path, DragSourceListener listener, DragGestureEvent dge) {
        if ( specialEffect != null ) {
            Transferable t = new ObjectTransferable(specialEffect, SpecialEffect.class);

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
    
    /** Getter for property specialEffect.
     * @return Value of property specialEffect.
     */
    public SpecialEffect getSpecialEffect() {
        return specialEffect;
    }
    
    /** Setter for property specialEffect.
     * @param specialEffect New value of property specialEffect.
     */
    public void setSpecialEffect(SpecialEffect specialEffect) {
        this.specialEffect = specialEffect;
        
        if ( specialEffect != null ) {
            Icon icon = specialEffect.getIcon();
            setIcon(icon);
        }
    }
    
    public Object getValueAt(int column) {
        switch ( column ) {
            case 0:
                return getName();
            case 1:
                return checkIcon;
        }
        return null;
    }
}
