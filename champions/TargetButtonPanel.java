/*
 * TargetButtonPanel.java
 *
 * Created on October 22, 2000, 5:47 PM
 */

package champions;

import champions.ColumnLayout;
import tjava.ContextMenu;
import champions.event.RosterAddEvent;
import champions.event.RosterRemoveEvent;
import tjava.ContextMenuListener;
import champions.interfaces.RosterListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import tjava.Destroyable;



/**
 *
 * @author  unknown
 * @version
 */ 
public class TargetButtonPanel extends JPanel
    implements RosterListener, ContextMenuListener, Destroyable {
    
    /** Holds value of property roster. */
    private Roster roster;
    /** Creates new TargetButtonPanel */
    private List<TargetButton2> buttonVector = new ArrayList<TargetButton2>();
    
    private JPanel buttonPanel;
    private TargetPanelHeader header;
   // private ContextMenu contextMenu = new ContextMenu();
    
    private AbstractAction newCharacterAction, openTargetAction, saveRosterAction, closeRosterAction;
    private AbstractAction saveAsRosterAction, newObjectAction;
    private Action addToBattleAction, removeFromBattleAction, spawnRosterAction;
    private JMenu newMenu;
    
    /** Holds value of property shadow. */
    private Color shadow;
    
    public TargetButtonPanel() {
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints constraint = new GridBagConstraints();
        
        this.setLayout( gbl );
        
        
        header = new TargetPanelHeader() ;
        header.setName("Header");
        
        constraint.weightx = 1;
        constraint.fill=GridBagConstraints.BOTH;
        constraint.gridwidth=GridBagConstraints.REMAINDER;
        constraint.insets = new Insets(0,12,0,0);
        gbl.setConstraints(header,constraint);
        
        header.setResizable(true);
        this.add(header);
        
        constraint.insets = new Insets(0,0,0,0);
        constraint.weightx = 1.0;
        constraint.weighty = 1.0;
        buttonPanel = new JPanel();
        buttonPanel.setName("Roster");
        ColumnLayout cl= new ColumnLayout(1, -1, 4, 0);
        
        buttonPanel.setLayout( cl);
        
        constraint.gridheight = GridBagConstraints.REMAINDER;
        gbl.setConstraints(buttonPanel,constraint);
        
        this.add(buttonPanel);
        
        //this.addMouseListener( contextMenu );
        ContextMenu.addContextMenu(this);
        ContextMenu.addContextMenu(buttonPanel);
        
        setupActions();
        
    }
    
    public void setupActions() {
        newCharacterAction = new AbstractAction("Character...") {
            public void actionPerformed(ActionEvent e) {
                if ( roster != null ) {
                    Character c = new Character("New Character");
                    roster.add(c);
                    c.editTarget();
                }
            }
        };
        
        newObjectAction = new AbstractAction("Object...") {
            public void actionPerformed(ActionEvent e) {
                if ( roster != null ) {
                    ObjectTarget c = new ObjectTarget("New Object");
                    roster.add(c);
                    c.editTarget();
                }
            }
        };
        
        newMenu = new JMenu("New");
        newMenu.add(newCharacterAction);
        newMenu.add(newObjectAction);
        JMenu objectMenu = buildPresetTargetsMenu();
        if ( objectMenu != null )  newMenu.add(objectMenu);
        
        openTargetAction = new AbstractAction("Open Character/Object...") {
            public void actionPerformed(ActionEvent e) {
                if ( roster != null ) {
                    DetailList[] targets = DetailList.openMultiple( new String[] {"hcs","tgt"}, "Characters", Target.class );
                    for(DetailList dl : targets) {
                        if ( dl instanceof Target ) {
                            Target t = (Target)dl;
                            if ( Battle.getCurrentBattle() != null ) {
                                if ( Battle.getCurrentBattle().checkUnique( t ) ) {
                                    roster.add(t);
                                }
                            }
                            else {
                                roster.add(t);
                            }
                        }
                    }
                }
            }
        };
        
        saveRosterAction = new AbstractAction("Save Roster") {
            public void actionPerformed(ActionEvent e) {
                if ( roster != null ) {
                    try {
                        roster.save();
                    }
                    catch (Exception exc) {
                        JOptionPane.showMessageDialog(null,
                        "An Error Occurred while saving roster:\n" +
                        exc.toString(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };
        
        saveAsRosterAction = new AbstractAction("Save Roster As...") {
            public void actionPerformed(ActionEvent e) {
                if ( roster != null ) {
                    try {
                        roster.save(null);
                    }
                    catch (Exception exc) {
                        JOptionPane.showMessageDialog(null,
                        "An Error Occurred while saving roster:\n" +
                        exc.toString(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };
        
        closeRosterAction = new AbstractAction("Close Roster") {
            public void actionPerformed(ActionEvent e) {
                if ( roster != null && Battle.currentBattle != null) {
                    Battle.currentBattle.removeRoster( roster );
                }
                RosterDockingPanel rdp = RosterDockingPanel.getDefaultRosterDockingPanel();
                if ( roster != null && rdp != null ) {
                    rdp.removeRoster(roster);
                }
            }
        };
        
        addToBattleAction = new AbstractAction("Add to Battle") {
            public void actionPerformed(ActionEvent e) {
                if ( roster != null && Battle.currentBattle != null) {
                    Battle.currentBattle.addRoster( roster );
                }
            }
        };
        
        removeFromBattleAction = new AbstractAction("Remove From Battle") {
            public void actionPerformed(ActionEvent e) {
                if ( roster != null && Battle.currentBattle != null) {
                    Battle.currentBattle.removeRoster( roster );
                }
            }
        };
        
        spawnRosterAction = new AbstractAction("Spawn Entire Roster") {
        	/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			Roster myRoster = roster;
        	public void actionPerformed(ActionEvent e) {
        		if ( myRoster != null ) {
        			
                    DetailList[] targets = DetailList.openMultiple( new String[] {"hcs","tgt"}, "Characters", Target.class );
                    for(DetailList dl : targets) {
                        if ( dl instanceof Target ) {
                            Target t = (Target)dl;
                            if ( Battle.getCurrentBattle() != null ) {
                                if ( Battle.getCurrentBattle().checkUnique( t ) ) {
                                    myRoster.add(t);
                                }
                            }
                            else {
                                myRoster.add(t);
                            }
                        }
                    }
                }
            }	
        	
        };
    }
    
    private JMenu buildPresetTargetsMenu() {
        TargetList tl = PADRoster.getPresetTargets();
        return buildPresetTargetsMenu(tl);
    }
    
    private JMenu buildPresetTargetsMenu(TargetList tl) {
        if ( tl == null ) return null;
        
        JMenu popup = new JMenu(tl.getName());
        
        int count = tl.getSublistCount();
        for(int index = 0; index < count; index++) {
            popup.add( buildPresetTargetsMenu( tl.getSublist(index) ) );
        }
        
        Iterator it = tl.getTargets(false);
        while(it.hasNext()) {
            final Target t = (Target)it.next();
            popup.add( new AbstractAction( t.getName() ) {
                public void actionPerformed( ActionEvent evt) {
                    Target newTarget = (Target)t.clone();
                    TargetButtonPanel.this.getRoster().add( newTarget );
                    newTarget.editTarget();
                }
            });
        }
        
        return popup;
    }
    
    /** Getter for property roster.
     * @return Value of property roster.
     */
    public Roster getRoster() {
        return roster;
    }
    /** Setter for property roster.
     * @param roster New value of property roster.
     */
    public void setRoster(Roster roster) {
        if ( roster == this.roster ) return;
        if ( this.roster != null ) {
            roster.removeRosterListener( this );
        }
        
        this.roster = roster;
        if ( roster.getColumnList() == null ) {
            roster.setColumnList( header.getColumnList() );
        }
        else {
            header.setColumnList( roster.getColumnList() );
        }
        
        if ( this.roster != null ) {
            roster.addRosterListener( this );
        }
        adjustTargets();
    }
    
    public void adjustTargets() {
        TargetButton2 targetButton;
        int i=0;
        if ( roster != null ) {
            
            for (i=0; i < roster.getSize(); i++) {
                if ( buttonVector.size() <= i ) {
                    targetButton =  new TargetButton2();
                    targetButton.setStatList( roster.getColumnList() );
                    targetButton.setForeground( getForeground() );
                    targetButton.setBackground( getBackground() );
                    targetButton.setShadow( getShadow() );
                    buttonVector.add(targetButton);
                    buttonPanel.add(targetButton);
                    SwingUtilities.updateComponentTreeUI(targetButton);
                    
                }
                targetButton = (TargetButton2)buttonVector.get(i);
                targetButton.setTarget( (Target)roster.get(i) );
               // targetButton.addMouseListener(contextMenu);
                ContextMenu.addContextMenu(targetButton);
                targetButton.setVisible( true );
                targetButton.repaint();
            }
        }
        for (; i < buttonVector.size(); i++ ) {
            targetButton = (TargetButton2)buttonVector.get(i);
            targetButton.setVisible(false);
        }
        
        this.revalidate();
    }
    public void rosterAdd(RosterAddEvent e) {
        adjustTargets();
    }
    
    public void rosterRemove(RosterRemoveEvent e) {
        adjustTargets();
    }
    
    public void rosterChange(ChangeEvent e) {
    }
    
    public boolean invokeMenu(JPopupMenu popup,Component inComponent,Point inPoint) {
        if ( roster != null ) {
            
            Component c = getComponentAt(inPoint);
            if ( c == buttonPanel ) {
                c = c.getComponentAt( inPoint.x - c.getX() , inPoint.y - c.getY() );
                if ( c != null &&  c.getClass() == TargetButton2.class && c.isVisible() ) {
                    Target t = ((TargetButton2)c).getTarget();
                    if ( t != null ) {
                        popup.add( new RemoveTargetAction(roster,t));
                    }
                }
            }
            
           // popup.add(newCharacterAction);
            popup.add(newMenu);
            popup.add(openTargetAction);
            popup.addSeparator();
            popup.add(saveRosterAction);
            popup.add(saveAsRosterAction);
            
            // Build and Add the Roster Profile option
            popup.addSeparator();
            popup.add( buildProfileMenu() );
            popup.addSeparator();
            popup.add(closeRosterAction);
            
            saveRosterAction.setEnabled( roster.getFile() != null);
            
            if ( Battle.currentBattle != null ) {
                popup.addSeparator();
                if ( Battle.currentBattle.getRosters().contains( roster ) ) {
                    popup.add(removeFromBattleAction);
                }
                else {
                    popup.add(addToBattleAction);
                }
            }
            
            popup.addSeparator();
            popup.add(spawnRosterAction);
            return true;
        }
        return false;
    }
    
    private JMenu buildProfileMenu() {
        JMenu profileMenu = new JMenu("Roster Profile");
        
        Object[] profiles = ProfileManager.getProfileList();
        if ( profiles.length > 0 ) {
            for(int index = 0; index < profiles.length; index++) {
                final String profileName = (String)profiles[index];
                JCheckBoxMenuItem mi = new JCheckBoxMenuItem();
                mi.setAction( new AbstractAction() {
                   public void actionPerformed(ActionEvent evt) {
                        roster.setRosterProfile( profileName );
                   }
                });
                mi.setText(profileName);
                Profile rosterProfile = roster.getRosterProfile();
                if ( rosterProfile != null && profileName.equals(rosterProfile.getName()) ) {
                    mi.setSelected(true);
                }
                profileMenu.add(mi);
            }
        }
        else {
            JMenuItem mi = new JMenuItem("No Profiles Avialable");
            mi.setEnabled(false);
            profileMenu.add(mi);
        }
        
        return profileMenu;
    }
    
    public void propertyChange(PropertyChangeEvent pce) {
    }
    
    /** Getter for property shadow.
     * @return Value of property shadow.
     */
    public Color getShadow() {
        return shadow;
    }
    
    /** Setter for property shadow.
     * @param shadow New value of property shadow.
     */
    public void setShadow(Color shadow) {
        if ( this.shadow != shadow ) {
            this.shadow = shadow;
            if ( buttonVector != null ) {
                Iterator i = buttonVector.iterator();
                while ( i.hasNext() ) {
                    ((TargetButton2)i.next()).setShadow(shadow);
                }
            }
        }
    }
    
    public void setBackground(Color bg) {
        if ( getBackground() != bg ) {
            super.setBackground(bg);
             if ( buttonVector != null ) {
                Iterator i = buttonVector.iterator();
                while ( i.hasNext() ) {
                    ((TargetButton2)i.next()).setBackground(bg);
                }
            }
        }    
    }
    
    public void setForeground(Color fg) {
        if ( getForeground() != fg ) {
            super.setForeground(fg);
            if ( buttonVector != null ) {
                Iterator i = buttonVector.iterator();
                while ( i.hasNext() ) {
                    ((TargetButton2)i.next()).setForeground(fg);
                }
            }
        }
    }

    public void destroy() {
        for(TargetButton2 tb2 : buttonVector) {
            if ( tb2 != null ) tb2.destroy();
        }
    }
    
    public static class RemoveTargetAction extends AbstractAction {
        private Target target;
        private Roster roster;
        public RemoveTargetAction(Roster r, Target t) {
            super("Remove Character...");
            target = t;
            roster = r;
        }
        
        public void actionPerformed(ActionEvent e ) {
            
            if ( target != null && roster != null ) {
                
                int result = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to remove the chracter " + target.getName() + "?",
                "Remove Character?",
                JOptionPane.OK_CANCEL_OPTION);
                
                if ( result == JOptionPane.OK_OPTION ) {
                    roster.remove( target );
                }
            }
        }
    }
}