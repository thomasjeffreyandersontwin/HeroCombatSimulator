/*
 * TargetingListener.java
 *
 * Created on October 29, 2000, 11:51 PM
 */

package champions.interfaces;

import champions.event.*;
import javax.swing.event.*;
import java.util.EventListener;
/**
 *
 * @author  unknown
 * @version 
 */
public interface TargetingListener extends EventListener {
    public void targetingEvent(TargetingEvent e);
}
