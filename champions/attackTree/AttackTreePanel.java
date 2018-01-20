/*
 * AttackTreePanel.java
 *
 * This is the replacement for the "old" InlineView/AttackTreeInlineView mechanism.  The functionality
 * contained in this class in the same as in AttackTreeInlineView.  However, the inline view functionality
 * has been abandoned and this has been placed in a seperate panel that can work by itself.
 *
 * This panel is meant to be placed in a dockable.DockingPanel and generally minimized/closed when not in use.
 *
 */

package champions.attackTree;
import champions.Battle;
import champions.BattleEvent;
import champions.ChampionsUtilities;
import champions.Profile;
import champions.ProfileManager;
import champions.ProfileTemplate;
import champions.Roster;
import champions.Target;
import champions.exception.BattleEventException;
import dockable.DockingPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;




/**
 *
 * @author  twalker
 */
public class AttackTreePanel extends JPanel implements TreeWillSelectListener {
    
    /** Stores the AttackTreeModel.
     *
     * This is the tree model that is currently being processed.  However, this
     * tree model might not be the one attached to the treePanel, since we only
     * attach to the treeModel when there is a GUI event within the model,
     * such as showPanel.  This is to reduce the number of exteraneous GUI updates
     * for events that never require user input.
     */
    public AttackTreeModel model;
    
    private AttackTreeInputPanel currentPanel;
    
    /** Holds value of property activeNode. */
    private AttackTreeNode activeNode;
    
    static public AttackTreePanel defaultAttackTreePanel;
    
    private DockingPanel dockingPanel;
    
    private AttackTreeModel defaultModel;
    
    private Icon defaultFalseIcon;
    private Icon defaultTrueIcon;
    private Icon trueIcon;
    private Icon falseIcon;
    
    private boolean processing = false;

	public boolean OKCLicked;

	public static AttackTreePanel Panel;
    
    private static EventListenerList listenerList;
    
    /**
     * Creates new form AttackTreePanel
     */
    public AttackTreePanel() {
        initComponents();
        
        setupIcons();
        
        treePanel.getSelectionModel().addTreeWillSelectListener(this);
        
        createDefaultModel();
        deactivateAttackTree();
        Panel = this;
    }
    
    public static void setDefaultPanel(AttackTreePanel atp) {
        defaultAttackTreePanel = atp;
    }
    
    /**
     * Gets the model this node belongs to.
     */
    public AttackTreeModel getModel() {
        return model;
    }
    
    /**
     * Sets teh model this node belongs to.
     */
    public void setModel(AttackTreeModel model) {
        if ( this.model != model ) {
            if ( this.model != null ) {
                // clear any association with the old model...
                this.model.setAttackTreePanel(null);
            }
            
            this.model = model;
            
            if ( model != null ) {
                this.model.setAttackTreePanel(this);
            }
        }
    }
    
    /** Attached the model to the tree panels.
     *
     * This method attaches the current model (in the model instance variable)
     * to the treePanel.  This will cause an update to occur in the treePanel
     * but will not force an advance to a new node.
     */
    protected void attachTreeModel(AttackTreeNode activeNode) {
        if ( treePanel.getModel() != model ) {
            treePanel.setModel(model);
            model.setTree(treePanel.getTree());
            
        }
        
        
    }
    
    protected boolean isModelAttached(AttackTreeModel model) {
        return this.model == model && treePanel.getModel() == model;
    }
    
    /**
     * Indicates that GUI event occurred which should advance the panel.
     */
    public void advanceNode() {
    	clearInputPanel();
        if ( model != null ) 
        {
            model.advanceAndActivate(null,null);      
        }
    }
    
