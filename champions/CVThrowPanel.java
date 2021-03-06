/*
 * CVBasePanel.java
 *
 * Created on November 13, 2000, 7:57 PM
 */

package champions;


/**
 *
 * @author  unknown
 * @version
 */
public class CVThrowPanel extends javax.swing.JPanel {
    /** Holds value of property value. */
    protected int value;
    /** Holds value of property index. */
    protected int index;
    protected String type;
    protected boolean active;
    
    static private final String options[] = new String[] { "Balanced and aerodynamic (-0)", "Balanced or aerodynamic (-2)", "Not balanced nor aerodynamic (-4)"};
    
    
    transient protected java.beans.PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
    
    /** Creates new form CVBasePanel */
    public CVThrowPanel(String type, int index, String desc, Integer value, boolean active) {
        super();
        initComponents();
        
        for (int i =0;i<options.length;i++) {
            valueText.addItem( options[i] );
        }
        
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
    }
    
    public void updatePanel() {
        valueText.setSelectedIndex(  value / -2);
    }
    
    /** Add a PropertyChangeListener to the listener list.
     * @param l The listener to add.
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }
    /** Removes a PropertyChangeListener from the listener list.
     * @param l The listener to remove.
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
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
        valueText = new javax.swing.JComboBox();

        setLayout(new java.awt.GridBagLayout());

        activeCheck.setPreferredSize(new java.awt.Dimension(18, 18));
        activeCheck.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                activeCheckStateChanged(evt);
            }
        });

        add(activeCheck, new java.awt.GridBagConstraints());

        descriptionLabel.setFont(new java.awt.Font("Arial", 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(descriptionLabel, gridBagConstraints);

        valueText.setFont(new java.awt.Font("Arial", 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        valueText.setMinimumSize(new java.awt.Dimension(30, 25));
        valueText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                valueTextActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        add(valueText, gridBagConstraints);

    }//GEN-END:initComponents
    
  private void valueTextActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_valueTextActionPerformed
      // Add your handling code here:
      setValue( -2 * valueText.getSelectedIndex() );
  }//GEN-LAST:event_valueTextActionPerformed
  
  private void activeCheckStateChanged (javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_activeCheckStateChanged
      // Add your handling code here:
      if ( activeCheck.isSelected() != this.active ) {
          active = activeCheck.isSelected();
          
          propertyChangeSupport.firePropertyChange( type + Integer.toString(index) + ".ACTIVE",
            (! active) ? "TRUE":"FALSE", active ? "TRUE":"FALSE");
      }
  }//GEN-LAST:event_activeCheckStateChanged
  
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox activeCheck;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JComboBox valueText;
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