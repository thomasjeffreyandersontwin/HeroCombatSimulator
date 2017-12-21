/*
 * Teams.java
 *
 * Created on September 13, 2000, 9:54 PM
 */

package champions;

import java.io.BufferedWriter;
import champions.event.RosterAddEvent;
import champions.event.RosterRemoveEvent;
import champions.event.SegmentAdvancedEvent;
import champions.event.SequenceChangedEvent;
import champions.event.TargetSelectedEvent;
import champions.event.TargetingEvent;
import champions.exception.BattleEventException;
import champions.exceptionWizard.ExceptionWizard;
import champions.interfaces.AbilityList;
import champions.interfaces.BattleListener;
import champions.interfaces.RosterListener;
import champions.interfaces.Sequencer;
import champions.interfaces.TargetingListener;
import champions.interfaces.Undoable;
import champions.undoableEvent.CombatStateUndoable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import tjava.Destroyable;

/**
 *
 * @author  unknown
 * @version
 */
public class Battle extends Object implements RosterListener, TargetingListener, PropertyChangeListener, Serializable, Destroyable {
    static final long serialVersionUID = -1368581058230247368L;
    
    private Set<Roster> rosters;
    private TargetList obstructions;
    // private Chronometer time;
    transient private Sequencer seq;
    private static List<WeakReference<BattleListener>> listenerList = new ArrayList<WeakReference<BattleListener>>();
    private List<BattleEvent> completedEventList = new ArrayList<BattleEvent>();
    private Set<BlockEntry> blockList = new HashSet<BlockEntry>();
    private Set<BattleEvent> delayedEventList = new HashSet<BattleEvent>();
    private Set<Target> specialTargets = new HashSet<Target>();
    private TargetList specialTargetList = new TargetList("Special Targets");
    private int completedEventPosition = 0;
    private int completedEventMaxRedo = 0;
    
    /** Holds value of property activeTarget. */
    public Target activeTarget;
    /** Utility field used by bound properties. */
    transient private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    
    /** Holds value of property battleEngine. */
    transient private BattleEngine battleEngine;
    /** Holds value of property frame. */
    transient private JFrame frame;
    /** Holds GM Target */
    private GMTarget gmTarget;
    /** Creates new Teams */
    
    /** Holds the purged messages. */
    protected Vector purgedMessageVector = new Vector();
    
    /** Holds Recently used Generic Abilities. */
    protected AbilityList recentGenericAbilityList;
    
    public static Battle currentBattle = null;
    static public int debugLevel = 0;
    
    static public final int DEBUG = 0;
    
    /** Holds value of property time. */
    private Chronometer time;
    /** Holds value of property stopped. */
    private boolean stopped = true;
    /** Holds value of property file. */
    transient private File file = null;
    
    //private List actionVector = new ArrayList();
    
    private AbilityList defaultAbilities = null;
    private static Ability recovery = null;
    private static Ability stunRecovery = null;
    private static Ability pass = null;
    
    public Battle() {
        rosters = new LinkedHashSet();
        obstructions = new TargetList("Obstructions");
        time = new Chronometer(12);
        
        seq = new Sequencer5(this);
        
        SplashScreen.advangeProgress();
        
        activeTarget = null;
        
        battleEngine = new BattleEngine("BattleEngine",this);
        
        // Setup the Special Targets
        Target t;
        gmTarget = new GMTarget();
        addSpecialTarget(gmTarget);
        
        t = new HexTarget(false);
        addSpecialTarget( t );
        
        t = new HexTarget(true);
        t.setName("Adjacent Hex");
        addSpecialTarget(t);
        
        // Initialize the Recent Generic Abilities list
        recentGenericAbilityList = new DefaultAbilityList();
        
        SplashScreen.advangeProgress();
        
        setCurrentBattle(this);
        reset();
        
        Integer i = (Integer)Preferences.getPreferenceList().getParameterValue("DebugLevel");
        if ( i == null ) {
            debugLevel = 0;
        } else {
            debugLevel = i.intValue();
        }
        
    }
    
    public void reset() {
        //msg("Battle Reset",BattleMessageEvent.MSG_UTILITY);
        time.setTime(12);
        Iterator<Target> i = getCombatants().iterator();
        while ( i.hasNext() ) {
            Target t = i.next();
            t.setCombatState(CombatState.STATE_FIN);
            t.setPrephaseDone(false);
        }
        fireSegmentAdvanced();
    }
    
    public Set<Target> getCombatants() {
        Set tempSet = new HashSet<Target>();
        Iterator<Roster> i = rosters.iterator();
        while ( i.hasNext()) {
            Iterator<Target> j = i.next().getCombatants().iterator();
            while ( j.hasNext() ) {
                tempSet.add(j.next());
            }
        }
        return tempSet;
    }
    
    public TargetList getTargetList(boolean flat) {
        TargetList tl = new TargetList();
        if ( flat ) {
            Iterator<Roster> i = rosters.iterator();
            while ( i.hasNext()) {
                Iterator<Target> j = i.next().getCombatants().iterator();
                while ( j.hasNext() ) {
                    tl.addTarget(  j.next() );
                }
            }
        } else {
            Iterator i = rosters.iterator();
            while ( i.hasNext()) {
                Roster r = (Roster)i.next();
                TargetList tl2 = new TargetList(r.getName());
                Iterator<Target> j = r.getCombatants().iterator();
                while ( j.hasNext() ) {
                    tl2.addTarget(  j.next() );
                }
                tl.addSublist(tl2);
            }
        }
        return tl;
    }
    
