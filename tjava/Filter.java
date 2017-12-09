/*
 * AbilityFilter.java
 *
 * Created on April 4, 2004, 7:08 PM
 */

package tjava;

/**
 *
 * @param <E> Type of objects accepted by the filter.
 * @author Trevor Walker
 */
public interface Filter<E> {
    /** Indicates the Object filterObject should be included by the filter.
     *
     * @param filterObject Object to be examined for filtering.
     * @return True to include filterObject, False to exclude it.
     */
    public boolean includeElement(E filterObject);
}
