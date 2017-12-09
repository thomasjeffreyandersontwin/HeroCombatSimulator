/*
 * AbilityTreeCellEditor.java
 *
 * Created on June 12, 2001, 11:52 AM
 */

package champions.abilityTree;

import champions.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.util.EventObject;

import champions.*;
import champions.interfaces.*;
/**
 *
 * @author  twalker
 * @version
 */
public class ImmediateEditor extends DefaultTreeCellEditor {
    
    AbilityTreeCellRenderer renderer;
    
    /** Creates new AbilityTreeCellEditor */
    public ImmediateEditor(JTree tree, AbilityTreeCellRenderer renderer, AbilityTreeCellEditor editor) {
        super(tree,renderer,editor);
        this.renderer = renderer;
    }
    
     /**
      * Returns true if <code>event</code> is null, or it is a MouseEvent
      * with a click count > 2 and inHitRegion returns true.
      */
    protected boolean canEditImmediately(EventObject event) {
        boolean rv = false;
        
        if((event instanceof MouseEvent) && SwingUtilities.isLeftMouseButton((MouseEvent)event)) {
            MouseEvent me = (MouseEvent)event;
            TreePath path = tree.getPathForLocation(me.getX(), me.getY());
            if ( path != null ) {
                int row = tree.getRowForLocation(me.getX(), me.getY());
                AbilityTreeNode node = (AbilityTreeNode)path.getLastPathComponent();
                TreeCellEditor editor = node.getTreeCellEditor(tree);
                
                if ( editor instanceof EnhancedCellEditor ) {
                    rv = ((EnhancedCellEditor)editor).canEditImmediately(event, tree, row, offset);
                }
                
                if ( rv == false ) {
                    rv = ((me.getClickCount() > 2) &&
                    inHitRegion(me.getX(), me.getY()));
                }
                return rv;
            }
        }
        return (event == null);
    }
    
        /**
         * If the realEditor will allow editing to stop, the realEditor is
         * removed and true is returned, otherwise false is returned.
         */
    public boolean stopCellEditing() {
        //   System.out.println("editing stopped");
        if(realEditor.stopCellEditing()) {
            if(editingComponent != null)
                editingContainer.remove(editingComponent);
            editingComponent = null;
            return true;
        }
        return false;
    }
    
    /**
     * Messages cancelCellEditing to the realEditor and removes it from this
     * instance.
     */
    public void cancelCellEditing() {
        //System.out.println("editing cancelled");
        realEditor.cancelCellEditing();
        if(editingComponent != null)
            editingContainer.remove(editingComponent);
        editingComponent = null;
    }
    
        /**
         * Returns true if <code>event</code> is a MouseEvent and the click
         * count is 1.
         */
    protected boolean shouldStartEditingTimer(EventObject event) {
        if( (event instanceof MouseEvent) &&
	    SwingUtilities.isLeftMouseButton((MouseEvent)event)) {
	    MouseEvent        me = (MouseEvent)event;

	    return (me.getClickCount() == 1 &&
		    inHitRegion(me.getX(), me.getY()));
	}
        return false;
    }
    
    protected void determineOffset(JTree tree, Object value,
				   boolean isSelected, boolean expanded,
				   boolean leaf, int row) {
	if(renderer != null) {
            renderer.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, true);
            editingIcon = ((AbilityTreeCellRenderer)renderer).getEditIcon();
	    if(editingIcon != null)
		offset = renderer.getIconTextGap() +
		         editingIcon.getIconWidth();
	    else
		offset = renderer.getIconTextGap();
	}
	else {
	    editingIcon = null;
	    offset = 0;
	}
    }
    
}
