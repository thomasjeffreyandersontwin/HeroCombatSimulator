/*
 * LifeSupportCreateImmunityDialog.java
 *
 * Created on November 11, 2006, 11:06 AM
 */

package champions.powers;

import champions.powers.powerLifeSupport.Immunity;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import javax.swing.JFrame;

/**
 *
 * @author  1425
 */
public class LifeSupportCreateImmunityDialog extends javax.swing.JDialog {
    
    private powerLifeSupport.Immunity immunity = null;
    /**
     * Creates new form LifeSupportCreateImmunityDialog
     */
    public LifeSupportCreateImmunityDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    private boolean  validateData() {
        if ( typeField.getText().equals("") || typeField.getText().equals("<Immunity>") ) {
            typeField.setText("<Immunity>");
            typeField.requestFocus();
            return false;
        } else {
            if ( costField.getText().equals("") ) {
                costField.setText("3");
                costField.requestFocus();
                return false;
            }
            try {
                String s = costField.getText();
                Integer.parseInt(s);
            } catch (NumberFormatException nfe) {
                costField.requestFocus();
                return false;
            }
        }
        
        return true;
    }
    
    private Immunity createImmunity() {
        String type = typeField.getText();
        int cost = 3;
        try {
            String s = costField.getText();
            cost = Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            
        }
        
        return new Immunity(type,cost);
    }
    
    public static Immunity showDialog(Frame frame) {
        LifeSupportCreateImmunityDialog dialog = new LifeSupportCreateImmunityDialog(frame, true);
        if ( frame == null ) dialog.centerDialogOnScreen();
        dialog.setVisible(true);
        
        return dialog.immunity;
    }
    
    public void centerDialogOnScreen() {
        Dimension d;
        Point loc;
        
        d = Toolkit.getDefaultToolkit().getScreenSize();
        loc = new Point(0,0);
        
        Dimension m = getSize();
        
        d.width -= m.width;
        d.height -= m.height;
        d.width /= 2;
        d.height /= 2;
        int x = (int)loc.getX() + d.width;
        if ( x < 0 ) x = 0;
        int y = (int)loc.getY() + d.height;
        if ( y < 0 ) y = 0;
        setLocation(x,y);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        typeField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        costField = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        okayButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Create Immunity");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLabel1.setText("Enter Immunity Information...");

        jLabel2.setText("Type");

        typeField.setText("<Immunity>");
        typeField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                typeFieldFocusGained(evt);
            }
        });

        jLabel3.setText("Cost");

        costField.setText("3");
        costField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                costFieldFocusGained(evt);
            }
        });

        okayButton.setText("Okay");
        okayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okayButtonActionPerformed(evt);
            }
        });
        jPanel1.add(okayButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        jPanel1.add(cancelButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(typeField, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(costField, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(typeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(costField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void okayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okayButtonActionPerformed

        if ( validateData() ) {
            immunity = createImmunity();
            dispose();
        }
    }//GEN-LAST:event_okayButtonActionPerformed
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed

        immunity = null;
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    private void costFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_costFieldFocusGained

        costField.selectAll();
        
    }//GEN-LAST:event_costFieldFocusGained
    
    private void typeFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_typeFieldFocusGained

        typeField.selectAll();
    }//GEN-LAST:event_typeFieldFocusGained
    
    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown

        typeField.requestFocus();
    }//GEN-LAST:event_formComponentShown
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Immunity immunity = showDialog(new JFrame());
                
                System.out.println("Immunity: " + immunity);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField costField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton okayButton;
    private javax.swing.JTextField typeField;
    // End of variables declaration//GEN-END:variables
    
}
