/*
 * ContextMenuListener.java
 *
 * Created on September 13, 2000, 4:32 PM
 */

package tjava;



import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPopupMenu;


/**
 *
 * @author  unknown
 * @version
 */
public class ContextMenu extends Object implements java.io.Serializable, MouseListener, PropertyChangeListener {

    private static ContextMenu defaultContextMenu = null;
    
    private static JPopupMenu popup;
    /** Creates new ContextMenuListener */
    public ContextMenu() {
    }

/*    public ContextMenu(Component c,Component t) {

        c.addMouseListener(this);
        setContextMenuListener(t);
    } */

   /** Invoked when the mouse has been clicked on a component.
     */
    public void mouseClicked(MouseEvent e) {
    }
    /** Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }
    /** Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }
    /** Invoked when the mouse enters a component.
     */
    public void mouseEntered(MouseEvent e) {
    }
    /** Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {
    }


    public void maybeShowPopup(MouseEvent e) {
        if ( e.isPopupTrigger()) {
            Component component;
            boolean trigger = false;
            boolean show;
            int index, count;
            Point thePoint = e.getPoint();
            
            popup = new MyPopupMenu();
            //popup.removeAll();

            component = e.getComponent();

            while ( component != null ) {
                if ( component instanceof ContextMenuListener ) {
                    show = ((ContextMenuListener)component).invokeMenu(popup,e.getComponent(), thePoint);
                    if ( show ) {
                        trigger = true;
                        popup.addSeparator();
                    }
                }
                thePoint.translate( component.getX(), component.getY() );
                component = component.getParent();
                
            }
            //contextMenuListener.InvokeMenu(e.getComponent(), e.getPoint());

            if ( trigger ) {
                int y = e.getY();
                // Remove the extra seperator...
                popup.remove( popup.getComponentCount() - 1 );

                if ( e.getComponent().isShowing() ) {
                    popup.show(e.getComponent(),e.getX(),y);
                    popup.addPropertyChangeListener(this);
                }
            }
            else {
                destroyMenu();
            }
        }
    }
    
    public static void addContextMenu(Component destinationComponent) {
        if ( defaultContextMenu == null ) defaultContextMenu = new ContextMenu();
        destinationComponent.addMouseListener(defaultContextMenu);
    }
    
    public static void removeContextMenu(Component destinationComponent) {
        if ( defaultContextMenu == null ) defaultContextMenu = new ContextMenu();
        destinationComponent.removeMouseListener(defaultContextMenu);
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if ( evt.getSource() == popup && "visible".equals(evt.getPropertyName()) 
            && evt.getNewValue().equals( Boolean.FALSE ) ) {
                destroyMenu();
        }
    }
    
    private void destroyMenu() {
        if ( popup != null ) {
            popup.removeAll();
            popup.removePropertyChangeListener(this);
            popup = null;
        }
    }
    
}