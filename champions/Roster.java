/*
 * Roster.java
 *
 * Created on September 13, 2000, 9:31 PM
 */

package champions;

import champions.ColumnList;
import champions.event.RosterAddEvent;
import champions.event.RosterRemoveEvent;
import champions.exceptionWizard.ExceptionWizard;
import champions.interfaces.Columned;
import champions.interfaces.RosterListener;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;


/** The Roster class provides a method to groups Targets.
 *
 * RosterListeners can listen to the roster to receive notifications
 * of roster changes.  These include Target additions and removals
 * and state changes of targets.
 *
 * Several HCS combat affecting properties can be applied to all of the
 * Targets in a given roster.  The most prevelant of those is the
 * ability to associate a Combat Profile with the whole roster.
 *
 * Unlike most HCS objects, Rosters are not based upon the DetailList
 * object.  Instead they use a simple Vector object to store and order
 * their Targets.
 * @author Trevor Walker
 * @version $Revision: 1.20 $
 */
public class Roster extends Object
implements PropertyChangeListener, java.io.Serializable, Columned {
    /** SerailVersionID for class. */
    static final long serialVersionUID = 5211014115178314362L;
    /** Extension used by roster files. */
    static protected String fileExtension = "rst";
    public Target MobTarget;
    private static final int DEBUG = 0;
    
    /** Holds a File object for the most recently read Roster.
     *
     * This is a crappy little hack that is necessary because there doesn't
     * apprear to be a way to get the FileInputStream from an ObjectInputStream.
     */
    static protected File rosterReadFile = null;
    
    transient private EventListenerList listenerList = new EventListenerList();
    
    transient private File file = null;
    /** Holds value of property name. */
    
    transient private int version = 1;
    
    transient private Battle battle;
    transient private List<Target> combatants;
    
    private String name;
    
    public Target MobLeader;
    
    /** Holds value of property preferredBounds. */
    private Rectangle layoutBounds;
    private Double frameID = null;
    private Point frameLocation = null;
    
    private ColumnList columnList = null;
    
    private String profileName = null;
    public boolean MobMode=false;
    
    /** Creates new Roster.
     *
     * The roster will initially be name "unnamed" and have no targets.
     */
    public Roster() {
        layoutBounds = new Rectangle(50,100,500,150);
        setName("Unnamed");
        combatants = new ArrayList<Target>();
        //Battle.addBattleListener(this);
    }
    
    /** Creates new Roster.
     *
     * The roster will initially be named <CODE>rosterName</CODE> and have
     * no targets.
     * @param rosterName Name of the new roster.
     */    
    public Roster(String rosterName) {
        this();
        setName(rosterName);
    }
    
    /** Returns the Vector containing all of the Targets in the Roster.
     *
     * This Vector should not be editted directly and should be used only
     * for imformational purposes (i.e. obtaining an iterator over the
     * Targets in the roster).
     * @return Vector containing all Targets in Roster.
     */    
    
    
    public List<Target> getCombatants() {
        return combatants;
    }
    
    /** Adds target specified by <CODE>Target</CODE> to Roster.
     *
     * A RosterAdd event will be fired to all registered RosterListeners.
     *
     * If the current battle is started, the added target will have
     * it combat state update to STATE_FIN.  Additionally, a battle
     * event will be added to the Battle indicating the Target has been
     * added to the battle.
     * @param target Target to add to Roster.
     */    
    public void add(Target target, BattleEvent be) {
        add(target,true,be);
    }
    
    /** Adds target specified by <CODE>Target</CODE> to Roster.
     *
     * A RosterAdd event will be fired to all registered RosterListeners.
     *
     * If the current battle is started, the added target will have
     * it combat state update to STATE_FIN.  Additionally, a battle
     * event will be added to the Battle indicating the Target has been
     * added to the battle.
     * @param target Target to add to Roster.
     */    
    public void add(Target target) {
        add(target,true,null);
    }
    
    /** Adds target specified by <CODE>Target</CODE> to Roster.
     *
     * A RosterAdd event will be fired to all registered RosterListeners.
     *
     * If the current battle is started and <CODE>postevent</CODE>,
     * the added target will have it combat state update to STATE_FIN and
     * additionally, a battle event will be added to the Battle indicating
     * the Target has been added to the battle.
     * @param target Target to be added to Roster.
     * @param postevent indicates if a RosterAdd event should be fired.
     */    
    public void add(Target target, boolean postevent) {
        add(target,postevent,null);
    }
    
    /** Adds target specified by <CODE>Target</CODE> to Roster.
     *
     * A RosterAdd event will be fired to all registered RosterListeners.
     *
     * If the current battle is started and <CODE>postevent</CODE>,
     * the added target will have it combat state update to STATE_FIN and
     * additionally, a battle event will be added to the Battle indicating
     * the Target has been added to the battle.
     * @param target Target to be added to Roster.
     * @param postevent indicates if a RosterAdd event should be fired.
     */    
    public void add(Target target, boolean postevent, BattleEvent be) {
        if ( target != null && combatants.contains(target) == false ) {
            
            
            if ( postevent && Battle.currentBattle != null && !Battle.currentBattle.isStopped() && getBattle() == Battle.currentBattle ) {
                if ( be == null ) {
                    be = new BattleEvent(BattleEvent.ROSTER_CHANGE_MARGER, false);
                    be.addRosterEvent(this,target, true);
                    if ( target.hasStat("SPD") ) {
                        be.addCombatStateEvent(target, target.getCombatState(), CombatState.STATE_FIN );
                    }
                    be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + " added to roster " + getName(), BattleEvent.MSG_ROSTER)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + " added to roster " + getName(), BattleEvent.MSG_ROSTER)); // .addMessage( target.getName() + " added to roster " + getName(), BattleEvent.MSG_ROSTER);
                    battle.addCompletedEvent( be);
                }
                else {
                    be.addRosterEvent(this,target, true);
                    if ( target.hasStat("SPD") ) {
                        be.addCombatStateEvent(target, target.getCombatState(), CombatState.STATE_FIN );
                    }
                    be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + " added to roster " + getName(), BattleEvent.MSG_ROSTER)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + " added to roster " + getName(), BattleEvent.MSG_ROSTER)); // .addMessage( target.getName() + " added to roster " + getName(), BattleEvent.MSG_ROSTER);
                }
            }
           
            combatants.add(target);
            if ( target.hasStat("SPD") ) {
                target.setCombatState(CombatState.STATE_FIN);
            }
            else {
                target.setCombatState(CombatState.STATE_INACTIVE);
            }
            
            target.setRoster(this);
            
            target.addPropertyChangeListener(this);
            fireRosterAdd(target, combatants.size() - 1);
        }
    }
    
    /** Removes target specified by <CODE>Target</CODE> from Roster.
     *
     * A RosterRemove event will be fired to all registered RosterListeners.
     *
     * If the current battle is in a non-stopped condition,
     * a battleEvent will be posted notifying the removal of the Target
     * from the roster.
     * @param target Target to be removed from the roster.
     */    
    public void remove(Target target) {
        remove(target, true);
    }
    
    /** Removes target specified by <CODE>Target</CODE> from Roster.
     *
     * A RosterRemove event will be fired to all registered RosterListeners.
     *
     * If the current battle is in a non-stopped condition and <CODE>postevent</CODE> is true,
     * a battleEvent will be posted notifying the removal of the Target
     * from the roster.
     * @param target Target to be removed from the roster.
     * @param postevent true if battleEvent should be posted notifying user of removal.
     */    
    public void remove(Target target, boolean postevent) {
        if ( target != null &&  combatants.contains(target) ) {
            int i = combatants.indexOf(target);
            
            if ( postevent && Battle.currentBattle != null && !Battle.currentBattle.isStopped() && getBattle() == Battle.currentBattle) {
                BattleEvent be = new BattleEvent(BattleEvent.ROSTER_CHANGE_MARGER, false);
                be.addRosterEvent(this,target, false);
                be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + " removed from roster " + getName(), BattleEvent.MSG_ROSTER)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + " removed from roster " + getName(), BattleEvent.MSG_ROSTER)); // .addMessage( target.getName() + " removed from roster " + getName(), BattleEvent.MSG_ROSTER);
                battle.addCompletedEvent( be);
            }
            
            combatants.remove(target);
            target.removePropertyChangeListener(this);
            fireRosterRemove(target, i);
        }
    }
    
    /** Removes target specified by <CODE>Target</CODE> from Roster.
     *
     * A RosterRemove event will be fired to all registered RosterListeners.
     *
     * A roster removal event will be added to <CODE>be</CODE> notifying
     * the removal of the Target.
     *
     * This method is an alternative version of the two <CODE>remove</CODE> methods.
     * For legacy reasons, it is named <CODE>removeTarget</CODE>.  However,
     * it effectively does the same operation as remove, only with a different
     * handling of the battle event notification.
     * @param target Target to be removed from Roster.
     * @param be BattleEvent into which a roster removal will be added.
     */    
    public void removeTarget(BattleEvent be, Target target) {
        if ( target != null &&  combatants.contains(target) ) {
            int i = combatants.indexOf(target);
            
            be.addRosterEvent(this,target, false);
            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + " removed from roster " + getName(), BattleEvent.MSG_ROSTER)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + " removed from roster " + getName(), BattleEvent.MSG_ROSTER)); // .addMessage( target.getName() + " removed from roster " + getName(), BattleEvent.MSG_ROSTER);
            
            combatants.remove(target);
            target.removePropertyChangeListener(this);
            fireRosterRemove(target, i);
        }
    }
    
    /** Removes all of the Targets from the roster.
     *
     * If the current Battle is in a non-stopped condition, battleEvents
     * will be posted to notify the user of the removals.
     *
     * RosterRemove events, one for each Target, will be sent to all of the
     * RosterListeners currently registered.
     */    
    public void removeAll() {
        int row;
        Target c;
        Iterator i = combatants.iterator();
        while ( i.hasNext() ) {
            c = (Target)i.next();
            row = combatants.indexOf(c);
            c.removePropertyChangeListener(this);
            i.remove();
            fireRosterRemove(c, row);
        }
    }

    
    /** Returns the number of targets in the Roster.
     * @return Number of Targets in Roster.
     */    
    public int getSize() {
        return combatants.size();
    }
    
    /** Returns the Target at position <CODE>index</CODE> in roster.
     * @param index Position in roster to return.
     * @return Target at position <CODE>index</CODE>.
     */    
    public Target get(int index) {
        return (Target)combatants.get(index);
    }
    
    /** Returns the position of <CODE>target</CODE> in the roster.
     * @return Position of <CODE>target</CODE>, -1 if <CODE>target</CODE> is not in roster.
     * @param target Target to find in roster.
     */    
    public int indexOf(Target target) {
        return combatants.indexOf(target);
    }
    
    /** Returns whether <CODE>target</CODE> is in roster.
     * @return true if <CODE>target</CODE> is in roster, false otherwise.
     * @param target Target to look up.
     */    
    public boolean contains(Target target) {
        return combatants.contains(target);
    }
    
