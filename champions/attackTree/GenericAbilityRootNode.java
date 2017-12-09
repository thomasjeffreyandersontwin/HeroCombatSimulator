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
public class GenericAbilityRootNode extends DefaultAttackTreeNode {
    
    /** Creates new ProcessActivateRoot */
    public GenericAbilityRootNode(String name, BattleEvent be) {
        super();
        this.name = name;
        setBattleEvent(be);
        
        //setVisible(false);
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
                if ( nextNodeName.equals("Select Power/Skill") ) {
                    GenericAbilitySelectNode node = new GenericAbilitySelectNode(nextNodeName);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Configure Ability") ) {
                    // Build the very first node: Attack Param
                    GenericAbilityConfigureNode node = new GenericAbilityConfigureNode(nextNodeName);
                    node.setAbility( battleEvent.getAbility() );
                    node.setModifiersShown(true);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Select Source") ) {
                    // Build the very first node: Attack Param
                    GenericAbilitySourceNode node = new GenericAbilitySourceNode(nextNodeName);
                    node.setAbility( battleEvent.getAbility() );
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Activate Ability") ) {
                    // Build the very first node: Attack Param
                    GenericAbilityActivateNode node = new GenericAbilityActivateNode(nextNodeName);
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
        
        if ( previousNodeName == null ) {
            nextNodeName = "Select Power/Skill";
        }
        else if (  previousNodeName.equals("Select Power/Skill") ) {
            nextNodeName = "Configure Ability";
        }
        else if (previousNodeName.equals("Configure Ability")) {
                nextNodeName = "Select Source";
        }
        else if (previousNodeName.equals("Select Source")) { 
            nextNodeName = "Activate Ability";
        }
        
        if ( nextNodeName == null && battleEvent.isEmbedded() == false 
        && (previousNodeName == null || previousNodeName.equals("Summary") == false) ) {
            nextNodeName = "Summary";
        }
        
        return nextNodeName;
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
        // This is called prior to any advancement of the nodes.
        // For ActivateRootNode, the node after the current node should always be
        // check for correctness.  If it is wrong, the remainder of the tree should be dismantled.
        
        // Grab the Active Node, then use it to find which child it is a descendent of...
        if ( children != null ) {
            AttackTreeNode atn = getModel().getActiveNode();
            
            boolean found = false;
            while ( atn != null && found == false) {
                if ( children.contains(atn) ) {
                    found = true;
                    break;
                }
                
                atn = atn.getRealParent();
            }
            
            if ( found ) {
                // It is part of the tree, so see if there is already a node after it
                // and, if so, make sure it is the correct node.
                int position = children.indexOf(atn);
                if ( position + 1 < children.size() ) {
                    String nextNodeName = nextNodeName( atn.getName() );
                    AttackTreeNode nextNode = (AttackTreeNode)children.get(position + 1);
                    if ( nextNodeName == null || nextNodeName.equals(nextNode.getName())==false) {
                        // There either shouldn't be an additional node, or the node is the wrong one.
                        // Destroy everything after the current node.
                        int index = children.size() - 1;
                        for(; position < index; index -- ) {
                            atn = (AttackTreeNode)children.get(index);
                            removeChild(atn);
                        }
                    }
                }
            }
        }
    }
    
}
