/*
 * PADAbstractEditor.java
 *
 * Created on October 9, 2000, 10:51 PM
 */

package champions;

import champions.event.*;
import champions.interfaces.*;
import javax.swing.*;
/**
 *
 * @author  unknown
 * @version
 */
public abstract class PADAbstractEditor extends javax.swing.JPanel {

    private javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();
    /** Holds value of property PADHelpWatcher. */
    private PADHelpWatcher PADHelpWatcher;
    /** Creates new PADAbstractEditor */
    public PADAbstractEditor() {
    }

    /**
     *  Adds a <code>PADValue</code> listener.
     *
     *  @param l  the <code>PADValueListener</code> to add
     */
    public void addPADValueListener(PADValueListener l) {
        listenerList.add(PADValueListener.class,l);
    }

    /**
     * Removes a <code>PADValue</code> listener.
     *
     * @param l  the <code>PADValueListener</code> to remove
     */
    public void removePADValueListener(PADValueListener l) {
        listenerList.remove(PADValueListener.class,l);
    }

    protected void firePADValueChanged(String key, Object value, Object old) {
        PADValueEvent e = null;
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==PADValueListener.class) {
                // Lazily create the event:
                if (e == null)
                e = new PADValueEvent(this,key,value, old);
                ((PADValueListener)listeners[i+1]).PADValueChanged(e);
            }
        }
    }

    protected boolean firePADValueChanging(String key, Object value, Object old) {
        PADValueEvent e = null;
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==PADValueListener.class) {
                // Lazily create the event:
                if (e == null)
                e = new PADValueEvent(this,key,value, old);
                if ( ((PADValueListener)listeners[i+1]).PADValueChanging(e) == false ) {
                    return false;
                }
            }
        }
        return true;
    }

    public void setValue(Object o) {

    }

    public void setHelp(String helpText, JTextArea helpPane) {
        if ( helpText != null && helpPane != null ) {
            PADHelpWatcher phw = new PADHelpWatcher(helpText, helpPane);
            setPADHelpWatcher(phw);
        }
        else {
            setPADHelpWatcher(null);
        }
    }

    /** Getter for property PADHelpWatcher.
     * @return Value of property PADHelpWatcher.
     */
    public PADHelpWatcher getPADHelpWatcher() {
        return PADHelpWatcher;
    }
    /** Setter for property PADHelpWatcher.
     * @param PADHelpWatcher New value of property PADHelpWatcher.
     */
    public void setPADHelpWatcher(PADHelpWatcher PADHelpWatcher) {
        if ( this.PADHelpWatcher != null ) {
            this.removeMouseListener(this.PADHelpWatcher);
        }
        this.PADHelpWatcher = PADHelpWatcher;
        if ( this.PADHelpWatcher != null ) {
            this.addMouseListener(this.PADHelpWatcher);
        }
    }
}
