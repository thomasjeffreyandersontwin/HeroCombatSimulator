/*
 * TargetList.java
 *
 * Created on February 8, 2002, 2:46 PM
 */

package champions;

import champions.exceptionWizard.ExceptionWizard;
import champions.interfaces.Debuggable;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;


/** 
 *
 * @author  twalker
 * @version
 */
public class TargetList  implements Serializable, Debuggable {
    static final long serialVersionUID = 8050853406152522L;
    
    transient private EventListenerList eventListenerList = new EventListenerList();
    transient private PropertyChangeSupport propertyChangeSupport;
    
    protected String name;
    protected TargetList parent;
    protected List<TargetList> sublists;
    protected List<Target> targets;
    
    /** Creates new TargetList */
    public TargetList() {
    }
    
    public TargetList(String name) {
        this();
        setName(name);
    }
    
    /** Sets the Parent list of this list.
     *
     * This method will set the parent list of this list.  This method does not
     * check to see that the list/sublist structure is correct and should not be
     * called directly.  Instead addSublist/removeSublist should be called.
     */
    public void setParent(TargetList parent) {
        //add("TargetList.PARENT", parent, true);
        this.parent = parent;
    }
    
    /** Returns the Parent list of this list.
     */
    public TargetList getParent() {
        //return (TargetList)getValue("TargetList.PARENT");
        return parent;
    }
    
    /** Returns the count of the child sublists of this list.
     */
    public int getSublistCount() {
        //return getIndexedSize( "TargetList" );
        if ( sublists == null ) return 0;
        else return sublists.size();
    }
    
    /** Returns an TargetIterator which iterates through all the Targets in the list.
     *
     * This version of getTargets should recursively iterator through all of the Targets
     * contained in this list.  Calling this method is equivalent to calling
     * getTargets(true);
     */
    public Iterator getTargets() {
        return getTargets(true);
    }
    
    /** Returns an TargetIterator which iterates through the Targets in the list.
     *
     * This version of getTargets will iterate through the Targets in the list.
     * If the recursive flag is true, it will recursively iterator through it's sublists
     * also.
     */
    public Iterator getTargets(boolean recursive) {
        return new TargetList.MyTargetIterator(this, recursive);
    }
    
    /** Returns the child sublist indicated by specified index.
     */
    public TargetList getSublist(int index) {
        //return (TargetList)getIndexedValue(index, "TargetList", "TargetList");
        return sublists.get(index);
    }
    
    /** Clones the list.
     *
     * This method will create a recursive clone of the list.  However, the cloned copy will have
     * it's target set to null and will no longer be a child of it's current parent.
     */
    public TargetList cloneList() throws CloneNotSupportedException {
        // The root Target list cannot be cloned really...
        throw new CloneNotSupportedException("Clone Not support yet!");
    }
    
    /** Adds the specified sublist to this list.
     *
     * This method adds the specified sublist as a child of this list.  If the
     * sublist previously belonged to a different sublist, it will be removed
     * from that sublist.  If the sublist previously belonged to a different
     * target, the sublists target will be updated.
     */
    public void addSublist(TargetList sublist) {
        TargetList oldParent = sublist.getParent();
        
        TargetList p = this;
        while ( p != null ) {
            if ( p == sublist ) {
                ExceptionWizard.postException(new Exception("Adding Sublist would cause loop."));
                return;
            }
            p = p.getParent();
        }
        
        if ( oldParent != this ) {
            if ( oldParent != null ) {
                // This sublist's current parent is non-null, so remove it from that sublist.
                oldParent.removeSublist(sublist);
            }
            
            if ( sublists == null ) sublists = new ArrayList<TargetList>();
            
            //int index = createIndexed( "TargetList", "TargetList", sublist);
            sublists.add(sublist);
            
            sublist.setParent(this);
            
            fireChangeEvent( "Sublist Added" );
            TargetModificationInSublist("Sublist Structure Changed");
        }
    }
    
    /** Removed the specified sublist from this list.
     *
     * This method removed the specified sublist from the list.  The sublists
     * target will be set to null and the sublists parent will be set to null.
     *
     * If the sublist is not a direct child of this list, nothing will be done.
     */
    public void removeSublist(TargetList sublist) {
        //int index = findExactIndexed("TargetList", "TargetList", sublist);
        if ( sublists == null ) return;
        
        int index = sublists.indexOf(sublist);
        if ( index != -1 ) {
            //removeAllIndexed(index, "TargetList");
            sublists.remove(index);
            sublist.setParent(null);
            
            fireChangeEvent( "Sublist Removed" );
        }
    }
    
