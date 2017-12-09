/*
 * OrFilter.java
 *
 * Created on April 4, 2004, 7:11 PM
 */

package champions.filters;
import tjava.Filter;

/**
 *
 * @author  1425
 */
public class OrFilter<E> implements Filter<E> {
    
    protected Filter<E> filter1;
    protected Filter<E> filter2;
    
    /**
     * Creates a new instance of OrFilter 
     */
    public OrFilter(Filter<E> filter1, Filter<E> filter2) {
        this.filter1 = filter1;
        this.filter2 = filter2;
    }
    
    public boolean includeElement(E object) {
        return filter1.includeElement(object) || filter2.includeElement(object);
    }
    
}
