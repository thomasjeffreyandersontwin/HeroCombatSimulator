/*
 * AndFilter.java
 *
 * Created on April 4, 2004, 7:11 PM
 */

package champions.filters;
import tjava.Filter;

/**
 *
 * @author  1425
 */
public class AndFilter<E> implements Filter<E> {
    
    protected Filter<E> filter1;
    protected Filter<E> filter2;
    
    /**
     * Creates a new instance of AndFilter 
     */
    public AndFilter(Filter<E> filter1, Filter<E> filter2) {
        this.filter1 = filter1;
        this.filter2 = filter2;
    }
    
    public boolean includeElement(E object) {
        return (filter1 == null || filter1.includeElement(object)) && (filter2 == null || filter2.includeElement(object));
    }
    
    /**
     * Getter for property filter1.
     * @return Value of property filter1.
     */
    public Filter<E> getFilter1() {
        return filter1;
    }
    
    /**
     * Setter for property filter1.
     * @param filter1 New value of property filter1.
     */
    public void setFilter1(Filter<E> filter1) {
        this.filter1 = filter1;
    }
    
    /**
     * Getter for property filter2.
     * @return Value of property filter2.
     */
    public Filter<E> getFilter2() {
        return filter2;
    }
    
    /**
     * Setter for property filter2.
     * @param filter2 New value of property filter2.
     */
    public void setFilter2(Filter<E> filter2) {
        this.filter2 = filter2;
    }
    
}
