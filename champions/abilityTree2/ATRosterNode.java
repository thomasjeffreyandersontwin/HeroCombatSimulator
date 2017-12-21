/*
 * PADTargetNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.abilityTree2;

import champions.Battle;
import champions.DetailList;
import champions.ObjectTarget;
import champions.PADRoster;
import champions.Character;
import champions.Profile;
import champions.ProfileManager;
import champions.Roster;
import champions.RosterDockingPanel;
import champions.Target;
import champions.TargetList;
import champions.event.RosterAddEvent;
import champions.event.RosterRemoveEvent;

import java.util.ArrayList;
import java.util.Comparator;
import tjava.Filter;
import champions.interfaces.RosterListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.TreePath;

import VirtualDesktop.Legacy.MobActions;
import treeTable.DefaultTreeTableModel;
import treeTable.TreeTable;
import treeTable.TreeTableColumnModel;



/**
 *
 * @author  twalker
 * @version
 *
 * The ATTargetNode is the root node for a list of all possible abilities
 * and disadvantages that can be used by the target.  This includes the
 * abilities owned by the target and the default abilities.
 *
 * The ATTargetNode2 should be used in situations where listing of the abilities
 * of a target, not including the default abilities, and any target specific
 * actions are needed.
 */
public class ATRosterNode extends ATNode implements RosterListener {
    
    /** Holds value of property target. */
    private Roster roster;   
    
    private AbstractAction newCharacterAction, openTargetAction, saveRosterAction, spawnRosterAction,closeRosterAction;
    private AbstractAction saveAsRosterAction, newObjectAction;
    private Action addToBattleAction, removeFromBattleAction, toggleMobMode;
    private JMenu newMenu;
    
    private int characterCount;
    private int deadCount;
    private int unconsciousCount;
    
    
    
//    /** Creates new PADTargetNode */
//    public ATRosterNode(Roster roster) {
//        this(roster, null, false);
//    }
    
    public ATRosterNode(Roster roster, ATNodeFactory nodeFactory, Filter<Object> nodeFilter, boolean pruned) {
        super(nodeFactory, nodeFilter, pruned);
        setup(roster);
        
    }
    
    public void setup(Roster roster) {
        this.setPruned(pruned);
       // this.nodeFilter = nodeFilter;
        this.setRoster(roster);
        setIcon( UIManager.getIcon("AbilityTree.rosterIcon"));
        setupActions();
        buildNode(); 
    }
    
