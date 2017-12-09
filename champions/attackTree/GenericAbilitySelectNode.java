/*
 * GenericAbilitySelectNode.java
 *
 * Created on October 30, 2001, 12:14 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.ActivationInfo;
import champions.BattleEngine;
import champions.Target;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author  twalker
 * @version 
 */
public class GenericAbilitySelectNode extends DefaultAttackTreeNode {

    /**
     * Stores Variable indicating this is the first time this node has been selected.
     * 
     * If it is the first time, it will always show the attack parameters
     */
    private boolean firstPass = true;
    
    /**
     * Indicates a change occurred in the GenericAbilitySelectPanel while this node was 
     * activated.
     */
    private boolean changeOccurred = false;
    
    /** Creates new TestAttackTreeNode */
    public GenericAbilitySelectNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.selectPowerIcon");
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        if ( firstPass == true ) {
            firstPass = false;
        }
        
        Ability ability = battleEvent.getAbility();
        
        if ( nodeRequiresInput() || manualOverride == true ) {
            GenericAbilitySelectPanel app = GenericAbilitySelectPanel.getGenericAbilitySelectPanel( getBattleEvent() );
            
            
            attackTreePanel.showInputPanel(this,app);
            if ( ability == null ) {
                attackTreePanel.setInstructions("Select Power/Skill...");
            }
            else {
                attackTreePanel.setInstructions("Change Power/Skill...");
            }
        
            changeOccurred = false;
            
            acceptActivation = true;
        }
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        boolean requiresInput = false;
        
        Ability ability = battleEvent.getAbility();
        
        requiresInput = (ability == null);
        
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
        firstPass = false;
        
        Ability ability = battleEvent.getAbility();
        
        return (ability != null);
    }

    
    public String getAutoBypassOption() {
        return "SHOW_SELECT_POWER_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return getBattleEvent().getSource();
    }
    

}
