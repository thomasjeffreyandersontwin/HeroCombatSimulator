/*
 * BattleListener.java
 *
 * Created on September 14, 2000, 2:06 PM
 */

package champions.interfaces;

import champions.BattleChangeEvent;
import java.util.EventListener;

import champions.event.*;
import javax.swing.event.*;
/**
 *
 * @author  unknown
 * @version 
 */
public interface BattleListener extends EventListener {
    
/** Target Selected Event Occured.
 * Indicates that a target has become active.
 * @param event
 */    
    public void battleTargetSelected(TargetSelectedEvent event);
    
/** Battle Segment has advanced.
 * Indicates the battle time has advanced at least one segment.
 * @param event
 */    
    public void battleSegmentAdvanced(SegmentAdvancedEvent event);
    
/** Sequence of Targets in Battle has changed.
 * Indicates that the sequence of upcoming targets has changed.  This can occur due to 
 * a number of reasons, including new rosters/characters being added and speed changes.
 * @param event
 */    
    public void battleSequenceChanged(SequenceChangedEvent event);
    
/** Generic Battle State Change
 * Indicates that some generic state changed occurred in the battle.  All listeners should verify
 * their state since, a stateChanged event may include many changes to the battle and participants.
 * 
 * Undo and Redo actions generate stateChanged events.
 *
 * @param event
 */    
    public void stateChanged(BattleChangeEvent e);
    
/** The Event list has changed.
 * Indicates some change occurred in the event list for the battle.  Either events were added, removed,
 * undone, or redone.
 *
 * EventNotifications are gauranteed to be sent out for all event list changes.  In the case of Undo/Redo both an 
 * eventNotification and a stateChanged will be sent.
 *
 * @param event
 */    
    public void eventNotification(ChangeEvent event);
    
/** Combat State changed for Participant in battle
 * Indicates that the combat state of one of the participant changed in the current battle.  Usually, the
 * change occurred to the active target, but all changes to combat states of any target will fire this event.
 *
 * @param event
 */    
    public void combatStateChange(ChangeEvent event);
    
/** Processing state of BattleEngine changed
 * Indicates the battleEngine has either started or stopped processing events.  
 * Check the Battle.getCurrentBattle().isProcessing() for the current state of the battleEngine.
 * @param event
 */    
    public void processingChange(BattleChangeEvent event);
}
