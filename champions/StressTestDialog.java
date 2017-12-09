/*
 * StressTestDialog.java
 *
 * Created on October 26, 2007, 11:35 AM
 */

package champions;

import champions.StressTest;

/**
 *
 * @author  twalker
 */
public class StressTestDialog extends javax.swing.JFrame {
    
    private static StressTestDialog stressTestDialog;
    
    private StressTest stressTest;
    
    /** Creates new form StressTestDialog */
    private StressTestDialog() {
        initComponents();
        
        stressTest = new StressTest();
        
        update();
    }
    
    public static StressTestDialog getStressTestDialog() {
        if ( stressTestDialog == null ) {
            stressTestDialog = new StressTestDialog();
            ChampionsUtilities.centerWindow(stressTestDialog);
        }
        
        return stressTestDialog;
    }
    
    public void update() {
        
        
        if ( stressTest.isRunning() ) {
            if ( stressTest.isTestingSave() ) {
                startButton.setEnabled(false);
                stateLabel.setText("Saving");
            }
            else {
                startButton.setText("Stop");
                startButton.setEnabled(true);
                stateLabel.setText("Running");
            }
        }
        else {
            startButton.setText("Start");
            startButton.setEnabled(true);
            stateLabel.setText("Stopped");
        }
        
        int count = stressTest.getActionCount();
        double actionsPerSecond = stressTest.getActionsPerSecond();
        String countString = String.format("%d (%.1f a/s)", count, actionsPerSecond);
        countLabel.setText( countString );
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        startButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        checkSavesBox = new javax.swing.JCheckBox();
        stateLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        countLabel = new javax.swing.JLabel();

        setTitle("Stress Test");
        setName("Stress Test Dialog"); // NOI18N
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

        startButton.setText("Start");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("State:");

        checkSavesBox.setSelected(true);
        checkSavesBox.setText("Check Saves");
        checkSavesBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkSavesBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        stateLabel.setFont(stateLabel.getFont().deriveFont(stateLabel.getFont().getStyle() & ~java.awt.Font.BOLD));
        stateLabel.setText("state");

        jLabel3.setText("Action Count:");

        countLabel.setFont(countLabel.getFont().deriveFont(countLabel.getFont().getStyle() & ~java.awt.Font.BOLD));
        countLabel.setText("count");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkSavesBox)
                    .addComponent(startButton, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(stateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                            .addComponent(countLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(stateLabel))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(countLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkSavesBox)
                .addGap(12, 12, 12)
                .addComponent(startButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden

        if ( stressTest != null ) stressTest.stopTest();
    }//GEN-LAST:event_formComponentHidden

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed

        if ( stressTest != null ) stressTest.stopTest();
    }//GEN-LAST:event_formWindowClosed

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed

        if ( stressTest.isRunning()) {
            stressTest.stopTest();
            update();
        }
        else {
            stressTest.startTest(checkSavesBox.isSelected());
            update();
        }
    }//GEN-LAST:event_startButtonActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StressTestDialog().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkSavesBox;
    private javax.swing.JLabel countLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JButton startButton;
    private javax.swing.JLabel stateLabel;
    // End of variables declaration//GEN-END:variables
    
}
