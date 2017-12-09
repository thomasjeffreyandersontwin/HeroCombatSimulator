/*
 * AttackTreeDockingPanel.java
 *
 * Created on September 11, 2005, 5:08 PM
 *
 */

package champions.attackTree;

import champions.SavedDockingPanel;
import dockable.DockingFrame;

/**
 *
 * @author 1425
 */
public class AttackTreeDockingPanel extends SavedDockingPanel{
    
    /** Creates a new instance of AttackTreeDockingPanel */
    public AttackTreeDockingPanel() {
        super("attackTreeDP");
        
        AttackTreePanel atp = new AttackTreePanel();
        getContentPane().add(atp);
        atp.setDockingPanel(this);
        
        setMinimizable(true);
        setName("Attack Info");
        setPreferredMinimizedLocation(DockingFrame.LEFT_SIDE);
        
        AttackTreePanel.setDefaultPanel(atp);
    }
    
    public AttackTreeDockingPanel(AttackTreePanel atp) {
        super("attackTreeDP");
        
        getContentPane().add(atp);
        atp.setDockingPanel(this);
        
         setMinimizable(true);
         
         setName("Attack Info");
         setPreferredMinimizedLocation(DockingFrame.LEFT_SIDE);
    }
    
}
