/*
 * DockingSplitPaneDivider.java
 *
 * Created on September 6, 2005, 11:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package dockable;

import java.awt.Graphics;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 *
 * @author 1425
 */
public class DockingSplitPaneDivider extends BasicSplitPaneDivider {
    public DockingSplitPaneDivider(BasicSplitPaneUI ui) {
        super(ui);
    }
    
    public void paint(Graphics g) {
       // super.paint(g);
    }
}
