/*
 * BasicTreeTableUI.java
 *
 * Created on June 5, 2002, 1:10 PM
 */
package treeTable;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.AbstractLayoutCache;

/**
 *
 * @author  Trevor Walker
 */
public class BasicTreeTableUI extends BasicTreeUI {

    public static ComponentUI createUI(JComponent x) {
        return new BasicTreeTableUI();
    }

    /** Creates a new instance of BasicTreeTableUI */
    public BasicTreeTableUI() {
        super();
    }

    /**
     * Creates the object responsible for managing what is expanded, as
     * well as the size of nodes.
     * @return 
     */
    protected AbstractLayoutCache createLayoutCache() {
        if (isLargeModel() && getRowHeight() > 0) {
            return new TreeTableFixedHeightLayoutCache();
        }
        return new TreeTableVariableHeightLayoutCache();
    }
}
