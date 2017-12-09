/*
 * GrabTriggerNode.java
 *
 * Created on June 5, 2002, 3:24 PM
 */

package champions.attackTree;

import champions.ActivationInfo;
import champions.Target;
import champions.exception.BattleEventException;


/**
 *
 * @author  Trevor Walker
 */
public class GrabActionNode extends DefaultAttackTreeNode {
    
        
    private boolean childrenBuilt = false;
    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    /** Creates new GrabTriggerNode */
    public GrabActionNode(String name) {
        this.name = name;
        
         setVisible(false);
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean activateNode = false;

        return activateNode;
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
    public boolean processAdvance() throws BattleEventException {
        boolean removeChildren = true;

        Target target = null;
        ActivationInfo ai = getBattleEvent().getActivationInfo();
        int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup() );
        if ( tindex != -1 ) {
            target = ai.getTarget(tindex);
            int gindex = battleEvent.findIndexed("GrabbedEffect", "TARGET", target);
            boolean grabbed = battleEvent.getIndexedBooleanValue(gindex, "GrabbedEffect", "GRABBED");

            if ( grabbed ) {
                removeChildren = false;
                // Make sure we have a grab option node
                if ( getChildCount() == 0 ) {
                    // Add a new grap option node...
                    GrabSelectActionNode node = new GrabSelectActionNode("Follow-up Action");
                    node.setGrabbedEffectIndex(gindex);
                    node.setTarget(target);
                    addChild( node );
                }
            }
        }
        
        if ( removeChildren ) {
            // remove all the nodes, cause there isn't a grab anymore
            while ( getRealChildCount() > 0 ) {
                removeChild( getRealChildAt(0) );
            }
        }
        
        return true;
    }
    
    public void checkNodes() {
        // Don't check the children ever.
    }
    
    /** Getter for property targetReferenceNumber.
     * @return Value of property targetReferenceNumber.
     */
    public int getTargetReferenceNumber() {
        return this.targetReferenceNumber;
    }
    
    /** Setter for property targetReferenceNumber.
     * @param targetReferenceNumber New value of property targetReferenceNumber.
     */
    public void setTargetReferenceNumber(int targetReferenceNumber) {
        this.targetReferenceNumber = targetReferenceNumber;
    }
    
}
