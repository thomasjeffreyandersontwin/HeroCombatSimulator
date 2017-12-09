/*
 * ContextMenuTarget.java
 *
 * Created on September 13, 2000, 4:36 PM
 */

package champions.interfaces;

import champions.event.*;
import java.util.EventListener;
/**
 *
 * @author  unknown
 * @version 
 */
public interface PADValueListener extends EventListener{ 
    public void PADValueChanged(PADValueEvent evt);
    public boolean PADValueChanging(PADValueEvent evt);
}
