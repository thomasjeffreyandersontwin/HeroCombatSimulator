/*
 * InformationPanel.java
 *
 * Created on December 17, 2001, 11:56 AM
 */

package champions.attackTree;

import champions.Battle;
import champions.BattleEvent;
import champions.GlobalFontSettings;
import champions.Roster;
import champions.Target;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author  twalker
 */
public class TargetRenamePanel extends JPanel implements AttackTreeInputPanel{
    
    static private TargetRenamePanel defaultPanel;
    
    /** Holds value of property text. */
    private Target target;
    
    private BattleEvent battleEvent;
    
    /** Creates new form InformationPanel */
    public TargetRenamePanel() {
        initComponents();
        
        JTextField jtf = (JTextField) rosterComboBox.getEditor().getEditorComponent();
        jtf.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateComboInfo();
            }
            public void removeUpdate(DocumentEvent e) {
                updateComboInfo();
            }
            public void changedUpdate(DocumentEvent e) {
                updateComboInfo();
            }
        } );
        
        
        
    }
    
    static public TargetRenamePanel getDefaultPanel(BattleEvent battleEvent, Target target) {
        if ( defaultPanel == null ) defaultPanel = new TargetRenamePanel();
        
        defaultPanel.setBattleEvent(battleEvent);
        defaultPanel.setTarget(target);
        
        return defaultPanel;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        rosterComboBox = new javax.swing.JComboBox();
        editTargetButton = new javax.swing.JButton();
        rosterInfoField = new javax.swing.JLabel();

        jLabel1.setText("Name");

        nameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameFieldActionPerformed(evt);
            }
        });
        nameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                nameFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                nameFieldFocusLost(evt);
            }
        });

        jLabel2.setText("Roster");

        rosterComboBox.setEditable(true);
        rosterComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        rosterComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rosterComboBoxActionPerformed(evt);
            }
        });
        rosterComboBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                rosterComboBoxKeyTyped(evt);
            }
        });

        editTargetButton.setText("Edit Target...");
        editTargetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editTargetButtonActionPerformed(evt);
            }
        });

        rosterInfoField.setFont(new java.awt.Font(GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        rosterInfoField.setForeground(java.awt.Color.red);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(rosterInfoField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                    .addComponent(rosterComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 235, Short.MAX_VALUE)
                    .addComponent(nameField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editTargetButton)
                .addGap(14, 14, 14))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editTargetButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(rosterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rosterInfoField)
                .addContainerGap(232, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void rosterComboBoxKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rosterComboBoxKeyTyped

        updateComboInfo();
    }//GEN-LAST:event_rosterComboBoxKeyTyped
    
    private void editTargetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editTargetButtonActionPerformed

        if ( target != null ) target.editTarget();
    }//GEN-LAST:event_editTargetButtonActionPerformed
    
    private void rosterComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rosterComboBoxActionPerformed

        updateComboInfo();
    }//GEN-LAST:event_rosterComboBoxActionPerformed
    
    private void nameFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameFieldFocusLost

        updateTargetName();
    }//GEN-LAST:event_nameFieldFocusLost
    
    private void nameFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameFieldFocusGained

        nameField.selectAll();
    }//GEN-LAST:event_nameFieldFocusGained
    
    private void nameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameFieldActionPerformed

        updateTargetName();
    }//GEN-LAST:event_nameFieldActionPerformed
    
    public Target getAutoBypassTarget() {
        return null;
    }
    
    public void showPanel(AttackTreePanel atip) {
        nameField.requestFocus();
    }
    
    public String getAutoBypassOption() {
        return null;
    }
    
    public JPanel getPanel() {
        return this;
    }
    
    public void hidePanel() {
        if ( target != null ) {
            Roster r = target.getRoster();
            
            String newRosterName = getRoster();
            
            if ( newRosterName.equals("None") || newRosterName.equals("")) {
                newRosterName = null;
            }
            
            if ( newRosterName == null && r != null ) {
                // Remove it from it's current roster and let it float...
                r.remove(target);
            } else if ( newRosterName != null ) {
                if ( r == null || r.getName() != newRosterName ) {
                    Roster newRoster = Battle.getCurrentBattle().findRoster(newRosterName);
                    if ( newRoster == null ) {
                        // There wasn't a roster by this name, so create one
                        newRoster = new Roster(newRosterName);
                        Battle.getCurrentBattle().addRoster(newRoster);
                    }
                    if ( r != null ) {
                        r.remove(target);
                    }
                    newRoster.add(target);
                }
            }
        }
        
    }
    
    public Target getTarget() {
        return target;
    }
    
    public void setTarget(Target target) {
        this.target = target;
        
        updatePanel();
    }
    
    private void updatePanel() {
        if ( target == null ) {
            nameField.setEnabled(false);
            rosterComboBox.setEnabled(false);
            editTargetButton.setEnabled(false);
            rosterComboBox.setEnabled(false);
        } else {
            nameField.setEnabled(true);
            rosterComboBox.setEnabled(true);
            editTargetButton.setEnabled(true);
            rosterComboBox.setEnabled(true);
            
            nameField.setText( target.getName() );
            
            updateRosters();
            
            if ( target.getRoster() != null) {
                rosterComboBox.setSelectedItem( target.getRoster().getName());
            } else {
                rosterComboBox.setSelectedItem("None");
            }
            updateComboInfo();
        }
    }
    
    private void updateRosters() {
        Set rosters = Battle.getCurrentBattle().getRosters();
        rosterComboBox.removeAllItems();
        Iterator i = rosters.iterator();
        
        rosterComboBox.addItem("None");
        
        while(i.hasNext()) {
            Roster r = (Roster)i.next();
            rosterComboBox.addItem(r.getName());
        }
    }
    
    private void updateTargetName() {
        if ( target != null ) {
            if ( nameField.getText().equals("") ) {
                nameField.setText( target.getName() ) ;
            } else {
                target.setName( nameField.getText());
            }
        }
    }
    
    private void updateComboInfo() {
        JTextField jtf = (JTextField) rosterComboBox.getEditor().getEditorComponent();
        String item = jtf.getText();
        
        if ( item == null || item.equals("None") || item.equals("") ) {
            rosterInfoField.setText("(Target will not be added to a roster)");
        } else {
            Roster r = Battle.getCurrentBattle().findRoster(item);
            if ( r == null ) {
                rosterInfoField.setText("(Creates a new roster)");
            } else {
                rosterInfoField.setText("");
            }
        }
        
    }
    
    public String getRoster() {
        return (String) rosterComboBox.getSelectedItem();
    }
    
    public BattleEvent getBattleEvent() {
        return battleEvent;
    }
    
    public void setBattleEvent(BattleEvent battleEvent) {
        this.battleEvent = battleEvent;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton editTargetButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField nameField;
    private javax.swing.JComboBox rosterComboBox;
    private javax.swing.JLabel rosterInfoField;
    // End of variables declaration//GEN-END:variables
    
    
    
}
