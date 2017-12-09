/*
 * ProcessActivateRoot.java
 *
 * Created on April 25, 2002, 2:17 PM
 */

package champions.attackTree;

import champions.BattleEvent;
import champions.ConfigureFrameworkBattleEvent;
import champions.SkillBasedSkillRollInfo;
import champions.battleMessage.BattleMessageGroup;
import champions.battleMessage.DefaultBattleMessageGroup;
import champions.exception.BattleEventException;
import champions.interfaces.Undoable;

/**
 *
 * @author  Trevor Walker
 * @version
 */
public class ConfigureFrameworkRootNode extends DefaultAttackTreeNode implements BattleMessageGroupProvider {
    
    private BattleMessageGroup messageGroup;
    /** Creates new ProcessActivateRoot */
    public ConfigureFrameworkRootNode(String name, BattleEvent be) {
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

    @Override
    public boolean processAdvance() {
        battleEvent.addUndoableEvent( new CancelConfigurationUndoable());
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
                if ( nextNodeName.equals("Description") ) {
                    // Build the very first node: Attack Param
                    ConfigureFrameworkDescriptionNode node = new ConfigureFrameworkDescriptionNode("Description");
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Allocate Framework Abilities") ) {
                    // Build the very first node: Attack Param
                    ConfigureFrameworkSetupNode node = new ConfigureFrameworkSetupNode("Allocate Framework Abilities");
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Open Message Group") ) {
                    messageGroup = new DefaultBattleMessageGroup("Configuring " + getBattleEvent().getFramework().getFrameworkAbility().getName() + " framework");

                    OpenMessageGroupNode node = new OpenMessageGroupNode(nextNodeName, this);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Skill Roll") ) {
                    ConfigureFrameworkBattleEvent be = (ConfigureFrameworkBattleEvent)getBattleEvent();
                    SkillRollNode2 node = new SkillRollNode2("Skill Roll", "FrameworkSkillRoll");
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Apply Configuration Changes") ) {
                    nextNode = getBattleEvent().getFramework().getConfiguration().getCommitAttackTreeNode("Apply Configuration Changes");
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
            nextNodeName = "Description";
        }
        else if ( previousNodeName.equals("Description") ) {
            nextNodeName = "Allocate Framework Abilities";
        }
        else if ( previousNodeName.equals("Allocate Framework Abilities") ) {
            nextNodeName = "Open Message Group";
        }
        else if ( previousNodeName.equals("Open Message Group") ) {
            SkillBasedSkillRollInfo sri = (SkillBasedSkillRollInfo)battleEvent.getSkillRollInfo("FrameworkSkillRoll", getTargetGroup());

            if ( sri != null ) {
                nextNodeName = "Skill Roll";
            }
            else {
                nextNodeName = "Apply Configuration Changes";
            }
        }
        else if ( previousNodeName.equals("Skill Roll") ) {
            nextNodeName = "Apply Configuration Changes";
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

    public BattleMessageGroup getBattleMessageGroup() {
        return messageGroup;
    }

    @Override
    public ConfigureFrameworkBattleEvent getBattleEvent() {
        return (ConfigureFrameworkBattleEvent)battleEvent;
    }

    private class CancelConfigurationUndoable implements Undoable {

        public CancelConfigurationUndoable() {
        }

        public void undo() throws BattleEventException {
            getBattleEvent().getFramework().getConfiguration().cancelActions();
        }

        public void redo() throws BattleEventException {
        }
    }
    
}
