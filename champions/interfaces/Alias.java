/*
 * Alias.java
 *
 * Created on February 13, 2007, 5:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.interfaces;

import java.io.Serializable;

/** Provides an Alias to an object.
 *
 *  This class provides a method to refer to an object by name without 
 *  actually having the object on hand.  This is generally used to decouple
 *  object during serialization.
 *
 *  Aliases can either be hard or soft.  Hard aliases are the
 *  object itself.  Soft alias hold an identifier for the object and
 *  know how to find the object if necessary.  Soft alias should only
 *  serialize the identifier, not the actual referent.
 *
 * @author twalker
 */
public interface Alias<E> {
    
    /** The referent is the actual object this alias refers to.
     *
     *  If the identifier is set incorrectly, the referent may not exist.
     */
    public E getAliasReferent();
    
    /** Returns the string identifying the referent for this alias.
     *
     */
    public String getAliasIdentifier();
    
    /**
     * Sets the string identifying the referent for this alis.
     * 
     * In some case, when the alias has already been instantiated by a call,
     * setAliasIdentifier will throw an IllegalArgumentException if the new identifier
     * does not agree with the identifier for the referent.
     */
    public void setAliasIdentifier(String identifier) throws IllegalArgumentException;
}
