/*
 * ObstructionPanel.java
 *
 * Created on January 7, 2002, 4:26 PM
 */

package champions.attackTree;

import champions.ActivationInfo;
import champions.Battle;
import champions.BattleEvent;
import champions.ObjectTarget;
import champions.ObstructionList;
import champions.PADRoster;
import champions.Roster;
import champions.RosterDockingPanel;
import champions.Target;
import champions.TargetList;
import champions.targetTree.TTModel;
import champions.targetTree.TTNode;
import champions.targetTree.TTObstructionListNode;
import champions.targetTree.TTTargetListNode;
import champions.targetTree.TTTargetNode;
import java.awt.event.MouseAdapter;
import javax.swing.JPanel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;







/**
 *
 * @author  twalker
 */
public class ObstructionPanel extends JPanel implements AttackTreeInputPanel, TreeSelectionListener {
    
        /** Holds value of property obstructionList. */
    private ObstructionList obstructionList;
    
    static public ObstructionPanel defaultPanel = null;
    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    /** Holds value of property targetGroup. */
    private String targetGroup;
    
    /** Holds value of property battleEvent. */
    private BattleEvent battleEvent;
    
    /** Holds value of property target. */
    private Target target;
    
  //  private ObstructionListModel olm = null;
  //  private MissingObstructionListModel molm = null;
    
    private TargetList sourceList;
    private TargetList destList;
    
