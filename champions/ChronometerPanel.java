/*
 * AbilityPanel.java
 *
 * Created on January 27, 2001, 4:27 PMtargetMovePanel
 */

package champions;

import champions.event.SegmentAdvancedEvent;
import champions.event.SequenceChangedEvent;
import champions.event.TargetSelectedEvent;
import champions.interfaces.BattleListener;
import dockable.DockingPanel;

import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;

import org.json.simple.JSONObject;


/**
 *
 * @author  unknown
 * @version
 */
public class ChronometerPanel extends JPanel implements BattleListener {
    
    private static final int DEBUG = 0;
    
  //  protected ColumnHeaderPanel targetHeaderPanel2, defaultHeaderPanel, targetMoveHeader, targetSkillHeader, targetDisadvantageHeader;
    
  //  protected Icon defaultPortrait;
    
  //  protected ActiveAbilitiesListModel activeAbilitiesListModel;
    
//    protected EffectListModel criticalEffectListModel, noncriticalEffectListModel, hiddenEffectListModel;
//    
//    /** Holds value of property activeTarget. */
//    private Target activeTarget;
//    
//    /** Holds value of property warningColor. */
//    private Color warningColor = new Color(153,0,153);
//    
//    /** Holds value of property negativeColor. */
//    private Color negativeColor = Color.red;
//    
//    /** Holds value of property bonusColor. */
//    private Color bonusColor = new Color(0,102,0);
    
    private DockingPanel dockingPanel;
    
    /** Holds the Top level ability filter for the ability list. */
  //  private AndAbilityFilter andAbilityFilter;
    
    /** Holds the AbilityName filter pointer. */
  //  private NameAbilityFilter nameAbilityFilter;
    
    /** Holds various static abilities. */
  //  static private AbilityFilter powerAbilityFilter;
  //  static private AbilityFilter movementAbilityFilter;
  //  static private AbilityFilter skillAbilityFilter;
 //   static private AbilityFilter disadvantageAbilityFilter;
    
