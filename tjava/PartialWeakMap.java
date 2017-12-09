/*
 * WeakMap.java
 *
 * Created on September 19, 2000, 6:45 PM
 */

package tjava;


import java.lang.ref.WeakReference;
import java.lang.ref.ReferenceQueue;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/**
 *
 * @author  unknown
 * @version
 */
public class PartialWeakMap extends AbstractMap implements Map  {
   static private class WeakValue extends WeakReference {
        private Object key;
        
        private WeakValue(Object v, Object k) {
            super(v);key = k;
        }

        private static WeakValue create(Object v, Object k) {
            if (k == null) return null;
            else return new WeakValue(v, k);
        }

        private WeakValue(Object v, Object k, ReferenceQueue q) {
            super(v, q);
            key = k;
        }

        private static WeakValue create(Object v, Object k, ReferenceQueue q) {
            if (v == null) return null;
            else return new WeakValue(v, k, q);
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof WeakValue)) return false;
            Object t = this.get();
            Object u = ((WeakValue)o).get();
            if ((t == null) || (u == null)) return false;
            if (t == u) return true;
            return t.equals(u);
        }
        
        public Object getKey() {
            return key;
        }
    }

 

    private Map hash;

    private ReferenceQueue queue = new ReferenceQueue();


    /* Remove all invalidated entries from the map, that is, remove all entries
    whose keys have been discarded.  This method should be invoked once by
    each public mutator in this class.  We don't invoke this method in
    public accessors because that can lead to surprising
    ConcurrentModificationExceptions. */
    private void processQueue() {
        WeakValue wv;
        while ((wv = (WeakValue)queue.poll()) != null) {
            hash.remove(wv.getKey());
            //System.out.println("Hash entry removed for key " + wv.getKey() +" in processQueue");
        }
    }


    /* -- Constructors -- */

    /**
     * Constructs a new, empty <code>WeakValueMap</code> with the given
     * initial capacity and the given load factor.
     *
     * @param  initialCapacity  The initial capacity of the
     *                          <code>WeakValueMap</code>
     *
     * @param  loadFactor       The load factor of the <code>WeakValueMap</code>
     *
     * @throws IllegalArgumentException  If the initial capacity is less than
     *                                   zero, or if the load factor is
     *                                   nonpositive
     */
    public PartialWeakMap(int initialCapacity,float loadFactor) {
        hash = new HashMap(initialCapacity, loadFactor);
    }

    /**
     * Constructs a new, empty <code>WeakValueMap</code> with the given
     * initial capacity and the default load factor, which is
     * <code>0.75</code>.
     *
     * @param  initialCapacity  The initial capacity of the
     *                          <code>WeakValueMap</code>
     *
     * @throws IllegalArgumentException  If the initial capacity is less than
     *                                   zero
     */
    public PartialWeakMap(int initialCapacity) {
        hash = new HashMap(initialCapacity);
    }

    /**
     * Constructs a new, empty <code>WeakValueMap</code> with the default
     * initial capacity and the default load factor, which is
     * <code>0.75</code>.
     */
    public PartialWeakMap() {
        hash = new HashMap();
    }

    /**
     * Constructs a new <code>WeakValueMap</code> with the same mappings as the
     * specified <tt>Map</tt>.  The <code>WeakValueMap</code> is created with an
     * initial capacity of twice the number of mappings in the specified map
     * or 11 (whichever is greater), and a default load factor, which is
     * <tt>0.75</tt>.
     *
     * @param   t the map whose mappings are to be placed in this map.
     * @since	1.3
     */
    public PartialWeakMap(Map t) {
        this(Math.max(2*t.size(), 11), 0.75f);
        putAll(t);
    }

    /* -- Simple queries -- */

    /**
     * Returns the number of key-value mappings in this map.
     * <strong>Note:</strong> <em>In contrast with most implementations of the
     * <code>Map</code> interface, the time required by this operation is
     * linear in the size of the map.</em>
     */
    public int size() {
        processQueue();
        return entrySet().size();
    }

    /**
     * Returns <code>true</code> if this map contains no key-value mappings.
     */
    public boolean isEmpty() {
        return entrySet().isEmpty();
    }

    /**
     * Returns <code>true</code> if this map contains a mapping for the
     * specified key.
     *
     * @param   key   The key whose presence in this map is to be tested
     */
    public boolean containsKey(Object key) {
        return hash.containsKey(key);
    }


    /* -- Lookup and modification operations -- */

    /**
     * Returns the value to which this map maps the specified <code>key</code>.
     * If this map does not contain a value for this key, then return
     * <code>null</code>.
     *
     * @param  key  The key whose associated value, if any, is to be returned
     */
    public Object get(Object key) {
        processQueue();
        Object wv;
        wv = hash.get(key);
        if ( wv != null ) {
            return ((WeakValue)wv).get();
        }
        return null;
    }

    /**
     * Updates this map so that the given <code>key</code> maps to the given
     * <code>value</code>.  If the map previously contained a mapping for
     * <code>key</code> then that mapping is replaced and the previous value is
     * returned.
     *
     * @param  key    The key that is to be mapped to the given
     *                <code>value</code>
     * @param  value  The value to which the given <code>key</code> is to be
     *                mapped
     *
     * @return  The previous value to which this key was mapped, or
     *          <code>null</code> if if there was no mapping for the key
     */
    public Object put(Object key, Object value) {
        processQueue();
        return hash.put(key, WeakValue.create(value, key, queue));
    }

    /**
     * Removes the mapping for the given <code>key</code> from this map, if
     * present.
     *
     * @param  key  The key whose mapping is to be removed
     *
     * @return  The value to which this key was mapped, or <code>null</code> if
     *          there was no mapping for the key
     */
    public Object remove(Object key) {
        processQueue();
        return hash.remove(key);
    }

    /**
     * Removes all mappings from this map.
     */
    public void clear() {
        processQueue();
        hash.clear();
    }
    
        /** Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value.  More formally, returns <tt>true</tt> if and only if
     * this map contains at least one mapping to a value <tt>v</tt> such that
     * <tt>(value==null ? v==null : value.equals(v))</tt>.  This operation
     * will probably require time linear in the map size for most
     * implementations of the <tt>Map</tt> interface.
     *
     * @param value value whose presence in this map is to be tested.
     * @return <tt>true</tt> if this map maps one or more keys to the
     *        specified value.
     */
    public boolean containsValue(Object value) {
        processQueue();
        return hash.containsValue( WeakValue.create(value,null) );
    }
    /** Copies all of the mappings from the specified map to this map
     * (optional operation).  These mappings will replace any mappings that
     * this map had for any of the keys currently in the specified map.
     *
     * @param t Mappings to be stored in this map.
     *
     * @throws UnsupportedOperationException if the <tt>putAll</tt> method is
     * 		  not supported by this map.
     *
     * @throws ClassCastException if the class of a key or value in the
     * 	          specified map prevents it from being stored in this map.
     *
     * @throws IllegalArgumentException some aspect of a key or value in the
     * 	          specified map prevents it from being stored in this map.
     *
     * @throws NullPointerException this map does not permit <tt>null</tt>
     *           keys or values, and the specified key or value is
     *           <tt>null</tt>.
     */
    public void putAll(Map t) {
    }
    /** Returns a set view of the keys contained in this map.  The set is
     * backed by the map, so changes to the map are reflected in the set, and
     * vice-versa.  If the map is modified while an iteration over the set is
     * in progress, the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding mapping from
     * the map, via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
     * <tt>removeAll</tt> <tt>retainAll</tt>, and <tt>clear</tt> operations.
     * It does not support the add or <tt>addAll</tt> operations.
     *
     * @return a set view of the keys contained in this map.
     */
    public Set keySet() {
        processQueue();
        return hash.keySet();
    }
    /** Returns a collection view of the values contained in this map.  The
     * collection is backed by the map, so changes to the map are reflected in
     * the collection, and vice-versa.  If the map is modified while an
     * iteration over the collection is in progress, the results of the
     * iteration are undefined.  The collection supports element removal,
     * which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt> and <tt>clear</tt> operations.
     * It does not support the add or <tt>addAll</tt> operations.
     *
     * @return a collection view of the values contained in this map.
     */
    public Collection values() {
        processQueue();
        return new HashSet();
    }
    /** Returns a set view of the mappings contained in this map.  Each element
     * in the returned set is a <tt>Map.Entry</tt>.  The set is backed by the
     * map, so changes to the map are reflected in the set, and vice-versa.
     * If the map is modified while an iteration over the set is in progress,
     * the results of the iteration are undefined.  The set supports element
     * removal, which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt> and <tt>clear</tt> operations.  It does not support
     * the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a set view of the mappings contained in this map.
     */
    public Set entrySet() {
        processQueue();
        return hash.entrySet();
    }
    /** Compares the specified object with this map for equality.  Returns
     * <tt>true</tt> if the given object is also a map and the two Maps
     * represent the same mappings.  More formally, two maps <tt>t1</tt> and
     * <tt>t2</tt> represent the same mappings if
     * <tt>t1.entrySet().equals(t2.entrySet())</tt>.  This ensures that the
     * <tt>equals</tt> method works properly across different implementations
     * of the <tt>Map</tt> interface.
     *
     * @param o object to be compared for equality with this map.
     * @return <tt>true</tt> if the specified object is equal to this map.
     */
    public boolean equals(Object o) {
        if ( this == o ) return true;
        return false;
    }
    /** Returns the hash code value for this map.  The hash code of a map
     * is defined to be the sum of the hashCodes of each entry in the map's
     * entrySet view.  This ensures that <tt>t1.equals(t2)</tt> implies
     * that <tt>t1.hashCode()==t2.hashCode()</tt> for any two maps
     * <tt>t1</tt> and <tt>t2</tt>, as required by the general
     * contract of Object.hashCode.
     *
     * @return the hash code value for this map.
     * @see Map.Entry#hashCode()
     * @see Object#hashCode()
     * @see Object#equals(Object)
     * @see #equals(Object)
     */
    public int hashCode() {
        processQueue();
        return hash.hashCode();
    }
}