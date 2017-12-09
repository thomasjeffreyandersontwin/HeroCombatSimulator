/*
 * EligibleModel.java
 *
 * Created on September 14, 2000, 5:55 PM
 */

package champions;

import champions.event.SegmentAdvancedEvent;
import champions.event.SequenceChangedEvent;
import champions.event.TargetSelectedEvent;
import champions.interfaces.BattleListener;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.ListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataListener;

import VirtualDesktop.BattleSequenceUpdateExporter;


/**
 *
 * @author  unknown
 * @version
 */
public class EligibleModel extends AbstractListModel
implements ListModel, BattleListener {
    
    public BattleSequence eligibleCombatants;
    
    /** Marks List as invalid. */
    protected boolean invalid = false;
    
    /** Holds value of property displayNoneWhenEmpty. */
    private boolean displayNoneWhenEmpty = true;
    
    /** Caches the list size */
    private int listSize;
    
    /** Creates new EligibleModel */
    public EligibleModel() {
        eligibleCombatants = new BattleSequence();
        
        Battle.addBattleListener(this);
    }
    
    /** Returns the length of the list.
     */
    public int getSize() {
        listSize = 0;
        if ( eligibleCombatants != null ) {
            listSize = eligibleCombatants.size();
        }
        
        if ( listSize == 0 && displayNoneWhenEmpty == true ) {
            return 1;
        }
        
        return listSize;
        
    }
    /** Returns the value at the specified index.
     */
    public Object getElementAt(int index) {
        if ( listSize == 0 && displayNoneWhenEmpty == true ) {
            return "None";
        }
        else if ( eligibleCombatants == null ) {
            return null;
        }
        else {
            BattleSequencePair bsp = eligibleCombatants.get(index);
            Object o = bsp.getTarget();
            
            if ( o instanceof BattleEvent ) {
                final BattleEvent be = (BattleEvent)o;
                
                if ( be.getType() == BattleEvent.ACTION ) {
                    return be.getAction().getValue("NAME") + "(" + be.getTimeParameter() + ")";
                }
                if (  be.getActivationInfo().getState().equals("TRIGGER") ) {
                    String s = be.getSource().getName() + "'s " + be.getAbility().getName() + "(Delayed)";
                    if ( be.getAbility().getBooleanValue("Ability.ISINDEPENDENT") ) {
                        Action action = new AbstractAction( s ) {
                            public void actionPerformed(ActionEvent e) {
                                if ( Battle.currentBattle != null ) {
                                    Battle.currentBattle.addEvent(be);
                                }
                            } 
                        };
                        
                        return action;
                    }
                    else {
                        // Not independent
                        if ( Battle.currentBattle != null && Battle.currentBattle.getActiveTarget() == be.getSource() ) {
                            Action action = new AbstractAction( s ) {
                                public void actionPerformed(ActionEvent e) {
                                    if ( Battle.currentBattle != null ) {
                                        Battle.currentBattle.addEvent(be);
                                    }
                                }
                            };
                            return action;
                        }
                        else {
                            return s;
                        }
                    }
                }
                else if(  be.getActivationInfo().getState().equals("DELAY") )  {
                    String dexString;
                    if ( be.getDex() == -1 ) {
                        dexString = ", EoS";
                    }
                    else {
                        dexString = ", Dex: " + Integer.toString(be.getDex());
                    }
                    String s = be.getSource().getName() + "'s " + be.getAbility().getName() + "(" + be.getTimeParameter().toString() + dexString + ")";
                    return s;
                }
                else {
                    return "Error";
                }
            }
            else if ( o instanceof Target ) {
                final Target t = (Target) o;
                
                String s = t.getName();
                if ( t.getCombatState() == CombatState.STATE_ACTIVE ) {
                    s = s.concat( "(Ready)" );
                }
                else if ( t.getCombatState() == CombatState.STATE_FIN ) {
                    s = s.concat( "(Finished)" );
                }
                else if ( t.getCombatState() == CombatState.STATE_HELD ) {
                    s = s.concat( "(Holding)" );
                }
                else if ( t.getCombatState() == CombatState.STATE_HALFHELD ) {
                    s = s.concat( "(Half Holding)" );
                }
                else if ( t.getCombatState() == CombatState.STATE_ABORTING ) {
                    s = s.concat( "(Aborting)" );
                }
                
                Action action = new AbstractAction( s ) {
                    public void actionPerformed(ActionEvent e) {
                        if ( Battle.currentBattle != null ) {
                            BattleEvent be = new BattleEvent(BattleEvent.ACTIVE_TARGET, t);
                            Battle.currentBattle.addEvent(be);
                        }
                    }
                };
                
                action.setEnabled( Battle.currentBattle.getTime().isTurnEnd() == false);
                
                return action;
            }
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
    
    
    public void battleSegmentAdvanced(SegmentAdvancedEvent e) {
        updateModel();
    }
    
    
    public void battleSequenceChanged(SequenceChangedEvent e) {
        updateModel();
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
    
    public void updateModel() {
        if ( Battle.getCurrentBattle() != null  && Battle.getCurrentBattle().isProcessing() == false ) {
            if ( Battle.currentBattle != null ) {
                if ( eligibleCombatants != null ) {
                    fireIntervalRemoved(this, 0, eligibleCombatants.size());
                }
                
                eligibleCombatants = Battle.currentBattle.getSequencer().getBattleEligible(eligibleCombatants);
                
                fireIntervalAdded(this, 0, eligibleCombatants.size());
                invalid = false;
            }
        }
        else {
            // Mark things as invalid and update later
            invalid = true;
        }

        new BattleSequenceUpdateExporter().ExportBattleSequence(eligibleCombatants, "EligibleCombatants");
    }
    
    public void processingChange(BattleChangeEvent event) {
        if ( invalid && Battle.getCurrentBattle() != null  && Battle.getCurrentBattle().isProcessing() == false ) {
            updateModel();
        }
    }
    
    /** Getter for property displayNoneWhenEmpty.
     * @return Value of property displayNoneWhenEmpty.
     */
    public boolean isDisplayNoneWhenEmpty() {
        return displayNoneWhenEmpty;
    }
    
    /** Setter for property displayNoneWhenEmpty.
     * @param displayNoneWhenEmpty New value of property displayNoneWhenEmpty.
     */
    public void setDisplayNoneWhenEmpty(boolean displayNoneWhenEmpty) {
        this.displayNoneWhenEmpty = displayNoneWhenEmpty;
    }
    
}