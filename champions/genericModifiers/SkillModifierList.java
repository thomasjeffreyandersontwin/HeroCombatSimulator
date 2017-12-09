/*
 * SkillModifierList.java
 *
 * Created on November 10, 2006, 9:45 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.genericModifiers;

/**
 *
 * @author 1425
 */
public class SkillModifierList extends GenericModifierList {
    
    private BaseGenericModifier baseModifier = null;
    
    private IncrementGenericModifier userModifier = null;
    
    /**
     * Creates a new instance of SkillModifierList
     */
    public SkillModifierList() {
        
    }
    
    /**
     * Creates a new instance of SkillModifierList
     */
    public SkillModifierList(String name) {
        super(name);
    }

    public BaseGenericModifier getBaseModifier() {
        return baseModifier;
    }

    public void setBaseModifier(BaseGenericModifier baseModifier) {
        if ( this.baseModifier != baseModifier ) {
            if ( this.baseModifier != null ) {
                getModifierList().remove(this.baseModifier);
            }
            
            this.baseModifier = baseModifier;
            
            if ( this.baseModifier != null ) {
                getModifierList().add(0, baseModifier);
            }
        }
    }

    public IncrementGenericModifier getUserModifier() {
        return userModifier;
    }

    public void setUserModifier(IncrementGenericModifier userModifier) {
        if ( this.userModifier != userModifier ) {
            if ( this.userModifier != null ) {
                getModifierList().remove(this.userModifier);
            }
            
            this.userModifier = userModifier;
            
            if ( this.userModifier != null ) {
                // This should really be inserted after other
                // increment modifiers, but before multiplier
                // modifiers.
                getModifierList().add(userModifier);
            }
        }
    }
    
    /** Adds a GenericModifier to the list.  
     *
     * This will add the modifiers to the list in the order
     * taht add is called, with the exception of a userModifier
     * already having been added.  In this case, the new modifier
     * will be added second to last.
     */
    public void addGenericModifier(GenericModifier modifier) {
        int index = getModifierList().size();
        
        if ( userModifier != null ) {
            index --;
        }
        
        getModifierList().add(index, modifier);
    }
    
    public boolean removeGenericModifier(GenericModifier modifier) {
        if ( modifier == userModifier ) {
            setUserModifier(null);
            return true;
        }
        else if ( modifier == baseModifier ) {
            setBaseModifier(null);
            return true;
        }
        else {
            return getModifierList().remove(modifier);
        }
    }
    
}
