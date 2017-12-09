/*
 * NullAbilityIterator.java
 *
 * Created on February 8, 2002, 10:55 PM
 */

package champions;

import champions.interfaces.*;

import java.util.*;
/**
 *
 * @author  twalker
 * @version 
 */
public class NullAbilityIterator implements AbilityIterator {

    /** Creates new NullAbilityIterator */
    public NullAbilityIterator() {
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the iterator has more elements.
     */
    public boolean hasNext() {
        return false;
    }
    
    /**
     * Returns the next element in the interation.
     *
     * @return the next element in the iteration.
     * @exception NoSuchElementException iteration has no more elements.
     */
    public Ability next() {
        throw new NoSuchElementException();
    }
    
    /** Returns the next Ability.
     */
    public Ability nextAbility() throws NoSuchElementException {
        throw new NoSuchElementException();
    }
    
    /**
     *
     * Removes from the underlying collection the last element returned by the
     * iterator (optional operation).  This method can be called only once per
     * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
     * the underlying collection is modified while the iteration is in
     * progress in any way other than by calling this method.
     *
     * @exception UnsupportedOperationException if the <tt>remove</tt>
     * 		  operation is not supported by this Iterator.
     *
     * @exception IllegalStateException if the <tt>next</tt> method has not
     * 		  yet been called, or the <tt>remove</tt> method has already
     * 		  been called after the last call to the <tt>next</tt>
     * 		  method.
     */
    public void remove() {
        throw new IllegalStateException();
    }
    
}
