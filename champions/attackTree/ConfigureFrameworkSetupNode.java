/*
 * GenericAbilitySelectNode.java
 *
 * Created on October 30, 2001, 12:14 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.ConfigureFrameworkBattleEvent;
import champions.SkillBasedSkillRollInfo;
import champions.SkillRollInfo;
import champions.Target;
import champions.VariablePointPoolAbilityConfiguration;
import champions.genericModifiers.SkillModifierList;
import champions.interfaces.Framework;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version 
 */
public class ConfigureFrameworkSetupNode extends DefaultAttackTreeNode {

    /** Creates new TestAttackTreeNode */
    public ConfigureFrameworkSetupNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("Framework.DefaultIcon");
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        if ( nodeRequiresInput() || manualOverride == true ) {
            
            ConfigureFrameworkBattleEvent cfbe = (ConfigureFrameworkBattleEvent)getBattleEvent();
            Framework framework = cfbe.getFramework();
            Target source = cfbe.getSource();

            ConfigureFrameworkSetupPanel app = ConfigureFrameworkSetupPanel.getGenericAbilityConfigurePanel(source, framework);

            attackTreePanel.showInputPanel(this, app);
            attackTreePanel.setInstructions("Configure abilities to start/stop...");
            
            acceptActivation = true;
        } 
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        boolean requiresInput = false;
        
        requiresInput = true;
            
        
        return requiresInput;
    }
    
    /** 
     * Causes node to process an advance and perform any work that it needs to do.
     *
     * This is a method introduced by DefaultAttackTreeNode.  DefaultAttackTreeNode
     * delegates to this method if advanceNode node is called and this is the active
     * node.  This method should do all the work of advanceNode whenever it can, since
     * the next node processing and buildNextChild methods depend on the existing 
     * DefaultAttackTreeNode advanceNode method.
     *
     * Returns true if it is okay to leave the node, false if the node should
     * be reactivated to gather more information.
     */
    public boolean processAdvance() {
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " exited.");

        // We want to set the pending configuration and skill roll if necessary...
        Framework framework = getBattleEvent().getFramework();
        VariablePointPoolAbilityConfiguration config = framework.getConfiguration();

        Ability skill = framework.getConfigurationSkill();
        if ( skill != null ) {

            SkillBasedSkillRollInfo sri = (SkillBasedSkillRollInfo)battleEvent.getSkillRollInfo("FrameworkSkillRoll", getTargetGroup());

            if ( sri == null ) {
                sri = new SkillBasedSkillRollInfo(battleEvent.getSource(), skill);
                battleEvent.addSkillRoll("FrameworkSkillRoll", getTargetGroup(), sri);
                getBattleEvent().setConfigurationSkillRoll(sri);
            }

            sri.setAdjustment( config.getProposedSkillRollAdjustment() );
        }
        
        return true;
    }

    
    public String getAutoBypassOption() {
        return null;
    }
    
    public Target getAutoBypassTarget() {
        return null;
    }
    
    public ConfigureFrameworkBattleEvent getBattleEvent() {
        return (ConfigureFrameworkBattleEvent)battleEvent;
    }
}
