/*
 * ProcessActivateAbilityNode.java
 *
 * Created on April 25, 2002, 4:05 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.Battle;
import champions.BattleEvent;
import champions.Target;
import champions.exception.BattleEventException;
import champions.interfaces.AbilityIterator;
import champions.interfaces.AbilityList;

/**
 *
 * @author  Trevor Walker
 * @version
 */
public class GenericAbilityActivateNode extends DefaultAttackTreeNode {
    
    protected boolean firstPass = true;
    
    /** Hold the BattleEvent for the actual activation. */
    protected BattleEvent subBattleEvent;
    
    /** Creates new ProcessActivateAbilityNode */
    public GenericAbilityActivateNode(String name) {
        this.name = name;
        
        setVisible(true);
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean activateNode = false;
        
        addToRecentAbilityList( battleEvent.getAbility() );
        
        // Check that the subBattleEvent is non-null and makes sense...
        if ( subBattleEvent == null || 
            subBattleEvent.getAbility().getInstanceGroup().isInstance(battleEvent.getAbility()) == false ||
            subBattleEvent.getSource() != battleEvent.getSource() 
        ) {
            subBattleEvent = new BattleEvent( battleEvent.getAbility() );
            subBattleEvent.setSource( battleEvent.getAbility().getSource() );
            firstPass = true; // Reset the tree...
        }
        
        // Embed the BattleEvent 
        battleEvent.embedBattleEvent(subBattleEvent);
        
        if ( firstPass ) {
            buildChildren();
            firstPass = false;
        }
        
        if ( nodeRequiresInput() || manualOverride == true ) {
            Target source = battleEvent.getAbility().getSource();
            Ability ability = battleEvent.getAbility();
            String reason = "This is the activation node for " + source.getName() + "'s " + ability.getName() +
                            " ability." +
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
        if ( getRealChildCount() != 0 ) {
            removeChild( (AttackTreeNode)getRealChildAt(0) );
        }
        
        ProcessActivateRootNode apn = new ProcessActivateRootNode("Process Activate Root Node", subBattleEvent);
        apn.setVisible(false);
        addChild(apn, false);
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
    
    private void addToRecentAbilityList(Ability ability) {
        if ( ability == null ) return;
        
        AbilityList al = Battle.currentBattle.getRecentGenericAbilityList();
        
        AbilityIterator ai = al.getAbilities();
        boolean found = false;
        while(!found && ai.hasNext()) {
            Ability a = ai.nextAbility();
            //if ( a.getName().equals( ability.getName() )) {
                if ( a.compareConfiguration(ability) ) {
                    // Just make sure the names are the same
                    a.setName( ability.getName() );
                    found = true;
                }
            //}
        }
        
        if ( ! found ) {
            al.addAbility(ability);
        }
    }
}
