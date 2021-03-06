/*
 * AdjAttackTreeNode.java
 *
 * Created on March 6, 2002, 7:59 PM
 */

package champions.adjustmentPowers.adjustmentTree;

import champions.*;
import champions.adjustmentPowers.AdjustmentPower;
import champions.attackTree.*;
import champions.powers.*;
import java.util.ArrayList;

import javax.swing.*;

/**
 *
 * @author  Trevor Walker
 * @version 
 */
public class DrainFromAttackTreeNode extends DefaultAttackTreeNode {

    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    /** Holds value of property target. */
    private Target target;
    
    /** Holds value of property adjustmentList. */
    private AdjustmentList adjustmentList;
    
    /** Holds value of property adjustables. */
    private ArrayList adjustables;
    
    /** Creates new AdjAttackTreeNode */
    public DrainFromAttackTreeNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.attackParametersIcon");
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        ActivationInfo ai = battleEvent.getActivationInfo();
        int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup());
        target = ai.getTarget(tindex);
        
        // Setup the AdjustmentList
        AdjustmentList targetAL = (AdjustmentList)ai.getIndexedValue(tindex, "Target", "DRAINFROMLIST");
        
        if ( targetAL == null ) {
            effectDrainTracker eat = powerDrain.findDrainTracker(ai.getSource(), ai.getAbility());
            if ( eat == null ) {
                targetAL = new AdjustmentList(target);
            }
            else {
                targetAL = eat.getAdjustmentList(target);
                if ( targetAL == null ) {
                    targetAL = new AdjustmentList(target);
                }
            }
        } 
        
        setAdjustmentList( targetAL);
        
        // Grab the Adjustables ArrayList
        AdjustmentPower power = (AdjustmentPower)ai.getAbility().getPower();
        setAdjustables( power.getAvailableAdjustables(battleEvent.getAbility(), target));
        
        if ( adjustables.size() == 1 ) {
            adjustmentList.addAdjustable( adjustables.get(0) , 100);
        }
        
        if ( nodeRequiresInput() || manualOverride == true ) {
            AdjustmentAttackTreePanel app = AdjustmentAttackTreePanel.getDefaultPanel(battleEvent, ADJ_CONFIG_DRAIN_FROM, battleEvent.getAbility(), target, adjustables, adjustmentList);
        
            attackTreePanel.showInputPanel(this,app);
            attackTreePanel.setInstructions("Configure the Abilities/Stats of " + target.getName() + " to Drain...");
            
            acceptActivation = true;
        }
        
       // if ( champions.attackTree.AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        boolean requiresInput = false;
        
        requiresInput = (adjustmentList.getAdjustableCount() == 0) || getBattleEvent().getSource().getBooleanProfileOption(getAutoBypassOption());
        
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
        // This node build everything upfront, when it's target is first set.
        return null;
    }
    
    /**
     * Causes the node to verify the layout of it's children and fix any discrepencies.
     *
     * checkNodes should guarantee that the current child structure is complete and
     * all nodes which should exist do.
     *
     * The default implementation calls the nextNodeName method to check that the nodes
     * are arranged properly.  If they are not, it deletes all nodes that aren't properly
     * arranged.
     */
    public void checkNodes() {
        // Don't do anything when you check the nodes.  The children are built when this
        // node is first activated.
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
        int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup());
        
        // Set the Adjustment List in the Target info
        ai.addIndexed(tindex, "Target", "DRAINFROMLIST", adjustmentList, true);
        
        // Always allow the node to advance
        return true;
    }

    public String getAutoBypassOption() {
        return "SHOW_ADJUSTMENT_PARAMETERS_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return getBattleEvent().getSource();
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
    
    /** Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        return target;
    }
    
    /** Setter for property target.
     * @param target New value of property target.
     */
    public void setTarget(Target target) {
        this.target = target;
    }
    
    /** Getter for property adjustmentList.
     * @return Value of property adjustmentList.
     */
    public AdjustmentList getAdjustmentList() {
        return adjustmentList;
    }
    
    /** Setter for property adjustmentList.
     * @param adjustmentList New value of property adjustmentList.
     */
    public void setAdjustmentList(AdjustmentList adjustmentList) {
        this.adjustmentList = adjustmentList;
    }
    
    /** Getter for property adjustables.
     * @return Value of property adjustables.
     */
    public ArrayList getAdjustables() {
        return adjustables;
    }
    
    /** Setter for property adjustables.
     * @param adjustables New value of property adjustables.
     */
    public void setAdjustables(ArrayList adjustables) {
        this.adjustables = adjustables;
    }
    
}
