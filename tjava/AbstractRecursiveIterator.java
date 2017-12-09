/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tjava;

import java.util.Iterator;
import java.util.NoSuchElementException;

/** Implements a flexible recursive iterator.
 *
 * <p>This class provides a flexible resursive depth-first iteration structure,
 * based upon a provided parent iterator or iterable.
 *
 * <p>Every element in the parent iterator is check by getChildIterator(T)
 * to determine if the is a child iterator that needs to be traversed.
 *
 * <p>Additionally, every element in the parent iterator as well as all
 * elements returned via traversing child iterators are check against
 * isElement(T) to determine if they should be returned as elements
 * of the ResursiveIterator.
 *
 * <p>AbstractRecursiveIterator also supports removal of elements.  However,
 * elements can only be removed after calling next() but prior to calling
 * a subsequent hasNext().  This constraint is dictated by the possiblity of
 * performing a multiple step look-ahead through the parent/child iterators
 * hasNext() determines the next element of the resursive iterator.
 *
 * <p>AbstractRecursiveIterator is not thread-safe.  Also, it has little
 * ability to detect concurrent modifications to the underlying recursive
 * data structures.  If the parent or the current child being iterated
 * throws a ConcurrentModificationException (or other runtime exception),
 * that exception will be propogated.  However, if other children are modified
 * after iterator begins but while that child is not being actively iterated,
 * that modification will go undetected.
 * 
 * @param <T> Type of the iteratable object.
 * @author twalker
 */
public abstract class AbstractRecursiveIterator<T> implements Iterator<T> {

    /** Parent iterator.
     *
     * All elements returned during parent iteration are check via
     * isElement(T) to determine if they are eligible to be as
     * the next element of the recursive iterator.
     *
     * After being checked via isElement (and possibly returned as the
     * next element), all parent elements are also check via
     * getChildIterator(T) to determine if the element has children.
     */
    private Iterator<? extends T> parentIterator;

    /** Current child being iterated.
     *
     * Elements returned via childIterator will be check via
     * isElement(T) and returned as the next element of the
     * recursive iteration if isElement returns true.
     *
     * Elements returned via childIterator are NOT check via
     * getChildIterator(T), and thus do not result in further
     * iteration.  If recursive iteration in desired, the
     * childIterator should perform that iteration itself.
     *
     */
    private Iterator<? extends T> childIterator = null;

    /** Next element in the recursive iteration.
     *
     * This is the next element that will be returned in the iteration.
     *
     * An element of the parent/child iterator can only become next if isElement(T) returns true.
     *
     * After this element is returned via next(),it is set to null.
     *
     * setupNext() is in charge of setting up the next element, if there is one.
     */
    private T next = null;

    /** Last element retrieved from the parent iterator,
     * possibly not returned as an element of this iterator.
     *
     * The lastParentElement is stored so that setupNext() can check it
     * via getChildIterator(T) to determine if the element can be
     * recursively iterated.
     *
     * If lastParentElement is null, then the last retrieved parent
     * element was already check for subiteration.
     */
    private T lastParentElement = null;

    /** Indicates that we are in a state which would allow the last retrieved element to be removed.
     *
     * The AbstractResursiveIterator.
     *
     * Removal is still constrained by the parentIterator/childIterator's removal support.
     */
    private boolean removeable = false;

    /** Indicates that iteration has started.
     *
     * True if at least one call to hasNext() or next() has been performed.
     */
    private boolean iterationStarted = false;

    /** Creates an AbstractRecursiveIterator based upon the elements in parentIterator.
     *
     * @param parentIterator Iterator providing the underlying elements to iterate over.
     */
    public AbstractRecursiveIterator(Iterator<? extends T> parentIterator) {
        this.parentIterator = parentIterator;
    }
    
    /** Creates an AbstractRecursiveIterator based upon the elements obtained by iterating over parentIterable.
     * 
     * @param parentIterable Iteratable from which the parent iterator is
     * obtain, providing the underlying elements to iterate over.  parentIterable
     * is only used during construction to obtain the parent iterable and and
     * is not retain by the recursive iterator.
     * 
     */
    public AbstractRecursiveIterator(Iterable<? extends T> parentIterable) {
        this.parentIterator = parentIterable.iterator();
    }

