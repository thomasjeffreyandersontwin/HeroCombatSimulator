/*
 * MoveByAttackNode.java
 *
 * Created on January 21, 2002, 9:51 AM
 */

package champions.attackTree;

import champions.exceptionWizard.ExceptionWizard;

/**
 *
 * @author  twalker
 * @version 
 */
public class MoveByAttackNode extends DefaultAttackTreeNode {

    /** Creates new AutofireSprayAttackNode */
    public MoveByAttackNode(String name) {
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
                if ( nextNodeName.startsWith("MoveBy Target ")) {
                    try {
                        int shotNumber = Integer.parseInt( nextNodeName.substring(14) );
                       
                        MoveByTargetNode node = new MoveByTargetNode( nextNodeName );
                        node.setShot( shotNumber );
                        node.setPreviousTargetGroup( getTargetGroup() + ".Target " + Integer.toString(shotNumber - 1));
                        node.setTargetGroupSuffix( "Target " + Integer.toString(shotNumber) );
                        nextNode = node;
                    }
                    catch ( NumberFormatException nfe ) {
                        ExceptionWizard.postException( nfe );
                    }
                }
            }
        }
        return nextNode;
    }
    
  /*  public void checkNodes() {
        int count = (children == null) ? 0 : children.size();
        int index;
        String previousNodeName = null;
        String nextNodeName = null;
        AttackTreeNode atn;
   
        for(index=0;index<count;index++) {
            atn = (AttackTreeNode)children.get(index);
            nextNodeName = nextNodeName(previousNodeName);
            if ( atn.getName().equals(nextNodeName) == false ) {
                // The next node name isn't what it should be, so rebuild this tree from this child down.
                while ( index < children.size() ) {
                    atn = (AttackTreeNode)children.get(index);
                    removeChild(atn);
                }
                break;
            }
            previousNodeName = nextNodeName;
        }
    } */
    
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        
        if ( previousNodeName == null ) {
            nextNodeName =  "MoveBy Target 1";
        }
        else {
            Integer shots = battleEvent.getActivationInfo().getIntegerValue( "Attack.MOVEBYTARGETS" );
            if ( shots == null ) shots = new Integer(1);
            
            try {
                int shotNumber = Integer.parseInt( previousNodeName.substring(14) );
                if ( shotNumber < shots.intValue() ) {
                    nextNodeName = "MoveBy Target " + Integer.toString(shotNumber + 1);
                }
            }
            catch ( NumberFormatException nfe ) {
                
            }
        }
        return nextNodeName;
    }
}
