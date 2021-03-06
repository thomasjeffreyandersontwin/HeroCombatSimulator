/*
 * MovementDistancePanel.java
 *
 * Created on May 1, 2002, 7:18 PM
 */

package champions.attackTree;

import champions.ActivationInfo;
import champions.BattleEvent;
import champions.event.PADValueEvent;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.PADValueListener;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;

/**
 *
 * @author  Trevor Walker
 */
public class MovementDistancePanel extends JPanel implements AttackTreeInputPanel, PADValueListener, ChampionsConstants    {

    /** Holds value of property battleEvent. */
    private BattleEvent battleEvent;
    
    public static MovementDistancePanel defaultPanel;
    
    private AttackTreePanel atip = null;
    
    /** Holds value of property distance. */
    private int distance;
    
    /** Creates new form MovementDistancePanel */
    public MovementDistancePanel() {
        initComponents();
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(distanceMovedButton);
        bg.add(halfButton);
        bg.add(fullButton);
        
        distanceEditor.addPADValueListener(this);
    }
    
    static public MovementDistancePanel getDefaultPanel(BattleEvent be, int maximumDistance, boolean fullMoveAllowed) {
        if ( defaultPanel == null ) defaultPanel = new MovementDistancePanel();
        
        defaultPanel.setBattleEvent(be);
        defaultPanel.setMaximumDistance(maximumDistance);
        defaultPanel.setFullMoveAllowed(fullMoveAllowed);
        
        return defaultPanel;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        distanceMovedButton = new javax.swing.JRadioButton();
        distanceEditor = new champions.PADIntegerEditor();
        distanceLabel = new javax.swing.JLabel();
        halfButton = new javax.swing.JRadioButton();
        fullButton = new javax.swing.JRadioButton();
        
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        distanceMovedButton.setText("DistanceFromCollision Moved");
        distanceMovedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                distanceMovedButtonActionPerformed(evt);
            }
        });
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(distanceMovedButton, gridBagConstraints1);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        add(distanceEditor, gridBagConstraints1);
        
        distanceLabel.setText("jLabel1");
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 1.0;
        add(distanceLabel, gridBagConstraints1);
        
        halfButton.setText("Half Move");
        halfButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                halfButtonActionPerformed(evt);
            }
        });
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(halfButton, gridBagConstraints1);
        
        fullButton.setText("Full Move");
        fullButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fullButtonActionPerformed(evt);
            }
        });
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add(fullButton, gridBagConstraints1);
        
    }//GEN-END:initComponents

    private void fullButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fullButtonActionPerformed
        // Add your handling code here:
        setDistance( MOVEMENT_FULL_MOVE );
        atip.advanceNode();
    }//GEN-LAST:event_fullButtonActionPerformed

    private void halfButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_halfButtonActionPerformed
        // Add your handling code here:
        setDistance( MOVEMENT_HALF_MOVE );
        atip.advanceNode();
    }//GEN-LAST:event_halfButtonActionPerformed

    private void distanceMovedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_distanceMovedButtonActionPerformed
        // Add your handling code here:
        setDistance( distanceEditor.getValue().intValue() );
        selectDistanceMovedButton();
    }//GEN-LAST:event_distanceMovedButtonActionPerformed

    private void selectDistanceMovedButton() {
        if ( distanceMovedButton.isSelected() == false ) {
            distanceMovedButton.setSelected(true);
        }
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

    public void showPanel(AttackTreePanel atip) {
        this.atip = atip;
        
        // Set the Text of the Full and Half Move Buttons
        fullButton.setEnabled(fullMoveAllowed);
        if ( fullMoveAllowed ) {
            fullButton.setText( "Full Move (" + Integer.toString(maximumDistance) + "\")" );
            halfButton.setText( "Half Move (" + Integer.toString((int)Math.ceil((double)maximumDistance/2.0)) + "\")" );
        }
        else {
            fullButton.setText( "Full Move (Not Allowed)" );
            halfButton.setText( "Half Move (" + Integer.toString(maximumDistance) + "\")" );
        }
        
        
        
        // Set the Text of the DistanceFromCollision Button
        distanceLabel.setText( " (0\" to " + Integer.toString(maximumDistance) + "\")");
        
        // Figure out which button should be selected..
        ActivationInfo ai = battleEvent.getActivationInfo();
        Integer distance = ai.getDistanceMoved();
        
        if ( distance == null ) {
            distanceEditor.setValue( new Integer(maximumDistance) );
            distanceMovedButton.setSelected(true);
        }
        else {
            int d = distance.intValue();
            
            if ( d == MOVEMENT_FULL_MOVE ) {
                if ( fullMoveAllowed ) {
                    fullButton.setSelected(true);
                }
                else {
                    halfButton.setSelected(true);
                }
            }
            else if ( d == MOVEMENT_HALF_MOVE ) {
                halfButton.setSelected(true);
            }
            else {
                distanceEditor.setValue( distance );
                distanceMovedButton.setSelected(true);
            }
        }
    }
    
    public JPanel getPanel() {
        return this;
    }
    
    public void hidePanel() {
        ActivationInfo ai = battleEvent.getActivationInfo();
        ai.setDistanceMoved(distance);
    }
    
    /** Getter for property distance.
     * @return Value of property distance.
     */
    public int getDistance() {
        return distance;
    }
    
    /** Setter for property distance.
     * @param distance New value of property distance.
     */
    public void setDistance(int distance) {
        this.distance = distance;
    }
    
    public boolean PADValueChanging(PADValueEvent evt) {
        int value = ((Integer)evt.getValue()).intValue();
        
        return ( value >= 0 && value <= maximumDistance );
    }
    
    public void PADValueChanged(PADValueEvent evt) {
        if ( distanceMovedButton.isSelected() ) {
            setDistance( ((Integer)evt.getValue()).intValue() );
        }
    }
    
    /** Getter for property maximumDistance.
     * @return Value of property maximumDistance.
     */
    public int getMaximumDistance() {
        return maximumDistance;
    }
    
    /** Setter for property maximumDistance.
     * @param maximumDistance New value of property maximumDistance.
     */
    public void setMaximumDistance(int maximumDistance) {
        this.maximumDistance = maximumDistance;
    }
    
    /** Getter for property fullMoveAllowed.
     * @return Value of property fullMoveAllowed.
     */
    public boolean isFullMoveAllowed() {
        return fullMoveAllowed;
    }
    
    /** Setter for property fullMoveAllowed.
     * @param fullMoveAllowed New value of property fullMoveAllowed.
     */
    public void setFullMoveAllowed(boolean fullMoveAllowed) {
        this.fullMoveAllowed = fullMoveAllowed;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton distanceMovedButton;
    private champions.PADIntegerEditor distanceEditor;
    private javax.swing.JLabel distanceLabel;
    private javax.swing.JRadioButton halfButton;
    private javax.swing.JRadioButton fullButton;
    // End of variables declaration//GEN-END:variables

    /** Holds value of property maximumDistance. */
    private int maximumDistance;    

    /** Holds value of property fullMoveAllowed. */
    private boolean fullMoveAllowed;
    
}
