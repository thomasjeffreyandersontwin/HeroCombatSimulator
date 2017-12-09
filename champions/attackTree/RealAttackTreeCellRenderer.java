/*
 * RealAttackTreeCellRenderer.java
 *
 * Created on January 5, 2004, 11:18 AM
 */

package champions.attackTree;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author  Trevor Walker
 */
public class RealAttackTreeCellRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer {
    
    /** Creates a new instance of RealAttackTreeCellRenderer */
    public RealAttackTreeCellRenderer() {
    }
   
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree,value,selected,expanded,leaf,row,hasFocus);
        
        if ( value != null ) {
            String textValue = this.getText();
            this.setText(textValue + " (" + value.getClass().getName() + ")");
        }
        return this;
    }
    
}
