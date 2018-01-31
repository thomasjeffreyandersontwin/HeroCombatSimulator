/*
 * GenericAbilitySelectPanel.java
 *
 * Created on October 31, 2001, 11:53 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.Battle;
import champions.BattleEvent;
import champions.GenericAbilityBattleEvent;
import champions.GlobalFontSettings;
import champions.abilityTree.PADTree.PADTreeTableNode;
import champions.interfaces.ChampionsConstants;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.tree.TreePath;


/**
 *
 * @author  twalker
 */
public class GenericAbilitySelectPanel extends JPanel implements AttackTreeInputPanel, ChampionsConstants {
    /** Stores a cached GenericAbilitySelectPanel which can be reused. */
    static protected GenericAbilitySelectPanel ad = null;
    
    protected AttackTreePanel atip;
    
    /** Currently Associated Battle Event. */
    protected BattleEvent battleEvent = null;
    
    /** Creates new form GenericAbilitySelectPanel */
    public GenericAbilitySelectPanel() {
        initComponents();
     //   saveButton.setIcon( UIManager.getIcon( "Save.DefaultIcon" ) );
        openButton.setIcon( UIManager.getIcon( "Open.DefaultIcon" ) );
        
        padTreeTable.addMouseListener( new MouseAdapter() {
            /**
             * Invoked when the mouse has been clicked on a component.
             */
            public void mouseClicked(MouseEvent e) {
                Object o = null;
                if ( e.isPopupTrigger() == false ) {
                    TreePath clickPath = padTreeTable.getSelectionPath();
                    if (clickPath != null && clickPath.getLastPathComponent() instanceof PADTreeTableNode ) {
                        o = ((PADTreeTableNode)clickPath.getLastPathComponent()).getPAD();
                    }

                    if ( o instanceof Ability ) {
                        Ability a = (Ability)o;
                        if ( a.getSource() == null ) {
                            a.setSource( Battle.currentBattle.getGmTarget() );
                        }
                        if ( e.getClickCount() == 1 ) {
                            //int index = PADListListener.this.list.locationToIndex(e.getPoint());
                            setAbility((Ability)o);
                        }
                        else if ( e.getClickCount() == 2 ) {
                            setAbility((Ability)o);
                            atip.advanceNode();
                        }   
                    }
                }
            }
        });
        // instructionLabel.setText("Set Attack Parameters");
        
        setupActions();
        
      
    }
    
    public void setupActions() {
        
    }
    
    static public GenericAbilitySelectPanel getGenericAbilitySelectPanel(BattleEvent be) {
        if ( ad == null ) ad = new GenericAbilitySelectPanel();
        
        ad.setBattleEvent(be);
               
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
    }
    
 
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        infoGroup = new javax.swing.JPanel();
        infoLabel = new javax.swing.JLabel();
        openButton = new javax.swing.JButton();
        selectGroup = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        padTreeTable = new champions.abilityTree.PADTree.PADTreeTable();
        warningLabel = new javax.swing.JLabel();

        infoGroup.setBorder(javax.swing.BorderFactory.createTitledBorder("Currently Selected Power/Skill"));

        infoLabel.setText(" ");

        openButton.setMargin(new java.awt.Insets(1, 1, 1, 1));
        openButton.setPreferredSize(new java.awt.Dimension(32, 32));
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout infoGroupLayout = new javax.swing.GroupLayout(infoGroup);
        infoGroup.setLayout(infoGroupLayout);
        infoGroupLayout.setHorizontalGroup(
            infoGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, infoGroupLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(infoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addComponent(openButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );
        infoGroupLayout.setVerticalGroup(
            infoGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoGroupLayout.createSequentialGroup()
                .addGroup(infoGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(openButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(infoLabel))
                .addGap(11, 11, 11))
        );

        selectGroup.setLayout(new java.awt.BorderLayout());
        selectGroup.add(jScrollPane1, java.awt.BorderLayout.NORTH);

        jScrollPane2.setViewportView(padTreeTable);

        selectGroup.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        warningLabel.setFont(new java.awt.Font("Dialog", 0, (int) (24 * GlobalFontSettings.SizeMagnification)));;
        warningLabel.setForeground(java.awt.Color.red);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(selectGroup, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(infoGroup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(warningLabel, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(warningLabel)
                .addGap(5, 5, 5)
                .addComponent(infoGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectGroup, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed
        // Add your handling code here:
        Ability a = (Ability)Ability.open( new String[] { "abt"}, "Ability", Ability.class );
        if ( a != null ) {
            setAbility(a);
        }
    }//GEN-LAST:event_openButtonActionPerformed
                                
    public void showPanel(AttackTreePanel atip) {
        this.atip = atip;
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
        Ability ability = battleEvent.getAbility();
        
        if ( ability != null ) {
            infoLabel.setText( ability.getName() + ": " + ability.getDescription() + ", Source: " + ability.getSource() );
        }
        else {
            infoLabel.setText("Unselected");
        }
        
        clearWarning();
    }
    
    /** Getter for property ability.
     * @return Value of property ability.
     *
     */
    public Ability getAbility() {
        if ( battleEvent != null ) {
            return battleEvent.getAbility();
        }
        else {
            return null;
        }
    }
    
    /** Setter for property ability.
     * @param ability New value of property ability.
     *
     */
    public void setAbility(Ability ability) {
        if ( battleEvent != null ) {
            battleEvent.setAbility( ability );
            if ( battleEvent instanceof GenericAbilityBattleEvent ) {
                ((GenericAbilityBattleEvent)battleEvent).setConfigured(false);
            }
            
            if ( ability == null ) {
                infoLabel.setText("Unselected...");
            }
            else {
                infoLabel.setText( ability.getName() + ": " + ability.getDescription() + ", Source: " + ability.getSource() );
            }
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel infoGroup;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton openButton;
    private champions.abilityTree.PADTree.PADTreeTable padTreeTable;
    private javax.swing.JPanel selectGroup;
    private javax.swing.JLabel warningLabel;
    // End of variables declaration//GEN-END:variables
    
}
