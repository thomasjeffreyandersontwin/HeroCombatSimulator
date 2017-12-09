/*
 * AttackDescriptionPanel.java
 *
 * Created on December 16, 2001, 11:33 AM
 */

package champions.attackTree;

import champions.BattleEvent;
import champions.CombatLevelList;
import champions.Target;
import javax.swing.JPanel;


/**
 *
 * @author  twalker
 */
public class AttackCombatLevelsPanel extends JPanel implements AttackTreeInputPanel {



    /** Holds value of property battleEvent. */
    private BattleEvent battleEvent;
    
    static private AttackCombatLevelsPanel defaultPanel;

    /** Holds value of property source. */
    private Target target;

    private boolean attacker;

    private CombatLevelList combatLevelList;
    
    /** Creates new form AttackDescriptionPanel */
    public AttackCombatLevelsPanel() {
        initComponents();
    }
    
    static public AttackCombatLevelsPanel getDefaultPanel(BattleEvent be,  boolean attacker, CombatLevelList combatLevelList) {
        if ( defaultPanel == null ) defaultPanel = new AttackCombatLevelsPanel();
        
        defaultPanel.setBattleEvent(be);
        
        defaultPanel.setAttacker(attacker);
        defaultPanel.setCombatLevelList(combatLevelList);
        
        return defaultPanel;
    }
    
    private void updatePanel() {

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        aTCombatLevelsTree1 = new champions.abilityTree2.ATCombatLevelsTree();

        jScrollPane1.setViewportView(aTCombatLevelsTree1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    

    public void showPanel(AttackTreePanel atip) {
        updatePanel();
    }    
    
    public JPanel getPanel() {
        return this;
    }
    
    public void hidePanel() {
    }
    
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
    
//    /** Getter for property source.
//     * @return Value of property source.
//     */
//    public Target getSource() {
//        return getTarget();
//    }
//
//    /** Setter for property source.
//     * @param source New value of property source.
//     */
//    public void setSource(Target source) {
//        this.setTarget(source);
//    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private champions.abilityTree2.ATCombatLevelsTree aTCombatLevelsTree1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the attacker
     */
    public boolean isAttacker() {
        return attacker;
    }

    /**
     * @param attacker the attacker to set
     */
    public void setAttacker(boolean attacker) {
        this.attacker = attacker;
    }

    /**
     * @return the target
     */
    public Target getTarget() {
        return target;
    }
//
//    /**
//     * @param target the target to set
//     */
//    public void setTarget(Target target) {
//        this.target = target;
//    }

    /**
     * @return the combatLevelList
     */
    public CombatLevelList getCombatLevelList() {
        return combatLevelList;
    }

//    /**
//     * @param combatLevelList the combatLevelList to set
//     */
//    public void setCombatLevelList(CombatLevelList combatLevelList) {
//        this.combatLevelList = combatLevelList;
//        aTCombatLevelsTree1.setCombatLevelsList(combatLevelList);
//    }

    public void setCombatLevelList(CombatLevelList combatLevelList) {

        this.combatLevelList = combatLevelList;
        
        aTCombatLevelsTree1.setup(combatLevelList);
    }


}
