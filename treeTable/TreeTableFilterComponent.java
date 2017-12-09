/*
TreeTableFilterComponent.java
 *
Created on January 20, 2008, 3:41 PM
 *
To change this template, choose Tools | Template Manager
and open the template in the editor.
 */

package treeTable;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

/**
 *
 * @author twalker
 */
public class TreeTableFilterComponent extends JPanel {
    
    private EventListenerList listenerList = new EventListenerList();
    
    /** Creates a new instance of TreeTableFilterComponent */
    public TreeTableFilterComponent() {
    }
    
    public void addTreeTableFilterListener(TreeTableFilterListener l) {
        
        listenerList.add(TreeTableFilterListener.class, l);
    }
    
    public void removeTreeTableFilterListener(TreeTableFilterListener l) {
        listenerList.remove(TreeTableFilterListener.class, l);
    }
    
// Notify all listeners that have registered interest for
// notification on this event type.  The event instance
// is lazily created using the parameters passed into
// the fire method.
    
    protected void fireFilterEvent(final TreeTableFilterEvent event) {
        // Guaranteed to return a non-null array
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                Object[] listeners = listenerList.getListenerList();
                // Process the listeners last to first, notifying
                // those that are interested in this event
                for (int i = listeners.length-2; i>=0; i-=2) {
                    if (listeners[i]==TreeTableFilterListener.class) {
                        
                        ((TreeTableFilterListener)listeners[i+1]).filter(event);
                        
                        
                    }
                }
            }
        });
    }
    
    public void setFilter(Object filter) {
        
    }
    
}
