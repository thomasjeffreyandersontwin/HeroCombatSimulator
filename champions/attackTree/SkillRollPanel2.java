/*
 * ToHitPanel.java
 *
 * Created on November 11, 2001, 9:09 PM
 */

package champions.attackTree;

import champions.*;
import champions.event.PADValueEvent;
import champions.genericModifiers.GenericModifierList;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.PADValueListener;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 *
 * @author  twalker
 */
public class SkillRollPanel2 extends JPanel implements AttackTreeInputPanel, ChampionsConstants {
    
    /** Stores the static, default toHit panel. */
    static private SkillRollPanel2 defaultPanel = null;
    
    private AttackTreePanel atip = null;
    
    /** Holds value of property battleEvent. */
    private BattleEvent battleEvent;
    
    private SkillRollInfo skillRollInfo;
    
    /** Creates new form ToHitPanel */
    public SkillRollPanel2() {
        initComponents();

        
    }
    
    static public SkillRollPanel2 getDefaultPanel(BattleEvent be, SkillRollInfo sri) {
        if ( defaultPanel == null ) defaultPanel = new SkillRollPanel2();
        
        defaultPanel.setBattleEvent(be);
        defaultPanel.setSkillRollInfo(sri);
        return defaultPanel;
        
    }
    
    public void setupPanel() {
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        genericModifierListPanel = new champions.genericModifiers.GenericModifierListPanel();
        jPanel2 = new javax.swing.JPanel();
        diceRollInfoPanel = new champions.DiceRollInfoPanel();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Modifiers"));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(genericModifierListPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(genericModifierListPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Dice Roll"));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(diceRollInfoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(211, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(diceRollInfoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
            
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
        setSkillRollInfo(null);
    }


    public SkillRollInfo getSkillRollInfo() {
        return skillRollInfo;
    }

    public void setSkillRollInfo(SkillRollInfo skillRollInfo) {
        if (this.skillRollInfo != skillRollInfo ) {
            this.skillRollInfo = skillRollInfo;
            
            if ( skillRollInfo != null ) {
                
                GenericModifierList gml = skillRollInfo.getModifierList();
                
                if ( gml == null ) {
                    gml = skillRollInfo.createModifierList();
                }
                
                genericModifierListPanel.setModifierList(gml);
                
                diceRollInfoPanel.setDiceRollInfo( skillRollInfo.getDiceRollInfo() );
            }
            else {
                genericModifierListPanel.setModifierList(null);
                diceRollInfoPanel.setDiceRollInfo(null);
            }
        }
    }
 
    
    /**
     * Removes a <code>Battle</code> listener.
     *
     * @param l  the <code>BattleListener</code> to remove
     * //==
     * public void removeChangeListener(ChangeListener l) {
     * listenerList.remove(ChangeListener.class,l);
     * }
     *
     */
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private champions.DiceRollInfoPanel diceRollInfoPanel;
    private champions.genericModifiers.GenericModifierListPanel genericModifierListPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
    

    
}
