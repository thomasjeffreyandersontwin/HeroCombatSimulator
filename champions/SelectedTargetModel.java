/*
 * EligibleModel.java
 *
 * Created on September 14, 2000, 5:55 PM
 */

package champions;

import javax.swing.ListModel;
import champions.interfaces.BattleListener;
import java.util.*;
import javax.swing.event.*;
import champions.event.*;
import champions.*;
import javax.swing.*;
/**
 *
 * @author  unknown
 * @version
 */
public class SelectedTargetModel extends AbstractListModel
implements ListModel, BattleListener {

    /** Creates new EligibleModel */
    public SelectedTargetModel() {
        Battle.addBattleListener(this);
    }

    /** Returns the length of the list.
     */
    public int getSize() {
        if ( Battle.currentBattle != null && Battle.currentBattle.getActiveTarget() != null ) {
            return 1;
        }
        else {
            return 0;
        }
    }
    /** Returns the value at the specified index.
     */
    public Object getElementAt(int index) {
        if ( Battle.currentBattle != null ) {
            Target t = Battle.currentBattle.getActiveTarget();
            
            StringBuffer s = new StringBuffer(t.getName());
            if ( t.isPostTurn() == true ) {
                s.append( "(Post Turn Recover)" );
            }
            else if ( t.getCombatState() == CombatState.STATE_ACTIVE ) {
                s.append( "(Full Action)" );
            }
            else if ( t.getCombatState() == CombatState.STATE_HALFFIN ) {
                s.append( "(Half Action)" );
            }
            else if ( t.getCombatState() == CombatState.STATE_FIN ) {
                s.append( "(Finished)" );
            }
            else if ( t.getCombatState() == CombatState.STATE_HELD ) {
                s.append( "(Holding)" );
            }
            else if ( t.getCombatState() == CombatState.STATE_HALFHELD ) {
                s.append( "(Half Holding)" );
            }
            else if ( t.getCombatState() == CombatState.STATE_ABORTING ) {
                s.append( "(Aborting)" );
            }
            else if ( t.getCombatState() == CombatState.STATE_ABORTED ) {
                s.append( "(Aborted)" );
            }
            else if ( t.getCombatState() == CombatState.STATE_INACTIVE ) {
                s.append( "(Inactive)" );
            }
            else if ( t.getCombatState() == CombatState.STATE_DELAYED ) {
                s.append( "(Activating Ability)" );
            }
            
            if (t.isEgoPhase() ) {
                s.append (" EGO Powers Only");
            }
            return s.toString();
        }
        else {
            return null;
        }
    }
    /** Add a listener to the list that's notified each time a change
     * to the data model occurs.
     * @param l the ListDataListener
     */
    public void addListDataListener(ListDataListener l) {
        listenerList.add(ListDataListener.class,l);
    }
    /** Remove a listener from the list that's notified each time a
     * change to the data model occurs.
     * @param l the ListDataListener
     */
    public void removeListDataListener(ListDataListener l) {
        listenerList.remove(ListDataListener.class,l);
    }



    public void battleTargetSelected(TargetSelectedEvent e) {
        fireContentsChanged(this, 0, 0);
    }

    public void battleSegmentAdvanced(SegmentAdvancedEvent e) {

    }

    public void battleSequenceChanged(SequenceChangedEvent e) {

    }

    public void stateChanged(BattleChangeEvent e) {
        fireContentsChanged(this, 0, 0);
    }
    public void eventNotification(ChangeEvent e) {
        //fireContentsChanged(this, 0, 0);
    }
    
    public void combatStateChange(ChangeEvent e) {    
        fireContentsChanged(this, 0, 0);
    }
    
        public void processingChange(BattleChangeEvent event) {
        
    }

}