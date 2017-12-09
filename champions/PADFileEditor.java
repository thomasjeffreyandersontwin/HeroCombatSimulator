/*
 * PADDiceEditor.java
 *
 * Created on October 9, 2000, 2:47 PM
 */

package champions;

import java.beans.*;
import javax.swing.*;
import java.io.*;
import champions.interfaces.*;

import java.awt.Color;
/**
 *
 * @author  unknown
 * @version
 */
public class PADFileEditor extends PADAbstractEditor {

    /** Holds value of property description. */
    private String description;
    /** Holds value of property propertyName. */
    private String propertyName;
    /** Holds value of property value. */
    private String value;
    /** Holds value of property file. */
    private boolean file;
    /** Creates new form PADDiceEditor */
    public PADFileEditor() {
        initComponents ();

        setDescription("");
        setPropertyName("");

        textField.setText("");
        setValue( "" );
        
        descriptionLabel.setForeground( getForeground() );
    }
    
    public PADFileEditor(String key,String desc, boolean file, PADValueListener l) {
        initComponents ();

        setDescription(desc);
        setPropertyName(key);

        textField.setText("");
        setValue( "" );
        
        setFile(file);

        addPADValueListener(l);
        
        descriptionLabel.setForeground( getForeground() );
    }

    public PADFileEditor(String name,String desc,String initial, boolean file, PADValueListener l) {
        this(name, desc, file, l);

        textField.setText(initial.toString());
        setValue(initial);
        
        setFile(file);
    }

    public void updateValue() {
        String currentValue = textField.getText();

        setValue( currentValue );

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
    public String getValue() {
        return value;
    }
    /** Setter for property value.
     * @param value New value of property value.
     */
    public void setValue(String value) {
        String oldvalue = this.value;
        if ( value.equals( oldvalue ) == false && firePADValueChanging(propertyName, value, oldvalue) ) {
            this.value = value;
            textField.setText( value );
            firePADValueChanged( propertyName, value, oldvalue);
        }
        else {
            textField.setText( oldvalue );
        }
    }

    public void setValue(Object o) {
        if ( o != null && o.getClass() == String.class) {
            setValue( (String) o);
        }
    }
                
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textField.setEnabled(enabled);
        
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
private void initComponents () {//GEN-BEGIN:initComponents
    descriptionLabel = new javax.swing.JLabel ();
    textField = new javax.swing.JTextField ();
    browseButton = new javax.swing.JButton ();
    setLayout (new java.awt.GridBagLayout ());
    java.awt.GridBagConstraints gridBagConstraints1;

    descriptionLabel.setText ("Description");


    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints1.insets = new java.awt.Insets (0, 5, 0, 0);
    add (descriptionLabel, gridBagConstraints1);

    textField.setPreferredSize (new java.awt.Dimension(40, 20));
    textField.setText ("1");
    textField.addActionListener (new java.awt.event.ActionListener () {
        public void actionPerformed (java.awt.event.ActionEvent evt) {
            dieTextActionPerformed (evt);
        }
    }
    );
    textField.addFocusListener (new java.awt.event.FocusAdapter () {
        public void focusGained (java.awt.event.FocusEvent evt) {
            dieTextFocusGained (evt);
        }
        public void focusLost (java.awt.event.FocusEvent evt) {
            dieTextFocusLost (evt);
        }
    }
    );


    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints1.insets = new java.awt.Insets (0, 5, 0, 5);
    gridBagConstraints1.weightx = 1.0;
    add (textField, gridBagConstraints1);

    browseButton.setText ("Browse...");
    browseButton.addActionListener (new java.awt.event.ActionListener () {
        public void actionPerformed (java.awt.event.ActionEvent evt) {
            browseButtonActionPerformed (evt);
        }
    }
    );


    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.gridwidth = 0;
    gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
    add (browseButton, gridBagConstraints1);

}//GEN-END:initComponents

  private void browseButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
// Add your handling code here:
    MyFileChooser chooser = MyFileChooser.chooser;
    
                chooser.setDialogTitle( "Select Location for " + description);
                chooser.setFileSelectionMode( file ? JFileChooser.FILES_ONLY : JFileChooser.DIRECTORIES_ONLY );
                chooser.setSelectedFile( new File( value ) );
                
         //   chooser.setSelectedFile( new File(chooser.getCurrentDirectory(), this.getName() + "." + this.getFileExtension() ));
            int returnVal = chooser.showSaveDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = chooser.getSelectedFile();
                    setValue( file.toString() );
                }
                catch ( Exception exc ) {
                    return;
                }
            }
            else {
                return;
            }
  }//GEN-LAST:event_browseButtonActionPerformed

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


// Variables declaration - do not modify//GEN-BEGIN:variables
private javax.swing.JLabel descriptionLabel;
private javax.swing.JTextField textField;
private javax.swing.JButton browseButton;
// End of variables declaration//GEN-END:variables


/** Getter for property file.
 * @return Value of property file.
 */
public boolean isFile() {
    return file;
}
/** Setter for property file.
 * @param file New value of property file.
 */
public void setFile(boolean file) {
    this.file = file;
}
}