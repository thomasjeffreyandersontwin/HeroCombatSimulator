/*
 * OrAbilityFilter.java
 *
 * Created on April 4, 2004, 7:11 PM
 */

package champions.filters;
import tjava.Filter;

/**
 *
 * @author  1425
 */
public class NotFilter<E> implements Filter<E> {
    
    protected Filter<E> filter;
    
    /** Creates a new instance of OrAbilityFilter */
    public NotFilter(Filter<E> filter) {
        this.filter = filter;
    }
    
    public boolean includeElement(E object) {
        return ! filter.includeElement(object) ;
    }
    
}
