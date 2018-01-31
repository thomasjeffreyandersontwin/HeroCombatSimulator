/*
 * AutofireAttackNode.java
 *
 * Created on December 16, 2001, 2:00 PM
 */

package champions.attackTree;


import champions.ActivationInfo;
import champions.BattleEvent;
import champions.CVList;
import champions.SweepBattleEvent;
import champions.Target;

/**
 *
 * @author  twalker
 * @version
 */
public class SweepExecuteNode extends DefaultAttackTreeNode {
    
    public static SweepExecuteNode SENode;

	/** Creates new AutofireAttackNode */
    public SweepExecuteNode(String name) {
        this.name = name;
        setVisible(false);
        SENode = this;
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
                SweepBattleEvent lbe = (SweepBattleEvent)battleEvent;
                
                //  Chop the index from the front of the name...
                String s = nextNodeName.substring(7, nextNodeName.indexOf(":"));
                int index = Integer.parseInt(s);
                
                if ( index != -1 ) {
                    // Build a node.  Embed the BattleEvent.  
                   
                    SweepActivateAbilityNode node = new SweepActivateAbilityNode(nextNodeName);
                    node.setSweepAbilityIndex(index-1);
                    node.setSweepBattleEvent( (SweepBattleEvent) battleEvent );
                    nextNode = node;
                }
            }
        }
        return nextNode;
    }

    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        
        SweepBattleEvent lbe = (SweepBattleEvent)battleEvent;
        int count = lbe.getLinkedAbilityCount();
        
        int previousIndex = 0;
        if ( previousNodeName == null ) {
            previousIndex = 0;
        }
        else {
            //  Chop the index from the front of the name...
            String s = previousNodeName.substring(7, previousNodeName.indexOf(":"));
            previousIndex = Integer.parseInt(s);
        }
            
        for(int i = previousIndex+1; i < count+1; i++) {
            if ( lbe.isLinkedAbilityEnabled(i-1) ) {
                nextNodeName = "Attack " + Integer.toString(i) + ": " + lbe.getLinkedAbility(i-1).getName();
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
        SweepBattleEvent lbe = (SweepBattleEvent)battleEvent;
        
        int children = lbe.getLinkedAbilityCount();
        for(int index=1; index<children; index++) {
            BattleEvent aBE = lbe.getLinkedBattleEvent(index);
            if ( aBE == be ) {
                // This was the node.  Find the other battleEvent
            	int targetReferenceNumber = aBE.getActivationInfo()
            			.getPrimaryTargetReferenceNumber(primaryTargetNumber);
            	String tGroup = aBE.getActivationInfo().getPrimaryTargetGroup(primaryTargetNumber);
            	int tindex = aBE.getActivationInfo().
            			getTargetIndex(targetReferenceNumber, tGroup);
                
            	if(tindex==0 || tindex==-1)
            	{
            	
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
	                        boolean previousHit = previousAI.getTargetHit(previousTargetIndex);
	                        
	                        
	                        ActivationInfo ai = be.getActivationInfo();
	                        
	                        // Add a new target (or set the already created one)
	                        tindex = ai.addTarget(previousTarget,targetGroup,referenceNumber);
	                        
	//                        String reason = "This target is a primary target from an earlier sweep or rapid fire attack.\n\n" +
	//                                        "Primary targets must be the same for consecutive sweep and rapid fire attacks.";
	//                        ai.setTargetFixed(tindex, true, reason);
	                        
	                        if ( previousHit == false ) { 
	                            String reason = "A previous attack in the sweep/rapid fire attack sequence missed.\n\n" +
	                                        "All remaining attacks in this sequence will miss.";
	                        
	                            ai.setTargetHitOverride(tindex, false, reason, reason);
	                        }
	                        else {
	                            ai.clearTargetHitOverride(tindex);
	                        }
	                    }
	                }
	                break;   
	            }
	        }
	    }
    }

    

}
