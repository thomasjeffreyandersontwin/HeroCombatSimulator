/*
 * KnockbackEffectPanel.java
 *
 * Created on November 27, 2001, 12:22 AM
 */

package champions.attackTree;

import champions.*;
import champions.enums.KnockbackEffect;
import champions.event.PADValueEvent;
import champions.interfaces.AbilityIterator;
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
public class KnockbackEffectPanel extends JPanel implements AttackTreeInputPanel, ChampionsConstants {
    
    static private KnockbackEffectPanel defaultPanel = null;

	public static KnockbackEffectPanel panel;
    
    private AttackTreePanel atip = null;
    
    private String rollMode;
    
    
    /** Holds value of property battleEvent. */
    private BattleEvent battleEvent;
    
    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    /** Holds value of property target. */
    private Target target;
    
    /** Holds value of property targetGroup. */
    private String targetGroup;
    
    /** Holds value of property knockbackGroup. */
    private String knockbackGroup;

    /** Holds value of property effect. */
    private KnockbackEffect effect;
    
    private int distance;

    private boolean knockdownPossible;
    
    /** Creates new form KnockbackEffectPanel */
    public KnockbackEffectPanel() {
        initComponents();
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(collisionButton);
        bg.add(noCollisionButton);
        bg.add(noEffectButton);
        bg.add(possibleCollisionButton);
        bg.add(onlyKnockdownButton);
        panel = this;
        
        //knockbackDamagePanel.setDescription("Knockback Damage");
    }
    
    static public KnockbackEffectPanel getDefaultPanel(BattleEvent be, Target target, String knockbackGroup, String targetGroup) {
        if ( defaultPanel == null ) defaultPanel = new KnockbackEffectPanel();
        
        defaultPanel.setBattleEvent(be);
        defaultPanel.setTarget(target);
        defaultPanel.setKnockbackGroup(knockbackGroup);
        defaultPanel.setTargetGroup(targetGroup);
        
        return defaultPanel;
    }
    
    public void setupPanel() {

    }
    
