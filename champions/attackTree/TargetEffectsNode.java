/*
 * SourceEffectsNode.java
 *
 * Created on March 14, 2002, 8:36 PM
 */

package champions.attackTree;

import champions.ActivationInfo;
import champions.Effect;
import champions.Target;

/**
 *
 * @author  Trevor Walker
 * @version 
 */
public class TargetEffectsNode extends DefaultAttackTreeNode {

    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    /** Holds value of property target. */
    private Target target;
    
    protected boolean childrenBuilt = false;
   
    /** Creates new SingleTargetNode */
    public TargetEffectsNode(String name) {
        this.name = name;
       // icon = UIManager.getIcon("AttackTree.selectTargetIcon");
        setVisible(false);
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        ActivationInfo ai = battleEvent.getActivationInfo();
        int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup() );
        
        if ( tindex != -1 ) {
            setTarget( ai.getTarget(tindex) );
        
            buildChildren();
        }
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        // This node is completely strange in that it firsts asks for a target, and once one is set, it
        // creates the ToHit and TargetOption nodes immediately.
        
        
     /*   AttackTreeNode nextNode = null;
        // Compare the activeChild to the possible children, to determine what comes next.
        if ( activeChild != null && activeChild.getName() == null ) {
            // This is probably an error
            int index = children.indexOf(activeChild);
            System.out.println("Null Node name for child " + Integer.toString(index) + " of Parent " + this);
            nextNode = null;
        }
        else if ( activeChild == null ) {
            // Build the very first node: Attack Param
         //   ToHitNode node = new ToHitNode("ToHit");
         //   nextNode = node;
        }
        else if ( activeChild.getName().equals("Single Target") ) {
            // Build second node: Single Attack
         //   EffectNode node = new EffectNode("Effect");
            //nextNode = node;
        }
      
        return nextNode; */
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
        // Don't do anything when you check the nodes.  This nodes is built by 
        // when the setTarget method is called.
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
    
    public void destroy() {
        setTarget(null);
        
        super.destroy();
    }
    
    public void buildChildren() {
        if ( childrenBuilt == true ) return;
        // Build a ToHit node and add it   
        AttackTreeNode atn;
        Effect effect;
        String tg = getTargetGroup();
        
        int index, count;
        count = target.getEffectCount();
        for(index=0;index<count;index++) {
            effect = target.getEffect(index);
            atn = effect.getTargetEffectNode( battleEvent, target, tg, targetReferenceNumber);
            if ( atn != null ) {
                addChild(atn);
            }
        }

        childrenBuilt = true;
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
    
}
