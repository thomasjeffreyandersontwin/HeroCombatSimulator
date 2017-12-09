/*
 * AbilityIterator.java
 *
 * Created on February 8, 2002, 1:05 PM
 */

package champions.interfaces;

import champions.Ability;
import java.util.Iterator;
import java.util.NoSuchElementException;
/**
 *
 * @author  twalker
 * @version 
 */
public interface AbilityIterator extends Iterator<Ability> {
    /** Returns the next Ability.
     */
    public Ability nextAbility() throws NoSuchElementException;
    
    /** Returns true if there are more abilities to iterate through.
     */
    public boolean hasNext();
}

