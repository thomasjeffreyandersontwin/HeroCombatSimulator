/*
 * EscapeNode.java
 *
 * Created on June 5, 2002, 3:27 PM
 */

package champions.attackTree;

import champions.ActivationInfo;
import champions.BattleEvent;
import champions.Dice;
import champions.Target;
import javax.swing.UIManager;

/**
 *
 * @author  Trevor Walker
 */
public class NNDDefenseNode extends DefaultAttackTreeNode {
    
    private int targetReferenceNumber;
    
    private boolean firstPass = true;
    
    private Target target;
    
/** Creates new EscapeNode */ 
    public NNDDefenseNode(String name, int targetReferenceNumber) {
        this.name = name;
        this.targetReferenceNumber = targetReferenceNumber;
        icon = UIManager.getIcon("AttackTree.defensesIcon");
    }
    
    /**
     * Activates the node.
     *
     * The method is called when this node is being activated.  Based on
     * the information the node current has available, it can choose to
     * accept the activation or reject it.
     *
     * If the node rejects the activation, the model will call the appropriate
     * methods to advance out of the node.
     *
     * The manualOverride boolean indicates that the user click on this node
     * specifically.  Even if this node has all the information to continue processing
     * without user input, it should accept activation so the user can make changes.
     *
     * This method should be overriden by children.
     */
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        prepareBattleEvent();
        
        if ( manualOverride || nodeRequiresInput() ) {
            
            ActivationInfo ai = battleEvent.getActivationInfo();
            int targetIndex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup());
            
            NNDDefensePanel ep = NNDDefensePanel.getDefaultPanel(battleEvent, targetIndex);
            
            attackTreePanel.showInputPanel(this,ep);
            attackTreePanel.setInstructions("Does target have NND Defense?");
            
            acceptActivation = true;
        }
     
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
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
        int targetIndex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup());
        boolean hit  = ai.getTargetHit( targetIndex );
        
        if ( hit ) {
            firstPass = false;
            
            if ( ai.isTargetHasNNDDefenseSet(targetIndex) == false) {
                // Lets default the value if the panel didn't get shown for some reason...
                ai.setTargetHasNNDDefense(targetIndex, false);
            }
        }
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println( "Node " + name + " exited.");
        return true;
    }

    
    /** Determines if this node needs to show an input panel.
     *
     */
    protected  boolean nodeRequiresInput() {
        ActivationInfo ai = battleEvent.getActivationInfo();
        boolean hit = ai.getTargetHit( ai.getTargetIndex(targetReferenceNumber, getTargetGroup()));
        
        return hit && (firstPass || getAutoBypassTarget().getBooleanProfileOption(getAutoBypassOption()));
    }
    
    public String getAutoBypassOption() {
        return "SHOW_NND_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return target;
    }
    
    private void prepareBattleEvent() {
        ActivationInfo ai = battleEvent.getActivationInfo();
        int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup());
        Target t = ai.getTarget(tindex);
        
        setTarget(t);
    }
    
  
    public int getTargetReferenceNumber() {
        return targetReferenceNumber;
    }

    public void setTargetReferenceNumber(int targetIndex) {
        this.targetReferenceNumber = targetIndex;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }
    
}
