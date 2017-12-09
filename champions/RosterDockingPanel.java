/*
 * RosterDockingPanel.java
 *
 * Created on January 1, 2001, 5:49 PM
 */

package champions;

import tjava.ContextMenu;
import champions.abilityTree2.ATRosterNode;
import champions.abilityTree2.ATTree;
import champions.event.RosterAddEvent;
import champions.event.RosterRemoveEvent;
import champions.event.SegmentAdvancedEvent;
import champions.event.SequenceChangedEvent;
import champions.event.TargetSelectedEvent;
import champions.interfaces.BattleListener;
import tjava.ContextMenuListener;
import champions.interfaces.RosterListener;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.TreePath;

/**
 *
 * @author  unknown
 */
public class RosterDockingPanel extends SavedDockingPanel
implements ContextMenuListener, BattleListener {
    
    //private List<JScrollPane> scrollList = new ArrayList<JScrollPane>();
    //private List<Roster> rosterList = new ArrayList<Roster>();
    //private JTabbedPane tabbedPane = null;
    
    private static RosterDockingPanel rdp = null;
    

    
    /**
     * Creates new form RosterDockingPanel
     */
    public RosterDockingPanel(String windowID) {
        super(windowID);
        initComponents();
       // setupActions();
        SwingUtilities.updateComponentTreeUI(this);
        
        ContextMenu.addContextMenu(this);
        
        setName("Rosters");
        setFrameName("Hero Combat Simulator");
        
        setMinimizable(true);
        
       // Battle.addBattleListener(this);
    }
    
    
    
    public static RosterDockingPanel getDefaultRosterDockingPanel() {
        if ( rdp == null ) rdp = new RosterDockingPanel( "rosterDP" );
        return rdp;
    }
    
    /** Setter for property roster.
     * @param roster New value of property roster.
     */
    public void addRoster(Roster roster) {
        
//        if ( rosterList.contains(roster) == false) {
//            JScrollPane jsp = new JScrollPane();
//            TargetButtonPanel rp = new TargetButtonPanel();
//            rp.setRoster(roster);
//            jsp.setViewportView(rp);
//
//            if ( rosterList.size() == 0 ) {
//                rostersPanel.add( jsp, BorderLayout.CENTER );
//            }
//            else if ( rosterList.size() == 1 ) {
//                rostersPanel.remove( scrollList.get(0) );
//                if ( tabbedPane == null ) {
//                    tabbedPane = new JTabbedPane();
//                    tabbedPane.setOpaque(false);
//                    tabbedPane.setTabPlacement( SwingConstants.BOTTOM );
//                    tabbedPane.addChangeListener( new ChangeListener() {
//                        public void stateChanged(ChangeEvent e) {
//                            switchTitle();
//                        }
//                    });
//                }
//                rostersPanel.add( tabbedPane, BorderLayout.CENTER );
//                tabbedPane.addTab( rosterList.get(0).getName(), scrollList.get(0) );
//                tabbedPane.addTab( roster.getName(), jsp );
//            }
//            else {
//                tabbedPane.addTab( roster.getName(), jsp );
//            }
//
//            revalidate();
//            repaint();
//
//            scrollList.add(jsp);
//            rosterList.add(roster);
//
//            roster.addRosterListener(this);
//
//            switchTitle();
//
//            SwingUtilities.updateComponentTreeUI(this);
//        }
        //System.out.println(FlexibleLookAndFeel.dumpComponentNames(this));
    }
    
    public void removeRoster(Roster roster) {
        
//        // ToDo: Need to destroy the TargetButtonPanel for this panel also...
//        
//        int index = rosterList.indexOf(roster);
//        if ( index != -1 ) {
//            if ( rosterList.size() == 1 ) {
//                rostersPanel.remove( scrollList.get(0) );
//            }
//            else if ( rosterList.size() == 2 ) {
//                //tabbedPane.removeTabAt( index );
//                rostersPanel.remove( tabbedPane );
//                tabbedPane.removeAll();
//                rostersPanel.add( scrollList.get( index == 0 ? 1 : 0 ));
//                
//            }
//            else {
//                tabbedPane.removeTabAt( index );
//            }
//            
//            rosterList.remove(index);
//            JScrollPane jsp = scrollList.remove(index);
//            
//            if ( jsp != null ) {
//                TargetButtonPanel tbp = TargetButtonPanel.class.cast( jsp.getViewport().getView() );
//                if ( tbp != null ) {
//                    tbp.destroy();
//                }
//            }
//            
//            roster.removeRosterListener(this);
//            switchTitle();
//            
//            revalidate();
//            repaint();
//        }
    }
    
//    public void updateTitle(Roster source) {
//        int index;
//        String title;
//        
//        if ( rosterList.size() >= 2 ) {
//            index = rosterList.indexOf( source );
//            if ( index != -1 ) {
//                tabbedPane.setTitleAt(index, source.getName() );
//                
//                if ( tabbedPane.getSelectedIndex() == index ) {
//                    setTitle(source);
//                }
//            }
//        }
//        else {
//            if ( rosterList.get(0) == source ) {
//                setTitle(source);
//            }
//        }
//    }
    
    
//    public void switchTitle() {
//        Roster roster = null;
//        
//        if ( rosterList.size() == 0 ) {
//            setName("Rosters");
//        }
//        else if ( rosterList.size() == 1 ) {
//            roster = (Roster)rosterList.get(0);
//            setTitle(roster);
//        }
//        else if ( rosterList.size() > 0 ) {
//            int index = tabbedPane.getSelectedIndex();
//            if ( index != -1 ) {
//                roster = (Roster)rosterList.get(index);
//                setTitle(roster);
//            }
//        }
//    }
    
//    public void setTitle(Roster roster) {
//        String title;
//        if ( roster != null ) {
//            if ( roster.getName() != null ) {
//                title = "Roster: " + roster.getName();
//            }
//            else {
//                title = "Roster: Unnamed";
//            }
//            
//            if ( roster.getBattle() == Battle.currentBattle ) {
//                title = title + "  (Participating)";
//            }
//            else {
//                title = title + "  (Not Participating)";
//            }
//            setName(title);
//        }
//        else {
//            setName( "Roster" );
//        }
//    }
    
//    public Roster getCurrentRoster() {
//        if ( rosterList.size() >= 2 ) {
//            return (Roster) rosterList.get( tabbedPane.getSelectedIndex() );
//        }
//        else if ( rosterList.size() == 1 ) {
//            return (Roster) rosterList.get( 0 );
//        }
//        else {
//            return null;
//        }
//    }
    
//    public void rosterAdd(RosterAddEvent e) {
//    }
//    
//    public void rosterRemove(RosterRemoveEvent e) {
//    }
//    
//    public void rosterChange(ChangeEvent e) {
//    }
    
//    public void propertyChange(PropertyChangeEvent pce) {
//        if ( pce.getSource() instanceof Roster ) {
//            updateTitle((Roster)pce.getSource());
//        }
//    }
    
    public boolean invokeMenu(JPopupMenu popup,Component inComponent,Point inPoint) {
        return false;
    }
    
    public void stateChanged(BattleChangeEvent e) {
       
//        // Make sure all the rosters are registered....
//        Set rosters = Battle.getCurrentBattle().getRosters();
//        
//        Iterator i = rosters.iterator();
//        while(i.hasNext() ) {
//            Roster r = (Roster)i.next();
//            
//            if ( rosterList.contains(r) == false) {
//                addRoster(r);
//            }
//        }
    }
    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        targetsPanel1 = new champions.TargetsPanel();

        getContentPane().add(targetsPanel1, java.awt.BorderLayout.CENTER);

    }// </editor-fold>//GEN-END:initComponents

    public void battleTargetSelected(TargetSelectedEvent event) {
    }

    public void battleSegmentAdvanced(SegmentAdvancedEvent event) {
    }

    public void battleSequenceChanged(SequenceChangedEvent event) {
    }

    public void eventNotification(ChangeEvent event) {
    }

    public void combatStateChange(ChangeEvent event) {
    }

    public void processingChange(BattleChangeEvent event) {
    }
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private champions.TargetsPanel targetsPanel1;
    // End of variables declaration//GEN-END:variables
    
    
}