/*
 * PropertyList.java
 *
 * Created on September 16, 2000, 1:03 PM
 */

package champions;

import champions.exception.DetailListNotFound;
import champions.exceptionWizard.ExceptionWizard;
import champions.interfaces.IOAdapter;
import champions.interfaces.IndexIterator;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import tjava.Destroyable;
import tjava.PartialWeakMap;



/**
 *
 * @author  unknown
 * @version
 */
public class DetailList extends Object implements Destroyable, Serializable {
    
    /** DEBUG variable determines what low level debugging is compiled into DetailList
     * 1 = Dispatched PropertyChanges are debugged
     * 2 = Dispatched and Undispatched PropertyChanes are debugged
     * 3 = CompactIndex executions
     */
    private static final int DEBUG = 0;
    private static final int TRACK_USAGE = 0;
    
    static final long serialVersionUID = -8220183961410791118L;
    
    /** List is a list of all created DetailLists */
    private static PartialWeakMap detailListHash = new PartialWeakMap();
    private static int nextDetailListIndex = 0;
    
    /** Hash Containing Queued Property Changes */
    private static Map propertyChangeHash = new HashMap();
    private static Map extraChangeHash = new HashMap();
    /** Temporary Storage Set for Keys about to be dispatched */
    private static Set propertyChangeDispatchSet = new HashSet();
    
    /** Mark to indicate there are property changes to dispatch */
    private static boolean propertyChangeInvalid = false;
    /** PropertyChange queuing lock object */
    private static Object propertyChangeLock = new Object();
    private static Object dispatchLock = new Object();
    /** Indicates Dispatch has already been sent to Event Thread */
    private static boolean propertyChangeDispatched = false;
    /** Indicates the Dispatch is already running. */
    private static boolean propertyChangeIsDispatching = false;
    
    //transient protected  boolean  modified = false;
    transient protected File file;
    
    /** Utility field used by bound properties. */
    transient protected ExtendedPropertyChangeSupport propertyChangeSupport = new ExtendedPropertyChangeSupport(this);
    
    transient protected Integer detailListIndex;
    
    transient private int listeners = 0;
    
    transient protected boolean beingDestroyed = false;
    
    //public String fileExtension = "lst";
    
    public TreeMap detailList;
    
    /** Holds value of property fireChangeByDefault. */
    private boolean fireChangeByDefault = true;
    
    private static Map writeUsageMap = null;
    private static Map readUsageMap = null;
    
    // Static Initializer used for debugging
    static {
        if ( TRACK_USAGE > 0 ) {
            System.out.println("Creating DetailList Usage Tracking Maps...");
            writeUsageMap = new HashMap();
            readUsageMap = new HashMap();
        }
    }
    
    public DetailList() {
        detailList = new TreeMap();
        detailListIndex = new Integer( getNextDetailListIndex() );
        DetailList.addDetailListReference(this);
        if ( DEBUG > 0 ) System.out.println("Created DetailList (Type: " + this.getClass().toString() + ", DLI: " + detailListIndex.toString() + ")");
    }
    
//    public DetailList(TreeMap map) {
//        detailListIndex = new Integer( getNextDetailListIndex() );
//        DetailList.addDetailListReference(this);
//        detailList = new TreeMap();
//    }
    
    public Map getMap() {
        return detailList;
    }
    
    /**
     * @return
     */
    /* public Iterator iterator() {
    return detailList.iterator();
    }*/
    
    /**
     * @return
     */
    public int size() {
        return detailList.size();
    }
    
    /**
     * @param name
     * @param type
     * @return
     */
/*    public synchronized Object getValue(String name, String type) {
 
        Object o = detailList.get(name + "." + type);
        return o;
    } */
    
    public synchronized Object getValue(String key) {
        Object o = /*detailList == null ? null :*/ detailList.get(key);
        if ( TRACK_USAGE > 0 ) recordRead(this, key);
        return o;
    }
    
    public synchronized boolean contains(String key) {
        return detailList.containsKey( key);
    }
    
    public void add(final String key, final Object value) {
        add( key, value, false, fireChangeByDefault);
    }
    
    public void add(final String key, final Object value, boolean replace ) {
        add( key, value, replace, fireChangeByDefault);
    }
    
    public synchronized void add(final String key, final Object value, boolean replace, boolean fireChange ) {
        final Object old = getValue(key);
        
        
        
        if ( replace || this.contains(key) == false) {
            detailList.put(key, value);
        }
        
        if ( fireChange && value != null && ! value.equals(old) ) {
            firePropertyChange(this,key,old,value);
        }
        
        if ( TRACK_USAGE > 0 ) recordWrite(this, key);
    }
    
    public void add(final String name, final String type, final Object value, boolean replace) {
        add( name + "." + type, value, replace, fireChangeByDefault);
    }
    
    public void add(final String name, final String type, final Object value, boolean replace, boolean fireChange) {
        add( name + "." + type, value, replace, fireChange);
    }
    
    
    
    /** Fires a Property Change Event.
     * <CODE>firePropertyChange</CODE> queue multiple propertyChanges and takes the
     * necessary steps to dispatch those events to the listeners.  If <CODE>firePropertyChange</CODE>
     * is executed in the event handling thread, the property changes are dispatched
     * immediately, otherwise, multiple changes are queue in a global hash and a
     * dispatch event is posted to the event handling thread.
     *
     * Unless otherwise indicated via the <CODE>fireDuplicate</CODE> parameter,
     * property changes occurring to the same detailList/property are consolidated
     * and only the first one that was posted is dispatched.
     * @param source DetailList which changed.
     * @param key Property which Changed.
     * @param oldValue Old Value of Property.
     * @param newValue New value of the Property.
     * @param fireDuplicate Indicates that multiple propertyChanges should be dispatched for the same property.
     * If TRUE, all the property changes will be queued and dispatched for a specific property.
     * If FALSE, only the first property change for a specific property will be queued.
     */
    static public void firePropertyChange(DetailList source, String key,Object oldValue,Object newValue, boolean fireDuplicate) {
        boolean triggerDispatch = false;
        
//        if ( source instanceof ParameterList ) {
//            System.out.println("ParameterList.firePropertyChange(" +  key + ", " + oldValue + ", " + newValue + ")");
//        
//        }
        
        synchronized ( propertyChangeLock ) {
            // First add change to queue
            if ( DEBUG < 2 && source.propertyChangeSupport.hasListeners(key) == false ) return;
            
            DetailListHashKey hashKey = new DetailListHashKey(source, key);
            DetailListHashValue hashValue;
            
            if ( propertyChangeHash.containsKey(hashKey)  ) {
                // There source/property already is queue.  If fireDuplicates is true, then add the values to the existing
                // hashValue object.
                if ( fireDuplicate ) {
                    hashValue = (DetailListHashValue)propertyChangeHash.get( hashKey );
                    hashValue.addValues(oldValue, newValue);
                    propertyChangeInvalid = true;
                }
            }
            else {
                hashValue = DetailListHashValue.getEmptyHashValue();
                hashValue.addValues(oldValue, newValue);
                propertyChangeHash.put(hashKey, hashValue);
                propertyChangeInvalid = true;
            }
            
            if ( DEBUG >= 3 ) {
                System.out.println("Queueing Property from class " + source.getClass().toString() + ": " + key +
                ". Dispatch State - Invalid: " + (propertyChangeInvalid ? "T" : "F")
                + " IsDispatching: " + (propertyChangeIsDispatching ? "T" : "F")
                + " Dispatched: " + (propertyChangeDispatched ? "T" : "F") );
                // + " Thread: " + Thread.currentThread().getName() );
            }
        } // Release propertyChangeLock
        
        
        
        if ( SwingUtilities.isEventDispatchThread() == false || propertyChangeIsDispatching == true ) {
            if  ( propertyChangeInvalid == true && propertyChangeDispatched == false ) {
                if ( DEBUG >= 3 ) System.out.println("Adding Dispatch Event to Event Thread.");
                Runnable propertyChangeRunnable = new Runnable() {
                    public void run() {
                        dispatchPropertyChanges();
                    }
                };
                
                propertyChangeDispatched = true;
                SwingUtilities.invokeLater(propertyChangeRunnable);
                
            }
        }
        else {
            if ( propertyChangeInvalid == true ) {
                dispatchPropertyChanges();
            }
        }
    }
    
