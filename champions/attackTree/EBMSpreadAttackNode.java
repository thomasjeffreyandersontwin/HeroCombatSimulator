/*
 * EBMSpreadAttackNode.java
 *
 * Created on January 19, 2002, 6:11 PM
 */

package champions.attackTree;
/**
 *
 * @author  twalker
 * @version 
 */
public class EBMSpreadAttackNode extends DefaultAttackTreeNode {

    /** Creates new AutofireSprayAttackNode */
    public EBMSpreadAttackNode(String name) {
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
                if ( nextNodeName.startsWith("EB Spread Hex ")) {
                    try {
                        int shotNumber = Integer.parseInt( nextNodeName.substring(14) );
                       
                        EBMShotNode node = new EBMShotNode( nextNodeName );
                        node.setShot( shotNumber );
                        //node.setFirstShotTargetGroup( getTargetGroup() + ".Hex 1");
                        node.setTargetGroupSuffix( "Hex " + Integer.toString(shotNumber) );
                        nextNode = node;
                    }
                    catch ( NumberFormatException nfe ) {
                        
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
            nextNodeName =  "EB Spread Hex 1";
        }
        else {
            Integer shots = battleEvent.getActivationInfo().getIntegerValue( "Attack.SPREADWIDTH" );
            if ( shots == null ) shots = new Integer(1);
            
            try {
                int shotNumber = Integer.parseInt( previousNodeName.substring(14) );
                if ( shotNumber < shots.intValue() ) {
                    nextNodeName = "EB Spread Hex " + Integer.toString(shotNumber + 1);
                }
            }
            catch ( NumberFormatException nfe ) {
                
            }
        }
        return nextNodeName;
    }
    
 /*   public void nodeChanged(AttackTreeNode changedNode) {
        if ( changedNode.getName().equals("ToHit") ) {
            checkNodes();
            
            AttackTreeNode atn = findNode("Effect", false);
            if ( atn != null ) atn.nodeChanged(changedNode);
        }
        
        //super.nodeChanged(changedNode);
    } */
    
   
}