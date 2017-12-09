/*
 * AutofireAttackNode.java
 *
 * Created on December 16, 2001, 2:00 PM
 */

package champions.attackTree;

import champions.ActivationInfo;
import champions.BattleEvent;
import champions.LinkedBattleEvent;
import champions.Target;

/**
 *
 * @author  twalker
 * @version
 */
public class LinkedExecuteNode extends DefaultAttackTreeNode {
    
    /** Creates new AutofireAttackNode */
    public LinkedExecuteNode(String name) {
        this.name = name;
        setVisible(false);
    }
    
    public boolean activateNode(boolean manualOverride) {
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");

        // The root node should never accept active node status, since
        // It isn't really a real node.
        return true;
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
                LinkedBattleEvent lbe = (LinkedBattleEvent)battleEvent;
                
                int index = Integer.parseInt(nextNodeName);
                
                if ( index != -1 ) {
                    // Build a node.  Embed the BattleEvent.  
                   
                    LinkedActivateAbilityNode node = new LinkedActivateAbilityNode(nextNodeName);
                    node.setLinkedAbilityIndex(index);
                    nextNode = node;
                }
            }
        }
        return nextNode;
    }

    public String nextNodeName(String previousNodeName) {
        String nextNodeName = "-1";
        
        LinkedBattleEvent lbe = (LinkedBattleEvent)battleEvent;
        int count = lbe.getLinkedAbilityCount();
        
        int previousIndex = -1;
        if ( previousNodeName == null ) {
            previousIndex = -1;
        }
        else {
            previousIndex = Integer.parseInt(previousNodeName);
        }
            
        for(int i = previousIndex+1; i < count; i++) {
            if ( lbe.isLinkedAbilityEnabled(i) ) {
                // Encode the index into the node name...
                nextNodeName = Integer.toString(i); 
                break;
            }
        }
        
        return nextNodeName;
    }
    
    /** 
     * Allows node to adjust the target set for an attack.
     *
     * Every node in the tree path of a target node has an oppertunity to
     * adjust the target for an attack via this method.  This method should
     * in most cases pass this call up to it's attack tree node parent (or
     * simply call the default implementation which does exactly that).
     *
     * PrimaryTargetNumber indicates if this particular target is considered 
     * a primary target for this attack.  If it is, primaryTargetNumber should
     * be one or greater.
     */
    public void adjustTarget(BattleEvent be, Target source, int referenceNumber, String targetGroup, int primaryTargetNumber) {
        // Don't do anything if this isn't a primary target...
        if ( primaryTargetNumber < 1 ) return;
        
        // We want to adjust the target for every battleEvent after the first event,
        // setting up the primary when applicable.
        LinkedBattleEvent lbe = (LinkedBattleEvent)battleEvent;
        
        int children = lbe.getLinkedAbilityCount();
        for(int index=1; index<children; index++) {
            BattleEvent aBE = lbe.getLinkedBattleEvent(index);
            if ( aBE == be ) {
                // This was the node.  Find the other battleEvent
                BattleEvent previousBE = lbe.getLinkedBattleEvent(index-1);
                
                ActivationInfo previousAI = previousBE.getActivationInfo();
                String previousTargetGroup = previousAI.getPrimaryTargetGroup(primaryTargetNumber);
                int previousTargetReferenceNumber = previousAI.getPrimaryTargetReferenceNumber(primaryTargetNumber);
                
                if ( previousTargetGroup != null ) {
                    // There is a set primaryTarget
                    // Find if the previous primary target is actually set...
                    int previousTargetIndex = previousAI.getTargetIndex(previousTargetReferenceNumber, previousTargetGroup);
                    if ( previousTargetIndex != -1 ) {
                        Target previousTarget = previousAI.getTarget(previousTargetIndex);
                        
                        ActivationInfo ai = be.getActivationInfo();
                        
                        // Add a new target (or set the already created one)
                        int tindex = ai.addTarget(previousTarget,targetGroup,referenceNumber);
                        
                        String reason = "This target is a primary target from an earlier linked attack.\n\n" +
                                        "Primary targets must be the same for consecutive linked attacks.";
                        ai.setTargetFixed(tindex, true, reason);
                    }
                }
                break;   
            }
        }
    }


}
