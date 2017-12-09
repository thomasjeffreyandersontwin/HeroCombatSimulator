/*
 * AttackDescriptionNode.java
 *
 * Created on December 16, 2001, 11:31 AM
 */

package champions.attackTree;

import champions.CombatLevelList;
import champions.Target;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version 
 */
public class AttackCombatLevelsNode extends DefaultAttackTreeNode {
    private Target target;
    private boolean attacker;

    private CombatLevelList combatLevelList;

    /** Creates new AttackDescriptionNode */
    public AttackCombatLevelsNode(String name, Target target, boolean attacker) {
        this.name = name;
        this.target = target;
        this.attacker = attacker;
        
        icon = UIManager.getIcon("AttackTree.attackParametersIcon");
    }
    
    public boolean activateNode(boolean manualOverride) {
        setupCombatLevelList();
        
        boolean acceptActivation = false;
        if ( nodeRequiresInput() || manualOverride == true ) {
            AttackCombatLevelsPanel app = AttackCombatLevelsPanel.getDefaultPanel( getBattleEvent(), attacker, getCombatLevelList());
        
            attackTreePanel.showInputPanel(this,app);
            attackTreePanel.setInstructions("Hit Okay to Continue...");
        
            acceptActivation = true;
        }
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        boolean requiresInput = false;
        
       
            
         //   requiresInput = getBattleEvent().getSource().getBooleanProfileOption();
        
        
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

        // In case the node wasn't shown, setup the list here...
        setupCombatLevelList();
        
        return true;
    }
    
    public Target getAutoBypassTarget() {
        return battleEvent.getSource();
    }    
    
    public String getAutoBypassOption() {
        return null;
    }

    /**
     * @return the target
     */
    public Target getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(Target target) {
        this.target = target;
    }

    /**
     * @return the attacker
     */
    public boolean isAttacker() {
        return attacker;
    }

    /**
     * @param attacker the attacker to set
     */
    public void setAttacker(boolean attacker) {
        this.attacker = attacker;
    }
    
    protected CombatLevelList getCombatLevelList() {
        setupCombatLevelList();

        return combatLevelList;
    }

    protected void setupCombatLevelList() {
        combatLevelList = battleEvent.getCombatLevelList(target);
        
        if ( combatLevelList != null && combatLevelList.isModified() == true ) {
            // We need to reset all the cvlists here...
            // There is probably a better way to do this, but I don't care...
            battleEvent.getActivationInfo().resetCVLists(target);
        }
    }
}
