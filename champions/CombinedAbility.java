/*
 * CombinedAbility.java
 *
 * Created on May 24, 2005, 11:11 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package champions;
import champions.event.AbilityAddedEvent;
import champions.event.AbilityRemovedEvent;
import champions.event.ActivationStateChangeEvent;
import champions.event.InstanceChangedEvent;
import champions.interfaces.AbilityInstanceGroupListener;
import champions.interfaces.AbilityList;
import champions.interfaces.Framework;
import champions.interfaces.PAD;
import champions.interfaces.SpecialParameter;
import champions.parameters.ParameterList;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 *
 * @author twalker
 */
public class CombinedAbility extends Ability
implements AbilityList, PropertyChangeListener, AbilityInstanceGroupListener {
    
    transient private EventListenerList eventListenerList = new EventListenerList();
    transient private boolean recalculatingCosts = false;
	private boolean collapsed;
	private boolean expanded;
    
    private static Power combinedPower = new CombinedPower();
    
    static final long serialVersionUID = 4998584887060789352L;
    
        /** Creates new Ability */
    public CombinedAbility() {
        super();
        this.power =  new CombinedPower();
    }
    
    /** Creates new Ability */
    public CombinedAbility(AbilityInstanceGroup aig) {
        super(aig);
        this.power = new CombinedPower();
    }
    
    /** Creates new Ability, with the name of <code>name</code>.
     * @param name Name of new Ability.
     */
    public CombinedAbility(String name) {
        super(name);
        this.power = new CombinedPower();
    }
    
    /** Returns the number of abilities combined with this power.
     *
     */
    public int getAbilityCount() {
        return getIndexedSize("Subability");
    }
    
    /** Return the combined power at indicated index.
     *
     */
    public Ability getAbility(int index) {
        return (Ability)getIndexedValue(index, "Subability", "ABILITY");
    }
    
    /** Returns the ability of the indicated name, if it exists.
     *
     * Return null if the ability does not exist.
     */
    public Ability getAbility(String name, boolean recurse) {
        for(int i = 0; i < getAbilityCount(); i++) {
            Ability a = getAbility(i);
            if ( a.getName().equals(name) ) return a;
        }
        
        return null;
    }
    
    /** Returns whether the abilityList contains the indicated Ability.
     *
     */
    public boolean hasAbility(Ability ability, boolean recursive) {
        return getAbilityIndex(ability) != -1;
    }
    
    /** Adds the combined ability to the ability.
     *
     */
    public void addAbility(Ability ability) {
        addAbility(ability, -1);
    }
    
    /** Adds the combined ability to the ability.
     *
     */
    public void addAbility(Ability ability, int insertIndex) {
        int index = findExactIndexed("Subability", "ABILITY", ability);
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
            
            index = createIndexed(insertIndex, "Subability", "ABILITY", ability );
            
            //if ( ability.getAbilityList() != this ) {
            ability.setAbilityList(this);
            //}
            
            ability.setFramework(getFramework());
            ability.add("Ability.PARTOFCOMPOUNDPOWER", "TRUE", true);
            
            ability.getInstanceGroup().addAbilityInstanceGroupListener(this);
            
            if ( getSource() != null && oldSource != getSource() ) {
                getSource().attachAbilityToTarget(ability);
            }
            
            if ( ability.hasSpecialParameter("Automatically Activate Ability") ) {
                boolean oldValue = ability.isNormallyOn();
                
                ability.setNormallyOn(false);
                SpecialParameter sp = ability.findSpecialParameter("Automatically Activate Ability");
                ability.removeSpecialParameter( sp );
                
                if ( getAbilityCount() == 1 ) {
                    setNormallyOn(oldValue);
                }
            }
            
            ability.addPropertyChangeListener("Ability.CPCOST", this);
            ability.addPropertyChangeListener("Ability.ENDCOST", this);
            
            fireChangeEvent( "Ability Added" );
            abilityModificationInSublist("Ability Added");
            
            ability.reconfigure();
            
            updateAbilityDescription();
            
            calculateMultiplier();
            calculateCPCost();
        }
        else if ( index != insertIndex ) {
            moveIndexed(index, insertIndex, "Subability", false);
            fireChangeEvent( "Ability Moved" );
            abilityModificationInSublist("Ability Moved");
        }
        
        
    }
    
    /** Removes the indicated combinded ability from this ability.
     *
     */
    public void removeAbility(Ability ability) {
        removeAbility(ability, true);
    }
    
    /** Removes the indicated combinded ability from this ability.
     *
     */
    public void removeAbility(Ability ability, boolean clearAbilitySource) {
        int index = findExactIndexed("Subability", "ABILITY", ability);
        if ( index != -1 ) {
            
            if ( getSource() != null && clearAbilitySource ) {
                getSource().detachAbilityFromTarget(ability);
            }
            
            if ( ability.getAbilityList() == this ) {
                ability.setAbilityList(null);
            }
            
            removeAllIndexed(index, "Subability");
            
            ability.removePropertyChangeListener("Ability.CPCOST", this);
            ability.removePropertyChangeListener("Ability.ENDCOST", this);
            ability.getInstanceGroup().removeAbilityInstanceGroupListener(this);
            
            ability.remove("Ability.PARTOFCOMPOUNDPOWER");
            
            fireChangeEvent( "Ability Removed" );
            abilityModificationInSublist("Ability Removed");
            
            ability.reconfigure();
            
            updateAbilityDescription();
            calculateMultiplier();
            calculateCPCost();
        }
    }
    
    /** Returns the index of the constituent ability in the combined ability.
     *
     * @return -1 if the ability isn't found.
     */
    /** Removes the indicated combinded ability from this ability.
     *
     */
    public int getAbilityIndex(Ability ability) {
       int index = findExactIndexed("Subability", "ABILITY", ability);
       return index;
    }
    
    /** Set the Index of the given ability.
     *
     * This method sets the index of the given ability, changing the order of the abilities in 
     * the list.
     *
     */
    public void setAbilityIndex(Ability ability, int newIndex) {
        int oldIndex = getAbilityIndex(ability);
        
        if ( oldIndex != -1 && newIndex != -1 ) {
            moveIndexed(newIndex, oldIndex, "Subability", true);
            fireChangeEvent( "Ability Moved" );
        }        
    }
    
    public void updateAbilityDescription() {
        StringBuffer sb = new StringBuffer();
        
        if ( getAbilityCount() == 0 ) {
            sb.append("Combined Power [No Sub-Abilities]");
        }
        else {
            sb.append("Combined Ability [");
            for(int i = 0; i < getAbilityCount(); i++) {
                Ability ability = getAbility(i);
                String desc = ability.getDescription();

                sb.append(desc);
                if ( i < getAbilityCount() -1 ) sb.append(" plus ");
            }
            sb.append("]");
        }
        
        int count = getIndexedSize("SpecialParameter");
        for (int i=0;i<count;i++) {
            String pad;
            if ( ( pad = getIndexedStringValue(i, "SpecialParameter","DESCRIPTION")) != null) {
                if ( pad != null && pad.equals("") == false) sb.append(  ", " + pad );
            }
        }
        
       // add( "Ability.HTMLDESCRIPTION", ChampionsUtilities.createWrappedHTMLString(sb.toString(), 40), true, true);
       // add( "Ability.DESCRIPTION", sb.toString(), true, true);
        setDescription(sb.toString());
    }
    
        /**
     * @param description  */
    public void setPowerDescription(String description) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * @param index
     * @param description  */
    public void setAdvantageDescription(int index, String description) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * @param index
     * @param description  */
    public void setLimitationDescription(int index, String description) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * 
     * 
     * @param source
     */
    public void setSource(Target source) {    
        //add("Ability.SOURCE", new TargetAlias(source), true, false);
        if ( source != null ) {
            sourceAlias = new TargetAlias(source);
        }
        else {
            sourceAlias = null;
        }
        
        for(int i = 0; i < getAbilityCount(); i++) {
            Ability ability = getAbility(i);
            ability.setSource(source);
        }
    }
    
    /**
     * 
     * 
     * @param sourceAlias
     * @param checkTime
     * @return 
     */
    public boolean isEnabled(Target source, boolean checkTime) {        
        if ( super.isEnabled(source,checkTime) == false ) return false;
        
        /*for(int i = 0; i < getAbilityCount(); i++) {
            Ability ability = getAbility(i);
            if ( ability.isEnabled(sourceAlias,checkTime) == false ) return false;
        }*/
        return true;
    }
    
    /**
     * Returns whether the Ability is activated.
     * 
     * For combined abilities, this means either one or more of its constitent 
     * abilities are activated.  Combined abilities are considered instant
     * abilities for terms of activation.
     * 
     * 
     * @param sourceAlias Target to check for activation.  If null, get the ability sourceAlias.
     * @return True if ability is activated, false otherwise.
     */
    public boolean isActivated(Target source) {
        if ( ! isModifiableInstance() && ! isCurrentInstance()) return getInstanceGroup().getCurrentInstance().isActivated(source);
       
        for(int i = 0; i < getAbilityCount(); i++) {
            Ability ability = getAbility(i);
            if ( ability.isActivated(source) ) return true;
        }
        
        return false;
    }
    
    /** Sets the "Persistence" type of the ability.
     *
     * Combined abilities are always instanst.
     */
    public void setPType(String ptype, boolean replace) {
        
    }
    
    /** Gets the "Persistence" type of the ability.
     *
     * Combined abilities are always instanst.
     *
     * @see setPType
     */
    public String getPType() {
        return "INSTANT";
    }
    
    /** Sets the amount of time it takes to activate this ability.
     *
     * I am uncertain how much time should be taken to activate a combined 
     * power.  What if the constituint powers have different activation times?
     *
     * @Todo Look into the proper setting for this...
     *
     * The activation time determines how much time is required to activate 
     * the power, and hence, how much time will be used by the character
     * who activates this power.
     *
     * ActivationTime should be one of the following: "HOLD", "INSTANT", "HALFMOVE",
     * "FULLMOVE", or "ATTACK".  Most are self explanetory.  HOLD causes the ability 
     * put the target into hold combat state.  ATTACK activation time uses the 
     * remainder of a characters phase.
     */