    static public void firePropertyChange(DetailList source, String key,Object oldValue,Object newValue) {
        firePropertyChange(source, key,oldValue,newValue,false);
    }
    
    public static void dispatchPropertyChanges() {
        synchronized ( dispatchLock ) {
            try {
                // Reset the Control Variables
                propertyChangeIsDispatching = true;
                propertyChangeDispatched = false;
                
                if ( propertyChangeInvalid == true ) {
                    
                    Map currentDispatchHash;
                    synchronized ( propertyChangeLock ) {
                        // Quickly swap the propertyChangeHash and extraChangeHash to allow
                        // other threads to put additional changes in even while dispatching
                        // existing changes.
                        currentDispatchHash = propertyChangeHash;
                        propertyChangeHash = extraChangeHash;
                        extraChangeHash = currentDispatchHash;
                        propertyChangeInvalid = false;
                    }
                    
                    DetailListHashKey hk;
                    DetailListHashValue hv;
                    DetailList source;
                    String property;
                    Object oldValue, newValue;
                    int index;
                    
                    try {
                        Iterator i = currentDispatchHash.keySet().iterator();
                        while ( i.hasNext() ) {
                            propertyChangeDispatchSet.add( i.next() );
                        }
                        
                        if ( DEBUG >= 1 ) System.out.println("Dispatching property change for " + Integer.toString(propertyChangeDispatchSet.size() ) + " changes.");
                        
                        i = propertyChangeDispatchSet.iterator();
                        while ( i.hasNext() ) {
                            hk = (DetailListHashKey)i.next();
                            hv = (DetailListHashValue)currentDispatchHash.get(hk);
                            source = hk.getSource();
                            property = hk.getProperty();
                            
                            try {
                                if ( DEBUG >= 2 && source.propertyChangeSupport.hasListeners(property) == false ) {
                                    System.out.println( "Dispatching Property Change from " + source.getClass().toString() + " (No Actual Listeners): " + property);
                                }
                                
                                if ( source.propertyChangeSupport.hasListeners(property) ) {
                                    for ( index = 0; index < hv.getSize(); index++) {
                                        oldValue = hv.getOldValue(index);
                                        newValue = hv.getNewValue(index);
                                        if ( DEBUG >= 1) System.out.println( "Dispatching Property Change from " + source.getClass().toString()
                                        + "(" + Integer.toString( source.propertyChangeSupport.getListenerCount() ) + " listeners): " + property + " " + oldValue + " " + newValue);
                                        
                                        
                                        source.propertyChangeSupport.firePropertyChange(property, oldValue, newValue );
                                    }
                                }
                            }
                            finally {
                                currentDispatchHash.remove(hk);
                                hv.reset();
                            }
                        }
                    }
                    finally {
                        propertyChangeDispatchSet.clear();
                    }
                    
                }
            }
            finally {
                propertyChangeIsDispatching = false;
            }
        }
    }
    
    /**
     * @param detail
     */
  /*  public synchronized void remove(Detail detail) {
        this.remove( detail.getKey() );
    }
   
    public synchronized void remove ( String name, String type) {
        this.remove ( name + "." + type);
    } */
    
    public synchronized void clear() {
        detailList.clear();
        firePropertyChange( this, "List Cleared", "", "");
    }
    
    public synchronized void remove( String key) {
        detailList.remove( key );
    }
    
    /**
     * @return
     */
    public synchronized String toString() {
        Iterator i = detailList.keySet().iterator();
        Object key,value;
        
        String s = "";
        String valueString;
        
        s = s.concat("DetailList Index: "+ detailListIndex.toString() +"\n");
        while ( i.hasNext() ) {
            key = i.next();
            value = detailList.get(key);
            if ( value != null ) {
                valueString = value.toString();
            }
            else {
                valueString = "null";
            }
            s = s.concat( key.toString() + "=" + valueString + "\n")   ;
        }
        return s;
    }
    
    public synchronized String toLongString() {
        Iterator i = detailList.keySet().iterator();
        Object key,value;
        
        String s = "";
        
        s = s.concat("DetailList Index: "+ detailListIndex.toString() +"\n");
        while ( i.hasNext() ) {
            key = i.next();
            s = s.concat( "" + key + "=" + detailList.get(key) + "\n")   ;
        }
        return s;
    }
    
    /**
     * These methods exist specifically for DetailList Editors...
     * @param index
     * @return
     */
    public synchronized Object getValue(int index) {
        int count = 0;
        Iterator i = detailList.keySet().iterator();
        
        Object key;
        while ( i.hasNext() ) {
            key = i.next();
            if ( count == index ) {
                return detailList.get( key);
            }
            count++;
        }
        return null;
    }
    
    public synchronized Object getKey(int index) {
        int count = 0;
        Iterator i = detailList.keySet().iterator();
        
        Object key;
        while ( i.hasNext() ) {
            key = i.next();
            if ( count == index ) {
                return key;
            }
            count++;
        }
        return null;
    }
    
  /*  public synchronized Detail get(int index) {
        Object o = getKey(index);
   
        if (o != null) {
            String k = (String)o;
            Object v = detailList.get(k);
            return new Detail( k.substring(0, k.indexOf(".")), k.substring( k.indexOf(".") +1), v);
        }
        return null;
    } */
    
    public Integer getIntegerValue( String key ) {
        return (Integer)getValue( key);
    }
    
    public Long getLongValue( String key ) {
        return (Long)getValue(key);       
    }
    
/*    public synchronized String getStringValue( String name, String type) {
 
        return getStringValue( name + "." + type );
    } */
    
    public String getStringValue( String key ) {
        Object o = getValue( key);
        return (o instanceof String) ? (String)o : null;
    }
    
  /*  public synchronized Dice getDiceValue( String name, String type) {
   
        Object o;
        if ( (o = getValue ( name,type)) == null ||o.getClass() != Dice.class ) {
            return null;
        }
        return (Dice)o;
   
    } */
    
    public Dice getDiceValue(String key) {
        Object o = getValue( key);
        return (o instanceof Dice) ? (Dice)o : null;
        
    }
    
    /*public synchronized Double getDoubleValue( String name, String type) {
     
        Object o;
        if ( (o = getValue ( name,type)) == null ||o.getClass() != Double.class ) {
            return null;
        }
        return (Double)o;
     
    }*/
    
    public Double getDoubleValue( String key) {
        Object o = getValue( key);
        return (o instanceof Double) ? (Double)o : null;
    }
    
   /* public synchronized boolean getBooleanValue( String name, String type) {
    
        Object o;
        if ( (o = getValue ( name,type)) == null ||o.getClass() != String.class ) {
            return false;
        }
    
        return (o.equals("TRUE") ) ? true : false;
    
    } */
    
    public boolean getBooleanValue( String key) {
        Object o = getValue( key);
        return (o != null && o.equals("TRUE") ) ? true : false;
    }
    
