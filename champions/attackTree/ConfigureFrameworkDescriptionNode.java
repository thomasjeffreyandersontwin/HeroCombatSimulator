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
public class ConfigureFrameworkDescriptionNode extends DefaultAttackTreeNode {

    /** Creates new AttackDescriptionNode */
    public ConfigureFrameworkDescriptionNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.attackParametersIcon");
    }
    
    public boolean activateNode(boolean manualOverride) {
        battleEvent.setAttackTreeDescriptionShown(true);
        
        boolean acceptActivation = false;
        if ( nodeRequiresInput() || manualOverride == true ) {
            
            String text = "Framework Ability Allocation\n\n" +
                    "When a framework is in explicit configuration mode, abilities have to be enabled/disable " +
                    "manual through the framework ability allocation panels.\n\nThe following panels will allow " +
                    "you to select which abilities are current enabled and make appropriate skill rolls if necessary " +
                    "to enable those abilities within the framework.  If the framework requires time to reconfigure, the " +
                    "appropriate time will also be used by the framework owner.";
            
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
