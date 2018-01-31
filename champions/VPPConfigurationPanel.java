/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * VPPConfigurationPanel.java
 *
 * Created on Dec 3, 2008, 12:08:25 AM
 */
package champions;

import champions.interfaces.Framework;
import java.awt.Color;
import java.awt.Event;

/**
 *
 * @author twalker
 */
public class VPPConfigurationPanel extends javax.swing.JPanel implements FrameworkConfigurationListener {

    protected Framework framework;
    protected Color originalColor = null;
    protected Color overusedColor = Color.RED;

    /** Creates new form VPPConfigurationPanel */
    public VPPConfigurationPanel() {
        initComponents();
    }

    /** Creates new form VPPConfigurationEditor */
    public VPPConfigurationPanel(Target target, Framework framework) {
        initComponents();

        setup(target, framework);
    }

    public void setup(Target target, Framework framework) {

        if (this.framework != null) {
            this.framework.getConfiguration().removeConfigurationListener(this);
        }

        this.framework = framework;
        atTree.setup(framework, target);

        update();

        if (this.framework != null) {
            this.framework.getConfiguration().addConfigurationListener(this);
        }




    }

    private void update() {

        String nameString = "";
        String currentString = "";
        String proposedString = "";
        String skillRollString = "";

        if (currentlyConfigurationLabel.getForeground() != overusedColor) {
            originalColor = currentlyConfigurationLabel.getForeground();
        }

        Color currentColor = originalColor;
        Color proposedColor = originalColor;

        if (framework != null) {
            VariablePointPoolAbilityConfiguration configuration = framework.getConfiguration();

            nameString = framework.getFrameworkAbility().getName();

            int poolSize = framework.getFrameworkPoolSize();
            int currentAP = configuration.getCurrentRealPointCost();
            int availableAP = poolSize - currentAP;

            if (availableAP >= 0) {
                currentString = currentAP + " of " + poolSize + " configured.  " + availableAP + " remaining.";
            } else {
                currentString = currentAP + " of " + poolSize + " configured.  " + (-1 * availableAP) + " overused.";
                currentColor = overusedColor;
            }

            int proposedAP = configuration.getProposedRealPointCost();
            int propsoedAvailable = poolSize - proposedAP;

            if (propsoedAvailable >= 0) {
                proposedString = proposedAP + " of " + poolSize + " configured.  " + propsoedAvailable + " remaining.";
            } else {
                proposedString = proposedAP + " of " + poolSize + " configured.  " + (-1 * propsoedAvailable) + " overused.";
                proposedColor = overusedColor;
            }

            int enableAbilityCost = configuration.getProposedActivePointEnableCost();



            skillRollString = ChampionsUtilities.toSignedString((int) Math.round(enableAbilityCost / -10.0)) + "  (" + enableAbilityCost + " active points attempting to be enabled.)";
        }

        nameLabel.setText(nameString);
        currentlyConfigurationLabel.setText(currentString);
        currentlyConfigurationLabel.setForeground(currentColor);
        proposedConfigurationLabel.setText(proposedString);
        proposedConfigurationLabel.setForeground(proposedColor);
        skillRollLabel.setText(skillRollString);
    }

    public void configurationChanged(Event event) {
        update();
    }

    public void applyActions() {
        atTree.applyActions();
    }

    public void cancelActions() {
        atTree.cancelActions();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        atTree = new champions.abilityTree2.ATVPPConfigurationTree();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        currentlyConfigurationLabel = new javax.swing.JLabel();
        proposedConfigurationLabel = new javax.swing.JLabel();
        skillRollLabel = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();

        jScrollPane1.setViewportView(atTree);

        jLabel1.setFont(new java.awt.Font(GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        jLabel1.setText("Framework Name:");

        jLabel3.setFont(new java.awt.Font(GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        jLabel3.setText("Currently Configuration");

        jLabel5.setFont(new java.awt.Font(GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        jLabel5.setText("Proposed Configuration");

        jLabel7.setFont(new java.awt.Font(GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        jLabel7.setText("Skill Roll Difficulty");

        currentlyConfigurationLabel.setFont(new java.awt.Font(GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        currentlyConfigurationLabel.setText("jLabel8");

        proposedConfigurationLabel.setFont(new java.awt.Font(GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        proposedConfigurationLabel.setText("jLabel8");

        skillRollLabel.setFont(new java.awt.Font(GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        skillRollLabel.setText("jLabel8");

        nameLabel.setFont(new java.awt.Font(GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        nameLabel.setText("jLabel8");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(currentlyConfigurationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                    .addComponent(proposedConfigurationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                    .addComponent(skillRollLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                    .addComponent(nameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(nameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(currentlyConfigurationLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(proposedConfigurationLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(skillRollLabel))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private champions.abilityTree2.ATVPPConfigurationTree atTree;
    private javax.swing.JLabel currentlyConfigurationLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel proposedConfigurationLabel;
    private javax.swing.JLabel skillRollLabel;
    // End of variables declaration//GEN-END:variables
}
