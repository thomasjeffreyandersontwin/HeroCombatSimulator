/*
 * ProcessActivateRoot.java
 *
 * Created on April 25, 2002, 2:17 PM
 */

package champions.attackTree;

import champions.BattleEvent;

/**
 *
 * @author  Trevor Walker
 * @version 
 */
public class ProcessDeactivateRootNode extends DefaultAttackTreeNode {

    /** Creates new ProcessActivateRoot */
    public ProcessDeactivateRootNode(String name, BattleEvent be) {
        super();
        this.name = name;
        setBattleEvent(be);
        
     //   setVisible(false);
    }
    
        public boolean activateNode(boolean manualOverride) {
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");

        // The root node should never accept active node status, since
        // It isn't really a real node.
        return false;
    }
    
    
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        
        AttackTreeNode nextNode = null;
        // Compare the activeChild to the possible children, to determine what comes next.
        if ( activeChild != null && activeChild.getName() == null ) {
            // This is probably an error
            int index = children.indexOf(activeChild);
            System.out.println("Null Node name for child " + Integer.toString(index) + " of Parent " + this);
            nextNode = null;
        }
        else {
            String previousNodeName = (activeChild == null ) ? null : activeChild.getName();
            String nextNodeName = nextNodeName(previousNodeName);
            if ( nextNodeName != null ) {
                if ( nextNodeName.equals("processDeactivating") ) {
                    // Build the very first node: Attack Param
                    ProcessStateDeactivating node = new ProcessStateDeactivating("processDeactivating");
                    nextNode = node;
                }
                else if (nextNodeName.equals("Summary") ) {
                    // Build the very first node: Attack Param
                    SummaryNode node = new SummaryNode("Summary");
                    nextNode = node;
                }
                
            }
        }
        return nextNode;
    }
    
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        
        String ai_state = battleEvent.getActivationInfo().getState();
        
        if ( battleEvent.isFinishedProcessingEvent() == false ) {
                nextNodeName = "processDeactivating";
        }
        
        if ( nextNodeName == null && battleEvent.isEmbedded() == false && (previousNodeName == null || previousNodeName.equals("Summary") == false) ) {
            nextNodeName = "Summary";
        }
        
        return nextNodeName;
    }
    
    public void checkNodes() {
        
    }

}
