/*
 * ObstructionNode.java
 *
 * Created on January 6, 2002, 12:05 PM
 */

package champions.attackTree;

import champions.*;
import javax.swing.UIManager;


/**
 *
 * @author  twalker
 * @version 
 */
public class ThrowParameterNode extends DefaultAttackTreeNode {

    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    /** Store Target Selected for this node */
    protected Target target;
    
    /** Creates new DefensesNode */
    public ThrowParameterNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.throwParameterIcon");
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        ActivationInfo ai = getBattleEvent().getActivationInfo();
        
        target = (Target)ai.getValue("ActivationInfo.THROWNOBJECT");
        
        if ( target == null ) {
            target = (Target)getBattleEvent().getValue("BattleEvent.THROWNOBJECT");
            if ( target != null ) ai.add("ActivationInfo.THROWNOBJECT", target, true);
        }
        
        String aoe = ai.getStringValue("ActivationInfo.THROWISAOE");
        if ( target != null && aoe == null ) {
            ai.add("ActivationInfo.THROWISAOE", (target.getCurrentHeight() > 0.5)?"TRUE":"FALSE",true);
        }
          
        if ( nodeRequiresInput() || manualOverride ) {
        
           ThrowParameterPanel op = ThrowParameterPanel.getDefaultPanel(battleEvent);
           attackTreePanel.showInputPanel(this,op);
           attackTreePanel.setInstructions("Configure Thrown Object Parameters");
            
           acceptActivation = true;
        }
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    public boolean nodeRequiresInput() {
        return target == null || getAutoBypassTarget().getBooleanProfileOption(getAutoBypassOption());
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
        ActivationInfo ai = battleEvent.getActivationInfo();
        ai.add("ActivationInfo.THROWNOBJECT", target);
        
        return true;
    }
    
    public void setThrownObject(Target target) {
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
    
    public Target getThrownObject() {
        return this.target;
    }
    
    public void destroy() {
        setThrownObject(null);
        
        super.destroy();
    }
    
    public void buildChildren() {
        // Build a ToHit node and add it
     //   ToHitNode thn = new ToHitNode("ToHit");
     //   addChild(thn);
     //   thn.setThrownObject(getThrownObject()); // Do this afterwards, since the BE would be set before the addChild.

    }
    
        
    public String getAutoBypassOption() {
        return "SHOW_THROWN_PARAMETER_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return battleEvent.getSource();
    }

}
