/*
p * AttackTreePanel.java
 *
 * Created on November 14, 2001, 7:31 PM
 */

package champions.attackTree;

import javax.swing.JPanel;

/**
 *
 * @author  twalker
 * @version 
 */
public interface AttackTreeInputPanel {
    public JPanel getPanel();
    public void showPanel(AttackTreePanel atip);
    public void hidePanel();
}