    public Set<Roster> getRosters() {
        return rosters;
    }
    
    public Roster findRoster(String name) {
        Iterator i = rosters.iterator();
        while ( i.hasNext() ) {
            Roster r = (Roster)i.next();
            if ( r.getName().equals(name) ) return r;
        }
        return null;
    }
    
    public void addRoster(Roster r) {
        if ( r == null ) return;
        
        Vector removeVector = new Vector();
        Vector activateVector = new Vector();
        
        if ( ! rosters.contains(r) ) {
            rosters.add(r);
            r.setBattle(this);
            r.addRosterListener(this);
            Iterator<Target> i = r.getCombatants().iterator();
            while ( i.hasNext() ) {
                Target t = i.next();
                
                if ( checkUnique(t) == false ) {
                    removeVector.add(t);
                } else {
                    if ( isStopped() == false && t.isCombatActive() && time.isActivePhase( t.getCurrentStat("SPD") )) {
                        t.setCombatState(CombatState.STATE_ACTIVE);
                        t.setPrephaseDone(false);
                    }
                    
                    t.addPropertyChangeListener(this);
                    
                    if ( isStopped() == false ) {
                        activateVector.add(t);
                    }
                    
                    if ( t.isObstruction() ) {
                        obstructions.addTarget(t);
                    }
                    
                    t.clearBattleStats();
                }
            }
            
            i = removeVector.iterator();
            while ( i.hasNext() ) {
                r.remove(  i.next() );
            }
            
//            i = activateVector.iterator();
//            while ( i.hasNext() ) {
//                (i.next()).activateNormallyOn();;
//            }
        }
        
        fireChangeEvent( BattleChangeType.ROSTER_ADDED, r );
        
        if ( isStopped() == false ) {
            addEvent( new ConfigureBattleBattleEvent(r, true) );
        }
    }
    
    public boolean checkUnique(Target newTarget) {
        Iterator<Target> i = getCombatants().iterator();
        while ( i.hasNext() ) {
            Target oldTarget = i.next();
            
            if ( newTarget != oldTarget && newTarget.getName().equals( oldTarget.getName() ) ) {
                // There is a duplicate name
                ChangeNameDialog cnd= new ChangeNameDialog(null, newTarget.getName(), getUniqueName( newTarget.getName() ) );
                cnd.setVisible(true);
                if ( cnd.getInputText() != null ) {
                    newTarget.setName( cnd.getInputText() );
                    newTarget.setFile(null);
                    return true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean checkUnique(String oldName) {
        Iterator<Target> i = getCombatants().iterator();
        while ( i.hasNext() ) {
            Target oldTarget = i.next();
            
            if ( oldName.equals( oldTarget.getName() ) ) {
                // There is a duplicate name
                return false;
            }
        }
        return true;
    }
    
    public String getUniqueName(String oldName) {
        String newName, baseName;
        
        int index = 2;
        
        
        if ( ChampionsMatcher.matches( "(.*) \\(([0-9]*)\\)", oldName ) ) {
            baseName = ChampionsMatcher.getMatchedGroup(1);
            index = ChampionsMatcher.getIntMatchedGroup(2);
        } else {
            baseName = oldName;
        }
        
        newName = oldName;
        while ( checkUnique( newName )  == false ) {
            newName = baseName + " (" + Integer.toString( index++ ) + ")";
        }
        
        return newName;
    }
    
    public void removeRoster(Roster r) {
        rosters.remove(r);
        r.setBattle( null );
        r.removeRosterListener(this);
        Iterator<Target> i = r.getCombatants().iterator();
        while ( i.hasNext() ) {
            Target t = i.next();
            t.removePropertyChangeListener(this);
            
            if ( t.isObstruction() ) {
                obstructions.removeTarget(t);
            }
        }
        
        addEvent( new BattleEvent(BattleEvent.ADVANCE_TIME,false));
        fireChangeEvent(BattleChangeType.ROSTER_REMOVED, r);
    }
    
    public void removeAllRosters() {
        Iterator i = rosters.iterator();
        while ( i.hasNext() ) {
            removeRoster( (Roster) i.next() );
            // !!! This may not actually work !!!
        }
    }
    
    public Sequencer getSequencer() {
        return seq;
    }
    
    /**
     *  Adds a <code>Battle</code> listener.
     *
     *  @param l  the <code>BattleListener</code> to add
     */
    public static void addBattleListener(BattleListener l) {
        if ( isBattleListener(l) == false ) {
            WeakReference<BattleListener> ref = new WeakReference<BattleListener>(l);
            listenerList.add(ref);
        }
    }
    
    /**
     * Removes a <code>Battle</code> listener.
     *
     * @param l  the <code>BattleListener</code> to remove
     */
    public static void removeBattleListener(BattleListener l) {
        //listenerList.remove(BattleListener.class,l);
        Iterator<WeakReference<BattleListener>> it = listenerList.iterator();
        while(it.hasNext()) {
            WeakReference<BattleListener> ref = it.next();
            BattleListener bl = ref.get();
            if ( l == bl ) {
                it.remove();
                break;
            }
        }
    }
    
    protected static boolean isBattleListener(BattleListener l) {
        Iterator<WeakReference<BattleListener>> it = listenerList.iterator();
        while(it.hasNext()) {
            WeakReference<BattleListener> ref = it.next();
            BattleListener bl = ref.get();
            if ( l == bl ) {
                return true;
            }
        }
        return false;
    }
    
    /*
    public static void addBattleMessageListener(BattleMessageListener l) {
    listenerList.add(BattleMessageListener.class,l);
    }
     
     
    public static void removeBattleMessageListener(BattleMessageListener l) {
    listenerList.remove(BattleMessageListener.class,l);
    } */
    
    static protected void fireSegmentAdvanced() {
        if ( ! SwingUtilities.isEventDispatchThread() ) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    
                    fireSegmentAdvanced();
                }
            });
            
            return;
        }
        if ( DEBUG >= 1 ) System.out.println( "Battle: firedSegmentAdvanced");
        //msg ( "Segment Advanced to " + time.toString(), BattleMessageEvent.MSG_SEGMENT);
        SegmentAdvancedEvent e = null;
        
        Iterator<WeakReference<BattleListener>> it = listenerList.iterator();
        while(it.hasNext()) {
            WeakReference<BattleListener> ref = it.next();
            BattleListener bl = ref.get();
            if ( bl == null ) {
                it.remove(); // remove dead references...
            }
            else {
                // Lazily create the event:
                if (e == null)
                    e = new SegmentAdvancedEvent(Battle.currentBattle, Battle.currentBattle.getTime());
                bl.battleSegmentAdvanced(e);
            }
        }
    }
    
