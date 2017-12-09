/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tjava;

import java.util.Iterator;

/**
 *
 * @param <T> 
 * @author twalker
 */
public class CompoundIterator<T> implements Iterator<T> {

    private Iterator<T> it1;
    private Iterator<T> it2;

    public CompoundIterator(Iterator it1, Iterator it2) {
        this.it1 = it1;
        this.it2 = it2;
    }

    @Override
    public boolean hasNext() {
        return it1.hasNext() || it2.hasNext();
    }

    @Override
    public T next() {
        if ( it1.hasNext() ) {
            return it1.next();
        }
        else {
            return it2.next();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
