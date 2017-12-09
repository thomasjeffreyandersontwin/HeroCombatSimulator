/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treeTable;

import java.awt.event.KeyEvent;
import javax.swing.JComponent;

/**
 *
 * @author twalker
 */
public interface TreeTableFilterComponentProvider {

    public JComponent getComponent();

    public void addTreeTableFilterListener(TreeTableFilterListener l);

    public void clearFilter();

    public void handleKeyEvent(KeyEvent keyEvent);

    public void removeTreeTableFilterListener(TreeTableFilterListener l);

}
