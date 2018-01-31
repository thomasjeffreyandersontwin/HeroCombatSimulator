/*
 * ProcessActivateRoot.java
 *
 * Created on April 25, 2002, 2:17 PM
 */

package champions.attackTree;

import champions.BattleEvent;
import champions.battleMessage.ActivateAbilityMessageGroup;

/**
 *
 * @author  Trevor Walker
 * @version
 */
public class ProcessActivateRootNode extends DefaultAttackTreeNode {
	public static  ProcessActivateRootNode PNode;
    /** Creates new ProcessActivateRoot */
    public ProcessActivateRootNode(String name, BattleEvent be) {
        super();
        this.name = name;
        setBattleEvent(be);
        PNode = this;
        
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
                if ( nextNodeName.equals("processStateNew") ) {
                    // Build the very first node: Attack Param
                    ProcessStateNewNode node = new ProcessStateNewNode("processStateNew");
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Attack Description") ) {
                    // Build the very first node: Attack Param
                    AttackDescriptionNode adn = new AttackDescriptionNode("Attack Description");
                    adn.setTargetGroupSuffix("");
                    nextNode = adn;
                }
                else if ( nextNodeName.equals("Attack Parameters") ) {
                    // Build the very first node: Attack Param
                    AttackParametersNode apn = new AttackParametersNode("Attack Parameters");
                    apn.setTargetGroupSuffix("");
                    nextNode = apn;
                }
                else if ( nextNodeName.equals("processStatePredelay") ) {
                    // Build the very first node: Attack Param
                    ProcessStatePredelay node = new ProcessStatePredelay("processStatePredelay");
                    nextNode = node;
                }
                else if ( nextNodeName.equals("processStateAbiltiyDelayed") ) {
                    // Build the very first node: Attack Param
                    ProcessStateAbilityDelayed node = new ProcessStateAbilityDelayed("processStateAbiltiyDelayed");
                    nextNode = node;
                }
                else if ( nextNodeName.equals("processStateWaitForAbilityTrigger") ) {
                    // Build the very first node: Attack Param
                    ProcessStateWaitForAbilityTrigger node = new ProcessStateWaitForAbilityTrigger("processStateWaitForAbilityTrigger");
                    nextNode = node;
                }
                else if ( nextNodeName.equals("processStateAbilityTriggered") ) {
                    // Build the very first node: Attack Param
                    ProcessStateAbilityTriggered node = new ProcessStateAbilityTriggered("processStateAbilityTriggered");
                    nextNode = node;
                }
                else if ( nextNodeName.equals("processStatePreactivateAbility") ) {
                    // Build the very first node: Attack Param
                    ProcessStatePreactivateAbility node = new ProcessStatePreactivateAbility("processStatePreactivateAbility");
                    nextNode = node;
                }
                else if ( nextNodeName.equals("processStateManeuverDelayed") ) {
                    // Build the very first node: Attack Param
                    ProcessStateManeuverDelayed node = new ProcessStateManeuverDelayed("processStateManeuverDelayed");
                    nextNode = node;
                }
                else if ( nextNodeName.equals("processStateManeuverTriggered") ) {
                    // Build the very first node: Attack Param
                    ProcessStateManeuverTriggered node = new ProcessStateManeuverTriggered("processStateManeuverTriggered");
                    nextNode = node;
                }
                else if ( nextNodeName.equals("processStatePreactivateManeuver") ) {
                    // Build the very first node: Attack Param
                    ProcessStatePreactivateManeuver node = new ProcessStatePreactivateManeuver("processStatePreactivateManeuver");
                    nextNode = node;
                }
                else if ( nextNodeName.equals("processStateActivated") ) {
                    // Build the very first node: Attack Param
                    ProcessStateActivated node = new ProcessStateActivated("processStateActivated");
                    nextNode = node;
                }
                else if ( nextNodeName.equals("processStateContinuing") ) {
                    // Build the very first node: Attack Param
                    ProcessStateContinuing node = new ProcessStateContinuing("processStateContinuing");
                    nextNode = node;
                }
                else if ( nextNodeName.equals("processStateActivationFailed") ) {
                    // Build the very first node: Attack Param
                    ProcessStateActivationFailed node = new ProcessStateActivationFailed("processStateActivationFailed");
                    nextNode = node;
                }
                else if ( nextNodeName.equals("processStateFinishing") ) {
                    // Build the very first node: Attack Param
                    ProcessStateFinishing node = new ProcessStateFinishing("processStateFinishing");
                    nextNode = node;
                }
                else if( nextNodeName.equals("processStateDeactivating") ) {
                    // Build the very first node: Attack Param
                    ProcessStateDeactivating node = new ProcessStateDeactivating("processStateDeactivating");
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
        
        String ai_state = battleEvent.getActivationInfo().getState();
        
        if ( battleEvent.isFinishedProcessingEvent() == false ) {
            if ( ai_state.equals( AI_STATE_NEW ) ) {
                nextNodeName = "processStateNew";
            }
            else if ( ai_state.equals( AI_STATE_PREDELAY ) ) {
                nextNodeName = "processStatePredelay";
            }
            else if ( ai_state.equals( AI_STATE_ABILITY_DELAYED ) ) {
                nextNodeName = "processStateAbiltiyDelayed";
            }
            else if ( ai_state.equals( AI_STATE_WAIT_FOR_ABILITY_TRIGGER ) ) {
                nextNodeName = "processStateWaitForAbilityTrigger";
            }
            else if ( ai_state.equals( AI_STATE_ABILITY_TRIGGERED ) ) {
                nextNodeName = "processStateAbilityTriggered";
            }
            else if ( ai_state.equals( AI_STATE_PREACTIVATE_ABILITY ) ) {
                nextNodeName = "processStatePreactivateAbility";
            }
            else if ( ai_state.equals( AI_STATE_MANEUVER_DELAYED ) ) {
                nextNodeName = "processStateManeuverDelayed";
            }
            else if ( ai_state.equals( AI_STATE_MANEUVER_TRIGGERED ) ) {
                nextNodeName = "processStateManeuverTriggered";
            }
            else if ( ai_state.equals( AI_STATE_PREACTIVATE_MANEUVER ) ) {
                nextNodeName = "processStatePreactivateManeuver";
            }
            else if ( ai_state.equals( AI_STATE_ACTIVATED ) ) {
                if ( previousNodeName == null || previousNodeName.equals("processStateActivated") == false ) {
                    nextNodeName = "processStateActivated";
                }
                else {
                    nextNodeName = "processStateFinishing";
                }
            }
            else if (  ai_state.equals( AI_STATE_CONTINUING ) ) {
                if ( previousNodeName == null  ) {
                    if ( ! battleEvent.isAttackTreeDescriptionShown() ) {
                        nextNodeName = "Attack Description";
                    }
                    else {
                        nextNodeName = "processStateContinuing";
                    }
                }
                else if ( previousNodeName.equals("Attack Description") ) {
                    nextNodeName = "Attack Parameters";
                }
                else if ( previousNodeName.equals("Attack Parameters") ) {
                    nextNodeName = "processStateContinuing";
                }
                else if ( previousNodeName.equals("processStateContinuing") && battleEvent.getType() != BattleEvent.CHARGE_END  ) {
                    // Skip StateActivated if we are just charging END...
                    nextNodeName = "processStateActivated";
                }
                else {
                    nextNodeName = "processStateFinishing";
                }
            }
            else if ( ai_state.equals( AI_STATE_ACTIVATION_FAILED ) ) {
                nextNodeName = "processStateActivationFailed";
            }
            else if ( ai_state.equals( AI_STATE_DEACTIVATING ) ) {
                nextNodeName = "processStateDeactivating";
            }
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
                    //jeff removed nextNodeName == null || from below 
                    if (  nextNodeName != null && nextNodeName.equals(nextNode.getName())==false) {
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
