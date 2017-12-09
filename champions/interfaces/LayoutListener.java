/*
 * LayoutListener.java
 *
 * Created on November 14, 2000, 11:33 AM
 */

package champions.interfaces;

import champions.event.LayoutEvent;
import tjava.*;
import champions.event.*;
import java.util.EventListener;
/**
 *
 * @author  unknown
 * @version 
 */
public interface LayoutListener extends EventListener {
    public void layoutPerformed(LayoutEvent e);
}
