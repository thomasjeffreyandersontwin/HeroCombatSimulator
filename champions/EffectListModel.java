/*
 * EffectListModel.java
 *
 * Created on November 6, 2000, 5:03 PM
 */

package champions;

import champions.event.SegmentAdvancedEvent;
import champions.event.SequenceChangedEvent;
import champions.event.TargetSelectedEvent;
import champions.exception.BattleEventException;
import champions.interfaces.BattleListener;
import tjava.ContextMenuListener;
import tjava.HTMLColor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.ListModel;
import javax.swing.event.ChangeEvent;




/**
 *
 * @author  unknown
 * @version
 */
public class EffectListModel extends AbstractListModel
implements ListModel, PropertyChangeListener, BattleListener {
    
    /** Holds value of property target. */
    protected Target target;
    /** Holds value of property filter. */
    protected int filter;
    
    /** Holds effect currently listened to.
     * effectHash contains a list of all effects currently being listened to.
     * Currently, all effects are removed and then readded whenever an effect change occurs.
     */
    protected HashSet effectHash = new HashSet();
    
    /** Cache count of elements in list. */
    protected int totalElements;
    
    /** Cached array of elements in list. */
    protected Vector effectVector = new Vector();
    
    /** Holds value of property displayNoneWhenEmpty. */
    private boolean displayNoneWhenEmpty;
    
    protected int invalidStart;
    protected int invalidEnd;
    protected boolean invalid;
    protected String invalidReason = null;
    
    /** Creates new EffectListModel */
    public EffectListModel() {
        setFilter( Effect.NORMAL );
        
        invalidStart = Integer.MAX_VALUE;
        invalidEnd = Integer.MIN_VALUE;
        invalid = false;
        invalidReason = null;
        
        Battle.addBattleListener(this);
    }
    
    public EffectListModel(int filter) {
        this();
        setFilter( filter);
    }
    
    public EffectListModel(Target target) {
        this();
        setFilter( Effect.NORMAL );
        setTarget(target);
    }
    
    public int getSize() {
        Effect effect;
        boolean hidden, critical;
        
        totalElements = 0;
        
        if ( target != null ) {
            // First determine current size from Target effect lists sizes
            totalElements = getEffectCount(filter);
        }
        
        if ( totalElements == 0 && displayNoneWhenEmpty == true ) return 1;
        return totalElements;
    }
    
    protected int getEffectCount(int type) {
        if ( type == Effect.NORMAL ) {
            return getEffectCount(Effect.CRITICAL) + getEffectCount(Effect.NONCRITICAL);
        }
        else if ( type == Effect.ALL ) {
            return target.getEffectCount();
        }
        else {
            int effectCount = 0;
            java.util.List<Effect> list = target.getEffects();
            for(Effect e : list ) {
                //Effect e = target.getEffect(i);
                switch ( type ) {
                    case Effect.HIDDEN:
                        if ( e.isHidden() ) effectCount++;
                        break;
                    case Effect.CRITICAL:
                        if ( e.isCritical() ) effectCount++;
                        break;
                    case Effect.NONCRITICAL:
                        if ( !e.isCritical() && !e.isHidden()) effectCount++;
                        break;
                }
            }
            return effectCount;
        }
    }
    
    protected Effect getEffect(int index, int type) {
        int effectCount = 0;
        int size = target.getEffectCount();
        for(int i = 0; i < size; i++) {
            Effect e = target.getEffect(i);
            if ( type == Effect.HIDDEN && e.isHidden() ) {
                if ( index == effectCount++ ) return e;
            }
            else if ( type == Effect.CRITICAL && e.isCritical() ) {
                if ( index == effectCount++ ) return e;
            }
            else if ( type == Effect.NONCRITICAL && !e.isCritical() && !e.isHidden() ) {
                if ( index == effectCount++ ) return e;
            }
        }
        return null;
    }
    
    /** Returns the value at the specified index.
     */
    public Object getElementAt(int index) {
        Object o;
        Effect effect = null;
        int count;
        
        if ( totalElements == 0 && displayNoneWhenEmpty == true ) return "None";
        
        if ( target != null ) {
            switch ( filter ) {
                case Effect.NORMAL:
                    count = getEffectCount(Effect.CRITICAL);
                    if ( index < count ) {
                        effect = getEffect(index, Effect.CRITICAL);
                    }
                    else {
                        // It is an effect from the noncritical list.  Offset value by count
                        effect = getEffect(index-count, Effect.NONCRITICAL);
                    }
                    break;
                case Effect.ALL:
                    //effect =   (Effect)target.getIndexedValue(index, "Effect","EFFECT");
                    effect = target.getEffect(index);
                    break;
                default:
                    effect = getEffect(index,filter);
                    break;
            }
            
            if ( effect != null ) {
                final Effect theEffect = effect;
                return new EffectListModel.EffectAction( effect.getName(), effect );
            }
        }
        return null;
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
        if ( this.target != null ) {
            this.target.removePropertyChangeListener("Effect.INDEXSIZE",this);
        }
        
        this.target = target;
        triggerContentsChanged("setTarget()",-1,-1);
        adjustEffectListeners();
        
        if ( this.target != null ) {
            this.target.addPropertyChangeListener("Effect.INDEXSIZE",this);
        }
    }
    
    public void propertyChange(PropertyChangeEvent e) {
        if ( target != null ) {
            if ( e.getSource() == target && e.getPropertyName().equals( "Effect.INDEXSIZE") ) {
                triggerContentsChanged("INDEXSIZE change",0,getSize());
                adjustEffectListeners();
            }
            
            if ( e.getSource() instanceof Effect ) {
                if ( e.getPropertyName().equals("Effect.NAME") ) {
                    
                    if ( target.hasEffect( (Effect)e.getSource() ) ) {
                        triggerContentsChanged("Effect.NAME change", 0,getSize());
                    }
                }
            }
        }
    }
    
    private void adjustEffectListeners() {
        if ( target == null ) return;
        
        int index,count;
        Effect effect;
        
        // Remove all effect in effectHash
        Iterator i = effectHash.iterator();
        while ( i.hasNext() ) {
            effect = (Effect)i.next();
            effect.removePropertyChangeListener("Effect.NAME", this);
        }
        effectHash.clear();
        
        // Add all the effect to the effectHash and listen to them
        //count = target.getIndexedSize("Effect");
        List<Effect> list = target.getEffects();
        for(Effect e : list) {
            //effect = (Effect)target.getIndexedValue(index, "Effect", "EFFECT");
            //effect = target.getEffect(index);
            if ( e != null ) { // This might be null if index size changed.
                e.addPropertyChangeListener("Effect.NAME", this);
                effectHash.add(e);
            }
        }
    }
    
    
    /** Getter for property filter.
     * @return Value of property filter.
     */
    public int getFilter() {
        return filter;
    }
    /** Setter for property filter.
     * @param filter New value of property filter.
     */
    public void setFilter(int filter) {
        this.filter = filter;
        triggerContentsChanged("setFilter()",-1,-1);
    }
    
    protected void triggerContentsChanged(String reason) {
        triggerContentsChanged(reason, Integer.MAX_VALUE,Integer.MIN_VALUE);
    }
    
    protected void triggerContentsChanged(String reason, int start, int end) {
        if ( invalidStart > start ) invalidStart = start;
        if ( invalidEnd < end ) invalidEnd = end;
        if ( invalidReason == null ) invalidReason = reason;
        
        if ( Battle.getCurrentBattle() != null && Battle.getCurrentBattle().isProcessing() == false ) {
            if ( invalidStart <= invalidEnd ) {
                fireContentsChanged(this, invalidStart, invalidEnd);
              //  System.out.println("Firing Contents Changed (" + invalidReason + "," + Integer.toString(invalidStart) + "," 
              //  + Integer.toString(invalidEnd) + ")");
            }
            invalidStart = Integer.MAX_VALUE;
            invalidEnd = Integer.MIN_VALUE;
            invalid = false;
            invalidReason = null;
        }
        else {
           // System.out.println("Queueing Invalid Update");
            invalid = true;
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
          //  System.out.println("Firing Invalid Update");
            triggerContentsChanged("ProcessingChange");
        }
    }
    
    public static class EffectAction extends AbstractAction implements ContextMenuListener, HTMLColor {
        private Effect effect;
        public EffectAction(String name, Effect effect) {
            super(name);
            this.effect = effect;
        }
        
        public void actionPerformed(ActionEvent evt) {
            EffectDetail ed = new EffectDetail(effect);
            ed.showEffectDetail(null);
        }
        
        public boolean invokeMenu(JPopupMenu popup,Component inComponent,Point inPoint) {
            popup.add ( new JMenuItem( new AbstractAction( "Effect Detail..." ) {
                public void actionPerformed(ActionEvent evt) {
                    EffectDetail ed = new EffectDetail(effect);
                    ed.showEffectDetail(null);
                }
            }));
            if ( Battle.debugLevel >= 1 ) {
                popup.add ( new JMenuItem( new AbstractAction( "Debug Effect..." ) {
                    public void actionPerformed(ActionEvent evt) {
                        effect.debugDetailList( "Debug: " + effect.getName() );
                    }
                }));
            }
            popup.add ( new JMenuItem( new AbstractAction( "Remove Effect..." ) {
                public void actionPerformed(ActionEvent evt) {
                    if ( Battle.currentBattle == null || effect == null ) return;
                    if ( JOptionPane.showConfirmDialog( null, "Are you sure you want to remove the effect \"" + effect.getName() + "\" from " + effect.getTarget().getName() + "?"
                    , "Remove Effect", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                        return;
                    }
                    
                    if ( Battle.currentBattle.isStopped() == false ) {
                        effect.triggerRemoval();
                    }
                    else {
                        BattleEvent be = new BattleEvent(BattleEvent.REMOVE_EFFECT, effect, effect.getTarget());
                        try {
                            effect.removeEffect( be, effect.getTarget() );
                        } catch ( BattleEventException bee ) {
                            be.displayBattleError(bee);
                        }
                    }
                }
            }));
            return true;
        }
        
        public Effect getEffect() {
            return effect;
        }
        
        public Color getEnabledColor() {
            if ( effect != null )
                return effect.getEffectColor();
            else
                return null;
        }
        
        public Color getPressedColor() {
            return null;
        }
    }
}
