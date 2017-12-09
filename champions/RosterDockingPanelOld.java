/*
 * RosterDockingPanelOld.java
 *
 * Created on January 1, 2001, 5:49 PM
 */

package champions;

import tjava.ContextMenu;
import champions.event.RosterAddEvent;
import champions.event.RosterRemoveEvent;
import champions.event.SegmentAdvancedEvent;
import champions.event.SequenceChangedEvent;
import champions.event.TargetSelectedEvent;
import champions.interfaces.BattleListener;
import tjava.ContextMenuListener;
import champions.interfaces.RosterListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



/**
 *
 * @author  unknown
 */
public class RosterDockingPanelOld extends SavedDockingPanel
implements RosterListener, ContextMenuListener, BattleListener {
    
    private List<JScrollPane> scrollList = new ArrayList<JScrollPane>();
    private List<Roster> rosterList = new ArrayList<Roster>();
    private JTabbedPane tabbedPane = null;
    
    private static RosterDockingPanelOld rdp = null;
    
    private Action openRosterAction, newRosterAction;
    private HealRosterAction healRosterAction;
    private HealRosterNoBODYAction healRosterNoBODYAction;
    private HealAllAction healAllAction;
    private HealAllNoBODYAction healAllNoBODYAction;
    private JMenu healMenu;
    
    /**
     * Creates new form RosterDockingPanelOld
     */
    public RosterDockingPanelOld(String windowID) {
        super(windowID);
        initComponents();
        setupActions();
        SwingUtilities.updateComponentTreeUI(this);
        
        ContextMenu.addContextMenu(this);
        
        setName("Rosters");
        setFrameName("Hero Combat Simulator");
        
        setMinimizable(true);
        
        Battle.addBattleListener(this);
        //System.out.println(FlexibleLookAndFeel.dumpComponentNames(this));
    }
    
    public void setupActions() {
        openRosterAction = new AbstractAction( "Open Roster..." ) {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Add your handling code here:
                Roster roster = Roster.open();
                if ( roster != null ) {
                    Battle.currentBattle.addRoster(roster);
                    addRoster(roster);
                }
            }
        };
        
        newRosterAction = new AbstractAction( "New Roster" ) {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Add your handling code here:
                Roster roster = new Roster();
                Battle.currentBattle.addRoster(roster);
                addRoster(roster);
                
            }
        };
        
        healRosterAction = new RosterDockingPanelOld.HealRosterAction();
        healRosterNoBODYAction = new RosterDockingPanelOld.HealRosterNoBODYAction();
        healAllAction = new RosterDockingPanelOld.HealAllAction();
        healAllNoBODYAction = new RosterDockingPanelOld.HealAllNoBODYAction();
        
  /*  addRosterAction = new AbstractAction( "Add Roster..." ) {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
   
            Roster roster = Roster.open();
            if ( roster != null ) {
                Battle.currentBattle.addRoster(roster);
                addRoster(roster);
            }
        }
    };  */
    }
    
    public static RosterDockingPanelOld getDefaultRosterDockingPanel() {
        if ( rdp == null ) rdp = new RosterDockingPanelOld( "rosterDP" );
        return rdp;
    }
    
    /** Setter for property roster.
     * @param roster New value of property roster.
     */
    public void addRoster(Roster roster) {
        
        if ( rosterList.contains(roster) == false) {
            JScrollPane jsp = new JScrollPane();
            TargetButtonPanel rp = new TargetButtonPanel();
            rp.setRoster(roster);
            jsp.setViewportView(rp);

            if ( rosterList.size() == 0 ) {
                rostersPanel.add( jsp, BorderLayout.CENTER );
            }
            else if ( rosterList.size() == 1 ) {
                rostersPanel.remove( scrollList.get(0) );
                if ( tabbedPane == null ) {
                    tabbedPane = new JTabbedPane();
                    tabbedPane.setOpaque(false);
                    tabbedPane.setTabPlacement( SwingConstants.BOTTOM );
                    tabbedPane.addChangeListener( new ChangeListener() {
                        public void stateChanged(ChangeEvent e) {
                            switchTitle();
                        }
                    });
                }
                rostersPanel.add( tabbedPane, BorderLayout.CENTER );
                tabbedPane.addTab( rosterList.get(0).getName(), scrollList.get(0) );
                tabbedPane.addTab( roster.getName(), jsp );
            }
            else {
                tabbedPane.addTab( roster.getName(), jsp );
            }

            revalidate();
            repaint();

            scrollList.add(jsp);
            rosterList.add(roster);

            roster.addRosterListener(this);

            switchTitle();

            SwingUtilities.updateComponentTreeUI(this);
        }
        //System.out.println(FlexibleLookAndFeel.dumpComponentNames(this));
    }
    
    public void removeRoster(Roster roster) {
        
        // ToDo: Need to destroy the TargetButtonPanel for this panel also...
        
        int index = rosterList.indexOf(roster);
        if ( index != -1 ) {
            if ( rosterList.size() == 1 ) {
                rostersPanel.remove( scrollList.get(0) );
            }
            else if ( rosterList.size() == 2 ) {
                //tabbedPane.removeTabAt( index );
                rostersPanel.remove( tabbedPane );
                tabbedPane.removeAll();
                rostersPanel.add( scrollList.get( index == 0 ? 1 : 0 ));
                
            }
            else {
                tabbedPane.removeTabAt( index );
            }
            
            rosterList.remove(index);
            JScrollPane jsp = scrollList.remove(index);
            
            if ( jsp != null ) {
                TargetButtonPanel tbp = TargetButtonPanel.class.cast( jsp.getViewport().getView() );
                if ( tbp != null ) {
                    tbp.destroy();
                }
            }
            
            roster.removeRosterListener(this);
            switchTitle();
            
            revalidate();
            repaint();
        }
    }
    
    public void updateTitle(Roster source) {
        int index;
        String title;
        
        if ( rosterList.size() >= 2 ) {
            index = rosterList.indexOf( source );
            if ( index != -1 ) {
                tabbedPane.setTitleAt(index, source.getName() );
                
                if ( tabbedPane.getSelectedIndex() == index ) {
                    setTitle(source);
                }
            }
        }
        else {
            if ( rosterList.get(0) == source ) {
                setTitle(source);
            }
        }
    }
    
    
    public void switchTitle() {
        Roster roster = null;
        
        if ( rosterList.size() == 0 ) {
            setName("Rosters");
        }
        else if ( rosterList.size() == 1 ) {
            roster = (Roster)rosterList.get(0);
            setTitle(roster);
        }
        else if ( rosterList.size() > 0 ) {
            int index = tabbedPane.getSelectedIndex();
            if ( index != -1 ) {
                roster = (Roster)rosterList.get(index);
                setTitle(roster);
            }
        }
    }
    
    public void setTitle(Roster roster) {
        String title;
        if ( roster != null ) {
            if ( roster.getName() != null ) {
                title = "Roster: " + roster.getName();
            }
            else {
                title = "Roster: Unnamed";
            }
            
            if ( roster.getBattle() == Battle.currentBattle ) {
                title = title + "  (Participating)";
            }
            else {
                title = title + "  (Not Participating)";
            }
            setName(title);
        }
        else {
            setName( "Roster" );
        }
    }
    
    public Roster getCurrentRoster() {
        if ( rosterList.size() >= 2 ) {
            return (Roster) rosterList.get( tabbedPane.getSelectedIndex() );
        }
        else if ( rosterList.size() == 1 ) {
            return (Roster) rosterList.get( 0 );
        }
        else {
            return null;
        }
    }
    
    public void rosterAdd(RosterAddEvent e) {
    }
    
    public void rosterRemove(RosterRemoveEvent e) {
    }
    
    public void rosterChange(ChangeEvent e) {
    }
    
    public void propertyChange(PropertyChangeEvent pce) {
        if ( pce.getSource() instanceof Roster ) {
            updateTitle((Roster)pce.getSource());
        }
    }
    
    public boolean invokeMenu(JPopupMenu popup,Component inComponent,Point inPoint) {
        popup.add(newRosterAction);
        popup.add(openRosterAction);
        
        popup.addSeparator();
        
        
        healMenu = new JMenu("Roster Heal Options");
        Roster r = getCurrentRoster();
        if ( r != null ) {
            //healMenu.add(healMenu);
            healRosterAction.setRoster(r);
            healMenu.add(healRosterAction);

            //healMenu.add(healMenu);
            healRosterNoBODYAction.setRoster(r);
            healMenu.add(healRosterNoBODYAction);
        }
        if ( rosterList.size() > 0 ) {
            healMenu.add(healAllAction);
            healMenu.add(healAllNoBODYAction);
        }
        popup.add(healMenu);
        //        Roster r = getCurrentRoster();
        //        if ( r != null ) {
        //            healRosterAction.setRoster(r);
        //            popup.add(healRosterAction);
        //        }
        //
        //        if ( rosterList.size() > 0 ) {
        //            popup.add(healAllAction);
        //        }
        
        return true;
    }
    
    public void stateChanged(BattleChangeEvent e) {
       
        // Make sure all the rosters are registered....
        Set rosters = Battle.getCurrentBattle().getRosters();
        
        Iterator i = rosters.iterator();
        while(i.hasNext() ) {
            Roster r = (Roster)i.next();
            
            if ( rosterList.contains(r) == false) {
                addRoster(r);
            }
        }
    }
    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        rostersPanel = new javax.swing.JPanel();

        rostersPanel.setLayout(new java.awt.BorderLayout());

        rostersPanel.setName("Rosters");
        getContentPane().add(rostersPanel, java.awt.BorderLayout.CENTER);

    }

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

    public void processingChange(ChangeEvent event) {
    }

    public void processingChange(BattleChangeEvent event) {
    }
    // </editor-fold>//GEN-END:initComponents
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel rostersPanel;
    // End of variables declaration//GEN-END:variables
    
    /** Generate the BattleEvent containing a ExecuteHealRosterAction.
     */
    public static class HealRosterAction extends AbstractAction {
        Roster roster = null;
        
        public HealRosterAction() {
            super("Heal All in Roster");
        }
        
        public void setRoster(Roster roster) {
            this.roster = roster;
        }
        /** Invoked when an action occurs.
         *
         */
        public void actionPerformed(ActionEvent e) {
            if ( roster != null ) {
                ExecuteHealRosterAction a = new ExecuteHealRosterAction();
                BattleEvent battleEvent = new BattleEvent(a);
                a.setRoster(roster);
                a.setBattleEvent(battleEvent);
                
                Battle.getCurrentBattle().addEvent(battleEvent);
            }
        }
        
        
    }
    
    public static class ExecuteHealRosterAction extends AbstractAction {
        BattleEvent battleEvent = null;
        Roster roster = null;
        
        public ExecuteHealRosterAction() {
            super("Heal All in Roster");
            
        }
        
        public void setBattleEvent(BattleEvent be) {
            battleEvent = be;
        }
        
        public void setRoster(Roster roster) {
            this.roster = roster;
        }
        
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            List<Target> targets = roster.getCombatants();
            for (int index = 0; index < targets.size(); index++) {
                Target t = (Target) targets.get(index);
                t.healCompletely(battleEvent);
            }
        }
    }
    
    /** Generate the BattleEvent containing a ExecuteHealRosterAction.
     */
    public static class HealRosterNoBODYAction extends AbstractAction {
        Roster roster = null;
        
        public HealRosterNoBODYAction() {
            super("Heal All in Roster (BODY Ignored)");
        }
        
        public void setRoster(Roster roster) {
            this.roster = roster;
        }
        /** Invoked when an action occurs.
         *
         */
        public void actionPerformed(ActionEvent e) {
            if ( roster != null ) {
                ExecuteHealRosterNoBODYAction a = new ExecuteHealRosterNoBODYAction();
                BattleEvent battleEvent = new BattleEvent(a);
                a.setRoster(roster);
                a.setBattleEvent(battleEvent);
                
                Battle.getCurrentBattle().addEvent(battleEvent);
            }
        }
        
        
    }
    
    public static class ExecuteHealRosterNoBODYAction extends AbstractAction {
        BattleEvent battleEvent = null;
        Roster roster = null;
        
        public ExecuteHealRosterNoBODYAction() {
            super("Heal All in Roster (BODY Ignored)");
            
        }
        
        public void setBattleEvent(BattleEvent be) {
            battleEvent = be;
        }
        
        public void setRoster(Roster roster) {
            this.roster = roster;
        }
        
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            List<Target> targets = roster.getCombatants();
            for (int index = 0; index < targets.size(); index++) {
                Target t = targets.get(index);
                t.healNoBODY(battleEvent);
            }
        }
    }
    
    public static class HealAllAction extends AbstractAction {
        
        public HealAllAction() {
            super("Heal All Rosters");
        }
        /** Invoked when an action occurs.
         *
         */
        public void actionPerformed(ActionEvent e) {
            ExecuteHealAllAction a = new ExecuteHealAllAction();
            BattleEvent battleEvent = new BattleEvent(a);
            a.setBattleEvent(battleEvent);
            
            Battle.getCurrentBattle().addEvent(battleEvent);
        }
    }
    
    public static class ExecuteHealAllAction extends AbstractAction {
        BattleEvent battleEvent = null;
        
        public ExecuteHealAllAction() {
            super("Heal All Rosters");
            
        }
        
        public void setBattleEvent(BattleEvent be) {
            battleEvent = be;
        }
        
        
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            Set<Roster> rosters = Battle.getCurrentBattle().getRosters();
            Iterator<Roster> i = rosters.iterator();
            while ( i.hasNext() ) {
                Roster roster = i.next();
                if ( roster != null ) {
                    List<Target> targets = roster.getCombatants();
                    for (int index = 0; index < targets.size(); index++) {
                        Target t = targets.get(index);
                        t.healCompletely(battleEvent);
                        
                        
                    }
                }
            }
        }
    }
    
    public static class HealAllNoBODYAction extends AbstractAction {
        
        public HealAllNoBODYAction() {
            super("Heal All Rosters (BODY Ignored)");
        }
        /** Invoked when an action occurs.
         *
         */
        public void actionPerformed(ActionEvent e) {
            ExecuteHealAllNoBODYAction a = new ExecuteHealAllNoBODYAction();
            BattleEvent battleEvent = new BattleEvent(a);
            a.setBattleEvent(battleEvent);
            
            Battle.getCurrentBattle().addEvent(battleEvent);
        }
    }
    
    public static class ExecuteHealAllNoBODYAction extends AbstractAction {
        BattleEvent battleEvent = null;
        
        public ExecuteHealAllNoBODYAction() {
            super("Heal All Rosters (BODY Ignored");
            
        }
        
        public void setBattleEvent(BattleEvent be) {
            battleEvent = be;
        }
        
        
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            Set<Roster> rosters = Battle.getCurrentBattle().getRosters();
            Iterator<Roster> i = rosters.iterator();
            while ( i.hasNext() ) {
                Roster roster = i.next();
                if ( roster != null ) {
                    List<Target> targets = roster.getCombatants();
                    for (int index = 0; index < targets.size(); index++) {
                        Target t = targets.get(index);
                        t.healNoBODY(battleEvent);
                        
                        
                    }
                }
            }
        }
    }
}