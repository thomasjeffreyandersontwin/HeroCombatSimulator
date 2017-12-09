/*
 * TargetListener.java
 *
 * Created on November 9, 2001, 1:59 PM
 */

package champions.interfaces;

import champions.event.*;
import java.util.*;
/**
 *
 * @author  twalker
 * @version 
 */
public interface TargetListener extends EventListener {
    public void targetSelected(TargetSelectedEvent e);
}

