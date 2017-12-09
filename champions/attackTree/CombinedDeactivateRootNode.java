/*
 * ProcessActivateRoot.java
 *
 * Created on April 25, 2002, 2:17 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.ActivationInfo;
import champions.BattleEvent;
import champions.CombinedAbility;

/**
 *
 * @author  Trevor Walker
 * @version 
 */
public class CombinedDeactivateRootNode extends DefaultAttackTreeNode {

    /** Creates new ProcessActivateRoot */
    public CombinedDeactivateRootNode(String name, BattleEvent be) {
        super();
        this.name = name;
        setBattleEvent(be);
        
     //   setVisible(false);
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
                int nodeIndex = -1;
                try {
                    nodeIndex = Integer.parseInt(nextNodeName);
                } catch (NumberFormatException ex) {
                }
                
                if ( nodeIndex != -1 ) {
                    CombinedAbility ability = (CombinedAbility)getBattleEvent().getAbility();
                    Ability ability2 = ability.getAbility(nodeIndex);
                    ActivationInfo ai = ability2.getActivations( ability.getSource() ).next();
                    BattleEvent be = new BattleEvent(battleEvent.DEACTIVATE, ai);
                    battleEvent.embedBattleEvent(be);
                    ProcessStateDeactivating node = new ProcessStateDeactivating(nextNodeName);
                    node.setBattleEvent(be);
                    nextNode = node;
                }
                else if (nextNodeName.equals("Summary") ) {
                    // Build the very first node: Attack Param
                    SummaryNode node = new SummaryNode("Summary");
                    node.setBattleEvent(battleEvent);
                    nextNode = node;
                }
            }
        }
        return nextNode;
    }
    
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        
        String ai_state = battleEvent.getActivationInfo().getState();
        
        CombinedAbility ability = (CombinedAbility)getBattleEvent().getAbility();
        
        int lastNodeIndex = ability.getAbilityCount();
        if ( previousNodeName == null ) {
            lastNodeIndex = -1;
        }
        else {
            try {

                lastNodeIndex = Integer.parseInt(previousNodeName);
            } catch (NumberFormatException ex) {
            }
        }
        
        while ( lastNodeIndex < ability.getAbilityCount() - 1 ) {
            lastNodeIndex++;
            if ( ability.getAbility(lastNodeIndex).isActivated( ability.getSource() ) ) {
                nextNodeName = Integer.toString(lastNodeIndex);
                break;
            }
        }
        
        if ( nextNodeName == null && battleEvent.isEmbedded() == false && (previousNodeName == null || previousNodeName.equals("Summary") == false) ) {
            nextNodeName = "Summary";
        }
        
        return nextNodeName;
    }
    
    public void checkNodes() {
        
    }

    public boolean propogateBattleEvent(AttackTreeNode node) {
        return false;
    }


}
