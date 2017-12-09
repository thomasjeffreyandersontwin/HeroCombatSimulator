/*
 * ToHitPanel.java
 *
 *
 * Created on November 11, 2001, 9:09 PM
 */

package champions.attackTree;

import champions.*;
import champions.event.PADValueEvent;
import champions.interfaces.AbilityIterator;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.PADValueListener;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 *
 * @author  twalker
 */
public class TargetSkillRollPanel extends JPanel implements PADValueListener, AttackTreeInputPanel, ChampionsConstants {
    
    /** Stores the static, default toHit panel. */
    static private TargetSkillRollPanel defaultPanel = null;
    
    private AttackTreePanel atip = null;
    
    private String rollMode;
    
    
    /** Holds value of property battleEvent. */
    private BattleEvent battleEvent;
    
    /** Stores the EventListenerList */
    private EventListenerList listenerList;
    
    /** Holds value of property dice. */
    private Dice dice;
    
    /** Holds value of property diceName. */
    private String diceName;
    
    /** Creates new form ToHitPanel */
    public TargetSkillRollPanel() {
        initComponents();
        
        ButtonGroup bg = new ButtonGroup();
        // Setup Spread paramters
        TargetskillModifier.setDescription("Modifier");
        TargetskillModifier.setPropertyName("Attack.TARGETMODIFIER");
        TargetskillModifier.addPADValueListener( this );
    }
    
    static public TargetSkillRollPanel getDefaultPanel(BattleEvent be, String diceName, Target target, String targetGroup) {
        if ( defaultPanel == null ) defaultPanel = new TargetSkillRollPanel();
        
        defaultPanel.setBattleEvent(be);
        defaultPanel.setDiceName(diceName);
        defaultPanel.setBattleEvent(be);
        //defaultPanel.setSkill(skill);
        defaultPanel.setTarget(target);
        defaultPanel.setDiceName(diceName);
        defaultPanel.setTargetGroup(targetGroup);
        
        
        return defaultPanel;
    }
    
