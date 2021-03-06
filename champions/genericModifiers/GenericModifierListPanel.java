/*
 * GenericModifierListPanel.java
 *
 * Created on November 9, 2006, 8:05 PM
 */

package champions.genericModifiers;

import champions.GlobalFontSettings;
import champions.PADLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import tjava.Destroyable;

/**
 *
 * @author  1425
 */
public class GenericModifierListPanel extends javax.swing.JPanel 
implements PropertyChangeListener{
    
    private GenericModifierList modifierList;
    
    /**
     * Creates new form GenericModifierListPanel
     */
    public GenericModifierListPanel() {
        initComponents();
        subpanels.setLayout( new PADLayout() );
    }
    
    private void updatePanel() {
        if ( modifierList == null ) return;
        String name = modifierList.getName();
        
        if ( name.equals("") ) {
            descriptionLabel.setText("Modifiers");
            finalDescLabel.setText("Final Value" );
        }
        else {
            descriptionLabel.setText(name + " modifiers");
            finalDescLabel.setText("Final " + name);
        }
        
        updateValue();
    }
    
    private void updateValue() {
        if ( modifierList == null ) return;
        finalValueLabel.setText( "= " + Integer.toString( modifierList.getValue() ));
    }

    public GenericModifierList getModifierList() {
        return modifierList;
    }

    public void setModifierList(GenericModifierList modifierList) {
        if ( this.modifierList != modifierList ) {
            if ( this.modifierList != null ) {
                
                Component[] c = getComponents();
                
                subpanels.removeAll();
                
                for(int i = 0; i < c.length; i++) {
                    if ( c[i] instanceof GenericModifierPanel ) {
                        GenericModifierPanel p = (GenericModifierPanel)c[i];
                        p.removePropertyChangeListener(this);
                        p.destroy();
                    }
                }
            }
            
            this.modifierList = modifierList;
            
            if ( this.modifierList != null ) {
                updateSubpanels();
            }
        }
        
        updatePanel();
        
    }
    
    private void updateSubpanels() {
        subpanels.removeAll();
        
        if ( modifierList != null ) {
            for(GenericModifier gm : modifierList) {
                GenericModifierPanel gmp = gm.getEditingPanel();
                if ( gmp != null ) {
                    subpanels.add( gmp );
                    gmp.addPropertyChangeListener(this);
                }
            }
            subpanels.validate();
        }
    }
    
     public void propertyChange(PropertyChangeEvent evt) {
         updateValue();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();
        subpanels = new javax.swing.JPanel();
        descriptionLabel = new javax.swing.JLabel();
        finalDescLabel = new javax.swing.JLabel();
        finalValueLabel = new javax.swing.JLabel();

        subpanels.setLayout(new java.awt.BorderLayout());
        scrollPane.setViewportView(subpanels);

        descriptionLabel.setFont(new java.awt.Font(GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        descriptionLabel.setText("Modifiers");

        finalDescLabel.setFont(new java.awt.Font(GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        finalDescLabel.setText("jLabel1");

        finalValueLabel.setFont(new java.awt.Font(GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        finalValueLabel.setText("jLabel2");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(finalDescLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(finalValueLabel)
                                .addGap(20, 20, 20))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(descriptionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(descriptionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(finalValueLabel)
                    .addComponent(finalDescLabel)))
        );
    }// </editor-fold>//GEN-END:initComponents

   
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JLabel finalDescLabel;
    private javax.swing.JLabel finalValueLabel;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JPanel subpanels;
    // End of variables declaration//GEN-END:variables
    
}
