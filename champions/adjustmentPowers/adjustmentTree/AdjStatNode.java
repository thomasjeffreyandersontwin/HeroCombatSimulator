/*
 * PADPowerNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.adjustmentPowers.adjustmentTree;

import tjava.ObjectTransferable;
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
public class AdjStatNode extends AdjTreeTableNode {
    
    /** Holds value of property powerName. */
    private String name;
    
    private static Icon checkIcon;
    private static Icon xIcon;
    private static Icon statIcon;

    
    /** Creates new PADPowerNode */
    public AdjStatNode(String stat) {
        setName(stat);
        
        setupIcons();
        
        setIcon(statIcon);
        
        setTreeTableCellRenderer(1, IconTreeTableCellRenderer.getDefaultRenderer() );
    }
    
    protected void setupIcons() {
        statIcon = UIManager.getIcon("Stat.DefaultIcon");
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
        if ( getName() != null ) {
            
            Characteristic p = new Characteristic(name);
            
            if ( p != null ) {
                Transferable t = new ObjectTransferable(p, Characteristic.class);
                
                Point pt = dge.getDragOrigin();
                Rectangle bounds = tree.getPathBounds(path);
                Point offset = new Point(pt.x - bounds.x, pt.y - bounds.y);
                
                BufferedImage i = ((DefaultTreeTable)tree).buildDragImage(path);
                DefaultTreeTable.startDrag(i, offset);
                
                dge.startDrag(null, i, offset, t, listener);
                return true;
            }
        }
        return false;
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
