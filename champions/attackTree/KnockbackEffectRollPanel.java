/*
 * KnockbackEffectRollPanel.java
 *
 * Created on November 24, 2001, 5:06 PM
 */

package champions.attackTree;

import champions.BattleEvent;
import champions.Dice;
import champions.KnockbackModifiersList;
import champions.Target;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;


/**
 *
 * @author  twalker
 */
public class KnockbackEffectRollPanel extends JPanel implements AttackTreeInputPanel, PropertyChangeListener {
    
    private static KnockbackEffectRollPanel defaultPanel = null;
    /**
     * Creates new form KnockbackEffectRollPanel
     */
    public KnockbackEffectRollPanel() {
        initComponents();
    }
    
    static public KnockbackEffectRollPanel getDefaultPanel(BattleEvent be, Target target, String knockbackGroup) {
        if ( defaultPanel == null ) defaultPanel = new KnockbackEffectRollPanel();
        
        defaultPanel.setBattleEvent(be);
        defaultPanel.setTarget(target);
        defaultPanel.setKnockbackGroup(knockbackGroup);
        
        return defaultPanel;
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        knockbackPanel = new champions.KnockbackPanel();
        knockbackRoll = new champions.PADDiceValueEditor();

        setLayout(new java.awt.BorderLayout());

        knockbackPanel.setBorder(new javax.swing.border.TitledBorder("Knockback Roll Modifiers"));
        add(knockbackPanel, java.awt.BorderLayout.CENTER);

        knockbackRoll.setBorder(new javax.swing.border.TitledBorder("Knockback Roll"));
        add(knockbackRoll, java.awt.BorderLayout.SOUTH);

    }//GEN-END:initComponents
    
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
    
    /** Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        return target;
    }
    
    /** Setter for property target.
     * @param target New value of property target.
     */
    public void setTarget(Target target) {
        this.target = target;
    }
    
    /** Getter for property knockbackGroup.
     * @return Value of property knockbackGroup.
     */
    public String getKnockbackGroup() {
        return knockbackGroup;
    }
    
    /** Setter for property knockbackGroup.
     * @param knockbackGroup New value of property knockbackGroup.
     */
    public void setKnockbackGroup(String knockbackGroup) {
        this.knockbackGroup = knockbackGroup;
    }
    
    public void showPanel(AttackTreePanel atip) {
        int kbindex = getBattleEvent().getKnockbackIndex(getTarget(), getKnockbackGroup());
        KnockbackModifiersList kml = getBattleEvent().getKnockbackModifiersList(kbindex);
        
        knockbackPanel.setKnockbackModifiersList(kml);
        
        Dice dice = getBattleEvent().getKnockbackAmountRoll(kbindex);
        boolean auto = getBattleEvent().getKnockbackAutoRollAmount(kbindex);
        
        knockbackRoll.setDescription("Knockback Roll for " + getTarget().getName());
        
        knockbackRoll.setAutoroll( auto );
        knockbackRoll.setDice(dice);
        
        updateDiceSize();
        
        kml.addPropertyChangeListener(this);
    }
    
    public JPanel getPanel() {
        return this;
    }
    
    public void hidePanel() {
        
        
        Dice d = knockbackRoll.getDice();
        boolean auto = knockbackRoll.isAutoroll();
        
        int kbindex = getBattleEvent().getKnockbackIndex(getTarget(), getKnockbackGroup());
        getBattleEvent().setKnockbackAmountRoll(kbindex,d);
        getBattleEvent().setKnockbackAutoRollAmount(kbindex, auto);
        
        KnockbackModifiersList kml = getBattleEvent().getKnockbackModifiersList(kbindex);
        kml.removePropertyChangeListener(this);
    }
    
    protected void updateDiceSize() {
        int kbindex = getBattleEvent().getKnockbackIndex(getTarget(), getKnockbackGroup());
        KnockbackModifiersList kml = getBattleEvent().getKnockbackModifiersList(kbindex);
        int size = kml.getKnockbackRoll();
        
        if ( knockbackRoll.getDiceSize() == null || knockbackRoll.getDiceSize().equals(Integer.toString(size) + "d6") == false ) {
            if ( size < 0 ) size = 0;
            
            knockbackRoll.setDiceSize(size + "d6");
            
            knockbackRoll.setAutoroll(true);
            Dice d = new Dice(size);
            knockbackRoll.setDice(d);
            
        }
    }
    
    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        updateDiceSize();
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private champions.PADDiceValueEditor knockbackRoll;
    private champions.KnockbackPanel knockbackPanel;
    // End of variables declaration//GEN-END:variables
    
    /** Holds value of property battleEvent. */
    private BattleEvent battleEvent;
    
    /** Holds value of property target. */
    private Target target;
    
    /** Holds value of property knockbackGroup. */
    private String knockbackGroup;
    
}
