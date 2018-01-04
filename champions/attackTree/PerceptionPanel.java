/*
 * ToHitPanel.java
 *
 *
 * Created on November 11, 2001, 9:09 PM
 */

package champions.attackTree;

import champions.*;
import champions.interfaces.ChampionsConstants;
import champions.senseTree.STNullSenseNode;
import champions.senseTree.STSenseListNode;
import champions.senseTree.STSenseNode;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 *
 * @author  twalker
 */
public class PerceptionPanel extends JPanel 
        implements AttackTreeInputPanel, TreeSelectionListener, ChampionsConstants {
    
    enum PanelType { ATTACKER_PERCEPTION_PANEL, TARGET_PRECEPTION_PANEL };
    /** Stores the static, default toHit panel. */
    public static PerceptionPanel defaultPanel = null;
    
    private AttackTreePanel atip = null;
    
    private Sense selectedSense;
    
    /** Holds value of property battleEvent. */
    private BattleEvent battleEvent;
    
    /** Holds value of property diceName. */
    private double cvMultiplier;
    
    private PanelType panelType;
    
    private Target target;
    private String targetGroup;
    
    private Target senser;
    private Target sensee;
    
    private List<Sense> senseList;
    
    private ComboBoxModel ocvComboModel;
    private ComboBoxModel dcvComboModel;
    
    enum PerceptionWarning {
        WARNING_ATTACKER_NO_SENSE("<bold>Warning:</bold> No sense is selected by the attacker " +
                                "(None are eligible?).  Standard no-sense OCV penalties will be applied.  " +
                                "Select a sense to use or have the attacker make a " +
                                "half-phase perception roll prior with some sense against this target " +
                                "prior to making this attack."),
        WARNING_ATTACKER_NONTARGETING_UNPERCEIVABLE("<bold>Warning:</bold> The target is not perceivable by " +
                                "the attacker with the selected sense.  Standard no-sense OCV penalties" +
                                "will be applied unless overriden.  Typically, the attacker should make a " +
                                "half-phase perception roll prior to using this sense against this target."),
        WARNING_ATTACKER_TARGETING_UNPERCEIVABLE("<bold>Warning:</bold> The target is not perceivable by " +
                                "the attacker with the selected sense.  Standard no-sense OCV penalties" +
                                "will be applied unless overriden.  Typically, the attacker should make a " +
                                "half-phase perception roll prior to using this sense against this target."),
        WARNING_ATTACKER_UNRANGED("<bold>Warning:</bold> The selected sense is a non-ranged sense.  Typically, " +
                                "non-ranged senses can not be used to attack.  Standard no-sense OCV penalties " +
                                "will be applied unless overriden.");
        
        public String text;
        
        PerceptionWarning(String text) { this.text = text; };
    }
    
    /** Creates new form ToHitPanel */
    public PerceptionPanel() {
        initComponents();
        
        senseTree.getSelectionModel().addTreeSelectionListener(this);
        
        ocvComboModel = new DefaultComboBoxModel(SenseCVModifier.getOCVValues().toArray());
        dcvComboModel = new DefaultComboBoxModel(SenseCVModifier.getDCVValues().toArray());
        defaultPanel=this;
    }
    
    static public PerceptionPanel getDefaultPanel(BattleEvent be, PanelType panelType, List<Sense> senseList, Sense selectedSense, SenseCVModifier cvModifier, Target target, String targetGroup) {
        if ( defaultPanel == null ) defaultPanel = new PerceptionPanel();
        
        defaultPanel.setBattleEvent(be);
        defaultPanel.setTarget(target);
        defaultPanel.setTargetGroup(targetGroup);
        defaultPanel.setPanelType(panelType);
        defaultPanel.setSenseList(senseList);
        defaultPanel.setSelectedSense(selectedSense);
        defaultPanel.setCVModifier(cvModifier);

        
        return defaultPanel;
    }
    
    public void setupPanel() {
        if ( getBattleEvent() != null  ) {
//            int dindex = battleEvent.getDiceIndex(diceName, getTargetGroup());
//            boolean auto = battleEvent.getDiceAutoRoll(dindex);
//            Dice d = battleEvent.getDiceRoll(dindex);
//            
//            ActivationInfo ai = battleEvent.getActivationInfo();
//            
//            setDice(d);
//            setRollMode( auto ? AUTO_ROLL : MANUAL_ROLL );
//            Integer modifier = ai.getIntegerValue( "Attack.TARGETMODIFIER" );
//            if ( modifier == null ) modifier = new Integer(0);
//            TargetskillModifier.setValue(modifier);
//            
//            DefaultComboBoxModel cbm;
//            //int index;
//            cbm = new DefaultComboBoxModel();
//            
//            Target target = ai.getTarget(0);
//            //Target target = getTarget();
//            AbilityIterator aiter = target.getSkills();
//            Integer comboindex = ai.getIntegerValue( "Attack.COMBOINDEX" );
//            int INTindex = 0;
//            int PERindex = 0;
//            int Concealmentindex = -1;
//            int Forgeryindex = -1;
//            int Gamblingindex = -1;
//            int index = 0;
//            Ability a = null;
//            //            Ability newINTRoll = PADRoster.newPowerInstance("INT Roll");
//            while (aiter.hasNext() ) { // Check to see if there is another item
//                // Rip next ability from the iterator.
//                // This a is guaranteed to have a power which is actually a skill
//                // since we used the getSkills.  If we use getAbilities() we will
//                // actually get both skills and powers.
//                a = aiter.nextAbility();
//                if (a.getName().equals("INT Roll")) {
//                    INTindex = index;
//                }
//                if (a.getName().equals("PER Roll")) {
//                    PERindex = index;
//                }
//                if (a.getName().equals("Concealment")) {
//                    Concealmentindex = index;
//                }
//                if (a.getName().equals("Forgery")) {
//                    Forgeryindex = index;
//                }
//                if (a.getName().equals("Gambling")) {
//                    Gamblingindex = index;
//                }
//                cbm.addElement(a);
//                index++;
//                
//                // Do something with the ability here...maybe add it to a list or
//                // create ComboBox entries based upon the ability...
//            }
//            TargetSkillCombo.setModel(cbm);
//            //            Integer comboindex = ai.getIntegerValue( "Attack.COMBOINDEX" );
//            Ability currentAbility = battleEvent.getActivationInfo().getAbility();
//            if (comboindex != null) {
//                TargetSkillCombo.setSelectedIndex(comboindex.intValue() );
//            }
//            
//            //set the default for acting to INTRoll for SvS rolls
//            else if (currentAbility.getPower().getName().equals("Acting") ) {
//                TargetSkillCombo.setSelectedIndex(INTindex);
//            }
//            //set the default for acting to INTRoll for SvS rolls
//            else if (currentAbility.getPower().getName().equals("Concealment") ) {
//                if (Concealmentindex >= 0 ) {
//                    TargetSkillCombo.setSelectedIndex(Concealmentindex);
//                }
//                else {
//                    TargetSkillCombo.setSelectedIndex(INTindex);
//                }
//            }
//            else if (currentAbility.getPower().getName().equals("Forgery") ) {
//                if (Forgeryindex >= 0 ) {
//                    TargetSkillCombo.setSelectedIndex(Forgeryindex);
//                }
//                else {
//                    TargetSkillCombo.setSelectedIndex(INTindex);
//                }
//            }
//            else if (currentAbility.getPower().getName().equals("Gambling") ) {
//                if (Gamblingindex >= 0 ) {
//                    TargetSkillCombo.setSelectedIndex(Gamblingindex);
//                }
//                else {
//                    TargetSkillCombo.setSelectedIndex(PERindex);
//                }
//            }
//            else if (currentAbility.getPower().getName().equals("Disguise") ||
//            currentAbility.getPower().getName().equals("Sleight of Hand") ||
//            currentAbility.getPower().getName().equals("Stealth")) {
//                TargetSkillCombo.setSelectedIndex(PERindex);
//            }
//            else {
//                TargetSkillCombo.setSelectedIndex(INTindex);
//            }
        }
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        senseTree = new champions.senseTree.STSenseTree();
        jLabel1 = new javax.swing.JLabel();
        selectedLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        canSenseLabel = new javax.swing.JLabel();
        cvLabel = new javax.swing.JLabel();
        cvModifierCombo = new javax.swing.JComboBox();
        warningLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        modifiersLabel = new javax.swing.JLabel();

        jLabel5.setText("jLabel5");

        jLabel8.setText("jLabel8");

        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        senseTree.setHighlightEnabled(true);
        jScrollPane1.setViewportView(senseTree);

        jLabel1.setText("Selected Sense");

        selectedLabel.setFont(new java.awt.Font("SansSerif", 0, 11));

        jLabel4.setText("Can Sense Target");

        canSenseLabel.setFont(new java.awt.Font("SansSerif", 0, 11));
        canSenseLabel.setText("Yes");

        cvLabel.setText("OCV Modifier");

        cvModifierCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        warningLabel.setFont(new java.awt.Font("SansSerif", 0, 11));
        warningLabel.setForeground(javax.swing.UIManager.getDefaults().getColor("nb.errorForeground"));
        warningLabel.setText("<html>Warning: This sense is a non-targeting sense and is not marked as being able to perceive the attacker.  By selecting this sense, you are overriding standard sense rules and perception rolls.  Typically, the defender should abort to a half-phase perception roll instead.</html> ");

        jLabel7.setText("Modifiers");

        modifiersLabel.setFont(new java.awt.Font("SansSerif", 0, 11));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(warningLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4)
                            .addComponent(cvLabel)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(canSenseLabel)
                            .addComponent(selectedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(modifiersLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(cvModifierCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(361, 361, 361))))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectedLabel)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(canSenseLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cvLabel)
                    .addComponent(cvModifierCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(modifiersLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(warningLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        if ( getBattleEvent() != null) {
//            int dindex = battleEvent.getDiceIndex(diceName, getTargetGroup() );
//            
//            String rollmode = getRollMode();
//            
//            if ( rollmode.equals(AUTO_ROLL) ) {
//                if ( getDice() == null ) {
//                    Dice autoRoll = new Dice(3);
//                    battleEvent.setDiceRoll(dindex, autoRoll);
//                }
//                else {
//                    battleEvent.setDiceRoll(dindex, getDice());
//                }
//                battleEvent.setDiceAutoRoll(dindex, true);
//            }
//            else if ( rollmode.equals(MANUAL_ROLL) ) {
//                String roll = TargetrollField.getText();
//                Dice diceRoll = null;
//                try {
//                    int value = Integer.parseInt(roll);
//                    
//                    diceRoll = new Dice(value, 0);
//                }
//                catch(NumberFormatException nfe) {
//                    // Don't do anything
//                }
//                
//                battleEvent.setDiceRoll(dindex, diceRoll);
//                battleEvent.setDiceAutoRoll(dindex, true    );
//            }
        }
    }
    
    /** Setter for property target.
     * @param target New value of property target.
     *
     */
    public void setTarget(Target target) {
        this.target = target;
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
    
        public double getCvMultiplier() {
        return cvMultiplier;
    }

    public void setCvMultiplier(double cvMultiplier) {
        this.cvMultiplier = cvMultiplier;
    }


    public PanelType getPanelType() {
        return panelType;
    }

    public void setPanelType(PanelType panelType) {
        this.panelType = panelType;
        
        Target source = battleEvent.getSource();
        
        if ( panelType == PanelType.ATTACKER_PERCEPTION_PANEL ) {
            setSenser(source);
            setSensee(target);
            
            cvModifierCombo.setModel(ocvComboModel);
            cvLabel.setText("OCV Modifier");
        }
        else{
            setSenser(target);
            setSensee(source);
            
            cvModifierCombo.setModel(dcvComboModel);
            cvLabel.setText("DCV Modifier");
        }
    }

    public Target getSenser() {
        return senser;
    }

    public void setSenser(Target senser) {
        this.senser = senser;
    }

    public Target getSensee() {
        return sensee;
    }

    public void setSensee(Target sensee) {
        this.sensee = sensee;
    }

    public List<Sense> getSenseList() {
        return senseList;
    }

    public void setSenseList(List<Sense> senseList) {
        this.senseList = senseList;
        
        STSenseListNode sn = new STSenseListNode(senser, senseList);
        sn.add(new STNullSenseNode());
        
       
        senseTree.setRoot(sn);
        
        
        
    }

    public Sense getSelectedSense() {
        return selectedSense;
    }

    public void setSelectedSense(Sense selectedSense) {
        if ( this.selectedSense != selectedSense ) {
        
            this.selectedSense = selectedSense;
        }
        
        updateCVModifier();
        ActivationInfo ai = battleEvent.getActivationInfo();
        
        //int tindex = ai.getSourceSenseIndex( getTarget() );
        
        int tindex =ai.getSourceSenseIndex(ai.getSource());
        if ( tindex != -1 ) {
            ai.setSourcesSense(tindex, selectedSense);
        }
        updateControls();
    }
    
    
    //todo persception only melee?
    protected void updateCVModifier() {
        if ( panelType == PanelType.ATTACKER_PERCEPTION_PANEL ) {
            SenseCVModifier mod = SenseCVModifier.getOCVModifier(selectedSense, sensee, battleEvent.isMeleeAttack() == false);
            
            cvModifierCombo.setSelectedItem(mod );
        }
        else {
            SenseCVModifier mod = SenseCVModifier.getDCVModifier(selectedSense, sensee, battleEvent.isMeleeAttack() == false);
            
            cvModifierCombo.setSelectedItem(mod );
        }
    }
    
    protected void updateControls() {
        if ( selectedSense == null ) {
            selectedLabel.setText("None");
            
            modifiersLabel.setText("");
        }
        else {
            selectedLabel.setText(selectedSense.getSenseName());
            String modifiers = selectedSense.getPenaltyString();
            if ( modifiers == null || modifiers.equals("") ) modifiers = "None";
            modifiersLabel.setText(modifiers);
        }
        
        updateWarning();
    }
    
    protected void updateWarning() {
        if ( panelType == PanelType.ATTACKER_PERCEPTION_PANEL ) {
            if ( selectedSense == null ) {
                warningLabel.setText(ChampionsUtilities.createHTMLString(PerceptionWarning.WARNING_ATTACKER_NO_SENSE.text));
            }
            else if ( selectedSense.isTargetableWithSense(sensee) == false ) {
                if ( selectedSense.isTargettingSense() ) {
                    warningLabel.setText(ChampionsUtilities.createHTMLString(PerceptionWarning.WARNING_ATTACKER_TARGETING_UNPERCEIVABLE.text));
                }
                else if ( selectedSense.isRangedSense() ) {
                    warningLabel.setText(ChampionsUtilities.createHTMLString(PerceptionWarning.WARNING_ATTACKER_NONTARGETING_UNPERCEIVABLE.text));
                }
                else {
                    warningLabel.setText(ChampionsUtilities.createHTMLString(PerceptionWarning.WARNING_ATTACKER_UNRANGED.text));
                }
            }
            else {
                warningLabel.setText("");
            }
        }
        else {
            
        }
    }
    
    public void valueChanged(TreeSelectionEvent e) {
        Object last = e.getPath().getLastPathComponent();
        
        if ( last instanceof STSenseNode ) {
            setSelectedSense( ((STSenseNode)last).getSense() );
        }
        else if ( last instanceof STNullSenseNode ) {
            setSelectedSense( null ); 
        }
        
    }

    public SenseCVModifier getCVModifier() {
        return (SenseCVModifier)cvModifierCombo.getSelectedItem();
    }

    public void setCVModifier(SenseCVModifier cvModifier) {
        if ( cvModifier != null ) {
            cvModifierCombo.setSelectedItem(cvModifier);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel canSenseLabel;
    private javax.swing.JLabel cvLabel;
    private javax.swing.JComboBox cvModifierCombo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel modifiersLabel;
    private javax.swing.JLabel selectedLabel;
    private champions.senseTree.STSenseTree senseTree;
    private javax.swing.JLabel warningLabel;
    // End of variables declaration//GEN-END:variables
 
}