//    public void setActivationTime(String activationTime, boolean replace) {
//        add("Ability.TIME", activationTime, replace);
//    }
    
    /** Returns the Activation time of the ability.
     *
     * For combined ability, calculate the worst case activation time
     * over all of the abilities.
     */
    public String getActivationTime() {

        String activationTime = "INSTANT";
        
        for(int i = 0; i < getAbilityCount(); i++) {
            Ability a = getAbility(i);
            String aTime2 = a.getActivationTime();
            
            activationTime = ChampionsUtilities.getLongerActivationTime(activationTime, aTime2);
        }
        
        return activationTime;
    }
    
        /** Creates an Activate BattleEvent for the indicated Ability. 
     *
     * This method is run when the user clicks on the ability to activate the ability.
     * It should create the default action ability.  For most powers, this 
     * will be an activate battle event.
     *
     * If the user wishes to use a maneuver with this ability, the maneuver's 
     * getActivateAbilityBattleEvent will first be called.  If it returns null, this 
     * method will be called.  If it returns non-null, this method will be skipped
     * and it will be assumed that the manuever is taking care of the 
     * activation.  
     *
     * Generally maneuvers will allow the power to build the activation event,
     * but either may be required to perform the action.
     *
     * This method will not be called if the ability is already activated.
     */
    public BattleEvent getActivateAbilityBattleEvent(Ability ability, Ability maneuver, Target source) {
        // Have to return the special battle event here to activate!
        if ( source == null ) source = getSource();
        
        return new CombinedAbilityBattleEvent(this,source);
    }
    
    /** Creates an Activate BattleEvent for the indicated Maneuver. 
     *
     * This method is run when the user clicks on the ability to activate the ability
     * with a maneuver.  It should create the default action maneuver.  
     * For most powers, this will be an activate battle event.
     *
     * If the user wishes to use a maneuver with this ability, the maneuver's 
     * getActivateManeuverBattleEvent will first be called.  If it returns null, this 
     * method will be called.  If it returns non-null, this method will be skipped
     * and it will be assumed that the manuever is taking care of the 
     * activation.  
     *
     * Generally maneuvers will allow the power to build the activation event,
     * but either may be required to perform the action.
     *
     * This method will not be called if the ability is already activated.
     */
    public BattleEvent getActivateManeuverBattleEvent(Ability ability, Ability maneuver, Target source) {
        return null;
    }
    
    /** Returns a cloned copy of the ability.
     *
     * A clone of the ability is not a perfect clone.  Since the clone will not be immediately attached
     * to the same abilityList, it's abilityList will be set to null.
     *
     * The Source target of a cloned ability will be let the same as the original.  However, the cloned
     * copy will not really be attached to the target until it is added to the target.
     */
    
    public Object clone() {
        
        Ability newAbility = createAbilityObject(true);
        
        // Copy Special Stuff...
        Object o;
        //newAbility.add("Ability.NAME", getName(),true, true);
        newAbility.name = name;
        
//        if ( ( o = getValue("Ability.SOURCE") ) != null ) {
//            newAbility.add("Ability.SOURCE", o, true, false);
//        }
//        
//        if ( ( o = getValue("Ability.AUTOSOURCE") ) != null ) {
//            newAbility.add("Ability.AUTOSOURCE", o, true, false);
//        }
        newAbility.sourceAlias = sourceAlias;
        newAbility.autoSource = autoSource;
        
        // Don't copy the AbilityList of the original!!!
        
        
        int index, count;
        String key;
        
        count = getIndexedSize( "SpecialParameter" ) ;
        for ( index=0;index<count;index++ ) {
            SpecialParameter l = (SpecialParameter)getIndexedValue( index, "SpecialParameter", "SPECIALPARAMETER" );
            newAbility.createIndexed("SpecialParameter", "SPECIALPARAMETER",l);
        }
        
        count = getIndexedSize( "SpecialEffect" ) ;
        for ( index=0;index<count;index++ ) {
            SpecialEffect se = (SpecialEffect)getIndexedValue( index, "SpecialEffect", "SPECIALEFFECT" );
            newAbility.createIndexed("SpecialEffect", "SPECIALEFFECT",se);
        }
        
        count = getIndexedSize( "SpecialParameter" ) ;
        for ( index=0;index<count;index++ ) {
            SpecialParameter l = (SpecialParameter)getIndexedValue( index, "SpecialParameter", "SPECIALPARAMETER" );
            ParameterList pl = l.getParameterList(this, index);
            l.configure(newAbility,pl);
        }
        
        // Clone the constituent abilties
        for(int i = 0; i < getAbilityCount(); i++) {
            Ability ability = getAbility(i);
            ability = (Ability)ability.clone();
            addAbility(ability);
        }
        
        // Calculate the multipliers, and trigger CP and END calculations.
        newAbility.calculateMultiplier();
        
        return newAbility;
    }
    
    /**
     * @param self  */
    public void setTargetSelf(boolean self) {
        super.setTargetSelf(self);
        
        // Clone the constituent abilties
        for(int i = 0; i < getAbilityCount(); i++) {
            Ability ability = getAbility(i);
            ability.setTargetSelf(self);
        }
    }
    
    /** Adds the indicated PAD to the ability.  
     *
     * The combined ability can't have pads added to it.
     *
     * @param pad
     * @param d
     * @return False if the pad wasn't added. 
     */
    public boolean addPAD(PAD pad, ParameterList pl) {
        return false;
    }
    
    /** Calculates the Advantage/Limitation multiplier for the ability.
     *
     * Combined Abilities never have limitations/advantages.  They are just
     * grouping mechanisms.
     */
    public void calculateMultiplier() {
        setAdvCost(0);
        setAdvDCCost(0);
        setLimCost(0);
    }
    
    /** Calculates the END cost of the ability as it is configured in this instance.
     * 
     * Just sums the end cost of the constituent abilities.
     */
    public int calculateENDCost() {
        boolean oldRecal = recalculatingCosts;
        recalculatingCosts = true;
        int end = 0;
        // Clone the constituent abilties
        for(int i = 0; i < getAbilityCount(); i++) {
            Ability ability = getAbility(i);
            end += ability.calculateENDCost();
        }
        recalculatingCosts = oldRecal;
        return end;
    }
    
    /** Calculates the CP cost of the ability as it is configured in this instance.
     * 
     * The CP cost of combined ability is just sum of constituent abilities.  This
     * might have to be modified by the multipower calculations, but I will look at
     * this later.
     *
     * I believe this will just be used for display and not actually added to the 
     * cost of the character.  Rather, the constituent abilities will add their 
     * costs directly.
     */
    public int calculateCPCost() {
        boolean oldRecal = recalculatingCosts;
        recalculatingCosts = true;
        int cpcost = 0;
        int apcost = 0;
        int realcost = 0;
        // Clone the constituent abilties
        for(int i = 0; i < getAbilityCount(); i++) {
            Ability ability = getAbility(i);
            cpcost += ability.getCPCost();
            apcost += ability.getAPCost();
            realcost += ability.getRealCost();
        }
        
        setAPCost(apcost);
        setCPCost(cpcost);
        setRealCost(realcost);
        
        recalculatingCosts = oldRecal;
        return cpcost;
    }
    
    /** Forces a reconfiguration of the ability.
     *
     * Reconfigure() will force a complete reconfiguration of the ability.  It does not rebuild the ability,
     * rather, causes configurePAD to be called for the power and all advantages/limitations.
     *
     * This should not be called from within a configurePAD of the power or an advantage/limitation, as it will
     * cause an infinite recursive call.
     */
    public void reconfigure() {
                
        int count = getIndexedSize( "SpecialParameter" ) ;
        for (int index=0;index<count;index++ ) {
            SpecialParameter l = (SpecialParameter)getIndexedValue( index, "SpecialParameter", "SPECIALPARAMETER" );
            ParameterList pl = l.getParameterList(this, index);
            reconfigureSpecialParameter(l,pl,index);
        }
        
        for(int i = 0; i < getAbilityCount(); i++) {
            Ability ability = getAbility(i);
            ability.reconfigure();
        }
        
        calculateMultiplier();
        calculateCPCost();
    }
    
    /**
     * @param required  */
    public void setAutoHit(boolean required) {
        super.setAutoHit(required);
        
        for(int i = 0; i < getAbilityCount(); i++) {
            Ability ability = getAbility(i);
            ability.setAutoHit(required);
        }
    }
    
        /**
     * Return true if abilities are configure in the same way. 
     * 
     * This method performs an deep check of the configuration of the ability
     * with another ability to determine if they are configured exactly the
     * same.  Currently, the order of Advantages, Limitations, Special Parameters
     * must be the same, or the configuration are considered different.
     * 
     * This method does not compare the Name of the ability, but it does
     * compare the sourceAlias of the ability and the autosource parameter.
     * 
     * 
     * @param ability Ability to compare to.
     * @return True if they have the same configuration, false otherwise.
     */
    public boolean compareConfiguration(Ability that) {
        if ( that == null ) return false;
        
        if ( that instanceof CombinedAbility == false ) return false;
        
        if ( this == that ) return true;
        
        // Check sources...
        if ( isAutoSource() != that.isAutoSource() ) return false;
        if ( getSource() != that.getSource() ) return false;
        
        // Compare SpecialParameters...assume in the same order
        int thisCount = getSpecialParameterCount();
        int thatCount = that.getSpecialParameterCount();

        if ( thisCount != thatCount ) return false;

        for(int index = 0; index < thisCount; index++) {
            //if ( this.getSpecialParameter(index).equals(that.getSpecialParameter(index)) == false) return false;
            if ( this.getSpecialParameter(index).compareConfiguration(this, index, that, index) == false ) return false;
        }
        
        // Compare Special Effect...order independent...
        thisCount = getSpecialEffectCount();
        thatCount = that.getSpecialEffectCount();

        if ( thisCount != thatCount ) return false;

        for(int index = 0; index < thisCount; index++) {
            if ( that.hasSpecialEffect(this.getSpecialEffect(index).getName()) == false ) return false;
        }
        for(int index = 0; index < thatCount; index++) {
            if ( this.hasSpecialEffect(that.getSpecialEffect(index).getName()) == false ) return false;
        }
        
        if ( getAbilityCount() != ((CombinedAbility)that).getAbilityCount() ) return false;
        
        for(int i = 0; i < getAbilityCount(); i++) {
            Ability ability = getAbility(i);
            Ability ability2 = ((CombinedAbility)that).getAbility(i);
            if( ability.compareConfiguration(that) == false ) return false;
        }
        
        return true;
    }
   
    /** Returns a new instance of this ability, which is linked to this one.
     *
     * The parameters lists of the child will be linked to this ability.  Also,
     * the Child.ABILITY index of this ability will be set so reconfiguration
     * information can be propogated.
     *
     * This is totally a hack and don't let anyone tell you different. Man,
     * this is really a bunch of crap and I can't imagine a more awkward and
     * difficult way to do this.  I am personally ashamed of this code and I 
     * truly believe that anyone who reads it is dumber for having done so.
     */
    
    public Ability createChildInstance() {
        CombinedAbility newAbility = (CombinedAbility)super.createChildInstance();
        
        for(int i = 0; i < getAbilityCount(); i++) {
            Ability ability = getAbility(i);
            ability = ability.createChildInstance();
            newAbility.addAbility(ability);
        }
        
        return newAbility;
    }
    
    /** Creates an unconfigured object of the correct class for use in cloning.
     *
     * This utility method should create a plain, unconfigured Ability object
     * of the same class as the original.  This object will then be configured
     * by clone or createChildInstance to create a new Ability.
     */
    protected Ability createAbilityObject(boolean createNewInstanceGroup) {
        return new CombinedAbility();
    }
    
    /** Set the Parent of the Ability.
     *
     * This method should be used to set the parent of the ability.  It will
     * take care of calling addChildAbility and removeChildAbility against the
     * old and new parents, if necessary.
     *
     * NOTE: All related abilties must be in the same instance group.  However,
     * due to synchronization issues, an ability parent should be set prior to
     * including it in an instance and the ability should be removed from the
     * instance prior to being removed from the parent.
     *
     * @todo Merge parameter list to parent when setting a parent.
     */
    protected void setParentAbility(Ability parent) {
        assert( parent instanceof CombinedAbility);
        
        super.setParentAbility(parent);
        
        for(int i = 0; i < getAbilityCount(); i++) {
            Ability ability = getAbility(i);
            Ability subParent = ((CombinedAbility)parent).getAbility(i);
            
            ability.setParentAbility(subParent);
        }
    }

    public void removeSublist(AbilityList sublist, boolean clearAbilitySource) {
    }

    public void removeSublist(AbilityList sublist) {
    }

    
    public boolean isFrameworkSet() {
        return getFramework() != null;
    }

    public AbilityList[] getSublists() {
        return new AbilityList[0];
    }

    public int getSublistCount() {
        return 0;
    }

    public AbilityList getSublist(int index) {
        return null;
    }

    public champions.interfaces.AbilityIterator getSkills(boolean recursive) {
        return new NullAbilityIterator();
    }

    public champions.interfaces.AbilityIterator getSkills() {
        return new NullAbilityIterator();
    }

    public champions.interfaces.AbilityIterator getAbilities(boolean recursive) {
        return new NullAbilityIterator();
    }

    public champions.interfaces.AbilityIterator getAbilities() {
        return new NullAbilityIterator();
    }

    public AbilityList findSublist(String sublistName) {
        return null;
    }

    public AbilityList cloneList() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public void addSublist(AbilityList sublist) {
        
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
        if ( eventListenerList == null ) eventListenerList = new EventListenerList();
        eventListenerList.add(ChangeListener.class, listener);
    }
    
    /** Removes a ChangeListener for the List.
     */
    public void removeChangeListener(ChangeListener listener) {
        if ( eventListenerList != null ) eventListenerList.remove(ChangeListener.class, listener);
    }
    
    protected void fireChangeEvent(final String reason) {
        if ( eventListenerList != null ) {
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

    /** Listens to change in the CP/END cost of child abilities.
     *
     */
    public void propertyChange(java.beans.PropertyChangeEvent propertyChangeEvent) {
        String name = propertyChangeEvent.getPropertyName();
    
        
        if ( name != null ) {
            if (name.equals("Ability.CPCOST")) {
                if ( ! recalculatingCosts ) calculateCPCost();
            }
            else if (name.equals("Ability.ENDCOST")) {
                if ( ! recalculatingCosts ) calculateENDCost();
            }
        }
    }

    public int calculateCost(Ability ability, int baseCost, double advantages, double limitations) {
        // Return the real cost of the subability, but pass in this as the 
        // ability so any framework parameters are taken from this ability.
        if ( getFramework() != null ) return calculateCost(this, baseCost, advantages, limitations);
        else return ChampionsUtilities.roundValue(baseCost * ( 1 + advantages ) / ( 1 - limitations ), false);
    }
    
        /**
     * @return  */
    public boolean isPersistent() {
        for(int i = 0; i < getAbilityCount(); i++) {
            Ability a = getAbility(i);
            if ( a.isPersistent() ) return true;
        }
        
        return false;
    }
    
    
    
    public boolean isConstant() {
        if ( isPersistent() ) return false;
        
        for(int i = 0; i < getAbilityCount(); i++) {
            Ability a = getAbility(i);
            if ( a.isConstant() ) return true;
        }
        
        return false;
    }

    public boolean isInstant() {
        return ! isConstant() || ! isPersistent();
    }
  
    public boolean isRangedAttack() {
        for(int i = 0; i < getAbilityCount(); i++) {
            Ability a = getAbility(i);
            if ( a.isRangedAttack() ) return true;
        }
        
        return false;
    }
    
    public boolean isAttack() {
        for(int i = 0; i < getAbilityCount(); i++) {
            Ability a = getAbility(i);
            if ( a.isAttack() ) return true;
        }
        
        return false;
    }
    
    public boolean isDefense() {
        for(int i = 0; i < getAbilityCount(); i++) {
            Ability a = getAbility(i);
            if ( a.isDefense() ) return true;
        }
        
        return false;
    }
    
    public boolean isMeleeAttack() {
        for(int i = 0; i < getAbilityCount(); i++) {
            Ability a = getAbility(i);
            if ( a.isMeleeAttack() ) return true;
        }
        
        return false;
    }
    
    public boolean isKillingAttack() {
        for(int i = 0; i < getAbilityCount(); i++) {
            Ability a = getAbility(i);
            if ( a.isKillingAttack() ) return true;
        }
        
        return false;
    }
    
//    public int findFrameworkAbilityInstanceGroup(Ability ability) {
//        return -1;
//    }
//
//    public int findFrameworkAbilityInstanceGroup(AbilityInstanceGroup aig) {
//        return -1;
//    }
//
//    public FrameworkAbility getFrameworkAbility() {
//        return null;
//    }
//
//    public AbilityInstanceGroup getFrameworkAbilityInstanceGroup(int index) {
//        return null;
//    }
//
//    public int getFrameworkAbilityInstanceGroupCount() {
//        return 0;
//    }
//
//    public int getFrameworkConfiguredPoints() {
//        return 0;
//    }
//
//    public int getFrameworkMode() {
//        return FRAMEWORK_IMPLICIT_RECONFIG;
//    }
//
//    public int getFrameworkPoolSize() {
//        return 0;
//    }
//
//    public boolean isFrameworkAbilityEnabled(Ability ability, Target sourceAlias) {
//        return this.isEnabled(sourceAlias);
//    }
//
//    /** Add the ability to the framework.
//     *
//     * This method should be called by the Ability when it is added to the 
//     * framework.  If the ability cannot be added to the framework for some
//     * reason, this method will return null.
//     *
//     */
//    public boolean addFrameworkAbilityInstanceGroup(AbilityInstanceGroup abilityInstanceGroup) {
//        if ( abilityInstanceGroup == null ) return false;
//       // if ( abilityInstanceGroup == getFrameworkAbility().getInstanceGroup()) return false;
//        int index = findExactIndexed("FrameworkAbilityInstanceGroup", "AIG", abilityInstanceGroup);
//        if ( index == -1 ) {
//            index = createIndexed("FrameworkAbilityInstanceGroup", "AIG", abilityInstanceGroup, false);
//            addIndexed(index, "FrameworkAbilityInstanceGroup", "CONFIGURED", "TRUE", true);
//            
//            // The Advantages/Limitations belonging to the Framework
//            // should be passed onto the ability
//            configureAbilityModifiers(abilityInstanceGroup.getBaseInstance());
//        }
//        return true;
//    }   
//    
//    /** Removes the Ability from the framework.
//     *
//     * This method should be called by the Ability when it is no longer a 
//     * member of the framework.  
//     *
//     * This should only be called by the baseAbility of the Ability instance group.  
//     * Other abiliies should use the base ability.
//     */
//    public void removeFrameworkAbilityInstanceGroup(AbilityInstanceGroup abilityInstanceGroup) {
//        int index = findExactIndexed("FrameworkAbilityInstanceGroup", "AIG", abilityInstanceGroup);
//        if ( index != -1 ) {
//            removeAllIndexed(index, "FrameworkAbilityInstanceGroup");
//            
//            unconfigureAbilityModifiers(abilityInstanceGroup.getBaseInstance());
//        }
//    }
//
//    public void setFrameworkMode(int newMode) {
//    }
//
//    public void setFrameworkPoolSize(int poolSize) {
//    }
//
//    public void configureAbilityModifiers(Ability ability) {
//        if ( getFramework() != null ) {
//                getFramework().configureAbilityModifiers(ability);
//        }
//    }
//
//    public void unconfigureAbilityModifiers(Ability ability) {
//        if ( getFramework() != null ) {
//                getFramework().unconfigureAbilityModifiers(ability);
//        }
//    }
//    
    /** Sets the Framework for the ability.
     *
     * This method should always be called on the Ability.  The Ability version
     * will call setFramework on the AbilityInstanceGroup, which will in turn
     * inform the framework itself that it wants to be part of the framework.
     *
     * The Ability instance group will inform all of the instance ability that
     * a framework has been set via the frameworkSet method.
     */
    public void setFramework(Framework framework) {
        Framework oldFramework = getFramework();
            
        super.setFramework(framework);
        
        for(int i = 0; i < getAbilityCount(); i++) {
            Ability ability = getAbility(i);
            ability.setFramework(framework);
        }
    }

    public void abilityAdded(AbilityAddedEvent evt) {
    }

    public void abilityRemove(AbilityRemovedEvent evt) {
    }

    public void instanceChanged(InstanceChangedEvent evt) {
    }

    public void activationStateChanged(ActivationStateChangeEvent evt) {
        Target source = evt.getAbility().getSource();
        boolean activated = isActivated(source);
        getInstanceGroup().activationStateChanged(this, evt.getActivationInfo(), !activated, activated);
    }
    
    public static class CombinedPower extends Power {
        
        public CombinedPower() {
            
        }

        public Object[][] getParameterArray() {
            return new Object[][] {};
        }
     

    }
    
	public void setCollapsed(boolean b) {
		collapsed = true;
		expanded = false;
	}
	
	public void setExpanded(boolean b) {
		 collapsed = false;
		 expanded = true;
		
	}

	@Override
	public boolean getCollapsed() {
		return collapsed;
	}

	@Override
	public boolean getExpanded() {
		return expanded;
	}
}