    /**
     * @param dl
     * @param replace
     */
    public synchronized void addAll(DetailList dl, boolean replace) {
        if ( dl != null) {
            detailList.putAll(dl.getMap());
        }
    }
    
    /**
     * @param dl
     */
    public void addAll(DetailList dl) {
        this.addAll(dl, false);
    }
    /** Add a PropertyChangeListener to the listener list.
     * @param l The listener to add.
     */
    public void addPropertyChangeListener(String property, PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(property,l);
        listeners++;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
        listeners++;
    }
    /** Removes a PropertyChangeListener from the listener list.
     * @param l The listener to remove.
     */
    public void removePropertyChangeListener(String property, PropertyChangeListener l) {
        if (propertyChangeSupport != null ) {
            propertyChangeSupport.removePropertyChangeListener(property, l);
            listeners--;
        }
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
        if (propertyChangeSupport != null ) {
            propertyChangeSupport.removePropertyChangeListener(l);
            listeners--;
        }
    }
    
    /**
     * @param inDetailList
     */
    public static void addDetailListReference(DetailList inDetailList)  {
        if ( ! detailListHash.containsKey(inDetailList.getDetailListIndex()) ) {
            detailListHash.put(inDetailList.getDetailListIndex(), inDetailList );
        }
    }
    
    /**
     * @param inDetailList
     * @throws DetailListNotFound
     * @return
     */
    public static DetailList getDetailListReference(Integer dli) throws DetailListNotFound {
        if ( detailListHash.containsKey( dli ) ) {
            return (DetailList)detailListHash.get(dli);
        }
        throw new DetailListNotFound("DetailList [" + dli.toString() + "] not found!");
    }
    
    /**
     * @param inDetailList
     */
    public static void removeDetailListReference(Integer dli) {
        detailListHash.remove( dli );
    }
    
    /**
     * @return
     */
    public static int getNextDetailListIndex() {
        return nextDetailListIndex++;
    }
    /** Getter for property detailListIndex.
     * @return Value of property detailListIndex.
     */
    public Integer getDetailListIndex() {
        return detailListIndex;
    }
    
    /**
     * @param o
     * @return
     */
    public boolean equals(Object o ) {
        if (o instanceof DetailList) {
            return this.getDetailListIndex().equals(((DetailList)o).getDetailListIndex());
        }
        return false;
    }
    
  /*  public synchronized int createIndexed(Detail detail, boolean fireChange) {
        return createIndexed( getIndexedSize( detail.getName() ), detail.getName(), detail.getType(), detail.getValue(), fireChange);
    }
   
    public synchronized int createIndexed(Detail detail) {
        return createIndexed(getIndexedSize( detail.getName() ), detail.getName(), detail.getType(), detail.getValue(), true);
    } */
    
    public int createIndexed(String indexName, String type, Object value) {
        return createIndexed(getIndexedSize(indexName),indexName,type,value,fireChangeByDefault);
    }
    
    public int createIndexed(String indexName, String type, Object value, boolean fireChange) {
        return createIndexed(getIndexedSize(indexName),indexName,type,value,fireChange);
    }
    
    public int createIndexed(int position, String indexName, String type, Object value) {
        return createIndexed(position,indexName,type,value,true);
    }
    
   /* public synchronized int createIndexed(int position, Detail detail) {
        return createIndexed(position, detail.getName(), detail.getType(), detail.getValue(),true);
    } */
    
    public int createIndexed(int position, String key, Object value) {
        return createIndexed(position, nameFromKey(key), typeFromKey(key), value, fireChangeByDefault);
    }
    
    public int createIndexed(int position, String key, Object value, boolean fireChanged) {
        return createIndexed(position, nameFromKey(key), typeFromKey(key), value, fireChanged);
    }
    
    public synchronized int createIndexed(int position, String indexName, String type, Object value, boolean fireChange) {
        //String indexName = detail.getName();
        int count = getIndexedSize(indexName);
        Vector v = new Vector();
        
        if ( position > count ) position = count;
        if ( position == -1 ) position = count;
        
        // If this is in the middle of the list, make a whole
        try {
            int current = count - 1;
            String key;
            String currentName;
            String lastName;
            String newKey;
            while ( current >= position ) {
                currentName= indexName + Integer.toString(current) ;
                lastName = indexName + Integer.toString(current+1);
                
                Iterator i = detailList.keySet().iterator();
                while ( i.hasNext() ) {
                    key = (String) i.next();
                    if ( key.startsWith(currentName + ".") ) {
                        newKey = lastName + key.substring( currentName.length() );
                        
                        v.add( new Detail( lastName, key.substring( currentName.length() + 1), detailList.get(key)));
                        i.remove();
                    }
                }
                current --;
            }
        } catch ( ConcurrentModificationException cme) {
            System.out.println("Damn, caught a CME!");
            cme.printStackTrace();
        }
        
        Detail detail;
        Iterator i = v.iterator();
        while ( i.hasNext() ) {
            //this.add( (Detail)i.next() );
            detail = (Detail)i.next();
            this.add( detail.getKey(), detail.getValue(), true, false);
        }
        
        String indexCurrent = indexName + Integer.toString(position);
        //detail.setName( indexCurrent);
        // this.add(indexCurrent + "." + type, value, true, false);
        detailList.put( indexCurrent + "." + type, value );
        // this.add( indexName + ".INDEXSIZE", new Integer(count + 1), true, false );
        detailList.put( indexName + ".INDEXSIZE", new Integer(count + 1) );
        
        if ( fireChange ) fireIndexedChanged( indexName );
        
        return position;
    }
    
    public synchronized void moveIndexed(int start, int end, String indexName, boolean fireChange) {
        int low, high, index, newIndex, count;
        String key, type, newKey;
        Object object;
        TreeMap tm;
        
        if ( start == end ) return;
        count = getIndexedSize( indexName );
        if ( count <= 1 ) return;
        if ( start < 0 || start >= count ) return;
        
        if ( end >= count || end == -1 ) end = count -1;
        
        low = start < end ? start : end;
        high = start < end ? end : start;
        
        tm = new TreeMap();
        
        Iterator i = detailList.keySet().iterator();
        
        int indexOffset = indexName.length();
        
        while ( i.hasNext() ) {
            key = (String) i.next();
            if ( key.startsWith( indexName ) && java.lang.Character.isDigit( key.charAt(indexOffset) ) && ! key.equals( indexName + ".INDEXSIZE" ) ) {
                try {
                    index = Integer.parseInt( key.substring( indexName.length(), key.indexOf(".") ) );
                }
                catch (NumberFormatException nfe) {
                    System.out.println( "DetailList.moveIndex: error parsing index number for key: " + key );
                    continue;
                }
                if ( index >= low && index <= high ) {
                    // This is a key that needs to be moved.
                    if ( index == start ) newIndex = end;
                    else if ( index < start ) newIndex = index + 1;
                    else newIndex = index - 1;
                    
                    object = detailList.get(key);
                    type = key.substring( key.indexOf(".") + 1 );
                    newKey = indexName + Integer.toString(newIndex) + "." + type;
                    i.remove();
                    tm.put(newKey, object);
                    //  System.out.println( "Moved " + key + " to " + newKey );
                }
            }
        }
        
        i = tm.keySet().iterator();
        while ( i.hasNext() ) {
            key = (String)i.next();
            object = tm.get(key);
            detailList.put( key, object );
        }
        
        if ( fireChange ) {
            fireIndexedChanged( indexName );
        }
        
    }
    
