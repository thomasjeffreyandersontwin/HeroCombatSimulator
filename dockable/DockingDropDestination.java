/*
 * DockingDropDestination.java
 *
 * Created on September 9, 2005, 1:43 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package dockable;

import java.awt.Point;
import java.awt.dnd.DropTargetDropEvent;

/**
 *
 * @author 1425
 */
public interface DockingDropDestination {
    /** Returns a DockingDropPanel with the bounds set appropriate to display user feedback.
     *
     * The point will be in frame coordinates (LayerPane specifically).
     */
    public DockingDropPanel getDropPanel();
    public void handleDrop(DropTargetDropEvent e);
    
    /** Return whether the indicated point is still within this drop destination.
     *
     * This method should determine whether the point is still within this drop destination
     * or if it has possibly moved into a different drop destination.
     *
    * The point will be in frame coordinates (LayerPane specifically).
     */
    public boolean isInBounds(Point p);
}
