/*
 * AdjConfigDialog.java
 *
 * Created on March 5, 2002, 1:37 PM
 */

package champions.adjustmentPowers.adjustmentTree;


import champions.*;
import champions.Power;
import champions.SpecialEffect;
import champions.adjustmentPowers.AdjustmentPower;
import champions.interfaces.*;
import champions.parameters.ParameterList;

import treeTable.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.event.*;


/**
 *
 * @author  Trevor Walker
 */
public class AdjConfigDialog extends javax.swing.JDialog
implements ChampionsConstants, MouseListener, TreeSelectionListener {
    
    private static AdjConfigDialog defaultDialog;
    /** Holds value of property ability. */
    private Ability ability;
    
    /** Holds value of property parameterList. */
    private ParameterList parameterList;
    
    /** Holds value of property parameter. */
    private String parameter;
    
    private AdjConfigSourceModel sourceModel;
    private AdjConfigDestinationModel destinationModel;
    
    /** Holds value of property adjustmentType. */
    private int adjustmentType;
    
    /** Creates new form AdjConfigDialog */
    public AdjConfigDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    private void setupDialog() {
        Target source = ability.getSource();
        
        sourceModel = new AdjConfigSourceModel(adjustmentType, getAdjustmentLevel(), source);
        adjConfigSourceTree.setTreeTableModel( sourceModel );
        
        TreeSelectionModel tsm = adjConfigSourceTree.getSelectionModel();
        tsm.setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
        
        TreeSelectionModel tdm = adjConfigDestinationTree.getSelectionModel();
        tdm.setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
        
        destinationModel = new AdjConfigDestinationModel(ability, adjustmentType, parameterList, parameter);
        adjConfigDestinationTree.setTreeTableModel(destinationModel);
        
        adjConfigSourceTree.addTreeSelectionListener( this);
        adjConfigDestinationTree.addTreeSelectionListener( this);
        
        adjConfigSourceTree.addMouseListener(this);
        adjConfigDestinationTree.addMouseListener(this);
        
        updateButtons();
        
    }
    
    public static AdjConfigDialog getDefaultDialog(Ability ability, int adjustmentType, ParameterList parameterList, String parameter) {
        if ( defaultDialog == null ) defaultDialog = new AdjConfigDialog(null, false);
        
        defaultDialog.setAbility(ability);
        defaultDialog.setAdjustmentType(adjustmentType);
        defaultDialog.setParameterList(parameterList);
        defaultDialog.setParameter(parameter);
        
        defaultDialog.setupDialog();
        
        return defaultDialog;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        instructionLabel = new javax.swing.JLabel();
        destinationScroll = new javax.swing.JScrollPane();
        adjConfigDestinationTree = new champions.adjustmentPowers.adjustmentTree.AdjTreeTable();
        destinationLabel = new javax.swing.JLabel();
        sourceScroll = new javax.swing.JScrollPane();
        adjConfigSourceTree = new champions.adjustmentPowers.adjustmentTree.AdjTreeTable();
        sourceLabel = new javax.swing.JLabel();
        okayButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        addAllButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        removeButton = new javax.swing.JButton();
        removeAllButton = new javax.swing.JButton();

        setTitle("Adjustment Power Configuration");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        instructionLabel.setText("Instructions");

        destinationScroll.setViewportView(adjConfigDestinationTree);

        destinationLabel.setText("Configured Powers & FX");

        sourceScroll.setViewportView(adjConfigSourceTree);

        sourceLabel.setText("Available Powers & FX");

        okayButton.setText("Okay");
        okayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okayButtonActionPerformed(evt);
            }
        });

        addButton.setText("Add >");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        jPanel1.add(addButton);

        addAllButton.setText("Add All >>");
        addAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAllButtonActionPerformed(evt);
            }
        });
        jPanel1.add(addAllButton);

        removeButton.setText("<  Remove");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });
        jPanel2.add(removeButton);

        removeAllButton.setText("<< Remove All");
        removeAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAllButtonActionPerformed(evt);
            }
        });
        jPanel2.add(removeAllButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(instructionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                                .addComponent(sourceLabel)
                                .addComponent(sourceScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(16, 16, 16)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(destinationScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                                .addComponent(destinationLabel)
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE))
                            .addContainerGap()))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(okayButton)
                        .addContainerGap())))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {destinationScroll, sourceScroll});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(instructionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sourceLabel)
                    .addComponent(destinationLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(destinationScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sourceScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(okayButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void removeAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAllButtonActionPerformed
        // Add your handling code here:
        parameterList.removeAllIndexedParameterValues(parameter);
        
    }//GEN-LAST:event_removeAllButtonActionPerformed
    
    private void addAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAllButtonActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_addAllButtonActionPerformed
    
    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        // Add your handling code here:
        TreePath destinationSelection = adjConfigDestinationTree.getSelectionPath();
        if ( destinationSelection != null ) {
            removeDestination( (AdjTreeTableNode) destinationSelection.getLastPathComponent() ) ;
        }
        updateButtons();
    }//GEN-LAST:event_removeButtonActionPerformed
    
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        // Add your handling code here:
        TreePath sourceSelection = adjConfigSourceTree.getSelectionPath();
        if ( sourceSelection != null ) {
            addDestination( (AdjTreeTableNode) sourceSelection.getLastPathComponent() );
        }
        updateButtons();
    }//GEN-LAST:event_addButtonActionPerformed
    
    private void okayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okayButtonActionPerformed
        // Add your handling code here:
        close();
    }//GEN-LAST:event_okayButtonActionPerformed
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        close();
    }//GEN-LAST:event_closeDialog
    
    private void updateButtons() {
        TreePath sourceSelection = adjConfigSourceTree.getSelectionPath();
        addButton.setEnabled( sourceSelection != null && isAddible( getAdjTreeTableNodeObject(sourceSelection.getLastPathComponent() ) ) );
        
        TreePath destinationSelection = adjConfigDestinationTree.getSelectionPath();
        removeButton.setEnabled( destinationSelection != null );
        
        removeAllButton.setEnabled( getDestinationCount() > 0);
        addAllButton.setEnabled( false );
    }
    
    
    private void close() {
        setVisible(false);
        ability.getPower().configurePAD(ability, parameterList);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new AdjConfigDialog(new javax.swing.JFrame(), true).setVisible(true);
    }

    public void setVisible(boolean b) {
        if ( b == true ) ChampionsUtilities.centerWindow(this);
        super.setVisible(b);
    }
    

    
    /** Getter for property ability.
     * @return Value of property ability.
     */
    public Ability getAbility() {
        return ability;
    }
    
    /** Setter for property ability.
     * @param ability New value of property ability.
     */
    public void setAbility(Ability ability) {
        this.ability = ability;
    }
    
    /** Getter for property parameterList.
     * @return Value of property parameterList.
     */
    public ParameterList getParameterList() {
        return parameterList;
    }
    
    /** Setter for property parameterList.
     * @param parameterList New value of property parameterList.
     */
    public void setParameterList(ParameterList parameterList) {
        this.parameterList = parameterList;
    }
    
    /** Getter for property parameter.
     * @return Value of property parameter.
     */
    public String getParameter() {
        return parameter;
    }
    
    /** Setter for property parameter.
     * @param parameter New value of property parameter.
     */
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
    
    /** Getter for property adjustmentType.
     * @return Value of property adjustmentType.
     */
    public int getAdjustmentType() {
        return adjustmentType;
    }
    
    /** Setter for property adjustmentType.
     * @param adjustmentType New value of property adjustmentType.
     */
    public void setAdjustmentType(int adjustmentType) {
        this.adjustmentType = adjustmentType;
    }
    
    /** Getter for property adjustmentLevel.
     * @return Value of property adjustmentLevel.
     */
    public int getAdjustmentLevel() {
        AdjustmentPower power = (AdjustmentPower)ability.getPower();
        return power.getAdjustmentLevel(ability);
    }
    

    private boolean isAddible(Object o) {
        return ( o instanceof Power 
        || o instanceof Ability 
        || o instanceof SpecialEffect 
        || o instanceof Characteristic
        || o instanceof String ) 
        && (hasDestination(o) == false);
    }
    
    private boolean hasDestination(Object o) {
        if ( o instanceof String ) {
            o = new Characteristic((String)o);
        }
        
        boolean result = false;
        if ( parameterList != null && parameter != null && o != null ) {
            for( int index = parameterList.getIndexedParameterSize(parameter) - 1; index >=0; index--) {
                if ( parameterList.getIndexedParameterValue(parameter, index) == o ) {
                    result = true;
                    break;
                }
            }
            
        }
        return result;
    }

    
    private int getDestinationCount() {
        return parameterList.getIndexedParameterSize(parameter);
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
        if ( e.getSource() == adjConfigSourceTree && e.getClickCount() == 2 ) {
            // Add your handling code here:
            TreePath sourceSelection = adjConfigSourceTree.getSelectionPath();
            if ( sourceSelection != null ) {
                Object node = sourceSelection.getLastPathComponent();
                addDestination((TreeTableNode)node);
            }
            
            updateButtons();
        }
        else if ( e.getSource() == adjConfigDestinationTree && e.getClickCount() == 2 ) {
            TreePath destinationSelection = adjConfigDestinationTree.getSelectionPath();
            if ( destinationSelection != null ) {
                Object node = destinationSelection.getLastPathComponent();
                removeDestination((TreeTableNode)node);
            }
            
            updateButtons();
        }
    }
    
    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered(MouseEvent e) {
    }
    
    /**
     * Called whenever the value of the selection changes.
     * @param e the event that characterizes the change.
     */
    public void valueChanged(TreeSelectionEvent e) {
        updateButtons();
    }
    
    private Object getAdjTreeTableNodeObject(Object node) {
        if ( node instanceof AdjAbilityNode ) {
            Object o = ((AdjAbilityNode)node).getAbility();
            return o;
        }
        else if ( node instanceof AdjPowerNode ) {
            Object o = ((AdjPowerNode)node).getPower();
            return o;
        }
        else if ( node instanceof AdjSpecialEffectNode ) {
            Object o = ((AdjSpecialEffectNode)node).getSpecialEffect();
            return o;
        }
        else if ( node instanceof AdjStatNode ) {
            Object o = new Characteristic(((AdjStatNode)node).getName());
            return o;
        }
        return null;
    }
    
    private void addDestination(TreeTableNode node ) {
        if ( node instanceof AdjAbilityNode ) {
            Object o = ((AdjAbilityNode)node).getAbility();
            if ( hasDestination(o) == false ) {
                addDestinationToParameterList(o);
            }
        }
        else if ( node instanceof AdjPowerNode ) {
            Object o = ((AdjPowerNode)node).getPower();
            if ( hasDestination(o) == false ) {
                addDestinationToParameterList(o);
            }
        }
        else if ( node instanceof AdjSpecialEffectNode ) {
            Object o = ((AdjSpecialEffectNode)node).getSpecialEffect();
            if ( hasDestination(o) == false ) {
                addDestinationToParameterList(o);
            }
        }
        else if ( node instanceof AdjStatNode ) {
            Object o = new Characteristic(((AdjStatNode)node).getName());
            if ( hasDestination(o) == false ) {
                addDestinationToParameterList(o);
            }
        }
    }
    
    private void removeDestination(TreeTableNode node) {
                if ( node instanceof AdjPowerNode ) {
                    removeDestinationFromParameterList( ((AdjPowerNode)node).getPower() );
                }
                else if ( node instanceof AdjStatNode ) {
                    removeDestinationFromParameterList( new Characteristic(((AdjStatNode)node).getName() ));
                }
                else if ( node instanceof AdjSpecialEffectNode ) {
                    removeDestinationFromParameterList(((AdjSpecialEffectNode)node).getSpecialEffect() );
                }
                else if (  node instanceof AdjAbilityNode ) {
                    removeDestinationFromParameterList( ((AdjAbilityNode)node).getAbility() );
                }
    }
    
        private void addDestinationToParameterList(Object o ) {
        if ( parameterList != null && parameter != null && o != null ) {
            parameterList.addIndexedParameterValue(parameter, o );
        }
    }
    
    private void removeDestinationFromParameterList(Object o) {
        if ( parameterList != null && parameter != null && o != null ) {
            parameterList.removeIndexedParameterValue(parameter, o);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAllButton;
    private javax.swing.JButton addButton;
    private champions.adjustmentPowers.adjustmentTree.AdjTreeTable adjConfigDestinationTree;
    private champions.adjustmentPowers.adjustmentTree.AdjTreeTable adjConfigSourceTree;
    private javax.swing.JLabel destinationLabel;
    private javax.swing.JScrollPane destinationScroll;
    private javax.swing.JLabel instructionLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton okayButton;
    private javax.swing.JButton removeAllButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JLabel sourceLabel;
    private javax.swing.JScrollPane sourceScroll;
    // End of variables declaration//GEN-END:variables
    
}
