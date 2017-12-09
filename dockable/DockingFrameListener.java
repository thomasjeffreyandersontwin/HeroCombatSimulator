/*
 * DockingFrameListener.java
 *
 * Created on January 1, 2001, 7:49 PM
 */

package dockable;

import java.util.EventListener;
import java.awt.event.*;
/**
 *
 * @author  unknown
 * @version
 */
public interface DockingFrameListener extends WindowListener {
    public void frameChange(FrameChangeEvent e);
}