    /** Resets the Iterator based on parentIterator.
     *
     * This is equivalent of creating a new AbstractRecursiveIterator but
     * saves you the allocation cost.  You probably shouldn't be doing this
     * so I don't know why I wrote it.
     *
     * @param parentIterator A fresh parent iterator.
     */
    public void reset(Iterator<? extends T> parentIterator) {
        this.parentIterator = parentIterator;
        this.childIterator = null;
        this.next = null;
        this.lastParentElement = null;
        this.removeable = false;
        this.iterationStarted = false;
    }

    /** Returns an iterator for the child, if it is an iterable element.
     *
     * <p>This is called for each and every element of the parentIterator,
     * independent of whether that element is an element of the overall
     * iteration (as specified by isElement(T)).
     *
     * <p>Subclasses must provide an implementation to this method.  No trivial
     * implementation is possible (say one where every parent element that supported
     * the Iterable was automatically iterated) since the generics type information
     * is lost at run-time and type safety can not be guaranteed.
     *
     * @param elementOfParentIterator An element of the parent iterator to recursively iterate.
     * @return An iterator for the childs elements, or null if the child has
     * no elements to iterate.
     */
    protected abstract Iterator<? extends T> getChildIterator(T elementOfParentIterator);

    /** Returns true if this element should be returned as an element of the iteration.
     *
     * @param prospectiveElement Prospective element to return (generated from both
     * the parent iterator or one of the child iterators).
     * @return True if this prospectiveElement should be returned as an element.
     */
    protected abstract boolean isElement(T prospectiveElement);

    /** Indicates that iteration over all elements is definitely complete.
     *
     * This methods return true if calls to next() have exhausted all iteration
     * elements.
     *
     * A return of false doesn't gaurantee there are more elements.  It just
     * means that we aren't yet certain if we has finished (which is why this
     * is call isInvalid and not isFinish, since isFinished might mislead
     * people into thinking it was the exact opposite of hasNext()).
     *
     * @return True if iteration is complete, false if there may be additional
     * elements to iterate over.
     */
    public boolean isInvalid() {
        return parentIterator == null;
    }

    /** Indicates that iteration has started.
     *
     * This method is mostly for use by subclasses to determine if changes
     * to the configuration of the class will affect the iteration.  For
     * example, a iterator that filters elements shouldn't change the
     * filtering criteria after iteration has been started.
     *
     * @return True if hasNext() or next() have been called at least once.
     */
    public boolean isIterationStarted() {
        return iterationStarted;
    }

    @Override
    public boolean hasNext() {
        return setupNext();
    }

    @Override
    public T next() {
        if (setupNext() == false) {
            throw new NoSuchElementException();
        }

        T result = next;
        removeable = true;
        next = null;

        return result;
    }

    @Override
    public void remove() {
        if (removeable == false) {
            throw new IllegalStateException("Elements must be removed after calling next(), but prior to calling hasNext()");
        }

        if (childIterator != null) {
            childIterator.remove();
        }
        else {
            parentIterator.remove();
        }
    }

    /** Sets up the next iterable element.
     *
     * This will grab the next element and put it into next field
     * if there are additional elements in the iteration.  Additionally,
     * the childIterator field will be set appropriately if a subiterator
     * is found (according to getChildIterator(T)) while iterating
     * the parent iterator.
     *
     * @return True if the is a next element, false if we are done.
     */
    private boolean setupNext() {
        if ( parentIterator == null ) return false;

        if (next == null) {
            removeable = false;

            while (next == null) {

                if (childIterator != null) {
                    if (childIterator.hasNext()) {
                        T aNext = childIterator.next();

                        if (isElement(aNext)) {
                            next = aNext;
                        }
                    }
                    else {
                        childIterator = null;
                    }
                }

                if (next == null && lastParentElement != null) {
                    // We haven't decided if the lastParentElement has
                    // a child iterator, so check that before looking
                    // for more elements.
                    childIterator = getChildIterator(lastParentElement);
                    lastParentElement = null;
                }

                if (childIterator == null && next == null) {
                    // The lastParentElement didn't generate a childIterator
                    // and we still
                    if (parentIterator.hasNext()) {
                        lastParentElement = parentIterator.next();

                        if (isElement(lastParentElement)) {
                            next = lastParentElement;
                        }
                    }
                    else {
                        parentIterator = null;
                        return false;  // We can't get anymore elements, so we must stop.
                    }
                }
            }
        }

        return true;
    }
}
