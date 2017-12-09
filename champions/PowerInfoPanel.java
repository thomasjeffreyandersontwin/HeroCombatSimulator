/*
 * PowerInfoPanel.java
 *
 * Created on August 15, 2001, 2:52 PM
 */

package champions;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

import champions.interfaces.*;
import champions.exceptionWizard.*;

/**
 *
 * @author  twalker
 * @version
 */
public class PowerInfoPanel extends javax.swing.JPanel {
    
    /** Creates new form PowerInfoPanel */
    public PowerInfoPanel() {
        initComponents ();
    }
    
    public void updateFields() {
        if ( power == null ) {
            nameLabel.setText("No Power Set");
            descriptionEditorPane.setContentType("text/plain");
            descriptionEditorPane.setText("");
            caveatEditorPane.setContentType("text/plain");
            caveatEditorPane.setText("");
            dynamicLabel.setText("N/A");
        }
        else {
            nameLabel.setText(power.getName());
            
            String description = power.getDescription();
            if ( description != null && description.startsWith("<") ){

                descriptionEditorPane.setContentType("text/html");
                descriptionEditorPane.setText(description);
            }
            else {
                descriptionEditorPane.setContentType("text/plain");
                descriptionEditorPane.setText(description);
            }
            
            String caveats = power.getCaveats();
            if ( caveats != null && caveats.startsWith("<") ){
                caveatEditorPane.setContentType("text/html");
                caveatEditorPane.setText(caveats);
            }
            else {
                caveatEditorPane.setContentType("text/plain");
                caveatEditorPane.setText(caveats);
            }
            
            dynamicLabel.setText(power.isDynamic() ? "Yes" : "No");
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        label1 = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        label2 = new javax.swing.JLabel();
        descriptionEditorPane = new javax.swing.JEditorPane();
        label3 = new javax.swing.JLabel();
        caveatEditorPane = new javax.swing.JEditorPane();
        dynamicGroup = new javax.swing.JPanel();
        label4 = new javax.swing.JLabel();
        dynamicLabel = new javax.swing.JLabel();
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        label1.setText("Name");
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        add(label1, gridBagConstraints1);
        
        
        nameLabel.setText("No Power Set");
        nameLabel.setForeground(java.awt.Color.black);
        nameLabel.setFont(new java.awt.Font ("SansSerif", 0, 12));
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(0, 5, 0, 0);
        gridBagConstraints1.weightx = 1.0;
        add(nameLabel, gridBagConstraints1);
        
        
        label2.setText("Description");
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(label2, gridBagConstraints1);
        
        
        descriptionEditorPane.setEditable(false);
        descriptionEditorPane.setFont(new java.awt.Font ("SansSerif", 0, 12));
        descriptionEditorPane.setBackground((java.awt.Color) javax.swing.UIManager.getDefaults ().get ("Label.background"));
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new java.awt.Insets(0, 5, 0, 0);
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add(descriptionEditorPane, gridBagConstraints1);
        
        
        label3.setText("Caveats");
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(label3, gridBagConstraints1);
        
        
        caveatEditorPane.setEditable(false);
        caveatEditorPane.setFont(new java.awt.Font ("SansSerif", 0, 12));
        caveatEditorPane.setBackground((java.awt.Color) javax.swing.UIManager.getDefaults ().get ("Label.background"));
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new java.awt.Insets(0, 5, 0, 0);
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add(caveatEditorPane, gridBagConstraints1);
        
        
        dynamicGroup.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints2;
        
        label4.setText("Auto Reconfigure when aided or drained");
          gridBagConstraints2 = new java.awt.GridBagConstraints();
          dynamicGroup.add(label4, gridBagConstraints2);
          
          
        dynamicLabel.setText("N/A");
          dynamicLabel.setForeground(java.awt.Color.black);
          dynamicLabel.setFont(new java.awt.Font ("SansSerif", 0, 12));
          gridBagConstraints2 = new java.awt.GridBagConstraints();
          gridBagConstraints2.gridwidth = 0;
          gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
          gridBagConstraints2.insets = new java.awt.Insets(0, 5, 0, 0);
          gridBagConstraints2.weightx = 1.0;
          dynamicGroup.add(dynamicLabel, gridBagConstraints2);
          
          
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(dynamicGroup, gridBagConstraints1);
        
    }//GEN-END:initComponents
    
    /** Getter for property power.
     * @return Value of property power.
     */
    public Power getPower() {
        return power;
    }
    
    /** Setter for property power.
     * @param power New value of property power.
     */
    public void setPower(Power power) {
        this.power = power;
        updateFields();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel label1;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel label2;
    private javax.swing.JEditorPane descriptionEditorPane;
    private javax.swing.JLabel label3;
    private javax.swing.JEditorPane caveatEditorPane;
    private javax.swing.JPanel dynamicGroup;
    private javax.swing.JLabel label4;
    private javax.swing.JLabel dynamicLabel;
    // End of variables declaration//GEN-END:variables
    
    /** Holds value of property power. */
    private Power power;
    
}
