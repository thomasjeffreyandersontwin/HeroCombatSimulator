/*
 * DiceRollInfoPanel.java
 *
 * Created on November 10, 2006, 10:28 AM
 */

package champions;

import champions.enums.DiceType;
import champions.exception.BadDiceException;

/**
 *
 * @author  1425
 */
public class DiceRollInfoPanel extends javax.swing.JPanel {
    
    private DiceRollInfo diceRollInfo;
    
    /** Creates new form DiceRollInfoPanel */
    public DiceRollInfoPanel() {
        initComponents();
    }
    
    public DiceRollInfo getDiceRollInfo() {
        return diceRollInfo;
    }
    
    public void setDiceRollInfo(DiceRollInfo diceRollInfo) {
      
        updateStunValue(false);
        updateBodyValue(false);
        
        this.diceRollInfo = diceRollInfo;
        
        updatePanel();
    }
    
    public void updatePanel() {
        if ( diceRollInfo == null ) return;
        
        String diceSize = getDiceSize();
        String description = getDescription();
        
        if ( diceSize != null ) {
            descriptionLabel.setText( "Enter Roll for " + description + ": " + diceSize );
        } else {
            descriptionLabel.setText( "Enter Roll for " + description  );
        }
        
        if ( isAutoroll() ) {
            bodyField.setText("AUTO");
            stunField.setText("AUTO");
            
            autoCheck.setSelected(true);
        } else {
            Dice dice = getDice();
            if ( dice != null ) {
                if ( dice.isRealized() ) {
                    bodyField.setText( dice.getBody().toString() );
                    stunField.setText( dice.getStun().toString() );
                } else {
                    bodyField.setText( "0" );
                    stunField.setText( "0" );
                }
            }
            
            autoCheck.setSelected(false);
        }
        
        updateDiceType();
    }
    
    private void updateBodyValue(boolean updatePanel) {
        if ( diceRollInfo == null ) return;
        if ( ! bodyField.getText().equals( "AUTO" ) ) {
            
            int die = 0;
            try {
                die = Integer.parseInt( bodyField.getText() );
            } catch ( NumberFormatException nfe ) {
            }
            if ( isAutoroll() == true ) {
                setAutoroll(false);
                
                getDice().setStun(0);
            }
            getDice().setBody(die);
            if ( updatePanel ) updatePanel();
        }
    }
    private void updateStunValue(boolean updatePanel) {
        if ( diceRollInfo == null ) return;
        if ( ! stunField.getText().equals( "AUTO" ) ) {
            
            int die = 0;
            try {
                die = Integer.parseInt( stunField.getText() );
            } catch ( NumberFormatException nfe ) {
            }
            if ( isAutoroll() == true ) {
                setAutoroll(false);
                
                getDice().setBody(0);
            }
            getDice().setStun(die);
            if ( updatePanel ) updatePanel();
        }
    }
    
    public boolean isAutoroll() {
        if ( diceRollInfo == null ) return false;
        return diceRollInfo.isAutoroll();
    }
    
    public void setAutoroll(boolean autoroll) {
        
        if ( diceRollInfo != null ) {
            if ( autoroll != isAutoroll() ) {
                diceRollInfo.setAutoroll(autoroll);
                if ( autoroll == true ) clearDice();
            }
        }
    }
    
    public String getDiceSize() {
        if ( diceRollInfo == null ) return "3d6";
        return diceRollInfo.getSize();
    }
    
    public void setDiceSize(String diceSize) {
        if ( diceRollInfo != null ) {
            diceRollInfo.setSize(diceSize);
        }
    }
    
    public Dice getDice() {
        if ( diceRollInfo == null ) return null;
        
        //updateBodyValue(false);
        //updateStunValue(false);
        
        return diceRollInfo.getDice();
    }
    
    public void setDice(Dice dice) {
        if ( diceRollInfo == null ) return;
        diceRollInfo.setDice(dice);
    }
    
    public String getDescription() {
        if ( diceRollInfo == null) return "";
        return diceRollInfo.getDescription();
    }
    
    public void setDescription(String description) {
        if ( diceRollInfo == null ) return;
        diceRollInfo.setDescription(description);
    }
    
    public DiceType getDiceType() {
        if ( diceRollInfo == null) return DiceType.STUNANDBODY;
        return diceRollInfo.getType();
    }
    
    public void setDiceType(DiceType diceType) {
        if ( diceRollInfo == null) return;
        diceRollInfo.setType(diceType);
    }
    
