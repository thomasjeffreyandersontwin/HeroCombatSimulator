/*
 * EffectPanel.java
 *
 * Created on November 20, 2001, 12:29 PM
 */

package champions.attackTree;

import champions.*;
import champions.event.PADValueEvent;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.Limitation;
import champions.interfaces.PADValueListener;
import champions.powers.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import tjava.PopupListCellRenderer;
import tjava.ToggleSelectionModel;


/**
 *
 * /**
 *
 * @author  twalker
 */
public class MentalEffectPanel extends JPanel implements ActionListener, PADValueListener, ListSelectionListener, AttackTreeInputPanel, ChampionsConstants {
    Vector  diceEditorVector = new Vector();
    
    /** Holds value of property battleEvent. */
    private BattleEvent battleEvent;
    
    /** Holds value of property targetGroup. */
    private String targetGroup;
    
    /** Holds value of property targetReferenceNumber */
    private int targetReferenceNumber;
    
    //**Hold mental effect prooperty */
    private MentalEffectInfo mentalEffectInfo;
    
    /**Hold */
    private boolean adjusting = false;
    
    /** Holds value of property ability. */
    private Ability ability;
    
    
    private static MentalEffectPanel defaultPanel = null;
    
    /** Creates new form EffectPanel */
    public MentalEffectPanel() {
        initComponents();
        
        ListCellRenderer lcr = new PopupListCellRenderer("MentalEffectList.checkedIcon","MentalEffectList.uncheckedIcon");
        effectModifier.setCellRenderer( lcr );
        
        effectLevel.addActionListener( this );
        effectModifier.addListSelectionListener( this );
        effectModifier.setSelectionModel( new ToggleSelectionModel() );
        additionalModifier.setPropertyName( "Attack.GENERICMODIFIER" );
        additionalModifier.addPADValueListener( this );
        effectDescription.addActionListener( this );
        
    }
    
    static public MentalEffectPanel getDefaultPanel(BattleEvent be, String targetGroup, int targetReferenceNumber, MentalEffectInfo mei) {
        if ( defaultPanel == null ) defaultPanel = new MentalEffectPanel();
        
        defaultPanel.setBattleEvent(be);
        defaultPanel.setTargetGroup(targetGroup);
        defaultPanel.setTargetReferenceNumber(targetReferenceNumber);
        defaultPanel.setMentalEffectInfo(mei);
        defaultPanel.setAbility(be.getAbility());
        
        return defaultPanel;
    }
    