  /*  public synchronized void addIndexed( int index, Detail detail) {
        addIndexed( index, detail, false);
    }
   
    public synchronized void addIndexed( int index, Detail detail, boolean replace) {
        detail.setName( detail.getName() + Integer.toString(index) );
        add(detail, replace);
    }
   
    public synchronized void addIndexed( int index, Detail detail, boolean replace, boolean fireChange) {
        detail.setName( detail.getName() + Integer.toString(index) );
        add(detail, replace, fireChange);
    } */
    
    public void addIndexed( int index, String name, String type, Object value, boolean replace, boolean fireChange) {
        add( name + Integer.toString(index) + "." + type, value, replace, fireChange);
    }
    
 /*   public synchronized void addIndexed( int index, String name, String type, Object value) {
        add( name + Integer.toString(index) + "." + type, value, false, true);
    } */
    
    public void addIndexed( int index, String name, String type, Object value, boolean replace) {
        add( name + Integer.toString(index) + "." + type, value, replace, fireChangeByDefault);
    }
    
    public void addIndexed( int index, String name, String type, Object value) {
        add( name + Integer.toString(index) + "." + type, value, false, fireChangeByDefault);
    }
    
    public synchronized int getIndexedSize(String indexName) {
        Object o = getValue( indexName + ".INDEXSIZE" );
        return ( o != null ) ? ((Integer)o).intValue() : 0;
    }
    
  /*  public synchronized Detail getIndexed(int index, String indexName, String indexType) {
        return get( indexName + Integer.toString(index), indexType);
    } */
    
   /* public synchronized Detail getIndexed(int index, Detail detail) {
        return getIndexed(index, detail.getName(), detail.getType());
    } */
    
    public Object getIndexedValue(int index, String indexName, String indexType) {
        return getValue(indexName + Integer.toString(index) + "." + indexType);
    }
    
   /* public synchronized Object getIndexedValue(int index, Detail detail) {
        return getValue(detail.getName() + Integer.toString(index), detail.getType());
    }
    
    public synchronized String getIndexedStringValue(int index, Detail detail) {
        return getStringValue(detail.getName() + Integer.toString(index), detail.getType());
    } */
    
    public String getIndexedStringValue(int index, String indexName, String indexType) {
        return getStringValue(indexName + Integer.toString(index) + "." + indexType);
    }
    
   /* public synchronized Integer getIndexedIntegerValue(int index, Detail detail) {
        return getIntegerValue(detail.getName() + Integer.toString(index), detail.getType());
    } */
    
    public Integer getIndexedIntegerValue(int index, String indexName, String indexType) {
        return getIntegerValue(indexName + Integer.toString(index) + "." + indexType);
    }
    
    public Long getIndexedLongValue(int index, String indexName, String indexType) {
        return getLongValue(indexName + Integer.toString(index) + "." + indexType);
    }
    
 /*   public synchronized Double getIndexedDoubleValue(int index, Detail detail) {
        return getDoubleValue(detail.getName() + Integer.toString(index), detail.getType());
    } */
    
    public Double getIndexedDoubleValue(int index, String indexName, String indexType) {
        return getDoubleValue(indexName + Integer.toString(index) + "." + indexType);
    }
    
  /*  public synchronized boolean getIndexedBooleanValue(int index, Detail detail) {
        return getBooleanValue(detail.getName() + Integer.toString(index), detail.getType());
    } */
    
    public boolean getIndexedBooleanValue(int index, String indexName, String indexType) {
        return getBooleanValue(indexName + Integer.toString(index) + "." + indexType);
    }
    
    public Dice getIndexedDiceValue(int index, String indexName, String indexType) {
        return getDiceValue(indexName + Integer.toString(index) + "." + indexType);
    }
    
    public int findIndexed(String indexName, String type, Object value) {
        return findIndexed(0, indexName, type, value);
    }
    
    public synchronized int findIndexed(int start, String indexName, String type, Object value) {
        if ( value == null ) return -1;
        
        String indexCurrent, key;
        int count = start;
        int size = getIndexedSize(indexName);
        //Object dlValue;
        
        while ( count < size ) {
            //   indexCurrent = indexName + Integer.toString(count);
            key = indexName + Integer.toString(count) + "." + type;
          /*  if ( detailList.containsKey( key ) ) {
                if (value.equals( detailList.get( key )) ) {
                    return count;
                }
            } */
            if (value.equals( detailList.get( key )) ) {
                return count;
            }
            count ++;
        }
        return -1;
    }
    
    public int findExactIndexed(String indexName, String type, Object value) {
        return findExactIndexed(0, indexName, type, value);
    }
    
    public synchronized int findExactIndexed(int start, String indexName, String type, Object value) {
        String indexCurrent, key;
        int count = start;
        int size = getIndexedSize(indexName);
        //Object dlValue;
        
        while ( count < size ) {
            //   indexCurrent = indexName + Integer.toString(count);
            key = indexName + Integer.toString(count) + "." + type;
        /*    if ( detailList.containsKey( key ) ) {
                if (value == detailList.get( key )) {
                    return count;
                }
            } */
            if (value == detailList.get( key )) {
                return count;
            }
            count ++;
        }
        return -1;
    }
    
    // These two need to be fixed.  If you are removing the last detail of a
    // particular index, these will leave a hole in the indexing scheme.  Bad.
    public void removeIndexed(int index, String indexName, String indexType) {
        removeIndexed(index,indexName,indexType,true);
    }
    
    public synchronized void removeIndexed(int index, String indexName, String indexType, boolean fireChange) {
        remove( indexName + Integer.toString(index) + "." + indexType);
        compactIndexed(index, indexName,fireChange);
    }
    
   /* public synchronized void removeIndexed(int index, Detail detail) {
        removeIndexed( index, detail.getName() + Integer.toString(index), detail.getType());
    
    } */
    
    public void removeAllIndexed(int index, String indexName) {
        removeAllIndexed(index, indexName, true);
    }
    
    public synchronized void removeAllIndexed(int index, String indexName, boolean fireChange) {
        if ( index >= getIndexedSize(indexName) ) return;
        
        Iterator i = detailList.keySet().iterator();
        String keyname = indexName + Integer.toString(index) + ".";
        String key;
        
        Vector v = new Vector();
        
        while ( i.hasNext()) {
            key = (String) i.next();
            if ( key.startsWith(keyname) ) {
                v.add(key);
            }
        }
        
        i = v.iterator();
        
        while ( i.hasNext()) {
            key = (String) i.next();
            detailList.remove(key);
        }
        
        compactIndexed(index, indexName, fireChange);
    }
    
    public synchronized void removeAll(String indexName) {
        removeAll(indexName, fireChangeByDefault);
    }
    
    public synchronized void removeAll(String indexName, boolean fireChange) {
        Iterator i = detailList.keySet().iterator();
        String key;
        
        Vector v = new Vector();
        
        while ( i.hasNext()) {
            key = (String) i.next();
            
            if ( ChampionsMatcher.matches( indexName + "[0-9]+\\..*", key ) ){
                v.add(key);
            }
        }
        
        i = v.iterator();
        
        while ( i.hasNext()) {
            key = (String) i.next();
            
            detailList.remove(key);
        }
        
        detailList.put( indexName + ".INDEXSIZE", new Integer(0));
        
        if ( fireChange ) {
            fireIndexedChanged(indexName);
        }
    }
    
    public String getName() {
        return "Unnamed";
    }
    
    public String getFileExtension() {
        return "lst";
    }
    
