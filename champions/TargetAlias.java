/*
 * TargetAlias.java
 *
 * Created on March 20, 2004, 11:54 PM
 */

package champions;

import champions.interfaces.Alias;
import champions.interfaces.Debuggable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author  1425
 */
public class TargetAlias implements Alias<Target>, Serializable, Debuggable {
    
    transient protected WeakReference<Target> targetRef;
    
    protected String targetName;
    
    private static final long serialVersionUID = -5389216947732728369L;
    
    /**
     * Creates a new instance of TargetAlias
     */
    public TargetAlias(String targetName) {
        setAliasIdentifier(targetName);
    }
    
    public TargetAlias(Target target) {
        if ( target != null ) {
            setAliasIdentifier(target.getName());
            this.targetRef = new WeakReference<Target>(target);
        }
    }
    
    /** Getter for property targetName.
     * @return Value of property targetName.
     *
     */
    public java.lang.String getAliasIdentifier() {
        
        if ( targetRef != null && targetRef.get() != null ) {
            return targetRef.get().getName();
        }
        else {
            return targetName;
        }
    }
    
    /** Setter for property targetName.
     * @param targetName New value of property targetName.
     *
     */
    public void setAliasIdentifier(String targetName) throws IllegalArgumentException {
        if ( targetRef != null && targetRef.get() != null && targetRef.get().getName().equals(targetName) == false ) {
            throw new IllegalArgumentException();
        }
        
        this.targetName = targetName;
    }
    
    /** Getter for property target.
     * @return Value of property target.
     *
     */
    public Target getAliasReferent() {
        if ( (targetRef == null || targetRef.get() == null ) && Battle.currentBattle != null) {
            // Find the Target in the current battle...
            boolean done = false;
            Set<Target> s = Battle.currentBattle.getCombatants();
            Iterator<Target> i = s.iterator();
            while (!done && i.hasNext() ) {
                Target t = i.next();
                if ( t.getName().equals(targetName) ) {
                    setTarget(t);
                    return t;
                }
            }
            
            if ( !done ) {
                Set<Target> sp = Battle.currentBattle.getSpecialTargets();
                i = s.iterator();
                while (!done && i.hasNext() ) {
                    Target t = i.next();
                    if ( t.getName().equals(targetName) ) {
                        setTarget(t);
                        return t;
                    }
                }
            }
            
            // If we got here, we couldn't find the target...
            return null;
        }
        
        return targetRef.get();
    }
    
    private void setTarget(Target target) {
        targetRef = new WeakReference<Target>(target);
    }

    public String toString() {
        if ( targetRef != null && targetRef.get() != null ) {
            return "TargetAlias: " + targetRef.get().getName();
        }
        else {
            return "TargetAlias: Linked to " + targetName;
        }
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        // Make sure we update the name appropriately prior
        // to writing it out...
        if ( targetRef != null && targetRef.get() != null ) targetName = targetRef.get().getName();
        out.defaultWriteObject();
    }
    
    public boolean equals(Object that) {
        if ( that instanceof Target ) {
            return getAliasReferent() == that;
        }
        else if ( that instanceof Alias ) {
            return ((Alias)that).getAliasReferent() == getAliasReferent(); 
        }
        else {
            return false;
        }
    }
    
    /** Trigger the display of debugging of window.
     *
     */
    public void displayDebugWindow() {
        Target t = getAliasReferent();
        
        if ( targetRef != null && targetRef.get() != null) {
            targetRef.get().debugDetailList(targetRef.get().toString());
        }
    }    

    public String toDebugString() {
        return toString();
    }
        
    
}
