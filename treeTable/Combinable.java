/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treeTable;

/** An object that knows how to be combined with other objects.
 *
 * @param <T> Type of the combinable.
 * @author twalker
 */
public interface Combinable {
    /** Attempt to combine two objects into one.
     * 
     * If the objects can be combined, the new object should be returned.  
     * 
     * If the objects can not be combined but both should be kept, return null.
     * 
     * To keep only a single object (possible after merging them), return
     * the one you wish to keep.
     * 
     * @param that Other object to possibly combine with.
     * @return New object or either this or that or null.
     * @throws CombinablesIncompatibleException indicates that the two objects
     * can not coexist and both should be ignored.
     */
    public Combinable combine(Combinable that) throws CombinablesIncompatibleException;
}