    static protected void fireTargetSelected() {
        if ( ! SwingUtilities.isEventDispatchThread() ) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    fireTargetSelected();
                }
            });
            
            return;
        }
        
        if ( DEBUG >= 1 ) System.out.println( "Battle: firedTargetSelected");
        TargetSelectedEvent e = null;
        
        Iterator<WeakReference<BattleListener>> it = listenerList.iterator();
        while(it.hasNext()) {
            WeakReference<BattleListener> ref = it.next();
            BattleListener bl = ref.get();
            if ( bl == null ) {
                it.remove(); // remove dead references...
            }
            else {
                // Lazily create the event:
                if (e == null)
                    e = new TargetSelectedEvent(Battle.currentBattle, Battle.currentBattle.getActiveTarget());
                bl.battleTargetSelected(e);
            }
        }
    }
    
    static protected void fireSequenceChanged() {
        if ( ! SwingUtilities.isEventDispatchThread() ) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    fireSequenceChanged();
                }
            });
            
            return;
        }
        if ( DEBUG >= 1 ) System.out.println( "Battle: firedSequenceChanged");
        SequenceChangedEvent e = null;
        
        Iterator<WeakReference<BattleListener>> it = listenerList.iterator();
        while(it.hasNext()) {
            WeakReference<BattleListener> ref = it.next();
            BattleListener bl = ref.get();
            if ( bl == null ) {
                it.remove(); // remove dead references...
            }
            else {
                // Lazily create the event:
                if (e == null)
                    e = new SequenceChangedEvent(Battle.currentBattle);
                bl.battleSequenceChanged(e);
            }
        }
    }
    
    /** Getter for property activeTarget.
     * @return Value of property activeTarget.
     */
    public Target getActiveTarget() {
        return activeTarget;
    }
    /** Setter for property activeTarget.
     * @param activeTarget New value of property activeTarget.
     */
    
    
    
    
    
    
    
    
    
    public void setSelectedTarget(Target activeTarget) {
        Target oldSelectedTarget = this.activeTarget;
        this.activeTarget = activeTarget;
        
        VirtualDesktop.Legacy.MessageExporter.exportActiveCharacter(activeTarget);
        //propertyChangeSupport.firePropertyChange ("activeTarget", oldSelectedTarget, activeTarget);
        fireTargetSelected();
        
        
    }
    
    public void rosterAdd(RosterAddEvent e) {
    	Target t = e.getTarget();
        Roster r= e.getTarget().getRoster();
        if ( checkUnique(t) == false ) {
            ((Roster)e.getSource()).remove(t);
            return;
        }
        
        if ( t.isCombatActive() && time.isActivePhase( t.getCurrentStat("SPD") )) {
            t.setCombatState(CombatState.STATE_ACTIVE);
        }
        
        if ( t.isObstruction() ) {
            obstructions.addTarget(t);
        }
        
        t.addPropertyChangeListener(this);
        if ( isStopped() == false ) t.activateNormallyOn();
        fireChangeEvent( BattleChangeType.TARGET_ADDED, t );
        
    }
    
    
    
    public void rosterRemove(RosterRemoveEvent e) {
        Target t = e.getTarget();
        Roster r= e.getTarget().getRoster();
        VirtualDesktop.Legacy.MessageExporter.exportEvent("RosterRemove", t, r);
        t.removePropertyChangeListener(this);
        
        if ( t.isObstruction() ) {
            obstructions.removeTarget(t);
        }
        
        fireChangeEvent( BattleChangeType.TARGET_REMOVED, t);
        
    }
    
    
    public void processEvents() {
        if ( ! battleEngine.isProcessing() ) battleEngine.startProcessing();
    }
    
    public boolean isProcessing() {
        return battleEngine.isProcessing();
    }
    
    public DetailListEditor dle = null;
    
    public AddBattleEventUndoable addEvent(BattleEvent be) {
        return addEvent(be,false);
    }
    
    public AddBattleEventUndoable addEvent(BattleEvent be, boolean first) {
        
        battleEngine.getBattleEventList().addEvent(be, first);
        boolean b=false;
       // if(be.getUndoableEventCount()>0 && be.getUndoableEvent(0) instanceof CombatStateUndoable )
        //{
        //	CombatStateUndoable u = (CombatStateUndoable) be.getUndoableEvent(0);
        //	b=u.getNewState().name().equals("STATE_ABORTING");
        //}
        if ( isStopped() == false && (b|| battleEngine.isProcessing() == false )) {
        	battleEngine.startProcessing();
        }
        
        return new Battle.AddBattleEventUndoable(be, first);
    }
    
    public void removeEvent(BattleEvent be) {
        battleEngine.getBattleEventList().removeEvent(be);
        // return new Battle.RemoveBattleEventUndoable(be);
    }
    
    /** Getter for property battleEngine.
     * @return Value of property battleEngine.
     */
    public BattleEngine getBattleEngine() {
        return battleEngine;
    }
    
    /** This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ( ! SwingUtilities.isEventDispatchThread() ) {
            System.out.println("Not in Event Thread (In Property Change)");
        }
        if ( evt.getSource() instanceof Target ) {
            Target c = (Target) evt.getSource();
            
            if ( evt.getPropertyName().startsWith("DEX") ) {
                BattleEvent be = battleEngine.getBattleEventList().peekAtFirstEvent();
                if ( be == null || be.getType() != BattleEvent.ADVANCE_TIME ) {
                    addEvent( new BattleEvent( BattleEvent.ADVANCE_TIME, false ) );
                }
            }
        }
    }
    
    public void targetingEvent(TargetingEvent e) {
        fireChangeEvent( BattleChangeType.TARGETING_EVENT, null );
    }
    
    static protected void fireChangeEvent(final BattleChangeType reason, final Object referenceObject) {
        if ( ! SwingUtilities.isEventDispatchThread() ) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    fireChangeEvent(reason, referenceObject);
                }
            });
            
            return;
        }
        if ( DEBUG >= 1 ) System.out.println( "Battle: fireChangeEvent for " + reason);
        BattleChangeEvent e = new BattleChangeEvent(Battle.currentBattle, reason, referenceObject);
        
        //Iterator<WeakReference<BattleListener>> it = listenerList.iterator();
        WeakReference[] listeners = listenerList.toArray(new WeakReference[0]);
        for(int i = 0; i < listeners.length; i++) {
            WeakReference<BattleListener> ref = (WeakReference<BattleListener>)listeners[i];
            BattleListener bl = ref.get();
            if ( bl != null ) {
                // System.out.println( "Firing chance event to: " + listeners[i+1].toString());
                bl.stateChanged(e);
            }
        }
    }
    
    static protected void fireNotificationEvent(final String reason) {
        if ( ! SwingUtilities.isEventDispatchThread() ) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    fireNotificationEvent(reason + "(invoked)");
                }
            });
            
            return;
        }
        if ( DEBUG >= 1 ) System.out.println( "Battle: fireNotificationEvent for " + reason);
        ChangeEvent e = new ChangeEvent(Battle.currentBattle);
        
        Iterator<WeakReference<BattleListener>> it = listenerList.iterator();
        while(it.hasNext()) {
            WeakReference<BattleListener> ref = it.next();
            BattleListener bl = ref.get();
            if ( bl == null ) {
                it.remove(); // remove dead references...
            }
            else {
                // System.out.println( "Firing chance event to: " + listeners[i+1].toString());
                bl.eventNotification(e);
            }
        }
        
    }
    
    static protected void fireCombatStateEvent(final String reason) {
        if ( Battle.currentBattle == null ) return;
        
        if ( ! SwingUtilities.isEventDispatchThread() ) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    fireCombatStateEvent(reason + "(invoked)");
                }
            });
            
            return;
        }
        if ( DEBUG >= 1 ) System.out.println( "Battle: fireCombatStateEvent for " + reason);
        ChangeEvent e = new ChangeEvent(Battle.currentBattle);
        // Guaranteed to return a non-null array
        Iterator<WeakReference<BattleListener>> it = listenerList.iterator();
        while(it.hasNext()) {
            WeakReference<BattleListener> ref = it.next();
            BattleListener bl = ref.get();
            if ( bl == null ) {
                it.remove(); // remove dead references...
            }
            else {
                // System.out.println( "Firing chance event to: " + listeners[i+1].toString());
                bl.combatStateChange(e);
            }
        }
    }
    
    static protected void fireProcessingEvent(final BattleChangeType reason, final Object referenceObject) {
        if ( ! SwingUtilities.isEventDispatchThread() ) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    fireProcessingEvent(reason,referenceObject);
                }
            });
            
            return;
        }
        if ( DEBUG >= 1 ) System.out.println( "Battle: fireProcessingEvent for " + reason);
        BattleChangeEvent e = new BattleChangeEvent(Battle.currentBattle, reason, referenceObject);
        // Guaranteed to return a non-null array
        Iterator<WeakReference<BattleListener>> it = listenerList.iterator();
        while(it.hasNext()) {
            WeakReference<BattleListener> ref = it.next();
            BattleListener bl = ref.get();
            if ( bl == null ) {
                it.remove(); // remove dead references...
            }
            else {
                // System.out.println( "Firing chance event to: " + listeners[i+1].toString());
                bl.processingChange(e);
            }
        }
    }
    
    public void triggerChangeEvent() {
        fireChangeEvent(BattleChangeType.TRIGGER_CHANGE, null);
    }
    
    public void addCompletedEvent(BattleEvent be) {
        if( be.getType() == 0) {
            System.out.println("WARNING: Bad BattleEvent added to EventList:");
            System.out.println(be);
            Thread.dumpStack();
        }
        
        if ( be.getTimeProcessed() == null ) {
            Chronometer time = (Chronometer)Battle.getCurrentBattle().getTime().clone();
            be.setTimeProcessed(time);
        }
        
        completedEventList.add(completedEventPosition++, be );
        completedEventMaxRedo = completedEventPosition;
        fireNotificationEvent( "Completed Event Addition" );
    }
    
    public void removeCompletedEvent(BattleEvent be) {
        if ( completedEventPosition > 0 && completedEventList.get(completedEventPosition-1) == be) {
            completedEventPosition--;
            
            completedEventMaxRedo = completedEventPosition;
            //System.out.println("Completed Event Removed: " + be );
            fireNotificationEvent( "Completed Event Removal");
        }
    }
    
    /** Undo all of the completed event upto and including battleEvent.
     *
     */
    public void undoCompleteEvent(BattleEvent battleEvent) throws BattleEventException {
        int position = completedEventList.indexOf(battleEvent);
        if ( position != -1 ) {
            for(int i = getCompletedEventPosition(); i > position; i--) {
                undoCompletedEvent();
            }
        }
    }
    
    public void undoCompletedEvent()  throws BattleEventException{
        if ( isUndoable() ) {
            BattleEvent be =  completedEventList.get( completedEventPosition - 1 );
            be.rollbackBattleEvent();
            completedEventPosition--;
            fireNotificationEvent("Undo Completed");
            fireChangeEvent(BattleChangeType.UNDO_COMPLETE, null);
        }
    }
    
    public void redoCompletedEvent()  throws BattleEventException {
        if ( isRedoable() ) {
            BattleEvent be =  completedEventList.get( completedEventPosition );
            be.rollforwardBattleEvent();
            completedEventPosition++;
            fireNotificationEvent("Redo Completed");
            fireChangeEvent(BattleChangeType.REDO_COMPLETE,null);
        }
    }
    
    public boolean isUndoable() {
        return completedEventPosition > 0;
    }
    
    public boolean isRedoable() {
        return completedEventPosition < completedEventMaxRedo;
    }
    
    /** Destroys old Completed BattleEvents.
     *
     * Purging Completed BattleEvent will free the memory they are holding,
     * remove them from the completed event list, and remove the ability
     * to undo that even.  Only completed events can be purged.
     *
     * The messages contained in the events will be copied into the purged
     * message queue.
     *
     * Events will be deconstructed in such a way that they won't harm the
     * operation of future combat.
     */
    public synchronized void purgeCompletedEvents(int remainer) {
        int eventCount = completedEventPosition;
        int removeCount = Math.max(eventCount - remainer, 0);
        
        for( int rindex = 0; rindex < removeCount; rindex++ ) {
            BattleEvent be = completedEventList.get(0);
            
            // Copy the messages to the old message buffer
            Chronometer eventTime = be.getTimeParameter();
            
            int messageCount = be.getMessageCount();
            for( int mindex = 0; mindex < messageCount; mindex++) {
                addPurgedMessage( be.getMessageText(mindex), be.getMessageType(mindex), eventTime );
            }
            
            // Remove the event from the completedList
            completedEventList.remove(0);
            
            // Destroy the event
            be.destroy();
            
            // Adjust the completed index
            completedEventPosition--;
            completedEventMaxRedo--;
        }
        
        fireNotificationEvent("Purge Completed");
    }
    
    /** Destroys old Completed BattleEvents.
     *
     * Purging Completed BattleEvent will free the memory they are holding,
     * remove them from the completed event list, and remove the ability
     * to undo that even.  Only completed events can be purged.
     *
     * The messages contained in the events will be copied into the purged
     * message queue.
     *
     * Events will be deconstructed in such a way that they won't harm the
     * operation of future combat.
     *
     * @param time Time before which all events will be removed.
     */
    public synchronized void purgeCompletedEvents(Chronometer removeUntil) {
        
        while( completedEventPosition > 0 ) {
            BattleEvent be = completedEventList.get(0);
            
            // Copy the messages to the old message buffer
            Chronometer eventTime = be.getTimeProcessed();
            
            if ( eventTime == null ) {
                System.out.println("WARNING: Null event processed time encountered:\n" + be.toString());
                break;
            }
            
            if ( eventTime.compareTo(removeUntil) > 0) {
                break;
            }
            
            int messageCount = be.getMessageCount();
            for( int mindex = 0; mindex < messageCount; mindex++) {
                addPurgedMessage( be.getMessageText(mindex), be.getMessageType(mindex), eventTime );
            }
            
            // Remove the event from the completedList
            completedEventList.remove(0);
            
            // Destroy the event
            be.destroy();
            
            // Adjust the completed index
            completedEventPosition--;
            completedEventMaxRedo--;
        }
        
        fireNotificationEvent("Purge Completed");
    }
    
    /** Getter for property time.
     * @return Value of property time.
     */
    public Chronometer getTime() {
        return time;
    }
    /** Setter for property time.
     * @param time New value of property time.
     */
    public void setTime(Chronometer time) {
        this.time = time;
        fireSegmentAdvanced();
    }
    
    public void addPurgedMessage(String message, int type, Chronometer eventTime) {
        purgedMessageVector.add( new Battle.PurgedMessageEntry(message, type, eventTime));
    }
    
    public int getPurgedMessageCount() {
        return purgedMessageVector.size();
    }
    
    public String getPurgedMessageText(int pindex) {
        return ((Battle.PurgedMessageEntry)purgedMessageVector.get(pindex)).message;
        
    }
    
    public int getPurgedMessageType(int pindex) {
        return ((Battle.PurgedMessageEntry)purgedMessageVector.get(pindex)).type;
    }
    
    public Chronometer getPurgedMessageTime(int pindex) {
        return ((Battle.PurgedMessageEntry)purgedMessageVector.get(pindex)).time;
    }
    
    public List<BattleEvent> getCompletedEventList() {
        return completedEventList;
        
    }
    
    public int getCompletedEventPosition() {
        return completedEventPosition;
    }
    
    public int getCompletedEventMaxRedo() {
        return completedEventMaxRedo;
    }
    public void rosterChange(ChangeEvent e) {
        fireChangeEvent(BattleChangeType.ROSTER_CHANGED, e.getSource());
    }
    /** Getter for property stopped.
     * @return Value of property stopped.
     */
    public boolean isStopped() {
        return stopped;
    }
    /** Setter for property stopped.
     * @param stopped New value of property stopped.
     */
    public void setStopped(boolean stopped) {
        this.stopped = stopped;
        if ( stopped == false ) {
            makeCombatantsActive();
//            if ( time.getTime() <= 12 )  {
//                Iterator<Target> i = getCombatants().iterator();
//                while ( i.hasNext() ) {
//                    Target t =  i.next();
//                    t.activateNormallyOn();
//                }
//            }
            
            fireSegmentAdvanced();
        }
    }
    
    public void makeCombatantsActive() {
        Iterator<Target> i = getCombatants().iterator();
        while ( i.hasNext() ) {
            Target t = i.next();
            if ( t.hasStat("SPD") && time.isActivePhase( t.getEffectiveSpeed(time) )) {
                t.setCombatState(CombatState.STATE_ACTIVE);
                t.setPrephaseDone(false);
            } else {
                t.setCombatState(CombatState.STATE_FIN);
                t.setPrephaseDone(true);
            }
        }
    }
    
    static public void setCurrentBattle(Battle battle) {
        if ( Battle.currentBattle != battle ) {
            Battle oldBattle = Battle.currentBattle;
            Battle.currentBattle = battle;
            fireChangeEvent(BattleChangeType.BATTLE_CHANGED, battle);
            fireSegmentAdvanced();
            fireSequenceChanged();
            fireTargetSelected();
            fireNotificationEvent("Current Battle Set");
            fireProcessingEvent( BattleChangeType.BATTLE_CHANGED, battle );
            
            if ( oldBattle != null ) {
                oldBattle.destroy();
            }
        }
    }
    
    static public Battle getCurrentBattle() {
        return Battle.currentBattle;
    }
    
    public Undoable addDelayedEvent(BattleEvent be) {
        Undoable u = null;
        
        if ( delayedEventList.contains( be ) == false) {
            delayedEventList.add(be);
            fireSequenceChanged();
            
            u = new AddDelayedEventUndoable(be);
        }
        return u;
    }
    
    public Undoable removeDelayedEvent(BattleEvent be) {
        Undoable u = null;
        
        if ( delayedEventList.contains( be ) ) {
            delayedEventList.remove(be);
            fireSequenceChanged();
            
            u = new RemoveDelayedEventUndoable(be);
        }
        
        return u;
    }
    
    public Set<BattleEvent> getDelayedEvents() {
        return delayedEventList;
    }
    
    public Undoable addBlockSequence(Chronometer time, Target blockingTarget, Target blockedTarget) {
        Undoable u = null;
        
        BlockEntry e = new BlockEntry((Chronometer)time.clone(), blockingTarget, blockedTarget);
        
        blockList.add(e);
        
        u = new AddBlockEntryUndoable(blockList, e);
        
        return u;
    }
    
    public Undoable removeBlockSequence(BlockEntry be) {
        Undoable u = null;
        
        if ( blockList.contains( be ) ) {
            blockList.remove(be);
            fireSequenceChanged();
            
            u = new RemoveBlockEntryUndoable(blockList, be);
        }
        
        return u;
    }
    
    public Set<BlockEntry> getBlockList() {
        return blockList;
    }
    
    public void startBattle() {
        if ( isStopped()) {
            setStopped(false);
            BattleEvent be = new BattleEvent(BattleEvent.ADVANCE_TIME, false);
            addEvent(be);
        }
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        
        propertyChangeSupport = new PropertyChangeSupport(this);
        battleEngine = new BattleEngine( "BattleEngine", this);
        seq = new Sequencer5(this);
        
        
    }
    
    static public Battle open(){
        MyFileChooser chooser = MyFileChooser.chooser;
        
        
        MyFileFilter mff = new MyFileFilter(new String[] {"btl"}, "Select Battle to Open");
        chooser.setFileFilter(mff);
        
        chooser.setDialogTitle( "Open" );
        
        int returnVal = chooser.showOpenDialog(null);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                Battle d = open(file);
                if ( d != null  ){
                    return d;
                } else {
                    JOptionPane.showMessageDialog(null,
                            "An Error Occurred while opening:\n" + "Incompatible File Type",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception exc) {
//                JOptionPane.showMessageDialog(null,
//                "An Error Occurred while opening:\n" +
//                exc.toString(),
//                "Error",
//                JOptionPane.ERROR_MESSAGE);
                ExceptionWizard.postException(exc);
            }
        }
        return null;
    }
    
    public static Battle open(File file) throws FileNotFoundException, IOException, ClassNotFoundException{
        return open(file, true);
    }
    
    public static Battle open(File file, boolean setAsCurrent) throws FileNotFoundException, IOException, ClassNotFoundException{
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Battle d = (Battle) ois.readObject();
        
        if ( setAsCurrent ) {
            setCurrentBattle(d);
            
            RosterDockingPanel rdp = RosterDockingPanel.getDefaultRosterDockingPanel();
            
            Iterator i = d.getRosters().iterator();
            while ( i.hasNext() ) {
                Roster r = (Roster)i.next();
                rdp.addRoster(r);
            }
        }
        
        d.setFile(file);
        
        
        return d;
    }
    
    public void save(File file) throws FileNotFoundException, IOException{
        save(file, true);
    }
    
    public void save() throws FileNotFoundException, IOException{
        save(file, true);
    }
    
    public void save(boolean checkForErrors) throws FileNotFoundException, IOException{
        save(file, checkForErrors);
    }
    
    public void save(File file, boolean checkForErrors) throws FileNotFoundException, IOException {
        setFile(file);
        
        if ( file == null ) {
            MyFileChooser chooser = MyFileChooser.chooser;
            
            MyFileFilter mff = new MyFileFilter("btl");
            chooser.setFileFilter(mff);
            
            chooser.setDialogTitle( "Save Location for Battle");
            chooser.setSelectedFile( new File(chooser.getCurrentDirectory(), "battle.btl" ));
            CheckBattleCheckBox cb = new CheckBattleCheckBox(checkForErrors);
            chooser.setAccessory(cb);
            int returnVal = chooser.showSaveDialog(null);
            chooser.setAccessory(null);
            
            checkForErrors = cb.isSelected();
            
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    File f = chooser.getSelectedFile();
                    setFile(f);
                } catch ( Exception exc ) {
                    return;
                }
            } else {
                return;
            }
        }
        
        final boolean finalCheckForErrors = checkForErrors;
        final Battle battle = this;
        
        final ProgressMonitor pm = new ProgressMonitor(CombatSimulator.getFrame(), "Saving Battle", null, 0, 4);
       
        final Timer timer = new Timer();
        timer.schedule( new TimerTask() {
            int i = 1;
            
            public void run() {
                pm.setProgress(i++);
                if ( i == 4 ) i = 0;
            }
        }, 0, 150);
        
        pm.setMillisToPopup(0);
        pm.setMillisToDecideToPopup(0);
        
        new Thread( new Runnable() {
            public void run() {
                
                boolean fail = false;
                
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(getFile());
                    ObjectOutputStream out;
                    out = new ObjectOutputStream(fos);
                    
                    out.writeObject(battle);
                    out.flush();
                    out.close();
                    
                    
                } catch (FileNotFoundException ex) {
                    ExceptionWizard.postException(ex);
                    fail = true;
                } catch (IOException ex) {
                    ExceptionWizard.postException(ex);
                    fail = true;
                }
                
                if ( finalCheckForErrors && fail == false ) {
                    try {
                        // Just reload the battle
                        Battle newBattle = open(Battle.this.file, false);
                        newBattle.destroy();
                    } catch ( IOException e ) {
                        ExceptionWizard.postException(e);
                        fail = true;
                    } catch ( ClassNotFoundException cnfe ) {
                        ExceptionWizard.postException(cnfe);
                        fail = true;
                    } catch ( OutOfMemoryError e ) {
                        ExceptionWizard.postException(e);
                        fail = true;
                    }
                }
                
                if ( fail == false ) {
                    JOptionPane.showMessageDialog(CombatSimulator.getFrame(),
                    "Battle was saved successfully.", 
                    "Save Status", JOptionPane.INFORMATION_MESSAGE);
                }
                else {
                    JOptionPane.showMessageDialog(CombatSimulator.getFrame(),
                    "Battle saving failed!", 
                    "Save Status", JOptionPane.ERROR_MESSAGE);
                }
                    
                timer.cancel();
                
               
                pm.close();
            }
        }).start();
        
        
        
        
        
    }
    
    public boolean saveWithBlocking(File file, boolean checkForErrors) throws FileNotFoundException, IOException {
        setFile(file);

        boolean fail = false;

        // for ( int i = 0; i < 10000; i ++ ) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(getFile());
            ObjectOutputStream out;
            out = new ObjectOutputStream(fos);

            out.writeObject(Battle.getCurrentBattle());
            out.flush();
            out.close();
        } catch (FileNotFoundException ex) {
            ExceptionWizard.postException(ex);
            fail = true;
        } catch (IOException ex) {
            ExceptionWizard.postException(ex);
            fail = true;
        }

        if ( checkForErrors && fail == false ) {
            try {
                // Just reload the battle
                Battle b = open(Battle.this.file, false);
                b.destroy();
            } catch ( IOException e ) {
                ExceptionWizard.postException(e);
                fail = true;
            } catch ( ClassNotFoundException cnfe ) {
                ExceptionWizard.postException(cnfe);
                fail = true;
            } catch ( OutOfMemoryError e ) {
                ExceptionWizard.postException(e);
                fail = true;
            }
        }
        
        return !fail;
    }
    
    public void close() {
        if ( Battle.currentBattle == this ) Battle.currentBattle = null;
        Iterator i = rosters.iterator();
        while ( i.hasNext() ) {
            RosterDockingPanel rdp = RosterDockingPanel.getDefaultRosterDockingPanel();
            rdp.removeRoster( (Roster)i.next() );
        }
        
        destroy();
    }
    /** Getter for property file.
     * @return Value of property file.
     */
    public File getFile() {
        return file;
    }
    /** Setter for property file.
     * @param file New value of property file.
     */
    public void setFile(File file) {
        this.file = file;
    }
    