    /** Returns an array of the child sublists of this list.
     */
    public TargetList[] getSublists() {
        int index, count;
        
        count = getSublistCount();
        TargetList[] al = new TargetList[count];
        
        if ( sublists != null ) {
            for(index=0; index<count;index++) {
                al[index] = getSublist(index);
            }
        }
        
        return al;
    }
    

    
    /** Returns the number of Targets contained in the list.
     *
     * This method returns the number of Targets directly contained in this sublist.  It does
     * not return the count from any sublists.
     */
    public int getTargetCount() {
        //return getIndexedSize("Target");
        if ( targets == null ) return 0;
        else return targets.size();
    }
    
    /** Returns the Index of the Given Target.
     *
     * The index indicates the location of the Target in the list, starting with location 0.
     * This can be used to place Targets in specific orders.
     */
    public int getTargetIndex(Target target) {
        //return findExactIndexed("Target", "Target", Target);
        if ( targets != null ) {
            for(int i = 0; i < targets.size(); i++) {
                if ( targets.get(i) == target ) return i;
            }
        }
        return -1;
    }
    
    /** Returns the indicated Target contained in this list.
     *
     * This method returns the Target at indicated index contained in this list.  It does
     * not return any Targets from sublists.
     */
    public Target getTarget(int aindex) {
        //return (Target)getIndexedValue(aindex, "Target", "Target");
        return targets.get(aindex);
    }
    
    /** Returns the Name of the list.
     *
     * Returns the list's name.
     */
    public String getName() {
        // getStringValue("TargetList.NAME");
        return name;
    }
    
    /** Set the Name of the list.
     *
     */
    public void setName(String name) {
        //add("TargetList.NAME", name, true);
        this.name = name;
    }
    
    /** Adds the Target to the List.
     *
     * This method will add the Target to the list.  The Targets target and sublist
     * parameters will be updated appropriately.
     */
    public void addTarget(Target target, int insertIndex) {
        if ( targets == null ) targets = new ArrayList<Target>();
        
        //int index = findExactIndexed("Target", "Target", Target);
        
        
        if ( insertIndex > targets.size() ) insertIndex = targets.size();
        
        int index = getTargetIndex(target);
        if ( index == -1 ) {
            //index = createIndexed(insertIndex, "Target", "Target", Target );
            targets.add(insertIndex, target);
            
            fireChangeEvent( "Target Added" );
            TargetModificationInSublist("Target Added");
        }
        else if ( index != insertIndex ) {
            //moveIndexed(index, insertIndex, "Target", false);
            targets.remove(index);
            if ( index < insertIndex ) insertIndex--;
            
            targets.add(insertIndex, target);
            
            fireChangeEvent( "Target Moved" );
            TargetModificationInSublist("Target Moved");
        }
    }
    
    /** Adds the Target to the List.
     *
     * This method will add the Target to the list.  The Targets target and sublist
     * parameters will be updated appropriately.
     */
    public void addTarget(Target target) {
        addTarget(target, Integer.MAX_VALUE);
    }
    
        /** Removes the Target from this list.
     *
     * This method will remove the Target from this list.  The Targets target and sublist
     * parameters will be set to null.
     */
    public void removeTarget(Target target) {
        //int index = findExactIndexed("Target", "Target", Target);
        int index = getTargetIndex(target);
        
        if ( index != -1 ) {
            //removeAllIndexed(index, "Target");
            targets.remove(index);
            
            fireChangeEvent( "Target Removed" );
            TargetModificationInSublist("Target Removed");
        }
    }
    
    /** Returns the sublist with the indicated name.
     *
     * If this list's name is equals to the indicated one, it returns this.  Otherwise, it searchs
     * recursively through sublists to find the name.
     *
     * Returns null if the name is not found.
     */
    public TargetList findSublist(String sublistName) {
        TargetList targetList = null;
        
        if ( getName() == sublistName || (getName() != null && getName().equals(sublistName)) ) {
            targetList = this;
        }
        else {
            int index, count;
            count = getSublistCount();
            for(index = 0; index<count; index++) {
                targetList = getSublist(index).findSublist(sublistName);
                if ( targetList != null ) {
                    break;
                }
            }
        }
        
        return targetList;
    }
    