    public void setupActions() {
        newCharacterAction = new AbstractAction("Character...") {
            public void actionPerformed(ActionEvent e) {
                if ( roster != null ) {
                    Character c = new Character("New Character");
                    getRoster().add(c);
                    c.editTarget();
                    VirtualDesktop.Legacy.MessageExporter.exportEvent("New Character",c,getRoster());
                }
            }
        };
        
        newObjectAction = new AbstractAction("Object...") {
            public void actionPerformed(ActionEvent e) {
                if ( roster != null ) {
                    ObjectTarget c = new ObjectTarget("New Object");
                    roster.add(c);
                    c.editTarget();
                    VirtualDesktop.Legacy.MessageExporter.exportEvent("New Object",c,getRoster());
                }
            }
        };
        
        
        
        newMenu = new JMenu("New");
        newMenu.add(newCharacterAction);
        newMenu.add(newObjectAction);
        JMenu objectMenu = buildPresetTargetsMenu();
        if ( objectMenu != null )  newMenu.add(objectMenu);
        
        spawnRosterAction = new AbstractAction("Spawn Entire Roster") {
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
        
        openTargetAction = new AbstractAction("Open Character/Object...") {
            public void actionPerformed(ActionEvent e) {
                Roster myRoster = roster;
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
                            VirtualDesktop.Legacy.MessageExporter.exportEvent("Open Character",t,getRoster());
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
                	VirtualDesktop.Legacy.MessageExporter.exportEvent("Close Roster", null, roster);
                    Battle.currentBattle.removeRoster( roster );
                    
                }
                RosterDockingPanel rdp = RosterDockingPanel.getDefaultRosterDockingPanel();
                if ( roster != null && rdp != null ) {
                	VirtualDesktop.Legacy.MessageExporter.exportEvent("Close Roster", null, roster);
                    rdp.removeRoster(roster);
                }
            }
        };
        
        toggleMobMode = MobActions.GetMobModeToggleAction(roster);
        
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
                    getRoster().add( newTarget );
                    newTarget.editTarget();
                    VirtualDesktop.Legacy.MessageExporter.exportEvent("RosterAdd", newTarget, getRoster());
                }
            });
        }
        
        return popup;
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
    
    public void buildNode() {
        removeAndDestroyAllChildren();
        
        if ( getRoster() != null ) {
            int count = 0;
            List<Target> targets = roster.getCombatants();
            for(Target t : targets ) {
                if ( nodeFilter == null || nodeFilter.includeElement(t) ) {
                    ATNode n = nodeFactory.createTargetNode(t,false,nodeFilter,pruned);
                    if ( n != null ) {
                        add(n);
                        count++;
                    }
                }
            }
            
            ATNode ccn = nodeFactory.createCreateCharacterNode(getRoster(), nodeFilter, pruned);
            if ( ccn != null && (nodeFilter == null || nodeFilter.includeElement(ccn))) {
                add(ccn);
            }
            
            
            
            /*
              
             ATNode V = nodeFactory.createVirtualDesktopActionNode(roster, nodeFilter, pruned, "Show Camera");
            if ( V != null && (nodeFilter == null || nodeFilter.includeElement(V))) {
                add(V);
            }
            
            ATNode V1= nodeFactory.createVirtualDesktopActionNode(roster, nodeFilter, pruned, "Jump to Camera");
            if ( V != null && (nodeFilter == null || nodeFilter.includeElement(V1))) {
                add(V1);
            }
            
            ATNode V2 = nodeFactory.createVirtualDesktopActionNode(roster, nodeFilter, pruned, "Manuever With Camera");
            if ( V2 != null && (nodeFilter == null || nodeFilter.includeElement(V2))) {
                add(V2);
            }
            
            ATNode V3 = nodeFactory.createVirtualDesktopActionNode(roster, nodeFilter, pruned, "Target Active Character");
            if ( V != null && (nodeFilter == null || nodeFilter.includeElement(V3))) {
                add(V3);
            }
            
            ATNode V4 = nodeFactory.createVirtualDesktopActionNode(roster, nodeFilter, pruned, "Spawn Model For Active Character");
            if ( V4 != null && (nodeFilter == null || nodeFilter.includeElement(V4))) {
                add(V4);
            }
            */
            
            updateCounts(false);
            
            if ( model instanceof DefaultTreeTableModel ) {
                ((DefaultTreeTableModel)model).nodeStructureChanged(this);
            }
        }
    }
    
    public Object getValueAt(int column) {
        if ( roster != null ) {
            if ( column == ATColumn.NAME_COLUMN.ordinal() ) {
                return roster.getName() + "   (" + getCharacterCount() + " characters, " + getUnconsciousCount() + " unconscious, "+ getDeadCount() + " dead)";
            }
        }
        
        return null;
    }
   
    public int getColumnSpan(int column, TreeTableColumnModel columnModel) {
        if ( column == ATColumn.NAME_COLUMN.ordinal() ) {
            return ATColumn.MAX_COLUMNS.ordinal();
        }
        return 0;
    }
    
    public boolean isCellEditable(int column) {
        return false;
    }

    public void setValueAt(int column, Object aValue) {
        if ( roster != null && column == ATColumn.NAME_COLUMN.ordinal()&& aValue instanceof String ) {
                roster.setName( (String) aValue );
        }
    }

    
    /** Allows the node to provide context menu feedback.
     *
     * invokeMenu is called whenever an event occurs which would cause a context menu to be displayed.
     * If the node has context menu items to display, it should add them to the JPopupMenu <code>popup</code>
     * and return true.
     * @param treeTable Indicates the TreeTable which is currently displaying the node.
     * @param path The TreePath which the cursor was over when the context menu
     * event occurred.
     * @param popup The JPopupMenu which is being built.  Always non-null and initialized.
     * @return True if menus where added, False if menus weren't.
     */
    public boolean invokeMenu(TreeTable treeTable,TreePath path, JPopupMenu popup) {
        boolean rv = false;
        if ( path.getLastPathComponent() == this ) {
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
            popup.add(spawnRosterAction);
            
            toggleMobMode.setEnabled(true);
            popup.add(toggleMobMode);
            
            saveRosterAction.setEnabled( roster.getFile() != null);
            
//            if ( Battle.currentBattle != null ) {
//                popup.addSeparator();
//                if ( Battle.currentBattle.getRosters().contains( roster ) ) {
//                    popup.add(removeFromBattleAction);
//                }
//                else {
//                    popup.add(addToBattleAction);
//                }
//            }
            
            rv = true;
        }
        return rv;
    }

