/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tjava;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @param <T>
 * @author twalker
 */
public class CompoundSet<T> implements Set<T> {

    private Set set1;
    private Set set2;

    public CompoundSet(Set set1, Set set2) {
        this.set1 = set1;
        this.set2 = set2;
    }

    @Override
    public int size() {
        return set1.size() + set2.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return set1.contains(o) || set2.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return new CompoundIterator<T>(set1.iterator(),set2.iterator());
    }

    @Override
    public Object[] toArray() {
        Object[] a = new Object[size()];
        int index = 0;
        for (T object : this) {
            a[index++] = object;
        }
        return a;
    }

    @Override
    public <X> X[] toArray(X[] a) {
        X[] array;
        Class componentType = a.getClass().getComponentType();
        if (a.length < size()) {
            // Make a new array of a's runtime type, but my contents:
            array = (X[]) Array.newInstance(componentType, size());
        }
        else {
            array = a;
        }

        int index = 0;
        for (T object : this) {
            array[index++] = (X)object;
        }

        if (array.length > size()) {
            array[size()] = null;
        }
        return array;
    }

    @Override
    public boolean add(T e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return Utilities.toCommaSeparateList(this, "{", "}");
    }



}
