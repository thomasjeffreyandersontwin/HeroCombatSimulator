/*
 * IndexIterator.java
 *
 * Created on November 19, 2001, 11:14 PM
 */

package champions.interfaces;

import java.util.*;
/**
 *
 * @author  twalker
 * @version
 */
public interface IndexIterator {
    
    /** Creates new IndexIterator */
    public boolean hasNext();
    public int nextIndex() throws NoSuchElementException;
    
}
