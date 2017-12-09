/*
 * TreeTableFilterListener.java
 *
 * Created on January 20, 2008, 3:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package treeTable;

import java.util.EventListener;

/**
 *
 * @author twalker
 */
public interface TreeTableFilterListener extends EventListener {
    public void filter(TreeTableFilterEvent event);
}
