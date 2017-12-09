/*
 * GenericModifier.java
 *
 * Created on November 9, 2006, 5:45 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.genericModifiers;

/**
 *
 * @author 1425
 */
public interface GenericModifier {
    
    /** Apply a modifier adjustment to the current value. 
     *
     *  Apply a class dependent type of modification to the
     *  modifier.  This may, in fact, do nothing if the 
     *  modifier is not active.
     */
    public int applyModifier(int currentValue);
    
    /** Return the name of the modifier. */
    public String getName();
    
    /**
     * Returns a GenericModifierListPanel used to edit this modifier.
     * 
     *  This method should return a panel that is able to edit this
     *  modifiers settings (such as value, active, etc).  
     * 
     *  For now, this should be unique to this GenericModifier.
     */
    public GenericModifierPanel getEditingPanel();
    
}
