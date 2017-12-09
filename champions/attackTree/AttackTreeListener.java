/*
 * AttackTreeListener.java
 *
 * Created on October 26, 2007, 1:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.attackTree;

import java.util.EventListener;

/**
 *
 * @author twalker
 */
public interface AttackTreeListener extends EventListener {
    public void inputNeeded(AttackTreePanel attackTreePanel);
}