    public void setupPanel() {
        if ( getBattleEvent() != null  ) {
            int dindex = battleEvent.getDiceIndex(diceName, getTargetGroup());
            boolean auto = battleEvent.getDiceAutoRoll(dindex);
            Dice d = battleEvent.getDiceRoll(dindex);
            
            ActivationInfo ai = battleEvent.getActivationInfo();
            
            setDice(d);
            setRollMode( auto ? AUTO_ROLL : MANUAL_ROLL );
            Integer modifier = ai.getIntegerValue( "Attack.TARGETMODIFIER" );
            if ( modifier == null ) modifier = new Integer(0);
            TargetskillModifier.setValue(modifier);
            
            DefaultComboBoxModel cbm;
            //int index;
            cbm = new DefaultComboBoxModel();
            
            Target target = ai.getTarget(0);
            //Target target = getTarget();
            AbilityIterator aiter = target.getSkills();
            Integer comboindex = ai.getIntegerValue( "Attack.COMBOINDEX" );
            int INTindex = 0;
            int PERindex = 0;
            int Concealmentindex = -1;
            int Forgeryindex = -1;
            int Gamblingindex = -1;
            int index = 0;
            Ability a = null;
            //            Ability newINTRoll = PADRoster.newPowerInstance("INT Roll");
            while (aiter.hasNext() ) { // Check to see if there is another item
                // Rip next ability from the iterator.
                // This a is guaranteed to have a power which is actually a skill
                // since we used the getSkills.  If we use getAbilities() we will
                // actually get both skills and powers.
                a = aiter.nextAbility();
                if (a.getName().equals("INT Roll")) {
                    INTindex = index;
                }
                if (a.getName().equals("PER Roll")) {
                    PERindex = index;
                }
                if (a.getName().equals("Concealment")) {
                    Concealmentindex = index;
                }
                if (a.getName().equals("Forgery")) {
                    Forgeryindex = index;
                }
                if (a.getName().equals("Gambling")) {
                    Gamblingindex = index;
                }
                cbm.addElement(a);
                index++;
                
                // Do something with the ability here...maybe add it to a list or
                // create ComboBox entries based upon the ability...
            }
            TargetSkillCombo.setModel(cbm);
            //            Integer comboindex = ai.getIntegerValue( "Attack.COMBOINDEX" );
            Ability currentAbility = battleEvent.getActivationInfo().getAbility();
            if (comboindex != null) {
                TargetSkillCombo.setSelectedIndex(comboindex.intValue() );
            }
            
            //set the default for acting to INTRoll for SvS rolls
            else if (currentAbility.getPower().getName().equals("Acting") ) {
                TargetSkillCombo.setSelectedIndex(INTindex);
            }
            //set the default for acting to INTRoll for SvS rolls
            else if (currentAbility.getPower().getName().equals("Concealment") ) {
                if (Concealmentindex >= 0 ) {
                    TargetSkillCombo.setSelectedIndex(Concealmentindex);
                }
                else {
                    TargetSkillCombo.setSelectedIndex(INTindex);
                }
            }
            else if (currentAbility.getPower().getName().equals("Forgery") ) {
                if (Forgeryindex >= 0 ) {
                    TargetSkillCombo.setSelectedIndex(Forgeryindex);
                }
                else {
                    TargetSkillCombo.setSelectedIndex(INTindex);
                }
            }
            else if (currentAbility.getPower().getName().equals("Gambling") ) {
                if (Gamblingindex >= 0 ) {
                    TargetSkillCombo.setSelectedIndex(Gamblingindex);
                }
                else {
                    TargetSkillCombo.setSelectedIndex(PERindex);
                }
            }
            else if (currentAbility.getPower().getName().equals("Disguise") ||
            currentAbility.getPower().getName().equals("Sleight of Hand") ||
            currentAbility.getPower().getName().equals("Stealth")) {
                TargetSkillCombo.setSelectedIndex(PERindex);
            }
            else {
                TargetSkillCombo.setSelectedIndex(INTindex);
            }
        }
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        rollGroup = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        TargetneededLabel = new javax.swing.JLabel();
        TargetSkillCombo = new javax.swing.JComboBox();
        TargetrollField = new javax.swing.JTextField();
        TargetrollCheckbox = new javax.swing.JCheckBox();
        TargetskillModifier = new champions.PADIntegerEditor();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        rollGroup.setLayout(new java.awt.GridBagLayout());

        rollGroup.setBorder(new javax.swing.border.TitledBorder("Target Skill Roll"));
        jLabel1.setText("Skill Roll Needed: ");
        rollGroup.add(jLabel1, new java.awt.GridBagConstraints());

        TargetneededLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        TargetneededLabel.setForeground(java.awt.Color.blue);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        rollGroup.add(TargetneededLabel, gridBagConstraints);

        TargetSkillCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TargetSkillComboActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        rollGroup.add(TargetSkillCombo, gridBagConstraints);

        TargetrollField.setText("AUTO");
        TargetrollField.setMinimumSize(new java.awt.Dimension(40, 20));
        TargetrollField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TargetrollFieldActionPerformed(evt);
            }
        });
        TargetrollField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                TargetrollFieldFocusGained(evt);
            }
        });

        rollGroup.add(TargetrollField, new java.awt.GridBagConstraints());

        TargetrollCheckbox.setText("AutoRoll Activation");
        TargetrollCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TargetrollCheckboxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        rollGroup.add(TargetrollCheckbox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        rollGroup.add(TargetskillModifier, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(rollGroup, gridBagConstraints);

    }//GEN-END:initComponents
    
    private void TargetSkillComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TargetSkillComboActionPerformed
        // Add your handling code here:
        int index = TargetSkillCombo.getSelectedIndex();
        ActivationInfo ai = battleEvent.getActivationInfo();
        Target target = ai.getTarget(0);
        Ability combo = (Ability)TargetSkillCombo.getSelectedItem();
        int targetbaseroll = combo.getSkillRoll(target);
        String crammed= combo.getStringValue("Power.CRAMMED");
        
        if (crammed != null && crammed.equals("TRUE") ) {
            ai.add("Attack.TARGETBASEROLL",new Integer(8),true);
        }
        else if (targetbaseroll >= 0) {
            ai.add("Attack.TARGETBASEROLL",new Integer(targetbaseroll),true);
        }
        else {
            ai.add("Attack.TARGETBASEROLL",new Integer(0),true);
        }
        ai.add("Attack.COMBOINDEX",new Integer(index),true);
        updateSkillRoll(combo);
        fireChangeEvent();
    }//GEN-LAST:event_TargetSkillComboActionPerformed
    
    private void TargetrollFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TargetrollFieldActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_TargetrollFieldActionPerformed
    
    private void TargetrollCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TargetrollCheckboxActionPerformed
        // Add your handling code here:
        if ( TargetrollCheckbox.isSelected() ) {
            setRollMode(AUTO_ROLL);
            setDice(null);
        }
        else {
            setRollMode(MANUAL_ROLL);
        }
    }//GEN-LAST:event_TargetrollCheckboxActionPerformed
    
    private void TargetrollFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TargetrollFieldFocusGained
        // Add your handling code here:
        
        if ( getRollMode().equals(MANUAL_ROLL) == false ) {
            setRollMode(MANUAL_ROLL);
        }
        
        TargetrollField.selectAll();
    }//GEN-LAST:event_TargetrollFieldFocusGained
    
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
    
    /** Getter for property dice.
     * @return Value of property dice.
     */
    public Dice getDice() {
        return dice;
    }
    
    /** Setter for property dice.
     * @param dice New value of property dice.
     */
    public void setDice(Dice dice) {
        this.dice = dice;
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
        if ( getBattleEvent() != null) {
            int dindex = battleEvent.getDiceIndex(diceName, getTargetGroup() );
            
            String rollmode = getRollMode();
            
            if ( rollmode.equals(AUTO_ROLL) ) {
                if ( getDice() == null ) {
                    Dice autoRoll = new Dice(3);
                    battleEvent.setDiceRoll(dindex, autoRoll);
                }
                else {
                    battleEvent.setDiceRoll(dindex, getDice());
                }
                battleEvent.setDiceAutoRoll(dindex, true);
            }
            else if ( rollmode.equals(MANUAL_ROLL) ) {
                String roll = TargetrollField.getText();
                Dice diceRoll = null;
                try {
                    int value = Integer.parseInt(roll);
                    
                    diceRoll = new Dice(value, 0);
                }
                catch(NumberFormatException nfe) {
                    // Don't do anything
                }
                
                battleEvent.setDiceRoll(dindex, diceRoll);
                battleEvent.setDiceAutoRoll(dindex, true    );
            }
        }
    }
    
    /** Getter for property rollMode.
     * @return Value of property rollMode.
     */
    public String getRollMode() {
        return rollMode;
    }
    
    
    /** Setter for property rollMode.
     * @param rollMode New value of property rollMode.
     */
    public void setRollMode(String rollMode) {
        String oldMode = this.rollMode;
        this.rollMode = rollMode;
        if ( rollMode.equals(MANUAL_ROLL) ) {
            TargetrollCheckbox.setSelected(false);
            
            if ( getDice() != null && getDice().isRealized() ) {
                TargetrollField.setText( getDice().getStun().toString() );
            }
            else {
                TargetrollField.setText( "" );
            }
            
            TargetrollField.requestFocus();
        }
        else if ( rollMode.equals(AUTO_ROLL) ) {
            TargetrollCheckbox.setSelected(true);
            
            TargetrollField.setText("AUTO");
        }
        
        
    }
    
    /** Getter for property diceName.
     * @return Value of property diceName.
     */
    public String getDiceName() {
        return this.diceName;
    }
    
    /** Setter for property diceName.
     * @param diceName New value of property diceName.
     */
    public void setDiceName(String diceName) {
        this.diceName = diceName;
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
    
    public boolean PADValueChanging(PADValueEvent evt) {
        if ( battleEvent != null ) {
            if ( evt.getSource() == TargetskillModifier ) {
                Ability ability = battleEvent.getAbility();
                int value = ((Integer)evt.getValue()).intValue();
                
            }
            
        }
        return true;
    }
    
    public void PADValueChanged(PADValueEvent evt) {
        ActivationInfo ai = battleEvent.getActivationInfo();
        ai.add( evt.getKey(), evt.getValue(),true );
        updateSkillRoll(null);
        fireChangeEvent();
    }
    
    private void updateSkillRoll(Ability combo) {
        Integer targetmodifier = battleEvent.getActivationInfo().getIntegerValue( "Attack.TARGETMODIFIER" );
        Integer targetbaseroll = battleEvent.getActivationInfo().getIntegerValue( "Attack.TARGETBASEROLL" );
        Ability ability = battleEvent.getAbility();
        ActivationInfo ai = battleEvent.getActivationInfo();
        //        Target source = battleEvent.getSource();
        Integer usedsl = new Integer(0);
        Ability effectAbility;
        Integer level = new Integer(0);
        Integer allocatesl = new Integer(0);
        Integer skillbonus = new Integer(0);
        //Target source = battleEvent.getSource();
        Target source = ai.getTarget(0);
        int count, i;
        
        int sbtot = 0;
        // Run through the source Effects to let them know that Ability is activating
        // First build list, then run through list (just in case it changes).
        count = source.getEffectCount();
        Effect[] sourceEffects = new Effect[count];
        for (i=0;i<count;i++) {
            sourceEffects[i] = source.getEffect(i);
        }
        
        for (i=0;i<count;i++) {
            if ( source.hasEffect(sourceEffects[i]) ) {
                sourceEffects[i].skillIsActivating(battleEvent,combo);
                skillbonus = sourceEffects[i].getIntegerValue("Effect.SKILLLEVEL");
                usedsl = (Integer)sourceEffects[i].getValue("Effect.USEDSL");
                effectAbility = (Ability)sourceEffects[i].getValue("Effect.ABILITY");
                level = effectAbility.getIntegerValue("Power.SKILLLEVEL");
                allocatesl = effectAbility.getIntegerValue("CombatLevel.ALLOCATESL");
                if (level == null) level = new Integer(0);
                if (usedsl == null) usedsl = new Integer(0);
                
                if (skillbonus != null && combo != null ) {
                    if (level.intValue() >= (usedsl.intValue() + skillbonus.intValue())) {
                        sourceEffects[i].skillIsActivating(battleEvent,combo);
                        sbtot = sbtot + skillbonus.intValue();
                        usedsl = new Integer(usedsl.intValue() + skillbonus.intValue());
                    }
                    else {
                        sbtot = sbtot + (level.intValue() - usedsl.intValue());
                        usedsl = new Integer(level.intValue());
                    }
                    ai.add("Attack.SBTOT",new Integer(sbtot),true);
                    sourceEffects[i].add("Effect.USEDSL", new Integer(usedsl.intValue()), true);
                    
                    
                }
                
            }
        }
        TargetneededLabel.setText( Integer.toString(sbtot + targetmodifier.intValue() + targetbaseroll.intValue() ) );
    }
    /** Setter for property target.
     * @param target New value of property target.
     *
     */
    public void setTarget(Target target) {
        this.target = target;
        updateSkillRoll(null);
    }
    
    /** Getter for property skill.
     * @return Value of property skill.
     *
     */
    public Ability getSkill() {
        return this.skill;
    }
    
    /** Setter for property skill.
     * @param skill New value of property skill.
     *
     */
    public void setSkill(Ability skill) {
        this.skill = skill;
        updateSkillRoll(null);
    }
    
    
    /** Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        return this.target;
    }
    
    /** Getter for property targetGroup.
     * @return Value of property targetGroup.
     *
     */
    public String getTargetGroup() {
        return this.targetGroup;
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
    private javax.swing.JComboBox TargetSkillCombo;
    private javax.swing.JLabel TargetneededLabel;
    private javax.swing.JCheckBox TargetrollCheckbox;
    private javax.swing.JTextField TargetrollField;
    private champions.PADIntegerEditor TargetskillModifier;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel rollGroup;
    // End of variables declaration//GEN-END:variables
    
    /** Holds value of property target. */
    private Target target;
    
    /** Holds value of property skill. */
    private Ability skill;
    
    /** Holds value of property targetGroup. */
    private String targetGroup;
    
}
