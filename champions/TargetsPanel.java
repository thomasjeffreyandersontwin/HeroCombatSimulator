/*
 * TargetsPanel.java
 *
 * Created on January 27, 2008, 2:00 PM
 */

package champions;

import champions.abilityTree2.ATTargetFilter;
import champions.filters.AndFilter;
import champions.filters.NotFilter;
import champions.filters.OrFilter;
import champions.filters.TargetHasEffectFilter;
import champions.filters.TargetIsConsciousFilter;
import champions.filters.TargetIsDeadFilter;
import tjava.Filter;
import champions.powers.effectDying;

/**
 *
 * @author  twalker
 */
public class TargetsPanel extends javax.swing.JPanel {
    
    /** Creates new form TargetsPanel */
    public TargetsPanel() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jScrollPane2 = new javax.swing.JScrollPane();
        aTAllTargetsTree1 = new champions.abilityTree2.ATAllTargetsTree();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        deadCheckbox = new javax.swing.JCheckBox();
        unconsciousCheckbox = new javax.swing.JCheckBox();

        setLayout(new java.awt.BorderLayout());

        aTAllTargetsTree1.setHighlightEnabled(true);
        aTAllTargetsTree1.setPopupFilterOverridesNodeFilter(true);
        aTAllTargetsTree1.setShowsRootHandles(true);
        jScrollPane2.setViewportView(aTAllTargetsTree1);

        add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 2));

        jLabel1.setFont(new java.awt.Font(GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        jLabel1.setText("Filter:");
        jPanel1.add(jLabel1);

        deadCheckbox.setFont(new java.awt.Font(GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        deadCheckbox.setText("Dead");
        deadCheckbox.setToolTipText("When checked, dead character will be filtered from the target panel.");
        deadCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        deadCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        deadCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deadCheckboxActionPerformed(evt);
            }
        });

        jPanel1.add(deadCheckbox);

        unconsciousCheckbox.setFont(new java.awt.Font(GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        unconsciousCheckbox.setText("Unconscious");
        unconsciousCheckbox.setToolTipText("When checked, unconscious characters will be filter from the target panel.  However, dying unconscious characters will still be displayed.");
        unconsciousCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        unconsciousCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        unconsciousCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unconsciousCheckboxActionPerformed(evt);
            }
        });

        jPanel1.add(unconsciousCheckbox);

        add(jPanel1, java.awt.BorderLayout.SOUTH);

    }// </editor-fold>//GEN-END:initComponents

    private void unconsciousCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unconsciousCheckboxActionPerformed

        updateFilter();
    }//GEN-LAST:event_unconsciousCheckboxActionPerformed

    private void deadCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deadCheckboxActionPerformed

        updateFilter();
    }//GEN-LAST:event_deadCheckboxActionPerformed
    
    protected void updateFilter() {
        Filter<Target> targetFilter = null;
        
        if ( deadCheckbox.isSelected() ) {
            targetFilter = new NotFilter<Target>( new TargetIsDeadFilter());
        }
        
        if ( unconsciousCheckbox.isSelected() ) {
            Filter<Target> uf = new OrFilter<Target>( new TargetIsConsciousFilter(), new TargetHasEffectFilter( effectDying.class ));
            if ( targetFilter == null ) {
                targetFilter = uf;
            }
            else {
                targetFilter = new AndFilter<Target>(targetFilter, uf);
            }
        }
        
        Filter<Object> f = null;
        if ( targetFilter != null ) {
            f = new ATTargetFilter(targetFilter);
        }
        
        aTAllTargetsTree1.setNodeFilter(f);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private champions.abilityTree2.ATAllTargetsTree aTAllTargetsTree1;
    private javax.swing.JCheckBox deadCheckbox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JCheckBox unconsciousCheckbox;
    // End of variables declaration//GEN-END:variables
    
}
