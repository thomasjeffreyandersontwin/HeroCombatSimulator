/*
 * KnockbackDamageGroupNode.java
 *
 * Created on December 2, 2001, 8:30 PM
 */

package champions.attackTree;

import champions.ActivationInfo;
import champions.Target;
import champions.interfaces.IndexIterator;
import java.util.Vector;

/**
 *
 * @author  twalker
 * @version 
 */
public class KnockbackDamageGroupNode extends DefaultAttackTreeNode {

    private String knockbackGroup;
    
    /**
     * Creates new KnockbackDamageGroupNode
     */
    public KnockbackDamageGroupNode(String name) {
        this.name = name;
        setVisible(false);
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        
    /*    if ( manualOverride || nodeRequiresInput() ) {
            EffectPanel ep = EffectPanel.getDefaultPanel(getBattleEvent(), getTargetGroup());
            inline.showInputPanel(ep);
            inline.setInstructions("Enter the Dice rolls for the Attack...");
            
            acceptActivation = true;
        } */
        
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return false;
    }
    
    private boolean nodeRequiresInput() {
        // Run through the dice and make sure the all have values;
        boolean requiresInput = false;

        return requiresInput;
    }
    
    /**
     * Builds the next child based upon the last child which was constructed.
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
        // Instead of just checking the node, always rebuilt the node structure.
        updateChildren();
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
        System.out.println("Node " + name + " exited.");
        return true;
    }
    
    public void updateChildren() {
        // This method must make sure that all the Targets hit in the TargetGroup
        // have an associated TargetEffect node.
        
        boolean fireChange = false;
        boolean found;
        // Make sure the abilities in the source match the abilityNodes that are childern
        Vector newChildren = new Vector();
        int nindex, ncount;
        int total = 0;
        ncount = ( children != null ) ? children.size() : 0;
        KnockbackDamageApplyNode ten;
        int dindex;
        Target target;
        
        ActivationInfo ai = getBattleEvent().getActivationInfo();
        
        IndexIterator ii = ai.getTargetGroupIterator(getTargetGroup());
        
        while ( ii.hasNext() ) {
            dindex = ii.nextIndex();
            target = (Target)ai.getIndexedValue(dindex, "Target", "TARGET");
            
            // Try to find it in the children array.
            found = false;
            for(nindex=0;nindex<ncount;nindex++) {
                ten = (KnockbackDamageApplyNode) children.get(nindex);
                if ( ten.getTarget() == target ) {
                    found = true;
                    // Move the ability node from the childern list to the newChildern list
                    newChildren.add(ten);
                    if ( nindex != total ) fireChange = true;
                    children.set(nindex, null);
                    break;
                }
            }
            
            if ( found == false ) {
                ten = new KnockbackDamageApplyNode("Apply Knockback Damage");
                ten.setParent(this);
                ten.setModel(getModel());
                ten.setBattleEvent(getBattleEvent());
                ten.setAttackTreePanel(getAttackTreePanel());
                ten.setTarget(target);
                
                newChildren.add(ten);
                fireChange = true;
            }
            total++;
        }
        
        Vector oldChildren = children;
        children = newChildren;
        
        // Now that everything is done, anything level not-null in oldChildren should be destroyed
        // and references to it released.
        if ( oldChildren != null ) {
            for(nindex=0;nindex<oldChildren.size();nindex++) {
                if ( oldChildren.get(nindex) != null ) {
                    ((AttackTreeNode)oldChildren.get(nindex)).destroy();
                    oldChildren.set(nindex,null);
                    fireChange = true;
                }
            }
        }
        
        if ( fireChange && model != null ) model.nodeStructureChanged(this);
        
        
    }
    
    public void nodeChanged(AttackTreeNode changedNode) {
        if ( changedNode.getName().equals("ToHit") ) {
            updateChildren();
        }
    }
    

    
            /** Getter for property knockbackGroup.
     * @return Value of property knockbackGroup.
     */
    public String getKnockbackGroup() {
        return knockbackGroup;
    }
    
    /** Setter for property knockbackGroup.
     * @param knockbackGroup New value of property knockbackGroup.
     */
    public void setKnockbackGroup(String knockbackGroup) {
        this.knockbackGroup = knockbackGroup;
    }

}
