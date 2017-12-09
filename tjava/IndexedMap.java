/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tjava;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @param <Key>
 * @param <Value>
 * @author twalker
 */
public class IndexedMap<Key, Value> implements Map<Key, Value>, Cloneable {

    private Map<Key, Value> map;

    private transient List<Key> list = null;

    public IndexedMap() {
        this(null);
    }

    public IndexedMap(Map<Key, Value> map) {

        this.map = new HashMap<Key, Value>();

        if (map != null) {
            this.map.putAll(map);
        }

        createList();
    }

    private void createList() {

        list = new ArrayList<Key>(map.size() + 1);

        for (Key key : map.keySet()) {
            list.add(key);
        }
    }

    @Override
    public Collection<Value> values() {
        return map.values();
    }

    @Override
    public int size() {
        return map.size();
    }


    @Override
    public void putAll(Map<? extends Key, ? extends Value> m) {
        for (Entry<? extends Key, ? extends Value> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Value put(Key key, Value value) {
        Value oldValue = map.put(key, value);
        if (oldValue == null) {
            list.add(key);
        }

        return oldValue;
    }

    @Override
    public Set<Key> keySet() {
        return map.keySet();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    @SuppressWarnings("element-type-mismatch")
    public Value get(Object key) {
        return map.get(key);
    }

    @Override
    public Set<Entry<Key, Value>> entrySet() {
        return new IndexedMapEntrySet();
    }

    @Override
    @SuppressWarnings("element-type-mismatch")
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    @SuppressWarnings("element-type-mismatch")
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public void clear() {
        map.clear();
        list.clear();
    }

    public Value getValue(int index) {

        Key key = list.get(index);
        return map.get(key);
    }

    public Key getKey(int index) {
        return list.get(index);
    }

    public int indexOfKey(Key o) {
        return list.indexOf(o);
    }

    /** Sets the value at index without changing the key.
     *
     * @param index
     * @param element
     * @return
     */
    public Value setValue(int index, Value element) {
        Key key = getKey(index);
        Value oldValue = map.put(key, element);
        return oldValue;
    }

    /** Sets both the key and value at index, removing the existing key/value pair.
     *
     * @param index
     * @param key
     * @param element
     * @return
     */
    public Value set(int index, Key key, Value element) {
        Key oldKey = getKey(index);

        Value oldValue;

        if (oldKey != key) {
            oldValue = map.remove(oldKey);
            map.put(key, element);
            list.set(index, key);
        }
        else {
            oldValue = map.put(key, element);
        }

        return oldValue;
    }

    public Value remove(int index) {

        Key key = list.remove(index);
        Value value = map.remove(key);
        return value;

    }

    @Override
    @SuppressWarnings("element-type-mismatch")
    public Value remove(Object key) {
        Value v = map.remove(key);
        if (v != null) {
            list.remove(key);
        }
        return v;
    }

    private Object readResolve() {
        createList();
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IndexedMap<Key, Value> other = (IndexedMap<Key, Value>) obj;
        if (this.map != other.map && (this.map == null || !this.map.equals(other.map))) {
            return false;
        }
        if (this.list != other.list && (this.list == null || !this.list.equals(other.list))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.map != null ? this.map.hashCode() : 0);
        hash = 67 * hash + (this.list != null ? this.list.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < list.size(); i++) {
            Key k = list.get(i);
            Value v = map.get(k);

            if ( i > 0 ) {
                sb.append(", ");
            }

            sb.append(k).append("=").append(v);
        }
        sb.append("]");

        return sb.toString();
    }

    /** Creates a shallow clone of the IndexedMap.
     *
     * The backing list/map will be cloned, so modifications
     * to the cloned IndexedMaps structure will not be reflected in
     * the original.  However, the key and value objects are not cloned.
     *
     * @return Shallow clone of this object.
     */
    @Override
    public IndexedMap<Key,Value> clone() {
        try {
            IndexedMap<Key,Value> clone = (IndexedMap<Key,Value>)super.clone();
            clone.list = Utilities.createClone(list);
            clone.map = Utilities.createClone(map);
        }
        catch(CloneNotSupportedException e) {

        }
        return null;
    }

    public class IndexedMapEntrySet implements Set<Entry<Key, Value>>, Iterable<Entry<Key, Value>> {

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public boolean isEmpty() {
            return map.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Iterator<Entry<Key, Value>> iterator() {
            return new EntrySetIterator();
        }

        @Override
        public Object[] toArray() {
            Object[] a = new Object[size()];
            int index = 0;
            for (Entry<Key, Value> object : this) {
                a[index++] = object;
            }
            return a;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean add(Entry<Key, Value> e) {
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
        public boolean addAll(Collection<? extends Entry<Key, Value>> c) {
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
    }

    public class MyEntry implements Entry<Key, Value> {

        private Key key;

        public MyEntry(Key key) {
            this.key = key;
        }

        @Override
        public Key getKey() {
            return key;
        }

        @Override
        public Value getValue() {
            return map.get(key);
        }

        @Override
        public Value setValue(Value value) {
            return map.put(key, value);
        }
    }

    public class EntrySetIterator implements Iterator<Entry<Key, Value>> {

        private Iterator<Key> it = list.iterator();

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public Entry<Key, Value> next() {
            return new MyEntry(it.next());
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    
}
