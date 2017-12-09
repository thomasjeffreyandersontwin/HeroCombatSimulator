/*
 * GenericModifierList.java
 *
 * Created on November 9, 2006, 5:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.genericModifiers;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author 1425
 */
public class GenericModifierList implements Iterable<GenericModifier>{
    
    private ArrayList<GenericModifier> modifierList;
    private String name = "";
    
    
    /** Creates a new instance of GenericModifierList */
    public GenericModifierList() {
        modifierList = new ArrayList<GenericModifier>();
    }
    
    public GenericModifierList(String name) {
        setName(name);
        modifierList = new ArrayList<GenericModifier>();
    }
    
    /** Returns the final value of the list after all the modifiers have been applied.
     *
     */
    public int getValue(int startValue) {
        int currentValue = startValue;
        
        for(GenericModifier m : modifierList) {
            currentValue = m.applyModifier(currentValue);
        }
        return currentValue;
    }
    
    /** Returns the final value of the list after all the modififer have been applied.
     *
     *  Since no starting value is specified, it is assumed that the modifier list
     *  contains all of the information.  A starting value of 0 is used in this case.
     */
    public int getValue() {
        return getValue(0);
    }

    public ArrayList<GenericModifier> getModifierList() {
        return modifierList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Iterator<GenericModifier> iterator() {
        return modifierList.iterator();
    }
    
    /** Adds a GenericModifier to the list.  
     *
     * This will add the modifiers to the list in the order
     * taht add is called.  
     */
    public void addGenericModifier(GenericModifier modifier) {
        modifierList.add(modifier);
    }
    
    public boolean removeGenericModifier(GenericModifier modifier) {
        return modifierList.remove(modifier);
    }
    
    public GenericModifier getGenericModifier(String name) {
        for(GenericModifier gm : modifierList){
            if ( gm.getName().equals(name) ) {
                return gm;
            }
        }
        return null;
    }
    
    public String toString() {
        
        return "GenericModifierList [" + name + ", value = " + getValue() + "]";
        
    }
    
}
