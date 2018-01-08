/*
 * AENormalTargets.java
 *
 * Created on December 28, 2001, 5:23 PM
 */

package champions.attackTree;

import champions.ActivationInfo;
import champions.Target;
import champions.interfaces.IndexIterator;

/**
 *
 * @author  twalker
 * @version
 */
public class AENormalTargetsNode extends DefaultAttackTreeNode {
    
    private boolean firstPass = true;
    
    public AENormalTargetsNode(String name) {
        this.name = name;
        setVisible(false);
        Node = this;
    }
    
    public boolean activateNode(boolean manualOverride) {
        
        if ( firstPass ) {
            buildChildren();
            firstPass = false;
        }
        
        return false;
    }
    
    
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        
        AttackTreeNode nextNode = null;
        // Compare the activeChild to the possible children, to determine what comes next.
        if ( activeChild != null && activeChild.getName() == null ) {
            // This is probably an error
            int index = children.indexOf(activeChild);
            if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Null Node name for child " + Integer.toString(index) + " of Parent " + this);
            nextNode = null;
        }
        else {
            boolean buildAnotherTarget = false;
            
            if ( activeChild != null ) {
                SingleTargetNode stn = (SingleTargetNode)activeChild;
                if ( stn.getTarget() != null ) buildAnotherTarget = true;
            }
            else {
                // There are no targets, so go ahead and build the first...
                buildAnotherTarget = true;
            }
            
            if ( buildAnotherTarget == true ) {
                ActivationInfo ai = battleEvent.getActivationInfo();
                
                int refNumber = ai.getNextTargetReferenceNumber( getTargetGroup() );
                int tindex = ai.addTarget(null, getTargetGroup(), refNumber);
                
                String reason = "Normal Area Effect attack: Target hit automatically.";
                String explination = "Target Hit Automatically.\n\n"
                + "This was a Normal Area Effect Attack.  All Targets caught in the Area of Effect are automatically "
                + "hit by the attack.\n\n" 
                + "To Avoid being hit, this Target must be removed from the Affected Targets list.";
                
                ai.setTargetHitOverride(tindex, true, reason, explination);
                                
                SingleTargetNode stn = new SingleTargetNode("Single Target");
                stn.setTargetReferenceNumber( refNumber );
                stn.setMode(AE_TARGET);
                
                nextNode = stn;
            }
            
        }
        return nextNode;
    }
    
    public void checkNodes() {
        int count = (children == null) ? 0 : children.size();
        int index;
        String previousNodeName = null;
        String nextNodeName = null;
        SingleTargetNode atn;
        
        for(index=0;index<count;index++) {
            atn = (SingleTargetNode)children.get(index);
            if ( atn.getTarget() == null && index != count -1  ) {
                // There is an empty target that isn't the last, so remove it.
                    removeChild(atn);
                    index--;
                    count--;
            }
        }
    }
    
    public void buildChildren() {
        // Build the initial children that already exist in the target group (probably due to continuing power)...
        ActivationInfo ai = battleEvent.getActivationInfo();
        IndexIterator ii = ai.getTargetGroupIterator( getTargetGroup() );
        
        while ( ii.hasNext() ) {
            int index = ii.nextIndex();
            int refNumber = ai.getTargetReferenceNumber(index);
            Target target = ai.getTarget(index);
            
            SingleTargetNode stn = new SingleTargetNode("Single Target");
            stn.setTargetReferenceNumber( refNumber );
            stn.setMode(AE_TARGET);
            
            addChild(stn);
            // Have to set the target after adding the node, otherwise the BattleEvent won't be setup...
            stn.setTarget(target);
        }
    }
    
}
