/*
 * DefaultAbilityList.java
 *
 * Created on February 8, 2002, 2:46 PM
 */

package champions;

import champions.exceptionWizard.ExceptionWizard;
import champions.interfaces.AbilityIterator;
import champions.interfaces.AbilityList;
import champions.interfaces.Framework;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
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
public class DefaultAbilityList // extends DetailList
implements AbilityList, Serializable {
    static final long serialVersionUID = 8050853606652523L;
    
    transient private EventListenerList eventListenerList = new EventListenerList();
    
    protected String name;
    
    protected Target source;
    protected AbilityList parent;
    
    protected List<AbilityList> sublists = new ArrayList<AbilityList>();
    protected List<Ability> abilities = new ArrayList<Ability>();
    
    protected Framework framework;
    
    /** Creates new DefaultAbilityList */
    public DefaultAbilityList() {
        //setFireChangeByDefault(false);
    }
    
    public DefaultAbilityList(String name) {
        this();
        setName(name);
    }
    
    /** Returns the Target which owns this list.
     */
    public Target getSource() {
        //return (Target)getValue("AbilityList.TARGET");
        return source;
    }
    
    /** Sets the Parent list of this list.
     *
     * This method will set the parent list of this list.  This method does not
     * check to see that the list/sublist structure is correct and should not be
     * called directly.  Instead addSublist/removeSublist should be called.
     */
    public void setAbilityList(AbilityList parent) {
        //add("AbilityList.PARENT", parent, true);
        this.parent = parent;
    }
    
    /** Returns the Parent list of this list.
     */
    public AbilityList getAbilityList() {
        //return (AbilityList)getValue("AbilityList.PARENT");
        return parent;
    }
    
    /** Returns the count of the child sublists of this list.
     */
    public int getSublistCount() {
        //return getIndexedSize( "AbilityList" );
        return sublists.size();
    }
    
    /** Returns an AbilityIterator which iterates through all the abilities in the list.
     *
     * This version of getAbilities should recursively iterator through all of the abilities
     * contained in this list.  Calling this method is equivalent to calling
     * getAbilities(true);
     */
    public AbilityIterator getAbilities() {
        return getAbilities(true);
    }
    
    /** Returns an AbilityIterator which iterates through the abilities in the list.
     *
     * This version of getAbilities will iterate through the abilities in the list.
     * If the recursive flag is true, it will recursively iterator through it's sublists
     * also.
     */
    public AbilityIterator getAbilities(boolean recursive) {
        return new DefaultAbilityList.MyAbilityIterator(this, recursive);
    }
    
        /** Returns an AbilityIterator which iterates through all the skills in the list.
     *
     * This version of getSkills should recursively iterator through all of the skills
     * contained in this list.  Calling this method is equivalent to calling
     * getAbilities(true);
     *
     */
    public AbilityIterator getSkills() {
        return getSkills(true);
    }    
    
    /** Returns an AbilityIterator which iterates through the skills in the list.
     *
     * This version of getSkills will iterate through the skills in the list.
     * If the recursive flag is true, it will recursively iterator through it's sublists
     * also.
     *
     */
    public AbilityIterator getSkills(boolean recursive) {
        return new DefaultAbilityList.MySkillIterator(this, true);
    }
    
    
    /** Returns the child sublist indicated by specified index.
     */
    public AbilityList getSublist(int index) {
        //return (AbilityList)getIndexedValue(index, "AbilityList", "ABILITYLIST");
    	if(sublists.size() > index) {
    		return sublists.get(index);
    	}
    	return null;
    }
    
    /** Clones the list.
     *
     * This method will create a recursive clone of the list.  However, the cloned copy will have
     * it's target set to null and will no longer be a child of it's current parent.
     */
    public AbilityList cloneList() throws CloneNotSupportedException {
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
    public void addSublist(AbilityList sublist) {
        AbilityList oldParent = sublist.getAbilityList();
        
        AbilityList p = this;
        while ( p != null ) {
            if ( p == sublist ) {
                ExceptionWizard.postException(new Exception("Adding Sublist would cause loop."));
                return;
            }
            p = p.getAbilityList();
        }
        
        if ( oldParent != this ) {
            if ( oldParent != null ) {
                // This sublist's current parent is non-null, so remove it from that sublist.
                oldParent.removeSublist(sublist, false);
            }
            
            Target oldTarget = sublist.getSource();
            
            //int index = createIndexed( "AbilityList", "ABILITYLIST", sublist);
            sublists.add(sublist);
            
            sublist.setAbilityList(this);
            
            if ( oldTarget != getSource() ) {
                sublist.setSource( getSource() );
            }
            
            fireChangeEvent( "Sublist Added" );
            abilityModificationInSublist("Sublist Structure Changed");
        }
    }
    
    /** Removed the specified sublist from this list.
     *
     * This method removed the specified sublist from the list.  The sublists
     * target will be set to null and the sublists parent will be set to null.
     *
     * If the sublist is not a direct child of this list, nothing will be done.
     */
    public void removeSublist(AbilityList sublist) {
        removeSublist(sublist, true);
    }
    
    /** Removed the specified sublist from this list.
     *
     * This method removed the specified sublist from the list.  The sublists
     * target will be set to null and the sublists parent will be set to null.
     *
     * If the sublist is not a direct child of this list, nothing will be done.
     */
    public void removeSublist(AbilityList sublist, boolean clearAbilitySource) {
        /*int index = findExactIndexed("AbilityList", "ABILITYLIST", sublist);
        if ( index != -1 ) {
            if ( clearAbilitySource ) sublist.setSource(null);
            
            removeAllIndexed(index, "AbilityList");
            sublist.setAbilityList(null);
            
            fireChangeEvent( "Sublist Removed" );
        }*/
        
        if ( sublists.remove(sublist) ) {
            if ( clearAbilitySource ) sublist.setSource(null);
            
            //removeAllIndexed(index, "AbilityList");
            sublist.setAbilityList(null);
            
            fireChangeEvent( "Sublist Removed" );
        }
        
    }
    
    /** Returns an array of the child sublists of this list.
     */
    public AbilityList[] getSublists() {
        /*int index, count;
        
        count = getIndexedSize("AbilityList");
        AbilityList[] al = new AbilityList[count];
        
        for(index=0; index<count;index++) {
            al[index] = getSublist(index);
        }*/
        
        return sublists.toArray(new AbilityList[0]);
    }
    
    /** Sets the Target which owns this list.
     *
     * This method will set the target which owns this list.  All of the abilities in the
     * list will be added to the target via the Target.addAbility method.  All child sublists
     * will have their setTarget methods called also.
     */
    public void setSource(Target target) {
        Ability ability;
        Target oldTarget = getSource();
        
        if ( target != oldTarget ) {
            if ( oldTarget != null ) {
                AbilityIterator ai = getAbilities();
                while ( ai.hasNext() ) {
                    ability = ai.nextAbility();
                    oldTarget.detachAbilityFromTarget( ability );
                }
            }
            
            // setTarget(target);
            //add( "AbilityList.TARGET", target, true);
            this.source = target;
            
            if ( target != null ) {
                AbilityIterator ai = getAbilities();
                while ( ai.hasNext() ) {
                    ability = ai.nextAbility();
                    target.attachAbilityToTarget( ability );
                }
            }
            
            // Always update the sublists
            int count, index;
            AbilityList sublist;
            count = getSublistCount();
            for(index = 0; index < count; index++) {
                sublist = getSublist(index);
                sublist.setSource(target);
            }
        }
        
    }
    
    /** Returns the number of abilities contained in the list.
     *
     * This method returns the number of abilities directly contained in this sublist.  It does
     * not return the count from any sublists.
     */
    public int getAbilityCount() {
        //return getIndexedSize("Ability");
        return abilities.size();
    }
    
    /** Returns the Index of the Given ability.
     *
     * The index indicates the location of the ability in the list, starting with location 0.
     * 
     * Note, this relies on == not equals to match abilities, so only the 
     * exact ability is found.
     */
    public int getAbilityIndex(Ability ability) {
        //return findExactIndexed("Ability", "ABILITY", ability);
        for(int i = 0; i < abilities.size(); i++) {
            if ( abilities.get(i) == ability ) {
                return i;
            }
        }
        return -1;
    }
    
    /** Returns the indicated ability contained in this list.
     *
     * This method returns the ability at indicated index contained in this list.  It does
     * not return any abilities from sublists.
     */
    public Ability getAbility(int aindex) {
        //return (Ability)getIndexedValue(aindex, "Ability", "ABILITY");
        return abilities.get(aindex);
    }
    
//    /** Returns whether a given ability is current enabled.
//     *
//     * This method is called by Ability.isEnabled to determine if the ability is currently
//     * enabled.  This can be used by sublists to control abilities for special list types such
//     * as Multipower and Variable Point Pool.
//     *
//     * If the sublist does not care to modify whether an ability is enabled, it should return true.
//     */
//    public boolean isEnabled(Ability ability, Target target) {
//        return true;
//    }
    
//    /** Returns the CP Cost of the Ability.
//     *
//     * This method is used to determine the cost of the Ability belonging to a list.  In general,
//     * the standard formula of Base * ( 1 + adv ) / (1 + lim ) should be used.  However, in some
//     * cases, the cost is overriden due to special calculations, such as Multipower and Variable Point pool.
//     *
//     * This method is used to calculate the cost of both the CP Cost and the Adjusted CP Cost, so the ability
//     * cost should be based on only the powerCost, advantage, and limitation parameters provided.
//     *
//     * This method will only be called if the useCustomCost() method returns true.
//     */
//    public int calculateCost(Ability ability, int powerCost, double advantage, double limitation) {
//        return 0;
//    }
    
//    /** Return whether the calculateCost method should be used to calculate special cost for the ability.
//     */
//    public boolean useCustomCost() {
//        return false;
//    }
    
    /** Returns the Name of the list.
     *
     * Returns the list's name.
     */
    public String getName() {
        //return getStringValue("AbilityList.NAME");
        return name;
    }
    
    /** Set the Name of the list.
     *
     */
    public void setName(String name) {
        //add("AbilityList.NAME", name, true);
        this.name = name;
    }
    
    /** Adds the Ability to the List.
     *
     * This method will add the ability to the list.  The abilities target and sublist
     * parameters will be updated appropriately.
     *
     * This method should also work with base instances. 
     */
    public void addAbility(Ability ability, int insertIndex) {
        //int index = findExactIndexed("Ability", "ABILITY", ability);
//        if ( insertIndex > abilities.size() ) {
//            insertIndex = abilities.size();
//        }
        
        int index = getAbilityIndex(ability);
        if ( index == -1 ) {
            Target oldSource = null;
            if ( ability.isAutoSource() == false ) oldSource = ability.getSource();
            
            if ( oldSource != null && oldSource != getSource() ) {
                oldSource.detachAbilityFromTarget(ability);
            }
            
            // Remove the Ability from the old list
            if ( ability.getAbilityList() != null ) {
                ability.getAbilityList().removeAbility(ability, false);
            }
            
            //index = createIndexed(insertIndex, "Ability", "ABILITY", ability );
            try {
        		abilities.add(insertIndex, ability);
        	}catch(Exception e)
        	{
        		abilities.add( ability);
        	}
            
            //if ( ability.getAbilityList() != this ) {
            ability.setAbilityList(this);
            //}
            
            if ( !(ability instanceof FrameworkAbility ) ) {
                ability.setFramework( getFramework() );
            }
            
            if ( getSource() != null && oldSource != getSource() ) {
                getSource().attachAbilityToTarget(ability);
            }
            
            fireChangeEvent( "Ability Added" );
            abilityModificationInSublist("Ability Added");
        }
        else if ( index != insertIndex  ) {
            //moveIndexed(index, insertIndex, "Ability", false);
            abilities.remove(index);
            if ( insertIndex <= index+1 ) {
            	try {
            		abilities.add(insertIndex, ability);
            	}catch(IndexOutOfBoundsException e)
            	{
            		abilities.add( ability);
            	}
            }
            else {
                // Shift by one, since we removed one before insert index
                abilities.add(insertIndex-1, ability);
            }
            fireChangeEvent( "Ability Moved" );
            abilityModificationInSublist("Ability Moved");
        }
    }
    
    /** Adds the Ability to the List.
     *
     * This method will add the ability to the list.  The abilities target and sublist
     * parameters will be updated appropriately.
     */
    public void addAbility(Ability ability) {
        addAbility(ability, getAbilityCount());
    }
    
        /** Removes the Ability from this list.
     *
     * This method will remove the ability from this list.  The abilities target and sublist
     * parameters will be set to null.
     */
    public void removeAbility(Ability ability) {
        removeAbility(ability, true);
    }
    /** Removes the Ability from this list.
     *
     * This method will remove the ability from this list.  The abilities target and sublist
     * parameters will be set to null.
     */
    public void removeAbility(Ability ability, boolean clearAbilitySource) {
        //int index = findExactIndexed("Ability", "ABILITY", ability);
        if ( abilities.remove(ability) ) {
            
            if ( getSource() != null && clearAbilitySource ) {
                getSource().detachAbilityFromTarget(ability);
            }
            
            if ( ability.getAbilityList() == this ) {
                ability.setAbilityList(null);
            }
            
            //removeAllIndexed(index, "Ability");
            
            fireChangeEvent( "Ability Removed" );
            abilityModificationInSublist("Ability Removed");
        }
    }
    
    /** Returns the sublist with the indicated name.
     *
     * If this list's name is equals to the indicated one, it returns this.  Otherwise, it searchs
     * recursively through sublists to find the name.
     *
     * Returns null if the name is not found.
     */
    public AbilityList findSublist(String sublistName) {
        AbilityList abilityList = null;
        
        if ( getName() == sublistName || (getName() != null && getName().equals(sublistName)) ) {
            abilityList = this;
        }
        else {
            int index, count;
            count = getSublistCount();
            for(index = 0; index<count; index++) {
                abilityList = getSublist(index).findSublist(sublistName);
                if ( abilityList != null ) {
                    break;
                }
            }
        }
        
        return abilityList;
    }
    
    /** Set the Index of the given ability.
     *
     * This method sets the index of the given ability, changing the order of the abilities in
     * the list.
     *
     */
    public void setAbilityIndex(Ability ability, int newIndex) {
        int oldIndex = getAbilityIndex(ability);
        
        if ( oldIndex != -1 && newIndex != -1 && oldIndex != newIndex) {
            //moveIndexed(newIndex, oldIndex, "Ability", true);
            
            abilities.remove(oldIndex);
            if ( newIndex <= oldIndex+1 ) {
                abilities.add(newIndex, ability);
            }
            else {
                // Shift by one, since we removed one before insert index
                abilities.add(newIndex-1, ability);
            }
            
            fireChangeEvent( "Ability Moved" );
        }
    }
    
    /** Adds a ChangeListener for the List.
     *
     * stateChange events are fired in the following circumstances:
     * When an ability is added directly to this list.
     * When an ability is remove directly from this list.
     * When a sublist is added directly to this list.
     * When a sublist is removed directly from this list.
     * When this is the top most list and an ability is added to any child sublist.
     */
    public void addChangeListener(ChangeListener listener) {
        eventListenerList.add(ChangeListener.class, listener);
    }
    
    /** Removes a ChangeListener for the List.
     */
    public void removeChangeListener(ChangeListener listener) {
        eventListenerList.remove(ChangeListener.class, listener);
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
        String s = "DefaultAbilityList: ";
        
        s = s + getName();
        
        s = s + "[";
        
        int index, count;
        count = getAbilityCount();
        if ( count == 0 ) {
            s = s + "No Abilities";
        }
        else {
            s = s + "Abilities: ";
            
            for (index = 0;index < count; index++) {
                s = s + getAbility(index).getName() ;
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
    
    /** Causes the List to recursively notify it's parent that an ability has been added.
     */
    public void abilityModificationInSublist(String reason) {
        if ( getAbilityList() == null ) {
            // System.out.println("DefaultAbilityList.abilityModificationInSublist(): abilityModificationInSublist fired from Root Parent");
            fireChangeEvent( "Modification to Sublist");
        }
        else {
            getAbilityList().abilityModificationInSublist(reason);
        }
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        
        // Create the proper listener list!
        eventListenerList = new EventListenerList();
    }
    
    /** Returns the Cost of all of the Abilities in the AbilityList, incluing sublists.
     */
    public int getRealCost() {
        int cost = 0;
        AbilityIterator ai = getAbilities(false);
        Ability ability;
        while (ai.hasNext()) {
            ability = ai.nextAbility();
            cost += ability.getRealCost();
        }
        
        int index = getSublistCount() -1;
        for( ;index >= 0; index--) {
            cost += getSublist(index).getRealCost();
        }
        
        return cost;
    }
    
    /** Returns the ability of the indicated name, if it exists.
     *
     * Return null if the ability does not exist.
     *
     */
    public Ability getAbility(String name, boolean recurse) {
        AbilityIterator ai = getAbilities(recurse);
        Ability ability;
        while (ai.hasNext()) {
            ability = ai.nextAbility();
            if ( ability.getName().equals(name) ) {
                return ability;
            }
        }
        
        return null;
    }    

    /** Returns whether the abilityList contains the indicated Ability.
     *
     *
     */
    public boolean hasAbility(Ability ability, boolean recursive) {
        AbilityIterator ai = getAbilities(recursive);
        Ability ability2;
        while (ai.hasNext()) {
            ability2 = ai.nextAbility();
            if ( ability2.equals(ability) ) {
                return true;
            }
        }
        
        return false;
    }    
    
    public Framework getFramework() {
        //return (Framework)getValue("AbilityList.FRAMEWORK");
        return framework;
    }
    
    public void setFramework(Framework framework) {
        if ( getFramework() == null ) {
            //add("AbilityList.FRAMEWORK", framework, true);
            this.framework = framework;
            
            updateAbilitiesInFramework();
            
            // Set the sublists up too...
            int count = getSublistCount();
            for(int i = 0; i < count; i++) {
                AbilityList sublist = getSublist(i);
                sublist.setFramework(framework);
            }
        }
    }
    
    /** Non-recursively sets the framework of all abilities in sublist. */
    protected void updateAbilitiesInFramework() {
        Framework fw = getFramework();
        AbilityIterator ai = getAbilities(false);
        while ( ai.hasNext() ) {
            ai.nextAbility().setFramework( fw );
        }
    }
    
    public boolean isFrameworkSet() {
        if ( getFramework() != null ) return true;
        
        int count = getSublistCount();
        for(int i = 0; i < count; i++) {
            AbilityList sublist = getSublist(i);
            if ( sublist.isFrameworkSet() ) return true;
        }
        return false;
    }
    
    private class MyAbilityIterator implements AbilityIterator {
        private DefaultAbilityList abilityList;
        private boolean recursive;
        
        private int abilityCount;
        private int abilityIndex;
        
        private int sublistCount;
        private int sublistIndex;
        
        private AbilityIterator sublistIterator;
        
        
        public MyAbilityIterator(DefaultAbilityList dal, boolean recursive) {
            abilityList = dal;
            this.recursive = recursive;
            
            initializeIterator();
        }
        
        private void initializeIterator() {
            abilityCount = abilityList.getAbilityCount();
            abilityIndex = 0;
            
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
            
            if ( abilityIndex < abilityCount ) {
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
                        
                        AbilityList sublist = abilityList.getSublist(sublistIndex);
                        sublistIterator = sublist.getAbilities(true);
                    }
                    else {
                        // The sublists are done and the ability is still null...
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
        public Ability next() {
            return nextAbility();
        }
        
        /** Returns the next Ability.
         */
        public Ability nextAbility() {
            Ability nextAbility = null;
            
            if ( abilityIndex < abilityCount ) {
                nextAbility = abilityList.getAbility(abilityIndex);
                abilityIndex ++;
            }
            else if ( recursive ) {
                while ( nextAbility == null ) {
                    if ( sublistIterator != null ) {
                        // Check the iterator for elements
                        if ( sublistIterator.hasNext() ) {
                            nextAbility = sublistIterator.nextAbility();
                        }
                        else {
                            // This one is all tapped out...
                            sublistIterator = null;
                            sublistIndex ++;
                        }
                    }
                    else if ( sublistIndex < sublistCount ) {
                        // Grab a new sublistIterator
                        AbilityList sublist = abilityList.getSublist(sublistIndex);
                        sublistIterator = sublist.getAbilities(true);
                    }
                    else {
                        // The sublists are done and the ability is still null...
                        throw new NoSuchElementException();
                    }
                }
            }
            else {
                throw new NoSuchElementException();
            }
            
            return nextAbility;
            
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
                // remove the (abilityIndex - 1) since index was already increased.
                Ability ability = abilityList.getAbility(abilityIndex - 1);
                abilityList.removeAbility(ability);
                
                // Now correct the counters...
                abilityIndex -= 1;
                abilityCount -= 1;
            }
        }
        
    }
    
    private class MySkillIterator implements AbilityIterator {
        private DefaultAbilityList abilityList;
        private boolean recursive;
          
        private AbilityIterator abilityIterator;
        
        private Ability next;
        
        
        public MySkillIterator(DefaultAbilityList dal, boolean recursive) {
            abilityList = dal;
            this.recursive = recursive;
            
            initializeIterator();
        }
        
        private void initializeIterator() {
            abilityIterator = abilityList.getAbilities(recursive);
        }
        
        /**
         * Returns <tt>true</tt> if the iteration has more elements. (In other
         * words, returns <tt>true</tt> if <tt>next</tt> would return an element
         * rather than throwing an exception.)
         *
         * @return <tt>true</tt> if the iterator has more elements.
         */
        public boolean hasNext() {
            if ( next != null ) return true;
            
            findNextAbility();
            
            return (next != null);
        }
        
        /**
         * Returns the next element in the interation.
         *
         * @return the next element in the iteration.
         * @exception NoSuchElementException iteration has no more elements.
         */
        public Ability next() {
            return nextAbility();
        }
        
        /** Returns the next Ability.
         */
        public Ability nextAbility() {
            Ability a;
            if ( next == null ) {
                findNextAbility();
                
            }
            a = next;
            next = null;
            
            return a;
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
            throw new UnsupportedOperationException();
        }
        
        private void findNextAbility() {
            while ( abilityIterator.hasNext() ) {
                Ability a = abilityIterator.nextAbility();
                if ( a.isSkill() || a.isDisadvantage() ) {
                    next = a;
                    break;
                } 
            }
        }
        
    }
    
}