    /** Set the Index of the given Target.
     *
     * This method sets the index of the given Target, changing the order of the Targets in
     * the list.
     *
     */
    public void setTargetIndex(Target target, int newIndex) {
        int oldIndex = getTargetIndex(target);
        
        if ( newIndex > getTargetCount() ) newIndex = getTargetCount();
        
        if ( oldIndex != -1 && newIndex != -1 && oldIndex != newIndex ) {
            targets.remove(oldIndex);
            if ( oldIndex < newIndex ) newIndex--;
            targets.add(newIndex, target);
            
            fireChangeEvent( "Target Moved" );
            TargetModificationInSublist("Target Moved");
        }
    }
    
    /** Adds a ChangeListener for the List.
     *
     * stateChange events are fired in the following circumstances:
     * When an Target is added directly to this list.
     * When an Target is remove directly from this list.
     * When a sublist is added directly to this list.
     * When a sublist is removed directly from this list.
     * When this is the top most list and an Target is added to any child sublist.
     */
    public void addChangeListener(ChangeListener listener) {
        eventListenerList.add(ChangeListener.class, listener);
    }
    
    /** Removes a ChangeListener for the List.
     */
    public void removeChangeListener(ChangeListener listener) {
        eventListenerList.remove(ChangeListener.class, listener);
    }
    
    /** Add a PropertyChangeListener to the listener list.
     * @param l The listener to add.
     */
    public void addPropertyChangeListener(String property, PropertyChangeListener l) {
//        if ( propertyChangeSupport == null ) propertyChangeSupport = new PropertyChangeSupport(this);
//        propertyChangeSupport.addPropertyChangeListener(property,l);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
//        if ( propertyChangeSupport == null ) propertyChangeSupport = new PropertyChangeSupport(this);
//        propertyChangeSupport.addPropertyChangeListener(l);
    }
    /** Removes a PropertyChangeListener from the listener list.
     * @param l The listener to remove.
     */
    public void removePropertyChangeListener(String property, PropertyChangeListener l) {
//        if (propertyChangeSupport != null ) {
//            propertyChangeSupport.removePropertyChangeListener(property, l);
//        }
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
//        if (propertyChangeSupport != null ) {
//            propertyChangeSupport.removePropertyChangeListener(l);
//        }
    }
    
