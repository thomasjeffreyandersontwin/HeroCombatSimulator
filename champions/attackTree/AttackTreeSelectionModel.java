/*
 * AttackTreeSelectionListener.java
 *
 * Created on October 31, 2001, 9:38 PM
 */

package champions.attackTree;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author  twalker
 * @version 
 */
public class AttackTreeSelectionModel extends DefaultTreeSelectionModel {

    /** Creates new AttackTreeSelectionListener */
    public AttackTreeSelectionModel() {
        setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }
    
    public void addTreeWillSelectListener(TreeWillSelectListener l) {
        listenerList.add( TreeWillSelectListener.class, l);
    }
    
    public void removeTreeWillSelectListener(TreeWillSelectListener l) {
        listenerList.remove(TreeWillSelectListener.class, l);
    }
    
    /**
     * Notifies all listeners that are registered for
     * tree selection events on this object.  
     * @see addTreeSelectionListener
     * @see EventListenerList
     */
    protected boolean fireValueWillChange(TreeSelectionEvent e) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// TreeSelectionEvent e = null;
	// Process the listeners last to first, notifying
	// those that are interested in this event
        boolean result = true;
        
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==TreeWillSelectListener.class) {
		// Lazily create the event:
		// if (e == null)
		// e = new ListSelectionEvent(this, firstIndex, lastIndex);
		if ( ((TreeWillSelectListener)listeners[i+1]).valueWillChange(e) == false ) 
                    result = false;
	    }	       
	}
        return result;
    }

    public void addSelectionPaths(TreePath[] paths) {
        addSelectionPaths(paths, true);
    }
    
    public void setSelectionPaths(TreePath[] paths) {
        setSelectionPaths(paths,true);
    }
    
    public void addSelectionPaths(TreePath[] paths, boolean checkWithListeners) {
        if ( checkWithListeners ) {
            // Build a TreeSelectionEvent
            TreePath oldLeadPath = this.getLeadSelectionPath();
            
            // We can cheat cause we know that this is always a single selection model
            TreePath newLeadPath = null;
            
            if ( paths.length > 0 ) {
                newLeadPath = paths[0];
            }
            
            TreeSelectionEvent tse = new TreeSelectionEvent(this, newLeadPath, true, oldLeadPath, newLeadPath);
            
            if ( fireValueWillChange(tse) == true ) {
                super.setSelectionPaths(paths);
            }            
        }
        else {
            super.addSelectionPaths(paths);
        }
    }
    
    public void setSelectionPath(TreePath path, boolean checkWithListeners) {
        TreePath[] tp = new TreePath[] { path };
        setSelectionPaths(tp,checkWithListeners);
    }
    
    public void setSelectionPaths(TreePath[] paths, boolean checkWithListeners) {
        if ( checkWithListeners ) {
            // Build a TreeSelectionEvent
            TreePath oldLeadPath = this.getLeadSelectionPath();
            
            // We can cheat cause we know that this is always a single selection model
            TreePath newLeadPath = null;
            
            if ( paths.length > 0 ) {
                newLeadPath = paths[0];
            }
            
            TreeSelectionEvent tse = new TreeSelectionEvent(this, newLeadPath, true, oldLeadPath, newLeadPath);
            
            if ( fireValueWillChange(tse) == true ) {
                super.setSelectionPaths(paths);
            }
        }
        else {
            super.setSelectionPaths(paths);
        }
    }

}
