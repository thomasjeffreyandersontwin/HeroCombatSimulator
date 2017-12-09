/*
 * MovementNode.java
 *
 * Created on May 1, 2002, 6:59 PM
 */

package champions.attackTree;

import champions.Ability;

/**
 *
 * @author  Trevor Walker
 * @version 
 */
public class MovementNode extends DefaultAttackTreeNode {

    /** Creates new TestAttackTreeNode */
    public MovementNode(String name) {
        this.name = name;
        setVisible(false);
    }
    
    public boolean activateNode(boolean manualOverride) {
        // This should actually check to make sure that the next node is correct give the
        // parameters configured for the ability activation...
        
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
                if ( nextNodeName.equals("Single Attack")) {
                    SingleAttackNode node = new SingleAttackNode(nextNodeName);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Movement Distance") ) {
                    MovementDistanceNode node = new MovementDistanceNode(nextNodeName);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Process Movement") ) {
                    ProcessMovementNode node = new ProcessMovementNode(nextNodeName);
                    nextNode = node;
                }
            }
        }
        return nextNode;
    }
     
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        
        if ( previousNodeName == null ) {
            nextNodeName = "Movement Distance";
        }
        else if ( previousNodeName.equals("Movement Distance") ) {
            Ability ability = battleEvent.getAbility();
            if ( ability.isRequiresTarget() ) {
                nextNodeName = "Single Attack";
            }
            else {
                nextNodeName = "Process Movement";
            }
        }
        else if ( previousNodeName.equals("Single Attack") ) {
            nextNodeName = "Process Movement";
        }
        
        return nextNodeName;
    }

}
