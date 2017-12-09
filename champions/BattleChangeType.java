/*
 * BattleChangeType.java
 *
 * Created on January 29, 2008, 8:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions;

/**
 *
 * @author twalker
 */
public enum BattleChangeType {
    
    TARGET_ADDED,
    TARGET_REMOVED,
    ROSTER_ADDED,
    ROSTER_REMOVED,
    ROSTER_CHANGED,
    TRIGGER_CHANGE, 
    TARGETING_EVENT,
    UNDO_COMPLETE,
    REDO_COMPLETE,
    BATTLE_CHANGED,
    EFFECT_REMOVED,
    STARTED_PROCESSING_EVENTS,
    FINISHED_PROCESSING_EVENTS;
   
}
