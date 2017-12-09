/*
 * AttackParametersPanel.java
 *
 * Created on October 31, 2001, 11:53 PM
 */

package champions.attackTree;

import champions.*;
import champions.event.PADValueEvent;
import champions.interfaces.AbilityIterator;
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
public class AttackParametersPanel extends JPanel implements PADValueListener, AttackTreeInputPanel {
    /** Stores a cached AttackParametersPanel which can be reused. */
    static public AttackParametersPanel ad = null;
    
    /** Stores a cached AttackParametersPanel which can be reused. */
    protected BattleEvent battleEvent = null;
    
    /** Stores the EventListenerList */
    private EventListenerList listenerList;
    
    /** Creates new form AttackParametersPanel */
    public AttackParametersPanel() {
        initComponents();
        
        // instructionLabel.setText("Set Attack Parameters");
        
        setupActions();
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(autofireButton);
        bg.add(sprayButton);
        
        bg = new ButtonGroup();
        bg.add(noSpreadButton);
        bg.add(spreadButton);
        bg.add(spreadmButton);
        
        bg = new ButtonGroup();
        bg.add(maintainAbility);
        bg.add(shutdownAbility);
        
        // Setup Autofire PADs
        autofireShots.setDescription( "Autofire Shots" );
        autofireShots.setPropertyName( "Attack.SHOTS" );
        autofireWidth.setDescription( "Autofire Width" );
        autofireWidth.setPropertyName( "Attack.SPRAYWIDTH" );
        accurateSprayfire.setDescription( "Accurate Sprayfire" );
        accurateSprayfire.setPropertyName( "Attack.ACCURATESPRAYFIRE" );
        concentratedSprayfire.setDescription( "Concentrated Sprayfire" );
        concentratedSprayfire.setPropertyName( "Attack.CONCENTRATEDSPRAYFIRE" );
        rapidAutofire.setDescription( "Rapid Autofire" );
        rapidAutofire.setPropertyName( "Attack.RAPIDAUTOFIRE" );
        skipoverSprayfire.setDescription( "Skipover Sprayfire" );
        skipoverSprayfire.setPropertyName( "Attack.SKIPOVERSPRAYFIRE" );
        autofireShots.addPADValueListener( this );
        autofireWidth.addPADValueListener( this );
        accurateSprayfire.addPADValueListener( this );
        concentratedSprayfire.addPADValueListener( this );
        rapidAutofire.addPADValueListener( this );
        skipoverSprayfire.addPADValueListener( this );
        
        // Setup Moveby multitarget
        movebyTargets.setDescription( "Move-By Targets" );
        movebyTargets.setPropertyName( "Attack.MOVEBYTARGETS" );
        movebyTargets.addPADValueListener( this );
        
        // Setup Spread paramters
        spreadWidth.setDescription("Spread Width");
        spreadWidth.setPropertyName("Attack.SPREADWIDTH");
        spreadWidth.addPADValueListener( this );
        
        // Setup Spread paramters
        burnStunPAD.setDescription("Burn STUN if necessary");
        burnStunPAD.setPropertyName("Attack.BURNSTUN");
        burnStunPAD.addPADValueListener( this );
        
        pushedStrPAD.setPropertyName("Pushed.STR");
        pushedStrPAD.addPADValueListener( this );
        ad= this;
    }
    
    public void setupActions() {
        
    }
    
    static public AttackParametersPanel getAttackParametersPanel(BattleEvent be) {
        if ( ad == null ) ad = new AttackParametersPanel();
        
        ad.setBattleEvent(be);
        
        // Clear any old listeners by creating a new listener list.
        ad.listenerList = new EventListenerList();
        
        return ad;
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
        
        
        
    /*    if ( autofireV == false && spreadV == false && movebyV == false && miscV == false ) {
            instructionLabel.setText( "There are no configurable parameters for this attack." );
        } */
    }
    
    public void PADValueChanged(PADValueEvent evt) {
        if ( evt.getSource() == pushedStrPAD ) {
            battleEvent.add( evt.getKey(), evt.getValue(),true );
            fireChangeEvent();
        }
        else {
            ActivationInfo ai = battleEvent.getActivationInfo();
            ai.add( evt.getKey(), evt.getValue(),true );
            fireChangeEvent();
            
            setWarning("Parameter Changes will reset previous manual CV changing in ToHit Panel!");
        }
    }
    
