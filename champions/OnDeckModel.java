/*
 * OnDeckModel.java
 *
 * Created on September 14, 2000, 6:48 PM
 */

package champions;

import champions.event.SegmentAdvancedEvent;
import champions.event.SequenceChangedEvent;
import champions.event.TargetSelectedEvent;
import champions.interfaces.BattleListener;

import java.io.FileWriter;
import java.io.IOException;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataListener;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Character.CharacterJSONExporter;
import VirtualDesktop.Roster.BattleSequenceUpdateExporter;


/**
 *
 * @author  unknown
 * @version
 */
public class OnDeckModel extends AbstractListModel
implements ListModel, BattleListener {

    /** Marks List as invalid. */
    protected boolean invalid = false;
    
    private BattleSequence onDeckCombatants;
    /** Holds value of property lookAhead. */
    private int lookAhead;
    /** Creates new OnDeckModel */
    public OnDeckModel() {

        onDeckCombatants = null;
        lookAhead = 20;

        Battle.addBattleListener(this);
    }

    /** Returns the length of the list.
     */
    public int getSize() {
        if ( onDeckCombatants == null )
        return 0;
        else
        return (int)Math.min ( onDeckCombatants.size(), lookAhead) ;
    }
    /** Returns the value at the specified index.
     */
    public Object getElementAt(int index) {
        if ( onDeckCombatants == null )
        return null;
        else {
            String s = null;
            BattleSequencePair bsp = onDeckCombatants.get(index);
            
          /*  Object o = bsp.getTarget();
            if ( o instanceof BattleEvent ) {
                s = "Delayed Event: " + ((BattleEvent)o).getAbility().getName();
            }
            else if ( o instanceof Target ) {
                s = ((Target)o).getName();
            } */

           // return s;
            return bsp;
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


    public void battleSegmentAdvanced(SegmentAdvancedEvent e) {
        updateModel();
    }

    public void battleSequenceChanged(SequenceChangedEvent e) {
       updateModel();
    }
    /** Getter for property lookAhead.
     * @return Value of property lookAhead.
     */
    public int getLookAhead() {
        return lookAhead;
    }
    /** Setter for property lookAhead.
     * @param lookAhead New value of property lookAhead.
     */
    public void setLookAhead(int lookAhead) {
        this.lookAhead = lookAhead;
    }
    public void battleTargetSelected(TargetSelectedEvent e) {
    }
    public void stateChanged(BattleChangeEvent e) {
        updateModel();
    }

    public void eventNotification(ChangeEvent e) {       
    }
    
    public void combatStateChange(ChangeEvent e) {  
        updateModel();
    }
        
    public void processingChange(BattleChangeEvent event) {
        if ( invalid && Battle.getCurrentBattle() != null  && Battle.getCurrentBattle().isProcessing() == false ) {
            updateModel();
        }
    }

    public void updateModel() {
        if ( Battle.getCurrentBattle() != null  && Battle.getCurrentBattle().isProcessing() == false ) {
        if ( Battle.currentBattle != null ) {
            if ( onDeckCombatants != null ) {
                fireIntervalRemoved(this, 0, onDeckCombatants.size());
            }

            onDeckCombatants = Battle.currentBattle.getSequencer().getBattleSequence(lookAhead+1);
       //     if ( onDeckCombatants.size() >= 1 && onDeckCombatants.get(0).getTarget() == Battle.currentBattle.getActiveTarget() ) {
        //        onDeckCombatants.remove(0);
         //   }

            fireIntervalAdded(this, 0, onDeckCombatants.size());
        }
        invalid = false;
        }
        else {
            invalid = true;
        }
        
        new BattleSequenceUpdateExporter().ExportBattleSequence(onDeckCombatants, "OnDeckCombatants");
        for (int i = 0; i < onDeckCombatants.size(); i++) {
        	if(onDeckCombatants.get(i).getTarget().getClass()== Target.class) {
        		Target character = (Target) onDeckCombatants.get(i).getTarget();
				new CharacterJSONExporter().ExportCharacterStates(character);
        }
		}
    }

}