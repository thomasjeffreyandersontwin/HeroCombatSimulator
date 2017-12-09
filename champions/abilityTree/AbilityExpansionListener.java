/*
 * AbilityExpansionListener.java
 *
 * Created on June 14, 2001, 8:00 PM
 */

package champions.abilityTree;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import java.io.*;
/**
 *
 * @author  twalker
 * @version 
 */
public class AbilityExpansionListener extends java.lang.Object 
implements TreeExpansionListener, Serializable {

    /** Creates new AbilityExpansionListener */
    public AbilityExpansionListener() {
    }

    /**
     * Called whenever an item in the tree has been expanded.
 */
    public void treeExpanded(TreeExpansionEvent event) {
        Object node = event.getPath().getLastPathComponent();
        Object t = event.getSource();
        if ( (node instanceof AbilityNode || node instanceof AbilityImportNode) && t instanceof JTree ) {
            AbilityTreeNode an = (AbilityTreeNode)node;
            JTree tree = (JTree)t;
            TreePath p;
            AbilityTreeNode child;
            
            int index;
            for(index = 0;index<an.getChildCount();index++) {
                child = (AbilityTreeNode)an.getChildAt(index);
                if ( child instanceof SpecialParameterNode || child instanceof SpecialEffectsNode ) continue;
                p = event.getPath().pathByAddingChild( child );
                tree.expandPath( p);
            }
        }
    }
    
    /**
     * Called whenever an item in the tree has been collapsed.
 */
    public void treeCollapsed(TreeExpansionEvent event) {
    }
    
}
