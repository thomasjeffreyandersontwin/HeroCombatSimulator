/*
 * ProcessActivateAbilityNode.java
 *
 * Created on April 25, 2002, 4:05 PM
 */

package champions.attackTree;

import champions.exception.BattleEventException;
import champions.*;
/**
 *
 * @author  Trevor Walker
 * @version
 */
public class CombinedActivateAbilityNode extends DefaultAttackTreeNode {
    
    boolean firstPass = true;
    
    /** Holds the index of the Ability in the CombinedAbilityBattleEvent */
    int combinedAbilityIndex = -1;
    
    /** Creates new ProcessActivateAbilityNode */
    public CombinedActivateAbilityNode(String name) {
        this.name = name;
        
        setVisible(true);
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean activateNode = false;
        
        // Embed the BattleEvent 
        if ( combinedAbilityIndex != -1 ) {
            BattleEvent be = ((CombinedAbilityBattleEvent)battleEvent).getSubBattleEvent(combinedAbilityIndex);
            battleEvent.embedBattleEvent(be);
            
        }
        
        if ( firstPass ) {
            buildChildren();
            firstPass = false;
        }
        
        if ( nodeRequiresInput() || manualOverride == true ) {
            Target source = battleEvent.getSource();
            Ability ability = ((CombinedAbilityBattleEvent)battleEvent).getCombinedAbility(combinedAbilityIndex);
            String reason = "This is the activation node for " + source.getName() + "'s " + ability.getName() +
                            " ability.  This is part of a Combined ability activation." +
                            "\n\nEverything contained in this folder pertains only to this single ability.";
            InformationPanel ip = InformationPanel.getDefaultPanel( reason );
            attackTreePanel.showInputPanel(this,ip);
            attackTreePanel.setInstructions("Hit Okay to Continue...");
            
            activateNode = true;
        }
        
        return activateNode;
    }
    
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        // This node is completely strange in that it firsts asks for a target, and once one is set, it
        // creates the ToHit and TargetOption nodes immediately.
        
        return null;
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
        // Don't do anything when you check the nodes.  This nodes is built by
        // when the setTarget method is called.
    }
    
    private void buildChildren() {
        if ( combinedAbilityIndex != -1 ) {
            BattleEvent be = ((CombinedAbilityBattleEvent)battleEvent).getSubBattleEvent(combinedAbilityIndex);
            
            ProcessActivateRootNode apn = new ProcessActivateRootNode("Process Activate Root Node", be);
            apn.setVisible(false);
            addChild(apn, false);
        }
    }
    
    
    /**
     * Causes node to process an advance and perform any work that it needs to do.
     *
     * This is a method introduced by DefaultAttackTreeNode.  DefaultAttackTreeNode
     * delegates to this method if advanceNode node is called and this is the active
     * node.  This method should do all the work of advanceNode whenever it can, since
     * the next node processing and buildNextChild methods depend on the existing
     * DefaultAttackTreeNode advanceNode method.
     *
     * Returns true if it is okay to leave the node, false if the node should
     * be reactivated to gather more information.
     */
    public boolean processAdvance() throws BattleEventException {
        
        
        return true;
    }
    
    private boolean nodeRequiresInput() {
        // Run through the dice and make sure the all have values;
        boolean requiresInput = false;
        
        return requiresInput;
    }
    
    /** Getter for property combinedAbilityIndex.
     * @return Value of property combinedAbilityIndex.
     *
     */
    public int getCombinedAbilityIndex() {
        return combinedAbilityIndex;
    }
    
    /** Setter for property combinedAbilityIndex.
     * @param combinedAbilityIndex New value of property combinedAbilityIndex.
     *
     */
    public void setCombinedAbilityIndex(int combinedAbilityIndex) {
        this.combinedAbilityIndex = combinedAbilityIndex;
    }
    
    public String toString() {
        if ( combinedAbilityIndex != -1 ) {
            Ability ability = ((CombinedAbilityBattleEvent)battleEvent).getCombinedAbility(combinedAbilityIndex);
            
            if ( ability != null ) return ability.getName();
        }
        
        return "CombinedActivateAbilityNode Error";
    }
    
}
