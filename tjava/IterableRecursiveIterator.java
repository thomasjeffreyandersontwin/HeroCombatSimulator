/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tjava;

import java.util.Iterator;
import tjava.AbstractRecursiveIterator;

/** This class defines an recursive iterator in which all of the elements are Iterables of the base type S.
 *
 * By default, this call will only recurse a single level.  To be truly recursive, the
 * iterators of the elements must also be recursive.
 *
 * @param <S> Base type returned during iteration.
 * @author twalker
 */
public class IterableRecursiveIterator<S extends Iterable<S>> extends AbstractRecursiveIterator<S> {

    public IterableRecursiveIterator(Iterable<? extends S> parentIterable) {
        super(parentIterable);
    }

    public IterableRecursiveIterator(Iterator<? extends S> parentIterator) {
        super(parentIterator);
    }



    @Override
    protected Iterator<? extends S> getChildIterator(S elementOfParentIterator) {
        return elementOfParentIterator.iterator();
    }

    @Override
    protected boolean isElement(S prospectiveElement) {
        return true;
    }

}
