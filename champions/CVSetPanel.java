/*
 * CVBasePanel.java
 *
 * Created on November 13, 2000, 7:57 PM
 */

package champions;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 *
 * @author  unknown
 * @version 
 */
public class CVSetPanel extends JPanel {

    /** Holds value of property value. */
    private int value;
    /** Holds value of property index. */
    private int index;
    private String type;
    private boolean active;
    
    protected transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport (this);
    /** Creates new form CVBasePanel */
    public CVSetPanel(String type,int index,String desc,Integer value,boolean active) {
        super();
        initComponents ();
        setupIcons();
        
        if ( value == null ) {
            this.value = 0;
        }
        else {
            this.value = value.intValue();
        }
        updatePanel();
        
        this.type = type;
        setIndex(index);
        descriptionLabel.setText(desc);
        activeCheck.setSelected(active);
        this.active = active;
        
        valueText.setVisible(false);
    }
    
    private void setupIcons() {
        Icon i = UIManager.getIcon("Editor.upButtonNormal");
        upButton.setIcon(i);
        
        i = UIManager.getIcon("Editor.upButtonPressed");
        upButton.setPressedIcon(i);
        
        i = UIManager.getIcon("Editor.downButtonNormal");
        downButton.setIcon(i);
        
        i = UIManager.getIcon("Editor.downButtonPressed");
        downButton.setPressedIcon(i);
    }
    
    public void updatePanel() {
        String s =  "= " + Integer.toString( getValue() );
        
        valueLabel.setText( s );
    }
    
     /** Add a PropertyChangeListener to the listener list.
     * @param l The listener to add.
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener (l);
    }
    /** Removes a PropertyChangeListener from the listener list.
     * @param l The listener to remove.
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener (l);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        activeCheck = new javax.swing.JCheckBox();
        descriptionLabel = new javax.swing.JLabel();
        valueLabel = new javax.swing.JLabel();
        valueText = new javax.swing.JTextField();
        buttonPanel = new javax.swing.JPanel();
        upButton = new javax.swing.JButton();
        downButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        activeCheck.setPreferredSize(new java.awt.Dimension(18, 18));
        activeCheck.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                activeCheckStateChanged(evt);
            }
        });

        add(activeCheck, new java.awt.GridBagConstraints());

        descriptionLabel.setFont(new java.awt.Font("Arial", 0, 11));
        add(descriptionLabel, new java.awt.GridBagConstraints());

        valueLabel.setFont(new java.awt.Font("Arial", 0, 11));
        valueLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        valueLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                valueLabelMouseClicked(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        add(valueLabel, gridBagConstraints);

        valueText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        valueText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                valueTextActionPerformed(evt);
            }
        });

        valueText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                valueTextFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        add(valueText, gridBagConstraints);

        buttonPanel.setLayout(new javax.swing.BoxLayout(buttonPanel, javax.swing.BoxLayout.Y_AXIS));

        buttonPanel.setPreferredSize(new java.awt.Dimension(15, 18));
        upButton.setBorderPainted(false);
        upButton.setContentAreaFilled(false);
        upButton.setFocusPainted(false);
        upButton.setPreferredSize(new java.awt.Dimension(13, 9));
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(upButton);

        downButton.setBorderPainted(false);
        downButton.setContentAreaFilled(false);
        downButton.setFocusPainted(false);
        downButton.setPreferredSize(new java.awt.Dimension(13, 9));
        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(downButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(buttonPanel, gridBagConstraints);

    }//GEN-END:initComponents

  private void activeCheckStateChanged (javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_activeCheckStateChanged
// Add your handling code here:
    if ( activeCheck.isSelected() != this.active ) {
        active = activeCheck.isSelected();
        
        propertyChangeSupport.firePropertyChange( type + Integer.toString(index) + ".ACTIVE", 
        (! active) ? "TRUE":"FALSE", 
        active ? "TRUE":"FALSE");
    }
  }//GEN-LAST:event_activeCheckStateChanged

  private void downButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downButtonActionPerformed
// Add your handling code here:
        if ( valueText.isVisible() ) {
        valueText.setText( Integer.toString( getValue() - 1 ) );
        valueText.setVisible(false);
        valueLabel.setVisible(true);
    }
    setValue( getValue() -1 );
  }//GEN-LAST:event_downButtonActionPerformed

  private void upButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upButtonActionPerformed
// Add your handling code here:
        if ( valueText.isVisible() ) {
        valueText.setText( Integer.toString( getValue() + 1 ) );
        valueText.setVisible(false);
        valueLabel.setVisible(true);
    }
    setValue( getValue() + 1);
  }//GEN-LAST:event_upButtonActionPerformed

  private void valueTextFocusLost (java.awt.event.FocusEvent evt) {//GEN-FIRST:event_valueTextFocusLost
// Add your handling code here:
        try {
        int newValue = Integer.parseInt( valueText.getText() );
        setValue(newValue);
        //valueLabel.setText( Integer.toString(value) );
        valueText.setVisible(false);
        valueLabel.setVisible(true);
    }
    catch ( NumberFormatException nfe ) {
        valueText.setText( Integer.toString(value) );
        valueText.selectAll();
    }
  }//GEN-LAST:event_valueTextFocusLost

  private void valueLabelMouseClicked (java.awt.event.MouseEvent evt) {//GEN-FIRST:event_valueLabelMouseClicked
// Add your handling code here:
    // Add your handling code here:
    valueText.setText( Integer.toString(value) );
    
    valueText.setPreferredSize( valueLabel.getSize() );
    valueLabel.setVisible(false);
    valueText.setVisible(true);
    valueText.requestFocus();
    valueText.selectAll();
  }//GEN-LAST:event_valueLabelMouseClicked

  private void valueTextActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_valueTextActionPerformed
// Add your handling code here:
        try {
        int newValue = Integer.parseInt( valueText.getText() );
        setValue(newValue);
        //valueLabel.setText( Integer.toString(value) );
        valueText.setVisible(false);
        valueLabel.setVisible(true);
    }
    catch ( NumberFormatException nfe ) {
        valueText.setText( Integer.toString(value) );
        valueText.selectAll();
    }
  }//GEN-LAST:event_valueTextActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton upButton;
    private javax.swing.JCheckBox activeCheck;
    private javax.swing.JTextField valueText;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JButton downButton;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel valueLabel;
    // End of variables declaration//GEN-END:variables

    /** Getter for property value.
     * @return Value of property value.
     */
    public int getValue() {
        return value;
    }
    /** Setter for property value.
     * @param value New value of property value.
     */
    public void setValue(Integer value) {
        if ( value == null ) {
            setValue(0);
        }
        else {
            setValue(value.intValue());
        }
    }
    
    public void setValue(int value) {
        if ( value != this.value ) {
            int oldvalue = this.value;
            this.value = value;
            propertyChangeSupport.firePropertyChange( type + Integer.toString(index) + ".VALUE", oldvalue, value);
            updatePanel();
            
        } 
    }
    
    /** Getter for property index.
     * @return Value of property index.
     */
    public int getIndex() {
        return index;
    }
    /** Setter for property index.
     * @param index New value of property index.
     */
    public void setIndex(int index) {
        this.index = index;
    }
}