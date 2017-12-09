/*
 * AttackParametersPanel.java
 *
 * Created on October 31, 2001, 11:53 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.SweepBattleEvent;
import champions.Target;
import champions.abilityTree2.ATAbilityFilter;
import champions.abilityTree2.ATAbilityListNodeFactory;
import champions.abilityTree2.ATAbilityNode;
import champions.abilityTree2.ATModel;
import champions.abilityTree2.ATSetupSweepModel;
import champions.abilityTree2.ATSweepSetupNodeFactory;
import champions.attackTree.sweepTree.SWTAbilityNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import treeTable.DefaultTreeTableCellRenderer;





/**
 *
 * @author  twalker
 */
public class SweepSetupPanel extends JPanel implements AttackTreeInputPanel, TreeSelectionListener {
    /** Stores a cached AttackParametersPanel which can be reused. */
    static public SweepSetupPanel ad = null;
    
    /** Stores a cached AttackParametersPanel which can be reused. */
    public SweepBattleEvent battleEvent = null;
    
    protected Target source;
    
    /** Stores the EventListenerList */
    protected EventListenerList listenerList;
    
    private Action upAction, downAction, enableAction, disableAction;
    
    /** Creates new form AttackParametersPanel */
    public SweepSetupPanel() {
        initComponents();
        
        setupIcons();
       // linkedList.setCellRenderer( new SweepListCellRenderer("SweepSetupPanel.checkedIcon", "SweepSetupPanel.uncheckedIcon"));
       // linkedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
       // linkedList.addListSelectionListener(this);
        
        sweepTree.addTreeSelectionListener(this);
        abilityTree.addTreeSelectionListener(this);
        
        
        abilityTree.setTreeTableModel( new ATSetupSweepModel( new ATSweepSetupNodeFactory().createTargetNode(source, false, null, true) ) );
        ((ATModel)abilityTree.getBaseTreeTableModel()).setSimpleTree(true);
        abilityTree.setDefaultTreeTableCellRenderer( new DefaultTreeTableCellRenderer());
        
        abilityTree.addMouseListener( new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if ( e.getClickCount() == 2 ) {
                    addAbilityToSweep();
                }
            }
        });
        
        sweepTree.addMouseListener( new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if ( e.getClickCount() == 2 ) {
                    removeAbilityFromSweep();
                }
            }
        });
        
        setupActions();
    }
    
    public void setupActions() {
       /* upAction = new AbstractAction( "Move Up" ) {
            public void actionPerformed(ActionEvent e) {
                int index = linkedList.getSelectedIndex();
                if ( index != -1 && index != 0 ) {
                    battleEvent.changeLinkedAbilityOrder(index, index - 1);
                    linkedList.setSelectedIndex(index - 1);
                    updatePanel();
                }
            }
        };
        moveUpButton.setAction(upAction);
        
        downAction = new AbstractAction( "Move Down" ) {
            public void actionPerformed(ActionEvent e) {
                int index = linkedList.getSelectedIndex();
                if ( index != -1 && index != linkedList.getModel().getSize()) {
                    battleEvent.changeLinkedAbilityOrder(index, index + 1);
                    linkedList.setSelectedIndex(index + 1);
                    updatePanel();
                }
            }
        };
        moveDownButton.setAction(downAction);
        
        enableAction = new AbstractAction( "Enable" ) {
            public void actionPerformed(ActionEvent e) {
                int selection = linkedList.getSelectedIndex();
                if ( selection != -1 ) {
                    battleEvent.setLinkedAbilityEnabled(selection, true);
                }
                updatePanel();
            }
        };
        enableButton.setAction(enableAction);
        
        disableAction = new AbstractAction( "Disable" ) {
            public void actionPerformed(ActionEvent e) {
                int selection = linkedList.getSelectedIndex();
                if ( selection != -1 ) {
                    battleEvent.setLinkedAbilityEnabled(selection, false);
                }
                updatePanel();
            }
        };
        disableButton.setAction(disableAction);
        
        linkedList.addMouseListener( new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int index = linkedList.getSelectedIndex();
                if ( e.getClickCount() == 2 && index != -1 ) {
                    battleEvent.setLinkedAbilityEnabled(index, ! battleEvent.isLinkedAbilityEnabled(index));
                }
                updatePanel();
            }
        }); */
        
    }
    
    public void setupIcons() {
        Icon addIcon = UIManager.getIcon("GenericControl.addIcon");
        if ( addIcon != null ) addButton.setIcon(addIcon);
        Icon removeIcon = UIManager.getIcon("GenericControl.removeIcon");
        if ( removeIcon != null ) removeButton.setIcon(removeIcon);
        Icon removeAllIcon = UIManager.getIcon("GenericControl.removeAllIcon");
        if ( removeAllIcon != null ) removeAllButton.setIcon(removeAllIcon);   
    }
    
    static public SweepSetupPanel getSweepSetupPanel(SweepBattleEvent be) {
        if ( ad == null ) ad = new SweepSetupPanel();
        
        ad.setBattleEvent(be);
        
        // Clear any old listeners by creating a new listener list.
        ad.listenerList = new EventListenerList();
        
        return ad;
    }
    
    /** Getter for property battleEvent.
     * @return Value of property battleEvent.
     */
    public SweepBattleEvent getBattleEvent() {
        return battleEvent;
    }
    /** Setter for property battleEvent.
     * @param battleEvent New value of property battleEvent.
     */
    public void setBattleEvent(SweepBattleEvent battleEvent) {
        this.battleEvent = battleEvent;
        sweepTree.setBattleEvent(battleEvent);
        
        if ( battleEvent != null ) {
            setSource( battleEvent.getSource() );
            abilityTree.setNodeFilter( new ATAbilityFilter(battleEvent.getAbilityFilter() ) );
            //ATTargetNode3 root = (ATTargetNode3)abilityTree.getProxyTreeTableModel().getRoot();
            //root.setup(battleEvent.getSource(), new ATAbilityFilter(battleEvent.getAbilityFilter()), true, true);
            abilityTree.setTreeTableModel( new ATSetupSweepModel( new ATSweepSetupNodeFactory().createTargetNode(source, false, new ATAbilityFilter(battleEvent.getAbilityFilter()), true) ) );
        }
    }
    
    protected void fireChangeEvent() {
        if ( listenerList != null ) {
            ChangeEvent e = new ChangeEvent(this);
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length-2; i>=0; i-=2) {
                if (listeners[i]==ChangeListener.class) {
                    // System.out.println( "Firing chance event to: " + listeners[i+1].toString());
                    ((ChangeListener)listeners[i+1]).stateChanged(e);
                }
            }
        }
    }
    
    private void updatePanel() {
        updateControls();
        //linkedList.repaint();
    }
    
    /**
     *  Adds a <code>Battle</code> listener.
     *
     *  @param l  the <code>ChangeListener</code> to add
     */
    public void addChangeListener(ChangeListener l) {
      //  listenerList.add(ChangeListener.class,l);
    }
    
    /**
     * Removes a <code>Battle</code> listener.
     *
     * @param l  the <code>BattleListener</code> to remove
     */
    public void removeChangeListener(ChangeListener l) {
      //  listenerList.remove(ChangeListener.class,l);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        removeAllButton = new javax.swing.JButton();
        sweepGroup = new javax.swing.JPanel();
        abilityScrollPane = new javax.swing.JScrollPane();
        abilityTree = new champions.abilityTree2.ATAbilityListTree();
        sweepScrollPane = new javax.swing.JScrollPane();
        sweepTree = new champions.attackTree.sweepTree.SweepTree();

        jLabel1.setText("The following abilities will be performed during the sweep/rapid fire:");

        addButton.setText("Add");
        addButton.setToolTipText("Add Ability to Sweep/Rapid Fire");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        removeButton.setText("Remove");
        removeButton.setToolTipText("Remove Ability from Sweep/Rapid Fire");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        removeAllButton.setText("Remove All");
        removeAllButton.setToolTipText("Remove All Abilities from Sweep/Rapid Fire");
        removeAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAllButtonActionPerformed(evt);
            }
        });

        sweepGroup.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 0, 0));
        sweepGroup.setLayout(new java.awt.GridBagLayout());

        abilityScrollPane.setViewportView(abilityTree);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        sweepGroup.add(abilityScrollPane, gridBagConstraints);

        sweepScrollPane.setViewportView(sweepTree);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        sweepGroup.add(sweepScrollPane, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(sweepGroup, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 184, Short.MAX_VALUE)
                        .addComponent(removeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeAllButton))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sweepGroup, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(removeAllButton)
                    .addComponent(removeButton)
                    .addComponent(addButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void removeAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAllButtonActionPerformed
        // Add your handling code here:
        removeAllAbilitiesFromSweep();
        updateControls();
    }//GEN-LAST:event_removeAllButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        // Add your handling code here:
        removeAbilityFromSweep();
        updateControls();
    }//GEN-LAST:event_removeButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        // Add your handling code here:
        addAbilityToSweep();
        updateControls();
    }//GEN-LAST:event_addButtonActionPerformed
    
    private void addAbilityToSweep() {
        Ability ability = getAbilityTreeAbility();
        if ( ability != null ) {
            int position = -1;
            SWTAbilityNode node = getSweepTreeAbilityNode();
            if ( node != null ) {
                position = node.getPosition();
            }
            
            battleEvent.addLinkedAbility(ability, false);
        }
    }
    
    private void removeAbilityFromSweep() {
        SWTAbilityNode node = getSweepTreeAbilityNode();
        if ( node != null ) {
            int pos = node.getPosition();
            battleEvent.removeLinkedAbility(pos);
        }
    }
    
    private void removeAllAbilitiesFromSweep() {
        battleEvent.removeAllLinkedAbilities();
    }
    
    private Ability getAbilityTreeAbility() {
        TreePath path = abilityTree.getSelectionPath();
        
        if ( path != null ) {
            Object node = path.getLastPathComponent();
            if ( node instanceof ATAbilityNode ) {
                return ((ATAbilityNode)node).getAbility();
            }
        }
        return null;
    }
    
    private SWTAbilityNode getSweepTreeAbilityNode() {
        TreePath path = sweepTree.getSelectionPath();
        if ( path != null ) {
            Object node = path.getLastPathComponent();
            if ( node instanceof SWTAbilityNode ) {
                return (SWTAbilityNode)node;
            }
        }
        return null;
    }
    
    public void showPanel(AttackTreePanel atip) {
        setupPanel();
    }
    
    public JPanel getPanel() {
        return this;
    }
    
    public void hidePanel() {
        ad.setSource(null);
        ad.setBattleEvent(null);
    }
    
    public void setupPanel() {
       // linkedList.setModel( new DetailListListModel(battleEvent, "SweepAbility", "ABILITY") );
        abilityTree.expandAll( new TreePath(abilityTree.getModel().getRoot() ));
        
        updateControls();
    }
    
    private void updateControls() {
        if ( battleEvent != null ) {
            addButton.setEnabled( getAbilityTreeAbility() != null );
            removeButton.setEnabled( getSweepTreeAbilityNode() != null );
            removeAllButton.setEnabled( battleEvent.getLinkedAbilityCount() > 0 );
        }
    }
    
    /** Getter for property source.
     * @return Value of property source.
     *
     */
    public Target getSource() {
        return source;
    }
    
    /** Setter for property source.
     * @param source New value of property source.
     *
     */
    public void setSource(Target source) {
        this.source = source;
    }
    
    public void valueChanged(TreeSelectionEvent e) {
        updateControls();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane abilityScrollPane;
    private champions.abilityTree2.ATAbilityListTree abilityTree;
    private javax.swing.JButton addButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton removeAllButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JPanel sweepGroup;
    private javax.swing.JScrollPane sweepScrollPane;
    private champions.attackTree.sweepTree.SweepTree sweepTree;
    // End of variables declaration//GEN-END:variables
    

}