    private synchronized void compactIndexed(int index, String indexName, boolean fireChange) {
        int count = getIndexedSize(indexName);
        int j;
        Iterator i;
        String oldname, newname;
        String currentKey, newKey;
        
        if ( DEBUG >= 3) System.out.println("Compacting Index " + indexName + " of " + this.getClass() );
        
        // First Check to make sure that there are definitely no entries left with the
        // index we are removing.
        
        i = detailList.keySet().iterator();
        oldname = indexName + Integer.toString(index) + ".";
        
        while ( i.hasNext() ) {
            currentKey = (String) i.next();
            if ( currentKey.startsWith( oldname ) ){
                // Found one.  This is an error.
                return;
            }
        }
        
        // Make a temporary tree of everything that needs to be moved around
        TreeMap tempMap = new TreeMap();
        
        for(j=index;j<count;j++) {
            oldname= indexName + Integer.toString(j + 1) + ".";
            newname = indexName + Integer.toString(j) + ".";
            
            i = detailList.keySet().iterator();
            
            while ( i.hasNext() ) {
                currentKey = (String) i.next();
                if ( currentKey.startsWith( oldname ) ){
                    newKey = newname + currentKey.substring( oldname.length() );
                    
                    tempMap.put(newKey, detailList.get(currentKey) );
                    i.remove();
                }
            }
        }
        
        detailList.putAll(tempMap);
        
        this.add( indexName + ".INDEXSIZE", new Integer(count - 1), true, fireChange);
        
    }
    
    public boolean containsIndexed(int index, String indexName, String type) {
        return contains( indexName + Integer.toString(index) + type);
    }
    
    public void displayDebugWindow() {
        debugDetailList("");
    }
    
    public void debugDetailList(String windowName) {
        JFrame f = new JFrame( toString() + "  [ " + getClass().getName() + "@" + Integer.toHexString(hashCode()) + " ]");
        DetailListEditor dle = new DetailListEditor();
        
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(dle);
        f.pack();
        f.setVisible(true);
        dle.setDetailList(this);
    }
    
    public void save() throws FileNotFoundException, IOException{
        if ( file == null ) {
            MyFileChooser chooser = MyFileChooser.chooser;
            
            MyFileFilter mff = new MyFileFilter(getFileExtension());
            chooser.setFileFilter(mff);
            
            chooser.setDialogTitle( "Save Location for " + this.getName());
            chooser.setSelectedFile( new File(chooser.getCurrentDirectory(), this.getName() + "." + this.getFileExtension() ));
            int returnVal = chooser.showSaveDialog(null);
            
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = chooser.getSelectedFile();
                    setFile(file);
                }
                catch ( Exception exc ) {
                    return;
                }
            }
            else {
                return;
            }
        }
        
        FileOutputStream fos = new FileOutputStream( getFile() );
        ObjectOutputStream out = new ObjectOutputStream(fos);
        
        out.writeObject(this);
        out.flush();
        out.close();
        
    }
    
    public static DetailList open(File file) throws FileNotFoundException, IOException, ClassNotFoundException{
        // First find the IOAdapter for the File type
        String extension = null;
        String filename = file.getName();
        int i = filename.lastIndexOf('.');
        if(i>0 && i<filename.length()-1) {
            extension = filename.substring(i+1).toLowerCase();
        };
        
        if ( extension != null ) {
            // Use the New IOAdapter methodology
            IOAdapter ioa = PADRoster.getReadAdapterForExtension(extension);
            if ( ioa != null ) {
                int format = ioa.lookupReadFileExtension(extension);
                if ( format != -1 ) {
                    FileInputStream fis = new FileInputStream(file);
                    DetailList d = (DetailList)ioa.readObject(file, format);
                    return d;
                }
            }
        }
        
        // Fall back to the serialize open version
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        DetailList d = (DetailList) ois.readObject();
        d.postRead();
        
        d.setFile(file);
        return d;
    }
    
    public File getFile() {
        return file;
    }
    
    public void setFile(File file) {
        this.file = file;
    }
    
    public void save(File file) throws FileNotFoundException, IOException{
        setFile(file);
        save();
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        
        in.defaultReadObject();
        detailListIndex = new Integer( getNextDetailListIndex() );
        addDetailListReference( this );
        propertyChangeSupport = new ExtendedPropertyChangeSupport(this);
        

         //   postRead();
    }
    
    /** Called After to reading of a detailList.
     *
     * An fix-up of the detailList after it has been read should be done
     * here.
     */
    protected void postRead() {
        
    }
    