//    static public void setDefaultAbilities(AbilityList al) {
//        defaultAbilities = al;
//    }
    
    /** Returns the default abilities.
     *
     * @deprecated
     */
    static public AbilityList getDefaultAbilitiesOld() {
        return Battle.currentBattle.getDefaultAbilities();
    }
    
    public AbilityList getDefaultAbilities() {
        if ( defaultAbilities == null ) defaultAbilities = PADRoster.createDefaults();
                return defaultAbilities;
    }
    
    static public void setRecoveryAbility(Ability ability) {
        recovery = ability;
    }
    
    static public Ability getRecoveryAbility() {
        return recovery;
    }
    
    static public void setStunRecoveryAbility(Ability ability) {
        stunRecovery = ability;
    }
    
    static public Ability getStunRecoveryAbility() {
        return stunRecovery;
    }
    
    static public void setPassAbility(Ability ability) {
        pass = ability;
    }
    
    static public Ability getPassAbility() {
        return pass;
    }
    
//    public List getBattleActions() {
//        return actionVector;
//    }
//
//    public void addBattleAction() {
//
//    }
//
//    public void removeBattleAction() {
//
//    }
    
    public void triggerProcessingNotify(boolean processing) {
        fireProcessingEvent( processing ? BattleChangeType.STARTED_PROCESSING_EVENTS : BattleChangeType.FINISHED_PROCESSING_EVENTS, null);
    }
    
    public void clearBattleStats() {
        Target target;
        Iterator<Target> i = getCombatants().iterator();
        while ( i.hasNext() ) {
            target = i.next();
            target.clearBattleStats();
        }
    }
    
    public void addSpecialTarget(Target target) {
        specialTargetList.addTarget(target);
        specialTargets.add(target);
    }
    
    public Set<Target> getSpecialTargets() {
        return specialTargets;
    }
    
    public TargetList getSpecialTargetList() {
        return specialTargetList;
    }
    
    public TargetList getObstructions() {
        return obstructions;
    }
    
    /** Getter for property gmTarget.
     * @return Value of property gmTarget.
     *
     */
    public champions.GMTarget getGmTarget() {
        return gmTarget;
    }
    
    /** Setter for property gmTarget.
     * @param gmTarget New value of property gmTarget.
     *
     */
    public void setGmTarget(champions.GMTarget gmTarget) {
        this.gmTarget = gmTarget;
    }
    
    /** Getter for property recentGenericAbilityList.
     * @return Value of property recentGenericAbilityList.
     *
     */
    public AbilityList getRecentGenericAbilityList() {
        return recentGenericAbilityList;
    }
    
    /** Setter for property recentGenericAbilityList.
     * @param recentGenericAbilityList New value of property recentGenericAbilityList.
     *
     */
    public void setRecentGenericAbilityList(AbilityList recentGenericAbilityList) {
        this.recentGenericAbilityList = recentGenericAbilityList;
    }

    public void destroy() {
        if ( battleEngine != null ) {
            battleEngine.terminateEngine();
        }
    }
    
    public static class CheckBattleCheckBox extends JCheckBox {
        
        public CheckBattleCheckBox(boolean selected) {
            super("Check Saved Battle", selected);
        }
    }
    
    public static class AddBattleEventUndoable implements Undoable, Serializable  {
        private BattleEvent be;
        private boolean first;
        
        public AddBattleEventUndoable(BattleEvent be, boolean first) {
            this.be = be;
            this.first = first;
        }
        
        public void redo() {
            Battle.currentBattle.addEvent(be, first);
        }
        
        public void undo() {
            Battle.currentBattle.removeEvent(be);
        }
        
    }
    
    public static class RemoveBattleEventUndoable implements Undoable, Serializable  {
        private BattleEvent be;
        
        public RemoveBattleEventUndoable(BattleEvent be) {
            this.be = be;
        }
        
        public void redo() {
            Battle.currentBattle.removeEvent(be);
        }
        
        public void undo() {
            Battle.currentBattle.addEvent(be);
        }
        
    }
    
    public static class AddDelayedEventUndoable implements Undoable, Serializable  {
        private BattleEvent be;
        
        public AddDelayedEventUndoable(BattleEvent be) {
            this.be = be;
        }
        
        public void redo() {
            Battle.currentBattle.addDelayedEvent(be);
        }
        
        public void undo() {
            Battle.currentBattle.removeDelayedEvent(be);
        }
    }
    
    public static class RemoveDelayedEventUndoable implements Undoable, Serializable  {
        private BattleEvent be;
        
        public RemoveDelayedEventUndoable(BattleEvent be) {
            this.be = be;
        }
        
        public void redo() {
            Battle.currentBattle.removeDelayedEvent(be);
        }
        
        public void undo() {
            Battle.currentBattle.addDelayedEvent(be);
        }
    }
    
    public static class AddBlockEntryUndoable implements Undoable, Serializable  {
        private BlockEntry be;
        private Set<BlockEntry> blockList;
        
        public AddBlockEntryUndoable(Set<BlockEntry> blockList, BlockEntry be) {
            this.blockList = blockList;
            this.be = be;
        }
        
        public void redo() {
            blockList.add(be);
        }
        
        public void undo() {
            blockList.remove(be);
        }
    }
    
    public static class RemoveBlockEntryUndoable implements Undoable, Serializable {
        private BlockEntry be;
        private Set<BlockEntry> blockList;
        
        public RemoveBlockEntryUndoable(Set<BlockEntry> blockList, BlockEntry be) {
            this.blockList = blockList;
            this.be = be;
        }
        
        public void redo() {
            blockList.remove(be);
        }
        
        public void undo() {
            blockList.add(be);
        }
    }
    
    public static class PurgedMessageEntry implements Serializable{
        protected String message;
        protected int type;
        protected Chronometer time;
        
        public PurgedMessageEntry(String message, int type, Chronometer time) {
            this.message = message;
            this.type = type;
            this.time = time;
        }
    }
    
}