    /** Creates new form AbilityPanel */
    public ChronometerPanel() {
        initComponents();
        
        // Finish the layout setup for non-default layouts
//        scrollSubgroup.setLayout( new ConstrainedScrollPaneLayout( activeScroll, scrollSubgroup, SwingConstants.HORIZONTAL ));
//        activeAbilities.setLayout( new ConstrainedFlowLayout( FlowLayout.LEFT, 0, 2, SwingConstants.HORIZONTAL));
//        criticalEffectsPanel.setLayout( new ConstrainedFlowLayout( FlowLayout.LEFT, 0, 2, SwingConstants.HORIZONTAL));
//        noncriticalEffectsPanel.setLayout( new ConstrainedFlowLayout( FlowLayout.LEFT, 0, 2, SwingConstants.HORIZONTAL));
//        hiddenEffectsPanel.setLayout( new ConstrainedFlowLayout( FlowLayout.LEFT, 0, 2, SwingConstants.HORIZONTAL));
//        
//        // this.setLayout( new ColumnLayout(3,-1,0,0) );
//        
//        defaultPortrait = UIManager.getIcon("AbilityPanel.defaultPortraitIcon");
        
//        abilityTabbedPanel.setTitleAt(0, "Powers");
//        abilityTabbedPanel.setTitleAt(1, "Movement");
//        abilityTabbedPanel.setTitleAt(2, "Skills");
//        abilityTabbedPanel.setTitleAt(3, "Disadvantages");
//        abilityTabbedPanel.setTitleAt(4, "Actions");
//        abilityTabbedPanel.setTitleAt(5, "Powers(NEW)");
        
        // Setup the Abilities and Effect Panels
        
        //activeAbilitiesListModel = new ActiveAbilitiesListModel();
        //activeAbilities.setModel(activeAbilitiesListModel);
        
//        criticalEffectListModel = new EffectListModel( Effect.CRITICAL );
//        criticalEffectListModel.setDisplayNoneWhenEmpty(true);
//        criticalEffectsPanel.setModel( criticalEffectListModel);
//        noncriticalEffectListModel = new EffectListModel( Effect.NONCRITICAL );
//        noncriticalEffectListModel.setDisplayNoneWhenEmpty(true);
//        noncriticalEffectsPanel.setModel( noncriticalEffectListModel );
//        hiddenEffectListModel = new EffectListModel( Effect.HIDDEN );
//        hiddenEffectListModel.setDisplayNoneWhenEmpty(true);
//        hiddenEffectsPanel.setModel( hiddenEffectListModel );
        
        updateChronometer();
        
        //setupIcons();
        
//        ColumnList headerDL;
        
//        String title = new String("Maneuver Name");
//        headerDL = getAbilityColumns(title);
//        ColumnLayout cl1 = new ColumnLayout(1, 175,0,0);
//        defaultHeaderPanel = new ColumnHeaderPanel();
//        defaultAbilityPanel.setLayout( cl1 );
//        //defaultAbilityPanel.setAbilityList( Battle.getDefaultAbilities() );
//        defaultAbilityPanel.setDefaultAbilities(true);
//        defaultHeaderPanel.setHeaderInsets( new Insets( 2,2,0,0) );
//        defaultHeaderPanel.setColumnLayout(cl1);
//        defaultHeaderPanel.setColumnList(headerDL);
//        defaultAbilityPanel.setColumnList(headerDL);
//        defaultAbilityPanel.setColumnHeader(defaultHeaderPanel);
//        defaultHeaderPanel.setResizable(true);
//        defaultHeaderPanel.setDynamicAdjust(false);
//        defaultHeaderPanel.setFont(new Font("Arial", 0, 9));
//        defaultHeaderPanel.setName("Header");
//        defaultAbilityScroll.setColumnHeaderView(defaultHeaderPanel);
        
//        title = new String("Ability Name");
//        headerDL = getAbilityColumns(title);
//        ColumnLayout cl2 = new ColumnLayout(1, 175,0,0);
//        targetAbilityPanel.setLayout( cl2 );
//        targetHeaderPanel2 = new ColumnHeaderPanel();
//        targetHeaderPanel2.setHeaderInsets( new Insets( 2,0,0,0) );
//        targetHeaderPanel2.setColumnLayout(cl2);
//        targetHeaderPanel2.setColumnList(headerDL);
//        targetAbilityPanel.setColumnHeader(targetHeaderPanel2);
//        targetAbilityPanel.setColumnList(headerDL);
//        targetAbilityPanel.setFilter(AbilityButtonPanel.POWER);
//        targetHeaderPanel2.setResizable(true);
//        targetHeaderPanel2.setDynamicAdjust(false);
//        targetHeaderPanel2.setFont(new Font("Arial", 0, 9));
//        targetHeaderPanel2.setName( "Header" );
//        targetAbilityScroll.setColumnHeaderView(targetHeaderPanel2);
//        
//        headerDL = getSkillColumns();
//        ColumnLayout cl3 = new ColumnLayout(1, 132,0,0);
//        targetSkillPanel.setLayout( cl3 );
//        targetSkillHeader = new ColumnHeaderPanel();
//        targetSkillHeader.setHeaderInsets( new Insets( 2,2,0,0) );
//        targetSkillHeader.setColumnLayout(cl3);
//        targetSkillHeader.setColumnList(headerDL);
//        targetSkillPanel.setColumnList(headerDL);
//        targetSkillPanel.setFilter( AbilityButtonPanel.SKILL );
//        targetSkillHeader.setResizable(true);
//        targetSkillHeader.setDynamicAdjust(false);
//        targetSkillHeader.setFont(new Font("Arial", 0, 9));
//        targetSkillHeader.setName( "Header");
//        targetSkillScroll.setColumnHeaderView(targetSkillHeader);
//        
//        
//        ColumnList moveColumns = getMoveColumns();
//        ColumnLayout cl4 = new ColumnLayout(1, 132,0,0);
//        targetMovePanel.setLayout( cl4 );
//        targetMoveHeader = new ColumnHeaderPanel();
//        targetMoveHeader.setHeaderInsets( new Insets( 2,2,0,0) );
//        targetMoveHeader.setColumnLayout(cl4);
//        targetMoveHeader.setColumnList(moveColumns);
//        targetMovePanel.setColumnList(moveColumns);
//        targetMovePanel.setFilter( AbilityButtonPanel.MOVEMENT );
//        targetMoveHeader.setResizable(true);
//        targetMoveHeader.setDynamicAdjust(false);
//        targetMoveHeader.setFont(new Font("Arial", 0, 9));
//        targetMoveHeader.setName( "Header");
//        targetMoveScroll.setColumnHeaderView(targetMoveHeader);
//        
//        headerDL = getDisadvantageColumns();
//        ColumnLayout cl5 = new ColumnLayout(1, 132,0,0);
//        targetDisadvantagePanel.setLayout( cl5 );
//        targetDisadvantageHeader = new ColumnHeaderPanel();
//        targetDisadvantageHeader.setHeaderInsets( new Insets( 2,2,0,0) );
//        targetDisadvantageHeader.setColumnLayout(cl5);
//        targetDisadvantageHeader.setColumnList(headerDL);
//        targetDisadvantagePanel.setColumnList(headerDL);
//        targetDisadvantagePanel.setFilter( AbilityButtonPanel.DISADVANTAGE );
//        targetDisadvantageHeader.setResizable(true);
//        targetDisadvantageHeader.setDynamicAdjust(false);
//        targetDisadvantageHeader.setFont(new Font("Arial", 0, 9));
//        targetDisadvantageHeader.setName( "Header");
//        targetDisadvantageScroll.setColumnHeaderView(targetDisadvantageHeader);
        // start listening for targetChange messages
        Battle.addBattleListener(this);
        
//        filterTextField.getDocument().addDocumentListener( new DocumentListener() {
//            public void changedUpdate(DocumentEvent e) {
//                updateFilter();
//            }
//            
//            public void insertUpdate(DocumentEvent e) {
//                updateFilter();
//            }
//            
//            public void removeUpdate(DocumentEvent e)  {
//                updateFilter();
//            }
//        });
        
//        filterTab.addTab("All");
//        filterTab.addTab("Powers");
//        filterTab.addTab("Skills");
//        filterTab.addTab("Movement");
//        filterTab.addTab("Disadvantages");
//        filterTab.addIndexChangeListener( new PropertyChangeListener() {
//           public void propertyChange(PropertyChangeEvent evt) {
//               String tabLabel = filterTab.getTabLabel(filterTab.getSelectedIndex());
//               filterCombo.setSelectedItem(tabLabel);
//               filterTextField.setText("");
//               updateFilter();
//           }
//        });
        
//        setupFilters();
//        filterCombo.setModel( new DefaultComboBoxModel(
//        new String[] {"All", "Powers", "Skills", "Movement", "Disadvantages"}));
//        
//        powerTree.setAbilityFilter( andAbilityFilter );
//       // powerTree.setSublistName("Powers");
//        powerTree.getModel().addTreeModelListener(this);
        
        
    }
    
//    public void setupFilters() {
//        if ( powerAbilityFilter == null ) powerAbilityFilter = new PowerAbilityFilter();
//        if ( movementAbilityFilter == null ) movementAbilityFilter = new MovementAbilityFilter();
//        if ( skillAbilityFilter == null ) skillAbilityFilter = new SkillAbilityFilter();
//        if ( disadvantageAbilityFilter == null ) disadvantageAbilityFilter = new DisadvantageAbilityFilter();
//        
//        nameAbilityFilter = new NameAbilityFilter();
//        andAbilityFilter = new AndAbilityFilter( null, nameAbilityFilter);
//    }
//    
//    public ColumnList getAbilityColumns(String title) {
//        // Setup correct headers for abilityPanels
//        ColumnList headerDL = new ColumnList();
//        int index = headerDL.createIndexed( "Column","NAME",title) ;
//        headerDL.addIndexed(index,   "Column","TYPE", "SPECIAL" ,  true);
//        headerDL.addIndexed(index,   "Column","SPECIAL", "NAME" ,  true);
//        headerDL.addIndexed(index,   "Column","WIDTH", new Integer(114) ,  true);
//        
//        index = headerDL.createIndexed( "Column","NAME","END") ;
//        headerDL.addIndexed(index,   "Column","TYPE", "SPECIAL" ,  true);
//        headerDL.addIndexed(index,   "Column","SPECIAL", "END" ,  true);
//        headerDL.addIndexed(index,   "Column","WIDTH", new Integer(30) ,  true);
//        
//        index = headerDL.createIndexed( "Column","NAME","OCV") ;
//        headerDL.addIndexed(index,   "Column","TYPE", "SPECIAL" ,  true);
//        headerDL.addIndexed(index,   "Column","SPECIAL", "OCV" ,  true);
//        headerDL.addIndexed(index,   "Column","WIDTH", new Integer(30) ,  true);
//        
//        index = headerDL.createIndexed( "Column","NAME","DCV") ;
//        headerDL.addIndexed(index,   "Column","TYPE", "SPECIAL" ,  true);
//        headerDL.addIndexed(index,   "Column","SPECIAL", "DCV" ,  true);
//        headerDL.addIndexed(index,   "Column","WIDTH", new Integer(30) ,  true);
//        
//        index = headerDL.createIndexed( "Column","NAME","") ;
//        headerDL.addIndexed(index,   "Column","TYPE", "SPECIAL" ,  true);
//        headerDL.addIndexed(index,   "Column","SPECIAL", "ICON" ,  true);
//        headerDL.addIndexed(index,   "Column","INSETS", new Insets(0,0,0,0),  true);
//        headerDL.addIndexed(index,   "Column","WIDTH", new Integer(16) ,  true);
//        return headerDL;
//    }
//    
//    public ColumnList getSkillColumns() {
//        ColumnList headerDL = new ColumnList();
//        int index = headerDL.createIndexed( "Column","NAME","Skill") ;
//        headerDL.addIndexed(index,   "Column","TYPE", "SPECIAL" ,  true);
//        headerDL.addIndexed(index,   "Column","SPECIAL", "NAME" ,  true);
//        headerDL.addIndexed(index,   "Column","WIDTH", new Integer(100) ,  true);
//        
//        index = headerDL.createIndexed( "Column","NAME","Roll") ;
//        headerDL.addIndexed(index,   "Column","TYPE", "SPECIAL" ,  true);
//        headerDL.addIndexed(index,   "Column","SPECIAL", "ROLL" ,  true);
//        headerDL.addIndexed(index,   "Column","WIDTH", new Integer(30) ,  true);
//        
//        return headerDL;
//    }
//    
//    public ColumnList getDisadvantageColumns() {
//        ColumnList headerDL = new ColumnList();
//        int index = headerDL.createIndexed( "Column","NAME","Disadvantage") ;
//        headerDL.addIndexed(index,   "Column","TYPE", "SPECIAL" ,  true);
//        headerDL.addIndexed(index,   "Column","SPECIAL", "NAME" ,  true);
//        headerDL.addIndexed(index,   "Column","WIDTH", new Integer(100) ,  true);
//        
//        index = headerDL.createIndexed( "Column","NAME","") ;
//        headerDL.addIndexed(index,   "Column","TYPE", "SPECIAL" ,  true);
//        headerDL.addIndexed(index,   "Column","SPECIAL", "ICON" ,  true);
//        headerDL.addIndexed(index,   "Column","INSETS", new Insets(0,0,0,0),  true);
//        headerDL.addIndexed(index,   "Column","WIDTH", new Integer(16) ,  true);
//        return headerDL;
//    }
//    
//    public ColumnList getMoveColumns() {
//        ColumnList headerDL = new ColumnList();
//        int index = headerDL.createIndexed( "Column","NAME","Movement") ;
//        headerDL.addIndexed(index,   "Column","TYPE", "SPECIAL" ,  true);
//        headerDL.addIndexed(index,   "Column","SPECIAL", "NAME" ,  true);
//        headerDL.addIndexed(index,   "Column","WIDTH", new Integer(100) ,  true);
//        
//        index = headerDL.createIndexed( "Column","NAME","") ;
//        headerDL.addIndexed(index,   "Column","TYPE", "SPECIAL" ,  true);
//        headerDL.addIndexed(index,   "Column","SPECIAL", "DISTANCE" ,  true);
//        headerDL.addIndexed(index,   "Column","WIDTH", new Integer(30) ,  true);
//        
//        return headerDL;
//    }
    
//    public void updateActiveInfo() {
//        updateNameGroup();
//        updateStatGroup();
//        updateEffects();
//    }
//    
//    public void updateNameGroup() {
//        if ( activeTarget == null ) {
//            pictureLabel.setIcon(defaultPortrait);
//            nameLabel.setText( "No Active Character" );
//            nameLabel.setForeground( Color.gray );
//            stateLabel.setText("");
//        }
//        else {
//            Icon portrait = activeTarget.getPortrait();
//            pictureLabel.setIcon(portrait != null ? portrait : defaultPortrait );
//            
//            nameLabel.setText( activeTarget.getName() );
//            nameLabel.setForeground( Color.red  );
//            stateLabel.setText( activeTarget.getStateDescription() );
//        }
//    }
//    
//    public void updateStatGroup() {
//        if ( activeTarget == null ) {
//            ocvValue.setText("-");
//            dcvValue.setText("-");
//            ecvValue.setText("-");
//            pdValue.setText("-");
//            rpdValue.setText("-");
//            edValue.setText("-");
//            redValue.setText("-");
//            mdValue.setText("-");
//            endValue.setText("-");
//            stunValue.setText("-");
//            bodyValue.setText("-");
//        }
//        else {
//            int value, base;
//            Color color;
//            
//            value = activeTarget.getCalculatedOCV();
//            base = activeTarget.getBaseOCV();
//            color = getColor(base, value);
//            ocvValue.setForeground(color);
//            ocvValue.setText( Integer.toString(value) );
//            
//            value = activeTarget.getCalculatedDCV();
//            base = activeTarget.getBaseDCV();
//            color = getColor(base, value);
//            dcvValue.setForeground(color);
//            dcvValue.setText(Integer.toString(value));
//            
//            value = activeTarget.getCalculatedECV();
//            base = activeTarget.getBaseECV();
//            color = getColor(base, value);
//            ecvValue.setForeground(color);
//            ecvValue.setText(Integer.toString(value));
//            
//            value = activeTarget.getDefense("PD");
//            base = activeTarget.getBaseStat("PD");
//            color = getColor(base, value);
//            pdValue.setForeground(color);
//            pdValue.setText(Integer.toString(value));
//            
//            value = activeTarget.getDefense("rPD");
//            base = activeTarget.getBaseStat("rPD");
//            color = getColor(base, value);
//            rpdValue.setForeground(color);
//            rpdValue.setText(Integer.toString(value));
//            
//            value = activeTarget.getDefense("ED");
//            base = activeTarget.getBaseStat("ED");
//            color = getColor(base, value);
//            edValue.setForeground(color);
//            edValue.setText(Integer.toString(value));
//            
//            value = activeTarget.getDefense("rED");
//            base = activeTarget.getBaseStat("rED");
//            color = getColor(base, value);
//            redValue.setForeground(color);
//            redValue.setText(Integer.toString(value));
//            
//            value = activeTarget.getDefense("MD");
//            base = activeTarget.getBaseStat("MD");
//            color = getColor(base, value);
//            mdValue.setForeground(color);
//            mdValue.setText(Integer.toString(value));
//            
//            value = activeTarget.getCurrentStat("END");
//            base = activeTarget.getBaseStat("END");
//            color = getColor(base, value);
//            endValue.setForeground(color);
//            endValue.setText(Integer.toString(value));
//            
//            value = activeTarget.getCurrentStat("STUN");
//            base = activeTarget.getBaseStat("STUN");
//            color = getColor(base, value);
//            stunValue.setForeground(color);
//            stunValue.setText(Integer.toString(value));
//            
//            value = activeTarget.getCurrentStat("BODY");
//            base = activeTarget.getBaseStat("BODY");
//            color = getColor(base, value);
//            bodyValue.setForeground(color);
//            bodyValue.setText(Integer.toString(value));
//        }
//    }
//    
//    public Color getColor(int base, int current) {
//        
//        if ( current > base )
//            return bonusColor;
//        else if ( current == base )
//            return getForeground();
//        if ( current <= 0 )
//            return negativeColor;
//        else
//            return warningColor;
//        
//        
//    }
//    
    public void updateChronometer() {
        if ( Battle.currentBattle != null && Battle.currentBattle.isStopped() == true ) {
            chronometerValue.setText( "Battle Not Started (Press play button to start battle)" );
        }
        else {
            chronometerValue.setText( Battle.currentBattle.getTime().toString() );
        }
        
        JSONObject chronometer = new JSONObject();
        chronometer.put("CurrentPhase", Battle.currentBattle.getTime().toString());
        FileWriter writer;
		try {
			writer = new FileWriter(VirtualDesktop.GLOBALS.EXPORT_PATH + "Chronometer.info");
			writer.write(chronometer.toJSONString());
	        writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
//    public void updateEffects() {
//        
//    }
    
    
    public void battleTargetSelected(TargetSelectedEvent e) {
        //setActiveTarget( e.getTarget() );
    }
    
    public void battleSegmentAdvanced(SegmentAdvancedEvent e) {
        updateChronometer();
    }
    
    public void battleSequenceChanged(SequenceChangedEvent e) {
        
    }
    
    public void stateChanged(BattleChangeEvent e) {
//        if ( Battle.currentBattle != null && Battle.currentBattle.isProcessing() == false ) {
//            updateTree();
//        }
    }
    
    public void eventNotification(ChangeEvent e) {
        
    }
    
    public void processingChange(BattleChangeEvent event) {
//        if ( Battle.currentBattle != null && Battle.currentBattle.isProcessing() == false ) {
//            updateTree();
//       //     updateStatGroup();
//        }
    }
    
    public void combatStateChange(ChangeEvent e) {
        
    }
    
//    protected void updateTree() {
//        updateNameGroup();
//        updateStatGroup();
//
//        powerTree.updateActions();
//        powerTree.updateNodes();
//    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        chronometerValue = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        setName("Abilities");
        chronometerValue.setFont(UIManager.getFont("CombatSimulator.defaultFont"));
        chronometerValue.setText("Chronometer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 5, 5, 0);
        add(chronometerValue, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents
                
    
//    /** Getter for property activeTarget.
//     * @return Value of property activeTarget.
//     */
//    public Target getActiveTarget() {
//        return activeTarget;
//    }
//    
//    /** Setter for property activeTarget.
//     * @param activeTarget New value of property activeTarget.
//     */
//    public void setActiveTarget(Target activeTarget) {
//        if ( this.activeTarget != activeTarget ) {
//            if ( this.activeTarget != null ) {
//                this.activeTarget.removePropertyChangeListener(this);
//            }
//            
//            this.activeTarget = activeTarget;
//            updateActiveInfo();
//            
//            
//            
////            AbilityList abilityList = (activeTarget != null) ? activeTarget.getAbilityList() : null;
////            targetAbilityPanel.setAbilityList(abilityList);
////            targetSkillPanel.setAbilityList( abilityList );
////            targetMovePanel.setAbilityList( abilityList);
////            targetDisadvantagePanel.setAbilityList( abilityList);
//            
////            activeAbilitiesListModel.setTarget( activeTarget );
//            
//            criticalEffectListModel.setTarget( activeTarget );
//            noncriticalEffectListModel.setTarget( activeTarget );
//            hiddenEffectListModel.setTarget( activeTarget );
//            
////            if (powerTree.stopEditing() == false) powerTree.cancelEditing();
////            if ( activeTarget == null ) {
////                powerTree.setTarget(null);
////            }
////            else {
////                nameAbilityFilter.setFilterString(null);
////                filterTextField.setText("");
////                powerTree.setTarget(activeTarget);
////                powerTree.expandAll( new TreePath(powerTree.getTreeTableModel().getRoot()));
////                
////            }
//            
//            if ( this.activeTarget != null ) {
//                this.activeTarget.addPropertyChangeListener(this);
//            }
//            
//            
//            // Pop up the window if it is minimized...
//            if ( dockingPanel != null && dockingPanel.isMinimized() ) {
//                dockingPanel.showMinimizedPanel();
//            }
//        }
//        else {
//            updateNameGroup();
//        }
//    }
    
    /** Triggers an update of the ability filter. */
//    public void updateFilter() {
//        boolean changed = false;
//        
//        AbilityFilter filter1 = null;
//        String selectedFilter = (String)filterCombo.getSelectedItem();
//        if ( selectedFilter != null ) {
//            if ( selectedFilter.equals("Powers")) {
//                filter1 = powerAbilityFilter;
//            }
//            else if ( selectedFilter.equals("Movement")) {
//                filter1 = movementAbilityFilter;
//            }
//            else if ( selectedFilter.equals("Skills")) {
//                filter1 = skillAbilityFilter;
//            }
//            else if ( selectedFilter.equals("Disadvantages")) {
//                filter1 = disadvantageAbilityFilter;
//            }
//        }
//        
//        if ( filter1 != andAbilityFilter.getFilter1() ) {
//            andAbilityFilter.setFilter1(filter1);
//            changed = true;
//        }
//        
//        String newString = filterTextField.getText();
//        String oldString = nameAbilityFilter.getFilterString();
//        
//        if ( (newString == null && oldString != null) ||
//        ( newString != null && ! newString.equals(oldString) ) ) {
//            nameAbilityFilter.setFilterString(newString);
//            changed = true;
//        }
//        
//        if ( changed = true ) {
//            powerTree.updateFilter();
//        }
//        
//    }
    
    /**
//     * This method gets called when a bound property is changed.
//     * @param evt A PropertyChangeEvent object describing the event source
//     *  	and the property that has changed.
//     */
//    public void propertyChange(PropertyChangeEvent evt) {
//        if ( Battle.currentBattle != null && Battle.currentBattle.isProcessing() == false ) {
//            if ( DEBUG > 0 ) System.out.println("ActiveTargetPanel.propertyChange: Updating Name and Stat Group due to prop \"" + evt.getPropertyName() + "\".");
//            String prop = evt.getPropertyName();
//            
//            if ( prop.equals( "Target.NAME" ) || prop.equals("COMBATSTATE") ) {
//                updateNameGroup();
//            }
//            else {
//                updateStatGroup();
//            }
//        }
//    }
//    
//    /** Getter for property warningColor.
//     * @return Value of property warningColor.
//     */
//    public Color getWarningColor() {
//        return warningColor;
//    }
//    
//    /** Setter for property warningColor.
//     * @param warningColor New value of property warningColor.
//     */
//    public void setWarningColor(Color warningColor) {
//        this.warningColor = warningColor;
//    }
//    
//    /** Getter for property negativeColor.
//     * @return Value of property negativeColor.
//     */
//    public Color getNegativeColor() {
//        return negativeColor;
//    }
//    
//    /** Setter for property negativeColor.
//     * @param negativeColor New value of property negativeColor.
//     */
//    public void setNegativeColor(Color negativeColor) {
//        this.negativeColor = negativeColor;
//    }
//    
//    /** Getter for property bonusColor.
//     * @return Value of property bonusColor.
//     */
//    public Color getBonusColor() {
//        return bonusColor;
//    }
//    
//    /** Setter for property bonusColor.
//     * @param bonusColor New value of property bonusColor.
//     */
//    public void setBonusColor(Color bonusColor) {
//        this.bonusColor = bonusColor;
//    }
    
    public static DockingPanel createChronometerDockingPanel() {
        DockingPanel dp = new SavedDockingPanel("chronometerDP");
        ChronometerPanel atp = new ChronometerPanel() ;
        dp.getContentPane().add( atp );
        atp.setDockingPanel(dp);
        
        dp.setName("Chronometer");
        dp.setMinimizable(true);
        
        return dp;
    }

    public DockingPanel getDockingPanel() {
        return dockingPanel;
    }

    public void setDockingPanel(DockingPanel dockingPanel) {
        this.dockingPanel = dockingPanel;
    }
    
//    public void treeNodesChanged(TreeModelEvent e) {
//    }
//    
//    public void treeNodesInserted(TreeModelEvent e) {
//    }
//    
//    public void treeNodesRemoved(TreeModelEvent e) {
//    }
//    
//    public void treeStructureChanged(TreeModelEvent e) {
//        //ATNode node = (ATNode)e.getTreePath().getLastPathComponent();
//        if ( e.getSource() == powerTree.getTreeTableModel() ) {
//            powerTree.expandAll(e.getTreePath());
//        }
//    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel chronometerValue;
    // End of variables declaration//GEN-END:variables
    
}