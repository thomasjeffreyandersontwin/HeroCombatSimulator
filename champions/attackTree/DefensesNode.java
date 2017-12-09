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
public class DefensesNode extends DefaultAttackTreeNode {

    /** Store Target Selected for this node */
    protected Target target;
    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    /** Creates new DefensesNode */
    public DefensesNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.defensesIcon");
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        if ( nodeRequiresInput() || manualOverride ) {
            ActivationInfo ai = getBattleEvent().getActivationInfo();
            
            int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup());
            Target target = ai.getTarget(tindex);
            
            DefenseList dl = ai.getDefenseList(targetReferenceNumber, getTargetGroup());
            DefensesPanel app = DefensesPanel.getDefaultPanel(target, dl );
        
            attackTreePanel.showInputPanel(this,app);
            attackTreePanel.setInstructions("Configure Defenses for " + getTarget().getName() + ".");
            
            acceptActivation = true;
        }
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        boolean requiresInput = false;

        return getAutoBypassTarget().getBooleanProfileOption( getAutoBypassOption());
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
        return true;
    }
    
    public void setTarget(Target target) {
        if ( this.target != target ) {
            ActivationInfo ai = getBattleEvent().getActivationInfo();
            if ( this.target != null ) {
                // This is actually an error, since these nodes should be created for one specific target
            }
            
            this.target = target;
            
            if ( this.target != null ) {
                buildChildren();
            }
        }
    }
    
    public Target getTarget() {
        return this.target;
    }
    
    public void destroy() {
        setTarget(null);
        
        super.destroy();
    }
    
    public void buildChildren() {
        // Build a ToHit node and add it
     //   ToHitNode thn = new ToHitNode("ToHit");
     //   addChild(thn);
     //   thn.setTarget(getTarget()); // Do this afterwards, since the BE would be set before the addChild.

    }
    
        
    public String getAutoBypassOption() {
        return "SHOW_SELECTABLE_DEFENSES_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return getTarget();
    }

    /** Getter for property targetReferenceNumber.
     * @return Value of property targetReferenceNumber.
     */
    public int getTargetReferenceNumber() {
        return targetReferenceNumber;
    }
    
    /** Setter for property targetReferenceNumber.
     * @param targetReferenceNumber New value of property targetReferenceNumber.
     */
    public void setTargetReferenceNumber(int targetReferenceNumber) {
        this.targetReferenceNumber = targetReferenceNumber;
    }
    
}
