/*
 * ActiveAbilitiesListModel.java
 *
 * Created on June 5, 2001, 4:06 PM
 */

package champions;

import javax.swing.*;
import javax.swing.event.*;
import java.beans.*;

import champions.interfaces.*;
import champions.event.*;


/**
 *
 * @author  twalker
 * @version
 */
public final class ActiveAbilitiesListModel extends AbstractListModel
implements ListModel, BattleListener {
    
    private static final int DEBUG = 0;
    /** Holds value of property target. */
    private Target target;
    
    protected boolean invalid;
    protected String invalidReason = null;
    
    /** Creates new ActiveAbilitiesListModel */
    public ActiveAbilitiesListModel() {
        Battle.addBattleListener(this);
    }
    
    /**
     * Returns the value at the specified index.
     */
    public Object getElementAt(int index) {
        if ( target == null || target.getIndexedSize("ActivationInfo") == 0 ) return "None";
        
        Ability a = (Ability)target.getIndexedValue( index, "ActivationInfo", "ABILITY");
        if ( a == null ) {
            return "";
        }
        else {
            return a.getName();
        }
    }
    
    /**
     * Returns the length of the list.
     */
    public int getSize() {
        int size = 0;
        if ( target != null ) {
            size = target.getIndexedSize("ActivationInfo");
        }
        if ( size == 0 ) {
            size = 1;
        }
        return size;
    }
    
    /** Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        return target;
    }
    
    /** Setter for property target.
     * @param target New value of property target.
     */
    public void setTarget(Target target) {
        if ( this.target != target ) {
            this.target = target;
            
            triggerContentsChanged("Target Set");
        }
    }
    
    protected void triggerContentsChanged(String reason) {
        invalidReason = reason;
        
        if ( Battle.getCurrentBattle() != null && Battle.getCurrentBattle().isProcessing() == false ) {
            if ( DEBUG > 0 ) System.out.println("ActiveAbilitiesListModel.triggerContentsChanged: Firing Contents Changed because \"" + invalidReason + "\".");
            fireContentsChanged(this, 0, Integer.MAX_VALUE);
            invalid = false;
            invalidReason = null;
        }
        else {
            if ( DEBUG > 0 ) System.out.println("ActiveAbilitiesListModel.triggerContentsChanged: Queueing Invalid Update because \"" + reason + "\".");
            invalid = true;
        }
    }
    
    
        /** Target Selected Event Occured.
 * Indicates that a target has become active.
 * @param e
 */    
    public void battleTargetSelected(TargetSelectedEvent e) {
        
    }
    
/** Battle Segment has advanced.
 * Indicates the battle time has advanced at least one segment.
 * @param e
 */    
    public void battleSegmentAdvanced(SegmentAdvancedEvent e) {
        
    }
    
/** Sequence of Targets in Battle has changed.
 * Indicates that the sequence of upcoming targets has changed.  This can occur due to 
 * a number of reasons, including new rosters/characters being added and speed changes.
 * @param e
 */    
    public void battleSequenceChanged(SequenceChangedEvent e) {
        
    }
    
/** Generic Battle State Change
 * Indicates that some generic state changed occurred in the battle.  All listeners should verify
 * their state since, a stateChanged event may include many changes to the battle and participants.
 * 
 * Undo and Redo actions generate stateChanged events.
 *
 * @param e
 */    
    public void stateChanged(BattleChangeEvent e) {
        
    }
    
/** The Event list has changed.
 * Indicates some change occurred in the event list for the battle.  Either events were added, removed,
 * undone, or redone.
 *
 * EventNotifications are gauranteed to be sent out for all event list changes.  In the case of Undo/Redo both an 
 * eventNotification and a stateChanged will be sent.
 *
 * @param e
 */    
    public void eventNotification(ChangeEvent e) {
        
    }
    
/** Combat State changed for Participant in battle
 * Indicates that the combat state of one of the participant changed in the current battle.  Usually, the
 * change occurred to the active target, but all changes to combat states of any target will fire this event.
 *
 * @param e
 */    
    public void combatStateChange(ChangeEvent e) {
        
    }
    
/** Processing state of BattleEngine changed
 * Indicates the battleEngine has either started or stopped processing events.  
 * Check the Battle.getCurrentBattle().isProcessing() for the current state of the battleEngine.
 * @param e
 */    
    public void processingChange(BattleChangeEvent event) {
        if ( Battle.getCurrentBattle().isProcessing() == false && invalid ) {
            triggerContentsChanged("ProcessingChange");
        }
    }
    
}
