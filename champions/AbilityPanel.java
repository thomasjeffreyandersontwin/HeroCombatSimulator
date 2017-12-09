/*
 * AbilityPanel.java
 *
 * Created on January 27, 2001, 4:27 PMtargetMovePanel
 */

package champions;
import champions.abilityTree2.ATAbilityFilter;
import champions.abilityTree2.ATSingleTargetTree;
import champions.event.SegmentAdvancedEvent;
import champions.event.SequenceChangedEvent;
import champions.event.TargetSelectedEvent;
import champions.filters.AndFilter;
import champions.filters.DisadvantageAbilityFilter;
import champions.filters.MartialArtsAbilityFilter;
import champions.filters.MovementAbilityFilter;
import champions.filters.PowerAbilityFilter;
import champions.filters.SkillAbilityFilter;
import champions.interfaces.AbilityFilter;
import champions.interfaces.AbilityList;
import champions.interfaces.BattleListener;
import dockable.DockingPanel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

/**
 *
 * @author  unknown
 * @version
 */
public class AbilityPanel extends JPanel 
        implements BattleListener, TreeModelListener {
    
    private static final int DEBUG = 0;
    
  //  protected ColumnHeaderPanel targetHeaderPanel2, defaultHeaderPanel, targetMoveHeader, targetSkillHeader, targetDisadvantageHeader;
    
  //  protected Icon defaultPortrait;
    
  //  protected ActiveAbilitiesListModel activeAbilitiesListModel;
    
//    protected EffectListModel criticalEffectListModel, noncriticalEffectListModel, hiddenEffectListModel;
    
    /** Holds value of property activeTarget. */
    private Target activeTarget;
    
    private boolean includeTargetActions = true;
    
    /** Holds the Top level ability filter for the ability list. */
    private AndFilter<Ability> andAbilityFilter;
    
    /** Holds the AbilityName filter pointer. */
    // private NameAbilityFilter nameAbilityFilter;
    
    /** Holds various static abilities. */
    static private AbilityFilter powerAbilityFilter;
    static private AbilityFilter movementAbilityFilter;
    static private AbilityFilter skillAbilityFilter;
    static private AbilityFilter disadvantageAbilityFilter;
    static private AbilityFilter martialArtsFilter;
    
    private AbilityFilter abilityFilter;
    
    private DockingPanel dockingPanel;
    
    /** Creates new form AbilityPanel */
    public AbilityPanel() {
        initComponents();
        
        // start listening for targetChange messages
        Battle.addBattleListener(this);
        
        
        setupFilters();
        filterCombo.setModel( new DefaultComboBoxModel(
        new String[] {"All", "Powers", "Skills", "Movement", "Disadvantages", "Martial Arts"}));
        
        atTargetTree.setNodeFilter( new ATAbilityFilter(andAbilityFilter) );
       // atTargetTree.setSublistName("Powers");
        atTargetTree.getModel().addTreeModelListener(this);
        atTargetTree.setUpdateBlockedWhileProcessing(true);
    }
    
    public void setupFilters() {
        if ( powerAbilityFilter == null ) powerAbilityFilter = new PowerAbilityFilter();
        if ( movementAbilityFilter == null ) movementAbilityFilter = new MovementAbilityFilter();
        if ( skillAbilityFilter == null ) skillAbilityFilter = new SkillAbilityFilter();
        if ( disadvantageAbilityFilter == null ) disadvantageAbilityFilter = new DisadvantageAbilityFilter();
        if ( martialArtsFilter == null ) martialArtsFilter = new MartialArtsAbilityFilter();
        
        //nameAbilityFilter = new NameAbilityFilter();
        andAbilityFilter = new AndFilter<Ability>( null, null);
    }
    
    
    public void battleTargetSelected(TargetSelectedEvent e) {
        setActiveTarget( e.getTarget() );
    }
    
    public void battleSegmentAdvanced(SegmentAdvancedEvent e) {
      //  updateChronometer();
    }
    
    public void battleSequenceChanged(SequenceChangedEvent e) {
        
    }
    
    public void stateChanged(BattleChangeEvent e) {
        if ( Battle.currentBattle != null && Battle.currentBattle.isProcessing() == false ) {
            updateTree();
        }
    }
    
    public void eventNotification(ChangeEvent e) {
        
    }
    
    public void processingChange(BattleChangeEvent event) {
        if ( Battle.currentBattle != null && Battle.currentBattle.isProcessing() == false ) {
            updateTree();
       //     updateStatGroup();
        }
        else {
            if ( dockingPanel != null ) dockingPanel.hideMinimizedPanel();
        }
    }
    
    public void combatStateChange(ChangeEvent e) {
        
    }
    
    protected void updateTree() {
       // updateNameGroup();
       // updateStatGroup();

        atTargetTree.updateActions();
        atTargetTree.updateNodes();
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        targetAbilityPanel2 = new javax.swing.JPanel();
        targetAbilityScroll1 = new javax.swing.JScrollPane();
        atTargetTree = new champions.abilityTree2.ATSingleTargetTree();
        filterCombo = new javax.swing.JComboBox();

        setLayout(new java.awt.GridBagLayout());

        setName("Abilities");
        targetAbilityPanel2.setLayout(new java.awt.BorderLayout());

        targetAbilityScroll1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        targetAbilityScroll1.setMinimumSize(new java.awt.Dimension(225, 22));
        targetAbilityScroll1.setViewportView(atTargetTree);

        targetAbilityPanel2.add(targetAbilityScroll1, java.awt.BorderLayout.CENTER);

        filterCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                filterComboChanged(evt);
            }
        });

        targetAbilityPanel2.add(filterCombo, java.awt.BorderLayout.SOUTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(targetAbilityPanel2, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents
            
    private void filterComboChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_filterComboChanged
        
        if ( evt.getStateChange() == evt.SELECTED ) updateFilter();
    }//GEN-LAST:event_filterComboChanged
    
    
    /** Getter for property activeTarget.
     * @return Value of property activeTarget.
     */
    public Target getActiveTarget() {
        return activeTarget;
    }
    
    /** Setter for property activeTarget.
     * @param activeTarget New value of property activeTarget.
     */
    public void setActiveTarget(Target activeTarget) {
        if ( this.activeTarget != activeTarget ) {
            
            this.activeTarget = activeTarget;
            
            AbilityList abilityList = (activeTarget != null) ? activeTarget.getAbilityList() : null;
            
            if (atTargetTree.stopEditing() == false) atTargetTree.cancelEditing();
            if ( activeTarget == null ) {
                atTargetTree.setTarget(null);
            } else {
                //nameAbilityFilter.setFilterString(null);
                //filterTextField.setText("");
                atTargetTree.setTarget(activeTarget);
                atTargetTree.setPopupFilter(null);
                atTargetTree.expandAll( new TreePath(atTargetTree.getProxyTreeTableModel().getRoot()));
                
            }
            
        } 
    }
    
    /** Triggers an update of the ability filter. */
    public void updateFilter() {
        boolean changed = false;
        
        AbilityFilter filter1 = null;
        
        if ( abilityFilter != null ) {
            filter1 = abilityFilter;
        }
        else {
            String selectedFilter = (String)filterCombo.getSelectedItem();
            if ( selectedFilter != null ) {
                if ( selectedFilter.equals("Powers")) {
                    filter1 = powerAbilityFilter;
                } else if ( selectedFilter.equals("Movement")) {
                    filter1 = movementAbilityFilter;
                } else if ( selectedFilter.equals("Skills")) {
                    filter1 = skillAbilityFilter;
                } else if ( selectedFilter.equals("Disadvantages")) {
                    filter1 = disadvantageAbilityFilter;
                } else if ( selectedFilter.equals("Martial Arts")) {
                    filter1 = martialArtsFilter;
                }
            }
        }
        
        if ( filter1 != andAbilityFilter.getFilter1() ) {
            andAbilityFilter.setFilter1(filter1);
            changed = true;
        }
        
        //String newString = filterTextField.getText();
//        String oldString = nameAbilityFilter.getFilterString();
        
//        if ( (newString == null && oldString != null) ||
//                ( newString != null && ! newString.equals(oldString) ) ) {
//            nameAbilityFilter.setFilterString(newString);
//            changed = true;
//        }
        
        if ( changed ) {
            atTargetTree.updateFilter();
        }
        
    }
    
    public void treeNodesChanged(TreeModelEvent e) {
        atTargetTree.expandAll( new TreePath(atTargetTree.getProxyTreeTableModel().getRoot()));
    }
    
    public void treeNodesInserted(TreeModelEvent e) {
    }
    
    public void treeNodesRemoved(TreeModelEvent e) {
    }
    
    public void treeStructureChanged(TreeModelEvent e) {
        //ATNode node = (ATNode)e.getTreePath().getLastPathComponent();
//        if ( e.getSource() == atTargetTree.getProxyTreeTableModel() ) {
//            atTargetTree.expandAll(e.getTreePath());
//        }
    }
    
    public static DockingPanel createAbilityDockingPanel() {
      
        return createAbilityDockingPanel("Abilities", null, true );
    }
    
    public static DockingPanel createAbilityDockingPanel(String name, AbilityFilter abilityFilter, boolean includeTargetActions) {
        DockingPanel dp = new SavedDockingPanel(name);
        AbilityPanel atp = new AbilityPanel();
        atp.setAbilityFilter(abilityFilter);
        atp.setIncludeTargetActions(includeTargetActions);
       
        dp.getContentPane().add( atp );
        atp.setDockingPanel(dp);
        
        dp.setName(name);
        dp.setMinimizable(true);
        
        dp.setPanelIcon( UIManager.getIcon("WindowIcon.abilitiesDP"));
        
        return dp;
    }

    public DockingPanel getDockingPanel() {
        return dockingPanel;
    }

    public void setDockingPanel(DockingPanel dockingPanel) {
        this.dockingPanel = dockingPanel;
    }

    public AbilityFilter getAbilityFilter() {
        return abilityFilter;
    }

    public void setAbilityFilter(AbilityFilter abilityFilter) {
        if ( this.abilityFilter != abilityFilter ) {
            this.abilityFilter = abilityFilter;
            
            filterCombo.setVisible(this.abilityFilter == null);
            updateFilter();
        }
        
    }

    public boolean getIncludeTargetActions() {
        return includeTargetActions;
    }

    public void setIncludeTargetActions(boolean includeTargetActions) {
        if ( this.includeTargetActions != includeTargetActions ) {
            this.includeTargetActions = includeTargetActions;
            atTargetTree.setIncludeTargetActions(includeTargetActions);
        }
    }

    public ATSingleTargetTree getTargetTree() {
        return atTargetTree;
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private champions.abilityTree2.ATSingleTargetTree atTargetTree;
    private javax.swing.JComboBox filterCombo;
    private javax.swing.JPanel targetAbilityPanel2;
    private javax.swing.JScrollPane targetAbilityScroll1;
    // End of variables declaration//GEN-END:variables
    
}