    public void setupPanel() {
        if ( battleEvent != null && targetGroup != null ) {
            
            DefaultComboBoxModel effectlevelcbm;
            int index;
            adjusting = true;
            effectlevelcbm = new DefaultComboBoxModel();
            boolean mandatoryeffectenforced = false;
            
            String level = mentalEffectInfo.getLimitationMandatoryEffectLevelDesc(battleEvent);
            effectlevelcbm.setSelectedItem(mentalEffectInfo.getMentalEffectLevelDesc());
            if ( ability.getPower() instanceof powerMindControl) {
                for (index = 0; index < powerMindControl.effectLevelOptions.length; index++) {
                    String leveldesc = powerMindControl.effectLevelOptions[index];
                    if (level != null) {
                        if (leveldesc.startsWith(level)) {
                            mentalEffectInfo.setFirstLevelDesc(leveldesc);
                            mandatoryeffectenforced = true;
                        }
                        if (mandatoryeffectenforced == true) {
                            effectlevelcbm.addElement(powerMindControl.effectLevelOptions[index]);
                        }
                    }
                    else {
                        effectlevelcbm.addElement(powerMindControl.effectLevelOptions[index]);
                    }
                }
                
                effectModifier.setModel(new ArrayListModel(powerMindControl.effectModifierOptions));
                effectLevel.setModel(effectlevelcbm);
                effectlevelcbm.setSelectedItem(powerMindControl.effectLevelOptions[0]);
            }
            if ( ability.getPower() instanceof powerMentalIllusions) {
                for (index = 0; index < powerMentalIllusions.effectLevelOptions.length; index++) {
                    effectlevelcbm.addElement(powerMentalIllusions.effectLevelOptions[index]);
                }
                effectModifier.setModel(new ArrayListModel(powerMentalIllusions.effectModifierOptions));
                effectLevel.setModel(effectlevelcbm);
                effectlevelcbm.setSelectedItem(powerMentalIllusions.effectLevelOptions[0]);
            }
            if ( ability.getPower() instanceof powerMindScan) {
                for (index = 0; index < powerMindScan.effectLevelOptions.length; index++) {
                    effectlevelcbm.addElement(powerMindScan.effectLevelOptions[index]);
                }
                effectModifier.setModel(new ArrayListModel(powerMindScan.effectModifierOptions));
                effectLevel.setModel(effectlevelcbm);
                effectlevelcbm.setSelectedItem(powerMindScan.effectLevelOptions[0]);
            }
            
             //entirety of setupupcontrols now here:
            additionalModifier.setValue(new Integer(mentalEffectInfo.getMentalEffectAdditionalModifier()));
            effectDescription.setText(mentalEffectInfo.getMentalEffectDescription());
            effectModifier.clearSelection();
            int size = effectModifier.getModel().getSize();
            for (int index3 = 0; index3 < size;index3++) {
                String item = (String)effectModifier.getModel().getElementAt(index3);
                int index2 = mentalEffectInfo.findMentalEffectModifier(item);
                if (index2 != -1) {
                    effectModifier.setSelectedIndex(index3);
                }
            }
            Limitation mandatoryeffect = new limitationMandatoryEffect();
            
            if (mentalEffectInfo.getMentalEffectLevelDesc() != null ) {
                effectlevelcbm.setSelectedItem(mentalEffectInfo.getMentalEffectLevelDesc());
            }
            
            else if (mentalEffectInfo.hasLimitationMandatoryEffect(battleEvent)) {
                level = mentalEffectInfo.getLimitationMandatoryEffectLevelDesc(battleEvent);
                effectlevelcbm.setSelectedItem(mentalEffectInfo.getFirstEffectLevelDesc());
                
            }
            
            adjusting = false;
            //setupControls();
        }
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        titledBorder1 = new javax.swing.border.TitledBorder("");
        jComboBox1 = new javax.swing.JComboBox();
        EffectLevelGroup = new javax.swing.JPanel();
        effectLevel = new javax.swing.JComboBox();
        EffectModifierGroup = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        effectModifier = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        additionalModifier = new champions.PADIntegerEditor();
        jLabel4 = new javax.swing.JLabel();
        effectDescription = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.TitledBorder(""));
        setMinimumSize(new java.awt.Dimension(450, 44));
        setPreferredSize(new java.awt.Dimension(450, 26));
        EffectLevelGroup.setLayout(new java.awt.BorderLayout());

