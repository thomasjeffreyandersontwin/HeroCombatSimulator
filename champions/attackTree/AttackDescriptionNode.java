/*
 * AttackDescriptionNode.java
 *
 * Created on December 16, 2001, 11:31 AM
 */

package champions.attackTree;

import champions.BattleEvent;
import champions.Target;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version 
 */
public class AttackDescriptionNode extends DefaultAttackTreeNode {

    /** Creates new AttackDescriptionNode */
    public AttackDescriptionNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.attackParametersIcon");
    }
    
    public boolean activateNode(boolean manualOverride) {
        battleEvent.setAttackTreeDescriptionShown(true);
        
        boolean acceptActivation = false;
        if ( nodeRequiresInput() || manualOverride == true ) {
            AttackDescriptionPanel app = AttackDescriptionPanel.getDefaultPanel( getBattleEvent() );
        
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
            String profileOption;
            boolean continuing = battleEvent.getActivationInfo().isContinuing();
            boolean attacking = battleEvent.getAbility().isAttack();
            boolean delayed = (battleEvent.getType() == BattleEvent.DELAYED_ACTIVATE);
            if ( continuing || delayed ) {
                if ( attacking ) {
                    profileOption = "SHOW_CONTINUING_ATTACK_DESCRIPTION_PANEL";
                }
                else {
                    profileOption = "SHOW_CONTINUING_NONATTACK_DESCRIPTION_PANEL";
                }
            }
            else {
                if ( attacking ) {
                    profileOption = "SHOW_ACTIVATING_ATTACK_DESCRIPTION_PANEL";
                }
                else {
                    profileOption = "SHOW_ACTIVATING_NONATTACK_DESCRIPTION_PANEL";
                }
            }
                
            
            requiresInput = getBattleEvent().getSource().getBooleanProfileOption(profileOption);
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
        return battleEvent.getSource();
    }    
    
    public String getAutoBypassOption() {
        if ( battleEvent.getActivationInfo().isContinuing() || battleEvent.getType() == BattleEvent.DELAYED_ACTIVATE ) {
            return "SHOW_CONTINUING_ATTACK_DESCRIPTION_PANEL";
        }
        else {
            return "SHOW_ACTIVATING_ATTACK_DESCRIPTION_PANEL";
        }
    }
    

}