    protected void fireChangeEvent(final String reason) {
        ChangeEvent e = new ChangeEvent(this);
        // Guaranteed to return a non-null array
        Object[] listeners = eventListenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ChangeListener.class) {
                // System.out.println( "Firing chance event to: " + listeners[i+1].toString());
                ((ChangeListener)listeners[i+1]).stateChanged(e);
            }
        }
    }
    
    public String toString() {
        String s = "TargetList: ";
        
        s = s + getName();
        
        s = s + "[";
        
        int index, count;
        count = getTargetCount();
        if ( count == 0 ) {
            s = s + "No Targets";
        }
        else {
            s = s + "Targets: ";
            
            for (index = 0;index < count; index++) {
                s = s + getTarget(index).getName() ;
                if ( index < count - 1) {
                    s = s + ", ";
                }
            }
        }
        
        s = s + "; ";
        
        count = getSublistCount();
        if ( count == 0 ) {
            s = s + "No Sublists";
        }
        else {
            s = s + "Sublists: ";
            
            for (index = 0;index < count; index++) {
                s = s + getSublist(index).getName() ;
                if ( index < count - 1) {
                    s = s + ", ";
                }
            }
        }
        
        s = s + "]";
        
        return s;
    }
    
    /** Causes the List to recursively notify it's parent that an Target has been added.
     */
    public void TargetModificationInSublist(String reason) {
        if ( getParent() == null ) {
            // System.out.println("TargetList.TargetModificationInSublist(): TargetModificationInSublist fired from Root Parent");
            fireChangeEvent( "Modification to Sublist");
        }
        else {
            getParent().TargetModificationInSublist(reason);
        }
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        
        // Create the proper listener list!
        eventListenerList = new EventListenerList();
    }
    
    /** Returns the Target of the indicated name, if it exists.
     *
     * Return null if the Target does not exist.
     *
     */
    public Target getTarget(String name, boolean recurse) {
        Iterator ai = getTargets(recurse);
        Target Target;
        while (ai.hasNext()) {
            Target = (Target)ai.next();
            if ( Target.getName().equals(name) ) {
                return Target;
            }
        }
        
        return null;
    }    

    /** Returns whether the TargetList contains the indicated Target.
     *
     *
     */
    public boolean hasTarget(Target Target, boolean recursive) {
        Iterator ai = getTargets(recursive);
        Target target2;
        while (ai.hasNext()) {
            target2 = (Target)ai.next();
            if ( target2.equals(Target) ) {
                return true;
            }
        }
        
        return false;
    }    

    public void displayDebugWindow() {
        ObjectDebugger.displayDebugWindow("TargetList Debugger", this);
    }

    public String toDebugString() {
        return toString();
    }
    
    private class MyTargetIterator implements Iterator {
        private TargetList TargetList;
        private boolean recursive;
        
        private int TargetCount;
        private int TargetIndex;
        
        private int sublistCount;
        private int sublistIndex;
        
        private Iterator sublistIterator;
        
        
        public MyTargetIterator(TargetList dal, boolean recursive) {
            TargetList = dal;
            this.recursive = recursive;
            
            initializeIterator();
        }
        
        private void initializeIterator() {
            TargetCount = TargetList.getTargetCount();
            TargetIndex = 0;
            
            if ( recursive ) {
                sublistCount = getSublistCount();
                sublistIndex = 0;
                sublistIterator = null;
            }
        }
        
        /**
         * Returns <tt>true</tt> if the iteration has more elements. (In other
         * words, returns <tt>true</tt> if <tt>next</tt> would return an element
         * rather than throwing an exception.)
         *
         * @return <tt>true</tt> if the iterator has more elements.
         */
        public boolean hasNext() {
            boolean more = false;
            
            if ( TargetIndex < TargetCount ) {
                more = true;
            }
            else if ( recursive ) {
                while ( true ) {
                    if ( sublistIterator != null ) {
                        // Check the iterator for elements
                        if ( sublistIterator.hasNext() ) {
                            more = true;
                            break;
                        }
                        else {
                            // This one is all tapped out...
                            sublistIterator = null;
                            sublistIndex ++;
                        }
                    }
                    else if ( sublistIndex < sublistCount ) {
                        // Grab a new sublistIterator
                        TargetList sublist = TargetList.getSublist(sublistIndex);
                        sublistIterator = sublist.getTargets(true);
                    }
                    else {
                        // The sublists are done and the Target is still null...
                        break;
                    }
                }
            }
            
            return more;
        }
        
        /**
         * Returns the next element in the interation.
         *
         * @return the next element in the iteration.
         * @exception NoSuchElementException iteration has no more elements.
         */
        public Object next() {
            Target nextTarget = null;
            
            if ( TargetIndex < TargetCount ) {
                nextTarget = TargetList.getTarget(TargetIndex);
                TargetIndex ++;
            }
            else if ( recursive ) {
                while ( nextTarget == null ) {
                    if ( sublistIterator != null ) {
                        // Check the iterator for elements
                        if ( sublistIterator.hasNext() ) {
                            nextTarget = (Target)sublistIterator.next();
                        }
                        else {
                            // This one is all tapped out...
                            sublistIterator = null;
                            sublistIndex ++;
                        }
                    }
                    else if ( sublistIndex < sublistCount ) {
                        // Grab a new sublistIterator
                        TargetList sublist = TargetList.getSublist(sublistIndex);
                        sublistIterator = sublist.getTargets(true);
                    }
                    else {
                        // The sublists are done and the Target is still null...
                        throw new NoSuchElementException();
                    }
                }
            }
            else {
                throw new NoSuchElementException();
            }
            
            return nextTarget;
            
        }
        
        /**
         *
         * Removes from the underlying collection the last element returned by the
         * iterator (optional operation).  This method can be called only once per
         * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
         * the underlying collection is modified while the iteration is in
         * progress in any way other than by calling this method.
         *
         * @exception UnsupportedOperationException if the <tt>remove</tt>
         * 		  operation is not supported by this Iterator.
         *
         * @exception IllegalStateException if the <tt>next</tt> method has not
         * 		  yet been called, or the <tt>remove</tt> method has already
         * 		  been called after the last call to the <tt>next</tt>
         * 		  method.
         */
        public void remove() {
            // First check to see if this a sublist member...
            if ( sublistIterator != null ) {
                sublistIterator.remove();
            }
            else {
                // This was supposed to be one of our...so remove it.  Remember, you have to
                // remove the (TargetIndex - 1) since index was already increased.
                Target Target = TargetList.getTarget(TargetIndex - 1);
                TargetList.removeTarget(Target);
                
                // Now correct the counters...
                TargetIndex -= 1;
                TargetCount -= 1;
            }
        }
        
    }
    
}
