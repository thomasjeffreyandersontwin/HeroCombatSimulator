/*
 * ProcessActivateRoot.java
 *
 * Created on April 25, 2002, 2:17 PM
 */

package champions.attackTree;

import champions.BattleEvent;
import champions.CombinedAbilityBattleEvent;
import champions.battleMessage.ActivateAbilityMessageGroup;
import champions.battleMessage.BattleMessageGroup;
import champions.battleMessage.ConfigureBattleMessageGroup;

/**
 *
 * @author  Trevor Walker
 * @version
 */
public class CombinedActivateRootNode extends DefaultAttackTreeNode implements BattleMessageGroupProvider {
    
    private BattleMessageGroup messageGroup;
    
    /** Creates new ProcessActivateRoot */
    public CombinedActivateRootNode(String name, BattleEvent be) {
        super();
        this.name = name;
        setBattleEvent(be);
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
                if ( nextNodeName.equals("processActivateRootNode") ) {
                    // Build the very first node: Attack Param
                    CombinedAbilityBattleEvent lbe = (CombinedAbilityBattleEvent) battleEvent;
                    lbe.embedBattleEvent(lbe.getSubBattleEvent(0));
                    ProcessActivateRootNode node = new ProcessActivateRootNode(nextNodeName, lbe.getSubBattleEvent(0));
                    node.setVisible(false);
                    nextNode = node;
                }
                else if (nextNodeName.equals("Combined Setup") ) {
                    // Build the Combined Setup node...
                    CombinedSetupNode node = new CombinedSetupNode(nextNodeName);
                    nextNode = node;
                }
                else if (nextNodeName.equals("Combined Execute") ) {
                    CombinedExecuteNode node = new CombinedExecuteNode(nextNodeName);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Open Message Group") ) {
                    // Build the very first node: Attack Param
                    messageGroup = new ActivateAbilityMessageGroup(getBattleEvent().getSource(), getBattleEvent().getCombinedAbility());
                    
                    OpenMessageGroupNode node = new OpenMessageGroupNode(nextNodeName, this);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Close Message Group") ) {
                    // Build the very first node: Attack Param
                    CloseMessageGroupNode node = new CloseMessageGroupNode(nextNodeName, this);
                    nextNode = node;
                }
                else if (nextNodeName.equals("Summary") ) {
                    // Build the summary node...
                    SummaryNode node = new SummaryNode("Summary");
                    nextNode = node;
                }
                
            }
        }
        return nextNode;
    }
    
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        
        CombinedAbilityBattleEvent lbe = (CombinedAbilityBattleEvent) battleEvent;
        
        if ( previousNodeName == null && lbe.getCombinedAbilityCount() == 1 ) {
            nextNodeName = "processActivateRootNode";
        }
        else if ( previousNodeName == null ) {
            nextNodeName = "Combined Setup";
        }
        else if ( previousNodeName.equals("Combined Setup") ) {
            nextNodeName = "Open Message Group";
        }
        else if ( previousNodeName.equals("Open Message Group")) {
            nextNodeName = "Combined Execute";
        }
        else if ( previousNodeName.equals("Combined Execute")) {
            nextNodeName = "Close Message Group";
        }
        
        
        
        if ( nextNodeName == null && battleEvent.isEmbedded() == false 
            && (previousNodeName == null || previousNodeName.equals("Summary") == false) ) {
            nextNodeName = "Summary";
        }
        
        return nextNodeName;
    }
    
    /**
     * Indicates whether the battleEvent of this parent should be
     * propogated to the child nodes when addChild is called.
     */
    public boolean propogateBattleEvent(AttackTreeNode node) {
        // Generally, the combined should propogate
        if ( node instanceof ProcessActivateRootNode ) {
            return false;
        }
        
        return true;
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

    public BattleMessageGroup getBattleMessageGroup() {
        return messageGroup;
    }
    
    public CombinedAbilityBattleEvent getBattleEvent() {
        return (CombinedAbilityBattleEvent) battleEvent;
    }
    
}
