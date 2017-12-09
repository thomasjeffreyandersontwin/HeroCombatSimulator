/*
 * RootNode.java
 *
 * Created on October 30, 2001, 11:56 AM
 */

package champions.attackTree;

import champions.Preferences;

/**
 *
 * @author  twalker
 * @version
 */
public class AttackProcessNode extends DefaultAttackTreeNode {
    
    private boolean showDescription = false;
    
    /** Creates new TestAttackTreeNode */
    public AttackProcessNode(String name) {
        this.name = name;
        setVisible(false);
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
                if ( nextNodeName.equals("Attack Description") ) {
                    // Build the very first node: Attack Param
                    AttackDescriptionNode node = new AttackDescriptionNode("Attack Description");
                    node.setTargetGroupSuffix("");
                    nextNode = node;
                }
                if ( nextNodeName.equals("Attack Parameters") ) {
                    // Build the very first node: Attack Param
                    AttackParametersNode node = new AttackParametersNode("Attack Parameters");
                    node.setTargetGroupSuffix("");
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Attack Type") ) {
                    // Build second node: Single Attack
                    AttackTypeNode node = new AttackTypeNode("Attack Type");
                    node.setTargetGroupSuffix("ATTACK");
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Knockback") ) {
                    KnockbackNode node = new KnockbackNode("Knockback");
                    node.setTargetGroupSuffix("KB");
                    node.setKnockbackGroup("KB");
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Move Through Effect") ) {
                    MoveThroughEffectNode node = new MoveThroughEffectNode("Move Through Effect");
                    node.setTargetGroupSuffix("ATTACK");
                    //node.setTargetGroupSuffix("KB");
                    //node.setKnockbackGroup("KB");
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Post Trigger") ) {
                    PostTriggerNode node = new PostTriggerNode("Post Trigger");
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Summary") ) {
                    SummaryNode node = new SummaryNode("Summary");
                    nextNode = node;
                }
            }
        }
        return nextNode;
    }
    
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        
        // The first time through, if the description hasn't
        // been shown, this will cache that.  After the
        // description child node is created, isAttackTreeDescriptionShown
        // will be true, but we still want the description to be displayed.
        if ( battleEvent.isAttackTreeDescriptionShown() == false ) {
            
            showDescription = true;
        }
        
        // Short Circuit the display of the Attack Description and Parameters if it has
        // already been displayed in this attack tree...
        //
        //  Note, I am changing previousNodeName here, which effectively
        //  tricks the tree to skip the description/parameters if
        //  they have already been shown...
        if ( previousNodeName == null && showDescription == false ) {
            previousNodeName = "Attack Parameters";
        }
        
        if ( previousNodeName == null ) {
            nextNodeName = "Attack Description";
        }
        else if ( previousNodeName.equals("Attack Description") ) {
            // Build second node: Single Attack
            nextNodeName = "Attack Parameters";
        }
        else if ( previousNodeName.equals("Attack Parameters") ) {
            // Build second node: Single Attack
            nextNodeName = "Attack Type";
        }
        else if ( previousNodeName.equals("Attack Type") ) {
            boolean enabled = (Boolean)Preferences.getPreferenceList().getParameterValue("KnockbackEnabled");
            
            if ( enabled ) {
                nextNodeName = "Knockback";
            }
          //  else {
          //      nextNodeName = "Summary";
          //  }
        }
        else if ( previousNodeName.equals("Knockback") ) {
            //boolean enabled = Preferences.getPreferenceList().getBooleanValue("Knockback.ENABLED" );
            boolean isMoveThrough = battleEvent.isMoveThrough();
            
            if ( isMoveThrough && battleEvent.getActivationInfo().getTargetGroupHasHitTargets(".ATTACK") ) {
                nextNodeName = "Move Through Effect";
            }
            else {
                nextNodeName = "Post Trigger";
            }
        }
        else if ( previousNodeName.equals("Move Through Effect") ) {
            nextNodeName = "Post Trigger";
        }
        
        return nextNodeName;
    }
    
    /**
     * Indicates that one of the nodes in this nodes hierarchy changed, usually
     * either a direct parent or direct child.
     */
  /*  public void nodeChanged(AttackTreeNode changedNode) {
        System.out.println(this + ".nodeChanged(" + changedNode + ") called.");
        // Just pass this up to the parent...
        int index, count;
        AttackTreeNode child;
        
        if ( changedNode.getName().equals("Attack Parameters") && children != null ) {
            count = children.size();
            for(index=0;index<count;index++) {
                child = (AttackTreeNode)children.get(index);
                if ( child.getName().equals("Attack Type") ) {
                    child.nodeChanged(changedNode);
                    break;
                }
            }
        }
        
    } */
    
}
