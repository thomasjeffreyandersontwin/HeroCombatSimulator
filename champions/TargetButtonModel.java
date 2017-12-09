/*
 * targetModel.java
 *
 * Created on September 17, 2000, 4:31 PM
 */

package champions;

import javax.swing.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.event.*;
/**
 *
 * @author  unknown
 * @version
 */
public class TargetButtonModel extends DefaultButtonModel implements ButtonModel {


    /** Holds value of property target. */
    private Target target;
    /** Creates new targetModel */

    private boolean armed = false;
    private boolean pressed = false;
    private boolean rollover = false;
    private boolean selected = false;

    public TargetButtonModel() {
    }
    /** Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        return target;
    }
    /** Setter for property Target.
     * @param Target New value of property Target.
     */
    public void setTarget(Target target) {
        if ( this.target != null ) {
            //this.target.removePropertyChangeListener(this);
        }

        this.target = target;
        
        if ( this.target != null ) {
           // this.target.addPropertyChangeListener(this);
        }
    }

    public void setPressed(boolean b) {

        if ( pressed != b ) {
            pressed = b;

            this.fireStateChanged();
            if ( armed && b == false  ) {

                fireActionPerformed( new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Trigger Target"));
            }
        }
    }

    public void setArmed(boolean b) {
        if ( isEnabled() == false ) {
            b = false;
        }

        if ( armed != b ) {
            armed = b;

            this.fireStateChanged();
        }
    }

    public void setRollover(boolean b) {
        if ( isEnabled() == false ) {
            b = false;
        }
        if ( rollover != b ) {
            rollover = b;
            this.fireStateChanged();
        }
    }

    public void setSelected(boolean b) {
        selected = b;
    }

    public boolean isPressed () {
        return pressed;
    }

    public boolean isArmed () {
        return armed;
    }
    public boolean isRollover () {
        return rollover;
    }
    public boolean isSelected () {
        if ( target == null ) return false;
        return false;
    }

    public boolean isEnabled() {
        if ( target == null ) return false;

        return true;
    }

}