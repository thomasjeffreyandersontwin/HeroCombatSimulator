/*
AbstractTreeTableFilterComponentProvider.java
 *
Created on January 20, 2008, 3:41 PM
 *
To change this template, choose Tools | Template Manager
and open the template in the editor.
 */

package treeTable;

import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

/**
 *
 * @author twalker
 */
public abstract class AbstractTreeTableFilterComponentProvider extends JPanel implements TreeTableFilterComponentProvider {
    
    private EventListenerList treeTableListenerList = new EventListenerList();
    
    /** Creates a new instance of AbstractTreeTableFilterComponentProvider */
    public AbstractTreeTableFilterComponentProvider() {
    }

    @Override
    public JComponent getComponent() {
        return this;
    }
    
    @Override
    public void addTreeTableFilterListener(TreeTableFilterListener l) {
        
        treeTableListenerList.add(TreeTableFilterListener.class, l);
    }
    
    @Override
    public void removeTreeTableFilterListener(TreeTableFilterListener l) {
        treeTableListenerList.remove(TreeTableFilterListener.class, l);
    }
    
// Notify all listeners that have registered interest for
// notification on this event type.  The event instance
// is lazily created using the parameters passed into
// the fire method.
    
    protected void fireFilterEvent(final TreeTableFilterEvent event) {
        // Guaranteed to return a non-null array
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                Object[] listeners = treeTableListenerList.getListenerList();
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
    
    @Override
    public abstract void handleKeyEvent(KeyEvent keyEvent);

    @Override
    public abstract void clearFilter();
    
}
