/*
 * SharedPopupAction.java
 *
 * Created on April 25, 2004, 11:16 PM
 */

package tjava;

import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 *
 * @author  Trevor Walker
 */
public abstract class SharedPopupAction extends AbstractAction {
    
    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    public SharedPopupAction() {
    }
    
    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a default icon.
     */
    public SharedPopupAction(String name) {
        super(name);
    }

    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a the specified icon.
     */
    public SharedPopupAction(String name, Icon icon) {
	super(name,icon);
    }
    
    /**
     * Adds a <code>PropertyChangeListener</code> to the listener list.
     * The listener is registered for all properties.
     * <p>
     * A <code>PropertyChangeEvent</code> will get fired in response to setting
     * a bound property, e.g. <code>setFont</code>, <code>setBackground</code>,
     * or <code>setForeground</code>.
     * Note that if the current component is inheriting its foreground, 
     * background, or font from its container, then no event will be 
     * fired in response to a change in the inherited property. <P>
     *
     * The SharedPopupAction only allows a single listener at a time.  This
     * is important for actions that are used as menu items in a popup menu
     * that is disposed of immediately after use.  This should only be used
     * when the action will be shared between transient popup menus (and only
     * then).
     *
     * @param listener  The <code>PropertyChangeListener</code> to be added
     *
     * @see Action#addPropertyChangeListener 
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
	changeSupport = new SwingPropertyChangeSupport(this);
        changeSupport.addPropertyChangeListener(listener);
    } 
    
}