    public boolean PADValueChanging(PADValueEvent evt) {
        if ( battleEvent != null ) {
            if ( evt.getSource() == autofireShots || evt.getSource() == autofireWidth ) {
                Ability ability = battleEvent.getAbility();
                int value = ((Integer)evt.getValue()).intValue();
                //String burnStun = burnStunPAD.getValue();
                //boolean burnStunEnabled = (burnStun != null && burnStun.equals("TRUE"));
                boolean burnStunEnabled = burnStunPAD.getValue();
                if ( value < 1
                || value > ability.getIntegerValue("Ability.MAXSHOTS" ).intValue()
                || ( burnStunEnabled == false && value > BattleEngine.checkEND(battleEvent)) ) return false;
            }
            else if ( evt.getSource() == spreadWidth ) {
                int value = ((Integer)evt.getValue()).intValue();
                if ( value <= 0 ) return false;
            }
            else if ( evt.getSource() == pushedStrPAD ) {
                int value = ((Integer)evt.getValue()).intValue();
                if ( value < 0 ) return false;
            }
            return true;
        }
        else {
            return false;
        }
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
    
    /**
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
     */
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class,l);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        continuousGroup = new javax.swing.JPanel();
        maintainAbility = new javax.swing.JRadioButton();
        shutdownAbility = new javax.swing.JRadioButton();
        autofireGroup = new javax.swing.JPanel();
        autofireButton = new javax.swing.JRadioButton();
        sprayButton = new javax.swing.JRadioButton();
        autofireShots = new champions.PADIntegerEditor();
        autofireWidth = new champions.PADIntegerEditor();
        accurateSprayfire = new champions.PADBooleanEditor();
        concentratedSprayfire = new champions.PADBooleanEditor();
        rapidAutofire = new champions.PADBooleanEditor();
        skipoverSprayfire = new champions.PADBooleanEditor();
        spreadGroup = new javax.swing.JPanel();
        noSpreadButton = new javax.swing.JRadioButton();
        spreadButton = new javax.swing.JRadioButton();
        spreadmButton = new javax.swing.JRadioButton();
        spreadWidth = new champions.PADIntegerEditor();
        movebyGroup = new javax.swing.JPanel();
        movebyTargets = new champions.PADIntegerEditor();
        otherGroup = new javax.swing.JPanel();
        pushedStrPAD = new champions.PADIntegerEditor();
        burnStunPAD = new champions.PADBooleanEditor();
        fillerGroup = new javax.swing.JPanel();
        warningLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        continuousGroup.setLayout(new java.awt.GridLayout(2, 1));

