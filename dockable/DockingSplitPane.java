/*
 * DockingSplitPane.java
 *
 * Created on January 6, 2001, 2:10 PM
 */

package dockable;

import java.awt.*;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 *
 * @author  unknown
 * @version
 */
public class DockingSplitPane extends javax.swing.JSplitPane {

  //  private String uiClassID = "DockingSplitPaneUI";
    
    /**
     * Creates new DockingSplitPane 
     */
    public DockingSplitPane() {
   //     setupDockingSplitPaneUI();
    }
    
    public void updateUI() {
        setUI( DockingSplitPaneUI.createUI(this));
        revalidate();
    }
    
    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return the string "SplitPaneUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     * @beaninfo
     *       expert: true
     *  description: A string that specifies the name of the L&F class.
     */
    public String getUIClassID() {
        return "DockingSplitPaneUI";
    }

    public int getMinimumDividerLocation() {
        int       minLoc = 0;

        Insets    insets = getInsets();
        if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
            minLoc = 0;
        } else {
            minLoc = 0;
        }
        if(insets != null) {
            if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                minLoc += insets.left;
            } else {
                minLoc += insets.top;
            }
        }

        return minLoc;
    }

    public int getMaximumDividerLocation() {
        Dimension splitPaneSize = getSize();
        int       maxLoc = 0;

        Insets    insets = getInsets();

        if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
            maxLoc = splitPaneSize.width;
        } else {
            maxLoc = splitPaneSize.height;
        }
        maxLoc -= dividerSize;
        if(insets != null) {
            if(orientation == JSplitPane.HORIZONTAL_SPLIT) {
                maxLoc -= insets.right;
            } else {
                maxLoc -= insets.top;
            }
        }

        return Math.max(getMinimumDividerLocation(), maxLoc);
    }
    

}