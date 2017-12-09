/*
 * DiceRollInfoPanelTest.java
 *
 * Created on November 10, 2006, 10:53 AM
 */

package champions;

import champions.exception.BadDiceException;

/**
 *
 * @author  1425
 */
public class DiceRollInfoPanelTest extends javax.swing.JFrame {
    
    DiceRollInfo info;
    /** Creates new form DiceRollInfoPanelTest */
    public DiceRollInfoPanelTest(DiceRollInfo info) {
        initComponents();
        
        this.info = info;
        diceRollInfoPanel.setDiceRollInfo(info);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        diceRollInfoPanel = new champions.DiceRollInfoPanel();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(diceRollInfoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(diceRollInfoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden

        System.out.println(info);
        System.exit(0);
    }//GEN-LAST:event_formComponentHidden

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed

        
        System.out.println(info);
        System.exit(0);
        
    }//GEN-LAST:event_formWindowClosed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws BadDiceException {
        
        final DiceRollInfo info = new DiceRollInfo("Breakfall skill roll", "3d6");
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DiceRollInfoPanelTest(info).setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private champions.DiceRollInfoPanel diceRollInfoPanel;
    // End of variables declaration//GEN-END:variables
    
}
