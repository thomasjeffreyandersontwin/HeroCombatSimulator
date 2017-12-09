/*
 * AbilityInstanceGroup.java
 *
 * Created on June 1, 2004, 10:13 PM
 */

package champions;

import champions.event.AbilityAddedEvent;
import champions.event.AbilityRemovedEvent;
import champions.event.ActivationStateChangeEvent;
import champions.event.InstanceChangedEvent;
import champions.exception.BattleEventException;
import champions.exceptionWizard.ExceptionWizard;
import champions.interfaces.AbilityInstanceGroupListener;
import champions.interfaces.Debuggable;
import champions.interfaces.Framework;
import java.awt.BorderLayout;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.event.EventListenerList;

/**
 *
 * @author  1425
 */
public class AbilityInstanceGroup
implements Debuggable, Serializable {
    static final long serialVersionUID = -5436079283350725953L;
    
    /** The Base Instance.
     *
     * The Base instance hold the basic, underlying configuration for the power.
     * This is the power as it would be configured in a character editor in its
     * pristine form.
     *
     * This instance can be edited, but editing it is equivilant to changing the
     * underlying character.  During the course of a simulation, HCS will never
     * edit the underlying configuration of this ability (unless the GM does it
     * via the character/ability editor).
     *
     * Once set, the base instance of the instance should not be modified or 
     * removed.  The only case where the baseInstance can be removed is when
     * there are no other instances in the instance group (and removing the 
     * base destroys the group).
     */
    protected Ability baseInstance;
    
    /** The current instance.
     *
     * The current instance hold the instance of the power that will be activated
     * by default when an activation occurs.  The GUI can override this and launch
     * a different instance of the ability when requested.  The GUI can also set
     * the current instance at will to represent different configurations of the
     * ability.<P>
     *
     * The current instance be equal to the frameworkInstance, the adjusted instance 
     * or the baseInstance.  However, since the framework and base are static from 
     * the perspective of the battle engine, when an activation occurs a child
     * instance of the current currentInstance will be created and the 
     * currentInstance will be update to that new instance.<P>  
     *
     * Typically, the currentInstance should be either the frameworkInstance,
     * the adjusted instance, or it should be a child of the adjusted instance
     * (which will either be equal to or a child of the framework instance).
     */
    protected Ability currentInstance;
    
    /** The Framework instance.
     *
     * The framework instance hold the current base framework instance, as
     * allocated by any framework which governs this ability.  The framework
     * is considered static by the BattleEngine and will never be modified.<P>
     *
     * The framework instance should typically be either the same as the 
     * baseInstance (in cases where no framework governs the ability) or it 
     * should be a direct child of the base instance.
     */
    protected Ability frameworkInstance;
    
    /** The adjusted instance.
     *
     * The adjusted instance holds the instance of the ability that is adjusted
     * by adjustment powers.  If no adjustments have occurred, it will usually
     * be equal to the can be equal to the framework instances (which is often,
     * in turn equal to the base).
     *
     * When an adjustment occurs, a child instance of the framework will be 
     * created.  Any children of the adjusted instance will have custodianship
     * transferred to the adjusted instance.
     *
     * Once the adjustment fades, the adjustment instance can be destroyed.
     */
    protected Ability adjustedInstance;
    
    /** Holds the Framework that this instance group belongs too.
     *
     */
    protected Framework framework;
    
    /** Sets of all the instances in the group. */
    protected Set<AbilityEntry> instanceSet = new LinkedHashSet<AbilityEntry>();
    
    /** AbilityInstanceGroupListener list */
    protected transient EventListenerList listenerList = null;
    
    /** Stores GUI preference for showing templates. */
    protected boolean showTemplates = false;
    
    protected List<AbilityActivationEntry> activations; 
    
    /** Creates a new instance of AbilityInstanceGroup */
    public AbilityInstanceGroup() {
        
    }
    
    public AbilityInstanceGroup(Ability baseInstance) {
        setBaseInstance(baseInstance);
    }
    
    public void addAbilityInstanceGroupListener(AbilityInstanceGroupListener listener) {
        if ( listenerList == null ) listenerList = new EventListenerList();
        listenerList.add(AbilityInstanceGroupListener.class, listener);
    }
    
    public void removeAbilityInstanceGroupListener(AbilityInstanceGroupListener listener) {
        if ( listenerList != null ) {
            listenerList.remove(AbilityInstanceGroupListener.class, listener);
        }
    }
 /** Returns whether this is the Base Ability of an Ability Instance Group.
     * @return True if Ability is Base Instance
     */
    public boolean isBaseInstance(Ability ability) {
        return ability == baseInstance;
    }
    
    /** Returns whether this Ability is the Currently Active Instance of an Ability
     * Instance Group.
     * @return True if Ability is Currently Active Instance
     */
    public boolean isCurrentInstance(Ability ability) {
        return ability == currentInstance;
    }
    
    /** Returns whether this Ability is the Currently Active Instance of an Ability
     * Instance Group.
     * @return True if Ability is Currently Active Instance
     */
    public boolean isFrameworkInstance(Ability ability) {
        return ability == frameworkInstance;
    }
    
    /** Returns whether this Ability is the Currently Active Instance of an Ability
     * Instance Group.
     * @return True if Ability is Currently Active Instance
     */
    public boolean isAdjustedInstance(Ability ability) {
        return ability == adjustedInstance;
    }
    
    /** Returns whether <CODE>anotherAbility</CODE> belongs to this Abilities
     * Instance Group.
     * @return True if anotherAbility is member of this Ability
     * Instance Group.
     * @param anotherAbility Ability which is being checked for instance group
     * membership.
     */
    public boolean isInstance(Ability ability) {
        //return instanceSet.contains(ability);
        for(AbilityEntry ae : instanceSet) { 
            if ( ae.ability == ability ) return true;
        }
        return false;
    }
    
    /** Returns the Base Ability Instance of the Ability Instance Group this ability
     * belongs to.
     * @return Base Ability of Instance Group.
     */
    public Ability getBaseInstance() {
        return baseInstance;
    }
    
    /** Returns the Current Ability Instance of the Ability Instance Group this ability
     * belongs to.
     * @return Base Ability of Instance Group.
     */
    public Ability getCurrentInstance() {
        return currentInstance;
    }
    
    /** Returns an iterator over the Ability instance in the group.
     *
     */
    public Iterator<Ability> getInstances() {
        return new InstanceIterator(instanceSet.iterator());
    }
    /** Return the number of instances of this ability, including the base.
     *
     */
//    public int getInstanceCount() {
//        return instanceSet.size();
//    }
    
    /** Returns the <CODE>instanceNumber</CODE> ability of this Ability's Ability
     * Instance Group.
     * @param instanceNumber Index of the instance to be returned.
     * @return Ability which is at index location <CODE>instanceNumber</CODE>.
     * NULL if <CODE>instanceNumber</CODE> is out of range.
     */
//    public Ability getInstance(int instanceNumber) {
//        return (Ability) instanceSet.get(instanceNumber);
//    }
    
    /** Creates a new Instance based upon the appropriate instance.
     *
     * Typically, this should create an instance based upon the framework
     * base instance.  However, in some cases it might make sense to base it
     * upon some other instance.  This usually occurs when the ability is 
     * drained or aided.
     */
    public Ability createNewInstance(boolean variation) {
        Ability newAbility = getAdjustedInstance().createChildInstance();
        // This is actually threading problem...
        // The GUI thread might rebuild ability trees prior to
        // the new ability being marked as a variation....
        if ( variation == true ) {
            setVariationInstance(newAbility, variation);
            // Also recalculate the modifiers....
            newAbility.calculateMultiplier();
        }
        
        return newAbility;
    }
    
    /** Creates a new Instance based upon the appropriate instance.
     *
     * Typically, this should create an instance based upon the framework
     * base instance.  However, in some cases it might make sense to base it
     * upon some other instance.  This usually occurs when the ability is 
     * drained or aided.
     */
    public Ability createNewInstance() {
        return createNewInstance(false);
    }
    
    /** Creates a new Ability based on the Base Ability of this ability's Ability
     * Instance Group.  All Instance Group Information is updated appropriately.
     * @return New Ability instance based on Base Ability Instance.
     */
    public Ability createNewInstanceFromBase() {
        Ability newAbility = getBaseInstance().createChildInstance();
        return newAbility;
    }
    
    /** Creates a new Ability based on the Framework Ability of this ability's Ability
     * Instance Group.  All Instance Group Information is updated appropriately.
     * @return New Ability instance based on Framework Ability Instance.
     */
    public Ability createNewInstanceFromFramework() {
        Ability newAbility = getFrameworkInstance().createChildInstance();
        return newAbility;
    }
    
    /** Creates a new Ability based on the Current Ability of this ability's Ability
     * Instance Group.  All Instance Group Information is updated appropriately.
     * @return New Ability instance based on Base Ability Instance.
     */
    public Ability createNewInstanceFromCurrent() {
        Ability newAbility = getCurrentInstance().createChildInstance();
        return newAbility;
    }
    
    /** Sets the Current Ability of this Ability's Ability Instance Group.
     * This does not have to be the base ability of the Abiltiy Instance Group.
     * All Instance information contained in the base ability will be updated
     * appropriately.
     * @param ability Ability which is now the new Current Ability.
     * <CODE>ability</CODE> must be an instance member of
     * this Ability's Ability Instance Group.  It can be the
     * Base ability.
     */
    public void setCurrentInstance(Ability ability) {
        if ( currentInstance != ability ) {
            if ( ability == null ) ability = adjustedInstance;
            Ability old = currentInstance;
            currentInstance = ability;
            fireInstanceChangedEvent(InstanceChangedEvent.CURRENT_INSTANCE, old, ability);
        }
    }
    
    /** Splits an Ability from it's current Ability Instance Group creating a new group with this ability as base.
     * A new Ability Instance Group will be created and this ability will be set
     * to be the base and current instance of the new Ability Instance Group.
     * The Ability is checked to make sure it is not currently a member of an instance Group.
     * If the Ability is a member of a current group, it will be removed from it's current group.
     */
    public void removeInstance(Ability ability) {
        if ( ability == baseInstance ) {
            // This should only happen when there is only one instance in the instance group
            if ( instanceSet.size() > 1 ) {
                ExceptionWizard.postException( new Exception( "WARNING: Base Ability Instance Split from Group.\n" + this ));
            }
            // This will only be done if the base instance is being moved to
            // a different instance group...
            baseInstance = null;
            currentInstance = null;
            adjustedInstance = null;
            frameworkInstance = null;
            
            
            Iterator<AbilityEntry> it = instanceSet.iterator();
            while(it.hasNext()) {
                if ( it.next().ability == ability ) {
                    it.remove();
                    break;
                }
            }
            
            fireInstanceChangedEvent( InstanceChangedEvent.ALL_INSTANCES, ability, null);
            fireAbilityRemovedEvent(ability);
            
        }
        else {
            if ( ability == currentInstance ) {
                if ( ability != adjustedInstance ) {
                    setCurrentInstance(adjustedInstance);
                }
                else if ( ability != frameworkInstance ) {
                    setCurrentInstance(frameworkInstance);
                }
                else {
                    setCurrentInstance( baseInstance );
                }
            }
            
            
            Iterator<AbilityEntry> it = instanceSet.iterator();
            while(it.hasNext()) {
                if ( it.next().ability == ability ) {
                    it.remove();
                    break;
                }
            }
            
            if ( ability.getParentAbility() != null ) {
                // Sever the connection to the parent...
                ability.setParentAbility(null);
            }
            
            fireAbilityRemovedEvent(ability);
        }
    }
    
     
    /** Adds <CODE>ability</CODE> to this Ability's Ability Instance Group.
     * @param ability
     */
    public void addInstance(Ability ability) {
        
        if ( findAbilityEntry(ability) == null ) {
        
            instanceSet.add(new AbilityEntry(ability));
            fireAbilityAddedEvent(ability);
            if ( baseInstance == null ) {
                setBaseInstance(ability);
            }
        }
    }
    
    private AbilityEntry findAbilityEntry(Ability ability) {
        for(AbilityEntry ae : instanceSet) {
            if ( ae.ability == ability ) {
                return ae;
            }
        }
        return null;
    }

    
    /**
     * Setter for property baseInstance.
     * @param baseInstance New value of property baseInstance.
     */
    protected void setBaseInstance(Ability baseInstance) {
        if ( this.baseInstance != null ) {
            throw new IllegalArgumentException("BaseInstance can't be set again once it has been set originally.");
        }
        else if ( baseInstance == null ) {
              throw new IllegalArgumentException("BaseInstance can't be set to null (unless destroying abilityInstanceGroup.");
        }
        //this.instanceSet.add(new AbilityEntry(baseInstance));
        //fireAbilityAddedEvent(baseInstance);
        addInstance(baseInstance);
        this.baseInstance = baseInstance;
        this.currentInstance = baseInstance;
        this.frameworkInstance = baseInstance;
        this.adjustedInstance = baseInstance;
        
        fireInstanceChangedEvent(InstanceChangedEvent.ALL_INSTANCES, null, baseInstance);
    }
    
    public void displayDebugWindow() {
        String windowName;
        if ( baseInstance != null ) {
            windowName = "AbilityInstanceGroup: " + baseInstance.getName()+ " @" + Integer.toHexString(hashCode());
        }
        else {
            windowName = "AbilityInstanceGroup: (No Base Instance Set)" + " @" + Integer.toHexString(hashCode());
        }
        JFrame f = new JFrame(windowName);
        ObjectDebugger dle = new ObjectDebugger();
        dle.setDebugObject(this);
        
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(dle);
        f.pack();
        f.setVisible(true);
    }
    
    /**
     * Getter for property frameworkInstance.
     * @return Value of property frameworkInstance.
     */
    public champions.Ability getFrameworkInstance() {
        return frameworkInstance;
    }    
    
    /**
     * Setter for property frameworkInstance.
     * @param frameworkInstance New value of property frameworkInstance.
     */
    public void setFrameworkInstance(champions.Ability frameworkInstance) {
        if ( frameworkInstance == null ) {
            // If the new frameworkInstance is null, replace with
            // the base instance
            frameworkInstance = baseInstance;
        }  

        if ( this.frameworkInstance != frameworkInstance ) {

            if ( this.frameworkInstance == adjustedInstance ) {
                setAdjustedInstance(frameworkInstance);
            }

            transferChildern(this.frameworkInstance, frameworkInstance);

            Ability old = this.frameworkInstance;
            this.frameworkInstance = frameworkInstance;
            
            fireInstanceChangedEvent(InstanceChangedEvent.FRAMEWORK_INSTANCE,old, frameworkInstance);
        }
    }   
    
    /** Returns the adjusted instance.
     *
     * This method does not necessarily return an ability that can be adjusted.
     * It just returns the current setting of the adjustedInstance (which is
     * typically the basis for current instances and all active instances.
     *
     * Use getEditableAdjustedInstance() to make sure that the adjusted 
     * instance is one that you will be able to modify.
     * Getter for property adjustedInstance.
     * @return Value of property adjustedInstance.
     */
    public champions.Ability getAdjustedInstance() {
        return adjustedInstance;
    }
    
    /** Returns the adjusted instance, creating one if the adjusted instance is modifiable.
     *
     * The method will retrieve the adjusted instance for the group.  If the 
     * adjusted instance isn't modifiable (it is either the base instance or
     * the framework instance), this method will create a new instance, transfer
     * parentship of all children appropriately.
     *
     * Whenever an ability is adjusted by an adjustment power, this is the
     * method to call to get the adjusted instance.
     */
    public Ability getModifiableAdjustedInstance() {
        if ( adjustedInstance != frameworkInstance ) {
            // This should be a valid check to see if 
            // the adjusted instance is editable...
            return adjustedInstance;
        }
        else {
            // Create a new adjusted instance and set it as the adjusted 
            // instance...
            Ability newAdjustedInstance = getFrameworkInstance().createChildInstance();
            setAdjustedInstance(newAdjustedInstance);
            return newAdjustedInstance;
        }
    }
    
    /** Sets the Adjusted Instance, transfering children appropriately.
     *
     * Passing null will remove the current adjusted instance (if it exist)
     * and set the adjustInstance to be the frameworkInstance.  This method
     * will destroy the old adjusted instance (since it assumed that only 
     * one adjusted instance will ever exist).
     *
     * Note, the ability passed into this method should be a child of the
     * framework ability.  This is not enforced, but bad things can happen if
     * it isn't.
     *
     * Setter for property adjustedInstance.
     * @param adjustedInstance New value of property adjustedInstance.
     */
    public void setAdjustedInstance(champions.Ability adjustedInstance) {
        // This doesn't really work correctly, since children need 
        // to be transferred around
        if ( adjustedInstance == null ) {
            if ( this.adjustedInstance != frameworkInstance ) {
                transferChildern(adjustedInstance, frameworkInstance);
            }
            
            Ability old = this.adjustedInstance;
            this.adjustedInstance = frameworkInstance;
            
            if ( old != frameworkInstance ) {
                old.removeInstanceFromInstanceGroup();
            }
        }
        else if ( this.adjustedInstance != adjustedInstance) {
            
            // Transfer all the children appropriately
            transferChildern(this.adjustedInstance,adjustedInstance);
            
            Ability old = this.adjustedInstance;
            this.adjustedInstance = adjustedInstance;
            
            fireInstanceChangedEvent(InstanceChangedEvent.ADJUSTED_INSTANCE, old, adjustedInstance);
            
            if ( old == currentInstance ) {
                setCurrentInstance(adjustedInstance);
            }
            
            if ( old != frameworkInstance ) {
                old.removeInstanceFromInstanceGroup();
            }
        }
    }
    
    private void transferChildern(Ability from, Ability to) {
        if ( from != to ) {
            for(int index = 0; index < from.getChildAbilityCount();) {
                Ability child = from.getChildAbility(index);
                if ( child == to ) {
                    index++;
                }
                else {
                    child.setParentAbility(to);
                }
            }
        }
    }
    
    /** Returns where this instance of the ability can be modified by the battleEngine.
     *
     * The base, framework, and adjusted instances are not considered modifiable.
     * Actually, the adjusted instance is modified by battle engine in cases
     * of adjustment effects, but it is not modified for any other reason.
     */
    public boolean isModifiableInstance(Ability ability) {
        return ability != baseInstance && ability != frameworkInstance && ability != adjustedInstance;
    }
    
    /** Tells the AbilityInstanceGroup that an instances activation state changed.
     *
     * This method is called by the member instances to notify the 
     * AbilityInstanceGroup that the member's activation state has changed.
     * This will in turn be passed onto the listeners of the AbilityInstanceGroup.
     */
    public void activationStateChanged(Ability ability, ActivationInfo ai, boolean oldState, boolean newState) {
        fireActivationStateChange(ability, ai, oldState, newState);
    }
    
    protected void fireAbilityAddedEvent(Ability ability) {
        if ( listenerList == null ) return;
        AbilityAddedEvent e = new AbilityAddedEvent(this, ability);
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==AbilityInstanceGroupListener.class) {
                // System.out.println( "Firing chance event to: " + listeners[i+1].toString());
                ((AbilityInstanceGroupListener)listeners[i+1]).abilityAdded(e);
            }
        }
    }
    
    protected void fireAbilityRemovedEvent(Ability ability) {
        if ( listenerList == null ) return;
        AbilityRemovedEvent e = new AbilityRemovedEvent(this, ability);
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==AbilityInstanceGroupListener.class) {
                // System.out.println( "Firing chance event to: " + listeners[i+1].toString());
                ((AbilityInstanceGroupListener)listeners[i+1]).abilityRemove(e);
            }
        }
    }
    
    protected void fireInstanceChangedEvent(int type, Ability oldAbility, Ability newAbility) {
        if ( listenerList == null || oldAbility == newAbility) return;
        InstanceChangedEvent e = new InstanceChangedEvent(this, type, oldAbility, newAbility);
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==AbilityInstanceGroupListener.class) {
                // System.out.println( "Firing chance event to: " + listeners[i+1].toString());
                ((AbilityInstanceGroupListener)listeners[i+1]).instanceChanged(e);
            }
        }
    }
    
    protected void fireActivationStateChange(Ability ability, ActivationInfo ai, boolean oldState, boolean newState) {
        if ( listenerList == null || oldState == newState ) return;
        ActivationStateChangeEvent e = new ActivationStateChangeEvent(this, ability, ai, newState);
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==AbilityInstanceGroupListener.class) {
                // System.out.println( "Firing chance event to: " + listeners[i+1].toString());
                ((AbilityInstanceGroupListener)listeners[i+1]).activationStateChanged(e);
            }
        }
    }
    
    public void setShowTemplates(boolean showTemplates) {
        this.showTemplates = showTemplates;
    }
    
    public boolean showTemplates() {
        return showTemplates;
    }
    
    /**
     * Getter for property framework.
     * @return Value of property framework.
     */
    public Framework getFramework() {
        return framework;
    }
    
    /**
     * Setter for property framework.
     * @param framework New value of property framework.
     */
    public void setFramework(Framework framework) {
        if ( this.framework != framework ) {
            if ( this.framework != null ) {
                this.framework.removeFrameworkAbilityInstanceGroup(this);
            }
            
            this.framework = framework;
            
            if ( framework != null ) {
                framework.addFrameworkAbilityInstanceGroup(this);
                
                Iterator<AbilityEntry> it = instanceSet.iterator();
                while(it.hasNext()) {
                    AbilityEntry ae = it.next();
                    ae.ability.frameworkSet();
                }
            }
        }
    }
    
    /** Shuts down all activated Abilities in the instance group.
     *
     * The shutdowns will be embedded into the provided be.
     *
     * @param forceAll If true, indicates that all abilities should be shut down 
     * no matter what state they are in.  If false, uncontrolled/independent
     * abilities will remain active until the shut down themselves.
     */
    public void shutdownActivatedInstances(BattleEvent be, boolean forceAll) throws BattleEventException{
        Iterator<AbilityEntry> it = instanceSet.iterator();
        while(it.hasNext()) {
            AbilityEntry ae = it.next();
            ae.ability.shutdownActivated(be, forceAll);
            
        }
    }
    
    /** Sets an Ability to be a variation of the base ability.
     *
     *  This means that the ability is one configured specifically for
     *  VariableEffect or VariableAdvantage.
     *
     *  This should only be called by the ability itself.
     *
     *  Ability should already be in the instance group.
     */
    public void setVariationInstance(Ability ability, boolean variation) {
        AbilityEntry ae = getAbilityEntry(ability);
        if ( ae != null && ae.variation != variation ) {
            if ( variation ) {
                ae.variation = true;
                fireInstanceChangedEvent(InstanceChangedEvent.VARIATION_INSTANCES, null, ability);
            }
            else {
                ae.variation = false;
                fireInstanceChangedEvent(InstanceChangedEvent.VARIATION_INSTANCES, ability, null);
            }
        }
    }
    
    /** Returns if this is a variation template.
     */
    public boolean isVariationInstance(Ability ability) {
        AbilityEntry ae = getAbilityEntry(ability);
        return ae != null && ae.variation;
    }
    
    /** Returns the number of variation instances configured.
     *
     */
    public int getVariationCount() {
        int count = 0;
        for(AbilityEntry ae : instanceSet) {
            if ( ae.variation ) count++;
        }
        return count;
    }
    
    /** Returns the indicated Variation ability.
     *
     */
    public Ability getVariation(int index) {
        int count = 0;
        for(AbilityEntry ae : instanceSet) {
            if ( ae.variation ) {
                if ( count == index ) {
                    return ae.ability;
                }
                count++;
            }
        }
        return null;
    }
    
    /** Return the number of Activations currently associated with this ability.
     *
     */
    public int getActivationCount() {
        //return getIndexedSize("ActivationInfo");
        if ( activations == null ) return 0;
        
        return activations.size();
    }
    
    public ActivationInfo getActivationInfo(int index) {
        //return (ActivationInfo)this.getIndexedValue( index, "ActivationInfo", "ACTIVATIONINFO");
        return activations.get(index).getActivationInfo();
    }
    
    public Target getActivationSource(int index) {
        return activations.get(index).getSource();
    }
    
    /** Iterates through all the activations.
     *
     *  Note:  This will not allow you to check the source of the activation.
     */
    public Iterator<ActivationInfo> getActivations() {
        return new AbilityActivationsIterator();
    }
    
    /** Iterates through all of the activations by the given source.
     *
     *  If source is null, all activations will be returned.
     */
    public Iterator<ActivationInfo> getActivations(Target source) {
        return new AbilityActivationsIterator(source);
    }
    
    /** Adds an Activation entry.
     *
     */
    protected void addActivation(ActivationInfo activationInfo, Target source) {
        if ( activations == null ) activations = new ArrayList<AbilityActivationEntry>();
        
        if ( hasActivation(activationInfo) == false ) {
            activations.add( new AbilityActivationEntry(activationInfo,source) );
            fireActivationStateChange(activationInfo.getAbility(), activationInfo, false, true);
        }
    }
    
    /** Removes an Activation entry.
     *
     */
    protected void removeActivation(ActivationInfo activationInfo) {
        if ( activations != null ) {
            int count = activations.size();
            for(int i = 0; i < count; i++) {
                if ( activations.get(i).getActivationInfo() == activationInfo ) {
                    activations.remove(i);
                    fireActivationStateChange(activationInfo.getAbility(), activationInfo, true, false);
                    break;
                }
            }
        }
    }
    
    protected boolean hasActivation(ActivationInfo activationInfo) {
        int count = activations.size();
        for(int i = 0; i < count; i++) {
            if ( activations.get(i).getActivationInfo() == activationInfo ) {
                return true;
            }
        }
        return false;
    }
    
    public AbilityEntry getAbilityEntry(Ability ability) {
        for(AbilityEntry ae : instanceSet) {
            if ( ae.ability == ability) return ae;
        }
        return null;
    }
    
    public String toDebugString() {
        return toString();
    }
    
    private class AbilityEntry implements Serializable {
        Ability ability;
        boolean variation = false;
        
        public AbilityEntry(Ability ability) {
            this.ability = ability;
        }
        
        public boolean equals(Object that) {
            return ability == that;
        }
        
        public String toString() {
            if ( ability == null ) return null;
            return ability.toDebugString();
        }
    }
    
    private class InstanceIterator implements Iterator<Ability> {
        Iterator<AbilityEntry> iterator;
        
        public InstanceIterator(Iterator<AbilityEntry> iterator) {
            this.iterator = iterator;
        }
        
        public boolean hasNext() {
            return iterator.hasNext();
        }

        public Ability next() {
            return iterator.next().ability;
        }

        public void remove() {
            iterator.remove();
        }
    }
    
    protected static class AbilityActivationEntry implements Serializable {
        protected ActivationInfo activationInfo;
        protected Target source;

        public AbilityActivationEntry(ActivationInfo activationInfo, Target source) {
            this.activationInfo = activationInfo;
            this.source = source;
        }

        public ActivationInfo getActivationInfo() {
            return activationInfo;
        }

        public void setActivationInfo(ActivationInfo activationInfo) {
            this.activationInfo = activationInfo;
        }

        public Target getSource() {
            return source;
        }

        public void setSource(Target source) {
            this.source = source;
        }
        
        public boolean equals(Object that) {
            return that != null && (this==that || this.activationInfo == that);
        }
    }
    
    protected class AbilityActivationsIterator implements Iterator<ActivationInfo> {

        protected ActivationInfo next;
        protected Target source;
        protected Iterator<AbilityActivationEntry> iterator;
        
        public AbilityActivationsIterator() {
            this(null);
        }
        
        public AbilityActivationsIterator(Target source) {
            if ( activations != null ) {
                iterator = activations.iterator();
            }
            
            this.source = source;
        }
        
        public boolean hasNext() {
            loadNext();
            return next != null;
        }

        public ActivationInfo next() {
            loadNext();
            
            ActivationInfo ai = next;
            next = null;
            
            return ai;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        protected void loadNext() {
            if ( next == null && iterator != null) {
                while(iterator.hasNext()) {
                    AbilityActivationEntry aae = iterator.next();
                    if ( source == null || aae.getSource() == source ) {
                        next = aae.getActivationInfo();
                        break;
                    }
                }
            }
        }
    }
}