//    static private int depth = 0;
    
    private void writeObject(ObjectOutputStream out) throws IOException {
//        for(int i = 0; i < depth; i++) {
//            System.out.print("  ");
//        }
//        if ( this instanceof Target) {
//            //depth = 0;
//            System.out.println("Writing " + this.getClass() + " " + ((Target)this).getName() );
//        }
//        else {
//            System.out.println("Writing " + this.getClass());
//        }
//        depth++;
        preWrite();
        out.defaultWriteObject();
//        depth--;

    }
    
    /** Called Prior to writing of the detailList.
     *
     * An cleanup that needs to be done prior to writing, such as removing
     * non-serializable objects from the detaillist, should be done here.
     */
    protected void preWrite() {
        
    }
    
    
    static public DetailList open(String[] extensions, String desc, Class c){
        MyFileChooser chooser = MyFileChooser.chooser;
        
        if ( extensions != null ) {
            // Lets build some FileFilters and add them to the
            // chooser.
            chooser.resetChoosableFileFilters();
            MyFileFilter groupFilter = new MyFileFilter(extensions, desc);
            
            
            Map m = PADRoster.getReadInfoForClass(c);
            Iterator i = m.keySet().iterator();
            while( i.hasNext() ) {
                String ext = (String)i.next();
                MyFileFilter mff = new MyFileFilter(ext, (String)m.get(ext) );
                groupFilter.addExtension(ext);
                chooser.setFileFilter(mff);
            }
            
            chooser.setFileFilter(groupFilter);
        }
        chooser.setDialogTitle( "Open" );
        
        int returnVal = chooser.showOpenDialog(null);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                
                DetailList d = open(file);
                if ( c != null && c.isInstance(d) ){
                    return d;
                }
                else {
                    JOptionPane.showMessageDialog(null,
                    "An Error Occurred while opening:\n" + "Incompatible File Type",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                }
            }
            catch (Exception exc) {
                ExceptionWizard.postException(exc);
//                JOptionPane.showMessageDialog(null,
//                "An Error Occurred while opening:\n" +
//                exc.toString(),
//                "Error",
//                JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
    
    static public DetailList[] openMultiple(String[] extensions, String desc, Class c){
        MyFileChooser chooser = MyFileChooser.chooser;
        
        if ( extensions != null ) {
            // Lets build some FileFilters and add them to the chooser.
            chooser.resetChoosableFileFilters();
            MyFileFilter groupFilter = new MyFileFilter(extensions, desc);
            
            
            Map m = PADRoster.getReadInfoForClass(c);
            Iterator i = m.keySet().iterator();
            while( i.hasNext() ) {
                String ext = (String)i.next();
                MyFileFilter mff = new MyFileFilter(ext, (String)m.get(ext) );
                groupFilter.addExtension(ext);
                chooser.setFileFilter(mff);
            }
            
            chooser.setFileFilter(groupFilter);
        }
        chooser.setDialogTitle( "Open" );
        chooser.setMultiSelectionEnabled(true);
        
        int returnVal = chooser.showOpenDialog(null);
        
        Set<DetailList> s = new HashSet<DetailList>();
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                File[] files = chooser.getSelectedFiles();
                
                for(File file : files) {
                    DetailList d = open(file);
                    if ( c != null && c.isInstance(d) ){
                        s.add(d);
                    }
                    else {
                        JOptionPane.showMessageDialog(null,
                        "An Error Occurred while opening:\n" + "Incompatible File Type",
                        "Error",
                        JOptionPane.ERROR_MESSAGE); 
                    }
                }
            }
            catch (Exception exc) {
                ExceptionWizard.postException(exc);
//                JOptionPane.showMessageDialog(null,
//                "An Error Occurred while opening:\n" +
//                exc.toString(),
//                "Error",
//                JOptionPane.ERROR_MESSAGE);
            }
        }
        return s.toArray(new DetailList[0] );
    }
    
    public Object[] parseParameters( Object[][] parameterArray ) {
        if ( parameterArray == null ) return null;
        
        int index, length;
        Object[] returnArray = null;
        
        length = parameterArray.length;
        returnArray = new Object[length];
        
        for (index = 0; index < length; index++ ) {
            if ( parameterArray[index].length != 4 ) {
                returnArray[index] = null;
                continue;
            }
            
            String key = (String)parameterArray[index][1];
            Class type = (Class)parameterArray[index][2];
            Object defaultValue = parameterArray[index][3];
            
            Object value;
            value = getValue( key );
            if ( value == null || value.getClass() != type ) {
                returnArray[index] = defaultValue;
            }
            else {
                returnArray[index] = value;
            }
        }
        
        return returnArray;
    }
    
    public Object parseParameter( Object[][] parameterArray, String parameter ) {
        if ( parameterArray == null || parameter == null) return null;
        
        int index, length;
        Object returnObject = null;
        
        length = parameterArray.length;
        
        for (index = 0; index < length; index++ ) {
            if ( parameterArray[index].length < 4 ) {
                continue;
            }
            
            String name = (String)parameterArray[index][0];
            
            if ( parameter.equals(name) ) {
                String key = (String)parameterArray[index][1];
                Class type = (Class)parameterArray[index][2];
                Object defaultValue = parameterArray[index][3];
                
                Object value;
                value = getValue( key );
                if ( value == null || value.getClass() != type ) {
                    returnObject = defaultValue;
                }
                else {
                    returnObject = value;
                }
                break;
            }
        }
        
        return returnObject;
    }
    
    static public int getIndexCount() {
        return nextDetailListIndex;
    }
    
    static public int getRealCount() {
        return detailListHash.size();
    }
    
    
    public synchronized void fireIndexedChanged( String indexName ) {
        final String key = indexName + ".INDEXSIZE";
        final Object value = detailList.get(key);
        firePropertyChange(this,key,new Integer(-1),value);
        
        /*if ( propertyChangeSupport.hasListeners(key)
        &&  value != null ) {
         
        if ( SwingUtilities.isEventDispatchThread() ) {
        if ( Battle.debugLevel > 2 ) System.out.println( "DetailList Property Change (immediate): " + key + " " + value + " " + value);
        propertyChangeSupport.firePropertyChange (key, value, value);
        }
        else {
        if ( Battle.debugLevel > 2 ) System.out.println( "DetailList Property Change (invoked): " + key + " " + value + " " + value);
        Runnable propertyChangeRunnable = new Runnable() {
        public void run() {
        propertyChangeSupport.firePropertyChange (key, value, value);
         
        }
        };
        SwingUtilities.invokeLater(propertyChangeRunnable);
        }
        } */
    }
    
    public Set keySet() {
        return detailList.keySet();
    }
    
    public String nameFromKey(String key) {
        return key.substring(0, key.indexOf("."));
    }
    
    public String typeFromKey(String key) {
        return key.substring(key.indexOf(".")+1);
    }
    
    private void propertyChangeDebug(String key, Object oldValue, Object newValue) {
        
        System.out.println( this + " sent propertyChange (" + Integer.toString(listeners) + " listeners): " + key + " " + oldValue + " => " + newValue );
        //System.out.println( this + " PCS: " + propertyChangeSupport.toString() );
    }
    
    public IndexIterator getIteratorForIndex(String indexName) {
        return new DetailList.OneDimensionalIndexIterator(this, indexName);
    }
    
    public IndexIterator getIteratorForIndex(String indexName, String restrictType, String restrictValue) {
        return new DetailList.TwoDimensionalIndexIterator(this, indexName, restrictType, restrictValue);
    }
    
    public IndexIterator getIteratorForIndex(String indexName, Object[][] restrictArray) {
        return new DetailList.MultiDimensionalIndexIterator(this, indexName, restrictArray);
    }
    
    /** Getter for property fireChangeByDefault.
     * @return Value of property fireChangeByDefault.
     */
    public boolean isFireChangeByDefault() {
        return fireChangeByDefault;
    }
    
    /** Setter for property fireChangeByDefault.
     * @param fireChangeByDefault New value of property fireChangeByDefault.
     */
    public void setFireChangeByDefault(boolean fireChangeByDefault) {
        this.fireChangeByDefault = fireChangeByDefault;
    }
    
    public static void recordRead(DetailList dl, String key) {
        String className = dl.getClass().toString();
        String fullKey = className + "." + key;
        
        Object o = readUsageMap.get(fullKey);
        if ( o == null ) {
            readUsageMap.put(fullKey, new Integer(1));
        }
        else {
            readUsageMap.put(fullKey, new Integer( ((Integer)o).intValue() + 1));
        }
    }
    
    public static void recordWrite(DetailList dl, String key) {
        String className = dl.getClass().toString();
        String fullKey = className + "." + key;
        
        Object o = writeUsageMap.get(fullKey);
        if ( o == null ) {
            writeUsageMap.put(fullKey, new Integer(1));
        }
        else {
            writeUsageMap.put(fullKey, new Integer( ((Integer)o).intValue() + 1));
        }
    }
    
    public static void dumpDetailListUsage() {
        System.out.println("DetailList Usage:");
        String key;
        Integer v;
        
        SortedMap<Integer,String> sm;
        Iterator i;
        Iterator<Integer> ii;
        
        sm = new TreeMap<Integer,String>();
        
        i = writeUsageMap.keySet().iterator();
        while ( i.hasNext() ) {
            key = (String)i.next();
            v = (Integer)writeUsageMap.get(key);
            sm.put(v,key);
        }
        
        ii = sm.keySet().iterator();
        while ( ii.hasNext() ) {
            v = ii.next();
            key = sm.get(v);
            System.out.println( key + "(WRITE) = " + v.toString());
        }
        
        sm = new TreeMap<Integer,String>();
        
        i = readUsageMap.keySet().iterator();
        while ( i.hasNext() ) {
            key = (String)i.next();
            v = (Integer)readUsageMap.get(key);
            sm.put(v,key);
        }
        
        ii = sm.keySet().iterator();
        while ( ii.hasNext() ) {
            v = ii.next();
            key = sm.get(v);
            System.out.println( key + "(READ) = " + v.toString());
        }
    }
    
    public static void exitHook() {
        if ( TRACK_USAGE > 0 ) dumpDetailListUsage();
    }
    
    /** Completely Destroys the DetailList, freeing all of the referenced elements.
     *
     * This method will iterate the underlying hash map and free all of the related resources.
     * If the objects stored in the hash map implement destroyable, the destoy method will be
     * called on them prior to destrution.
     *
     * Subclasses can override this method in order to provide special handling of the destruction
     * of the detailList.  However, it is usually preferable that the destroyValues(),
     * destroyListeners(), destoryFile(), and destroyOthers() methods be used to destroy the object.
     */
    public synchronized void destroy() {
        if ( beingDestroyed == false ) {
            beingDestroyed = true;
            destroyValues();
            
            destroyListeners();
            
            destroyFile();
            
            destroyOthers();
            beingDestroyed = false;
        }
    }
    
    /** Destorys the propertyChangeSupport reference associated with the detail list.
     *
     * Subclasses can override this for special handling of the
     * detaillist object contained by the list.
     * The reference should be set to null if the list is completely done using it.
     */
    protected void destroyValues() {
        if ( detailList != null ) {
            Iterator i = detailList.keySet().iterator();
            
            // Destroy/Remove all key/value pairs.
            while (i.hasNext() ) {
                Object key = i.next();
                Object value = detailList.get(key);
                
                // Destroy Destroyables which aren't detailLists.
                if ( value instanceof Destroyable && value instanceof DetailList == false ) {
                    ((Destroyable)value).destroy();
                    //  System.out.println("ActivationInfo::Destroying " + value );
                }
            }
            detailList.clear();
            detailList = null;
        }
    }
    
    /** Destorys the propertyChangeSupport reference associated with the detail list.
     *
     * Subclasses can override this for special handling of the
     * propertyChangeSupport object contained by the list.
     * The reference should be set to
     * null if the list is completely done using it.
     */
    protected void destroyListeners() {
        // remove all listeners
        if ( propertyChangeSupport != null ) {
            propertyChangeSupport.destroy();
            propertyChangeSupport = null;
            listeners=0;
        }
    }
    
    /** Destorys the file reference associated with the detail list.
     *
     * Subclasses can override this for special handling of the file
     * object contained by the list.  The reference should be set to
     * null if the list is completely done using it.
     */
    protected void destroyFile() {
        // remove the file header
        file = null;
    }
    
    /** Destorys other instance variables associated with the object.
     *
     * Subclasses can override this for special handling of the destruction
     * of instance variables.  When destroying an object, all instance
     * variables that reference something should be set to null and the
     * references themselves should be destroyed whenever possible.
     */
    protected void destroyOthers() {
        
    }
    
    public class OneDimensionalIndexIterator extends Object
    implements IndexIterator {
        private DetailList dl;
        private String indexName;
        private int nextIndex;
        
        public OneDimensionalIndexIterator(DetailList dl, String indexName) {
            nextIndex = 0;
            this.dl = dl;
            this.indexName = indexName;
        }
        
        public boolean hasNext() {
            return nextIndex < dl.getIndexedSize(indexName);
        }
        
        public int nextIndex() throws NoSuchElementException {
            if ( nextIndex >= dl.getIndexedSize(indexName) ) {
                throw new NoSuchElementException();
            }
            
            int result = nextIndex;
            nextIndex = nextIndex + 1;
            
            return result;
        }
    }
    
    public class TwoDimensionalIndexIterator extends Object
    implements IndexIterator {
        private int currentIndex;
        private DetailList dl;
        private String indexName;
        private String restrictionType;
        private String restrictionValue;
        private int nextIndex;
        
        public TwoDimensionalIndexIterator(DetailList dl, String indexName, String restrictionType, String restrictionValue) {
            currentIndex = 0;
            this.dl = dl;
            this.indexName = indexName;
            this.restrictionType = restrictionType;
            this.restrictionValue = restrictionValue;
            // lastSize = ai.getTargetGroupSize(targetGroup);
            nextIndex = -1;
        }
        
        public boolean hasNext() {
            if ( currentIndex == -1 ) {
                return false;
            }
            else {
                if ( nextIndex == -1 ) {
                    nextIndex = getNextIndex();
                }
            }
            return nextIndex != -1;
        }
        
        public int nextIndex() throws NoSuchElementException {
            if ( currentIndex == -1 ) {
                throw new NoSuchElementException();
            }
            else if ( nextIndex == -1 ) {
                nextIndex = getNextIndex();
                
                // If it still equals -1, we have run out of elements
                if ( nextIndex == -1 ) {
                    throw new NoSuchElementException();
                }
            }
            
            int result = nextIndex;
            nextIndex = -1;
            return result;
        }
        
        private int getNextIndex() {
            int next = dl.findIndexed(currentIndex, indexName, restrictionType, restrictionValue);
            if ( next != -1 ) {
                // Set the currentIndex (which really indicates where to start looking) to
                // nextIndex (the most recent one found) + 1.
                currentIndex = next + 1;
            }
            else {
                // There wasn't a new one found, so just make the currentIndex as -1 to indicate
                // we are done.
                currentIndex = -1;
            }
            return next;
        }
    }
    
    public class MultiDimensionalIndexIterator extends Object
    implements IndexIterator {
        private int currentIndex;
        private DetailList dl;
        private String indexName;
        private Object[][] restrictionArray;
        private int nextIndex;
        
        public MultiDimensionalIndexIterator(DetailList dl, String indexName, Object[][] restrictionArray) {
            currentIndex = 0;
            this.dl = dl;
            this.indexName = indexName;
            this.restrictionArray = restrictionArray;
            // lastSize = ai.getTargetGroupSize(targetGroup);
            nextIndex = -1;
        }
        
        public boolean hasNext() {
            if ( currentIndex == -1 ) {
                return false;
            }
            else {
                if ( nextIndex == -1 ) {
                    nextIndex = getNextIndex();
                }
            }
            return nextIndex != -1;
        }
        
        public int nextIndex() throws NoSuchElementException {
            if ( currentIndex == -1 ) {
                throw new NoSuchElementException();
            }
            else if ( nextIndex == -1 ) {
                nextIndex = getNextIndex();
                
                // If it still equals -1, we have run out of elements
                if ( nextIndex == -1 ) {
                    throw new NoSuchElementException();
                }
            }
            
            int result = nextIndex;
            nextIndex = -1;
            return result;
        }
        
        private int getNextIndex() {
            int next = -1;
            int count = getIndexedSize(indexName);
            boolean found = false;
            
            while( currentIndex < count ) {
                if ( checkIndex(currentIndex) ) {
                    // This is a good one
                    next = currentIndex;
                    found = true;
                    currentIndex ++; // Set the currentIndex one higher for the next pass.
                    break;
                }
                
                currentIndex++;
            }
            
            if ( found == false ) {
                // There wasn't a new one found, so just make the currentIndex as -1 to indicate
                // we are done.
                currentIndex = -1;
            }
            return next;
        }
        
        private boolean checkIndex(int index) {
            boolean result = true;
            int rindex;
            Object value;
            
            for (rindex = 0; rindex < restrictionArray.length; rindex++) {
                value = getIndexedValue(index, indexName, (String)restrictionArray[rindex][0]);
                if ( restrictionArray[rindex][1].equals(value) == false ) {
                    result = false;
                    break;
                }
            }
            return result;
        }
    }
    
    public class ExtendedPropertyChangeSupport extends Object implements Destroyable {
        
        /**
         * Constructs a <code>ExtendedPropertyChangeSupport</code> object.
         *
         * @param sourceBean  The bean to be given as the source for any events.
         */
        
        public ExtendedPropertyChangeSupport(Object sourceBean) {
            if (sourceBean == null) {
                throw new NullPointerException();
            }
            source = sourceBean;
        }
        
        /**
         * Add a PropertyChangeListener to the listener list.
         * The listener is registered for all properties.
         *
         * @param listener  The PropertyChangeListener to be added
         */
        
        public synchronized void addPropertyChangeListener(
        PropertyChangeListener listener) {
            if (listeners == null) {
                listeners = new Vector();
            }
            listeners.addElement(listener);
        }
        
        /**
         * Remove a PropertyChangeListener from the listener list.
         * This removes a PropertyChangeListener that was registered
         * for all properties.
         *
         * @param listener  The PropertyChangeListener to be removed
         */
        
        public synchronized void removePropertyChangeListener(
        PropertyChangeListener listener) {
            if (listeners == null) {
                return;
            }
            listeners.removeElement(listener);
        }
        
        /**
         * Add a PropertyChangeListener for a specific property.  The listener
         * will be invoked only when a call on firePropertyChange names that
         * specific property.
         *
         * @param propertyName  The name of the property to listen on.
         * @param listener  The PropertyChangeListener to be added
         */
        
        public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            if (children == null) {
                children = new Hashtable();
            }
            
            DetailList.ExtendedPropertyChangeSupport child = (DetailList.ExtendedPropertyChangeSupport)children.get(propertyName);
            if (child == null) {
                child = new DetailList.ExtendedPropertyChangeSupport(source);
                children.put(propertyName, child);
            }
            child.addPropertyChangeListener(listener);
        }
        
        /**
         * Remove a PropertyChangeListener for a specific property.
         *
         * @param propertyName  The name of the property that was listened on.
         * @param listener  The PropertyChangeListener to be removed
         */
        
        public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            if (children == null) {
                return;
            }
            ExtendedPropertyChangeSupport child = (DetailList.ExtendedPropertyChangeSupport)children.get(propertyName);
            if (child == null) {
                return;
            }
            child.removePropertyChangeListener(listener);
        }
        
        /**
         * Report a bound property update to any registered listeners.
         * No event is fired if old and new are equal and non-null.
         *
         * @param propertyName  The programmatic name of the property
         *		that was changed.
         * @param oldValue  The old value of the property.
         * @param newValue  The new value of the property.
         */
        public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
            
            if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
                return;
            }
            
            Vector targets = null;
            ExtendedPropertyChangeSupport child = null;
            synchronized (this) {
                if (listeners != null) {
                    targets = (Vector) listeners.clone();
                }
                if (children != null && propertyName != null) {
                    child = (DetailList.ExtendedPropertyChangeSupport)children.get(propertyName);
                }
            }
            
            PropertyChangeEvent evt = new PropertyChangeEvent(source,
            propertyName, oldValue, newValue);
            
            if (targets != null) {
                for (int i = 0; i < targets.size(); i++) {
                    PropertyChangeListener target = (PropertyChangeListener)targets.elementAt(i);
//                    if ( source instanceof ParameterList ) {
//                        System.out.println("  ParameterList.dispatchPropertyChange to " + target.getClass());
//                    }
                    target.propertyChange(evt);
                }
            }
            
            if (child != null) {
                child.firePropertyChange(evt);
            }
        }
        
        /**
         * Report an int bound property update to any registered listeners.
         * No event is fired if old and new are equal and non-null.
         * <p>
         * This is merely a convenience wrapper around the more general
         * firePropertyChange method that takes Object values.
         *
         * @param propertyName  The programmatic name of the property
         *		that was changed.
         * @param oldValue  The old value of the property.
         * @param newValue  The new value of the property.
         */
        public void firePropertyChange(String propertyName,int oldValue, int newValue) {
            if (oldValue == newValue) {
                return;
            }
            firePropertyChange(propertyName, new Integer(oldValue), new Integer(newValue));
        }
        
        
        /**
         * Report a boolean bound property update to any registered listeners.
         * No event is fired if old and new are equal and non-null.
         * <p>
         * This is merely a convenience wrapper around the more general
         * firePropertyChange method that takes Object values.
         *
         * @param propertyName  The programmatic name of the property
         *		that was changed.
         * @param oldValue  The old value of the property.
         * @param newValue  The new value of the property.
         */
        public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
            if (oldValue == newValue) {
                return;
            }
            firePropertyChange(propertyName, new Boolean(oldValue), new Boolean(newValue));
        }
        
        /**
         * Fire an existing PropertyChangeEvent to any registered listeners.
         * No event is fired if the given event's old and new values are
         * equal and non-null.
         * @param evt  The PropertyChangeEvent object.
         */
        public void firePropertyChange(PropertyChangeEvent evt) {
            Object oldValue = evt.getOldValue();
            Object newValue = evt.getNewValue();
            String propertyName = evt.getPropertyName();
            if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
                return;
            }
            
            Vector targets = null;
            ExtendedPropertyChangeSupport child = null;
            synchronized (this) {
                if (listeners != null) {
                    targets = (Vector) listeners.clone();
                }
                if (children != null && propertyName != null) {
                    child = (DetailList.ExtendedPropertyChangeSupport)children.get(propertyName);
                }
            }
            
            if (targets != null) {
                for (int i = 0; i < targets.size(); i++) {
                    PropertyChangeListener target = (PropertyChangeListener)targets.elementAt(i);
//                    if ( source instanceof ParameterList ) {
//                        System.out.println("  ParameterList.dispatchPropertyChange to " + target.getClass());
//                    }
                    target.propertyChange(evt);
                }
            }
            if (child != null) {
                child.firePropertyChange(evt);
            }
        }
        
        /**
         * Check if there are any listeners for a specific property.
         *
         * @param propertyName  the property name.
         * @return true if there are ore or more listeners for the given property
         */
        public synchronized boolean hasListeners(String propertyName) {
            if (listeners != null && !listeners.isEmpty()) {
                // there is a generic listener
                return true;
            }
            if (children != null) {
                ExtendedPropertyChangeSupport child = (ExtendedPropertyChangeSupport)children.get(propertyName);
                if (child != null && child.listeners != null) {
                    return !child.listeners.isEmpty();
                }
            }
            return false;
        }
        
        public int getListenerCount() {
            return listeners == null ? 0 : listeners.size();
        }
        
        /**
         * @serialData Null terminated list of <code>PropertyChangeListeners</code>.
         * <p>
         * At serialization time we skip non-serializable listeners and
         * only serialize the serializable listeners.
         *
         */
        private void writeObject(ObjectOutputStream s) throws IOException {
            
            s.defaultWriteObject();
            
            Vector v = null;
            synchronized (this) {
                if (listeners != null) {
                    v = (Vector) listeners.clone();
                }
            }
            
            if (v != null) {
                for (int i = 0; i < v.size(); i++) {
                    PropertyChangeListener l = (PropertyChangeListener)v.elementAt(i);
                    if (l instanceof Serializable) {
                        s.writeObject(l);
                    }
                }
            }
            s.writeObject(null);
        }
        
        
        private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
            s.defaultReadObject();
            
            Object listenerOrNull;
            while (null != (listenerOrNull = s.readObject())) {
                addPropertyChangeListener((PropertyChangeListener)listenerOrNull);
            }
        }
        
        public synchronized void destroy() {
            Iterator i;
            
            if ( listeners != null ) {
                listeners.clear();
                listeners = null;
            }
            
            if ( children != null ) {
                i = children.values().iterator();
                while (i.hasNext()) {
                    Object entry = i.next();
                    //System.out.println(entry);
                    ((DetailList.ExtendedPropertyChangeSupport)entry).destroy();
                    //i.remove();
                }
                children.clear();
                children = null;
            }
            
            source = null;
        }
        
        /**
         * "listeners" lists all the generic listeners.
         *
         *  This is transient - its state is written in the writeObject method.
         */
        private transient Vector listeners;
        
        /**
         * Hashtable for managing listeners for specific properties.
         * Maps property names to ExtendedPropertyChangeSupport objects.
         * @serial
         * @since 1.2
         */
        private Hashtable children;
        
        /**
         * The object to be provided as the "source" for any generated events.
         * @serial
         */
        private Object source;
        
        /**
         * Internal version number
         * @serial
         * @since
         */
        private int propertyChangeSupportSerializedDataVersion = 2;
        
        /**
         * Serialization version ID, so we're compatible with JDK 1.1
         */
        static final long serialVersionUID = 6401253773779951803L;
    }
}