    private void setupDescription() {
        
        if ( distance > 0 ) {
            descriptionLabel.setText( getTarget().getName() + " was knocked back " );
            distanceLabel.setText( Integer.toString(distance));
            descriptionLabel2.setText( "\".");
        }
        else if ( knockdownPossible ) {
            descriptionLabel.setText( getTarget().getName() + " was not knocked back, but was possibly knocked down." );
            distanceLabel.setText("");
            descriptionLabel2.setText( "");
        }
        else {
            descriptionLabel.setText( getTarget().getName() + " was not knocked back." );
            distanceLabel.setText("");
            descriptionLabel2.setText( "");
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        descriptionLabel = new javax.swing.JLabel();
        distanceLabel = new javax.swing.JLabel();
        descriptionLabel2 = new javax.swing.JLabel();
        effectGroup = new javax.swing.JPanel();
        noEffectButton = new javax.swing.JRadioButton();
        onlyKnockdownButton = new javax.swing.JRadioButton();
        noCollisionButton = new javax.swing.JRadioButton();
        collisionButton = new javax.swing.JRadioButton();
        possibleCollisionButton = new javax.swing.JRadioButton();

        descriptionLabel.setText("Character <XXX> was knocked back ");

        distanceLabel.setForeground(java.awt.Color.red);
        distanceLabel.setText("<7>");

        descriptionLabel2.setText("\".");

        effectGroup.setBorder(javax.swing.BorderFactory.createTitledBorder("Knockback Effect Selector"));

        noEffectButton.setText("No Knockback Effect (No damage or knockdown applied)");
        noEffectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noEffectButtonActionPerformed(evt);
            }
        });

        onlyKnockdownButton.setText("Knockdown Only (No damage, breakfall possible)");
        onlyKnockdownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onlyKnockdownButtonActionPerformed(evt);
            }
        });

        noCollisionButton.setText("Knockback withOUT collision (Half damage, No secondary targets)");
        noCollisionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noCollisionButtonActionPerformed(evt);
            }
        });

        collisionButton.setText("Knockback WITH collision (Full damage regardless of secondary targets)");
        collisionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                collisionButtonActionPerformed(evt);
            }
        });

        possibleCollisionButton.setText("Possible Collision Occurred (Damage depends on secondary targets) ");
        possibleCollisionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                possibleCollisionButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout effectGroupLayout = new javax.swing.GroupLayout(effectGroup);
        effectGroup.setLayout(effectGroupLayout);
        effectGroupLayout.setHorizontalGroup(
            effectGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(effectGroupLayout.createSequentialGroup()
                .addGroup(effectGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(noEffectButton)
                    .addComponent(onlyKnockdownButton)
                    .addComponent(possibleCollisionButton)
                    .addComponent(collisionButton)
                    .addComponent(noCollisionButton))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        effectGroupLayout.setVerticalGroup(
            effectGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(effectGroupLayout.createSequentialGroup()
                .addComponent(noEffectButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(onlyKnockdownButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(possibleCollisionButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(collisionButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noCollisionButton)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(descriptionLabel)
                        .addGap(0, 0, 0)
                        .addComponent(distanceLabel)
                        .addGap(0, 0, 0)
                        .addComponent(descriptionLabel2))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(effectGroup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(distanceLabel)
                    .addComponent(descriptionLabel2)
                    .addComponent(descriptionLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(effectGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(95, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void possibleCollisionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_possibleCollisionButtonActionPerformed

        if ( possibleCollisionButton.isSelected() ) {
            setEffect( KnockbackEffect.POSSIBLECOLLISION);
            
        }
    }//GEN-LAST:event_possibleCollisionButtonActionPerformed
                
    private void collisionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_collisionButtonActionPerformed
        // Add your handling code here:
        if ( collisionButton.isSelected() ) {
            setEffect( KnockbackEffect.COLLISION );
            
        }
    }//GEN-LAST:event_collisionButtonActionPerformed
    
    private void noCollisionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noCollisionButtonActionPerformed
        // Add your handling code here:
        if ( noCollisionButton.isSelected() ) {
            setEffect( KnockbackEffect.NOCOLLISION );
            
        }
    }//GEN-LAST:event_noCollisionButtonActionPerformed
    
    public void noEffectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noEffectButtonActionPerformed
        // Add your handling code here:
        if ( noEffectButton.isSelected() ) {
            setEffect( KnockbackEffect.NOEFFECT );
        }
    }//GEN-LAST:event_noEffectButtonActionPerformed

    private void onlyKnockdownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onlyKnockdownButtonActionPerformed
        
        if ( onlyKnockdownButton.isSelected() ) {
            setEffect( KnockbackEffect.KNOCKDOWNONLY );
        }
}//GEN-LAST:event_onlyKnockdownButtonActionPerformed
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
        
        // Grab the target's current effect
        BattleEvent be = getBattleEvent();
        ActivationInfo ai = battleEvent.getActivationInfo();
        int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup());
        
        int kbindex = be.getKnockbackIndex(getTarget(), getKnockbackGroup());
        
        int distance = getBattleEvent().getKnockbackDistance(kbindex);
       // int amount = getBattleEvent().getKnockbackAmount(kbindex);
        
        setDistance(distance);

        boolean knockdown = be.isKnockedDownPossible(kbindex);


        
        KnockbackEffect effect = be.getKnockbackEffect(kbindex);
        
        if(effect!=null)
        {
        	setEffect(effect);
        }
        else {
        	setEffect(  KnockbackEffect.POSSIBLECOLLISION );
        }
        
        this.atip = atip;
        setupPanel();
    }
    
    public JPanel getPanel() {
        return this;
    }
    
    public void hidePanel() {
        
        BattleEvent be = getBattleEvent();
        int kbindex = be.getKnockbackIndex(getTarget(), getKnockbackGroup());
        be.setKnockbackEffect(kbindex, getEffect() );
        
    }
    
    
    
    /** Getter for property target.
     * @return Value of property target.
     */
    public final Target getTarget() {
        return target;
    }
    
    /** Setter for property target.
     * @param target New value of property target.
     */
    public final void setTarget(Target target) {
        this.target = target;
    }
    
    /** Getter for property knockbackGroup.
     * @return Value of property knockbackGroup.
     */
    public final String getKnockbackGroup() {
        return knockbackGroup;
    }
    
    /** Setter for property knockbackGroup.
     * @param knockbackGroup New value of property knockbackGroup.
     */
    public final void setKnockbackGroup(String knockbackGroup) {
        this.knockbackGroup = knockbackGroup;
    }
    
    /** Getter for property effect.
     * @return Value of property effect.
     */
    public final KnockbackEffect getEffect() {
        return effect;
    }
    
    /** Setter for property effect.
     * @param effect New value of property effect.
     */
    public final void setEffect(KnockbackEffect effect) {
        if ( effect != null ) 
        {
            this.effect = effect;

            if ( this.effect.equals(KnockbackEffect.COLLISION)  ) {
                if (collisionButton.isSelected() == false) collisionButton.setSelected(true);

            }
            else if ( this.effect.equals(KnockbackEffect.NOCOLLISION)) {
                if ( noCollisionButton.isSelected() == false ) noCollisionButton.setSelected(true);

            }
            else if ( this.effect.equals(KnockbackEffect.POSSIBLECOLLISION)) {
                if ( possibleCollisionButton.isSelected() == false ) possibleCollisionButton.setSelected(true);

            }
            else if ( this.effect.equals(KnockbackEffect.NOEFFECT)  ) {
                if ( noEffectButton.isSelected() == false ) noEffectButton.setSelected(true);
            }
            else if ( this.effect.equals(KnockbackEffect.KNOCKDOWNONLY)) {
                if ( onlyKnockdownButton.isSelected() == false ) onlyKnockdownButton.setSelected(true);
            
            }
       }
        
    }
    
    public String getAutoBypassOption() {
        return "SHOW_KNOCKBACK_EFFECT_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return getTarget();
    }
    
    /** Getter for property distance.
     * @return Value of property distance.
     */
    public int getDistance() {
        return distance;
    }
    
    public void setTargetReferenceNumber(int targetReferenceNumber) {
        this.targetReferenceNumber = targetReferenceNumber;
    }
    
    /** Getter for property targetGroup.
     * @return Value of property targetGroup.
     */
    public String getTargetGroup() {
        return targetGroup;
    }
    
    /** Setter for property distance.
     * @param distance New value of property distance.
     */
    public void setDistance(int distance) {
        this.distance = distance;
        
        if ( distance <= 0 ) {
            possibleCollisionButton.setEnabled(false);
            noCollisionButton.setEnabled(false);
            collisionButton.setEnabled(false);
        }
        else {
            possibleCollisionButton.setEnabled(true);
            noCollisionButton.setEnabled(true);
            collisionButton.setEnabled(true);
        }
        
        setupDescription();
    }

    /**
     * @return the knockdownPossible
     */
    public boolean isKnockdownPossible() {
        return knockdownPossible;
    }

    /**
     * @param knockdownPossible the knockdownPossible to set
     */
    public void setKnockdownPossible(boolean knockdownPossible) {
        this.knockdownPossible = knockdownPossible;

        onlyKnockdownButton.setEnabled(knockdownPossible);

        setupDescription();
    }
    

    
    protected void fireChangeEvent() {
        if ( listenerList != null ) {
            ChangeEvent e = new ChangeEvent(this);
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length-2; i>=0; i-=2) {
                if (listeners[i]==ChangeListener.class) {
                    // System.out.println( "Firing chance event to: " + listeners[i+1].toString());
                    ((ChangeListener)listeners[i+1]).stateChanged(e);
                }
            }
        }
    }
    
    
    
    /** Setter for property targetGroup.
     * @param targetGroup New value of property targetGroup.
     *
     */
    public void setTargetGroup(String targetGroup) {
        this.targetGroup = targetGroup;
    }
    
    
    /*
     
     
     *  Adds a <code>Battle</code> listener.
     *
     *  @param l  the <code>ChangeListener</code> to add
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class,l);
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
    private javax.swing.JRadioButton collisionButton;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JLabel descriptionLabel2;
    private javax.swing.JLabel distanceLabel;
    private javax.swing.JPanel effectGroup;
    private javax.swing.JRadioButton noCollisionButton;
    public javax.swing.JRadioButton noEffectButton;
    private javax.swing.JRadioButton onlyKnockdownButton;
    private javax.swing.JRadioButton possibleCollisionButton;
    // End of variables declaration//GEN-END:variables




    
}
