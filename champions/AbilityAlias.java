/*
 * TargetAlias.java
 *
 * Created on March 20, 2004, 11:54 PM
 */

package champions;

import champions.interfaces.Alias;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;

/**
 *
 * @author  1425
 */
public class AbilityAlias implements Alias<Ability>, Serializable {
    
    transient protected WeakReference<Ability> abilityRef;
    
    protected TargetAlias targetAlias;
    protected String abilityName;
    
    private static final long serialVersionUID = -5389216947732728369L;
    
    /**
     * Creates a new instance of TargetAlias
     */
    public AbilityAlias(String targetName, String abilityName) {
        setAliasIdentifier(abilityName);
        setTargetName(targetName);
    }
    
    public AbilityAlias(Target target, Ability ability) {
        if ( ability != null ) {
            setAliasIdentifier(ability.getName());
            this.abilityRef = new WeakReference<Ability>(ability);
            
            if ( target != null ) {
                setTargetName(target.getName());
            }
        }
    }
    
    /** Getter for property targetName.
     * @return Value of property targetName.
     *
     */
    public java.lang.String getAliasIdentifier() {
        
        if ( abilityRef != null && abilityRef.get() != null ) {
            return abilityRef.get().getName();
        }
        else {
            return abilityName;
        }
    }
    
    /** Setter for property targetName.
     * @param targetName New value of property targetName.
     *
     */
    public void setAliasIdentifier(String abilityName) throws IllegalArgumentException {
        if ( abilityRef != null && abilityRef.get() != null && abilityRef.get().getName().equals(abilityName) == false ) {
            throw new IllegalArgumentException();
        }
        
        this.abilityName = abilityName;
    }
    
    /** Getter for property target.
     * @return Value of property target.
     *
     */
    public Ability getAliasReferent() {
        if ( abilityRef == null || abilityRef.get() == null ) {
            // First grab the target...
            Target target = targetAlias.getAliasReferent();
            if ( target != null ) {
                Ability ability = target.getAbility(abilityName);
                abilityRef = new WeakReference<Ability>(ability);
                return ability;
            }
            
            // If we got here, we couldn't find the target...
            return null;
        }
        
        return abilityRef.get();
    }
    
    public void setTarget(Target target) {
        targetAlias = new TargetAlias(target);
    }
    
    public void setTargetName(String targetName) {
        targetAlias = new TargetAlias(targetName);
    }
    
    public Target getTarget() {
        if ( targetAlias != null ) return targetAlias.getAliasReferent();
        else return null;
    }
    
    public String getTargetName() {
        if ( targetAlias != null ) return targetAlias.getAliasIdentifier();
        else return null;
    }

    public String toString() {
        if ( abilityRef != null && abilityRef.get() != null ) {
            return abilityRef.get().getName();
        }
        else {
            return abilityName;
        }
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        // Make sure we update the name appropriately prior
        // to writing it out...
        if ( abilityRef != null && abilityRef.get() != null ) abilityName = abilityRef.get().getName();
        out.defaultWriteObject();
    }
    
    public boolean equals(Object that) {
        if ( that instanceof Ability ) {
            return getAliasReferent().equals(that);
        }
        else if ( that instanceof Alias ) {
            return ((Alias)that).getAliasReferent().equals(getAliasReferent()); 
        }
        else {
            return false;
        }
    }
    
    /** Trigger the display of debugging of window.
     *
     */
//    public void displayDebugWindow() {
//        Target t = getAliasReferent();
//        
//        if ( abilityRef != null && abilityRef.get() != null) {
//            abilityRef.get().debugDetailList(abilityRef.get().toString());
//        }
//    }    

    public String toDebugString() {
        return "Alias: " + toString();
    }
        
    
}
