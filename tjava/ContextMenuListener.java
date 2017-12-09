/*
 * ContextMenuTarget.java
 *
 * Created on September 13, 2000, 4:36 PM
 */

package tjava;

import java.awt.Point;
import javax.swing.JPopupMenu;
import java.awt.Component;
/**
 *
 * @author  unknown
 * @version 
 */
public interface ContextMenuListener { 
    public boolean invokeMenu(JPopupMenu popup, Component inComponent, Point inPoint);
}