    /** Creates new form ObstructionPanel */
    public ObstructionPanel() {
        initComponents();
        sourceTree.addTreeSelectionListener(this);
        destTree.addTreeSelectionListener(this);
        
        // Setup the double click stuff...
        sourceTree.addMouseListener( new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if ( e.getClickCount() == 2 && e.isPopupTrigger() == false ) {
                    performAddAction();
                }
            }
        });
        
        // Setup the double click stuff...
        destTree.addMouseListener( new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if ( e.getClickCount() == 2 && e.isPopupTrigger() == false ) {
                    performRemoveAction();
                }
            }
        });
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        leftGroup = new javax.swing.JPanel();
        leftScroll = new javax.swing.JScrollPane();
        sourceTree = new champions.targetTree.TargetTree();
        rightGroup = new javax.swing.JPanel();
        rightScroll = new javax.swing.JScrollPane();
        destTree = new champions.targetTree.TargetTree();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        removeAllButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        leftGroup.setLayout(new java.awt.BorderLayout());

        leftGroup.setBorder(javax.swing.BorderFactory.createTitledBorder("Available Obstructions"));
        leftScroll.setViewportView(sourceTree);

        leftGroup.add(leftScroll, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(leftGroup, gridBagConstraints);

        rightGroup.setLayout(new java.awt.BorderLayout());

        rightGroup.setBorder(javax.swing.BorderFactory.createTitledBorder("Obstructions In the Way"));
        rightScroll.setViewportView(destTree);

        rightGroup.add(rightScroll, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(rightGroup, gridBagConstraints);

        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        add(addButton, new java.awt.GridBagConstraints());

        removeButton.setText("Remove");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        add(removeButton, new java.awt.GridBagConstraints());

        removeAllButton.setText("Remove All");
        removeAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAllButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        add(removeAllButton, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void removeAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAllButtonActionPerformed
        // Add your handling code here:
        if ( obstructionList != null ) {
            obstructionList.removeAllObstructions();
            //leftList.clearSelection();
            adjustControls();
        }
    }//GEN-LAST:event_removeAllButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        performRemoveAction();
    }//GEN-LAST:event_removeButtonActionPerformed

    private void performRemoveAction() {
    // Add your handling code here:
        if ( obstructionList != null ) {
            Target destTarget = null;
            TreePath destPath = destTree.getSelectionPath();
                if ( destPath != null ) {
                    destTarget = ((TTNode)destPath.getLastPathComponent()).getTarget();
                }
            
            int oindex = obstructionList.findObstruction(destTarget);
            if ( oindex != -1 ) {
                obstructionList.removeObstruction(oindex);
              //  leftList.setSelectedValue(t, true);
              //  rightList.clearSelection();
                adjustControls();
            }
        }    
    }
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        performAddAction();
    }//GEN-LAST:event_addButtonActionPerformed
    
    private void performAddAction() {
        // Add your handling code here:
        if ( obstructionList != null ) {
            Target sourceTarget = null;
            Target destTarget = null;
            TreePath sourcePath = sourceTree.getSelectionPath();
            if ( sourcePath != null ) {
                sourceTarget = ((TTNode)sourcePath.getLastPathComponent()).getTarget();
            }

            if ( sourceTarget != null ) {
                if ( Battle.currentBattle.getObstructions().hasTarget( sourceTarget, true ) == false ){
                    // Clone the target if it is not already in the battle, cause it is coming
                    // from the preset list and we should never use those directly.
                    sourceTarget = (Target)sourceTarget.clone();
                    //sourceTarget.editTarget();
                    
                    Roster r = Battle.currentBattle.findRoster("Obstructions");
                    if ( r == null ) {
                        r = new Roster("Obstructions");
                        Battle.currentBattle.addRoster(r);
                        RosterDockingPanel.getDefaultRosterDockingPanel().addRoster(r);
                    }
                    r.add(sourceTarget, battleEvent);
                }
                
                TreePath destPath = destTree.getSelectionPath();
                if ( destPath != null ) {
                    destTarget = ((TTNode)destPath.getLastPathComponent()).getTarget();
                }
                
                if ( destTarget != null ) {
                    int position = obstructionList.findObstruction(destTarget);
                    obstructionList.addObstruction(sourceTarget,position);
                }
                else {
                    obstructionList.addObstruction(sourceTarget);
                }
            }
            
          //  rightList.setSelectedValue(t, true);
          //  leftList.clearSelection();
            adjustControls();
        }
    }
    
    static public ObstructionPanel getDefaultPanel(BattleEvent battleEvent, int targetReferenceNumber, String targetGroup) {
        if ( defaultPanel == null ) defaultPanel = new ObstructionPanel();
        
        defaultPanel.setBattleEvent(battleEvent);
        defaultPanel.setTargetReferenceNumber(targetReferenceNumber);
        defaultPanel.setTargetGroup(targetGroup);
        
        return defaultPanel;
    }
    
    public void showPanel(AttackTreePanel atip) {
        
        // Setup the Panel
        
        buildSourceModel();
        
        ActivationInfo ai = battleEvent.getActivationInfo();
        int tindex = ai.getTargetIndex(targetReferenceNumber, targetGroup);
        setObstructionList( ai.getObstructionList(tindex) );
        
        adjustControls();
        
    }
    
    public JPanel getPanel() {
        return this;
    }
    
    public void hidePanel() {
        setObstructionList(null);
    }
    
    /** Getter for property targetReferenceNumber.
     * @return Value of property targetReferenceNumber.
     */
    public int getTargetReferenceNumber() {
        return targetReferenceNumber;
    }
    
    /** Setter for property targetReferenceNumber.
     * @param targetReferenceNumber New value of property targetReferenceNumber.
     */
    public void setTargetReferenceNumber(int targetReferenceNumber) {
        this.targetReferenceNumber = targetReferenceNumber;
    }
    
    /** Getter for property targetGroup.
     * @return Value of property targetGroup.
     */
    public String getTargetGroup() {
        return targetGroup;
    }
    
    /** Setter for property targetGroup.
     * @param targetGroup New value of property targetGroup.
     */
    public void setTargetGroup(String targetGroup) {
        this.targetGroup = targetGroup;
    }
    
    /** Getter for property battleEvent.
     * @return Value of property battleEvent.
     */
    public BattleEvent getBattleEvent() {
        return battleEvent;
    }
    
    /** Setter for property battleEvent.
     * @param battleEvent New value of property battleEvent.
     */
    public void setBattleEvent(BattleEvent battleEvent) {
        this.battleEvent = battleEvent;
    }
    
    /** Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        return target;
    }
    
    /** Setter for property target.
     * @param target New value of property target.
     */
    public void setTarget(Target target) {
        this.target = target;
    }
    
    /** Getter for property obstructionList.
     * @return Value of property obstructionList.
     */
    public ObstructionList getObstructionList() {
        return obstructionList;
    }
    
    /** Setter for property obstructionList.
     * @param obstructionList New value of property obstructionList.
     */
    public void setObstructionList(ObstructionList obstructionList) {
        if ( this.obstructionList != obstructionList ) {
            
            this.obstructionList = obstructionList;
            
           // olm.setObstructionList(obstructionList);
           // molm.setObstructionList(obstructionList);
            buildDestModel();
            
            
        }
    }
    
    public void valueChanged(TreeSelectionEvent e) {
        adjustControls();
    }
    
    private void adjustControls() {
        if ( obstructionList == null ) return;
        Target sourceTarget = null;
        Target destTarget = null;
        TreePath sourcePath = sourceTree.getSelectionPath();
        if ( sourcePath != null ) {
            sourceTarget = ((TTNode)sourcePath.getLastPathComponent()).getTarget();
        }
        
        TreePath destPath = destTree.getSelectionPath();
        if ( destPath != null ) {
            destTarget = ((TTNode)destPath.getLastPathComponent()).getTarget();
        }
        
        if ( sourceTarget != null && obstructionList.findObstruction(sourceTarget) == -1 ) {
            if ( Battle.currentBattle.getObstructions().hasTarget( sourceTarget, true ) ) {
                addButton.setText("> Add >");
            }
            else {
                addButton.setText("> Create >");
            }
            addButton.setEnabled( true );
        }
        else {
            addButton.setEnabled( false );
        }
        
        removeButton.setEnabled( destTarget != null );
     
        removeAllButton.setEnabled( obstructionList.getObstructionCount() != 0 );
    }
    
    private void buildSourceModel() {
        TTNode root = new TTNode();
        
        TargetList set = Battle.currentBattle.getObstructions();
            
        TTNode existingNode = new TTTargetListNode(null, set);
        existingNode.setUserObject("Existing");
        root.add( existingNode );
    
        TTNode preset = new TTTargetListNode(null, PADRoster.getPresetTargets() );
        preset.setUserObject("Object Templates (Create New)");
        root.add( preset );
        
        TTNode genericObject = new TTTargetNode(null, new ObjectTarget("Generic Object", -1, -1), null);
        genericObject.setUserObject("Generic Object (Create New)");
        root.add( genericObject );
        
        TTModel model = new TTModel(root, "");
        root.setModel(model);
        
        sourceTree.setTreeTableModel(model);
    }
    
    private void buildDestModel() {
        TTNode root = new TTObstructionListNode(null, obstructionList);
        TTModel model = new TTModel(root, "");
        root.setModel(model);
        
        destTree.setTreeTableModel(model);
        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private champions.targetTree.TargetTree destTree;
    private javax.swing.JPanel leftGroup;
    private javax.swing.JScrollPane leftScroll;
    private javax.swing.JButton removeAllButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JPanel rightGroup;
    private javax.swing.JScrollPane rightScroll;
    private champions.targetTree.TargetTree sourceTree;
    // End of variables declaration//GEN-END:variables
    

}
