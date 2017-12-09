/*
 * ToHitPanel.java
 *
 * Created on November 11, 2001, 9:09 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.ActivationInfo;
import champions.BattleEvent;
import champions.ChampionsUtilities;
import champions.Target;
import champions.interfaces.ChampionsConstants;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 */
public class NNDDefensePanel extends JPanel implements AttackTreeInputPanel, ChampionsConstants {
    /** Stores the static, default toHit panel. */
    static private NNDDefensePanel defaultPanel = null;
    
    private AttackTreePanel atip = null;

    /** Holds value of property battleEvent. */
    private BattleEvent battleEvent;
    
    private int targetIndex;
    
    /** Creates new form ToHitPanel */
    public NNDDefensePanel() {
        initComponents();
        
    }
    
    static public NNDDefensePanel getDefaultPanel(BattleEvent be, int targetIndex) {
        if ( defaultPanel == null ) defaultPanel = new NNDDefensePanel();
        
        defaultPanel.battleEvent = be;
        defaultPanel.targetIndex = targetIndex;
        
        defaultPanel.updatePanel();

        return defaultPanel;
    }
    
    public void setupPanel() {
        
    }
    
    protected void updatePanel() {
        if ( battleEvent != null  ) {
            // Lookup the Source Die
            ActivationInfo ai = battleEvent.getActivationInfo();
            Target target = ai.getTarget(targetIndex);
            Target source = battleEvent.getSource();
            Ability ability = battleEvent.getAbility();
            String nnddefense = battleEvent.getNNDDefense();
            
            String text = source.getName() + " is attacking " + target.getName() + 
                    " with a No Normal Defense ability, " + ability.getName() + ".  Does " +
                    target.getName() + " have the defense \"" + nnddefense + "\"?";
            
            textLabel.setText( ChampionsUtilities.createHTMLString(text) );
            
            if ( ai.isTargetHasNNDDefenseSet(targetIndex) ) {
                boolean hasDefense = ai.getTargetHasNNDDefense( targetIndex );
                if ( hasDefense ) {
                    yesButton.setSelected(true);
                    noButton.setSelected(false);
                }
                else {
                    noButton.setSelected(true);
                    yesButton.setSelected(false);
                }
            }
            else {
                yesButton.setSelected(false);
                noButton.setSelected(true);
            }
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup = new javax.swing.ButtonGroup();
        textLabel = new javax.swing.JLabel();
        yesButton = new javax.swing.JRadioButton();
        noButton = new javax.swing.JRadioButton();

        textLabel.setFont(UIManager.getFont("CombatSimulator.defaultFont"));
        textLabel.setText("<html>XXX is attacking YYY with a No Normal Defense ability AAA.  Does YYY have the defense DDD?</html>");

        buttonGroup.add(yesButton);
        yesButton.setSelected(true);
        yesButton.setText("Yes (Target will not take damage)");
        yesButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        yesButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        yesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yesButtonActionPerformed(evt);
            }
        });

        buttonGroup.add(noButton);
        noButton.setText("No (Target will take damage)");
        noButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        noButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        noButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(noButton)
                            .addComponent(yesButton))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(textLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(yesButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noButton)
                .addGap(239, 239, 239))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void noButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noButtonActionPerformed

        if ( isVisible() ) {
            atip.advanceNode();
        }
    }//GEN-LAST:event_noButtonActionPerformed

    private void yesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yesButtonActionPerformed

         if ( isVisible() ) {
            atip.advanceNode();
        }
    }//GEN-LAST:event_yesButtonActionPerformed
            
    /** Getter for property battleEvent.
     * @return Value of property battleEvent.
     */
    public BattleEvent getBattleEvent() {
        return battleEvent;
    }
    
    /** Setter for property battleEvent.
     * @param battleEvent New value of property battleEvent.
     */
    public void setBattleEvent(BattleEvent battleEvent) {
        this.battleEvent = battleEvent;
    }
    
    public void showPanel(AttackTreePanel atip) {
        this.atip = atip;
        setupPanel();
    }
    
    public JPanel getPanel() {
        return this;
    }
    
    public void hidePanel() {
        // When the panel is hidden, make sure you copy the values out
        if ( battleEvent != null ) {
            ActivationInfo ai = battleEvent.getActivationInfo();
            ai.setTargetHasNNDDefense(targetIndex, yesButton.isSelected());
        }
        
        setBattleEvent(null);
    }
        
    public int getTargetIndex() {
        return targetIndex;
    }

    public void setTargetIndex(int targetIndex) {
        this.targetIndex = targetIndex;
    }
  
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JRadioButton noButton;
    private javax.swing.JLabel textLabel;
    private javax.swing.JRadioButton yesButton;
    // End of variables declaration//GEN-END:variables

  

    
}
