/*
 * SweepMessageGroup.java
 *
 * Created on February 23, 2008, 5:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.battleMessage;

import javax.swing.UIManager;

/**
 *
 * @author twalker
 */
public class SweepMessageGroup extends AbstractBattleMessageGroup{
    
    /** Creates a new instance of SweepMessageGroup */
    public SweepMessageGroup() {
        
        setMessageIcon( UIManager.getIcon("AttackTree.powerIcon") );
    }

    public void closeGroup() {
    }

    public String getMessage() {
        return "Sweep/rapid fire activated.";
    }
    
}
