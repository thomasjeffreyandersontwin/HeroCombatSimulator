/*
 * ActivationEvent.java
 *
 * Created on April 25, 2001, 9:31 AM
 */

package champions;

import champions.interfaces.ChampionsConstants;
import champions.interfaces.IndexIterator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import tjava.Destroyable;


/**
 *
 * @author  twalker
 * @version
 */
final public class ActivationInfo extends DetailList
implements ChampionsConstants {
    static final long serialVersionUID = 8911139552166141395L;
    
    protected Ability ability;
    protected Ability maneuver;
    protected String state;
    protected Chronometer startTime;
    protected BattleEvent delayedEvent;
    protected Chronometer lastProcessedTime;
    protected Target source;
    protected Chronometer lastEndTime;
    
    /** Creates new ActivationEvent */
    public ActivationInfo() {
        setFireChangeByDefault(false);
        setState("NEW");
    }
    
    
    /** Add for property ability.
     * @param ability New value of property ability.
     */
    
    
    public void addAbilityLink(Ability ability) {
        int index;
        if ( ability != null ) {
            if ( this.findIndexed( "Ability","ABILITY", this) == -1 ) {
                
                index = this.createIndexed(0,"Ability","ABILITY", ability, false);
                
                addLinks();
            }
        }
    }
    /** Remover for property ability.
     * @param ability New value of property ability.
     */
    public void removeAbilityLink(Ability ability) {
        if ( ability != null ) {
            int aiIndex, aIndex;
            if ( (aiIndex = this.findIndexed( "Ability","ABILITY", ability)) != -1 ) {
                ability.removeActivation(this);
                this.removeAllIndexed(aiIndex, "Ability" );
            }
            
            // Remove the Target:ActivationInfo[].* for this ability
            Target source = getSource();
            if ( source != null ) {
                int sIndex = 0;
                while ( (sIndex = source.findIndexed(sIndex+1, "ActivationInfo", "ACTIVATIONINFO", this )) != -1 ) {
                    if ( source.getIndexedValue( sIndex, "ActivationInfo","ABILITY" ) == ability ) {
                        //You found one, so get out of here...
                        break;
                    }
                }
                
                if ( sIndex != -1 ) {
                    // Found it
                    source.removeAllIndexed(sIndex, "ActivationInfo");
                }
            }
        }
    }
    
    public boolean hasAbilityLinks() {
        return (getIndexedSize("Ability") > 0);
    }
    
    /** Getter for property state.
     * @return Value of property state.
     */
    public String getState() {
        //return this.getStringValue( "ActivationInfo.STATE" );
        return state;
    }
    
    /** Setter for property state.
     * @param state New value of property state.
     */
    public void setState(String state) {
        String oldState = getState();
        //this.add( "ActivationInfo.STATE", state, true );
        
        this.state = state;
        
        Ability a;
        
      //  if ( getAbility() != null ) getAbility().activationStateChanged(this, oldState, state);
      //  if ( getManeuver() != null ) getManeuver().activationStateChanged(this, oldState, state);
    }
    
    /** Sets the original time that the ability was started.
     *
     */
    public void setStartTime(Chronometer startTime) {
        //this.add("ActivationInfo.STARTTIME", startTime, true);
        this.startTime = startTime;
    }
    
    /** Returns the time this ability was originally activated.
     *
     */
    public Chronometer getStartTime() {
        //return (Chronometer)getValue("ActivationInfo.STARTTIME");
        return startTime;
    }
    
    /** Sets the Delayed Activation battle event associated with this activation.
     *
     */
    public void setDelayedActivationEvent(BattleEvent be) {
        //this.add("ActivationInfo.DELAYEDEVENT", be, true);
        this.delayedEvent = be;
    }
    
    /** Removes the Delayed Activation event information.
     *
     */
    public void clearDelayedActivationEvent() {
        //remove("ActivationInfo.DELAYEDEVENT");
        this.delayedEvent = null;
    }
    
    /** Returns the Delayed Activation battle event associated with this activation.
     *
     */
    public BattleEvent getDelayedActivationEvent() {
        //return (BattleEvent) getValue("ActivationInfo.DELAYEDEVENT");
        return delayedEvent;
    }
    
    /** Set the last time continuation was processed.
     *
     *  The continuation time is the last time this abilities
     *  continuation is processed.  At the beginning of a characters
     *  phase, all of his continuing abilities are processed and this
     *  time is update.
     */
    public void setLastProcessedTime(Chronometer lastProcessedTime) {
        //this.add("ActivationInfo.PROCESSEDTIME", originalTime, true);
        this.lastProcessedTime = lastProcessedTime;
    }
    
    /** Returns the time this ability was originally activated.
     *
     *  The continuation time is the last time this abilities
     *  continuation is processed.  At the beginning of a characters
     *  phase, all of his continuing abilities are processed and this
     *  time is update.
     */
    public Chronometer getLastProcessedTime() {
        //return (Chronometer)getValue("ActivationInfo.PROCESSEDTIME");
        return lastProcessedTime;
    }
    
    /** Getter for property state.
     * @return Value of property state.
     */
    public Target getSource() {
        //Object o = this.getValue( "ActivationInfo.SOURCE" );
        //if ( o instanceof Target ) {
        //    return (Target) o;
        //}
        //return null;
        return source;
    }
    
    /** Setter for property state.
     * @param state New value of property state.
     */
    public void setSource(Target source) {
        if ( this.source != source ) {
        
            if ( this.source != null ) {
                removeSource();
            }
        
            // Set the Source in the ActivationInfo
           // this.add( "ActivationInfo.SOURCE", source, true );
            this.source = source;
        
            addLinks();
        }
    }
    
    public void removeSource() {
        if ( getSource() != null ) {
            // Just remove the links.  Don't remove the ActivationInfo.SOURCE value/pair because most code assumes it exists
            // and will cause nullPointerExceptions if removed.
            removeLinks();
        }
    }
    
    public void addLinks() {
        int count, index, sIndex, aIndex;
        Ability ability;
        
        Target source = getSource();
        
        count = this.getIndexedSize( "Ability" );
        
        if ( source != null ) {
            // Run Through the Source:ActivationInfo[] looking for all abilities.  Add if they aren't listed.
            for ( index = 0; index< count; index ++ )  {
                ability = (Ability)this.getIndexedValue(index, "Ability", "ABILITY" );
                if ( ability != null ) {
                    sIndex = 0;
                    while ( (sIndex = source.findIndexed(sIndex+1, "ActivationInfo", "ACTIVATIONINFO", this )) != -1 ) {
                        if ( source.getIndexedValue( sIndex, "ActivationInfo","ABILITY" ) == ability ) {
                            //You found one, so get out of here...
                            break;
                        }
                    }
                    
                    if ( sIndex == -1 ) {
                        // Ended up not finding the pair, so add it
                        sIndex = source.createIndexed( "ActivationInfo", "ACTIVATIONINFO", this, false );
                        source.addIndexed( sIndex, "ActivationInfo", "ABILITY", ability, true, false );
                        source.fireIndexedChanged("ActivationInfo");
                    }
                }
            }
            
            for ( index = 0; index< count; index ++ )  {
                // Run through the current abilities and adjust their ActivationInfo[].SOURCE v/ps
                ability = (Ability)this.getIndexedValue(index, "Ability", "ABILITY" );
//                if ( ability != null && ( aIndex = ((Ability)ability).findIndexed( "ActivationInfo", "ACTIVATIONINFO", this ) ) == -1 ) {
//                    aIndex = ((Ability)ability).createIndexed( "ActivationInfo", "ACTIVATIONINFO", this, false);
//                    ((Ability)ability).addIndexed( aIndex, "ActivationInfo", "SOURCE", source, true, false);
//                }
                if ( ability != null ) {
                    ability.addActivation(this, source);
                }
            }
            
            
        }
        
        //System.out.println("ActivationInfo.addLinks called");
    }
    
    public void removeLinks() {
        int count, sIndex, index;
        Target source = getSource();
        
        count = this.getIndexedSize( "Ability" );
        
        // Remove All ActivationInfo[] from source
        if ( source != null ) {
            while ( ( sIndex = source.findIndexed( "ActivationInfo", "ACTIVATIONINFO", this ) ) != -1 ) {
                source.removeAllIndexed( sIndex, "ActivationInfo", false);
                source.fireIndexedChanged("ActivationInfo");
            }
            
            // Remove All ActivationInfo[]  for this ActivationInfo from abilities
            for ( index = 0; index< count; index ++ )  {
                // Run through the current abilities and adjust their ActivationInfo[].SOURCE v/ps
                Ability ability = (Ability) this.getIndexedValue(index, "Ability", "ABILITY" );
//                if ( ability != null && ( aIndex = ((Ability)ability).findIndexed( "ActivationInfo", "ACTIVATIONINFO", this ) ) != -1 ) {
//                    ((Ability)ability).removeAllIndexed( aIndex, "ActivationInfo", false);
//                }
                ability.removeActivation(this);
            }
        }
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( "ActivationInfo [Source: " );
        sb.append( getSource() );
        
        Ability a = getAbility();
        if ( a != null ) {
            sb.append(", Ability: ");
            sb.append( a.getName() );
        }
        
        a = getManeuver();
        if ( a != null ) {
            sb.append(", Maneuver: ");
            sb.append(a.getName());
        }

        sb.append("]");
        return sb.toString();
    }
    
    public void setAbility(Ability ability) {
        if ( ability != null && ability.isBaseInstance() == true ) ability = ability.getModifiableInstance();
        //add("ActivationInfo.ABILITY",  ability, true );
        
        this.ability = ability;
    }
    
    public void setAbility(Ability ability, boolean abilityConstant) {
        if ( abilityConstant == false && ability != null && ability.isBaseInstance() == true )
        	ability = ability.getModifiableInstance();
        //add("ActivationInfo.ABILITY",  ability, true );
        this.ability = ability;
    }
    
    public Ability getAbility() {
        //Object o =  getValue("ActivationInfo.ABILITY" );
        //if ( o != null ) return (Ability)o;
        //return null;
        
        return ability;
    }
    
    public void setManeuver(Ability maneuver) {
        if ( maneuver != null && maneuver.isBaseInstance() == true ) maneuver = maneuver.getInstanceGroup().getCurrentInstance();
        //add("ActivationInfo.MANEUVER",  maneuver, true );
        this.maneuver = maneuver;
    }
    
    public void setManeuver(Ability maneuver, boolean abilityConstant) {
        if ( abilityConstant == false && maneuver != null && maneuver.isBaseInstance() == true ) maneuver = maneuver.getInstanceGroup().getCurrentInstance();
       // add("ActivationInfo.MANEUVER",  maneuver, true );
        this.maneuver = maneuver;
    }
    
    public Ability getManeuver() {
        //Object o =  getValue("ActivationInfo.MANEUVER" );
        //if ( o != null ) return (Ability)o;
        //return null;
        return maneuver;
    }
    
    /* Returns True if ActivtionInfo is in a state considered activated.
     * The Activated States include ACTIVATED and CONTINUING.
     */
    public boolean isActivated() {
        return (getState().equals( "ACTIVATED" ) || getState().equals( "CONTINUING" ));
    }
    
    /** Retruns True if the ActivationInfo is in a delayed state.
     *
     *  
     */
    public boolean isDelayed() {
        return getState().equals("ABILITY_DELAYED");
    }
    
    /* Returns True if ActivtionInfo is in a continuing state..
     */
    public boolean isContinuing() {
        return getState().equals( "CONTINUING" );
    }
    
    public int addTargetGroup(String targetGroup) {
        int index = findIndexed("TargetGroup","NAME",targetGroup);
        
        if ( index == -1 ) {
            index = createIndexed("TargetGroup", "NAME", targetGroup, true);
            addIndexed(index, "TargetGroup", "SIZE", new Integer(0), true);
            // Set the next available reference number vp.  Set to 1, since 0 is always the primary.
            addIndexed(index, "TargetGroup", "REFERENCENUMBER", new Integer(1));
        }
        return index;
    }
    
    public int getTargetGroupIndex(String targetGroup) {
        return findIndexed("TargetGroup","NAME",targetGroup);
    }
    
    public void removeTargetGroup(String targetGroup) {
        int index = findIndexed("TargetGroup","NAME",targetGroup);
        
        if ( index != -1 ) {
            
            // Loop through the Targets and remove all the targets in targetGroup.
            int tindex = findIndexed("Target","TARGETGROUP",targetGroup);
            while ( tindex != -1 ) {
                // Remove this target
                removeAllIndexed(tindex, "Target");
                
                // Find the next target, starting at this one
                // Don't increase the index since, the currect one was removed.
                tindex = findIndexed(tindex, "Target","TARGETGROUP",targetGroup);
            }
            
            removeAllIndexed(index, "TargetGroup");
        }
    }
    
    public void setKnockbackGroup(String targetGroup, String knockbackGroup) {
        int tgindex = findIndexed("TargetGroup","NAME",targetGroup);
        if ( tgindex != -1 ) {
            setKnockbackGroup(tgindex, knockbackGroup);
        }
    }
    
    public void setKnockbackGroup(int tgindex, String knockbackGroup) {
        addIndexed(tgindex, "TargetGroup", "KNOCKBACKGROUP", knockbackGroup, true);
    }
    
    public String getKnockbackGroup(String targetGroup) {
        int tgindex = findIndexed("TargetGroup","NAME",targetGroup);
        if ( tgindex != -1 ) {
            return getKnockbackGroup(tgindex);
        }
        else {
            return "";
        }
    }
    
    public String getKnockbackGroup(int tgindex) {
        return getIndexedStringValue(tgindex, "TargetGroup", "KNOCKBACKGROUP");
    }
    
    public int getTargetGroupSize(String targetGroup) {
        int index = findIndexed("TargetGroup","NAME",targetGroup);
        Integer size = null;
        
        if ( index != -1 ) {
            size  = getIndexedIntegerValue(index, "TargetGroup","SIZE");
            return size.intValue();
        }
        
        return 0;
    }
    
    public void setTargetGroupDCModifier(String targetGroup, double dcModifier) {
        int tgindex = getTargetGroupIndex(targetGroup);
        if ( tgindex == -1 ) {
            tgindex = addTargetGroup(targetGroup);
        }
        
        addIndexed(tgindex, "TargetGroup", "DCMODIFIER", new Double(dcModifier), true);
        
    }
    
    public double getTargetGroupDCModifier(int tgindex) {
        Double i = getIndexedDoubleValue(tgindex, "TargetGroup", "DCMODIFIER");
        return (i==null)?0:i.doubleValue();
    }
    
    /** Returns the next unique ReferenceNumber available in a TargetGroup.
     *
     * The next reference number is incremented each time this method is executed, so
     * the method should only be used once for each node created.
     *
     * The first reference number is always 0 and is considered the Primary target for
     * a targetGroup.  The primary target number (0) will never be returned by this
     * method.  If you are dealing with the primary target for the group, specify 0
     * explicitly for the reference number.
     */
    public int getNextTargetReferenceNumber(String targetGroup) {
        // Check to make sure TargetGroup exists
        int tgindex = findIndexed("TargetGroup","NAME",targetGroup);
        if ( tgindex == -1 ) {
            tgindex = addTargetGroup(targetGroup);
        }
        
        Integer refNumber = getIndexedIntegerValue(tgindex, "TargetGroup", "REFERENCENUMBER");
        addIndexed(tgindex, "TargetGroup", "REFERENCENUMBER", new Integer(refNumber.intValue() + 1), true);
        
        return refNumber.intValue();
    }
    
    /** Adds a new Target to a TargetGroup.
     *
     * This flavor of the method creates the target with the next available
     * target referenceNumber for that target group.
     *
     * @Returns the index of the Target in the ActivationInfo's "Target" index.
     */
    
    public int addTarget(Target target, String targetGroup) {
        
        // Check to make sure TargetGroup exists
        int tgindex = findIndexed("TargetGroup","NAME",targetGroup);
        if ( tgindex == -1 ) {
            tgindex = addTargetGroup(targetGroup);
        }
        
        Integer size = getIndexedIntegerValue(tgindex, "TargetGroup", "SIZE");
        addIndexed(tgindex, "TargetGroup", "SIZE", new Integer(size.intValue() + 1));
        
        int refNumber = getNextTargetReferenceNumber(targetGroup);
        
        int index = createIndexed("Target","TARGET",target);
        addIndexed(index, "Target","TARGETGROUP",targetGroup,true);
        addIndexed(index, "Target","HIT","FALSE",true);
        addIndexed(index, "Target","TOHIT", new Integer(-1),true);
        addIndexed(index, "Target", "TOHITDIE", null, true);
        addIndexed(index, "Target","HITROLL", new Integer(-1),true);
        addIndexed(index, "Target","HITMODE", USEDICE,true);
        addIndexed(index, "Target","DICEROLLMODE", AUTO_ROLL,true);
        addIndexed(index, "Target","OVERRIDEREASON", "",true);
        
        addIndexed(index, "Target","REFERENCENUMBER", new Integer(refNumber),true);
        
        return index;
    }
    
    /** Adds a new Target to a TargetGroup.
     *
     * This flavor of the method creates the target with the indicated reference
     * number.  That target reference number must be obtained through the
     * getNextTargetReferenceNumber method, in order to guarantee uniqueness.
     *
     * @Returns the index of the Target in the ActivationInfo's "Target" index.
     */
    public int addTarget(Target target, String targetGroup, int referenceNumber) {
        // Check to make sure TargetGroup exists
        int tgindex = findIndexed("TargetGroup","NAME",targetGroup);
        if ( tgindex == -1 ) {
            tgindex = addTargetGroup(targetGroup);
        }
        
        int index = getTargetIndex(referenceNumber, targetGroup);
        
        if ( index == -1 ) {
            index = createIndexed("Target","TARGET",target);
            Integer size = getIndexedIntegerValue(tgindex, "TargetGroup", "SIZE");
            addIndexed(tgindex, "TargetGroup", "SIZE", new Integer(size.intValue() + 1));
            
            // Since we are creating for the first time, initialize everything to defaults.
            addIndexed(index, "Target","TARGETGROUP",targetGroup,true);
            addIndexed(index, "Target","HIT","FALSE",true);
            addIndexed(index, "Target","TOHIT", new Integer(-1),true);
            addIndexed(index, "Target","TOHITDIE", null, true);
            addIndexed(index, "Target","HITROLL", new Integer(-1),true);
            addIndexed(index, "Target","HITMODE", USEDICE,true);
            addIndexed(index, "Target","DICEROLLMODE", AUTO_ROLL,true);
            addIndexed(index, "Target","OVERRIDEREASON", "",true);
            
            addIndexed(index, "Target","REFERENCENUMBER", new Integer(referenceNumber),true);
        }
        else {
            if ( target != null ) addIndexed(index,"Target","TARGET",target,true);
        }
        
        
        
        return index;
    }
    
    public void setTargetHit(int tindex, boolean hit) {
        addIndexed(tindex, "Target","HIT", hit ? "TRUE" : "FALSE", true);
    }
    
    public void setTargetHitLocation(int tindex, String hitLocation) {
        addIndexed(tindex, "Target", "HITLOCATION", hitLocation, true);
    }
    
    /** Set the Target HITMODE to OVERRIDE and set the appropriate vps.
     *
     * If a Target should be hit for reasons beyond user input, the
     * Target[].HITMODE should be set to Target[].OVERRIDE should be set true.
     * Target[].HIT should be set appropriate to indicate whether the target was hit or missed.
     * 
     * The reason should be a short string, indicating briefly why the override occurred.
     * The reason is added to the BattleEvent messages by the determineHit method.  If the
     * reason is null, no message will be added to the battleEvent.
     *
     * The explination should be a long string describing to the user why the hit succeeded/fail.  The
     * explination will be displayed by the ToHitNode to give the user information as to what happened.
     * The explination should never be null.
     */
    public void setTargetHitOverride(int tindex, boolean hit, String reason, String explination) {
        addIndexed(tindex, "Target","HIT", hit ? "TRUE" : "FALSE", true);
        addIndexed(tindex, "Target","OVERRIDEREASON", reason, true);
        addIndexed(tindex, "Target","OVERRIDEEXPLINATION", explination, true);
        addIndexed(tindex, "Target","HITMODE", OVERRIDE, true);
    }
    
    public void clearTargetHitOverride(int tindex) {
        String hitMode = getIndexedStringValue(tindex, "Target", "HITMODE");
        
        if ( hitMode.equals(OVERRIDE) ) {
            addIndexed(tindex, "Target","OVERRIDEREASON", null, true);
            addIndexed(tindex, "Target","OVERRIDEEXPLINATION", null, true);
            addIndexed(tindex, "Target","HITMODE", USEDICE, true);
        }
    }
    
    public void setTargetHitMode(int tindex, String mode) {
        addIndexed(tindex, "Target","HITMODE", mode, true);
    }
    
    public void setTargetToHit(int tindex, int toHit) {
        addIndexed(tindex, "Target","TOHIT", new Integer(toHit), true);
    }
    
    public void setTargetToHitDice(int tindex, Dice dice) {
        addIndexed(tindex, "Target","TOHITDIE", dice, true);
    }
    
    public void setTargetToHitRoll(int tindex, int roll) {
        addIndexed(tindex, "Target","HITROLL", new Integer(roll), true);
    }
    
    /** Set the Target Fixed value/reason for the specified target index.
     *
     * If a Target shouldn't be changed by the SelectTarget panel, the
     * Target[].FIXED should be set to true, and the Target[].FIXEDREASON should be set
     * to a full explination of why the Target is fixed.  The explination will
     * be displayed in full in the SelectTargetPanel.
     */
    public void setTargetFixed(int tindex, boolean fixed, String reason) {
        addIndexed(tindex, "Target","FIXED", fixed ? "TRUE" : "FALSE", true);
        addIndexed(tindex, "Target","FIXEDREASON", reason, true);
        
    }
    
    public boolean getTargetFixed(int tindex) {
        return getIndexedBooleanValue(tindex, "Target", "FIXED");
    }
    
    public String getTargetFixedReason(int tindex) {
        return getIndexedStringValue(tindex, "Target", "FIXEDREASON");
    }
    
    public void setTargetDistanceFromExplosion(int tindex, int distance) {
        addIndexed(tindex, "Target","EXPLOSIONDISTANCE", distance, true);
    }
    
    public int getTargetDistanceFromExplosion(int tindex) {
        Integer i = getIndexedIntegerValue(tindex, "Target", "EXPLOSIONDISTANCE");
        return i == null ? 0 : i;
    }

    public void resetCVLists(Target target) {
        // Resets all of the CVLists for the target...
        int count = getIndexedSize("Target");
        for(int i = 0; i < count; i++) {
            Target t = (Target)getIndexedValue(i, "Target", "TARGET");
            if ( t == target ) {
                removeIndexed(i, "Target", "CVLIST");
            }
        }
    }

    public Target getTarget(int tindex) {
        Object o = getIndexedValue(tindex, "Target", "TARGET");
        return ( o == null ) ? null : (Target)o;
    }
    
    public void setTarget(int tindex, Target target) {
        addIndexed(tindex, "Target", "TARGET", target, true, false);
    }
    
    public Target getTargetSource(int tindex) {
        Object o = getIndexedValue(tindex, "Target", "SOURCE");
        return ( o == null ) ? null : (Target)o;
    }
    
    public void setTargetSource(int tindex, Target source) {
        addIndexed(tindex, "Target", "SOURCE", source, true, false);
    }
    
    public String getTargetHitMode(int tindex) {
        return getIndexedStringValue(tindex, "Target", "HITMODE");
    }
    
    public boolean getTargetHit(int tindex) {
        return getIndexedBooleanValue(tindex, "Target", "HIT");
    }
    
    public String getTargetHitLocation(int tindex) {
        return getIndexedStringValue(tindex, "Target", "HITLOCATION");
    }
  
    public String getTargetOverrideReason(int tindex) {
        return getIndexedStringValue(tindex, "Target", "OVERRIDEREASON");
    }
    
    public String getTargetOverrideExplination(int tindex) {
        return getIndexedStringValue(tindex, "Target", "OVERRIDEEXPLINATION");
    }
    
    public int getTargetReferenceNumber(int tindex) {
        Integer o = getIndexedIntegerValue(tindex, "Target", "REFERENCENUMBER");
        return ( o == null ) ? -1 : o.intValue();
    }
    
    public boolean getTargetHasNNDDefense(int tindex) {
        return getIndexedBooleanValue(tindex, "Target", "HASNNDDEFENSE");
    }
    
    public void setTargetHasNNDDefense(int tindex, boolean hasNNDDefense) {
        addIndexed(tindex, "Target","HASNNDDEFENSE", hasNNDDefense ? "TRUE" : "FALSE", true);
    }
    
    public boolean isTargetHasNNDDefenseSet(int tindex) {
        return contains("Target" + Integer.toString(tindex) + ".HASNNDDEFENSE");
    }
    
    public int getSourceSenseIndex(Target target) {
        int index = findIndexed("SourceSense", "TARGET", target);
        if ( index == -1 ) {
            index = createIndexed("SourceSense", "TARGET", target, false);
        }
        return index;
    }
    
    public Sense getSourcesSense(int tindex) {
        return (Sense)getIndexedValue(tindex, "SourceSense", "SENSE");
    }
    
    public void setSourcesSense(int tindex, Sense sense) {
        addIndexed(tindex, "SourceSense","SENSE", sense, true);
    }
    
    public Dice getSourceSenseRoll(int tindex) {
        return (Dice)getIndexedValue(tindex, "SourceSense", "PERROLL");
    }
    
    public void setSourceSenseRoll(int tindex, Dice roll) {
        addIndexed(tindex, "SourceSense","PERROLL", roll, true);
    }
    
    public boolean getSourceRequiresSenseRoll(int tindex) {
        return getIndexedBooleanValue(tindex, "SourceSense", "REQUIRESSENSEROLL");
    }
    
    public void setSourceRequiresSenseRoll(int tindex, boolean requiresRoll) {
        addIndexed(tindex, "SourceSense","REQUIRESSENSEROLL", requiresRoll?"TRUE":"FALSE", true);
    }
    
    public boolean getSourceCanSenseTarget(int tindex) {
        return getIndexedBooleanValue(tindex, "SourceSense", "CANSENSETARGET");
    }
    
    public void setSourceCanSenseTarget(int tindex, boolean canSenseTarget) {
        addIndexed(tindex, "SourceSense","CANSENSETARGET", canSenseTarget?"TRUE":"FALSE", true);
    }
    
    public int getTargetSenseIndex(Target target) {
        int index = findIndexed("TargetSense", "TARGET", target);
        if ( index == -1 ) {
            index = createIndexed("TargetSense", "TARGET", target, false);
        }
        return index;
    }
    
    public Sense getTargetsSense(int tindex) {
        return (Sense)getIndexedValue(tindex, "TargetSense", "SENSE");
    }
    
    public void setTargetsSense(int tindex, Sense sense) {
        addIndexed(tindex, "TargetSense","SENSE", sense, true);
    }
    
    public List<Sense> getTargetsSenseList(int tindex) {
        return (List<Sense>)getIndexedValue(tindex, "TargetSense", "LIST");
    }
    
    public void setTargetsSenseList(int tindex, List<Sense> senseList) {
        addIndexed(tindex, "TargetSense","LIST", senseList, true);
    }
    
    public Sense getAttackerSenseList(int tindex) {
        return (Sense)getIndexedValue(tindex, "AttackerSense", "LIST");
    }
    
    public void setAttackerSenseList(int tindex, List<Sense> senseList) {
        addIndexed(tindex, "AttackerSense","LIST", senseList, true);
    }
    
    public Dice getTargetSenseRoll(int tindex) {
        return (Dice)getIndexedValue(tindex, "TargetSense", "PERROLL");
    }
    
    public void setTargetSenseRoll(int tindex, Dice roll) {
        addIndexed(tindex, "TargetSense","PERROLL", roll, true);
    }
    
    public boolean getTargetRequiresSenseRoll(int tindex) {
        return getIndexedBooleanValue(tindex, "TargetSense", "REQUIRESSENSEROLL");
    }
    
    public void setTargetRequiresSenseRoll(int tindex, boolean requiresRoll) {
        addIndexed(tindex, "TargetSense","REQUIRESSENSEROLL", requiresRoll?"TRUE":"FALSE", true);
    }
    
    public boolean getTargetCanSenseSource(int tindex) {
        return getIndexedBooleanValue(tindex, "TargetSense", "CANSENSESOURCE");
    }
    
    public void setTargetCanSenseSource(int tindex, boolean canSenseTarget) {
        addIndexed(tindex, "TargetSense","CANSENSESOURCE", canSenseTarget?"TRUE":"FALSE", true);
    }
    
    
    public boolean isTargetSecondary(int tindex) {
        return getIndexedBooleanValue(tindex, "Target", "ISSECONDARY");
    }
    
    public void setTargetSecondary(int tindex, boolean secondary) {
        addIndexed(tindex, "Target","ISSECONDARY", secondary ? "TRUE" : "FALSE", true);
        if ( secondary ) {
            // Clear kb secondary...
            addIndexed(tindex, "Target","ISKBSECONDARY", "FALSE", true);
        }
    }
    
    public boolean isTargetKnockbackSecondary(int tindex) {
        return getIndexedBooleanValue(tindex, "Target", "ISKBSECONDARY");
    }
    
    public void setTargetKnockbackSecondary(int tindex, boolean secondary) {
        
        addIndexed(tindex, "Target","ISKBSECONDARY", secondary ? "TRUE" : "FALSE", true);
        if ( secondary ) {
            addIndexed(tindex, "Target","ISSECONDARY", "FALSE", true);
        }
    }
    
    /** Returns the index for the Target in targetGroup.
     *
     * Only the first occurance of the Target will be returned.  If the same Target
     * can occur multiple times in the same TargetGroup, the refNumber versions of the
     * targetGroup methods should be used instead.
     *
     * @deprecated
     */
    
     public int getTargetIndex(Target target, String targetGroup) {
        int index;
        String foundGroup;
        
        index = findIndexed("Target", "TARGET", target);
        while ( index != -1 ) {
            // Grab the group for the occurance of Target.TARGET=taget
            foundGroup = getIndexedStringValue(index, "Target", "TARGETGROUP");
            
            // Check if this is the right group
            if ( foundGroup != null && foundGroup.equals(targetGroup) ) {
                // This is the one, so break.
                break;
            }
            
            // Last once wasn't right, so grab the next target...
            index = findIndexed(index+1, "Target", "TARGET", target);
        }
        return index;
    }
    
    /** Returns the index of the Target with the indicated referenceNumber and targetGroup.
     *
     * If the specified referenceNumber/targetGroup combination does not exist, -1 is return
     * to indicate failure.
     */
     
    public int getTargetIndex(Target target)
    {
    	return  findIndexed("Target", "TARGET", target);
    }
    
    public int getTargetIndex(int referenceNumber, String targetGroup) {
        int index;
        String foundGroup;
        Integer refNumber;
        
        IndexIterator ii = getIteratorForIndex("Target");
        while ( ii.hasNext() ) {
            index = ii.nextIndex();
            // Grab the group for the occurance of Target.TARGET=taget
            foundGroup = getIndexedStringValue(index, "Target", "TARGETGROUP");
            
            // Check if this is the right group
            if ( foundGroup != null && foundGroup.equals(targetGroup) ) {
                refNumber = getIndexedIntegerValue(index, "Target", "REFERENCENUMBER");
                if ( refNumber != null && refNumber.intValue() == referenceNumber ) {
                    // this is the one
                    return index;
                }
            }
        }
        
        // If it gets to here, it wasn't found
        return -1;
    }
    
    /** Removes the indicated target from the targetGroup.
     *
     * If a targetGroup contains multiple copies of the same target,
     * only the first target will be removed.
     *
     * If the target is not found in the targetGroup, nothing occurs.
     *
     * @deprecated The targetReferenceNumber form of this method should now be used.
     */
    public void removeTarget(Target target, String targetGroup) {
        int index = getTargetIndex(target,targetGroup);
        
        if ( index != -1 ) {
            removeAllIndexed(index, "Target");
            
            int tgindex = findIndexed("TargetGroup","NAME",targetGroup);
            Integer size = getIndexedIntegerValue(tgindex, "TargetGroup", "SIZE");
            addIndexed(tgindex, "TargetGroup", "SIZE", new Integer(size.intValue() - 1));
        }
    }
    
    /** Removes the target references by the refNumber from the targetGroup.
     *
     * If the refNumber is not found in the targetGroup, nothing is done.
     */
    public void removeTarget(int refNumber, String targetGroup) {
        int index = getTargetIndex(refNumber,targetGroup);
        
        if ( index != -1 ) {
            removeAllIndexed(index, "Target");
            
            int tgindex = findIndexed("TargetGroup","NAME",targetGroup);
            Integer size = getIndexedIntegerValue(tgindex, "TargetGroup", "SIZE");
            addIndexed(tgindex, "TargetGroup", "SIZE", new Integer(size.intValue() - 1));
        }
    }
    
    /** Returns whether any of the targets of a targetGroup has been hit.
     *
     * This method will run through all of the targets of a target group, searching
     * for targets which has been hit.
     *
     * @Returns True if targets were hit, False is nothing was hit.
     */
    public boolean getTargetGroupHasHitTargets(String targetGroup) {
        IndexIterator tgi = getTargetGroupIterator(targetGroup);
        int tindex;
        boolean targetsHit = false;
        while ( tgi.hasNext() ) {
            tindex = tgi.nextIndex();
            if ( getIndexedBooleanValue(tindex, "Target", "HIT") == true && getTarget(tindex) != null) {
                
                targetsHit = true;
                break;
            }
        }
        return targetsHit;
    }
    
    
    public boolean getHasHitTargets() {
        IndexIterator ii = this.getIteratorForIndex( "Target" );
        int tindex;
        boolean targetsHit = false;
        while ( ii.hasNext() ) {
            tindex = ii.nextIndex();
            if ( getIndexedBooleanValue(tindex, "Target", "HIT") == true && getTarget(tindex) != null) {
                targetsHit = true;
                break;
            }
        }
        return targetsHit;
    }
    
    /** Returns an IndexIterator for the specified TargetGroup.
     */
    public IndexIterator getTargetGroupIterator(String targetGroup) {
        return new TargetGroupIterator(this, targetGroup);
    }
    
    /** Set the temporary indicator on the TargetGroup.
     *
     * Temporary Groups are removed as soon as the battleEvent finish processing.
     * TargetGroup for Knockback and other short term targets should be removed after
     * a battleEvent has finished processing.
     */
    public void setTargetGroupIsTemporary(int tgindex, boolean temporary) {
        addIndexed(tgindex, "TargetGroup","ISTEMPORARY", temporary ? "TRUE" : "FALSE", true);
    }
    
    /** Get the temporary indicator for the TargetGroup.
     */
    public boolean getTargetGroupIsTemporary(int tgindex) {
        return getIndexedBooleanValue(tgindex, "TargetGroup", "ISTEMPORARY");
    }
    
 /*   public CVList getCVList(Target target, String targetGroup) {
        int index = getTargetIndex(target, targetGroup);
        return getCVList(index);
    } */
    
    /** Returns the CVList for the specified TargetIndex, Creating it if necessary.
     *
     * getCVList will create the CVList, but will not populate the CV Values.  This has
     * to be done elsewhere.
     */
    public CVList getCVList(int tindex) {
        CVList cvList = null;
        
        if ( tindex != -1 ) {
            Object o = getIndexedValue(tindex, "Target", "CVLIST");
            
            if ( o != null ) {
                cvList = (CVList)o;
            }
            else {
                cvList = new CVList();
                addIndexed(tindex,"Target","CVLIST",cvList, true);
            }
        }
        return cvList;
    }
    
    public DefenseList getDefenseList(int targetReferenceNumber, String targetGroup) {
        int tindex = getTargetIndex(targetReferenceNumber, targetGroup);
        return getDefenseList(tindex);
    }
    
    public DefenseList getDefenseList(int tindex) {
        DefenseList defenseList = null;
        
        if ( tindex != -1 ) {
            defenseList = (DefenseList)getIndexedValue(tindex, "Target", "DEFENSELIST");
        }
        return defenseList;
    }
    
    public void setDefenseList(int tindex, DefenseList dl) {
        addIndexed(tindex, "Target", "DEFENSELIST", dl, true);
    }
    
    public ObstructionList getObstructionList(int tindex) {
        ObstructionList ol = (ObstructionList)getIndexedValue(tindex, "Target", "OBSTRUCTIONLIST");
        return ol;
    }
    
    public void setObstructionList(int tindex, ObstructionList ol) {
        addIndexed(tindex, "Target", "OBSTRUCTIONLIST", ol, true);
    }
    
    /** Destroys the propertyChangeSupport reference associated with the detail list.
     *
     * Subclasses can override this for special handling of the
     * detaillist object contained by the list.
     * The reference should be set to null if the list is completely done using it.
     */
    protected void destroyValues() {
        if ( detailList != null ) {
            //System.out.println("Destroying values for ActivationInfo: " + this);
            Iterator i = detailList.keySet().iterator();
            // Destroy/Remove all key/value pairs.
            while (i.hasNext() ) {
                Object key = i.next();
                Object value = detailList.get(key);
                
                if ( value instanceof Destroyable && value instanceof DetailList == false ) {
                    ((Destroyable)value).destroy();
                  //  System.out.println("ActivationInfo::Destroying " + value );
                }
            }
            detailList.clear();
            detailList = null;
        }
    }
    
    /** Cleans out non-persistent values after the attack process in complete.
     */
    public void cleanActivationInfo(BattleEvent be) {
        // Clean up temporary TargetGroups up and removes the DCCALCULATED value
        // Grab an iterator for the temporary groups
        String targetGroup;
        boolean temporary;
        int count, index;
        
        count = getIndexedSize("TargetGroup");
        for ( index = 0; index < count; ) {
            temporary = getTargetGroupIsTemporary(index);
            if ( temporary ) {
                targetGroup = getIndexedStringValue(index, "TargetGroup", "NAME");
                removeTargetGroup(targetGroup);
                count --;
            }
            else {
                // Set the DC to uncalculated and increase index.
                addIndexed(index, "TargetGroup", "DCCALCULATED", "FALSE", true);
                index ++;
            }
        }
    }
    
    public void setPrimaryTarget(int primaryIndex, String targetGroup, int targetReferenceNumber) {
        add( "PrimaryTarget" + primaryIndex + ".TARGETGROUP", targetGroup, true);
        add( "PrimaryTarget" + primaryIndex + ".TARGETREFERENCENUMBER", new Integer(targetReferenceNumber), true);
    }
    
    public String getPrimaryTargetGroup(int primaryIndex) {
        return getStringValue("PrimaryTarget" + primaryIndex + ".TARGETGROUP");
    }
    
    public int getPrimaryTargetReferenceNumber(int primaryIndex) {
        Integer i = getIntegerValue("PrimaryTarget" + primaryIndex + ".TARGETREFERENCENUMBER");
        return (i==null) ? -1 : i.intValue();
    }
    
    public boolean hasPrimaryTarget(int primaryIndex) {
        return contains("PrimaryTarget" + primaryIndex + ".TARGETGROUP");
    }
    
    public Target getKnockbackSourceTarget(String targetGroup) {
        IndexIterator ii = getIteratorForIndex("Target", "TARGETGROUP", targetGroup);
        
        Target sourceTarget = null;
        int tindex;
        
        while (ii.hasNext()) {
            tindex = ii.nextIndex();
            Target target = (Target)getIndexedValue(tindex, "Target", "TARGET");
            boolean knockbackSource = isKnockbackSourceTarget(tindex);

            if ( knockbackSource ) {
                sourceTarget = target;
                break;
            }
        }
        
        return sourceTarget;
    } 
    
    public void setKnockbackSourceTarget(int tindex, boolean knockbackSourceTarget) {
        addIndexed(tindex, "Target", "ISKNOCKBACKSOURCE", (knockbackSourceTarget?"TRUE":"FALSE"), true);
    }
    
    public boolean isKnockbackSourceTarget(int tindex) {
        return getIndexedBooleanValue(tindex, "Target", "ISKNOCKBACKSOURCE");
    }
    
    public Chronometer getTimeOfLastENDPayment() {
        return lastEndTime;
    }
    
    public void setTimeOfLastENDPaymet(Chronometer lastEndTime) {
        this.lastEndTime = lastEndTime;
    }
    
    public Integer getDistanceMoved() {
            return getIntegerValue("Movement.DISTANCE");
    }
    
    public void setDistanceMoved(int distanceMoved) {
        add("Movement.DISTANCE", distanceMoved );
    }
    

    
    /** Returns true if any of the secondary knockback targets were actually hit.
     *
     *  This checks if any of the secondary knockback targets were actually hit.
     *  If so, secondary knockback may be necessary.
     */
    public boolean getKnockbackCollisionWithSecondaryTargetOccurred(String targetGroup) {
    
        boolean collisionOccurred = false;

        IndexIterator ii = getIteratorForIndex("Target", "TARGETGROUP", targetGroup);
        
        while (ii.hasNext()) {
            int tindex = ii.nextIndex();
            Target target = (Target)getIndexedValue(tindex, "Target", "TARGET");
            
            if ( isKnockbackSourceTarget(tindex) == false ) {
                boolean hit = getIndexedBooleanValue(tindex, "Target", "HIT");
                if ( hit ) {
                    collisionOccurred = true;
                    break;
                }
            }        
        }

        return collisionOccurred;
    }
    
    public class TargetGroupIterator extends Object
    implements IndexIterator {
        private int currentIndex;
        private ActivationInfo ai;
        private String targetGroup;
        // private lastSize;
        private int nextIndex;
        
        public TargetGroupIterator(ActivationInfo ai, String targetGroup) {
            currentIndex = 0;
            this.ai = ai;
            this.targetGroup = targetGroup;
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
            int next = ai.findIndexed(currentIndex, "Target", "TARGETGROUP", targetGroup);
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
    
}