    public void showInputPanel(AttackTreeNode activeNode, AttackTreeInputPanel panel) {
        showInputPanel(activeNode,panel,true);
        
    }
    public void showInputPanel(AttackTreeNode activeNode, AttackTreeInputPanel panel, boolean okayEnabled) {
        if ( panel != this.getCurrentPanel() || activeNode != this.activeNode ) {
            attachTreeModel(activeNode);
            
            if ( this.getCurrentPanel() != null ) {
                inputPanelGroup.remove(this.getCurrentPanel().getPanel());
                this.getCurrentPanel().hidePanel();
            }
            
            this.currentPanel = panel;
            this.activeNode = activeNode;
            
            model.setActiveNode(activeNode);
            
            //   setupBypassButton();
            
            if ( this.getCurrentPanel() != null ) {
                inputPanelGroup.add(this.getCurrentPanel().getPanel(), BorderLayout.CENTER);
                getCurrentPanel().showPanel(this);
                
                if ( panel instanceof AboutPanel == false ) showDockingPanel();
            }
            
            updateCombatProfileIcon();
            
            okayButton.setEnabled( okayEnabled );
            
            inputPanelGroup.revalidate();
            inputPanelGroup.repaint();
            
            Component c = this;
            while ( c != null || c instanceof JRootPane == false ) {
                //System.out.println(c);
                c = c.getParent();
                if ( c instanceof JRootPane ) {
                    ((JRootPane)c).setDefaultButton(okayButton);
                    break;
                }
            }
            
            fireInputNeededEvent();
        }
    }
    
    public void clearInputPanel() {
        if ( this.getCurrentPanel() != null ) {
            inputPanelGroup.remove(this.getCurrentPanel().getPanel());
            this.getCurrentPanel().hidePanel();
            this.currentPanel = null;
        }
    }
    
    protected void showDockingPanel() {
        
        setupDockingPanel();
        if ( dockingPanel.getDockingFrame() == null ) {
            dockingPanel.dockIntoFrame();
        }
        
        if ( dockingPanel.isMinimized() ) {
            dockingPanel.showMinimizedPanel();
        } else {
            // We may need to do something here...
            // I am not sure.
        }
    }
    
    protected void setupDockingPanel() {
        if ( dockingPanel == null ) {
            DockingPanel dp = new AttackTreeDockingPanel(this);
        }
    }
    
//    public void setupBypassButton() {
//        autoBypassButton.reset();
//
//        if ( activeNode == null ) {
//            autoBypassButton.setVisible(false);
//        }
//        else {
//            Target t = activeNode.getAutoBypassTarget();
//            String o = activeNode.getAutoBypassOption();
//
//            if ( t != null && o != null ) {
//                autoBypassButton.setTarget(t);
//                autoBypassButton.setOption(o);
//                autoBypassButton.setVisible(true);
//            }
//            else {
//                autoBypassButton.setVisible(false);
//            }
//        }
//    }
    
