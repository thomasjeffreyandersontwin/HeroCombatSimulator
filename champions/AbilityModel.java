/*
 * AbilityModel.java
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
public class AbilityModel extends DefaultButtonModel
implements ButtonModel, PropertyChangeListener {
    
    private static final int DEBUG = 0;
    
    /** Holds value of property ability. */
    private Ability ability;
    private Target source;
    /** Creates new AbilityModel */
    
    private boolean armed = false;
    private boolean pressed = false;
    private boolean rollover = false;
    private boolean selected = false;
    
    public AbilityModel() {
        if ( DEBUG > 0 ) System.out.println("AbilityModel.AbiltyModel: created Ability Model.");
    }
    /** Getter for property ability.
     * @return Value of property ability.
     */
    public Ability getAbility() {
        return ability;
    }
    /** Setter for property ability.
     * @param ability New value of property ability.
     */
    public void setAbility(Ability ability) {
        if ( this.ability != null ) {
            //this.ability.removePropertyChangeListener(this);
        }
        
        this.ability = ability;
        
        if ( this.ability != null ) {
            //this.ability.adjustDice();
           // this.ability.addPropertyChangeListener(this);
            if ( ability.isAutoSource() ) {
                source = null;
            }
            else {
                source = ability.getSource();
            }
        }
    }
    
    public void setPressed(boolean b) {
        
        if ( pressed != b ) {
            pressed = b;
            
            this.fireStateChanged();
            if ( armed && b == false ) {
                if (ability.isActivated(source) ) {
                    fireActionPerformed( new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Deactivate Ability"));
                }
                else if ( ability.isEnabled(source) ) {
                    fireActionPerformed( new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Trigger Ability"));
                }
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
        return armed && abilityButtonUsable();
    }
    public boolean isRollover () {
        return rollover;
    }
    public boolean isSelected () {
        if ( ability == null ) return false;
        
        return ability.isActivated(source);
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        this.fireStateChanged();
    }
    
    public boolean isEnabled() {
        return abilityButtonUsable();
    }
    
    private boolean abilityButtonUsable() {
        if ( ability == null ) return false;
        
        if ( ability.isActivated(source) ) return true;
        if ( ability.isEnabled(source) ) return true;
        
        return false;
    }
    
}