/*
 * DefensesNode.java
 *
 * Created on November 21, 2001, 9:32 PM
 */

package champions.attackTree;

import champions.ActivationInfo;
import champions.DefenseList;
import champions.Target;
import javax.swing.UIManager;


/**
 *
 * @author  twalker
 * @version 
 */
public class ExplosionDistanceNode extends DefaultAttackTreeNode {

    protected boolean first = true;
    
    /** Creates new DefensesNode */
    public ExplosionDistanceNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.defensesIcon");
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        if ( nodeRequiresInput() || manualOverride ) {
            
            ExplosionDistancePanel edp = ExplosionDistancePanel.getDefaultPanel(battleEvent, getTargetGroup());
            attackTreePanel.showInputPanel(this,edp);
            attackTreePanel.setInstructions("Configure distance from center of explosion for targets...");
            
            acceptActivation = true;
        }
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        boolean requiresInput = false;

        return first && getAutoBypassTarget().getBooleanProfileOption( getAutoBypassOption());
    }
    
    
    
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        // This node build everything upfront, when it's target is first set.
        return null;
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
        first = false;
        
        return true;
    }
    

    
    public void destroy() {
        
        
        super.destroy();
    }

    
        
    public String getAutoBypassOption() {
        return "SHOW_EXPLOSION_DISTANCE_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return battleEvent.getSource();
    }
    
}
