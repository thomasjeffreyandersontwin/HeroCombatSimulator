/*
 * PADDiceEditor.java
 *
 * Created on October 9, 2000, 2:47 PM
 */

package champions;

import champions.interfaces.PADValueListener;
import java.awt.Color;
import java.awt.event.KeyEvent;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.text.Keymap;


/**
 *
 * @author  unknown
 * @version
 */
public class PADIntegerEditor extends PADAbstractEditor {

    /** Holds value of property description. */
    private String description;
    /** Holds value of property propertyName. */
    private String propertyName;
    /** Holds value of property value. */
    private Integer value;
      /** Holds value of property increment. */
    private int increment = 1;  
    /** Creates new form PADDiceEditor */
    public PADIntegerEditor() {
        initComponents ();

        setDescription("");
        setPropertyName("");

        textField.setText("");
        setValue(new Integer(0));

        descriptionLabel.setForeground( getForeground() );
        
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        Keymap map = textField.getKeymap();
        map.removeKeyStrokeBinding(enter);
        
        InputMap im = textField.getInputMap();
        im.clear();
        
        setupIcons();
    }

    public PADIntegerEditor(String key,String desc,PADValueListener l) {
        initComponents ();

        setDescription(desc);
        setPropertyName(key);

        textField.setText("1");
        setValue( new Integer(1) );

        addPADValueListener(l);

        descriptionLabel.setForeground( getForeground() );

        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        Keymap map = textField.getKeymap();
        map.removeKeyStrokeBinding(enter);
        
        InputMap im = textField.getInputMap();
        im.clear();

        setupIcons();
    }

    public PADIntegerEditor(String name,String desc, Integer initial,PADValueListener l) {
        this(name, desc, l);

        textField.setText(initial.toString());
        setValue(initial);
        
        setupIcons();
    }
    
    public void updateUI() {
        super.updateUI();
        
        setupIcons();
    }
    
    private void setupIcons() {
        Icon icon;
        icon = UIManager.getIcon("Editor.upButtonNormal");
        if ( upButton != null) upButton.setIcon(icon );
        icon = UIManager.getIcon("Editor.upButtonPressed");
        if ( upButton != null) upButton.setPressedIcon(icon );
        icon = UIManager.getIcon("Editor.downButtonNormal");
        if ( downButton != null) downButton.setIcon(icon );
        icon = UIManager.getIcon("Editor.downButtonPressed");
        if ( downButton != null) downButton.setPressedIcon(icon );
    }

    public void updateValue() {
        String currentValue = textField.getText();
        Integer newValue;
        try {
            newValue = new Integer( currentValue );
            setValue( newValue );
        }
        catch ( NumberFormatException nfe) {
            textField.setText( getValue().toString() );
        }
    }
    


    /** Getter for property description.
     * @return Value of property description.
     */
    public String getDescription() {
        return description;
    }
    /** Setter for property description.
     * @param description New value of property description.
     */
    public void setDescription(String description) {
        this.description = description;
        descriptionLabel.setText( description );
    }
    /** Getter for property propertyName.
     * @return Value of property propertyName.
     */
    public String getPropertyName() {
        return propertyName;
    }
    /** Setter for property propertyName.
     * @param propertyName New value of property propertyName.
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
    /** Getter for property value.
     * @return Value of property value.
     */
    public Integer getValue() {
        return value;
    }
    /** Setter for property value.
     * @param value New value of property value.
     */
    public void setValue(Integer value) {
        Integer oldvalue = this.value;
        if ( value.equals( oldvalue ) == false && firePADValueChanging(propertyName, value, oldvalue)) {
            this.value = value;
            textField.setText( value.toString() );
            firePADValueChanged( propertyName, value, oldvalue);
        }
        else {
            textField.setText( oldvalue.toString() );
        }
    }

    public void setValue(Object o) {
        if ( o != null && o.getClass() == Integer.class) {
            setValue( (Integer) o);
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        descriptionLabel.setEnabled(enabled);
        textField.setEnabled(enabled);
        upButton.setEnabled(enabled);
        downButton.setEnabled(enabled);
    }

    public void setForeground( Color c ) {
        super.setForeground(c);
        if ( descriptionLabel != null ) descriptionLabel.setForeground(c);
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        descriptionLabel = new javax.swing.JLabel();
        textField = new javax.swing.JTextField();
        upButton = new javax.swing.JButton();
        downButton = new javax.swing.JButton();
        
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        descriptionLabel.setText("Description");
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(0, 5, 0, 0);
        add(descriptionLabel, gridBagConstraints1);
        
        textField.setText("1");
        textField.setPreferredSize(new java.awt.Dimension(40, 20));
        textField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dieTextActionPerformed(evt);
            }
        });
        
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                dieTextFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                dieTextFocusLost(evt);
            }
        });
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(0, 5, 0, 0);
        add(textField, gridBagConstraints1);
        
        upButton.setPreferredSize(new java.awt.Dimension(13, 9));
        upButton.setMaximumSize(new java.awt.Dimension(13, 9));
        upButton.setContentAreaFilled(false);
        upButton.setMinimumSize(new java.awt.Dimension(13, 9));
        upButton.setBorderPainted(false);
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dieUpActionPerformed(evt);
            }
        });
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 1.0;
        add(upButton, gridBagConstraints1);
        
        downButton.setPreferredSize(new java.awt.Dimension(13, 9));
        downButton.setMaximumSize(new java.awt.Dimension(13, 9));
        downButton.setContentAreaFilled(false);
        downButton.setMinimumSize(new java.awt.Dimension(13, 9));
        downButton.setBorderPainted(false);
        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dieDownActionPerformed(evt);
            }
        });
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 2;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 1.0;
        add(downButton, gridBagConstraints1);
        
    }//GEN-END:initComponents

  private void dieUpActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dieUpActionPerformed
    // Add your handling code here:
    Integer newValue = new Integer( getValue().intValue() + increment );
    setValue( newValue );
  }//GEN-LAST:event_dieUpActionPerformed

  private void dieDownActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dieDownActionPerformed
    // Add your handling code here:
    Integer newValue = new Integer( getValue().intValue() - increment );
    setValue( newValue );
  }//GEN-LAST:event_dieDownActionPerformed

  private void dieTextFocusGained (java.awt.event.FocusEvent evt) {//GEN-FIRST:event_dieTextFocusGained
    // Add your handling code here:
    textField.selectAll();
  }//GEN-LAST:event_dieTextFocusGained

  private void dieTextFocusLost (java.awt.event.FocusEvent evt) {//GEN-FIRST:event_dieTextFocusLost
    // Add your handling code here:
    updateValue();
  }//GEN-LAST:event_dieTextFocusLost

  private void dieTextActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dieTextActionPerformed
    // Add your handling code here:
    updateValue();
  }//GEN-LAST:event_dieTextActionPerformed

  /** Getter for property increment.
   * @return Value of property increment.
 */
  public int getIncrement() {
      return increment;
  }  

  /** Setter for property increment.
   * @param increment New value of property increment.
 */
  public void setIncrement(int increment) {
      this.increment = increment;
  }
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel descriptionLabel;
  private javax.swing.JTextField textField;
  private javax.swing.JButton upButton;
  private javax.swing.JButton downButton;
  // End of variables declaration//GEN-END:variables




}