/*
 * KnockbackNode.java
 *
 * Created on November 8, 2001, 11:55 AM
 */

package champions.attackTree;

import champions.ActivationInfo;
import champions.BattleEvent;
import champions.Target;
import champions.battleMessage.BattleMessageGroup;
import champions.battleMessage.KnockbackMessageGroup;
import champions.interfaces.IndexIterator;

/**
 *
 * @author  twalker
 * @version
 */
public class KnockbackNode extends DefaultAttackTreeNode implements BattleMessageGroupProvider{
    
    public static KnockbackNode Node;

	/** Holds value of property knockbackGroup. */
    private String knockbackGroup;
    
    private KnockbackMessageGroup messageGroup;
    
    private int knockbackTargets = 0;
    
    /** Creates new KnockbackNode */
    public KnockbackNode(String name) {
        this.name = name;
        setVisible(false);
        Node = this;
    }
    
    public boolean activateNode(boolean manualOverride) {
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        
        if ( knockbackTargets > 0 ) {
            messageGroup = new KnockbackMessageGroup();
            battleEvent.openMessageGroup(messageGroup);
        }
        else {
            messageGroup = null;
        }
        // The root node should never accept active node status, since
        // It isn't really a real node.
        return true;
    }
    
    
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        // This nodes is built all at once using the updateChildren method.
        AttackTreeNode nextNode = null;

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
    
    public void updateChildren() {
        // This method must make sure that all the Targets hit in the TargetGroup
        // have an associated TargetEffect node.
        
        int nindex, ncount;
        int tindex;
        int total = 0;
        
        KnockbackTargetNode ten;
        Target target;
        boolean inserted;
        
        BattleEvent be = getBattleEvent();
        String knockbackGroup = getKnockbackGroup();
        
        IndexIterator ii = be.getKnockbackTargets( knockbackGroup );
        
        knockbackTargets = 0;
        
        nindex=0;
        
        ncount = ( children != null ) ? children.size() : 0;
        
        while ( ii.hasNext() ) {
            tindex = ii.nextIndex();
            target = (Target)be.getIndexedValue(tindex, "Knockback", "TARGET");
            
            if ( target.getBooleanValue( "Target.CANBEKNOCKEDBACK" ) && be.getTotalKnockback(tindex) > 0) {
                // target is the next element that should be in the array
                inserted = false;
                
                knockbackTargets++;
                
                while( nindex < ncount - 1 ) {
                    ten = (KnockbackTargetNode) children.get(nindex);
                    if ( ten.getTarget() == target ) {
                        // The target is already in the list, at position nindex.
                        // Increase to the next node, but don't do anything.
                        nindex ++;
                        inserted = true;
                        break;
                    }
                    else if ( target.compareTo(ten.getTarget()) < 0 ) {
                        // The target comes before the current nindex, so add it at nindex and
                        // increase nindex.
                        ten = new KnockbackTargetNode("Knockback Target");
                        addChild(ten, nindex, true);
                        ten.setTargetGroupSuffix( target.getName());
                        ten.setTarget(target);
                        ten.setKnockbackGroup( knockbackGroup );
                        
                        
                        inserted = true;
                        nindex ++;
                        ncount ++;
                        break;
                    }
                    else if ( target.compareTo(ten.getTarget()) > 0 ) {
                        // The next node comes after the current nindex.  This indicates there is an extra
                        // target in the mix, so delete it.  Don't increase the index, since the node will be deleted.
                        removeChild(ten);
                        ncount --;
                    }
                }
                
                if ( inserted == false ) {
                    // The target comes after everything in the list, so add it at the end.
                    ten = new KnockbackTargetNode("Knockback Target");
                    addChild(ten, nindex, true);
                    ten.setTargetGroupSuffix( target.getName());
                    ten.setTarget(target);
                    ten.setKnockbackGroup( knockbackGroup );
                    nindex ++;
                }
            }
        }
        
        while ( nindex < ncount - 1 ) {
            // Remove any extra nodes that may be lying around...
            AttackTreeNode oldChild = (AttackTreeNode)children.get(nindex);
            removeChild(oldChild);
            ncount --;
        }
        
        if ( ncount == 0 ) {
            CloseMessageGroupNode node = new CloseMessageGroupNode("Close Message Group", this);
            addChild(node);
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

    public BattleMessageGroup getBattleMessageGroup() {
        return messageGroup;
    }
    
    
    
}