        EffectLevelGroup.setBorder(new javax.swing.border.TitledBorder("Select Level"));
        EffectLevelGroup.setMinimumSize(new java.awt.Dimension(520, 60));
        EffectLevelGroup.setPreferredSize(new java.awt.Dimension(520, 60));
        effectLevel.setMinimumSize(new java.awt.Dimension(510, 30));
        effectLevel.setPreferredSize(new java.awt.Dimension(510, 30));
        effectLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                effectLevelActionPerformed(evt);
            }
        });

        EffectLevelGroup.add(effectLevel, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(EffectLevelGroup, gridBagConstraints);

        EffectModifierGroup.setLayout(new java.awt.BorderLayout());

        EffectModifierGroup.setBorder(new javax.swing.border.TitledBorder("Select Any Modifiers"));
        EffectModifierGroup.setMinimumSize(new java.awt.Dimension(520, 150));
        EffectModifierGroup.setPreferredSize(new java.awt.Dimension(520, 150));
        effectModifier.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane1.setViewportView(effectModifier);

        EffectModifierGroup.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(EffectModifierGroup, gridBagConstraints);

        jLabel3.setText("Additional Modifier:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel3, gridBagConstraints);

        additionalModifier.setMinimumSize(new java.awt.Dimension(100, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(additionalModifier, gridBagConstraints);

        jLabel4.setText("Effect Description:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel4, gridBagConstraints);

        effectDescription.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                effectDescriptionActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(effectDescription, gridBagConstraints);

    }//GEN-END:initComponents
    
    private void effectLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_effectLevelActionPerformed
        updateMentalEffect();
    }//GEN-LAST:event_effectLevelActionPerformed
    
    private void effectDescriptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_effectDescriptionActionPerformed
        updateMentalEffect();
    }//GEN-LAST:event_effectDescriptionActionPerformed
    
    public void showPanel(AttackTreePanel atip) {
        setupPanel();
    }
    
    public JPanel getPanel() {
        return this;
    }
    
    public void hidePanel() {
        //        mentalEffectInfo.setMentalEffectLevel(0);
        //        mentalEffectInfo.setMentalEffectLevelDesc("HELP");
        
        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel EffectLevelGroup;
    private javax.swing.JPanel EffectModifierGroup;
    private champions.PADIntegerEditor additionalModifier;
    private javax.swing.JTextField effectDescription;
    private javax.swing.JComboBox effectLevel;
    private javax.swing.JList effectModifier;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.border.TitledBorder titledBorder1;
    // End of variables declaration//GEN-END:variables
    
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
    
    
    
    /** Getter for property targetGroup.
     * @return Value of property targetGroup.
     */
    public String getTargetGroup() {
        return targetGroup;
    }
    
    /** Setter for property targetGroup.
     * @param targetGroup New value of property targetGroup.
     */
    public void setTargetGroup(String targetGroup) {
        this.targetGroup = targetGroup;
    }
    
    public boolean PADValueChanging(PADValueEvent evt) {
        
        return true;
    }
    
    
    public void PADValueChanged(PADValueEvent evt) {
        updateMentalEffect();
        return;
    }
    /** Getter for property targetReferenceNumber.
     * @return Value of property targetReferenceNumber.
     */
    public int getTargetReferenceNumber() {
        return targetReferenceNumber;
    }
    
    /** Setter for property targetReferenceNumber.
     * @param targetReferenceNumber New value of property targetReferenceNumber.
     */
    public void setTargetReferenceNumber(int targetReferenceNumber) {
        this.targetReferenceNumber = targetReferenceNumber;
    }
    
    private void updateMentalEffect() {
        if (!adjusting) {
            String mentaleffectlevel = (String)effectLevel.getSelectedItem();
            ActivationInfo ai =battleEvent.getActivationInfo();
            
            
            int i = mentalEffectInfo.convertMentalEffectLevelDesctoInt(mentaleffectlevel);
            
            //mentalEffectInfo.setMentalEffectLevel(i);
            mentalEffectInfo.setMentalEffectLevelDesc(mentaleffectlevel);
            int size = effectModifier.getModel().getSize();
            for (int index =0; index < size ; index++ ){
                if (effectModifier.isSelectedIndex(index) == true) {
                    mentalEffectInfo.addMentalEffectModifier((String)effectModifier.getModel().getElementAt(index), getModifierValue(index));
                }
                else {
                    mentalEffectInfo.removeMentalEffectModifier((String)effectModifier.getModel().getElementAt(index));
                }
                
            }
            
            mentalEffectInfo.setMentalEffectAdditionalModifier(additionalModifier.getValue().intValue());
            mentalEffectInfo.setMentalEffectDescription(effectDescription.getText());
            
            
        }
    }
    
    private int getModifierValue(int index) {
        if ( ability.getPower() instanceof powerMindControl) {
            return powerMindControl.effectModifierValueOptions[index];
        }
        if ( ability.getPower() instanceof powerMentalIllusions) {
            return 0;
        }
        if ( ability.getPower() instanceof powerMindScan) {
            return 0;
        }
        return 0;
    }
    
    public void valueChanged(ListSelectionEvent e) {
        updateMentalEffect();
    }
    
    public void actionPerformed(ActionEvent e) {
        updateMentalEffect();
    }
    
    /** Getter for property mentalEffectInfo.
     * @return Value of property mentalEffectInfo.
     *
     */
    public MentalEffectInfo getMentalEffectInfo() {
        return mentalEffectInfo;
    }
    
    /** Setter for property mentalEffectInfo.
     * @param mentalEffectInfo New value of property mentalEffectInfo.
     *
     */
    public void setMentalEffectInfo(MentalEffectInfo mentalEffectInfo) {
        if ( this.mentalEffectInfo != mentalEffectInfo ) {
            if ( this.mentalEffectInfo != null ) {
                updateMentalEffect();
            }
            
            this.mentalEffectInfo = mentalEffectInfo;
        }
    }
    
    /** Getter for property ability.
     * @return Value of property ability.
     *
     */
    public Ability getAbility() {
        return ability;
    }
    
    /** Setter for property ability.
     * @param ability New value of property ability.
     *
     */
    public void setAbility(Ability ability) {
        this.ability = ability;
    }
    
}
