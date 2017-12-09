/*
 * DockingPanelListener.java
 *
 * Created on January 5, 2001, 5:07 PM
 */

package dockable;

import java.util.EventListener;
/**
 *
 * @author  unknown
 * @version 
 */
public interface DockingPanelListener extends EventListener {
    public void layoutChanged(javax.swing.event.ChangeEvent e);
    public void frameChanged(DockingPanel panel, DockingFrame oldFrame, DockingFrame newFrame);
}
