/*
 * AttackDescriptionNode.java
 *
 * Created on December 16, 2001, 11:31 AM
 */

package champions.attackTree;

import champions.ProfileManager;
import champions.Target;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version 
 */
public class ConfigureBattleDescriptionNode extends DefaultAttackTreeNode {

    /** Creates new AttackDescriptionNode */
    public ConfigureBattleDescriptionNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.attackParametersIcon");
    }
    
    public boolean activateNode(boolean manualOverride) {
        battleEvent.setAttackTreeDescriptionShown(true);
        
        boolean acceptActivation = false;
        if ( nodeRequiresInput() || manualOverride == true ) {
            
            String text = "Configure Battle\n\n" +
                    "The configure battle panel allows the control of all non-instant " +
                    "abilities.  At the start of battle it allow the GM to specify which " +
                    "abilities were activated prior to the start of the battle.  During " +
                    "an active battle, abilities can be both start and stopped, regardless " +
                    "of the active character.";
            
            InformationPanel app = InformationPanel.getDefaultPanel( text );
        
            attackTreePanel.showInputPanel(this,app);
            attackTreePanel.setInstructions("Hit Okay to Continue...");
        
            acceptActivation = true;
        }
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        boolean requiresInput = false;
        
        if ( battleEvent.isAlwaysShowAttackTree() ) {
            requiresInput = true;
        }
        else {
            requiresInput = ProfileManager.getDefaultProfile().getBooleanProfileOption(getAutoBypassOption());
        }
        
        return requiresInput;
    }
    
    /**
     * Builds the next child based upon the last child which was constru || node.isVisiblected.
     *
     * If all the children necessary have been built, return null, otherwise
     * return the next node.
     *
     * BuildNextChild should add that child to the tree.
     */
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        
        AttackTreeNode nextNode = null;
        
        // AttackParameter has no children...
        
        return nextNode;
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
        
        return true;
    }
    
    public Target getAutoBypassTarget() {
        return null;
    }    
    
    public String getAutoBypassOption() {
        return "SHOW_CONFIGURE_BATTLE_DESCRIPTION_PANEL";
    }
    

}