    public void setInstructions(String instructions) {
        if ( instructions != null ) {
            instructionsLabel.setText(instructions);
        } else {
            instructionsLabel.setText("");
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        splitGroup = new javax.swing.JPanel();
        splitPane = new javax.swing.JSplitPane();
        treePanel = new champions.attackTree.TreePanel();
        rightSplitGroup = new javax.swing.JPanel();
        instructionGroup = new javax.swing.JPanel();
        instructionsLabel = new javax.swing.JLabel();
        combatProfileLabel = new javax.swing.JLabel();
        inputPanelGroup = new javax.swing.JPanel();
        buttonGroup = new javax.swing.JPanel();
        okayButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(600, 300));
        splitGroup.setLayout(new java.awt.GridBagLayout());

        splitGroup.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        splitPane.setDividerSize(3);
        splitPane.setResizeWeight(0.33);
        splitPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        splitPane.setLeftComponent(treePanel);

        rightSplitGroup.setLayout(new java.awt.GridBagLayout());

        instructionGroup.setLayout(new java.awt.GridBagLayout());

        instructionGroup.setBackground(new java.awt.Color(204, 204, 255));
        instructionGroup.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 204)));
        instructionsLabel.setBackground(new java.awt.Color(204, 204, 255));
        instructionsLabel.setFont(UIManager.getFont("CombatSimulator.boldFont"));
        instructionsLabel.setText("Instructions");
        instructionsLabel.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        instructionGroup.add(instructionsLabel, gridBagConstraints);

        combatProfileLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                combatProfileLabelMousePressed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        instructionGroup.add(combatProfileLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        rightSplitGroup.add(instructionGroup, gridBagConstraints);

        inputPanelGroup.setLayout(new java.awt.BorderLayout());

        inputPanelGroup.setPreferredSize(new java.awt.Dimension(300, 600));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rightSplitGroup.add(inputPanelGroup, gridBagConstraints);

        splitPane.setRightComponent(rightSplitGroup);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        splitGroup.add(splitPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(splitGroup, gridBagConstraints);

        buttonGroup.setLayout(new java.awt.GridBagLayout());

        okayButton.setText("Okay");
        okayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okayButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        buttonGroup.add(okayButton, gridBagConstraints);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        buttonGroup.add(cancelButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(buttonGroup, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents
    
    private void combatProfileLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_combatProfileLabelMousePressed

        showCombatProfileMenu(evt.getPoint());
    }//GEN-LAST:event_combatProfileLabelMousePressed
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        if ( model != null ) model.setError( new BattleEventException("BattleEvent Cancelled", false) );
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    private void okayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okayButtonActionPerformed
    	OKCLicked = true;
    	advanceNode();
        OKCLicked = false;
       // Battle b = Battle.currentBattle;
       // BattleEvent be = b.getCompletedEventList().get(b.getCompletedEventList().size()-1);
       // be.getMessage
    }//GEN-LAST:event_okayButtonActionPerformed
    
    private void advanceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_advanceButtonActionPerformed
        // Add your handling code here:
        advanceNode();
    }//GEN-LAST:event_advanceButtonActionPerformed
    
    
    public void deactivateAttackTree() {
        // Unattached the model...
        if ( model != defaultModel ) setModel(defaultModel);
        attachTreeModel(null);
        if ( dockingPanel != null ) dockingPanel.hideMinimizedPanel();
    }
    
    public void cancelAttack() {
        if ( model != null ) model.setError( new BattleEventException("BattleEvent Cancelled", false) );
    }
    
//    public String getTitle() {
//        return "Battle Action Panel";
//    }
    
//    public String getInstructions() {
//        return "";
//    }
    
//    public boolean buttonPressed(InlineView inlineView, int value) {
//        boolean result = true;
//        if ( value == JOptionPane.OK_OPTION ) {
//            advanceNode();
//            result = model.isFinished();
//        }
//        else if ( value == JOptionPane.CANCEL_OPTION ) {
//            model.setError( new BattleEventException("BattleEvent Cancelled", false) );
//        }
//        return result;
//    }
    
    /**
     * Initializes a AttackTreePanel and returns the panel.
     */
    static public AttackTreePanel getAttackTreePanel(AttackTreeModel atm) {
        if ( defaultAttackTreePanel == null ) defaultAttackTreePanel = new AttackTreePanel();
        
        //defaultAttackTreePanel.inititializePanel(be);
        //defaultAttackTreePanel.setInlineView(iv);
        defaultAttackTreePanel.setModel(atm);
        
        return defaultAttackTreePanel;
    }
    
    static public AttackTreePanel getAttackTreePanel() {
        return defaultAttackTreePanel;
    }
    
   /* public void inititializePanel(BattleEvent be) {
        AttackProcessNode rn = new AttackProcessNode("Root");
        AttackTreeModel atm = new AttackTreeModel(rn);
        atm.setBattleEvent(be);
        atm.setInlinePanel(this);
        atm.setTree(treePanel.getTree());
    
    
        setModel(atm);
    
        atm.activateNode(null, null);
    } */
    
    public boolean valueWillChange(TreeSelectionEvent tse) {
        TreePath tp = tse.getNewLeadSelectionPath();
        if ( tp  == null ) {
            return false;
        } else if ( (tp.getLastPathComponent() instanceof AttackTreeNode) == false ) {
            return false;
        } else {
            
            AttackTreeNode atn = (AttackTreeNode) tp.getLastPathComponent();
            AttackTreeNode activeNode = getModel().getActiveNode();
            AttackTreeModel atm = getModel();
            
            if ( atm != null && atn != null && atn != activeNode ) {
                if ( atm.getRealPreorderPosition(activeNode) > atm.getRealPreorderPosition(atn) ) {
                    // The activeNode is after this node, so go ahead and active the new node directly.
                    //atm.activateNode(atn, atn);
                    atm.advanceAndActivate(atn,atn);
                } else {
                    // The activeNode is currently before this node, so run advanceNode with atn as the manual override
                    //atm.advanceNode(atn);
                    atm.advanceAndActivate(null,atn);
                }
            }
            return false;
        }
    }
    
//    public boolean requiresScroll(InlineView inlineView) {
//        return false;
//    }
    
    /** Getter for property activeNode.
     * @return Value of property activeNode.
     */
    public AttackTreeNode getActiveNode() {
        return activeNode;
    }
    
    /** Setter for property activeNode.
     * @param activeNode New value of property activeNode.
     */
    public void setActiveNode(AttackTreeNode activeNode) {
        this.activeNode = activeNode;
    }
    
//    /** Getter for property inlineView.
//     * @return Value of property inlineView.
//     */
//    public InlineView getInlineView() {
//        return inlineView;
//    }
    
//    /** Setter for property inlineView.
//     * @param inlineView New value of property inlineView.
//     */
//    public void setInlineView(InlineView inlineView) {
//        this.inlineView = inlineView;
//    }
    
    public TreePanel getTreePanel() {
        return treePanel;
    }
    
    public DockingPanel getDockingPanel() {
        return dockingPanel;
    }
    
    public void setDockingPanel(DockingPanel dockingPanel) {
        this.dockingPanel = dockingPanel;
    }
    
    protected void createDefaultModel() {
        defaultModel = new AttackTreeModel(new AboutRootAttackTreeNode());
        defaultModel.setAttackTreePanel(this);
    }
    
    private void updateCombatProfileIcon() {
        
        Icon icon = null;
        String tooltip = null;
        
        if ( activeNode != null ) {
            String bypassOption = activeNode.getAutoBypassOption();
            Target bypassTarget = activeNode.getAutoBypassTarget();
            
            if ( bypassOption != null && bypassTarget != null ) {
                Profile profile = bypassTarget.getTargetProfile();
                
                boolean optionSetting = bypassTarget.getBooleanProfileOption(bypassOption);
                
                int oindex = ProfileTemplate.getDefaultProfileTemplate().getOptionIndexByID(bypassOption);
                String optionName = ProfileTemplate.getDefaultProfileTemplate().getOptionName(oindex);
                
                if ( optionSetting ) {
                    icon = trueIcon;
                    
                    tooltip = ChampionsUtilities.createWrappedHTMLString("<b>" + optionName + "</b><br><br>This panel will be shown for " + bypassTarget.getName() + " (unless it was previously viewed).<br><br><i>(Click to adjust show/skip settings)</i>", 45);
                    
                } else {
                    icon = falseIcon;
                    
                    tooltip = ChampionsUtilities.createWrappedHTMLString("<b>" + optionName + "</b><br><br>This panel will be skipped for " + bypassTarget.getName() + " (unless information is required).<br><br><i>(Click to adjust show/skip settings)</i>", 45);
                    
                }
            }
        }
        
        combatProfileLabel.setIcon(icon);
        combatProfileLabel.setToolTipText(tooltip);
        
    }
    
    public void setProcessing(boolean processing) {
//        if ( progressBar.isIndeterminate() != processing ) {
//            progressBar.setIndeterminate(processing);
//        }
//        

        if ( this.processing != processing ) {
            this.processing = processing;
            
            if ( processing ) {
                setCursor( Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) );
            }
            else {
                setCursor( null );
            }
        }
        
//        if ( processing ) {
//            Dimension d = progressBar.getSize();
//            progressBar.paintImmediately(0,0,d.width,d.height);
//        }
    }
    
    private void setupIcons() {
        if ( trueIcon == null ) trueIcon = UIManager.getIcon("ProfileOption.trueIcon");
        if ( defaultTrueIcon == null ) defaultTrueIcon = UIManager.getIcon("ProfileOption.defaultTrueIcon");
        if ( falseIcon == null ) falseIcon = UIManager.getIcon("ProfileOption.falseIcon");
        if ( defaultFalseIcon == null ) defaultFalseIcon = UIManager.getIcon("ProfileOption.defaultFalseIcon");
    }
    
    private void showCombatProfileMenu(Point p) {
        
        if ( activeNode != null ) {
            final String option = activeNode.getAutoBypassOption();
            final Target target = activeNode.getAutoBypassTarget();
            
            if ( option != null ) {
                JPopupMenu menu = new JPopupMenu();
                
                int oindex = ProfileTemplate.getDefaultProfileTemplate().getOptionIndexByID(option);
                String optionName = ProfileTemplate.getDefaultProfileTemplate().getOptionName(oindex);
                
                JMenuItem title = new JMenuItem(optionName);
                title.setEnabled(false);
                menu.add( title );
                menu.addSeparator();
                
                boolean targetOptionSet = false;
                boolean rosterOptionSet = false;
                
                if ( target != null ) {
                    
                    Profile profile;
                    profile = target.getTargetProfile();
                    if ( profile == null ) {
                        menu.add( new JMenuItem( new AbstractAction("Always show panel for " + target.getName() + " (Create personal profile)", trueIcon) {
                            public void actionPerformed(ActionEvent e) {
                                target.setBooleanProfileOption(option, true);
                                updateCombatProfileIcon();
                            }
                        }));
                        
                        menu.add( new JMenuItem( new AbstractAction("Always skip panel for " + target.getName() + " (Create personal profile)", falseIcon) {
                            public void actionPerformed(ActionEvent e) {
                                target.setBooleanProfileOption(option, false);
                                updateCombatProfileIcon();
                            }
                        }));
                    } else {
                        targetOptionSet = target.getProfileOptionIsSet(option);
                        if ( targetOptionSet ) {
                            boolean optionValue = target.getBooleanProfileOption(option);
                            
                            if ( optionValue == true ) {
                                menu.add( new JMenuItem( new AbstractAction("Always skip panel for " + target.getName(), falseIcon) {
                                    public void actionPerformed(ActionEvent e) {
                                        target.setBooleanProfileOption(option, false);
                                        updateCombatProfileIcon();
                                    }
                                }));
                                
                            } else {
                                
                                menu.add( new JMenuItem( new AbstractAction("Always show panel for " + target.getName() , trueIcon) {
                                    public void actionPerformed(ActionEvent e) {
                                        target.setBooleanProfileOption(option, true);
                                        updateCombatProfileIcon();
                                    }
                                }));
                            }
                            
                            menu.add( new JMenuItem( new AbstractAction("Show/skip panel based upon roster/global profile for " + target.getName()) {
                                public void actionPerformed(ActionEvent e) {
                                    target.unsetBooleanProfileOption(option);
                                    updateCombatProfileIcon();
                                }
                            }));
                        } else {
                            // Target profile wasn't set
                            menu.add( new JMenuItem( new AbstractAction("Always show panel for " + target.getName() + " (Override roster/global profile)", trueIcon) {
                                public void actionPerformed(ActionEvent e) {
                                    target.setBooleanProfileOption(option, true);
                                    updateCombatProfileIcon();
                                }
                            }));
                            
                            menu.add( new JMenuItem( new AbstractAction("Always skip panel for " + target.getName() + " (Override roster/global profile)", falseIcon) {
                                public void actionPerformed(ActionEvent e) {
                                    target.setBooleanProfileOption(option, false);
                                    updateCombatProfileIcon();
                                }
                            }));
                        }
                    }
                    
                    
                    
                    //  Roster base profile settings
                    if ( targetOptionSet == false ) {
                        final Roster roster = target.getRoster();
                        if ( roster != null ) {
                            menu.addSeparator();
                            
                            final Profile rosterProfile = roster.getRosterProfile();
                            
                            if ( rosterProfile == null ) {
                                menu.add( new JMenuItem( new AbstractAction("Always show panel for " + target.getName() + "'s roster (Create roster profile)", trueIcon) {
                                    public void actionPerformed(ActionEvent e) {
                                        Profile p = new Profile(roster.getName());
                                        ProfileManager.addProfile(p);
                                        roster.setRosterProfile(roster.getName());
                                        p.setBooleanProfileOption(option, true);
                                        updateCombatProfileIcon();
                                    }
                                }));
                                
                                menu.add( new JMenuItem( new AbstractAction("Always skip panel for " + target.getName() + "'s roster (Create roster profile)", falseIcon) {
                                    public void actionPerformed(ActionEvent e) {
                                        Profile p = new Profile(roster.getName());
                                        ProfileManager.addProfile(p);
                                        roster.setRosterProfile(roster.getName());
                                        p.setBooleanProfileOption(option, false);
                                        updateCombatProfileIcon();
                                    }
                                }));
                            } else {
                                rosterOptionSet = rosterProfile.isProfileOptionSet(option);
                                if ( rosterOptionSet ) {
                                    boolean optionValue = target.getBooleanProfileOption(option);
                                    
                                    if ( optionValue == true ) {
                                        menu.add( new JMenuItem( new AbstractAction("Always skip panel for " + target.getName() + "'s roster profile " + rosterProfile.getName() + " (Override global profile)", falseIcon) {
                                            public void actionPerformed(ActionEvent e) {
                                                rosterProfile.setBooleanProfileOption(option, false);
                                                updateCombatProfileIcon();
                                            }
                                        }));
                                        
                                    } else {
                                        
                                        menu.add( new JMenuItem( new AbstractAction("Always show panel for " + target.getName() + "'s roster profile " + rosterProfile.getName() + " (Override global profile)", trueIcon) {
                                            public void actionPerformed(ActionEvent e) {
                                                rosterProfile.setBooleanProfileOption(option, true);
                                                updateCombatProfileIcon();
                                            }
                                        }));
                                    }
                                    
                                    menu.add( new JMenuItem( new AbstractAction("Show/skip panel based upon global profile for " + target.getName() + "'s roster profile "  + rosterProfile.getName()) {
                                        public void actionPerformed(ActionEvent e) {
                                            rosterProfile.unsetBooleanProfileOption(option);
                                            updateCombatProfileIcon();
                                        }
                                    }));
                                } else {
                                    // roster profile wasn't set
                                    menu.add( new JMenuItem( new AbstractAction("Always show panel for " + target.getName() + "'s roster profile "  + rosterProfile.getName() + " (Override global profile)", trueIcon) {
                                        public void actionPerformed(ActionEvent e) {
                                            rosterProfile.setBooleanProfileOption(option, true);
                                            updateCombatProfileIcon();
                                        }
                                    }));
                                    
                                    menu.add( new JMenuItem( new AbstractAction("Always skip panel for " + target.getName() + "'s roster profile "  + rosterProfile.getName() + " (Override global profile)", falseIcon) {
                                        public void actionPerformed(ActionEvent e) {
                                            rosterProfile.setBooleanProfileOption(option, false);
                                            updateCombatProfileIcon();
                                        }
                                    }));
                                    
                                    
                                }
                            }
                        }
                    }
                }
                if ( targetOptionSet == false && rosterOptionSet == false) {
                    menu.addSeparator();
                    
                    final Profile defaultProfile = ProfileManager.getDefaultProfile();
                    if ( defaultProfile == null ) {
                        // The no-profile result is always false, so only provide a true
                        // item when no profile is defined at all...
                        menu.add( new JMenuItem( new AbstractAction("Always show panel (Set global default)", trueIcon) {
                            public void actionPerformed(ActionEvent e) {
                                Profile p = new Profile("Default Profile");
                                ProfileManager.setDefaultProfile(p);
                                p.setBooleanProfileOption(option, true);
                                updateCombatProfileIcon();
                            }
                        }));
                        
                    } else {
                        boolean optionValue = defaultProfile.getBooleanProfileOption(option);
                        
                        if ( optionValue == true ) {
                            menu.add( new JMenuItem( new AbstractAction("Always skip panel (Set global default)", falseIcon) {
                                public void actionPerformed(ActionEvent e) {
                                    defaultProfile.setBooleanProfileOption(option, false);
                                    updateCombatProfileIcon();
                                }
                            }));
                            
                        } else {
                            menu.add( new JMenuItem( new AbstractAction("Always show panel (Set global default)", trueIcon) {
                                public void actionPerformed(ActionEvent e) {
                                    defaultProfile.setBooleanProfileOption(option, true);
                                    updateCombatProfileIcon();
                                }
                            }));
                        }
                    }
                    
                    
                }
                menu.show(combatProfileLabel, p.x, p.y);
            }
            
        }
    }
    
    public static void addAttackTreeListener(AttackTreeListener atl) {
        if ( listenerList == null ) listenerList = new EventListenerList();
        
        listenerList.add(AttackTreeListener.class, atl);
    }
    
    public static void removeAttackTreeListener(AttackTreeListener atl) {
        if ( listenerList != null ) {
            listenerList.remove(AttackTreeListener.class, atl);
        }
    }
    
    protected void fireInputNeededEvent() {
        // Guaranteed to return a non-null array
        if ( listenerList != null ) {
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length-2; i>=0; i-=2) {
                if (listeners[i]==AttackTreeListener.class) {
                    // System.out.println( "Firing chance event to: " + listeners[i+1].toString());
                    ((AttackTreeListener)listeners[i+1]).inputNeeded(this);
                }
            }
        }
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonGroup;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel combatProfileLabel;
    private javax.swing.JPanel inputPanelGroup;
    private javax.swing.JPanel instructionGroup;
    private javax.swing.JLabel instructionsLabel;
    private javax.swing.JButton okayButton;
    private javax.swing.JPanel rightSplitGroup;
    private javax.swing.JPanel splitGroup;
    private javax.swing.JSplitPane splitPane;
    private champions.attackTree.TreePanel treePanel;
    // End of variables declaration//GEN-END:variables
    
    static public class AboutRootAttackTreeNode extends DefaultAttackTreeNode {
        public AboutRootAttackTreeNode() {
            this.name = "Default Root";
            addChild( new AboutAttackTreeNode() );
        }
        
        public boolean activateNode(boolean manualOverride) {
            return false;
        }
        
        public AttackTreeNode advanceNode(AttackTreeNode activeChild) throws BattleEventException{
            if ( (AttackTreeNode)getChildAt(0) != activeChild ) return (AttackTreeNode)getChildAt(0);
            else return null;
        }
        
    }
    
    static public class AboutAttackTreeNode extends DefaultAttackTreeNode {
        static AboutPanel ddp;
        public AboutAttackTreeNode() {
            this.name = "About";
            setVisible(true);
            
            if (ddp == null ) ddp = new AboutPanel();
        }
        
        public boolean activateNode(boolean manualOverride) {
            attackTreePanel.showInputPanel(this, ddp);
            
            return true;
        }
        
        public AttackTreeNode advanceNode(AttackTreeNode activeChild) throws BattleEventException{
            
            return null;
        }
    }
    
    
    static public class AboutPanel extends JPanel implements AttackTreeInputPanel  {
        
        /** Creates new form AttackTreePanelDescriptionPanel */
        public AboutPanel() {
            initComponents();
        }
        
        private void initComponents() {
            jTextArea1 = new javax.swing.JTextArea();
            
            setLayout(new java.awt.BorderLayout());
            
            jTextArea1.setEditable(false);
            jTextArea1.setLineWrap(true);
            jTextArea1.setText("This is the Attack Info window.  This window is used during the activation of an ability to set parameters, select targets, and view the results of an ability activity.\n\nThis window can be minimized and will automatically show when an ability activation requires input or has new information.\n\nNot all ability activations events require input and generate output.  To control which attack panels are show and which are automatically skip, select Character -> Edit Combat Profile... for the menus.");
            jTextArea1.setOpaque(false);
            jTextArea1.setRequestFocusEnabled(false);
            add(jTextArea1, java.awt.BorderLayout.CENTER);
            
        }
        // Variables declaration - do not modify
        private javax.swing.JTextArea jTextArea1;
        // End of variables declaration
        
        public JPanel getPanel() {
            return this;
        }
        
        public void hidePanel() {
        }
        
        public void showPanel(champions.attackTree.AttackTreePanel atip) {
        }
        
    }

    public AttackTreeInputPanel getCurrentPanel() {
        return currentPanel;
    }
}
