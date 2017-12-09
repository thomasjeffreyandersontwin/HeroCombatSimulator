package dockable;

import dockable.DockingSplitPaneDivider;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;


public class DockingSplitPaneUI extends BasicSplitPaneUI {
    /**
     * Creates a new MetalSplitPaneUI instance
     */
    public static ComponentUI createUI(JComponent x) {
        return new DockingSplitPaneUI();
    }
    
    /**
     * Creates the default divider.
     */
    public BasicSplitPaneDivider createDefaultDivider() {
        return new DockingSplitPaneDivider(this);
    }
}