    private void updateDiceType() {
        DiceType diceType = getDiceType();
        
        switch(diceType) {
            case STUNONLY:
                stunFieldLabel.setVisible(true);
                stunField.setVisible(true);
                bodyFieldLabel.setVisible(false);
                bodyField.setVisible(false);
                break;
            case BODYONLY:
                stunFieldLabel.setVisible(false);
                stunField.setVisible(false);
                bodyFieldLabel.setVisible(true);
                bodyField.setVisible(true);
                break;
            case STUNANDBODY:
                stunFieldLabel.setVisible(true);
                stunField.setVisible(true);
                bodyFieldLabel.setVisible(true);
                bodyField.setVisible(true);
                break;
        }
    }
    
    private void clearDice() {
        try {
            Dice d = new Dice(getDiceSize(),isAutoroll());
            setDice(d);
        } catch ( BadDiceException bde ) {
            setDice( new Dice(0) );
        }
    }
    
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        
        descriptionLabel.setEnabled( enabled  );
        
        stunFieldLabel.setEnabled( enabled );
        stunField.setEnabled( enabled );
        bodyFieldLabel.setEnabled( enabled );
        bodyField.setEnabled( enabled );
        autoCheck.setEnabled( enabled );
        
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        descriptionLabel = new javax.swing.JLabel();
        bodyField = new javax.swing.JTextField();
        bodyFieldLabel = new javax.swing.JLabel();
        autoCheck = new javax.swing.JCheckBox();
        stunField = new javax.swing.JTextField();
        stunFieldLabel = new javax.swing.JLabel();

        descriptionLabel.setFont(new java.awt.Font("SansSerif", 0, 11));
        descriptionLabel.setText("Enter Dice Roll for");

        bodyField.setText("0");
        bodyField.setPreferredSize(new java.awt.Dimension(40, 20));
        bodyField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bodyFieldActionPerformed(evt);
            }
        });
        bodyField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                bodyFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                bodyFieldFocusLost(evt);
            }
        });

        bodyFieldLabel.setFont(new java.awt.Font("SansSerif", 0, 11));
        bodyFieldLabel.setText("Body");

        autoCheck.setFont(new java.awt.Font("SansSerif", 0, 11));
        autoCheck.setText("AutoRoll");
        autoCheck.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                autoCheckStateChanged(evt);
            }
        });

        stunField.setText("0");
        stunField.setPreferredSize(new java.awt.Dimension(40, 20));
        stunField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stunFieldActionPerformed(evt);
            }
        });
        stunField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                stunFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                stunFieldFocusLost(evt);
            }
        });

        stunFieldLabel.setFont(new java.awt.Font("SansSerif", 0, 11));
        stunFieldLabel.setText("Stun");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(descriptionLabel)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(stunFieldLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stunField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bodyFieldLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bodyField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(autoCheck))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(descriptionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stunField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bodyFieldLabel)
                    .addComponent(bodyField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(autoCheck)
                    .addComponent(stunFieldLabel)))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void autoCheckStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_autoCheckStateChanged
// Add your handling code here:
        if ( isAutoroll() != autoCheck.isSelected() ) {
            setAutoroll( autoCheck.isSelected() );
            updatePanel();
        }
    }//GEN-LAST:event_autoCheckStateChanged
    
    private void bodyFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_bodyFieldFocusLost
// Add your handling code here:
        updateBodyValue(true);
    }//GEN-LAST:event_bodyFieldFocusLost
    
    private void bodyFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_bodyFieldFocusGained
// Add your handling code here:
        bodyField.selectAll();
    }//GEN-LAST:event_bodyFieldFocusGained
    
    private void bodyFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bodyFieldActionPerformed
        updateBodyValue(true);
    }//GEN-LAST:event_bodyFieldActionPerformed
    
    private void stunFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_stunFieldFocusLost
        updateStunValue(true);
    }//GEN-LAST:event_stunFieldFocusLost
    
    private void stunFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_stunFieldFocusGained
// Add your handling code here:
        stunField.selectAll();
    }//GEN-LAST:event_stunFieldFocusGained
    
    private void stunFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stunFieldActionPerformed
// Add your handling code here:
        updateStunValue(true);
    }//GEN-LAST:event_stunFieldActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox autoCheck;
    private javax.swing.JTextField bodyField;
    private javax.swing.JLabel bodyFieldLabel;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JTextField stunField;
    private javax.swing.JLabel stunFieldLabel;
    // End of variables declaration//GEN-END:variables
    
}