//    @Override
//    public Comparator getSortComparator(int columnIndex, boolean ascending) {
//        // Always sort the roster children in descending order?!?
//        return super.getSortComparator(columnIndex, false);
//    }



    
    
    protected void updateName() {
        //setUserObject(target.getName());
        if ( model instanceof DefaultTreeTableModel ) {
            ((DefaultTreeTableModel)model).nodeChanged(this);
        }
    }
    
    public void destroy() {
        super.destroy();
        
        setRoster(null);
    }
    
    public Roster getRoster() {
        return roster;
    }
    
    public void setRoster(Roster roster) {
        if ( this.roster != roster ) {
            if ( this.roster != null ) {
                this.roster.removeRosterListener(this);
            }
            
            this.roster = roster;
            
            if ( this.roster != null ) {
                this.roster.addRosterListener(this);
            }
            
            rebuildNode();
        }
        
    }
    
    public void rosterAdd(RosterAddEvent e) {
        rebuildNode();
    }
    
    public void rosterRemove(RosterRemoveEvent e) {
        rebuildNode();
    }
    
    public void rosterChange(ChangeEvent e) {
        updateName();
    }
    
    public void propertyChange(PropertyChangeEvent pce) {
        updateName();
    }

    public void updateTree() {
        updateCounts(true);
        
        //sortNode(getSortComparator(), true);
        
        super.updateTree();
    }

    protected void updateCounts(boolean fireUpdate) {
        if ( roster == null ) {
            setCharacterCount(0);
            setDeadCount(0);
            setUnconsciousCount(0);
        }
        else {
            int dead = 0;
            int uncon = 0;
            List<Target> list = roster.getCombatants();
            
            for(Target t : list) {
                if ( t.isDead() ) {
                    dead ++;
                }
                else if ( t.isUnconscious() ) {
                    uncon ++;
                }
            }
            
            setCharacterCount(list.size());
            setDeadCount(dead);
            setUnconsciousCount(uncon);
        }
        
        if ( fireUpdate ) {
            updateName();
        }
        
        
    }

    public int getCharacterCount() {
        return characterCount;
    }

    public void setCharacterCount(int characterCount) {
        if ( this.characterCount != characterCount ) {
        this.characterCount = characterCount;
        }
    }

    public int getDeadCount() {
        return deadCount;
    }

    public void setDeadCount(int deadCount) {
        if ( this.deadCount != deadCount ) {
            this.deadCount = deadCount;
        }
    }

    public int getUnconsciousCount() {
        return unconsciousCount;
    }

    public void setUnconsciousCount(int unconsciousCount) {
        if ( this.unconsciousCount != unconsciousCount ) {
        this.unconsciousCount = unconsciousCount;
        }
    }
     
}
