/*
 * AidToAttackTreePanel.java
 *
 * Created on March 6, 2002, 8:10 PM
 */

package champions.adjustmentPowers.adjustmentTree;

import champions.Ability;
import champions.AdjustmentList;
import champions.BattleEvent;
import champions.Characteristic;
import champions.Target;
import champions.adjustmentPowers.Adjustable;
import champions.adjustmentPowers.AdjustmentPower;
import champions.attackTree.AttackTreeInputPanel;
import champions.attackTree.AttackTreePanel;
import champions.interfaces.ChampionsConstants;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import treeTable.DefaultTreeTableModel;
import treeTable.DefaultTreeTableNode;


/** 
 *
 * @author  Trevor Walker
 */
public class AdjustmentAttackTreePanel extends javax.swing.JPanel 
implements AttackTreeInputPanel, ChampionsConstants, TreeSelectionListener, MouseListener {
    
    static protected AdjustmentAttackTreePanel defaultPanel = null;
    
    private AdjAttackSourceModel sourceModel = null;
    private AdjAttackDestinationModel destinationModel = null;
    
    
    /** Holds value of property sourceAbility. */
    private Ability sourceAbility;
    
    /** Holds value of property target. */
    private Target target;
    
    /** Holds value of property battleEvent. */
    private BattleEvent battleEvent;
    
    /** Holds value of property adjustmentList. */
    private AdjustmentList adjustmentList;
    
    /** Holds value of property adjustables. */
    private ArrayList adjustables;
    
    /** Holds value of property type. */
    private int type;
    
    
    /** Creates new form AidToAttackTreePanel */
    public AdjustmentAttackTreePanel() {
        initComponents();
        
        sourceTree.addTreeSelectionListener( this);
        destinationTree.addTreeSelectionListener( this);
        
        sourceTree.addMouseListener(this);
        destinationTree.addMouseListener(this);
        
        Icon addIcon = UIManager.getIcon("AdjustmentControl.addIcon");
        addButton.setIcon(addIcon);
        Icon addAllIcon = UIManager.getIcon("AdjustmentControl.addAllIcon");
        addAllButton.setIcon(addAllIcon);
        Icon removeIcon = UIManager.getIcon("AdjustmentControl.removeIcon");
        removeButton.setIcon(removeIcon);
        Icon removeAllIcon = UIManager.getIcon("AdjustmentControl.removeAllIcon");
        removeAllButton.setIcon(removeAllIcon);
    }
    
    static public AdjustmentAttackTreePanel getDefaultPanel(BattleEvent be, int type, Ability sourceAbility, Target target, ArrayList adjustables, AdjustmentList adjustmentList) {
        if ( defaultPanel == null ) defaultPanel = new AdjustmentAttackTreePanel();
        
        defaultPanel.setBattleEvent(be);
        defaultPanel.setType(type);
        defaultPanel.setSourceAbility(sourceAbility);
        defaultPanel.setTarget(target);
        defaultPanel.setAdjustables(adjustables);
        defaultPanel.setAdjustmentList(adjustmentList);
        
        return defaultPanel;
    }
    
    protected void updateButtons() {
        if ( adjustmentList != null ) {
            
            TreePath sourceSelection = sourceTree.getSelectionPath();
            addButton.setEnabled( canAdjustMore()
            && (sourceSelection != null
                    && ( sourceSelection.getLastPathComponent() instanceof AdjAbilityNode
                    || sourceSelection.getLastPathComponent() instanceof AdjStatNode) ));
            
            TreePath destinationSelection = destinationTree.getSelectionPath();
            removeButton.setEnabled( destinationSelection != null
                    && ( destinationSelection.getLastPathComponent() instanceof AdjAttackDestinationAbilityNode
                    || destinationSelection.getLastPathComponent() instanceof AdjAttackDestinationStatNode) );
            
            removeAllButton.setEnabled( adjustmentList.getAdjustableCount() > 0);
            AdjustmentPower power = (AdjustmentPower)sourceAbility.getPower();
            addAllButton.setEnabled( power.getAdjustmentLevel(sourceAbility) == ADJ_VARIABLE_ALL_ADJUSTMENT);
        }
    }
    
    protected void updateDescription() {
        if ( adjustmentList != null ) {
            StringBuffer sb = new StringBuffer();
            int count = adjustmentList.getAdjustableCount();
            
            AdjustmentPower power = (AdjustmentPower)sourceAbility.getPower();
            
            switch ( power.getAdjustmentLevel(sourceAbility) ) {
                case ADJ_SINGLE_ADJUSTMENT:
                    sb.append("Only a Single Ability or Character Statistic can be adjusted with this power. ");
                    sb.append(count );
                    sb.append(" of 1 selected.");
                    
                    break;
                case ADJ_VARIABLE_1_ADJUSTMENT:
                    sb.append("Only a Single Ability or Character Statistic can be adjusted with this power. ");
                    sb.append(count );
                    sb.append(" of 1 selected.");
                    break;
                case ADJ_VARIABLE_2_ADJUSTMENT:
                    sb.append("Up to two Abilities or Character Statistics can be adjusted with this power. ");
                    sb.append(count );
                    sb.append(" of 2 selected.");
                    break;
                case ADJ_VARIABLE_4_ADJUSTMENT:
                    sb.append("Up to four Abilities or Character Statistics can be adjusted with this power. ");
                    sb.append(count );
                    sb.append(" of 4 selected.");
                    break;
                case ADJ_VARIABLE_ALL_ADJUSTMENT:
                    sb.append("All Abilities or Character Statistics can be adjusted with this power. ");
                    sb.append(count );
                    sb.append(" selected.");
                    break;
            }
            
            infoLabel.setText(sb.toString());
        }
    }
    
    private boolean canAdjustMore() {
        if ( adjustmentList == null ) return false;
        
        int count = adjustmentList.getAdjustableCount();
        
        AdjustmentPower power = (AdjustmentPower)sourceAbility.getPower();
        
        switch ( power.getAdjustmentLevel(sourceAbility) ) {
            case ADJ_SINGLE_ADJUSTMENT:
                return count < 1;
            case ADJ_VARIABLE_1_ADJUSTMENT:
                return count < 1;
            case ADJ_VARIABLE_2_ADJUSTMENT:
                return count < 2;
            case ADJ_VARIABLE_4_ADJUSTMENT:
                return count < 4;
            case ADJ_VARIABLE_ALL_ADJUSTMENT:
                return true;
            default:
                return false;
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

        infoLabel = new javax.swing.JLabel();
        sourceGroup = new javax.swing.JPanel();
        sourceScroll = new javax.swing.JScrollPane();
        sourceTree = new champions.adjustmentPowers.adjustmentTree.AdjAttackTreeTable();
        controlGroup = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        addAllButton = new javax.swing.JButton();
        removeAllButton = new javax.swing.JButton();
        destinationGroup = new javax.swing.JPanel();
        destinationScroll = new javax.swing.JScrollPane();
        destinationTree = new champions.adjustmentPowers.adjustmentTree.AdjAttackTreeTable();

        setLayout(new java.awt.GridBagLayout());

        infoLabel.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(infoLabel, gridBagConstraints);

        sourceGroup.setLayout(new java.awt.BorderLayout());

        sourceGroup.setBorder(javax.swing.BorderFactory.createTitledBorder("Available Adjustables"));
        sourceScroll.setViewportView(sourceTree);

        sourceGroup.add(sourceScroll, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(sourceGroup, gridBagConstraints);

        controlGroup.setLayout(new java.awt.GridLayout(0, 1));

        addButton.setBorder(null);
        addButton.setBorderPainted(false);
        addButton.setContentAreaFilled(false);
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        controlGroup.add(addButton);

        removeButton.setBorder(null);
        removeButton.setBorderPainted(false);
        removeButton.setContentAreaFilled(false);
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        controlGroup.add(removeButton);

        addAllButton.setBorder(null);
        addAllButton.setBorderPainted(false);
        addAllButton.setContentAreaFilled(false);
        addAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAllButtonActionPerformed(evt);
            }
        });

        controlGroup.add(addAllButton);

        removeAllButton.setBorder(null);
        removeAllButton.setBorderPainted(false);
        removeAllButton.setContentAreaFilled(false);
        removeAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAllButtonActionPerformed(evt);
            }
        });

        controlGroup.add(removeAllButton);

        add(controlGroup, new java.awt.GridBagConstraints());

        destinationGroup.setLayout(new java.awt.BorderLayout());

        destinationGroup.setBorder(javax.swing.BorderFactory.createTitledBorder("Configurated Adjustables"));
        destinationScroll.setViewportView(destinationTree);

        destinationGroup.add(destinationScroll, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(destinationGroup, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents
    
    private void removeAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAllButtonActionPerformed
        // Add your handling code here:
        removeAllAdjustables();
        updateButtons();
    }//GEN-LAST:event_removeAllButtonActionPerformed
    
    private void addAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAllButtonActionPerformed
        // Add your handling code here:
        addAllAdjustables();
        updateButtons();
    }//GEN-LAST:event_addAllButtonActionPerformed
    
    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        // Add your handling code here:
        
        TreePath destinationSelection = destinationTree.getSelectionPath();
        if ( destinationSelection != null ) {
            Object node = destinationSelection.getLastPathComponent();
            if ( node instanceof AdjAttackDestinationAbilityNode ) {
                removeAdjustables( ((AdjAttackDestinationAbilityNode)node).getTargetAbility());
            }
            else if ( node instanceof AdjAttackDestinationStatNode)  {
                removeAdjustables( ((AdjAttackDestinationStatNode)node).getStat());
            }
        }
        
        updateButtons();
    }//GEN-LAST:event_removeButtonActionPerformed
    
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        // Add your handling code here:
        TreePath sourceSelection = sourceTree.getSelectionPath();
        if ( sourceSelection != null ) {
            Object node = sourceSelection.getLastPathComponent();
            if ( node instanceof AdjAbilityNode ) {
                addAdjustables( ((AdjAbilityNode)node).getAbility());
            }
            else if ( node instanceof AdjStatNode)  {
                addAdjustables( new Characteristic(((AdjStatNode)node).getName()));
            }
        }
        
        updateButtons();
        updateDescription();
    }//GEN-LAST:event_addButtonActionPerformed
    
    private void removeAllAdjustables() {
        // Add your handling code here:
        adjustmentList.removeAllAdjustables();
    }
    
    private void addAllAdjustables() {
        // Add your handling code here:
        AdjustmentPower power = (AdjustmentPower)sourceAbility.getPower();
        if ( power.getAdjustmentLevel(sourceAbility) == ADJ_VARIABLE_ALL_ADJUSTMENT ) {
            Iterator i = adjustables.iterator();
            while ( i.hasNext() ) {
                adjustmentList.addAdjustable(i.next(), 100);
            }
        }
    }
    
    private void removeAdjustables(Adjustable adjustable) {
        // Add your handling code here:
        adjustmentList.removeAdjustable(adjustable);
    }
    
    private void addAdjustables(Adjustable adjustable) {
        // Add your handling code here:
        if ( canAdjustMore() ) {
            adjustmentList.addAdjustable(adjustable, 100);
        }
    }
    
    
    public void showPanel(AttackTreePanel atip) {
        setupPanel();
        updateButtons();
        updateDescription();
    }
    
    public JPanel getPanel() {
        return this;
    }
    
    public void hidePanel() {
        setSourceAbility(null);
        setTarget(null);
        setAdjustmentList(null);
        setBattleEvent(null);
        setAdjustables(null);
        
        sourceTree.setTreeTableModel( new DefaultTreeTableModel(new DefaultTreeTableNode() ) );
        destinationTree.setTreeTableModel( new DefaultTreeTableModel(new DefaultTreeTableNode() ) );
    }
    
    private void setupPanel() {
        if ( sourceModel == null ) sourceModel = new AdjAttackSourceModel(sourceAbility, target, type, adjustables);
        sourceTree.setTreeTableModel(sourceModel);
        
        if ( destinationModel == null ) destinationModel = new AdjAttackDestinationModel(sourceAbility, target, type, adjustmentList);
        destinationTree.setTreeTableModel(destinationModel);
        
        DefaultTreeTableNode node;
        node = (DefaultTreeTableNode)sourceModel.getRoot();
        
        Enumeration e = node.depthFirstEnumeration();
        while ( e.hasMoreElements() ) {
            node = (DefaultTreeTableNode)e.nextElement();
            if ( node.isLeaf() == false ) {
                TreePath tp = new TreePath(node.getPath());
                sourceTree.expandPath(tp);
            }
        }
        
    }
    
    /** Getter for property sourceAbility.
     * @return Value of property sourceAbility.
     */
    public Ability getSourceAbility() {
        return sourceAbility;
    }
    
    /** Setter for property sourceAbility.
     * @param sourceAbility New value of property sourceAbility.
     */
    public void setSourceAbility(Ability sourceAbility) {
        if ( this.sourceAbility != sourceAbility ) {
            sourceModel = null;
            destinationModel = null;
        }
        
        this.sourceAbility = sourceAbility;
    }
    
    /** Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        if ( this.target != target ) {
            sourceModel = null;
            destinationModel = null;
        }
        
        return target;
    }
    
    /** Setter for property target.
     * @param target New value of property target.
     */
    public void setTarget(Target target) {
        this.target = target;
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
        if ( this.battleEvent != battleEvent ) {
            sourceModel = null;
            destinationModel = null;
        }
        
        this.battleEvent = battleEvent;
    }
    
    /** Getter for property adjustmentList.
     * @return Value of property adjustmentList.
     */
    public AdjustmentList getAdjustmentList() {
        return adjustmentList;
    }
    
    /** Setter for property adjustmentList.
     * @param adjustmentList New value of property adjustmentList.
     */
    public void setAdjustmentList(AdjustmentList adjustmentList) {
        if ( this.adjustmentList != adjustmentList ) {
            sourceModel = null;
            destinationModel = null;
        }
        
        this.adjustmentList = adjustmentList;
    }
    
    /**
     * Called whenever the value of the selection changes.
     * @param e the event that characterizes the change.
     */
    public void valueChanged(TreeSelectionEvent e) {
        updateButtons();
        updateDescription();
    }
    
    /** Getter for property adjustables.
     * @return Value of property adjustables.
     */
    public ArrayList getAdjustables() {
        return adjustables;
    }
    
    /** Setter for property adjustables.
     * @param adjustables New value of property adjustables.
     */
    public void setAdjustables(ArrayList adjustables) {
        this.adjustables = adjustables;
    }
    
    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {
    }
    
    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {
    }
    
    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent e) {
    }
    
    /**
     * Invoked when the mouse has been clicked on a component.
     */
    public void mouseClicked(MouseEvent e) {
        if ( e.getSource() == sourceTree && e.getClickCount() == 2 ) {
            // Add your handling code here:
            TreePath sourceSelection = sourceTree.getSelectionPath();
            if ( sourceSelection != null ) {
                Object node = sourceSelection.getLastPathComponent();
                if ( node instanceof AdjAbilityNode ) {
                    addAdjustables( ((AdjAbilityNode)node).getAbility());
                }
                else if ( node instanceof AdjStatNode)  {
                    addAdjustables( new Characteristic(((AdjStatNode)node).getName()));
                }
            }
            
            updateButtons();
            updateDescription();
        }
        else if ( e.getSource() == destinationTree && e.getClickCount() == 2 ) {
            TreePath destinationSelection = destinationTree.getSelectionPath();
            if ( destinationSelection != null ) {
                Object node = destinationSelection.getLastPathComponent();
                if ( node instanceof AdjAttackDestinationAbilityNode ) {
                    removeAdjustables( ((AdjAttackDestinationAbilityNode)node).getTargetAbility());
                }
                else if ( node instanceof AdjAttackDestinationStatNode)  {
                    removeAdjustables( ((AdjAttackDestinationStatNode)node).getStat());
                }
            }
            
            updateButtons();
            updateDescription();
        }
    }
    
    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered(MouseEvent e) {
    }
    
    /** Getter for property type.
     * @return Value of property type.
     *
     */
    public int getType() {
        return this.type;
    }
    
    /** Setter for property type.
     * @param type New value of property type.
     *
     */
    public void setType(int type) {
        this.type = type;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAllButton;
    private javax.swing.JButton addButton;
    private javax.swing.JPanel controlGroup;
    private javax.swing.JPanel destinationGroup;
    private javax.swing.JScrollPane destinationScroll;
    private champions.adjustmentPowers.adjustmentTree.AdjAttackTreeTable destinationTree;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JButton removeAllButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JPanel sourceGroup;
    private javax.swing.JScrollPane sourceScroll;
    private champions.adjustmentPowers.adjustmentTree.AdjAttackTreeTable sourceTree;
    // End of variables declaration//GEN-END:variables
    

    
}
