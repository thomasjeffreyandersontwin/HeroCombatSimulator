/*
 * EffectPanel.java
 *
 * Created on November 20, 2001, 12:29 PM
 */

package champions.attackTree;

import champions.BattleEvent;
import champions.Dice;
import champions.PADDiceValueEditor;
import champions.PADLayout;
import champions.abilityTree2.ATDCInfoModel;
import champions.abilityTree2.ATDCInfoRoot;
import champions.abilityTree2.ATNode;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.IndexIterator;
import java.util.Vector;
import javax.swing.JPanel;

/**
 *
 * @author  twalker
 */
public class EffectPanel extends JPanel implements AttackTreeInputPanel, ChampionsConstants {
    Vector  diceEditorVector = new Vector();  
    
    /** Holds value of property battleEvent. */
    private BattleEvent battleEvent;

    /** Holds value of property targetGroup. */
    private String targetGroup;
    
    public static EffectPanel defaultPanel = null;
    
    private ATDCInfoRoot treeRoot;
    
    /** Creates new form EffectPanel */
    public EffectPanel() {
        initComponents();
        diceEditorGroup.setLayout(new PADLayout());
        
        treeRoot = new ATDCInfoRoot();
        aTTree1.setTreeTableModel( new ATDCInfoModel(treeRoot) );
        defaultPanel=this;
    }
    
    public void setupPanel() {
        if ( battleEvent != null && targetGroup != null ) {
            IndexIterator ii = battleEvent.getDiceIterator(targetGroup);
            
            int vcount = diceEditorVector.size();
            int vindex = 0;
            int dindex;
            
            String name, description, size, type, stunLabel, bodyLabel;
            Dice dice;
            String auto;
            
            PADDiceValueEditor editor;
            
            // Clear the existing dice editors from the panel
            diceEditorGroup.removeAll();
            
            while ( ii.hasNext() ) {
                dindex = ii.nextIndex();
                name = battleEvent.getIndexedStringValue(dindex, "Die", "NAME");
                description = battleEvent.getIndexedStringValue(dindex, "Die", "DESCRIPTION");
                size = battleEvent.getIndexedStringValue(dindex, "Die", "SIZE");
                dice = battleEvent.getIndexedDiceValue(dindex, "Die", "ROLL");
                
                auto = battleEvent.getIndexedStringValue(dindex, "Die", "AUTOROLL");
                type = battleEvent.getIndexedStringValue(dindex, "Die", "TYPE");
                stunLabel = battleEvent.getIndexedStringValue(dindex, "Die", "STUNLABEL");
                bodyLabel = battleEvent.getIndexedStringValue(dindex, "Die", "BODYLABEL");
                
                boolean isAuto = (auto != null) ? (auto.equals("TRUE")) : true;
                
                if ( vindex < vcount ) {
                    editor = (PADDiceValueEditor)diceEditorVector.get(vindex);
                }
                else {
                    editor = new PADDiceValueEditor();
                    diceEditorVector.add(editor);
                }
                
                editor.setDescription(description);
                editor.setDiceSize(size);
                editor.setAutoroll( isAuto );
                
                if ( isAuto == true && dice != null && dice.checkSize(size) == false ) {
                    editor.setDice(null);
                }
                else {
             //   	dice.setD6(22);
              //  	size="22";
                	 editor.setDiceSize(size);
                    editor.setDice(dice);
                }
                
                if ( type != null ) {
                    editor.setDiceType(type);
                }
                else {
                    editor.setDiceType(STUN_AND_BODY);
                }
                
                if ( stunLabel != null ) {
                    editor.setStunLabel(stunLabel);
                }
                else {
                    editor.setStunLabel("Stun");
                }
                
                if ( bodyLabel != null ) {
                    editor.setBodyLabel(bodyLabel);
                }
                else {
                    editor.setBodyLabel("Body");
                }
                
                diceEditorGroup.add(editor);
                
                vindex ++;
            }
            diceEditorGroup.revalidate();
            diceEditorGroup.repaint();
        }
    }
    
    public void copyDiceToBE() {
        if ( battleEvent != null && targetGroup != null ) {
            IndexIterator ii = battleEvent.getDiceIterator(targetGroup);
            
            int vindex = 0;
            int dindex;
            
            Dice dice;
            boolean auto;
            
            PADDiceValueEditor editor;
           
            while ( ii.hasNext() ) {
                dindex = ii.nextIndex();
                
                editor = (PADDiceValueEditor)diceEditorVector.get(vindex);


                dice = editor.getDice();
                auto = editor.isAutoroll();
                
               
                battleEvent.setDiceRoll(dindex, dice);
                battleEvent.setDiceAutoRoll(dindex, auto);
                
                vindex ++;
            }
            diceEditorGroup.revalidate();
            diceEditorGroup.repaint();
        }
    }
    
    static public EffectPanel getDefaultPanel(BattleEvent be, String targetGroup) {
        if ( defaultPanel == null ) defaultPanel = new EffectPanel();
        
        
        defaultPanel.setTargetGroup(targetGroup);
        defaultPanel.setBattleEvent(be);
        
        return defaultPanel;
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        dcGroup = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        aTTree1 = new champions.abilityTree2.ATTree();
        diceGroup = new javax.swing.JPanel();
        diceEditorScroll = new javax.swing.JScrollPane();
        diceEditorGroup = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        dcGroup.setLayout(new java.awt.BorderLayout());

        dcGroup.setBorder(javax.swing.BorderFactory.createTitledBorder("Damage Class Calculation"));
        jScrollPane1.setViewportView(aTTree1);

        dcGroup.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(dcGroup, gridBagConstraints);

        diceGroup.setLayout(new java.awt.BorderLayout());

        diceGroup.setBorder(javax.swing.BorderFactory.createTitledBorder("Dice Rolls"));
        diceEditorGroup.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 0, 0));
        diceEditorScroll.setViewportView(diceEditorGroup);

        diceGroup.add(diceEditorScroll, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(diceGroup, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    public void showPanel(AttackTreePanel atip) {
        setupPanel();
    }    

    public JPanel getPanel() {
        return this;
        
    }    

    public void hidePanel() {
        copyDiceToBE();
        
        
        battleEvent = null;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private champions.abilityTree2.ATTree aTTree1;
    private javax.swing.JPanel dcGroup;
    private javax.swing.JPanel diceEditorGroup;
    private javax.swing.JScrollPane diceEditorScroll;
    private javax.swing.JPanel diceGroup;
    private javax.swing.JScrollPane jScrollPane1;
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
        if ( this.battleEvent != battleEvent ) {
            this.battleEvent = battleEvent;
            
            if ( battleEvent != null ) {
                int dindex = battleEvent.getDiceIndex("DamageDie", targetGroup);
                if ( dindex == -1 ) {
                    dcGroup.setVisible(false);
                    revalidate();
                    repaint();
                }
                else {
                    if ( treeRoot != null ) treeRoot.setBattleEvent(battleEvent);
                    dcGroup.setVisible(true);
                    revalidate();
                    repaint();
                }
            }
        }
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
    

    
}
