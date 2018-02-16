/*
 * SpecialEffect.java
 *
 * Created on March 1, 2002, 4:16 PM
 */

package champions;

import champions.adjustmentPowers.Adjustable;
import champions.adjustmentPowers.AdjustmentClass;
import champions.interfaces.AbilityIterator;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.*;

/**
 *
 * @author  twalker
 * @version 
 */
public class SpecialEffect implements AdjustmentClass, Serializable
 {

    /** Holds value of property name. */
    private String name;
    
    /** Holds value of property icon. */
    private Icon icon;
    
    /**
     * Creates new SpecialEffect
     */
    public SpecialEffect(String name) {
        setName(name);
        setIcon( UIManager.getIcon( "SpecialEffect.DefaultIcon" ) );
        if(!PADRoster.padClassMap.containsKey(getName())) 
    	{
    		PADRoster.AddSpecialEffect(getName(), "Special Effects", getIcon().toString());
    	}
    }
    
    public SpecialEffect(String name, Icon icon) {
        setName(name);
        setIcon(icon);
        if(!PADRoster.padClassMap.containsKey(getName())) 
    	{
    		PADRoster.AddSpecialEffect(getName(), "Special Effects", getIcon().toString());
    	}
    }
    
    /** Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return name;
    }    
   
    /** Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }    
    
    /** Getter for property icon.
     * @return Value of property icon.
     */
    public Icon getIcon() {
        return icon;
    }
    
    /** Setter for property icon.
     * @param icon New value of property icon.
     */
    public void setIcon(Icon icon) {
        this.icon = icon;
    }
    
    public SpecialEffect newInstance() {
        return new SpecialEffect(name,icon);
    }
    
    public String toString() {
        return "SpecialEffect: " + name;
    }
    
    public boolean equals(Object that) {
        return (that instanceof SpecialEffect && this.getName().equals(((SpecialEffect)that).getName()));
    }

    public ArrayList<Adjustable> getAdjustablesForTarget(Target target, ArrayList<Adjustable> list) {
        AbilityIterator ai = target.getAbilities();
        while ( ai.hasNext() ) {
            Ability targetAbility = ai.nextAbility();
            
            if ( targetAbility.hasSpecialEffect( getName() ) ) {
                // Should we break here?
                // Can there be multiple ones?
                if ( list == null ) list = new ArrayList<Adjustable>();
                list.add(targetAbility);
            }
        }
        return list;
    }
    
}