//    /**
//     * @param target
//     * @param index
//     */    
//    public void insertElementAt(Target target, int index) {
//        if ( ! combatants.contains(target) ) {
//            combatants.insertElementAt(target, index);
//            target.setCombatState(CombatState.STATE_FIN);
//            fireRosterAdd(target, index);
//        }
//    }
//    
    /**
     *  Adds <code>l</code> as a RosterListener for this roster.
     *
     *  @param l  the <code>RosterListener</code> to add.
     */
    public void addRosterListener(RosterListener l) {
        listenerList.add(RosterListener.class,l);
    }
    
    /**
     * Removes <code>l</code> as a RosterListener for this roster.
     *
     * @param l  the <code>RosterListener</code> to remove
     */
    public void removeRosterListener(RosterListener l) {
        listenerList.remove(RosterListener.class,l);
    }
    
    /** Fires RosterAdd event to all RosterListeners.
     * @param target Target being added.
     * @param index index target was added at.
     */    
    protected void fireRosterAdd(Target target, int index) {
        RosterAddEvent e = null;
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==RosterListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new RosterAddEvent(this, target, index);
                ((RosterListener)listeners[i+1]).rosterAdd(e);
            }
        }
    }
    
    /** Fires RosterRemove event to all RosterListeners.
     * @param target Target being removed.
     * @param index Old index of target.
     */    
    protected void fireRosterRemove(Target target, int index) {
        RosterRemoveEvent e = null;
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==RosterListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new RosterRemoveEvent(this, target, index);
                ((RosterListener)listeners[i+1]).rosterRemove(e);
            }
        }
    }
    /** Fires StateChangeEvent to all RosterListeners. */
    protected void fireStateChangeEvent() {
        ChangeEvent e = null;
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==RosterListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new ChangeEvent(this);
                ((RosterListener)listeners[i+1]).rosterChange(e);
            }
        }
    }
    
    /** Fires property change event to all RosterListeners.
     * @param propName Name of property being modified.
     * @param oldValue Old property value.
     * @param newValue New property value.
     */    
    protected void firePropertyChange(String propName, Object oldValue, Object newValue) {
        PropertyChangeEvent e = null;
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==RosterListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new PropertyChangeEvent(this, propName, oldValue, newValue);
                ((RosterListener)listeners[i+1]).propertyChange(e);
            }
        }
    }
    

    /** Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return name;
    }
    /** Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        if ( name != this.name ) {
            String oldname = this.name;
            this.name = name;
            
            firePropertyChange("Name",oldname, name);
        }
    }
    
    /** This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ( evt.getPropertyName().equals("Target.NAME") ) {
            if ( SwingUtilities.isEventDispatchThread() ) {
                fireStateChangeEvent();
            }
            else {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        fireStateChangeEvent();
                    }
                });
            }
        }
    }
    
    /** Saves the roster.
     * @throws FileNotFoundException It's a FileNotFoundException.
     * @throws IOException It's an IOException.
     */    
    public void save() throws FileNotFoundException, IOException{
        if ( file == null ) {
            MyFileChooser chooser = MyFileChooser.chooser;
            
            MyFileFilter mff = new MyFileFilter(fileExtension);
            chooser.setFileFilter(mff);
            
            chooser.setDialogTitle( "Save Location for " + this.getName() + " Roster");
            chooser.setSelectedFile( new File(chooser.getCurrentDirectory(), this.getName() + "." + fileExtension));
            
            int returnVal = chooser.showSaveDialog(null);
            
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                setFile(file);
                
                
            }
            else {
                return;
            }
        }
        
        FileOutputStream fos = new FileOutputStream( getFile() );
        ObjectOutputStream out = new ObjectOutputStream(fos);
        
        version = 2;
        out.writeObject(this);
        version = 1;
        out.flush();
        out.close();
        
        String s;
        int index;
        s = getFile().getName();
        if ( (index = s.indexOf( ".rst" )) > 0 ) {
            s = s.substring(0, index);
        }
        
        setName( s );
    }
    
    /** Creates a new roster based upon the indicated file <CODE>file</CODE>.
     * @param file File to open and create roster from.
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException
     * @return New roster.
     */    
    public static Roster open(File file) throws FileNotFoundException, IOException, ClassNotFoundException{
        rosterReadFile = file;
        
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Roster r = (Roster) ois.readObject();
        
        r.setFile(file);
        
        String s;
        int index;
        s = file.getName();
        if ( (index = s.indexOf( ".rst" )) > 0 ) {
            s = s.substring(0, index);
        }
        
        r.setName( s );
        
        rosterReadFile = null;
        return r;
    }
    
    /**
     * @return
     */    
    public File getFile() {
        return file;
    }
    
    /**
     * @param file
     */    
    public void setFile(File file) {
        this.file = file;
    }
    
    /**
     * @param file
     * @throws FileNotFoundException
     * @throws IOException
     */    
    public void save(File file) throws FileNotFoundException, IOException{
        setFile(file);
        save();
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int version = in.readInt();
        
        if ( version == 1 ) {
            //  System.out.println( "Reading V1 Roster");
            battle = (Battle) in.readObject();
            combatants = (ArrayList) in.readObject();
            in.defaultReadObject();
            
            file = null;
        }
        else if ( version == 2 ) {
            //   System.out.println( "Reading V2 Roster");
            
            in.defaultReadObject();
            
            combatants = new ArrayList<Target>();
            
            int count = in.readInt();
            int i;
            File file;
            Target t;
            Object o;
            DetailList d;
            
            for(i=0;i<count;i++) {
                o = in.readObject();
                if ( o.getClass() != File.class ) {
                    throw new IOException("Invalid File Format");
                }
                file = (File)o;
                
                if ( file.isAbsolute() == false && rosterReadFile == null ) {
                    throw new IOException("Unable to determine Roster File for resolution of relative pathname.");
                }
                else if ( file.isAbsolute() == false ) {
                    file = new File( rosterReadFile.getParent(), file.getPath() );
                    if ( DEBUG >= 1 ) System.out.println("Attempting to open Target from relative path. Final file: " + file);
                }
                
                d = Target.open(file);
                if ( d == null || ! (d instanceof Target) ) {
                    throw new IOException("Unable to open member target: " + file.getName() );
                }
                combatants.add((Target)d);
                ((Target)d).setRoster(this);
            }
            
            // name = "Unnamed";
            battle = null;
        }
        
        listenerList = new EventListenerList();
        
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException{
        // Make sure all the characters are saved first
        if ( version == 2 ) {
            writeV2Object(out);
        }
        else {
            if ( DEBUG >= 1 ) System.out.println( "Saving V1 Roster");
            out.writeInt( 1);
            
            out.writeObject( battle );
            out.writeObject( combatants );
            
            out.defaultWriteObject();
        }
    }
    
    private void writeV2Object(ObjectOutputStream out) throws IOException{
        // Make sure all the characters are saved first
        //  System.out.println( "Saving V2 Roster");
        
        out.writeInt( 2 );
        List<Target> tempCombatants = combatants;
        
        Target t;
        Iterator<Target> i = tempCombatants.iterator();
        while ( i.hasNext() ) {
            t = i.next();
            t.save();
            
            if ( t.getFile() == null ) {
                throw new IOException("Unabled to save roster entry for" + t.getName());
            }
        }
        
        // Write out all important information
        out.defaultWriteObject();
        
        out.writeInt( tempCombatants.size() );
        
        i = tempCombatants.iterator();
        while ( i.hasNext() ) {
            t = (Target)i.next();
            if ( DEBUG >= 1 ) System.out.println("Character file for " + t.getName() + ": " );
            File characterFile = getRelativePathToCharacter(file, t.getFile());
            if ( DEBUG >= 1 ) System.out.println("Is Relative: " + (characterFile.isAbsolute() ? "No" : "Yes"));
            out.writeObject( characterFile );
        }
    }
    
    private File getRelativePathToCharacter(File rosterFile, File characterFile) {
        File rosterPath = rosterFile.getParentFile();
        
        File path = characterFile.getParentFile();
        
        boolean isRelative = false;
        
        StringBuffer relativeName = new StringBuffer( characterFile.getName() );
        
        while ( isRelative == false && path != null ) {
            if ( DEBUG >= 1 )System.out.println(path);
            if ( rosterPath.equals(path) ) {
                if ( DEBUG >= 1 )System.out.println("We have a match with: " + path);
                isRelative = true;
            }
            else {
                // construct the path and move to next part...
                relativeName.insert(0, File.separator );
                relativeName.insert(0, path.getName() );
            }
            path = path.getParentFile();
        }
        
        if ( isRelative ) {
            if ( DEBUG >= 1 )System.out.println("Creating new file: " + relativeName);
            return new File ( relativeName.toString() );
        }
        else {
            return characterFile;
        }
    }
    
    /**
     * @return
     */    
    static public Roster open(){
        MyFileChooser chooser = MyFileChooser.chooser;
        
        MyFileFilter mff = new MyFileFilter("rst");
        chooser.resetChoosableFileFilters();
        chooser.setFileFilter(mff);
        
        chooser.setDialogTitle( "Open Roster");
        
        int returnVal = chooser.showOpenDialog(null);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                Roster r = open(file);
                return r;
            }
            catch (Exception exc) {
                JOptionPane.showMessageDialog(null,
                "An Error Occurred while opening:\n" +
                exc.toString(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
    
        static public Roster[] openMultiple(){
        MyFileChooser chooser = MyFileChooser.chooser;
        
        
            // Lets build some FileFilters and add them to the chooser.
            chooser.resetChoosableFileFilters();
            MyFileFilter mff = new MyFileFilter("rst");
            
           
            
            chooser.setFileFilter(mff);
        
        chooser.setDialogTitle( "Open Rosters" );
        
        chooser.setMultiSelectionEnabled(true);
        
        int returnVal = chooser.showOpenDialog(null);
        
        Set<Roster> s = new HashSet<Roster>();
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                File[] files = chooser.getSelectedFiles();
                
                for(File file : files) {
                    Roster d = open(file);
                    if ( d != null && d instanceof Roster ){
                        s.add(d);
                    }
                    else {
                        JOptionPane.showMessageDialog(null,
                        "An Error Occurred while opening:\n" + "Incompatible File Type",
                        "Error",
                        JOptionPane.ERROR_MESSAGE); 
                    }
                }
            }
            catch (Exception exc) {
                ExceptionWizard.postException(exc);
//                JOptionPane.showMessageDialog(null,
//                "An Error Occurred while opening:\n" +
//                exc.toString(),
//                "Error",
//                JOptionPane.ERROR_MESSAGE);
            }
        }
        return s.toArray(new Roster[0] );
    }
    
    /**
     * @param battle
     */    
    public void setBattle(Battle battle) {
        if ( battle != this.battle ) {
            Battle oldBattle = this.battle;
            this.battle = battle;
            
            firePropertyChange("Battle",oldBattle, battle);
        }
    }
    
    /**
     * @return
     */    
    public Battle getBattle() {
        return battle;
    }
    
    /**
     * @param d
     */    
    public void setColumnList(ColumnList d ) {
        columnList = d;
    }
    
    /**
     * @return
     */    
    public ColumnList getColumnList() {
        return columnList;
    }
    
    /**
     * @return
     */    
    public Profile getRosterProfile() {
        if ( profileName != null ) {
            return ProfileManager.getProfile(profileName);
        }
        return null;
    }
    
    /**
     * @param name
     */    
    public void setRosterProfile(String name) {
        profileName = name;
    }
    
    public String toString() {
        return name;
    }
    
   /* public void setWindowInformation(Rectangle r, Double d, Point p) {
        layoutBounds = r;
        frameID = d;
        frameLocation = p;
    }
    
    public Rectangle getLayoutBounds() {
        return layoutBounds;
    }
    
    public Double getFrameID() {
        return frameID;
    }
    
    public Point getFrameLocation() {
        return frameLocation;
    } */
    
}