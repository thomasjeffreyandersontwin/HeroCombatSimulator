/*
 * BattleListener.java
 *
 * Created on September 14, 2000, 2:06 PM
 */

package champions.interfaces;

import java.util.EventListener;

import champions.event.*;
import javax.swing.event.*;
import java.beans.*;
/**
 *
 * @author  unknown
 * @version 
 */
public interface RosterListener extends EventListener {
    
    public void rosterAdd(RosterAddEvent e);
    
    public void rosterRemove(RosterRemoveEvent e);
    
    public void rosterChange(ChangeEvent e);
    
    public void propertyChange(PropertyChangeEvent pce);
}
