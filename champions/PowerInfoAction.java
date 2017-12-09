/*
 * PowerInfoAction.java
 *
 * Created on August 15, 2001, 3:34 PM
 */

package champions;

import dockable.DockingPanel;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;



/** Action which displays a PowerInfoPanel inside a dockingPanel when triggered.
 *
 *
 * @author  twalker
 * @version 
 */
public class PowerInfoAction extends AbstractAction 
implements Action {

    /** Holds value of property power. */
    private Power power;
    
    /** Creates new PowerInfoAction */
    public PowerInfoAction() {
        super("Power Info");
    }

    /**
     * Invoked when an action occurs.
 */
    public void actionPerformed(ActionEvent e) {
        if ( power != null ) {
            DockingPanel dp = new DockingPanel();
            PowerInfoPanel pip = new PowerInfoPanel();
            pip.setPower(power);
            dp.getContentPane().add(pip);
            dp.setName( "Power Info: " + power.getName() );
            dp.dockIntoFrame();
            dp.setVisible(true);
        }
    }
    
    /**
     * Returns the enabled state of the <code>Action</code>. When enabled,
     * any component associated with this object is active and
     * able to fire this object's <code>actionPerformed</code> method.
     *
     * @return true if this <code>Action</code> is enabled
 */
    public boolean isEnabled() {
        return power != null;
    }
    
    /** Getter for property power.
     * @return Value of property power.
 */
    public Power getPower() {
        return power;
    }
    
    /** Setter for property power.
     * @param power New value of property power.
 */
    public void setPower(Power power) {
        this.power = power;
    }
    
}
