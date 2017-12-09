/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tjava;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @param <T>
 * @author twalker
 */
public class SingletonIterator<T> implements Iterator<T> {

    private T singleton;

    public SingletonIterator(T singleton) {
        this.singleton = singleton;
    }

    @Override
    public boolean hasNext() {
        return (singleton != null);
    }

    @Override
    public T next() {
        if ( singleton == null ) throw new NoSuchElementException();

        T result = singleton;
        singleton = null;
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
