/*
 * AbilityIterator.java
 *
 * Created on February 8, 2002, 1:05 PM
 */

package champions.interfaces;

import champions.*;
import champions.SpecialEffect;
import java.util.Iterator;
import java.util.NoSuchElementException;
/**
 *
 * @author  twalker
 * @version 
 */
public interface SpecialEffectIterator extends Iterator {
    /** Returns the next Ability.
     */
    public SpecialEffect nextSpecialEffect() throws NoSuchElementException;
    
    /** Returns true if there are more abilities to iterate through.
     */
    public boolean hasNext();
}

