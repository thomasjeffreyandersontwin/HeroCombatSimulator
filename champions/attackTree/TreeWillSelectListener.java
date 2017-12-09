/*
 * TreeWillSelectListener.java
 *
 * Created on October 31, 2001, 9:48 PM
 */

package champions.attackTree;

import java.util.EventListener;
import javax.swing.event.TreeSelectionEvent;

/**
 *
 * @author  twalker
 * @version 
 */
public interface TreeWillSelectListener extends EventListener {

    public boolean valueWillChange(TreeSelectionEvent tse);

}