        continuousGroup.setBorder(new javax.swing.border.TitledBorder("Continuing Ability"));
        maintainAbility.setText("Maintain Constant Ability");
        maintainAbility.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maintainAbilityActionPerformed(evt);
            }
        });

        continuousGroup.add(maintainAbility);

        shutdownAbility.setText("Stop Maintaining Constant Ability");
        shutdownAbility.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shutdownAbilityActionPerformed(evt);
            }
        });

        continuousGroup.add(shutdownAbility);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(continuousGroup, gridBagConstraints);

        autofireGroup.setLayout(new java.awt.GridBagLayout());

        autofireGroup.setBorder(new javax.swing.border.TitledBorder("Autofire Settings"));
        autofireButton.setFont(new java.awt.Font("SansSerif", 0, 11));
        autofireButton.setSelected(true);
        autofireButton.setText("Autofire");
        autofireButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autofireButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        autofireGroup.add(autofireButton, gridBagConstraints);

        sprayButton.setFont(new java.awt.Font("SansSerif", 0, 11));
        sprayButton.setText("Autofire Spray");
        sprayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sprayButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        autofireGroup.add(sprayButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        autofireGroup.add(autofireShots, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        autofireGroup.add(autofireWidth, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        autofireGroup.add(accurateSprayfire, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        autofireGroup.add(concentratedSprayfire, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        autofireGroup.add(rapidAutofire, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        autofireGroup.add(skipoverSprayfire, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(autofireGroup, gridBagConstraints);

        spreadGroup.setLayout(new java.awt.GridBagLayout());

        spreadGroup.setBorder(new javax.swing.border.TitledBorder("Spread EB Settings"));
        noSpreadButton.setFont(new java.awt.Font("SansSerif", 0, 11));
        noSpreadButton.setSelected(true);
        noSpreadButton.setText("No Spread");
        noSpreadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noSpreadButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        spreadGroup.add(noSpreadButton, gridBagConstraints);

        spreadButton.setFont(new java.awt.Font("SansSerif", 0, 11));
        spreadButton.setText("Spread (Single Target)");
        spreadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spreadButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        spreadGroup.add(spreadButton, gridBagConstraints);

        spreadmButton.setFont(new java.awt.Font("SansSerif", 0, 11));
        spreadmButton.setText("Spread (Multi-Target)");
        spreadmButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spreadmButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        spreadGroup.add(spreadmButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        spreadGroup.add(spreadWidth, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(spreadGroup, gridBagConstraints);

        movebyGroup.setLayout(new java.awt.GridBagLayout());

        movebyGroup.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EtchedBorder(), "Move-By Settings"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        movebyGroup.add(movebyTargets, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(movebyGroup, gridBagConstraints);

        otherGroup.setLayout(new java.awt.GridBagLayout());

        otherGroup.setBorder(new javax.swing.border.TitledBorder("Misc. Settings"));
        pushedStrPAD.setDescription("Pushed Strength");
        pushedStrPAD.setPropertyName("Pushed.STR");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        otherGroup.add(pushedStrPAD, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        otherGroup.add(burnStunPAD, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(otherGroup, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(fillerGroup, gridBagConstraints);

        warningLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        warningLabel.setForeground(java.awt.Color.red);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(warningLabel, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    private void shutdownAbilityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shutdownAbilityActionPerformed
        // Add your handling code here:
        if ( shutdownAbility.isSelected() ) {
            battleEvent.add("BattleEvent.SHOULDSHUTDOWN", "TRUE", true);
        }
    }//GEN-LAST:event_shutdownAbilityActionPerformed
    
    private void maintainAbilityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maintainAbilityActionPerformed
        // Add your handling code here:
        if ( maintainAbility.isSelected() ) {
            battleEvent.add("BattleEvent.SHOULDSHUTDOWN", "FALSE", true);
        }
    }//GEN-LAST:event_maintainAbilityActionPerformed
    
    public void sprayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sprayButtonActionPerformed
        // Add your handling code here:
        if ( battleEvent != null && sprayButton.isSelected() ) {
            ActivationInfo ai = battleEvent.getActivationInfo();
            ai.add("Attack.ISSPRAY", "TRUE" , true);
            autofireWidth.setEnabled(true);
            boolean isSpray = ai.getBooleanValue( "Attack.ISSPRAY" );
            
            Target source = battleEvent.getSource();
            if(source!=null) {
	            AbilityIterator aiter = source.getSkills();
	            while (aiter.hasNext() ) {
	                Ability a = aiter.nextAbility();
	                if (a.getName().equals("Accurate Sprayfire") && a.isEnabled(source) && isSpray ) {
	                    accurateSprayfire.setEnabled( true );
	                    break;
	                }
	                else accurateSprayfire.setEnabled( false );
	            }
	            
	            aiter = source.getSkills();
	            while (aiter.hasNext() ) {
	                Ability a = aiter.nextAbility();
	                if (a.getName().equals("Concentrated Sprayfire") && a.isEnabled(source) && isSpray ) {
	                    concentratedSprayfire.setEnabled( true );
	                    break;
	                }
	                else concentratedSprayfire.setEnabled( false );
	            }
	            
	            aiter = source.getSkills();
	            while (aiter.hasNext() ) {
	                Ability a = aiter.nextAbility();
	                if (a.getName().equals("Skipover Sprayfire") && a.isEnabled(source) && isSpray ) {
	                    skipoverSprayfire.setEnabled( true );
	                    break;
	                }
	                else skipoverSprayfire.setEnabled( false );
	            }
            }
            
            fireChangeEvent();
        }
    }//GEN-LAST:event_sprayButtonActionPerformed
    
    private void autofireButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autofireButtonActionPerformed
        // Add your handling code here:
        if ( battleEvent != null && autofireButton.isSelected() ) {
            ActivationInfo ai = battleEvent.getActivationInfo();
            ai.add("Attack.ISSPRAY", "FALSE",  true);
            autofireWidth.setEnabled( false );
            autofireWidth.setValue(new Integer(1));
            accurateSprayfire.setEnabled(false);
            concentratedSprayfire.setEnabled(false);
            rapidAutofire.setEnabled(false);
            skipoverSprayfire.setEnabled(false);
            
            
            fireChangeEvent();
        }
    }//GEN-LAST:event_autofireButtonActionPerformed
    
    private void spreadmButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spreadmButtonActionPerformed
        // Add your handling code here:
        if ( battleEvent != null && spreadmButton.isSelected() == true) {
            ActivationInfo ai = battleEvent.getActivationInfo();
            ai.add("Attack.ISSPREAD", "TRUE" , true);
            ai.add("Attack.ISSPREADM", "TRUE" , true);
            spreadWidth.setEnabled( true );
            fireChangeEvent();
        }
    }//GEN-LAST:event_spreadmButtonActionPerformed
    
    private void spreadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spreadButtonActionPerformed
        // Add your handling code here:
        if ( battleEvent != null && spreadButton.isSelected() == true) {
            ActivationInfo ai = battleEvent.getActivationInfo();
            ai.add("Attack.ISSPREAD", "TRUE" , true);
            ai.add("Attack.ISSPREADM", "FALSE" , true);
            spreadWidth.setEnabled( true );
            fireChangeEvent();
        }
    }//GEN-LAST:event_spreadButtonActionPerformed
    
    private void noSpreadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noSpreadButtonActionPerformed
        // Add your handling code here:
        if ( battleEvent != null && noSpreadButton.isSelected() == true) {
            ActivationInfo ai = battleEvent.getActivationInfo();
            ai.add("Attack.ISSPREAD", "FALSE" , true);
            ai.add("Attack.ISSPREADM", "FALSE" , true);
            spreadWidth.setEnabled( false );
            accurateSprayfire.setEnabled(false);
            concentratedSprayfire.setEnabled(false);
            rapidAutofire.setEnabled(false);
            skipoverSprayfire.setEnabled(false);
            fireChangeEvent();
        }
    }//GEN-LAST:event_noSpreadButtonActionPerformed
    
    public void showPanel(AttackTreePanel atip) {
        setupPanel();
    }
    
    public JPanel getPanel() {
        return this;
    }
    
    public void hidePanel() {
        
    }
    
    
    private void clearWarning() {
        warningLabel.setText("");
    }
    
    private void setWarning(String text) {
        warningLabel.setText(text);
    }
    
    public void setupPanel() {
        boolean continuingV = false;
        boolean autofireV = false;
        boolean spreadV = true;
        boolean movebyV = false;
        boolean miscV = true;
        
        Ability ability = battleEvent.getAbility();
        
        ActivationInfo ai = battleEvent.getActivationInfo();
        
        if ( ai.isContinuing() ) {
            continuingV = true;
        }
        
        if ( battleEvent.is("AE") ){
            spreadV = false;
        }
        if ( battleEvent.is("AUTOFIRE" ) ) {
            spreadV = false;
            autofireV = true;
            
            Integer maxshots = ai.getIntegerValue( "Attack.SHOTS" );
            if ( maxshots == null ) maxshots = new Integer(1);
            
            Integer spraywidth = ai.getIntegerValue( "Attack.SPRAYWIDTH" );
            if ( spraywidth == null ) spraywidth = new Integer(1);
            
            autofireShots.setValue(maxshots);
            autofireWidth.setValue(spraywidth);
            
            boolean isSpray = ai.getBooleanValue( "Attack.ISSPRAY" );
            autofireButton.setSelected( !isSpray);
            sprayButton.setSelected( isSpray );
            autofireWidth.setEnabled( isSpray );
            accurateSprayfire.setEnabled(isSpray);
            concentratedSprayfire.setEnabled(isSpray);
            rapidAutofire.setEnabled(isSpray);
            skipoverSprayfire.setEnabled(isSpray);
            
        }
        else if ( battleEvent.is("MOVEBY" ) ) {
            spreadV = false;
            autofireV = false;
            movebyV= true;
            
            Integer targets = ai.getIntegerValue( "Attack.MOVEBYTARGETS" );
            if ( targets == null ) targets = new Integer(1);
            
            movebyTargets.setValue( targets );
        }
        
        if ( spreadV == true && battleEvent.can("SPREAD") == false ) {
            spreadV = false;
        }
        else {
            Integer width = ai.getIntegerValue( "Attack.SPREADWIDTH" );
            if ( width == null ) width = new Integer(1);
            
            spreadWidth.setValue( width );
            
            boolean isSpread = ai.getBooleanValue( "Attack.ISSPREAD" );
            boolean isSpreadm = ai.getBooleanValue( "Attack.ISSPREADM" );
            
            if ( isSpread ) spreadButton.setSelected(true);
            if ( isSpreadm ) spreadmButton.setSelected(true);
            if ( isSpread == false && isSpreadm == false ) noSpreadButton.setSelected(true);
        }
        
        if ( battleEvent.isMeleeAttack() ) {
            Integer pushedStr = battleEvent.getIntegerValue( "Pushed.STR" );
            if ( pushedStr == null ) pushedStr = new Integer(0);
            
            pushedStrPAD.setVisible( true );
            pushedStrPAD.setValue( pushedStr );
            miscV = true;
        }
        else {
            pushedStrPAD.setVisible( false );
        }
        
        boolean burnStun = ai.getBooleanValue("Attack.BURNSTUN");
        burnStunPAD.setValue( burnStun ? "TRUE" : "FALSE" );
        
        boolean shouldShutdown = battleEvent.getBooleanValue("BattleEvent.SHOULDSHUTDOWN");
        maintainAbility.setSelected(! shouldShutdown );
        shutdownAbility.setSelected( shouldShutdown );
        
        boolean accuratesprayfire = ai.getBooleanValue("Attack.ACCURATESPRAYFIRE");
        accurateSprayfire.setValue( accuratesprayfire ? "TRUE" : "FALSE" );
        boolean concentratedsprayfire = ai.getBooleanValue("Attack.CONCENTRATEDSPRAYFIRE");
        concentratedSprayfire.setValue( concentratedsprayfire ? "TRUE" : "FALSE" );
        boolean skipoversprayfire = ai.getBooleanValue("Attack.SKIPOVERSPRAYFIRE");
        skipoverSprayfire.setValue( skipoversprayfire ? "TRUE" : "FALSE" );
        
        //        boolean isSpray = ai.getBooleanValue( "Attack.ISSPRAY" );
        //
        //        Target source = battleEvent.getSource();
        //        AbilityIterator aiter = source.getSkills();
        //        while (aiter.hasNext() ) {
        //            Ability a = aiter.nextAbility();
        //            if (a.getName().equals("Accurate Sprayfire") && a.isEnabled(source) && isSpray ) {
        //                accurateSprayfire.setEnabled( true );
        //                break;
        //            }
        //            else accurateSprayfire.setEnabled( false );
        //        }
        //
        //        aiter = source.getSkills();
        //        while (aiter.hasNext() ) {
        //            Ability a = aiter.nextAbility();
        //            if (a.getName().equals("Concentrated Sprayfire") && a.isEnabled(source) && isSpray ) {
        //                concentratedSprayfire.setEnabled( true );
        //                break;
        //            }
        //            else concentratedSprayfire.setEnabled( false );
        //        }
        //
        //        aiter = source.getSkills();
        //        while (aiter.hasNext() ) {
        //            Ability a = aiter.nextAbility();
        //            if (a.getName().equals("Skipover Sprayfire") && a.isEnabled(source) && isSpray ) {
        //                skipoverSprayfire.setEnabled( true );
        //                break;
        //            }
        //            else skipoverSprayfire.setEnabled( false );
        //        }
        //
        continuousGroup.setVisible(continuingV);
        autofireGroup.setVisible(autofireV);
        spreadGroup.setVisible(spreadV);
        movebyGroup.setVisible(movebyV);
        otherGroup.setVisible(miscV);
        
        clearWarning();
        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public champions.PADBooleanEditor accurateSprayfire;
    private javax.swing.JRadioButton autofireButton;
    private javax.swing.JPanel autofireGroup;
    public champions.PADIntegerEditor autofireShots;
    public champions.PADIntegerEditor autofireWidth;
    public champions.PADBooleanEditor burnStunPAD;
    private champions.PADBooleanEditor concentratedSprayfire;
    private javax.swing.JPanel continuousGroup;
    private javax.swing.JPanel fillerGroup;
    private javax.swing.JRadioButton maintainAbility;
    private javax.swing.JPanel movebyGroup;
    private champions.PADIntegerEditor movebyTargets;
    private javax.swing.JRadioButton noSpreadButton;
    private javax.swing.JPanel otherGroup;
    public champions.PADIntegerEditor pushedStrPAD;
    private champions.PADBooleanEditor rapidAutofire;
    private javax.swing.JRadioButton shutdownAbility;
    private champions.PADBooleanEditor skipoverSprayfire;
    public javax.swing.JRadioButton sprayButton;
    private javax.swing.JRadioButton spreadButton;
    private javax.swing.JPanel spreadGroup;
    private champions.PADIntegerEditor spreadWidth;
    private javax.swing.JRadioButton spreadmButton;
    private javax.swing.JLabel warningLabel;
    // End of variables declaration//GEN-END:variables
    
}
